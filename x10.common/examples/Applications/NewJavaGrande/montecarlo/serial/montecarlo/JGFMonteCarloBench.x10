/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
package montecarlo;

import java.io.*;
import jgfutil.*;

/**
 * X10 port of montecarlo benchmark from Section 3 of Java Grande Forum Benchmark Suite (Version 2.0).
 *
 * @author Vivek Sarkar (vsarkar@us.ibm.com)
 * @author vj
 *
 * Porting issues identified:
 * 3) Add D explicitly to literals in double array initializer
 * 4) Increase error threshold for validation from 1e-12 to 1e-9
 */
public class JGFMonteCarloBench extends CallAppDemo implements JGFSection3 {

	public void JGFsetsize(int size) {
		this.size = size;
	}

	public void JGFinitialise() {
		initialise();
	}

	public void JGFapplication() {
		System.out.println("JGFMonteCarloBench size = " + datasizes[size]);
		JGFInstrumentor.startTimer("Section3:MonteCarlo:Run");

		runiters();

		JGFInstrumentor.stopTimer("Section3:MonteCarlo:Run");

		presults();
	}

	public void JGFvalidate() {
		//double[] refval = { -0.0333976656762814D, -0.03215796752868655D };
		double[] refval = { -0.03234469082617506D, -0.03215796752868655D };
		double dev = Math.abs(AppDemo.JGFavgExpectedReturnRateMC() - refval[size]);
		if (dev > 1.0e-12) {
			System.out.println("Validation failed");
			System.out.println(" expectedReturnRate = " + AppDemo.JGFavgExpectedReturnRateMC() + "  " + dev + "  " + size);
			throw new Error("Validation failed");
		}
	}

	public void JGFtidyup() {
		System.gc();
	}

	public void JGFrun(int size) {
		JGFInstrumentor.addTimer("Section3:MonteCarlo:Total", "Solutions", size);
		JGFInstrumentor.addTimer("Section3:MonteCarlo:Run", "Samples", size);

		JGFsetsize(size);

		JGFInstrumentor.startTimer("Section3:MonteCarlo:Total");

		JGFinitialise();
		JGFapplication();
		JGFvalidate();
		JGFtidyup();

		JGFInstrumentor.stopTimer("Section3:MonteCarlo:Total");

		JGFInstrumentor.addOpsToTimer("Section3:MonteCarlo:Run", (double) input[1]);
		JGFInstrumentor.addOpsToTimer("Section3:MonteCarlo:Total", 1);

		JGFInstrumentor.printTimer("Section3:MonteCarlo:Run");
		JGFInstrumentor.printTimer("Section3:MonteCarlo:Total");
	}
}

