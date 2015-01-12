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
import x10.serialization.X10JavaDeserializer;
import x10.serialization.X10JavaSerializable;
import x10.serialization.X10JavaSerializer;

/**
 * Represents a boxed Short value. Boxed representation is used when casting
 * an Short value to type Any, parameter type T or superinterfaces such
 * as Comparable<Short>.
 */
final public class Short extends java.lang.Number implements StructI, java.lang.Comparable<Short>,
// for X10PrettyPrinterVisitor.exposeSpecialDispatcherThroughSpecialInterface
//    x10.lang.Arithmetic<Short>, x10.lang.Bitwise<Short>, x10.util.Ordered<Short>
    x10.core.Arithmetic.x10$lang$Short, x10.core.Bitwise.x10$lang$Short, x10.util.Ordered<Short>
{
    public static final RuntimeType<?> $RTT = Types.SHORT;
    public RuntimeType<?> $getRTT() {return $RTT;}
    public Type<?> $getParam(int i) {return null;}

    final short $value;

    private Short(short value) {
        this.$value = value;
    }
    
    private abstract static class Cache {
        static final boolean enabled = java.lang.Boolean.parseBoolean(System.getProperty("x10.lang.Short.Cache.enabled", "false"));
        static final int low = -128;
        static final int high = enabled ? 127 : (low - 1); // disable caching
        static final Short cache[] = new Short[high - low + 1];
        static {
            for (int i = 0; i < cache.length; ++i) {
                cache[i] = new Short((short)(low + i));
            }
        }
    }

    public static Short $box(short value) {
        if (Cache.enabled) {
            int valueAsInt = value;
            if (Cache.low <= valueAsInt && valueAsInt <= Cache.high) {
                return Cache.cache[valueAsInt - Cache.low];
            }
        }
        return new Short(value);
    }

    public static Short $box(int value) { // int because literals essentially have int type in Java
        return $box((short)value);
    }

    public static short $unbox(Short obj) {
        return obj.$value;
    }
    
    public static short $unbox(Object obj) {
        if (obj instanceof Short) return ((Short)obj).$value;
        else return ((java.lang.Short)obj).shortValue();
    }
    
    // make $box/$unbox idempotent
    public static Short $box(Short obj) {
        return obj;
    }

    public static Short $box(Object obj) {
        if (obj instanceof Short) return (Short) obj;
        else return $box(((java.lang.Short)obj).shortValue());
    }

    public static short $unbox(short value) {
        return value;
    }
    
    public static short $unbox(int value) {
        return (short)value;
    }
    
    public boolean _struct_equals$O(Object obj) {
        if (obj instanceof Short && ((Short) obj).$value == $value)
            return true;
        return false;
    }
    
    @Override
    public boolean equals(Object value) {
        if (value instanceof Short) {
            return ((Short) value).$value == $value;
        } else if (value instanceof java.lang.Short) { // integer literals come here as Short autoboxed values
            return ((java.lang.Short) value).shortValue() == $value;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return $value;
    }

    @Override
    public String toString() {
        return java.lang.Short.toString($value);
    }
    
    // implements Comparable<Short>
    public int compareTo(Short o) {
        if ($value > o.$value) return 1;
        else if ($value < o.$value) return -1;
        return 0;
    }
    
    // implements Arithmetic<Short>
    public Short $plus$G() { return this; }
    public Short $minus$G() { return Short.$box(-$value); }
    public Short $plus(Object b, Type t) { return Short.$box($value + ((Short)b).$value); }
    public Short $minus(Object b, Type t) { return Short.$box($value - ((Short)b).$value); }
    public Short $times(Object b, Type t) { return Short.$box($value * ((Short)b).$value); }
    public Short $over(Object b, Type t) { return Short.$box($value / ((Short)b).$value); }
    // for X10PrettyPrinterVisitor.exposeSpecialDispatcherThroughSpecialInterface
    public short $plus$S(Object b, Type t) { return (short) ($value + ((Short)b).$value); }
    public short $minus$S(Object b, Type t) { return (short) ($value - ((Short)b).$value); }
    public short $times$S(Object b, Type t) { return (short) ($value * ((Short)b).$value); }
    public short $over$S(Object b, Type t) { return (short) ($value / ((Short)b).$value); }
    
    // implements Bitwise<Short>
    public Short $tilde$G() { return Short.$box(~$value); }
    public Short $ampersand(Object b, Type t) { return Short.$box($value & ((Short)b).$value); }
    public Short $bar(Object b, Type t) { return Short.$box($value | ((Short)b).$value); }
    public Short $caret(Object b, Type t) { return Short.$box($value ^ ((Short)b).$value); }
    public Short $left$G(long count) { return Short.$box($value << count); }
    public Short $right$G(long count) { return Short.$box($value >> count); }
    public Short $unsigned_right$G(long count) { return Short.$box($value >>> count); }
    // for X10PrettyPrinterVisitor.exposeSpecialDispatcherThroughSpecialInterface
    public short $ampersand$S(Object b, Type t) { return (short) ($value & ((Short)b).$value); }
    public short $bar$S(Object b, Type t) { return (short) ($value | ((Short)b).$value); }
    public short $caret$S(Object b, Type t) { return (short) ($value ^ ((Short)b).$value); }
    
    // implements Ordered<Short>
    public Object $lt(Object b, Type t) { return Boolean.$box($value < ((Short)b).$value); }
    public Object $gt(Object b, Type t) { return Boolean.$box($value > ((Short)b).$value); }
    public Object $le(Object b, Type t) { return Boolean.$box($value <= ((Short)b).$value); }
    public Object $ge(Object b, Type t) { return Boolean.$box($value >= ((Short)b).$value); }
    // for X10PrettyPrinterVisitor.generateSpecialDispatcher
    public boolean $lt$Z(Object b, Type t) { return $value < ((Short)b).$value; }
    public boolean $gt$Z(Object b, Type t) { return $value > ((Short)b).$value; }
    public boolean $le$Z(Object b, Type t) { return $value <= ((Short)b).$value; }
    public boolean $ge$Z(Object b, Type t) { return $value >= ((Short)b).$value; }

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

    public static X10JavaSerializable $_deserialize_body(Short $_obj, X10JavaDeserializer $deserializer) throws IOException {
        short value  = $deserializer.readShort();
        $_obj = new Short(value);
        return $_obj;
    }
}
