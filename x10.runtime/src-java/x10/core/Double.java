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
import x10.x10rt.X10JavaDeserializer;
import x10.x10rt.X10JavaSerializable;
import x10.x10rt.X10JavaSerializer;

import java.io.IOException;

/**
 * Represents a boxed Double value. Boxed representation is used when casting
 * an Double value to type Any, parameter type T or superinterfaces such
 * as Comparable<Double>.
 */
final public class Double extends Number implements StructI,
	java.lang.Comparable<Double>, x10.lang.Arithmetic<Double>, x10.util.Ordered<Double>
{
    private static final long serialVersionUID = 1L;
    static {
        x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Double.class);
    }
    private static short _serialization_id;
    
    public static final RuntimeType<?> $RTT = Types.DOUBLE;
    public RuntimeType<?> $getRTT() {return $RTT;}
    public Type<?> $getParam(int i) {return null;}

    final double $value;

    private Double(double value) {
        this.$value = value;
    }

    public static Double $box(double value) { // int because literals essentially have int type in Java
        return new Double(value);
    }

    public static double $unbox(Double obj) {
        return obj.$value;
    }
    
    public static double $unbox(Object obj) {
        if (obj instanceof Double) return ((Double)obj).$value;
        else return ((java.lang.Double)obj).doubleValue();
    }
    
    // make $box/$unbox idempotent
    public static Double $box(Double obj) {
        return obj;
    }

    public static double $unbox(double value) {
        return value;
    }
    
    public boolean _struct_equals$O(Object obj) {
        if (obj instanceof Double && ((Double) obj).$value == $value)
            return true;
        return false;
    }
    
    @Override
    public boolean equals(Object value) {
        if (value instanceof Double) {
            return ((Double) value).$value == $value;
        } else if (value instanceof java.lang.Double) {
            return ((java.lang.Double) value).doubleValue() == $value;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return (int)$value;
    }

    @Override
    public java.lang.String toString() {
        return java.lang.Double.toString($value);
    }
    
    // implements Comparable<Double>
    public int compareTo(Double o) {
        if ($value > o.$value) return 1;
        else if ($value < o.$value) return -1;
        return 0;
    }
    
    // implements Arithmetic<Double>
    public Double $plus$G() { return this; }
    public Double $minus$G() { return Double.$box(-$value); }
    public Double $plus(Double b, Type t) { return Double.$box($value + b.$value); }
    public Double $minus(Double b, Type t) { return Double.$box($value - b.$value); }
    public Double $times(Double b, Type t) { return Double.$box($value * b.$value); }
    public Double $over(Double b, Type t) { return Double.$box($value / b.$value); }
    
    // implements Ordered<Double>
    public java.lang.Object $lt(Double b, Type t) { return x10.core.Boolean.$box($value < b.$value); }
    public java.lang.Object $gt(Double b, Type t) { return x10.core.Boolean.$box($value > b.$value); }
    public java.lang.Object $le(Double b, Type t) { return x10.core.Boolean.$box($value <= b.$value); }
    public java.lang.Object $ge(Double b, Type t) { return x10.core.Boolean.$box($value >= b.$value); }
    
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

    public void $_serialize(X10JavaSerializer serializer) throws IOException {
        serializer.write($value);
    }

    public short $_get_serialization_id() {
        return _serialization_id;
    }

    public static void $_set_serialization_id(short id) {
         _serialization_id = id;
    }

    public static X10JavaSerializable $_deserializer(X10JavaDeserializer deserializer) throws IOException {
        return $_deserialize_body(null, deserializer);
    }

    public static X10JavaSerializable $_deserialize_body(Double d, X10JavaDeserializer deserializer) throws IOException {
        double value  = deserializer.readDouble();
        d = new Double(value);
        deserializer.record_reference(d);
        return d;
    }
}