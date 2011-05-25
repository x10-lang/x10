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
 * Represents a boxed ULong value. Boxed representation is used when casting
 * a ULong value into type Any or parameter type T.
 */
final public class ULong extends x10.core.Struct implements java.lang.Comparable<x10.core.ULong>,
    x10.lang.Arithmetic<x10.core.ULong>, x10.lang.Bitwise<x10.core.ULong>, x10.util.Ordered<x10.core.ULong>
{
    private static final long serialVersionUID = 5575376732671214307L;
    
    public static final x10.rtt.RuntimeType<?> $RTT = Types.ULONG;
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    public x10.rtt.Type<?> $getParam(int i) {return null;}

    final long $value;

    private ULong(long value) {
        this.$value = value;
    }

    public static ULong $box(long value) {
        return new ULong(value);
    }
    
    public static long $unbox(ULong obj) {
        return obj.$value;
    }
    
    // make $box/$unbox idempotent
    public static ULong $box(ULong obj) {
        return obj;
    }

    public static long $unbox(long value) {
        return value;
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof ULong && ((ULong)obj).$value == $value)
            return true;
        return false;
    }
    
    public boolean _struct_equals$O(Object o) {
        return equals(o);
    }
    
    @Override
    public java.lang.String toString() {
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
                java.lang.String.format("%018d", li % 1000000000000000000L); // 1e18
        }
    }
    
 	// implements Comparable<ULong>
    public int compareTo(ULong o) {
        long a = Unsigned.inject($value);
        long b = Unsigned.inject(o.$value);
        if (a > b) return 1;
        else if (a < b) return -1;
        return 0;
    }
    
    // implements Arithmetic<ULong>
    public ULong $plus$G() { return this; }
    public ULong $minus$G() { return ULong.$box(-$value); }
    public ULong $plus(ULong a, Type t) { return ULong.$box($value + a.$value); }
    public ULong $minus(ULong a, Type t) { return ULong.$box($value - a.$value); }
    public ULong $times(ULong a, Type t) { return ULong.$box($value * a.$value); }
    public ULong $over(ULong a, Type t) { return ULong.$box(Unsigned.div($value,a.$value)); }
    
    // implements Bitwise<ULong>
    public ULong $tilde$G() { return ULong.$box(~$value); }
    public ULong $ampersand(ULong a, Type t) { return ULong.$box($value & a.$value); }
    public ULong $bar(ULong a, Type t) { return ULong.$box($value | a.$value); }
    public ULong $caret(ULong a, Type t) { return ULong.$box($value ^ a.$value); }
    public ULong $left$G(final int count) { return ULong.$box($value << count); }
    public ULong $right$G(final int count) { return ULong.$box($value >>> count); } // ULong is always unsigned
    public ULong $unsigned_right$G(final int count) { return ULong.$box($value >>> count); }        
    
    // implements Ordered<ULong>. Rely on autoboxing of booleans
    public Object $lt(ULong a, Type t) { return Unsigned.lt($value,a.$value); }
    public Object $gt(ULong a, Type t) { return Unsigned.gt($value,a.$value); }
    public Object $le(ULong a, Type t) { return Unsigned.le($value,a.$value); }
    public Object $ge(ULong a, Type t) { return Unsigned.ge($value,a.$value); }
}
