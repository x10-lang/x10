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

@NativeRep("java", "char", "x10.core.BoxedChar", "x10.types.Type.CHAR")
@NativeRep("c++", "x10_char", "x10_char", null)
public final value Char {
    // Binary and unary operations and conversions are built-in.  No need to declare them here.
    
    // Duplicate the methods from java.lang.Character, changing static methods to non-static.
    // We'll ignore the code point methods for now and just include the isXXX ones.
    
    @Native("java", "java.lang.Character.isLowerCase(#0)")
    @Native("c++", "x10aux::char_utils::isLowerCase(#0)")
    public native def isLowerCase(): boolean;

    @Native("java", "java.lang.Character.isUpperCase(#0)")
    @Native("c++", "x10aux::char_utils::isUpperCase(#0)")
    public native def isUpperCase(): boolean;

    @Native("java", "java.lang.Character.isTitleCase(#0)")
    @Native("c++", "x10aux::char_utils::isTitleCase(#0)")
    public native def isTitleCase(): boolean;

    @Native("java", "java.lang.Character.isDigit(#0)")
    @Native("c++", "x10aux::char_utils::isDigit(#0)")
    public native def isDigit(): boolean;

    @Native("java", "java.lang.Character.isLetter(#0)")
    @Native("c++", "x10aux::char_utils::isLetter(#0)")
    public native def isLetter(): boolean;

    @Native("java", "java.lang.Character.isLetterOrDigit(#0)")
    @Native("c++", "x10aux::char_utils::isLetterOrDigit(#0)")
    public native def isLetterOrDigit(): boolean;

    @Native("java", "java.lang.Character.isUnicodeIdentifierStart(#0)")
    @Native("c++", "x10aux::char_utils::isUnicodeIdentifierStart(#0)")
    public native def isUnicodeIdentifierStart(): boolean;

    @Native("java", "java.lang.Character.isUnicodeIdentifierPart(#0)")
    @Native("c++", "x10aux::char_utils::isUnicodeIdentifierPart(#0)")
    public native def isUnicodeIdentifierPart(): boolean;

    @Native("java", "java.lang.Character.isIdentifierIgnorable(#0)")
    @Native("c++", "x10aux::char_utils::isUnicodeIdentiferIgnorable(#0)")
    public native def isIdentifierIgnorable(): boolean;

    @Native("java", "java.lang.Character.isSpaceChar(#0)")
    @Native("c++", "x10aux::char_utils::isSpaceChar(#0)")
    public native def isSpaceChar(): boolean;

    @Native("java", "java.lang.Character.isWhitespace(#0)")
    @Native("c++", "x10aux::char_utils::isWhitespace(#0)")
    public native def isWhitespace(): boolean;

    @Native("java", "java.lang.Character.isISOControl(#0)")
    @Native("c++", "x10aux::char_utils::isISOControl(#0)")
    public native def isISOControl(): boolean;
    
    @Native("java", "java.lang.Character.toLowerCase(#0)")
    @Native("c++", "x10aux::char_utils::toLowerCase(#0)")
    public native def toLowerCase(): Char;

    @Native("java", "java.lang.Character.toUpperCase(#0)")
    @Native("c++", "x10aux::char_utils::toUpperCase(#0)")
    public native def toUpperCase(): Char;

    @Native("java", "java.lang.Character.toTitleCase(#0)")
    @Native("c++", "x10aux::char_utils::toTitleCase(#0)")
    public native def toTitleCase(): Char;
    
    @Native("java", "((int) (#0))")
    @Native("c++", "((x10_int) (#0))")
    public native def ord(): Int;

    @Native("java", "((int) (#1))")
    @Native("c++", "((x10_int) (#1))")
    public native static def ord(Char): Int;

    @Native("java", "((char) (#1))")
    @Native("c++", "((x10_char) (#1))")
    public native static def chr(Int): Char;
        
    @Native("java", "java.lang.Character.reverseBytes(#0)")
    @Native("c++", "x10aux::char_utils::reverseBytes(#0)")
    public native def reverseBytes(): Char;
}
