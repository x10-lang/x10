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
 * Represents a boxed Double value. Boxed representation is used when casting
 * an Double value to type Any, parameter type T or superinterfaces such
 * as Comparable<Double>.
 */
final public class Double extends java.lang.Number implements StructI, java.lang.Comparable<Double>,
// for X10PrettyPrinterVisitor.exposeSpecialDispatcherThroughSpecialInterface
//    x10.lang.Arithmetic<Double>, x10.util.Ordered<Double>
    x10.core.Arithmetic.x10$lang$Double, x10.util.Ordered<Double>
{
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

    public static Double $box(Object obj) {
        if (obj instanceof Double) return (Double) obj;
        else return $box(((java.lang.Double)obj).doubleValue());
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
    public String toString() {
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
    public Double $plus(Object b, Type t) { return Double.$box($value + ((Double)b).$value); }
    public Double $minus(Object b, Type t) { return Double.$box($value - ((Double)b).$value); }
    public Double $times(Object b, Type t) { return Double.$box($value * ((Double)b).$value); }
    public Double $over(Object b, Type t) { return Double.$box($value / ((Double)b).$value); }
    // for X10PrettyPrinterVisitor.exposeSpecialDispatcherThroughSpecialInterface
    public double $plus$D(Object b, Type t) { return $value + ((Double)b).$value; }
    public double $minus$D(Object b, Type t) { return $value - ((Double)b).$value; }
    public double $times$D(Object b, Type t) { return $value * ((Double)b).$value; }
    public double $over$D(Object b, Type t) { return $value / ((Double)b).$value; }
    
    // implements Ordered<Double>
    public Object $lt(Object b, Type t) { return Boolean.$box($value < ((Double)b).$value); }
    public Object $gt(Object b, Type t) { return Boolean.$box($value > ((Double)b).$value); }
    public Object $le(Object b, Type t) { return Boolean.$box($value <= ((Double)b).$value); }
    public Object $ge(Object b, Type t) { return Boolean.$box($value >= ((Double)b).$value); }
    // for X10PrettyPrinterVisitor.generateSpecialDispatcher
    public boolean $lt$Z(Object b, Type t) { return $value < ((Double)b).$value; }
    public boolean $gt$Z(Object b, Type t) { return $value > ((Double)b).$value; }
    public boolean $le$Z(Object b, Type t) { return $value <= ((Double)b).$value; }
    public boolean $ge$Z(Object b, Type t) { return $value >= ((Double)b).$value; }

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

    public static X10JavaSerializable $_deserialize_body(Double $_obj, X10JavaDeserializer $deserializer) throws IOException {
        double value  = $deserializer.readDouble();
        $_obj = new Double(value);
        return $_obj;
    }
}