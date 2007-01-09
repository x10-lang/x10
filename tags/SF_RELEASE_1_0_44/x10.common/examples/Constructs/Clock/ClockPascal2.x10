/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Create a "Pascal's triangle" matrix using a wavefront
 * computation with multiple clocks.
 *
 * This is the same as ClockPascal but uses
 * array elements as clocks (was failing as of 5/17)
 *
 * Here is a sample computation with a 3*3 matrix:
 * <code>

   a00 a01 a02
   a10 a11 a12
   a20 a21 a22

11: (w10,n01,w11,n11)
// activity 11 is registered with w10 n01 w11 n11
   a11 = f(a10,a01);
   next;

21: (w20,n11,w21,n21)
   next;
   a21 = f(a20,a11)
   next;

12: (w11,n02,w12,n12)
   next;
   a12 = f(a11,a02);
   next;

22: (w21,n12,w22,n22)
   next;
   next;
   a22 = f(a21,a12);
   next;

Column 0 and row 0 is initialized to 1s.

One possible execution:

0.451 sec: About to do next 1 of 3 (wait for input): [2,2]
0.732 sec: About to do next 1 of 1 (produce output): A[1,1] = 2
1.122 sec: About to do next 1 of 2 (wait for input): [1,2]
1.122 sec: Passed next 1 of 2 (wait for input): [1,2]
1.383 sec: About to do next 2 of 2 (produce output): A[1,2] = 3
1.983 sec: About to do next 1 of 2 (wait for input): [2,1]
1.993 sec: Passed next 1 of 3 (wait for input): [2,2]
1.993 sec: Passed next 1 of 1 (produce output): A[1,1] = 2
1.993 sec: Passed next 1 of 2 (wait for input): [2,1]
2.424 sec: About to do next 2 of 2 (produce output): A[2,1] = 3
2.895 sec: About to do next 2 of 3 (wait for input): [2,2]
2.895 sec: Passed next 2 of 2 (produce output): A[1,2] = 3
2.895 sec: Passed next 2 of 2 (produce output): A[2,1] = 3
2.895 sec: Passed next 2 of 3 (wait for input): [2,2]
3.506 sec: About to do next 3 of 3 (produce output): A[2,2] = 6
3.506 sec: Passed next 3 of 3 (produce output): A[2,2] = 6

 * </code>
 *
 * @author kemal, 5/2005
 */
public class ClockPascal2 extends x10Test {

	const int N = 5;
	const int EXPECTED_CHECKSUM = prod(N+1, 2*N) / prod(1, N) - 1;
	//const int EXPECTED_CHECKSUM = 251; // (for N = 5)
	const int DELAY = 2000;
	public boolean run() {
		final dist D = [0:N-1,0:N-1]->here;
		final dist(:rank==D.rank) Dinner = D|[1:N-1,1:N-1];
		final dist Dboundary = D-Dinner;
		final int[.] A = new int[D](point [i,j]) { return Dboundary.contains([i,j]) ? 1 : 0; };
		finish async {
			final nullable<clock>[.] N = new nullable<clock>[D];
			for (point [i,j]: D) { N[i,j] = clock.factory.clock(); }
			final nullable<clock>[.] W = new nullable<clock>[D];
			for (point [i,j]: D) { W[i,j] = clock.factory.clock(); }

			foreach (point [i,j]: Dinner)
				clocked(N[i-1,j], W[i,j-1], N[i,j], W[i,j]) {
					for (point [n]: [3:(i+j)]) {
						randDelay(DELAY);
						pr1(i, j, n);
						next;
						pr2(i, j, n);
					}
					randDelay(DELAY);
					A[i,j] = compute(A[i-1,j], A[i,j-1]);
					pr3(i, j, A);
					next;
					pr4(i, j, A);
				}
		}
		return A.sum() == EXPECTED_CHECKSUM;
	}

	const java.util.Random rand = new java.util.Random(1L);

	static void randDelay(int millis) {
		int n;
		atomic n = rand.nextInt(millis);
		x10.lang.Runtime.sleep(n);
	}

	const long startTime = System.currentTimeMillis();

	static double tim() {
		long x = System.currentTimeMillis();
		return (double)((x-startTime)/1000.00);
	}

	static void pr1(int i, int j, int n) {
		System.out.println(tim()+" sec: About to do next " +(n-2) + " of " + (i+j-1)+ " (wait for input): ["+i+","+j+"]");
	}

	static void pr2(int i, int j, int n) {
		System.out.println(tim()+" sec: Passed next " +(n-2) + " of " + (i+j-1)+ " (wait for input): ["+i+","+j+"]");
	}

	static void pr3(int i, int j, int[.] A) {
		System.out.println(tim()+" sec: About to do next "+(i+j-1)+" of "+(i+j-1)+" (produce output): A["+i+","+j+"] = "+A[i,j]);
	}

	static void pr4(int i, int j, int[.] A) {
		System.out.println(tim()+" sec: Passed next "+(i+j-1)+" of "+(i+j-1)+" (produce output): A["+i+","+j+"] = "+A[i,j]);
	}

	/**
	 * Compute the array element using its west and north neighbors
	 */
	static int compute(int x, int y) {
		return x+y;
	}

	/**
	 * Product of numbers from m to n inclusive.
	 * Note that prod(1, n) == n!
	 */
	static int prod(int m, int n) {
		int s = 1;
		for (int i = m; i <= n; i++)
			s *= i;
		return s;
	}

	public static void main(String[] args) {
		new ClockPascal2().execute();
	}
}

