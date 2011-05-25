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

/**
 * Represents a boxed UShort value. Boxed representation is used when casting
 * a UShort value into type Any or parameter type T.
 */
final public class UShort extends x10.core.Struct implements java.lang.Comparable<x10.core.UShort>,
    x10.lang.Arithmetic<x10.core.UShort>, x10.lang.Bitwise<x10.core.UShort>, x10.util.Ordered<x10.core.UShort>
{
    private static final long serialVersionUID = 5575376732671214307L;
    
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

    public static int $unbox(short value) {
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
}
