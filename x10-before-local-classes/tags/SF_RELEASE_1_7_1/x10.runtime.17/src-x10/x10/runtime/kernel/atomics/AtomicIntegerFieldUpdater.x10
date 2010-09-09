/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 runtime. It is 
 *  governed by the licence under which 
 *  X10 is released.
 *
 */
package x10.runtime.kernel.atomics;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "java.util.concurrent.atomic.AtomicIntegerFieldUpdater<#1>")
public class AtomicIntegerFieldUpdater[T] {
	
	@Native("java", "((java.util.concurrent.atomic.AtomicIntegerFieldUpdater<#1>)java.util.concurrent.atomic.AtomicIntegerFieldUpdater.newUpdater(#3.getJavaClass(), #4))")
	public static native def newUpdater[T](fieldName:String):AtomicIntegerFieldUpdater[T];	

	@Native("java", "#0.compareAndSet(#1, #2 ,#3)")
	public native def compareAndSet(obj:T, expect:int, update:int):boolean;

	@Native("java", "#0.weakCompareAndSet(#1, #2 ,#3)")
	public native def weakCompareAndSet(obj:T, expect:int, update:int):boolean;

	@Native("java", "#0.set(#1, #2)")
	public native def set(obj:T, value:int):void;
	
	@Native("java", "#0.get(#1)")
	public native def get(obj:T):int;

	@Native("java", "#0.getAndSet(#1,#2)")
	public native def getAndSet(obj:T, value:int):int;
	
	@Native("java", "#0.getAndIncrement(#1)")
	public native def getAndIncrement(obj:T):int;
	
	@Native("java", "#0.getAndDecrement(#1)")
	public native def getAndDecrement(obj:T):int;

	@Native("java", "#0.getAndAdd(#1,#2)")
	public native def getAndAdd(obj:T, delta:int):int;

	@Native("java", "#0.incrementAndGet(#1)")
	public native def incrementAndGet(obj:T):int;
	
	@Native("java", "#0.decrementAndGet(#1)")
	public native def decrementAndGet(obj:T):int;

	@Native("java", "#0.addAndGet(#1,#2)")
	public native def addAndGet(obj:T, delta:int):int;
}