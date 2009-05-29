package x10.lang;

public value Double implements Arithmetic[Double], Comparable[Double] {
    public extern def add(x: Double): Double;
    public extern def sub(x: Double): Double;
    public extern def mul(x: Double): Double;
    public extern def div(x: Double): Double;
    public extern def mod(x: Double): Double;
    
    public extern def cosub(x: Double): Double;
    public extern def codiv(x: Double): Double;
    public extern def neginv(): Double;
    public extern def mulinv(): Double;

    public extern def eq(y: Double): Boolean;
    public extern def lt(y: Double): Boolean;
    public extern def gt(y: Double): Boolean;
    public extern def le(y: Double): Boolean;
    public extern def ge(y: Double): Boolean;
    public extern def ne(y: Double): Boolean;

    public extern static def zero(): Double;
    public extern static def unit(): Double;
}
