package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "byte")
public final value Byte implements Integer, Signed {
    // Binary and unary operations and conversions are built-in.  No need to declare them here.
    
    @Native("java", "java.lang.Byte.MIN_VALUE")
    public const MIN_VALUE = 0x8000 to Byte;
    
    @Native("java", "java.lang.Byte.MAX_VALUE")
    public const MAX_VALUE = 0x7fff to Byte;

    @Native("java", "java.lang.Byte.toString(#0, #1)")
    public native def toString(radix: Int): String;
    
    @Native("java", "java.lang.Byte.toHexString(#0)")
    public native def toHexString(): String;    
    
    @Native("java", "java.lang.Byte.toOctalString(#0)")
    public native def toOctalString(): String;  
      
    @Native("java", "java.lang.Byte.toBinaryString(#0)")
    public native def toBinaryString(): String;    
    
    @Native("java", "java.lang.Byte.toString(#0)")
    public native def toString(): String;
    
    @Native("java", "java.lang.Byte.parseByte(#1, #2)")
    public native static def parseByte(String, radix: Int): Byte throws NumberFormatException;
    
    @Native("java", "java.lang.Byte.parseByte(#1)")
    public native static def parseByte(String): Byte throws NumberFormatException;
    
    @Native("java", "java.lang.Byte.reverseBytes(#0)")
    public native def reverseBytes(): Byte;
}
