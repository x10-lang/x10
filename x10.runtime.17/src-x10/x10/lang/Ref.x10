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

// MASSIVE FIXME:
// uncomment hashCode or other super.blah() implementation to get compiler stack trace

/**
 * The base class for all reference classes.
 */
@NativeRep("java", "x10.core.Ref", null, null)
@NativeRep("c++", "x10aux::ref<x10::lang::Ref>", "x10::lang::Ref", null)
public class Ref(
        @Native("java", "(x10.lang.Place) #0.location()")
        @Native("c++", "/*TODO: PLACES_NOT_IMPLEMENTED*/0")
        location: Place) {

    public native def this();

    @Native("java", "#0.equals(#1)")
    @Native("c++", "#0.equals(#1)")
    public native def equals(other : Object) : Boolean;

    @Native("java", "#0.hashCode()")
    @Native("c++", "#0.hashCode()")
    public native def hashCode() : Int;

    @Native("java", "#0.toString()")
    @Native("c++", "#0.toString()")
    public native def toString() : String;

    @Native("java", "#0.getClass().toString()")
    @Native("c++", "x10aux::getRTT<x10::lang::Value>()->name()")
    public native def className() : String;
}
