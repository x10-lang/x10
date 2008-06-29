package x10.lang;

public value BitString[T]{T <: BitString[T]} {
    public extern def shl(x: Nat): T;
    public extern def shr(x: Nat): T;

    public extern def not(): T;
    public extern def and(x: T): T;
    public extern def or(x: T): T;
    public extern def xor(x: T): T;

    public extern static def zero(): T;
}
