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

package x10.core;

import java.io.IOException;

import x10.rtt.RuntimeType;
import x10.rtt.Type;
import x10.rtt.Types;
import x10.runtime.impl.java.UIntUtils;
import x10.serialization.X10JavaDeserializer;
import x10.serialization.X10JavaSerializable;
import x10.serialization.X10JavaSerializer;

/**
 * Represents a boxed UByte value. Boxed representation is used when casting
 * a UByte value into type Any or parameter type T.
 */
final public class UByte extends java.lang.Number implements StructI, java.lang.Comparable<UByte>,
// for X10PrettyPrinterVisitor.exposeSpecialDispatcherThroughSpecialInterface
//    x10.lang.Arithmetic<UByte>, x10.lang.Bitwise<UByte>, x10.util.Ordered<UByte>
    x10.core.Arithmetic.x10$lang$UByte, x10.core.Bitwise.x10$lang$UByte, x10.util.Ordered<UByte>
{
    public static final RuntimeType<?> $RTT = Types.UBYTE;
    public RuntimeType<?> $getRTT() {return $RTT;}
    public Type<?> $getParam(int i) {return null;}

    final byte $value;

    private UByte(byte value) {
        this.$value = value;
    }

    private abstract static class Cache {
        static final boolean enabled = java.lang.Boolean.parseBoolean(System.getProperty("x10.lang.UByte.Cache.enabled", "false"));
        static final int low = -128;
        static final int high = enabled ? 127 : (low - 1); // disable caching
        static final UByte cache[] = new UByte[high - low + 1];
        static {
            for (int i = 0; i < cache.length; ++i) {
                cache[i] = new UByte((byte)(low + i));
            }
        }
    }

    public static UByte $box(byte value) {
        if (Cache.enabled) {
            int valueAsInt = value;
            return Cache.cache[valueAsInt - Cache.low];  // fully cached
        }
        return new UByte(value);
    }
    
    public static UByte $box(int value) {   // int is required for literals
        return $box((byte) value);
    }

    public static byte $unbox(UByte o) {
        return o.$value;
    }
    
    public static byte $unbox(Object obj) {
    	if (obj instanceof UByte) return ((UByte)obj).$value;
    	else return ((java.lang.Byte)obj).byteValue();
    }
    
    // make $box/$unbox idempotent
    public static UByte $box(UByte obj) {
        return obj;
    }

    public static UByte $box(Object obj) {
        if (obj instanceof UByte) return (UByte) obj;
        else return $box(((java.lang.Byte)obj).byteValue());
    }

    public static byte $unbox(byte value) {
        return value;
    }
    
    public boolean _struct_equals$O(Object o) {
        if (o instanceof UByte && ((UByte)o).$value == $value)
            return true;
        return false;
    }
    
    @Override
    public boolean equals(Object o) {
        return _struct_equals$O(o);
    }
    
    @Override
    public int hashCode() {
        return (int)$value;
    }
    
    @Override
    public String toString() {
        if ($value >= 0)
            return java.lang.Byte.toString($value);
        else
            return java.lang.Integer.toString((int)$value & 0xff);
    }
    
 	// implements Comparable<UByte>
    public int compareTo(UByte o) {
        int a = ((int)$value) & 0xFF;
        int b = ((int)o.$value) & 0xFF;
        if (a > b) return 1;
        else if (a < b) return -1;
        return 0;
    }

    private Object writeReplace() throws java.io.ObjectStreamException {
        return new x10.serialization.SerializationProxy(this);
    }

    public void $_serialize(X10JavaSerializer $serializer) throws IOException {
        $serializer.write($value);
    }

    public static X10JavaSerializable $_deserializer(X10JavaDeserializer $deserializer) throws IOException {
        return $_deserialize_body(null, $deserializer);
    }

    public static X10JavaSerializable $_deserialize_body(UByte $_obj, X10JavaDeserializer $deserializer) throws IOException {
        byte value = $deserializer.readByte();
        $_obj = new UByte(value);
        return $_obj;
    }
    
    // implements Arithmetic<UByte>
    public UByte $plus$G() { return this; }
    public UByte $minus$G() { return UByte.$box(-$value); }
    public UByte $plus(Object a, Type t) { return UByte.$box($value + ((UByte)a).$value); }
    public UByte $minus(Object a, Type t) { return UByte.$box($value - ((UByte)a).$value); }
    public UByte $times(Object a, Type t) { return UByte.$box($value * ((UByte)a).$value); }
    public UByte $over(Object a, Type t) { return UByte.$box((byte)((0xff & $value) / (0xff & ((UByte)a).$value))); }
    // for X10PrettyPrinterVisitor.exposeSpecialDispatcherThroughSpecialInterface
    public byte $plus$b(Object a, Type t) { return (byte) ($value + ((UByte)a).$value); }
    public byte $minus$b(Object a, Type t) { return (byte) ($value - ((UByte)a).$value); }
    public byte $times$b(Object a, Type t) { return (byte) ($value * ((UByte)a).$value); }
    public byte $over$b(Object a, Type t) { return (byte) ((0xff & $value) / (0xff & ((UByte)a).$value)); }

    // implements Bitwise<UByte>
    public UByte $tilde$G() { return UByte.$box(~$value); }
    public UByte $ampersand(Object a, Type t) { return UByte.$box($value & ((UByte)a).$value); }
    public UByte $bar(Object a, Type t) { return UByte.$box($value | ((UByte)a).$value); }
    public UByte $caret(Object a, Type t) { return UByte.$box($value ^ ((UByte)a).$value); }
    public UByte $left$G(long count) { return UByte.$box($value << count); }
    public UByte $right$G(long count) { return UByte.$box((0xff & $value) >>> count); } // UByte is always unsigned
    public UByte $unsigned_right$G(long count) { return UByte.$box((0xff & $value) >>> count); }
    // for X10PrettyPrinterVisitor.exposeSpecialDispatcherThroughSpecialInterface
    public byte $ampersand$b(Object a, Type t) { return (byte) ($value & ((UByte)a).$value); }
    public byte $bar$b(Object a, Type t) { return (byte) ($value | ((UByte)a).$value); }
    public byte $caret$b(Object a, Type t) { return (byte) ($value ^ ((UByte)a).$value); }
    
    // implements Ordered<UByte>
    public Object $lt(Object a, Type t) { return Boolean.$box(UIntUtils.lt($value,((UByte)a).$value)); }
    public Object $gt(Object a, Type t) { return Boolean.$box(UIntUtils.gt($value,((UByte)a).$value)); }
    public Object $le(Object a, Type t) { return Boolean.$box(UIntUtils.le($value,((UByte)a).$value)); }
    public Object $ge(Object a, Type t) { return Boolean.$box(UIntUtils.ge($value,((UByte)a).$value)); }
    // for X10PrettyPrinterVisitor.generateSpecialDispatcher
    public boolean $lt$Z(Object a, Type t) { return UIntUtils.lt($value,((UByte)a).$value); }
    public boolean $gt$Z(Object a, Type t) { return UIntUtils.gt($value,((UByte)a).$value); }
    public boolean $le$Z(Object a, Type t) { return UIntUtils.le($value,((UByte)a).$value); }
    public boolean $ge$Z(Object a, Type t) { return UIntUtils.ge($value,((UByte)a).$value); }

    // extends abstract class java.lang.Number
    @Override
    public int intValue() {
        return (int)(((int)$value)&0xff);
    }
    @Override
    public long longValue() {
        return (long)(((int)$value)&0xff);
    }
    @Override
    public float floatValue() {
        return (float)(((int)$value)&0xff);
    }
    @Override
    public double doubleValue() {
        return (double)(((int)$value)&0xff);
    }
}
