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
    @Native("c++", "#0->equals(#1)")
    public def equals(Object): Boolean;

    @Native("java", "#0.hashCode()")
    @Native("c++", "#0->hashCode()")
    public def hashCode(): Int;

    @Native("java", "#0.toString()")
    @Native("c++", "#0->toString()")
    public def toString(): String;

    @Native("java", "#0.getClass().toString()")
    @Native("c++", "#0._type()->name()")
    public def className(): String;
}
