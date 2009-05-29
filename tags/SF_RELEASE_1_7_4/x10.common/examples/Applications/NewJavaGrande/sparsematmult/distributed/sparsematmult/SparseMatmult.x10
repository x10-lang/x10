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

	// final checksum -- cvp
	public static final Double ytotal = new Double(0.0);

	/* 10 iterations used to make kernel have roughly
	   same granulairty as other Scimark kernels. */
	public static void test(final double[.] y, final double value [.] val, final int value [.] row, final int value [.] col,
							final double value [.] x, final int NUM_ITERATIONS, final int value [.] lowsum,
							final int value [.] highsum)
	{
		final int nz = val.region.size();

		JGFInstrumentor.startTimer("Section2:SparseMatmult:Kernel");

		finish foreach (point [id] : dist.factory.unique(place.places))
			for (point [reps] : [0: NUM_ITERATIONS-1])
				for (point [i] : [lowsum[id] : highsum[id]-1])
					// finish async (yt.distribution[row[i]]) { yt[row[i]] += x[col[i]]*val[i]; }
					//  cvp finish async not necessary because access is place-local
					//  vj. No, thats not the case. Get bad place exception.
					//  cvp - again: finish async not necessary - I just overlooked to check in the change
					//        of the initialization of ilow and iup in JGFSparseMatMultBench.x10.
					finish async (y.distribution[row[i]]) { y[row[i]] += x[col[i]]*val[i]; }

		JGFInstrumentor.stopTimer("Section2:SparseMatmult:Kernel");
		for (point [i]: [0:nz-1]) {
			ytotal.val += future (y.distribution[row[i]]) { y[row[i]] }.force();
		}
	}
}

