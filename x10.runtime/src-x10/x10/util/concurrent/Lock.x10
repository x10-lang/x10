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

package x10.util.concurrent;

import x10.compiler.NativeClass;
import x10.compiler.Pinned;
import x10.io.Unserializable;

/**
 * <p>X10 wrapper class for native reentrant lock.
 * Acquiring the lock halts the thread running the current activity until the lock is acquired.</p>
 */
@NativeClass("java", "java.util.concurrent.locks", "ReentrantLock")
@NativeClass("c++", "x10.lang", "Lock__ReentrantLock")
@Pinned public class Lock implements Unserializable {
    public native def this();

    public native def lock():void;

    public native def tryLock():Boolean;

    public native def unlock():void;

    public native def getHoldCount():Int; // only supported on some platforms
}
