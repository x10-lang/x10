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
import x10.runtime.impl.java.ULongUtils;
import x10.serialization.X10JavaDeserializer;
import x10.serialization.X10JavaSerializable;
import x10.serialization.X10JavaSerializer;

/**
 * Represents a boxed ULong value. Boxed representation is used when casting
 * a ULong value into type Any or parameter type T.
 */
final public class ULong extends java.lang.Number implements StructI, java.lang.Comparable<ULong>,
// for X10PrettyPrinterVisitor.exposeSpecialDispatcherThroughSpecialInterface
//    x10.lang.Arithmetic<ULong>, x10.lang.Bitwise<ULong>, x10.util.Ordered<ULong>
    x10.core.Arithmetic.x10$lang$ULong, x10.core.Bitwise.x10$lang$ULong, x10.util.Ordered<ULong>
{
    public static final RuntimeType<?> $RTT = Types.ULONG;
    public RuntimeType<?> $getRTT() {return $RTT;}
    public Type<?> $getParam(int i) {return null;}

    final long $value;

    private ULong(long value) {
        this.$value = value;
    }

    private abstract static class Cache {
        static final boolean enabled = java.lang.Boolean.parseBoolean(System.getProperty("x10.lang.ULong.Cache.enabled", "false"));
        static final int low = 0;
        static final int high = enabled ? 255 : (low - 1); // disable caching
        static final ULong cache[] = new ULong[high - low + 1];
        static {
            for (int i = 0; i < cache.length; ++i) {
                cache[i] = new ULong(low + i);
            }
        }
    }

    public static ULong $box(long value) {
        if (Cache.enabled) {
            if (Cache.low <= value && value <= Cache.high) {
                return Cache.cache[(int)value - Cache.low];
            }
        }
        return new ULong(value);
    }
    
    public static long $unbox(ULong obj) {
        return obj.$value;
    }
    
    public static long $unbox(Object obj) {
        if (obj instanceof ULong) return ((ULong)obj).$value;
        else return ((java.lang.Long)obj).longValue();
    }

    // make $box/$unbox idempotent
    public static ULong $box(ULong obj) {
        return obj;
    }

    public static ULong $box(Object obj) {
        if (obj instanceof ULong) return (ULong) obj;
        else return $box(((java.lang.Long)obj).longValue());
    }

    public static long $unbox(long value) {
        return value;
    }
    
    public boolean _struct_equals$O(Object o) {
        if (o instanceof ULong && ((ULong) o).$value == $value)
            return true;
        return false;
    }
    
    @Override
    public boolean equals(Object o) {
        return _struct_equals$O(o);
    }

    @Override
    public int hashCode() {
        return (int)($value ^ ($value >>> 32));
    }
    
    @Override
    public String toString() {
        if ($value >= 0)
            return java.lang.Long.toString($value);
        else {
            // handle negative value
            int highest = 9;      // first digit of 9223372036854775808 = 2^63 ~= 9e18
            long li = $value - 1000000000000000000L; // 1e18
            while (li < 0) {
                li -= 1000000000000000000L; // 1e18
                highest += 1;
            }
            if (li >= 9000000000000000000L) highest++; // 9e18
            return java.lang.Integer.toString(highest) +
                String.format("%018d", li % 1000000000000000000L); // 1e18
        }
    }
    
 	// implements Comparable<ULong>
    public int compareTo(ULong o) {
        long a = ULongUtils.inject($value);
        long b = ULongUtils.inject(o.$value);
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

    public static X10JavaSerializable $_deserialize_body(ULong $_obj, X10JavaDeserializer $deserializer) throws IOException {
        long value = $deserializer.readLong();
        $_obj = new ULong(value);
        return $_obj;
    }
    
    // implements Arithmetic<ULong>
    public ULong $plus$G() { return this; }
    public ULong $minus$G() { return ULong.$box(-$value); }
    public ULong $plus(Object a, Type t) { return ULong.$box($value + ((ULong)a).$value); }
    public ULong $minus(Object a, Type t) { return ULong.$box($value - ((ULong)a).$value); }
    public ULong $times(Object a, Type t) { return ULong.$box($value * ((ULong)a).$value); }
    public ULong $over(Object a, Type t) { return ULong.$box(ULongUtils.div($value,((ULong)a).$value)); }
    // for X10PrettyPrinterVisitor.exposeSpecialDispatcherThroughSpecialInterface
    public long $plus$j(Object a, Type t) { return $value + ((ULong)a).$value; }
    public long $minus$j(Object a, Type t) { return $value - ((ULong)a).$value; }
    public long $times$j(Object a, Type t) { return $value * ((ULong)a).$value; }
    public long $over$j(Object a, Type t) { return ULongUtils.div($value,((ULong)a).$value); }
    
    // implements Bitwise<ULong>
    public ULong $tilde$G() { return ULong.$box(~$value); }
    public ULong $ampersand(Object a, Type t) { return ULong.$box($value & ((ULong)a).$value); }
    public ULong $bar(Object a, Type t) { return ULong.$box($value | ((ULong)a).$value); }
    public ULong $caret(Object a, Type t) { return ULong.$box($value ^ ((ULong)a).$value); }
    public ULong $left$G(long count) { return ULong.$box($value << count); }
    public ULong $right$G(long count) { return ULong.$box($value >>> count); } // ULong is always unsigned
    public ULong $unsigned_right$G(long count) { return ULong.$box($value >>> count); }
    // for X10PrettyPrinterVisitor.exposeSpecialDispatcherThroughSpecialInterface
    public long $ampersand$j(Object a, Type t) { return $value & ((ULong)a).$value; }
    public long $bar$j(Object a, Type t) { return $value | ((ULong)a).$value; }
    public long $caret$j(Object a, Type t) { return $value ^ ((ULong)a).$value; }
    
    // implements Ordered<ULong>
    public Object $lt(Object a, Type t) { return Boolean.$box(ULongUtils.lt($value,((ULong)a).$value)); }
    public Object $gt(Object a, Type t) { return Boolean.$box(ULongUtils.gt($value,((ULong)a).$value)); }
    public Object $le(Object a, Type t) { return Boolean.$box(ULongUtils.le($value,((ULong)a).$value)); }
    public Object $ge(Object a, Type t) { return Boolean.$box(ULongUtils.ge($value,((ULong)a).$value)); }
    // for X10PrettyPrinterVisitor.generateSpecialDispatcher
    public boolean $lt$Z(Object a, Type t) { return ULongUtils.lt($value,((ULong)a).$value); }
    public boolean $gt$Z(Object a, Type t) { return ULongUtils.gt($value,((ULong)a).$value); }
    public boolean $le$Z(Object a, Type t) { return ULongUtils.le($value,((ULong)a).$value); }
    public boolean $ge$Z(Object a, Type t) { return ULongUtils.ge($value,((ULong)a).$value); }

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
        if ($value >= 0) return (float)$value;
        else return ((float)$value - 2.0f*java.lang.Long.MIN_VALUE);
    }
    @Override
    public double doubleValue() {
        if ($value >= 0) return (double)$value;
        else return ((double)$value - 2.0*java.lang.Long.MIN_VALUE);
    }
}
