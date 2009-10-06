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

@NativeRep("java", "long", "x10.core.BoxedLong", "x10.types.Types.ULONG")
//                  v-- when used
@NativeRep("c++", "uint64_t", "uint64_t", null)
//                            ^ when constructed
public final struct ULong {
    // Binary and unary operations and conversions are built-in.  No need to declare them here.

    @Native("java", "x10.core.Unsigned.lt(#1, #2)")
    @Native("c++",  "((#1) < (#2))")
    public native static safe operator (x:ULong) < (y:ULong): Boolean;

    @Native("java", "x10.core.Unsigned.gt(#1, #2)")
    @Native("c++",  "((#1) > (#2))")
    public native static safe operator (x:ULong) > (y:ULong): Boolean;

    @Native("java", "x10.core.Unsigned.le(#1, #2)")
    @Native("c++",  "((#1) <= (#2))")
    public native static safe operator (x:ULong) <= (y:ULong): Boolean;

    @Native("java", "x10.core.Unsigned.ge(#1, #2)")
    @Native("c++",  "((#1) >= (#2))")
    public native static safe operator (x:ULong) >= (y:ULong): Boolean;

    @Native("java", "((#1) + (#2))")
    @Native("c++",  "((#1) + (#2))")
    public native static safe operator (x:ULong) + (y:ULong): ULong;

    @Native("java", "((#1) - (#2))")
    @Native("c++",  "((#1) - (#2))")
    public native static safe operator (x:ULong) - (y:ULong): Long;

    @Native("java", "((#1) * (#2))")
    @Native("c++",  "((#1) * (#2))")
    public native static safe operator (x:ULong) * (y:ULong): ULong;

    @Native("java", "x10.core.Unsigned.div(#1, #2)")
    @Native("c++",  "((#1) / (#2))")
    public native static safe operator (x:ULong) / (y:ULong): ULong;

    @Native("java", "x10.core.Unsigned.rem(#1, #2)")
    @Native("c++",  "((#1) % (#2))")
    public native static safe operator (x:ULong) % (y:ULong): ULong;
    
    @Native("java", "((#1) & (#2))")
    @Native("c++",  "((#1) & (#2))")
    public native static safe operator (x:ULong) & (y:ULong): ULong;
    
    @Native("java", "((#1) ^ (#2))")
    @Native("c++",  "((#1) ^ (#2))")
    public native static safe operator (x:ULong) ^ (y:ULong): ULong;
    
    @Native("java", "((#1) | (#2))")
    @Native("c++",  "((#1) | (#2))")
    public native static safe operator (x:ULong) | (y:ULong): ULong;
    
    @Native("java", "((#1) << (#2))")
    @Native("c++",  "((#1) << (#2))")
    public native static safe operator (x:ULong) << (y:Long): ULong;
    
    @Native("java", "((#1) >>> (#2))")
    @Native("c++",  "((#1) >> (#2))")
    public native static safe operator (x:ULong) >> (y:Long): ULong;

    @Native("java", "((#1) >>> (#2))")
    @Native("c++",  "((#1) >> (#2))")
    public native static safe operator (x:ULong) >>> (y:Long): ULong;
    
    @Native("java", "+(#1)")
    @Native("c++",  "+(#1)")
    public native static safe operator + (x:ULong): ULong;
    
    @Native("java", "-(#1)")
    @Native("c++",  "-(#1)")
    public native static safe operator - (x:ULong): Long;
    
    @Native("java", "~(#1)")
    @Native("c++",  "~(#1)")
    public native static safe operator ~ (x:ULong): ULong;
    

    @Native("java", "((long) ((#1) & 0xffL))")
    @Native("c++",  "((uint64_t) (#1))")
    public native static safe operator (x:UByte): ULong;

    @Native("java", "((long) ((#1) & 0xffffL))")
    @Native("c++",  "((uint64_t) (#1))")
    public native static safe operator (x:UShort): ULong;

    @Native("java", "((long) ((#1) & 0xffffffffL))")
    @Native("c++",  "((uint64_t) (#1))")
    public native static safe operator (x:UInt): ULong;
    
    @Native("java", "((long) (#1))")
    @Native("c++",  "((uint64_t) (#1))")
    public native static safe operator (x:Byte) as ULong;

    @Native("java", "((long) (#1))")
    @Native("c++",  "((uint64_t) (#1))")
    public native static safe operator (x:Short) as ULong;

    @Native("java", "((long) (#1))")
    @Native("c++",  "((uint64_t) (#1))")
    public native static safe operator (x:Int) as ULong;

    @Native("java", "((long) (#1))")
    @Native("c++",  "((uint64_t) (#1))")
    public native static safe operator (x:Long) as ULong;
    
    @Native("java", "((long) (#1))")
    @Native("c++",  "((uint64_t) (#1))")
    public native static safe operator (x:Float) as ULong;
    
    @Native("java", "((long) (#1))")
    @Native("c++",  "((uint64_t) (#1))")
    public native static safe operator (x:Double) as ULong;
    

    @Native("java", "0L")
    @Native("c++", "0UL")
    public const MIN_VALUE = 0L;
    
    @Native("java", "-1")
    @Native("c++", "0xffffffffffffffffLU")
    public const MAX_VALUE = 0xffffffffffffffffL;

    @Native("java", "java.lang.Long.toString(#0 & 0xffffffffffffffffL)")
    @Native("c++", "x10aux::to_string(#0)")
    public native def toString(): String;
    
    @Native("java", "java.lang.Long.toString(#0 & 0xffffffffffffffffL, #1)")
    @Native("c++", "x10aux::int_utils::toString(#0, #1)")
    public native def toString(radix: Int): String;
    
    @Native("java", "java.lang.Long.toHexString(#0)")
    @Native("c++", "x10aux::int_utils::toHexString(#0)")
    public native def toHexString(): String;    
    
    @Native("java", "java.lang.Long.toOctalString(#0)")
    @Native("c++", "x10aux::int_utils::toOctalString(#0)")
    public native def toOctalString(): String;  
      
    @Native("java", "java.lang.Long.toBinaryString(#0)")
    @Native("c++", "x10aux::int_utils::toBinaryString(#0)")
    public native def toBinaryString(): String;    
    
    @Native("java", "java.lang.Long.parseInt(#1, #2)")
    @Native("c++", "x10aux::int_utils::parseInt(#1, #2)")
    public native static def parseInt(String, radix: Int): Int throws NumberFormatException;
    
    @Native("java", "java.lang.Long.parseInt(#1)")
    @Native("c++", "x10aux::int_utils::parseInt(#1)")
    public native static def parseInt(String): Int throws NumberFormatException;

    @Native("java", "java.lang.Long.highestOneBit(#0)")
    @Native("c++", "x10aux::int_utils::highestOneBit(#0)")
    public native def highestOneBit(): Int;
    
    @Native("java", "java.lang.Long.lowestOneBit(#0)")
    @Native("c++", "x10aux::int_utils::lowestOneBit(#0)")
    public native def lowestOneBit(): Int;

    @Native("java", "java.lang.Long.numberOfLeadingZeros(#0)")
    @Native("c++", "x10aux::int_utils::numberOfLeadingZeros(#0)")
    public native def numberOfLeadingZeros(): Int;

    @Native("java", "java.lang.Long.numberOfTrailingZeros(#0)")
    @Native("c++", "x10aux::int_utils::numberOfTrailingZeros(#0)")
    public native def numberOfTrailingZeros(): Int;

    @Native("java", "java.lang.Long.bitCount(#0)")
    @Native("c++", "x10aux::int_utils::bitCount(#0)")
    public native def bitCount(): Int;

    @Native("java", "java.lang.Long.rotateLeft(#0)")
    @Native("c++", "x10aux::int_utils::rotateLeft(#0)")
    public native def rotateLeft(): ULong;
    
    @Native("java", "java.lang.Long.rotateRight(#0)")
    @Native("c++", "x10aux::int_utils::rotateRight(#0)")
    public native def rotateRight(): ULong;
    
    @Native("java", "java.lang.Long.reverse(#0)")
    @Native("c++", "x10aux::int_utils::reverse(#0)")
    public native def reverse(): ULong;
    
    @Native("java", "((#0==0) ? 0 : 1)")
    @Native("c++",  "((#0==0U) ? 0 : 1)")
    public native def signum(): Int;
    
    @Native("java", "java.lang.Long.reverseBytes(#0)")
    @Native("c++", "x10aux::int_utils::reverseBytes(#0)")
    public native def reverseBytes(): ULong;
}
