package x10.lang;

public value Int implements Arithmetic[Int], Comparable[Int], BitString[Int] {
    public extern def add(x: Int): Int;
    public extern def sub(x: Int): Int;
    public extern def mul(x: Int): Int;
    public extern def div(x: Int): Int;
    public extern def mod(x: Int): Int;
    
    public extern def cosub(x: Int): Int;
    public extern def codiv(x: Int): Int;
    public extern def neginv(): Int;
    public extern def mulinv(): Int;

    public extern static def zero(): Int;
    public extern static def unit(): Int;

    public extern def eq(y: Int): Boolean;
    public extern def lt(y: Int): Boolean;
    public extern def gt(y: Int): Boolean;
    public extern def le(y: Int): Boolean;
    public extern def ge(y: Int): Boolean;
    public extern def ne(y: Int): Boolean;
}
