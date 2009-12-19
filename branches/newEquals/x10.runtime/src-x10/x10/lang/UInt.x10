/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "int", "x10.core.BoxedInt", "x10.types.Types.UINT")
//                  v-- when used
@NativeRep("c++", "uint32_t", "uint32_t", null)
//                            ^ when constructed
public final struct UInt {
    // Binary and unary operations and conversions are built-in.  No need to declare them here.

    @Native("java", "x10.core.Unsigned.lt(#1, #2)")
    @Native("c++",  "((#1) < (#2))")
    public native static safe operator (x:UInt) < (y:UInt): Boolean;

    @Native("java", "x10.core.Unsigned.gt(#1, #2)")
    @Native("c++",  "((#1) > (#2))")
    public native static safe operator (x:UInt) > (y:UInt): Boolean;

    @Native("java", "x10.core.Unsigned.le(#1, #2)")
    @Native("c++",  "((#1) <= (#2))")
    public native static safe operator (x:UInt) <= (y:UInt): Boolean;

    @Native("java", "x10.core.Unsigned.ge(#1, #2)")
    @Native("c++",  "((#1) >= (#2))")
    public native static safe operator (x:UInt) >= (y:UInt): Boolean;

    @Native("java", "((#1) + (#2))")
    @Native("c++",  "((#1) + (#2))")
    public native static safe operator (x:UInt) + (y:UInt): UInt;

    @Native("java", "((#1) - (#2))")
    @Native("c++",  "((#1) - (#2))")
    public native static safe operator (x:UInt) - (y:UInt): Int;

    @Native("java", "((#1) * (#2))")
    @Native("c++",  "((#1) * (#2))")
    public native static safe operator (x:UInt) * (y:UInt): UInt;

    @Native("java", "x10.core.Unsigned.div(#1, #2)")
    @Native("c++",  "((#1) / (#2))")
    public native static safe operator (x:UInt) / (y:UInt): UInt;

    @Native("java", "x10.core.Unsigned.rem(#1, #2)")
    @Native("c++",  "((#1) % (#2))")
    public native static safe operator (x:UInt) % (y:UInt): UInt;
    
    @Native("java", "((#1) & (#2))")
    @Native("c++",  "((#1) & (#2))")
    public native static safe operator (x:UInt) & (y:UInt): UInt;
    
    @Native("java", "((#1) ^ (#2))")
    @Native("c++",  "((#1) ^ (#2))")
    public native static safe operator (x:UInt) ^ (y:UInt): UInt;
    
    @Native("java", "((#1) | (#2))")
    @Native("c++",  "((#1) | (#2))")
    public native static safe operator (x:UInt) | (y:UInt): UInt;
    
    @Native("java", "((#1) << (#2))")
    @Native("c++",  "((#1) << (#2))")
    public native static safe operator (x:UInt) << (y:UInt): UInt;
    
    @Native("java", "((#1) >>> (#2))")
    @Native("c++",  "((#1) >> (#2))")
    public native static safe operator (x:UInt) >> (y:UInt): UInt;

    @Native("java", "((#1) >>> (#2))")
    @Native("c++",  "((#1) >> (#2))")
    public native static safe operator (x:UInt) >>> (y:UInt): UInt;
    
    @Native("java", "+(#1)")
    @Native("c++",  "+(#1)")
    public native static safe operator + (x:UInt): UInt;
    
    @Native("java", "-(#1)")
    @Native("c++",  "-(#1)")
    public native static safe operator - (x:UInt): Int;
    
    @Native("java", "~(#1)")
    @Native("c++",  "~(#1)")
    public native static safe operator ~ (x:UInt): UInt;
    
    // In Java,
    // an unsigned int with value v is represented as v+MININT
    // thus, 0 is represented as MININT (0xffffffff)
    //       0x7fffffff MAXINT is represented as -1
    //       0x80000000 MAXINT+1 is represented as 0
    //       0xffffffff 2*MAXINT-1 is represented as MAXINT (0x7fffffff)
    // To convert from int to uint, add MININT
    // That is (int) 0 is converted to (uint) 0, represented as MININT

    @Native("java", "((int) ((#1) & 0xff))")
    @Native("c++",  "((uint32_t) (#1))")
    public native static safe operator (x:UByte): UInt;

    @Native("java", "((int) ((#1) & 0xffff))")
    @Native("c++",  "((uint32_t) (#1))")
    public native static safe operator (x:UShort): UInt;

    @Native("java", "((int) (#1))")
    @Native("c++",  "((uint32_t) (#1))")
    public native static safe operator (x:ULong) as UInt;

    @Native("java", "((int) (#1))")
    @Native("c++",  "((uint32_t) (#1))")
    public native static safe operator (x:Byte) as UInt;

    @Native("java", "((int) (#1))")
    @Native("c++",  "((uint32_t) (#1))")
    public native static safe operator (x:Short) as UInt;

    @Native("java", "((int) (#1))")
    @Native("c++",  "((uint32_t) (#1))")
    public native static safe operator (x:Int) as UInt;

    @Native("java", "((int) (#1))")
    @Native("c++",  "((uint32_t) (#1))")
    public native static safe operator (x:Long) as UInt;
    
    @Native("java", "((int) (#1))")
    @Native("c++",  "((uint32_t) (#1))")
    public native static safe operator (x:Float) as UInt;
    
    @Native("java", "((int) (#1))")
    @Native("c++",  "((uint32_t) (#1))")
    public native static safe operator (x:Double) as UInt;
    

    @Native("java", "0")
    @Native("c++", "0U")
    public const MIN_VALUE = 0;
    
    @Native("java", "-1")
    @Native("c++", "0xffffffffU")
    public const MAX_VALUE = 0xffffffff;

    @Native("java", "java.lang.Long.toString(#0 & 0xffffffffL)")
    @Native("c++", "x10aux::to_string(#0)")
    public global safe native def toString(): String;
    
    @Native("java", "java.lang.Long.toString(#0 & 0xffffffffL, #1)")
    @Native("c++", "x10aux::int_utils::toString(#0, #1)")
    public native def toString(radix: Int): String;
    
    @Native("java", "java.lang.Integer.toHexString(#0)")
    @Native("c++", "x10aux::int_utils::toHexString(#0)")
    public native def toHexString(): String;    
    
    @Native("java", "java.lang.Integer.toOctalString(#0)")
    @Native("c++", "x10aux::int_utils::toOctalString(#0)")
    public native def toOctalString(): String;  
      
    @Native("java", "java.lang.Integer.toBinaryString(#0)")
    @Native("c++", "x10aux::int_utils::toBinaryString(#0)")
    public native def toBinaryString(): String;    
    
    @Native("java", "java.lang.Integer.parseInt(#1, #2)")
    @Native("c++", "x10aux::int_utils::parseInt(#1, #2)")
    public native static def parseInt(String, radix: Int): Int throws NumberFormatException;
    
    @Native("java", "java.lang.Integer.parseInt(#1)")
    @Native("c++", "x10aux::int_utils::parseInt(#1)")
    public native static def parseInt(String): Int throws NumberFormatException;

    @Native("java", "java.lang.Integer.highestOneBit(#0)")
    @Native("c++", "x10aux::int_utils::highestOneBit(#0)")
    public native def highestOneBit(): Int;
    
    @Native("java", "java.lang.Integer.lowestOneBit(#0)")
    @Native("c++", "x10aux::int_utils::lowestOneBit(#0)")
    public native def lowestOneBit(): Int;

    @Native("java", "java.lang.Integer.numberOfLeadingZeros(#0)")
    @Native("c++", "x10aux::int_utils::numberOfLeadingZeros(#0)")
    public native def numberOfLeadingZeros(): Int;

    @Native("java", "java.lang.Integer.numberOfTrailingZeros(#0)")
    @Native("c++", "x10aux::int_utils::numberOfTrailingZeros(#0)")
    public native def numberOfTrailingZeros(): Int;

    @Native("java", "java.lang.Integer.bitCount(#0)")
    @Native("c++", "x10aux::int_utils::bitCount(#0)")
    public native def bitCount(): Int;

    @Native("java", "java.lang.Integer.rotateLeft(#0)")
    @Native("c++", "x10aux::int_utils::rotateLeft(#0)")
    public native def rotateLeft(): Int;
    
    @Native("java", "java.lang.Integer.rotateRight(#0)")
    @Native("c++", "x10aux::int_utils::rotateRight(#0)")
    public native def rotateRight(): Int;
    
    @Native("java", "java.lang.Integer.reverse(#0)")
    @Native("c++", "x10aux::int_utils::reverse(#0)")
    public native def reverse(): Int;
    
    @Native("java", "((#0==0) ? 0 : 1)")
    @Native("c++",  "((#0==0U) ? 0 : 1)")
    public native def signum(): Int;
    
    @Native("java", "java.lang.Integer.reverseBytes(#0)")
    @Native("c++", "x10aux::int_utils::reverseBytes(#0)")
    public native def reverseBytes(): Int;

    @Native("java", "((((#2) instanceof int) && #1 == ((int)#2)) || (((#2) instanceof x10.core.BoxedInt) && #1 == ((x10.core.BoxedInt) #2).value.value))")
    public global safe native def equals(x:Any):Boolean;
}
