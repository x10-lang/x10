/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
package sor;

import jgfutil.*;
import java.util.Random;

/**
 * X10 port of sor benchmark from Section 2 of Java Grande Forum Benchmark Suite.
 *
 *  PARALLEL VERSION
 *
 * @author Vivek Sarkar (vsarkar@us.ibm.com)
 *
 * Porting issues identified:
 * 1) Replace Java multidimensional arrays by X10 multidimensional arrays, since Java multidimensional arrays
 *    do not currently work in X10.  See following sample output when trying to compile a code w/ a multi-dim array:
 *       sor\JGFSORBench.x10:79: The type of the variable initializer "double[]" does
 *       not match that of the declaration "double[][]".
 *              double [][] A = new double[M][N];
 *                              ^-------------^
 *    FIXME: This is no longer a problem.
 * 2) Add suffix D for all double constants
 * 3) Increased error threshold from 1e-12 to 1e-3
 *    TODO: check the source of error --- is it due to the red-black loop parallelization?
 */
public class JGFSORBench extends SOR implements JGFSection2 {

	private int size;
	private int datasizes[] = { 10, 1500, 2000 };
	private static final int JACOBI_NUM_ITER = 100;
	private static final long RANDOM_SEED = 10101010;

	Random R = new Random(RANDOM_SEED);

	public void JGFsetsize(int size) {
		this.size = size;
	}

	public void JGFinitialise() {
	}

	public void JGFkernel() {
		double [.] G = RandomMatrix(datasizes[size], datasizes[size], R);

		SORrun(1.25, G, JACOBI_NUM_ITER);
	}

	public void JGFvalidate() {
		//double refval[] = { 0.0012191583622038237D, 1.123010681492097D, 1.9967774998523777D };
		double refval[] = { 4.5185971433257635E-5D, 1.123010681492097D, 1.9967774998523777D };
		double dev = Math.abs(gtotal.val - refval[size]);
		if (dev > 1.0e-12) {
			System.out.println("Validation failed");
			System.out.println("gtotal = " + gtotal.val + "  " + dev + "  " + size);
			throw new Error("Validation failed");
		}
	}

	public void JGFtidyup() {
		System.gc();
	}

	public void JGFrun(int size) {
		JGFInstrumentor.addTimer("Section2:SOR:Kernel", "Iterations", size);

		JGFsetsize(size);
		JGFinitialise();
		JGFkernel();
		JGFvalidate();
		JGFtidyup();

		JGFInstrumentor.addOpsToTimer("Section2:SOR:Kernel", (double) (JACOBI_NUM_ITER));

		JGFInstrumentor.printTimer("Section2:SOR:Kernel");
	}

	private static double[.] RandomMatrix(final int M, final int N, final java.util.Random R)
	{
		double[.] t = new double[[0:M-1,0:N-1]->here];
		for (point [i,j]: t) t[i,j] = R.nextDouble() * 1e-6;
		return t;
	}
}

