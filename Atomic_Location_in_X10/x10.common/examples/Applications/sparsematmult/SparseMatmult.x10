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

public class SparseMatmult
{

    // final checksum -- cvp
    public static final BoxedDouble ytotal = new BoxedDouble(0.0);


    /* 10 iterations used to make kernel have roughly
       same granulairty as other Scimark kernels. */

    public static void test( final double[.] yt, 
			     final double[.] val, 
			     final int[.] row,
			     final int[.] col, 
			     final double[.] x, 
			     final int NUM_ITERATIONS, 
			     final int[.] lowsum, 
			     final int[.] highsum)
    {
        final int nz = val.region.size();

        JGFInstrumentor.startTimer("Section2:SparseMatmult:Kernel"); 

        final clock c = clock.factory.clock();
	final dist p = dist.factory.unique(place.places);
	finish {
	    foreach(point [i] : [0 : place.MAX_PLACES - 1]) {
		async(p[i]) {
		    new SparseRunner(i, val, row, col, x, NUM_ITERATIONS, nz, lowsum, highsum).run(yt);
		}
	    }
	}

        JGFInstrumentor.stopTimer("Section2:SparseMatmult:Kernel"); 

	for (int i=0; i < nz; i++) {
	    final int i_final = i;
	    final int row_i = (future (row.distribution[i]) { row[i_final] }).force();
            ytotal.val += (future (yt.distribution[row_i]) { yt[row_i] }).force();
	}

    }
}


class SparseRunner {

    int id;     // read-only -- cvp
    int nz;     // read-only -- cvp
    int[.] row; // read-only -- cvp
    int[.] col;          // read-only -- cvp
    int num_ITERATIONS;  // read-only -- uppercase name was assumed implicitly final but that cause compliation error in javac
    double[.] val;   // read-only -- cvp
    double[.] x;     // read-only -- cvp
    int[.] lowsum;   // read-only -- cvp
    int[.] highsum;  // read-only -- cvp

    public SparseRunner(final int id, 
			final double[.] val, 
			final int[.] row,
			final int[.] col, 
			final double[.] x, 
			final int NUM_ITERATIONS,
			final int nz, 
			final int[.] lowsum, 
			final int[.] highsum) {
        this.id = id;
        this.x=x;
        this.val=val;
        this.col=col;
        this.row=row;
        this.nz=nz;
        this.num_ITERATIONS=NUM_ITERATIONS;
        this.lowsum = lowsum;
        this.highsum = highsum;
    }

    public void run(final double[.] yt) {

	for (int reps=0; reps<num_ITERATIONS; reps++) {
	    for (int i=lowsum[id]; i<highsum[id]; i++) {
		// cvp: row, col an val are values arrays at this point -
		// it would be good if they could be distributed ...
		final int i_final = i;
		place pl_i = row.distribution[i];
		final int row_i = (future (pl_i) { row[i_final] }).force();
		final int col_i = (future (pl_i) { col[i_final] }).force();
		final double val_i = (future (pl_i) { val[i_final] }).force();
		
		final double rhs = (future (x.distribution[col_i]) { x[col_i] * val_i }).force();
		finish async (yt.distribution[row_i]) { yt[row_i] += rhs; }
	    }
	}

    }


}