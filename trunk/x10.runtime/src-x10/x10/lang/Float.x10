/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
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
@NativeRep("java", "float", null, "x10.rtt.Type.FLOAT")
@NativeRep("c++", "x10_float", "x10_float", null)
public final struct Float /*TODO implements Arithmetic[Float], Ordered[Float]*/ {
    /**
     * A less-than operator.
     * Compares the given Float with another Float and returns true if the given Float is
     * strictly less than the other Float.
     * Note that NaN values are unordered.
     * @param x the given Float
     * @param y the other Float
     * @return true if the given Float is strictly less than the other Float.
     */
    @Native("java", "((#1) < (#2))")
    @Native("c++",  "((#1) < (#2))")
    public native static safe operator (x:Float) < (y:Float): Boolean;

    /**
     * A greater-than operator.
     * Compares the given Float with another Float and returns true if the given Float is
     * strictly greater than the other Float.
     * Note that NaN values are unordered.
     * @param x the given Float
     * @param y the other Float
     * @return true if the given Float is strictly greater than the other Float.
     */
    @Native("java", "((#1) > (#2))")
    @Native("c++",  "((#1) > (#2))")
    public native static safe operator (x:Float) > (y:Float): Boolean;

    /**
     * A less-than-or-equal-to operator.
     * Compares the given Float with another Float and returns true if the given Float is
     * less than or equal to the other Float.
     * Note that NaN values are unordered.
     * @param x the given Float
     * @param y the other Float
     * @return true if the given Float is less than or equal to the other Float.
     */
    @Native("java", "((#1) <= (#2))")
    @Native("c++",  "((#1) <= (#2))")
    public native static safe operator (x:Float) <= (y:Float): Boolean;

    /**
     * A greater-than-or-equal-to operator.
     * Compares the given Float with another Float and returns true if the given Float is
     * greater than or equal to the other Float.
     * Note that NaN values are unordered.
     * @param x the given Float
     * @param y the other Float
     * @return true if the given Float is greater than or equal to the other Float.
     */
    @Native("java", "((#1) >= (#2))")
    @Native("c++",  "((#1) >= (#2))")
    public native static safe operator (x:Float) >= (y:Float): Boolean;


    /**
     * A binary plus operator.
     * Computes the result of the addition of the two operands.
     * @param x the given Float
     * @param y the other Float
     * @return the sum of the given Float and the other Float.
     */
    @Native("java", "((#1) + (#2))")
    @Native("c++",  "((#1) + (#2))")
    public native static safe operator (x:Float) + (y:Float): Float;

    /**
     * A binary minus operator.
     * Computes the result of the subtraction of the two operands.
     * @param x the given Float
     * @param y the other Float
     * @return the difference of the given Float and the other Float.
     */
    @Native("java", "((#1) - (#2))")
    @Native("c++",  "((#1) - (#2))")
    public native static safe operator (x:Float) - (y:Float): Float;

    /**
     * A binary multiply operator.
     * Computes the result of the multiplication of the two operands.
     * @param x the given Float
     * @param y the other Float
     * @return the product of the given Float and the other Float.
     */
    @Native("java", "((#1) * (#2))")
    @Native("c++",  "((#1) * (#2))")
    public native static safe operator (x:Float) * (y:Float): Float;

    /**
     * A binary divide operator.
     * Computes the result of the division of the two operands.
     * @param x the given Float
     * @param y the other Float
     * @return the quotient of the given Float and the other Float.
     */
    @Native("java", "((#1) / (#2))")
    @Native("c++",  "((#1) / (#2))")
    public native static safe operator (x:Float) / (y:Float): Float;

    /**
     * A binary remainder operator.
     * Computes a remainder from the division of the two operands.
     * @param x the given Float
     * @param y the other Float
     * @return the remainder from dividing the given Float by the other Float.
     */
    @Native("java", "((#1) % (#2))")
    @Native("c++",  "x10aux::mod(#1, #2)")
    public native static safe operator (x:Float) % (y:Float): Float;

    /**
     * A unary plus operator.
     * A no-op.
     * @param x the given Float
     * @return the value of the given Float.
     */
    @Native("java", "(+(#1))")
    @Native("c++",  "(+(#1))")
    public native static safe operator + (x:Float): Float;

    /**
     * A unary minus operator.
     * Negates the operand.
     * @param x the given Float
     * @return the negated value of the given Float.
     */
    @Native("java", "(-(#1))")
    @Native("c++",  "(-(#1))")
    public native static safe operator - (x:Float): Float;


    /**
     * Convert a given Byte to a Float.
     * @param x the given Byte
     * @return the given Byte converted to a Float.
     */
    @Native("java", "((float)(byte)(#1))")
    @Native("c++",  "((x10_float) (#1))")
    public native static safe operator (x:Byte): Float;

    /**
     * Convert a given Short to a Float.
     * @param x the given Short
     * @return the given Short converted to a Float.
     */
    @Native("java", "((float)(short)(#1))")
    @Native("c++",  "((x10_float) (#1))")
    public native static safe operator (x:Short): Float;

    /**
     * Convert a given Int to a Float.
     * @param x the given Int
     * @return the given Int converted to a Float.
     */
    @Native("java", "((float)(int)(#1))")
    @Native("c++",  "((x10_float) (#1))")
    public native static safe operator (x:Int): Float;

    /**
     * Convert a given Long to a Float.
     * @param x the given Long
     * @return the given Long converted to a Float.
     */
    @Native("java", "((float)(long)(#1))")
    @Native("c++",  "((x10_float) (#1))")
    public native static safe operator (x:Long): Float;

    /**
     * Convert a given Double to a Float.
     * @param x the given Double
     * @return the given Double converted to a Float.
     */
    @Native("java", "((float)(double)(#1))")
    @Native("c++",  "((x10_float) (#1))")
    public native static safe operator (x:Double) as Float;


    /**
     * A constant holding the smallest positive nonzero value of type Float,
     * 2<sup>-149</sup>.
     * It is equal to Float.fromIntBits(0x1).
     */
    // TODO: hexadecimal floating-point literal 0x0.000002P-126f
    @Native("java", "java.lang.Float.MIN_VALUE")
    @Native("c++", "x10aux::float_utils::fromIntBits(0x00000001)")
    @Native("cuda", "1.401298464E-45")
    public const MIN_VALUE: Float = Float.fromIntBits(0x00000001);

    /**
     * A constant holding the smallest positive normal value of type Float,
     * 2<sup>-126</sup>.
     * It is equal to Float.fromIntBits(0x00800000).
     */
    // TODO: hexadecimal floating-point literal 0x1.0P-126f
    @Native("java", "java.lang.Float.intBitsToFloat(0x00800000)")
    @Native("c++", "x10aux::float_utils::fromIntBits(0x00800000)")
    @Native("cuda", "FLT_MIN")
    public const MIN_NORMAL: Float = Float.fromIntBits(0x00800000);

    /**
     * A constant holding the largest positive finite value of type Float,
     * (2-2<sup>-23</sup>)*2<sup>127</sup>.
     * It is equal to Float.fromIntBits(0x7f7fffff).
     */
    // TODO: hexadecimal floating-point literal 0x1.fffffeP+127f
    @Native("java", "java.lang.Float.MAX_VALUE")
    @Native("c++", "x10aux::float_utils::fromIntBits(0x7f7fffff)")
    @Native("cuda", "FLT_MAX")
    public const MAX_VALUE: Float = Float.fromIntBits(0x7f7fffff);

    /**
     * A constant holding the positive infinity of type Float.
     * It is equal to Float.fromIntBits(0x7f800000).
     */
    @Native("java", "java.lang.Float.POSITIVE_INFINITY")
    @Native("c++", "x10aux::float_utils::fromIntBits(0x7f800000)")
    public const POSITIVE_INFINITY: Float = Float.fromIntBits(0x7f800000);

    /**
     * A constant holding the negative infinity of type Float.
     * It is equal to Float.fromIntBits(0xff800000).
     */
    @Native("java", "java.lang.Float.NEGATIVE_INFINITY")
    @Native("c++", "x10aux::float_utils::fromIntBits(0xff800000)")
    public const NEGATIVE_INFINITY: Float = Float.fromIntBits(0xff800000);

    /**
     * A constant holding a Not-a-Number (NaN) value of type Float.
     * It is equal to Float.fromIntBits(0x7fc00000).
     */
    @Native("java", "java.lang.Float.NaN")
    @Native("c++", "x10aux::float_utils::fromIntBits(0x7fc00000)")
    public const NaN: Float = Float.fromIntBits(0x7fc00000);


    /**
     * Returns a hexadecimal String representation of this Float.
     * @return a hex String representation of this Float.
     */
    @Native("java", "java.lang.Float.toHexString(#0)")
    @Native("c++", "x10aux::float_utils::toHexString(#0)")
    public native def toHexString(): String;

    /**
     * Returns a String representation of this Float.
     * The result has enough significant digits to uniquely distinguish it from
     * adjacent Float values.
     * @return a String representation of this Float.
     */
    @Native("java", "java.lang.Float.toString(#0)")
    @Native("c++", "x10aux::to_string(#0)")
    public global safe native def toString(): String;


    /**
     * @deprecated use {@link #parse(String)} instead
     */
    @Native("java", "java.lang.Float.parseFloat(#1)")
    @Native("c++", "x10aux::float_utils::parseFloat(#1)")
    public native static def parseFloat(String): Float throws NumberFormatException;

    /**
     * Parses the String argument as a Float value.
     * @param s the String containing the Float representation to be parsed
     * @return the Float represented by the String argument.
     * @throws NumberFormatException if the String does not contain a parsable Float.
     */
    @Native("java", "java.lang.Float.parseFloat(#1)")
    @Native("c++", "x10aux::float_utils::parseFloat(#1)")
    public native static def parse(s:String): Float throws NumberFormatException;


    /**
     * Returns true if this Float is a Not-a-Number (NaN), false otherwise.
     * @return true if this Float is NaN; false otherwise.
     */
    @Native("java", "java.lang.Float.isNaN(#0)")
    @Native("c++", "x10aux::float_utils::isNaN(#0)")
    public native def isNaN(): boolean;

    /**
     * Returns true if this Float is infinitely large in magnitude, false otherwise.
     * @return true if this Float is positive infinity or negative infinity; false otherwise.
     */
    @Native("java", "java.lang.Float.isInfinite(#0)")
    @Native("c++", "x10aux::float_utils::isInfinite(#0)")
    public native def isInfinite(): boolean;


    /**
     * Returns a representation of this Float according to the IEEE 754
     * floating-point "single format" bit layout, collapsing all NaN
     * values to a single "canonical" value.
     * The result is an Int that, when given to the {@link #fromIntBits(Int)}
     * method, will produce a Float value the same as this Float (except
     * all NaN values are collapsed to a single "canonical" NaN value).
     * @return the bits that represent this Float.
     */
    @Native("java", "java.lang.Float.floatToIntBits(#0)")
    @Native("c++", "x10aux::float_utils::toIntBits(#0)")
    public native def toIntBits(): Int;

    /**
     * Returns a representation of this Float according to the IEEE 754
     * floating-point "single format" bit layout, preserving NaN values.
     * The result is an Int that, when given to the {@link #fromIntBits(Int)}
     * method, will produce a Float value the same as this Float.
     * @return the bits that represent this Float.
     */
    @Native("java", "java.lang.Float.floatToRawIntBits(#0)")
    @Native("c++", "x10aux::float_utils::toRawIntBits(#0)")
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
    @Native("java", "java.lang.Float.intBitsToFloat(#1)")
    @Native("c++", "x10aux::float_utils::fromIntBits(#1)")
    public static native def fromIntBits(Int): Float;


    /**
     * Return true if the given entity is a Float, and this Float is equal
     * to the given entity.
     * @param x the given entity
     * @return true if this Float is equal to the given entity.
     */
    @Native("java", "x10.rtt.Equality.equalsequals(#0, #1)")
    @Native("c++", "x10aux::equals(#0,#1)")
    public global safe native def equals(x:Any):Boolean;

    /**
     * Returns true if this Float is equal to the given Float.
     * @param x the given Float
     * @return true if this Float is equal to the given Float.
     */
    @Native("java", "x10.rtt.Equality.equalsequals(#0, #1)")
    @Native("c++", "x10aux::equals(#0,#1)")
    public global safe native def equals(x:Float):Boolean;
}
