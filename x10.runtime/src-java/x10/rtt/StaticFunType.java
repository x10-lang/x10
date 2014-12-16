/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.rtt;

import java.util.concurrent.ConcurrentHashMap;

import x10.core.fun.Fun;

// for static inner classes that are compiled from closures
public final class StaticFunType<T> extends RuntimeType<T> {
    
    protected StaticFunType(Class<?> javaClass, int numParams, Type<?>[] parents) {
    	super(javaClass, numParams, parents);
    }

    private static final boolean useCache = true;
    private static final ConcurrentHashMap<Class<?>, StaticFunType<?>> typeCache = new ConcurrentHashMap<Class<?>, StaticFunType<?>>();
    public static <T> StaticFunType/*<T>*/ make(Class<?> javaClass) {
        if (useCache) {
            StaticFunType<?> type = typeCache.get(javaClass);
            if (type == null) {
                StaticFunType<?> type0 = new StaticFunType<T>(javaClass, 0, null);
                type = typeCache.putIfAbsent(javaClass, type0);
                if (type == null) type = type0;
            }
            return (StaticFunType<T>) type;
        } else {
            return new StaticFunType<T>(javaClass, 0, null);
        }
    }
    
    public static <T> StaticFunType/*<T>*/ make(Class<?> javaClass, int numParams) {
        if (useCache) {
            StaticFunType<?> type = typeCache.get(javaClass);
            if (type == null) {
                StaticFunType<?> type0 = new StaticFunType<T>(javaClass, numParams, null);
                type = typeCache.putIfAbsent(javaClass, type0);
                if (type == null) type = type0;
            }
            return (StaticFunType<T>) type;
        } else {
            return new StaticFunType<T>(javaClass, numParams, null);
        }
    }

    public static <T> StaticFunType/*<T>*/ make(Class<?> javaClass, Type<?>[] parents) {
        if (useCache) {
            StaticFunType<?> type = typeCache.get(javaClass);
            if (type == null) {
                StaticFunType<?> type0 = new StaticFunType<T>(javaClass, 0, parents);
                type = typeCache.putIfAbsent(javaClass, type0);
                if (type == null) type = type0;
            }
            return (StaticFunType<T>) type;
        } else {
            return new StaticFunType<T>(javaClass, 0, parents);
        }
    }
    
    public static <T> StaticFunType/*<T>*/ make(Class<?> javaClass, int numParams, Type<?>[] parents) {
        if (useCache) {
            StaticFunType<?> type = typeCache.get(javaClass);
            if (type == null) {
                StaticFunType<?> type0 = new StaticFunType<T>(javaClass, numParams, parents);
                type = typeCache.putIfAbsent(javaClass, type0);
                if (type == null) type = type0;
            }
            return (StaticFunType<T>) type;
        } else {
            return new StaticFunType<T>(javaClass, numParams, parents);
        }
    }

    @Override
    public String typeName(Object o) {
        // Note: assume that the first parent in this RuntimeType is the parameterized type which corresponds to the function type
        assert o instanceof Fun;
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

	@Override
	protected RuntimeType.Variance getVariance(int i) {
		if (i == numParams() - 1) return RuntimeType.Variance.COVARIANT; // return type is covarient
		return RuntimeType.Variance.CONTRAVARIANT; // parameter types are contravarient
	}

}
