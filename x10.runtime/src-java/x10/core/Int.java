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
 * Represents a boxed Int value. Boxed representation is used when casting
 * an Int value to type Any, parameter type T or superinterfaces such
 * as Comparable<Int>.
 */
final public class Int extends x10.core.Struct implements java.lang.Comparable<Int>,
    x10.lang.Arithmetic<Int>, x10.lang.Bitwise<Int>, x10.util.Ordered<Int>
{
    private static final long serialVersionUID = 1L;
    
    public static final x10.rtt.RuntimeType<?> $RTT = Types.INT;
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    public x10.rtt.Type<?> $getParam(int i) {return null;}

    final int $value;

    private Int(int value) {
        this.$value = value;
    }

    public static Int $box(int value) {
        return new Int(value);
    }

    public static int $unbox(Int obj) {
        return obj.$value;
    }
    
    // make $box/$unbox idempotent
    public static Int $box(Int obj) {
        return obj;
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
    public Int $plus(Int b, Type t) { return Int.$box($value + b.$value); }
    public Int $minus(Int b, Type t) { return Int.$box($value - b.$value); }
    public Int $times(Int b, Type t) { return Int.$box($value * b.$value); }
    public Int $over(Int b, Type t) { return Int.$box($value / b.$value); }
    
    // implements Bitwise<Int>
    public Int $tilde$G() { return Int.$box(~$value); }
    public Int $ampersand(Int b, Type t) { return Int.$box($value & b.$value); }
    public Int $bar(Int b, Type t) { return Int.$box($value | b.$value); }
    public Int $caret(Int b, Type t) { return Int.$box($value ^ b.$value); }
    public Int $left$G(final int count) { return Int.$box($value << count); }
    public Int $right$G(final int count) { return Int.$box($value >> count); }
    public Int $unsigned_right$G(final int count) { return Int.$box($value >>> count); }        
    
    // implements Ordered<Int>. Rely on autoboxing of booleans
    public Object $lt(Int b, Type t) { return ($value < b.$value); }
    public Object $gt(Int b, Type t) { return ($value > b.$value); }
    public Object $le(Int b, Type t) { return ($value <= b.$value); }
    public Object $ge(Int b, Type t) { return ($value >= b.$value); }
}
