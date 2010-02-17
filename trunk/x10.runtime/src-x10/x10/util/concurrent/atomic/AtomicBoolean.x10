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

@NativeRep("java", "java.util.concurrent.atomic.AtomicBoolean", null, null)
@NativeRep("c++", "x10aux::ref<x10::util::concurrent::atomic::AtomicBoolean>", "x10::util::concurrent::atomic::AtomicBoolean", null)
public final class AtomicBoolean {
	
	public native def this():AtomicBoolean;
	public native def this(val:boolean):AtomicBoolean;
	
	@Native("java", "#0.get()")
	@Native("c++", "(#0)->get()")
	public native def get():boolean;

	@Native("java", "#0.set(#1)")
	@Native("c++", "(#0)->set(#1)")
	public native def set(val:boolean):void;

	@Native("java", "#0.compareAndSet(#1,#2)")
	@Native("c++", "(#0)->compareAndSet(#1,#2)")
	public native def compareAndSet(expect:boolean, update:boolean):boolean;

	@Native("java", "#0.weakCompareAndSet(#1,#2)")
	@Native("c++", "(#0)->weakCompareAndSet(#1,#2)")
	public native def weakCompareAndSet(expect:boolean, update:boolean):boolean;
	
	@Native("java", "#0.getAndSet(#1)")
	@Native("c++", "(#0)->getAndSet(#1)")
	public native def getAndSet(val:boolean):boolean;

	@Native("java", "#0.toString()")
	@Native("c++", "(#0)->toString()")
	public global safe native def toString():String;
}
 
