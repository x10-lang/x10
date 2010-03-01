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
 * UByte is an 8-bit unsigned integral data type, with
 * values ranging from 0 to 255, inclusive.  All of the normal
 * arithmetic and bitwise operations are defined on UByte, and UByte
 * is closed under those operations.  There are also static methods
 * that define conversions from other data types, including String,
 * as well as some UByte constants.
 */
@NativeRep("java", "byte", null, "x10.rtt.Types.UBYTE")
@NativeRep("c++", "x10_ubyte", "x10_ubyte", null)
public final struct UByte /*TODO implements Arithmetic[UByte], Bitwise[UByte], Ordered[UByte]*/ {
    /**
     * A less-than operator.
     * Compares the given UByte with another UByte and returns true if the given UByte is
     * strictly less than the other UByte.
     * @param x the given UByte
     * @param y the other UByte
     * @return true if the given UByte is strictly less than the other UByte.
     */
    @Native("java", "x10.core.Unsigned.lt(#1, #2)")
    @Native("c++",  "((#1) < (#2))")
    public native static safe operator (x:UByte) < (y:UByte): Boolean;

    /**
     * A greater-than operator.
     * Compares the given UByte with another UByte and returns true if the given UByte is
     * strictly greater than the other UByte.
     * @param x the given UByte
     * @param y the other UByte
     * @return true if the given UByte is strictly greater than the other UByte.
     */
    @Native("java", "x10.core.Unsigned.gt(#1, #2)")
    @Native("c++",  "((#1) > (#2))")
    public native static safe operator (x:UByte) > (y:UByte): Boolean;

    /**
     * A less-than-or-equal-to operator.
     * Compares the given UByte with another UByte and returns true if the given UByte is
     * less than or equal to the other UByte.
     * @param x the given UByte
     * @param y the other UByte
     * @return true if the given UByte is less than or equal to the other UByte.
     */
    @Native("java", "x10.core.Unsigned.le(#1, #2)")
    @Native("c++",  "((#1) <= (#2))")
    public native static safe operator (x:UByte) <= (y:UByte): Boolean;

    /**
     * A greater-than-or-equal-to operator.
     * Compares the given UByte with another UByte and returns true if the given UByte is
     * greater than or equal to the other UByte.
     * @param x the given UByte
     * @param y the other UByte
     * @return true if the given UByte is greater than or equal to the other UByte.
     */
    @Native("java", "x10.core.Unsigned.ge(#1, #2)")
    @Native("c++",  "((#1) >= (#2))")
    public native static safe operator (x:UByte) >= (y:UByte): Boolean;


    /**
     * A binary plus operator.
     * Computes the result of the addition of the two operands.
     * Overflows result in truncating the high bits.
     * @param x the given UByte
     * @param y the other UByte
     * @return the sum of the given UByte and the other UByte.
     */
    @Native("java", "((byte) ((#1) + (#2)))")
    @Native("c++",  "((x10_ubyte) ((#1) + (#2)))")
    public native static safe operator (x:UByte) + (y:UByte): UByte;

    /**
     * A binary minus operator.
     * Computes the result of the subtraction of the two operands.
     * Overflows result in truncating the high bits.
     * @param x the given UByte
     * @param y the other UByte
     * @return the difference of the given UByte and the other UByte.
     */
    @Native("java", "((byte) ((#1) - (#2)))")
    @Native("c++",  "((x10_ubyte) ((#1) - (#2)))")
    public native static safe operator (x:UByte) - (y:UByte): UByte;

    /**
     * A binary multiply operator.
     * Computes the result of the multiplication of the two operands.
     * Overflows result in truncating the high bits.
     * @param x the given UByte
     * @param y the other UByte
     * @return the product of the given UByte and the other UByte.
     */
    @Native("java", "((byte) ((#1) * (#2)))")
    @Native("c++",  "((x10_ubyte) ((#1) * (#2)))")
    public native static safe operator (x:UByte) * (y:UByte): UByte;

    /**
     * A binary divide operator.
     * Computes the result of the division of the two operands.
     * @param x the given UByte
     * @param y the other UByte
     * @return the quotient of the given UByte and the other UByte.
     */
    @Native("java", "((byte) x10.core.Unsigned.div(#1, #2))")
    @Native("c++",  "((x10_ubyte) ((#1) / (#2)))")
    public native static safe operator (x:UByte) / (y:UByte): UByte;

    /**
     * A binary remainder operator.
     * Computes a remainder from the division of the two operands.
     * @param x the given UByte
     * @param y the other UByte
     * @return the remainder from dividing the given UByte by the other UByte.
     */
    @Native("java", "((byte) x10.core.Unsigned.rem(#1, #2))")
    @Native("c++",  "((x10_ubyte) ((#1) % (#2)))")
    public native static safe operator (x:UByte) % (y:UByte): UByte;

    /**
     * A unary plus operator.
     * A no-op.
     * @param x the given UByte
     * @return the value of the given UByte.
     */
    @Native("java", "((byte) +(#1))")
    @Native("c++",  "((x10_ubyte) +(#1))")
    public native static safe operator + (x:UByte): UByte;

    /**
     * A unary minus operator.
     * Computes the two's complement of the operand.
     * Overflows result in truncating the high bits.
     * @param x the given UByte
     * @return the two's complement of the given UByte.
     */
    @Native("java", "((byte) -(#1))")
    @Native("c++",  "((x10_ubyte) -(#1))")
    public native static safe operator - (x:UByte): UByte;


    /**
     * A bitwise and operator.
     * Computes a bitwise AND of the two operands.
     * @param x the given UByte
     * @param y the other UByte
     * @return the bitwise AND of the given UByte and the other UByte.
     */
    @Native("java", "((byte) ((#1) & (#2)))")
    @Native("c++",  "((x10_ubyte) ((#1) & (#2)))")
    public native static safe operator (x:UByte) & (y:UByte): UByte;

    /**
     * A bitwise or operator.
     * Computes a bitwise OR of the two operands.
     * @param x the given UByte
     * @param y the other UByte
     * @return the bitwise OR of the given UByte and the other UByte.
     */
    @Native("java", "((byte) ((#1) | (#2)))")
    @Native("c++",  "((x10_ubyte) ((#1) | (#2)))")
    public native static safe operator (x:UByte) | (y:UByte): UByte;

    /**
     * A bitwise xor operator.
     * Computes a bitwise XOR of the two operands.
     * @param x the given UByte
     * @param y the other UByte
     * @return the bitwise XOR of the given UByte and the other UByte.
     */
    @Native("java", "((byte) ((#1) ^ (#2)))")
    @Native("c++",  "((x10_ubyte) ((#1) ^ (#2)))")
    public native static safe operator (x:UByte) ^ (y:UByte): UByte;

    /**
     * A bitwise left shift operator.
     * Computes the value of the left-hand operand shifted left by the value of the right-hand operand.
     * If the right-hand operand is negative, the results are undefined.
     * @param x the given UByte
     * @param count the shift count
     * @return the given UByte shifted left by count.
     */
    @Native("java", "((byte) ((#1) << (#2)))")
    @Native("c++",  "((x10_ubyte) ((#1) << (#2)))")
    public native static safe operator (x:UByte) << (count:Int): UByte;

    /**
     * A bitwise right shift operator.
     * Computes the value of the left-hand operand shifted right by the value of the right-hand operand,
     * filling the high bits with zeros.
     * If the right-hand operand is negative, the results are undefined.
     * @param x the given UByte
     * @param count the shift count
     * @return the given UByte shifted right by count.
     */
    @Native("java", "((byte) ((#1) >>> (#2)))")
    @Native("c++",  "((x10_ubyte) ((#1) >> (#2)))")
    public native static safe operator (x:UByte) >> (count:Int): UByte;

    /**
     * A bitwise logical right shift operator (zero-fill).
     * Computes the value of the left-hand operand shifted right by the value of the right-hand operand,
     * filling the high bits with zeros.
     * If the right-hand operand is negative, the results are undefined.
     * @deprecated use the right-shift operator.
     * @param x the given UByte
     * @param count the shift count
     * @return the given UByte shifted right by count with high bits zero-filled.
     */
    @Native("java", "((byte) ((#1) >>> (#2)))")
    @Native("c++",  "((x10_ubyte) ((#1) >> (#2)))")
    public native static safe operator (x:UByte) >>> (count:Int): UByte;

    /**
     * A bitwise complement operator.
     * Computes a bitwise complement (NOT) of the operand.
     * @param x the given UByte
     * @return the bitwise complement of the given UByte.
     */
    @Native("java", "((byte) ~(#1))")
    @Native("c++",  "((x10_ubyte) ~(#1))")
    public native static safe operator ~ (x:UByte): UByte;


    /**
     * Convert a given UShort to a UByte.
     * @param x the given UShort
     * @return the given UShort converted to a UByte.
     */
    @Native("java", "((byte)(short)(#1))")
    @Native("c++",  "((x10_ubyte) (#1))")
    public native static safe operator (x:UShort) as UByte;

    /**
     * Convert a given UInt to a UByte.
     * @param x the given UInt
     * @return the given UInt converted to a UByte.
     */
    @Native("java", "((byte)(int)(#1))")
    @Native("c++",  "((x10_ubyte) (#1))")
    public native static safe operator (x:UInt) as UByte;

    /**
     * Convert a given ULong to a UByte.
     * @param x the given ULong
     * @return the given ULong converted to a UByte.
     */
    @Native("java", "((byte)(long)(#1))")
    @Native("c++",  "((x10_ubyte) (#1))")
    public native static safe operator (x:ULong) as UByte;


    /**
     * Convert a given Short to a UByte.
     * @param x the given Short
     * @return the given Short converted to a UByte.
     */
    @Native("java", "((byte)(short)(#1))")
    @Native("c++",  "((x10_ubyte) (#1))")
    public native static safe operator (x:Short) as UByte;

    /**
     * Convert a given Int to a UByte.
     * @param x the given Int
     * @return the given Int converted to a UByte.
     */
    @Native("java", "((byte)(int)(#1))")
    @Native("c++",  "((x10_ubyte) (#1))")
    public native static safe operator (x:Int) as UByte;

    /**
     * Convert a given Long to a UByte.
     * @param x the given Long
     * @return the given Long converted to a UByte.
     */
    @Native("java", "((byte)(long)(#1))")
    @Native("c++",  "((x10_ubyte) (#1))")
    public native static safe operator (x:Long) as UByte;

    /**
     * Convert a given Float to a UByte.
     * @param x the given Float
     * @return the given Float converted to a UByte.
     */
    @Native("java", "((byte)(float)(#1))")
    @Native("c++",  "((x10_ubyte) (#1))")
    public native static safe operator (x:Float) as UByte;

    /**
     * Convert a given Double to a UByte.
     * @param x the given Double
     * @return the given Double converted to a UByte.
     */
    @Native("java", "((byte)(double)(#1))")
    @Native("c++",  "((x10_ubyte) (#1))")
    public native static safe operator (x:Double) as UByte;

    /**
     * Convert a given Byte to a UByte.
     * @param x the given Byte
     * @return the given Byte converted to a UByte.
     */
    @Native("java", "((byte)(#1))")
    @Native("c++",  "((x10_ubyte) (#1))")
    public native static safe operator (x:Byte) as UByte;


    /**
     * A constant holding the minimum value a UByte can have, 0.
     */
    @Native("java", "0")
    @Native("c++", "0U")
    public const MIN_VALUE = 0 as UByte;

    /**
     * A constant holding the maximum value a UByte can have, 2<sup>8</sup>-1.
     */
    @Native("java", "0xff")
    @Native("c++", "0xffU")
    public const MAX_VALUE = 0xff as UByte;


    /**
     * Returns a String representation of this UByte in the specified radix.
     * @param radix the radix to use in the String representation
     * @return a String representation of this UByte in the specified radix.
     */
    @Native("java", "java.lang.Integer.toString((#0) & 0xff, #1)")
    @Native("c++", "x10aux::int_utils::toString((#0) & 0xff, #1)")
    public global safe native def toString(radix:Int): String;

    /**
     * Returns a String representation of this UByte as a hexadecimal number.
     * @return a String representation of this UByte as a hexadecimal number.
     */
    @Native("java", "java.lang.Integer.toHexString((#0) & 0xff)")
    @Native("c++", "x10aux::int_utils::toHexString((#0) & 0xff)")
    public global safe native def toHexString(): String;

    /**
     * Returns a String representation of this UByte as an octal number.
     * @return a String representation of this UByte as an octal number.
     */
    @Native("java", "java.lang.Integer.toOctalString((#0) & 0xff)")
    @Native("c++", "x10aux::int_utils::toOctalString((#0) & 0xff)")
    public global safe native def toOctalString(): String;

    /**
     * Returns a String representation of this UByte as a binary number.
     * @return a String representation of this UByte as a binary number.
     */
    @Native("java", "java.lang.Integer.toBinaryString((#0) & 0xff)")
    @Native("c++", "x10aux::int_utils::toBinaryString((#0) & 0xff)")
    public global safe native def toBinaryString(): String;

    /**
     * Returns a String representation of this UByte as a decimal number.
     * @return a String representation of this UByte as a decimal number.
     */
    @Native("java", "java.lang.Integer.toString((#0) & 0xff)")
    @Native("c++", "x10aux::to_string(#0)")
    public global safe native def toString(): String;

    /**
     * @deprecated use {@link #parse(String,Int)} instead
     */
    @Native("java", "((byte) (java.lang.Integer.parseInt(#1, #2) & 0xff))")
    @Native("c++", "((x10_ubyte) x10aux::int_utils::parseInt(#1, #2))")
    public native static def parseUByte(String, radix:Int): UByte throws NumberFormatException;

    /**
     * @deprecated use {@link #parse(String)} instead
     */
    @Native("java", "java.lang.Integer.parseInt(#1)")
    @Native("c++", "x10aux::int_utils::parseInt(#1)")
    public native static def parseUByte(String): UByte throws NumberFormatException;

    /**
     * Parses the String argument as a UByte in the radix specified by the second argument.
     * @param s the String containing the UByte representation to be parsed
     * @param radix the radix to be used while parsing s
     * @return the UByte represented by the String argument in the specified radix.
     * @throws NumberFormatException if the String does not contain a parsable UByte.
     */
    @Native("java", "((byte) (java.lang.Integer.parseInt(#1, #2) & 0xff))")
    @Native("c++", "((x10_ubyte) x10aux::int_utils::parseInt(#1, #2))")
    public native static def parse(s:String, radix:Int): UByte throws NumberFormatException;

    /**
     * Parses the String argument as a decimal UByte.
     * @param s the String containing the UByte representation to be parsed
     * @return the UByte represented by the String argument.
     * @throws NumberFormatException if the String does not contain a parsable UByte.
     */
    @Native("java", "java.lang.Integer.parseInt(#1)")
    @Native("c++", "x10aux::int_utils::parseInt(#1)")
    public native static def parse(s:String): UByte throws NumberFormatException;


    /**
     * Returns the value obtained by reversing the order of the bits in the
     * binary representation of this UByte.
     * @return the value obtained by reversing order of the bits in this UByte.
     */
    @Native("java", "((byte)(java.lang.Integer.reverse(#0)>>>24))")
    @Native("c++", "((x10_ubyte)(x10aux::int_utils::reverse(#0)>>24))")
    public native def reverse(): Byte;

    /**
     * Returns the signum function of this UByte.  The return value is 0 if
     * this UByte is zero and 1 if this UByte is non-zero.
     * @return the signum function of this UByte.
     */
    @Native("java", "(((#0)==0) ? 0 : 1)")
    @Native("c++",  "(((#0)==0U) ? 0 : 1)")
    public native def signum(): Int;


    /**
     * Return true if the given entity is a UByte, and this UByte is equal
     * to the given entity.
     * @param x the given entity
     * @return true if this UByte is equal to the given entity.
     */
    @Native("java", "x10.rtt.Equality.equalsequals(#0, #1)")
    @Native("c++", "x10aux::equals(#0,#1)")
    public global safe native def equals(x:Any):Boolean;

    /**
     * Returns true if this UByte is equal to the given UByte.
     * @param x the given UByte
     * @return true if this UByte is equal to the given UByte.
     */
    @Native("java", "x10.rtt.Equality.equalsequals(#0, #1)")
    @Native("c++", "x10aux::equals(#0,#1)")
    public global safe native def equals(x:UByte):Boolean;
}
