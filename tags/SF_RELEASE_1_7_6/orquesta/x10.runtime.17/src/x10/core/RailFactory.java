package x10.core;

import x10.core.fun.Fun_0_1;
import x10.types.RuntimeType;
import x10.types.Type;
import x10.types.Types;

public class RailFactory {
    public static <T> ValRail<T> makeValRail(Type<T> type, int length, Fun_0_1<Integer,T> init) {
        Object o = type.makeArray(length);
        for (int i = 0; i < length; i++) {
            type.setArray(o, i, init.apply(i));
        }
        ValRail<T> array = new ValRail<T>(type, length, o);
        return array;
    }
    
    public static <T> Rail<T> makeVarRail(Type<T> type, int length, Fun_0_1<Integer,T> init) {
        Rail<T> array = new Rail<T>(type, length);
        for (int i = 0; i < length; i++) {
            array.set(i, init.apply(i));
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
        return new Rail<T>(type, r.length, r.getBackingArray());
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
    
}
