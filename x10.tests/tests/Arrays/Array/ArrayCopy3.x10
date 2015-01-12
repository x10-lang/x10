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
 * Test for arrays, regions and dists.
 * Based on original arraycopy3 by vj.
 *
 * @author kemal 1/2005
 */

public class ArrayCopy3 extends x10Test {

    /**
     * Returns true iff point x is not in the domain of
     * dist D
     */
    static def outOfRange(val D: Dist, val x: Point{x.rank==D.rank}): boolean = {
        var gotException: boolean = false;
        try {
            async at(D(x)) {}; // dummy op just to use D[x]
        } catch (var e: Exception) {
            gotException = true;
        }
        return gotException;
    }

    /**
     * Does not throw an error iff A[i] == B[i] for all points i.
     */
    public def arrayEqual(A: DistArray[long], B: DistArray[long](A.rank)) {
        val D = A.dist;
        val E:Dist(A.rank) = B.dist;
        // Spawn an activity for each index to
        // fetch the B[i] value
        // Then compare it to the A[i] value
        finish
            ateach (val p: Point(B.rank) in D) {
            val f = at(E(p))B(p);
            chk(A(p as Point(A.rank)) == f);
            }
    }

    /**
     * Set A[i] = B[i] for all points i.
     * A and B can have different dists whose
     * regions are equal.
     * Throws an error iff some assertion failed.
     */
    public def arrayCopy(val A: DistArray[long], val B: DistArray[long](A.rank)) {

        val D = A.dist;
        val E = B.dist;

        // Allows message aggregation
        val D_1 = Dist.makeUnique(D.places());

        // number of times elems of A are accessed
        val accessed_a = DistArray.make[int](D, (Point)=>0n);

        // number of times elems of B are accessed
        val accessed_b  = DistArray.make[int](E, (Point)=>0n);

        finish
            ateach (val x in D_1) {
                val px: Place = D_1(x);
                chk(here == px);
                val LocalD: Region(A.rank) = (D | px).region;
                for (val py: Place in (E | LocalD).places()) {
                    val RemoteE: Region(A.rank) = (E | py).region;
                    val Common: Region(A.rank) = LocalD && RemoteE;
                    val D_common: Dist(A.rank) = D | Common;
                    // the future's can be aggregated
                    for (val i: Point(A.rank) in D_common) {
                        async at(py) atomic accessed_b(i) += 1n;
                        val temp  = at(py)B(i);
                        // the following may need to be bracketed in
                        // atomic, unless the disambiguator
                        // knows about dists
                        A(i) = temp;
                        atomic accessed_a(i) += 1n;
                    }
                    // check if dist ops are working
	            /* TODO: Restore this part of test test when Region/Dist - is supported again
                    val D_notCommon: Dist{rank==A.rank} = D - D_common;
                    chk((D_common || D_notCommon).equals(D));
                    val E_common: Dist{rank==A.rank} = E | Common;
                    val E_notCommon: Dist{rank==A.rank} = E - E_common;

                    chk((E_common || E_notCommon).equals(E));
                    for (val k: Point in D_common) {
                        chk(D_common(k) == px);
                        chk(outOfRange(D_notCommon, k));
                        chk(E_common(k) == py);
                        chk(outOfRange(E_notCommon, k));
                        chk(D(k) == px && E(k) == py);
                    }

                    for (val k: Point in D_notCommon) {
                        chk(outOfRange(D_common, k));
                        chk(!outOfRange(D_notCommon, k));
                        chk(outOfRange(E_common, k));
                        chk(!outOfRange(E_notCommon, k));
                        chk(!(D(k) == px && E(k) == py));
                    }
	            */
                }
            }

        // ensure each A[i] was accessed exactly once
        finish ateach (i: Point(A.rank) in D) chk(accessed_a(i) == 1n);

        // ensure each B[i] was accessed exactly once
        finish ateach (i: Point(A.rank) in E) chk(accessed_b(i) == 1n);
    }

    public static N: int = 3n;

    /**
     * For all combinations of dists of arrays B and A,
     * do an array copy from B to A, and verify.
     */
    public def run(): boolean = {

        val R: Region{rank==4} = Region.make(0..(N-1), 0..(N-1), 0..(N-1), 0..(N-1));
        val TestDists: Region(2) = Region.make(0..(dist2.N_DIST_TYPES-1), 0..(dist2.N_DIST_TYPES-1));

        for (distP[dX,dY]: Point(2) in TestDists) {
            val D: Dist{rank==4} = dist2.getDist(dX, R);
            val E: Dist{rank==4} = dist2.getDist(dY, R);
            chk(D.region.equals(E.region) && D.region.equals(R));
            val A: DistArray[long]{rank==4} = DistArray.make[long](D, (Point)=>0L);
            val B: DistArray[long]{rank==A.rank} = DistArray.make[long](E, 
                (p[i,j,k,l]: Point) => { val x=((i*N+j)*N+k)*N+l; x*x+1});
            arrayCopy(A, B);
            arrayEqual(A, B);
        }
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ArrayCopy3().execute();
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
        public static def getDist(distType: Long, r: Region): Dist(r) = {
            switch(distType as Int) {
                case BLOCK: return Dist.makeBlock(r);
                case BLOCKBLOCK: return Dist.makeBlockBlock(r, 0,1);
                case CONSTANT: return r->here;
                default: throw new Exception();
            }
        }
    }
}
