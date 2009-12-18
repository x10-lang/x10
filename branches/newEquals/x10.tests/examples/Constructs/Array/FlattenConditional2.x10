/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;


/**
 * 
 */

public class FlattenConditional2 extends x10Test {

    var a: Array[int](2);

    public def this(): FlattenConditional2 = {
        a = Array.make[int](([1..10, 1..10] as Region)->here, ((i,j): Point) => { return i+j;});
    }

    var extra: int = 4;

    def m(var i: int): int = {
        if (i==6) throw new Error();
        return i;
    }

    public def run(): boolean = {
        var x: int = a(1, 1)==2? m(a(2, 2)) : m(a(3, 3));
        return x==4;
    }

    public static def main(var args: Rail[String]): void = {
        new FlattenConditional2().execute();
    }
}
