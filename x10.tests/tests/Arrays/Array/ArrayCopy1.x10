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
import x10.io.*;

/**
 * Test for arrays, regions and dists.
 * Based on original arraycopy1 by vj.
 *
 * @author kemal 1/2005
 */

public class ArrayCopy1 extends x10Test {

    /**
     * Does not throw an error iff A[i] == B[i] for all points i.
     * A and B can have differing dists
     * whose regions are equal.
     */

    public def arrayEqual(A: DistArray[int], B: DistArray[int]) {

        val D = A.dist;
        val E = B.dist;

        // Spawn an activity for each index to
        // fetch the B[i] value
        // Then compare it to the A[i] value
        finish
            ateach (p in D) {
                val pa = p as Point(A.dist.region.rank);
                val pb = p as Point(B.dist.region.rank);
                val fp:Int = at (E(pb)) B(pb);
                if (A(p) != fp)
                    throw new Exception("****Error: A(" + p + ")= " + A(p) + ", B(" + p + ")=" + B(pb) + " fp= " + fp);
                chk(A(p)==fp); 
                chk(A(p)==(at (E(pb)) B(pb)));
            }
    }

    /**
     * Set A[i] = B[i] for all points i.
     * A and B can have different dists whose
     * regions are equal.
     * Throws an error iff some assertion failed.
     */
    public def arrayCopy(val A: DistArray[int], val B: DistArray[int]): void = {

        val D = A.dist;
        val E = B.dist;

        // Spawn an activity for each index to
        // fetch and copy the value
        finish
            ateach (p in D) {
                val pa = p as Point(A.dist.region.rank);
                val pb = p as Point(B.dist.region.rank);
                chk(D(p) == here);
                async at(E(pb)) chk(E(pb) == here);
                A(p) = at(E(pb)) B(pb);
            }
    }

    static N = 3;

    /**
     * For all combinations of dists of arrays B and A,
     * do an array copy from B to A, and verify.
     */
    public def run(): boolean = {

        try {

            val R  = Region.make(0..(N-1), 0..(N-1), 0..(N-1), 0..(N-1));
            val TestDists = Region.make(0..(dist2.N_DIST_TYPES-1), 0..(dist2.N_DIST_TYPES-1));

            for (distP[dX,dY]: Point in TestDists) {
                val D = dist2.getDist(dX, R);
                val E = dist2.getDist(dY, R);
                chk(D.region.equals(E.region) && D.region.equals(R));
                val A = DistArray.make[int](D, (Point)=>0n);
                val B = DistArray.make[int](E,
                    (p[i,j,k,l]: Point): int => { 
                        val x = ((i*N+j)*N+k)*N+l; 
                        return (x*x+1) as Int; 
                    }
                );
                arrayCopy(A, B);
                arrayEqual(A, B);
            }

            return true;

        } catch (e:Exception) {
            //e.printStackTrace();
            x10.io.Console.OUT.println(e.toString());
            return false;
        }
    }

    public static def main(Rail[String]) {
        new ArrayCopy1().execute();
    }

    /**
     * utility for creating a dist from a
     * a dist type int value and a region
     */
    static class dist2 {

        static BLOCK: int = 0n;
        static BLOCKBLOCK: int = 1n;
        static CONSTANT: int = 2n;
        static N_DIST_TYPES: int = 3n; 

        /**
         * Return a dist with region r, of type disttype
         */
        public static def getDist(distType: Long, r: Region): Dist(r) {
            switch(distType as Int) {
                case BLOCK: return Dist.makeBlock(r, 0);
                case BLOCKBLOCK: return Dist.makeBlockBlock(r, 0,1);
                case CONSTANT: return r->here;
                default: throw new Exception();
            }
        }
    }
}
