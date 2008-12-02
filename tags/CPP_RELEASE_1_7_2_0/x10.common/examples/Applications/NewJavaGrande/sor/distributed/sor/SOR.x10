/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
package sor;

import jgfutil.*;
import x10.lang.Double;

/**
 * X10 port of sor benchmark from Section 2 of Java Grande Forum Benchmark Suite.
 *
 *  SERIAL VERSION
 *
 * @author Vivek Sarkar (vsarkar@us.ibm.com)
 *
 * Porting issues identified:
 * 1) Replace Java multidimensional array by X10 Multidimensional array
 *    FIXME: This is no longer a problem.
 */
public class SOR {

	const Double gtotal = new Double(0.0);

	public static double read(final double[.] G, final int i, final int j) {
		return future (G.distribution[i,j]) { G[i,j] }.force();
	}

	public static final void SORrun(final double omega, final double[.] G, final int num_iterations)
	{
		final int M = G.distribution.region.rank(0).size();
		final int N = G.distribution.region.rank(1).size();

		final double omega_over_four = omega * 0.25;
		final double one_minus_omega = 1.0 - omega;

		// update interior points
		//
		final int Mm1 = M-1;
		final int Nm1 = N-1;

		JGFInstrumentor.startTimer("Section2:SOR:Kernel");

		for (point [p] : [0 : num_iterations-1])
			for (point [o] : [0:1])
				finish foreach (point [ii] : [0: (((Mm1-1)-(1+o))/2)]) {
					final int i = 2 * ii + 1 + o;
					finish async (G.distribution[i,1])
						for (point [j] : [1 : Nm1-1])
							G[i,j] = omega_over_four * (read(G, i-1, j) + read(G, i+1, j) + G[i,j-1]
									+ G[i,j+1]) + one_minus_omega * G[i,j];
				}
		JGFInstrumentor.stopTimer("Section2:SOR:Kernel");
		gtotal.val = G.sum();
	}
}

