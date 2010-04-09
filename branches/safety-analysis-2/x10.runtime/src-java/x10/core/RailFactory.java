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

package x10.core;

import x10.core.fun.Fun_0_1;
import x10.rtt.RuntimeType;
import x10.rtt.Type;
import x10.rtt.Types;

public class RailFactory {
    public static <T> ValRail<T> makeValRail(Type<T> type, int length, Fun_0_1<Integer,T> init) {
        Object o = type.makeArray(length);
        for (int i = 0; i < length; i++) {
            type.setArray(o, i, init.apply(i));
        }
        ValRail<T> array = new ValRail<T>(type, length, o);
        return array;
    }
    
    public static <T> T initCompute (Type<T> type, int i, Fun_0_1<Integer,T> init)
    {
    	return init.apply(i);
    }
    
    public static <T> Rail<T> makeVarRail(Type<T> type, int length, Fun_0_1<Integer,T> init) {
        Rail<T> array = new Rail<T>(type, length);
        for (int i = 0; i < length; i++) {
            array.set(init.apply(i), i);
        }
        return array;
    }
    
    public static <T> ValRail<T> makeValRail(Type<T> type, int length) {
        Object o = type.makeArray(length);
        ValRail<T> array = new ValRail<T>(type, length, o);
        T zero = type.zeroValue();
        for (int i = 0; i < length; i++) {
            type.setArray(o, i, zero);
        }
        return array;
    }
    
    public static <T> Rail<T> makeVarRail(Type<T> type, int length) {
        Rail<T> array = new Rail<T>(type, length);
        T zero = type.zeroValue();
        for (int i = 0; i < length; i++) {
            array.set(zero, i);
        }
        return array;
    }

    public static <T> Rail<T> makeRailFromJavaArray(Object array) {
        if (array instanceof boolean[]) {
            return new Rail<T>((Type) Types.BOOLEAN, ((boolean[]) array).length, array);
        }
        if (array instanceof byte[]) {
            return new Rail<T>((Type) Types.BYTE, ((byte[]) array).length, array);
        }
        if (array instanceof short[]) {
            return new Rail<T>((Type) Types.SHORT, ((short[]) array).length, array);
        }
        if (array instanceof char[]) {
            return new Rail<T>((Type) Types.CHAR, ((char[]) array).length, array);
        }
        if (array instanceof int[]) {
            return new Rail<T>((Type) Types.INT, ((int[]) array).length, array);
        }
        if (array instanceof long[]) {
            return new Rail<T>((Type) Types.LONG, ((long[]) array).length, array);
        }
        if (array instanceof float[]) {
            return new Rail<T>((Type) Types.FLOAT, ((float[]) array).length, array);
        }
        if (array instanceof double[]) {
            return new Rail<T>((Type) Types.DOUBLE, ((double[]) array).length, array);
        }
        if (array instanceof String[]) {
            return new Rail<T>(new RuntimeType(String.class), ((String[]) array).length, array);
        }
        return new Rail<T>(new RuntimeType(array.getClass().getComponentType()), ((Object[]) array).length, array);
    }
    
    public static <T> Rail<T> makeRailFromValRail(Type type, ValRail<T> r) {
        Object newArray = type.makeArray(r.length);
        System.arraycopy(r.getBackingArray(), 0, newArray, 0, r.length);
        return new Rail<T>(type, r.length, newArray);
    }
    
    public static <T> Rail<T> makeRailFromJavaArray(Type type, Object array) {
        Rail<T> r = makeRailFromJavaArray(array);
        return new Rail<T>(type, r.length, array);
    }
    
    public static <T> ValRail<T> makeValRailFromJavaArray(Type type, Object array) {
        Rail<T> r = makeRailFromJavaArray(array);
        return new ValRail<T>(type, r.length, array);
    }
    
    public static <T> ValRail<T> makeValRailFromJavaArray(Object array) {
        Rail<T> r = makeRailFromJavaArray(array);
        return new ValRail<T>(r.type, r.length, r.value);
    }

    public static <T> ValRail<T> makeValRailFromRail(Type type, Rail<T> r) {
        Object newArray = type.makeArray(r.length);
        System.arraycopy(r.getBackingArray(), 0, newArray, 0, r.length);
        return new ValRail<T>(r.type, r.length, newArray);
    }
    public static <T> ValRail<T> makeValRailViewFromRail(Type type, Rail<T> r) {
        return new ValRail<T>(r.type, r);
    }
    
}
