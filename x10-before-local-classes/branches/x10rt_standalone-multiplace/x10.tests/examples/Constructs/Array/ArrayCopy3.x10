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
 * Based on original arraycopy3 by vj.
 *
 * @author kemal 1/2005
 */

public class ArrayCopy3 extends x10Test {

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
    public def arrayEqual(A: Array[int], B: Array[int](A.rank)) {
        val D = A.dist;
        val E:Dist(A.rank) = B.dist;
        // Spawn an activity for each index to
        // fetch the B[i] value
        // Then compare it to the A[i] value
        finish
            ateach (val p: Point(B.rank) in D) {
            val f = (future(E(p)){B(p)}).force();
            chk(A(p as Point(A.rank)) == f);
            }
    }

    /**
     * Set A[i] = B[i] for all points i.
     * A and B can have different dists whose
     * regions are equal.
     * Throws an error iff some assertion failed.
     */
    public def arrayCopy(val A: Array[int], val B: Array[int](A.rank)) {

        val D = A.dist;
        val E = B.dist;

        // Allows message aggregation
        val D_1 = Dist.makeUnique(D.places());

        // number of times elems of A are accessed
        val accessed_a = Array.make[int](D, (Point)=>0);

        // number of times elems of B are accessed
        val accessed_b  = Array.make[int](E, (Point)=>0);

        finish
            ateach (val x: Point in D_1) {
                val px: Place = D_1(x);
                chk(here == px);
                val LocalD: Region(A.rank) = (D | px).region;
                for (val py: Place in (E | LocalD).places()) {
                    val RemoteE: Region(A.rank) = (E | py).region;
                    val Common: Region(A.rank) = LocalD && RemoteE;
                    val D_common: Dist(A.rank) = D | Common;
                    // the future's can be aggregated
                    for (val i: Point(A.rank) in D_common) {
                        async(py) atomic accessed_b(i) += 1;
                        val temp  = (future(py){B(i)}).force();
                        // the following may need to be bracketed in
                        // atomic, unless the disambiguator
                        // knows about dists
                        A(i) = temp;
                        atomic accessed_a(i) += 1;
                    }
                    // check if dist ops are working
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
                }
            }

        // ensure each A[i] was accessed exactly once
        finish ateach (val i: Point(A.rank) in D) chk(accessed_a(i) == 1);

        // ensure each B[i] was accessed exactly once
        finish ateach (val i: Point(A.rank) in E) chk(accessed_b(i) == 1);
    }

    public const N: int = 3;

    /**
     * For all combinations of dists of arrays B and A,
     * do an array copy from B to A, and verify.
     */
    public def run(): boolean = {

        val R: Region{rank==4} = [0..N-1, 0..N-1, 0..N-1, 0..N-1];
        val TestDists: Region(2) = [0..dist2.N_DIST_TYPES-1, 0..dist2.N_DIST_TYPES-1];

        for (val distP(dX,dY): Point(2) in TestDists) {
            val D: Dist{rank==4} = dist2.getDist(dX, R);
            val E: Dist{rank==4} = dist2.getDist(dY, R);
            chk(D.region.equals(E.region) && D.region.equals(R));
            val A: Array[int]{rank==4} = Array.make[int](D, (Point)=>0);
            val B: Array[int]{rank==A.rank} = Array.make[int](E, 
            (p(i,j,k,l): Point) => { val x=((i*N+j)*N+k)*N+l; x*x+1});
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

        const BLOCK: int = 0;
        const CYCLIC: int = 1;
        const BLOCKCYCLIC: int = 2;
        const CONSTANT: int = 3;
        const N_DIST_TYPES: int = 4;

        /**
         * Return a dist with region r, of type disttype
         */
        public static def getDist(distType: Int, r: Region): Dist(r) = {
            switch(distType) {
                case BLOCK: return Dist.makeBlock(r);
                case CYCLIC: return Dist.makeCyclic(r);
                case BLOCKCYCLIC: return Dist.makeBlockCyclic(r, 0, 3);
                case CONSTANT: return r->here;
                default: throw new Error();
            }
        }
    }
}
