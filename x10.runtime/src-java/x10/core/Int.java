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
 * Represents a boxed Int value. Boxed representation is used when casting
 * an Int value to type Any, parameter type T or superinterfaces such
 * as Comparable<Int>.
 */
final public class Int extends java.lang.Number implements StructI, java.lang.Comparable<Int>,
// for X10PrettyPrinterVisitor.exposeSpecialDispatcherThroughSpecialInterface
//    x10.lang.Arithmetic<Int>, x10.lang.Bitwise<Int>, x10.util.Ordered<Int>
    x10.core.Arithmetic.x10$lang$Int, x10.core.Bitwise.x10$lang$Int, x10.util.Ordered<Int>
{
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
            String highPropValue = System.getProperty("x10.lang.Int.Cache.high");
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
    public String toString() {
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
    public Int $plus(Object b, Type t) { return Int.$box($value + ((Int)b).$value); }
    public Int $minus(Object b, Type t) { return Int.$box($value - ((Int)b).$value); }
    public Int $times(Object b, Type t) { return Int.$box($value * ((Int)b).$value); }
    public Int $over(Object b, Type t) { return Int.$box($value / ((Int)b).$value); }
    // for X10PrettyPrinterVisitor.exposeSpecialDispatcherThroughSpecialInterface
    public int $plus$I(Object b, Type t) { return $value + ((Int)b).$value; }
    public int $minus$I(Object b, Type t) { return $value - ((Int)b).$value; }
    public int $times$I(Object b, Type t) { return $value * ((Int)b).$value; }
    public int $over$I(Object b, Type t) { return $value / ((Int)b).$value; }
    
    // implements Bitwise<Int>
    public Int $tilde$G() { return Int.$box(~$value); }
    public Int $ampersand(Object b, Type t) { return Int.$box($value & ((Int)b).$value); }
    public Int $bar(Object b, Type t) { return Int.$box($value | ((Int)b).$value); }
    public Int $caret(Object b, Type t) { return Int.$box($value ^ ((Int)b).$value); }
    public Int $left$G(long count) { return Int.$box($value << count); }
    public Int $right$G(long count) { return Int.$box($value >> count); }
    public Int $unsigned_right$G(long count) { return Int.$box($value >>> count); }
    // for X10PrettyPrinterVisitor.exposeSpecialDispatcherThroughSpecialInterface
    public int $ampersand$I(Object b, Type t) { return $value & ((Int)b).$value; }
    public int $bar$I(Object b, Type t) { return $value | ((Int)b).$value; }
    public int $caret$I(Object b, Type t) { return $value ^ ((Int)b).$value; }
    
    // implements Ordered<Int>
    public Object $lt(Object b, Type t) { return Boolean.$box($value < ((Int)b).$value); }
    public Object $gt(Object b, Type t) { return Boolean.$box($value > ((Int)b).$value); }
    public Object $le(Object b, Type t) { return Boolean.$box($value <= ((Int)b).$value); }
    public Object $ge(Object b, Type t) { return Boolean.$box($value >= ((Int)b).$value); }
    // for X10PrettyPrinterVisitor.generateSpecialDispatcher
    public boolean $lt$Z(Object b, Type t) { return $value < ((Int)b).$value; }
    public boolean $gt$Z(Object b, Type t) { return $value > ((Int)b).$value; }
    public boolean $le$Z(Object b, Type t) { return $value <= ((Int)b).$value; }
    public boolean $ge$Z(Object b, Type t) { return $value >= ((Int)b).$value; }

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

    public static X10JavaSerializable $_deserialize_body(Int $_obj, X10JavaDeserializer $deserializer) throws IOException {
        int value  = $deserializer.readInt();
        $_obj = new Int(value);
        return $_obj;
    }
}
