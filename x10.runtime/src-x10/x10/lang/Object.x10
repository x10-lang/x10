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
    @Native("c++", "x10aux::equals(x10aux::class_cast<x10aux::ref<x10::lang::Object> >(#0), #1)")
    public def equals(Ref): Boolean;
    
    @Native("java", "#0.equals(#1)")
    @Native("c++", "x10aux::equals(x10aux::class_cast<x10aux::ref<x10::lang::Object> >(#0), #1)")
    public def equals(Value): Boolean;
    
    @Native("java", "#0.hashCode()")
    @Native("c++", "x10aux::hash_code(x10aux::class_cast<x10aux::ref<x10::lang::Object> >(#0))")
    public def hashCode(): Int;

    @Native("java", "#0.toString()")
    @Native("c++", "x10aux::to_string(x10aux::class_cast<x10aux::ref<x10::lang::Object> >(#0))")
    public def toString(): String;

    @Native("java", "#0.getClass().toString()")
    @Native("c++", "x10aux::class_name(x10aux::class_cast<x10aux::ref<x10::lang::Object> >(#0))")
    public def className(): String;

    property def loc():Place;
    property def at(p:Place):Boolean;
    property def at(r:Ref):Boolean;

}
