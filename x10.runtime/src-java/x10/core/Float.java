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
import x10.serialization.X10JavaDeserializer;
import x10.serialization.X10JavaSerializable;
import x10.serialization.X10JavaSerializer;

/**
 * Represents a boxed Float value. Boxed representation is used when casting
 * an Float value to type Any, parameter type T or superinterfaces such
 * as Comparable<Float>.
 */
final public class Float extends java.lang.Number implements StructI, java.lang.Comparable<Float>,
// for X10PrettyPrinterVisitor.exposeSpecialDispatcherThroughSpecialInterface
//    x10.lang.Arithmetic<Float>, x10.util.Ordered<Float>
    x10.core.Arithmetic.x10$lang$Float, x10.util.Ordered<Float>
{
    public static final RuntimeType<?> $RTT = Types.FLOAT;
    public RuntimeType<?> $getRTT() {return $RTT;}
    public Type<?> $getParam(int i) {return null;}

    final float $value;

    private Float(float value) {
        this.$value = value;
    }

    public static Float $box(float value) { // int because literals essentially have int type in Java
        return new Float(value);
    }

    public static float $unbox(Float obj) {
        return obj.$value;
    }
    
    public static float $unbox(Object obj) {
        if (obj instanceof Float) return ((Float)obj).$value;
        else return ((java.lang.Float)obj).floatValue();
    }
    
    // make $box/$unbox idempotent
    public static Float $box(Float obj) {
        return obj;
    }

    public static Float $box(Object obj) {
        if (obj instanceof Float) return (Float) obj;
        else return $box(((java.lang.Float)obj).floatValue());
    }

    public static float $unbox(float value) {
        return value;
    }
    
    public boolean _struct_equals$O(Object obj) {
        if (obj instanceof Float && ((Float) obj).$value == $value)
            return true;
        return false;
    }
    
    @Override
    public boolean equals(Object value) {
        if (value instanceof Float) {
            return ((Float) value).$value == $value;
        } else if (value instanceof java.lang.Float) {
            return ((java.lang.Float) value).floatValue() == $value;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return (int)$value;
    }

    @Override
    public String toString() {
        return java.lang.Float.toString($value);
    }
    
    // implements Comparable<Float>
    public int compareTo(Float o) {
        if ($value > o.$value) return 1;
        else if ($value < o.$value) return -1;
        return 0;
    }
    
    // implements Arithmetic<Float>
    public Float $plus$G() { return this; }
    public Float $minus$G() { return Float.$box(-$value); }
    public Float $plus(Object b, Type t) { return Float.$box($value + ((Float)b).$value); }
    public Float $minus(Object b, Type t) { return Float.$box($value - ((Float)b).$value); }
    public Float $times(Object b, Type t) { return Float.$box($value * ((Float)b).$value); }
    public Float $over(Object b, Type t) { return Float.$box($value / ((Float)b).$value); }
    // for X10PrettyPrinterVisitor.exposeSpecialDispatcherThroughSpecialInterface
    public float $plus$F(Object b, Type t) { return $value + ((Float)b).$value; }
    public float $minus$F(Object b, Type t) { return $value - ((Float)b).$value; }
    public float $times$F(Object b, Type t) { return $value * ((Float)b).$value; }
    public float $over$F(Object b, Type t) { return $value / ((Float)b).$value; }
    
    // implements Ordered<Float>
    public Object $lt(Object b, Type t) { return Boolean.$box($value < ((Float)b).$value); }
    public Object $gt(Object b, Type t) { return Boolean.$box($value > ((Float)b).$value); }
    public Object $le(Object b, Type t) { return Boolean.$box($value <= ((Float)b).$value); }
    public Object $ge(Object b, Type t) { return Boolean.$box($value >= ((Float)b).$value); }
    // for X10PrettyPrinterVisitor.generateSpecialDispatcher
    public boolean $lt$Z(Object b, Type t) { return $value < ((Float)b).$value; }
    public boolean $gt$Z(Object b, Type t) { return $value > ((Float)b).$value; }
    public boolean $le$Z(Object b, Type t) { return $value <= ((Float)b).$value; }
    public boolean $ge$Z(Object b, Type t) { return $value >= ((Float)b).$value; }

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
        return (float)$value;
    }
    @Override
    public double doubleValue() {
        return (double)$value;
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

    public static X10JavaSerializable $_deserialize_body(Float $_obj, X10JavaDeserializer $deserializer) throws IOException {
        float value  = $deserializer.readFloat();
        $_obj = new Float(value);
        return $_obj;
    }
}