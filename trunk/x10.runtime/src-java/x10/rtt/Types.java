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


public class Types {
    public static boolean instanceof$(Type<?> t, Object o) {
        return t.instanceof$(o);
    }

    public static Object cast$(Type<?> t, Object o) {
        if (! instanceof$(t, o))
            throw new ClassCastException(t.toString());
        return o;
    }
    
    // Hack to get around parsing problems for generated Java casts.
    public static <T> T javacast(Object o) {
        return (T) o;
    }

    public static Type runtimeType(Class<?> c) {
        return new RuntimeType(c);
    }

    public static Type<Boolean> BOOLEAN = new BooleanType();
    public static Type<Byte> BYTE = new ByteType();
    public static Type<Short> SHORT = new ShortType();
    public static Type<Character> CHAR = new CharType();
    public static Type<Integer> INT = new IntType();
    public static Type<Long> LONG = new LongType();
    public static Type<Byte> UBYTE = new UByteType();
    public static Type<Short> USHORT = new UShortType();
    public static Type<Integer> UINT = new UIntType();
    public static Type<Long> ULONG = new ULongType();
    public static Type<Float> FLOAT = new FloatType();
    public static Type<Double> DOUBLE = new DoubleType();
    
    public static <T> T conversion(Class<T> dep, Object typeParam) {
        if(dep == java.lang.Byte.class) return (T)conversion(BYTE, typeParam);
        if(dep == java.lang.Short.class) return (T)conversion(SHORT, typeParam);
        if(dep == java.lang.Character.class) return (T)conversion(CHAR, typeParam);
        if(dep == java.lang.Integer.class) return (T)conversion(INT, typeParam);
        if(dep == java.lang.Long.class) return (T)conversion(LONG, typeParam);
        if(dep == java.lang.Float.class) return (T)conversion(FLOAT, typeParam);
        if(dep == java.lang.Double.class) return (T)conversion(DOUBLE, typeParam);
        return (T)typeParam;
    }
    
    public static Object conversion(Type rtt, Object primOrTypeParam) {
        if (rtt == BOOLEAN) {
            if (primOrTypeParam == null) {throw new ClassCastException();}
        }
        if (rtt == BYTE) {
            if (primOrTypeParam == null) {throw new ClassCastException();}
            if (primOrTypeParam instanceof java.lang.Number) return ((java.lang.Number) primOrTypeParam).byteValue();
            if (primOrTypeParam instanceof java.lang.Byte) return primOrTypeParam;
            if (primOrTypeParam instanceof java.lang.Character) return (byte)((java.lang.Character) primOrTypeParam).charValue();
            return primOrTypeParam;
        }
        if (rtt == SHORT) {
            if (primOrTypeParam == null) {throw new ClassCastException();}
            if (primOrTypeParam instanceof java.lang.Number) return ((java.lang.Number) primOrTypeParam).shortValue();
            if (primOrTypeParam instanceof java.lang.Short) return primOrTypeParam;
            if (primOrTypeParam instanceof java.lang.Character) return (short)((java.lang.Character) primOrTypeParam).charValue();
            return primOrTypeParam;
        }
        if (rtt == CHAR) {
            if (primOrTypeParam == null) {throw new ClassCastException();}
            if (primOrTypeParam instanceof java.lang.Byte) return (char)(byte)((java.lang.Byte) primOrTypeParam);
            if (primOrTypeParam instanceof java.lang.Short) return (char)(short)((java.lang.Short) primOrTypeParam);
            if (primOrTypeParam instanceof java.lang.Character) return primOrTypeParam;
            if (primOrTypeParam instanceof java.lang.Integer) return (char)(int)((java.lang.Integer) primOrTypeParam);
            if (primOrTypeParam instanceof java.lang.Long) return (char)(long)((java.lang.Long) primOrTypeParam);
            if (primOrTypeParam instanceof java.lang.Float) return (char)(float)((java.lang.Float) primOrTypeParam);
            if (primOrTypeParam instanceof java.lang.Double) return (char)(double)((java.lang.Double) primOrTypeParam);
            return primOrTypeParam;
        }
        if (rtt == INT) {
            if (primOrTypeParam == null) {throw new ClassCastException();}
            if (primOrTypeParam instanceof java.lang.Number) return ((java.lang.Number) primOrTypeParam).intValue();
            if (primOrTypeParam instanceof java.lang.Character) return (int)((java.lang.Character) primOrTypeParam).charValue();
            if (primOrTypeParam instanceof java.lang.Integer) return primOrTypeParam;
            return primOrTypeParam;
        }
        if (rtt == LONG) {
            if (primOrTypeParam == null) {throw new ClassCastException();}
            if (primOrTypeParam instanceof java.lang.Number) return ((java.lang.Number) primOrTypeParam).longValue();
            if (primOrTypeParam instanceof java.lang.Character) return (long)((java.lang.Character) primOrTypeParam).charValue();
            if (primOrTypeParam instanceof java.lang.Long) return primOrTypeParam;
            return primOrTypeParam;
        }
        if (rtt == FLOAT) {
            if (primOrTypeParam == null) {throw new ClassCastException();}
            if (primOrTypeParam instanceof java.lang.Number) return ((java.lang.Number) primOrTypeParam).floatValue();
            if (primOrTypeParam instanceof java.lang.Character) return (float)((java.lang.Character) primOrTypeParam).charValue();
            if (primOrTypeParam instanceof java.lang.Float) return primOrTypeParam;
            return primOrTypeParam;
        }
        if (rtt == DOUBLE) {
            if (primOrTypeParam == null) {throw new ClassCastException();}
            if (primOrTypeParam instanceof java.lang.Number) return ((java.lang.Number) primOrTypeParam).doubleValue();
            if (primOrTypeParam instanceof java.lang.Character) return (double)((java.lang.Character) primOrTypeParam).charValue();
            if (primOrTypeParam instanceof java.lang.Double) return primOrTypeParam;
            return primOrTypeParam;
        }
        
        // unimplemented
        if (rtt == UBYTE) {
            if (primOrTypeParam == null) {throw new ClassCastException();}
            return primOrTypeParam;
        }
        if (rtt == USHORT) {
            if (primOrTypeParam == null) {throw new ClassCastException();}
            return primOrTypeParam;
        }
        if (rtt == UINT) {
            if (primOrTypeParam == null) {throw new ClassCastException();}
            return primOrTypeParam;
        }
        if (rtt == ULONG) {
            if (primOrTypeParam == null) {throw new ClassCastException();}
            return primOrTypeParam;
        }
        return primOrTypeParam;
    }
}
