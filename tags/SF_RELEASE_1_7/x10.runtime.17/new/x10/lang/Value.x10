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
public value Value {
    public native def equals(Object): boolean;
    public native def hashCode(): int;
    public native def toString(): String;
    
    @Native("java", "(#0).getClass().toString()")
    public native def className():String;
}
