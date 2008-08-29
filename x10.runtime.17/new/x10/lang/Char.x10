package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "char")
public final value Char {
    // Binary and unary operations and conversions are built-in.  No need to declare them here.
    
    @Native("java", "java.lang.Character.MIN_VALUE")
    public const MIN_VALUE = '0';
    
    @Native("java", "java.lang.Character.MAX_VALUE")
    public const MAX_VALUE = '\uffff';
    
    // Duplicate the methods from java.lang.Character, changing static methods to non-static.
    // We'll ignore the code point methods for now and just include the isXXX ones.
    
    @Native("java", "java.lang.Character.isLowerCase(#0)")
    public native def isLowerCase(): boolean;
    @Native("java", "java.lang.Character.isUpperCase(#0)")
    public native def isUpperCase(): boolean;
    @Native("java", "java.lang.Character.isTitleCase(#0)")
    public native def isTitleCase(): boolean;
    @Native("java", "java.lang.Character.isDigit(#0)")
    public native def isDigit(): boolean;
    @Native("java", "java.lang.Character.isLetter(#0)")
    public native def isLetter(): boolean;
    @Native("java", "java.lang.Character.isLetterOrDigit(#0)")
    public native def isLetterOrDigit(): boolean;
    @Native("java", "java.lang.Character.isJavaLetter(#0)")
    public native def isJavaLetter(): boolean;
    @Native("java", "java.lang.Character.isJavaLetterOrDigit(#0)")
    public native def isJavaLetterOrDigit(): boolean;
    @Native("java", "java.lang.Character.isJavaIdentifierStart(#0)")
    public native def isJavaIdentifierStart(): boolean;
    @Native("java", "java.lang.Character.isJavaIdentifierPart(#0)")
    public native def isJavaIdentifierPart(): boolean;
    @Native("java", "java.lang.Character.isUnicodeIdentifierStart(#0)")
    public native def isUnicodeIdentifierStart(): boolean;
    @Native("java", "java.lang.Character.isUnicodeIdentifierPart(#0)")
    public native def isUnicodeIdentifierPart(): boolean;
    @Native("java", "java.lang.Character.isIdentifierIgnorable(#0)")
    public native def isIdentifierIgnorable(): boolean;
    @Native("java", "java.lang.Character.isSpace(#0)")
    public native def isSpace(): boolean;
    @Native("java", "java.lang.Character.isSpaceChar(#0)")
    public native def isSpaceChar(): boolean;
    @Native("java", "java.lang.Character.isWhitespace(#0)")
    public native def isWhitespace(): boolean;
    @Native("java", "java.lang.Character.isISOControl(#0)")
    public native def isISOControl(): boolean;
    
    @Native("java", "java.lang.Character.toLowerCase(#0)")
    public native def toLowerCase(): Char;
    @Native("java", "java.lang.Character.toUpperCase(#0)")
    public native def toUpperCase(): Char;
    @Native("java", "java.lang.Character.toTitleCase(#0)")
    public native def toTitleCase(): Char;
    
    @Native("java", "((int) #0)")
    public native def ord(): Int;
    @Native("java", "((char) #1)")
    public native static def ord(Char): Int;
    @Native("java", "((char) #1)")
    public native static def chr(Int): Char;
        
    @Native("java", "java.lang.Character.reverseBytes(#0)")
    public native def reverseBytes(): Char;
}
