package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "float")
public final value Float {
    // Binary and unary operations and conversions are built-in.  No need to declare them here.
    
    public const POSITIVE_INFINITY: Float = java.lang.Float.POSITIVE_INFINITY;
    public const NEGATIVE_INFINITY: Float = java.lang.Float.NEGATIVE_INFINITY;
    public const NaN: Float = java.lang.Float.NaN;
    public const MAX_VALUE: Float = java.lang.Float.MAX_VALUE;
    public const MIN_VALUE: Float = java.lang.Float.MIN_VALUE;
    
    @Native("java", "java.lang.Float.toHexString(#0)")
    public native def toHexString(): String;    
    
    @Native("java", "java.lang.Float.toString(#0)")
    public native def toString(): String;
    
    @Native("java", "java.lang.Float.parseFloat(#1)")
    public native static def parseFloat(String): Float throws NumberFormatException;

    @Native("java", "java.lang.Float.isNaN(#0)")
    public native def isNaN(): boolean;
    @Native("java", "java.lang.Float.isInfinite(#0)")
    public native def isInfinite(): boolean;
    
    @Native("java", "java.lang.Float.floatToIntBits(#0)")
    public native def toIntBits(): Int;
    @Native("java", "java.lang.Float.floatToRawIntBits(#0)")
    public native def toRawIntBits(): Int;
    @Native("java", "java.lang.Float.intBitsToFloat(#1)")
    public static native def fromIntBits(Int): Float;
}
