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

package x10.rtt;

import java.util.concurrent.ConcurrentHashMap;

import x10.core.fun.VoidFun;

// for static inner classes that are compiled from closures
public final class StaticVoidFunType<T> extends RuntimeType<T> {
    
    protected StaticVoidFunType(Class<?> javaClass, int numParams, Type<?>[] parents) {
        super(javaClass, numParams, parents);
    }

    private static final boolean useCache = true;
    private static final ConcurrentHashMap<Class<?>, StaticVoidFunType<?>> typeCache = new ConcurrentHashMap<Class<?>, StaticVoidFunType<?>>();
    public static <T> StaticVoidFunType/*<T>*/ make(Class<?> javaClass) {
        if (useCache) {
            StaticVoidFunType<?> type = typeCache.get(javaClass);
            if (type == null) {
                StaticVoidFunType<?> type0 = new StaticVoidFunType<T>(javaClass, 0, null);
                type = typeCache.putIfAbsent(javaClass, type0);
                if (type == null) type = type0;
            }
            return (StaticVoidFunType<T>) type;
        } else {
            return new StaticVoidFunType<T>(javaClass, 0, null);
        }
    }
    
    public static <T> StaticVoidFunType/*<T>*/ make(Class<?> javaClass, int numParams) {
        if (useCache) {
            StaticVoidFunType<?> type = typeCache.get(javaClass);
            if (type == null) {
                StaticVoidFunType<?> type0 = new StaticVoidFunType<T>(javaClass, numParams, null);
                type = typeCache.putIfAbsent(javaClass, type0);
                if (type == null) type = type0;
            }
            return (StaticVoidFunType<T>) type;
        } else {
            return new StaticVoidFunType<T>(javaClass, numParams, null);
        }
    }

    public static <T> StaticVoidFunType/*<T>*/ make(Class<?> javaClass, Type<?>[] parents) {
        if (useCache) {
            StaticVoidFunType<?> type = typeCache.get(javaClass);
            if (type == null) {
                StaticVoidFunType<?> type0 = new StaticVoidFunType<T>(javaClass, 0, parents);
                type = typeCache.putIfAbsent(javaClass, type0);
                if (type == null) type = type0;
            }
            return (StaticVoidFunType<T>) type;
        } else {
            return new StaticVoidFunType<T>(javaClass, 0, parents);
        }
    }
    
    public static <T> StaticVoidFunType/*<T>*/ make(Class<?> javaClass, int numParams, Type<?>[] parents) {
        if (useCache) {
            StaticVoidFunType<?> type = typeCache.get(javaClass);
            if (type == null) {
                StaticVoidFunType<?> type0 = new StaticVoidFunType<T>(javaClass, numParams, parents);
                type = typeCache.putIfAbsent(javaClass, type0);
                if (type == null) type = type0;
            }
            return (StaticVoidFunType<T>) type;
        } else {
            return new StaticVoidFunType<T>(javaClass, numParams, parents);
        }
    }

    @Override
    public String typeName(Object o) {
        // Note: assume that the first parent in this RuntimeType is the parameterized type which corresponds to the function type
        assert o instanceof VoidFun;
        Type<?> parent = getParents()[0];
        String typeName;
        if (parent instanceof ParameterizedType<?>) {
        	typeName = ((ParameterizedType<?>) parent).typeNameForVoidFun(o);
        } else {
        	assert parent instanceof RuntimeType<?>;
        	typeName = ((RuntimeType<?>) parent).typeNameForVoidFun(o);
        }
        return typeName;
    }

	@Override
	protected RuntimeType.Variance getVariance(int i) {
		return RuntimeType.Variance.CONTRAVARIANT; // parameter types are contravarient
	}

}
