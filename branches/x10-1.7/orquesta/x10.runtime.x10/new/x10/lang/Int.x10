package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "int")
public final value Int implements Integer, Signed {
    // Binary and unary operations and conversions are built-in.  No need to declare them here.
    
    @Native("java", "java.lang.Integer.MIN_VALUE")
    public const MIN_VALUE = 0x80000000;
    
    @Native("java", "java.lang.Integer.MAX_VALUE")
    public const MAX_VALUE = 0x7fffffff;

    @Native("java", "java.lang.Integer.toString(#0, #1)")
    public native def toString(radix: Int): String;
    
    @Native("java", "java.lang.Integer.toHexString(#0)")
    public native def toHexString(): String;    
    
    @Native("java", "java.lang.Integer.toOctalString(#0)")
    public native def toOctalString(): String;  
      
    @Native("java", "java.lang.Integer.toBinaryString(#0)")
    public native def toBinaryString(): String;    
    
    @Native("java", "java.lang.Integer.toString(#0)")
    public native def toString(): String;
    
    @Native("java", "java.lang.Integer.parseInt(#1, #2)")
    public native static def parseInt(String, radix: Int): Int throws NumberFormatException;
    
    @Native("java", "java.lang.Integer.parseInt(#1)")
    public native static def parseInt(String): Int throws NumberFormatException;

    @Native("java", "java.lang.Integer.getInteger(#1, #2).intValue()")
    public native static def getInteger(property: String, defaultValue: Int): Int;
    
    @Native("java", "x10.runtime.Runtime.boxJavaObject(java.lang.Integer.getInteger(#1))")
    public native static def getInteger(property: String): Box[Int];

    @Native("java", "java.lang.Integer.highestOneBit(#0)")
    public native def highestOneBit(): Int;
    
    @Native("java", "java.lang.Integer.lowestOneBit(#0)")
    public native def lowestOneBit(): Int;

    @Native("java", "java.lang.Integer.numberOfLeadingZeros(#0)")
    public native def numberOfLeadingZeros(): Int;

    @Native("java", "java.lang.Integer.numberOfTrailingZeros(#0)")
    public native def numberOfTrailingZeros(): Int;

    @Native("java", "java.lang.Integer.bitCount(#0)")
    public native def bitCount(): Int;

    @Native("java", "java.lang.Integer.rotateLeft(#0)")
    public native def rotateLeft(): Int;
    
    @Native("java", "java.lang.Integer.rotateRight(#0)")
    public native def rotateRight(): Int;
    
    @Native("java", "java.lang.Integer.reverse(#0)")
    public native def reverse(): Int;
    
    @Native("java", "java.lang.Integer.signum(#0)")
    public native def signum(): Int;
    
    @Native("java", "java.lang.Integer.reverseBytes(#0)")
    public native def reverseBytes(): Int;
}
