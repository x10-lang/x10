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
 * User defined type array bounds test - 3D.
 *
 * Randomly generate 3D arrays and indices.
 *
 * See if the array index out of bounds exception occurs
 * in the right conditions.
 *
 * @author kemal 11/2005
 */

public class UserArrayBounds3D extends x10Test {

    public def run(): boolean = {

        val COUNT: int = 100;
        val L: int = 3;
        val K: int = 1;

        for(var n: int = 0; n < COUNT; n++) {
            var i: int = ranInt(-L-K, L+K);
            var j: int = ranInt(-L-K, L+K);
            var k: int = ranInt(-L-K, L+K);
            var lb1: int = ranInt(-L, L);
            var lb2: int = ranInt(-L, L);
            var lb3: int = ranInt(-L, L);
            var ub1: int = ranInt(lb1, L);
            var ub2: int = ranInt(lb2, L);
            var ub3: int = ranInt(lb3, L);
            var withinBounds: boolean = arrayAccess(lb1, ub1, lb2, ub2, lb3, ub3, i, j, k);
            chk(iff(withinBounds, i>=lb1 && i<=ub1 && j>=lb2 && j<=ub2 && k>=lb3 && k<=ub3));
        }
        return true;
    }

    /**
     * create a[lb1..ub1,lb2..ub2,lb3..ub3] then access a[i,j,k],
     * return true iff
     * no array bounds exception occurred
     */
    private static def arrayAccess(var lb1: int, var ub1: int, var lb2: int, var ub2: int, var lb3: int, var ub3: int, var i: int, var j: int, var k: int): boolean = {

        //pr(lb1+" "+ub1+" "+lb2+" "+ub2+" "+lb3+" "+ub3+" "+i+" "+j+" "+k);

        var a: Array[boxedInt](3) = Array.make[boxedInt](
            Dist.makeConstant([lb1..ub1, lb2..ub2, lb3..ub3], here), 
            ((i,j,k): Point)=> new boxedInt(0));

        var withinBounds: boolean = true;
        try {
            a(i, j, k) = new boxedInt(0xabcdef07L as Int);
            chk(a(i, j, k).equals(new boxedInt( 0xabcdef07L as Int)));
        } catch (var e: ArrayIndexOutOfBoundsException) {
            withinBounds = false;
        }
        //pr(lb1+" "+ub1+" "+lb2+" "+ub2+" "+lb3+" "+ub3+" "+i+" "+j+" "+k+" "+withinBounds);

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
    private static def iff(var x: boolean, var y: boolean)= x==y;

    public static def main(var args: Rail[String]): void = {
        new UserArrayBounds3D().execute();
    }

    static class boxedInt {
        var val: int;
        public def this(var x: int): boxedInt = { val = x; }
        public def equals(var other: boxedInt): boolean = {
            return other.at(here) && this.val == (other as boxedInt!).val;
        }
    }
}
