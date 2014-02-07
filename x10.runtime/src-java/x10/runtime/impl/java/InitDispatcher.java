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

package x10.runtime.impl.java;


public abstract class InitDispatcher {

    public static final int UNINITIALIZED = 0;
    public static final int INITIALIZING = 1;
    public static final int INITIALIZED = 2;
    public static final int EXCEPTION_RAISED = 3;

    public static void lockInitialized() {
        x10.lang.Runtime.StaticInitBroadcastDispatcherLock();
    }

    public static void unlockInitialized() {
        x10.lang.Runtime.StaticInitBroadcastDispatcherUnlock();
    }

    public static void awaitInitialized() {
        x10.lang.Runtime.StaticInitBroadcastDispatcherAwait();
    }

    public static void notifyInitialized() {
        x10.lang.Runtime.StaticInitBroadcastDispatcherNotify();
    }

    public static void printStaticInitMessage(String message) {
        Runtime.printStaticInitMessage(message);
    }

    public static final boolean TRACE_STATIC_INIT = Runtime.TRACE_STATIC_INIT;
}
