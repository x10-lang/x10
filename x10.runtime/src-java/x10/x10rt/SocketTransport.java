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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import x10.core.fun.VoidFun_0_0;
import x10.lang.FinishState;
import x10.lang.Place;
import x10.serialization.X10JavaDeserializer;
import x10.x10rt.X10RT.State;

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
	private static enum CTRL_MSG_TYPE {HELLO, CONFIGURE, GOODBYE, PORT_REQUEST, PORT_RESPONSE};
	private static enum MSGTYPE {STANDARD, PUT, GET, GET_COMPLETED};
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
    	private CommunicationLink(SocketChannel sc) {
			super();
			this.sc = sc;
			this.writeLock = new ReentrantLock();
			this.pendingWrites = null;
		}
		SocketChannel sc;
		ReentrantLock writeLock;
		LinkedList<ByteBuffer> pendingWrites;
    }
	
	private int nplaces = 1; // number of places
	private int myPlaceId = 0; // my place ID
	private ServerSocketChannel localListenSocket = null;
	private CommunicationLink channels[] = null; // communication links to remote places, and launcher at [myPlaceId]
	private Selector selector = null;
	private Iterator<SelectionKey> events = null;
	private AtomicInteger numDead = new AtomicInteger(0);
	private boolean bufferedWrites = true;
	private int socketTimeout = -1;
	private boolean shuttingDown = false;
    
	
	public SocketTransport() {
		String nplacesFlag = System.getenv(X10_NPLACES);
		if (nplacesFlag != null) {
			nplaces = Integer.parseInt(nplacesFlag);
			channels = new CommunicationLink[nplaces];
		}
		else
			channels = new CommunicationLink[1];
		
		String placeFlag = System.getenv(X10_LAUNCHER_PLACE);
		if (placeFlag != null) myPlaceId = Integer.parseInt(placeFlag);
		
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
					initLink(myPlaceId, launcherLocation, null);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (Boolean.parseBoolean(System.getenv(X10_NOWRITEBUFFER)) || Boolean.parseBoolean(System.getProperty(X10_NOWRITEBUFFER)))
			bufferedWrites = false;
		
		try {
			socketTimeout = Integer.parseInt(System.getProperty(X10_SOCKET_TIMEOUT));
		}
		catch (NumberFormatException e){} // not set.		
		
		if (DEBUG) System.err.println("Socket library initialized");
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
	
	public int establishLinks() {
		if (nplaces > 1) {
			for (int i=0; i<myPlaceId; i++) {
				try {
					initLink(i, null, null);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // connect to all lower places
			}
			for (int i=myPlaceId+1; i<nplaces; i++)
				while (!shuttingDown && channels[i] == null)
					x10rt_probe(true, 0); // wait for connections from all upper places
		} 
		else {
			try {
				if (localListenSocket != null)
					localListenSocket.close();
			} catch (IOException e) {}
		}
	
		return RETURNCODE.X10RT_ERR_OK.ordinal();
	}
	
    public int establishLinks(int myPlaceId, String[] connectionStrings, boolean remoteStart) {
    	if (DEBUG) System.err.println("Place "+myPlaceId+" establishing links.  id="+this.myPlaceId+" np="+this.nplaces);
    	if (this.myPlaceId != 0 || this.nplaces != 1) // make we are in the right state to establish links
    		return RETURNCODE.X10RT_ERR_INVALID.ordinal();
    		
    	this.myPlaceId = myPlaceId;
    	if (connectionStrings != null && connectionStrings.length > 1)
    		this.nplaces = connectionStrings.length;
    	else {
    		// single place.  No need to establish any links.
    		this.nplaces = 1;
    		if (localListenSocket != null) {
    			try {
					localListenSocket.close();
				} catch (IOException e) {}
    		}
    		return RETURNCODE.X10RT_ERR_OK.ordinal();
    	}
    	if (channels.length == 1 && channels[0] != null) {
    		// save the launcher link
    		CommunicationLink ll = channels[0];
    		channels = new CommunicationLink[nplaces];
    		channels[myPlaceId] = ll;
    	}
    	else
    		channels = new CommunicationLink[nplaces];
    	
    	if (shuttingDown)
    		return RETURNCODE.X10RT_ERR_OTHER.ordinal();
    	else if (remoteStart) {
    		StringBuffer sb = new StringBuffer();
    		for (int i=0; i<connectionStrings.length; i++) {
    			sb.append(connectionStrings[i]);
    			sb.append(',');
    		}
    		ByteBuffer allPlaces = ByteBuffer.wrap(sb.toString().getBytes(Charset.forName("UTF-8")));

    		for (int i=0; i<nplaces; i++) {
    			if (i == myPlaceId) continue; // skip myself
				try {
					initLink(i, connectionStrings[i], allPlaces);
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	}
    	}
    	else {
	    	for (int i=0; i<myPlaceId; i++) {
				try {
					initLink(i, connectionStrings[i], null);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // connect to all lower places
	    	}
			for (int i=myPlaceId+1; i<nplaces; i++)
				while (!shuttingDown && channels[i] == null)
					x10rt_probe(true, 0); // wait for connections from all upper places
	    }
    	
    	return RETURNCODE.X10RT_ERR_OK.ordinal();
    }
    
    public int numDead() {
    	return numDead.get();
    }
    
    public boolean isPlaceDead(int place) {
    	if (place < 0 || place >= channels.length)
    		return true;
    	
    	return (channels[place] != null && channels[place].sc == null);
    }
    
    // this method will cause the establishLinks method to return, even if it is 
    // waiting for links to be established.  This allows that thread to get unstuck
    // in the event that the place list is invalid.
    public int shutdown() {
    	if (DEBUG) System.err.println("shutting down");
    	shuttingDown = true;
    	bufferedWrites = false;
   		try {
   			if (localListenSocket != null)
    			localListenSocket.close();
   			if (channels != null) {
	   		   	for (int i=0; i<channels.length; i++) {
	    			if (channels[i] != null) {
	    				channels[i].writeLock.lock();
	    				try {
	    					if (channels[i].sc != null) {
	    						 channels[i].sc.close();
	    						 channels[i].sc = null;
	    					}
	    				}
	    				finally {
	    					channels[i].writeLock.unlock();
	    					channels[i].pendingWrites = null;
	    				}
   			}	}	}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	myPlaceId = 0;
    	nplaces = 1;
    	return RETURNCODE.X10RT_ERR_OK.ordinal();
    }

    public int x10rt_nplaces(){
    	return nplaces;
    }
        
    public int x10rt_here(){
    	return myPlaceId;
    }
    
    // If a thread is blocked on the blocking_probe, wake it up so that it can return.  If nothing is blocked, 
    // the next call to probe will not block.
    public void wakeup() {
    	selector.wakeup();
    }
    
    // onlyProcessAccept is set to true only during startup time, to prioritize establishing links
    // timeout is how long we're willing to block waiting for something to happen. 
    // returns true if something is processed
    boolean x10rt_probe(boolean onlyProcessAccept, long timeout) {
    	if (!onlyProcessAccept && nplaces == 1)
    		return false;
    	
    	int eventCount = 0;
    	try {
    		SelectionKey key;
    		synchronized (selector) {
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
						if (remote < channels.length) {
							if (DEBUG) System.err.println("Incoming HELLO message to "+to+" from "+remote);
							controlMsg.clear();
							controlMsg.putInt(CTRL_MSG_TYPE.HELLO.ordinal());
							controlMsg.putInt(remote);
							controlMsg.putInt(myPlaceId);
							controlMsg.putInt(0);
							controlMsg.flip();
							writeNBytes(sc, controlMsg);
							channels[remote] = new CommunicationLink(sc);
							sc.configureBlocking(false);
							if (socketTimeout != -1) sc.socket().setSoTimeout(socketTimeout);
							sc.register(selector, SelectionKey.OP_READ);
							if (DEBUG) System.err.println("Place "+myPlaceId+" accepted a connection from place "+remote);
						}
						else {
							remote = -1;
							if (DEBUG) System.err.println("Incoming HELLO message from "+remote+" dropped because "+remote+" is an unknown place ID");
						}
					}
					else if (DEBUG) 
						System.err.println("Incoming HELLO message to place "+to+" ignored, because my place is ."+myPlaceId);
				}
				else if (CTRL_MSG_TYPE.CONFIGURE.ordinal() == msgtype) {
					int mynewid = controlMsg.getInt();
					if (onlyProcessAccept) {
						remote = controlMsg.getInt();
						if (DEBUG) System.err.println("Incoming CONFIGURE message to "+mynewid+" from "+remote);
						// read in the list of host:port,host:port,host:port etc for all places
						int datalen = controlMsg.getInt();
						byte[] chars = new byte[datalen];
						ByteBuffer placeList = ByteBuffer.wrap(chars);
						while (!readNBytes(sc, placeList, datalen)){}
		    			String allPlaces = new String(chars, Charset.forName("UTF-8"));
		    			String[] places = allPlaces.split(",");
		    			controlMsg.clear();						
		    			if (DEBUG) System.err.println("Recieved place list: "+allPlaces);
		    			
						controlMsg.putInt(CTRL_MSG_TYPE.HELLO.ordinal());
						controlMsg.putInt(remote);
						controlMsg.putInt(mynewid);
						controlMsg.putInt(0);
						controlMsg.flip();
						writeNBytes(sc, controlMsg);

						// configure myself
						this.myPlaceId = mynewid;
				    	this.nplaces = places.length;
				    	if (channels.length == 1 && channels[0] != null) {
				    		// save the launcher link
				    		CommunicationLink ll = channels[0];
				    		channels = new CommunicationLink[nplaces];
				    		channels[myPlaceId] = ll;
				    	}
				    	else
				    		channels = new CommunicationLink[nplaces];
				    	
				    	channels[remote] = new CommunicationLink(sc);
						sc.configureBlocking(false);
						sc.register(selector, SelectionKey.OP_READ);
						
						// establish remote links to lower-numbered places
						for (int i=0; i<myPlaceId; i++) {
							if (i == remote) continue;
							try {
								initLink(i, places[i], null);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
				    	}
			    	
						// configure X10RT from the bottom up, instead of top-down
						X10RT.here = this.myPlaceId;
						X10RT.numPlaces = this.nplaces;
						x10.runtime.impl.java.Runtime.MAX_PLACES = this.nplaces;
				        X10RT.state = State.RUNNING;
						
						if (DEBUG) System.err.println("X10RT reconfigured as place "+myPlaceId+" of "+nplaces+" places");
					}
					else if (DEBUG) 
						System.err.println("Recieved a CONFIGURE message, but we're already configured.  Ignored.");
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
			if (onlyProcessAccept) return false;
			if (key.isWritable()) {
				Integer place = (Integer)key.attachment();
				if (place != null && !isPlaceDead(place.intValue()))
					flushBufferedBytes(key, place.intValue());
			}
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
						if (DEBUG) {
							System.out.print("Place "+myPlaceId+" processing an incoming message of type "+callbackId+" and size "+datalen+"...");
							System.out.flush();
						}
						//TODO - eliminate this buffer by modifying the deserializer to take the channel as input
						bb = ByteBuffer.allocate(datalen);
						while (!readNBytes(sc, bb, datalen)){}
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
					else 
						System.err.println("Unknown message type: "+msgType);
				}
				catch (IOException e) {
					// figure out which place this is
					for (int i=0; i<channels.length; i++) {
						try {
							if (channels[i] != null && sc.equals(channels[i].sc)) {
								if (DEBUG) System.err.println("Place "+myPlaceId+" discovered link to place "+i+" is broken in probe");
								channels[i].sc = null;
								channels[i].pendingWrites = null;
								break;
							}
						} catch (NullPointerException e2){} // channels[i] can become null after we check for null
					}
					try {sc.close();}
		    		catch (Exception e2){}
		    		numDead.incrementAndGet();
		    		return false;
				}
				// TODO GET & PUT message types
				return true;
			}
			else if (DEBUG)
				System.err.println("Unhandled key type in probe: "+ key);
		} catch (CancelledKeyException e) {
			// a key may be cancelled on us if the runtime disconnects while there is active communication
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
    }
    
    public int x10rt_probe() {
    	boolean somethingProcessed;
    	do somethingProcessed = x10rt_probe(false, 1);
    	while (somethingProcessed);
    	
    	return RETURNCODE.X10RT_ERR_OK.ordinal();
    }
    
    public int x10rt_blocking_probe() {
    	x10rt_probe(false, 0);
    	return RETURNCODE.X10RT_ERR_OK.ordinal();
    }
    
    public int sendMessage(int place, int msg_id, ByteBuffer[] bytes) {
    	if (numDead.get() == 0) {// don't try to re-establish links after we find dead ones, or we'll get into a loop
    		try {
    			initLink(place, null, null);
    		} catch (IOException e) {
    			return RETURNCODE.X10RT_ERR_OTHER.ordinal();
    		}
    	}
    	else if (channels[place] == null || channels[place].sc == null) // don't send messages to dead or uninitialized places
    		return RETURNCODE.X10RT_ERR_OTHER.ordinal();
    	
    	// write out the x10SocketMessage data
    	// Format: type, p.type, p.len, p.msg
    	ByteBuffer controlData = ByteBuffer.allocateDirect(12);
    	controlData.putInt(MSGTYPE.STANDARD.ordinal());
    	controlData.putInt(msg_id);
    	int len = 0;
    	if (bytes != null)
    		for (int i=0; i<bytes.length; i++)
    			len+=bytes[i].remaining();
    	controlData.putInt(len);
    	controlData.flip();
    	if (DEBUG) {
    		System.out.print("Place "+myPlaceId+" sending a message to place "+place+" of type "+msg_id+" and size "+len+"...");
    		System.out.flush();
    	}
    	try {
	    	channels[place].writeLock.lock();
	    	try {
		    	writeBytes(place, controlData);
		    	if (bytes != null)
		    		for (int i=0; i<bytes.length; i++)
		    			writeBytes(place, bytes[i]);
				if (DEBUG) System.err.println("Sent");
	    	} 
	    	finally {
	    		channels[place].writeLock.unlock();
	    	}
    	}
    	catch (IOException e) {
    		if (DEBUG) System.err.println("Place "+myPlaceId+" discovered link to place "+place+" is broken in send");
    		try {channels[place].sc.close();}
    		catch (Exception e2){}
    		channels[place].sc = null;
    		channels[place].pendingWrites = null;
    		numDead.incrementAndGet();
    		return RETURNCODE.X10RT_ERR_OTHER.ordinal();
    	}
		
    	return RETURNCODE.X10RT_ERR_OK.ordinal();
    }
    
    private void initLink(int remotePlace, String connectionInfo, ByteBuffer allPlaces) throws IOException{
    	if (channels[remotePlace] != null || shuttingDown) return;
    	
    	String hostname;
    	int port;
    	
    	if (connectionInfo == null && channels[myPlaceId] == null) {
    		String forcePortsFlag = System.getenv(X10_FORCEPORTS);
    		if (forcePortsFlag == null) throw new IOException("Unknown location for place "+remotePlace);
    		hostname = "localhost";		
    		port = Integer.parseInt(forcePortsFlag)+remotePlace;
    	}
    	else {
    		if (connectionInfo == null) {
    			// ask the launcher
    			ByteBuffer placeRequest = ByteBuffer.allocateDirect(16);
    			placeRequest.order(ByteOrder.nativeOrder());
    			placeRequest.putInt(CTRL_MSG_TYPE.PORT_REQUEST.ordinal());
    			placeRequest.putInt(remotePlace);
    			placeRequest.putInt(myPlaceId);
    			placeRequest.putInt(0);
    			placeRequest.flip();
    			writeNBytes(channels[myPlaceId].sc, placeRequest);
    			placeRequest.clear();
    			while (!readNBytes(channels[myPlaceId].sc, placeRequest, placeRequest.capacity())){}
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
    			while (!readNBytes(channels[myPlaceId].sc, bb, strlen)){}
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
		
		ByteBuffer controlMsg = ByteBuffer.allocateDirect(20);
		if (remotePlace == myPlaceId)
			controlMsg.order(ByteOrder.nativeOrder()); // the launcher is native code, and probably uses a different endian order
		if (null == allPlaces)
			controlMsg.putInt(CTRL_MSG_TYPE.HELLO.ordinal());
		else
			controlMsg.putInt(CTRL_MSG_TYPE.CONFIGURE.ordinal());
		controlMsg.putInt(remotePlace);
		controlMsg.putInt(myPlaceId);
		if (remotePlace == myPlaceId) { // send connection details to launcher
			int myPort = localListenSocket.socket().getLocalPort();
			controlMsg.putInt(4);
			// the launcher is expecting an unsigned short, in network order
			controlMsg.put((byte)(myPort >>> 8));
			controlMsg.put((byte)myPort);
			controlMsg.putShort((short)0);
			controlMsg.flip();
			writeNBytes(sc, controlMsg);
			channels[myPlaceId] = new CommunicationLink(sc);
			sc.configureBlocking(false);
			sc.register(selector, SelectionKey.OP_READ);
			if (DEBUG) System.err.println("Place "+myPlaceId+" established a link to local launcher, sent local port="+myPort);
		}
		else {
			if (null == allPlaces)
				controlMsg.putInt(0);
			else
				controlMsg.putInt(allPlaces.remaining());			
			controlMsg.flip();
			writeNBytes(sc, controlMsg);
			if (null != allPlaces) {
				writeNBytes(sc, allPlaces);
				allPlaces.rewind();
			}
			controlMsg.clear();
			while (!readNBytes(sc, controlMsg, 16)){}
			controlMsg.flip();
			if (controlMsg.getInt() == CTRL_MSG_TYPE.HELLO.ordinal()) {
				channels[remotePlace] = new CommunicationLink(sc);
				sc.configureBlocking(false);
				if (socketTimeout != -1) sc.socket().setSoTimeout(socketTimeout);
				sc.register(selector, SelectionKey.OP_READ);
				if (DEBUG) System.err.println("Place "+myPlaceId+" established a link to place "+remotePlace+" and sent place info");
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
    	int flush=0;
		do {
			bytesRead+=sc.read(data);
			if (bytesRead > 0) {
				totalBytesRead+=bytesRead;
				bytesRead = 0;
			}
			else if (bytesRead < -100)
				throw new IOException("End of stream");
			else if (totalBytesRead == 0 || shuttingDown) // nothing is available to read, but the socket is alive
				return false;
			else if (bufferedWrites) {
				// while we wait for data to come in, flush anything waiting to go out
				if (flush==channels.length)
					flush=0;
				for (; flush<channels.length; flush++) {
		    		if (!isPlaceDead(flush) && flushBufferedBytes(null, flush))
		    			break;
		    	}
			}
		} while (totalBytesRead < bytes);
		return true;
    }

    private void writeBytes(int placeid, ByteBuffer data) throws IOException {
    	if (!bufferedWrites)
    		writeNBytes(channels[placeid].sc, data);
    	else if (!shuttingDown) {
    		if (channels[placeid].pendingWrites != null) {
    			// data is already pending.  Add this new data to the back of the queue
    			channels[placeid].pendingWrites.addLast(data); // store the current write request at the end of the queue
    		}
    		else { // nothing pending.  Write immediately
    			long bytesWrittenThisRound;
    			do { bytesWrittenThisRound=channels[placeid].sc.write(data);
    			} while (bytesWrittenThisRound > 0 && data.hasRemaining());
    			
    			// Did we get the whole message out?
    			if (data.hasRemaining()) {
    				// nope.  Set the buffer aside and register with the selector to write when ready
    				channels[placeid].pendingWrites = new LinkedList<ByteBuffer>();
    				channels[placeid].pendingWrites.addLast(data);
    				channels[placeid].sc.register(selector, (SelectionKey.OP_WRITE | SelectionKey.OP_READ), placeid);
    				if (DEBUG) System.err.println("Stashed "+data.remaining()+" bytes in the buffer for place "+placeid);
    			}
    		}
    	}
    }
    
    // returns true if at least some data was sent out
    private boolean flushBufferedBytes(SelectionKey key, int placeid) {
    	if (DEBUG) System.err.println("Flush called for place "+placeid);
    	
    	if (!shuttingDown && channels[placeid] != null && channels[placeid].writeLock.tryLock()) {
    		try {
    			if (channels[placeid].pendingWrites == null) return false;
    	    	
    			while (!shuttingDown && channels[placeid].pendingWrites != null && !channels[placeid].pendingWrites.isEmpty()) {
    				ByteBuffer data = channels[placeid].pendingWrites.peekFirst();
    				try {
    					//long startRemain = data.remaining();
	    				long bytesWrittenThisRound;
	        			do { bytesWrittenThisRound=channels[placeid].sc.write(data);
	        			} while (!shuttingDown && bytesWrittenThisRound > 0 && data.hasRemaining());
	        			
	        			//if (DEBUG) System.err.println("Flushed "+(startRemain-data.remaining())+" bytes in the buffer to place "+placeid);
	        			
	        			if (!shuttingDown && !data.hasRemaining()) // all data was written out
	        				channels[placeid].pendingWrites.removeFirst();
	        			else
	        				return true; //(startRemain==data.remaining()); // data remains, but the channel is not accepting more
    				}
    				catch (IOException e) {
    					if (DEBUG) System.err.println("Place "+myPlaceId+" discovered link to place "+placeid+" is broken in buffer flush");
    		    		try {channels[placeid].sc.close();}
    		    		catch (Exception e2){}
    		    		channels[placeid].sc = null;
    		    		channels[placeid].pendingWrites = null;
    		    		numDead.incrementAndGet();
    				}
    				catch (NullPointerException e){} // the remote place died and link was closed by another thread 
    			}
				// at this point, all data has been written out.  Remove the OP_WRITE selector key
    			try {
					channels[placeid].sc.register(selector, SelectionKey.OP_READ);
				} catch (ClosedChannelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				channels[placeid].pendingWrites = null;
    		}
    		finally {
    			channels[placeid].writeLock.unlock();
    		}
    		return true;
    	}
    	return false;
    }
    
    	
    // simple utility method which forces out a specific number of bytes before returning
    private void writeNBytes(SocketChannel sc, ByteBuffer data) throws IOException {    	
		do { sc.write(data);
		} while (!shuttingDown && data.hasRemaining());
    }

    private static void runClosureAtReceive(ByteBuffer input) throws IOException {
    	//X10JavaDeserializer deserializer = new X10JavaDeserializer(new DataInputStream(Channels.newInputStream(input)));
    	X10JavaDeserializer deserializer = new X10JavaDeserializer(new DataInputStream(new ByteArrayInputStream(input.array())));
    	VoidFun_0_0 actObj = (VoidFun_0_0) deserializer.readObject();
    	actObj.$apply();
    }
    
    private static void runSimpleAsyncAtReceive(ByteBuffer input) throws IOException {
    	//X10JavaDeserializer deserializer = new X10JavaDeserializer(new DataInputStream(Channels.newInputStream(input)));
    	X10JavaDeserializer deserializer = new X10JavaDeserializer(new DataInputStream(new ByteArrayInputStream(input.array())));
    	FinishState finishState = (FinishState) deserializer.readObject();
    	Place src = (Place) deserializer.readObject();
    	VoidFun_0_0 actObj = (VoidFun_0_0) deserializer.readObject();
    	x10.lang.Runtime.execute(actObj, src, finishState);
    }
}
