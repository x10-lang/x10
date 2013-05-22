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

package x10.util.concurrent;

import x10.compiler.NativeClass;
import x10.compiler.Pinned;
import x10.io.CustomSerialization;
import x10.io.SerialData;

/**
 * <p>X10 wrapper class for native reentrant lock.
 * Acquiring the lock halts the thread running the current activity until the lock is acquired.</p>
 * 
 * <p>Lock implements CustomSerialization to prevent instances of
 * Locks from being copied between places by <code>at</code> statements.
 * The motivation for this is to prevent implicit copying of Lock objects,
 * since that is very likely to lead to concurrency errors in the program.
 * If the serialize method of a Lock instance is invoked, an UnsupportedOperationException
 * will be thrown.</p>
 */
@NativeClass("java", "java.util.concurrent.locks", "ReentrantLock")
@NativeClass("c++", "x10.lang", "Lock__ReentrantLock")
@Pinned public class Lock implements CustomSerialization {
    public native def this();

    public native def lock():void;

    public native def tryLock():Boolean;

    public native def unlock():void;

    public native def getHoldCount():Int; // only supported on some platforms

   /**
    * Serialization of Lock objects is forbidden.
    * @throws UnsupportedOperationException
    */
    public def serialize():SerialData {
        throw new UnsupportedOperationException("Cannot serialize "+typeName());
    }

   /**
    * Serialization of Lock objects is forbidden.
    * @throws UnsupportedOperationException
    */
    private def this(SerialData) {
        throw new UnsupportedOperationException("Cannot deserialize "+typeName());
    }

}