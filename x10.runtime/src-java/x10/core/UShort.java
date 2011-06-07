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
final public class UShort extends x10.core.Struct implements java.lang.Comparable<UShort>,
    x10.lang.Arithmetic<UShort>, x10.lang.Bitwise<UShort>, x10.util.Ordered<UShort>
{
    private static final long serialVersionUID = 1L;
    private final int _serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(getClass().getName());

    public static final x10.rtt.RuntimeType<?> $RTT = Types.USHORT;
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    public x10.rtt.Type<?> $getParam(int i) {return null;}

    final short $value;

    private UShort(short value) {
        this.$value = value;
    }

    public static UShort $box(short value) {
        return new UShort(value);
    }
    
    public static UShort $box(int value) {  // int is required for literals
        return new UShort((short) value);
    }

    public static short $unbox(UShort o) {
        return o.$value;
    }
    
    // make $box/$unbox idempotent
    public static UShort $box(UShort obj) {
        return obj;
    }

    public static short $unbox(short value) {
        return value;
    }
    
    public boolean equals(Object o) {
        if (o instanceof UShort && ((UShort)o).$value == $value)
            return true;
        return false;
    }
    
    public boolean _struct_equals$O(Object o) {
        return equals(o);
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
    public UShort $plus(UShort a, Type t) { return UShort.$box($value + a.$value); }
    public UShort $minus(UShort a, Type t) { return UShort.$box($value - a.$value); }
    public UShort $times(UShort a, Type t) { return UShort.$box($value * a.$value); }
    public UShort $over(UShort a, Type t) { return UShort.$box((short)((0xffff & $value) / (0xffff & a.$value))); }
    
    // implements Bitwise<UShort>
    public UShort $tilde$G() { return UShort.$box(~$value); }
    public UShort $ampersand(UShort a, Type t) { return UShort.$box($value & a.$value); }
    public UShort $bar(UShort a, Type t) { return UShort.$box($value | a.$value); }
    public UShort $caret(UShort a, Type t) { return UShort.$box($value ^ a.$value); }
    public UShort $left$G(final int count) { return UShort.$box($value << count); }
    public UShort $right$G(final int count) { return UShort.$box((0xffff & $value) >>> count); } // UShort is always unsigned
    public UShort $unsigned_right$G(final int count) { return UShort.$box((0xffff & $value) >>> count); }        
    
    // implements Ordered<UShort>. Rely on autoboxing of booleans
    public Object $lt(UShort a, Type t) { return Unsigned.lt($value,a.$value); }
    public Object $gt(UShort a, Type t) { return Unsigned.gt($value,a.$value); }
    public Object $le(UShort a, Type t) { return Unsigned.le($value,a.$value); }
    public Object $ge(UShort a, Type t) { return Unsigned.ge($value,a.$value); }

    public void _serialize(X10JavaSerializer serializer) throws IOException {
        serializer.write($value);
    }

    public int _get_serialization_id() {
        return _serialization_id;
    }

    public static X10JavaSerializable _deserializer(X10JavaDeserializer deserializer) throws IOException {
        return _deserialize_body(null, deserializer);
    }

    public static X10JavaSerializable _deserialize_body(UShort us, X10JavaDeserializer deserializer) throws IOException {
        short value  = deserializer.readShort();
        us = new UShort(value);
        return us;
    }
}
