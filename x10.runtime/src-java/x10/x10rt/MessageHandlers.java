/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */
package x10.x10rt;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;

import x10.lang.FinishState;
import x10.runtime.impl.java.Runtime;
import x10.serialization.DeserializationDispatcher;
import x10.serialization.X10JavaDeserializer;

/**
 * A class to contain the Java portion of message send/receive pairs.
 */
public class MessageHandlers {
		
	/*
	 * This send/receive pair is used to serialize a ()=>void closure to
	 * a remote place, which deserializes the closure object and calls eval on it.
	 * 
	 * One important use of this message pair is the non-optimized implementation of
	 * x10.lang.Runtime.runClosureAt and x10.lang.Runtime.runClosureCopyAt. 
	 */
	
    public static void runClosureAtSend(int place, int arraylen, byte[] rawBytes, int msg_id) {
    	runClosureAtSendImpl(place, arraylen, rawBytes, msg_id);
    }
    
    private static native void runClosureAtSendImpl(int place, int arraylen, byte[] rawBytes, int msg_id);

    /**
     * Register the asyncs with the X10 RT implementation and obtain message ID's
     */
    public static native void registerHandlers(int arraylen);

    /**
     *  When  registerHandlers is called the native code invokes this method with the message ID's obtained
     */
    private static void registerHandlersCallback(int[] ids) {
            DeserializationDispatcher.registerHandlersCallback(ids);
    }

    // Invoked from native code at receiving place
    // This function gets called by the callback thats registered to handle messages with the X10 RT implementation
    private static void receiveAsync(byte[] args, int type) {
        short serializationID = DeserializationDispatcher.getSerializationID(type);
        DeserializationDispatcher.ClosureKind closureKind = DeserializationDispatcher.getClosureKind(serializationID);
        if (closureKind == DeserializationDispatcher.ClosureKind.CLOSURE_KIND_GENERAL_ASYNC) {
               runClosureAtReceive(args, serializationID);
        } else if (closureKind == DeserializationDispatcher.ClosureKind.CLOSURE_KIND_SIMPLE_ASYNC) {
              runAsyncAtReceive(args, serializationID);
        } else {
            throw new RuntimeException("Received a unrecognized ASYNC");
        }
    }

    /**
     * Receive a closure
     */
    private static void runClosureAtReceive(byte[] args , short sid) {
    	try{
    		if (X10RT.VERBOSE) System.out.println("@MultiVM : runClosureAtReceive is called");
    		java.io.ByteArrayInputStream byteStream = new java.io.ByteArrayInputStream(args);
    		if (X10RT.VERBOSE) System.out.println("runClosureAtReceive: ByteArrayInputStream");

    		long start = Runtime.PROF_SER ? System.nanoTime() : 0;
    		InputStream objStream = new DataInputStream(byteStream);
    		if (X10RT.VERBOSE) System.out.println("runClosureAtReceive: ObjectInputStream");
    		X10JavaDeserializer deserializer = new X10JavaDeserializer((DataInputStream) objStream);
    		if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
    			System.out.println("Starting deserialization ");
    		}
    		x10.core.fun.VoidFun_0_0 actObj = (x10.core.fun.VoidFun_0_0) deserializer.readRef(sid);
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
    	} catch(Exception ex){
    		System.out.println("runClosureAtReceive error !!!");
    		ex.printStackTrace();
    	}
    }

    /**
     * Receive a async
     */
    private static void runAsyncAtReceive(byte[] args, short sid) {
    	try{
    		if (X10RT.VERBOSE) System.out.println("@MultiVM : runAsyncAtReceive is called");
    		ByteArrayInputStream byteStream = new ByteArrayInputStream(args);
    		if (X10RT.VERBOSE) System.out.println("runAsyncAtReceive: ByteArrayInputStream");
    		x10.core.fun.VoidFun_0_0 actObj;

    		long start = Runtime.PROF_SER ? System.nanoTime() : 0;
    		InputStream objStream = new DataInputStream(byteStream);
    		if (X10RT.VERBOSE) System.out.println("runAsyncAtReceive: ObjectInputStream");
    		X10JavaDeserializer deserializer = new X10JavaDeserializer((DataInputStream) objStream);
    		if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
    			System.out.println("Starting deserialization ");
    		}
    		FinishState finishState = (FinishState) deserializer.readRef();
    		actObj = (x10.core.fun.VoidFun_0_0) deserializer.readRef(sid);
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

    		if (X10RT.VERBOSE) System.out.println("runAsyncAtReceive: after cast and deserialization");
    		x10.lang.Runtime.execute(actObj, finishState);
    		if (X10RT.VERBOSE) System.out.println("runAsyncAtReceive: after apply");
    		objStream.close();
    		if (X10RT.VERBOSE) System.out.println("runAsyncAtReceive is done !");
    	} catch(Exception ex){
    		System.out.println("runAsyncAtReceive error !!!");
    		ex.printStackTrace();
    	}
    }
}
