package x10.lang;

public value long implements Arithmetic[long] {
    public extern add(x:long):long;
    public extern def sub(x:long):long;
    public extern def mul(x:long):long;
    public extern def div(x:long):long;
    public extern def cosub(x:T):T;
    public extern def codiv(x:T):T;
    public extern def negiv():T;
    public extern def muliv():T;
    public extern def zero():T;
    public extern def unit():T;
    public extern def eq(y:T):boolean;
    public extern def lt(y:T):boolean;
    public extern def gt(y:T):boolean;
    public extern def le(y:T):boolean;
    public extern def ge(y:T):boolean;
    public extern def ne(y:T):boolean;
}
