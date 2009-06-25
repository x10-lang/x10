package x10.lang;

public value Float implements Arithmetic[Float], Comparable[Float] {
    public extern def add(x: Float): Float;
    public extern def sub(x: Float): Float;
    public extern def mul(x: Float): Float;
    public extern def div(x: Float): Float;
    public extern def mod(x: Float): Float;
    
    public extern def cosub(x: Float): Float;
    public extern def codiv(x: Float): Float;
    public extern def neginv(): Float;
    public extern def mulinv(): Float;

    public extern def eq(y: Float): Boolean;
    public extern def lt(y: Float): Boolean;
    public extern def gt(y: Float): Boolean;
    public extern def le(y: Float): Boolean;
    public extern def ge(y: Float): Boolean;
    public extern def ne(y: Float): Boolean;

    public extern static def zero(): Float;
    public extern static def unit(): Float;
}
