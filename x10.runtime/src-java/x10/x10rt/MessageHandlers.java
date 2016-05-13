/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2016.
 */
package x10.x10rt;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import x10.core.fun.VoidFun_0_1;
import x10.lang.Place;
import x10.network.NetworkTransportCallbacks;
import x10.network.SocketTransport.CALLBACKID;
import x10.runtime.impl.java.Runtime;

/**
 * A class to contain the Java portion of message send/receive pairs
 * for the native X10RT transports (sockets, mpi, ...).
 */
public class MessageHandlers implements NetworkTransportCallbacks {
    
    // values set in native method registerHandlers()
    private static int closureMessageID;
    private static int simpleAsyncMessageID;
    private static int uncountedPutMessageID;
    static VoidFun_0_1<Place> placeAddedHandler = null;
    static VoidFun_0_1<Place> placeRemovedHandler = null;
    private compressionCodec networkCompressor = ("snappy".equalsIgnoreCase(System.getProperty("X10RT_COMPRESSION", "none")))?compressionCodec.SNAPPY:compressionCodec.NONE;

    /**
     * Register the native methods that will invoke runClosureAtReceive
     * and runSimpleAsyncAtReceive as message handlers with the x10rt layer.
     * The message ids obtained from this registration will be stored
     * in the static fields closureMessageId and simpleAsyncMessageId.
     */
    public static synchronized native void registerHandlers();
    
    /**
     * Send an active message using a native X10RT transport.
     */
    private static native void sendMessage(int place, int msg_id, int arraylen, byte[] rawBytes);

	/*
	 * This send/receive pair is used to serialize a ()=>void closure to
	 * a remote place, which will deserialize the closure object and calls apply on it.
	 * 
	 * One important use of this message pair is the non-optimized implementation of
	 * x10.lang.Runtime.runClosureAt and x10.lang.Runtime.runClosureCopyAt. 
	 */
	
    public static void runClosureAtSend(int place, byte[] rawBytes) {
        sendMessage(place, closureMessageID, rawBytes.length, rawBytes);
    }
        
    // Invoked from native code at receiving place
    // This function gets called by the x10rt callback that is registered to handle
    // the receipt of general closures.
    private static void runClosureAtReceive(byte[] args) {
        Runtime.runClosureAtReceive(new ByteArrayInputStream(args));
    }

    /*
     * This send/receive pair is used to serialize a simple async
     * (finish state + async body closure) to
     * a remote place, which deserializes the two objects and invokes the
     * async body. 
     * 
     * This is the "normal" case and is used to implement a typical X10-level async.
     */
    
    public static void runSimpleAsyncAtSend(int place, byte[] rawBytes) { 
        sendMessage(place, simpleAsyncMessageID, rawBytes.length, rawBytes);
    }
    
    /**
     * Receive a simple async
     */
    private static void runSimpleAsyncAtReceive(byte[] args) {
        Runtime.runSimpleAsyncAtReceive(new ByteArrayInputStream(args), false);
    }

    /*
     * This send/receive pair is used to implement an uncountedPut
     */

    public static void uncountedPutSend(int place, byte[] rawBytes) {
        sendMessage(place, uncountedPutMessageID, rawBytes.length, rawBytes);
    }
        
    // Invoked from native code at receiving place
    // This function gets called by the x10rt callback that is registered to handle
    // the receipt of a put message
    private static void uncountedPutReceive(byte[] args) {
        Runtime.uncountedPutReceive(new ByteArrayInputStream(args));
    }

    
    /*
     * Message Dispatcher for JavaSockets transport
     */
    public static void runCallback(int callbackId, ByteBuffer bb) throws IOException {
    	byte[] data;
    	if (bb.hasArray())
    		data = bb.array();
    	else {
    		data = new byte[bb.remaining()];
    		bb.get(data);
    	}
    	
    	if (callbackId == CALLBACKID.closureMessageID.ordinal()) {
			Runtime.runClosureAtReceive(new ByteArrayInputStream(data));
    	} else if (callbackId == CALLBACKID.simpleAsyncMessageID.ordinal()) {
			Runtime.runSimpleAsyncAtReceive(new ByteArrayInputStream(data), true);
    	} else if (callbackId == CALLBACKID.uncountedPutID.ordinal()) {
		    Runtime.uncountedPutReceive(new ByteArrayInputStream(data));
    	} else {
			System.err.println("Unknown message callback type: "+callbackId);
    	}
    }
    
    public void initDataStore(String connectTo) {
    	X10RT.initDataStore(connectTo);
    }
    
    public long getEpoch() {
    	return x10.xrx.Runtime.epoch$O();
    }
    
    public void setEpoch(long val) {
    	X10RT.initialEpoch = val;
    }
    
    public void runPlaceAddedHandler(int placeId) {
    	VoidFun_0_1<Place> handler = placeAddedHandler;
    	if (handler == null) return;
        
    	PlaceChangeWrapper pcw = new PlaceChangeWrapper(handler, placeId);
    	x10.xrx.Runtime.submitUncounted(pcw);
    }
    
    public void runPlaceRemovedHandler(int placeId) {
    	VoidFun_0_1<Place> handler = placeRemovedHandler;
    	if (handler == null) return;
    	
    	PlaceChangeWrapper pcw = new PlaceChangeWrapper(handler, placeId);
    	x10.xrx.Runtime.submitUncounted(pcw);
    }
    
    final public static class PlaceChangeWrapper extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.serialization.X10JavaSerializable
    {
    	private VoidFun_0_1<Place> handler;
    	private long place;
    	
        public PlaceChangeWrapper(VoidFun_0_1<Place> handler, long place) {
        	this.handler = handler;
        	this.place = place;
        }

        @SuppressWarnings("unchecked")
        public static final x10.rtt.RuntimeType<PlaceChangeWrapper> $RTT = 
            x10.rtt.StaticVoidFunType.<PlaceChangeWrapper> make(PlaceChangeWrapper.class, new x10.rtt.Type[] { x10.core.fun.VoidFun_0_0.$RTT });
        
        public x10.rtt.RuntimeType<?> $getRTT() { return $RTT; }
        
        public x10.rtt.Type<?> $getParam(int i) { return null; }
        
        private Object writeReplace() throws java.io.ObjectStreamException {
            return new x10.serialization.SerializationProxy(this);
        }
        
        public static x10.serialization.X10JavaSerializable $_deserialize_body(x10.x10rt.MessageHandlers.PlaceChangeWrapper $_obj, x10.serialization.X10JavaDeserializer $deserializer) throws java.io.IOException {
        	$_obj.handler = $deserializer.readObject();
            $_obj.place = $deserializer.readLong();
            return $_obj;
        }
        
        public static x10.serialization.X10JavaSerializable $_deserializer(x10.serialization.X10JavaDeserializer $deserializer) throws java.io.IOException {
        	x10.x10rt.MessageHandlers.PlaceChangeWrapper $_obj = new x10.x10rt.MessageHandlers.PlaceChangeWrapper((java.lang.System[]) null);
            $deserializer.record_reference($_obj);
            return $_deserialize_body($_obj, $deserializer);
        }
        
        public void $_serialize(x10.serialization.X10JavaSerializer $serializer) throws java.io.IOException {
        	$serializer.write(this.handler);
        	$serializer.write(this.place);
        }
        
        // constructor just for allocation
        public PlaceChangeWrapper(final java.lang.System[] $dummy) {}
        
		@Override
		public void $apply() {
			Place p = new Place(this.place);
			this.handler.$apply(p, p.$getRTT());
		}
    }

	@Override
	public compressionCodec useCompressionCodec() {
		return networkCompressor;
	}
}
