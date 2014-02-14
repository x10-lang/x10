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
 * Float is a 32-bit single-precision IEEE 754 floating point data type.
 * Unlike Java, X10 does not restrict the precision of floating
 * point values, so they may be represented by an extended-exponent
 * variant at runtime.  All of the normal
 * arithmetic and bitwise operations are defined on Float, and Float
 * is closed under those operations.  There are also static methods
 * that define conversions from other data types, including String,
 * as well as some Float constants.
 */
@NativeRep("java", "float", null, "x10.rtt.Types.FLOAT")
@NativeRep("c++", "x10_float", "x10_float", null)
public struct Float implements Comparable[Float], Arithmetic[Float], Ordered[Float] {

    /**
     * A less-than operator.
     * Compares this Float with another Float and returns true if this Float is
     * strictly less than the other Float.
     * Note that NaN values are unordered.
     * @param x the other Float
     * @return true if this Float is strictly less than the other Float.
     */
    @Native("java", "((#this) < (#x))")
    @Native("c++",  "((#this) < (#x))")
    public native operator this < (x:Float): Boolean;

    /**
     * A greater-than operator.
     * Compares this Float with another Float and returns true if this Float is
     * strictly greater than the other Float.
     * Note that NaN values are unordered.
     * @param x the other Float
     * @return true if this Float is strictly greater than the other Float.
     */
    @Native("java", "((#this) > (#x))")
    @Native("c++",  "((#this) > (#x))")
    public native operator this > (x:Float): Boolean;

    /**
     * A less-than-or-equal-to operator.
     * Compares this Float with another Float and returns true if this Float is
     * less than or equal to the other Float.
     * Note that NaN values are unordered.
     * @param x the other Float
     * @return true if this Float is less than or equal to the other Float.
     */
    @Native("java", "((#this) <= (#x))")
    @Native("c++",  "((#this) <= (#x))")
    public native operator this <= (x:Float): Boolean;

    /**
     * A greater-than-or-equal-to operator.
     * Compares this Float with another Float and returns true if this Float is
     * greater than or equal to the other Float.
     * Note that NaN values are unordered.
     * @param x the other Float
     * @return true if this Float is greater than or equal to the other Float.
     */
    @Native("java", "((#this) >= (#x))")
    @Native("c++",  "((#this) >= (#x))")
    public native operator this >= (x:Float): Boolean;


    /**
     * A binary plus operator.
     * Computes the result of the addition of the two operands.
     * @param x the other Float
     * @return the sum of this Float and the other Float.
     */
    @Native("java", "((#this) + (#x))")
    @Native("c++",  "((#this) + (#x))")
    public native operator this + (x:Float): Float;

    /**
     * A binary minus operator.
     * Computes the result of the subtraction of the two operands.
     * @param x the other Float
     * @return the difference of this Float and the other Float.
     */
    @Native("java", "((#this) - (#x))")
    @Native("c++",  "((#this) - (#x))")
    public native operator this - (x:Float): Float;

    /**
     * A binary multiply operator.
     * Computes the result of the multiplication of the two operands.
     * @param x the other Float
     * @return the product of this Float and the other Float.
     */
    @Native("java", "((#this) * (#x))")
    @Native("c++",  "((#this) * (#x))")
    public native operator this * (x:Float): Float;

    /**
     * A binary divide operator.
     * Computes the result of the division of the two operands.
     * @param x the other Float
     * @return the quotient of this Float and the other Float.
     */
    @Native("java", "((#this) / (#x))")
    @Native("c++",  "((#this) / (#x))")
    public native operator this / (x:Float): Float;

    /**
     * A binary remainder operator.
     * Computes a remainder from the division of the two operands.
     * @param x the other Float
     * @return the remainder from dividing this Float by the other Float.
     */
    @Native("java", "((#this) % (#x))")
    @Native("c++",  "::x10::lang::FloatNatives::mod(#this, #x)")
    public native operator this % (x:Float): Float;

    /**
     * A unary plus operator.
     * A no-op.
     * @return the value of this Float.
     */
    @Native("java", "(+(#this))")
    @Native("c++",  "(+(#this))")
    public native operator + this: Float;

    /**
     * A unary minus operator.
     * Negates the operand.
     * @return the negated value of this Float.
     */
    @Native("java", "(-(#this))")
    @Native("c++",  "(-(#this))")
    public native operator - this: Float;


    /**
     * Coerce a given Byte to a Float.
     * @param x the given Byte
     * @return the given Byte converted to a Float.
     */
    @Native("java", "((float)(byte)(#x))")
    @Native("c++",  "((x10_float) (#x))")
    public native static operator (x:Byte): Float;

    /**
     * Coerce a given Short to a Float.
     * @param x the given Short
     * @return the given Short converted to a Float.
     */
    @Native("java", "((float)(short)(#x))")
    @Native("c++",  "((x10_float) (#x))")
    public native static operator (x:Short): Float;

    /**
     * Coerce a given Int to a Float.
     * @param x the given Int
     * @return the given Int converted to a Float.
     */
    @Native("java", "((float)(int)(#x))")
    @Native("c++",  "((x10_float) (#x))")
    public native static operator (x:Int): Float;

    /**
     * Coerce a given Long to a Float.
     * @param x the given Long
     * @return the given Long converted to a Float.
     */
    @Native("java", "((float)(long)(#x))")
    @Native("c++",  "((x10_float) (#x))")
    public native static operator (x:Long): Float;

    /**
     * Coerce a given UByte to a Float.
     * @param x the given UByte
     * @return the given UByte converted to a Float.
     */
    @Native("java", "((float)((int)(#x)&0xff))")
    @Native("c++",  "((x10_float) (#x))")
    public native static operator (x:UByte): Float;

    /**
     * Coerce a given UShort to a Float.
     * @param x the given UShort
     * @return the given UShort converted to a Float.
     */
    @Native("java", "((float)((int)(#x)&0xffff))")
    @Native("c++",  "((x10_float) (#x))")
    public native static operator (x:UShort): Float;

    /**
     * Coerce a given UInt to a Float.
     * @param x the given UInt
     * @return the given UInt converted to a Float.
     */
    @Native("java", "((float)(((long)#x)&0xffffffffL))")
    @Native("c++",  "((x10_float) (#x))")
    public native static operator (x:UInt): Float;

    /**
     * Coerce a given ULong to a Float.
     * @param x the given ULong
     * @return the given ULong converted to a Float.
     */
    @Native("java", "(x10.runtime.impl.java.ULongUtils.toFloat(#x))")
    @Native("c++",  "((x10_float) (#x))")
    public native static operator (x:ULong): Float;

    /**
     * Convert a given Double to a Float.
     * @param x the given Double
     * @return the given Double converted to a Float.
     */
    @Native("java", "((float)(double)(#x))")
    @Native("c++",  "((x10_float) (#x))")
    public native static operator (x:Double) as Float;


    /**
     * A constant holding the smallest positive nonzero value of type Float,
     * 2<sup>-149</sup>.
     * It is equal to Float.fromIntBits(0x1n).
     */
    // TODO: hexadecimal floating-point literal 0x0.000002P-126f
    @Native("java", "java.lang.Float.MIN_VALUE")
    @Native("c++", "::x10::lang::FloatNatives::fromIntBits(0x00000001)")
    @Native("cuda", "1.401298464E-45")
    public static MIN_VALUE: Float = Float.fromIntBits(0x00000001n);

    /**
     * A constant holding the smallest positive normal value of type Float,
     * 2<sup>-126</sup>.
     * It is equal to Float.fromIntBits(0x00800000n).
     */
    // TODO: hexadecimal floating-point literal 0x1.0P-126f
    @Native("java", "java.lang.Float.intBitsToFloat(0x00800000)")
    @Native("c++", "::x10::lang::FloatNatives::fromIntBits(0x00800000)")
    @Native("cuda", "FLT_MIN")
    public static MIN_NORMAL: Float = Float.fromIntBits(0x00800000n);

    /**
     * A constant holding the largest positive finite value of type Float,
     * (2-2<sup>-23</sup>)*2<sup>127</sup>.
     * It is equal to Float.fromIntBits(0x7f7fffffn).
     */
    // TODO: hexadecimal floating-point literal 0x1.fffffeP+127f
    @Native("java", "java.lang.Float.MAX_VALUE")
    @Native("c++", "::x10::lang::FloatNatives::fromIntBits(0x7f7fffff)")
    @Native("cuda", "FLT_MAX")
    public static MAX_VALUE: Float = Float.fromIntBits(0x7f7fffffn);

    /**
     * A constant holding the positive infinity of type Float.
     * It is equal to Float.fromIntBits(0x7f800000n).
     */
    @Native("java", "java.lang.Float.POSITIVE_INFINITY")
    @Native("c++", "::x10::lang::FloatNatives::fromIntBits(0x7f800000)")
    public static POSITIVE_INFINITY: Float = Float.fromIntBits(0x7f800000n);

    /**
     * A constant holding the negative infinity of type Float.
     * It is equal to Float.fromIntBits(0xff800000n).
     */
    @Native("java", "java.lang.Float.NEGATIVE_INFINITY")
    @Native("c++", "::x10::lang::FloatNatives::fromIntBits(0xff800000)")
    public static NEGATIVE_INFINITY: Float = Float.fromIntBits(0xff800000n);

    /**
     * A constant holding a Not-a-Number (NaN) value of type Float.
     * It is equal to Float.fromIntBits(0x7fc00000n).
     */
    @Native("java", "java.lang.Float.NaN")
    @Native("c++", "::x10::lang::FloatNatives::fromIntBits(0x7fc00000)")
    public static NaN: Float = Float.fromIntBits(0x7fc00000n);


    /**
     * Returns a hexadecimal String representation of this Float.
     * @return a hex String representation of this Float.
     */
    @Native("java", "java.lang.Float.toHexString(#this)")
    @Native("c++", "::x10::lang::FloatNatives::toHexString(#this)")
    public native def toHexString(): String;

    /**
     * Returns a String representation of this Float.
     * The result has enough significant digits to uniquely distinguish it from
     * adjacent Float values.
     * @return a String representation of this Float.
     */
    @Native("java", "java.lang.Float.toString(#this)")
    @Native("c++", "::x10aux::to_string(#this)")
    public native def toString(): String;


    /**
     * @deprecated use {@link #parse(String)} instead
     */
    @Native("java", "java.lang.Float.parseFloat(#s)")
    @Native("c++", "::x10::lang::FloatNatives::parseFloat(#s)")
    public native static def parseFloat(s:String): Float; //throwsNumberFormatException;

    /**
     * Parses the String argument as a Float value.
     * @param s the String containing the Float representation to be parsed
     * @return the Float represented by the String argument.
     * @throws NumberFormatException if the String does not contain a parsable Float.
     */
    @Native("java", "java.lang.Float.parseFloat(#s)")
    @Native("c++", "::x10::lang::FloatNatives::parseFloat(#s)")
    public native static def parse(s:String): Float; //throwsNumberFormatException;


    /**
     * Returns true if this Float is a Not-a-Number (NaN), false otherwise.
     * @return true if this Float is NaN; false otherwise.
     */
    @Native("java", "java.lang.Float.isNaN(#this)")
    @Native("c++", "::x10::lang::FloatNatives::isNaN(#this)")
    public native def isNaN(): Boolean;

    /**
     * Returns true if this Float is infinitely large in magnitude, false otherwise.
     * @return true if this Float is positive infinity or negative infinity; false otherwise.
     */
    @Native("java", "java.lang.Float.isInfinite(#this)")
    @Native("c++", "::x10::lang::FloatNatives::isInfinite(#this)")
    public native def isInfinite(): Boolean;


    /**
     * Returns a representation of this Float according to the IEEE 754
     * floating-point "single format" bit layout, collapsing all NaN
     * values to a single "canonical" value.
     * The result is an Int that, when given to the {@link #fromIntBits(Int)}
     * method, will produce a Float value the same as this Float (except
     * all NaN values are collapsed to a single "canonical" NaN value).
     * @return the bits that represent this Float.
     */
    @Native("java", "java.lang.Float.floatToIntBits(#this)")
    @Native("c++", "::x10::lang::FloatNatives::toIntBits(#this)")
    public native def toIntBits(): Int;

    /**
     * Returns a representation of this Float according to the IEEE 754
     * floating-point "single format" bit layout, preserving NaN values.
     * The result is an Int that, when given to the {@link #fromIntBits(Int)}
     * method, will produce a Float value the same as this Float.
     * @return the bits that represent this Float.
     */
    @Native("java", "java.lang.Float.floatToRawIntBits(#this)")
    @Native("c++", "::x10::lang::FloatNatives::toRawIntBits(#this)")
    public native def toRawIntBits(): Int;

    /**
     * Returns the Float corresponding to a given bit representation.
     * The argument is considered to be a representation of a floating-point
     * value according to the IEEE 754 floating-point "single format" bit
     * layout.
     * The NaN values constructed by this method are not distinguishable
     * by any operation provided by X10 except for the {@link #toRawIntBits()}
     * method.<br>
     * Note that on some architectures, it may not be possible to return
     * certain patterns of NaN from this method, so for some Int values
     * <code>Float.fromIntBits(start).toRawIntBits()</code> may not equal
     * start.
     * @param bits an Int representing the floating-point value
     * @return the Float with the same bit pattern.
     */
    @Native("java", "java.lang.Float.intBitsToFloat(#x)")
    @Native("c++", "::x10::lang::FloatNatives::fromIntBits(#x)")
    public static native def fromIntBits(x:Int): Float;


    /**
     * Return true if the given entity is a Float, and this Float is equal
     * to the given entity.
     * @param x the given entity
     * @return true if this Float is equal to the given entity.
     */
    @Native("java", "x10.rtt.Equality.equalsequals(#this, #x)")
    @Native("c++", "::x10aux::equals(#this,#x)")
    public native def equals(x:Any):Boolean;

    /**
     * Returns true if this Float is equal to the given Float.
     * @param x the given Float
     * @return true if this Float is equal to the given Float.
     */
    @Native("java", "x10.rtt.Equality.equalsequals(#this, #x)")
    @Native("c++", "::x10aux::equals(#this,#x)")
    public native def equals(x:Float):Boolean;

    /**
    * Returns a negative Int, zero, or a positive Int if this Float is less than, equal
    * to, or greater than the given Float.
    * @param x the given Float
    * @return a negative Int, zero, or a positive Int if this Float is less than, equal
    * to, or greater than the given Float.
    */
   @Native("java", "x10.rtt.Equality.compareTo(#this, #x)")
   @Native("c++", "::x10::lang::FloatNatives::compareTo(#this, #x)")
   public native def compareTo(x:Float):Int;
}
public type Float(b:Float) = Float{self==b};
