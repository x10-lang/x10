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

    public static RuntimeType runtimeType(Class<?> c) {
        return new RuntimeType<Class<?>>(c);
    }

    // create rtt of comparable before all types that implement comparable (e.g. int)
    public static final RuntimeType<?> COMPARABLE = new RuntimeType(Comparable.class, RuntimeType.Variance.INVARIANT) {
        @Override
        public String typeName() {
            return "x10.lang.Comparable";
        }
    };

    public static final RuntimeType<Boolean> BOOLEAN = new BooleanType();
    public static final RuntimeType<Byte> BYTE = new ByteType();
    public static final RuntimeType<Short> SHORT = new ShortType();
    public static final RuntimeType<Character> CHAR = new CharType();
    public static final RuntimeType<Integer> INT = new IntType();
    public static final RuntimeType<Long> LONG = new LongType();
    public static final RuntimeType<Float> FLOAT = new FloatType();
    public static final RuntimeType<Double> DOUBLE = new DoubleType();
    public static final Object BOOLEAN_ZERO = Boolean.valueOf(false);
    public static final Object BYTE_ZERO = Byte.valueOf((byte) 0);
    public static final Object SHORT_ZERO = Short.valueOf((short) 0);
    public static final Object CHAR_ZERO = Character.valueOf((char) 0);
    public static final Object INT_ZERO = Integer.valueOf(0);
    public static final Object LONG_ZERO = Long.valueOf(0L);
    public static final Object FLOAT_ZERO = Float.valueOf(0.0F);
    public static final Object DOUBLE_ZERO = Double.valueOf(0.0);

    public static final RuntimeType<String> STRING = new RuntimeType<String>(
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
    public static final RuntimeType<Object> OBJECT = new RuntimeType<Object>(Object.class) {
        @Override
        public String typeName() {
            return "x10.lang.Object";
        }
        
        @Override
        public boolean isSubtype(x10.rtt.Type<?> o) {
            return o == OBJECT || o == ANY;
        };
    };
    public static final RuntimeType<Object> ANY = new RuntimeType<Object>(Object.class) {
        @Override
        public String typeName() {
            return "x10.lang.Any";
        }
        
        @Override
        public boolean isSubtype(x10.rtt.Type<?> o) {
            return o == ANY;
        };
    };

    public static RuntimeType<?> UBYTE;
    public static RuntimeType<?> USHORT;
    public static RuntimeType<?> UINT;
    public static RuntimeType<?> ULONG;
    public static Object UBYTE_ZERO;
    public static Object USHORT_ZERO;
    public static Object UINT_ZERO;
    public static Object ULONG_ZERO;
    static {
        try {
            Class<?> c;
            java.lang.reflect.Field f;
            c = Class.forName("x10.lang.UByte");
            f = c.getDeclaredField("_RTT");
            UBYTE = (RuntimeType<?>) f.get(null);
            UBYTE_ZERO = c.getConstructor(new Class[]{byte.class}).newInstance(new Object[]{(byte)0});
            c = Class.forName("x10.lang.UShort");
            f = c.getDeclaredField("_RTT");
            USHORT = (RuntimeType<?>) f.get(null);
            USHORT_ZERO = c.getConstructor(new Class[]{short.class}).newInstance(new Object[]{(short)0});
            c = Class.forName("x10.lang.UInt");
            f = c.getDeclaredField("_RTT");
            UINT = (RuntimeType<?>) f.get(null);
            UINT_ZERO = c.getConstructor(new Class[]{int.class}).newInstance(new Object[]{0});
            c = Class.forName("x10.lang.ULong");
            f = c.getDeclaredField("_RTT");
            ULONG = (RuntimeType<?>) f.get(null);
            ULONG_ZERO = c.getConstructor(new Class[]{long.class}).newInstance(new Object[]{0L});
        } catch (Exception e) {}
    }

    public static RuntimeType<?> getNativeRepRTT(Object o) {
        if (o instanceof Byte) return BYTE;
        if (o instanceof Short) return SHORT;
        if (o instanceof Integer) return INT;
        if (o instanceof Long) return LONG;
        if (o instanceof Float) return FLOAT;
        if (o instanceof Double) return DOUBLE;
        if (o instanceof Character) return CHAR;
        if (o instanceof Boolean) return BOOLEAN;
        if (o instanceof String) return STRING;
        return null;
    }

    static boolean isStructType(Type<?> rtt) {
        if (rtt == BYTE  || rtt == SHORT  || rtt == INT   || rtt == LONG ||
            /*rtt == UBYTE  || rtt == USHORT  || rtt == UINT || rtt == ULONG ||*/
            rtt == FLOAT || rtt == DOUBLE || rtt == CHAR || rtt == BOOLEAN) {
            return true;
        }
        else if (rtt.isSubtype(x10.core.Struct._RTT)) {
            return true;
        }
        return false;
    }

    public static boolean instanceofObject(Object o) {
        return o != null && !isStruct(o);
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

    public static Object asStruct(Type<?> rtt, Object typeParamOrAny) {
        if (typeParamOrAny == null) {nullIsCastedToStruct();}

        if (rtt == UBYTE) {
            if (typeParamOrAny instanceof x10.lang.UByte) { return typeParamOrAny;}
//            if (typeParamOrAny instanceof x10.lang.UShort) { return (UByte)...;}
//            if (typeParamOrAny instanceof x10.lang.UInt) { return (UByte)...;}
//            if (typeParamOrAny instanceof x10.lang.ULong) { return (UByte)...;}
        }
        else if (rtt == USHORT) {
            if (typeParamOrAny instanceof x10.lang.UShort) { return typeParamOrAny;}
        }
        else if (rtt == UINT) {
            if (typeParamOrAny instanceof x10.lang.UInt) { return typeParamOrAny;}
        }
        else if (rtt == ULONG) {
            if (typeParamOrAny instanceof x10.lang.ULong) { return typeParamOrAny;}
        }
        else {
            return typeParamOrAny;
        }
        throw new ClassCastException();
    }
    
    public static Object conversion(Type<?> rtt, Object primOrTypeParam) {
        if (primOrTypeParam == null && isStructType(rtt)) {nullIsCastedToStruct();}
        
        if (rtt == BYTE) {
            if (primOrTypeParam instanceof java.lang.Byte) return primOrTypeParam;
            if (primOrTypeParam instanceof java.lang.Number) return ((java.lang.Number) primOrTypeParam).byteValue();
            return primOrTypeParam;
        }
        if (rtt == SHORT) {
            if (primOrTypeParam instanceof java.lang.Short) return primOrTypeParam;
            if (primOrTypeParam instanceof java.lang.Number) return ((java.lang.Number) primOrTypeParam).shortValue();
            return primOrTypeParam;
        }
        if (rtt == INT) {
            if (primOrTypeParam instanceof java.lang.Integer) return primOrTypeParam;
            if (primOrTypeParam instanceof java.lang.Number) return ((java.lang.Number) primOrTypeParam).intValue();
            return primOrTypeParam;
        }
        if (rtt == LONG) {
            if (primOrTypeParam instanceof java.lang.Long) return primOrTypeParam;
            if (primOrTypeParam instanceof java.lang.Number) return ((java.lang.Number) primOrTypeParam).longValue();
            return primOrTypeParam;
        }
        if (rtt == FLOAT) {
            if (primOrTypeParam instanceof java.lang.Float) return primOrTypeParam;
            if (primOrTypeParam instanceof java.lang.Number) return ((java.lang.Number) primOrTypeParam).floatValue();
            return primOrTypeParam;
        }
        if (rtt == DOUBLE) {
            if (primOrTypeParam instanceof java.lang.Double) return primOrTypeParam;
            if (primOrTypeParam instanceof java.lang.Number) return ((java.lang.Number) primOrTypeParam).doubleValue();
            return primOrTypeParam;
        }
        
        return primOrTypeParam;
    }

    public static void nullIsCastedToStruct(){throw new java.lang.ClassCastException();}

    public static boolean hasNaturalZero(Type<?> rtt) {
        if (rtt.isSubtype(OBJECT) ||
            rtt == BYTE || rtt == SHORT || rtt == INT || rtt == LONG ||
            /*rtt == UBYTE || rtt == USHORT || rtt == UINT || rtt == ULONG ||*/
            rtt == FLOAT || rtt == DOUBLE || rtt == CHAR || rtt == BOOLEAN) return true;
        return false;
    }

    private static Object zeroValue(Class<?> c) {
        if (c.equals(BYTE.getJavaClass()) || c.equals(Byte.class)) return BYTE_ZERO;
        if (c.equals(SHORT.getJavaClass()) || c.equals(Short.class)) return SHORT_ZERO;
        if (c.equals(INT.getJavaClass()) || c.equals(Integer.class)) return INT_ZERO;
        if (c.equals(LONG.getJavaClass()) || c.equals(Long.class)) return LONG_ZERO;
        if (c.equals(UBYTE.getJavaClass())) return UBYTE_ZERO;
        if (c.equals(USHORT.getJavaClass())) return USHORT_ZERO;
        if (c.equals(UINT.getJavaClass())) return UINT_ZERO;
        if (c.equals(ULONG.getJavaClass())) return ULONG_ZERO;
        if (c.equals(FLOAT.getJavaClass()) || c.equals(Float.class)) return FLOAT_ZERO;
        if (c.equals(DOUBLE.getJavaClass()) || c.equals(Double.class)) return DOUBLE_ZERO;
        if (c.equals(CHAR.getJavaClass()) || c.equals(Character.class)) return CHAR_ZERO;
        if (c.equals(BOOLEAN.getJavaClass()) || c.equals(Boolean.class)) return BOOLEAN_ZERO;
        if (x10.core.Struct.class.isAssignableFrom(c)) {
            try {
                Object zero = null;
                // Generate "default" constructor for all non-primitive structs, or 
                //zero = c.getConstructor(null).newInstance(null);
                // Instantiate with an arbitrary constructor then initialize all fields with zero value recursively
                java.lang.reflect.Constructor<?> ctor = c.getConstructors()[0];
                Class<?>[] paramTypes = ctor.getParameterTypes();
                Object[] params = new Object[paramTypes.length];
                for (int i = 0; i < paramTypes.length; ++i) {
                    // these value is not necessarily same as zero value
                    params[i] = zeroValue(paramTypes[i]);
                }
                zero = ctor.newInstance(params);
                for (java.lang.reflect.Field field : c.getDeclaredFields()) {
                    if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) continue;
                    field.setAccessible(true);
                    field.set(zero, zeroValue(field.getType()));
                }
                return zero;
            } catch (Exception e) {
                e.printStackTrace();
                throw new java.lang.Error(e);
            }
        }
        // zero-length array, or null (fall through)
        //if (c.isArray()) return java.lang.reflect.Array.newInstance(c.getComponentType(), 0);
        return null;
    }

    public static Object zeroValue(Type<?> rtt) {
        //assert isStructType(rtt) : "haszero is valid only for structs";
        return zeroValue(rtt.getJavaClass());
    }
}
