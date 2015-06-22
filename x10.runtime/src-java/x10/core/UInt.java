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
 * Represents a boxed UInt value. Boxed representation is used when casting
 * a UInt value into type Any or parameter type T.
 */
final public class UInt extends java.lang.Number implements StructI, java.lang.Comparable<UInt>,
// for X10PrettyPrinterVisitor.exposeSpecialDispatcherThroughSpecialInterface
//    x10.lang.Arithmetic<UInt>, x10.lang.Bitwise<UInt>, x10.util.Ordered<UInt>
    x10.core.Arithmetic.x10$lang$UInt, x10.core.Bitwise.x10$lang$UInt, x10.util.Ordered<UInt>
{
    public static final RuntimeType<?> $RTT = Types.UINT;
    public RuntimeType<?> $getRTT() {return $RTT;}
    public Type<?> $getParam(int i) {return null;}

    final int $value;

    private UInt(int value) {
        this.$value = value;
    }

    private abstract static class Cache {
        static final boolean enabled = java.lang.Boolean.parseBoolean(System.getProperty("x10.lang.UInt.Cache.enabled", "false"));
        static final int low = 0;
        static final int high;
        static final UInt cache[];
        static {
            // high value may be configured by property
            int h = 255;
            String highPropValue = System.getProperty("x10.lang.UInt.Cache.high");
            if (highPropValue != null) {
                int i = java.lang.Integer.parseInt(highPropValue);
                i = Math.max(i, h);
                // Maximum array size is Integer.MAX_VALUE
                h = Math.min(i, Integer.MAX_VALUE + low);
            }
            high = enabled ? h : (low - 1); // disable caching

            cache = new UInt[high - low + 1];
            for (int i = 0; i < cache.length; ++i) {
                cache[i] = new UInt(low + i);
            }
        }
    }

    public static UInt $box(int value) {
        if (Cache.enabled) {
            if (Cache.low <= value && value <= Cache.high) {
                return Cache.cache[value - Cache.low];
            }
        }
        return new UInt(value);
    }

    public static int $unbox(UInt obj) {
        return obj.$value;
    }
    
    public static int $unbox(Object obj) {
        if (obj instanceof UInt) return ((UInt)obj).$value;
        else return ((java.lang.Integer)obj).intValue();
    }
    
    // make $box/$unbox idempotent
    public static UInt $box(UInt obj) {
        return obj;
    }

    public static UInt $box(Object obj) {
        if (obj instanceof UInt) return (UInt) obj;
        else return $box(((java.lang.Integer)obj).intValue());
    }

    public static int $unbox(int value) {
        return value;
    }
    
    public boolean _struct_equals$O(Object o) {
        if (o instanceof UInt && ((UInt) o).$value == $value)
            return true;
        return false;
    }
    
    @Override
    public boolean equals(Object o) {
        return _struct_equals$O(o);
    }
  
    @Override
    public int hashCode() {
        return $value;
    }

    @Override
    public String toString() {
        if ($value >= 0)
            return java.lang.Integer.toString($value);
        else
            return java.lang.Long.toString((long)$value & 0xFFFFffffL);
    }
	
	// implements Comparable<UInt>
    public int compareTo(UInt o) {
        int a = UIntUtils.inject($value);
        int b = UIntUtils.inject(o.$value);
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

    public static X10JavaSerializable $_deserialize_body(UInt $_obj, X10JavaDeserializer $deserializer) throws IOException {
        int value = $deserializer.readInt();
        $_obj = new UInt(value);
        return $_obj;
    }
    
    // implements Arithmetic<UInt>
    public UInt $plus$G() { return this; }
    public UInt $minus$G() { return UInt.$box(-$value); }
    public UInt $plus(Object a, Type t) { return UInt.$box($value + ((UInt)a).$value); }
    public UInt $minus(Object a, Type t) { return UInt.$box($value - ((UInt)a).$value); }
    public UInt $times(Object a, Type t) { return UInt.$box($value * ((UInt)a).$value); }
    public UInt $over(Object a, Type t) { return UInt.$box(UIntUtils.div($value,((UInt)a).$value)); }
    // for X10PrettyPrinterVisitor.exposeSpecialDispatcherThroughSpecialInterface
    public int $plus$i(Object a, Type t) { return $value + ((UInt)a).$value; }
    public int $minus$i(Object a, Type t) { return $value - ((UInt)a).$value; }
    public int $times$i(Object a, Type t) { return $value * ((UInt)a).$value; }
    public int $over$i(Object a, Type t) { return UIntUtils.div($value,((UInt)a).$value); }
    
    // implements Bitwise<UInt>
    public UInt $tilde$G() { return UInt.$box(~$value); }
    public UInt $ampersand(Object a, Type t) { return UInt.$box($value & ((UInt)a).$value); }
    public UInt $bar(Object a, Type t) { return UInt.$box($value | ((UInt)a).$value); }
    public UInt $caret(Object a, Type t) { return UInt.$box($value ^ ((UInt)a).$value); }
    public UInt $left$G(long count) { return UInt.$box($value << count); }
    public UInt $right$G(long count) { return UInt.$box($value >>> count); } // UInt is always unsigned
    public UInt $unsigned_right$G(long count) { return UInt.$box($value >>> count); }
    // for X10PrettyPrinterVisitor.exposeSpecialDispatcherThroughSpecialInterface
    public int $ampersand$i(Object a, Type t) { return $value & ((UInt)a).$value; }
    public int $bar$i(Object a, Type t) { return $value | ((UInt)a).$value; }
    public int $caret$i(Object a, Type t) { return $value ^ ((UInt)a).$value; }
    
    // implements Ordered<UInt>
    public Object $lt(Object a, Type t) { return Boolean.$box(UIntUtils.lt($value,((UInt)a).$value)); }
    public Object $gt(Object a, Type t) { return Boolean.$box(UIntUtils.gt($value,((UInt)a).$value)); }
    public Object $le(Object a, Type t) { return Boolean.$box(UIntUtils.le($value,((UInt)a).$value)); }
    public Object $ge(Object a, Type t) { return Boolean.$box(UIntUtils.ge($value,((UInt)a).$value)); }
    // for X10PrettyPrinterVisitor.generateSpecialDispatcher
    public boolean $lt$Z(Object a, Type t) { return UIntUtils.lt($value,((UInt)a).$value); }
    public boolean $gt$Z(Object a, Type t) { return UIntUtils.gt($value,((UInt)a).$value); }
    public boolean $le$Z(Object a, Type t) { return UIntUtils.le($value,((UInt)a).$value); }
    public boolean $ge$Z(Object a, Type t) { return UIntUtils.ge($value,((UInt)a).$value); }

    // extends abstract class java.lang.Number
    @Override
    public int intValue() {
        return (int)$value;
    }
    @Override
    public long longValue() {
        return (long)(((long)$value)&0xffffFFFFl);
    }
    @Override
    public float floatValue() {
        return (float)(((long)$value)&0xffffFFFFl);
    }
    @Override
    public double doubleValue() {
        return (double)(((long)$value)&0xffffFFFFl);
    }
}
