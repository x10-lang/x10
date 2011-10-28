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

package x10.rtt;

import java.util.concurrent.ConcurrentHashMap;

public class VoidFunType<T> extends RuntimeType<T> {
    
    private static final long serialVersionUID = 1L;

    // not used
//    protected VoidFunType(Class<?> javaClass) {
//        super(javaClass);
//    }
//    
//    protected VoidFunType(Class<?> javaClass, Variance[] variances) {
//        super(javaClass, variances);
//    }
//
//    protected VoidFunType(Class<?> javaClass, Type<?>[] parents) {
//        super(javaClass, parents);
//    }
    
    protected VoidFunType(Class<?> javaClass, Variance[] variances, Type<?>[] parents) {
        super(javaClass, variances, parents);
    }

    private static final boolean useCache = true;
    private static final ConcurrentHashMap<Class<?>, VoidFunType<?>> typeCache = new ConcurrentHashMap<Class<?>, VoidFunType<?>>();
    public static <T> VoidFunType/*<T>*/ make(Class<?> javaClass) {
        if (useCache) {
            VoidFunType<?> type = typeCache.get(javaClass);
            if (type == null) {
                VoidFunType<?> type0 = new VoidFunType<T>(javaClass, null, null);
                type = typeCache.putIfAbsent(javaClass, type0);
                if (type == null) type = type0;
            }
            return (VoidFunType<T>) type;
        } else {
            return new VoidFunType<T>(javaClass, null, null);
        }
    }
    
    public static <T> VoidFunType/*<T>*/ make(Class<?> javaClass, Variance[] variances) {
        if (useCache) {
            VoidFunType<?> type = typeCache.get(javaClass);
            if (type == null) {
                VoidFunType<?> type0 = new VoidFunType<T>(javaClass, variances, null);
                type = typeCache.putIfAbsent(javaClass, type0);
                if (type == null) type = type0;
            }
            return (VoidFunType<T>) type;
        } else {
            return new VoidFunType<T>(javaClass, variances, null);
        }
    }

    public static <T> VoidFunType/*<T>*/ make(Class<?> javaClass, Type<?>[] parents) {
        if (useCache) {
            VoidFunType<?> type = typeCache.get(javaClass);
            if (type == null) {
                VoidFunType<?> type0 = new VoidFunType<T>(javaClass, null, parents);
                type = typeCache.putIfAbsent(javaClass, type0);
                if (type == null) type = type0;
            }
            return (VoidFunType<T>) type;
        } else {
            return new VoidFunType<T>(javaClass, null, parents);
        }
    }
    
    public static <T> VoidFunType/*<T>*/ make(Class<?> javaClass, Variance[] variances, Type<?>[] parents) {
        if (useCache) {
            VoidFunType<?> type = typeCache.get(javaClass);
            if (type == null) {
                VoidFunType<?> type0 = new VoidFunType<T>(javaClass, variances, parents);
                type = typeCache.putIfAbsent(javaClass, type0);
                if (type == null) type = type0;
            }
            return (VoidFunType<T>) type;
        } else {
            return new VoidFunType<T>(javaClass, variances, parents);
        }
    }

    @Override
    public String typeName(Object o) {
        return typeNameForVoidFun(o);
    }

}
