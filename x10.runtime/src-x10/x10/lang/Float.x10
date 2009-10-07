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

@NativeRep("java", "float", "x10.core.BoxedFloat", "x10.types.Type.FLOAT")
@NativeRep("c++", "x10_float", "x10_float", null)
public final struct Float {
    @Native("java", "((#1) < (#2))")
    @Native("c++",  "((#1) < (#2))")
    public native static safe operator (x:Float) < (y:Float): Boolean;

    @Native("java", "((#1) > (#2))")
    @Native("c++",  "((#1) > (#2))")
    public native static safe operator (x:Float) > (y:Float): Boolean;

    @Native("java", "((#1) <= (#2))")
    @Native("c++",  "((#1) <= (#2))")
    public native static safe operator (x:Float) <= (y:Float): Boolean;

    @Native("java", "((#1) >= (#2))")
    @Native("c++",  "((#1) >= (#2))")
    public native static safe operator (x:Float) >= (y:Float): Boolean;
    
    @Native("java", "((#1) + (#2))")
    @Native("c++",  "((#1) + (#2))")
    public native static safe operator (x:Float) + (y:Float): Float;

    @Native("java", "((#1) - (#2))")
    @Native("c++",  "((#1) - (#2))")
    public native static safe operator (x:Float) - (y:Float): Float;

    @Native("java", "((#1) * (#2))")
    @Native("c++",  "((#1) * (#2))")
    public native static safe operator (x:Float) * (y:Float): Float;

    @Native("java", "((#1) / (#2))")
    @Native("c++",  "((#1) / (#2))")
    public native static safe operator (x:Float) / (y:Float): Float;

    @Native("java", "((#1) % (#2))")
    @Native("c++",  "x10aux::mod(#1, #2)")
    public native static safe operator (x:Float) % (y:Float): Float;

    @Native("java", "+(#1)")
    @Native("c++",  "+(#1)")
    public native static safe operator + (x:Float): Float;

    @Native("java", "-(#1)")
    @Native("c++",  "-(#1)")
    public native static safe operator - (x:Float): Float;
    
    @Native("java", "((float) (#1))")
    @Native("c++",  "((x10_float) (#1))")
    public native static safe operator (x:Byte): Float;

    @Native("java", "((float) (#1))")
    @Native("c++",  "((x10_float) (#1))")
    public native static safe operator (x:Short): Float;

    @Native("java", "((float) (#1))")
    @Native("c++",  "((x10_float) (#1))")
    public native static safe operator (x:Int): Float;

    @Native("java", "((float) (#1))")
    @Native("c++",  "((x10_float) (#1))")
    public native static safe operator (x:Long): Float;
    
    @Native("java", "((float) (#1))")
    @Native("c++",  "((x10_float) (#1))")
    public native static safe operator (x:Double) as Float;


    @Native("java", "java.lang.Float.POSITIVE_INFINITY")
    @Native("c++", "x10aux::float_utils::fromIntBits(0x7f800000)")
    public const POSITIVE_INFINITY: Float = Float.fromIntBits(0x7f800000);

    @Native("java", "java.lang.Float.NEGATIVE_INFINITY")
    @Native("c++", "x10aux::float_utils::fromIntBits(0xff800000)")
    public const NEGATIVE_INFINITY: Float = Float.fromIntBits(0xff800000);

    @Native("java", "java.lang.Float.NaN")
    @Native("c++", "x10aux::float_utils::fromIntBits(0x7fc00000)")
    public const NaN: Float = Float.fromIntBits(0x7fc00000);

    @Native("java", "java.lang.Float.MAX_VALUE")
    @Native("c++", "x10aux::float_utils::fromIntBits(0x7f7fffff)")
    @Native("cuda", "FLT_MAX")
    public const MAX_VALUE: Float = Float.fromIntBits(0x7f7fffff);

    @Native("java", "java.lang.Float.MIN_VALUE")
    @Native("c++", "x10aux::float_utils::fromIntBits(0x00800000)")
    @Native("cuda", "FLT_MIN")
    public const MIN_VALUE: Float = Float.fromIntBits(0x00800000);
    
    @Native("java", "java.lang.Float.toHexString(#0)")
    @Native("c++", "x10aux::float_utils::toHexString(#0)")
    public native def toHexString(): String;    
    
    @Native("java", "java.lang.Float.toString(#0)")
    @Native("c++", "x10aux::to_string(#0)")
    public native def toString(): String;
    
    @Native("java", "#0.getClass().toString()")
    @Native("c++", "x10aux::type_name(#0)")
    public native def typeName(): String;

    @Native("java", "java.lang.Float.parseFloat(#1)")
    @Native("c++", "x10aux::float_utils::parseFloat(#1)")
    public native static def parseFloat(String): Float throws NumberFormatException;

    @Native("java", "java.lang.Float.isNaN(#0)")
    @Native("c++", "x10aux::float_utils::isNaN(#0)")
    public native def isNaN(): boolean;

    @Native("java", "java.lang.Float.isInfinite(#0)")
    @Native("c++", "x10aux::float_utils::isInfinite(#0)")
    public native def isInfinite(): boolean;
    
    @Native("java", "java.lang.Float.floatToIntBits(#0)")
    @Native("c++", "x10aux::float_utils::toIntBits(#0)")
    public native def toIntBits(): Int;

    @Native("java", "java.lang.Float.floatToRawIntBits(#0)")
    @Native("c++", "x10aux::float_utils::toRawIntBits(#0)")
    public native def toRawIntBits(): Int;

    @Native("java", "java.lang.Float.intBitsToFloat(#1)")
    @Native("c++", "x10aux::float_utils::fromIntBits(#1)")
    public static native def fromIntBits(Int): Float;
}
