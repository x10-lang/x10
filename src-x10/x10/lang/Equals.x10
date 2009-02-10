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

public interface Equals[-T] /* {T <: Equals[T]} */ {
    public def equals(T) /* {T <: Equals[T]} */: Boolean;
}
