/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.types;


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

    public static Type runtimeType(Class c) {
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
}
