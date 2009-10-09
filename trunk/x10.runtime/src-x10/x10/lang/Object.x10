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

@NativeRep("java", "java.lang.Object", null, null)
@NativeRep("c++", "x10aux::ref<x10::lang::Object>", "x10::lang::Object", null)
public interface Object {
    @Native("java", "#0.equals(#1)")
    @Native("c++", "x10aux::equals(#0, #1)")
    public def equals(Object): Boolean;
    
    @Native("java", "#0.hashCode()")
    @Native("c++", "x10aux::hash_code(#0)")
    public def hashCode(): Int;

    @Native("java", "#0.toString()")
    @Native("c++", "x10aux::to_string(#0)")
    public def toString(): String;

    @Native("java", "#0.getClass().toString()")
    @Native("c++", "x10aux::type_name(#0)")
    public def typeName(): String;

    @Native("java", "x10.lang.Place.place(#0.location())")
    @Native("c++", "x10::lang::Place_methods::place(x10aux::location(#0))")
    property def loc():Place;

    @Native("java", "x10.core.Ref.at(#0, #1.id)")
    @Native("c++", "(x10aux::location(#0) == (#1)->FMGL(id))")
    property def at(p:Place):Boolean;

    @Native("java", "x10.core.Ref.at(#0, #1)")
    @Native("c++", "(x10aux::location(#0) == x10aux::location(#1))")
    property def at(r:Ref):Boolean;
}
