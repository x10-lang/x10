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

@NativeRep("java", "java.util.concurrent.atomic.AtomicReference<#1>")
public class AtomicReference[T] {
	
	// Unusable due to compiler bug.  See http://jira.codehaus.org/browse/XTENLANG-127
	// public native def this():AtomicReference[T];
	// public native def this(val:T):AtomicReference[T];
	
	// Hack around 127.  Delete as soon as it is fixed.
	@Native("java", "(new java.util.concurrent.atomic.AtomicReference<#1>())")
	public static native def newAtomicReference[T]():AtomicReference[T];

	// Hack around 127.  Delete as soon as it is fixed.
	@Native("java", "(new java.util.concurrent.atomic.AtomicReference<#1>(#4))")
	public static native def newAtomicReference[T](val:T):AtomicReference[T];

	@Native("java", "#0.get()")
	public native def get():T;

	@Native("java", "#0.set(#1)")
	public native def set(val:T):void;

	@Native("java", "#0.compareAndSet(#1,#2)")
	public native def compareAndSet(expect:T, update:T):boolean;

	@Native("java", "#0.weakCompareAndSet(#1,#2)")
	public native def weakCompareAndSet(expect:T, update:T):boolean;
	
	@Native("java", "#0.getAndSet(#1)")
	public native def getAndSet(val:T):T;

	@Native("java", "#0.toString()")
	public native def toString():String;
}
 