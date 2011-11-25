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
 * UInt is a 32-bit unsigned integral data type, with
 * values ranging from 0 to 4294967295, inclusive.  All of the normal
 * arithmetic and bitwise operations are defined on UInt, and UInt
 * is closed under those operations.  There are also static methods
 * that define conversions from other data types, including String,
 * as well as some UInt constants.
 */
@NativeRep("java", "int", null, "x10.rtt.Types.UINT")
//                 v-- when used
@NativeRep("c++", "x10_uint", "x10_uint", null)
//                             ^ when constructed
public struct UInt implements Comparable[UInt], Arithmetic[UInt], Bitwise[UInt], Ordered[UInt] {

    /** The actual number with Int representation */
    /* boxed representation disabled
    public val intVal:Int;
    public def this(value:Int) {
        this.intVal = value;
    } */

    /**
     * A less-than operator.
     * Compares this UInt with another UInt and returns true if this UInt is
     * strictly less than the other UInt.
     * @param x the other UInt
     * @return true if this UInt is strictly less than the other UInt.
     */
    @Native("java", "x10.core.Unsigned.lt(#this, #x)")
    @Native("c++",  "((#0) < (#1))")
    public native operator this < (x:UInt): Boolean; /*  {
         return (intVal + Int.MIN_VALUE) < (x.intVal + Int.MIN_VALUE);
     } */

    /**
     * A greater-than operator.
     * Compares this UInt with another UInt and returns true if this UInt is
     * strictly greater than the other UInt.
     * @param x the other UInt
     * @return true if this UInt is strictly greater than the other UInt.
     */
    @Native("java", "x10.core.Unsigned.gt(#this, #x)")
    @Native("c++",  "((#0) > (#1))")
    public native operator this > (x:UInt): Boolean; /*  {
        return (intVal + Int.MIN_VALUE) > (x.intVal + Int.MIN_VALUE);
    } */

    /**
     * A less-than-or-equal-to operator.
     * Compares this UInt with another UInt and returns true if this UInt is
     * less than or equal to the other UInt.
     * @param x the other UInt
     * @return true if this UInt is less than or equal to the other UInt.
     */
    @Native("java", "x10.core.Unsigned.le(#this, #x)")
    @Native("c++",  "((#0) <= (#1))")
    public native operator this <= (x:UInt): Boolean; /*  {
         return (intVal + Int.MIN_VALUE) <= (x.intVal + Int.MIN_VALUE);
     } */

    /**
     * A greater-than-or-equal-to operator.
     * Compares this UInt with another UInt and returns true if this UInt is
     * greater than or equal to the other UInt.
     * @param x the other UInt
     * @return true if this UInt is greater than or equal to the other UInt.
     */
    @Native("java", "x10.core.Unsigned.ge(#this, #x)")
    @Native("c++",  "((#0) >= (#1))")
    public native operator this >= (x:UInt): Boolean; /*  {
        return (intVal + Int.MIN_VALUE) >= (x.intVal + Int.MIN_VALUE);
    } */


    /**
     * A binary plus operator.
     * Computes the result of the addition of the two operands.
     * Overflows result in truncating the high bits.
     * @param x the other UInt
     * @return the sum of this UInt and the other UInt.
     */
    @Native("java", "((#this) + (#x))")
    @Native("c++",  "((x10_uint) ((#0) + (#1)))")
    public native operator this + (x:UInt): UInt; /*  = UInt(intVal + x.intVal); */

    /**
     * A binary minus operator.
     * Computes the result of the subtraction of the two operands.
     * Overflows result in truncating the high bits.
     * @param x the other UInt
     * @return the difference of this UInt and the other UInt.
     */
    @Native("java", "((#this) - (#x))")
    @Native("c++",  "((x10_uint) ((#0) - (#1)))")
    public native operator this - (x:UInt): UInt; /*  = UInt(intVal - x.intVal); */

    /**
     * A binary multiply operator.
     * Computes the result of the multiplication of the two operands.
     * Overflows result in truncating the high bits.
     * @param x the other UInt
     * @return the product of this UInt and the other UInt.
     */
    @Native("java", "((#this) * (#x))")
    @Native("c++",  "((x10_uint) ((#0) * (#1)))")
    public native operator this * (x:UInt): UInt; /*  = UInt(intVal * x.intVal); */

    /**
     * A binary divide operator.
     * Computes the result of the division of the two operands.
     * @param x the other UInt
     * @return the quotient of this UInt and the other UInt.
     */
    @Native("java", "x10.core.Unsigned.div(#this, #x)")
    @Native("c++",  "((x10_uint) ((#0) / x10aux::zeroCheck(#1)))")
    public native operator this / (x:UInt): UInt; /*  {
        return UInt(((intVal as Long) / (x.intVal as Long)) as Int);
    } */

    /**
     * A binary remainder operator.
     * Computes a remainder from the division of the two operands.
     * @param x the other UInt
     * @return the remainder from dividing this UInt by the other UInt.
     */
    @Native("java", "x10.core.Unsigned.rem(#this, #x)")
    @Native("c++",  "((x10_uint) ((#0) % x10aux::zeroCheck(#1)))")
    public native operator this % (x:UInt): UInt; /*  {
        return UInt(((intVal as Long) % (x.intVal as Long)) as Int);
    } */

    /**
     * A unary plus operator.
     * A no-op.
     * @return the value of this UInt.
     */
    @Native("java", "((int) +(#this))")
    @Native("c++",  "((x10_uint) +(#0))")
    public native operator + this: UInt; /*  = this; */

    /**
     * A unary minus operator.
     * Computes the two's complement of the operand.
     * Overflows result in truncating the high bits.
     * @return the two's complement of this UInt.
     */
    @Native("java", "((int) -(#this))")
    @Native("c++",  "((x10_uint) -(#0))")
    public native operator - this: UInt; /*  = UInt(-(intVal)); */


    /**
     * A bitwise and operator.
     * Computes a bitwise AND of the two operands.
     * @param x the other UInt
     * @return the bitwise AND of this UInt and the other UInt.
     */
    @Native("java", "((#this) & (#x))")
    @Native("c++",  "((x10_uint) ((#0) & (#1)))")
    public native operator this & (x:UInt): UInt; /*  = UInt(intVal & x.intVal); */
    /**
     * A bitwise and operator (unsigned disambiguation).
     * @see #operator(UInt)&(UInt)
     */
    @Native("java", "((#this) & (#x))")
    @Native("c++",  "((x10_uint) ((#0) & (#1)))")
    public native operator (x:Int) & this: UInt; /*  = UInt(x & intVal); */
    /**
     * A bitwise and operator (unsigned disambiguation).
     * @see #operator(UInt)&(UInt)
     */
    @Native("java", "((#this) & (#x))")
    @Native("c++",  "((x10_uint) ((#0) & (#1)))")
    public native operator this & (x:Int): UInt; /*  = UInt(intVal & x); */

    /**
     * A bitwise or operator.
     * Computes a bitwise OR of the two operands.
     * @param x the other UInt
     * @return the bitwise OR of this UInt and the other UInt.
     */
    @Native("java", "((#this) | (#x))")
    @Native("c++",  "((x10_uint) ((#0) | (#1)))")
    public native operator this | (x:UInt): UInt; /*  = UInt(intVal | x.intVal); */
    /**
     * A bitwise or operator (unsigned disambiguation).
     * @see #operator(UInt)|(UInt)
     */
    @Native("java", "((#this) | (#x))")
    @Native("c++",  "((x10_uint) ((#0) | (#1)))")
    public native operator (x:Int) | this: UInt; /*  = UInt(x | intVal); */
    /**
     * A bitwise or operator (unsigned disambiguation).
     * @see #operator(UInt)|(UInt)
     */
    @Native("java", "((#this) | (#x))")
    @Native("c++",  "((x10_uint) ((#0) | (#1)))")
    public native operator this | (x:Int): UInt; /*  = UInt(intVal | x); */

    /**
     * A bitwise xor operator.
     * Computes a bitwise XOR of the two operands.
     * @param x the other UInt
     * @return the bitwise XOR of this UInt and the other UInt.
     */
    @Native("java", "((#this) ^ (#x))")
    @Native("c++",  "((x10_uint) ((#0) ^ (#1)))")
    public native operator this ^ (x:UInt): UInt; /*  = UInt(intVal ^ x.intVal); */
    /**
     * A bitwise xor operator (unsigned disambiguation).
     * @see #operator(UInt)^(UInt)
     */
    @Native("java", "((#this) ^ (#x))")
    @Native("c++",  "((x10_uint) ((#0) ^ (#1)))")
    public native operator (x:Int) ^ this: UInt; /*  = UInt(x ^ intVal); */
    /**
     * A bitwise xor operator (unsigned disambiguation).
     * @see #operator(UInt)^(UInt)
     */
    @Native("java", "((#this) ^ (#x))")
    @Native("c++",  "((x10_uint) ((#0) ^ (#1)))")
    public native operator this ^ (x:Int): UInt; /*  = UInt(intVal ^ x); */

    /**
     * A bitwise left shift operator.
     * Computes the value of the left-hand operand shifted left by the value of the right-hand operand.
     * The shift count will be masked with 0x1f before the shift is applied.
     * @param count the shift count
     * @return this UInt shifted left by count.
     */
    @Native("java", "((#this) << (#count))")  // no mask. Java defines shift as masked.
    @Native("c++",  "((x10_uint) ((#0) << (0x1f & (#1))))")
    public native operator this << (count:Int): UInt; /*  = UInt(intVal << count); */

    /**
     * A bitwise right shift operator.
     * Computes the value of the left-hand operand shifted right by the value of the right-hand operand,
     * filling the high bits with zeros.
     * The shift count will be masked with 0x1f before the shift is applied.
     * @param count the shift count
     * @return this UInt shifted right by count.
     */
    @Native("java", "((#this) >>> (#count))")  // no mask. Java defines shift as masked.
    @Native("c++",  "((x10_uint) ((#0) >> (0x1f & (#1))))")
    public native operator this >> (count:Int): UInt; /*  = UInt(intVal >>> count); */

    /**
     * A bitwise logical right shift operator (zero-fill).
     * Computes the value of the left-hand operand shifted right by the value of the right-hand operand,
     * filling the high bits with zeros.
     * The shift count will be masked with 0x1f before the shift is applied.
     * @deprecated use the right-shift operator.
     * @param count the shift count
     * @return this UInt shifted right by count with high bits zero-filled.
     */
    @Native("java", "((#this) >>> (#count))")  // no mask. Java defines shift as masked.
    @Native("c++",  "((x10_uint) ((#0) >> (0x1f & (#1))))")
    public native operator this >>> (count:Int): UInt; /*  = UInt(intVal >>> count); */

    /**
     * A bitwise complement operator.
     * Computes a bitwise complement (NOT) of the operand.
     * @return the bitwise complement of this UInt.
     */
    @Native("java", "((int) ~(#this))")
    @Native("c++",  "((x10_uint) ~(#0))")
    public native operator ~ this: UInt; /*  = UInt(~(intVal)); */


    /**
     * Coerce a given UByte to a UInt.
     * @param x the given UByte
     * @return the given UByte converted to a UInt.
     */
    //@Native("java", "((int) (#x.byteVal & 0xff))")	// boxed
    @Native("java", "((int) (((byte)(#x)) & 0xff))")
    @Native("c++",  "((x10_uint) (#1))")
    public static native operator (x:UByte): UInt; /*  = UInt(x.byteVal & 0xff); */

    /**
     * Coerce a given UShort to a UInt.
     * @param x the given UShort
     * @return the given UShort converted to a UInt.
     */
    //@Native("java", "((int) (#x.shortVal & 0xffff))") // boxed
    @Native("java", "((int) (((short)(#x)) & 0xffff))")
    @Native("c++",  "((x10_uint) (#1))")
    public static native operator (x:UShort): UInt; /*  = UInt(x.shortVal & 0xffff); */

    /**
     * Convert a given ULong to a UInt.
     * @param x the given ULong
     * @return the given ULong converted to a UInt.
     */
    //@Native("java", "((int)#x.longVal)") // boxed
    @Native("java", "((int)(long)(#x))")
    @Native("c++",  "((x10_uint) (#1))")
    public static native operator (x:ULong) as UInt; /*  = UInt(x.longVal as Int); */


    /**
     * Coerce a given Byte to a UInt.
     * @param x the given Byte
     * @return the given Byte converted to a UInt.
     */
    @Native("java", "((int)(byte)(#x))")
    @Native("c++",  "((x10_uint) (#1))")
    public static native operator (x:Byte): UInt; /*  = UInt(x); */

    /**
     * Coerce a given Short to a UInt.
     * @param x the given Short
     * @return the given Short converted to a UInt.
     */
    @Native("java", "((int)(short)(#x))")
    @Native("c++",  "((x10_uint) (#1))")
    public static native operator (x:Short): UInt; /*  = UInt(x); */

    /**
     * Convert a given Long to a UInt.
     * @param x the given Long
     * @return the given Long converted to a UInt.
     */
    @Native("java", "((int)(long)(#x))")
    @Native("c++",  "((x10_uint) (#1))")
    public static native operator (x:Long) as UInt; /*  = UInt(x as Int); */

    /**
     * Convert a given Float to a UInt.
     * @param x the given Float
     * @return the given Float converted to a UInt.
     */
    @Native("java", "((int)(float)(#x))")
    @Native("c++",  "x10aux::float_utils::toUInt(#1)")
    public static native operator (x:Float) as UInt; /*  = UInt(x as Int); */

    /**
     * Convert a given Double to a UInt.
     * @param x the given Double
     * @return the given Double converted to a UInt.
     */
    @Native("java", "((int)(double)(#x))")
    @Native("c++",  "x10aux::double_utils::toUInt(#1)")
    public static native operator (x:Double) as UInt; /*  {
        val temp : Long = x as Long;
        if (temp > 0xffffffff) return UInt(0xffffffff as Int);
        else if (temp < 0) return UInt(0);
        else return UInt(temp as Int);
    } */

    /**
     * Coerce a given Int to a UInt.
     * @param x the given Int
     * @return the given Int converted to a UInt.
     */
    @Native("java", "((int)(#x))")
    @Native("c++",  "((x10_uint) (#1))")
    public static native operator (x:Int) as UInt; /*  = UInt(x); */


    /**
     * A constant holding the minimum value a UInt can have, 0.
     */
    @Native("java", "0")
    @Native("c++", "((x10_uint)0U)")
    public static MIN_VALUE: UInt{self==0U} = 0U;

    /**
     * A constant holding the maximum value a UInt can have, 2<sup>32</sup>-1.
     */
    @Native("java", "0xffffffff")
    @Native("c++", "((x10_uint)0xffffffffU)")
    public static MAX_VALUE: UInt{self==0xffffffffU} = 0xffffffffU;


    /**
     * Returns a String representation of this UInt in the specified radix.
     * @param radix the radix to use in the String representation
     * @return a String representation of this UInt in the specified radix.
     */
    @Native("java", "java.lang.Long.toString((#this) & 0xffffffffL, #radix)")
    @Native("c++", "x10aux::int_utils::toString(#0, #1)")
    public native def toString(radix:Int): String; /*  = ((this.intVal & 0xFFFFFFFFL) as Long).toString(radix); */

    /**
     * Returns a String representation of this UInt as a hexadecimal number.
     * @return a String representation of this UInt as a hexadecimal number.
     */
    @Native("java", "java.lang.Integer.toHexString(#this)")
    @Native("c++", "x10aux::int_utils::toString(#0, 16)")
    public native def toHexString(): String;

    /**
     * Returns a String representation of this UInt as an octal number.
     * @return a String representation of this UInt as an octal number.
     */
    @Native("java", "java.lang.Integer.toOctalString(#this)")
    @Native("c++", "x10aux::int_utils::toString(#0, 8)")
    public native def toOctalString(): String;

    /**
     * Returns a String representation of this UInt as a binary number.
     * @return a String representation of this UInt as a binary number.
     */
    @Native("java", "java.lang.Integer.toBinaryString(#this)")
    @Native("c++", "x10aux::int_utils::toString(#0, 2)")
    public native def toBinaryString(): String;

    /**
     * Returns a String representation of this UInt as a decimal number.
     * @return a String representation of this UInt as a decimal number.
     */
    @Native("java", "java.lang.Long.toString((#this) & 0xffffffffL)")
    @Native("c++", "x10aux::to_string(#0)")
    public native def toString(): String; /*  = ((this.intVal & 0xFFFFFFFFL) as Long).toString(); */

    /**
     * @deprecated use {@link #parse(String,Int)} instead
     */
    @Native("java", "java.lang.Integer.parseInt(#s, #radix)")
    @Native("c++", "(x10aux::int_utils::parseUInt(#1, #2))")
    public static native def parseUInt(s:String, radix:Int): UInt; /*  //throwsNumberFormatException 
    {
        return parse(s, radix);
    } */

    /**
     * @deprecated use {@link #parse(String)} instead
     */
    @Native("java", "java.lang.Integer.parseInt(#s)")
    @Native("c++", "(x10aux::int_utils::parseUInt(#1))")
    public static native def parseUInt(s:String): UInt; /*  //throwsNumberFormatException 
    {
        return parse(s);
    } */

    /**
     * Parses the String argument as a UInt in the radix specified by the second argument.
     * @param s the String containing the UInt representation to be parsed
     * @param radix the radix to be used while parsing s
     * @return the UInt represented by the String argument in the specified radix.
     * @throws NumberFormatException if the String does not contain a parsable UInt.
     */
    @Native("java", "java.lang.Integer.parseInt(#s, #radix)")
    @Native("c++", "(x10aux::int_utils::parseUInt(#1, #2))")
    public static native def parse(s:String, radix:Int): UInt; /*  //throwsNumberFormatException 
    {
    	val l = Long.parse(s, radix);
    	if (l < 0 || l > 0xffffffffL) {
    		throw new NumberFormatException("Value out of range. Value:\"" + s + "\" Radix:" + radix);
    	}
    	return l as UInt;
    } */

    /**
     * Parses the String argument as a decimal UInt.
     * @param s the String containing the UInt representation to be parsed
     * @return the UInt represented by the String argument.
     * @throws NumberFormatException if the String does not contain a parsable UInt.
     */
    @Native("java", "java.lang.Integer.parseInt(#s)")
    @Native("c++", "(x10aux::int_utils::parseUInt(#1))")
    public static native def parse(s:String): UInt; /*  //throwsNumberFormatException 
    {
        return parse(s, 10);
    } */


    /**
     * Returns a UInt value with at most a single one-bit, in the position
     * of the highest-order ("leftmost") one-bit in this UInt value.
     * Returns zero if this UInt has no one-bits in its
     * binary representation, that is, if it is equal to zero.
     * @return a UInt value with a single one-bit, in the position of the highest-order one-bit in this UInt, or zero if this UInt is itself equal to zero.
     */
    @Native("java", "java.lang.Integer.highestOneBit(#this)")
    @Native("c++", "((x10_uint) x10aux::int_utils::highestOneBit(#0))")
    public native def highestOneBit(): UInt; /*  = UInt(this.intVal.highestOneBit()); */

    /**
     * Returns a UInt value with at most a single one-bit, in the position
     * of the lowest-order ("rightmost") one-bit in this UInt value.
     * Returns zero if this UInt has no one-bits in its
     * binary representation, that is, if it is equal to zero.
     * @return a UInt value with a single one-bit, in the position of the lowest-order one-bit in this UInt, or zero if this UInt is itself equal to zero.
     */
    @Native("java", "java.lang.Integer.lowestOneBit(#this)")
    @Native("c++", "((x10_uint) x10aux::int_utils::lowestOneBit(#0))")
    public native def lowestOneBit(): UInt; /*  = UInt(this.intVal.lowestOneBit()); */

    /**
     * Returns the number of zero bits preceding the highest-order ("leftmost")
     * one-bit in the binary representation of this UInt.
     * Returns 32 if this UInt has no one-bits in its representation,
     * in other words if it is equal to zero.
     * @return the number of zero bits preceding the highest-order one-bit in the binary representation of this UInt, or 32 if this UInt is equal to zero.
     */
    @Native("java", "java.lang.Integer.numberOfLeadingZeros(#this)")
    @Native("c++", "x10aux::int_utils::numberOfLeadingZeros(#0)")
    public native def numberOfLeadingZeros(): Int; /*  = this.intVal.numberOfLeadingZeros(); */

    /**
     * Returns the number of zero bits following the lowest-order ("rightmost")
     * one-bit in the binary representation of this UInt.
     * Returns 32 if this UInt has no one-bits in its representation,
     * in other words if it is equal to zero.
     * @return the number of zero bits following the lowest-order one-bit in the binary representation of this UInt, or 32 if this UInt is equal to zero.
     */
    @Native("java", "java.lang.Integer.numberOfTrailingZeros(#this)")
    @Native("c++", "x10aux::int_utils::numberOfTrailingZeros(#0)")
    public native def numberOfTrailingZeros(): Int; /*  = this.intVal.numberOfTrailingZeros(); */

    /**
     * Returns the number of one-bits in the binary representation
     * of this UInt.  This function is sometimes referred
     * to as the <i>population count</i>.
     * @return the number of one-bits in the binary representation of this UInt.
     */
    @Native("java", "java.lang.Integer.bitCount(#this)")
    @Native("c++", "x10aux::int_utils::bitCount(#0)")
    public native def bitCount(): Int; /*  = this.intVal.bitCount(); */

    /**
     * Returns the value obtained by rotating the binary representation
     * of this UInt left by the specified number of bits.
     * (Bits shifted out of the left hand, or high-order, side reenter on
     * the right, or low-order.)<br>
     * Note that left rotation with a negative distance is equivalent to
     * right rotation:
     * <code>rotateLeft(val, -distance) == rotateRight(val, distance)</code>.
     * Note also that rotation by any multiple of 32 is a no-op, so all but
     * the last five bits of the rotation distance can be ignored, even if
     * the distance is negative:
     * <code>rotateLeft(val, distance) == rotateLeft(val, distance &amp; 0x1F)</code>.
     * @param distance the distance to rotate by
     * @return the value obtained by rotating the binary representation of this UInt left by the specified number of bits.
     * @see #rotateRight(Int)
     */
    @Native("java", "java.lang.Integer.rotateLeft(#this, #distance)")
    @Native("c++", "x10aux::int_utils::rotateLeft(#0, #1)")
    public native def rotateLeft(distance:Int): UInt; /*  = UInt(this.intVal.rotateLeft(distance)); */

    /**
     * Returns the value obtained by rotating the binary representation
     * of this UInt right by the specified number of bits.
     * (Bits shifted out of the right hand, or low-order, side reenter on
     * the left, or high-order.)<br>
     * Note that right rotation with a negative distance is equivalent to
     * left rotation:
     * <code>rotateRight(val, -distance) == rotateLeft(val, distance)</code>.
     * Note also that rotation by any multiple of 32 is a no-op, so all but
     * the last five bits of the rotation distance can be ignored, even if
     * the distance is negative:
     * <code>rotateRight(val, distance) == rotateRight(val, distance &amp; 0x1F)</code>.
     * @param distance the distance to rotate by
     * @return the value obtained by rotating the binary representation of this UInt right by the specified number of bits.
     * @see #rotateLeft(Int)
     */
    @Native("java", "java.lang.Integer.rotateRight(#this, #distance)")
    @Native("c++", "x10aux::int_utils::rotateRight(#0, #1)")
    public native def rotateRight(distance:Int): UInt; /*  = UInt(this.intVal.rotateRight(distance)); */

    /**
     * Returns the value obtained by reversing the order of the bits in the
     * binary representation of this UInt.
     * @return the value obtained by reversing order of the bits in this UInt.
     */
    @Native("java", "java.lang.Integer.reverse(#this)")
    @Native("c++", "x10aux::int_utils::reverse(#0)")
    public native def reverse(): UInt; /*  = UInt(this.intVal.reverse()); */

    /**
     * Returns the signum function of this UInt.  The return value is 0 if
     * this UInt is zero and 1 if this UInt is non-zero.
     * @return the signum function of this UInt.
     */
    @Native("java", "(((#this)==0) ? 0 : 1)")
    @Native("c++",  "(((#0)==0U) ? 0 : 1)")
    public native def signum(): Int; /*  = (this.intVal == 0) ? 0 : 1; */

    /**
     * Returns the value obtained by reversing the order of the bytes in the
     * representation of this UInt.
     * @return the value obtained by reversing the bytes in this UInt.
     */
    @Native("java", "java.lang.Integer.reverseBytes(#this)")
    @Native("c++", "((x10_uint) x10aux::int_utils::reverseBytes((x10_int) #0))")
    public native def reverseBytes(): UInt; /*  = UInt(this.intVal.reverseBytes()); */


    /**
     * Return true if the given entity is a UInt, and this UInt is equal
     * to the given entity.
     * @param x the given entity
     * @return true if this UInt is equal to the given entity.
     */
    @Native("java", "x10.rtt.Equality.equalsequals(#this, #x)")
    @Native("c++", "x10aux::equals(#0,#1)")
    public native def equals(x:Any):Boolean;

    /**
     * Returns true if this UInt is equal to the given UInt.
     * @param x the given UInt
     * @return true if this UInt is equal to the given UInt.
     */
    @Native("java", "x10.rtt.Equality.equalsequals(#this, #x)")
    @Native("c++", "x10aux::equals(#0,#1)")
    public native def equals(x:UInt):Boolean;

    /**
    * Returns a negative Int, zero, or a positive Int if this UInt is less than, equal
    * to, or greater than the given UInt.
    * @param x the given UInt
    * @return a negative Int, zero, or a positive Int if this UInt is less than, equal
    * to, or greater than the given UInt.
    */
    @Native("java", "x10.rtt.Equality.compareTo(#this + java.lang.Integer.MIN_VALUE, #x + java.lang.Integer.MIN_VALUE)")
    @Native("c++", "x10aux::int_utils::compareTo(#0, #1)")
    public native def compareTo(x:UInt): Int; /*  = (this.intVal + Int.MIN_VALUE).compareTo(x.intVal + Int.MIN_VALUE); */
    
    @Native("java", "x10.rtt.Types.UINT.typeName()")
    @Native("c++", "x10aux::type_name(#this)")
    public native def typeName():String; /*  = "x10.lang.UInt"; */
}
