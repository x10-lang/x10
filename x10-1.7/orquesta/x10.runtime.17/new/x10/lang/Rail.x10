package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "x10.core.Rail<#1>")
public abstract value class Rail[T](length: nat)
    implements Indexable[nat,T], Settable[nat,T]
{
    // need to declare a constructor to shut up the initialization checker
    private native def this(n: nat): Rail[T]{length==n};
    
    @Native("java", "x10.core.RailFactory.<#2>makeValRail(#3, #4, #5)")
    public native static def makeVal[S](length: nat, init: (nat) => S): ValRail[S]{self.length==length}; // should be native

    @Native("java", "x10.core.RailFactory.<#2>makeVarRail(#3, #4, #5)")
    public native static def makeVar[S](length: nat, init: (nat) => S): Rail[S]{self.length==length}; // should be native
    
    @Native("java", "x10.core.RailFactory.<#2>makeRailFromValRail(#3, #4)")
    public native static def make[U](r: ValRail[U]): Rail[U]{self.length==r.length};

    @Native("java", "(#0).get(#1)")
    public native def get(i: nat): T;

    @Native("java", "(#0).apply(#1)")
    public native def apply(i: nat): T;

    @Native("java", "(#0).set(#1, #2)")
    public native def set(i: nat, v: T): void;
}
