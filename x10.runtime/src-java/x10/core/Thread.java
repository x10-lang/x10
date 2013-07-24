/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2012.
 */

package x10.core;

import x10.io.Unserializable;
import x10.lang.Place;
import x10.rtt.NamedType;
import x10.rtt.RuntimeType;
import x10.rtt.Type;
import x10.rtt.Types;
import x10.serialization.X10JavaSerializer;
import x10.x10rt.X10RT;

import java.io.IOException;

/**
 */
public class Thread implements Any, Unserializable {
    private static final long serialVersionUID = 1L;
    public static final RuntimeType<Thread> $RTT = NamedType.<Thread> make("x10.lang.Thread", Thread.class);
    public RuntimeType<?> $getRTT() { return $RTT; }
    public Type<?> $getParam(int i) { return null; }

    private java.lang.Thread jthread;

    static final ThreadLocal<Thread> context = new ThreadLocal<Thread>() {
        protected Thread initialValue() {
            return x10.lang.Runtime.wrapNativeThread();
        }
    };

    public static Thread currentThread() {
        return context.get();
    }

    private Place home;    // the current place

    public x10.core.fun.VoidFun_0_0 body;

    // constructor just for allocation
    public Thread(java.lang.System[] $dummy) {}

    public final Thread x10$lang$Thread$$init$S(java.lang.String name) {
        jthread = new java.lang.Thread(name) {
            public void run() {
                context.set(Thread.this);
                if (null != body) {
                    body.$apply();
                } else {
                    $apply();
                }
            }
        };
        home = Place.place(X10RT.here());
        return this;
    }

    public Thread(java.lang.String name) {
        x10$lang$Thread$$init$S(name);
    }

    public final Thread x10$lang$Thread$$init$S() {
        jthread = java.lang.Thread.currentThread();
        home = Place.place(X10RT.here());
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

    /**
     * Return current place
     */
    public Place home() {
        return home;
    }

    public java.lang.String name() {
        return jthread.getName();
    }

    public void name(java.lang.String name) {
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
                throw new x10.lang.InterruptedException();
            } catch (java.lang.Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public short $_get_serialization_id() {
        throw new java.lang.UnsupportedOperationException("Cannot serialize " + getClass());
    }

    public void $_serialize(X10JavaSerializer $serializer) throws IOException {
        throw new java.lang.UnsupportedOperationException("Cannot serialize " + getClass());
    }

}
