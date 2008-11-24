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

@NativeRep("java", "java.lang.String", "x10.core.BoxedString", null)
@NativeRep("c++", "x10aux::ref<x10::lang::String>", "x10::lang::String", null)
public final value String implements (nat) => Char {
    // TODO: constructors
    public native def this(): String;
    public native def this(String): String;
    
    @Native("java", "(#0).equals(#1)")
    @Native("c++", "(#0)->equals(#1)")
    public native def equals(Object): boolean;
    
    @Native("java", "(#0).hashCode()")
    @Native("c++", "(#0)->hashCode()")
    public native def hashCode(): int;
    
    @Native("java", "(#0).toString()")
    @Native("c++", "(#0)->toString()")
    public native def toString(): String;
    
    @Native("java", "\"x10.lang.String\"")
    @Native("c++", "(#0)->_type()->name()")
    public native def className():String;
    
    @Native("java", "#0.length()")
    @Native("c++", "#0->length()")
    public native def length(): Int;
    
    @Native("java", "(#0).charAt(#1)")
    @Native("c++", "(#0)->charAt(#1)")
    public native def apply(index: nat): Char;

    @Native("java", "(#0).charAt(#1)")
    @Native("c++", "(#0)->charAt(#1)")
    public native def charAt(index: nat): Char;
    
    @Native("java", "x10.core.RailFactory.<java.lang.Character>makeValRailFromJavaArray(#0.toCharArray())")
    @Native("c++", "(#0)->chars()")
    public native def chars(): ValRail[Char];
    
    @Native("java", "x10.core.RailFactory.<java.lang.Byte>makeValRailFromJavaArray(#0.getBytes())")
    @Native("c++", "(#0)->bytes()")
    public native def bytes(): ValRail[Byte];
    
    @Native("java", "#0.substring(#1, #2)")
    @Native("c++", "(#0)->substring(#1, #2)")
    public native def substring(fromIndex: nat, toIndex: nat): String;
    
    @Native("java", "#0.indexOf(#1)")
    @Native("c++", "(#0)->indexOf(#1)")
    public native def indexOf(Char): Int;
    
    @Native("java", "#0.lastIndexOf(#1)")
    @Native("c++", "(#0)->lastIndexOf(#1)")
    public native def lastIndexOf(Char): Int;

    @Native("java", "java.lang.String.format(#1, #2.getBoxedArray())")
    @Native("c++", "x10::lang::String::format(#1,#2)")
    public native static def format(fmt: String, Rail[Object]): String;
    
    @Native("java", "java.lang.String.format(#1, #2.getBoxedArray())")
    @Native("c++", "x10::lang::String::format(#1,#2)")
    public native static def format(fmt: String, ValRail[Object]): String;
}
