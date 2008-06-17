package x10.lang;

public value float implements Arithmetic[float] {
    public extern add(x:float):float;
    public extern def sub(x:float):float;
    public extern def mul(x:float):float;
    public extern def div(x:float):float;
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
