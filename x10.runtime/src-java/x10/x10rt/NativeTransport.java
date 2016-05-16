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

import x10.runtime.impl.java.Runtime;

/**
 * A class to encapsulate the Java/Native boundary for sending 
 * and receiving active messages using one of the native X10RT transports.
 */
public class NativeTransport {
    
    // messageIds;  their values set in native method registerHandlers()
    public static int closureMessageID;
    public static int simpleAsyncMessageID;
    public static int getMessageID;
    public static int getCompletedMessageID;
    public static int putMessageID;

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
    public static native void sendMessage(int place, int msg_id, int arraylen, byte[] rawBytes);

    // callback invoked from native code when receiving closureMessageID
    private static void runClosureAtReceive(byte[] args) {
        Runtime.runClosureAtReceive(new ByteArrayInputStream(args));
    }

    // callback invoked from native code when receiving simpleAsyncMessageID
    private static void runSimpleAsyncAtReceive(byte[] args) {
        Runtime.runSimpleAsyncAtReceive(new ByteArrayInputStream(args), false);
    }
    
    // callback invoked from native code when receiving getMessageID
    private static void getReceive(byte[] args) {
        Runtime.getReceive(new ByteArrayInputStream(args));
    }
    
    // callback invoked from native code when receiving getCompletedMessageID
    private static void getCompletedReceive(byte[] args) {
        Runtime.getCompletedReceive(new ByteArrayInputStream(args));
    }

    // callback invoked from native code when receiving uncountedPutMessageID
    private static void putReceive(byte[] args) {
        Runtime.putReceive(new ByteArrayInputStream(args));
    }
}
