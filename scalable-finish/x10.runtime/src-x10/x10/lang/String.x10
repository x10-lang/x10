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
public final class String implements (Int) => Char {
    // TODO: constructors
    public native def this(): String;
    public native def this(String): String;
    
    @Native("java", "(#0).equals(#1)")
    @Native("c++", "x10aux::equals(#0,#1)")
    public native global safe def equals(Any): boolean;
    
    @Native("java", "(#0).hashCode()")
    @Native("c++", "x10aux::hash_code(#0)")
    public native global safe def hashCode(): int;
    
    @Native("java", "(#0).toString()")
    @Native("c++", "x10aux::to_string(#0)")
    public native global safe def toString(): String;
    
    @Native("java", "#0.length()")
    @Native("c++", "#0->length()")
    public native global def length(): Int;
    
    @Native("java", "(#0).charAt(#1)")
    @Native("c++", "(#0)->charAt(#1)")
    public native global def apply(index: Int): Char;

    @Native("java", "(#0).charAt(#1)")
    @Native("c++", "(#0)->charAt(#1)")
    public native global def charAt(index: Int): Char;
    
    @Native("java", "x10.core.RailFactory.<java.lang.Character>makeValRailFromJavaArray(#0.toCharArray())")
    @Native("c++", "(#0)->chars()")
    public native global def chars(): ValRail[Char];
    
    @Native("java", "x10.core.RailFactory.<java.lang.Byte>makeValRailFromJavaArray(#0.getBytes())")
    @Native("c++", "(#0)->bytes()")
    public native global def bytes(): ValRail[Byte];
    
    @Native("java", "#0.substring(#1, #2)")
    @Native("c++", "(#0)->substring(#1, #2)")
    public native global def substring(fromIndex: Int, toIndex: Int): String;
    
    @Native("java", "#0.indexOf(#1)")
    @Native("c++", "(#0)->indexOf(#1)")
    public native global def indexOf(Char): Int;
    
    @Native("java", "#0.indexOf(#1)")
    @Native("c++", "(#0)->indexOf(#1)")
    public native global def indexOf(String): Int;
    
    @Native("java", "#0.lastIndexOf(#1)")
    @Native("c++", "(#0)->lastIndexOf(#1)")
    public native global def lastIndexOf(Char): Int;

    @Native("java", "#0.lastIndexOf(#1)")
    @Native("c++", "(#0)->lastIndexOf(#1)")
    public native global def lastIndexOf(String): Int;

    @Native("java", "x10.core.RailFactory.makeValRailFromJavaArray(#0.split(#1))")
    @Native("c++", "(#0)->split(#1)")
    public native global def split(String): ValRail[String];

    @Native("java", "java.lang.String.valueOf(#4)")
    @Native("c++", "x10aux::safe_to_string(#4)")
    public native static def valueOf[T](T):String;
    
    @Native("java", "java.lang.String.format(#1, #2.getBoxedArray())")
    @Native("c++", "x10::lang::String::format(#1,#2)")
    public native static def format(fmt: String, Rail[Any]): String;
    
    @Native("java", "java.lang.String.format(#1, #2.getBoxedArray())")
    @Native("c++", "x10::lang::String::format(#1,#2)")
    public native static def format(fmt: String, ValRail[Any]): String;
}
