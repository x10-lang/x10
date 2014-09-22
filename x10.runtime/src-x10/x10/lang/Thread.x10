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

package x10.lang;

import x10.compiler.NativeRep;
import x10.compiler.Native;
import x10.io.Unserializable;

/**
 * Native thread. Only to be used in the runtime implementation.
 */
@NativeRep("java", "x10.core.Thread", null, "x10.core.Thread.$RTT")
@NativeRep("c++", "x10::lang::Thread*", "x10::lang::Thread", null)
class Thread implements Unserializable {

    public native def this(String);

    public native def this();

    @Native("c++", "true")
    public static native def isX10WorkerThread():Boolean;

    public static native def currentThread():Thread;

    public native def start():void;

    public static native def sleep(millis:Long):void;

    public static native def sleep(millis:Long, nanos:Int):void;

    public static native def park():void;

    public static native def parkNanos(nanos:Long):void;

    public native def unpark():void;

    public native def name():String;

    public native def name(name:String):void;

    public native operator this():void;

    public native def removeWorkerContext():void;
}

// vim:shiftwidth=4:tabstop=4:expandtab
