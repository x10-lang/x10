package x10.lang;

public value BitString[T]{T <: BitString[T]} {
    public native def shl(x: Nat): T;
    public native def shr(x: Nat): T;

    public native def not(): T;
    public native def and(x: T): T;
    public native def or(x: T): T;
    public native def xor(x: T): T;

    // public native static def zero(): T;
}
