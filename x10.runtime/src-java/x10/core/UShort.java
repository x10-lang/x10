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
 * Represents a boxed UShort value. Boxed representation is used when casting
 * a UShort value into type Any or parameter type T.
 */
final public class UShort extends java.lang.Number implements StructI, java.lang.Comparable<UShort>,
// for X10PrettyPrinterVisitor.exposeSpecialDispatcherThroughSpecialInterface
//    x10.lang.Arithmetic<UShort>, x10.lang.Bitwise<UShort>, x10.util.Ordered<UShort>
    x10.core.Arithmetic.x10$lang$UShort, x10.core.Bitwise.x10$lang$UShort, x10.util.Ordered<UShort>
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = DeserializationDispatcher.addDispatcher(DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, UShort.class);

    public static final RuntimeType<?> $RTT = Types.USHORT;
    public RuntimeType<?> $getRTT() {return $RTT;}
    public Type<?> $getParam(int i) {return null;}

    final short $value;

    private UShort(short value) {
        this.$value = value;
    }

    private abstract static class Cache {
        static final boolean enabled = java.lang.Boolean.parseBoolean(System.getProperty("x10.lang.UShort.Cache.enabled", "false"));
        static final int low = 0;
        static final int high = enabled ? 255 : (low - 1); // disable caching
        static final UShort cache[] = new UShort[high - low + 1];
        static {
            for (int i = 0; i < cache.length; ++i) {
                cache[i] = new UShort((short)(low + i));
            }
        }
    }

    public static UShort $box(short value) {
        if (Cache.enabled) {
            int valueAsInt = value;
            if (Cache.low <= valueAsInt && valueAsInt <= Cache.high) {
                return Cache.cache[valueAsInt - Cache.low];
            }
        }
        return new UShort(value);
    }
    
    public static UShort $box(int value) {  // int is required for literals
        return $box((short)value);
    }

    public static short $unbox(UShort obj) {
        return obj.$value;
    }
    
    public static short $unbox(Object obj) {
        if (obj instanceof UShort) return ((UShort)obj).$value;
        else return ((java.lang.Short)obj).shortValue();
    }
    
    // make $box/$unbox idempotent
    public static UShort $box(UShort obj) {
        return obj;
    }

    public static UShort $box(Object obj) {
        if (obj instanceof UShort) return (UShort) obj;
        else return $box(((java.lang.Short)obj).shortValue());
    }

    public static short $unbox(short value) {
        return value;
    }
    
    public boolean _struct_equals$O(Object o) {
        if (o instanceof UShort && ((UShort)o).$value == $value)
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
    public java.lang.String toString() {
        if ($value >= 0)
            return java.lang.Short.toString($value);
        else
            return java.lang.Integer.toString((int)$value & 0xFFFF);
    }
    
 	// implements Comparable<UShort>
    public int compareTo(UShort o) {
        int a = ((int)$value) & 0xFFFF;
        int b = ((int)o.$value) & 0xFFFF;
        if (a > b) return 1;
        else if (a < b) return -1;
        return 0;
    }

    // implements Arithmetic<UShort>
    public UShort $plus$G() { return this; }
    public UShort $minus$G() { return UShort.$box(-$value); }
    public UShort $plus(java.lang.Object a, Type t) { return UShort.$box($value + ((UShort)a).$value); }
    public UShort $minus(java.lang.Object a, Type t) { return UShort.$box($value - ((UShort)a).$value); }
    public UShort $times(java.lang.Object a, Type t) { return UShort.$box($value * ((UShort)a).$value); }
    public UShort $over(java.lang.Object a, Type t) { return UShort.$box((short)((0xffff & $value) / (0xffff & ((UShort)a).$value))); }
    // for X10PrettyPrinterVisitor.exposeSpecialDispatcherThroughSpecialInterface
    public short $plus$s(java.lang.Object a, Type t) { return (short) ($value + ((UShort)a).$value); }
    public short $minus$s(java.lang.Object a, Type t) { return (short) ($value - ((UShort)a).$value); }
    public short $times$s(java.lang.Object a, Type t) { return (short) ($value * ((UShort)a).$value); }
    public short $over$s(java.lang.Object a, Type t) { return (short) ((0xffff & $value) / (0xffff & ((UShort)a).$value)); }
    
    // implements Bitwise<UShort>
    public UShort $tilde$G() { return UShort.$box(~$value); }
    public UShort $ampersand(java.lang.Object a, Type t) { return UShort.$box($value & ((UShort)a).$value); }
    public UShort $bar(java.lang.Object a, Type t) { return UShort.$box($value | ((UShort)a).$value); }
    public UShort $caret(java.lang.Object a, Type t) { return UShort.$box($value ^ ((UShort)a).$value); }
    public UShort $left$G(int count) { return UShort.$box($value << count); }
    public UShort $right$G(int count) { return UShort.$box((0xffff & $value) >>> count); } // UShort is always unsigned
    public UShort $unsigned_right$G(int count) { return UShort.$box((0xffff & $value) >>> count); }
    // for X10PrettyPrinterVisitor.exposeSpecialDispatcherThroughSpecialInterface
    public short $ampersand$s(java.lang.Object a, Type t) { return (short) ($value & ((UShort)a).$value); }
    public short $bar$s(java.lang.Object a, Type t) { return (short) ($value | ((UShort)a).$value); }
    public short $caret$s(java.lang.Object a, Type t) { return (short) ($value ^ ((UShort)a).$value); }
    
    // implements Ordered<UShort>
    public java.lang.Object $lt(java.lang.Object a, Type t) { return x10.core.Boolean.$box(x10.runtime.impl.java.UIntUtils.lt($value,((UShort)a).$value)); }
    public java.lang.Object $gt(java.lang.Object a, Type t) { return x10.core.Boolean.$box(x10.runtime.impl.java.UIntUtils.gt($value,((UShort)a).$value)); }
    public java.lang.Object $le(java.lang.Object a, Type t) { return x10.core.Boolean.$box(x10.runtime.impl.java.UIntUtils.le($value,((UShort)a).$value)); }
    public java.lang.Object $ge(java.lang.Object a, Type t) { return x10.core.Boolean.$box(x10.runtime.impl.java.UIntUtils.ge($value,((UShort)a).$value)); }
    // for X10PrettyPrinterVisitor.generateSpecialDispatcher
    public boolean $lt$Z(java.lang.Object a, Type t) { return x10.runtime.impl.java.UIntUtils.lt($value,((UShort)a).$value); }
    public boolean $gt$Z(java.lang.Object a, Type t) { return x10.runtime.impl.java.UIntUtils.gt($value,((UShort)a).$value); }
    public boolean $le$Z(java.lang.Object a, Type t) { return x10.runtime.impl.java.UIntUtils.le($value,((UShort)a).$value); }
    public boolean $ge$Z(java.lang.Object a, Type t) { return x10.runtime.impl.java.UIntUtils.ge($value,((UShort)a).$value); }

    public void $_serialize(X10JavaSerializer $serializer) throws IOException {
        $serializer.write($value);
    }

    public short $_get_serialization_id() {
        return $_serialization_id;
    }

    public static X10JavaSerializable $_deserializer(X10JavaDeserializer $deserializer) throws IOException {
        return $_deserialize_body(null, $deserializer);
    }

    public static X10JavaSerializable $_deserialize_body(UShort $_obj, X10JavaDeserializer $deserializer) throws IOException {
        short value = $deserializer.readShort();
        $_obj = new UShort(value);
        $deserializer.record_reference($_obj);
        return $_obj;
    }

    // extends abstract class java.lang.Number
    @Override
    public int intValue() {
        return (int)(((int)$value)&0xffff);
    }
    @Override
    public long longValue() {
        return (long)(((int)$value)&0xffff);
    }
    @Override
    public float floatValue() {
        return (float)(((int)$value)&0xffff);
    }
    @Override
    public double doubleValue() {
        return (double)(((int)$value)&0xffff);
    }
}
