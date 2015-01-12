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
@NativeRep("java", "short", null, "x10.rtt.Types.SHORT")
@NativeRep("c++", "x10_short", "x10_short", null)
public struct Short implements Comparable[Short], Arithmetic[Short], Bitwise[Short], Ordered[Short] {

    /**
     * A less-than operator.
     * Compares this Short with another Short and returns true if this Short is
     * strictly less than the other Short.
     * @param x the other Short
     * @return true if this Short is strictly less than the other Short.
     */
    @Native("java", "((#this) < (#x))")
    @Native("c++",  "((#this) < (#x))")
    public native operator this < (x:Short): Boolean;

    /**
     * A greater-than operator.
     * Compares this Short with another Short and returns true if this Short is
     * strictly greater than the other Short.
     * @param x the other Short
     * @return true if this Short is strictly greater than the other Short.
     */
    @Native("java", "((#this) > (#x))")
    @Native("c++",  "((#this) > (#x))")
    public native operator this > (x:Short): Boolean;

    /**
     * A less-than-or-equal-to operator.
     * Compares this Short with another Short and returns true if this Short is
     * less than or equal to the other Short.
     * @param x the other Short
     * @return true if this Short is less than or equal to the other Short.
     */
    @Native("java", "((#this) <= (#x))")
    @Native("c++",  "((#this) <= (#x))")
    public native operator this <= (x:Short): Boolean;

    /**
     * A greater-than-or-equal-to operator.
     * Compares this Short with another Short and returns true if this Short is
     * greater than or equal to the other Short.
     * @param x the other Short
     * @return true if this Short is greater than or equal to the other Short.
     */
    @Native("java", "((#this) >= (#x))")
    @Native("c++",  "((#this) >= (#x))")
    public native operator this >= (x:Short): Boolean;


    /**
     * A binary plus operator.
     * Computes the result of the addition of the two operands.
     * Overflows result in truncating the high bits.
     * @param x the other Short
     * @return the sum of this Short and the other Short.
     */
    @Native("java", "((short) ((#this) + (#x)))")
    @Native("c++",  "((x10_short) ((#this) + (#x)))")
    public native operator this + (x:Short): Short;

    /**
     * A binary minus operator.
     * Computes the result of the subtraction of the two operands.
     * Overflows result in truncating the high bits.
     * @param x the other Short
     * @return the difference of this Short and the other Short.
     */
    @Native("java", "((short) ((#this) - (#x)))")
    @Native("c++",  "((x10_short) ((#this) - (#x)))")
    public native operator this - (x:Short): Short;

    /**
     * A binary multiply operator.
     * Computes the result of the multiplication of the two operands.
     * Overflows result in truncating the high bits.
     * @param x the other Short
     * @return the product of this Short and the other Short.
     */
    @Native("java", "((short) ((#this) * (#x)))")
    @Native("c++",  "((x10_short) ((#this) * (#x)))")
    public native operator this * (x:Short): Short;

    /**
     * A binary divide operator.
     * Computes the result of the division of the two operands.
     * @param x the other Short
     * @return the quotient of this Short and the other Short.
     */
    @Native("java", "((short) ((#this) / (#x)))")
    @Native("c++",  "((x10_short) ((#this) / ::x10aux::zeroCheck(#x)))")
    public native operator this / (x:Short): Short;

    /**
     * A binary remainder operator.
     * Computes a remainder from the division of the two operands.
     * @param x the other Short
     * @return the remainder from dividing this Short by the other Short.
     */
    @Native("java", "((short) ((#this) % (#x)))")
    @Native("c++",  "((x10_short) ((#this) % ::x10aux::zeroCheck(#x)))")
    public native operator this % (x:Short): Short;

    /**
     * A unary plus operator.
     * A no-op.
     * @return the value of this Short.
     */
    @Native("java", "((short) +(#this))")
    @Native("c++",  "((x10_short) +(#this))")
    public native operator + this: Short;

    /**
     * A unary minus operator.
     * Negates the operand.
     * Overflows result in truncating the high bits.
     * @return the negated value of this Short.
     */
    @Native("java", "((short) -(#this))")
    @Native("c++",  "((x10_short) -(#this))")
    public native operator - this: Short;


    /**
     * A bitwise and operator.
     * Computes a bitwise AND of the two operands.
     * @param x the other Short
     * @return the bitwise AND of this Short and the other Short.
     */
    @Native("java", "((short) ((#this) & (#x)))")
    @Native("c++",  "((x10_short) ((#this) & (#x)))")
    public native operator this & (x:Short): Short;

    /**
     * A bitwise or operator.
     * Computes a bitwise OR of the two operands.
     * @param x the other Short
     * @return the bitwise OR of this Short and the other Short.
     */
    @Native("java", "((short) ((#this) | (#x)))")
    @Native("c++",  "((x10_short) ((#this) | (#x)))")
    public native operator this | (x:Short): Short;

    /**
     * A bitwise xor operator.
     * Computes a bitwise XOR of the two operands.
     * @param x the other Short
     * @return the bitwise XOR of this Short and the other Short.
     */
    @Native("java", "((short) ((#this) ^ (#x)))")
    @Native("c++",  "((x10_short) ((#this) ^ (#x)))")
    public native operator this ^ (x:Short): Short;

    /**
     * A bitwise left shift operator.
     * Computes the value of the left-hand operand shifted left by the value of the right-hand operand.
     * The shift count will be masked with 0xf before the shift is applied.
     * @param count the shift count
     * @return this Short shifted left by count.
     */
    @Native("java", "((short) ((#this) << (0xf & (int)(#count))))")
    @Native("c++",  "((x10_short) ((#this) << (0xf & (x10_int)(#count))))")
    public native operator this << (count:Long): Short;

    /**
     * A bitwise right shift operator.
     * Computes the value of the left-hand operand shifted right by the value of the right-hand operand,
     * replicating the sign bit into the high bits.
     * The shift count will be masked with 0xf before the shift is applied.
     * @param count the shift count
     * @return this Short shifted right by count.
     */
    @Native("java", "((short) ((#this) >> (0xf & (int)(#count))))")
    @Native("c++",  "((x10_short) ((#this) >> (0xf & (x10_int)(#count))))")
    public native operator this >> (count:Long): Short;

    /**
     * A bitwise logical right shift operator (zero-fill).
     * Computes the value of the left-hand operand shifted right by the value of the right-hand operand,
     * filling the high bits with zeros.
     * The shift count will be masked with 0xf before the shift is applied.
     * @deprecated use the right-shift operator and unsigned conversions instead.
     * @param count the shift count
     * @return this Short shifted right by count with high bits zero-filled.
     */
    @Native("java", "((short) ((#this) >>> (0xf & (int)(#count))))")
    @Native("c++",  "((x10_short) ((x10_uint) (#this) >> (0xf & (x10_int)(#count))))")
    public native operator this >>> (count:Long): Short;

    /**
     * A bitwise complement operator.
     * Computes a bitwise complement (NOT) of the operand.
     * @return the bitwise complement of this Short.
     */
    @Native("java", "((short) ~(#this))")
    @Native("c++",  "((x10_short) ~(#this))")
    public native operator ~ this: Short;


    /**
     * Coerce a given Byte to a Short.
     * @param x the given Byte
     * @return the given Byte converted to a Short.
     */
    @Native("java", "((short) (#x))")
    @Native("c++",  "((x10_short) (#x))")
    public native static operator (x:Byte): Short;

    /**
     * Convert a given Int to a Short.
     * @param x the given Int
     * @return the given Int converted to a Short.
     */
    @Native("java", "((short)(int)(#x))")
    @Native("c++",  "((x10_short) (#x))")
    public native static operator (x:Int) as Short;

    /**
     * Convert a given Long to a Short.
     * @param x the given Long
     * @return the given Long converted to a Short.
     */
    @Native("java", "((short)(long)(#x))")
    @Native("c++",  "((x10_short) (#x))")
    public native static operator (x:Long) as Short;

    /**
     * Convert a given Float to a Short.
     * @param x the given Float
     * @return the given Float converted to a Short.
     */
    @Native("java", "x10.runtime.impl.java.FloatUtils.toShort(#x)")
    @Native("c++",  "::x10::lang::FloatNatives::toShort(#x)")
    @Native("cuda", "((x10_short) (#x))")
    public native static operator (x:Float) as Short;

    /**
     * Convert a given Double to a Short.
     * @param x the given Double
     * @return the given Double converted to a Short.
     */
    @Native("java", "x10.runtime.impl.java.DoubleUtils.toShort(#x)")
    @Native("c++",  "::x10::lang::DoubleNatives::toShort(#x)")
    @Native("cuda", "((x10_short) (#x))")
    public native static operator (x:Double) as Short;

    /**
     * Convert a given UShort to a Short.
     * @param x the given UShort
     * @return the given UShort converted to a Short.
     */
    @Native("java", "((short)(#x))")
    @Native("c++",  "((x10_short) (#x))")
    public native static operator (x:UShort) as Short;

    /**
     * Coerce a given UByte to a Short.
     * @param x the given UByte
     * @return the given UByte converted to a Short.
     */
    @Native("java", "((short)(#x))")
    @Native("c++",  "((x10_short) (#x))")
    public native static operator (x:UByte): Short;

    /**
     * A constant holding the minimum value a Short can have, -2<sup>15</sup>.
     */
    @Native("java", "java.lang.Short.MIN_VALUE")
    @Native("c++", "((x10_short)0x8000)")
    public static MIN_VALUE: Short{self==0x8000S} = 0x8000S;

    /**
     * A constant holding the maximum value a Short can have, 2<sup>15</sup>-1.
     */
    @Native("java", "java.lang.Short.MAX_VALUE")
    @Native("c++", "((x10_short)0x7fff)")
    public static MAX_VALUE: Short{self==0x7fffS} = 0x7fffS;


    /**
     * Returns a String representation of this Short in the specified radix.
     * @param radix the radix to use in the String representation
     * @return a String representation of this Short in the specified radix.
     */
    @Native("java", "x10.runtime.impl.java.ShortUtils.toString((short)#this, #radix)")
    @Native("c++", "::x10::lang::ShortNatives::toString(#this, #radix)")
    public native def toString(radix:Int): String;

    /**
     * Returns a String representation of this Short in base 16.
     * This method is simply a synonym for toString(16).
     * In particular toHexString(-20) will print -14; to print
     * the 16 bit two's complement hexadecimal representation of a Short v
     * use (v as UShort).toHexString().  
     *
     * @return a String representation of this Short in base 16.
     */
    @Native("java", "x10.runtime.impl.java.ShortUtils.toString((short)#this, 16)")
    @Native("c++", "::x10::lang::ShortNatives::toString(#this, 16)")
    public native def toHexString(): String;

    /**
     * Returns a String representation of this Short in base 8.
     * This method is simply a synonym for toString(8).
     * In particular toOctalString(-20) will print -24; to print
     * the 16 bit two's complement octal representation of a Short v
     * use (v as UShort).toOctalString().  
     *
     * @return a String representation of this Short in base 8.
     */
    @Native("java", "x10.runtime.impl.java.ShortUtils.toString((short)#this, 8)")
    @Native("c++", "::x10::lang::ShortNatives::toString(#this, 8)")
    public native def toOctalString(): String;

    /**
     * Returns a String representation of this Short in base 2.
     * This method is simply a synonym for toString(2).
     * In particular toBinaryString(-20) will print -10100; to print
     * the 16 bit two's complement binary representation of a Short v
     * use (v as UShort).toBinaryString().  
     *
     * @return a String representation of this Short in base 2.
     */
    @Native("java", "x10.runtime.impl.java.ShortUtils.toString((short)#this, 2)")
    @Native("c++", "::x10::lang::ShortNatives::toString(#this, 2)")
    public native def toBinaryString(): String;

    /**
     * Returns a String representation of this Short as a decimal number.
     * @return a String representation of this Short as a decimal number.
     */
    @Native("java", "java.lang.Short.toString((short)#this)")
    @Native("c++", "::x10aux::to_string(#this)")
    public native def toString(): String;

    /**
     * @deprecated use {@link #parse(String,Int)} instead
     */
    @Native("java", "java.lang.Short.parseShort(#s, #radix)")
    @Native("c++", "::x10::lang::ShortNatives::parseShort(#s, #radix)")
    public native static def parseShort(s:String, radix:Int): Short; //throwsNumberFormatException;

    /**
     * @deprecated use {@link #parse(String)} instead
     */
    @Native("java", "java.lang.Short.parseShort(#s)")
    @Native("c++", "::x10::lang::ShortNatives::parseShort(#s)")
    public native static def parseShort(s:String): Short; //throwsNumberFormatException;

    /**
     * Parses the String argument as a Short in the radix specified by the second argument.
     * @param s the String containing the Short representation to be parsed
     * @param radix the radix to be used while parsing s
     * @return the Short represented by the String argument in the specified radix.
     * @throws NumberFormatException if the String does not contain a parsable Short.
     */
    @Native("java", "java.lang.Short.parseShort(#s, #radix)")
    @Native("c++", "::x10::lang::ShortNatives::parseShort(#s, #radix)")
    public native static def parse(s:String, radix:Int): Short; //throwsNumberFormatException;

    /**
     * Parses the String argument as a decimal Short.
     * @param s the String containing the Short representation to be parsed
     * @return the Short represented by the String argument.
     * @throws NumberFormatException if the String does not contain a parsable Short.
     */
    @Native("java", "java.lang.Short.parseShort(#s)")
    @Native("c++", "::x10::lang::ShortNatives::parseShort(#s)")
    public native static def parse(s:String): Short; //throwsNumberFormatException;


    /**
     * Returns the value obtained by reversing the order of the bits in the
     * two's complement binary representation of this Int.
     * @return the value obtained by reversing order of the bits in this Int.
     */
    @Native("java", "((short)(java.lang.Integer.reverse(#this)>>>16))")
    @Native("c++", "((x10_short)(::x10::lang::IntNatives::reverse(#this)>>16))")
    public native def reverse(): Short;

    /**
     * Returns the signum function of this Short.  The return value is -1 if
     * this Short is negative; 0 if this Short is zero; and 1 if this Short is
     * positive.
     * @return the signum function of this Short.
     */
    @Native("java", "java.lang.Integer.signum(#this)")
    @Native("c++", "::x10::lang::IntNatives::signum((x10_int)#this)")
    public native def signum(): Int;

    /**
     * Returns the value obtained by reversing the order of the bytes in the
     * two's complement representation of this Short.
     * @return the value obtained by reversing (or, equivalently, swapping) the bytes in this Short.
     */
    @Native("java", "java.lang.Short.reverseBytes(#this)")
    @Native("c++", "::x10::lang::ShortNatives::reverseBytes(#this)")
    public native def reverseBytes(): Short;


    /**
     * Return true if the given entity is a Short, and this Short is equal
     * to the given entity.
     * @param x the given entity
     * @return true if this Short is equal to the given entity.
     */
    @Native("java", "x10.rtt.Equality.equalsequals(#this, #x)")
    @Native("c++", "::x10aux::equals(#this, #x)")
    public native def equals(x:Any):Boolean;

    /**
     * Returns true if this Short is equal to the given Short.
     * @param x the given Short
     * @return true if this Short is equal to the given Short.
     */
    @Native("java", "x10.rtt.Equality.equalsequals(#this, #x)")
    @Native("c++", "::x10aux::equals(#this, #x)")
    public native def equals(x:Short):Boolean;

    /**
    * Returns a negative Int, zero, or a positive Int if this Short is less than, equal
    * to, or greater than the given Short.
    * @param x the given Short
    * @return a negative Int, zero, or a positive Int if this Short is less than, equal
    * to, or greater than the given Short.
    */
   @Native("java", "x10.rtt.Equality.compareTo(#this, #x)")
   @Native("c++", "::x10::lang::ShortNatives::compareTo(#this, #x)")
   public native def compareTo(x:Short):Int;
}
public type Short(b:Short) = Short{self==b};
