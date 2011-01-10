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
 * Test for arrays, regions and dists.
 * Based on original arraycopy2 by vj.
 *
 * @author kemal 1/2005
 */
public class ArrayCopy2 extends x10Test {

    /**
     * Returns true iff point x is not in the domain of
     * dist D
     */
    static def outOfRange(D: Dist, x: Point(D.rank)): boolean {
        try {
            async at(D(x)) {}; // dummy op just to use D(x)
        } catch (Throwable) {
            return true;
        }
        return false;
    }

    /**
     * Does not throw an error iff A(i) == B(i) for all points i.
     */
    public def arrayEqual[T](A: DistArray[T], B: DistArray[T](A.rank)) {
        // Spawn an activity for each index to
        // fetch the B(i) value
        // Then compare it to the A(i) value
        finish
            ateach (p:Point(B.rank) in A.dist) {
            val v = at (B.dist(p)) B(p);
            chk(A(p) == v);
	}
    }

    /**
     * Set A(i) = B(i) for all points i.
     * A and B can have different dists whose
     * regions are equal.
     * Throws an error iff some assertion failed.
     */
    public def arrayCopy(val A: DistArray[int], val B: DistArray[int](A.rank)) {

        val D = A.dist;
        val E = B.dist;

        // Spawn one activity per place
        val D_1 = Dist.makeUnique(D.places());

        // number of times elems of A are accessed
        val accessed_a = DistArray.make[Int](D);

        // number of times elems of B are accessed
        val accessed_b = DistArray.make[Int](E);

        finish
            ateach (x in D_1) {
                val px = D_1(x);
                chk(px == here);
                val D_local  = (D | px);
                for (i:Point(E.rank) in D_local) {
                    // assignment to A(i) may need to be atomic
                    // unless disambiguator has high level
                    // knowledge about dists
                    A(i) = at(E(i)){ 
			atomic accessed_b(i)++;
			B(i)
		    };
                    atomic accessed_a(i)++;
                }
                // check if dist ops are working
		/*
		 TODO: restore this test case once we restore Dist/Region subtraction.
                val D_nonlocal = D - D_local;
                chk((D_local || D_nonlocal).equals(D));
                for (k in D_local) {
                    chk(outOfRange(D_nonlocal, k));
                    chk(D_local(k) == px);
                }
                for (k in D_nonlocal) {
                    chk(outOfRange(D_local, k));
                    chk(D_nonlocal(k) != px);
                }
	       */
            }

        // ensure each A[i] was accessed exactly once
        finish ateach (i:Point(D.rank) in D) chk(accessed_a(i) == 1);

        // ensure each B[i] was accessed exactly once
        finish ateach (i:Point(E.rank) in E) chk(accessed_b(i) == 1);
    }

    public static N: int = 3;

    /**
     * For all combinations of dists of arrays B and A,
     * do an array copy from B to A, and verify.
     */
    public def run(): boolean = {

        val R:Region(4) = 0..(N-1)*0..(N-1)*0..(N-1)*0..(N-1);
        val TestDists = 0..(dist2.N_DIST_TYPES-1)*0..(dist2.N_DIST_TYPES-1);

        for (distP[dX,dY]:Point(2) in TestDists) {
            val D= dist2.getDist(dX, R);
            val E = dist2.getDist(dY, R);
	    /*
	    x10.io.Console.OUT.println("dX,dY=" + dX + "," + dY);
	    x10.io.Console.OUT.println("E.region=" + E.region 
				       + " D.region=" + D.region
				       + " R=" + R);
	    x10.io.Console.OUT.println("D.region.equals(E.region)=" 
				       + D.region.equals(E.region));
	    x10.io.Console.OUT.println("D.region.equals(R)=" 
				       + D.region.equals(R));
	    */
            chk(D.region.equals(E.region) && D.region.equals(R));
            val A = DistArray.make[int](D);
            val B = DistArray.make[int](E, (p[i,j,k,l]: Point) => { val x = ((i*N+j)*N+k)*N+l; x*x+1 });
            arrayCopy(A, B);
            arrayEqual(A, B);  
        }
        return true;
    }

    public static def main(var args: Array[String](1)): void = {
        new ArrayCopy2().execute();
    }

    /**
     * utility for creating a dist from a
     * a dist type int value and a region
     */
    static class dist2 {

        static BLOCK: int = 0;
        static CONSTANT: int = 1;
        //static BLOCKCYCLIC: int = 1;
	static N_DIST_TYPES=2;
        /**
         * Return a dist with region r, of type disttype
         */
        public static def getDist(distType: Int, R: Region): Dist(R) = {
            switch(distType) {
                case BLOCK: return Dist.makeBlock(R,0) as Dist(R);
                case CONSTANT: return (R->here) as Dist(R);
                // case BLOCKCYCLIC: return Dist.makeBlockCyclic(R, 0,3) as Dist(R);
                default: throw new Error();
            }
        }
    }
}
