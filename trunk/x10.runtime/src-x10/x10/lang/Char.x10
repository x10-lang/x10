/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "char", "x10.core.BoxedChar", "x10.rtt.Type.CHAR")
@NativeRep("c++", "x10_char", "x10_char", null)
public final struct Char {
    @Native("java", "((char) (#1))")
    @Native("c++",  "((x10_char) (char) (#1))")
    public native static safe operator (x:Byte) as Char;
    
    @Native("java", "((char) (#1))")
    @Native("c++",  "((x10_char) (char) (#1))")
    public native static safe operator (x:Short) as Char;
    
    @Native("java", "((char) (#1))")
    @Native("c++",  "((x10_char) (char) (#1))")
    public native static safe operator (x:Int) as Char;
    
    @Native("java", "((char) (#1))")
    @Native("c++",  "((x10_char) (char) (#1))")
    public native static safe operator (x:Long) as Char;
    
    @Native("java", "((char) ((#1) + (#2)))")
    @Native("c++",  "((x10_char) (char) ((#1) + (#2)))")
    public native static safe operator (x:Char) + (y:Int): Char;

    @Native("java", "((char) ((#1) + (#2)))")
    @Native("c++",  "((x10_char) (char) ((#1) + (#2)))")
    public native static safe operator (x:Int) + (y:Char): Char;

    @Native("java", "((char) ((#1) - (#2)))")
    @Native("c++",  "((x10_char) (char) ((#1) - (#2)))")
    public native static safe operator (x:Char) - (y:Int): Char;

    @Native("java", "((#1) - (#2))")
    @Native("c++",  "((#1) - (#2))")
    public native static safe operator (x:Char) - (y:Char): Int;

    @Native("java", "((#1) < (#2))")
    @Native("c++",  "((#1) < (#2))")
    public native static safe operator (x:Char) < (y:Char): Boolean;
    @Native("java", "((#1) > (#2))")
    @Native("c++",  "((#1) > (#2))")
    public native static safe operator (x:Char) > (y:Char): Boolean;
    @Native("java", "((#1) <= (#2))")
    @Native("c++",  "((#1) <= (#2))")
    public native static safe operator (x:Char) <= (y:Char): Boolean;
    @Native("java", "((#1) >= (#2))")
    @Native("c++",  "((#1) >= (#2))")
    public native static safe operator (x:Char) >= (y:Char): Boolean;
    
    @Native("java", "java.lang.Character.toString(#0)")
    @Native("c++", "x10aux::to_string(#0)")
    public global safe native def toString(): String;
    
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
    @Native("c++", "((x10_int) (#0).v)")
    public native def ord(): Int;

    @Native("java", "((int) (#1))")
    @Native("c++", "((x10_int) (#1).v)")
    public native static def ord(Char): Int;

    @Native("java", "((char) (#1))")
    @Native("c++", "((x10_char) (int) (#1))")
    public native static def chr(Int): Char;
        
    @Native("java", "java.lang.Character.reverseBytes(#0)")
    @Native("c++", "x10aux::char_utils::reverseBytes(#0)")
    public native def reverseBytes(): Char;

    @Native("java", "x10.rtt.Equality.equalsequals(#0, #1)")
    @Native("c++", "x10aux::equals(#0,#1)")
    public global safe native def equals(x:Any):Boolean;

    @Native("java", "x10.rtt.Equality.equalsequals(#0, #1)")
    @Native("c++", "x10aux::equals(#0,#1)")
    public global safe native def equals(x:Char):Boolean;
}
