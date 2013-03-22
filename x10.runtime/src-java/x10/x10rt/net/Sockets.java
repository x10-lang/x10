package x10.x10rt.net;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import x10.lang.FinishState;
import x10.serialization.X10JavaDeserializer;

public class Sockets {
	private static final boolean DEBUG = false;
	public static final String X10_FORCEPORTS = "X10_FORCEPORTS";
	public static final String X10_LAUNCHER_PLACE = "X10_LAUNCHER_PLACE";
	public static final String X10_NPLACES = "X10_NPLACES";
	public static final String X10_LAUNCHER_PARENT = "X10_LAUNCHER_PARENT";
	private static enum CTRL_MSG_TYPE {HELLO, GOODBYE, PORT_REQUEST, PORT_RESPONSE};
	private static enum MSGTYPE {STANDARD, PUT, GET, GET_COMPLETED};
	public static enum CALLBACKID {closureMessageID, closureMessageNoDictionaryID, simpleAsyncMessageID, simpleAsyncMessageNoDictionaryID};
	public static enum RETURNCODE {
	    X10RT_ERR_OK,   /* No error */
	    X10RT_ERR_MEM,   /* Out of memory error */
	    X10RT_ERR_INVALID,   /* Invalid method call, at this time (e.g. probe() before init()) */
	    X10RT_ERR_UNSUPPORTED,   /* Not supported by this implementation of X10RT */
	    X10RT_ERR_INTL, /* Internal implementation error */
	    X10RT_ERR_OTHER /* Other unclassified runtime error */
	};
	
	private int nplaces = 1; // number of places
	private int myPlaceId = 0; // my place ID
	private ServerSocketChannel localListenSocket = null;
	private SocketChannel channels[] = null; // communication links to remote places, and launcher at [myPlaceId]
	private Selector selector = null;
	
	
	public Sockets() {
		String nplacesFlag = System.getenv(X10_NPLACES);
		if (nplacesFlag != null) {
			nplaces = Integer.parseInt(nplacesFlag);
			channels = new SocketChannel[nplaces];
		}
		else 
			channels = new SocketChannel[1];
		
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
					initLink(myPlaceId, launcherLocation);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (DEBUG) System.out.println("Socket library initialized");
	}
	
	public String getLocalConnectionInfo() {
		int port = localListenSocket.socket().getLocalPort();
		String hostname = localListenSocket.socket().getInetAddress().getHostName();
		return hostname+":"+port;
	}
	
	public int establishLinks() {
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
				while (channels[i] == null)
					x10rt_probe(true, 0); // wait for connections from all upper places
		}
	
		return RETURNCODE.X10RT_ERR_OK.ordinal();
	}
	
    public int establishLinks(int myPlaceId, String[] connectionStrings) {
    	if (myPlaceId != 0 || nplaces != 1) 
    		return RETURNCODE.X10RT_ERR_INVALID.ordinal();
    		
    	this.myPlaceId = myPlaceId;
    	this.nplaces = connectionStrings.length;
    	if (channels.length == 1 && channels[0] != null) {
    		// save the launcher link
    		SocketChannel ll = channels[0];
    		channels = new SocketChannel[nplaces];
    		channels[myPlaceId] = ll;
    	}
    	else
    		channels = new SocketChannel[nplaces];
    	
    	for (int i=0; i<myPlaceId; i++) {
			try {
				initLink(i, connectionStrings[i]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // connect to all lower places
    	}
		for (int i=myPlaceId+1; i<nplaces; i++)
			while (channels[i] == null)
				x10rt_probe(true, 0); // wait for connections from all upper places
		
    	return RETURNCODE.X10RT_ERR_OK.ordinal();
    }
    
    public int shutdown() {
    	if (DEBUG) System.out.println("shutting down");
   		try {
   			if (localListenSocket != null)
    			localListenSocket.close();
   			if (channels != null) {
	   		   	for (int i=0; i<channels.length; i++) {
	    			if (channels[i] != null) {
	    				channels[i].close();
	    				channels[i] = null;
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
    
    private boolean x10rt_probe(boolean onlyProcessAccept, long timeout) {
    	int eventCount = 0;
    	try {
    		SelectionKey key;
    		synchronized (selector) {
    			eventCount = selector.select(timeout);
    			if (eventCount == 0) return false;
    			
    			Iterator<SelectionKey> it = selector.selectedKeys().iterator();
    			key = it.next();
    			it.remove();
    		}
			if (key.isAcceptable()) {
				ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
				SocketChannel sc = ssc.accept();
				if (sc == null)
					return false; // nothing actually here
				int remote = -1;
				
				ByteBuffer controlMsg = ByteBuffer.allocateDirect(16);
				Sockets.readNBytes(sc, controlMsg, controlMsg.capacity());
				controlMsg.flip();
				int msgtype = controlMsg.getInt();
				if (CTRL_MSG_TYPE.HELLO.ordinal() == msgtype) {
					int to = controlMsg.getInt();
					if (to == myPlaceId) {
						remote = controlMsg.getInt();
						controlMsg.clear();
						controlMsg.putInt(CTRL_MSG_TYPE.HELLO.ordinal());
						controlMsg.putInt(remote);
						controlMsg.putInt(myPlaceId);
						controlMsg.putInt(0);
						controlMsg.flip();
						Sockets.writeNBytes(sc, controlMsg, controlMsg.capacity());
						channels[remote] = sc;
						sc.configureBlocking(false);
						sc.register(selector, SelectionKey.OP_READ);
						if (DEBUG) System.out.println("Place "+myPlaceId+" accepted a connection from place "+remote);
				}	}
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
			if (key.isReadable()) {
				if (DEBUG) System.out.println("Detected incoming messaged");
				SocketChannel sc = (SocketChannel) key.channel();
				ByteBuffer controlData = ByteBuffer.allocateDirect(12);
				Sockets.readNBytes(sc, controlData, controlData.capacity());
				controlData.flip(); // switch from write to read mode
				// Format: type, p.type, p.len, p.msg
				int msgType = controlData.getInt();
				if (msgType == MSGTYPE.STANDARD.ordinal()) {
					int callbackId = controlData.getInt();
					int datalen = controlData.getInt(); // not needed here, because we don't create an intermediate buffer :-)
					if (DEBUG) System.out.println("Processing an incoming message of type "+callbackId+" and size "+datalen);
					
					//TODO - eliminate this buffer by modifying the deserializer to take the channel as input
					ByteBuffer bb = ByteBuffer.allocate(datalen);
					Sockets.readNBytes(sc, bb, datalen);
					bb.flip();

					if (callbackId == CALLBACKID.closureMessageID.ordinal())
						Sockets.runClosureAtReceive(bb, true);
					else if (callbackId == CALLBACKID.closureMessageNoDictionaryID.ordinal())
						Sockets.runClosureAtReceive(bb, false);
					else if (callbackId == CALLBACKID.simpleAsyncMessageID.ordinal())
						Sockets.runSimpleAsyncAtReceive(bb, true);
					else if (callbackId == CALLBACKID.simpleAsyncMessageNoDictionaryID.ordinal())
						Sockets.runSimpleAsyncAtReceive(bb, false);
					else
						System.err.println("Unknown message callback type: "+callbackId);
					
					if (DEBUG) System.out.println("Finished processing message type "+callbackId+" and size "+datalen);
				}
				else 
					System.err.println("Unknown message type: "+msgType);
				// TODO GET & PUT message types
				return true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
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
    
    public int sendMessage(int place, int msg_id, ByteBuffer[] bytes) throws IOException {
   		initLink(place, null);
    	
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
    		System.out.print("Sending a message to place "+place+" of type "+msg_id+" and size "+len+"...");
    		System.out.flush();
    	}
    	Sockets.writeNBytes(channels[place], controlData, controlData.capacity());
		//channels[place].write(controlData);
    	if (bytes != null)
    		for (int i=0; i<bytes.length; i++)
    			Sockets.writeNBytes(channels[place], bytes[i], bytes[i].remaining());
		if (DEBUG) System.out.println("Sent");
		
    	return RETURNCODE.X10RT_ERR_OK.ordinal();
    }
    
    private void initLink(int remotePlace, String connectionInfo) throws IOException{
    	if (channels[remotePlace] != null) return;
    	
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
    			Sockets.writeNBytes(channels[myPlaceId], placeRequest, placeRequest.capacity());
    			placeRequest.clear();
    			Sockets.readNBytes(channels[myPlaceId], placeRequest, placeRequest.capacity());
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
    			Sockets.readNBytes(channels[myPlaceId], bb, strlen);
    			connectionInfo = new String(chars);
    			if (DEBUG) System.out.println("Lookup of place "+remotePlace+" returned \""+connectionInfo+"\" (len="+strlen+")");
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
					if (delay <= 0)
						throw new IOException("Unable to connect to place "+remotePlace);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	    	}
    	} while (sc == null);
		
		ByteBuffer controlMsg = ByteBuffer.allocateDirect(20);
		if (remotePlace == myPlaceId)
			controlMsg.order(ByteOrder.nativeOrder()); // the launcher is native code, and probably uses a different endian order
		controlMsg.putInt(CTRL_MSG_TYPE.HELLO.ordinal());
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
			Sockets.writeNBytes(sc, controlMsg, 20);
			channels[myPlaceId] = sc;
			sc.configureBlocking(false);
			sc.register(selector, SelectionKey.OP_READ);
			if (DEBUG) System.out.println("Place "+myPlaceId+" established a link to local launcher, sent local port="+myPort);
		}
		else {
			controlMsg.putInt(0);
			controlMsg.flip();
			Sockets.writeNBytes(sc, controlMsg, 16);
			controlMsg.clear();
			Sockets.readNBytes(sc, controlMsg, 16);
			controlMsg.flip();
			if (controlMsg.getInt() == CTRL_MSG_TYPE.HELLO.ordinal()) {
				channels[remotePlace] = sc;
				sc.configureBlocking(false);
				sc.register(selector, SelectionKey.OP_READ);
				if (DEBUG) System.out.println("Place "+myPlaceId+" established a link to place "+remotePlace);
			}
			else
				System.err.println("Bad response to HELLO");
		}
	}
    
    // simple utility method which forces the read of a specific number of bytes before returning
    static void readNBytes(SocketChannel sc, ByteBuffer data, int bytes) throws IOException {
    	int bytesRead = 0;
		do { bytesRead+=sc.read(data);
		} while (bytesRead < bytes);
    }
    
    // simple utility method which forces out a specific number of bytes before returning
    static void writeNBytes(SocketChannel sc, ByteBuffer data, int bytes) throws IOException {
    	int bytesWritten = 0;
		do { bytesWritten+=sc.write(data);
		} while (bytesWritten < bytes);
    }

    private static void runClosureAtReceive(ByteBuffer input, boolean messageHasDictionary) throws IOException {
    	//X10JavaDeserializer deserializer = new X10JavaDeserializer(new DataInputStream(Channels.newInputStream(input)), messageHasDictionary);
    	X10JavaDeserializer deserializer = new X10JavaDeserializer(new DataInputStream(new ByteArrayInputStream(input.array())), messageHasDictionary);
    	x10.core.fun.VoidFun_0_0 actObj = (x10.core.fun.VoidFun_0_0) deserializer.readRef();
    	actObj.$apply();
    }
    
    private static void runSimpleAsyncAtReceive(ByteBuffer input, boolean messageHasDictionary) throws IOException {
    	//X10JavaDeserializer deserializer = new X10JavaDeserializer(new DataInputStream(Channels.newInputStream(input)), messageHasDictionary);
    	X10JavaDeserializer deserializer = new X10JavaDeserializer(new DataInputStream(new ByteArrayInputStream(input.array())), messageHasDictionary);
    	FinishState finishState = (FinishState) deserializer.readRef();
    	x10.core.fun.VoidFun_0_0 actObj = (x10.core.fun.VoidFun_0_0) deserializer.readRef();
    	x10.lang.Runtime.execute(actObj, finishState);
    }
}