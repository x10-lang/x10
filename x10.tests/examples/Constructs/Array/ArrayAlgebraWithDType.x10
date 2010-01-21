/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;
/* STATUS: 1/21/2010 -- this file doesn't compile, because 
   it uses some unimplemented Array operations.
*/

/**
 * Constant promotions to arrays: (D n)
 * disjoint union and overlay of arrays
 * array lift, scan and reduce.
 *
 * @author kemal 4/2005
 */

public class ArrayAlgebraWithDType extends x10Test {

    public const N: int = 24;

    def makeArray(val D: Dist, val k: int): Array[int](D) = {
        return Array.make[int](D, (Point) => k);
    }

    public def run(): boolean = {

        val R=0..N-1;
        val D:Dist(1)= Dist.makeBlockCyclic(R, 0, 2);
        val D01 = D | 0..N/2-1;
        val D23 = D | (N/2)..N-1;
        val D0  = D | 0..N/4-1;
        val D1  = D | (N/4)..N/2-1;
        val D2  = D | (N/2)..3*N/4-1;
        val D3  = D | (3*N/4)..N-1;

        val ia1 = 
          makeArray(D, -99)
          .overlay((makeArray(D01, -1) 
                    || makeArray(D23, -2))
          .overlay(makeArray(D3, 3))
          .overlay(makeArray(D0, 9)));

        arrEq(ia1 | D0, makeArray(D0, 9));
        arrEq(ia1 | D1, makeArray(D1, -1));
        arrEq(ia1 | D2, makeArray(D2, -2));
        arrEq(ia1 | D3, makeArray(D3, 3));

        chk(ia1.sum() == 9*N/4);

        arrEq(ia1.scan(Int.+, 0),
              Array.make[Int](D, (var Point (i): Point): int => (ia1 | 0..i).reduce(Int.+)));
        
        arrEq(makeArray(D01, 1).lift(Int.+, makeArray(D01, -4)), makeArray(D01, -3));

        // are we still supporting +,-,... on arrays?
        arrEq(makeArray(D01, 1) + makeArray(D01, -4), makeArray(D01, -3) * makeArray(D01, 1));
        return true;
    }

    /**
     * Throw an error iff x and y are not arrays with same
     * content and dist
     */
    static def arrEq(val x: Array[int], val y: Array[int]): void = {
        chk(x.dist.equals(y.dist));
        finish ateach (val p: Point in x) chk(x(p) == y(p));
    }

    public static def main(var args: Rail[String]): void = {
        new ArrayAlgebraWithDType().execute();
    }
}
