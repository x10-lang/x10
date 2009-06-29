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

@NativeRep("java", "x10.core.Ref", null, null)
@NativeRep("c++", "x10aux::ref<x10::lang::Ref>", "x10::lang::Ref", null)
public class Object(
        @Native("java", "x10.lang.Place.place(x10.core.Ref.location(#0))")
        @Native("c++", "x10::lang::Place::place(x10aux::location(#0))")
    	location: Place) 
{
    public native def this();
    
    @Native("java", "#0.equals(#1)")
    @Native("c++", "x10aux::equals(#0, #1)")
    public native def equals(Object): Boolean;
    
    @Native("java", "#0.hashCode()")
    @Native("c++", "x10aux::hash_code(#0)")
    public native def hashCode(): Int;

    @Native("java", "#0.toString()")
    @Native("c++", "x10aux::to_string(#0)")
    public native def toString(): String;

    @Native("java", "#0.getClass().toString()")
    @Native("c++", "x10aux::class_name(#0)")
    public native def className(): String;
}
