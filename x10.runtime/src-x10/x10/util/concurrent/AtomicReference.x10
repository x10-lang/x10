/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.util.concurrent;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "x10.core.concurrent.AtomicReference<#T$box>", null, "x10.rtt.ParameterizedType.make(x10.core.concurrent.AtomicReference.$RTT, #T$rtt)")
@NativeRep("c++", "x10::util::concurrent::AtomicReference< #T >*", "x10::util::concurrent::AtomicReference< #T >", null)
public final class AtomicReference[T]{T isref} {
	
	@Native("java", "new x10.core.concurrent.AtomicReference<#T$box>(#T$rtt)")
	@Native("c++", "::x10::util::concurrent::AtomicReference< #T >::_make()")
	public native def this():AtomicReference[T];

	@Native("java", "new x10.core.concurrent.AtomicReference<#T$box>(#T$rtt,#v)")
	@Native("c++", "::x10::util::concurrent::AtomicReference< #T >::_make(#v)")
	public native def this(v:T):AtomicReference[T];

	/**
	 * @Deprecated("Use this()")
	 */	
	@Native("java", "new x10.core.concurrent.AtomicReference<#T$box>(#T$rtt)")
	@Native("c++", "::x10::util::concurrent::AtomicReference< #T >::_make()")
	public static native def newAtomicReference[T]() {T isref} :AtomicReference[T];

	/**
	 * @Deprecated("Use this(T)")
	 */	
	@Native("java", "new x10.core.concurrent.AtomicReference<#T$box>(#T$rtt,#v)")
	@Native("c++", "::x10::util::concurrent::AtomicReference< #T >::_make(#v)")
	public static native def newAtomicReference[T](v:T) {T isref} :AtomicReference[T];

	@Native("java", "#this.get()")
	public native def get():T;

	@Native("java", "#this.set(#v)")
	public native def set(v:T):void;

	@Native("java", "#this.compareAndSet(#expect,#update)")
	public native def compareAndSet(expect:T, update:T):Boolean;

	@Native("java", "#this.weakCompareAndSet(#expect,#update)")
	public native def weakCompareAndSet(expect:T, update:T):Boolean;
	
	@Native("java", "#this.getAndSet(#v)")
	public native def getAndSet(v:T):T;

	@Native("java", "#this.toString()")
	public native def toString():String;
}
 
