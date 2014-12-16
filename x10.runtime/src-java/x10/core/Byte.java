/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.core;

import java.io.IOException;

import x10.rtt.RuntimeType;
import x10.rtt.Type;
import x10.rtt.Types;
import x10.serialization.X10JavaDeserializer;
import x10.serialization.X10JavaSerializable;
import x10.serialization.X10JavaSerializer;

/**
 * Represents a boxed Byte value. Boxed representation is used when casting
 * an Byte value to type Any, parameter type T or superinterfaces such
 * as Comparable<Byte>.
 */
final public class Byte extends java.lang.Number implements StructI, java.lang.Comparable<Byte>,
// for X10PrettyPrinterVisitor.exposeSpecialDispatcherThroughSpecialInterface
//    x10.lang.Arithmetic<Byte>, x10.lang.Bitwise<Byte>, x10.util.Ordered<Byte>
    x10.core.Arithmetic.x10$lang$Byte, x10.core.Bitwise.x10$lang$Byte, x10.util.Ordered<Byte>
{
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
    public String toString() {
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
    public Byte $plus(Object b, Type t) { return Byte.$box($value + ((Byte)b).$value); }
    public Byte $minus(Object b, Type t) { return Byte.$box($value - ((Byte)b).$value); }
    public Byte $times(Object b, Type t) { return Byte.$box($value * ((Byte)b).$value); }
    public Byte $over(Object b, Type t) { return Byte.$box($value / ((Byte)b).$value); }
    // for X10PrettyPrinterVisitor.exposeSpecialDispatcherThroughSpecialInterface
    public byte $plus$B(Object b, Type t) { return (byte) ($value + ((Byte)b).$value); }
    public byte $minus$B(Object b, Type t) { return (byte) ($value - ((Byte)b).$value); }
    public byte $times$B(Object b, Type t) { return (byte) ($value * ((Byte)b).$value); }
    public byte $over$B(Object b, Type t) { return (byte) ($value / ((Byte)b).$value); }
    
    // implements Bitwise<Byte>
    public Byte $tilde$G() { return Byte.$box(~$value); }
    public Byte $ampersand(Object b, Type t) { return Byte.$box($value & ((Byte)b).$value); }
    public Byte $bar(Object b, Type t) { return Byte.$box($value | ((Byte)b).$value); }
    public Byte $caret(Object b, Type t) { return Byte.$box($value ^ ((Byte)b).$value); }
    public Byte $left$G(long count) { return Byte.$box($value << count); }
    public Byte $right$G(long count) { return Byte.$box($value >> count); }
    public Byte $unsigned_right$G(long count) { return Byte.$box($value >>> count); }
    // for X10PrettyPrinterVisitor.exposeSpecialDispatcherThroughSpecialInterface
    public byte $ampersand$B(Object b, Type t) { return (byte) ($value & ((Byte)b).$value); }
    public byte $bar$B(Object b, Type t) { return (byte) ($value | ((Byte)b).$value); }
    public byte $caret$B(Object b, Type t) { return (byte) ($value ^ ((Byte)b).$value); }
    
    // implements Ordered<Byte>
    public Object $lt(Object b, Type t) { return Boolean.$box($value < ((Byte)b).$value); }
    public Object $gt(Object b, Type t) { return Boolean.$box($value > ((Byte)b).$value); }
    public Object $le(Object b, Type t) { return Boolean.$box($value <= ((Byte)b).$value); }
    public Object $ge(Object b, Type t) { return Boolean.$box($value >= ((Byte)b).$value); }
    // for X10PrettyPrinterVisitor.generateSpecialDispatcher
    public boolean $lt$Z(Object b, Type t) { return $value < ((Byte)b).$value; }
    public boolean $gt$Z(Object b, Type t) { return $value > ((Byte)b).$value; }
    public boolean $le$Z(Object b, Type t) { return $value <= ((Byte)b).$value; }
    public boolean $ge$Z(Object b, Type t) { return $value >= ((Byte)b).$value; }
    
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

    private Object writeReplace() throws java.io.ObjectStreamException {
        return new x10.serialization.SerializationProxy(this);
    }

    public void $_serialize(X10JavaSerializer $serializer) throws IOException {
        $serializer.write($value);
    }

    public static X10JavaSerializable $_deserializer(X10JavaDeserializer $deserializer) throws IOException {
        return $_deserialize_body(null, $deserializer);
    }

    public static X10JavaSerializable $_deserialize_body(Byte $_obj, X10JavaDeserializer $deserializer) throws IOException {
        byte value  = $deserializer.readByte();
        $_obj = new Byte(value);
        return $_obj;
    }
}
