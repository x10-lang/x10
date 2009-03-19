/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Minimal test for conditional atomics.
 */
public class ConditionalAtomicTest extends x10Test {

	int value1 = 0;
	int value2 = 0;

	public boolean run() {
		final clock c = clock.factory.clock();
		async(this) clocked(c) {
			// this activity waits until value1 and
			// value2 are equal, then atomically makes
			// value1 two higher then value2
			while (true) {
				int temp;
				atomic temp = value1;
				if (temp >= 42) break;
				when (value1 == value2) { value1++; value2--; }
			}
		}
		async(this) clocked(c) {
			// this activity waits until value1 is
			// two higher than value2, then atomically raises
			// value2 to value1's level so they become equal
			while (true) {
				int temp;
				atomic temp = value2;
				if (temp >= 42) break;
				when (value1 == value2 + 2)
				{ value2 = value1; }
				or (value1 != value2+2 &&
						value1 != value2) //something went wrong
				{ value1 = value2 = 43; /* error */ };
			}
		}
		next; // wait until both activities end

		int temp;
		atomic temp = value1;
		return temp == 42;
	}

	public static void main(String[] args) {
		new ConditionalAtomicTest().executeAsync();
	}
}

