package x10.lang;

public abstract value class Rail[T](length: nat)
    implements Indexable[nat,T], Settable[nat,T]
{
    public def this(n: nat): Rail[T]{length==n} = { property(n); } // needed temporarily since native constructors do not parse
    
    incomplete public static def make[T](length: nat, init: (nat) => T, value: boolean): Rail[T]; // should be native

    public abstract def get(i: nat): T;
    public abstract def set(i: nat, v: T): void;
}
