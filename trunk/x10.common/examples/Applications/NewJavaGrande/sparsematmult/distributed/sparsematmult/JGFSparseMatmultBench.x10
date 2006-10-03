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
import java.util.Random;

public class JGFSparseMatmultBench extends SparseMatmult implements JGFSection2 {

	public int nthreads;

	private int size;
	private static final long RANDOM_SEED = 10101010;

	// reduced data sizes for test suite
	//private static final int[] datasizes_M = { 50000, 100000, 500000 };
	private static final int[] datasizes_M = { 100, 100000, 500000 };
	//private static final int[] datasizes_N = { 50000, 100000, 500000 };
	private static final int[] datasizes_N = { 100, 100000, 500000 };
	//private static final int[] datasizes_nz = { 250000, 500000, 2500000 };
	private static final int[] datasizes_nz = { 500, 500000, 2500000 };
	private static final int SPARSE_NUM_ITER = 200;

	final Random R = new Random(RANDOM_SEED);

	double value [.] x;
	double value [.] val;
	int value [.] col;
	int value [.] row;
	int value [.] lowsum;
	int value [.] highsum;

	double [.] y;

	public JGFSparseMatmultBench() {
		this.nthreads = place.MAX_PLACES;
	}

	public void JGFsetsize(int size) {
		this.size = size;
	}

	public void JGFinitialise() {
		final int ds_N = datasizes_N[size];
		final int ds_M = datasizes_M[size];
		final int ds_nz = datasizes_nz[size];
		final region r_nz = [0 : ds_nz-1];
		final region r_nthreads = [0 : nthreads-1];
		final dist d_M = dist.factory.block([0 : ds_M-1], place.places);

		final double[] xin = init(new double[ds_N], R);
		x = new double value[[0:xin.length-1]->here] (point [i]) { return xin[i]; };
		//x = doubleArray.factory.doubleValueArray(xin); // value array.
		y = new double[d_M];      // distributed -- cvp

		int [] ilow = new int[nthreads];
		int [] iup = new int[nthreads];
		int [] sum = new int[nthreads+1];
		final int [] rowt = new int[ds_nz];
		final int [] colt = new int[ds_nz];
		final double [] valt = new double[ds_nz];
		int sect = (ds_M + nthreads-1)/nthreads;

		int[] rowin = new int[ds_nz];
		int[] colin = new int[ds_nz];
		double[] valin = new double[ds_nz];
		final int[] lowsumin = new int[nthreads];
		final int[] highsumin = new int[nthreads];

		for (point [i] : [0:ds_nz-1]) {
			rowin[i] = Math.abs(R.nextInt()) % ds_M;
			colin[i] = Math.abs(R.nextInt()) % ds_N;
			valin[i] = R.nextDouble();
		}

		// reorder arrays for parallel decomposition

		for (point [i] : r_nthreads) {
			ilow[i] = i*sect;
			iup[i] = ((i+1)*sect)-1;
			if (iup[i] > ds_M) iup[i] = ds_M;
		}

		for (point [i] : r_nz)
			for (point [j] : r_nthreads)
				if ((rowin[i] >= ilow[j]) && (rowin[i] <= iup[j]))
					sum[j+1]++;

		for (point [j] : r_nthreads)
			for (point [i] : [0:j]) {
				lowsumin[j] +=  sum[j-i];
				highsumin[j] +=  sum[j-i];
			}

		for (point [i] : r_nz)
			for (point [j] : r_nthreads)
				if ((rowin[i] >= ilow[j]) && (rowin[i] <= iup[j])) {
					rowt[highsumin[j]] = rowin[i];
					colt[highsumin[j]] = colin[i];
					valt[highsumin[j]] = valin[i];
					highsumin[j]++;
				}

		row = new int value[[0:rowt.length-1]->here] (point [i]) { return rowt[i]; };
		col = new int value[[0:colt.length-1]->here] (point [i]) { return colt[i]; };
		val = new double value[[0:valt.length-1]->here] (point [i]) { return valt[i]; };
		lowsum = new int value[[0:lowsumin.length-1]->here] (point [i]) { return lowsumin[i]; };
		highsum = new int value[[0:highsumin.length-1]->here] (point [i]) { return highsumin[i]; };
		//row = intArray.factory.intValueArray(rowt);
		//col = intArray.factory.intValueArray(colt);
		//val = doubleArray.factory.doubleValueArray(valt);
		//lowsum = intArray.factory.intValueArray(lowsumin);
		//highsum = intArray.factory.intValueArray(highsumin);
	}

	public void JGFkernel() {
		SparseMatmult.test(y, val, row, col, x, SPARSE_NUM_ITER, lowsum, highsum);
	}

	public void JGFvalidate() {
		//double refval[] = { 75.02484945753453, 150.0130719633895, 749.5245870753752 };
		double refval[] = { 0.1436496372119012, 150.0130719633895, 749.5245870753752 };
		double dev = Math.abs(ytotal.val - refval[size]);
		if (dev > 1.0e-10) {
			System.out.println("Validation failed");
			System.out.println("ytotal = " + ytotal.val + "  " + dev + "  " + size);
			throw new Error("Validation failed");
		}
	}

	public void JGFtidyup() {
		System.gc();
	}

	public void JGFrun(int size) {
		JGFInstrumentor.addTimer("Section2:SparseMatmult:Kernel", "Iterations", size);

		JGFsetsize(size);
		JGFinitialise();
		JGFkernel();
		JGFvalidate();
		JGFtidyup();

		JGFInstrumentor.addOpsToTimer("Section2:SparseMatmult:Kernel", (double) (SPARSE_NUM_ITER));

		JGFInstrumentor.printTimer("Section2:SparseMatmult:Kernel");
	}

	private static double[] init (double[] a, java.util.Random R) {
		for (point [i]: [0:a.length-1]) a[i] = R.nextDouble() * 1e-6;
		return a;
	}
}

