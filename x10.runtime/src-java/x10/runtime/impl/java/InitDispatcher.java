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

    private static Set<Method> initializeMethods = new HashSet<Method>();

    public static void runInitializer() {
        for (final Method initializer : initializeMethods) {
            // System.out.println(" executing " + initializer.getName());
            x10.core.fun.VoidFun_0_0 body = new x10.core.fun.VoidFun_0_0() {
                public void apply() {
                    // execute X10-level static initialization
                    try {
                        initializer.invoke(null, (Object[])null);
                    } catch (InvocationTargetException e) {
                    } catch (IllegalAccessException e) {
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
    }

    public static void addInitializer(String className, String initializerName) {
        try {
            Class<?> clazz = Class.forName(className);
            Method initlaizer = clazz.getMethod(initializerName, (Class<?>[])null);
            initializeMethods.add(initlaizer);
        } catch (ClassNotFoundException e) {
        } catch (NoSuchMethodException e) {
        }
    }
}
