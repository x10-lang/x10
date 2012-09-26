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

package x10.core;

import x10.rtt.RuntimeType;
import x10.rtt.Type;
import x10.rtt.Types;
import x10.x10rt.DeserializationDispatcher;
import x10.x10rt.X10JavaDeserializer;
import x10.x10rt.X10JavaSerializable;
import x10.x10rt.X10JavaSerializer;

import java.io.IOException;

/**
 * Represents a boxed Byte value. Boxed representation is used when casting
 * an Byte value to type Any, parameter type T or superinterfaces such
 * as Comparable<Byte>.
 */
final public class Byte extends java.lang.Number implements StructI, java.lang.Comparable<Byte>,
// for X10PrettyPrinterVisitor.useSpecialDispatcherThroughKnownInterfaces
//    x10.lang.Arithmetic<Byte>, x10.lang.Bitwise<Byte>, x10.util.Ordered<Byte>
    x10.core.Arithmetic.x10$lang$Byte, x10.core.Bitwise.x10$lang$Byte, x10.util.Ordered<Byte>
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = DeserializationDispatcher.addDispatcher(DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Byte.class);
    
    public static final RuntimeType<?> $RTT = Types.BYTE;
    public RuntimeType<?> $getRTT() {return $RTT;}
    public Type<?> $getParam(int i) {return null;}

    final byte $value;

    private Byte(byte value) {
        this.$value = value;
    }
    
    private abstract static class Cache {
        static final boolean enabled = java.lang.Boolean.parseBoolean(System.getProperty("x10.lang.Byte.Cache.enabled", "false"));
        static final int low = -128;
        static final int high = enabled ? 127 : (low - 1); // disable caching
        static final Byte cache[] = new Byte[high - low + 1];
        static {
            for (int i = 0; i < cache.length; ++i) {
                cache[i] = new Byte((byte)(low + i));
            }
        }
    }

    public static Byte $box(byte value) {
        if (Cache.enabled) {
            int valueAsInt = value;
            return Cache.cache[valueAsInt - Cache.low];  // fully cached
        }
        return new Byte(value);
    }

    public static Byte $box(int value) { // int because literals essentially have int type in Java
        return $box((byte)value);
    }

    public static byte $unbox(Byte obj) {
        return obj.$value;
    }
    
    public static byte $unbox(Object obj) {
        if (obj instanceof Byte) return ((Byte)obj).$value;
        else return ((java.lang.Byte)obj).byteValue();
    }
    
    // make $box/$unbox idempotent
    public static Byte $box(Byte obj) {
        return obj;
    }

    public static Byte $box(Object obj) {
        if (obj instanceof Byte) return (Byte) obj;
        else return $box(((java.lang.Byte)obj).byteValue());
    }

    public static byte $unbox(byte value) {
        return value;
    }
    
    public static byte $unbox(int value) {
        return (byte)value;
    }
    
    public boolean _struct_equals$O(Object obj) {
        if (obj instanceof Byte && ((Byte) obj).$value == $value)
            return true;
        return false;
    }
    
    @Override
    public boolean equals(Object value) {
        if (value instanceof Byte) {
            return ((Byte) value).$value == $value;
        } else if (value instanceof java.lang.Byte) { // integer literals come here as Byte autoboxed values
            return ((java.lang.Byte) value).byteValue() == $value;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return $value;
    }

    @Override
    public java.lang.String toString() {
        return java.lang.Byte.toString($value);
    }
    
    // implements Comparable<Byte>
    public int compareTo(Byte o) {
        if ($value > o.$value) return 1;
        else if ($value < o.$value) return -1;
        return 0;
    }
    
    // implements Arithmetic<Byte>
    public Byte $plus$G() { return this; }
    public Byte $minus$G() { return Byte.$box(-$value); }
    public Byte $plus(java.lang.Object b, Type t) { return Byte.$box($value + ((Byte)b).$value); }
    public Byte $minus(java.lang.Object b, Type t) { return Byte.$box($value - ((Byte)b).$value); }
    public Byte $times(java.lang.Object b, Type t) { return Byte.$box($value * ((Byte)b).$value); }
    public Byte $over(java.lang.Object b, Type t) { return Byte.$box($value / ((Byte)b).$value); }
    // for X10PrettyPrinterVisitor.useSpecialDispatcherThroughKnownInterfaces
    public byte $plus$B(java.lang.Object b, Type t) { return (byte) ($value + ((Byte)b).$value); }
    public byte $minus$B(java.lang.Object b, Type t) { return (byte) ($value - ((Byte)b).$value); }
    public byte $times$B(java.lang.Object b, Type t) { return (byte) ($value * ((Byte)b).$value); }
    public byte $over$B(java.lang.Object b, Type t) { return (byte) ($value / ((Byte)b).$value); }
    
    // implements Bitwise<Byte>
    public Byte $tilde$G() { return Byte.$box(~$value); }
    public Byte $ampersand(java.lang.Object b, Type t) { return Byte.$box($value & ((Byte)b).$value); }
    public Byte $bar(java.lang.Object b, Type t) { return Byte.$box($value | ((Byte)b).$value); }
    public Byte $caret(java.lang.Object b, Type t) { return Byte.$box($value ^ ((Byte)b).$value); }
    public Byte $left$G(int count) { return Byte.$box($value << count); }
    public Byte $right$G(int count) { return Byte.$box($value >> count); }
    public Byte $unsigned_right$G(int count) { return Byte.$box($value >>> count); }
    // for X10PrettyPrinterVisitor.useSpecialDispatcherThroughKnownInterfaces
    public byte $ampersand$B(java.lang.Object b, Type t) { return (byte) ($value & ((Byte)b).$value); }
    public byte $bar$B(java.lang.Object b, Type t) { return (byte) ($value | ((Byte)b).$value); }
    public byte $caret$B(java.lang.Object b, Type t) { return (byte) ($value ^ ((Byte)b).$value); }
    
    // implements Ordered<Byte>
    public java.lang.Object $lt(java.lang.Object b, Type t) { return x10.core.Boolean.$box($value < ((Byte)b).$value); }
    public java.lang.Object $gt(java.lang.Object b, Type t) { return x10.core.Boolean.$box($value > ((Byte)b).$value); }
    public java.lang.Object $le(java.lang.Object b, Type t) { return x10.core.Boolean.$box($value <= ((Byte)b).$value); }
    public java.lang.Object $ge(java.lang.Object b, Type t) { return x10.core.Boolean.$box($value >= ((Byte)b).$value); }
    // for X10PrettyPrinterVisitor.generateSpecialDispatcher
    public boolean $lt$Z(java.lang.Object b, Type t) { return $value < ((Byte)b).$value; }
    public boolean $gt$Z(java.lang.Object b, Type t) { return $value > ((Byte)b).$value; }
    public boolean $le$Z(java.lang.Object b, Type t) { return $value <= ((Byte)b).$value; }
    public boolean $ge$Z(java.lang.Object b, Type t) { return $value >= ((Byte)b).$value; }
    
    // extends abstract class java.lang.Number
    @Override
    public int intValue() {
        return (int)$value;
    }
    @Override
    public long longValue() {
        return (long)$value;
    }
    @Override
    public float floatValue() {
        return (float)$value;
    }
    @Override
    public double doubleValue() {
        return (double)$value;
    }

    public void $_serialize(X10JavaSerializer serializer) throws IOException {
        serializer.write($value);
    }

    public short $_get_serialization_id() {
        return $_serialization_id;
    }

    public static X10JavaSerializable $_deserializer(X10JavaDeserializer deserializer) throws IOException {
        return $_deserialize_body(null, deserializer);
    }

    public static X10JavaSerializable $_deserialize_body(Byte b, X10JavaDeserializer deserializer) throws IOException {
        byte value  = deserializer.readByte();
        b = new Byte(value);
        deserializer.record_reference(b);
        return b;
    }
    
    // imitate java.lang.Byte
    public static final byte MIN_VALUE = java.lang.Byte.MIN_VALUE;
    public static final byte MAX_VALUE = java.lang.Byte.MAX_VALUE;
}
