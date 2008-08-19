package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

public abstract value class Rail[T](length: nat)
    implements Indexable[nat,T], Settable[nat,T]
{
    // need to declare a constructor to shut up the initialization checker
    private native def this(n: nat): Rail[T]{length==n};
    
    @Native("java", "x10.runtime.Runtime.makeValRail(#0, #1)")
    incomplete public static def makeVal[S](length: nat, init: (nat) => S): ValRail[S]{self.length==length}; // should be native

    @Native("java", "x10.runtime.Runtime.makeVarRail(#0, #1)")
    incomplete public static def makeVar[S](length: nat, init: (nat) => S): Rail[S]{self.length==length}; // should be native
    
    @Native("java", "x10.runtime.Runtime.makeValRail(#0, #1)")
    public static def make[U](r: ValRail[U]): Rail[U]{self.length==r.length} = makeVar[U](r.length, r);

    public abstract def get(i: nat): T;
    public abstract def set(i: nat, v: T): void;
}
