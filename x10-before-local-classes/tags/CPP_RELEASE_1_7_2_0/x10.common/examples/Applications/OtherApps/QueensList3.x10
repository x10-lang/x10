/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import java.util.Random;
import harness.x10Test;

/**
 * N-queens problem.
 *
 * This version chooses a random place to run each new activity.
 *
 * @author kemal 4/2005
 */
public value class QueensList3 extends x10Test {
	// Operations and data structures for a list of queen coordinates

	// Since QueensList3 is an immutable value class,
	// its instances do not have a fixed place.
	// The instances are passed from place to place
	// freely, allowing activities operating on the instances
	// to be load-balanced dynamically.

	/**
	 * nullary constructor: create an empty value array
	 */
	public QueensList3() {
		q = new int value[[0:-1]->here];
	}

	/**
	 * Constructor creates oldList with newItem added at end
	 */
	public QueensList3(final int value [.] oldList, final int newItem) {
		final int n = length(oldList);
		q = new int value[[0:n]->here]
			(point [j]) { return j < n ? oldList[j] : newItem; };
	}

	// Number of solutions
	const BoxedInt nSolutions = new BoxedInt(0);

	// Length of one side of chess board
	const int N = 5;

	// Expected number of solutions for each N
	const int[] expectedSolutions =
	{ 0, 1, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200,
		73712, 365596, 2279184, 14772512 };

	// Let n = q.length, then
	// q[0],q[1],...,q[n-1] are the columns in which queens were placed
	// for rows 0,1,...,n-1, respectively.
	// I.e., (0,q[0]) (1.q[1]) ... (n-1,q[n-1]) are the coordinates
	// of the queens that were already placed.
	private int value[.] q;

	/**
	 * Return true iff one of the already placed queens in coordinates
	 * (0,q[0]) .... (n-1,q[n-1]) "attacks" a new queen at (n,j)
	 */
	public boolean attacks(int j) {

		//Exists(row k:0..n-1)(sameColumn(j,q[k])||sameDiagonal(<n,j>,<k,q[k]>))

		int n = length(q); // the next row to place a queen
		for (point [k]: [0:(n-1)]) {
			if (j == q[k] ||                       // same column
					Math.abs(n-k) == Math.abs(j-q[k])) // same diagonal
				return true;
		}
		return false;
	}

	/**
	 * Method Q solves the N-queens problem with
	 * a brute-force tree search.
	 *
	 * If all N queens were already placed, atomically increment the
	 * number of solutions, and return.
	 *
	 * Otherwise: given a list of queen positions placed in rows
	 * 0,...,n-1,
	 * for each column in row n (in parallel)
	 * if a new queen can be placed in this column,
	 * place the queen there, and spawn a new activity (in an
	 * appropriately chosen place) to recursively continue from
	 * this chess board configuration, with the next row n+1.
	 * After spawning all such activities, return.
	 */
	public void Q() {
		if (length(q) == N) { async(nSolutions) atomic nSolutions.val++; return; }

		foreach (point p[k]: [0:(N-1)]) { //try all columns of next row
			if (!attacks(k)) {
				async(loadBalancer.choosePlace())
				{ (new QueensList3(q, k)).Q(); };
			}
		}
	}

	/**
	 * utility function length() for
	 * one dimensional value arrays.
	 */
	public static int length(int value [.] q) {
		if (q.region.size() == 0) return 0;
		else return q.region.high()-q.region.low()+1;
	}

	/**
	 * run the n-queens problem
	 */
	public boolean run() {
		finish { this.Q(); }
		System.out.println("Number of solutions for "+N+" queens: "+nSolutions.val);
		return nSolutions.val == expectedSolutions[N];
	}

	public static void main(String[] args) {
		new QueensList3().execute();
	}

	/**
	 * Need this class since we cannot store java objects in x10 arrays
	 */
	static class BoxedRandom {
		nullable<Random> val;
		public BoxedRandom(long x) {
			val = new Random(x);
		}
		int nextInt(int n) {
			return ((Random)val).nextInt(n);
		}
	}

	/**
	 * Load balancer class.
	 */
	static class loadBalancer {
		const dist UniqueD = dist.factory.unique();
		const int NP = place.MAX_PLACES;
		// random number generators with different seeds
		// in each place
		const BoxedRandom[.] Ran = new BoxedRandom[UniqueD]
			(point p[i]) { return new BoxedRandom((long)i*17L); };

		/**
		 * find an appropriate place to ship a task
		 */
		public static place choosePlace() {
			// choose a random place (includes here)
			place nextPlace = UniqueD[Ran[(here).id].nextInt(NP)];
			System.out.println("Sending work from "+(here).id+" to "+nextPlace.id);
			return nextPlace;
		}
	}

	/**
	 * Helper class.
	 */
	static class BoxedInt {
		public int val;
		BoxedInt(int x) {
			val = x;
		}
	}
}

