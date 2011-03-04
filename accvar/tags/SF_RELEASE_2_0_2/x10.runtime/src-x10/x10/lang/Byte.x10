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
 * Byte is an 8-bit signed two's complement integral data type, with
 * values ranging from -128 to 127, inclusive.  All of the normal
 * arithmetic and bitwise operations are defined on Byte, and Byte
 * is closed under those operations.  There are also static methods
 * that define conversions from other data types, including String,
 * as well as some Byte constants.
 */
@NativeRep("java", "byte", null, "x10.rtt.Type.BYTE")
@NativeRep("c++", "x10_byte", "x10_byte", null)
public final struct Byte /*TODO implements Arithmetic[Byte], Bitwise[Byte], Ordered[Byte]*/ {
    /**
     * A less-than operator.
     * Compares the given Byte with another Byte and returns true if the given Byte is
     * strictly less than the other Byte.
     * @param x the given Byte
     * @param y the other Byte
     * @return true if the given Byte is strictly less than the other Byte.
     */
    @Native("java", "((#1) < (#2))")
    @Native("c++",  "((#1) < (#2))")
    public native static safe operator (x:Byte) < (y:Byte): Boolean;

    /**
     * A greater-than operator.
     * Compares the given Byte with another Byte and returns true if the given Byte is
     * strictly greater than the other Byte.
     * @param x the given Byte
     * @param y the other Byte
     * @return true if the given Byte is strictly greater than the other Byte.
     */
    @Native("java", "((#1) > (#2))")
    @Native("c++",  "((#1) > (#2))")
    public native static safe operator (x:Byte) > (y:Byte): Boolean;

    /**
     * A less-than-or-equal-to operator.
     * Compares the given Byte with another Byte and returns true if the given Byte is
     * less than or equal to the other Byte.
     * @param x the given Byte
     * @param y the other Byte
     * @return true if the given Byte is less than or equal to the other Byte.
     */
    @Native("java", "((#1) <= (#2))")
    @Native("c++",  "((#1) <= (#2))")
    public native static safe operator (x:Byte) <= (y:Byte): Boolean;

    /**
     * A greater-than-or-equal-to operator.
     * Compares the given Byte with another Byte and returns true if the given Byte is
     * greater than or equal to the other Byte.
     * @param x the given Byte
     * @param y the other Byte
     * @return true if the given Byte is greater than or equal to the other Byte.
     */
    @Native("java", "((#1) >= (#2))")
    @Native("c++",  "((#1) >= (#2))")
    public native static safe operator (x:Byte) >= (y:Byte): Boolean;


    /**
     * A binary plus operator.
     * Computes the result of the addition of the two operands.
     * Overflows result in truncating the high bits.
     * @param x the given Byte
     * @param y the other Byte
     * @return the sum of the given Byte and the other Byte.
     */
    @Native("java", "((byte) ((#1) + (#2)))")
    @Native("c++",  "((x10_byte) ((#1) + (#2)))")
    public native static safe operator (x:Byte) + (y:Byte): Byte;

    /**
     * A binary minus operator.
     * Computes the result of the subtraction of the two operands.
     * Overflows result in truncating the high bits.
     * @param x the given Byte
     * @param y the other Byte
     * @return the difference of the given Byte and the other Byte.
     */
    @Native("java", "((byte) ((#1) - (#2)))")
    @Native("c++",  "((x10_byte) ((#1) - (#2)))")
    public native static safe operator (x:Byte) - (y:Byte): Byte;

    /**
     * A binary multiply operator.
     * Computes the result of the multiplication of the two operands.
     * Overflows result in truncating the high bits.
     * @param x the given Byte
     * @param y the other Byte
     * @return the product of the given Byte and the other Byte.
     */
    @Native("java", "((byte) ((#1) * (#2)))")
    @Native("c++",  "((x10_byte) ((#1) * (#2)))")
    public native static safe operator (x:Byte) * (y:Byte): Byte;

    /**
     * A binary divide operator.
     * Computes the result of the division of the two operands.
     * @param x the given Byte
     * @param y the other Byte
     * @return the quotient of the given Byte and the other Byte.
     */
    @Native("java", "((byte) ((#1) / (#2)))")
    @Native("c++",  "((x10_byte) ((#1) / (#2)))")
    public native static safe operator (x:Byte) / (y:Byte): Byte;

    /**
     * A binary remainder operator.
     * Computes a remainder from the division of the two operands.
     * @param x the given Byte
     * @param y the other Byte
     * @return the remainder from dividing the given Byte by the other Byte.
     */
    @Native("java", "((byte) ((#1) % (#2)))")
    @Native("c++",  "((x10_byte) ((#1) % (#2)))")
    public native static safe operator (x:Byte) % (y:Byte): Byte;

    /**
     * A unary plus operator.
     * A no-op.
     * @param x the given Byte
     * @return the value of the given Byte.
     */
    @Native("java", "((byte) +(#1))")
    @Native("c++",  "((x10_byte) +(#1))")
    public native static safe operator + (x:Byte): Byte;

    /**
     * A unary minus operator.
     * Negates the operand.
     * Overflows result in truncating the high bits.
     * @param x the given Byte
     * @return the negated value of the given Byte.
     */
    @Native("java", "((byte) -(#1))")
    @Native("c++",  "((x10_byte) -(#1))")
    public native static safe operator - (x:Byte): Byte;


    /**
     * A bitwise and operator.
     * Computes a bitwise AND of the two operands.
     * @param x the given Byte
     * @param y the other Byte
     * @return the bitwise AND of the given Byte and the other Byte.
     */
    @Native("java", "((byte) ((#1) & (#2)))")
    @Native("c++",  "((x10_byte) ((#1) & (#2)))")
    public native static safe operator (x:Byte) & (y:Byte): Byte;

    /**
     * A bitwise or operator.
     * Computes a bitwise OR of the two operands.
     * @param x the given Byte
     * @param y the other Byte
     * @return the bitwise OR of the given Byte and the other Byte.
     */
    @Native("java", "((byte) ((#1) | (#2)))")
    @Native("c++",  "((x10_byte) ((#1) | (#2)))")
    public native static safe operator (x:Byte) | (y:Byte): Byte;

    /**
     * A bitwise xor operator.
     * Computes a bitwise XOR of the two operands.
     * @param x the given Byte
     * @param y the other Byte
     * @return the bitwise XOR of the given Byte and the other Byte.
     */
    @Native("java", "((byte) ((#1) ^ (#2)))")
    @Native("c++",  "((x10_byte) ((#1) ^ (#2)))")
    public native static safe operator (x:Byte) ^ (y:Byte): Byte;

    /**
     * A bitwise left shift operator.
     * Computes the value of the left-hand operand shifted left by the value of the right-hand operand.
     * If the right-hand operand is negative, the results are undefined.
     * @param x the given Byte
     * @param count the shift count
     * @return the given Byte shifted left by count.
     */
    @Native("java", "((byte) ((#1) << (#2)))")
    @Native("c++",  "((x10_byte) ((#1) << (#2)))")
    public native static safe operator (x:Byte) << (count:Int): Byte;

    /**
     * A bitwise right shift operator.
     * Computes the value of the left-hand operand shifted right by the value of the right-hand operand,
     * replicating the sign bit into the high bits.
     * If the right-hand operand is negative, the results are undefined.
     * @param x the given Byte
     * @param count the shift count
     * @return the given Byte shifted right by count.
     */
    @Native("java", "((byte) ((#1) >> (#2)))")
    @Native("c++",  "((x10_byte) ((#1) >> (#2)))")
    public native static safe operator (x:Byte) >> (count:Int): Byte;

    /**
     * A bitwise logical right shift operator (zero-fill).
     * Computes the value of the left-hand operand shifted right by the value of the right-hand operand,
     * filling the high bits with zeros.
     * If the right-hand operand is negative, the results are undefined.
     * @deprecated use the right-shift operator and unsigned conversions instead.
     * @param x the given Byte
     * @param count the shift count
     * @return the given Byte shifted right by count with high bits zero-filled.
     */
    @Native("java", "((byte) ((#1) >>> (#2)))")
    @Native("c++",  "((x10_byte) ((x10_uint) (#1) >> (#2)))")
    public native static safe operator (x:Byte) >>> (count:Int): Byte;

    /**
     * A bitwise complement operator.
     * Computes a bitwise complement (NOT) of the operand.
     * @param x the given Byte
     * @return the bitwise complement of the given Byte.
     */
    @Native("java", "((byte) ~(#1))")
    @Native("c++",  "((x10_byte) ~(#1))")
    public native static safe operator ~ (x:Byte): Byte;


    /**
     * Convert a given Short to a Byte.
     * @param x the given Short
     * @return the given Short converted to a Byte.
     */
    @Native("java", "((byte)(short)(#1))")
    @Native("c++",  "((x10_byte) (#1))")
    public native static safe operator (x:Short) as Byte;

    /**
     * Convert a given Int to a Byte.
     * @param x the given Int
     * @return the given Int converted to a Byte.
     */
    @Native("java", "((byte)(int)(#1))")
    @Native("c++",  "((x10_byte) (#1))")
    public native static safe operator (x:Int) as Byte;

    /**
     * Convert a given Long to a Byte.
     * @param x the given Long
     * @return the given Long converted to a Byte.
     */
    @Native("java", "((byte)(long)(#1))")
    @Native("c++",  "((x10_byte) (#1))")
    public native static safe operator (x:Long) as Byte;

    /**
     * Convert a given Float to a Byte.
     * @param x the given Float
     * @return the given Float converted to a Byte.
     */
    @Native("java", "((byte)(float)(#1))")
    @Native("c++",  "((x10_byte) (#1))")
    public native static safe operator (x:Float) as Byte;

    /**
     * Convert a given Double to a Byte.
     * @param x the given Double
     * @return the given Double converted to a Byte.
     */
    @Native("java", "((byte)(double)(#1))")
    @Native("c++",  "((x10_byte) (#1))")
    public native static safe operator (x:Double) as Byte;

    /**
     * Coerce a given UByte to a Byte.
     * @param x the given UByte
     * @return the given UByte converted to a Byte.
     */
    @Native("java", "((byte)(#1))")
    @Native("c++",  "((x10_byte) (#1))")
    public native static safe operator (x:UByte): Byte;


    /**
     * A constant holding the minimum value a Byte can have, -2<sup>7</sup>.
     */
    @Native("java", "java.lang.Byte.MIN_VALUE")
    @Native("c++", "((x10_byte)0x80)")
    public const MIN_VALUE = 0x80 as Byte;

    /**
     * A constant holding the maximum value a Byte can have, 2<sup>7</sup>-1.
     */
    @Native("java", "java.lang.Byte.MAX_VALUE")
    @Native("c++", "((x10_byte)0x7f)")
    public const MAX_VALUE = 0x7f as Byte;


    /**
     * Returns a String representation of this Byte in the specified radix.
     * @param radix the radix to use in the String representation
     * @return a String representation of this Byte in the specified radix.
     */
    @Native("java", "java.lang.Integer.toString(#0, #1)")
    @Native("c++", "x10aux::byte_utils::toString(#0, #1)")
    public global safe native def toString(radix:Int): String;

    /**
     * Returns a String representation of this Byte as a hexadecimal number.
     * @return a String representation of this Byte as a hexadecimal number.
     */
    @Native("java", "java.lang.Integer.toHexString(#0)")
    @Native("c++", "x10aux::byte_utils::toHexString(#0)")
    public global safe native def toHexString(): String;

    /**
     * Returns a String representation of this Byte as an octal number.
     * @return a String representation of this Byte as an octal number.
     */
    @Native("java", "java.lang.Integer.toOctalString(#0)")
    @Native("c++", "x10aux::byte_utils::toOctalString(#0)")
    public global safe native def toOctalString(): String;

    /**
     * Returns a String representation of this Byte as a binary number.
     * @return a String representation of this Byte as a binary number.
     */
    @Native("java", "java.lang.Integer.toBinaryString(#0)")
    @Native("c++", "x10aux::byte_utils::toBinaryString(#0)")
    public global safe native def toBinaryString(): String;

    /**
     * Returns a String representation of this Byte as a decimal number.
     * @return a String representation of this Byte as a decimal number.
     */
    @Native("java", "java.lang.Byte.toString(#0)")
    @Native("c++", "x10aux::to_string(#0)")
    public global safe native def toString(): String;

    /**
     * @deprecated use {@link #parse(String,Int)} instead
     */
    @Native("java", "java.lang.Byte.parseByte(#1, #2)")
    @Native("c++", "x10aux::byte_utils::parseByte(#1, #2)")
    public native static def parseByte(String, radix:Int): Byte throws NumberFormatException;

    /**
     * @deprecated use {@link #parse(String)} instead
     */
    @Native("java", "java.lang.Byte.parseByte(#1)")
    @Native("c++", "x10aux::byte_utils::parseByte(#1)")
    public native static def parseByte(String): Byte throws NumberFormatException;

    /**
     * Parses the String argument as a Byte in the radix specified by the second argument.
     * @param s the String containing the Byte representation to be parsed
     * @param radix the radix to be used while parsing s
     * @return the Byte represented by the String argument in the specified radix.
     * @throws NumberFormatException if the String does not contain a parsable Byte.
     */
    @Native("java", "java.lang.Byte.parseByte(#1, #2)")
    @Native("c++", "x10aux::byte_utils::parseByte(#1, #2)")
    public native static def parse(s:String, radix:Int): Byte throws NumberFormatException;

    /**
     * Parses the String argument as a decimal Byte.
     * @param s the String containing the Byte representation to be parsed
     * @return the Byte represented by the String argument.
     * @throws NumberFormatException if the String does not contain a parsable Byte.
     */
    @Native("java", "java.lang.Byte.parseByte(#1)")
    @Native("c++", "x10aux::byte_utils::parseByte(#1)")
    public native static def parse(s:String): Byte throws NumberFormatException;


    /**
     * Returns the value obtained by reversing the order of the bits in the
     * two's complement binary representation of this Byte.
     * @return the value obtained by reversing order of the bits in this Byte.
     */
    @Native("java", "((byte)(java.lang.Integer.reverse(#0)>>>24))")
    @Native("c++", "((x10_byte)(x10aux::int_utils::reverse(#0)>>24))")
    public native def reverse(): Byte;

    /**
     * Returns the signum function of this Byte.  The return value is -1 if
     * this Byte is negative; 0 if this Byte is zero; and 1 if this Byte is
     * positive.
     * @return the signum function of this Byte.
     */
    @Native("java", "java.lang.Integer.signum(#0)")
    @Native("c++", "x10aux::int_utils::signum((x10_int)#0)")
    public native def signum(): Int;


    /**
     * Return true if the given entity is a Byte, and this Byte is equal
     * to the given entity.
     * @param x the given entity
     * @return true if this Byte is equal to the given entity.
     */
    @Native("java", "x10.rtt.Equality.equalsequals(#0, #1)")
    @Native("c++", "x10aux::equals(#0,#1)")
    public global safe native def equals(x:Any):Boolean;

    /**
     * Returns true if this Byte is equal to the given Byte.
     * @param x the given Byte
     * @return true if this Byte is equal to the given Byte.
     */
    @Native("java", "x10.rtt.Equality.equalsequals(#0, #1)")
    @Native("c++", "x10aux::equals(#0,#1)")
    public global safe native def equals(x:Byte):Boolean;
}
