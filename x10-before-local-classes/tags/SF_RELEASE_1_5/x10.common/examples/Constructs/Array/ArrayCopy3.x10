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
	static boolean outOfRange(final dist D, final point x) {
		boolean gotException = false;
		try {
			async(D[x]) {}; // dummy op just to use D[x]
		} catch (Throwable e) {
			gotException = true;
		}
		return gotException;
	}

	/**
	 * Does not throw an error iff A[i] == B[i] for all points i.
	 */
	public void arrayEqual(final int[.] A, final int[.] B) {
		final dist D = A.distribution;
		final dist E = B.distribution;
		// Spawn an activity for each index to
		// fetch the B[i] value
		// Then compare it to the A[i] value
		finish
			ateach(point p: D) chk(A[p] == future(E[p]){B[p]}.force());
	}

	/**
	 * Set A[i] = B[i] for all points i.
	 * A and B can have different dists whose
	 * regions are equal.
	 * Throws an error iff some assertion failed.
	 */
	public void arrayCopy(final int[.] A, final int[:rank==A.rank] B) {
		final dist(:rank==A.rank) D = A.distribution;
		final dist(:rank==A.rank) E = B.distribution;
		// Allows message aggregation

		final dist D_1 = dist.factory.unique(D.places());
		// number of times elems of A are accessed
		final int[.] accessed_a = new int[D];
		// number of times elems of B are accessed
		final int[.] accessed_b = new int[E];

		finish
			ateach (point x: D_1) {
				final place px = D_1[x];
				chk(here == px);
				final region(:rank==A.rank) LocalD = (D | px).region;
				for (place py : (E | LocalD).places()) {
					final region(:rank==A.rank) RemoteE = (E | py).region;
					final region(:rank==A.rank) Common = LocalD && RemoteE;
					final dist(:rank==A.rank) D_common = D | Common;
					// the future's can be aggregated
					for(point i: D_common) {
						async(py) atomic accessed_b[i] += 1;
						final int temp =
							future(py){B[i]}.force();
						// the following may need to be bracketed in
						// atomic, unless the disambiguator
						// knows about dists
						A[i] = temp;
						atomic accessed_a[i] += 1;
					}
					// check if dist ops are working
					final dist(:rank==A.rank) D_notCommon = D - D_common;
					chk((D_common || D_notCommon).equals(D));
					final dist(:rank==A.rank) E_common = E | Common;
					final dist(:rank==A.rank) E_notCommon= E - E_common;

					chk((E_common || E_notCommon).equals(E));
					for (point k: D_common) {
						chk(D_common[k] == px);
						chk(outOfRange(D_notCommon, k));
						chk(E_common[k] == py);
						chk(outOfRange(E_notCommon, k));
						chk(D[k] == px && E[k] == py);
					}

					for (point k: D_notCommon) {
						chk(outOfRange(D_common, k));
						chk(!outOfRange(D_notCommon, k));
						chk(outOfRange(E_common, k));
						chk(!outOfRange(E_notCommon, k));
						chk(!(D[k] == px && E[k] == py));
					}
				}
			}
		// ensure each A[i] was accessed exactly once
		finish ateach(point i: D) chk(accessed_a[i] == 1);
		// ensure each B[i] was accessed exactly once
		finish ateach(point i: E) chk(accessed_b[i] == 1);
	}

	const int N = 3;

	/**
	 * For all combinations of dists of arrays B and A,
	 * do an array copy from B to A, and verify.
	 */
	public boolean run() {
		final region(:rank==4) R = [0:N-1,0:N-1,0:N-1,0:N-1];
		final region TestDists = [0:dist2.N_DIST_TYPES-1,0:dist2.N_DIST_TYPES-1];

		for (point distP[dX,dY]: TestDists) {
			final dist(:rank==4) D = dist2.getDist(dX, R);
			final dist(:rank==4) E = dist2.getDist(dY, R);
			chk(D.region.equals(E.region) && D.region.equals(R));
			final int[:rank==4] A = new int[D];
			final int[:rank==A.rank] B = (int[:rank==A.rank])  new int[E]
				(point p[i,j,k,l]) { int x = ((i*N+j)*N+k)*N+l; return x*x+1; };
			arrayCopy(A, B);
			arrayEqual(A, B);
		}
		return true;
	}

	public static void main(String[] args) {
		new ArrayCopy3().execute();
	}

	/**
	 * utility for creating a dist from a
	 * a dist type int value and a region
	 */
	static class dist2 {
		const int BLOCK = 0;
		const int CYCLIC = 1;
		const int BLOCKCYCLIC = 2;
		const int CONSTANT = 3;
		const int RANDOM = 4;
		const int ARBITRARY = 5;
		public const int N_DIST_TYPES = 6;

		/**
		 * Return a dist with region r, of type disttype
		 */
		public static dist(:rank==r.rank) getDist(int distType, final region r) {
			switch(distType) {
				case BLOCK: return (dist(:rank==r.rank)) dist.factory.block(r);
				case CYCLIC: return (dist(:rank==r.rank))dist.factory.cyclic(r);
				case BLOCKCYCLIC: return (dist(:rank==r.rank))dist.factory.blockCyclic(r, 3);
				case CONSTANT: return (dist(:rank==r.rank)) dist.factory.constant(r, here);
				case RANDOM: return (dist(:rank==r.rank)) dist.factory.random(r);
				case ARBITRARY: return (dist(:rank==r.rank)) dist.factory.arbitrary(r);
				default: throw new Error();
			}
		}
	}
}

