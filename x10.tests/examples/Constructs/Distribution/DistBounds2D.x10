/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;
import x10.array.*;

/**
 * DistArray bounds test - 2D.
 *
 * randomly generate 2D arrays and indices,
 *
 * This version also generates a random dist
 * for the arrays
 *
 * see if the array index out of bounds exception occurs
 * in the right  conditions
 */

public class DistBounds2D extends x10Test {

    public def run(): boolean = {
        val COUNT: int = 200;
        val L: int = 10;
        val K: int = 3;
        for (var n: int = 0; n < COUNT; n++) {
            var i: int = ranInt(-L-K, L+K);
            var j: int = ranInt(-L-K, L+K);
            var lb1: int = ranInt(-L, L);
            var lb2: int = ranInt(-L, L);
            var ub1: int = ranInt(lb1, L);
            var ub2: int = ranInt(lb2, L);
            var d: int = ranInt(0, dist2.N_DIST_TYPES-1);
            var withinBounds: boolean = arrayAccess(lb1, ub1, lb2, ub2, i, j, d);
            chk(iff(withinBounds,
                    i >= lb1 && i <= ub1 &&
                    j >= lb2 && j <= ub2));
        }
        return true;
    }

    /**
     * create a[lb1..ub1,lb2..ub2] then access a[i,j], return true iff
     * no array bounds exception occurred
     */
    private static def arrayAccess(
        var lb1: int, var ub1: int, 
        var lb2: int, var ub2: int, val i: int, val j: int, 
        var distType: int
    ): boolean = {

        //pr(lb1+" "+ub1+" "+lb2+" "+ub2+" "+i+" "+j+" "+distType);

        // XTENLANG-192
        val a = DistArray.make[int](dist2.getDist(distType, Region.make([lb1..ub1, lb2..ub2])));

        var withinBounds: boolean = true;
        try {
            chk(a.dist(i, j).id<Place.MAX_PLACES &&
                    a.dist(i, j).id >= 0);
            finish async at(a.dist(i, j)) {
                a(i, j) = ( 0xabcdef07L as Int);
                chk(a(i, j) == ( 0xabcdef07L as Int));
            }
        } catch (var e: ArrayIndexOutOfBoundsException) {
            withinBounds = false;
        }

        //pr(lb1+" "+ub1+" "+lb2+" "+ub2+" "+i+" "+j+" "+distType+" "+withinBounds);

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
        new DistBounds2D().execute();
    }

    /**
     * utility for creating a dist from a
     * a dist type int value
     */
    static class dist2 {

        // X10 has poor support for enum
        static BLOCK: int = 0;
        static CONSTANT: int = 1;
        //public static val CYCLIC: int = 2;
        //public static val RANDOM: int = 3;
        //public static val ARBITRARY: int = 4;
        static N_DIST_TYPES: int = 2; //5;

        /**
         * Return a dist with region r, of type disttype
         */
        public static def getDist(val distType: int, val r: Region): Dist{region==r} = {
            switch(distType) {
                case BLOCK: return Dist.makeBlock(r, 0);
                //case CYCLIC: return Dist.makeCyclic(r, 0);
                case CONSTANT: return r->here;
                //case RANDOM: return Dist.makeRandom(r);
                //case ARBITRARY: return Dist.makeArbitrary(r);
                default:throw new Exception("TODO");
            }
        }
    }
}
