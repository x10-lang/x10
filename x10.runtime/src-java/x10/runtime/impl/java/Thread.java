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

package x10.runtime.impl.java;

import x10.io.SerialData;
import x10.lang.Place;
import x10.rtt.NamedType;
import x10.rtt.RuntimeType;
import x10.rtt.Type;
import x10.rtt.Types;
import x10.x10rt.X10JavaSerializer;
import x10.x10rt.X10RT;

import java.io.IOException;

/**
 * @author Christian Grothoff
 * @author vj
 * @author Raj Barik, Vivek Sarkar
 * @author tardieu
 */
public class Thread implements x10.core.Any {
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
    public Thread(SerialData $dummy) {
        throw new UnsupportedOperationException("Cannot deserialize Thread");
    }

    public final Thread x10$lang$Thread$$init$S(String name) {
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
    // XTENLANG-3063
    // not used if X10PrettyPrinterVisitor.supportConstructorWithThrows == true
    public Thread $init(String name) {
        return x10$lang$Thread$$init$S(name);
    }

    public Thread(String name) {
        // XTENLANG-3063
//        $init(name);
        x10$lang$Thread$$init$S(name);
    }

    public final Thread x10$lang$Thread$$init$S() {
        jthread = java.lang.Thread.currentThread();
        home = Place.place(X10RT.here());
        return this;
    }
    // XTENLANG-3063
    // not used if X10PrettyPrinterVisitor.supportConstructorWithThrows == true
    public Thread $init() {
        return x10$lang$Thread$$init$S();
    }

    public Thread() {
        // XTENLANG-3063
//        $init();
        x10$lang$Thread$$init$S();
    }

    public void $apply() {}

    public void start() {
        jthread.start();
    }
    
    public void join() throws InterruptedException {
        jthread.join();
    }

    /**
     * Return current place
     */
    public Place home() {
        return home;
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

    public static void parkNanos(Long nanos) {
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
        } catch (InterruptedException e) {
            x10.core.Throwable e1 = null;
            try {
                e1 = (x10.core.Throwable)Class.forName("x10.lang.InterruptedException").newInstance();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            throw e1;
        }
    }

    public void $_serialize(X10JavaSerializer serializer) throws IOException {
        throw new UnsupportedOperationException("Cannot serialize " + getClass());
    }

        public short $_get_serialization_id() {
            throw new UnsupportedOperationException("Cannot serialize " + getClass());
        }

}
