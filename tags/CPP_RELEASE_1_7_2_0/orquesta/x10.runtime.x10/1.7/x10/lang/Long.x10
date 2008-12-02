package x10.lang;

public value Long implements Arithmetic[Long], Comparable[Long], BitString[Long] {
    public extern def add(x: Long): Long;
    public extern def sub(x: Long): Long;
    public extern def mul(x: Long): Long;
    public extern def div(x: Long): Long;
    public extern def mod(x: Long): Long;
    
    public extern def cosub(x: Long): Long;
    public extern def codiv(x: Long): Long;
    public extern def neginv(): Long;
    public extern def mulinv(): Long;

    public extern def eq(y: Long): Boolean;
    public extern def lt(y: Long): Boolean;
    public extern def gt(y: Long): Boolean;
    public extern def le(y: Long): Boolean;
    public extern def ge(y: Long): Boolean;
    public extern def ne(y: Long): Boolean;

    public extern static def zero(): Long;
    public extern static def unit(): Long;
}
