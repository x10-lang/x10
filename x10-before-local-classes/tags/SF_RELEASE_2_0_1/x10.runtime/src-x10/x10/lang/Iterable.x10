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
 * The compiler accepts the syntax 'for (x in s)' if x is a formal
 * parameter of type T, and if s implements Iterable[T].
 */
@NativeRep("java", "x10.core.Iterable<#1 >", null, null)
public interface Iterable[+T] {
    @Native("java", "(#0).iterator()")
    def iterator(): Iterator[T];
}
