/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Tests conversion of arrays to regions/dists.
 *
 * @author kemal 3/2005
 */

public class ArrayToDist extends x10Test {

    const N = 4;

    public def run(): boolean = {

        val R = [0..N-1, 0..N-1];
        val D  = Dist.makeBlock(R, 0);
        val A1 = Array.make[int](D, ((i,j): Point ) => f(i, j));
        val A2 = Array.make[foo](D, ((i,j): Point) => new foo(f(i, j)));

        for (val p(i,j): Point(2) in A1.region) 
            chk(f(i, j) == (future(A1.dist(i, j)) { A1(i, j) }).force(), "1");

        finish foreach (val p(i,j): Point(2) in A1.region) 
            chk(f(i, j) == (future(A1.dist(i, j)) { A1(i, j) }).force(), "2");

        finish ateach (val p(i,j): Point(2) in A1.dist) 
            chk(f(i, j) == A1(i, j), "3");

        for (val p(i,j): Point(2) in A2.region) 
            chk(f(i, j) == (future(A2.dist(i, j)) { A2(i, j).val }).force(), "4");

        finish foreach (val p(i,j): Point(2) in A2.region) 
           chk(f(i, j) == (future(A2.dist(i, j)) { A2(i, j).val }).force(), "5");

        finish ateach (val p(i,j): Point(2)  in A2.dist) 
            chk(f(i, j) == A2(i, j).val, "6");

        return true;
    }

    static def f(var i: int, var j: int): int = {
        return N * i + j;
    }

    public static def main(var args: Rail[String]): void = {
        new ArrayToDist().execute();
    }

    static class foo {
        public var val: int;
        public def this(var x: int): foo = { this.val = x; }
    }
}
