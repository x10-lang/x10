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

package safe.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;
import safe.util.Ordered;
import safe.lang.Boolean;
import x10.compiler.Opaque;

/**
 * Int is a 32-bit signed two's complement integral data type, with
 * values ranging from -2147483648 to 2147483647, inclusive.  All of the normal
 * arithmetic and bitwise operations are defined on Int, and Int
 * is closed under those operations.  There are also static methods
 * that define conversions from other data types, including String,
 * as well as some Int constants.
 */
@NativeRep("java", "int", null, "x10.rtt.Types.INT")
//                 v-- when used
@NativeRep("c++", "x10_int", "x10_int", null)
//                            ^ when constructed
// TODO: ad constraint to Arithmetic interface for division

public struct Int implements Comparable[Int], Ordered[Int] {

	
    /**
     * A less-than operator.
     * Compares this Int with another Int and returns true if this Int is
     * strictly less than the other Int.
     * @param x the other Int
     * @return true if this Int is strictly less than the other Int.
     */
    @Native("java", "((#this) < (#x))")
    @Native("c++",  "((#0) < (#1))")
    public native operator this < (x:Int): Boolean {self == I.lt(this, x)};
    
    @Opaque("lt")
	public static native property def lt(x: Int, y:Int) :Boolean; 


    /**
     * A greater-than operator.
     * Compares this Int with another Int and returns true if this Int is
     * strictly greater than the other Int.
     * @param x the other Int
     * @return true if this Int is strictly greater than the other Int.
     */
    @Native("java", "((#this) > (#x))")
    @Native("c++",  "((#0) > (#1))")
    public native operator this > (x:Int): Boolean {self == I.gt(this, x)};

    @Opaque("gt")
	public static native property def gt(x: Int, y:Int) :Boolean; 

    /**
     * A less-than-or-equal-to operator.
     * Compares this Int with another Int and returns true if this Int is
     * less than or equal to the other Int.
     * @param x the other Int
     * @return true if this Int is less than or equal to the other Int.
     */
    @Native("java", "((#this) <= (#x))")
    @Native("c++",  "((#0) <= (#1))")
    public native operator this <= (x:Int): Boolean {self == I.le(this,x)};
    @Opaque("le")
	public static native property def le(x: Int, y:Int) :Boolean; 

    /**
     * A greater-than-or-equal-to operator.
     * Compares this Int with another Int and returns true if this Int is
     * greater than or equal to the other Int.
     * @param x the other Int
     * @return true if this Int is greater than or equal to the other Int.
     */
    @Native("java", "((#this) >= (#x))")
    @Native("c++",  "((#0) >= (#1))")
    public native operator this >= (x:Int): Boolean {self == I.ge(this, x)};
    @Opaque("ge")
	public static native property def ge(x: Int, y:Int) :Boolean; 


    /**
     * A binary plus operator.
     * Computes the result of the addition of the two operands.
     * Overflows result in truncating the high bits.
     * @param x the other Int
     * @return the sum of this Int and the other Int.
     */
    @Native("java", "((#this) + (#x))")
    @Native("c++",  "((x10_int) ((#0) + (#1)))")
    public native operator this + (x:Int): Int{self == I.add(this,x)};
    @Opaque("add")
	public static native property def add(x: Int, y:Int) : Int; 

    /**
     * A binary minus operator.
     * Computes the result of the subtraction of the two operands.
     * Overflows result in truncating the high bits.
     * @param x the other Int
     * @return the difference of this Int and the other Int.
     */
    @Native("java", "((#this) - (#x))")
    @Native("c++",  "((x10_int) ((#0) - (#1)))")
    public native operator this - (x:Int): Int{self == I.sub(this,x)};
    @Opaque("sub")
	public static native property def sub(x: Int, y:Int) : Int; 


    /**
     * A binary multiply operator.
     * Computes the result of the multiplication of the two operands.
     * Overflows result in truncating the high bits.
     * @param x the other Int
     * @return the product of this Int and the other Int.
     */
    @Native("java", "((#this) * (#x))")
    @Native("c++",  "((x10_int) ((#0) * (#1)))")
    public native operator this * (x:Int): Int{self == I.mult(this,x)};
    @Opaque("mult")
	public static native property def mult(x: Int, y:Int) : Int; 


    /**
     * A binary divide operator.
     * Computes the result of the division of the two operands.
     * @param x the other Int
     * @return the quotient of this Int and the other Int.
     */
    @Native("java", "((#this) / (#x))")
    @Native("c++",  "((x10_int) ((#0) / x10aux::zeroCheck(#1)))")
    public native operator this / (x:Int/*{self!= I.asInt(0)}*/): Int{self == I.div(this,x)};
    @Opaque("div")
	public static native property def div(x: Int, y:Int) : Int; 


    /**
     * A binary remainder operator.
     * Computes a remainder from the division of the two operands.
     * @param x the other Int
     * @return the remainder from dividing this Int by the other Int.
     */
    @Native("java", "((#this) % (#x))")
    @Native("c++",  "((x10_int) ((#0) % x10aux::zeroCheck(#1)))")
    public native operator this % (x:Int/*{self!= I.asInt(0)}*/): Int{self == I.mod(this,x)};
    @Opaque("mod")
	public static native property def mod(x: Int, y:Int) : Int; 


    /**
     * A unary plus operator.
     * A no-op.
     * @return the value of this Int.
     */
    @Native("java", "(+(#this))")
    @Native("c++",  "((x10_int) +(#0))")
    public native operator + this: Int {self == this};

    /**
     * A unary minus operator.
     * Negates the operand.
     * Overflows result in truncating the high bits.
     * @return the negated value of this Int.
     */
    @Native("java", "(-(#this))")
    @Native("c++",  "((x10_int) -(#0))")
    public native operator - this: Int {self == I.neg(this)};
    @Opaque("neg")
	public static native property def neg(x: Int) : Int; 


    /**
     * A bitwise and operator.
     * Computes a bitwise AND of the two operands.
     * @param x the other Int
     * @return the bitwise AND of this Int and the other Int.
     */
    @Native("java", "((#this) & (#x))")
    @Native("c++",  "((x10_int) ((#0) & (#1)))")
    public native operator this & (x:Int): Int{self == I.iand(this,x)};
    @Opaque("iand")
	public static native property def iand(x: Int, y:Int) : Int; 


    /**
     * A bitwise or operator.
     * Computes a bitwise OR of the two operands.
     * @param x the other Int
     * @return the bitwise OR of this Int and the other Int.
     */
    @Native("java", "((#this) | (#x))")
    @Native("c++",  "((x10_int) ((#0) | (#1)))")
    public native operator this | (x:Int): Int{self == I.ior(this,x)};
    @Opaque("ior")
	public static native property def ior(x: Int, y:Int) : Int; 


    /**
     * A bitwise xor operator.
     * Computes a bitwise XOR of the two operands.
     * @param x the other Int
     * @return the bitwise XOR of this Int and the other Int.
     */
    @Native("java", "((#this) ^ (#x))")
    @Native("c++",  "((x10_int) ((#0) ^ (#1)))")
    public native operator this ^ (x:Int): Int{self == I.ixor(this,x)};
    @Opaque("ixor")
	public static native property def ixor(x: Int, y:Int) : Int; 


    /**
     * A bitwise left shift operator.
     * Computes the value of the left-hand operand shifted left by the value of the right-hand operand.
     * The shift count will be masked with 0x1f before the shift is applied.
     * @param count the shift count
     * @return this Int shifted left by count.
     */
    @Native("java", "((#this) << (#count))") // no mask. Java defines shift as masked.
    @Native("c++",  "((x10_int) ((#0) << (0x1f & (#1))))")
    public native operator this << (count:Int): Int{self == I.shl(this,count)};
    @Opaque("shl")
	public static native property def shl(x: Int, y:Int) : Int; 


    /**
     * A bitwise right shift operator.
     * Computes the value of the left-hand operand shifted right by the value of the right-hand operand,
     * replicating the sign bit into the high bits.
     * The shift count will be masked with 0x1f before the shift is applied.
     * @param count the shift count
     * @return this Int shifted right by count.
     */
    @Native("java", "((#this) >> (#count))") // no mask. Java defines shift as masked.
    @Native("c++",  "((x10_int) ((#0) >> (0x1f & (#1))))")
    public native operator this >> (count:Int): Int{self == I.shr(this,count)};
    @Opaque("shr")
	public static native property def shr(x: Int, y:Int) : Int; 


    /**
     * A bitwise logical right shift operator (zero-fill).
     * Computes the value of the left-hand operand shifted right by the value of the right-hand operand,
     * filling the high bits with zeros.
     * The shift count will be masked with 0x1f before the shift is applied.
     * @deprecated use the right-shift operator and unsigned conversions instead.
     * @param count the shift count
     * @return this Int shifted right by count with high bits zero-filled.
     */
    @Native("java", "((#this) >>> (#count))") // no mask. Java defines shift as masked.
    @Native("c++",  "((x10_int) ((x10_uint) (#0) >> (0x1f & (#1))))")
    public native operator this >>> (count:Int): Int{self == I.lshr(this,count)};
    @Opaque("lshr")
	public static native property def lshr(x: Int, y:Int) : Int; 


    /**
     * A bitwise complement operator.
     * Computes a bitwise complement (NOT) of the operand.
     * @return the bitwise complement of this Int.
     */
    @Native("java", "(~(#this))")
    @Native("c++",  "((x10_int) ~(#0))")
    public native operator ~ this: Int{self == I.inot(this)};
    @Opaque("inot")
	public static native property def inot(x: Int) : Int; 



    /**
     * Coerce a given Byte to an Int.
     * @param x the given Byte
     * @return the given Byte converted to an Int.
     */
    @Native("java", "((int)(byte)(#x))")
    @Native("c++",  "((x10_int) (#1))")
    public native static operator (x:Byte): Int;

    /**
     * Coerce a given Short to an Int.
     * @param x the given Short
     * @return the given Short converted to an Int.
     */
    @Native("java", "((int)(short)(#x))")
    @Native("c++",  "((x10_int) (#1))")
    public native static operator (x:Short): Int;

    /**
     * Convert a given Long to an Int.
     * @param x the given Long
     * @return the given Long converted to an Int.
     */
    @Native("java", "((int)(long)(#x))")
    @Native("c++",  "((x10_int) (#1))")
    public native static operator (x:Long) as Int;

    /**
     * Convert a given Float to an Int.
     * @param x the given Float
     * @return the given Float converted to an Int.
     */
    @Native("java", "((int)(float)(#x))")
    @Native("c++",  "x10aux::float_utils::toInt(#1)")
    @Native("cuda",  "((x10_int)#1)")
    public native static operator (x:Float) as Int;

    /**
     * Convert a given Double to an Int.
     * @param x the given Double
     * @return the given Double converted to an Int.
     */
    @Native("java", "((int)(double)(#x))")
    @Native("c++",  "x10aux::double_utils::toInt(#1)")
    public native static operator (x:Double) as Int;

    /**
     * Coerce a given UByte to an Int.
     * @param x the given UByte
     * @return the given UByte converted to an Int.
     */
    @Native("java", "((int)#x)")
    @Native("c++",  "((x10_int) (#1))")
    public native static operator (x:UByte): Int;

    /**
     * Coerce a given UShort to an Int.
     * @param x the given UShort
     * @return the given UShort converted to an Int.
     */
    @Native("java", "((int)#x)")
    @Native("c++",  "((x10_int) (#1))")
    public native static operator (x:UShort): Int;

    /**
     * Convert a given UInt to an Int.
     * @param x the given UInt
     * @return the given UInt converted to an Int.
     */
    @Native("java", "((int)#x)")
    @Native("c++",  "((x10_int) (#1))")
    public native static operator (x:UInt) as Int;

    /**
     * A constant holding the minimum value an Int can have, -2<sup>31</sup>.
     */
    @Native("java", "java.lang.Integer.MIN_VALUE")
    @Native("c++", "((x10_int)0x80000000)")
    property static native MIN_VALUE(): Int {self == I.asInt(0x80000000)};

    /**
     * A constant holding the maximum value an Int can have, 2<sup>31</sup>-1.
     */
    @Native("java", "java.lang.Integer.MAX_VALUE")
    @Native("c++", "((x10_int)0x7fffffff)")
    property static native MAX_VALUE(): Int {self == I.asInt(0x7fffffff)}; 


    /**
     * Returns a String representation of this Int in the specified radix.
     * @param radix the radix to use in the String representation
     * @return a String representation of this Int in the specified radix.
     */
    @Native("java", "java.lang.Integer.toString(#this, #radix)")
    @Native("c++", "x10aux::int_utils::toString(#0, #1)")
    public native def toString(radix:Int): String;

    /**
     * Returns a String representation of this Int in base 16.
     * This method is simply a synonym for toString(16).
     * In particular toHexString(-20) will print -14; to print
     * the 32 bit two's complement hexadecimal representation of an Int v
     * use (v as UInt).toHexString().  
     *
     * @return a String representation of this Int in base 16.
     */
    @Native("java", "java.lang.Integer.toString(#this, 16)")
    @Native("c++", "x10aux::int_utils::toString(#0, 16)")
    public native def toHexString(): String;

    /**
     * Returns a String representation of this Int in base 8.
     * This method is simply a synonym for toString(8).
     * In particular toOctalString(-20) will print -24; to print
     * the 32 bit two's complement octal representation of an Int v
     * use (v as UInt).toHexString().  
     *
     * @return a String representation of this Int in base 8.
     */
    @Native("java", "java.lang.Integer.toString(#this, 8)")
    @Native("c++", "x10aux::int_utils::toString(#0, 8)")
    public native def toOctalString(): String;

    /**
     * Returns a String representation of this Int in base 2.
     * This method is simply a synonym for toString(2).
     * In particular toBinaryString(-20) will print -10100; to print
     * the 32 bit two's complement binary representation of an Int v
     * use (v as UInt).toBinaryString().  
     *
     * @return a String representation of this Int in base 2.
     */
    @Native("java", "java.lang.Integer.toString(#this, 2)")
    @Native("c++", "x10aux::int_utils::toString(#0, 2)")
    public native def toBinaryString(): String;

    /**
     * Returns a String representation of this Int as a decimal number.
     * @return a String representation of this Int as a decimal number.
     */
    @Native("java", "java.lang.Integer.toString(#this)")
    @Native("c++", "x10aux::to_string(#0)")
    public native def toString(): String;

    /**
     * @deprecated use {@link #parse(String,Int)} instead
     */
    @Native("java", "java.lang.Integer.parseInt(#s, #radix)")
    @Native("c++", "x10aux::int_utils::parseInt(#1, #2)")
    public native static def parseInt(s:String, radix:Int): Int; //throwsNumberFormatException;

    /**
     * @deprecated use {@link #parse(String)} instead
     */
    @Native("java", "java.lang.Integer.parseInt(#s)")
    @Native("c++", "x10aux::int_utils::parseInt(#1)")
    public native static def parseInt(s:String): Int; //throwsNumberFormatException;

    /**
     * Parses the String argument as an Int in the radix specified by the second argument.
     * @param s the String containing the Int representation to be parsed
     * @param radix the radix to be used while parsing s
     * @return the Int represented by the String argument in the specified radix.
     * @throws NumberFormatException if the String does not contain a parsable Int.
     */
    @Native("java", "java.lang.Integer.parseInt(#s, #radix)")
    @Native("c++", "x10aux::int_utils::parseInt(#1, #2)")
    public native static def parse(s:String, radix:Int): Int; //throwsNumberFormatException;

    /**
     * Parses the String argument as a decimal Int.
     * @param s the String containing the Int representation to be parsed
     * @return the Int represented by the String argument.
     * @throws NumberFormatException if the String does not contain a parsable Int.
     */
    @Native("java", "java.lang.Integer.parseInt(#s)")
    @Native("c++", "x10aux::int_utils::parseInt(#1)")
    public native static def parse(s:String): Int; //throwsNumberFormatException;


    /**
     * Returns an Int value with at most a single one-bit, in the position
     * of the highest-order ("leftmost") one-bit in this Int value.
     * Returns zero if this Int has no one-bits in its two's complement
     * binary representation, that is, if it is equal to zero.
     * @return an Int value with a single one-bit, in the position of the highest-order one-bit in this Int, or zero if this Int is itself equal to zero.
     */
    @Native("java", "java.lang.Integer.highestOneBit(#this)")
    @Native("c++", "x10aux::int_utils::highestOneBit(#0)")
    public native def highestOneBit(): Int;

    /**
     * Returns an Int value with at most a single one-bit, in the position
     * of the lowest-order ("rightmost") one-bit in this Int value.
     * Returns zero if this Int has no one-bits in its two's complement
     * binary representation, that is, if it is equal to zero.
     * @return an Int value with a single one-bit, in the position of the lowest-order one-bit in this Int, or zero if this Int is itself equal to zero.
     */
    @Native("java", "java.lang.Integer.lowestOneBit(#this)")
    @Native("c++", "x10aux::int_utils::lowestOneBit(#0)")
    public native def lowestOneBit(): Int;

    /**
     * Returns the number of zero bits preceding the highest-order ("leftmost")
     * one-bit in the two's complement binary representation of this Int.
     * Returns 32 if this Int has no one-bits in its two's complement
     * representation, in other words if it is equal to zero.
     * @return the number of zero bits preceding the highest-order one-bit in the two's complement binary representation of this Int, or 32 if this Int is equal to zero.
     */
    @Native("java", "java.lang.Integer.numberOfLeadingZeros(#this)")
    @Native("c++", "x10aux::int_utils::numberOfLeadingZeros(#0)")
    public native def numberOfLeadingZeros(): Int;

    /**
     * Returns the number of zero bits following the lowest-order ("rightmost")
     * one-bit in the two's complement binary representation of this Int.
     * Returns 32 if this Int has no one-bits in its two's complement
     * representation, in other words if it is equal to zero.
     * @return the number of zero bits following the lowest-order one-bit in the two's complement binary representation of this Int, or 32 if this Int is equal to zero.
     */
    @Native("java", "java.lang.Integer.numberOfTrailingZeros(#this)")
    @Native("c++", "x10aux::int_utils::numberOfTrailingZeros(#0)")
    public native def numberOfTrailingZeros(): Int;

    /**
     * Returns the number of one-bits in the two's complement binary
     * representation of this Int.  This function is sometimes referred
     * to as the <i>population count</i>.
     * @return the number of one-bits in the two's complement binary representation of this Int.
     */
    @Native("java", "java.lang.Integer.bitCount(#this)")
    @Native("c++", "x10aux::int_utils::bitCount(#0)")
    public native def bitCount(): Int;

    /**
     * Returns the value obtained by rotating the two's complement binary
     * representation of this Int left by the specified number of bits.
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
     * @return the value obtained by rotating the two's complement binary representation of this Int left by the specified number of bits.
     * @see #rotateRight(Int)
     */
    @Native("java", "java.lang.Integer.rotateLeft(#this, #distance)")
    @Native("c++", "x10aux::int_utils::rotateLeft(#0, #1)")
    public native def rotateLeft(distance:Int): Int;

    /**
     * Returns the value obtained by rotating the two's complement binary
     * representation of this Int right by the specified number of bits.
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
     * @return the value obtained by rotating the two's complement binary representation of this Int right by the specified number of bits.
     * @see #rotateLeft(Int)
     */
    @Native("java", "java.lang.Integer.rotateRight(#this, #distance)")
    @Native("c++", "x10aux::int_utils::rotateRight(#0, #1)")
    public native def rotateRight(distance:Int): Int;

    /**
     * Returns the value obtained by reversing the order of the bits in the
     * two's complement binary representation of this Int.
     * @return the value obtained by reversing order of the bits in this Int.
     */
    @Native("java", "java.lang.Integer.reverse(#this)")
    @Native("c++", "x10aux::int_utils::reverse(#0)")
    public native def reverse(): Int;

    /**
     * Returns the signum function of this Int.  The return value is -1 if
     * this Int is negative; 0 if this Int is zero; and 1 if this Int is
     * positive.
     * @return the signum function of this Int.
     */
    @Native("java", "java.lang.Integer.signum(#this)")
    @Native("c++", "x10aux::int_utils::signum(#0)")
    public native def signum(): Int;

    /**
     * Returns the value obtained by reversing the order of the bytes in the
     * two's complement representation of this Int.
     * @return the value obtained by reversing the bytes in this Int.
     */
    @Native("java", "java.lang.Integer.reverseBytes(#this)")
    @Native("c++", "x10aux::int_utils::reverseBytes(#0)")
    public native def reverseBytes(): Int;


    /**
     * Return true if the given entity is an Int, and this Int is equal
     * to the given entity.
     * @param x the given entity
     * @return true if this Int is equal to the given entity.
     */
    @Native("java", "x10.rtt.Equality.equalsequals(#this, #x)")
    @Native("c++", "x10aux::equals(#0,#1)")
    public native def equals(x:Any):x10.lang.Boolean {self == Boolean.asX10Boolean(I.sequals(this,x))};

    /**
     * Returns true if this Int is equal to the given Int.
     * @param x the given Int
     * @return true if this Int is equal to the given Int.
     */
    @Native("java", "x10.rtt.Equality.equalsequals(#this, #x)")
    @Native("c++", "x10aux::equals(#0,#1)")
    public native def equals(x:Int):x10.lang.Boolean {self == Boolean.asX10Boolean(I.sequals(this,x))};

    @Native("java", "x10.rtt.Equality.equalsequals(#this, #x)")
    @Native("c++", "x10aux::equals(#0,#1)")
    public native def equals_(x:Int):Boolean {self == I.sequals(this,x)};

    
     /**
     * Return true if the given entity is an Int, and this Int is equal
     * to the given entity.
     * @param x the given entity
     * @return true if this Int is equal to the given entity.
     */
    @Opaque("sequals")
	public static native property def sequals(x: Int, y:Int) : Boolean; 

    /**
     * Returns true if this Int is equal to the given Int.
     * @param x the given Int
     * @return true if this Int is equal to the given Int.
     */
    @Opaque("sequals")
	public static native property def sequals(x: Int, y:Any) : Boolean; 

    
    /**
     * Returns a negative Int, zero, or a positive Int if this Int is less than, equal
     * to, or greater than the given Int.
     * @param x the given Int
     * @return a negative Int, zero, or a positive Int if this Int is less than, equal
     * to, or greater than the given Int.
     */
    @Native("java", "x10.rtt.Equality.compareTo(#this, #x)")
    @Native("c++", "x10aux::int_utils::compareTo(#0, #1)")
    public native def compareTo(x:Int):Int;

    /**
     * Constructs a IntRange from
     * the lower bound to the upper bound, inclusive.
     * @param lower the lower bound
     * @param upper the upper bound
     * @return a range from lower to upper, inclusive.
     */
    // XTENLANG-3063
    // @Native("java", "new x10.lang.IntRange((java.lang.System[]) null).$init(#x, #y)")
    @Native("java", "new x10.lang.IntRange((java.lang.System[]) null).x10$lang$IntRange$$init$S(#x, #y)")
    @Native("c++", "x10::lang::IntRange::_make(#1, #2)")
    public native static operator (x:Int) .. (y:Int):IntRange{min==x,max==y};
    
    
    // TODO: move coercion the other way to x10.lang.Boolean class
    @Opaque("asX10Int")
    public static native property def asX10Int(x : Int): x10.lang.Int; 
    @Opaque("asInt")
    public static native property def asInt(x: x10.lang.Int): Int {I.asX10Int(self) == x}; 

    
    /**
    * Coerce a given x10.lang.Int to an safe.lang.Int.
    * @param x the given x10.lang.Int
    * @return the given x10.lang.Int converted to an safe.lang.Int.
    */
    @Native("java", "((Int)#x)")
    @Native("c++",  "((x10_int) (#1))")
    public native static operator (x:x10.lang.Int): Int {self == I.asInt(x)};
    
    public native operator this() : x10.lang.Int {self == I.asX10Int(this)}; 

}
public type Int(b:Int) = Int{self==b};
public type I = Int; 
