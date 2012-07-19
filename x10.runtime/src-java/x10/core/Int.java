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
import x10.x10rt.X10JavaDeserializer;
import x10.x10rt.X10JavaSerializable;
import x10.x10rt.X10JavaSerializer;

import java.io.IOException;

/**
 * Represents a boxed Int value. Boxed representation is used when casting
 * an Int value to type Any, parameter type T or superinterfaces such
 * as Comparable<Int>.
 */
final public class Int extends Number implements StructI, java.lang.Comparable<Int>,
    x10.lang.Arithmetic<Int>, x10.lang.Bitwise<Int>, x10.util.Ordered<Int>
{
    private static final long serialVersionUID = 1L;
    private static final short _serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Int.class);
    
    public static final RuntimeType<?> $RTT = Types.INT;
    public RuntimeType<?> $getRTT() {return $RTT;}
    public Type<?> $getParam(int i) {return null;}

    final int $value;

    private Int(int value) {
        this.$value = value;
    }
    
    private abstract static class Cache {
        // N.B. enabled by default to avoid boxing of return value from Comparator.compare
        static final boolean enabled = java.lang.Boolean.parseBoolean(System.getProperty("x10.lang.Int.Cache.enabled", "true"));
        static final int low = -128;
        static final int high;
        static final Int cache[];
        static {
            // high value may be configured by property
            int h = 127;
            java.lang.String highPropValue = System.getProperty("x10.lang.Int.Cache.high");
            if (highPropValue != null) {
            	int i = java.lang.Integer.parseInt(highPropValue);
                i = Math.max(i, h);
                // Maximum array size is Integer.MAX_VALUE
                h = Math.min(i, Integer.MAX_VALUE + low);
            }
            high = enabled ? h : (low - 1); // disable caching

            cache = new Int[high - low + 1];
            for (int i = 0; i < cache.length; ++i) {
                cache[i] = new Int(low + i);
            }
        }
    }

    public static Int $box(int value) {
        if (Cache.enabled) { 
            if (Cache.low <= value && value <= Cache.high) {
                return Cache.cache[value - Cache.low];
            }
        }
        return new Int(value);
    }

    public static int $unbox(Int obj) {
        return obj.$value;
    }
    
    public static int $unbox(Object obj) {
        if (obj instanceof Int) return ((Int)obj).$value;
        else return ((java.lang.Integer)obj).intValue();
    }
    
    // make $box/$unbox idempotent
    public static Int $box(Int obj) {
        return obj;
    }

    public static Int $box(Object obj) {
        if (obj instanceof Int) return (Int) obj;
        else return $box(((java.lang.Integer)obj).intValue());
    }

    public static int $unbox(int value) {
        return value;
    }
    
    public boolean _struct_equals$O(Object obj) {
        if (obj instanceof Int && ((Int) obj).$value == $value)
            return true;
        return false;
    }
    
    @Override
    public boolean equals(Object value) {
        if (value instanceof Int) {
            return ((Int) value).$value == $value;
        } else if (value instanceof java.lang.Integer) { // integer literals come here as Integer autoboxed values
            return ((java.lang.Integer) value).intValue() == $value;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return $value;
    }

    @Override
    public java.lang.String toString() {
        return java.lang.Integer.toString($value);
    }
    
    // implements Comparable<Int>
    public int compareTo(Int o) {
        if ($value > o.$value) return 1;
        else if ($value < o.$value) return -1;
        return 0;
    }
    
    // implements Arithmetic<Int>
    public Int $plus$G() { return this; }
    public Int $minus$G() { return Int.$box(-$value); }
    public Int $plus(java.lang.Object b, Type t) { return Int.$box($value + ((Int)b).$value); }
    public Int $minus(java.lang.Object b, Type t) { return Int.$box($value - ((Int)b).$value); }
    public Int $times(java.lang.Object b, Type t) { return Int.$box($value * ((Int)b).$value); }
    public Int $over(java.lang.Object b, Type t) { return Int.$box($value / ((Int)b).$value); }
    
    // implements Bitwise<Int>
    public Int $tilde$G() { return Int.$box(~$value); }
    public Int $ampersand(java.lang.Object b, Type t) { return Int.$box($value & ((Int)b).$value); }
    public Int $bar(java.lang.Object b, Type t) { return Int.$box($value | ((Int)b).$value); }
    public Int $caret(java.lang.Object b, Type t) { return Int.$box($value ^ ((Int)b).$value); }
    public Int $left$G(final int count) { return Int.$box($value << count); }
    public Int $right$G(final int count) { return Int.$box($value >> count); }
    public Int $unsigned_right$G(final int count) { return Int.$box($value >>> count); }
    
    // implements Ordered<Int>
    public java.lang.Object $lt(java.lang.Object b, Type t) { return x10.core.Boolean.$box($value < ((Int)b).$value); }
    public java.lang.Object $gt(java.lang.Object b, Type t) { return x10.core.Boolean.$box($value > ((Int)b).$value); }
    public java.lang.Object $le(java.lang.Object b, Type t) { return x10.core.Boolean.$box($value <= ((Int)b).$value); }
    public java.lang.Object $ge(java.lang.Object b, Type t) { return x10.core.Boolean.$box($value >= ((Int)b).$value); }
    // for X10PrettyPrinterVisitor.generateSpecialDispatcher
    public boolean $lt$Z(java.lang.Object b, Type t) { return $value < ((Int)b).$value; }
    public boolean $gt$Z(java.lang.Object b, Type t) { return $value > ((Int)b).$value; }
    public boolean $le$Z(java.lang.Object b, Type t) { return $value <= ((Int)b).$value; }
    public boolean $ge$Z(java.lang.Object b, Type t) { return $value >= ((Int)b).$value; }

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
        return _serialization_id;
    }

    public static X10JavaSerializable $_deserializer(X10JavaDeserializer deserializer) throws IOException {
        return $_deserialize_body(null, deserializer);
    }

    public static X10JavaSerializable $_deserialize_body(Int i, X10JavaDeserializer deserializer) throws IOException {
        int value  = deserializer.readInt();
        i = new Int(value);
        deserializer.record_reference(i);
        return i;
    }
}
