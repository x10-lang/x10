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

package x10.lang;

import x10.compiler.NativeClass;
import x10.compiler.Pinned;

/**
 * X10 wrapper class for native reentrant lock.
 * Can be used in arbitrary code but should not.
 * See Monitor class for a lock that plays nice with the runtime scheduler.
 * 
 * @author tardieu
 */
@NativeClass("java", "java.util.concurrent.locks", "ReentrantLock")
@NativeClass("c++", "x10.lang", "Lock__ReentrantLock")
@Pinned public class Lock implements x10.io.CustomSerialization {
    public native def this();

    public native def lock():void;

    public native def tryLock():Boolean;

    public native def unlock():void;

    public native def getHoldCount():Int; // only supported on some platforms

    public def serialize():Any {
        throw new UnsupportedOperationException("Cannot serialize "+typeName());
    }

    private def this(Any) {
        throw new UnsupportedOperationException("Cannot deserialize "+typeName());
    }

}