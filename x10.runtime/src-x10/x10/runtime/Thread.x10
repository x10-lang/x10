/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 runtime. It is 
 *  governed by the licence under which 
 *  X10 is released.
 *
 */
package x10.runtime;

import x10.compiler.Native;
import x10.compiler.NativeRep;
import x10.runtime.InterruptedException;
 
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

@NativeRep("java", "x10.runtime.impl.java.Thread", null, null)
@NativeRep("c++", "x10aux::ref<x10::runtime::Thread>", "x10::runtime::Thread", null)
public class Thread {
        
    /**
     * Allocates new thread in current place
     */
    public native def this(body:()=>Void, name:String):Thread;
        
    @Native("java", "#0.currentThread()")
    @Native("c++", "x10::runtime::Thread::currentThread()")
	public static native def currentThread():Thread!;
        
    @Native("java", "#0.start()")
    @Native("c++", "(#0)->start()")
    public native def start():void;
        
    @Native("java", "#0.sleep(#1)")
    @Native("c++", "x10::runtime::Thread::sleep(#1)")
    public static native def sleep(millis:Long):void throws InterruptedException;
    
    @Native("java", "#0.sleep(#1,#2)")
    @Native("c++", "x10::runtime::Thread::sleep(#1,#2)")
    public static native def sleep(millis:Long, nanos:Int):void throws InterruptedException;
    
    @Native("java","java.util.concurrent.locks.LockSupport.park()")
    @Native("c++", "x10::runtime::Thread::park()")
    public static native def park():void;
    
    @Native("java", "java.util.concurrent.locks.LockSupport.parkNanos(#1)")
    @Native("c++", "x10::runtime::Thread::parkNanos(#1)")
    public static native def parkNanos(nanos:Long):void;
    
    @Native("java", "java.util.concurrent.locks.LockSupport.unpark(#1)")
    @Native("c++", "x10::runtime::Thread::unpark(#1)")
    public static native def unpark(thread:Thread):void;

    @Native("java", "((x10.runtime.Worker)(#0.worker()))")
    @Native("c++",  "x10::runtime::Thread::currentThread()->worker()")
	public native def worker():Worker!;

    @Native("java", "#0.worker(#1)")
    @Native("c++", "(#0)->worker(#1)")
    public native def worker(worker:Worker!):void;

    @Native("java", "#0.getName()")
    @Native("c++", "(#0)->getName()")
    public native def name():String;

    @Native("java", "#0.setName(#1)")
    @Native("c++", "(#0)->setName(#1)")
    public native def name(name:String):void;
    
    @Native("java", "#0.home()")
    @Native("c++", "x10aux::location(#0)")
    public native def locInt():Int;
}
