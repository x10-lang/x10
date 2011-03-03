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

public abstract class RailFactory {
    public static <T> Rail<T> makeVarRail(Type type, int length) {
        Object value = type.makeArray(length);
        if (!x10.rtt.Types.hasNaturalZero(type)) {
            Object zeroValue = x10.rtt.Types.zeroValue(type);
            java.util.Arrays.fill((Object[]) value, zeroValue);
        }
        return new Rail<T>(type, length, value);
    }

    public static <T> Rail<T> makeVarRail(Type type, int length, T init) {
        Object value = type.makeArray(length);
        Rail.resetLocal(value, init);
        return new Rail<T>(type, length, value);
    }

    public static <T> Rail<T> makeVarRail(Type type, int length, Fun_0_1<Integer,T> init) {
        Object value = type.makeArray(length);
        initJavaArray(value, init);
        return new Rail<T>(type, length, value);
    }

    public static <T> Rail<T> makeVarRail(Type type, int length, int offset, Rail<T> init) {
        Object value = type.makeArray(length);
        System.arraycopy(init.getBackingArray(), offset, value, 0, length);
        return new Rail<T>(type, length, value);
    }

//    public static <T> Rail<T> makeRailFromJavaArray(Object value) {
//        if (value instanceof byte[]) {
//            return new Rail<T>((Type) Types.BYTE, ((byte[]) value).length, value);
//        }
//        if (value instanceof short[]) {
//            return new Rail<T>((Type) Types.SHORT, ((short[]) value).length, value);
//        }
//        if (value instanceof int[]) {
//            return new Rail<T>((Type) Types.INT, ((int[]) value).length, value);
//        }
//        if (value instanceof long[]) {
//            return new Rail<T>((Type) Types.LONG, ((long[]) value).length, value);
//        }
//        if (value instanceof float[]) {
//            return new Rail<T>((Type) Types.FLOAT, ((float[]) value).length, value);
//        }
//        if (value instanceof double[]) {
//            return new Rail<T>((Type) Types.DOUBLE, ((double[]) value).length, value);
//        }
//        if (value instanceof char[]) {
//            return new Rail<T>((Type) Types.CHAR, ((char[]) value).length, value);
//        }
//        if (value instanceof boolean[]) {
//            return new Rail<T>((Type) Types.BOOLEAN, ((boolean[]) value).length, value);
//        }
//        if (value instanceof String[]) {
//            return new Rail<T>((Type) Types.STRING, ((String[]) value).length, value);
//        }
//        // subtype of RefI should have $RTT field 
//        Class<?> componentType = value.getClass().getComponentType();
//        if (x10.core.RefI.class.isAssignableFrom(componentType)) {
//            try {
//                RuntimeType $RTT = (RuntimeType) componentType.getDeclaredField("$RTT").get(null);
//                return new Rail<T>($RTT, ((Object[]) value).length, value);
//            } catch (Exception e) {
//                e.printStackTrace();
//                throw ThrowableUtilities.getCorrespondingX10Exception(e);
//            }
//        }
//        // cannot get correct RTT info. from array
//        return new Rail<T>(new RuntimeType(componentType), ((Object[]) value).length, value);
//    }

    public static <T> x10.array.Array<T> makeArrayFromJavaArray(Type type, Object value) {
        int length = type.arrayLength(value);
        x10.array.Array<T> array = new x10.array.Array<T>(type, length);
        System.arraycopy(value, 0, array.raw.value, 0, length);
        return array;
    }

    public static <T> Rail<T> makeRailFromJavaArray(Type type, Object value) {
        return new Rail<T>(type, type.arrayLength(value) , value);
    }

    private static <T> void initJavaArray(Object value, Fun_0_1<Integer,T> init) {
        if (value instanceof int[]) {
            int[] typed_value = (int[]) value;
            for (int i = 0; i < typed_value.length; i++) {
                typed_value[i] = (Integer) init.$apply(i, Types.INT);
            }
        } else if (value instanceof long[]) {
            long[] typed_value = (long[]) value;
            for (int i = 0; i < typed_value.length; i++) {
                typed_value[i] = (Long) init.$apply(i, Types.INT);
            }
        } else if (value instanceof float[]) {
            float[] typed_value = (float[]) value;
            for (int i = 0; i < typed_value.length; i++) {
                typed_value[i] = (Float) init.$apply(i, Types.INT);
            }
        } else if (value instanceof double[]) {
            double[] typed_value = (double[]) value;
            for (int i = 0; i < typed_value.length; i++) {
                typed_value[i] = (Double) init.$apply(i, Types.INT);
            }
        } else if (value instanceof byte[]) {
            byte[] typed_value = (byte[]) value;
            for (int i = 0; i < typed_value.length; i++) {
                typed_value[i] = (Byte) init.$apply(i, Types.INT);
            }
        } else if (value instanceof short[]) {
            short[] typed_value = (short[]) value;
            for (int i = 0; i < typed_value.length; i++) {
                typed_value[i] = (Short) init.$apply(i, Types.INT);
            }
        } else if (value instanceof char[]) {
            char[] typed_value = (char[]) value;
            for (int i = 0; i < typed_value.length; i++) {
                typed_value[i] = (Character) init.$apply(i, Types.INT);
            }
        } else if (value instanceof boolean[]) {
            boolean[] typed_value = (boolean[]) value;
            for (int i = 0; i < typed_value.length; i++) {
                typed_value[i] = (Boolean) init.$apply(i, Types.INT);
            }
        } else {
            Object[] typed_value = (Object[]) value;
            for (int i = 0; i < typed_value.length; i++) {
                typed_value[i] = init.$apply(i, Types.INT);
            }
        }
    }
}
