/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.util;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "java.util.Iterator<#1>")
public class Iterator[T] {
    @Native("java", "#0.hasNext()")
    public native def hasNext():boolean;

    @Native("java", "#0.next()")
    public native def next():T;
}
