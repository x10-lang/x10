/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
package series;

import jgfutil.*;

//This is the Thread
public class SeriesRunner {

	int id;
	double[.] testArray;
	int array_rows;
	int nthreads;

	public SeriesRunner(int id, int p, int rows, double[.] a) {
		this.id = id;
		this.nthreads = p;
		this.testArray = a;
		this.array_rows = rows;
	}

	public void run() {
		double omega;       // Fundamental frequency.
		int ilow, iupper, slice;

		//int array_rows = SeriesTest.array_rows;

		// Calculate the fourier series. Begin by calculating A[0].

		if (id == 0) {
			testArray[0,0] = TrapezoidIntegrate((double)0.0, //Lower bound.
					(double)2.0,            // Upper bound.
					1000,                    // # of steps.
					(double)0.0,            // No omega*n needed.
					0) / (double)2.0;       // 0 = term A[0].
		}

		// Calculate the fundamental frequency.
		// (2 * pi) / period...and since the period
		// is 2, omega is simply pi.

		omega = (double) 3.1415926535897932;

		slice = (array_rows + nthreads-1)/nthreads;

		ilow = id*slice;
		if (id == 0) ilow = id*slice+1;
		iupper = (id+1)*slice;
		if (iupper > array_rows) iupper = array_rows;

		for (int i = ilow; i < iupper; i++)
		{
			// Calculate A[i] terms. Note, once again, that we
			// can ignore the 2/period term outside the integral
			// since the period is 2 and the term cancels itself
			// out.

			testArray[0,i] = TrapezoidIntegrate((double)0.0,
					(double)2.0,
					1000,
					omega * (double)i,
					1);                       // 1 = cosine term.

			// Calculate the B[i] terms.

			testArray[1,i] = TrapezoidIntegrate((double)0.0,
					(double)2.0,
					1000,
					omega * (double)i,
					2);                       // 2 = sine term.
		}
	}

	/**
	 * TrapezoidIntegrate.
	 *
	 * Perform a simple trapezoid integration on the function (x+1)**x.
	 * x0,x1 set the lower and upper bounds of the integration.
	 * nsteps indicates # of trapezoidal sections.
	 * omegan is the fundamental frequency times the series member #.
	 * select = 0 for the A[0] term, 1 for cosine terms, and 2 for
	 * sine terms. Returns the value.
	 */
	private double TrapezoidIntegrate (double x0,     // Lower bound.
									   double x1,     // Upper bound.
									   int nsteps,    // # of steps.
									   double omegan, // omega * n.
									   int select)    // Term type.
	{
		double x;               // Independent variable.
		double dx;              // Step size.
		double rvalue;          // Return value.

		// Initialize independent variable.

		x = x0;

		// Calculate stepsize.

		dx = (x1 - x0) / (double)nsteps;

		// Initialize the return value.

		rvalue = thefunction(x0, omegan, select) / (double)2.0;

		// Compute the other terms of the integral.

		if (nsteps != 1)
		{
			--nsteps;               // Already done 1 step.
			while (--nsteps > 0)
			{
				x += dx;
				rvalue += thefunction(x, omegan, select);
			}
		}

		// Finish computation.

		rvalue = (rvalue + thefunction(x1, omegan, select) / (double)2.0) * dx;
		return (rvalue);
	}

	/**
	 * thefunction.
	 *
	 * This routine selects the function to be used in the Trapezoid
	 * integration. x is the independent variable, omegan is omega * n,
	 * and select chooses which of the sine/cosine functions
	 * are used. Note the special case for select = 0.
	 */
	private double thefunction(double x,      // Independent variable.
							   double omegan, // Omega * term.
							   int select)    // Choose type.
	{
		// Use select to pick which function we call.

		switch (select)
		{
			case 0: return (Math.pow(x+(double)1.0, x));
			case 1: return (Math.pow(x+(double)1.0, x) * Math.cos(omegan*x));
			case 2: return (Math.pow(x+(double)1.0, x) * Math.sin(omegan*x));
		}

		// We should never reach this point, but the following
		// keeps compilers from issuing a warning message.

		return (0.0);
	}
}

