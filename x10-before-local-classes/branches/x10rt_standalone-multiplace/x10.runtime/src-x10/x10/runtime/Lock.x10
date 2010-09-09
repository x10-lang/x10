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
    static class ReentrantLock {}

    private val lock = new ReentrantLock();

    public def lock() {
        @Native("java", "lock.lock();")
        @Native("c++", "FMGL(lock)->lock();") {}
    }

    public def tryLock() {
        @Native("java", "lock.tryLock();")
        @Native("c++", "FMGL(lock)->tryLock();") {}
    }

    public def unlock() {
        @Native("java", "lock.unlock();")
        @Native("c++", "FMGL(lock)->unlock();") {}
    }

    public def getHoldCount() {
        @Native("java", "return lock.getHoldCount();")
        @Native("c++", "return FMGL(lock)->getHoldCount();") { return 0; }
    }
}

// vim:shiftwidth=4:tabstop=4:expandtab
