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

@NativeRep("java", "short", "x10.core.BoxedShort", "x10.types.Type.SHORT")
@NativeRep("c++", "x10_short", "x10_short", null)
public final value Short implements Integer, Signed {
    // Binary and unary operations and conversions are built-in.  No need to declare them here.
    
    @Native("java", "java.lang.Short.MIN_VALUE")
    @Native("c++", "(x10_short)0x8000")
    public const MIN_VALUE = 0x8000 as Short;
    
    @Native("java", "java.lang.Short.MAX_VALUE")
    @Native("c++", "(x10_short)0x7fff")
    public const MAX_VALUE = 0x7fff as Short;

    @Native("java", "java.lang.Integer.toString(#0, #1)")
    @Native("c++", "x10aux::short_utils::toString(#0, #1)")
    public native def toString(radix: Int): String;
    
    @Native("java", "java.lang.Integer.toHexString(#0)")
    @Native("c++", "x10aux::short_utils::toHexString(#0)")
    public native def toHexString(): String;    
    
    @Native("java", "java.lang.Integer.toOctalString(#0)")
    @Native("c++", "x10aux::short_utils::toOctalString(#0)")
    public native def toOctalString(): String;  
      
    @Native("java", "java.lang.Integer.toBinaryString(#0)")
    @Native("c++", "x10aux::short_utils::toBinaryString(#0)")
    public native def toBinaryString(): String;    
    
    @Native("java", "java.lang.Short.toString(#0)")
    @Native("c++", "x10aux::to_string(#0)")
    public native def toString(): String;
    
    @Native("java", "java.lang.Short.parseShort(#1, #2)")
    @Native("c++", "x10aux::short_utils::parseShort(#1, #2)")
    public native static def parseShort(String, radix: Int): Short throws NumberFormatException;
    
    @Native("java", "java.lang.Short.parseShort(#1)")
    @Native("c++", "x10aux::short_utils::parseShort(#1)")
    public native static def parseShort(String): Short throws NumberFormatException;

    @Native("java", "java.lang.Short.reverseBytes(#0)")
    @Native("c++", "x10aux::short_utils::reverseBytes(#0)")
    public native def reverseBytes(): Short;
}
