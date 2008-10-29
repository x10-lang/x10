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

/* 
 * Would have prefered to name this "x10.runtime.kernel.Thread" but worried 
 * about name clashes with java.lang.Thread for the Java backend so 
 * decided to use unambiguous name.
 */
 
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
@NativeRep("java", "java.lang.Thread")
public class NativeThread {
	
	public native def this(task:Runnable):NativeThread;
	
	public native def this(task:Runnable, name:String):NativeThread;
	
	@Native("java", "#0.run()")
	public native def run():void;
	
	@Native("java", "java.lang.Thread.sleep(#1)")
	public static native def sleep(millis:Long):void throws InterruptedException;
	
	@Native("java", "java.lang.Thread.sleep(#1,#2)")
	public static native def sleep(millis:Long, nanos:Int):void throws InterruptedException;
	
	@Native("java","java.util.concurrent.locks.LockSupport.park()")
	public static native def park():void;
	
	@Native("java", "java.util.concurrent.locks.LockSupport.parkNanos(#1)")
	public static native def parkNanos(nanos:Long):void;
	
	@Native("java", "java.util.concurrent.locks.LockSupport.unpark(#1)")
	public static native def unpark(thread:NativeThread):void;
}
 