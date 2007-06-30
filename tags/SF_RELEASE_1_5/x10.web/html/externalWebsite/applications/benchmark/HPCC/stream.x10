/**
The x10 version of the original code (Stream) described in the following. The 
HPCC source code can be downloaded at
                 http://icl.cs.utk.edu/hpcc/software/index.html
(C) Copyright IBM Corp. 2006
**/

/*-----------------------------------------------------------------------*/
/* Program: Stream                                                       */
/* Revision: $Id: stream.x10,v 1.3 2007-04-10 20:32:11 ipeshansky Exp $ */
/* Original code developed by John D. McCalpin                           */
/* Programmers: John D. McCalpin                                         */
/*              Joe R. Zagar                                             */
/*                                                                       */
/* This program measures memory transfer rates in MB/s for simple        */
/* computational kernels coded in C.                                     */
/*-----------------------------------------------------------------------*/
/* Copyright 1991-2005: John D. McCalpin                                 */
/*-----------------------------------------------------------------------*/
/* License:                                                              */
/*  1. You are free to use this program and/or to redistribute           */
/*     this program.                                                     */
/*  2. You are free to modify this program for your own use,             */
/*     including commercial use, subject to the publication              */
/*     restrictions in item 3.                                           */
/*  3. You are free to publish results obtained from running this        */
/*     program, or from works that you derive from this program,         */
/*     with the following limitations:                                   */
/*     3a. In order to be referred to as "STREAM benchmark results",     */
/*         published results must be in conformance to the STREAM        */
/*         Run Rules, (briefly reviewed below) published at              */
/*         http://www.cs.virginia.edu/stream/ref.html                    */
/*         and incorporated herein by reference.                         */
/*         As the copyright holder, John McCalpin retains the            */
/*         right to determine conformity with the Run Rules.             */
/*     3b. Results based on modified source code or on runs not in       */
/*         accordance with the STREAM Run Rules must be clearly          */
/*         labelled whenever they are published.  Examples of            */
/*         proper labelling include:                                     */
/*         "tuned STREAM benchmark results"                              */
/*         "based on a variant of the STREAM benchmark code"             */
/*         Other comparable, clear and reasonable labelling is           */
/*         acceptable.                                                   */
/*     3c. Submission of results to the STREAM benchmark web site        */
/*         is encouraged, but not required.                              */
/*  4. Use of this program or creation of derived works based on this    */
/*     program constitutes acceptance of these licensing restrictions.   */
/*  5. Absolutely no warranty is expressed or implied.                   */
/*-----------------------------------------------------------------------*/

/* INSTRUCTIONS:
 *
 *	1) Stream requires a good bit of memory to run.  Adjust the
 *          value of 'N' (below) to give a 'timing calibration' of 
 *          at least 20 clock-ticks.  This will provide rate estimates
 *          that should be good to about 5% precision.
 */

class stream {
    
    //public final static int N = (1 << 26);
    public final static int N = 200000;
    public final static int NTIMES = 10;

/*
 *	3) Compile the code with full optimization.  Many compilers
 *	   generate unreasonably bad code before the optimizer tightens
 *	   things up.  If the results are unreasonably good, on the
 *	   other hand, the optimizer might be too smart for me!
 *
 *         Try compiling with:
 *               cc -O stream_omp.c -o stream_omp
 *
 *         This is known to work on Cray, SGI, IBM, and Sun machines.
 *
 *
 *	4) Mail the results to mccalpin@cs.virginia.edu
 *	   Be sure to include:
 *		a) computer hardware model number and software revision
 *		b) the compiler flags
 *		c) all of the output from the test case.
 * Thanks!
 *
 */

    private static final String HLINE = "-------------------------------------------------------------";

    static final region R = [0:N-1];
    static final dist D = dist.factory.block(R);
    static final double[:self.rect && self.zeroBased && self.rank==1] a = (double[:self.rect && self.zeroBased && self.rank==1]) new double[D];
    static final double[:self.rect && self.zeroBased && self.rank==1] b = (double[:self.rect && self.zeroBased && self.rank==1]) new double[D];
    static final double[:self.rect && self.zeroBased && self.rank==1] c = (double[:self.rect && self.zeroBased && self.rank==1]) new double[D];
    static final double[] avgtime = new double[4];
    static final double[] maxtime = new double[4];
    static final double[] mintime = new double[4];
    static {
        for (int i = 0; i < mintime.length; ++i) {
            mintime[i] = java.lang.Float.MAX_VALUE;
        }
    }
    static final String[] label = {
        "Copy:      ", "Scale:     ",    "Add:       ", "Triad:     "};

    private static final int sizeOfDouble = 8;
    static final double	bytes[] = new double[4];
    static {
        for (int i = 0; i < bytes.length; ++i) {
            if (i < 2) {
                bytes[i] = 2 * sizeOfDouble * N;
            } else {
                bytes[i] = 3 * sizeOfDouble * N;
            }
        }
    };

    public static void main(String[] args) {
        int			quantum;
        int			BytesPerWord;
        double  		t;
        final double  		scalar = 3.0;
        double times[][] = new double[4][NTIMES];

        /* --- SETUP --- determine precision and check timing --- */

        System.out.println(HLINE);
        System.out.println("STREAM version $Revision: 1.3 $");
        System.out.println(HLINE);
        BytesPerWord = sizeOfDouble;
        System.out.println("This system uses " + BytesPerWord + "bytes per DOUBLE PRECISION word.");

        System.out.println(HLINE);
        System.out.println("Array size = "+N);
        System.out.println("Total memory required = " + (3.0 * BytesPerWord) * ( (double) N / 1048576.0) + " MB.");
        System.out.println("Each test is run " + NTIMES + " times, but only");
        System.out.println("the *best* time for each is used.");

        System.out.println(HLINE);
        
        /* Get initial value for system clock. */
        for (int j=0; j<N; j++) {
            a[j] = 1.0;
            b[j] = 2.0;
            c[j] = 0.0;
	}

        System.out.println(HLINE);

        if  ( (quantum = checktick()) >= 1) 
            System.out.println("Your clock granularity/precision appears to be " + quantum + " microseconds.");
        else
            System.out.println("Your clock granularity appears to be " +
                               "less than one microsecond.");

        t = mysecond();

        for (int j = 0; j < N; j++)
            a[j] = 2.0E0 * a[j];
        t = 1.0E6 * (mysecond() - t);

        System.out.println("Each test below will take on the order" +
	" of " + t + " microseconds.");
        System.out.println("   (= "+(t/quantum)+" clock ticks)");
        System.out.println("Increase the size of the arrays if this shows that");
        System.out.println("you are not getting at least 20 clock ticks per test.");

        System.out.println(HLINE);

        System.out.println("WARNING -- The above is only a rough guideline.");
        System.out.println("For best results, please be sure you know the");
        System.out.println("precision of your system timer.");
        System.out.println(HLINE);
    
        /*	--- MAIN LOOP --- repeat test cases NTIMES times --- */

        for (int k=0; k<NTIMES; k++) {
            times[0][k] = mysecond();

            finish ateach(point p : dist.factory.unique()) {
		final region myR = (D | here).region;
                for (int j = myR.rank(0).low(); j <= myR.rank(0).high(); ++j) {
                    c[j] = a[j];
                }
            }
            times[0][k] = mysecond() - times[0][k];
            
            times[1][k] = mysecond();
            finish ateach(point p : dist.factory.unique()) {
		final region myR = (D | here).region;
                for (int j = myR.rank(0).low(); j <= myR.rank(0).high(); ++j) {
                    b[j] = scalar*c[j];
                }
            }
            times[1][k] = mysecond() - times[1][k];
            
            times[2][k] = mysecond();
            finish ateach(point p : dist.factory.unique()) {
		final region myR = (D | here).region;
                for (int j = myR.rank(0).low(); j <= myR.rank(0).high(); ++j) {
                    c[j] = a[j]+b[j];
                }
            }
            times[2][k] = mysecond() - times[2][k];
            
            times[3][k] = mysecond();
            finish ateach(point p : dist.factory.unique()) {
		final region myR = (D | here).region;
                for (int j = myR.rank(0).low(); j <= myR.rank(0).high(); ++j) {
                    a[j] = b[j]+scalar*c[j];
                }
            }
            times[3][k] = mysecond() - times[3][k];
        }

        /*	--- SUMMARY --- */

        for (int k=1; k<NTIMES; k++) {/* note -- skip first iteration */
            for (int j=0; j<4; j++) {
                avgtime[j] = avgtime[j] + times[j][k];
                mintime[j] = java.lang.Math.min(mintime[j], times[j][k]);
                maxtime[j] = java.lang.Math.max(maxtime[j], times[j][k]);
	    }
	}
    
        System.out.println("Function      Rate (MB/s)   Avg time     Min time     Max time");
        for (int j=0; j<4; j++) {
            avgtime[j] = avgtime[j]/(double)(NTIMES-1);

            System.out.println(label[j] + " " +
                               1.0E-06 * bytes[j]/mintime[j] + " " +
                               avgtime[j] + " " +
                               mintime[j] + " " +
                               maxtime[j]);
        }
        System.out.println(HLINE);

        /* --- Check Results --- */
        checkSTREAMresults();
        System.out.println(HLINE);

    }

    private final static int M = 20;

    static int checktick() { return 5; }
    static int xchecktick() {
        int		i, minDelta, Delta;
        double	t1, t2;
        double[] timesfound = new double[M];

        /*  Collect a sequence of M unique time values from the system. */
        
        for (i = 0; i < M; i++) {
            t1 = mysecond();
            while( ((t2=mysecond()) - t1) < 1.0E-6 )
                ;
            timesfound[i] = t1 = t2;
	}

        /*
         * Determine the minimum difference between these M values.
         * This result will be our estimate (in microseconds) for the
         * clock granularity.
         */

        minDelta = 1000000;
        for (i = 1; i < M; i++) {
            Delta = (int)( 1.0E6 * (timesfound[i]-timesfound[i-1]));
            minDelta = java.lang.Math.min(minDelta, java.lang.Math.max(Delta,0));
	}
        
        return(minDelta);
    }


    
    /* A gettimeofday routine to give access to the wall
       clock timer on most UNIX-like systems.  */

    static double mysecond() {
        return (double) ((double)(System.nanoTime() / 1000) * 1.e-6);
    }

    static void checkSTREAMresults () {
	double aj,bj,cj,scalar;
	double asum,bsum,csum;
	double epsilon;

        /* reproduce initialization */
	aj = 1.0;
	bj = 2.0;
	cj = 0.0;
        /* a[] is modified during timing check */
	aj = 2.0E0 * aj;
        /* now execute timing loop */
	scalar = 3.0;
	for (int k=0; k<NTIMES; k++) {
            cj = aj;
            bj = scalar*cj;
            cj = aj+bj;
            aj = bj+scalar*cj;
        }
	aj = aj * (double) (N);
	bj = bj * (double) (N);
	cj = cj * (double) (N);

	asum = 0.0;
	bsum = 0.0;
	csum = 0.0;
	for (int j=0; j<N; j++) {
		asum += a[j];
		bsum += b[j];
		csum += c[j];
	}
        //#ifdef VERBOSE
        //	System.out.println ("Results Comparison: ");
        //	System.out.println ("        Expected  : %f %f %f ",aj,bj,cj);
        //	System.out.println ("        Observed  : %f %f %f ",asum,bsum,csum);
        //#endif

	epsilon = 1.e-8;

	if (java.lang.Math.abs(aj-asum)/asum > epsilon) {
            System.out.println ("Failed Validation on array a[]");
            System.out.println ("        Expected  : " +aj);
            System.out.println ("        Observed  : " +asum);
	} else if (java.lang.Math.abs(bj-bsum)/bsum > epsilon) {
            System.out.println ("Failed Validation on array b[]");
            System.out.println ("        Expected  : " + bj);
            System.out.println ("        Observed  : " + bsum);
	} else if (java.lang.Math.abs(cj-csum)/csum > epsilon) {
            System.out.println ("Failed Validation on array c[]");
            System.out.println ("        Expected  : " + cj);
            System.out.println ("        Observed  : " + csum);
	} else {
            System.out.println ("Solution Validates");
	}
    }
}
