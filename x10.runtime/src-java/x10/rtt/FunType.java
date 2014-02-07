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

public final class FunType<T> extends RuntimeType<T> {

    protected FunType(Class<?> javaClass, int numParams, Type<?>[] parents) {
        super(javaClass, numParams, parents);
    }

    private static final boolean useCache = true;
    private static final ConcurrentHashMap<Class<?>, FunType<?>> typeCache = new ConcurrentHashMap<Class<?>, FunType<?>>();
    public static <T> FunType/*<T>*/ make(Class<?> javaClass) {
        if (useCache) {
            FunType<?> type = typeCache.get(javaClass);
            if (type == null) {
                FunType<?> type0 = new FunType<T>(javaClass, 0, null);
                type = typeCache.putIfAbsent(javaClass, type0);
                if (type == null) type = type0;
            }
            return (FunType<T>) type;
        } else {
            return new FunType<T>(javaClass, 0, null);
        }
    }
    
    public static <T> FunType/*<T>*/ make(Class<?> javaClass, int numParams) {
        if (useCache) {
            FunType<?> type = typeCache.get(javaClass);
            if (type == null) {
                FunType<?> type0 = new FunType<T>(javaClass, numParams, null);
                type = typeCache.putIfAbsent(javaClass, type0);
                if (type == null) type = type0;
            }
            return (FunType<T>) type;
        } else {
            return new FunType<T>(javaClass, numParams, null);
        }
    }

    public static <T> FunType/*<T>*/ make(Class<?> javaClass, Type<?>[] parents) {
        if (useCache) {
            FunType<?> type = typeCache.get(javaClass);
            if (type == null) {
                FunType<?> type0 = new FunType<T>(javaClass, 0, parents);
                type = typeCache.putIfAbsent(javaClass, type0);
                if (type == null) type = type0;
            }
            return (FunType<T>) type;
        } else {
            return new FunType<T>(javaClass, 0, parents);
        }
    }
    
    public static <T> FunType/*<T>*/ make(Class<?> javaClass, int numParams, Type<?>[] parents) {
        if (useCache) {
            FunType<?> type = typeCache.get(javaClass);
            if (type == null) {
                FunType<?> type0 = new FunType<T>(javaClass, numParams, parents);
                type = typeCache.putIfAbsent(javaClass, type0);
                if (type == null) type = type0;
            }
            return (FunType<T>) type;
        } else {
            return new FunType<T>(javaClass, numParams, parents);
        }
    }

    @Override
    public String typeName(Object o) {
        return typeNameForFun(o);
    }

	@Override
	protected RuntimeType.Variance getVariance(int i) {
		if (i == numParams() - 1) return RuntimeType.Variance.COVARIANT; // return type is covarient
		return RuntimeType.Variance.CONTRAVARIANT; // parameter types are contravarient
	}

}
