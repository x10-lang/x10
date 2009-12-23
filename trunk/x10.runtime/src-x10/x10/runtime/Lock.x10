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

import x10.compiler.NativeClass;
import x10.compiler.NativeDef;

/**
 * A low-level lock that provides a subset of
 * the functionality of java.util.concurrent.locks.ReentrantLock.
 * The API is subsetted to that which is also supported by pthread_mutex.
 */
@NativeClass("java", "java.util.concurrent.locks", "ReentrantLock")
@NativeClass("c++", "x10.runtime", "Lock__ReentrantLock")
public class Lock {

    @NativeDef("java")
    @NativeDef("c++")
    public def lock():Void {}

    @NativeDef("java")
    @NativeDef("c++")
    public def tryLock():Void {}

    @NativeDef("java")
    @NativeDef("c++")
    public def unlock():Void {}

    @NativeDef("java")
    @NativeDef("c++")
    public def getHoldCount():Int = 0;
}

// vim:shiftwidth=4:tabstop=4:expandtab
