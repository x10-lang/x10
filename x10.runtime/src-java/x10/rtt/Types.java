/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.rtt;

import x10.core.Any;


public class Types {
    
    public static RuntimeType/*<?>*/ getRTT(Class<?> impl) {
        return RuntimeType.make(impl);
    }

    public static RuntimeType/*<?>*/ getRTT(Object obj) {
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
        if (obj instanceof Any) {
            return ((Any) obj).$getParam(i);
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
    

    /*
     * returns runtime type for known types.
     * mainly for primitives, string and exceptions.
     */
    public static RuntimeType/*<?>*/ getRTTForKnownType(Class<?> javaClass) {
        if (Object.class.equals(javaClass)) {
            return ANY;
        } else if (String.class.equals(javaClass)) {
            return STRING;
        } else if (java.lang.Comparable.class.equals(javaClass)) {
            return COMPARABLE;
        } else if (java.lang.CharSequence.class.equals(javaClass)) {
            return CHAR_SEQUENCE;
        } else if (javaClass.isPrimitive()) {
            if (byte.class.equals(javaClass)) {
                return BYTE;
            } else if (short.class.equals(javaClass)) {
                return SHORT;
            } else if (int.class.equals(javaClass)) {
                return INT;
            } else if (long.class.equals(javaClass)) {
                return LONG;
            } else if (float.class.equals(javaClass)) {
                return FLOAT;
            } else if (double.class.equals(javaClass)) {
                return DOUBLE;
            } else if (char.class.equals(javaClass)) {
                return CHAR;
            } else if (boolean.class.equals(javaClass)) {
                return BOOLEAN;
            }
        } else if (java.lang.Throwable.class.isAssignableFrom(javaClass)) {
            if (java.lang.RuntimeException.class.isAssignableFrom(javaClass)) {
                if (java.lang.NullPointerException.class.equals(javaClass)) {
                    return NULL_POINTER_EXCEPTION;
                } else if (java.lang.ClassCastException.class.equals(javaClass)) {
                    return CLASS_CAST_EXCEPTION;
                } else if (java.lang.ArithmeticException.class.equals(javaClass)) {
                    return ARITHMETIC_EXCEPTION;
                } else if (java.lang.UnsupportedOperationException.class.equals(javaClass)) {
                    return UNSUPPORTED_OPERATION_EXCEPTION;
                } else if (java.util.NoSuchElementException.class.equals(javaClass)) {
                    return NO_SUCH_ELEMENT_EXCEPTION;
                } else if (java.lang.NegativeArraySizeException.class.equals(javaClass)) {
                    return NEGATIVE_ARRAY_SIZE_EXCEPTION;
                } else if (java.lang.ArrayIndexOutOfBoundsException.class.equals(javaClass)) {
                    return ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION;
                } else if (java.lang.StringIndexOutOfBoundsException.class.equals(javaClass)) {
                    return STRING_INDEX_OUT_OF_BOUNDS_EXCEPTION;
                } else if (java.lang.IndexOutOfBoundsException.class.equals(javaClass)) {
                    return INDEX_OUT_OF_BOUNDS_EXCEPTION;
                } else if (java.lang.NumberFormatException.class.equals(javaClass)) {
                    return NUMBER_FORMAT_EXCEPTION;
                } else if (java.lang.IllegalArgumentException.class.equals(javaClass)) {
                    return ILLEGAL_ARGUMENT_EXCEPTION;
                } else if (java.lang.IllegalStateException.class.equals(javaClass)) {
                    return ILLEGAL_STATE_EXCEPTION;
                } else if (java.lang.RuntimeException.class.equals(javaClass)) {
                    return EXCEPTION;
                }
            } else if (java.lang.Exception.class.isAssignableFrom(javaClass)) {
                if (java.lang.Exception.class.equals(javaClass)) {
                    return CHECKED_EXCEPTION;
                }
            } else if (java.lang.Error.class.isAssignableFrom(javaClass)) {
                if (java.lang.InternalError.class.equals(javaClass)) {
                    return INTERNAL_ERROR;
                } else if (java.lang.OutOfMemoryError.class.equals(javaClass)) {
                    return OUT_OF_MEMORY_ERROR;
                } else if (java.lang.StackOverflowError.class.equals(javaClass)) {
                    return STACK_OVERFLOW_ERROR;
                } else if (java.lang.AssertionError.class.equals(javaClass)) {
                    return ASSERTION_ERROR;
                } else if (java.lang.Error.class.equals(javaClass)) {
                    return ERROR;
                }
            } else {
                if (java.lang.Throwable.class.equals(javaClass)) {
                    return CHECKED_THROWABLE;
                }
            }
        }
        return null;
    }

    public static final RuntimeType<Object> ANY = new AnyType();
    // Struct is not an X10 type, but it has RTT for runtime type checking such as instanceof
    // create rtt of struct before all struct types (e.g. Int)
    public static final RuntimeType<x10.core.StructI> STRUCT = new StructType();

    // create rtt of Comparable before all types that implement Comparable (e.g. Int)
    public static final RuntimeType<java.lang.Comparable> COMPARABLE = new NamedType<java.lang.Comparable>(
        "x10.lang.Comparable",
        java.lang.Comparable.class,
        1,
        null
        );

    // create rtt of CharSequence before all types that implement CharSequence (e.g. String)
    public static final RuntimeType<java.lang.CharSequence> CHAR_SEQUENCE = new NamedType<java.lang.CharSequence>(
        "x10.lang.CharSequence",
        java.lang.CharSequence.class,
        0,
        null
        );

    public static final RuntimeType<java.lang.Throwable> CHECKED_THROWABLE = new NamedType<java.lang.Throwable>(
	"x10.lang.CheckedThrowable",
	java.lang.Throwable.class,
	0,
	new Type[] { ANY }
    );
    
    public static final RuntimeType<java.lang.Exception> CHECKED_EXCEPTION = new NamedType<java.lang.Exception>(
	"x10.lang.CheckedException",
	java.lang.Exception.class,
	0,
	new Type[] { CHECKED_THROWABLE }
    );
    
    public static final RuntimeType<java.lang.RuntimeException> EXCEPTION = new NamedType<java.lang.RuntimeException>(
	"x10.lang.Exception",
	java.lang.RuntimeException.class,
	0,
	new Type[] { CHECKED_EXCEPTION }
    );
    
    public static final RuntimeType<java.lang.NullPointerException> NULL_POINTER_EXCEPTION = new NamedType<java.lang.NullPointerException>(
	"x10.lang.NullPointerException",
	java.lang.NullPointerException.class,
	0,
	new Type[] { EXCEPTION }
    );
    
    public static final RuntimeType<java.lang.ClassCastException> CLASS_CAST_EXCEPTION = new NamedType<java.lang.ClassCastException>(
	"x10.lang.ClassCastException",
	java.lang.ClassCastException.class,
	0,
	new Type[] { EXCEPTION }
    );
    
    public static final RuntimeType<java.lang.IndexOutOfBoundsException> INDEX_OUT_OF_BOUNDS_EXCEPTION = new NamedType<java.lang.IndexOutOfBoundsException>(
	"x10.lang.IndexOutOfBoundsException",
	java.lang.IndexOutOfBoundsException.class,
	0,
	new Type[] { EXCEPTION }
    );
    
    public static final RuntimeType<java.lang.NegativeArraySizeException> NEGATIVE_ARRAY_SIZE_EXCEPTION = new NamedType<java.lang.NegativeArraySizeException>(
	"x10.lang.NegativeArraySizeException",
	java.lang.NegativeArraySizeException.class,
	0,
	new Type[] { EXCEPTION }
    );

    public static final RuntimeType<java.util.MissingResourceException> MISSING_RESOURCE_EXCEPTION = new NamedType<java.util.MissingResourceException>(
	"x10.util.MissingResourceException",
	java.util.MissingResourceException.class,
	0,
	new Type[] { EXCEPTION }
    );
    
    public static final RuntimeType<java.lang.ArrayIndexOutOfBoundsException> ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION = new NamedType<java.lang.ArrayIndexOutOfBoundsException>(
	"x10.lang.ArrayIndexOutOfBoundsException",
	java.lang.ArrayIndexOutOfBoundsException.class,
	0,
	new Type[] { INDEX_OUT_OF_BOUNDS_EXCEPTION }
    );
    
    public static final RuntimeType<java.lang.StringIndexOutOfBoundsException> STRING_INDEX_OUT_OF_BOUNDS_EXCEPTION = new NamedType<java.lang.StringIndexOutOfBoundsException>(
	"x10.lang.StringIndexOutOfBoundsException",
	java.lang.StringIndexOutOfBoundsException.class,
	0,
	new Type[] { INDEX_OUT_OF_BOUNDS_EXCEPTION }
    );
    
    public static final RuntimeType<java.lang.ArithmeticException> ARITHMETIC_EXCEPTION = new NamedType<java.lang.ArithmeticException>(
	"x10.lang.ArithmeticException",
	java.lang.ArithmeticException.class,
	0,
	new Type[] { EXCEPTION }
    );
    
    public static final RuntimeType<java.lang.IllegalStateException> ILLEGAL_STATE_EXCEPTION = new NamedType<java.lang.IllegalStateException>(
        "x10.lang.IllegalStateException",
        java.lang.IllegalStateException.class,
    	0,
        new Type[] { EXCEPTION }
    );
    
    public static final RuntimeType<java.lang.IllegalArgumentException> ILLEGAL_ARGUMENT_EXCEPTION = new NamedType<java.lang.IllegalArgumentException>(
	"x10.lang.IllegalArgumentException",
	java.lang.IllegalArgumentException.class,
	0,
	new Type[] { EXCEPTION }
    );
    
    public static final RuntimeType<java.lang.NumberFormatException> NUMBER_FORMAT_EXCEPTION = new NamedType<java.lang.NumberFormatException>(
	"x10.lang.NumberFormatException",
	java.lang.NumberFormatException.class,
	0,
	new Type[] { ILLEGAL_ARGUMENT_EXCEPTION }
    );
    
    public static final RuntimeType<java.lang.UnsupportedOperationException> UNSUPPORTED_OPERATION_EXCEPTION = new NamedType<java.lang.UnsupportedOperationException>(
	"x10.lang.UnsupportedOperationException",
	java.lang.UnsupportedOperationException.class,
	0,
	new Type[] { EXCEPTION }
    );
    
    public static final RuntimeType<java.util.NoSuchElementException> NO_SUCH_ELEMENT_EXCEPTION = new NamedType<java.util.NoSuchElementException>(
	"x10.util.NoSuchElementException",
	java.util.NoSuchElementException.class,
	0,
	new Type[] { EXCEPTION }
    );
    
    public static final RuntimeType<java.lang.Error> ERROR = new NamedType<java.lang.Error>(
	"x10.lang.Error",
	java.lang.Error.class,
	0,
	new Type[] { CHECKED_THROWABLE }
    );
    
    public static final RuntimeType<java.lang.AssertionError> ASSERTION_ERROR = new NamedType<java.lang.AssertionError>(
	"x10.lang.AssertionError",
	java.lang.AssertionError.class,
	0,
	new Type[] { ERROR }
    );
    
    public static final RuntimeType<java.lang.StackOverflowError> STACK_OVERFLOW_ERROR = new NamedType<java.lang.StackOverflowError>(
	"x10.lang.StackOverflowError",
	java.lang.StackOverflowError.class,
	0,
	new Type[] { ERROR }
    );
    
    public static final RuntimeType<java.lang.OutOfMemoryError> OUT_OF_MEMORY_ERROR = new NamedType<java.lang.OutOfMemoryError>(
	"x10.lang.OutOfMemoryError",
	java.lang.OutOfMemoryError.class,
	0,
	new Type[] { ERROR }
    );
    
    public static final RuntimeType<java.lang.InternalError> INTERNAL_ERROR = new NamedType<java.lang.InternalError>(
        "x10.lang.InternalError",
        java.lang.InternalError.class,
    	0,
        new Type[] { ERROR }
    );

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
    public static final x10.core.Boolean BOOLEAN_ZERO = x10.core.Boolean.$box(false);
    public static final x10.core.Char CHAR_ZERO = x10.core.Char.$box((char)0);
    public static final x10.core.Byte BYTE_ZERO = x10.core.Byte.$box(0);
    public static final x10.core.Short SHORT_ZERO = x10.core.Short.$box(0);
    public static final x10.core.Int INT_ZERO = x10.core.Int.$box(0);
    public static final x10.core.Long LONG_ZERO = x10.core.Long.$box(0L);
    public static final x10.core.Float FLOAT_ZERO = x10.core.Float.$box(0.0F);
    public static final x10.core.Double DOUBLE_ZERO = x10.core.Double.$box(0.0);
    public static final x10.core.UByte UBYTE_ZERO = x10.core.UByte.$box(0);
    public static final x10.core.UShort USHORT_ZERO = x10.core.UShort.$box(0);
    public static final x10.core.UInt UINT_ZERO = x10.core.UInt.$box(0);
    public static final x10.core.ULong ULONG_ZERO = x10.core.ULong.$box(0L);

    public static final RuntimeType<String> STRING = new StringType();

    static boolean isNumericType(Type<?> rtt) {
        if (rtt == BYTE  || rtt == SHORT  || rtt == INT  || rtt == LONG  ||
            rtt == UBYTE || rtt == USHORT || rtt == UINT || rtt == ULONG ||
            rtt == FLOAT || rtt == DOUBLE) {
            return true;
        }
        return false;
    }
    public static boolean isPrimitiveType(Type<?> rtt) {
        return isNumericType(rtt) || rtt == CHAR || rtt == BOOLEAN;
    }
    static boolean isStructType(Type<?> rtt) {
    	return isPrimitiveType(rtt) || rtt.isAssignableTo(STRUCT);
    }
    public static boolean isStringType(Type<?> rtt) {
        return rtt == STRING;
    }

    
    public static boolean asboolean(Object typeParamOrAny, Type<?> origRTT) {
        if (typeParamOrAny == null) {nullIsCastToStruct("x10.lang.Boolean");}
        if (typeParamOrAny instanceof x10.core.Boolean) return x10.core.Boolean.$unbox((x10.core.Boolean)typeParamOrAny);
        else if (typeParamOrAny instanceof java.lang.Boolean) {return (java.lang.Boolean) typeParamOrAny;}
        throw new java.lang.ClassCastException("x10.lang.Boolean");
    }
    
    public static char aschar(Object typeParamOrAny, Type<?> origRTT) {
        if (typeParamOrAny == null) {nullIsCastToStruct("x10.lang.Char");}
        if (typeParamOrAny instanceof x10.core.Char) return x10.core.Char.$unbox((x10.core.Char)typeParamOrAny);
        else if (typeParamOrAny instanceof java.lang.Character) {return (java.lang.Character) typeParamOrAny;}
        throw new java.lang.ClassCastException("x10.lang.Char");
    }

    public static byte asbyte(Object typeParamOrAny, Type<?> origRTT){
        if (typeParamOrAny == null) {nullIsCastToStruct("x10.lang.Byte");}
        if (isNumericType(origRTT)) {
        	return ((java.lang.Number) typeParamOrAny).byteValue();
        } else {
        	if (typeParamOrAny instanceof x10.core.Byte) return x10.core.Byte.$unbox((x10.core.Byte)typeParamOrAny);
        	else if (typeParamOrAny instanceof java.lang.Byte) {return (java.lang.Byte) typeParamOrAny;}
        }
        throw new java.lang.ClassCastException("x10.lang.Byte");
    }
    
    public static short asshort(Object typeParamOrAny, Type<?> origRTT){
        if (typeParamOrAny == null) {nullIsCastToStruct("x10.lang.Short");}
        if (isNumericType(origRTT)) {
        	return ((java.lang.Number) typeParamOrAny).shortValue();        	
        } else {
        	if (typeParamOrAny instanceof x10.core.Short) return x10.core.Short.$unbox((x10.core.Short)typeParamOrAny);
        	else if (typeParamOrAny instanceof java.lang.Short) {return (java.lang.Short) typeParamOrAny;}
        }
        throw new java.lang.ClassCastException("x10.lang.Short");
    }
    
    public static int asint(Object typeParamOrAny, Type<?> origRTT){
        if (typeParamOrAny == null) {nullIsCastToStruct("x10.lang.Int");}
        if (isNumericType(origRTT)) {
        	return ((java.lang.Number) typeParamOrAny).intValue();
        } else {
            if (typeParamOrAny instanceof x10.core.Int) return x10.core.Int.$unbox((x10.core.Int) typeParamOrAny);
            else if (typeParamOrAny instanceof java.lang.Integer) {return (java.lang.Integer) typeParamOrAny;}
        }
        throw new java.lang.ClassCastException("x10.lang.Int");
    }

    public static long aslong(Object typeParamOrAny, Type<?> origRTT){
        if (typeParamOrAny == null) {nullIsCastToStruct("x10.lang.Long");}
        if (isNumericType(origRTT)) {
        	return ((java.lang.Number) typeParamOrAny).longValue();
        } else {
        	if (typeParamOrAny instanceof x10.core.Long) {return x10.core.Long.$unbox((x10.core.Long)typeParamOrAny);}
        	else if (typeParamOrAny instanceof java.lang.Long) {return (java.lang.Long) typeParamOrAny;}
        }
        throw new java.lang.ClassCastException("x10.lang.Long");
    }

    public static float asfloat(Object typeParamOrAny, Type<?> origRTT){
        if (typeParamOrAny == null) {nullIsCastToStruct("x10.lang.Float");}
        if (isNumericType(origRTT)) {
        	return ((java.lang.Number) typeParamOrAny).floatValue();
        } else {
        	if (typeParamOrAny instanceof x10.core.Float) {return x10.core.Float.$unbox((x10.core.Float)typeParamOrAny);}
        	else if (typeParamOrAny instanceof java.lang.Float) {return (java.lang.Float) typeParamOrAny;}
        }
        throw new java.lang.ClassCastException("x10.lang.Float");
    }

    public static double asdouble(Object typeParamOrAny, Type<?> origRTT){
        if (typeParamOrAny == null) {nullIsCastToStruct("x10.lang.Double");}
        if (isNumericType(origRTT)) {
        	return ((java.lang.Number) typeParamOrAny).doubleValue();
        } else {
        	if (typeParamOrAny instanceof x10.core.Double) {return x10.core.Double.$unbox((x10.core.Double)typeParamOrAny);}
        	else if (typeParamOrAny instanceof java.lang.Double) {return (java.lang.Double) typeParamOrAny;}
        }
        throw new java.lang.ClassCastException("x10.lang.Double");
    }

    public static byte asUByte(Object typeParamOrAny, Type<?> origRTT){
        if (typeParamOrAny == null) {nullIsCastToStruct("x10.lang.UByte");}
        if (isNumericType(origRTT)) {
        	return ((java.lang.Number) typeParamOrAny).byteValue();
        } else {
            if (typeParamOrAny instanceof x10.core.UByte) {return x10.core.UByte.$unbox((x10.core.UByte)typeParamOrAny);}
            else if (typeParamOrAny instanceof java.lang.Byte) {return (java.lang.Byte)typeParamOrAny;}
        }
        throw new java.lang.ClassCastException("x10.lang.UByte");
    }
    // not used
//    public static Object asBoxedUByte(Object typeParamOrAny, Type<?> origRTT){
//    	return x10.core.UByte.$box(asUByte(typeParamOrAny, origRTT));
//    }

    public static short asUShort(Object typeParamOrAny, Type<?> origRTT){
        if (typeParamOrAny == null) {nullIsCastToStruct("x10.lang.UShort");}
        if (isNumericType(origRTT)) {
        	return ((java.lang.Number) typeParamOrAny).shortValue();
        } else {
            if (typeParamOrAny instanceof x10.core.UShort) {return x10.core.UShort.$unbox((x10.core.UShort)typeParamOrAny);}
            else if (typeParamOrAny instanceof java.lang.Short) {return (java.lang.Short)typeParamOrAny;}
        }
        throw new java.lang.ClassCastException("x10.lang.UShort");
    }
    // not used
//    public static Object asBoxedUShort(Object typeParamOrAny, Type<?> origRTT){
//    	return x10.core.UShort.$box(asUShort(typeParamOrAny, origRTT));
//    }

    public static int asUInt(Object typeParamOrAny, Type<?> origRTT){
        if (typeParamOrAny == null) {nullIsCastToStruct("x10.lang.UInt");}
        if (isNumericType(origRTT)) {
        	return ((java.lang.Number) typeParamOrAny).intValue();
        } else {
        	if (typeParamOrAny instanceof x10.core.UInt) {return x10.core.UInt.$unbox((x10.core.UInt)typeParamOrAny);}
        	else if (typeParamOrAny instanceof java.lang.Integer) {return (java.lang.Integer)typeParamOrAny;}
        }
        throw new java.lang.ClassCastException("x10.lang.UInt");
    }
    // not used
//    public static Object asBoxedUInt(Object typeParamOrAny, Type<?> origRTT){
//    	return x10.core.UInt.$box(asUInt(typeParamOrAny, origRTT));
//    }

    public static long asULong(Object typeParamOrAny, Type<?> origRTT){
        if (typeParamOrAny == null) {nullIsCastToStruct("x10.lang.ULong");}
        if (isNumericType(origRTT)) {
        	return ((java.lang.Number) typeParamOrAny).longValue();
        } else {
            if (typeParamOrAny instanceof x10.core.ULong) {return x10.core.ULong.$unbox((x10.core.ULong)typeParamOrAny);}
            else if (typeParamOrAny instanceof java.lang.Long) {return (java.lang.Long)typeParamOrAny;}
        }
        throw new java.lang.ClassCastException("x10.lang.ULong");
    }
    // not used
//    public static Object asBoxedULong(Object typeParamOrAny, Type<?> origRTT){
//    	return x10.core.ULong.$box(asULong(typeParamOrAny, origRTT));
//    }

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
        if (rtt == UBYTE) {return x10.core.UByte.$box(asUByte(primOrTypeParam, convert ? UBYTE : null));}
        if (rtt == USHORT) {return x10.core.UShort.$box(asUShort(primOrTypeParam, convert ? USHORT : null));}
        if (rtt == UINT) {return x10.core.UInt.$box(asUInt(primOrTypeParam, convert ? UINT : null));}
        if (rtt == ULONG) {return x10.core.ULong.$box(asULong(primOrTypeParam, convert ? ULONG : null));}

        return primOrTypeParam;
    }

    public static Object conversion(Type<?> rtt, Object primOrTypeParam) {
    	return conversion(rtt, primOrTypeParam, false);
    }

    private static void nullIsCastToStruct(Type<?> rtt) {throw new java.lang.ClassCastException(rtt.typeName());}
    private static void nullIsCastToStruct(String msg){throw new java.lang.ClassCastException(msg);}

    private static boolean isNullable(Type<?> rtt) {
        return rtt.isref();
    }

    public static boolean hasNaturalZero(Type<?> rtt) {
    	return isNullable(rtt) || isPrimitiveType(rtt);
    }

    public static <T> T cast(final Object self, Type<?> rtt) {
        // XTENLANG-3093
        if (self == null) {
            if (rtt == null || isNullable(rtt)) return null;
            throw new java.lang.ClassCastException(rtt.typeName());
        }
        if (rtt != null && !rtt.isInstance(self)) throw new java.lang.ClassCastException(rtt.typeName());
        return (T) self;
    }
    
    public static <T> T castConversion(final Object self, Type<?> rtt) {
        // XTENLANG-3093
        if (self == null) {
            if (rtt == null || isNullable(rtt)) return null;
            throw new java.lang.ClassCastException(rtt.typeName());
        }
        T ret = (T) conversion(rtt, self, true);
        if (rtt != null && !rtt.isInstance(ret)) throw new java.lang.ClassCastException(rtt.typeName());
        return ret;
    }

    public static Object zeroValue(Type<?> rtt) {
        Type<?>[] actualTypeArguments = null;
        if (rtt instanceof ParameterizedType) {
            ParameterizedType<?> pt = (ParameterizedType<?>) rtt;
            rtt = pt.getRawType(); 
            actualTypeArguments = pt.getActualTypeArguments();
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
            // N.B. since GlobalRef has their own zero value constructor, special paths are no longer needed
//            if (rtt == x10.core.GlobalRef.$RTT) return new x10.core.GlobalRef(actualTypeArguments[0], (java.lang.System) null);
            // for user-defined structs, call zero value constructor
            try {
                Class<?> impl = rtt.getJavaClass();
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
                
                assert actualTypeArguments == null ? paramTypes.length == 1/*(java.lang.System)null*/ : paramTypes.length == actualTypeArguments.length/*T1,T2,...*/ + 1/*(java.lang.System)null*/;
                int i = 0;
                if (actualTypeArguments != null) {
                    for ( ; i < actualTypeArguments.length; ++i) {
                        // pass type params
                        params[i] = actualTypeArguments[i];
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
