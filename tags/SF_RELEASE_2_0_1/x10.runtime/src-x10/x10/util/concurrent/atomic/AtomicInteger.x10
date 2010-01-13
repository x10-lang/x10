/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 runtime. It is 
 *  governed by the licence under which 
 *  X10 is released.
 *
 */
package x10.util.concurrent.atomic;

import x10.compiler.Native;
import x10.compiler.NativeRep;

/*
 * vj: Making the methods global is a hack for the Java backend.
 */
@NativeRep("java", "java.util.concurrent.atomic.AtomicInteger", null, null)
@NativeRep("c++", "x10aux::ref<x10::util::concurrent::atomic::AtomicInteger>", "x10::util::concurrent::atomic::AtomicInteger", null)
public final class AtomicInteger {
	
	public native def this(): AtomicInteger;
	public native def this(val:int):  AtomicInteger;

	@Native("java", "#0.get()")
	@Native("c++", "(#0)->get()")
	public global native def get():int;
	
	@Native("java", "#0.set(#1)")
	@Native("c++", "(#0)->set(#1)")
	public global native def set(newVal:int):void;
	
	@Native("java", "#0.compareAndSet(#1,#2)")
	@Native("c++", "(#0)->compareAndSet(#1,#2)")
	public global native def compareAndSet(expect:int, update:int):boolean;

	@Native("java", "#0.weakCompareAndSet(#1,#2)")
	@Native("c++", "(#0)->weakCompareAndSet(#1,#2)")
	public global native def weakCompareAndSet(expect:int, update:int):boolean;
	
	@Native("java", "#0.getAndIncrement()")
	@Native("c++", "(#0)->getAndIncrement()")
	public global native def getAndIncrement():int;

	@Native("java", "#0.getAndDecrement()")
	@Native("c++", "(#0)->getAndDecrement()")
	public global native def getAndDecrement():int;
	
	@Native("java", "#0.getAndAdd(#1)")
	@Native("c++", "(#0)->getAndAdd(#1)")
	public global native def getAndAdd(delta:int):int;
	
	@Native("java", "#0.incrementAndGet()")
	@Native("c++", "(#0)->incrementAndGet()")
	public global native def incrementAndGet():int;

	@Native("java", "#0.decrementAndGet()")
	@Native("c++", "(#0)->decrementAndGet()")
	public global native def decrementAndGet():int;
	
	@Native("java", "#0.addAndGet(#1)")
	@Native("c++", "(#0)->addAndGet(#1)")
	public global native def addAndGet(delta:int):int;
	
	@Native("java", "#0.toString()")
	@Native("c++", "(#0)->toString()")
	public global safe native def toString():String;

	@Native("java", "#0.intValue()")
	@Native("c++", "(#0)->intValue()")
	public global native def intValue():int;

	@Native("java", "#0.longValue()")
	@Native("c++", "(#0)->longValue()")
	public global native def longValue():long;
	
	@Native("java", "#0.floatValue()")
	@Native("c++", "(#0)->floatValue()")
	public global native def floatValue():float;
	
	@Native("java", "#0.doubleValue()")
	@Native("c++", "(#0)->doubleValue()")
	public global native def doubleValue():double;
}
