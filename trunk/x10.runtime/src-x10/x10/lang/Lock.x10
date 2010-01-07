/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 runtime. It is
 *  governed by the licence under which
 *  X10 is released.
 *
 */
package x10.lang;

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
    public native def this();

    public native def lock():Void;

    public native def tryLock():Void;

    public native def unlock():Void;

    public native def getHoldCount():Int;
}

// vim:shiftwidth=4:tabstop=4:expandtab
