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
		// Spawn an activity for each index to
		// fetch and copy the value
		finish
			ateach (point p: D) {
				chk(D[p] == here);
				async(E[p]) chk(E[p] == here);
				A[p] = future(E[p]){B[p]}.force();
			}
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
		new ArrayCopy1().execute();
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

