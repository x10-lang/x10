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

/**
 * @author xinb
 * 	o 	modification; aggregate primitive cross-place write.
 */
package sparsematmult;
import jgfutil.*;
import x10.lang.Double;
public class SparseMatmult {
	static final dist uniqueD = dist.factory.unique();
	// final checksum -- cvp
	public static final Double ytotal = new Double(0.0);
	
	/* 10 iterations used to make kernel have roughly
	 same granulairty as other Scimark kernels. */
	
	public static void test( final double[.] y, final double value [.] val, final int value [.] row, final int value [.] col, 
			final double value [.] x, final int NUM_ITERATIONS, final int value [.] lowsum, final int value [.] highsum) {
		final int nz = val.region.size();
		
		JGFInstrumentor.startTimer("Section2:SparseMatmult:Kernel"); 
		
		finish foreach (point [id] : uniqueD) {
			final int low = lowsum[id];
			final int high = highsum[id];
			final double[] tmp = new double[high-low];
			
			for (point [reps] : [0: NUM_ITERATIONS-1]) 
				for (point [i] : [low : high-1]) {				
					tmp[i-low] += x[col[i]]*val[i];
				}
			for (point [i] : [low : high-1]) {
				final double yi = tmp[i-low];
				final int indx = row[i];
				finish async (y.distribution[indx]) { 
					y[indx] += yi; 
				}
			}
		}
		
		JGFInstrumentor.stopTimer("Section2:SparseMatmult:Kernel"); 
		
		for (point [i]:[0:nz-1]) {
            ytotal.val += future (y.distribution[row[i]]) {y[ row[i] ]}.force();
          }
	}
}


