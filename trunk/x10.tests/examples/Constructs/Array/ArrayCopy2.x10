/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
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
    static def outOfRange(val D: Dist, val x: Point): boolean = {
        var gotException: boolean = false;
        try {
            async(D(x)) {}; // dummy op just to use D[x]
        } catch (var e: Throwable) {
            gotException = true;
        }
        return gotException;
    }

    /**
     * Does not throw an error iff A[i] == B[i] for all points i.
     */
    public def arrayEqual(val A: Array[int], val B: Array[int]): void = {
        val D: Dist = A.dist;
        val E: Dist = B.dist;
        // Spawn an activity for each index to
        // fetch the B[i] value
        // Then compare it to the A[i] value
        finish
            ateach (val p: Point in D) {
            val v = (future (E(p)) B(p))();
            chk(A(p) == v);
            }
    }

    /**
     * Set A[i] = B[i] for all points i.
     * A and B can have different dists whose
     * regions are equal.
     * Throws an error iff some assertion failed.
     */
    public def arrayCopy(val A: Array[int], val B: Array[int]): void = {

        val D: Dist = A.dist;
        val E: Dist = B.dist;

        // Spawn one activity per place
        val D_1: Dist = Dist..makeUnique(D.places());

        // number of times elems of A are accessed
        val accessed_a: Array[int] = Array.make[Int](D);

        // number of times elems of B are accessed
        val accessed_b: Array[int] = Array.make[Int](E);

        finish
            ateach (val x: Point in D_1) {
                val px: Place = D_1(x);
                chk(px == here);
                val D_local:Dist(D.rank)  = (D | px);
                for (val i: Point in D_local) {
                    // assignment to A[i] may need to be atomic
                    // unless disambiguator has high level
                    // knowledge about dists
                    async (E(i)) {
                        chk(E(i) == here);
                        atomic accessed_b(i) += 1;
                    }
                    A(i) = (future(E(i)){B(i)}).force();
                    atomic accessed_a(i) += 1;
                }
                // check if dist ops are working

                val D_nonlocal = D - D_local;
                chk((D_local || D_nonlocal).equals(D));
                for (val k: Point in D_local) {
                    chk(outOfRange(D_nonlocal, k));
                    chk(D_local(k) == px);
                }
                for (val k: Point in D_nonlocal) {
                    chk(outOfRange(D_local, k));
                    chk(D_nonlocal(k) != px);
                }
            }

        // ensure each A[i] was accessed exactly once
        finish ateach (val i: Point in D) chk(accessed_a(i) == 1);

        // ensure each B[i] was accessed exactly once
        finish ateach (val i: Point in E) chk(accessed_b(i) == 1);
    }

    public const N: int = 3;

    /**
     * For all combinations of dists of arrays B and A,
     * do an array copy from B to A, and verify.
     */
    public def run(): boolean = {

        val R: Region = [0..N-1, 0..N-1, 0..N-1, 0..N-1];
        val TestDists: Region = [0..dist2.N_DIST_TYPES-1, 0..dist2.N_DIST_TYPES-1];

        for (val distP(dX,dY): Point in TestDists) {
            val D: Dist = dist2.getDist(dX, R);
            val E: Dist = dist2.getDist(dY, R);
            chk(D.region.equals(E.region) && D.region.equals(R));
            val A: Array[int] = Array.make[int](D);
            val B: Array[int] = Array.make[int](E, (var p(i,j,k,l): Point) => { val x = ((i*N+j)*N+k)*N+l; x*x+1 });
            arrayCopy(A, B);
            arrayEqual(A, B);
        }
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ArrayCopy2().execute();
    }

    /**
     * utility for creating a dist from a
     * a dist type int value and a region
     */
    static class dist2 {

        const BLOCK: int = 0;
        const CYCLIC: int = 1;
        const BLOCKCYCLIC: int = 2;
        const CONSTANT: int = 3;
        const RANDOM: int = 4;
        const ARBITRARY: int = 5;
        const N_DIST_TYPES: int = 6;

        /**
         * Return a dist with region r, of type disttype
         */
        public static def getDist(distType: Int, r: Region): Dist(r) = {
            switch(distType) {
                case BLOCK: return Dist.makeBlock(r);
                case CYCLIC: return Dist.makeCyclic(r);
                case BLOCKCYCLIC: return Dist.makeBlockCyclic(r, 3);
                case CONSTANT: return r->here;
                case RANDOM: return Dist.makeRandom(r);
                case ARBITRARY:return Dist.makeArbitrary(r);
                default: throw new Error();
            }
        }
    }
}
