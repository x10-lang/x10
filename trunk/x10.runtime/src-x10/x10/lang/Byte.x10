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

@NativeRep("java", "byte", "x10.core.BoxedByte", "x10.rtt.Type.BYTE")
@NativeRep("c++", "x10_byte", "x10_byte", null)
public final struct Byte {
    @Native("java", "((#1) < (#2))")
    @Native("c++",  "((#1) < (#2))")
    public native static safe operator (x:Byte) < (y:Byte): Boolean;

    @Native("java", "((#1) > (#2))")
    @Native("c++",  "((#1) > (#2))")
    public native static safe operator (x:Byte) > (y:Byte): Boolean;

    @Native("java", "((#1) <= (#2))")
    @Native("c++",  "((#1) <= (#2))")
    public native static safe operator (x:Byte) <= (y:Byte): Boolean;

    @Native("java", "((#1) >= (#2))")
    @Native("c++",  "((#1) >= (#2))")
    public native static safe operator (x:Byte) >= (y:Byte): Boolean;

    @Native("java", "((#1) + (#2))")
    @Native("c++",  "((x10_int) ((#1) + (#2)))")
    public native static safe operator (x:Byte) + (y:Byte): Int;

    @Native("java", "((#1) - (#2))")
    @Native("c++",  "((x10_int) ((#1) - (#2)))")
    public native static safe operator (x:Byte) - (y:Byte): Int;

    @Native("java", "((#1) * (#2))")
    @Native("c++",  "((x10_int) ((#1) * (#2)))")
    public native static safe operator (x:Byte) * (y:Byte): Int;

    @Native("java", "((#1) / (#2))")
    @Native("c++",  "((x10_int) ((#1) / (#2)))")
    public native static safe operator (x:Byte) / (y:Byte): Int;

    @Native("java", "((#1) % (#2))")
    @Native("c++",  "((x10_int) ((#1) % (#2)))")
    public native static safe operator (x:Byte) % (y:Byte): Int;
    
    @Native("java", "((#1) & (#2))")
    @Native("c++",  "((x10_int) ((#1) & (#2)))")
    public native static safe operator (x:Byte) & (y:Byte): Int;
    
    @Native("java", "((#1) ^ (#2))")
    @Native("c++",  "((x10_int) ((#1) ^ (#2)))")
    public native static safe operator (x:Byte) ^ (y:Byte): Int;
    
    @Native("java", "((#1) | (#2))")
    @Native("c++",  "((x10_int) ((#1) | (#2)))")
    public native static safe operator (x:Byte) | (y:Byte): Int;
    
    @Native("java", "((#1) << (#2))")
    @Native("c++",  "((x10_int) ((#1) << (#2)))")
    public native static safe operator (x:Byte) << (y:Int): Int;
    
    @Native("java", "((#1) >> (#2))")
    @Native("c++",  "((x10_int) ((#1) >> (#2)))")
    public native static safe operator (x:Byte) >> (y:Int): Int;
    
    @Native("java", "((#1) >>> (#2))")
    @Native("c++",  "((x10_int) ((x10_uint) (#1) >> (#2)))")
    public native static safe operator (x:Byte) >>> (y:Int): Int;
    
    // FIXME: why is this here?
    @Native("java", "((#1) << (#2))")
    @Native("c++",  "((x10_int) ((#1) << (#2)))")
    public native static safe operator (x:Byte) << (y:Long): Int;
    
    // FIXME: why is this here?
    @Native("java", "((#1) >> (#2))")
    @Native("c++",  "((x10_int) ((#1) >> (#2)))")
    public native static safe operator (x:Byte) >> (y:Long): Int;
    
    // FIXME: why is this here?
    @Native("java", "((#1) >>> (#2))")
    @Native("c++",  "((x10_int) ((x10_uint) (#1) >> (#2)))")
    public native static safe operator (x:Byte) >>> (y:Long): Int;
    
    @Native("java", "+(#1)")
    @Native("c++",  "((x10_int) +(#1))")
    public native static safe operator + (x:Byte): Int;
    
    @Native("java", "-(#1)")
    @Native("c++",  "((x10_int) -(#1))")
    public native static safe operator - (x:Byte): Int;
    
    @Native("java", "~(#1)")
    @Native("c++",  "((x10_int) ~(#1))")
    public native static safe operator ~ (x:Byte): Int;
    
    @Native("java", "((byte)(short)(#1))")
    @Native("c++",  "((x10_byte) (#1))")
    public native static safe operator (x:Short) as Byte;
    
    @Native("java", "((byte)(int)(#1))")
    @Native("c++",  "((x10_byte) (#1))")
    public native static safe operator (x:Int) as Byte;

    @Native("java", "((byte)(long)(#1))")
    @Native("c++",  "((x10_byte) (#1))")
    public native static safe operator (x:Long) as Byte;

    @Native("java", "((byte)(float)(#1))")
    @Native("c++",  "((x10_byte) (#1))")
    public native static safe operator (x:Float) as Byte;
    
    @Native("java", "((byte)(double)(#1))")
    @Native("c++",  "((x10_byte) (#1))")
    public native static safe operator (x:Double) as Byte;

    
    @Native("java", "java.lang.Byte.MIN_VALUE")
    @Native("c++", "(x10_byte)0x80")
    public const MIN_VALUE = 0x80 as Byte;
    
    @Native("java", "java.lang.Byte.MAX_VALUE")
    @Native("c++", "(x10_byte)0x7f")
    public const MAX_VALUE = 0x7f as Byte;

    @Native("java", "java.lang.Integer.toString(#0, #1)")
    @Native("c++", "x10aux::byte_utils::toString(#0, #1)")
    public global safe native def toString(radix: Int): String;
    
    @Native("java", "java.lang.Integer.toHexString(#0)")
    @Native("c++", "x10aux::byte_utils::toHexString(#0)")
    public native def toHexString(): String;    
    
    @Native("java", "java.lang.Integer.toOctalString(#0)")
    @Native("c++", "x10aux::byte_utils::toOctalString(#0)")
    public native def toOctalString(): String;  
      
    @Native("java", "java.lang.Integer.toBinaryString(#0)")
    @Native("c++", "x10aux::byte_utils::toBinaryString(#0)")
    public native def toBinaryString(): String;    
    
    @Native("java", "java.lang.Byte.toString(#0)")
    @Native("c++", "x10aux::to_string(#0)")
    public global safe native def toString(): String;
    
    @Native("java", "java.lang.Byte.parseByte(#1, #2)")
    @Native("c++", "x10aux::byte_utils::parseByte(#1, #2)")
    public native static def parseByte(String, radix: Int): Byte throws NumberFormatException;
    
    @Native("java", "java.lang.Byte.parseByte(#1)")
    @Native("c++", "x10aux::byte_utils::parseByte(#1)")
    public native static def parseByte(String): Byte throws NumberFormatException;

    @Native("java", "x10.rtt.Equality.equalsequals(#0, #1)")
    @Native("c++", "x10aux::equals(#0,#1)")
    public global safe native def equals(x:Any):Boolean;

    @Native("java", "x10.rtt.Equality.equalsequals(#0, #1)")
    @Native("c++", "x10aux::equals(#0,#1)")
    public global safe native def equals(x:Byte):Boolean;
}
