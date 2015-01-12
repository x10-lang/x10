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
 * Long is a 64-bit signed two's complement integral data type, with
 * values ranging from -9223372036854775808 to 9223372036854775807, inclusive.  All of the normal
 * arithmetic and bitwise operations are defined on Long, and Long
 * is closed under those operations.  There are also static methods
 * that define conversions from other data types, including String,
 * as well as some Long constants.
 */
@NativeRep("java", "long", null, "x10.rtt.Types.LONG")
@NativeRep("c++", "x10_long", "x10_long", null)
public struct Long implements Comparable[Long], Arithmetic[Long], Bitwise[Long], Ordered[Long] {


    /**
     * A less-than operator.
     * Compares this Long with another Long and returns true if this Long is
     * strictly less than the other Long.
     * @param x the other Long
     * @return true if this Long is strictly less than the other Long.
     */
    @Native("java", "((#this) < (#x))")
    @Native("c++",  "((#this) < (#x))")
    public native operator this < (x:Long): Boolean;

    /**
     * A greater-than operator.
     * Compares this Long with another Long and returns true if this Long is
     * strictly greater than the other Long.
     * @param x the other Long
     * @return true if this Long is strictly greater than the other Long.
     */
    @Native("java", "((#this) > (#x))")
    @Native("c++",  "((#this) > (#x))")
    public native operator this > (x:Long): Boolean;

    /**
     * A less-than-or-equal-to operator.
     * Compares this Long with another Long and returns true if this Long is
     * less than or equal to the other Long.
     * @param x the other Long
     * @return true if this Long is less than or equal to the other Long.
     */
    @Native("java", "((#this) <= (#x))")
    @Native("c++",  "((#this) <= (#x))")
    public native operator this <= (x:Long): Boolean;

    /**
     * A greater-than-or-equal-to operator.
     * Compares this Long with another Long and returns true if this Long is
     * greater than or equal to the other Long.
     * @param x the other Long
     * @return true if this Long is greater than or equal to the other Long.
     */
    @Native("java", "((#this) >= (#x))")
    @Native("c++",  "((#this) >= (#x))")
    public native operator this >= (x:Long): Boolean;


    /**
     * A binary plus operator.
     * Computes the result of the addition of the two operands.
     * Overflows result in truncating the high bits.
     * @param x the other Long
     * @return the sum of this Long and the other Long.
     */
    @Native("java", "((#this) + (#x))")
    @Native("c++",  "((#this) + (#x))")
    public native operator this + (x:Long): Long;

    /**
     * A binary minus operator.
     * Computes the result of the subtraction of the two operands.
     * Overflows result in truncating the high bits.
     * @param x the other Long
     * @return the difference of this Long and the other Long.
     */
    @Native("java", "((#this) - (#x))")
    @Native("c++",  "((#this) - (#x))")
    public native operator this - (x:Long): Long;

    /**
     * A binary multiply operator.
     * Computes the result of the multiplication of the two operands.
     * Overflows result in truncating the high bits.
     * @param x the other Long
     * @return the product of this Long and the other Long.
     */
    @Native("java", "((#this) * (#x))")
    @Native("c++",  "((#this) * (#x))")
    public native operator this * (x:Long): Long;

    /**
     * A binary divide operator.
     * Computes the result of the division of the two operands.
     * @param x the other Long
     * @return the quotient of this Long and the other Long.
     */
    @Native("java", "((#this) / (#x))")
    @Native("c++",  "((#this) / ::x10aux::zeroCheck(#x))")
    public native operator this / (x:Long): Long;

    /**
     * A binary remainder operator.
     * Computes a remainder from the division of the two operands.
     * @param x the other Long
     * @return the remainder from dividing this Long by the other Long.
     */
    @Native("java", "((#this) % (#x))")
    @Native("c++",  "((#this) % ::x10aux::zeroCheck(#x))")
    public native operator this % (x:Long): Long;

    /**
     * A unary plus operator.
     * A no-op.
     * @return the value of this Long.
     */
    @Native("java", "(+(#this))")
    @Native("c++",  "(+(#this))")
    public native operator + this: Long;

    /**
     * A unary minus operator.
     * Negates the operand.
     * Overflows result in truncating the high bits.
     * @return the negated value of this Long.
     */
    @Native("java", "(-(#this))")
    @Native("c++",  "(-(#this))")
    public native operator - this: Long;


    /**
     * A bitwise and operator.
     * Computes a bitwise AND of the two operands.
     * @param x the other Long
     * @return the bitwise AND of this Long and the other Long.
     */
    @Native("java", "((#this) & (#x))")
    @Native("c++",  "((#this) & (#x))")
    public native operator this & (x:Long): Long;

    /**
     * A bitwise or operator.
     * Computes a bitwise OR of the two operands.
     * @param x the other Long
     * @return the bitwise OR of this Long and the other Long.
     */
    @Native("java", "((#this) | (#x))")
    @Native("c++",  "((#this) | (#x))")
    public native operator this | (x:Long): Long;

    /**
     * A bitwise xor operator.
     * Computes a bitwise XOR of the two operands.
     * @param x the other Long
     * @return the bitwise XOR of this Long and the other Long.
     */
    @Native("java", "((#this) ^ (#x))")
    @Native("c++",  "((#this) ^ (#x))")
    public native operator this ^ (x:Long): Long;

    /**
     * A bitwise left shift operator.
     * Computes the value of the left-hand operand shifted left by the value of the right-hand operand.
     * The shift count will be masked with 0x3f before the shift is applied.
     * @param count the shift count
     * @return this Long shifted left by count.
     */
    @Native("java", "((#this) << (int)(#count))")  // no mask. Java defines shift as masked.
    @Native("c++",  "((#this) << (0x3f & (x10_int)(#count)))")
    public native operator this << (count:Long): Long;

    /**
     * A bitwise right shift operator.
     * Computes the value of the left-hand operand shifted right by the value of the right-hand operand,
     * replicating the sign bit into the high bits.
     * The shift count will be masked with 0x3f before the shift is applied.
     * @param count the shift count
     * @return this Long shifted right by count.
     */
    @Native("java", "((#this) >> (int)(#count))")  // no mask. Java defines shift as masked.
    @Native("c++",  "((#this) >> (0x3f & (x10_int)(#count)))")
    public native operator this >> (count:Long): Long;

    /**
     * A bitwise logical right shift operator (zero-fill).
     * Computes the value of the left-hand operand shifted right by the value of the right-hand operand,
     * filling the high bits with zeros.
     * The shift count will be masked with 0x3f before the shift is applied.
     * @deprecated use the right-shift operator and unsigned conversions instead.
     * @param count the shift count
     * @return this Long shifted right by count with high bits zero-filled.
     */
    @Native("java", "((#this) >>> (int)(#count))")  // no mask. Java defines shift as masked.
    @Native("c++",  "((x10_long) ((x10_ulong) (#this) >> (0x3f & (x10_int)(#count))))")
    public native operator this >>> (count:Long): Long;

    /**
     * A bitwise complement operator.
     * Computes a bitwise complement (NOT) of the operand.
     * @return the bitwise complement of this Long.
     */
    @Native("java", "((long) ~(#this))")
    @Native("c++",  "(~(#this))")
    public native operator ~ this: Long;


    /**
     * Coerce a given Byte to a Long.
     * @param x the given Byte
     * @return the given Byte converted to a Long.
     */
    @Native("java", "((long)(#x))")
    @Native("c++",  "((x10_long)(#x))")
    public native static operator (x:Byte): Long;

    /**
     * Coerce a given Short to a Long.
     * @param x the given Short
     * @return the given Short converted to a Long.
     */
    @Native("java", "((long)(#x))")
    @Native("c++",  "((x10_long)(#x))")
    public native static operator (x:Short): Long;

    /**
     * Coerce a given Int to a Long.
     * @param x the given Int
     * @return the given Int converted to a Long.
     */
    @Native("java", "((long)(#x))")
    @Native("c++",  "((x10_long)(#x))")
    public native static operator (x:Int): Long;

    /**
     * Convert a given Float to a Long.
     * @param x the given Float
     * @return the given Float converted to a Long.
     */
    @Native("java", "((long)(float)(#x))")
    @Native("c++",  "::x10::lang::FloatNatives::toLong(#x)")
    @Native("cuda", "((x10_long)(#x))")
    public native static operator (x:Float) as Long;

    /**
     * Convert a given Double to a Long.
     * @param x the given Double
     * @return the given Double converted to a Long.
     */
    @Native("java", "((long)(double)(#x))")
    @Native("c++",  "::x10::lang::DoubleNatives::toLong(#x)")
    @Native("cuda", "((x10_long)(#x))")
    public native static operator (x:Double) as Long;

    /**
     * Coerce a given UByte to a Long.
     * @param x the given UByte
     * @return the given UByte converted to a Long.
     */
    @Native("java", "((long)#x)")
    @Native("c++",  "((x10_long)(#x))")
    public native static operator (x:UByte): Long;

    /**
     * Coerce a given UShort to a Long.
     * @param x the given UShort
     * @return the given UShort converted to a Long.
     */
    @Native("java", "((long)#x)")
    @Native("c++",  "((x10_long)(#x))")
    public native static operator (x:UShort): Long;

    /**
     * Coerce a given UInt to a Long.
     * @param x the given UInt
     * @return the given UInt converted to a Long.
     */
    @Native("java", "((long)(#x))")
    @Native("c++",  "((x10_long)(#x))")
    public native static operator (x:UInt): Long;

    /**
     * Convert a given ULong to a Long.
     * @param x the given ULong
     * @return the given ULong converted to a Long.
     */
    @Native("java", "((long)(#x))")
    @Native("c++",  "((x10_long)(#x))")
    public native static operator (x:ULong) as Long;


    /**
     * A constant holding the minimum value a Long can have, -2<sup>63</sup>.
     */
    @Native("java", "java.lang.Long.MIN_VALUE")
    @Native("c++", "(x10_long)0x8000000000000000LL")
    public static MIN_VALUE: Long{self==0x8000000000000000} = 0x8000000000000000;

    /**
     * A constant holding the maximum value a Long can have, 2<sup>63</sup>-1.
     */
    @Native("java", "java.lang.Long.MAX_VALUE")
    @Native("c++", "(x10_long)0x7fffffffffffffffLL")
    public static MAX_VALUE: Long{self==0x7fffffffffffffff} = 0x7fffffffffffffff;


    /**
     * Returns a String representation of this Long in the specified radix.
     * @param radix the radix to use in the String representation
     * @return a String representation of this Long in the specified radix.
     */
    @Native("java", "java.lang.Long.toString(#this, #radix)")
    @Native("c++", "::x10::lang::LongNatives::toString(#this, #radix)")
    public native def toString(radix:Int): String;

    /**
     * Returns a String representation of this Long in base 16.
     * This method is simply a synonym for toString(16).
     * In particular toHexString(-20) will print -14; to print
     * the 64 bit two's complement hexadecimal representation of a 
     * Long l use (l as ULong).toHexString().  
     *
     * @return a String representation of this Long in base 16.
     */
    @Native("java", "java.lang.Long.toString(#this, 16)")
    @Native("c++", "::x10::lang::LongNatives::toString(#this, 16)")
    public native def toHexString(): String;

    /**
     * Returns a String representation of this Long in base 8.
     * This method is simply a synonym for toString(8).
     * In particular toHexString(-20) will print -24; to print
     * the 64 bit two's complement octal representation of a Long l
     * use (l as ULong).toOctalString().  
     *
     * @return a String representation of this Long in base 8.
     */
    @Native("java", "java.lang.Long.toString(#this, 8)")
    @Native("c++", "::x10::lang::LongNatives::toString(#this, 8)")
    public native def toOctalString(): String;

    /**
     * Returns a String representation of this Long in base 2.
     * This method is simply a synonym for toString(2).
     * In particular toBinaryString(-20) will print -10100; to print
     * the 64 bit two's complement binary representation of a Long l
     * use (l as ULong).toBinaryString().  
     *
     * @return a String representation of this Long in base 2.
     */
    @Native("java", "java.lang.Long.toString(#this, 2)")
    @Native("c++", "::x10::lang::LongNatives::toString(#this, 2)")
    public native def toBinaryString(): String;

    /**
     * Returns a String representation of this Long as a decimal number.
     * @return a String representation of this Long as a decimal number.
     */
    @Native("java", "java.lang.Long.toString(#this)")
    @Native("c++", "::x10aux::to_string(#this)")
    public native def toString(): String;

    /**
     * @deprecated use {@link #parse(String,Long)} instead
     */
    @Native("java", "java.lang.Long.parseLong(#s, #radix)")
    @Native("c++", "::x10::lang::LongNatives::parseLong(#s, #radix)")
    public native static def parseLong(s:String, radix:Int): Long; //throwsNumberFormatException;

    /**
     * @deprecated use {@link #parse(String)} instead
     */
    @Native("java", "java.lang.Long.parseLong(#s)")
    @Native("c++", "::x10::lang::LongNatives::parseLong(#s)")
    public native static def parseLong(s:String): Long; //throwsNumberFormatException;

    /**
     * Parses the String argument as a Long in the radix specified by the second argument.
     * @param s the String containing the Long representation to be parsed
     * @param radix the radix to be used while parsing s
     * @return the Long represented by the String argument in the specified radix.
     * @throws NumberFormatException if the String does not contain a parsable Long.
     */
    @Native("java", "java.lang.Long.parseLong(#s, #radix)")
    @Native("c++", "::x10::lang::LongNatives::parseLong(#s, #radix)")
    public native static def parse(s:String, radix:Int): Long; //throwsNumberFormatException;

    /**
     * Parses the String argument as a decimal Long.
     * @param s the String containing the Long representation to be parsed
     * @return the Long represented by the String argument.
     * @throws NumberFormatException if the String does not contain a parsable Long.
     */
    @Native("java", "java.lang.Long.parseLong(#s)")
    @Native("c++", "::x10::lang::LongNatives::parseLong(#s)")
    public native static def parse(s:String): Long; //throwsNumberFormatException;


    /**
     * Returns a Long value with at most a single one-bit, in the position
     * of the highest-order ("leftmost") one-bit in this Long value.
     * Returns zero if this Long has no one-bits in its two's complement
     * binary representation, that is, if it is equal to zero.
     * @return a Long value with a single one-bit, in the position of the highest-order one-bit in this Long, or zero if this Long is itself equal to zero.
     */
    @Native("java", "java.lang.Long.highestOneBit(#this)")
    @Native("c++", "::x10::lang::LongNatives::highestOneBit(#this)")
    public native def highestOneBit(): Long;

    /**
     * Returns a Long value with at most a single one-bit, in the position
     * of the lowest-order ("rightmost") one-bit in this Long value.
     * Returns zero if this Long has no one-bits in its two's complement
     * binary representation, that is, if it is equal to zero.
     * @return a Long value with a single one-bit, in the position of the lowest-order one-bit in this Long, or zero if this Long is itself equal to zero.
     */
    @Native("java", "java.lang.Long.lowestOneBit(#this)")
    @Native("c++", "::x10::lang::LongNatives::lowestOneBit(#this)")
    public native def lowestOneBit(): Long;

    /**
     * Returns the number of zero bits preceding the highest-order ("leftmost")
     * one-bit in the two's complement binary representation of this Long.
     * Returns 64 if this Long has no one-bits in its two's complement
     * representation, in other words if it is equal to zero.
     * @return the number of zero bits preceding the highest-order one-bit in the two's complement binary representation of this Long, or 64 if this Long is equal to zero.
     */
    @Native("java", "java.lang.Long.numberOfLeadingZeros(#this)")
    @Native("c++", "::x10::lang::LongNatives::numberOfLeadingZeros(#this)")
    public native def numberOfLeadingZeros(): Int;

    /**
     * Returns the number of zero bits following the lowest-order ("rightmost")
     * one-bit in the two's complement binary representation of this Long.
     * Returns 64 if this Long has no one-bits in its two's complement
     * representation, in other words if it is equal to zero.
     * @return the number of zero bits following the lowest-order one-bit in the two's complement binary representation of this Long, or 64 if this Long is equal to zero.
     */
    @Native("java", "java.lang.Long.numberOfTrailingZeros(#this)")
    @Native("c++", "::x10::lang::LongNatives::numberOfTrailingZeros(#this)")
    public native def numberOfTrailingZeros(): Int;

    /**
     * Returns the number of one-bits in the two's complement binary
     * representation of this Long.  This function is sometimes referred
     * to as the <i>population count</i>.
     * @return the number of one-bits in the two's complement binary representation of this Long.
     */
    @Native("java", "java.lang.Long.bitCount(#this)")
    @Native("c++", "::x10::lang::LongNatives::bitCount(#this)")
    public native def bitCount(): Int;

    /**
     * Returns the value obtained by rotating the two's complement binary
     * representation of this Long left by the specified number of bits.
     * (Bits shifted out of the left hand, or high-order, side reenter on
     * the right, or low-order.)<br>
     * Note that left rotation with a negative distance is equivalent to
     * right rotation:
     * <code>rotateLeft(val, -distance) == rotateRight(val, distance)</code>.
     * Note also that rotation by any multiple of 64 is a no-op, so all but
     * the last five bits of the rotation distance can be ignored, even if
     * the distance is negative:
     * <code>rotateLeft(val, distance) == rotateLeft(val, distance &amp; 0x3F)</code>.
     * @param distance the distance to rotate by
     * @return the value obtained by rotating the two's complement binary representation of this Long left by the specified number of bits.
     * @see #rotateRight(Int)
     */
    @Native("java", "java.lang.Long.rotateLeft(#this, #distance)")
    @Native("c++", "::x10::lang::LongNatives::rotateLeft(#this, #distance)")
    public native def rotateLeft(distance:Int): Long;

    /**
     * Returns the value obtained by rotating the two's complement binary
     * representation of this Long right by the specified number of bits.
     * (Bits shifted out of the right hand, or low-order, side reenter on
     * the left, or high-order.)<br>
     * Note that right rotation with a negative distance is equivalent to
     * left rotation:
     * <code>rotateRight(val, -distance) == rotateLeft(val, distance)</code>.
     * Note also that rotation by any multiple of 64 is a no-op, so all but
     * the last five bits of the rotation distance can be ignored, even if
     * the distance is negative:
     * <code>rotateRight(val, distance) == rotateRight(val, distance &amp; 0x3F)</code>.
     * @param distance the distance to rotate by
     * @return the value obtained by rotating the two's complement binary representation of this Long right by the specified number of bits.
     * @see #rotateLeft(Int)
     */
    @Native("java", "java.lang.Long.rotateRight(#this, #distance)")
    @Native("c++", "::x10::lang::LongNatives::rotateRight(#this, #distance)")
    public native def rotateRight(distance:Int): Long;

    /**
     * Returns the value obtained by reversing the order of the bits in the
     * two's complement binary representation of this Long.
     * @return the value obtained by reversing order of the bits in this Long.
     */
    @Native("java", "java.lang.Long.reverse(#this)")
    @Native("c++", "::x10::lang::LongNatives::reverse(#this)")
    public native def reverse(): Long;

    /**
     * Returns the signum function of this Long.  The return value is -1 if
     * this Long is negative; 0 if this Long is zero; and 1 if this Long is
     * positive.
     * @return the signum function of this Long.
     */
    @Native("java", "java.lang.Long.signum(#this)")
    @Native("c++", "::x10::lang::LongNatives::signum(#this)")
    public native def signum(): Int;

    /**
     * Returns the value obtained by reversing the order of the bytes in the
     * two's complement representation of this Long.
     * @return the value obtained by reversing the bytes in this Long.
     */
    @Native("java", "java.lang.Long.reverseBytes(#this)")
    @Native("c++", "::x10::lang::LongNatives::reverseBytes(#this)")
    public native def reverseBytes(): Long;


    /**
     * Return true if the given entity is a Long, and this Long is equal
     * to the given entity.
     * @param x the given entity
     * @return true if this Long is equal to the given entity.
     */
    @Native("java", "x10.rtt.Equality.equalsequals(#this, #x)")
    @Native("c++", "::x10aux::equals(#this, #x)")
    public native def equals(x:Any):Boolean;

    /**
     * Returns true if this Long is equal to the given Long.
     * @param x the given Long
     * @return true if this Long is equal to the given Long.
     */
    @Native("java", "x10.rtt.Equality.equalsequals(#this, #x)")
    @Native("c++", "::x10aux::equals(#this, #x)")
    public native def equals(x:Long):Boolean;

    /**
    * Returns a negative Int, zero, or a positive Int if this Long is less than, equal
    * to, or greater than the given Long.
    * @param x the given Long
    * @return a negative Int, zero, or a positive Int if this Long is less than, equal
    * to, or greater than the given Long.
    */
   @Native("java", "x10.rtt.Equality.compareTo(#this, #x)")
   @Native("c++", "::x10::lang::LongNatives::compareTo(#this, #x)")
   public native def compareTo(x:Long):Int;
   
   /**
    * Constructs a LongRange from
    * the lower bound to the upper bound, inclusive.
    * @param lower the lower bound
    * @param upper the upper bound
    * @return a range from lower to upper, inclusive.
    */
   @Native("java", "new x10.lang.LongRange((java.lang.System[]) null).x10$lang$LongRange$$init$S(#x, #y)")
   @Native("c++", "::x10::lang::LongRange::_make(#x, #y)")
   public native static operator (x:Long) .. (y:Long):LongRange{min==x,max==y};
}
public type Long(b:Long) = Long{self==b};
