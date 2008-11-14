/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 runtime. It is 
 *  governed by the licence under which 
 *  X10 is released.
 *
 */
package x10.runtime.kernel;

import x10.compiler.Native;
import x10.compiler.NativeRep;
import x10.runtime.kernel.Runnable;
import x10.runtime.kernel.InterruptedException;
 
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
public value Thread {
	
	public native def this(place:Object, runnable:Runnable, name:String):Thread;
	
	@Native("java", "#0.currentThread()")
	public static native def currentThread():Thread;
	
	@Native("java", "#0.start()")
	public native def start():void;
	
	@Native("java", "#0.sleep(#1)")
	public static native def sleep(millis:Long):void throws InterruptedException;
	
	@Native("java", "#0.sleep(#1,#2)")
	public static native def sleep(millis:Long, nanos:Int):void throws InterruptedException;
	
	@Native("java","java.util.concurrent.locks.LockSupport.park()")
	public static native def park():void;
	
	@Native("java", "java.util.concurrent.locks.LockSupport.parkNanos(#1)")
	public static native def parkNanos(nanos:Long):void;
	
	@Native("java", "java.util.concurrent.locks.LockSupport.unpark(#1)")
	public static native def unpark(thread:Thread):void;

    @Native("java", "#0.activity()")
    public native def activity():Object;

	@Native("java", "#0.activity(#1)")
	public native def activity(activity:Object):void;

	@Native("java", "#0.place()")
	public native def place():Object;

	@Native("java", "#0.place(#1)")
	public native def place(place:Object):void;

    @Native("java", "#0.getName()")
    public native def name():String;

	@Native("java", "#0.setName(#1)")
	public native def name(name:String):void;
}
