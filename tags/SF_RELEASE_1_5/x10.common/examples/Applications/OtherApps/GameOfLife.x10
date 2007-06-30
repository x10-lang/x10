/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Conway's game of life
 *
 * @author kemal
 * @author vj 12/2004
 */
public class GameOfLife extends x10Test {

	// this constructor is necessary to run this test in the TestCompiler harness.
	public GameOfLife() { }

	/**
	 * Conway's game of life test.
	 * <code>
	 put this "glider" pattern in top left corner of the matrix

	 001
	 101
	 011

	 and let it move with periodic shape changes
	 to the bottom right to converge at the
	 4-cell block stable pattern:

	 000
	 011
	 011

	 * </code>
	 */
	public boolean run() {
		final int N = 6;

		final region R = [0:(N+1),0:(N+1)];
		final region(:rank==2) R_inner = [1:N,1:N];

		// Distribution D includes in its domain the
		// boundary rows and columns, as well as the inner
		// region of the cell matrix.

		final dist(:rank==2) D = (dist(:rank==2)) dist.factory.block(R);

		// Distribution D_inner implements the same index->place
		// mapping as D, but does not include boundaries in its domain.

		final dist(:rank==2) D_inner = D | R_inner;

		// D_boundary is (D-D_inner)

		final dist D_boundary = D-R_inner;

		final int EXPECTED_ITER = 15;
		final int EXPECTED_SUM = 4;

		// isAlive[i,j] is true iff the cell (i,j) is alive
		final int[.] isAlive = new int[D];

		//initialize the matrix
		init(isAlive, 1, 1,
			 new String[] {
				 "001",
				 "101",
				 "011",
			 });

		int nIter = 0;
		pr("isAlive", isAlive | D_inner);

		while (true) {
			// nn[i,j] = number of live neighbors of cell(i,j)
			final int[.] nn = new int[D_inner]
				(point p[i,j])
				{ return (isAlive | [(i-1):(i+1),(j-1):(j+1)]).sum()-isAlive[i,j]; };
			// Now recompute if each cell is still alive.
			// A live cell survives iff it has exactly 2 or 3 live neighbors.
			// A dead cell is born again iff it has exactly 3 live neighbors.
			final int[:distribution==D_inner] temp = new int[D_inner]
				(point p[i,j])
				{ return b2i(((isAlive[i,j] != 0) & (nn[i,j] == 2)) | (nn[i,j] == 3)); };
			pr("temp", temp);
			if (((isAlive | D_inner)-temp).abs().sum() == 0) break;
			isAlive.update(temp);
			nIter++;
		}
		int sum = isAlive.sum();
		System.out.println("Converged after "+nIter+" iterations");
		System.out.println("sum = "+sum);
		return nIter == EXPECTED_ITER && sum == EXPECTED_SUM;
	}

	/**
	 * Convert a boolean to an integer
	 */
	private static int b2i(boolean b) { return b ? 1 : 0; }

	/**
	 * method for initializing isAlive starting at (x0,y0)
	 * as the upper left corner of the given pattern.
	 *
	 * Example:
	 * <code>
	 init(isAlive, 1, 1, new String[] {
	 "001",
	 "101",
	 "011" });
	 * </code>
	 * overlays the given pattern on isAlive at position (1,1)
	 * as the upper left corner of the pattern
	 */
	private static void init(final int[.] isAlive, final int x0, final int y0, final String[] s) {
		final dist d = isAlive.distribution;
		finish
			for (point p[i]: [0:s.length-1]) {
				for (point q[j]: [0:s[i].length()-1]) {
					if (s[i].charAt(j) != '0') {
						async(d[i+x0,j+y0]) { isAlive[i+x0,j+y0] = 1; }
					}
				}
			}
	}

	/**
	 * print a matrix
	 */
	private static void pr(String s, final int[.] y) {
		System.out.println(s+":");
		dist d = y.distribution;
		region r = d.region;
		int nCol = r.rank(1).high()-r.rank(1).low()+1;
		int n = 0;
		for (point p[i,j]: d) {
			System.out.print(future(d[i,j]) { y[i,j] }.force()+" ");
			n++;
			if (n%nCol == 0) System.out.println("");
		}
	}

	public static void main(String[] args) {
		new GameOfLife().execute();
	}
}

