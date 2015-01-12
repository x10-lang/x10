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
 * UByte is an 8-bit unsigned integral data type, with
 * values ranging from 0 to 255, inclusive.  All of the normal
 * arithmetic and bitwise operations are defined on UByte, and UByte
 * is closed under those operations.  There are also static methods
 * that define conversions from other data types, including String,
 * as well as some UByte constants.
 */
@NativeRep("java", "byte", null, "x10.rtt.Types.UBYTE")
@NativeRep("c++", "x10_ubyte", "x10_ubyte", null)
public struct UByte implements Comparable[UByte], Arithmetic[UByte], Bitwise[UByte], Ordered[UByte] {

    /** The actual number with Byte representation */
    /* boxed representation disabled
    public val byteVal:Byte;
    public def this(value:Byte) {
        this.byteVal = value;
    } */

    /**
     * A less-than operator.
     * Compares this UByte with another UByte and returns true if this UByte is
     * strictly less than the other UByte.
     * @param x the other UByte
     * @return true if this UByte is strictly less than the other UByte.
     */
    @Native("java", "x10.runtime.impl.java.UIntUtils.lt(#this, #x)")
    @Native("c++",  "((#this) < (#x))")
    public native operator this < (x:UByte): Boolean; /*  {
        return (byteVal + Byte.MIN_VALUE) < (x.byteVal + Byte.MIN_VALUE);
    } */

    /**
     * A greater-than operator.
     * Compares this UByte with another UByte and returns true if this UByte is
     * strictly greater than the other UByte.
     * @param x the other UByte
     * @return true if this UByte is strictly greater than the other UByte.
     */
    @Native("java", "x10.runtime.impl.java.UIntUtils.gt(#this, #x)")
    @Native("c++",  "((#this) > (#x))")
    public native operator this > (x:UByte): Boolean; /*  {
        return (byteVal + Byte.MIN_VALUE) > (x.byteVal + Byte.MIN_VALUE);
    } */

    /**
     * A less-than-or-equal-to operator.
     * Compares this UByte with another UByte and returns true if this UByte is
     * less than or equal to the other UByte.
     * @param x the other UByte
     * @return true if this UByte is less than or equal to the other UByte.
     */
    @Native("java", "x10.runtime.impl.java.UIntUtils.le(#this, #x)")
    @Native("c++",  "((#this) <= (#x))")
    public native operator this <= (x:UByte): Boolean; /*  {
        return (byteVal + Byte.MIN_VALUE) <= (x.byteVal + Byte.MIN_VALUE);
    } */

    /**
     * A greater-than-or-equal-to operator.
     * Compares this UByte with another UByte and returns true if this UByte is
     * greater than or equal to the other UByte.
     * @param x the other UByte
     * @return true if this UByte is greater than or equal to the other UByte.
     */
    @Native("java", "x10.runtime.impl.java.UIntUtils.ge(#this, #x)")
    @Native("c++",  "((#this) >= (#x))")
    public native operator this >= (x:UByte): Boolean; /*  {
        return (byteVal + Byte.MIN_VALUE) >= (x.byteVal + Byte.MIN_VALUE);
    } */


    /**
     * A binary plus operator.
     * Computes the result of the addition of the two operands.
     * Overflows result in truncating the high bits.
     * @param x the other UByte
     * @return the sum of this UByte and the other UByte.
     */
    @Native("java", "((byte) ((#this) + (#x)))")
    @Native("c++",  "((x10_ubyte) ((#this) + (#x)))")
    public native operator this + (x:UByte): UByte; /*  = UByte(byteVal + x.byteVal); */

    /**
     * A binary minus operator.
     * Computes the result of the subtraction of the two operands.
     * Overflows result in truncating the high bits.
     * @param x the other UByte
     * @return the difference of this UByte and the other UByte.
     */
    @Native("java", "((byte) ((#this) - (#x)))")
    @Native("c++",  "((x10_ubyte) ((#this) - (#x)))")
    public native operator this - (x:UByte): UByte; /*  = UByte(byteVal - x.byteVal); */

    /**
     * A binary multiply operator.
     * Computes the result of the multiplication of the two operands.
     * Overflows result in truncating the high bits.
     * @param x the other UByte
     * @return the product of this UByte and the other UByte.
     */
    @Native("java", "((byte) ((#this) * (#x)))")
    @Native("c++",  "((x10_ubyte) ((#this) * (#x)))")
    public native operator this * (x:UByte): UByte; /*  = UByte(byteVal * x.byteVal); */

    /**
     * A binary divide operator.
     * Computes the result of the division of the two operands.
     * @param x the other UByte
     * @return the quotient of this UByte and the other UByte.
     */
    @Native("java", "((byte)((0xff & #this) / (0xff & #x)))")
    @Native("c++",  "((x10_ubyte) ((#this) / ::x10aux::zeroCheck(#x)))")
    public native operator this / (x:UByte): UByte; /* {
        return UByte(((byteVal as Long) / (x.byteVal as Long)) as Byte);
    } */

    /**
     * A binary remainder operator.
     * Computes a remainder from the division of the two operands.
     * @param x the other UByte
     * @return the remainder from dividing this UByte by the other UByte.
     */
    @Native("java", "((byte)((0xff & #this) % (0xff & #x)))")
    @Native("c++",  "((x10_ubyte) ((#this) % ::x10aux::zeroCheck(#x)))")
    public native operator this % (x:UByte): UByte; /*  {
        return UByte(((byteVal as Long) % (x.byteVal as Long)) as Byte);
    } */

    /**
     * A unary plus operator.
     * A no-op.
     * @return the value of this UByte.
     */
    @Native("java", "((byte) +(#this))")
    @Native("c++",  "((x10_ubyte) +(#this))")
    public native operator + this: UByte; /*  = this; */

    /**
     * A unary minus operator.
     * Computes the two's complement of the operand.
     * Overflows result in truncating the high bits.
     * @return the two's complement of this UByte.
     */
    @Native("java", "((byte) -(#this))")
    @Native("c++",  "((x10_ubyte) -(#this))")
    public native operator - this: UByte; /*  = UByte(-(byteVal)); */


    /**
     * A bitwise and operator.
     * Computes a bitwise AND of the two operands.
     * @param x the other UByte
     * @return the bitwise AND of this UByte and the other UByte.
     */
    @Native("java", "((byte) ((#this) & (#x)))")
    @Native("c++",  "((x10_ubyte) ((#this) & (#x)))")
    public native operator this & (x:UByte): UByte; /*  = UByte(byteVal & x.byteVal); */
    /**
     * A bitwise and operator (unsigned disambiguation).
     * @see #operator(UByte)&(UByte)
     */
    @Native("java", "((byte) ((#this) & (#x)))")
    @Native("c++",  "((x10_ubyte) ((#this) & (#x)))")
    public native operator (x:Byte) & this: UByte; /*  = UByte(x & byteVal); */
    /**
     * A bitwise and operator (unsigned disambiguation).
     * @see #operator(UByte)&(UByte)
     */
    @Native("java", "((byte) ((#this) & (#x)))")
    @Native("c++",  "((x10_ubyte) ((#this) & (#x)))")
    public native operator this & (x:Byte): UByte; /*  = UByte(byteVal & x); */

    /**
     * A bitwise or operator.
     * Computes a bitwise OR of the two operands.
     * @param x the other UByte
     * @return the bitwise OR of this UByte and the other UByte.
     */
    @Native("java", "((byte) ((#this) | (#x)))")
    @Native("c++",  "((x10_ubyte) ((#this) | (#x)))")
    public native operator this | (x:UByte): UByte; /*  = UByte(byteVal | x.byteVal); */
    /**
     * A bitwise or operator (unsigned disambiguation).
     * @see #operator(UByte)|(UByte)
     */
    @Native("java", "((byte) ((#this) | (#x)))")
    @Native("c++",  "((x10_ubyte) ((#this) | (#x)))")
    public native operator (x:Byte) | this: UByte; /*  = UByte(x | byteVal); */
    /**
     * A bitwise or operator (unsigned disambiguation).
     * @see #operator(UByte)|(UByte)
     */
    @Native("java", "((byte) ((#this) | (#x)))")
    @Native("c++",  "((x10_ubyte) ((#this) | (#x)))")
    public native operator this | (x:Byte): UByte; /*  = UByte(byteVal | x); */

    /**
     * A bitwise xor operator.
     * Computes a bitwise XOR of the two operands.
     * @param x the other UByte
     * @return the bitwise XOR of this UByte and the other UByte.
     */
    @Native("java", "((byte) ((#this) ^ (#x)))")
    @Native("c++",  "((x10_ubyte) ((#this) ^ (#x)))")
    public native operator this ^ (x:UByte): UByte; /*  = UByte(byteVal ^ x.byteVal); */
    /**
     * A bitwise xor operator (unsigned disambiguation).
     * @see #operator(UByte)^(UByte)
     */
    @Native("java", "((byte) ((#this) ^ (#x)))")
    @Native("c++",  "((x10_ubyte) ((#this) ^ (#x)))")
    public native operator (x:Byte) ^ this: UByte; /*  = UByte(x ^ byteVal); */
    /**
     * A bitwise xor operator (unsigned disambiguation).
     * @see #operator(UByte)^(UByte)
     */
    @Native("java", "((byte) ((#this) ^ (#x)))")
    @Native("c++",  "((x10_ubyte) ((#this) ^ (#x)))")
    public native operator this ^ (x:Byte): UByte; /*  = UByte(byteVal ^ x); */

    /**
     * A bitwise left shift operator.
     * Computes the value of the left-hand operand shifted left by the value of the right-hand operand.
     * The shift count will be masked with 0x7 before the shift is applied.
     * @param count the shift count
     * @return this UByte shifted left by count.
     */
    @Native("java", "((byte) ((#this) << (0x7 & (int)(#count))))")
    @Native("c++",  "((x10_ubyte) ((#this) << (0x7 & (x10_int)(#count))))")
    public native operator this << (count:Long): UByte; /*  = UByte(byteVal << (0x7 & (count as Int)); */

    /**
     * A bitwise right shift operator.
     * Computes the value of the left-hand operand shifted right by the value of the right-hand operand,
     * filling the high bits with zeros.
     * The shift count will be masked with 0x7 before the shift is applied.
     * @param count the shift count
     * @return this UByte shifted right by count.
     */
    @Native("java", "((byte) ((0xff & #this) >>> (0x7 & (int)(#count))))")
    @Native("c++",  "((x10_ubyte) ((#this) >> (0x7 & (x10_int)(#count))))")
    public native operator this >> (count:Long): UByte; /*  = UByte(byteVal >>> (0x7 & count as Int)); */

    /**
     * A bitwise logical right shift operator (zero-fill).
     * Computes the value of the left-hand operand shifted right by the value of the right-hand operand,
     * filling the high bits with zeros.
     * The shift count will be masked with 0x7 before the shift is applied.
     * @deprecated use the right-shift operator.
     * @param count the shift count
     * @return this UByte shifted right by count with high bits zero-filled.
     */
    @Native("java", "((byte) ((0xff & #this) >>> (0x7 & (int)(#count))))")
    @Native("c++",  "((x10_ubyte) ((#this) >> (0x7 & (int)(#count))))")
    public native operator this >>> (count:Long): UByte; /*  = UByte(byteVal >>> (0x7 & count as Int)); */

    /**
     * A bitwise complement operator.
     * Computes a bitwise complement (NOT) of the operand.
     * @return the bitwise complement of this UByte.
     */
    @Native("java", "((byte) ~(#this))")
    @Native("c++",  "((x10_ubyte) ~(#this))")
    public native operator ~ this: UByte; /*  = UByte(~(byteVal)); */

    /**
     * Convert a given UShort to a UByte.
     * @param x the given UShort
     * @return the given UShort converted to a UByte.
     */
    @Native("java", "((byte)(short)(#x))")
    @Native("c++",  "((x10_ubyte) (#x))")
    public static native operator (x:UShort) as UByte; /*  = UByte(x.shortVal as Byte); */

    /**
     * Convert a given UInt to a UByte.
     * @param x the given UInt
     * @return the given UInt converted to a UByte.
     */
    @Native("java", "((byte)(int)(#x))")
    @Native("c++",  "((x10_ubyte) (#x))")
    public static native operator (x:UInt) as UByte; /*  = UByte((x as Int) as Byte); */

    /**
     * Convert a given ULong to a UByte.
     * @param x the given ULong
     * @return the given ULong converted to a UByte.
     */
    @Native("java", "((byte)(long)(#x))")
    @Native("c++",  "((x10_ubyte) (#x))")
    public static native operator (x:ULong) as UByte; /*  = UByte(x.longVal as Byte); */


    /**
     * Convert a given Short to a UByte.
     * @param x the given Short
     * @return the given Short converted to a UByte.
     */
    @Native("java", "((byte)(short)(#x))")
    @Native("c++",  "((x10_ubyte) (#x))")
    public static native operator (x:Short) as UByte; /*  = UByte(x as Byte); */

    /**
     * Convert a given Int to a UByte.
     * @param x the given Int
     * @return the given Int converted to a UByte.
     */
    @Native("java", "((byte)(int)(#x))")
    @Native("c++",  "((x10_ubyte) (#x))")
    public static native operator (x:Int) as UByte; /*  = UByte(x as Byte); */

    /**
     * Convert a given Long to a UByte.
     * @param x the given Long
     * @return the given Long converted to a UByte.
     */
    @Native("java", "((byte)(long)(#x))")
    @Native("c++",  "((x10_ubyte) (#x))")
    public static native operator (x:Long) as UByte; /*  = UByte(x as Byte); */

    /**
     * Convert a given Float to a UByte.
     * @param x the given Float
     * @return the given Float converted to a UByte.
     */
    @Native("java", "x10.runtime.impl.java.FloatUtils.toUByte(#x)")
    @Native("c++",  "::x10::lang::FloatNatives::toUByte(#x)")
    @Native("cuda", "((x10_ubyte) (#x))")
    public static native operator (x:Float) as UByte; /*  {
        val temp : Int = x as Int;
        if (temp > 0xff) return UByte(0xff as Byte);
        else if (temp < 0) return UByte(0);
        else return UByte(temp as Byte);
    } */

    /**
     * Convert a given Double to a UByte.
     * @param x the given Double
     * @return the given Double converted to a UByte.
     */
    @Native("java", "x10.runtime.impl.java.DoubleUtils.toUByte(#x)")
    @Native("c++",  "::x10::lang::DoubleNatives::toUByte(#x)")
    @Native("cuda", "((x10_ubyte) (#x))")
    public static native operator (x:Double) as UByte; /*  {
        val temp : Int = x as Int;
        if (temp > 0xff) return UByte(0xff as Byte);
        else if (temp < 0) return UByte(0);
        else return UByte(temp as Byte);
    } */

    /**
     * Coerce a given Byte to a UByte.
     * @param x the given Byte
     * @return the given Byte converted to a UByte.
     */
    @Native("java", "((byte)(#x))")
    @Native("c++",  "((x10_ubyte) (#x))")
    public static native operator (x:Byte) as UByte; /*  = UByte(x); */


    /**
     * A constant holding the minimum value a UByte can have, 0.
     */
    @Native("java", "((byte)0)")
    @Native("c++", "((x10_ubyte)0U)")
    public static MIN_VALUE: UByte{self==0UY} = 0UY;

    /**
     * A constant holding the maximum value a UByte can have, 2<sup>8</sup>-1.
     */
    @Native("java", "((byte)0xff)")
    @Native("c++", "((x10_ubyte)0xffU)")
    public static MAX_VALUE: UByte{self==0xffUY} = 0xffUY;


    /**
     * Returns a String representation of this UByte in the specified radix.
     * @param radix the radix to use in the String representation
     * @return a String representation of this UByte in the specified radix.
     */
    @Native("java", "java.lang.Integer.toString((#this) & 0xff, #radix)")
    @Native("c++", "::x10::lang::UByteNatives::toString(#this, #radix)")
    public native def toString(radix:Int): String;

    /**
     * Returns a String representation of this UByte as a hexadecimal number.
     * @return a String representation of this UByte as a hexadecimal number.
     */
    @Native("java", "java.lang.Integer.toHexString((#this) & 0xff)")
    @Native("c++", "::x10::lang::UByteNatives::toString(#this, 16)")
    public native def toHexString(): String;

    /**
     * Returns a String representation of this UByte as an octal number.
     * @return a String representation of this UByte as an octal number.
     */
    @Native("java", "java.lang.Integer.toOctalString((#this) & 0xff)")
    @Native("c++", "::x10::lang::UByteNatives::toString(#this, 8)")
    public native def toOctalString(): String;

    /**
     * Returns a String representation of this UByte as a binary number.
     * @return a String representation of this UByte as a binary number.
     */
    @Native("java", "java.lang.Integer.toBinaryString((#this) & 0xff)")
    @Native("c++", "::x10::lang::UByteNatives::toString(#this, 2)")
    public native def toBinaryString(): String;

    /**
     * Returns a String representation of this UByte as a decimal number.
     * @return a String representation of this UByte as a decimal number.
     */
    @Native("java", "java.lang.Integer.toString((#this) & 0xff)")
    @Native("c++", "::x10aux::to_string(#this)")
    public native def toString(): String;

    /**
     * @deprecated use {@link #parse(String,Int)} instead
     */
    @Native("java", "((byte)(java.lang.Integer.parseInt(#s, #radix) & 0xff))")
    @Native("c++", "(::x10::lang::UByteNatives::parseUByte(#s, #radix))")
    public static def parseUByte(s:String, radix:Int): UByte //throws NumberFormatException 
    {
        return parse(s, radix);
    }

    /**
     * @deprecated use {@link #parse(String)} instead
     */
    @Native("java", "((byte)(java.lang.Integer.parseInt(#s) & 0xff))")
    @Native("c++", "(::x10::lang::UByteNatives::parseUByte(#s))")
    public static def parseUByte(s:String): UByte //throws NumberFormatException 
    {
        return parse(s);
    }

    /**
     * Parses the String argument as a UByte in the radix specified by the second argument.
     * @param s the String containing the UByte representation to be parsed
     * @param radix the radix to be used while parsing s
     * @return the UByte represented by the String argument in the specified radix.
     * @throws NumberFormatException if the String does not contain a parsable UByte.
     */
    @Native("java", "((byte)(java.lang.Integer.parseInt(#s, #radix) & 0xff))")
    @Native("c++", "(::x10::lang::UByteNatives::parseUByte(#s, #radix))")
    public static native def parse(s:String, radix:Int): UByte; /*  //throws NumberFormatException 
    {
    	val i = Int.parse(s, radix);
    	if (i < 0 || i > 0xff) {
    		throw new NumberFormatException("Value out of range. Value:\"" + s + "\" Radix:" + radix);
    	}
    	return i as UByte;
    } */

    /**
     * Parses the String argument as a decimal UByte.
     * @param s the String containing the UByte representation to be parsed
     * @return the UByte represented by the String argument.
     * @throws NumberFormatException if the String does not contain a parsable UByte.
     */
    @Native("java", "((byte)(java.lang.Integer.parseInt(#s) & 0xff))")
    @Native("c++", "(::x10::lang::UByteNatives::parseUByte(#s))")
    public static native def parse(s:String): UByte; /*  //throws NumberFormatException 
    {
        return parse(s, 10);
    } */


    /**
     * Returns the value obtained by reversing the order of the bits in the
     * binary representation of this UByte.
     * @return the value obtained by reversing order of the bits in this UByte.
     */
    @Native("java", "((byte)(java.lang.Integer.reverse(#this)>>>24))")
    @Native("c++", "((x10_ubyte)(::x10::lang::IntNatives::reverse(#this)>>24))")
    public native def reverse(): UByte; /*  = UByte(this.byteVal.reverse()); */

    /**
     * Returns the signum function of this UByte.  The return value is 0 if
     * this UByte is zero and 1 if this UByte is non-zero.
     * @return the signum function of this UByte.
     */
    @Native("java", "(((#this)==0) ? 0 : 1)")
    @Native("c++",  "(((#this)==0U) ? 0 : 1)")
    public native def signum(): Int; /*  = (this.byteVal == 0y) ? 0 : 1; */


    /**
     * Return true if the given entity is a UByte, and this UByte is equal
     * to the given entity.
     * @param x the given entity
     * @return true if this UByte is equal to the given entity.
     */
    @Native("java", "x10.rtt.Equality.equalsequals(#this, #x)")
    @Native("c++", "::x10aux::equals(#this, #x)")
    public native def equals(x:Any):Boolean; /*  = x instanceof UByte && (x as UByte).byteVal == this.byteVal; */

    /**
     * Returns true if this UByte is equal to the given UByte.
     * @param x the given UByte
     * @return true if this UByte is equal to the given UByte.
     */
    @Native("java", "x10.rtt.Equality.equalsequals(#this, #x)")
    @Native("c++", "::x10aux::equals(#this, #x)")
    public native def equals(x:UByte):Boolean; /*  = this.byteVal == x.byteVal; */

    /**
    * Returns a negative Int, zero, or a positive Int if this UByte is less than, equal
    * to, or greater than the given UByte.
    * @param x the given UByte
    * @return a negative Int, zero, or a positive Int if this UByte is less than, equal
    * to, or greater than the given UByte.
    */
    @Native("java", "x10.rtt.Equality.compareTo((byte)(#this + java.lang.Byte.MIN_VALUE), (byte)(#x + java.lang.Byte.MIN_VALUE))")
    @Native("c++", "::x10::lang::UByteNatives::compareTo(#this, #x)")
    public native def compareTo(x:UByte): Int; /*  = (this.byteVal + Byte.MIN_VALUE).compareTo(x.byteVal + Byte.MIN_VALUE); */

    @Native("java", "x10.rtt.Types.UBYTE.typeName()")
    @Native("c++", "::x10aux::type_name(#this)")
    public native def typeName():String; /*  = "x10.lang.UByte"; */
}
