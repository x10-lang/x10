/**************************************************************************
*                                                                         *
*         Java Grande Forum Benchmark Suite - Thread Version 1.0          *
*                                                                         *
*                            produced by                                  *
*                                                                         *
*                  Java Grande Benchmarking Project                       *
*                                                                         *
*                                at                                       *
*                                                                         *
*                Edinburgh Parallel Computing Centre                      *
*                                                                         *
*                email: epcc-javagrande@epcc.ed.ac.uk                     *
*                                                                         *
*      adapted from SciMark 2.0, author Roldan Pozo (pozo@cam.nist.gov)   *
*                                                                         *
*      This version copyright (c) The University of Edinburgh, 2001.      *
*                         All rights reserved.                            *
*                                                                         *
**************************************************************************/
package sparsematmult;

import jgfutil.*;
import x10.lang.Double;

public class SparseMatmult {

	const Double ytotal = new Double(0.0);

	/* 10 iterations used to make kernel have roughly
	   same granularity as other Scimark kernels. */
	public static void test(final double[.] y, final double value[.] val, final int value[.] row, final int value[.] col,
							final double value[.] x, final int NUM_ITERATIONS, final int numthreads, final int value[.] lowsum,
							final int value [.] highsum)
	{
		final int nz = val.region.size();

		JGFInstrumentor.startTimer("Section2:SparseMatmult:Kernel");

		finish foreach (point [id] : [0:numthreads-1])
			for (point [reps] : [0: NUM_ITERATIONS-1])
				for (point [i] : [lowsum[id] : highsum[id]-1])
					y[row[i]] += x[col[i]]*val[i];

		JGFInstrumentor.stopTimer("Section2:SparseMatmult:Kernel");
		for (point [i]: [0:nz-1]) {
			ytotal.val += y[row[i]];
		}
	}
}

