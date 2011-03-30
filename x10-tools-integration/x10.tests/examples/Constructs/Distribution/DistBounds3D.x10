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
        val COUNT: int = 200;
        val L: int = 3;
        val K: int = 1;
        for (var n: int = 0; n < COUNT; n++) {
            var i: int = ranInt(-L-K, L+K);
            var j: int = ranInt(-L-K, L+K);
            var k: int = ranInt(-L-K, L+K);
            var lb1: int = ranInt(-L, L);
            var lb2: int = ranInt(-L, L);
            var lb3: int = ranInt(-L, L);
            var ub1: int = ranInt(lb1, L);
            var ub2: int = ranInt(lb2, L);
            var ub3: int = ranInt(lb3, L);
            var d: int = ranInt(0, dist2.N_DIST_TYPES-1);
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
        val a = DistArray.make[int](dist2.getDist(distType, (lb1..ub1)*(lb2..ub2)*(lb3..ub3)));

        var withinBounds: boolean = true;
        try {
            chk(a.dist(i, j, k).id< Place.MAX_PLACES && a.dist(i, j, k).id >= 0);
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

    public static def main(var args: Array[String](1)): void = {
        new DistBounds3D().execute();
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
                default:throw new Error("TODO");
            }
        }
    }
}
