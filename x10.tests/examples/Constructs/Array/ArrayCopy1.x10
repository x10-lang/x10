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

            val R  = 0..(N-1)*0..(N-1)*0..(N-1)*0..(N-1);
            val TestDists = 0..(dist2.N_DIST_TYPES-1)*0..(dist2.N_DIST_TYPES-1);

            for (distP[dX,dY]: Point in TestDists) {
                val D = dist2.getDist(dX, R);
                val E = dist2.getDist(dY, R);
                chk(D.region.equals(E.region) && D.region.equals(R));
                val A = DistArray.make[int](D, (Point)=>0);
                val B = DistArray.make[int](E,
                    (p[i,j,k,l]: Point): int => { 
                        var x: int = ((i*N+j)*N+k)*N+l; 
                        return x*x+1; 
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

        static BLOCK: int = 0;
        //public static val CYCLIC: int = 1;
        //public static val BLOCKCYCLIC: int = 2;
        static CONSTANT: int = 1;
        //public static val RANDOM: int = 4;
        //public static val ARBITRARY: int = 5;
        static N_DIST_TYPES: int = 2; //6;

        /**
         * Return a dist with region r, of type disttype
         */
        public static def getDist(distType: Int, r: Region): Dist(r) {
            switch(distType) {
                case BLOCK: return Dist.makeBlock(r, 0);
                //case CYCLIC: return Dist.makeCyclic(r, 0);
                //case BLOCKCYCLIC: return Dist.makeBlockCyclic(r, 0, 3);
                case CONSTANT: return r->here;
                //case RANDOM: return Dist.makeRandom(r);
                //case ARBITRARY:return Dist.makeArbitrary(r);
                default: throw new Exception();
            }
        }
    }
}
