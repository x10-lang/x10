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

@NativeRep("java", "java.lang.String")
public final value String implements (nat) => Char {
    // TODO: constructors
    public native def this(): String;
    public native def this(String): String;
    
    @Native("java", "(#0).equals(#1)")
    public native def equals(Object): boolean;
    
    @Native("java", "(#0).hashCode()")
    public native def hashCode(): int;
    
    @Native("java", "(#0).toString()")
    public native def toString(): String;
    
    @Native("java", "\"x10.lang.String\"")
    public native def className():String;
    
    @Native("java", "#0.length()")
    public native def length(): Int;
    
    @Native("java", "#0.charAt(#1)")
    public native def apply(index: nat): Char;

    @Native("java", "#0.charAt(#1)")
    public native def charAt(index: nat): Char;
    
    @Native("java", "#0.substring(#1, #2)")
    public native def substring(fromIndex: nat, toIndex: nat): String;
}
