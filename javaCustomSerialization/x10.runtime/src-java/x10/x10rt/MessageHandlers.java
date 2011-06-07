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
	
    public static void runClosureAtSend(int place, int arraylen, byte[] rawBytes) {
    	runClosureAtSendImpl(place, arraylen, rawBytes);
    }
    
    private static native void runClosureAtSendImpl(int place, int arraylen, byte[] rawBytes);
    
    // Invoked from native code at receiving place
    // This function gets called by the callback thats registered to handle messages with the X10 RT implementation
    private static void runClosureAtReceive(byte[] args) {
    	try{
    		if (X10RT.VERBOSE) System.out.println("@MultiVM : runClosureAtReceive is called");
    		java.io.ByteArrayInputStream byteStream 
    			= new java.io.ByteArrayInputStream(args);
    		if (X10RT.VERBOSE) System.out.println("runClosureAtReceive: ByteArrayInputStream");
    		java.io.ObjectInputStream objStream = new java.io.ObjectInputStream(byteStream);
    		if (X10RT.VERBOSE) System.out.println("runClosureAtReceive: ObjectInputStream");
    		x10.core.fun.VoidFun_0_0 actObj = (x10.core.fun.VoidFun_0_0) objStream.readObject();
    		if (X10RT.VERBOSE) System.out.println("runClosureAtReceive: after cast");
    		actObj.$apply();
    		if (X10RT.VERBOSE) System.out.println("runClosureAtReceive: after apply");
    		objStream.close();
    		if (X10RT.VERBOSE) System.out.println("runClosureAtReceive is done !");
    	} catch(Exception ex){
    		System.out.println("runClosureAtReceive error !!!");
    		ex.printStackTrace();
    	}
    }
    
    static native void initialize();
}
