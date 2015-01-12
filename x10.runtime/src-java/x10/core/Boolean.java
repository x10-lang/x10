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
 * Represents a boxed Boolean value. Boxed representation is used when casting
 * a Boolean value to type Any, parameter type T or superinterfaces such
 * as Comparable<Boolean>.
 */
final public class Boolean implements StructI, java.lang.Comparable<Boolean>
{
    public static final RuntimeType<?> $RTT = Types.BOOLEAN;
    public RuntimeType<?> $getRTT() {return $RTT;}
    public Type<?> $getParam(int i) {return null;}
    
    public static final boolean TRUE = true;
    public static final boolean FALSE = false;
    public static boolean get$TRUE() { return true; }
    public static boolean get$FALSE() { return false; }

    final boolean $value;

    private Boolean(boolean value) {
        this.$value = value;
    }

    private static final Boolean BOXED_TRUE = new Boolean(true);
    private static final Boolean BOXED_FALSE = new Boolean(false);
    public static Boolean $box(boolean value) {
        return value ? BOXED_TRUE : BOXED_FALSE;
    }

    public static boolean $unbox(Boolean obj) {
        return obj.$value;
    }
    
    public static boolean $unbox(Object obj) {
        if (obj instanceof Boolean) return ((Boolean)obj).$value;
        else return ((java.lang.Boolean)obj).booleanValue();
    }
    
    // make $box/$unbox idempotent
    public static Boolean $box(Boolean obj) {
        return obj;
    }

    public static Boolean $box(Object obj) {
        if (obj instanceof Boolean) return (Boolean) obj;
        else return $box(((java.lang.Boolean)obj).booleanValue());
    }

    public static boolean $unbox(boolean value) {
        return value;
    }
    
    public boolean _struct_equals$O(Object obj) {
        if (obj instanceof Boolean && ((Boolean) obj).$value == $value)
            return true;
        return false;
    }
    
    @Override
    public boolean equals(Object value) {
        if (value instanceof Boolean) {
            return ((Boolean) value).$value == $value;
        } else if (value instanceof java.lang.Boolean) { // Boolean literals come here as Boolean autoboxed values
            return ((java.lang.Boolean) value).booleanValue() == $value;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return $value ? 1231 : 1237;
    }

    @Override
    public String toString() {
        return java.lang.Boolean.toString($value);
    }
    
    // implements Comparable<Boolean>
    public int compareTo(Boolean o) {
        return (o.$value == $value ? 0 : ($value ? 1 : -1));
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

    public static X10JavaSerializable $_deserialize_body(Boolean $_obj, X10JavaDeserializer $deserializer) throws IOException {
        boolean value  = $deserializer.readBoolean();
        $_obj = new Boolean(value);
        return $_obj;
    }
}
