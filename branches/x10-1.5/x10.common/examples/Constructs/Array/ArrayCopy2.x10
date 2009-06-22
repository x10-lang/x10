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
	public void arrayCopy(final int[.] A, final int[.] B) {
		final dist D = A.distribution;
		final dist E = B.distribution;
		// Spawn one activity per place

		final dist D_1 = dist.factory.unique(D.places());
		// number of times elems of A are accessed
		final int[.] accessed_a = new int[D];
		// number of times elems of B are accessed
		final int[.] accessed_b = new int[E];

		finish
			ateach (point x: D_1) {
				final place px = D_1[x];
				chk(px == here);
				final dist(:rank==D.rank) D_local= (D | px);
				for (point i : D_local) {
					// assignment to A[i] may need to be atomic
					// unless disambiguator has high level
					// knowledge about dists
					async (E[i]) {
						chk(E[i] == here);
						atomic accessed_b[i] += 1;
					}
					A[i] = future(E[i]){B[i]}.force();
					atomic accessed_a[i] += 1;
				}
				// check if dist ops are working

				final dist(:rank==D.rank) D_nonlocal= D - D_local;
				chk((D_local || D_nonlocal).equals(D));
				for(point k: D_local) {
					chk(outOfRange(D_nonlocal, k));
					chk(D_local[k] == px);
				}
				for (point k: D_nonlocal) {
					chk(outOfRange(D_local, k));
					chk(D_nonlocal[k] != px);
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
		final region R = [0:N-1,0:N-1,0:N-1,0:N-1];
		final region TestDists = [0:dist2.N_DIST_TYPES-1,0:dist2.N_DIST_TYPES-1];

		for (point distP[dX,dY]: TestDists) {
			final dist D = dist2.getDist(dX, R);
			final dist E = dist2.getDist(dY, R);
			chk(D.region.equals(E.region) && D.region.equals(R));
			final int[.] A = new int[D];
			final int[.] B = new int[E]
				(point p[i,j,k,l]) { int x = ((i*N+j)*N+k)*N+l; return x*x+1; };
			arrayCopy(A, B);
			arrayEqual(A, B);
		}
		return true;
	}

	public static void main(String[] args) {
		new ArrayCopy2().execute();
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
		public static dist getDist(int distType, region r) {
			switch(distType) {
				case BLOCK: return dist.factory.block(r);
				case CYCLIC: return dist.factory.cyclic(r);
				case BLOCKCYCLIC: return dist.factory.blockCyclic(r, 3);
				case CONSTANT: return dist.factory.constant(r, here);
				case RANDOM: return dist.factory.random(r);
				case ARBITRARY: return dist.factory.arbitrary(r);
				default: throw new Error();
			}
		}
	}
}

