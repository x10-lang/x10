/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

import x10.compiler.NativeRep;

@NativeRep("c++", "x10aux::ref<x10::lang::Primitive>", "x10::lang::Primitive", null) // HACK: pretend structs are values
public abstract struct Primitive {}
