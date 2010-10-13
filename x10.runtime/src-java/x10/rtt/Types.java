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

import x10.core.Any;
import x10.core.fun.Fun_0_1;


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
    
    public static String typeName(Object obj) {
        String s;
        if (obj instanceof Any) {
            s = ((Any) obj).getRTT().typeName(obj);
        } else if (Types.getNativeRepRTT(obj) != null) {
            s = Types.getNativeRepRTT(obj).typeName();
        } else {
            // Note: for java classes that don't have RTTs
            s = obj.getClass().toString().substring("class ".length());
        }
        return s;
    }

    public static Type runtimeType(Class<?> c) {
        return new RuntimeType<Class<?>>(c);
    }

    public static Type<Boolean> BOOLEAN = new BooleanType();
    public static Type<Byte> BYTE = new ByteType();
    public static Type<Short> SHORT = new ShortType();
    public static Type<Character> CHAR = new CharType();
    public static Type<Integer> INT = new IntType();
    public static Type<Long> LONG = new LongType();
    public static Type<Float> FLOAT = new FloatType();
    public static Type<Double> DOUBLE = new DoubleType();

    public static RuntimeType<Comparable<?>> COMPARABLE;
    static {
        try {
            COMPARABLE = new RuntimeType<Comparable<?>>(Class.forName("x10.lang.Comparable"));
        } catch (ClassNotFoundException e) {}
    }

    public static Type<String> STRING = new RuntimeType<String>(
        String.class,
        new Type[] {
            new ParameterizedType(Fun_0_1._RTT, Types.INT, Types.CHAR),
            new ParameterizedType(Types.COMPARABLE, new UnresolvedType(-1))
        }
    ) {
        @Override
        public String typeName() {
            return "x10.lang.String";
        }
    };
    public static Type<Object> OBJECT = new RuntimeType<Object>(Object.class) {
        @Override
        public String typeName() {
            return "x10.lang.Object";
        }
        
        @Override
        public boolean isSubtype(x10.rtt.Type<?> o) {
            return o == OBJECT || o == ANY;
        };
    };
    public static Type<Object> ANY = new RuntimeType<Object>(Object.class) {
        @Override
        public String typeName() {
            return "x10.lang.Any";
        }
        
        @Override
        public boolean isSubtype(x10.rtt.Type<?> o) {
            return o == ANY;
        };
    };

    public static Type<?> UBYTE;
    public static Type<?> USHORT;
    public static Type<?> UINT;
    public static Type<?> ULONG;
    static {
        try {
            Class<?> c;
            java.lang.reflect.Field f;
            c = Class.forName("x10.lang.UByte");
            f = c.getDeclaredField("_RTT");
            UBYTE = (RuntimeType<?>) f.get(null);
            c = Class.forName("x10.lang.UShort");
            f = c.getDeclaredField("_RTT");
            USHORT = (RuntimeType<?>) f.get(null);
            c = Class.forName("x10.lang.UInt");
            f = c.getDeclaredField("_RTT");
            UINT = (RuntimeType<?>) f.get(null);
            c = Class.forName("x10.lang.ULong");
            f = c.getDeclaredField("_RTT");
            ULONG = (RuntimeType<?>) f.get(null);
        } catch (Exception e) {}
    }

    public static Type<?> getNativeRepRTT(Object o) {
        if (o instanceof Boolean) return BOOLEAN;
        if (o instanceof Byte) return BYTE;
        if (o instanceof Character) return CHAR;
        if (o instanceof Short) return SHORT;
        if (o instanceof Integer) return INT;
        if (o instanceof Long) return LONG;
        if (o instanceof Float) return FLOAT;
        if (o instanceof Double) return DOUBLE;
        if (o instanceof String) return STRING;
        return null;
    }

    static boolean isStructType(Type<?> rtt) {
        if (
            rtt == BOOLEAN
            || rtt == BYTE  || rtt == SHORT  || rtt == CHAR || rtt == INT   || rtt == LONG
            || rtt == UBYTE  || rtt == USHORT  || rtt == UINT   || rtt == ULONG
            || rtt == FLOAT || rtt == DOUBLE
            ) {
            return true;
        }
        else if (rtt.isSubtype(x10.core.Struct._RTT)) {
            return true;
        }
        return false;
    }

    public static boolean isStruct(Object o) {
        return x10.core.Struct._RTT.instanceof$(o) ||
        BYTE.instanceof$(o) || SHORT.instanceof$(o) || INT.instanceof$(o) || LONG.instanceof$(o) ||
        FLOAT.instanceof$(o) || DOUBLE.instanceof$(o) || CHAR.instanceof$(o) || BOOLEAN.instanceof$(o);
    }

    public static boolean asboolean(Object typeParamOrAny) {
        if (typeParamOrAny == null) {nullIsCastedToStruct();}
        if (typeParamOrAny instanceof java.lang.Boolean) {return (java.lang.Boolean) typeParamOrAny;}
        throw new ClassCastException();
    }
    
    public static byte asbyte(Object typeParamOrAny){
        if (typeParamOrAny == null) {nullIsCastedToStruct();}
        if (typeParamOrAny instanceof java.lang.Number) {return((java.lang.Number) typeParamOrAny).byteValue();}
        throw new ClassCastException();
    }
    
    public static short asshort(Object typeParamOrAny){
        if (typeParamOrAny == null) {nullIsCastedToStruct();}
        if (typeParamOrAny instanceof java.lang.Number) {return((java.lang.Number) typeParamOrAny).shortValue();}
        throw new ClassCastException();
    }
    
    public static int asint(Object typeParamOrAny){
        if (typeParamOrAny == null) {nullIsCastedToStruct();}
        if (typeParamOrAny instanceof java.lang.Number) {return((java.lang.Number) typeParamOrAny).intValue();}
        throw new ClassCastException();
    }

    public static long aslong(Object typeParamOrAny){
        if (typeParamOrAny == null) {nullIsCastedToStruct();}
        if (typeParamOrAny instanceof java.lang.Number) {return((java.lang.Number) typeParamOrAny).longValue();}
        throw new ClassCastException();
    }

    public static float asfloat(Object typeParamOrAny){
        if (typeParamOrAny == null) {nullIsCastedToStruct();}
        if (typeParamOrAny instanceof java.lang.Number) {return((java.lang.Number) typeParamOrAny).floatValue();}
        throw new ClassCastException();
    }

    public static double asdouble(Object typeParamOrAny){
        if (typeParamOrAny == null) {nullIsCastedToStruct();}
        if (typeParamOrAny instanceof java.lang.Number) {return((java.lang.Number) typeParamOrAny).doubleValue();}
        throw new ClassCastException();
    }

    public static char aschar(Object typeParamOrAny) {
        if (typeParamOrAny == null) {nullIsCastedToStruct();}
        if (typeParamOrAny instanceof java.lang.Character) {return (java.lang.Character) typeParamOrAny;}
        throw new ClassCastException();
    }
    
    
    public static Object conversion(Type<?> rtt, Object primOrTypeParam) {
        if (primOrTypeParam == null && isStructType(rtt)) {nullIsCastedToStruct();}
        
        if (rtt == BYTE) {
            if (primOrTypeParam instanceof java.lang.Number) return ((java.lang.Number) primOrTypeParam).byteValue();
            if (primOrTypeParam instanceof java.lang.Byte) return primOrTypeParam;
            return primOrTypeParam;
        }
        if (rtt == SHORT) {
            if (primOrTypeParam instanceof java.lang.Number) return ((java.lang.Number) primOrTypeParam).shortValue();
            if (primOrTypeParam instanceof java.lang.Short) return primOrTypeParam;
            return primOrTypeParam;
        }
        if (rtt == INT) {
            if (primOrTypeParam instanceof java.lang.Number) return ((java.lang.Number) primOrTypeParam).intValue();
            if (primOrTypeParam instanceof java.lang.Integer) return primOrTypeParam;
            return primOrTypeParam;
        }
        if (rtt == LONG) {
            if (primOrTypeParam instanceof java.lang.Number) return ((java.lang.Number) primOrTypeParam).longValue();
            if (primOrTypeParam instanceof java.lang.Long) return primOrTypeParam;
            return primOrTypeParam;
        }
        if (rtt == FLOAT) {
            if (primOrTypeParam instanceof java.lang.Number) return ((java.lang.Number) primOrTypeParam).floatValue();
            if (primOrTypeParam instanceof java.lang.Float) return primOrTypeParam;
            return primOrTypeParam;
        }
        if (rtt == DOUBLE) {
            if (primOrTypeParam instanceof java.lang.Number) return ((java.lang.Number) primOrTypeParam).doubleValue();
            if (primOrTypeParam instanceof java.lang.Double) return primOrTypeParam;
            return primOrTypeParam;
        }
        
        return primOrTypeParam;
    }

    public static void nullIsCastedToStruct(){throw new java.lang.ClassCastException();}
}
