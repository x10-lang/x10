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

@NativeRep("java", "byte", "x10.core.BoxedShort", "x10.types.Types.UBYTE")
//                  v-- when used
@NativeRep("c++", "x10_ubyte", "x10_ubyte", null)
//                               ^ when constructed
public final struct UByte {
    @Native("java", "x10.core.Unsigned.lt(#1, #2)")
    @Native("c++",  "((#1) < (#2))")
    public native static safe operator (x:UByte) < (y:UByte): Boolean;

    @Native("java", "x10.core.Unsigned.gt(#1, #2)")
    @Native("c++",  "((#1) > (#2))")
    public native static safe operator (x:UByte) > (y:UByte): Boolean;

    @Native("java", "x10.core.Unsigned.le(#1, #2)")
    @Native("c++",  "((#1) <= (#2))")
    public native static safe operator (x:UByte) <= (y:UByte): Boolean;

    @Native("java", "x10.core.Unsigned.ge(#1, #2)")
    @Native("c++",  "((#1) >= (#2))")
    public native static safe operator (x:UByte) >= (y:UByte): Boolean;

    @Native("java", "((#1) + (#2))")
    @Native("c++",  "((x10_uint) ((#1) + (#2)))")
    public native static safe operator (x:UByte) + (y:UByte): UInt;

    @Native("java", "((#1) - (#2))")
    @Native("c++",  "((x10_int) ((#1) - (#2)))")
    public native static safe operator (x:UByte) - (y:UByte): Int;

    @Native("java", "((#1) * (#2))")
    @Native("c++",  "((x10_uint) ((#1) * (#2)))")
    public native static safe operator (x:UByte) * (y:UByte): UInt;

    @Native("java", "x10.core.Unsigned.div(#1, #2)")
    @Native("c++",  "((x10_uint) ((#1) / (#2)))")
    public native static safe operator (x:UByte) / (y:UByte): UInt;

    @Native("java", "x10.core.Unsigned.rem(#1, #2)")
    @Native("c++",  "((x10_uint) ((#1) % (#2)))")
    public native static safe operator (x:UByte) % (y:UByte): UInt;
    
    @Native("java", "((#1) & (#2))")
    @Native("c++",  "((x10_uint) ((#1) & (#2)))")
    public native static safe operator (x:UByte) & (y:UByte): UInt;
    
    @Native("java", "((#1) ^ (#2))")
    @Native("c++",  "((x10_uint) ((#1) ^ (#2)))")
    public native static safe operator (x:UByte) ^ (y:UByte): UInt;
    
    @Native("java", "((#1) | (#2))")
    @Native("c++",  "((x10_uint) ((#1) | (#2)))")
    public native static safe operator (x:UByte) | (y:UByte): UInt;
    
    @Native("java", "((#1) << (#2))")
    @Native("c++",  "((x10_uint) ((#1) << (#2)))")
    public native static safe operator (x:UByte) << (y:Int): UInt;
    
    @Native("java", "((#1) >>> (#2))")
    @Native("c++",  "((x10_uint) ((#1) >> (#2)))")
    public native static safe operator (x:UByte) >> (y:Int): UInt;

    @Native("java", "((#1) >>> (#2))")
    @Native("c++",  "((x10_uint) ((#1) >> (#2)))")
    public native static safe operator (x:UByte) >>> (y:Int): UInt;
    
    @Native("java", "+(#1)")
    @Native("c++",  "((x10_uint) (+(#1)))")
    public native static safe operator + (x:UByte): UInt;
    
    @Native("java", "-(#1)")
    @Native("c++",  "((x10_int) (-(#1)))")
    public native static safe operator - (x:UByte): Int;
    
    @Native("java", "~(#1)")
    @Native("c++",  "((x10_uint) (~(#1)))")
    public native static safe operator ~ (x:UByte): UInt;
    
    @Native("java", "((byte) (#1))")
    @Native("c++",  "((x10_ubyte) (#1))")
    public native static safe operator (x:UShort) as UByte;

    @Native("java", "((byte) (#1))")
    @Native("c++",  "((x10_ubyte) (#1))")
    public native static safe operator (x:UInt) as UByte;

    @Native("java", "((byte) (#1))")
    @Native("c++",  "((x10_ubyte) (#1))")
    public native static safe operator (x:ULong) as UByte;
    
    @Native("java", "((byte) (#1))")
    @Native("c++",  "((x10_ubyte) (#1))")
    public native static safe operator (x:Byte) as UByte;

    @Native("java", "((byte) (#1))")
    @Native("c++",  "((x10_ubyte) (#1))")
    public native static safe operator (x:Short) as UByte;

    @Native("java", "((byte) (#1))")
    @Native("c++",  "((x10_ubyte) (#1))")
    public native static safe operator (x:Int) as UByte;

    @Native("java", "((byte) (#1))")
    @Native("c++",  "((x10_ubyte) (#1))")
    public native static safe operator (x:Long) as UByte;
    
    @Native("java", "((byte) (#1))")
    @Native("c++",  "((x10_ubyte) (#1))")
    public native static safe operator (x:Float) as UByte;
    
    @Native("java", "((byte) (#1))")
    @Native("c++",  "((x10_ubyte) (#1))")
    public native static safe operator (x:Double) as UByte;
    

    @Native("java", "0")
    @Native("c++", "0U")
    public const MIN_VALUE = 0;
    
    @Native("java", "0xff")
    @Native("c++", "0xffU")
    public const MAX_VALUE = 0xff;


    @Native("java", "java.lang.Integer.toString(#0 & 0xff)")
    @Native("c++", "x10aux::to_string(#0)")
    public global safe native def toString(): String;
    
    @Native("java", "java.lang.Integer.toString(#0 & 0xff, #1)")
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
    public native def rotateLeft(): UByte;
    
    @Native("java", "java.lang.Integer.rotateRight(#0)")
    @Native("c++", "x10aux::int_utils::rotateRight(#0)")
    public native def rotateRight(): UByte;
    
    @Native("java", "java.lang.Integer.reverse(#0)")
    @Native("c++", "x10aux::int_utils::reverse(#0)")
    public native def reverse(): UByte;
    
    @Native("java", "((#0==0) ? 0 : 1)")
    @Native("c++",  "((#0==0U) ? 0 : 1)")
    public native def signum(): Int;
    
    @Native("java", "java.lang.Integer.reverseBytes(#0)")
    @Native("c++", "x10aux::int_utils::reverseBytes(#0)")
    public native def reverseBytes(): UByte;

    @Native("java", "((((#2) instanceof byte) && #1 == ((byte)#2)) || (((#2) instanceof x10.core.BoxedByte) && #1 == ((x10.core.BoxedByte) #2).value.value))")
    @Native("c++", "x10aux::equals(#0,#1)")
    public global safe native def equals(x:Any):Boolean;
}
