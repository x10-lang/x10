/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 runtime. It is
 *  governed by the licence under which
 *  X10 is released.
 *
 */
package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeClass;
import x10.compiler.NativeDef;

/**
 * A unit of execution (a thread).
 *
 * A union of a subset of the functionality of java.lang.Thread augmented
 * with the park/unpark API of java.util.concurrent.locks.LockSupport.
 *
 * The goal is to have just what we need to implement the X10 runtime
 * and no more --- this API is not intended to be exposed to
 * general X10 programmers.
 */

@NativeClass("java", "x10.runtime.impl.java" , "Thread")
@NativeClass("c++", "x10.lang", "Thread")
final class X10Thread {

    /**
     * Allocates new thread in current place
     */
    public native def this(body:()=>Void, name:String);

    public static native def currentThread():X10Thread!;

    public native def start():Void;

    public native static def sleep(millis:Long):Void throws InterruptedException;

    public native static def sleep(millis:Long, nanos:Int):Void throws InterruptedException;

    public native static def park():Void;

    public native static def parkNanos(nanos:Long):Void;

    public native global def unpark():Void;

    @NativeDef("c++")
    public def worker():Worker! {
        // TODO fix place inconsistency in java backend
        @Native("java", "return ((x10.lang.Worker)(__NATIVE_FIELD__.worker()));")
        { return null; }
    }

    public native def worker(worker:Worker!):Void;

    public native def name():String;

    public native def name(name:String):void;

    public native def locInt():Int;
}

// vim:shiftwidth=4:tabstop=4:expandtab
