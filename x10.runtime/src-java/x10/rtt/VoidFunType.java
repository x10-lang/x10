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

public final class VoidFunType<T> extends RuntimeType<T> {
    
    protected VoidFunType(Class<?> javaClass, int numParams, Type<?>[] parents) {
        super(javaClass, numParams, parents);
    }

    private static final boolean useCache = true;
    private static final ConcurrentHashMap<Class<?>, VoidFunType<?>> typeCache = new ConcurrentHashMap<Class<?>, VoidFunType<?>>();
    public static <T> VoidFunType/*<T>*/ make(Class<?> javaClass) {
        if (useCache) {
            VoidFunType<?> type = typeCache.get(javaClass);
            if (type == null) {
                VoidFunType<?> type0 = new VoidFunType<T>(javaClass, 0, null);
                type = typeCache.putIfAbsent(javaClass, type0);
                if (type == null) type = type0;
            }
            return (VoidFunType<T>) type;
        } else {
            return new VoidFunType<T>(javaClass, 0, null);
        }
    }
    
    public static <T> VoidFunType/*<T>*/ make(Class<?> javaClass, int numParams) {
        if (useCache) {
            VoidFunType<?> type = typeCache.get(javaClass);
            if (type == null) {
                VoidFunType<?> type0 = new VoidFunType<T>(javaClass, numParams, null);
                type = typeCache.putIfAbsent(javaClass, type0);
                if (type == null) type = type0;
            }
            return (VoidFunType<T>) type;
        } else {
            return new VoidFunType<T>(javaClass, numParams, null);
        }
    }

    public static <T> VoidFunType/*<T>*/ make(Class<?> javaClass, Type<?>[] parents) {
        if (useCache) {
            VoidFunType<?> type = typeCache.get(javaClass);
            if (type == null) {
                VoidFunType<?> type0 = new VoidFunType<T>(javaClass, 0, parents);
                type = typeCache.putIfAbsent(javaClass, type0);
                if (type == null) type = type0;
            }
            return (VoidFunType<T>) type;
        } else {
            return new VoidFunType<T>(javaClass, 0, parents);
        }
    }
    
    public static <T> VoidFunType/*<T>*/ make(Class<?> javaClass, int numParams, Type<?>[] parents) {
        if (useCache) {
            VoidFunType<?> type = typeCache.get(javaClass);
            if (type == null) {
                VoidFunType<?> type0 = new VoidFunType<T>(javaClass, numParams, parents);
                type = typeCache.putIfAbsent(javaClass, type0);
                if (type == null) type = type0;
            }
            return (VoidFunType<T>) type;
        } else {
            return new VoidFunType<T>(javaClass, numParams, parents);
        }
    }

    @Override
    public String typeName(Object o) {
        return typeNameForVoidFun(o);
    }

	@Override
	protected RuntimeType.Variance getVariance(int i) {
		return RuntimeType.Variance.CONTRAVARIANT; // parameter types are contravarient
	}

}
