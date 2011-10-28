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

public class FunType<T> extends RuntimeType<T> {

    private static final long serialVersionUID = 1L;

    // not used
//    protected FunType(Class<?> javaClass) {
//        super(javaClass);
//    }
//    
//    protected FunType(Class<?> javaClass, Variance[] variances) {
//        super(javaClass, variances);
//    }
//
//    protected FunType(Class<?> javaClass, Type<?>[] parents) {
//        super(javaClass, parents);
//    }
    
    protected FunType(Class<?> javaClass, Variance[] variances, Type<?>[] parents) {
        super(javaClass, variances, parents);
    }

    private static final boolean useCache = true;
    private static final ConcurrentHashMap<Class<?>, FunType<?>> typeCache = new ConcurrentHashMap<Class<?>, FunType<?>>();
    public static <T> FunType/*<T>*/ make(Class<?> javaClass) {
        if (useCache) {
            FunType<?> type = typeCache.get(javaClass);
            if (type == null) {
                FunType<?> type0 = new FunType<T>(javaClass, null, null);
                type = typeCache.putIfAbsent(javaClass, type0);
                if (type == null) type = type0;
            }
            return (FunType<T>) type;
        } else {
            return new FunType<T>(javaClass, null, null);
        }
    }
    
    public static <T> FunType/*<T>*/ make(Class<?> javaClass, Variance[] variances) {
        if (useCache) {
            FunType<?> type = typeCache.get(javaClass);
            if (type == null) {
                FunType<?> type0 = new FunType<T>(javaClass, variances, null);
                type = typeCache.putIfAbsent(javaClass, type0);
                if (type == null) type = type0;
            }
            return (FunType<T>) type;
        } else {
            return new FunType<T>(javaClass, variances, null);
        }
    }

    public static <T> FunType/*<T>*/ make(Class<?> javaClass, Type<?>[] parents) {
        if (useCache) {
            FunType<?> type = typeCache.get(javaClass);
            if (type == null) {
                FunType<?> type0 = new FunType<T>(javaClass, null, parents);
                type = typeCache.putIfAbsent(javaClass, type0);
                if (type == null) type = type0;
            }
            return (FunType<T>) type;
        } else {
            return new FunType<T>(javaClass, null, parents);
        }
    }
    
    public static <T> FunType/*<T>*/ make(Class<?> javaClass, Variance[] variances, Type<?>[] parents) {
        if (useCache) {
            FunType<?> type = typeCache.get(javaClass);
            if (type == null) {
                FunType<?> type0 = new FunType<T>(javaClass, variances, parents);
                type = typeCache.putIfAbsent(javaClass, type0);
                if (type == null) type = type0;
            }
            return (FunType<T>) type;
        } else {
            return new FunType<T>(javaClass, variances, parents);
        }
    }

    @Override
    public String typeName(Object o) {
        return typeNameForFun(o);
    }

}
