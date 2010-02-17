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

@NativeRep("java", "double", "x10.core.BoxedDouble", "x10.rtt.Types.DOUBLE")
@NativeRep("c++", "x10_double", "x10_double", null)
public final struct Double {
    @Native("java", "((#1) < (#2))")
    @Native("c++",  "((#1) < (#2))")
    public native static safe operator (x:Double) < (y:Double): Boolean;

    @Native("java", "((#1) > (#2))")
    @Native("c++",  "((#1) > (#2))")
    public native static safe operator (x:Double) > (y:Double): Boolean;

    @Native("java", "((#1) <= (#2))")
    @Native("c++",  "((#1) <= (#2))")
    public native static safe operator (x:Double) <= (y:Double): Boolean;

    @Native("java", "((#1) >= (#2))")
    @Native("c++",  "((#1) >= (#2))")
    public native static safe operator (x:Double) >= (y:Double): Boolean;
    
    @Native("java", "((#1) + (#2))")
    @Native("c++",  "((#1) + (#2))")
    public native static safe operator (x:Double) + (y:Double): Double;

    @Native("java", "((#1) - (#2))")
    @Native("c++",  "((#1) - (#2))")
    public native static safe operator (x:Double) - (y:Double): Double;

    @Native("java", "((#1) * (#2))")
    @Native("c++",  "((#1) * (#2))")
    public native static safe operator (x:Double) * (y:Double): Double;

    @Native("java", "((#1) / (#2))")
    @Native("c++",  "((#1) / (#2))")
    public native static safe operator (x:Double) / (y:Double): Double;

    @Native("java", "((#1) % (#2))")
    @Native("c++",  "x10aux::mod(#1, #2)")
    public native static safe operator (x:Double) % (y:Double): Double;

    @Native("java", "+(#1)")
    @Native("c++",  "+(#1)")
    public native static safe operator + (x:Double): Double;

    @Native("java", "-(#1)")
    @Native("c++",  "-(#1)")
    public native static safe operator - (x:Double): Double;
    
    @Native("java", "((double)(byte)(#1))")
    @Native("c++",  "((x10_double) (#1))")
    public native static safe operator (x:Byte): Double;
    
    @Native("java", "((double)(short)(#1))")
    @Native("c++",  "((x10_double) (#1))")
    public native static safe operator (x:Short): Double;
    
    @Native("java", "((double)(int)(#1))")
    @Native("c++",  "((x10_double) (#1))")
    public native static safe operator (x:Int): Double;
    
    @Native("java", "((double)(long)(#1))")
    @Native("c++",  "((x10_double) (#1))")
    public native static safe operator (x:Long): Double;
    
    @Native("java", "((double)(float)(#1))")
    @Native("c++",  "((x10_double) (#1))")
    public native static safe operator (x:Float): Double;

    
    @Native("java", "java.lang.Double.POSITIVE_INFINITY")
    @Native("c++", "x10aux::double_utils::fromLongBits(0x7ff0000000000000LL)")
    public const POSITIVE_INFINITY: Double = Double.fromLongBits(0x7ff0000000000000L);

    @Native("java", "java.lang.Double.NEGATIVE_INFINITY")
    @Native("c++", "x10aux::double_utils::fromLongBits(0xfff0000000000000LL)")
    public const NEGATIVE_INFINITY: Double = Double.fromLongBits(0xfff0000000000000L);

    @Native("java", "java.lang.Double.NaN")
    @Native("c++", "x10aux::double_utils::fromLongBits(0x7ff8000000000000LL)")
    public const NaN: Double = Double.fromLongBits(0x7ff8000000000000L);

    @Native("java", "java.lang.Double.MAX_VALUE")
    @Native("c++", "x10aux::double_utils::fromLongBits(0x7fefffffffffffffLL)")
    public const MAX_VALUE: Double = Double.fromLongBits(0x7fefffffffffffffL);

    @Native("java", "java.lang.Double.MIN_VALUE")
    @Native("c++", "x10aux::double_utils::fromLongBits(0x1LL)")
    public const MIN_VALUE: Double = Double.fromLongBits(0x1L);
    
    @Native("java", "java.lang.Double.toHexString(#0)")
    @Native("c++", "x10aux::double_utils::toHexString(#0)")
    public native def toHexString(): String;    
    
    @Native("java", "java.lang.Double.toString(#0)")
    @Native("c++", "x10aux::to_string(#0)")
    public global safe native def toString(): String;
    

    @Native("java", "java.lang.Double.parseDouble(#1)")
    @Native("c++", "x10aux::double_utils::parseDouble(#1)")
    public native static def parseDouble(String): Double throws NumberFormatException;
    
    @Native("java", "java.lang.Double.isNaN(#0)")
    @Native("c++", "x10aux::double_utils::isNaN(#0)")
    public native def isNaN(): boolean;

    @Native("java", "java.lang.Double.isInfinite(#0)")
    @Native("c++", "x10aux::double_utils::isInfinite(#0)")
    public native def isInfinite(): boolean;
    
    @Native("java", "java.lang.Double.doubleToLongBits(#0)")
    @Native("c++", "x10aux::double_utils::toLongBits(#0)")
    public native def toLongBits(): Long;

    @Native("java", "java.lang.Double.doubleToRawLongBits(#0)")
    @Native("c++", "x10aux::double_utils::toRawLongBits(#0)")
    public native def toRawLongBits(): Long;

    @Native("java", "java.lang.Double.longBitsToDouble(#1)")
    @Native("c++", "x10aux::double_utils::fromLongBits(#1)")
    public static native def fromLongBits(Long): Double;

    @Native("java", "x10.rtt.Equality.equalsequals(#0, #1)")
    @Native("c++", "x10aux::equals(#0,#1)")
    public global safe native def equals(x:Any):Boolean;

    @Native("java", "x10.rtt.Equality.equalsequals(#0, #1)")
    @Native("c++", "x10aux::equals(#0,#1)")
    public global safe native def equals(x:Double):Boolean;
}
