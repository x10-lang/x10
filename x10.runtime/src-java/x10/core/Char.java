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
 * Represents a boxed Char value. Boxed representation is used when casting
 * an Char value to type Any, parameter type T or superinterfaces such
 * as Comparable<Char>.
 */
final public class Char extends Struct implements java.lang.Comparable<Char>, x10.util.Ordered<Char>
{
    private static final long serialVersionUID = 1L;
    
    public static final x10.rtt.RuntimeType<?> $RTT = Types.CHAR;
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    public x10.rtt.Type<?> $getParam(int i) {return null;}

    final char $value;

    private Char(char value) {
        this.$value = value;
    }

    public static Char $box(char value) {
        return new Char(value);
    }

    public static char $unbox(Char obj) {
        return obj.$value;
    }
    
    public static char $unbox(Object obj) {
        if (obj instanceof Char) return ((Char)obj).$value;
        else return ((java.lang.Character)obj).charValue();
    }
    
    // make $box/$unbox idempotent
    public static Char $box(Char obj) {
        return obj;
    }

    public static char $unbox(char value) {
        return value;
    }
    
    public boolean _struct_equals$O(Object obj) {
        if (obj instanceof Char && ((Char) obj).$value == $value)
            return true;
        return false;
    }
    
    @Override
    public boolean equals(Object value) {
        if (value instanceof Char) {
            return ((Char) value).$value == $value;
        } else if (value instanceof java.lang.Character) { // char literals come here as Character autoboxed values
            return ((java.lang.Character) value).charValue() == $value;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return (int) $value;
    }

    @Override
    public java.lang.String toString() {
        return java.lang.Character.toString($value);
    }
    
    // implements Comparable<Char>
    public int compareTo(Char o) {
        if ($value > o.$value) return 1;
        else if ($value < o.$value) return -1;
        return 0;
    }      
    
    // implements Ordered<Char>. Rely on autoboxing of booleans
    public Object $lt(Char b, Type t) { return ($value < b.$value); }
    public Object $gt(Char b, Type t) { return ($value > b.$value); }
    public Object $le(Char b, Type t) { return ($value <= b.$value); }
    public Object $ge(Char b, Type t) { return ($value >= b.$value); }
    
}
