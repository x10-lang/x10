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
    public static double ytotal = 0.0;

    // this is the actul array where the result will be written to -- cvp
    public  static double[.] yt;

    /* 10 iterations used to make kernel have roughly
       same granulairty as other Scimark kernels. */

    public static void test( final double[.] y, 
			     final double[.] val, 
			     final int[.] row,
			     final int[.] col, 
			     final double[.] x, 
			     final int NUM_ITERATIONS, 
			     final int[.] lowsum, 
			     final int[.] highsum)
    {
        final int nz = val.region.size();
        yt=y;

        JGFInstrumentor.startTimer("Section2:SparseMatmult:Kernel"); 

        final clock c = clock.factory.clock();
	final dist p = dist.factory.unique(place.places);
	finish {
	    foreach(point [i] : (0 : place.MAX_PLACES - 1)) {
		async(p[i]) {
		    new SparseRunner(i, val, row, col, x, NUM_ITERATIONS, nz, lowsum, highsum).run();
		}
	    }
	}

        JGFInstrumentor.stopTimer("Section2:SparseMatmult:Kernel"); 

	for (int i=0; i < nz; i++) {
            ytotal += yt[ row[i] ];
	}

    }
}


class SparseRunner implements Runnable {

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

    public void run() {

	for (int reps=0; reps<num_ITERATIONS; reps++) {
	    for (int i=lowsum[id]; i<highsum[id]; i++) {
		SparseMatmult.yt[ row[i] ] += x[ col[i] ] * val[i];
	    }
	}

    }


}
