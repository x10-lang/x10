/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2011.
 */

package x10.rtt;

import x10.core.Any;
import x10.core.RefI;
import x10.core.StructI;


public class Types {
    
    // WIP type parameter of Java type
    // N.B. type parameter of Java type is not required at runtime with current design since Java types are represented as erased ones in X10
//    public static final boolean supportTypeParameterOfJavaType = true;
    public static final boolean supportTypeParameterOfJavaType = false;
    
    public static RuntimeType<?> getRTT(Class<?> impl) {
        java.lang.reflect.TypeVariable<?>[] typeVariables = impl.getTypeParameters();
        Class<?>[] interfaces = impl.getInterfaces();
        Class<?> superclass = impl.getSuperclass();   // null for java.lang.Object
        if (supportTypeParameterOfJavaType && (typeVariables.length > 0 || interfaces.length > 0 || superclass != null)) {
            // type parameters for unknown raw Java classes are Any
            RuntimeType.Variance[] variances = new RuntimeType.Variance[typeVariables.length];
            java.util.Arrays.fill(variances, RuntimeType.Variance.INVARIANT);
            // add superclass and all interfaces to parents
            Type<?>[] parents = new Type[interfaces.length + (superclass != null ? 1 : 0)];
            int i = 0;
            for (Class<?> intf : interfaces) {
                java.lang.reflect.TypeVariable<?>[] parentTypeVariables = intf.getTypeParameters();
                RuntimeType<?> parentRTT = getRTT(intf);
                if (parentTypeVariables.length > 0) {
                    Type<?>[] parentParams = new Type<?>[parentTypeVariables.length];
                    // TODO bounds
                    java.util.Arrays.fill(parentParams, ANY);
                    parents[i] = new ParameterizedType(parentRTT, parentParams);
                } else {
                    parents[i] = parentRTT;
                }
                ++i;
            }
            if (superclass != null) {
                java.lang.reflect.TypeVariable<?>[] parentTypeVariables = superclass.getTypeParameters();
                RuntimeType<?> parentRTT = getRTT(superclass);
                if (parentTypeVariables.length > 0) {
                    Type<?>[] parentParams = new Type<?>[parentTypeVariables.length];
                    // TODO bounds
                    java.util.Arrays.fill(parentParams, ANY);
                    parents[i] = new ParameterizedType(parentRTT, parentParams);
                } else {
                    parents[i] = parentRTT;
                }
                ++i;
            }
            return new RuntimeType(impl, variances, parents);
        } else {
            return new RuntimeType(impl);
        }
        // TODO cache RTT to WeakHashMap<Class,RuntimeType>
    }
    public static RuntimeType<?> getRTT(Object obj) {
        RuntimeType<?> rtt = null;
        if (obj instanceof Any) {
            rtt = ((Any) obj).$getRTT();
        } else if (obj != null) {
            // rtt for raw Java classes
            rtt = getRTT(obj.getClass());
        }
        return rtt;
    }
    
    public static Type<?> getParam(Object obj, int i) {
        if (obj instanceof x10.core.Any) {
            return ((Any) obj).$getParam(i);
        }
        // type parameters for unknown raw Java classes are Any
        if (supportTypeParameterOfJavaType) {
            // TODO bounds
            return ANY;
        }
        assert false;
        return null;
    }
    
	// XTENLANG-2488
	// get $RTT field from class using reflection
	public static <T> RuntimeType<T> $RTT(Class<?> c) {
		RuntimeType<T> rtt = null;
		try {
		    java.lang.reflect.Field rttField = c.getField("$RTT");
			if (rttField != null) {
				rtt = (RuntimeType<T>) rttField.get(null);
			}
		} catch (Exception e) {
		}
		return rtt;
	}
	
	
    // Fast implementation of Any.typeName() without boxing
    public static String typeName(Object obj) {
    	return getRTT(obj).typeName(obj);
    }
    public static String typeName(boolean value) {
    	return BOOLEAN.typeName();
    }
    public static String typeName(char value) {
    	return CHAR.typeName();
    }
    // N.B. typeName({byte,short,int,long}) are for signed types.
    public static String typeName(byte value) {
    	return BYTE.typeName();
    }
    public static String typeName(short value) {
    	return SHORT.typeName();
    }
    public static String typeName(int value) {
    	return INT.typeName();
    }
    public static String typeName(long value) {
    	return LONG.typeName();
    }
    public static String typeName(float value) {
    	return FLOAT.typeName();
    }
    public static String typeName(double value) {
    	return DOUBLE.typeName();
    }

    
    // Fast implementation of Any.hashCode() without boxing
    // N.B. hashCode for unsigned types are same as those for signed types
    public static int hashCode(Object value) {
        return value.hashCode();
    }
    public static int hashCode(boolean value) {
        return value ? 1231 : 1237;
    }
//    public static int hashCode(char value) {
//        return value;
//    }
//    public static int hashCode(byte value) {
//        return value;
//    }
//    public static int hashCode(short value) {
//        return value;
//    }
    public static int hashCode(int value) {
        // for char, byte, short and int 
        return value;
    }
    public static int hashCode(long value) {
        return (int)(value ^ (value >>> 32));
    }
    public static int hashCode(float value) {
        return Float.floatToIntBits(value);
    }
    public static int hashCode(double value) {
        long bits = Double.doubleToLongBits(value);
        return (int)(bits ^ (bits >>> 32));
    }
    

    // Fast implementation of Any.toString() without boxing
    public static String toString(Object value) {
        return value.toString();
    }
    // not used because primitives has their own @Native and they are boxed when converted to Any
    public static String toString(boolean value) {
        return Boolean.toString(value);
    }
    public static String toString(char value) {
    	return Character.toString(value);
    }
//    public static String toString(byte value) {
//    	return Integer.toString(value);
//    }
//    public static String toString(short value) {
//    	return Integer.toString(value);
//    }
    public static String toString(int value) {
        // for byte, short and int 
    	return Integer.toString(value);
    }
    public static String toString(long value) {
        return Long.toString(value);
    }
    public static String toString(float value) {
    	return Float.toString(value);
    }
    public static String toString(double value) {
    	return Double.toString(value);
    }
    
    // not used
//    // for convenience
//    public static boolean instanceOf(Object o, RuntimeType<?> rtt) {
//        return rtt.isInstance(o);
//    }
//    public static boolean instanceOf(Object o, RuntimeType<?> rtt, Type<?> param0) {
//        return rtt.isInstance(o, param0);
//    }
//    public static boolean instanceOf(Object o, RuntimeType<?> rtt, Type<?> param0, Type<?> param1) {
//        return rtt.isInstance(o, param0, param1);
//    }
//    public static boolean instanceOf(Object o, RuntimeType<?> rtt, Type<?> param0, Type<?> param1, Type<?> param2) {
//        return rtt.isInstance(o, param0, param1, param2);
//    }
//    public static boolean instanceOf(Object o, RuntimeType<?> rtt, Type<?>... params) {
//        return rtt.isInstance(o, params);
//    }
    
    // box java primitives to x10 boxed types
    public static Object $box(Object o) {
        if (o instanceof java.lang.Byte) {
            return x10.core.Byte.$box(((java.lang.Byte) o).byteValue());
        }
        if (o instanceof java.lang.Short) {
            return x10.core.Short.$box(((java.lang.Short) o).shortValue());
        }
        if (o instanceof java.lang.Integer) {
            return x10.core.Int.$box(((java.lang.Integer) o).intValue());
        }
        if (o instanceof java.lang.Long) {
            return x10.core.Long.$box(((java.lang.Long) o).longValue());
        }
        if (o instanceof java.lang.Float) {
            return x10.core.Float.$box(((java.lang.Float) o).floatValue());
        }
        if (o instanceof java.lang.Double) {
            return x10.core.Double.$box(((java.lang.Double) o).doubleValue());
        }
        if (o instanceof java.lang.Character) {
            return x10.core.Char.$box(((java.lang.Character) o).charValue());
        }
        if (o instanceof java.lang.Boolean) {
            return x10.core.Boolean.$box(((java.lang.Boolean) o).booleanValue());
        }
        if (o instanceof java.lang.String) {
            return x10.core.String.$box((java.lang.String) o);
        }
        return o;
    }
    public static x10.core.Byte $box(byte o) {
        return x10.core.Byte.$box(o);
    }
    public static x10.core.Short $box(short o) {
        return x10.core.Short.$box(o);
    }
    public static x10.core.Int $box(int o) {
        return x10.core.Int.$box(o);
    }
    public static x10.core.Long $box(long o) {
        return x10.core.Long.$box(o);
    }
    public static x10.core.Float $box(float o) {
        return x10.core.Float.$box(o);
    }
    public static x10.core.Double $box(double o) {
        return x10.core.Double.$box(o);
    }
    public static x10.core.Char $box(char o) {
        return x10.core.Char.$box(o);
    }
    public static x10.core.Boolean $box(boolean o) {
        return x10.core.Boolean.$box(o);
    }
    public static x10.core.String $box(java.lang.String o) {
        return x10.core.String.$box(o);
    }
    public static Object $boxu(Object o) {
        if (o instanceof java.lang.Byte) {
            return x10.core.UByte.$box(((java.lang.Byte) o).byteValue());
        }
        if (o instanceof java.lang.Short) {
            return x10.core.UShort.$box(((java.lang.Short) o).shortValue());
        }
        if (o instanceof java.lang.Integer) {
            return x10.core.UInt.$box(((java.lang.Integer) o).intValue());
        }
        if (o instanceof java.lang.Long) {
            return x10.core.ULong.$box(((java.lang.Long) o).longValue());
        }
        assert !(o instanceof java.lang.Float);
        assert !(o instanceof java.lang.Double);
        assert !(o instanceof java.lang.Character);
        assert !(o instanceof java.lang.Boolean);
        assert !(o instanceof java.lang.String);
//        if (o instanceof java.lang.Float) {
//            return x10.core.Float.$box(((java.lang.Float) o).floatValue());
//        }
//        if (o instanceof java.lang.Double) {
//            return x10.core.Double.$box(((java.lang.Double) o).doubleValue());
//        }
//        if (o instanceof java.lang.Character) {
//            return x10.core.Char.$box(((java.lang.Character) o).charValue());
//        }
//        if (o instanceof java.lang.Boolean) {
//            return x10.core.Boolean.$box(((java.lang.Boolean) o).booleanValue());
//        }
//        if (o instanceof java.lang.String) {
//            return x10.core.String.$box((java.lang.String) o);
//        }
        return o;
    }
    public static x10.core.UByte $boxu(byte o) {
        return x10.core.UByte.$box(o);
    }
    public static x10.core.UShort $boxu(short o) {
        return x10.core.UShort.$box(o);
    }
    public static x10.core.UInt $boxu(int o) {
        return x10.core.UInt.$box(o);
    }
    public static x10.core.ULong $boxu(long o) {
        return x10.core.ULong.$box(o);
    }
    
    public static final RuntimeType<Object> ANY = new AnyType();
    public static final RuntimeType<RefI> OBJECT = new ObjectType();
    // Struct is not an X10 type, but it has RTT for runtime type checking such as instanceof
    // create rtt of struct before all struct types (e.g. int)
    public static final RuntimeType<StructI> STRUCT = new StructType();

    // create rtt of comparable before all types that implement comparable (e.g. int)
    public static final RuntimeType<Comparable> COMPARABLE = new NamedType<Comparable>(
        "x10.lang.Comparable",
        Comparable.class, 
        new RuntimeType.Variance[] {
            RuntimeType.Variance.INVARIANT
        }
    ) {
        // make sure deserialized RTT object is not duplicated
        private Object readResolve() throws java.io.ObjectStreamException {
            return COMPARABLE;
        }
    };

    public static final RuntimeType<x10.core.Boolean> BOOLEAN = new BooleanType();
    public static final RuntimeType<x10.core.Char> CHAR = new CharType();
    public static final RuntimeType<x10.core.Byte> BYTE = new ByteType();
    public static final RuntimeType<x10.core.Short> SHORT = new ShortType();
    public static final RuntimeType<x10.core.Int> INT = new IntType();
    public static final RuntimeType<x10.core.Long> LONG = new LongType();
    public static final RuntimeType<x10.core.Float> FLOAT = new FloatType();
    public static final RuntimeType<x10.core.Double> DOUBLE = new DoubleType();
    public static final RuntimeType<x10.core.UByte> UBYTE = new UByteType();
    public static final RuntimeType<x10.core.UShort> USHORT = new UShortType();
    public static final RuntimeType<x10.core.UInt> UINT = new UIntType();
    public static final RuntimeType<x10.core.ULong> ULONG = new ULongType();
    public static final x10.core.Boolean BOOLEAN_ZERO = x10.core.Boolean.FALSE;
    public static final x10.core.Char CHAR_ZERO = x10.core.Char.$box((char)0);
    public static final x10.core.Byte BYTE_ZERO = x10.core.Byte.$box(0);
    public static final x10.core.Short SHORT_ZERO = x10.core.Short.$box(0);
    public static final x10.core.Int INT_ZERO = x10.core.Int.$box(0);
    public static final x10.core.Long LONG_ZERO = x10.core.Long.$box(0l);
    public static final x10.core.Float FLOAT_ZERO = x10.core.Float.$box(0.0F);
    public static final x10.core.Double DOUBLE_ZERO = x10.core.Double.$box(0.0);
    public static final x10.core.UByte UBYTE_ZERO = x10.core.UByte.$box((byte)0);
    public static final x10.core.UShort USHORT_ZERO = x10.core.UShort.$box((short)0);
    public static final x10.core.UInt UINT_ZERO = x10.core.UInt.$box(0);
    public static final x10.core.ULong ULONG_ZERO = x10.core.ULong.$box((long)0);

    public static final RuntimeType<String> STRING = new StringType();

    static boolean isNumericType(Type<?> rtt) {
        if (rtt == BYTE  || rtt == SHORT  || rtt == INT  || rtt == LONG  ||
            rtt == UBYTE || rtt == USHORT || rtt == UINT || rtt == ULONG ||
            rtt == FLOAT || rtt == DOUBLE) {
            return true;
        }
        return false;
    }
    static boolean isStructType(Type<?> rtt) {
    	return isNumericType(rtt) || rtt == CHAR || rtt == BOOLEAN || rtt.isAssignableTo(STRUCT);
    }

    
    public static boolean asboolean(Object typeParamOrAny, Type<?> origRTT) {
        if (typeParamOrAny == null) {nullIsCastToStruct("x10.lang.Boolean");}
        if (typeParamOrAny instanceof x10.core.Boolean) return x10.core.Boolean.$unbox((x10.core.Boolean)typeParamOrAny);
        else if (typeParamOrAny instanceof java.lang.Boolean) {return (java.lang.Boolean) typeParamOrAny;}
        throw new ClassCastException("x10.lang.Boolean");
    }
    
    public static char aschar(Object typeParamOrAny, Type<?> origRTT) {
        if (typeParamOrAny == null) {nullIsCastToStruct("x10.lang.Char");}
        if (typeParamOrAny instanceof x10.core.Char) return x10.core.Char.$unbox((x10.core.Char)typeParamOrAny);
        else if (typeParamOrAny instanceof java.lang.Character) {return (java.lang.Character) typeParamOrAny;}
        throw new ClassCastException("x10.lang.Char");
    }

    public static byte asbyte(Object typeParamOrAny, Type<?> origRTT){
        if (typeParamOrAny == null) {nullIsCastToStruct("x10.lang.Byte");}
        if (isNumericType(origRTT)) {
        	return ((java.lang.Number) typeParamOrAny).byteValue();
        } else {
        	if (typeParamOrAny instanceof x10.core.Byte) return x10.core.Byte.$unbox((x10.core.Byte)typeParamOrAny);
        	else if (typeParamOrAny instanceof java.lang.Byte) {return (java.lang.Byte) typeParamOrAny;}
        }
        throw new ClassCastException("x10.lang.Byte");
    }
    
    public static short asshort(Object typeParamOrAny, Type<?> origRTT){
        if (typeParamOrAny == null) {nullIsCastToStruct("x10.lang.Short");}
        if (isNumericType(origRTT)) {
        	return ((java.lang.Number) typeParamOrAny).shortValue();        	
        } else {
        	if (typeParamOrAny instanceof x10.core.Short) return x10.core.Short.$unbox((x10.core.Short)typeParamOrAny);
        	else if (typeParamOrAny instanceof java.lang.Short) {return (java.lang.Short) typeParamOrAny;}
        }
        throw new ClassCastException("x10.lang.Short");
    }
    
    public static int asint(Object typeParamOrAny, Type<?> origRTT){
        if (typeParamOrAny == null) {nullIsCastToStruct("x10.lang.Int");}
        if (isNumericType(origRTT)) {
        	return ((java.lang.Number) typeParamOrAny).intValue();
        } else {
            if (typeParamOrAny instanceof x10.core.Int) return x10.core.Int.$unbox((x10.core.Int) typeParamOrAny);
            else if (typeParamOrAny instanceof java.lang.Integer) {return (java.lang.Integer) typeParamOrAny;}
        }
        throw new ClassCastException("x10.lang.Int");
    }

    public static long aslong(Object typeParamOrAny, Type<?> origRTT){
        if (typeParamOrAny == null) {nullIsCastToStruct("x10.lang.Long");}
        if (isNumericType(origRTT)) {
        	return ((java.lang.Number) typeParamOrAny).longValue();
        } else {
        	if (typeParamOrAny instanceof x10.core.Long) {return x10.core.Long.$unbox((x10.core.Long)typeParamOrAny);}
        	else if (typeParamOrAny instanceof java.lang.Long) {return (java.lang.Long) typeParamOrAny;}
        }
        throw new ClassCastException("x10.lang.Long");
    }

    public static float asfloat(Object typeParamOrAny, Type<?> origRTT){
        if (typeParamOrAny == null) {nullIsCastToStruct("x10.lang.Float");}
        if (isNumericType(origRTT)) {
        	return ((java.lang.Number) typeParamOrAny).floatValue();
        } else {
        	if (typeParamOrAny instanceof x10.core.Float) {return x10.core.Float.$unbox((x10.core.Float)typeParamOrAny);}
        	else if (typeParamOrAny instanceof java.lang.Float) {return (java.lang.Float) typeParamOrAny;}
        }
        throw new ClassCastException("x10.lang.Float");
    }

    public static double asdouble(Object typeParamOrAny, Type<?> origRTT){
        if (typeParamOrAny == null) {nullIsCastToStruct("x10.lang.Double");}
        if (isNumericType(origRTT)) {
        	return ((java.lang.Number) typeParamOrAny).doubleValue();
        } else {
        	if (typeParamOrAny instanceof x10.core.Double) {return x10.core.Double.$unbox((x10.core.Double)typeParamOrAny);}
        	else if (typeParamOrAny instanceof java.lang.Double) {return (java.lang.Double) typeParamOrAny;}
        }
        throw new ClassCastException("x10.lang.Double");
    }

    public static byte asUByte(Object typeParamOrAny, Type<?> origRTT){
        if (typeParamOrAny == null) {nullIsCastToStruct("x10.lang.UByte");}
        if (isNumericType(origRTT)) {
        	return ((java.lang.Number) typeParamOrAny).byteValue();
        } else {
            if (typeParamOrAny instanceof x10.core.UByte) {return x10.core.UByte.$unbox((x10.core.UByte)typeParamOrAny);}
            else if (typeParamOrAny instanceof java.lang.Byte) {return (java.lang.Byte)typeParamOrAny;}
        }
        throw new ClassCastException("x10.lang.UByte");
    }
    public static Object asBoxedUByte(Object typeParamOrAny, Type<?> origRTT){
    	return x10.core.UByte.$box(asUByte(typeParamOrAny, origRTT));
    }

    public static short asUShort(Object typeParamOrAny, Type<?> origRTT){
        if (typeParamOrAny == null) {nullIsCastToStruct("x10.lang.UShort");}
        if (isNumericType(origRTT)) {
        	return ((java.lang.Number) typeParamOrAny).shortValue();
        } else {
            if (typeParamOrAny instanceof x10.core.UShort) {return x10.core.UShort.$unbox((x10.core.UShort)typeParamOrAny);}
            else if (typeParamOrAny instanceof java.lang.Short) {return (java.lang.Short)typeParamOrAny;}
        }
        throw new ClassCastException("x10.lang.UShort");
    }
    public static Object asBoxedUShort(Object typeParamOrAny, Type<?> origRTT){
    	return x10.core.UShort.$box(asUShort(typeParamOrAny, origRTT));
    }

    public static int asUInt(Object typeParamOrAny, Type<?> origRTT){
        if (typeParamOrAny == null) {nullIsCastToStruct("x10.lang.UInt");}
        if (isNumericType(origRTT)) {
        	return ((java.lang.Number) typeParamOrAny).intValue();
        } else {
        	if (typeParamOrAny instanceof x10.core.UInt) {return x10.core.UInt.$unbox((x10.core.Byte)typeParamOrAny);}
        	else if (typeParamOrAny instanceof java.lang.Integer) {return (java.lang.Integer)typeParamOrAny;}
        }
        throw new ClassCastException("x10.lang.UInt");
    }
    public static Object asBoxedUInt(Object typeParamOrAny, Type<?> origRTT){
    	return x10.core.UInt.$box(asUInt(typeParamOrAny, origRTT));
    }

    public static long asULong(Object typeParamOrAny, Type<?> origRTT){
        if (typeParamOrAny == null) {nullIsCastToStruct("x10.lang.ULong");}
        if (isNumericType(origRTT)) {
        	return ((java.lang.Number) typeParamOrAny).longValue();
        } else {
            if (typeParamOrAny instanceof x10.core.ULong) {return x10.core.ULong.$unbox((x10.core.ULong)typeParamOrAny);}
            else if (typeParamOrAny instanceof java.lang.Long) {return (java.lang.Long)typeParamOrAny;}
        }
        throw new ClassCastException("x10.lang.ULong");
    }
    public static Object asBoxedULong(Object typeParamOrAny, Type<?> origRTT){
    	return x10.core.ULong.$box(asULong(typeParamOrAny, origRTT));
    }

    public static Object asStruct(Type<?> rtt, Object typeParamOrAny) {
        if (typeParamOrAny == null) {nullIsCastToStruct(rtt);}
        // N.B. ClassCastException is not explicitly checked here since it will be checked by a Java cast of the return value
        return typeParamOrAny;
    }
    
    // FIXME this should be replaced by virtual method for user defined conversion
    private static Object conversion(Type<?> rtt, Object primOrTypeParam, boolean convert) {
        if (primOrTypeParam == null) {
            if (isStructType(rtt)) {
                nullIsCastToStruct(rtt);
            }
            else {
                return null;
            }
        }
        
        if (rtt == BOOLEAN) {return x10.core.Boolean.$box(asboolean(primOrTypeParam, convert ? BOOLEAN : null));}
        if (rtt == CHAR) {return x10.core.Char.$box(aschar(primOrTypeParam, convert ? CHAR : null));}
        if (rtt == BYTE) {return x10.core.Byte.$box(asbyte(primOrTypeParam, convert ? BYTE : null));}
        if (rtt == SHORT) {return x10.core.Short.$box(asshort(primOrTypeParam, convert ? SHORT : null));}
        if (rtt == INT) {return x10.core.Int.$box(asint(primOrTypeParam, convert ? INT : null));}
        if (rtt == LONG) {return x10.core.Long.$box(aslong(primOrTypeParam, convert ? LONG : null));}
        if (rtt == FLOAT) {return x10.core.Float.$box(asfloat(primOrTypeParam, convert ? FLOAT : null));}
        if (rtt == DOUBLE) {return x10.core.Double.$box(asdouble(primOrTypeParam, convert ? DOUBLE : null));}
        if (rtt == UBYTE) {return asBoxedUByte(primOrTypeParam, convert ? UBYTE : null);}
        if (rtt == USHORT) {return asBoxedUShort(primOrTypeParam, convert ? USHORT : null);}
        if (rtt == UINT) {return asBoxedUInt(primOrTypeParam, convert ? UINT : null);}
        if (rtt == ULONG) {return asBoxedULong(primOrTypeParam, convert ? ULONG : null);}
        
        if (rtt == STRING) {
            if (primOrTypeParam instanceof x10.core.String) return x10.core.String.$unbox((x10.core.String) primOrTypeParam);
            return primOrTypeParam;
        }
        else if (primOrTypeParam instanceof java.lang.String) { // i.e. rtt==Any|Object|Fun
            return x10.core.String.$box((java.lang.String) primOrTypeParam);
        }
        
        return primOrTypeParam;
    }

    public static Object conversion(Type<?> rtt, Object primOrTypeParam) {
    	return conversion(rtt, primOrTypeParam, false);
    }

    private static void nullIsCastToStruct(Type<?> rtt) {throw new java.lang.ClassCastException(rtt.typeName());}
    private static void nullIsCastToStruct(String msg){throw new java.lang.ClassCastException(msg);}

    public static boolean hasNaturalZero(Type<?> rtt) {
    	return rtt.isAssignableTo(OBJECT) || isNumericType(rtt) || rtt == CHAR || rtt == BOOLEAN;
    }

    public static <T> T cast(final java.lang.Object self, Type<?> rtt) {
        if (self == null) return null;
        if (rtt != null && !rtt.isInstance(self)) throw new x10.lang.ClassCastException(rtt.typeName());
        return (T) self;
    }
    
    public static <T> T castConversion(final java.lang.Object self, Type<?> rtt) {
        if (self == null) return null;
        T ret = (T) conversion(rtt, self, true);
        if (rtt != null && !rtt.isInstance(ret)) throw new x10.lang.ClassCastException(rtt.typeName());
        return ret;
    }

    // TODO haszero
    /*
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
        // Note: user defined structs is not supported
//        assert !STRUCT.getJavaClass().isAssignableFrom(c) : "user defined structs is not supported";
        return null;
    }
    */
    public static Object zeroValue(Type<?> rtt) {
        Type<?>[] typeParams = null;
        if (rtt instanceof ParameterizedType) {
            ParameterizedType<?> pt = (ParameterizedType<?>) rtt;
            rtt = pt.getRuntimeType(); 
            typeParams = pt.getParams();
        }
        if (isStructType(rtt)) {
            if (rtt == BYTE) return BYTE_ZERO;
            if (rtt == SHORT) return SHORT_ZERO;
            if (rtt == INT) return INT_ZERO;
            if (rtt == LONG) return LONG_ZERO;
            if (rtt == UBYTE) return UBYTE_ZERO;
            if (rtt == USHORT) return USHORT_ZERO;
            if (rtt == UINT) return UINT_ZERO;
            if (rtt == ULONG) return ULONG_ZERO;
            if (rtt == FLOAT) return FLOAT_ZERO;
            if (rtt == DOUBLE) return DOUBLE_ZERO;
            if (rtt == CHAR) return CHAR_ZERO;
            if (rtt == BOOLEAN) return BOOLEAN_ZERO;
            // N.B. to enable following special paths, make corresponding $RTTs singleton
            // N.B. since GlobalRef and IndexedMemoryChunk have their own zero value constructor, special paths are no longer needed
//            if (rtt == x10.core.IndexedMemoryChunk.$RTT) return new x10.core.IndexedMemoryChunk(typeParams[0], (java.lang.System) null);
//            if (rtt == x10.core.GlobalRef.$RTT) return new x10.core.GlobalRef(typeParams[0], (java.lang.System) null);
            // for user-defined structs, call zero value constructor
            try {
                Class<?> impl = rtt.getImpl();
                java.lang.reflect.Constructor<?> ctor = null;
                Class<?>[] paramTypes = null;
                for (java.lang.reflect.Constructor<?> ctor0 : impl.getConstructors()) {
                    paramTypes = ctor0.getParameterTypes();
                    if (paramTypes.length >= 1 && paramTypes[paramTypes.length-1].equals(java.lang.System.class)) {
                        ctor = ctor0;
                        break;
                    }
                }
                assert ctor != null;
                Object[] params = new Object[paramTypes.length];
                
                /*
                int i = 0;
                if (typeParams != null) {
                    for ( ; i < typeParams.length; ++i) {
                        // pass type params
                        params[i] = typeParams[i];
                    }
                }
                for ( ; i < paramTypes.length; ++i) {
                    // these values are not necessarily zero value
                    params[i] = zeroValue(paramTypes[i]);
                }
                */
                assert typeParams == null ? paramTypes.length == 1/*(java.lang.System)null*/ : paramTypes.length == typeParams.length/*T1,T2,...*/ + 1/*(java.lang.System)null*/;
                int i = 0;
                if (typeParams != null) {
                    for ( ; i < typeParams.length; ++i) {
                        // pass type params
                        params[i] = typeParams[i];
                    }
                }
                params[i] = null;

                return ctor.newInstance(params);
            } catch (Exception e) {
                e.printStackTrace();
                throw new java.lang.Error(e);
            }
        }
        return null;
    }
}
