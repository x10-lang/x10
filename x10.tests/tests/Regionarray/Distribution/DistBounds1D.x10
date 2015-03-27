/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;
import x10.regionarray.*;

/**
 * DistArray bounds test - 1D.
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

    public def run(): boolean {
        val COUNT: int = 200n;
        val L: int = 10n;
        val K: int = 3n;
        for (var n: int = 0n; n < COUNT; n++) {
            var i: int = ranInt(-L-K, L+K);
            var lb1: int = ranInt(-L, L);
            var ub1: int = ranInt(lb1-1n, L); // include empty reg.
            var d: int = ranInt(0n, dist2.N_DIST_TYPES-1n);
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

        val a = DistArray.make[int](dist2.getDist(distType, Region.make(lb1,ub1)));

        var withinBounds: boolean = true;
        try {
            chk(a.dist(i).id< Place.numPlaces() &&
                    a.dist(i).id >= 0);
            finish async at(a.dist(i)) {
                a(i) = (0xabcdef07L as Int);
                chk(a(i) == (0xabcdef07L as Int));
            }
        } catch (e: ArrayIndexOutOfBoundsException) {
            withinBounds = false;
        }

        //pr(lb1+" "+ub1+" "+i+" "+distType+" "+withinBounds);

        return withinBounds;
    }

    // utility methods after this point

    /**
     * print a string
     */
    private static def pr(var s: String): void {
        x10.io.Console.OUT.println(s);
    }

    /**
     * true iff (x if and only if y)
     */
    private static def iff(var x: boolean, var y: boolean): boolean {
        return x == y;
    }

    public static def main(args: Rail[String]) {
        new DistBounds1D().execute();
    }

    /**
     * utility for creating a dist from a
     * a dist type int value
     */
    static class dist2 {

        // X10 has poor support for enum
        static BLOCK: int = 0n;
        static CONSTANT: int = 1n;
        static N_DIST_TYPES: int = 2n; // 5;

        /**
         * Return a dist with region r, of type disttype
         */
        public static def getDist(val distType: int, val r: Region): Dist{region==r} {
            switch(distType) {
                case BLOCK: return Dist.makeBlock(r, 0);
                case CONSTANT: return r->here;
                default:throw new Exception("TODO");
            }
        }
    }
}
