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

/**
 * A low-level lock that provides a subset of
 * the functionality of java.util.concurrent.locks.ReentrantLock.
 * The API is subsetted to that which is also supported by pthread_mutex.
 */
@NativeRep("java", "java.util.concurrent.locks.ReentrantLock", null, null)
@NativeRep("c++", "x10aux::ref<x10::runtime::Lock>", "x10::runtime::Lock", null)
public class Lock {
	
	public native def this():Lock;
	
	@Native("java", "#0.lock()")
	@Native("c++", "(#0)->lock()")
	public native def lock():void;
	
	@Native("java", "#0.tryLock()")
	@Native("c++", "(#0)->tryLock()")
	public native def tryLock():boolean;
	
	@Native("java", "#0.unlock()")
	@Native("c++", "(#0)->unlock()")
	public native def unlock():void;

	@Native("java", "#0.getHoldCount()")
	@Native("c++", "(#0)->getHoldCount()")
	public native def getHoldCount():int;
}
