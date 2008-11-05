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
public interface Object {
    @Native("java", "#0.equals(#1)")
    public def equals(Object): boolean;

    @Native("java", "#0.hashCode()")
    public def hashCode(): int;

    @Native("java", "#0.toString()")
    public def toString(): String;

    @Native("java", "#0.getClass().toString()")
    public def className(): String;
}
