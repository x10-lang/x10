

class SeriesTest {

	// Declare class data.

	var array_rows: int;
	var testArray: Array[double](2);  // Array of arrays.

	/**
	 * buildTestData.
	 * Instantiate array(s) to hold fourier coefficients.
	 */
	def buildTestData(): void = {
		val R: Region = [0..1, 0..array_rows-1];
		testArray = new Array[double]([0..1, 0..array_rows-1]);  // Array of arrays.
	}

	/**
	 * Do.
	 *
	 * This consists of calculating the
	 * first n pairs of fourier coefficients of the function (x+1)^x on
	 * the interval 0,2. n is given by array_rows, the array size.
	 * NOTE: The # of integration steps is fixed at 1000.
	 */
	def Do(): void = {
		// Start the stopwatch.

		//JGFInstrumentor.startTimer("Section2:Series:Kernel");

		// Calculate the fourier series. Begin by calculating A[0].

		testArray(0, 0) = TrapezoidIntegrate(0.0D, // Lower bound.
				2.0,            // Upper bound.
				1000,                    // # of steps.
				0.0,            // No omega*n needed.
				0) / 2.0;       // 0 = term A[0].

		// Calculate the fundamental frequency.
		// (2 * pi) / period...and since the period
		// is 2, omega is simply pi.

		val omega: double = 3.1415926535897932;

		finish for ((i) in 1..array_rows-1) async {
			// Calculate A[i] terms. Note, once again, that we
			// can ignore the 2/period term outside the integral
			// since the period is 2 and the term cancels itself
			// out.

			testArray(0, i) = TrapezoidIntegrate(0.0,
					2.0,
					1000,
					omega * i,
					1);                       // 1 = cosine term.

			// Calculate the B[i] terms.

			testArray(1, i) = TrapezoidIntegrate(0.0,
					2.0,
					1000,
					omega * i,
					2);                       // 2 = sine term.
		}

		// Stop the stopwatch.

		//JGFInstrumentor.stopTimer("Section2:Series:Kernel");
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
	private def TrapezoidIntegrate(var x0: double, var x1: double, var nsteps: int, var omegan: double, var select: int): double = {
		var x: double;               // Independent variable.
		var dx: double;              // Step size.
		var rvalue: double;          // Return value.

		// Initialize independent variable.

		x = x0;

		// Calculate stepsize.

		dx = (x1 - x0) / nsteps;

		// Initialize the return value.

		rvalue = thefunction(x0, omegan, select) / 2.0;

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

		rvalue = (rvalue + thefunction(x1, omegan, select) / 2.0) * dx;
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
	private def thefunction(var x: double, var omegan: double, var select: int): double = {
		// Use select to pick which function we call.

		switch (select)
		{
			case 0: return (Math.pow(x+ 1.0, x));
			case 1: return (Math.pow(x+ 1.0, x) * Math.cos(omegan*x));
			case 2: return (Math.pow(x+ 1.0, x) * Math.sin(omegan*x));
		}

		// We should never reach this Point, but the following
		// keeps compilers from issuing a warning message.

		return (0.0);
	}

	/**
	 * freeTestData.
	 *
	 * Nulls array that is created with every run and forces garbage
	 * collection to free up memory.
	 */
	def freeTestData(): void = {
		//testArray = null;    // Destroy the array.
		//System.gc();         // Force garbage collection.
	}	
}


public class SeriesOrig extends SeriesTest {

	private var size: int;

	public def JGFsetsize(var size: int): void = {
		this.size = size;
	}

	public def JGFinitialise(): void = {
		switch (size) {
			case 0: array_rows = 1000; break;
			case 1: array_rows = 10000; break;
			case 2: array_rows = 100000; break;
			default: throw new Error();
		}
		buildTestData();
	}

	public def JGFkernel(): void = {
		Do();
	}

	public def JGFvalidate(): void = {
		var ref: ValRail[ValRail[double]] = [ [2.8729524964837996, 0.0 ] ,
						   [ 1.1161046676147888, -1.8819691893398025 ] ,
						   [ 0.34429060398168704, -1.1645642623320958 ] ,
						   [ 0.15238898702519288, -0.8143461113044298 ] ];
	
		for (var i: int = 0; i < 4; i++) {
			for (var j: int = 0; j < 2; j++) {
				var error: double = Math.abs(testArray(j, i) - ref(i)(j));
				if (error > 1.0e-12) {
					Console.OUT.println("Validation failed for coefficient " + j + "," + i);
					Console.OUT.println("Computed value = " + testArray(j, i));
					Console.OUT.println("Reference value = " + ref(i)(j));
					throw new Error("Validation failed");
				}
			}
		}
	}

	public def JGFtidyup(): void = {
		freeTestData();
	}

	public def JGFrun(var size: int): void = {
		//JGFInstrumentor.addTimer("Section2:Series:Kernel", "coefficients", size);
		JGFsetsize(size);
		JGFinitialise();
		JGFkernel();
		JGFvalidate();
		JGFtidyup();

		//JGFInstrumentor.addOpsToTimer("Section2:Series:Kernel",  (array_rows * 2 - 1));

		//JGFInstrumentor.printTimer("Section2:Series:Kernel");
	}
  
   public static def main(args:Rail[String]!)= {
			new SeriesOrig().JGFrun(0);
		
	}
}





