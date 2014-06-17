/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.x10rt;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import x10.core.fun.VoidFun_0_0;
import x10.lang.FinishState;
import x10.lang.Place;
import x10.serialization.X10JavaDeserializer;

/**
 * Implementation of JavaSockets transport
 */
public class SocketTransport {
	private static final boolean DEBUG = false;
	public static final String X10_FORCEPORTS = "X10_FORCEPORTS";
	public static final String X10_LAUNCHER_PLACE = "X10_LAUNCHER_PLACE";
	public static final String X10_NPLACES = "X10_NPLACES";
	public static final String X10_LAUNCHER_PARENT = "X10_LAUNCHER_PARENT";
	public static final String X10_NOWRITEBUFFER = "X10_NOWRITEBUFFER"; // turns off non-blocking sockets
	public static final String X10_SOCKET_TIMEOUT = "X10_SOCKET_TIMEOUT";
	static final String UTF8 = "UTF-8";
	private static final String DEAD = "DEAD";
	private static enum PROBE_TYPE {ACCEPT, ACCEPTORWRITE, ALL};
	private static enum CTRL_MSG_TYPE {HELLO, GOODBYE, PORT_REQUEST, PORT_RESPONSE}; // Correspond to values in Launcher.h
	static enum MSGTYPE {STANDARD, PUT, GET, GET_COMPLETED, GET_PLACE_REQUEST, GET_PLACE_RESPONSE, CONNECT_DATASTORE}; // note that GET_PLACE_REQUEST does not overlap with CTRL_MSG_TYPE
	public static enum CALLBACKID {closureMessageID, simpleAsyncMessageID};
	public static enum RETURNCODE { // see matching list of error codes "x10rt_error" in x10rt_types.h 
	    X10RT_ERR_OK,   /* No error */
	    X10RT_ERR_MEM,   /* Out of memory error */
	    X10RT_ERR_INVALID,   /* Invalid method call, at this time (e.g. probe() before init()) */
	    X10RT_ERR_UNSUPPORTED,   /* Not supported by this implementation of X10RT */
	    X10RT_ERR_INTL, /* Internal implementation error */
	    X10RT_ERR_OTHER /* Other unclassified runtime error */
	};
	
	private class CommunicationLink {
    	private CommunicationLink(SocketChannel sc, int placeId, String portInfo) {
			super();
			this.sc = sc;
			this.writeLock = new ReentrantLock();
			this.pendingWrites = null;
			this.placeid = placeId;
			this.portInfo = portInfo;
		}
    	final int placeid;
    	final String portInfo;
		final SocketChannel sc;
		final ReentrantLock writeLock;
		LinkedList<ByteBuffer> pendingWrites;
    }
	
	private volatile int nplaces = -1; // number of places
	private int myPlaceId = -1; // my place ID
	private volatile int lowestValidPlaceId = 0; // responsible for global knowledge of nplaces. Increases as places die
	private ServerSocketChannel localListenSocket = null;
	private final ConcurrentHashMap<Integer,CommunicationLink> channels = new ConcurrentHashMap<Integer, SocketTransport.CommunicationLink>(); // communication links to remote places, and launcher stored at myPlaceId
	private final TreeSet<Integer> deadPlaces = new TreeSet<Integer>();
	private final boolean bufferedWrites;
	private final LinkedList<CommunicationLink> pendingJoins = new LinkedList<CommunicationLink>();
	
	private final ReentrantLock selectorLock = new ReentrantLock(); // protects both the selector and events objects
	private Selector selector = null;
	private Iterator<SelectionKey> events = null;
	private int socketTimeout = -1;
	private volatile boolean shuttingDown = false;
    
	
	public SocketTransport() {
		String nplacesFlag = System.getenv(X10_NPLACES);
		if (nplacesFlag != null)
			nplaces = Integer.parseInt(nplacesFlag);
		
		String placeFlag = System.getenv(X10_LAUNCHER_PLACE);
		if (placeFlag != null) 
			myPlaceId = Integer.parseInt(placeFlag);
		else if (nplaces == 1)
			myPlaceId = 0;
		
		try {
			localListenSocket = ServerSocketChannel.open();
			String forcePortsFlag = System.getenv(X10_FORCEPORTS);
			if (forcePortsFlag != null && myPlaceId >= 0)
				localListenSocket.socket().bind(new InetSocketAddress(Integer.parseInt(forcePortsFlag) + myPlaceId));
			else
				localListenSocket.socket().bind(null);
			localListenSocket.configureBlocking(false);
			
			selector = Selector.open();
			localListenSocket.register(selector, SelectionKey.OP_ACCEPT);
			
			if (nplaces > 1) {
				// we may be under the control of a launcher.  If so, link up
				String launcherLocation = System.getenv(X10_LAUNCHER_PARENT);
				if (launcherLocation != null)
					initLink(myPlaceId, launcherLocation);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		bufferedWrites = !(Boolean.parseBoolean(System.getenv(X10_NOWRITEBUFFER)) || Boolean.parseBoolean(System.getProperty(X10_NOWRITEBUFFER)));
		try {
			socketTimeout = Integer.parseInt(System.getProperty(X10_SOCKET_TIMEOUT));
		}
		catch (NumberFormatException e){} // not set.		
		
		if (DEBUG) System.err.println("Socket library initialized. myPlaceid="+this.myPlaceId+" nplaces="+this.nplaces);
	}
	
	public String getLocalConnectionInfo() {
		int port = localListenSocket.socket().getLocalPort();
		String hostname;
		try {
			hostname = InetAddress.getLocalHost().getCanonicalHostName();
		} catch (UnknownHostException e) {
			try {
				hostname = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e1) {
				hostname = localListenSocket.socket().getInetAddress().getHostName();
			}
		}
		return hostname+":"+port;
	}
	
	// this form is used in elastic X10. It links to any known place 
	// and gets config information from it
	public int establishLinks(String initialLink) {
		if (DEBUG) System.out.println("Joining existing computation at "+initialLink);
		try {
			initLink(-1, initialLink);
			return RETURNCODE.X10RT_ERR_OK.ordinal();
		} catch (IOException e) {
			e.printStackTrace();
			return RETURNCODE.X10RT_ERR_OTHER.ordinal();
		}
	}
	
	// this form is used when the launcher provides config information, 
	// or, when there is a single place and no launcher
	public int establishLinks() {
		if (this.myPlaceId == -1 && this.nplaces == -1) {
			this.nplaces = 1;
			this.myPlaceId = 0;
		}

		if (nplaces > 1) {
			for (int i=0; i<myPlaceId; i++) {
				try {
					initLink(i, null);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // connect to all lower places
			}
			for (int i=myPlaceId+1; i<nplaces; i++)
				while (!shuttingDown && !channels.containsKey(i))
					x10rt_probe(PROBE_TYPE.ACCEPT, 0); // wait for connections from all upper places
		} 
/* a single place may have other places join it later		
		else {
			try {
				if (localListenSocket != null)
					localListenSocket.close();
			} catch (IOException e) {}
		}
*/	
		return RETURNCODE.X10RT_ERR_OK.ordinal();
	}
	
	// this form is used when config information is provided from outside, after initial loading
    public int establishLinks(int myPlaceId, String[] connectionStrings) {
    	if (this.myPlaceId != -1 || this.nplaces != -1) // make we are in the right state to establish links
    		return RETURNCODE.X10RT_ERR_INVALID.ordinal();

    	if (connectionStrings != null && connectionStrings.length > 1) {
    		this.nplaces = connectionStrings.length;
        	this.myPlaceId = myPlaceId;
    	}
    	else {
    		// single place.  No need to establish any links.
    		if (myPlaceId != 0)
    			return RETURNCODE.X10RT_ERR_INVALID.ordinal();
    		this.nplaces = 1;
    		this.myPlaceId = 0;
    		if (localListenSocket != null) {
    			try {
					localListenSocket.close();
				} catch (IOException e) {}
    		}
    		return RETURNCODE.X10RT_ERR_OK.ordinal();
    	}
    	
    	if (shuttingDown)
    		return RETURNCODE.X10RT_ERR_OTHER.ordinal();
    	else {
    		if (DEBUG) System.err.println("Place "+myPlaceId+" establishing links.  id="+myPlaceId+" np="+((connectionStrings==null)?'1':connectionStrings.length));
        	
	    	for (int i=0; i<myPlaceId; i++) {
				try {
					initLink(i, connectionStrings[i]);
				} catch (IOException e) {
					// try one more time.  Maybe a transient issue
					try {
						initLink(i, connectionStrings[i]);
					} catch (IOException e2) {
						e2.printStackTrace();
						return RETURNCODE.X10RT_ERR_OTHER.ordinal();
					}
				} // connect to all lower places
	    	}
			for (int i=myPlaceId+1; i<nplaces; i++)
				while (!shuttingDown && !channels.containsKey(i))
					x10rt_probe(PROBE_TYPE.ACCEPT, 0); // wait for connections from all upper places
	    }
    	
    	return RETURNCODE.X10RT_ERR_OK.ordinal();
    }
    
    public int numDead() {
    	// no synchronization needed
    	return deadPlaces.size();
    }
    
    public boolean isPlaceDead(int place) {
    	// no synchronization needed
    	return deadPlaces.contains(place);
    }
    
    private void markPlaceDead(int place) {
    	channels.remove(place);
    	synchronized (deadPlaces) {
    		deadPlaces.add(place);
    		if (place == lowestValidPlaceId) {
    			lowestValidPlaceId++;
    			while (isPlaceDead(lowestValidPlaceId)) 
    				lowestValidPlaceId++;
    		}
    	}
    }
    
    // this method will cause the establishLinks method to return, even if it is 
    // waiting for links to be established.  This allows that thread to get unstuck
    // in the event that the place list is invalid.
    public synchronized int shutdown() {
    	if (DEBUG) System.err.println("shutting down");
    	shuttingDown = true;
   		try {
   			if (localListenSocket != null)
    			localListenSocket.close();
   			
   			for (CommunicationLink channel : channels.values()) {
	   		   	channel.writeLock.lock();
	    		try {
    				 channel.sc.close();
	    		}
	    		catch (Exception e){}// ignore... shutting down
	    		finally {
	    			channel.writeLock.unlock();
	    			channel.pendingWrites = null;
	    		}
   			}
   			channels.clear();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	myPlaceId = -1;
    	nplaces = -1;
    	return RETURNCODE.X10RT_ERR_OK.ordinal();
    }

    public int x10rt_nplaces(){
    	return this.nplaces;
    }
        
    public int x10rt_here(){
    	return this.myPlaceId;
    }
    
    // If a thread is blocked on the blocking_probe, wake it up so that it can return.  If nothing is blocked, 
    // the next call to probe will not block.
    public void wakeup() {
    	selector.wakeup();
    }
    
    
    private String getAllPlaceLinks() {
    	// build up the place list.  The format is host:port,host:port,,host:port etc
		// blank host:port slots represent dead places
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<this.nplaces; i++) {
			if (this.myPlaceId == i)
				sb.append(getLocalConnectionInfo());
			else if (isPlaceDead(i))
				sb.append(DEAD);
			else
				sb.append(channels.get(i).portInfo);
			sb.append(',');
		}		
		return sb.toString();
    }
    
    // onlyProcessAccept is set to true only during startup time, to prioritize establishing links
    // timeout is how long we're willing to block waiting for something to happen. 
    // returns true if something is processed
    boolean x10rt_probe(PROBE_TYPE probeType, long timeout) {
/* a single place may have other places join it later
    	if (!onlyProcessAccept && nplaces == 1)
    		return false;
*/    	
    	int eventCount = 0;
    	try {
    		SelectionKey key;
    		if (timeout == 0) // blocking probe, wait for the selector to become available
    			selectorLock.lock();
    		else if (!selectorLock.tryLock()) // non-blocking probe, return immediately if selector is busy
    			return false;
    		// we have the selector lock.  go ahead and get the next event, or call select
    		try {
    			if (events != null && events.hasNext()) {
    				key = events.next();
	    			events.remove();
    			}
	    		else {
	    			eventCount = selector.select(timeout);
	    			if (eventCount == 0) return false;
	    			
	    			events = selector.selectedKeys().iterator();
	    			key = events.next();
	    			events.remove();
    			}
    		} finally {
    			selectorLock.unlock();
    		}
			if (key.isAcceptable()) {
				ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
				SocketChannel sc = ssc.accept();
				if (sc == null)
					return false; // nothing actually here
				int remote = -1;
				
				// see the format of "ctrl_msg" in Launcher.h
				ByteBuffer controlMsg = ByteBuffer.allocateDirect(16);
				if (!readNBytes(sc, controlMsg, controlMsg.capacity()))
					return false;
				controlMsg.flip();
				int msgtype = controlMsg.getInt();
				
				if (CTRL_MSG_TYPE.HELLO.ordinal() == msgtype) {
					int to = controlMsg.getInt();
					if (to == myPlaceId) {
						remote = controlMsg.getInt();
						int strlen = controlMsg.getInt();
						byte[] linkdata = new byte[strlen];
						ByteBuffer linkBB = ByteBuffer.wrap(linkdata);
						if (!readNBytes(sc, linkBB, strlen)) {
							System.err.println("Error reading "+strlen+" bytes from HELLO message");
							return false;
						}
						String linkString = new String(linkdata, Charset.forName(UTF8));
						if (DEBUG) System.err.println("Incoming HELLO message to "+to+" from "+remote+" strlen="+strlen+" link=\""+linkString+"\"");
						controlMsg.clear();
						controlMsg.putInt(CTRL_MSG_TYPE.HELLO.ordinal());
						controlMsg.putInt(remote);
						controlMsg.putInt(myPlaceId);
						controlMsg.putInt(0);
						controlMsg.flip();
						writeNBytes(sc, controlMsg);
						channels.put(remote, new CommunicationLink(sc, remote, linkString));
						sc.configureBlocking(false);
						if (socketTimeout != -1) sc.socket().setSoTimeout(socketTimeout);
						sc.register(selector, SelectionKey.OP_READ);
						if (DEBUG) System.err.println("Place "+myPlaceId+" accepted a connection from place "+remote);
						
						if (remote >= nplaces)
							this.nplaces = remote+1;
					}
					else if (to == -1) {
						remote = controlMsg.getInt();
						if (remote == -1) {
							int strlen = controlMsg.getInt();
							byte[] linkdata = new byte[strlen];
							ByteBuffer linkBB = ByteBuffer.wrap(linkdata);
							if (!readNBytes(sc, linkBB, strlen)) {
								System.err.println("Error reading "+strlen+" bytes from HELLO message");
								return false;
							}
							String linkString = new String(linkdata, Charset.forName(UTF8));
							if (DEBUG) System.err.println("Incoming HELLO message to "+to+" from "+remote+" strlen="+strlen+" link=\""+linkString+"\"");
							if (myPlaceId == lowestValidPlaceId) {
								String allPlaceLinks = getAllPlaceLinks();
								byte[] allPlaceLinksBytes = allPlaceLinks.getBytes(Charset.forName(UTF8));
								controlMsg = ByteBuffer.allocateDirect(16+allPlaceLinksBytes.length);
								controlMsg.putInt(CTRL_MSG_TYPE.HELLO.ordinal());
								// we handle new place id assignment directly here
								remote = this.nplaces++;
								controlMsg.putInt(remote);
								controlMsg.putInt(myPlaceId);
								controlMsg.putInt(allPlaceLinksBytes.length);
								controlMsg.put(allPlaceLinksBytes);
								controlMsg.flip();
								writeNBytes(sc, controlMsg);
								channels.put(remote, new CommunicationLink(sc, remote, linkString));
								sc.configureBlocking(false);
								if (socketTimeout != -1) sc.socket().setSoTimeout(socketTimeout);
								sc.register(selector, SelectionKey.OP_READ);
								if (DEBUG) System.err.println("Place "+myPlaceId+" initialized new place "+remote);
								
								// tell the new place to connect to the hazelcast cluster
								if (X10RT.hazelcastDatastore != null) {
									ByteBuffer[] connectionBytes;
									try {
										connectionBytes = new ByteBuffer[]{ByteBuffer.wrap(X10RT.hazelcastDatastore.getConnectionInfo().getBytes(SocketTransport.UTF8))};
						      	   		sendMessage(SocketTransport.MSGTYPE.CONNECT_DATASTORE, remote, 0, connectionBytes);
									} catch (UnsupportedEncodingException e) {
										// this won't happen, because UTF8 is a required encoding
										e.printStackTrace();
										assert(false);
									}
								}
							}
							else { // Ask the lowest numbered place for a new place ID
								// store link for later, when the GET_PLACE_RESPONSE comes in
								synchronized (pendingJoins) {
									pendingJoins.add(new CommunicationLink(sc, remote, linkString));
								}
								// send the place request to the lowest place
								if (sendMessage(MSGTYPE.GET_PLACE_REQUEST, lowestValidPlaceId, -1, null) != RETURNCODE.X10RT_ERR_OK.ordinal() &&
									    // try again.  Maybe the place died while transmitting
									    sendMessage(MSGTYPE.GET_PLACE_REQUEST, lowestValidPlaceId, -1, null) != RETURNCODE.X10RT_ERR_OK.ordinal()) {
										    System.err.println("Error sending place request to "+lowestValidPlaceId);
								}
								else
									remote = -2; // do not close the channel
							}
						}
						else {
							System.err.println("ERROR: Place "+myPlaceId+" got a HELLO message to place -1 from "+remote);
							remote = -1;
						}
					}
					else if (DEBUG) 
						System.err.println("Incoming HELLO message to place "+to+" ignored, because my place is ."+myPlaceId);
				}
				if (remote == -1) {
					controlMsg.clear();
					controlMsg.putInt(CTRL_MSG_TYPE.GOODBYE.ordinal());
					sc.write(controlMsg);
					sc.close();
					System.err.println("Unknown connection");
				}
				return true;
			}
			if (probeType == PROBE_TYPE.ACCEPT) return false;
			if (key.isWritable()) {
				Integer place = (Integer)key.attachment();
				if (DEBUG) System.err.println(myPlaceId+" Flushing data to "+place);
				if (place != null && !isPlaceDead(place.intValue()))
					flushBufferedBytes(channels.get(place));
			}
			if (probeType == PROBE_TYPE.ACCEPTORWRITE) return false;
			if (key.isReadable()) {
				if (DEBUG) System.err.println("Place "+myPlaceId+" detected incoming message");
				SocketChannel sc = (SocketChannel) key.channel();
				ByteBuffer controlData = ByteBuffer.allocateDirect(12);
				ByteBuffer bb = null;
				int msgType=0, callbackId=0, datalen;				
				try {
					synchronized (sc) {
						if (!readNBytes(sc, controlData, controlData.capacity()))
							return false;
						controlData.flip(); // switch from write to read mode
						// Format: type, p.type, p.len, p.msg
						msgType = controlData.getInt();
						callbackId = controlData.getInt();
						datalen = controlData.getInt();
						if (DEBUG) System.err.print("Place "+myPlaceId+" processing an incoming message of type "+callbackId+" and size "+datalen+"...");
						//TODO - eliminate this buffer by modifying the deserializer to take the channel as input
						bb = ByteBuffer.allocate(datalen);
						while (!readNBytes(sc, bb, datalen));
						bb.flip();
					}
					if (msgType == MSGTYPE.STANDARD.ordinal()) {
					    if (callbackId == CALLBACKID.closureMessageID.ordinal())
							SocketTransport.runClosureAtReceive(bb);
						else if (callbackId == CALLBACKID.simpleAsyncMessageID.ordinal())
							SocketTransport.runSimpleAsyncAtReceive(bb);
						else
							System.err.println("Unknown message callback type: "+callbackId);
						
						//if (DEBUG) System.err.println("Place "+myPlaceId+" finished processing message type "+callbackId+" and size "+datalen);
						if (DEBUG) System.err.println("done");
					}
					else if (msgType == MSGTYPE.GET_PLACE_REQUEST.ordinal()) {
						// this comes into the lowest numbered place, which is responsible for place assignment
						// assign a new place id, increment nplaces
						controlData.clear(); 
						controlData.putInt(MSGTYPE.GET_PLACE_RESPONSE.ordinal());
						controlData.putInt(this.nplaces++);
						controlData.putInt(myPlaceId);
						controlData.flip();// switch from write to read mode (for outputting to the socket)
						writeNBytes(sc, controlData); // write back to the original requester, not "remote"
					}
					else if (msgType == MSGTYPE.GET_PLACE_RESPONSE.ordinal()) {
						// get the socket channel we stashed earlier
						int remote = callbackId;
						CommunicationLink newPlace = null;
						synchronized (pendingJoins) {
							newPlace = pendingJoins.poll();
						}
						if (newPlace != null) {
							String allPlaceLinks = getAllPlaceLinks();
							byte[] allPlaceLinksBytes = allPlaceLinks.getBytes(Charset.forName(UTF8));
							ByteBuffer controlMsg = ByteBuffer.allocateDirect(16+allPlaceLinksBytes.length);
							controlMsg.putInt(CTRL_MSG_TYPE.HELLO.ordinal());
							controlMsg.putInt(remote); // actually the new place id
							controlMsg.putInt(myPlaceId);
							controlMsg.putInt(allPlaceLinksBytes.length);
							controlMsg.put(allPlaceLinksBytes);
							controlMsg.flip();
							writeNBytes(newPlace.sc, controlMsg);
							channels.put(remote, new CommunicationLink(sc, remote, newPlace.portInfo));
							sc.configureBlocking(false);
							if (socketTimeout != -1) sc.socket().setSoTimeout(socketTimeout);
							sc.register(selector, SelectionKey.OP_READ);
							if (DEBUG) System.err.println("Place "+myPlaceId+" initialized new place "+remote);
							
							// update nplaces here, because we won't get a connection from the new place, as it already exists
							if (remote >= nplaces)
								this.nplaces = remote+1;
							
							// tell the new place to connect to the hazelcast cluster
							if (X10RT.hazelcastDatastore != null) {
								ByteBuffer[] connectionBytes;
								try {
									connectionBytes = new ByteBuffer[]{ByteBuffer.wrap(X10RT.hazelcastDatastore.getConnectionInfo().getBytes(SocketTransport.UTF8))};
					      	   		sendMessage(SocketTransport.MSGTYPE.CONNECT_DATASTORE, remote, 0, connectionBytes);
								} catch (UnsupportedEncodingException e) {
									// this won't happen, because UTF8 is a required encoding
									e.printStackTrace();
									assert(false);
								}
							}
						}
						else
							System.err.println("Unexpected GET_PLACE_RESPONSE arrived!!");
					}
					else if (msgType == MSGTYPE.CONNECT_DATASTORE.ordinal()) {
						byte[] linkdata = new byte[datalen];
						bb.get(linkdata);
						String linkString = new String(linkdata, UTF8);
						X10RT.initDataStore(linkString);
					}
					else 
						System.err.println("Unknown message type: "+msgType);
				}
				catch (IOException e) {
					// figure out which place this is
					for (Integer place : channels.keySet()) {
						try {
							CommunicationLink cl = channels.get(place);
							if (cl != null && sc.equals(cl.sc)) {
								if (DEBUG) System.err.println("Place "+myPlaceId+" discovered link to place "+place+" is broken in probe");
								markPlaceDead(place);
								cl.pendingWrites = null;
								break;
							}
						} catch (NullPointerException e2){} // channels[i] can become null after we check for null
					}
					try {sc.close();}
		    		catch (Exception e2){}
		    		return false;
				}
				// TODO GET & PUT message types
				return true;
			}
		} catch (CancelledKeyException e) {
			// a key may be cancelled on us if the runtime disconnects while there is active communication
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
    }
    
    public int x10rt_probe() {
    	boolean somethingProcessed;
    	do somethingProcessed = x10rt_probe(PROBE_TYPE.ALL, 1);
    	while (somethingProcessed);
    	
    	return RETURNCODE.X10RT_ERR_OK.ordinal();
    }
    
    public int x10rt_blocking_probe() {
    	x10rt_probe(PROBE_TYPE.ALL, 0);
    	return RETURNCODE.X10RT_ERR_OK.ordinal();
    }
    
    public int sendMessage(int place, int msg_id, ByteBuffer[] bytes) {
    	return sendMessage(MSGTYPE.STANDARD, place, msg_id, bytes);
    }
    
    public int sendMessage(MSGTYPE msgtype, int place, int msg_id, ByteBuffer[] bytes) {
    	if (isPlaceDead(place)) // don't send messages to dead or uninitialized places
    		return RETURNCODE.X10RT_ERR_OTHER.ordinal();

    	// connect to remote place, if not already connected
    	try {
			initLink(place, null);
		} catch (IOException e) {
			return RETURNCODE.X10RT_ERR_OTHER.ordinal();
		}
    	
    	// write out the x10SocketMessage data
    	// Format: type, p.type, p.len, p.msg
    	ByteBuffer controlData = ByteBuffer.allocateDirect(12);
    	controlData.putInt(msgtype.ordinal());
    	controlData.putInt(msg_id);
    	int len = 0;
    	if (bytes != null)
    		for (int i=0; i<bytes.length; i++)
    			len+=bytes[i].remaining();
    	controlData.putInt(len);
    	controlData.flip();
    	if (DEBUG) System.err.print("Place "+myPlaceId+" sending a message to place "+place+" of type "+msg_id+" and size "+len+"...");
    	CommunicationLink cl = channels.get(place);
    	try {
	    	cl.writeLock.lock();
	    	try {
		    	writeBytes(cl, controlData);
		    	if (bytes != null)
		    		for (int i=0; i<bytes.length; i++)
		    			writeBytes(cl, bytes[i]);
				if (DEBUG) System.err.println("Sent");
	    	} 
	    	finally {
	    		cl.writeLock.unlock();
	    	}
    	}
    	catch (IOException e) {
    		if (DEBUG) System.err.println("Place "+myPlaceId+" discovered link to place "+place+" is broken in send");
    		try {cl.sc.close();}
    		catch (Exception e2){}
    		markPlaceDead(place);
    		cl.pendingWrites = null;
    		return RETURNCODE.X10RT_ERR_OTHER.ordinal();
    	}
		
    	return RETURNCODE.X10RT_ERR_OK.ordinal();
    }
    
    private void initLink(int remotePlace, String connectionInfo) throws IOException{
    	if (shuttingDown || channels.containsKey(remotePlace)) return;
    	
    	String hostname;
    	int port;
    	
    	if (connectionInfo == null && !channels.containsKey(myPlaceId) && remotePlace > -1) { // link does not exist, and no link to launcher
    		String forcePortsFlag = System.getenv(X10_FORCEPORTS);
    		if (forcePortsFlag == null) throw new IOException("Unknown location for place "+remotePlace);
    		hostname = "localhost";		
    		port = Integer.parseInt(forcePortsFlag)+remotePlace;
    		connectionInfo = new String(hostname+":"+port);
    	}
    	else {
    		if (connectionInfo == null && remotePlace > -1) {
    			// ask the launcher
    			CommunicationLink launcherLink = channels.get(myPlaceId);
    			ByteBuffer placeRequest = ByteBuffer.allocateDirect(16);
    			placeRequest.order(ByteOrder.nativeOrder());
    			placeRequest.putInt(CTRL_MSG_TYPE.PORT_REQUEST.ordinal());
    			placeRequest.putInt(remotePlace);
    			placeRequest.putInt(myPlaceId);
    			placeRequest.putInt(0);
    			placeRequest.flip();
    			writeNBytes(launcherLink.sc, placeRequest);
    			placeRequest.clear();
    			while (!readNBytes(launcherLink.sc, placeRequest, placeRequest.capacity()));
    			placeRequest.flip();
    			int type = placeRequest.getInt();
    			if (type != CTRL_MSG_TYPE.PORT_RESPONSE.ordinal()) 
    				throw new IOException("Invalid response to launcher lookup for place "+remotePlace);
    			placeRequest.getInt();
    			placeRequest.getInt();
    			int strlen = placeRequest.getInt();
    			if (strlen <=0)
    				throw new IOException("Invalid response length to launcher lookup for place "+remotePlace);
    			byte[] chars = new byte[strlen];
    			ByteBuffer bb = ByteBuffer.wrap(chars);
    			while (!readNBytes(launcherLink.sc, bb, strlen));
    			connectionInfo = new String(chars);
    			if (DEBUG) System.err.println("Place "+myPlaceId+" lookup of place "+remotePlace+" returned \""+connectionInfo+"\" (len="+strlen+")");
    		}
    		String[] split = connectionInfo.split(":");
    		hostname = split[0];
    		port = Integer.parseInt(split[1]);
    	}
    	
    	InetSocketAddress addr = new InetSocketAddress(hostname, port);
    	SocketChannel sc = null;
    	// wait up to 30 seconds for the remote place to become available.  It may be starting up still.
    	int delay = 30000;
    	do {
	    	try { sc = SocketChannel.open(addr);
	    	} catch (ConnectException e) {
	    		try {
					Thread.sleep(100);
					delay-=100;
					if (delay <= 0 || shuttingDown)
						throw new IOException("Place "+myPlaceId+" unable to connect to place "+remotePlace);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	    	}
    	} while (sc == null);
		
    	String myConnectionInfo = getLocalConnectionInfo();
		byte[] myConnectionInfoBytes = myConnectionInfo.getBytes(Charset.forName(UTF8));
		ByteBuffer controlMsg = ByteBuffer.allocateDirect(20+myConnectionInfoBytes.length);
		if (remotePlace == myPlaceId && myPlaceId != -1)
			controlMsg.order(ByteOrder.nativeOrder()); // the launcher is native code, and probably uses a different endian order
		controlMsg.putInt(CTRL_MSG_TYPE.HELLO.ordinal());
		controlMsg.putInt(remotePlace);
		controlMsg.putInt(myPlaceId);
		if (remotePlace == myPlaceId && myPlaceId != -1) { // send connection details to launcher
			int myPort = localListenSocket.socket().getLocalPort();
			controlMsg.putInt(4);
			// the launcher is expecting an unsigned short, in network order
			controlMsg.put((byte)(myPort >>> 8));
			controlMsg.put((byte)myPort);
			controlMsg.putShort((short)0);
			controlMsg.flip();
			writeNBytes(sc, controlMsg);
			channels.put(myPlaceId, new CommunicationLink(sc, myPlaceId, getLocalConnectionInfo()));
			sc.configureBlocking(false);
			sc.register(selector, SelectionKey.OP_READ);
			if (DEBUG) System.err.println("Place "+myPlaceId+" established a link to local launcher, sent local port="+myPort);
		}
		else {
	    	if (DEBUG) System.out.println("Place "+myPlaceId+" sending connection info of "+myConnectionInfo+" to place "+remotePlace);
	    	controlMsg.putInt(myConnectionInfoBytes.length);
			controlMsg.put(myConnectionInfoBytes);
			controlMsg.flip();
			writeNBytes(sc, controlMsg);
			controlMsg.clear();
			controlMsg = ByteBuffer.allocateDirect(16);
			while (!readNBytes(sc, controlMsg, 16));
			controlMsg.flip();
			if (controlMsg.getInt() == CTRL_MSG_TYPE.HELLO.ordinal()) {
				int toPlace = controlMsg.getInt();
				if (this.myPlaceId == -1 && toPlace > -1) {
					// a place ID was assigned
					this.myPlaceId = toPlace; // save the "to" as my own ID
					this.nplaces = toPlace+1; // I'm the highest place ID, so nplaces is my ID+1
					remotePlace = controlMsg.getInt(); // save the ID of the place we're linked to
					
					channels.put(remotePlace, new CommunicationLink(sc, remotePlace, connectionInfo));
					sc.configureBlocking(false);
					if (socketTimeout != -1) sc.socket().setSoTimeout(socketTimeout);
					sc.register(selector, SelectionKey.OP_READ);
					if (DEBUG) System.err.println("Place "+this.myPlaceId+" established a link to place "+remotePlace+" of "+this.nplaces+" places at "+connectionInfo);

					// now we have one link.  Establish the rest of them
					int datalen = controlMsg.getInt();
					byte[] connectionStringBuffer = new byte[datalen];
					ByteBuffer connectionStringBB = ByteBuffer.wrap(connectionStringBuffer);
					while (!readNBytes(sc, connectionStringBB, datalen));
					String connectionStrings = new String(connectionStringBuffer, UTF8);
					String[] placeStrings = connectionStrings.split(",");
					for (int i=0; i<placeStrings.length; i++) {
						if (DEAD.equals(placeStrings[i]))
							deadPlaces.add(i);
						else if (remotePlace != i)
							initLink(i, placeStrings[i]);
					}
					if (DEBUG) System.err.println("Place "+myPlaceId+" established links to "+placeStrings.length+" additional places");
				}
				else {
					channels.put(remotePlace, new CommunicationLink(sc, remotePlace, connectionInfo));
					sc.configureBlocking(false);
					if (socketTimeout != -1) sc.socket().setSoTimeout(socketTimeout);
					sc.register(selector, SelectionKey.OP_READ);
					if (DEBUG) System.err.println("Place "+this.myPlaceId+" established a link to place "+remotePlace+" of "+this.nplaces+" places at "+connectionInfo);
				}
			}
			else
				System.err.println("Bad response to HELLO");
		}
	}
    
    // simple utility method which forces the read of a specific number of bytes before returning
    // returns true if read ok, or false if nothing was available on the socket to read.
    // throws an exception if the socket is closed
    boolean readNBytes(SocketChannel sc, ByteBuffer data, int bytes) throws IOException {    	
    	int totalBytesRead = 0;
    	int bytesRead = 0;
		do {
			bytesRead+=sc.read(data);
			if (bytesRead > 0) {
				totalBytesRead+=bytesRead;
				bytesRead = 0;
			}
			else if (bytesRead < -100)
				throw new IOException("End of stream");
			else if ((bytesRead == 0 && totalBytesRead == 0) || shuttingDown) // nothing is available to read, but the socket is alive
				return false;
			else if (bufferedWrites) {
				// while we wait for data to come in, flush anything waiting to go out
				x10rt_probe(PROBE_TYPE.ACCEPTORWRITE, 100);
			}
		} while (totalBytesRead < bytes);
		return true;
    }

    private void writeBytes(CommunicationLink link, ByteBuffer data) throws IOException {
    	if (!bufferedWrites) writeNBytes(link.sc, data);
    	else if (!shuttingDown) {
    		if (link.pendingWrites != null) {
    			// data is already pending.  Add this new data to the back of the queue
    			link.pendingWrites.addLast(data); // store the current write request at the end of the queue
    		}
    		else { // nothing pending.  Write immediately
    			long bytesWrittenThisRound;
    			do { bytesWrittenThisRound=link.sc.write(data);
    			} while (bytesWrittenThisRound > 0 && data.hasRemaining());
    			
    			// Did we get the whole message out?
    			if (data.hasRemaining()) {
    				// nope.  Set the buffer aside and register with the selector to write when ready
    				link.pendingWrites = new LinkedList<ByteBuffer>();
    				link.pendingWrites.addLast(data);
    				link.sc.register(selector, (SelectionKey.OP_WRITE | SelectionKey.OP_READ), link.placeid);
    				if (DEBUG) System.err.println("Stashed "+data.remaining()+" bytes in the buffer for place "+link.placeid);
    			}
    		}
    	}
    }
    
    // returns true if at least some data was sent out
    private void flushBufferedBytes(CommunicationLink link) {
    	if (DEBUG) System.err.println("Flushing data");
    	
    	if (!shuttingDown && !deadPlaces.contains(link.placeid) && link.writeLock.tryLock()) {
    		try {
    			if (link.pendingWrites == null) return;
    	    	
    			while (!shuttingDown && link.pendingWrites != null && !link.pendingWrites.isEmpty()) {
    				ByteBuffer data = link.pendingWrites.peekFirst();
    				try {
    					//long startRemain = data.remaining();
	    				long bytesWrittenThisRound;
	        			do { bytesWrittenThisRound=link.sc.write(data);
	        			} while (!shuttingDown && bytesWrittenThisRound > 0 && data.hasRemaining());
	        			
	        			if (DEBUG) System.err.println("Flushed "+bytesWrittenThisRound+" bytes in the buffer to place "+link.placeid);
	        			
	        			if (!shuttingDown && !data.hasRemaining()) // all data was written out
	        				link.pendingWrites.removeFirst();
	        			else // data remains, but the channel is not accepting more
	        				return;
    				}
    				catch (IOException e) {
    					if (DEBUG) System.err.println("Place "+myPlaceId+" discovered link to place "+link.placeid+" is broken in buffer flush");
    		    		try {link.sc.close();}
    		    		catch (Exception e2){}
    		    		markPlaceDead(link.placeid);
    		    		link.pendingWrites = null;
    				}
    				catch (NullPointerException e){} // the remote place died and link was closed by another thread 
    			}
				// at this point, all data has been written out.  Remove the OP_WRITE selector key
    			try {
					link.sc.register(selector, SelectionKey.OP_READ);
				} catch (ClosedChannelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				link.pendingWrites = null;
    		}
    		finally {
    			link.writeLock.unlock();
    		}
    	}
    }
    
    	
    // simple utility method which forces out a specific number of bytes before returning
    private void writeNBytes(SocketChannel sc, ByteBuffer data) throws IOException {    	
		do { sc.write(data);
		} while (!shuttingDown && data.hasRemaining());
    }

    static void runClosureAtReceive(ByteBuffer input) throws IOException {
    	//X10JavaDeserializer deserializer = new X10JavaDeserializer(new DataInputStream(Channels.newInputStream(input)));
    	X10JavaDeserializer deserializer = new X10JavaDeserializer(new DataInputStream(new ByteArrayInputStream(input.array())));
    	VoidFun_0_0 actObj = (VoidFun_0_0) deserializer.readObject();
    	actObj.$apply();
    }
    
    static void runSimpleAsyncAtReceive(ByteBuffer input) throws IOException {
    	//X10JavaDeserializer deserializer = new X10JavaDeserializer(new DataInputStream(Channels.newInputStream(input)));
    	X10JavaDeserializer deserializer = new X10JavaDeserializer(new DataInputStream(new ByteArrayInputStream(input.array())));
    	FinishState finishState = (FinishState) deserializer.readObject();
    	Place src = (Place) deserializer.readObject();
    	VoidFun_0_0 actObj;
    	try {
    	    actObj = (VoidFun_0_0) deserializer.readObject();
    	} catch (Throwable e) {
            finishState.notifyActivityCreation$O(src);
            finishState.pushException(new x10.io.SerializationException(e));
            finishState.notifyActivityTermination();
            return;
    	}
    	x10.lang.Runtime.execute(actObj, src, finishState);
    }
}
