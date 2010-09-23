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

package x10.util.concurrent.atomic;

import x10.compiler.Native;
import x10.compiler.NativeRep;

// @NativeRep("java", "java.util.concurrent.atomic.AtomicReference<#1>", null, "new x10.rtt.ParameterizedType(x10.rtt.Types.ATOMIC_REFERENCE, #2)")/*TODO pass type params*/
@NativeRep("java", "java.util.concurrent.atomic.AtomicReference<#1>", null, "x10.rtt.Types.ATOMIC_REFERENCE")
@NativeRep("c++", "x10aux::ref<x10::util::concurrent::atomic::AtomicReference<#1 > >", "x10::util::concurrent::atomic::AtomicReference<#1 >", null)
public final class AtomicReference[T]{T<:Object} {
	
	// Unusable due to compiler bug.  See http://jira.codehaus.org/browse/XTENLANG-127
	// public native def this():AtomicReference[T];
	// public native def this(v:T):AtomicReference[T];
	
	// Hack around XTENLANG-127.  Delete as soon as it is fixed.
	@Native("java", "(new java.util.concurrent.atomic.AtomicReference<#1>())")
	@Native("c++", "x10::util::concurrent::atomic::AtomicReference<#1 >::_make()")
	public static native def newAtomicReference[T]():AtomicReference[T];

	// Hack around XTENLANG-127.  Delete as soon as it is fixed.
	@Native("java", "(new java.util.concurrent.atomic.AtomicReference<#1>(#4))")
	@Native("c++", "x10::util::concurrent::atomic::AtomicReference<#1 >::_make(#4)")
	public static  native def newAtomicReference[T](v:T):AtomicReference[T];

	@Native("java", "#0.get()")
	@Native("c++", "(#0)->get()")
	public native def get():T;

	@Native("java", "#0.set(#1)")
	@Native("c++", "(#0)->set(#1)")
	public native def set(v:T):void;

	@Native("java", "#0.compareAndSet(#1,#2)")
	@Native("c++", "(#0)->compareAndSet(#1,#2)")
	public native def compareAndSet(expect:T, update:T):boolean;

	@Native("java", "#0.weakCompareAndSet(#1,#2)")
	@Native("c++", "(#0)->weakCompareAndSet(#1,#2)")
	public native def weakCompareAndSet(expect:T, update:T):boolean;
	
	@Native("java", "#0.getAndSet(#1)")
	@Native("c++", "(#0)->getAndSet(#1)")
	public native def getAndSet(v:T):T;

	@Native("java", "#0.toString()")
	@Native("c++", "(#0)->toString()")
	public native def toString():String;
}
 
