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

@NativeRep("java", "java.util.concurrent.atomic.AtomicReferenceFieldUpdater<#1,#2>", null, null)
public class AtomicReferenceFieldUpdater[T,V] {
	
	@Native("java", "((java.util.concurrent.atomic.AtomicReferenceFieldUpdater<#1,#4>)java.util.concurrent.atomic.AtomicReferenceFieldUpdater.newUpdater(#3.getJavaClass(), #6.getJavaClass(), #7))")
	public static native def newUpdater[T,V](fieldName:String):AtomicReferenceFieldUpdater[T,V];	

	@Native("java", "#0.compareAndSet(#1, #2 ,#3)")
	public native def compareAndSet(obj:T, expect:V, update:V):boolean;

	@Native("java", "#0.weakCompareAndSet(#1, #2 ,#3)")
	public native def weakCompareAndSet(obj:T, expect:V, update:V):boolean;

	@Native("java", "#0.set(#1, #2)")
	public native def set(obj:T, value:V):void;
	
	@Native("java", "#0.get(#1)")
	public native def get(obj:T):V;

	@Native("java", "#0.getAndSet(#1,#2)")
	public native def getAndSet(obj:T, value:V):V;
}
