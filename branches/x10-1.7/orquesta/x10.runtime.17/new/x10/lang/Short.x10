package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "short")
public final value Short implements Integer, Signed {
    // Binary and unary operations and conversions are built-in.  No need to declare them here.
    
    @Native("java", "java.lang.Short.MIN_VALUE")
    public const MIN_VALUE = 0x8000 to Short;
    
    @Native("java", "java.lang.Short.MAX_VALUE")
    public const MAX_VALUE = 0x7fff to Short;

    @Native("java", "java.lang.Short.toString(#0, #1)")
    public native def toString(radix: Int): String;
    
    @Native("java", "java.lang.Short.toHexString(#0)")
    public native def toHexString(): String;    
    
    @Native("java", "java.lang.Short.toOctalString(#0)")
    public native def toOctalString(): String;  
      
    @Native("java", "java.lang.Short.toBinaryString(#0)")
    public native def toBinaryString(): String;    
    
    @Native("java", "java.lang.Short.toString(#0)")
    public native def toString(): String;
    
    @Native("java", "java.lang.Short.parseShort(#1, #2)")
    public native static def parseShort(String, radix: Int): Short throws NumberFormatException;
    
    @Native("java", "java.lang.Short.parseShort(#1)")
    public native static def parseShort(String): Short throws NumberFormatException;

    @Native("java", "java.lang.Short.reverseBytes(#0)")
    public native def reverseBytes(): Short;
}
