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

@NativeRep("java", "x10.core.ValRail<#1>", "x10.core.ValRail.BoxedValRail", "new x10.core.ValRail.RTT(#2)")
public value class ValRail[+T](length: nat)
    implements Indexable[nat,T], Iterable[T]
{
    // need to declare a constructor to shut up the initialization checker
    private native def this(n: nat): ValRail[T]{length==n};
    
    @Native("java", "x10.core.RailFactory.<#2>makeValRail(#3, #4, #5)")
    public native static def make[T](length: Nat, init: (Nat) => T, value: boolean): ValRail[T](length);
    
     @Native("java", "x10.core.RailFactory.<#2>makeValRail(#3, #4,#5)")
    public native static def make[T](length: Nat, init: (Nat) => T): ValRail[T](length);

    @Native("java", "#0.get(#1)")
    public native def get(i: nat): T;

    @Native("java", "#0.apply(#1)")
    public native def apply(i: nat): T;
    
    @Native("java", "#0.iterator()")
    public native def iterator(): Iterator[T];
}
