/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;
import x10.regionarray.*;

/**
 * Array bounds test - 3D.
 *
 * randomly generate 3D arrays and indices,
 *
 * see if the array index out of bounds exception occurs
 * in the right  conditions
 */

public class DistBounds3D extends x10Test {

    public def run(): boolean = {
        val COUNT: int = 200n;
        val L: int = 3n;
        val K: int = 1n;
        for (var n: int = 0n; n < COUNT; n++) {
            var i: int = ranInt(-L-K, L+K);
            var j: int = ranInt(-L-K, L+K);
            var k: int = ranInt(-L-K, L+K);
            var lb1: int = ranInt(-L, L);
            var lb2: int = ranInt(-L, L);
            var lb3: int = ranInt(-L, L);
            var ub1: int = ranInt(lb1, L);
            var ub2: int = ranInt(lb2, L);
            var ub3: int = ranInt(lb3, L);
            var d: int = ranInt(0n, dist2.N_DIST_TYPES-1n);
            var withinBounds: boolean = arrayAccess(lb1, ub1, lb2, ub2, lb3, ub3, i, j, k, d);
            chk(iff(withinBounds,
                    i >= lb1 && i <= ub1 &&
                    j >= lb2 && j <= ub2 &&
                    k >= lb3 && k <= ub3));
        }
        return true;
    }

    /**
     * create a[lb1..ub1,lb2..ub2,lb3..ub3] then access a[i,j,k],
     * return true iff
     * no array bounds exception occurred
     */
    private static def arrayAccess(var lb1: int, var ub1: int, var lb2: int, var ub2: int, var lb3: int, var ub3: int, val i: int, val j: int, val k: int, var distType: int): boolean = {

        //pr(lb1+" "+ub1+" "+lb2+" "+ub2+" "+lb3+" "+ub3+" "+i+" "+j+" "+k+" "+ distType);

        // XTENLANG-192
        val a = DistArray.make[int](dist2.getDist(distType, Region.make(lb1..ub1, lb2..ub2, lb3..ub3)));

        var withinBounds: boolean = true;
        try {
            chk(a.dist(i, j, k).id< Place.numPlaces() && a.dist(i, j, k).id >= 0);
            finish async at(a.dist(i, j, k)) {
                a(i, j, k) = ( 0xabcdef07L as Int);
                chk(a(i, j, k) == (0xabcdef07L as Int));
            }
        } catch (var e: ArrayIndexOutOfBoundsException) {
            withinBounds = false;
        }

        //pr(lb1+" "+ub1+" "+lb2+" "+ub2+" "+lb3+" "+ub3+" "+i+" "+j+" "+k+" "+distType+" "+ withinBounds);

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
        new DistBounds3D().execute();
    }

    /**
     * utility for creating a dist from a
     * a dist type int value
     */
    static class dist2 {

        // X10 has poor support for enum
        static BLOCK: int = 0n;
        static BLOCKBLOCK: int = 1n;
        static CONSTANT: int = 2n;
        static N_DIST_TYPES: int = 3n; 

        /**
         * Return a dist with region r, of type disttype
         */
        public static def getDist(val distType: int, val r: Region): Dist{region==r} = {
            switch(distType) {
                case BLOCK: return Dist.makeBlock(r, 0);
                case BLOCKBLOCK: return Dist.makeBlockBlock(r, 0, 1);
                case CONSTANT: return r->here;
                default:throw new Exception("TODO");
            }
        }
    }
}
