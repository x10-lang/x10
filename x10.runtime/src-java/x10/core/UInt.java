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

import x10.rtt.Types;

/**
 * Represents a boxed UInt value. Boxed representation is used when casting
 * a UInt value into type Any or parameter type T.
 */
final public class UInt extends x10.core.Struct
{
    final int value;

    private UInt(int value) {
        this.value = value;
    }

    public static UInt box(int value) {
        return new UInt(value);
    }

    public static int unbox(UInt o) {
        return o.value;
    }
    
    public boolean equals(Object o) {
        if (o instanceof UInt && ((UInt)o).value == value)
            return true;
        return false;
    }
    
    private static final long serialVersionUID = 5575376732671214307L;
    
    public static final x10.rtt.RuntimeType<?> $RTT = Types.UINT;
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    public x10.rtt.Type<?> $getParam(int i) {return null;}

    public boolean _struct_equals$O(Object o) {
        return equals(o);
    }
    
    @Override
    public java.lang.String toString() {
        if (value >= 0)
            return java.lang.Integer.toString(value);
        else
            return java.lang.Long.toString((long)value & 0xFFFFffffL);
    }
}
