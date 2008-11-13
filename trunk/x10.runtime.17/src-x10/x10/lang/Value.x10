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

/**
 * Base class of all value classes.
 */
@NativeRep("java", "x10.core.Value", "x10.core.Value.BoxedValue", null)
@NativeRep("c++", "x10aux::ref<x10::lang::Value>", "x10::lang::Value", null)
public value Value {
    @Native("java", "#0.equals(#1)")
    @Native("c++", "(#0)->equals(#1)")
    public native def equals(Object): boolean;

    @Native("java", "#0.hashCode()")
    @Native("c++", "(#0)->hashCode()")
    public native def hashCode(): int;

    @Native("java", "#0.toString()")
    @Native("c++", "(#0)->toString()")
    public native def toString() : String;
    
    @Native("java", "#0.getClass().toString()")
    @Native("c++", "(#0)->_type()->name()")
    public native def className() : String;
}
