/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import x10.util.Random;
import harness.x10Test;

/**
 * User defined type array bounds test - 2D.
 *
 * Randomly generate 2D arrays and indices.
 *
 * See if the array index out of bounds exception occurs
 * in the right conditions.
 *
 * @author kemal 11/2005
 */

public class UserArrayBounds2D extends x10Test {

    public def run(): boolean = {

        val COUNT: int = 100;
        val L: int = 10;
        val K: int = 3;

        for(var n: int = 0; n < COUNT; n++) {
            var i: int = ranInt(-L-K, L+K);
            var j: int = ranInt(-L-K, L+K);
            var lb1: int = ranInt(-L, L);
            var lb2: int = ranInt(-L, L);
            var ub1: int = ranInt(lb1, L);
            var ub2: int = ranInt(lb2, L);
            var withinBounds: boolean = arrayAccess(lb1, ub1, lb2, ub2, i, j);
            chk(iff(withinBounds, i>=lb1 && i<=ub1 && j>=lb2 && j<=ub2));
        }
        return true;
    }

    /**
     * create a[lb1..ub1,lb2..ub2] then access a[i,j], return true iff
     * no array bounds exception occurred
     */
    private static def arrayAccess(var lb1: int, var ub1: int, var lb2: int, var ub2: int, var i: int, var j: int): boolean = {

        //pr(lb1+" "+ub1+" "+lb2+" "+ub2+" "+i+" "+j);

        var a: Array[boxedInt](2) = Array.make[boxedInt](
           Dist.makeConstant([lb1..ub1, lb2..ub2], here), 
           ((i,j): Point)=> new boxedInt(0));

        var withinBounds: boolean = true;
        try {
            a(i, j) = new boxedInt(0xabcdef07L as Int);
            //pr("assigned");
            chk(a(i, j).equals(new boxedInt(0xabcdef07L as Int)));
        } catch (var e: ArrayIndexOutOfBoundsException) {
            withinBounds = false;
        }
        //pr(lb1+" "+ub1+" "+lb2+" "+ub2+" "+i+" "+j+" "+withinBounds);

        return withinBounds;
    }

    // utility methods after this point

    /**
     * print a string
     */
    private static def pr(var s: String): void = {
        x10.io.Console.OUT.println(s);
    }

    /**
     * true iff (x if and only if y)
     */
    private static def iff(var x: boolean, var y: boolean): boolean = {
        return x == y;
    }

    public static def main(var args: Rail[String]): void = {
        new UserArrayBounds2D().execute();
    }

    static class boxedInt {
        var val: int;
        public def this(var x: int): boxedInt = { val = x; }
        public def equals(var other: boxedInt): boolean = {
            return other.at(here) && this.val == (other as boxedInt!).val;
        }
    }
}
