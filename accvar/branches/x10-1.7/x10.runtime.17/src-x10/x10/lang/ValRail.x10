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
@NativeRep("c++", "x10aux::ref<x10::lang::ValRail<#1 > >", "x10::lang::ValRail<#1 >", null)
public final value class ValRail[+T](length: nat)
    implements (nat) => T, Iterable[T]
{
    // need to declare a constructor to shut up the initialization checker
    private native def this(n: nat): ValRail[T]{self.length==n};
    
    @Native("java", "x10.core.RailFactory.<#2>makeValRail(#3, #4, #5)")
    @Native("c++", "x10::lang::ValRail<#1 >::make(#4, #5)")
    public native static def make[T](length: Nat, init: (Nat) => T, value: boolean): ValRail[T](length);
    
    @Native("java", "x10.core.RailFactory.<#2>makeValRail(#3, #4, #5)")
    @Native("c++", "x10::lang::ValRail<#1 >::make(#4, #5)")
    public native static def make[T](length: Nat, init: (Nat) => T): ValRail[T](length);

    @Native("java", "x10.core.RailFactory.<#2>makeValRailFromRail(#3, #4)")
    @Native("c++", "x10::lang::ValRail<#1 >::make(#4)")
    public native static def $convert[U](r: Rail[U]): ValRail[U]{self.length==r.length};

    @Native("java", "#0.get(#1)")
    @Native("c++", "(#0)->get(#1)")
    public native def get(i: nat): T;

    @Native("java", "#0.apply(#1)")
    @Native("c++", "(*#0)[#1]")
    public native def apply(i: nat): T;
    
    @Native("java", "#0.iterator()")
    @Native("c++", "(#0)->iterator()")
    public native def iterator(): Iterator[T];
}
