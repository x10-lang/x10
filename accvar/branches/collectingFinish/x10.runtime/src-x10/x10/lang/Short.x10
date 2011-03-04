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
 * Short is a 16-bit signed two's complement integral data type, with
 * values ranging from -32768 to 32767, inclusive.  All of the normal
 * arithmetic and bitwise operations are defined on Short, and Short
 * is closed under those operations.  There are also static methods
 * that define conversions from other data types, including String,
 * as well as some Short constants.
 */
@NativeRep("java", "short", null, "x10.rtt.Type.SHORT")
@NativeRep("c++", "x10_short", "x10_short", null)
public final struct Short /*TODO implements Arithmetic[Short], Bitwise[Short], Ordered[Short]*/ {
    /**
     * A less-than operator.
     * Compares the given Short with another Short and returns true if the given Short is
     * strictly less than the other Short.
     * @param x the given Short
     * @param y the other Short
     * @return true if the given Short is strictly less than the other Short.
     */
    @Native("java", "((#1) < (#2))")
    @Native("c++",  "((#1) < (#2))")
    public native static safe operator (x:Short) < (y:Short): Boolean;

    /**
     * A greater-than operator.
     * Compares the given Short with another Short and returns true if the given Short is
     * strictly greater than the other Short.
     * @param x the given Short
     * @param y the other Short
     * @return true if the given Short is strictly greater than the other Short.
     */
    @Native("java", "((#1) > (#2))")
    @Native("c++",  "((#1) > (#2))")
    public native static safe operator (x:Short) > (y:Short): Boolean;

    /**
     * A less-than-or-equal-to operator.
     * Compares the given Short with another Short and returns true if the given Short is
     * less than or equal to the other Short.
     * @param x the given Short
     * @param y the other Short
     * @return true if the given Short is less than or equal to the other Short.
     */
    @Native("java", "((#1) <= (#2))")
    @Native("c++",  "((#1) <= (#2))")
    public native static safe operator (x:Short) <= (y:Short): Boolean;

    /**
     * A greater-than-or-equal-to operator.
     * Compares the given Short with another Short and returns true if the given Short is
     * greater than or equal to the other Short.
     * @param x the given Short
     * @param y the other Short
     * @return true if the given Short is greater than or equal to the other Short.
     */
    @Native("java", "((#1) >= (#2))")
    @Native("c++",  "((#1) >= (#2))")
    public native static safe operator (x:Short) >= (y:Short): Boolean;


    /**
     * A binary plus operator.
     * Computes the result of the addition of the two operands.
     * Overflows result in truncating the high bits.
     * @param x the given Short
     * @param y the other Short
     * @return the sum of the given Short and the other Short.
     */
    @Native("java", "((short) ((#1) + (#2)))")
    @Native("c++",  "((x10_short) ((#1) + (#2)))")
    public native static safe operator (x:Short) + (y:Short): Short;

    /**
     * A binary minus operator.
     * Computes the result of the subtraction of the two operands.
     * Overflows result in truncating the high bits.
     * @param x the given Short
     * @param y the other Short
     * @return the difference of the given Short and the other Short.
     */
    @Native("java", "((short) ((#1) - (#2)))")
    @Native("c++",  "((x10_short) ((#1) - (#2)))")
    public native static safe operator (x:Short) - (y:Short): Short;

    /**
     * A binary multiply operator.
     * Computes the result of the multiplication of the two operands.
     * Overflows result in truncating the high bits.
     * @param x the given Short
     * @param y the other Short
     * @return the product of the given Short and the other Short.
     */
    @Native("java", "((short) ((#1) * (#2)))")
    @Native("c++",  "((x10_short) ((#1) * (#2)))")
    public native static safe operator (x:Short) * (y:Short): Short;

    /**
     * A binary divide operator.
     * Computes the result of the division of the two operands.
     * @param x the given Short
     * @param y the other Short
     * @return the quotient of the given Short and the other Short.
     */
    @Native("java", "((short) ((#1) / (#2)))")
    @Native("c++",  "((x10_short) ((#1) / (#2)))")
    public native static safe operator (x:Short) / (y:Short): Short;

    /**
     * A binary remainder operator.
     * Computes a remainder from the division of the two operands.
     * @param x the given Short
     * @param y the other Short
     * @return the remainder from dividing the given Short by the other Short.
     */
    @Native("java", "((short) ((#1) % (#2)))")
    @Native("c++",  "((x10_short) ((#1) % (#2)))")
    public native static safe operator (x:Short) % (y:Short): Short;

    /**
     * A unary plus operator.
     * A no-op.
     * @param x the given Short
     * @return the value of the given Short.
     */
    @Native("java", "((short) +(#1))")
    @Native("c++",  "((x10_short) +(#1))")
    public native static safe operator + (x:Short): Short;

    /**
     * A unary minus operator.
     * Negates the operand.
     * Overflows result in truncating the high bits.
     * @param x the given Short
     * @return the negated value of the given Short.
     */
    @Native("java", "((short) -(#1))")
    @Native("c++",  "((x10_short) -(#1))")
    public native static safe operator - (x:Short): Short;


    /**
     * A bitwise and operator.
     * Computes a bitwise AND of the two operands.
     * @param x the given Short
     * @param y the other Short
     * @return the bitwise AND of the given Short and the other Short.
     */
    @Native("java", "((short) ((#1) & (#2)))")
    @Native("c++",  "((x10_short) ((#1) & (#2)))")
    public native static safe operator (x:Short) & (y:Short): Short;

    /**
     * A bitwise or operator.
     * Computes a bitwise OR of the two operands.
     * @param x the given Short
     * @param y the other Short
     * @return the bitwise OR of the given Short and the other Short.
     */
    @Native("java", "((short) ((#1) | (#2)))")
    @Native("c++",  "((x10_short) ((#1) | (#2)))")
    public native static safe operator (x:Short) | (y:Short): Short;

    /**
     * A bitwise xor operator.
     * Computes a bitwise XOR of the two operands.
     * @param x the given Short
     * @param y the other Short
     * @return the bitwise XOR of the given Short and the other Short.
     */
    @Native("java", "((short) ((#1) ^ (#2)))")
    @Native("c++",  "((x10_short) ((#1) ^ (#2)))")
    public native static safe operator (x:Short) ^ (y:Short): Short;

    /**
     * A bitwise left shift operator.
     * Computes the value of the left-hand operand shifted left by the value of the right-hand operand.
     * If the right-hand operand is negative, the results are undefined.
     * @param x the given Short
     * @param count the shift count
     * @return the given Short shifted left by count.
     */
    @Native("java", "((short) ((#1) << (#2)))")
    @Native("c++",  "((x10_short) ((#1) << (#2)))")
    public native static safe operator (x:Short) << (count:Int): Short;

    /**
     * A bitwise right shift operator.
     * Computes the value of the left-hand operand shifted right by the value of the right-hand operand,
     * replicating the sign bit into the high bits.
     * If the right-hand operand is negative, the results are undefined.
     * @param x the given Short
     * @param count the shift count
     * @return the given Short shifted right by count.
     */
    @Native("java", "((short) ((#1) >> (#2)))")
    @Native("c++",  "((x10_short) ((#1) >> (#2)))")
    public native static safe operator (x:Short) >> (count:Int): Short;

    /**
     * A bitwise logical right shift operator (zero-fill).
     * Computes the value of the left-hand operand shifted right by the value of the right-hand operand,
     * filling the high bits with zeros.
     * If the right-hand operand is negative, the results are undefined.
     * @deprecated use the right-shift operator and unsigned conversions instead.
     * @param x the given Short
     * @param count the shift count
     * @return the given Short shifted right by count with high bits zero-filled.
     */
    @Native("java", "((short) ((#1) >>> (#2)))")
    @Native("c++",  "((x10_short) ((x10_uint) (#1) >> (#2)))")
    public native static safe operator (x:Short) >>> (count:Int): Short;

    /**
     * A bitwise complement operator.
     * Computes a bitwise complement (NOT) of the operand.
     * @param x the given Short
     * @return the bitwise complement of the given Short.
     */
    @Native("java", "((short) ~(#1))")
    @Native("c++",  "((x10_short) ~(#1))")
    public native static safe operator ~ (x:Short): Short;


    /**
     * Coerce a given Byte to a Short.
     * @param x the given Byte
     * @return the given Byte converted to a Short.
     */
    @Native("java", "((short) (#1))")
    @Native("c++",  "((x10_short) (#1))")
    public native static safe operator (x:Byte): Short;

    /**
     * Convert a given Int to a Short.
     * @param x the given Int
     * @return the given Int converted to a Short.
     */
    @Native("java", "((short)(int)(#1))")
    @Native("c++",  "((x10_short) (#1))")
    public native static safe operator (x:Int) as Short;

    /**
     * Convert a given Long to a Short.
     * @param x the given Long
     * @return the given Long converted to a Short.
     */
    @Native("java", "((short)(long)(#1))")
    @Native("c++",  "((x10_short) (#1))")
    public native static safe operator (x:Long) as Short;

    /**
     * Convert a given Float to a Short.
     * @param x the given Float
     * @return the given Float converted to a Short.
     */
    @Native("java", "x10.core.Floats.toShort(#1)")
    @Native("c++",  "((x10_short) (#1))")
    public native static safe operator (x:Float) as Short;

    /**
     * Convert a given Double to a Short.
     * @param x the given Double
     * @return the given Double converted to a Short.
     */
    @Native("java", "x10.core.Floats.toShort(#1)")
    @Native("c++",  "((x10_short) (#1))")
    public native static safe operator (x:Double) as Short;

    /**
     * Coerce a given UShort to a Short.
     * @param x the given UShort
     * @return the given UShort converted to a Short.
     */
    @Native("java", "((short)(#1))")
    @Native("c++",  "((x10_short) (#1))")
    public native static safe operator (x:UShort): Short;


    /**
     * A constant holding the minimum value a Short can have, -2<sup>15</sup>.
     */
    @Native("java", "java.lang.Short.MIN_VALUE")
    @Native("c++", "((x10_short)0x8000)")
    public const MIN_VALUE = 0x8000 as Short;

    /**
     * A constant holding the maximum value a Short can have, 2<sup>15</sup>-1.
     */
    @Native("java", "java.lang.Short.MAX_VALUE")
    @Native("c++", "((x10_short)0x7fff)")
    public const MAX_VALUE = 0x7fff as Short;


    /**
     * Returns a String representation of this Short in the specified radix.
     * @param radix the radix to use in the String representation
     * @return a String representation of this Short in the specified radix.
     */
    @Native("java", "java.lang.Integer.toString(#0, #1)")
    @Native("c++", "x10aux::short_utils::toString(#0, #1)")
    public global safe native def toString(radix:Int): String;

    /**
     * Returns a String representation of this Short as a hexadecimal number.
     * @return a String representation of this Short as a hexadecimal number.
     */
    @Native("java", "java.lang.Integer.toHexString(#0)")
    @Native("c++", "x10aux::short_utils::toHexString(#0)")
    public global safe native def toHexString(): String;

    /**
     * Returns a String representation of this Short as an octal number.
     * @return a String representation of this Short as an octal number.
     */
    @Native("java", "java.lang.Integer.toOctalString(#0)")
    @Native("c++", "x10aux::short_utils::toOctalString(#0)")
    public global safe native def toOctalString(): String;

    /**
     * Returns a String representation of this Short as a binary number.
     * @return a String representation of this Short as a binary number.
     */
    @Native("java", "java.lang.Integer.toBinaryString(#0)")
    @Native("c++", "x10aux::short_utils::toBinaryString(#0)")
    public global safe native def toBinaryString(): String;

    /**
     * Returns a String representation of this Short as a decimal number.
     * @return a String representation of this Short as a decimal number.
     */
    @Native("java", "java.lang.Short.toString(#0)")
    @Native("c++", "x10aux::to_string(#0)")
    public global safe native def toString(): String;

    /**
     * @deprecated use {@link #parse(String,Int)} instead
     */
    @Native("java", "java.lang.Short.parseShort(#1, #2)")
    @Native("c++", "x10aux::short_utils::parseShort(#1, #2)")
    public native static def parseShort(String, radix:Int): Short throws NumberFormatException;

    /**
     * @deprecated use {@link #parse(String)} instead
     */
    @Native("java", "java.lang.Short.parseShort(#1)")
    @Native("c++", "x10aux::short_utils::parseShort(#1)")
    public native static def parseShort(String): Short throws NumberFormatException;

    /**
     * Parses the String argument as a Short in the radix specified by the second argument.
     * @param s the String containing the Short representation to be parsed
     * @param radix the radix to be used while parsing s
     * @return the Short represented by the String argument in the specified radix.
     * @throws NumberFormatException if the String does not contain a parsable Short.
     */
    @Native("java", "java.lang.Short.parseShort(#1, #2)")
    @Native("c++", "x10aux::short_utils::parseShort(#1, #2)")
    public native static def parse(s:String, radix:Int): Short throws NumberFormatException;

    /**
     * Parses the String argument as a decimal Short.
     * @param s the String containing the Short representation to be parsed
     * @return the Short represented by the String argument.
     * @throws NumberFormatException if the String does not contain a parsable Short.
     */
    @Native("java", "java.lang.Short.parseShort(#1)")
    @Native("c++", "x10aux::short_utils::parseShort(#1)")
    public native static def parse(s:String): Short throws NumberFormatException;


    /**
     * Returns the value obtained by reversing the order of the bits in the
     * two's complement binary representation of this Int.
     * @return the value obtained by reversing order of the bits in this Int.
     */
    @Native("java", "((short)(java.lang.Integer.reverse(#0)>>16))")
    @Native("c++", "((x10_short)(x10aux::int_utils::reverse(#0)>>16))")
    public native def reverse(): Short;

    /**
     * Returns the signum function of this Short.  The return value is -1 if
     * this Short is negative; 0 if this Short is zero; and 1 if this Short is
     * positive.
     * @return the signum function of this Short.
     */
    @Native("java", "java.lang.Integer.signum(#0)")
    @Native("c++", "x10aux::int_utils::signum((x10_int)#0)")
    public native def signum(): Int;

    /**
     * Returns the value obtained by reversing the order of the bytes in the
     * two's complement representation of this Short.
     * @return the value obtained by reversing (or, equivalently, swapping) the bytes in this Short.
     */
    @Native("java", "java.lang.Short.reverseBytes(#0)")
    @Native("c++", "x10aux::short_utils::reverseBytes(#0)")
    public native def reverseBytes(): Short;


    /**
     * Return true if the given entity is a Short, and this Short is equal
     * to the given entity.
     * @param x the given entity
     * @return true if this Short is equal to the given entity.
     */
    @Native("java", "x10.rtt.Equality.equalsequals(#0, #1)")
    @Native("c++", "x10aux::equals(#0,#1)")
    public global safe native def equals(x:Any):Boolean;

    /**
     * Returns true if this Short is equal to the given Short.
     * @param x the given Short
     * @return true if this Short is equal to the given Short.
     */
    @Native("java", "x10.rtt.Equality.equalsequals(#0, #1)")
    @Native("c++", "x10aux::equals(#0,#1)")
    public global safe native def equals(x:Short):Boolean;
}
