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

// for static inner classes that are compiled from closures
public class StaticFunType<T> extends RuntimeType<T> {
    
    private static final long serialVersionUID = 1L;

    // not used
//    protected StaticFunType(Class<?> javaClass) {
//        super(javaClass);
//    }
//    
//    protected StaticFunType(Class<?> javaClass, Variance[] variances) {
//        super(javaClass, variances);
//    }
//
//    protected StaticFunType(Class<?> javaClass, Type<?>[] parents) {
//        super(javaClass, parents);
//    }
    
    protected StaticFunType(Class<?> javaClass, Variance[] variances, Type<?>[] parents) {
        super(javaClass, variances, parents);
    }

    private static final boolean useCache = true;
    private static final ConcurrentHashMap<Class<?>, StaticFunType<?>> typeCache = new ConcurrentHashMap<Class<?>, StaticFunType<?>>();
    public static <T> StaticFunType/*<T>*/ make(Class<?> javaClass) {
        if (useCache) {
            StaticFunType<?> type = typeCache.get(javaClass);
            if (type == null) {
                StaticFunType<?> type0 = new StaticFunType<T>(javaClass, null, null);
                type = typeCache.putIfAbsent(javaClass, type0);
                if (type == null) type = type0;
            }
            return (StaticFunType<T>) type;
        } else {
            return new StaticFunType<T>(javaClass, null, null);
        }
    }
    
    public static <T> StaticFunType/*<T>*/ make(Class<?> javaClass, Variance[] variances) {
        if (useCache) {
            StaticFunType<?> type = typeCache.get(javaClass);
            if (type == null) {
                StaticFunType<?> type0 = new StaticFunType<T>(javaClass, variances, null);
                type = typeCache.putIfAbsent(javaClass, type0);
                if (type == null) type = type0;
            }
            return (StaticFunType<T>) type;
        } else {
            return new StaticFunType<T>(javaClass, variances, null);
        }
    }

    public static <T> StaticFunType/*<T>*/ make(Class<?> javaClass, Type<?>[] parents) {
        if (useCache) {
            StaticFunType<?> type = typeCache.get(javaClass);
            if (type == null) {
                StaticFunType<?> type0 = new StaticFunType<T>(javaClass, null, parents);
                type = typeCache.putIfAbsent(javaClass, type0);
                if (type == null) type = type0;
            }
            return (StaticFunType<T>) type;
        } else {
            return new StaticFunType<T>(javaClass, null, parents);
        }
    }
    
    public static <T> StaticFunType/*<T>*/ make(Class<?> javaClass, Variance[] variances, Type<?>[] parents) {
        if (useCache) {
            StaticFunType<?> type = typeCache.get(javaClass);
            if (type == null) {
                StaticFunType<?> type0 = new StaticFunType<T>(javaClass, variances, parents);
                type = typeCache.putIfAbsent(javaClass, type0);
                if (type == null) type = type0;
            }
            return (StaticFunType<T>) type;
        } else {
            return new StaticFunType<T>(javaClass, variances, parents);
        }
    }

    @Override
    public String typeName(Object o) {
        // Note: assume that the first parent in this RuntimeType is the parameterized type which corresponds to the function type
        assert o instanceof x10.core.fun.Fun;
        Type<?> parent = getParents()[0];
        String typeName;
        if (parent instanceof ParameterizedType<?>) {
        	typeName = ((ParameterizedType<?>) parent).typeNameForFun(o);
        } else {
        	assert parent instanceof RuntimeType<?>;
        	typeName = ((RuntimeType<?>) parent).typeNameForFun(o);
        }
        return typeName;
    }

}
