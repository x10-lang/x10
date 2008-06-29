package x10.lang;

public value Nat implements Arithmetic[Nat], Comparable[Nat], BitString[Nat] {
    public extern def add(x: Nat): Nat;
    public extern def sub(x: Nat): Nat;
    public extern def mul(x: Nat): Nat;
    public extern def div(x: Nat{self != 0}): Nat;
    public extern def mod(x: Nat{self != 0}): Nat;
    
    public extern def cosub(x: Nat): Nat;
    public extern def codiv(x: Nat): Nat;
    public extern def neginv(): Int;
    public extern def mulinv(){this != 0}: Nat;

    public extern def eq(y: Nat): Boolean;
    public extern def lt(y: Nat): Boolean;
    public extern def gt(y: Nat): Boolean;
    public extern def le(y: Nat): Boolean;
    public extern def ge(y: Nat): Boolean;
    public extern def ne(y: Nat): Boolean;

    public extern static def zero(): Nat;
    public extern static def unit(): Nat;
}
