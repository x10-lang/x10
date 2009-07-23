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

@NativeRep("java", "int", "x10.core.BoxedInt", "x10.types.Type.INT")
//                  v-- when used
@NativeRep("c++", "x10_int", "x10_int", null)
//                            ^ when constructed
public final value Int {
    @Native("java", "((#1) < (#2))")
    @Native("c++",  "((#1) < (#2))")
    public native static safe operator (x:Int) < (y:Int): Boolean;

    @Native("java", "((#1) > (#2))")
    @Native("c++",  "((#1) > (#2))")
    public native static safe operator (x:Int) > (y:Int): Boolean;

    @Native("java", "((#1) <= (#2))")
    @Native("c++",  "((#1) <= (#2))")
    public native static safe operator (x:Int) <= (y:Int): Boolean;

    @Native("java", "((#1) >= (#2))")
    @Native("c++",  "((#1) >= (#2))")
    public native static safe operator (x:Int) >= (y:Int): Boolean;

    @Native("java", "((#1) + (#2))")
    @Native("c++",  "((#1) + (#2))")
    public native static safe operator (x:Int) + (y:Int): Int;

    @Native("java", "((#1) - (#2))")
    @Native("c++",  "((#1) - (#2))")
    public native static safe operator (x:Int) - (y:Int): Int;

    @Native("java", "((#1) * (#2))")
    @Native("c++",  "((#1) * (#2))")
    public native static safe operator (x:Int) * (y:Int): Int;

    @Native("java", "((#1) / (#2))")
    @Native("c++",  "((#1) / (#2))")
    public native static safe operator (x:Int) / (y:Int): Int;

    @Native("java", "((#1) % (#2))")
    @Native("c++",  "((#1) % (#2))")
    public native static safe operator (x:Int) % (y:Int): Int;
    
    @Native("java", "((#1) & (#2))")
    @Native("c++",  "((#1) & (#2))")
    public native static safe operator (x:Int) & (y:Int): Int;
    
    @Native("java", "((#1) ^ (#2))")
    @Native("c++",  "((#1) ^ (#2))")
    public native static safe operator (x:Int) ^ (y:Int): Int;
    
    @Native("java", "((#1) | (#2))")
    @Native("c++",  "((#1) | (#2))")
    public native static safe operator (x:Int) | (y:Int): Int;
    
    @Native("java", "((#1) << (#2))")
    @Native("c++",  "((#1) << (#2))")
    public native static safe operator (x:Int) << (y:Int): Int;
    
    @Native("java", "((#1) >> (#2))")
    @Native("c++",  "((#1) >> (#2))")
    public native static safe operator (x:Int) >> (y:Int): Int;

    @Native("java", "((#1) >>> (#2))")
    @Native("c++",  "((x10_int) ((uint32_t) (#1) >> (#2)))")
    public native static safe operator (x:Int) >>> (y:Int): Int;
    
    @Native("java", "((#1) << (#2))")
    @Native("c++",  "((#1) << (#2))")
    public native static safe operator (x:Int) << (y:Long): Int;
    
    @Native("java", "((#1) >> (#2))")
    @Native("c++",  "((#1) >> (#2))")
    public native static safe operator (x:Int) >> (y:Long): Int;

    @Native("java", "((#1) >>> (#2))")
    @Native("c++",  "((x10_int) ((uint32_t) (#1) >> (#2)))")
    public native static safe operator (x:Int) >>> (y:Long): Int;
    
    @Native("java", "+(#1)")
    @Native("c++",  "+(#1)")
    public native static safe operator + (x:Int): Int;
    
    @Native("java", "-(#1)")
    @Native("c++",  "-(#1)")
    public native static safe operator - (x:Int): Int;
    
    @Native("java", "~(#1)")
    @Native("c++",  "~(#1)")
    public native static safe operator ~ (x:Int): Int;
    
    @Native("java", "((int) (#1))")
    @Native("c++",  "((x10_int) (#1))")
    public native static safe operator (x:Byte): Int;

    @Native("java", "((int) (#1))")
    @Native("c++",  "((x10_int) (#1))")
    public native static safe operator (x:Short): Int;

    @Native("java", "((int) (#1))")
    @Native("c++",  "((x10_int) (#1))")
    public native static safe operator (x:Long) as Int;
    
    @Native("java", "((int) (#1))")
    @Native("c++",  "((x10_int) (#1))")
    public native static safe operator (x:Float) as Int;
    
    @Native("java", "((int) (#1))")
    @Native("c++",  "((x10_int) (#1))")
    public native static safe operator (x:Double) as Int;

    @Native("java", "((int) (#1))")
    @Native("c++",  "((x10_int) (#1))")
    public native static safe operator (x:UInt) as Int;
    

    @Native("java", "java.lang.Integer.MIN_VALUE")
    @Native("c++", "(x10_int)0x80000000")
    public const MIN_VALUE = 0x80000000;
    
    @Native("java", "java.lang.Integer.MAX_VALUE")
    @Native("c++", "(x10_int)0x7fffffff")
    public const MAX_VALUE = 0x7fffffff;

    @Native("java", "java.lang.Integer.toString(#0)")
    @Native("c++", "x10aux::to_string(#0)")
    public native def toString(): String;
    
    @Native("java", "java.lang.Integer.toString(#0, #1)")
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
    
    @Native("java", "java.lang.Integer.signum(#0)")
    @Native("c++", "x10aux::int_utils::signum(#0)")
    public native def signum(): Int;
    
    @Native("java", "java.lang.Integer.reverseBytes(#0)")
    @Native("c++", "x10aux::int_utils::reverseBytes(#0)")
    public native def reverseBytes(): Int;
}
