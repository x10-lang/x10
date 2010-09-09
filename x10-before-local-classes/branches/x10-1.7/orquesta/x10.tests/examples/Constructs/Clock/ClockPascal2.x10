/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

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

	public const N: int = 5;
	public const EXPECTED_CHECKSUM: int = prod(N+1, 2*N) / prod(1, N) - 1;
	//const int EXPECTED_CHECKSUM = 251; // (for N = 5)
	public const DELAY: int = 2000;
	public def run(): boolean = {
		final val D: dist = Dist.makeConstant([0..N-1, 0..N-1], here);
		final val Dinner: dist{rank==D.rank} = D|[1..N-1, 1..N-1];
		final val Dboundary: dist = D-Dinner;
		final val A: Array[int] = new Array[int](D, (var point [i,j]: point): int => { return Dboundary.contains([i, j]) ? 1 : 0; });
		finish async {
			final val N: Array[nullable<clock>] = new Array[nullable<clock>](D);
			for (val (i,j): point in D) { N(i, j) = clock.factory.clock(); }
			final val W: Array[nullable<clock>] = new Array[nullable<clock>](D);
			for (val (i,j): point in D) { W(i, j) = clock.factory.clock(); }

			foreach (val (i,j): point in Dinner) {
					for (val (n): point in [3..(i+j)]) {
						randDelay(DELAY);
						pr1(i, j, n);
						next;
						pr2(i, j, n);
					}
					randDelay(DELAY);
					A(i, j) = compute(A(i-1, j), A(i, j-1));
					pr3(i, j, A);
					next;
					pr4(i, j, A);
				}
		}
		return A.sum() == EXPECTED_CHECKSUM;
	}

	public const rand: java.util.Random = new java.util.Random(1L);

	static def randDelay(var millis: int): void = {
		var n: int;
		atomic n = rand.nextInt(millis);
		x10.lang.Runtime.sleep(n);
	}

	public const startTime: long = System.currentTimeMillis();

	static def tim(): double = {
		var x: long = System.currentTimeMillis();
		return (double)((x-startTime)/1000.00);
	}

	static def pr1(var i: int, var j: int, var n: int): void = {
		System.out.println(tim()+" sec: About to do next " +(n-2) + " of " + (i+j-1)+ " (wait for input): ["+i+","+j+"]");
	}

	static def pr2(var i: int, var j: int, var n: int): void = {
		System.out.println(tim()+" sec: Passed next " +(n-2) + " of " + (i+j-1)+ " (wait for input): ["+i+","+j+"]");
	}

	static def pr3(var i: int, var j: int, var A: Array[int]): void = {
		System.out.println(tim()+" sec: About to do next "+(i+j-1)+" of "+(i+j-1)+" (produce output): A["+i+","+j+"] = "+A(i, j));
	}

	static def pr4(var i: int, var j: int, var A: Array[int]): void = {
		System.out.println(tim()+" sec: Passed next "+(i+j-1)+" of "+(i+j-1)+" (produce output): A["+i+","+j+"] = "+A(i, j));
	}

	/**
	 * Compute the array element using its west and north neighbors
	 */
	static def compute(var x: int, var y: int): int = {
		return x+y;
	}

	/**
	 * Product of numbers from m to n inclusive.
	 * Note that prod(1, n) == n!
	 */
	static def prod(var m: int, var n: int): int = {
		var s: int = 1;
		for (var i: int = m; i <= n; i++)
			s *= i;
		return s;
	}

	public static def main(var args: Rail[String]): void = {
		new ClockPascal2().execute();
	}
}
