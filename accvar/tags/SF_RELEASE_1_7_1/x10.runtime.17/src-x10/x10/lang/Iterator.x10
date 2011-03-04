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

@NativeRep("java", "java.util.Iterator<#1>")
public interface Iterator[+T] {
    @Native("java", "#0.hasNext()")
    public def hasNext():boolean;

    @Native("java", "#0.next()")
    public def next():T;
}
