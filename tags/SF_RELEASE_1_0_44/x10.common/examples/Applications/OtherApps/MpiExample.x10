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
 *
 * This program illustrates a multiple-producer single consumer
 * paradigm in X10. It is inspired from an MPI code example in
 * "MPI: The complete reference" by Snir et al.
 *
 * @author kemal 12/2004
 */
public class MpiExample extends x10Test {

	public boolean run() {

		// Distribution P just supplies a mapping from
		// integer i to place i

		final dist P = unique();

		// The current place is place 0.  Spawn producer activities
		// (producing an object of value type "T") in each of the other
		// places 1..MAX_PLACES-1, and a corresponding local consumer
		// activity in place 0.  Each consumer activity in place 0
		// first blocks while waiting for the result of its producer,
		// and then atomically consumes the result. Production takes
		// a random time, and results will arrive from the producers
		// in an arbitrary order.

		finish ateach (point [k] : [0:place.MAX_PLACES-1]->here) {
			future(P[k]) { new T(k) }.force().consume();
		}
		int nConsumed_value = (future (T.nConsumed.location) { T.nConsumed.value }).force();
		System.out.println("Total items consumed = " + nConsumed_value);
		return nConsumed_value == place.MAX_PLACES;
	}

	/**
	 * Utility function for creating a unique dist
	 */
	private static dist unique() {
		return dist.factory.unique(place.places);
	}

	/**
	 * Main routine
	 */
	public static void main(String[] args) {
		new MpiExample().execute();
	}

	/**
	 * T: the item to produce/consume, for testing a multiple producer,
	 * single consumer paradigm.
	 * @author kemal 12/2004
	 */
	static value class T extends x10.lang.Object {
		public static final boxedInt nConsumed = new boxedInt(0);

		// the dummy content of the produced object.
		private int val;

		/**
		 * Produce a T. i indicates place number where it is produced.
		 * Production has a random duration, so T's will arrive in
		 * random order from the producers.
		 */
		T(int i) {
			System.out.println("Start producing # "+i);
			sleep(randomMillis(i));
			this.val = i;
			System.out.println("End producing # "+i);
		}

		/*
		 * Atomically consume a T.
		 * Other consumer activities in the same place
		 * will wait, while the current activity
		 * performs the consume operation.
		 */
		public void consume() {
			System.out.println("Start consuming #"+this.val);
			atomic delayLoop(1000000);
			async(nConsumed) atomic nConsumed.value++;
			System.out.println("End consuming #"+this.val);
		}

		/**
		 * a delay loop
		 */
		private safe void delayLoop(int n) {
			int s = 0;
			for (int i = 0; i < n; i++) s += i;
			use(s); // prevent dead code elim.
		}

		/**
		 * dummy method to try to avoid dead code elimination
		 */
		private void use(int s) { }

		/**
		 * Return a random number between 1 and N
		 * using i as initial seed
		 */
		private final static int N = 10000;
		private static int randomMillis(int i) {
			return (new Random(i+17)).nextInt(N)+1;
		}

		/**
		 * The current activity will sleep for n milliseconds.
		 * Borrowed from Java infrastructure.
		 */
		private static void sleep(int n) {
			try {
				Thread.sleep(n);
			}
			catch (InterruptedException e) {
				// no-op
			}
		}
	}

	/**
	 * A boxed int class, serves like a mutable static
	 */
	static class boxedInt {
		public int value;
		public boxedInt(int x) {
			value = x;
		}
	}
}

