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
        @Native("java", "x10.lang.Place.place(#0.location())")
        @Native("c++", "x10::lang::Place::place(x10aux::location(#0))")
    	location: Place) 
    /* @EQ implements Equals[Ref] */
    implements Object
{

    @Native("java", "new x10.lang.Box<#2>(#3, #4)")
//    @Native("c++", "new (x10aux::alloc<x10::lang::Box>()) x10::lang::Box<#2>(#3, #4)")
    @Native("c++", "x10::lang::Box<#1 >::_make(#4)")
    public static operator [T](x: T): Box[T] /* {T <: Value} */ = new Box[T](x);

    public native def this();

    @Native("java", "#0.equals(#1)")
    @Native("c++", "x10aux::equals(#0,#1)")
    public native def equals(Ref): boolean;

    @Native("java", "#0.equals(#1)")
    @Native("c++", "x10aux::equals(#0,#1)")
    public native def equals(Value): boolean;

    @Native("java", "#0.hashCode()")
    @Native("c++", "x10aux::hash_code(#0)")
    public native def hashCode() : Int;

    @Native("java", "#0.toString()")
    @Native("c++", "x10aux::to_string(#0)")
    public native def toString() : String;

    @Native("java", "#0.getClass().getName()")
    @Native("c++", "x10aux::class_name(#0)")
    public native def className() : String;
}
