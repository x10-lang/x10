/**************************************************************************
*                                                                         *
*             Java Grande Forum Benchmark Suite - Version 2.0             *
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
*                                                                         *
*      This version copyright (c) The University of Edinburgh, 1999.      *
*                         All rights reserved.                            *
*                                                                         *
**************************************************************************/
package lufact;

import jgfutil.*;

/**
 * Ported to x10 March 18th 2005
 * @author ahk
 * @author cmd
 */
public class JGFLUFactBench extends Linpack implements JGFSection2 {

	private int size;
	//private int datasizes[] = { 150, 1000, 2000 };
	private int datasizes[] = { 50, 1000, 2000 };
	public void JGFsetsize(int size) {
		this.size = size;
	}

	public void JGFinitialise() {
		n = datasizes[size];
		System.out.println("ATTENTION: Running with smaller size (" + n + " instead of 500)");
		ldaa = n;
		lda = ldaa + 1;

		region vectorRegion = [0:ldaa];
		region rectangularRegion = [0:ldaa, 0:lda];
		region slimRegion = [0:0, 0:lda]; //fake out because we don't support array sections
		dist rectangular_distribution = rectangularRegion->here; // dist.factory.blockCyclic(rectangularRegion, lda+1);

		a = new double[rectangular_distribution];
		b = new double [slimRegion->here];
		x = new double [slimRegion->here];
		ipvt = new int [ldaa];

		long nl = (long) n;   //avoid integer overflow
		ops = (2.0*(nl*nl*nl))/3.0 + 2.0*(nl*nl);

		norma = matgen(a, lda, n, b);
	}

	public void JGFkernel() {
		JGFInstrumentor.startTimer("Section2:LUFact:Kernel");
		info = dgefa(a, lda, n, ipvt);
		dgesl(a, lda, n, ipvt, b, 0);
		JGFInstrumentor.stopTimer("Section2:LUFact:Kernel");
	}

	public void JGFvalidate() {
		int i;
		double eps, residn;
		final double ref[] = { 6.0, 12.0, 20.0 };

		for (i = 0; i < n; i++) {
			x[0,i] = b[0,i];
		}
		norma = matgen(a, lda, n, b);
		for (i = 0; i < n; i++) {
			b[0,i] = -b[0,i];
		}

		dmxpy(n, b, n, lda, x, a);
		resid = 0.0;
		normx = 0.0;
		for (i = 0; i < n; i++) {
			resid = (resid > abs(b[0,i])) ? resid : abs(b[0,i]);
			normx = (normx > abs(x[0,i])) ? normx : abs(x[0,i]);
		}

		eps =  epslon((double)1.0);
		residn = resid/( n*norma*normx*eps );

		if (residn > ref[size]) {
			System.out.println("Validation failed");
			System.out.println("Computed Norm Res = " + residn);
			System.out.println("Reference Norm Res = " + ref[size]);
			throw new Error("Validation failed");
		}
	}

	public void JGFtidyup() {
		// Make sure large arrays are gc'd.

		/* CMD
		* this causes problems in X10, and strictly spreaking, is
		* unnecessary

		a = null;
		b = null;
		x = null;
		ipvt = null;
		System.gc();
		*/
	}

	public void JGFrun(int size) {
		JGFInstrumentor.addTimer("Section2:LUFact:Kernel", "Mflops", size);

		JGFsetsize(size);
		JGFinitialise();
		JGFkernel();
		JGFvalidate();
		JGFtidyup();

		JGFInstrumentor.addOpsToTimer("Section2:LUFact:Kernel", ops/1.0e06);
		JGFInstrumentor.printTimer("Section2:LUFact:Kernel");
	}
}

