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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class InitDispatcher {

    public static final int UNINITIALIZED = 0;
    public static final int INITIALIZING = 1;
    public static final int INITIALIZED = 2;

    private static Set<Method> initializeMethods = new HashSet<Method>();
    private static int initc = 0;

    public static void runInitializer() {
        for (final Method initializer : initializeMethods) {
            // System.out.println(" executing " + initializer.getName());
            x10.core.fun.VoidFun_0_0 body = new x10.core.fun.VoidFun_0_0() {
                public void apply() {
                    // execute X10-level static initialization
                    try {
                        initializer.invoke(null, (Object[])null);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                        throw new java.lang.Error(e);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        throw new java.lang.Error(e);
                    }
                }
                public x10.rtt.RuntimeType<?> getRTT() {
                    return _RTT;
                }
                public x10.rtt.Type<?> getParam(int i) {
                    return null;
                }
            };
            x10.lang.Runtime.runAsync(body);
        }
        // static initialization all finished
        initc = -1;
    }

    public static void addInitializer(String className, String initializerName) {
        if (initc < 0) {
            System.err.println("Adding initializer too late!");
            System.exit(-1);
        }

        try {
            Class<?> clazz = Class.forName(className);
            Method initlaizer = clazz.getMethod(initializerName, (Class<?>[])null);
            initializeMethods.add(initlaizer);
            initc++;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new java.lang.Error(e);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new java.lang.Error(e);
        }
    }

    public static void broadcastStaticField(Object field) {
        // dummy
        // System.out.println("broadcasting:"+field+" from place:"+x10.lang.Runtime.hereInt());
    }

    public static void awaitInitialized() {
        // dummy
        // x10.lang.Runtime.StaticInitBroadcastDispatcherAwait();
    }

    public static void notifyInitialized() {
        // dummy
        // x10.lang.Runtime.StaticInitBroadcastDispatcherNotify();
    }
}
