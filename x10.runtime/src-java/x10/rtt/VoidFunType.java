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
//    protected VoidFunType(Class<?> c) {
//        super(c);
//    }
//    
//    protected VoidFunType(Class<?> c, Variance[] variances) {
//        super(c, variances);
//    }
//
//    protected VoidFunType(Class<?> c, Type<?>[] parents) {
//        super(c, parents);
//    }
    
    protected VoidFunType(Class<?> c, Variance[] variances, Type<?>[] parents) {
        super(c, variances, parents);
    }

    private static final boolean useCache = true;
    private static final ConcurrentHashMap<Class<?>, VoidFunType<?>> typeCache = new ConcurrentHashMap<Class<?>, VoidFunType<?>>();
    public static <T> VoidFunType/*<T>*/ make(Class<?> c) {
        if (useCache) {
            VoidFunType<?> type = typeCache.get(c);
            if (type == null) {
                VoidFunType<?> type0 = new VoidFunType<T>(c, null, null);
                type = typeCache.putIfAbsent(c, type0);
                if (type == null) type = type0;
            }
            return (VoidFunType<T>) type;
        } else {
            return new VoidFunType<T>(c, null, null);
        }
    }
    
    public static <T> VoidFunType/*<T>*/ make(Class<?> c, Variance[] variances) {
        if (useCache) {
            VoidFunType<?> type = typeCache.get(c);
            if (type == null) {
                VoidFunType<?> type0 = new VoidFunType<T>(c, variances, null);
                type = typeCache.putIfAbsent(c, type0);
                if (type == null) type = type0;
            }
            return (VoidFunType<T>) type;
        } else {
            return new VoidFunType<T>(c, variances, null);
        }
    }

    public static <T> VoidFunType/*<T>*/ make(Class<?> c, Type<?>[] parents) {
        if (useCache) {
            VoidFunType<?> type = typeCache.get(c);
            if (type == null) {
                VoidFunType<?> type0 = new VoidFunType<T>(c, null, parents);
                type = typeCache.putIfAbsent(c, type0);
                if (type == null) type = type0;
            }
            return (VoidFunType<T>) type;
        } else {
            return new VoidFunType<T>(c, null, parents);
        }
    }
    
    public static <T> VoidFunType/*<T>*/ make(Class<?> c, Variance[] variances, Type<?>[] parents) {
        if (useCache) {
            VoidFunType<?> type = typeCache.get(c);
            if (type == null) {
                VoidFunType<?> type0 = new VoidFunType<T>(c, variances, parents);
                type = typeCache.putIfAbsent(c, type0);
                if (type == null) type = type0;
            }
            return (VoidFunType<T>) type;
        } else {
            return new VoidFunType<T>(c, variances, parents);
        }
    }

    @Override
    public String typeName(Object o) {
        return typeNameForVoidFun(o);
    }

}
