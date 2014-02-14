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

package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;
import x10.util.Ordered;

/**
 * Double is a 64-bit double-precision IEEE 754 floating point data type.
 * Unlike Java, X10 does not restrict the precision of floating
 * point values, so they may be represented by an extended-exponent
 * variant at runtime.  All of the normal
 * arithmetic and bitwise operations are defined on Double, and Double
 * is closed under those operations.  There are also static methods
 * that define conversions from other data types, including String,
 * as well as some Double constants.
 */
@NativeRep("java", "double", null, "x10.rtt.Types.DOUBLE")
@NativeRep("c++", "x10_double", "x10_double", null)
public struct Double implements Comparable[Double], Arithmetic[Double], Ordered[Double] {


    /**
     * A less-than operator.
     * Compares this Double with another Double and returns true if this Double is
     * strictly less than the other Double.
     * Note that NaN values are unordered.
     * @param x the other Double
     * @return true if this Double is strictly less than the other Double.
     */
    @Native("java", "((#this) < (#x))")
    @Native("c++",  "((#this) < (#x))")
    public native operator this < (x:Double): Boolean;

    /**
     * A greater-than operator.
     * Compares this Double with another Double and returns true if this Double is
     * strictly greater than the other Double.
     * Note that NaN values are unordered.
     * @param x the other Double
     * @return true if this Double is strictly greater than the other Double.
     */
    @Native("java", "((#this) > (#x))")
    @Native("c++",  "((#this) > (#x))")
    public native operator this > (x:Double): Boolean;

    /**
     * A less-than-or-equal-to operator.
     * Compares this Double with another Double and returns true if this Double is
     * less than or equal to the other Double.
     * Note that NaN values are unordered.
     * @param x the other Double
     * @return true if this Double is less than or equal to the other Double.
     */
    @Native("java", "((#this) <= (#x))")
    @Native("c++",  "((#this) <= (#x))")
    public native operator this <= (x:Double): Boolean;

    /**
     * A greater-than-or-equal-to operator.
     * Compares this Double with another Double and returns true if this Double is
     * greater than or equal to the other Double.
     * Note that NaN values are unordered.
     * @param x the other Double
     * @return true if this Double is greater than or equal to the other Double.
     */
    @Native("java", "((#this) >= (#x))")
    @Native("c++",  "((#this) >= (#x))")
    public native operator this >= (x:Double): Boolean;


    /**
     * A binary plus operator.
     * Computes the result of the addition of the two operands.
     * @param x the other Double
     * @return the sum of this Double and the other Double.
     */
    @Native("java", "((#this) + (#x))")
    @Native("c++",  "((#this) + (#x))")
    public native operator this + (x:Double): Double;

    /**
     * A binary minus operator.
     * Computes the result of the subtraction of the two operands.
     * @param x the other Double
     * @return the difference of this Double and the other Double.
     */
    @Native("java", "((#this) - (#x))")
    @Native("c++",  "((#this) - (#x))")
    public native operator this - (x:Double): Double;

    /**
     * A binary multiply operator.
     * Computes the result of the multiplication of the two operands.
     * @param x the other Double
     * @return the product of this Double and the other Double.
     */
    @Native("java", "((#this) * (#x))")
    @Native("c++",  "((#this) * (#x))")
    public native operator this * (x:Double): Double;

    /**
     * A binary divide operator.
     * Computes the result of the division of the two operands.
     * @param x the other Double
     * @return the quotient of this Double and the other Double.
     */
    @Native("java", "((#this) / (#x))")
    @Native("c++",  "((#this) / (#x))")
    public native operator this / (x:Double): Double;

    /**
     * A binary remainder operator.
     * Computes a remainder from the division of the two operands.
     * @param x the other Double
     * @return the remainder from dividing this Double by the other Double.
     */
    @Native("java", "((#this) % (#x))")
    @Native("c++",  "::x10::lang::DoubleNatives::mod(#this, #x)")
    public native operator this % (x:Double): Double;

    /**
     * A unary plus operator.
     * A no-op.
     * @return the value of this Double.
     */
    @Native("java", "(+(#this))")
    @Native("c++",  "(+(#this))")
    public native operator + this: Double;

    /**
     * A unary minus operator.
     * Negates the operand.
     * @return the negated value of this Double.
     */
    @Native("java", "(-(#this))")
    @Native("c++",  "(-(#this))")
    public native operator - this: Double;


    /**
     * Coerce a given Byte to a Double.
     * @param x the given Byte
     * @return the given Byte converted to a Double.
     */
    @Native("java", "((double)(byte)(#x))")
    @Native("c++",  "((x10_double) (#x))")
    public native static operator (x:Byte): Double;

    /**
     * Coerce a given Short to a Double.
     * @param x the given Short
     * @return the given Short converted to a Double.
     */
    @Native("java", "((double)(short)(#x))")
    @Native("c++",  "((x10_double) (#x))")
    public native static operator (x:Short): Double;

    /**
     * Coerce a given Int to a Double.
     * @param x the given Int
     * @return the given Int converted to a Double.
     */
    @Native("java", "((double)(int)(#x))")
    @Native("c++",  "((x10_double) (#x))")
    public native static operator (x:Int): Double;

    /**
     * Coerce a given Long to a Double.
     * @param x the given Long
     * @return the given Long converted to a Double.
     */
    @Native("java", "((double)(long)(#x))")
    @Native("c++",  "((x10_double) (#x))")
    public native static operator (x:Long): Double;

    /**
     * Coerce a given UByte to a Double.
     * @param x the given UByte
     * @return the given UByte converted to a Double.
     */
    @Native("java", "((double)((int)(#x)&0xff))")
    @Native("c++",  "((x10_double) (#x))")
    public native static operator (x:UByte): Double;

    /**
     * Coerce a given UShort to a Double.
     * @param x the given UShort
     * @return the given UShort converted to a Double.
     */
    @Native("java", "((double)((int)(#x)&0xffff))")
    @Native("c++",  "((x10_double) (#x))")
    public native static operator (x:UShort): Double;

    /**
     * Coerce a given UInt to a Double.
     * @param x the given UInt
     * @return the given UInt converted to a Double.
     */
    @Native("java", "((double)(((long)#x)&0xffffffffL))")
    @Native("c++",  "((x10_double) (#x))")
    public native static operator (x:UInt): Double;

    /**
     * Coerce a given ULong to a Double.
     * @param x the given ULong
     * @return the given ULong converted to a Double.
     */
    @Native("java", "(x10.runtime.impl.java.ULongUtils.toDouble(#x))")
    @Native("c++",  "((x10_double) (#x))")
    public native static operator (x:ULong): Double;

    /**
     * Coerce a given Float to a Double.
     * @param x the given Float
     * @return the given Float converted to a Double.
     */
    @Native("java", "((double)(float)(#x))")
    @Native("c++",  "((x10_double) (#x))")
    public native static operator (x:Float): Double;


    /**
     * A constant holding the smallest positive nonzero value of type Double,
     * 2<sup>-1074</sup>.
     * It is equal to Double.fromLongBits(0x1).
     */
    // TODO: hexadecimal floating-point literal 0x0.0000000000001P-1022
    @Native("java", "java.lang.Double.MIN_VALUE")
    @Native("c++", "::x10::lang::DoubleNatives::fromLongBits(0x1LL)")
    public static MIN_VALUE: Double = Double.fromLongBits(0x1);

    /**
     * A constant holding the smallest positive normal value of type Double,
     * 2<sup>-1022</sup>.
     * It is equal to Double.fromLongBits(0x0010000000000000).
     */
    // TODO: hexadecimal floating-point literal 0x1.0P-1022
    @Native("java", "java.lang.Double.longBitsToDouble(0x0010000000000000L)")
    @Native("c++", "::x10::lang::DoubleNatives::fromLongBits(0x0010000000000000LL)")
    public static MIN_NORMAL: Double = Double.fromLongBits(0x0010000000000000);

    /**
     * A constant holding the largest positive finite value of type Double,
     * (2-2<sup>-52</sup>)*2<sup>1023</sup>.
     * It is equal to Double.fromLongBits(0x7fefffffffffffff).
     */
    // TODO: hexadecimal floating-point literal 0x1.fffffffffffffP+1023
    @Native("java", "java.lang.Double.MAX_VALUE")
    @Native("c++", "::x10::lang::DoubleNatives::fromLongBits(0x7fefffffffffffffLL)")
    public static MAX_VALUE: Double = Double.fromLongBits(0x7fefffffffffffff);

    /**
     * A constant holding the positive infinity of type Double.
     * It is equal to Double.fromLongBits(0x7ff0000000000000).
     */
    @Native("java", "java.lang.Double.POSITIVE_INFINITY")
    @Native("c++", "::x10::lang::DoubleNatives::fromLongBits(0x7ff0000000000000LL)")
    public static POSITIVE_INFINITY: Double = Double.fromLongBits(0x7ff0000000000000);

    /**
     * A constant holding the negative infinity of type Double.
     * It is equal to Double.fromLongBits(0xfff0000000000000).
     */
    @Native("java", "java.lang.Double.NEGATIVE_INFINITY")
    @Native("c++", "::x10::lang::DoubleNatives::fromLongBits(0xfff0000000000000LL)")
    public static NEGATIVE_INFINITY: Double = Double.fromLongBits(0xfff0000000000000);

    /**
     * A constant holding a Not-a-Number (NaN) value of type Double.
     * It is equal to Double.fromLongBits(0x7ff8000000000000).
     */
    @Native("java", "java.lang.Double.NaN")
    @Native("c++", "::x10::lang::DoubleNatives::fromLongBits(0x7ff8000000000000LL)")
    public static NaN: Double = Double.fromLongBits(0x7ff8000000000000);


    /**
     * Returns a hexadecimal String representation of this Double.
     * @return a hex String representation of this Double.
     */
    @Native("java", "java.lang.Double.toHexString(#this)")
    @Native("c++", "::x10::lang::DoubleNatives::toHexString(#this)")
    public native def toHexString(): String;

    /**
     * Returns a String representation of this Double.
     * The result has enough significant digits to uniquely distinguish it from
     * adjacent Double values.
     * @return a String representation of this Double.
     */
    @Native("java", "java.lang.Double.toString(#this)")
    @Native("c++", "::x10aux::to_string(#this)")
    public native def toString(): String;


    /**
     * @deprecated use {@link #parse(String)} instead
     */
    @Native("java", "java.lang.Double.parseDouble(#s)")
    @Native("c++", "::x10::lang::DoubleNatives::parseDouble(#s)")
    public native static def parseDouble(s:String): Double ; //throwsNumberFormatException;

    /**
     * Parses the String argument as a Double value.
     * @param s the String containing the Double representation to be parsed
     * @return the Double represented by the String argument.
     * @throws NumberFormatException if the String does not contain a parsable Double.
     */
    @Native("java", "java.lang.Double.parseDouble(#s)")
    @Native("c++", "::x10::lang::DoubleNatives::parseDouble(#s)")
    public native static def parse(s:String): Double ; //throwsNumberFormatException;


    /**
     * Returns true if this Double is a Not-a-Number (NaN), false otherwise.
     * @return true if this Double is NaN; false otherwise.
     */
    @Native("java", "java.lang.Double.isNaN(#this)")
    @Native("c++", "::x10::lang::DoubleNatives::isNaN(#this)")
    public native def isNaN(): Boolean;

    /**
     * Returns true if this Double is infinitely large in magnitude, false otherwise.
     * @return true if this Double is positive infinity or negative infinity; false otherwise.
     */
    @Native("java", "java.lang.Double.isInfinite(#this)")
    @Native("c++", "::x10::lang::DoubleNatives::isInfinite(#this)")
    public native def isInfinite(): Boolean;


    /**
     * Returns a representation of this Double according to the IEEE 754
     * floating-point "double format" bit layout, collapsing all NaN
     * values to a single "canonical" value.
     * The result is an Int that, when given to the {@link #fromLongBits(Int)}
     * method, will produce a Double value the same as this Double (except
     * all NaN values are collapsed to a single "canonical" NaN value).
     * @return the bits that represent this Double.
     */
    @Native("java", "java.lang.Double.doubleToLongBits(#this)")
    @Native("c++", "::x10::lang::DoubleNatives::toLongBits(#this)")
    public native def toLongBits(): Long;

    /**
     * Returns a representation of this Double according to the IEEE 754
     * floating-point "double format" bit layout, preserving NaN values.
     * The result is an Int that, when given to the {@link #fromLongBits(Int)}
     * method, will produce a Double value the same as this Double.
     * @return the bits that represent this Double.
     */
    @Native("java", "java.lang.Double.doubleToRawLongBits(#this)")
    @Native("c++", "::x10::lang::DoubleNatives::toRawLongBits(#this)")
    public native def toRawLongBits(): Long;

    /**
     * Returns the Double corresponding to a given bit representation.
     * The argument is considered to be a representation of a floating-point
     * value according to the IEEE 754 floating-point "double format" bit
     * layout.
     * The NaN values constructed by this method are not distinguishable
     * by any operation provided by X10 except for the {@link #toRawLongBits()}
     * method.<br>
     * Note that on some architectures, it may not be possible to return
     * certain patterns of NaN from this method, so for some Int values
     * <code>Double.fromLongBits(start).toRawLongBits()</code> may not equal
     * start.
     * @param bits an Int representing the floating-point value
     * @return the Double with the same bit pattern.
     */
    @Native("java", "java.lang.Double.longBitsToDouble(#x)")
    @Native("c++", "::x10::lang::DoubleNatives::fromLongBits(#x)")
    public static native def fromLongBits(x:Long): Double;


    /**
     * Return true if the given entity is a Double, and this Double is equal
     * to the given entity.
     * @param x the given entity
     * @return true if this Double is equal to the given entity.
     */
    @Native("java", "x10.rtt.Equality.equalsequals(#this, #x)")
    @Native("c++", "::x10aux::equals(#this,#x)")
    public native def equals(x:Any):Boolean;

    /**
     * Returns true if this Double is equal to the given Double.
     * @param x the given Double
     * @return true if this Double is equal to the given Double.
     */
    @Native("java", "x10.rtt.Equality.equalsequals(#this, #x)")
    @Native("c++", "::x10aux::equals(#this,#x)")
    public native def equals(x:Double):Boolean;

    /**
    * Returns a negative Int, zero, or a positive Int if this Double is less than, equal
    * to, or greater than the given Double.
    * @param x the given Double
    * @return a negative Int, zero, or a positive Int if this Double is less than, equal
    * to, or greater than the given Double.
    */
   @Native("java", "x10.rtt.Equality.compareTo(#this, #x)")
   @Native("c++", "::x10::lang::DoubleNatives::compareTo(#this, #x)")
   public native def compareTo(x:Double):Int;
}
public type Double(b:Double) = Double{self==b};
