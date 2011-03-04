package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "long")
public final value Long implements Integer, Signed {
    // Binary and unary operations and conversions are built-in.  No need to declare them here.
    
    @Native("java", "java.lang.Long.MIN_VALUE")
    public const MIN_VALUE = 0x8000000000000000L;
    
    @Native("java", "java.lang.Long.MAX_VALUE")
    public const MAX_VALUE = 0x7fffffffffffffffL;

    @Native("java", "java.lang.Long.toString(#0, #1)")
    public native def toString(radix: Int): String;
    
    @Native("java", "java.lang.Long.toHexString(#0)")
    public native def toHexString(): String;    
    
    @Native("java", "java.lang.Long.toOctalString(#0)")
    public native def toOctalString(): String;  
      
    @Native("java", "java.lang.Long.toBinaryString(#0)")
    public native def toBinaryString(): String;    
    
    @Native("java", "java.lang.Long.toString(#0)")
    public native def toString(): String;
    
    @Native("java", "java.lang.Long.parseLong(#1, #2)")
    public native static def parseLong(String, radix: Int): Long throws NumberFormatException;
    
    @Native("java", "java.lang.Long.parseLong(#1)")
    public native static def parseLong(String): Long throws NumberFormatException;

    @Native("java", "java.lang.Long.getLong(#1, #2)")
    public native static def getLong(property: String, defaultValue: Long): Long;
    
    @Native("java", "x10.core.Box.make(java.lang.Long.getLong(#1))")
    public native static def getLong(property: String): Box[Long];

    @Native("java", "java.lang.Long.highestOneBit(#0)")
    public native def highestOneBit(): Long;
    
    @Native("java", "java.lang.Long.lowestOneBit(#0)")
    public native def lowestOneBit(): Long;

    @Native("java", "java.lang.Long.numberOfLeadingZeros(#0)")
    public native def numberOfLeadingZeros(): Int;

    @Native("java", "java.lang.Long.numberOfTrailingZeros(#0)")
    public native def numberOfTrailingZeros(): Int;

    @Native("java", "java.lang.Long.bitCount(#0)")
    public native def bitCount(): Int;

    @Native("java", "java.lang.Long.rotateLeft(#0)")
    public native def rotateLeft(): Long;
    
    @Native("java", "java.lang.Long.rotateRight(#0)")
    public native def rotateRight(): Long;
    
    @Native("java", "java.lang.Long.reverse(#0)")
    public native def reverse(): Long;
    
    @Native("java", "java.lang.Long.signum(#0)")
    public native def signum(): Int;
    
    @Native("java", "java.lang.Long.reverseBytes(#0)")
    public native def reverseBytes(): Long;
}
