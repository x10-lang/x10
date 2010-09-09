package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "double")
public final value Double {
    // Binary and unary operations and conversions are built-in.  No need to declare them here.
    
    public const POSITIVE_INFINITY: Double = java.lang.Double.POSITIVE_INFINITY;
    public const NEGATIVE_INFINITY: Double = java.lang.Double.NEGATIVE_INFINITY;
    public const NaN: Double = java.lang.Double.NaN;
    public const MAX_VALUE: Double = java.lang.Double.MAX_VALUE;
    public const MIN_VALUE: Double = java.lang.Double.MIN_VALUE;
    
    @Native("java", "java.lang.Double.toHexString(#0)")
    public native def toHexString(): String;    
    
    @Native("java", "java.lang.Double.toString(#0)")
    public native def toString(): String;
    
    @Native("java", "java.lang.Double.parseDouble(#1)")
    public native static def parseDouble(String): Double throws NumberFormatException;
    
    @Native("java", "java.lang.Double.isNaN(#0)")
    public native def isNaN(): boolean;
    @Native("java", "java.lang.Double.isInfinite(#0)")
    public native def isInfinite(): boolean;
    
    @Native("java", "java.lang.Double.doubleToLongBits(#0)")
    public native def toLongBits(): Long;
    @Native("java", "java.lang.Double.floatToRawLongBits(#0)")
    public native def toRawLongBits(): Int;
    @Native("java", "java.lang.Double.longBitsToDouble(#1)")
    public static native def fromLongBits(Int): Double;
}
