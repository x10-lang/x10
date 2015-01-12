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

package x10.core;

import java.io.IOException;

import x10.core.fun.VoidFun_0_0;
import x10.io.Unserializable;
import x10.rtt.NamedType;
import x10.rtt.RuntimeType;
import x10.rtt.Type;
import x10.serialization.X10JavaSerializer;

/**
 */
public class Thread implements Any, Unserializable {
    public static final RuntimeType<Thread> $RTT = NamedType.<Thread> make("x10.lang.Thread", Thread.class);
    public RuntimeType<?> $getRTT() { return $RTT; }
    public Type<?> $getParam(int i) { return null; }

    private java.lang.Thread jthread;

    public static boolean isX10WorkerThread$O() {
	return java.lang.Thread.currentThread() instanceof X10WorkerThread;
    }

    static final ThreadLocal<Thread> context = new ThreadLocal<Thread>() {
        protected Thread initialValue() {
            return x10.xrx.Runtime.wrapNativeThread();
        }
    };

    public static Thread currentThread() {
        return context.get();
    }

    public VoidFun_0_0 body;

    // constructor just for allocation
    public Thread(java.lang.System[] $dummy) {}

    class X10WorkerThread extends java.lang.Thread {
	public X10WorkerThread (String name) {
	    super(name);
	}
            public void run() {
	       context.set(Thread.this);
	       if (null != body) {
		   body.$apply();
	       } else {
		   $apply();
	       }
	   }
        }

    public final Thread x10$lang$Thread$$init$S(String name) {
        jthread = new X10WorkerThread(name);
	return this;
    }
    
    public void removeWorkerContext() {
        context.set(null);
    }

    public Thread(String name) {
        x10$lang$Thread$$init$S(name);
    }

    public final Thread x10$lang$Thread$$init$S() {
        jthread = java.lang.Thread.currentThread();
        return this;
    }

    public Thread() {
        x10$lang$Thread$$init$S();
    }

    public void $apply() {}

    public void start() {
        jthread.start();
    }

    // Note: since this isn't user visible, java.lang.InterruptedException is used.
    public void join() throws java.lang.InterruptedException {
        jthread.join();
    }

    public String name() {
        return jthread.getName();
    }

    public void name(String name) {
        jthread.setName(name);
    }

    public static void park() {
        java.util.concurrent.locks.LockSupport.park();
    }

    public void unpark() {
        java.util.concurrent.locks.LockSupport.unpark(jthread);
    }

    public static void parkNanos(java.lang.Long nanos) {
        java.util.concurrent.locks.LockSupport.parkNanos(nanos);
    }

    public static long getTid() {
        return java.lang.Thread.currentThread().getId();
    }

    public static void sleep(long time) {
        Thread.sleep(time, 0);
    }

    public static void sleep(long time, int nanos) {
        try {
            java.lang.Thread.sleep(time, nanos);
        } catch (java.lang.InterruptedException e) {
            try {
                throw new x10.xrx.InterruptedException();
            } catch (java.lang.Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    private Object writeReplace() throws java.io.ObjectStreamException {
        throw new x10.io.NotSerializableException("Cannot serialize " + getClass());
    }

    public short $_get_serialization_id() {
        throw new x10.io.NotSerializableException("Cannot serialize " + getClass());
    }

    public void $_serialize(X10JavaSerializer $serializer) throws IOException {
        throw new x10.io.NotSerializableException("Cannot serialize " + getClass());
    }

}
