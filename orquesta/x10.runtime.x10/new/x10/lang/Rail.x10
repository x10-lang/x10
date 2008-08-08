package x10.lang;

public abstract value class Rail[T](length: nat)
    implements Indexable[nat,T], Settable[nat,T]
{
    incomplete public static def make(length: nat, init: Indexable[nat,T], value: boolean): Rail[T];

    public abstract def get(i: nat): T;
    public abstract def set(i: nat, v: T): void;
}
