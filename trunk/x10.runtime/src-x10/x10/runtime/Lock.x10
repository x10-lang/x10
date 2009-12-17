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
public class Lock {

    @NativeRep("java", "java.util.concurrent.locks.ReentrantLock", null, null)
    @NativeRep("c++", "x10aux::ref<x10::runtime::Lock__ReentrantLock>", "x10::runtime::Lock__ReentrantLock", null)
    static class ReentrantLock {

        public native def this():ReentrantLock;

        @Native("java", "#0.lock()")
        @Native("c++", "(#0)->lock()")
        native def lock():Void;

        @Native("java", "#0.tryLock()")
        @Native("c++", "(#0)->tryLock()")
        native def tryLock():Boolean;

        @Native("java", "#0.unlock()")
        @Native("c++", "(#0)->unlock()")
        native def unlock():Void;

        @Native("java", "#0.getHoldCount()")
        @Native("c++", "(#0)->getHoldCount()")
        native def getHoldCount():Int;
    }

    private val lock = new ReentrantLock();

    public def lock() { lock.lock(); }

    public def tryLock() { lock.tryLock(); }

    public def unlock() { lock.unlock(); }

    public def getHoldCount() = lock.getHoldCount();
}

// vim:shiftwidth=4:tabstop=4:expandtab
