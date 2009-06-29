/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Array bounds test - 1D.
 *
 * randomly generate 1D arrays and indices
 *
 * This version also generates random dists for the
 * arrays
 *
 * see if the array index out of bounds exception occurs
 * in the right  conditions
 */

public class DistBounds1D extends x10Test {

    public def run(): boolean = {
        val COUNT: int = 200;
        val L: int = 10;
        val K: int = 3;
        for (var n: int = 0; n < COUNT; n++) {
            var i: int = ranInt(-L-K, L+K);
            var lb1: int = ranInt(-L, L);
            var ub1: int = ranInt(lb1-1, L); // include empty reg.
            var d: int = ranInt(0, dist2.N_DIST_TYPES-1);
            var withinBounds: boolean = arrayAccess(lb1, ub1, i, d);
            chk(iff(withinBounds, i >= lb1 && i <= ub1));
        }
        return true;
    }

    /**
     * create a[lb1..ub1] then access a[i], return true iff
     * no array bounds exception occurred
     */
    private static def arrayAccess(var lb1: int, var ub1: int,  val i: int, var distType: int): boolean {

        //pr(lb1+" "+ub1+" "+i+" "+distType);

        val a = Array.make[int](dist2.getDist(distType, lb1..ub1));

        var withinBounds: boolean = true;
        try {
            chk(a.dist(i).id< Place.MAX_PLACES &&
                    a.dist(i).id >= 0);
            finish async(a.dist(i)) {
                a(i) = (0xabcdef07L as Int);
                chk(a(i) == (0xabcdef07L as Int));
            }
        } catch (var e: ArrayIndexOutOfBoundsException) {
            withinBounds = false;
        }

        //pr(lb1+" "+ub1+" "+i+" "+distType+" "+withinBounds);

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
        new DistBounds1D().execute();
    }

    /**
     * utility for creating a dist from a
     * a dist type int value
     */
    static class dist2 {

        // Java has poor support for enum
        const BLOCK: int = 0;
        const CYCLIC: int = 1;
        const CONSTANT: int = 2;
        //const RANDOM: int = 3;
        //const ARBITRARY: int = 4;
        const N_DIST_TYPES: int = 3; // 5;

        /**
         * Return a dist with region r, of type disttype
         */
        public static def getDist(val distType: int, val r: Region): Dist{region==r} = {
            switch(distType) {
                case BLOCK: return Dist.makeBlock(r, 0);
                case CYCLIC: return Dist.makeCyclic(r, 0);
                case CONSTANT: return r->here;
                //case RANDOM: return Dist.makeRandom(r);
                //case ARBITRARY: return Dist.makeArbitrary(r);
                default:throw new Error("TODO");
            }
        }
    }
}
