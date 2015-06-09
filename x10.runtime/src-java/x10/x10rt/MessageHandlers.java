/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */
package x10.x10rt;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import x10.core.fun.VoidFun_0_0;
import x10.core.fun.VoidFun_0_1;
import x10.network.SocketTransport.CALLBACKID;
import x10.xrx.FinishState;
import x10.lang.Place;
import x10.network.NetworkTransportCallbacks;
import x10.runtime.impl.java.Runtime;
import x10.serialization.X10JavaDeserializer;

/**
 * A class to contain the Java portion of message send/receive pairs.
 */
public class MessageHandlers implements NetworkTransportCallbacks {
    
    // values set in native method registerHandlers()
    private static int closureMessageID;
    private static int simpleAsyncMessageID;
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
     * Send an active message.
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
    	try{
    		if (X10RT.VERBOSE) System.out.println("runClosureAtReceive is called");
    		java.io.ByteArrayInputStream byteStream = new java.io.ByteArrayInputStream(args);
    		if (X10RT.VERBOSE) System.out.println("runClosureAtReceive: ByteArrayInputStream");

    		long start = Runtime.PROF_SER ? System.nanoTime() : 0;
    		DataInputStream objStream = new DataInputStream(byteStream);
    		if (X10RT.VERBOSE) System.out.println("runClosureAtReceive: ObjectInputStream");
    		X10JavaDeserializer deserializer = new X10JavaDeserializer(objStream);
    		if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
    			System.out.println("Starting deserialization ");
    		}
    		VoidFun_0_0 actObj = (VoidFun_0_0) deserializer.readObject();
    		if (Runtime.PROF_SER) {
    			long stop = System.nanoTime();
    			long duration = stop-start;
    			if (duration >= Runtime.PROF_SER_FILTER) {
    				System.out.println("Deserialization took "+(((double)duration)/1e6)+" ms.");
    			}
    		}
    		if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
    			System.out.println("Ending deserialization ");
    		}

    		if (X10RT.VERBOSE) System.out.println("runClosureAtReceive: after cast and deserialization");
    		actObj.$apply();
    		if (X10RT.VERBOSE) System.out.println("runClosureAtReceive: after apply");
    		objStream.close();
    		if (X10RT.VERBOSE) System.out.println("runClosureAtReceive is done !");
    	} catch(Throwable ex){
            if (!x10.xrx.Configuration.silenceInternalWarnings$O()) {
                System.out.println("WARNING: Ignoring uncaught exception in @Immediate async.");
                ex.printStackTrace();
            }
    	}
    }

    /*
     * This send/receive pair is used to serialize a simple async
     * (finish state + async body closure) to
     * a remote place, which deserializes the two objects and invokes the
     * async body. 
     * 
     * This is the "normal" case of used to implement a typical X10-level async.
     */
    
    public static void runSimpleAsyncAtSend(int place, byte[] rawBytes) { 
        sendMessage(place, simpleAsyncMessageID, rawBytes.length, rawBytes);
    }
    
    /**
     * Receive a simple async
     */
    private static void runSimpleAsyncAtReceive(byte[] args) {
    	try{
    		if (X10RT.VERBOSE) System.out.println("runSimpleAsyncAtReceive is called");
    		ByteArrayInputStream byteStream = new ByteArrayInputStream(args);
    		if (X10RT.VERBOSE) System.out.println("runSimpleAsyncAtReceive: ByteArrayInputStream");
    		VoidFun_0_0 actObj;

    		long start = Runtime.PROF_SER ? System.nanoTime() : 0;
    		DataInputStream objStream = new DataInputStream(byteStream);
    		if (X10RT.VERBOSE) System.out.println("runSimpleAsyncAtReceive: ObjectInputStream");
    		X10JavaDeserializer deserializer = new X10JavaDeserializer(objStream);
    		if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
    			System.out.println("Starting deserialization ");
    		}
    		FinishState finishState = (FinishState) deserializer.readObject();
            Place src = (Place) deserializer.readObject();
            
            try {
                actObj = (VoidFun_0_0) deserializer.readObject();
                if (Runtime.PROF_SER) {
                    long stop = System.nanoTime();
                    long duration = stop-start;
                    if (duration >= Runtime.PROF_SER_FILTER) {
                        System.out.println("Deserialization took "+(((double)duration)/1e6)+" ms.");
                    }
                }
                if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
                    System.out.println("Ending deserialization ");
                }
            } catch (Throwable e) {
                if (X10RT.VERBOSE) System.out.println("runSimpleAsyncAtReceive: handling exception during deserialization");
                finishState.notifyActivityCreationFailed(src, new x10.io.SerializationException(e));
                if (X10RT.VERBOSE) System.out.println("runSimpleAsyncAtReceive: exception pushed; bookkeeping complete");
                return;
            }
    		
    		if (X10RT.VERBOSE) System.out.println("runSimpleAsyncAtReceive: after cast and deserialization");
    		x10.xrx.Runtime.submitRemoteActivity(actObj, src, finishState);
    		if (X10RT.VERBOSE) System.out.println("runSimpleAsyncAtReceive: after submitRemoteActivity");
    		objStream.close();
    		if (X10RT.VERBOSE) System.out.println("runSimpleAsyncAtReceive is done !");
    	} catch(Exception ex){
            if (!x10.xrx.Configuration.silenceInternalWarnings$O()) {
                System.out.println("runSimpleAsyncAtReceive error !!!");
                ex.printStackTrace();
            }
    	}
    }
    
    /*
     * Java forms
     */
    public static void runCallback(int callbackId, ByteBuffer bb) throws IOException {
    	byte[] data;
    	if (bb.hasArray())
    		data = bb.array();
    	else {
    		data = new byte[bb.remaining()];
    		bb.get(data);
    	}
    	
    	if (callbackId == CALLBACKID.closureMessageID.ordinal())
			runClosureAtReceive(new ByteArrayInputStream(data));
		else if (callbackId == CALLBACKID.simpleAsyncMessageID.ordinal())
			runSimpleAsyncAtReceive(new ByteArrayInputStream(data));
		else
			System.err.println("Unknown message callback type: "+callbackId);
    }

    static void runClosureAtReceive(InputStream input) {
        try {
            X10JavaDeserializer deserializer = new X10JavaDeserializer(new DataInputStream(input));
            VoidFun_0_0 actObj = (VoidFun_0_0) deserializer.readObject();
            actObj.$apply();
        } catch (Throwable e) {
            if (!x10.xrx.Configuration.silenceInternalWarnings$O()) {
                System.out.println("WARNING: Ignoring uncaught exception in @Immediate async.");
                e.printStackTrace();
            }
        }
    }
    
    static void runSimpleAsyncAtReceive(InputStream input) throws IOException {
    	X10JavaDeserializer deserializer = new X10JavaDeserializer(new DataInputStream(input));
    	FinishState finishState = (FinishState) deserializer.readObject();
    	Place src = (Place) deserializer.readObject();
        long epoch = deserializer.readLong();
    	VoidFun_0_0 actObj;
    	try {
    	    actObj = (VoidFun_0_0) deserializer.readObject();
    	} catch (Throwable e) {
    	    // TODO: handle epoch?
            finishState.notifyActivityCreationFailed(src, new x10.io.SerializationException(e));
            return;
    	}
    	x10.xrx.Runtime.submitRemoteActivity(epoch, actObj, src, finishState);
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
