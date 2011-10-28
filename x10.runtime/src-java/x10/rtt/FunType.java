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
//    protected FunType(Class<?> c) {
//        super(c);
//    }
//    
//    protected FunType(Class<?> c, Variance[] variances) {
//        super(c, variances);
//    }
//
//    protected FunType(Class<?> c, Type<?>[] parents) {
//        super(c, parents);
//    }
    
    protected FunType(Class<?> c, Variance[] variances, Type<?>[] parents) {
        super(c, variances, parents);
    }

    private static final boolean useCache = true;
    private static final ConcurrentHashMap<Class<?>, FunType<?>> typeCache = new ConcurrentHashMap<Class<?>, FunType<?>>();
    public static <T> FunType/*<T>*/ make(Class<?> c) {
        if (useCache) {
            FunType<?> type = typeCache.get(c);
            if (type == null) {
                FunType<?> type0 = new FunType<T>(c, null, null);
                type = typeCache.putIfAbsent(c, type0);
                if (type == null) type = type0;
            }
            return (FunType<T>) type;
        } else {
            return new FunType<T>(c, null, null);
        }
    }
    
    public static <T> FunType/*<T>*/ make(Class<?> c, Variance[] variances) {
        if (useCache) {
            FunType<?> type = typeCache.get(c);
            if (type == null) {
                FunType<?> type0 = new FunType<T>(c, variances, null);
                type = typeCache.putIfAbsent(c, type0);
                if (type == null) type = type0;
            }
            return (FunType<T>) type;
        } else {
            return new FunType<T>(c, variances, null);
        }
    }

    public static <T> FunType/*<T>*/ make(Class<?> c, Type<?>[] parents) {
        if (useCache) {
            FunType<?> type = typeCache.get(c);
            if (type == null) {
                FunType<?> type0 = new FunType<T>(c, null, parents);
                type = typeCache.putIfAbsent(c, type0);
                if (type == null) type = type0;
            }
            return (FunType<T>) type;
        } else {
            return new FunType<T>(c, null, parents);
        }
    }
    
    public static <T> FunType/*<T>*/ make(Class<?> c, Variance[] variances, Type<?>[] parents) {
        if (useCache) {
            FunType<?> type = typeCache.get(c);
            if (type == null) {
                FunType<?> type0 = new FunType<T>(c, variances, parents);
                type = typeCache.putIfAbsent(c, type0);
                if (type == null) type = type0;
            }
            return (FunType<T>) type;
        } else {
            return new FunType<T>(c, variances, parents);
        }
    }

    @Override
    public String typeName(Object o) {
        return typeNameForFun(o);
    }

}
