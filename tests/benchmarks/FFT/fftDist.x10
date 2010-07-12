import java.util.*;

/**
 * Optimized version of the ftStatic/fftDist.x10 (Fragmented version).  
 * Created : November 23 2007
 */

value class fftDist {
    
    /* constants for choosing the input to exchange */
    const int ARRAY_A = 0;
    const int ARRAY_B = 1;

    /* parameters for blocking */
    const int FFTE_NBLK = 16;
 
    const int NUM_PLACES = place.MAX_PLACES;
    const int FFTW_MEASURE = 0;
    const dist(:unique) UNIQUE = dist.UNIQUE;
    
    public static extern void executedft(long plan, double[:rail] A, int i0, int i1);
    public static extern long fftw_plan_dft_1d(int SQRTN, double[:rail] A1, double[:rail] A2, int m1, int what);
    public static extern void start(int sqrtn, long n);
    public static extern double getPI();
    
    static {	
	//System.loadLibrary("libfft.so");
	System.load("c:/FFTW/fftw-3.1.2-dll/libfftw3-3.dll"); //comment this out for AIX
	System.loadLibrary("fft");
    }
    
    static final class Block {
	double[:rail] A, B, B0;
	final long fftwPlan, fftwInversePlan;
	final int I, nRows;
	final int SQRTN;
	final long N;
	public Block(int I,  int nRows, int localSize, long N, int SQRTN) {
	    System.out.println("Making Block(I="+I+",nRows="+nRows+",localSize="+localSize+")");
	    this.nRows=nRows; this.SQRTN=SQRTN; this.N=N; this.I=I;
	    final Random rnd = new Random(I);
	    region(:rail) R = [0:localSize-1];
	    A= new double[R];
	    fftwPlan=fftw_plan_dft_1d(SQRTN, A, A, -1, FFTW_MEASURE);
	    fftwInversePlan=fftw_plan_dft_1d(SQRTN, A, A, 1, FFTW_MEASURE);
	    for (point [p] : A) { A[p] = rnd.nextDouble()-0.5;};
	    B0=new double[R] (point [p]) { return A[p];};
	    B=new double[R] (point [p]) {return A[p];};
	    start(SQRTN,N);
	}
	public String toString() { return "Block("+I+")";}
	void rowFFTS(boolean isA, boolean fwd) {
	    executedft(fwd?fftwPlan:fftwInversePlan, isA?A:B,0, nRows);
	}
	void bytwiddle(boolean isA, double M_PI, int sign) {
	    double[:rail] X = isA?A:B;
	    double W_N = 2.0 * M_PI / N;
	    for (int i=0; i < nRows; ++i) for (int j=0; j<SQRTN;++j) {
		int idx =(2*(i*SQRTN+j));
		double ar = X[idx];   //SUB(A, i, j)[0];
		double ai = X[idx+1]; //SUB(A, i, j)[1];
		// use global coordinate for the ij factor.
		int ij = (I*nRows+i) * j;
		double c = Math.cos(W_N * ij);
		double s = Math.sin(W_N * ij)*sign;
		X[idx] = ar * c + ai * s;
		X[idx+1] = ai * c - ar * s;
	    }
	}
	void check() {
	    double epsilon = 1.0e-15;
	    double threshold = epsilon*Math.log(N)/Math.log(2)*16;
	    for (point [q]:A) 
		if (Math.abs(A[q]/N-B0[q])> threshold)
		    System.err.println("Error at "+q+" "+A[q]/N+" "+B0[q]);
	}

	void checkEQ() {
	    double epsilon = 1.0e-15;
	    double threshold = epsilon*Math.log(N)/Math.log(2)*16;
	    for (point [q]:A) 
		if (Math.abs(A[q] - B0[q])> threshold)
		    System.err.println("Error at "+q+" "+A[q] +" "+B0[q] + "p: " + I);
	}
	

	/* Transposition before global exchange -- local operation 
	 * Y = transpose (X) 
         * Logical view : Y = n1 X n2, X =  n2 X n1, where
                          n1 = SQRTN / NUM_PLACES ; n2 = SQRTN.
         * Storage order : j, i
         */
	void transpose (final Block[:rail] FFT, final double[:rail] X, final double [:rail] Y) {	    
	    int n1 = nRows;
	    int n2 = SQRTN;	    
	
             /* Block Transposition - very effective */ 
	     for (int ii = 0; ii < n1; ii += FFTE_NBLK)
		for (int jj = 0; jj < n2; jj += FFTE_NBLK) {
		    
		    int tmin1 = ii + FFTE_NBLK < n1 ? ii + FFTE_NBLK : n1;
		    for (int i = ii; i < tmin1; ++i) {
			
		        int  tmin2 = jj + FFTE_NBLK < n2 ?  jj + FFTE_NBLK : n2;
			for (int j = jj; j < tmin2; ++j) {
			    Y [2*(n1 * j + i)] = X [2*(n2 * i + j)];
			    Y [2*(n1 * j + i) + 1] = X [2*(n2 * i + j) + 1];
			}
		    }
		}


	       /* for (int i = 0; i < n1; ++i) {
		for (int j = 0; j < n2; ++j) {
		    Y [2*(n1 * j + i)] = X [2*(n2 * i + j)];
		    Y [2*(n1 * j + i) + 1] = X [2*(n2 * i + j) + 1];
		}			
	      } */
	}
	
	/* Global exchange of pieces 
	 * Toggles the input (and output) between the array variables A and B
	 * if input = ARRAY_B  ==> A = exchange (B) 
	 * if input = ARRAY_A, ==> B = exchange (A)
         * Logical View : A = B  = 1 X LOCAL_SIZE
	 */
	void exchange (final Block[:rail] FFT, final int input) {
	    final double [:rail] Y = input == ARRAY_A ? A : B;
	    final int chunkSize = 2 * nRows * nRows;
	    /* TODO: Use Alltoall */
	    for (int k=0; k < NUM_PLACES;++k) {

		final int srcIndex =  k * chunkSize;
		final int dstIndex =  I * chunkSize;	

		final int kk=k;
		async (UNIQUE[k])
		    x10.lang.Runtime.arrayCopy (Y, srcIndex, 
						(input == ARRAY_A ? FFT[kk].B : FFT[kk].A), dstIndex, 
						chunkSize);
	    }			
	}

	/* Scatter after global exchange -- local operation
	 * Y = scatter (X) 
         * Logical view : X = p X n2 X n2, Y =  n2 X n2 X p
                          p = NUM_PLACES, n2 = SQRTN / NUM_PLACES 
         * Storage order : j, k, i                
         */
	void scatter (final Block[:rail] FFT, final double[:rail] X, final double [:rail] Y) {

            int n1 = NUM_PLACES;
            int n2 = SQRTN / NUM_PLACES;

            /* Blocked Transposition -- has NEGATIVE effect */
	    /* for (int k = 0; k < n2; ++k) {

	     for (int ii = 0; ii < n1; ii += FFTE_NBLK)
		for (int jj = 0; jj < n2; jj += FFTE_NBLK) {
		    
		    int tmin1 = ii + FFTE_NBLK < n1 ? ii + FFTE_NBLK : n1;
		    for (int i = ii; i < tmin1; ++i) {
			
		        int  tmin2 = jj + FFTE_NBLK < n2 ?  jj + FFTE_NBLK : n2;
			for (int j = jj; j < tmin2; ++j) {
				Y [2*(k * n2 * n1 + j + i * n2)] = X [2*(i * n2 * n2 + k * n2 + j)];
				Y [2*(k * n2 * n1 + j + i * n2)+1] = X [2*(i * n2 * n2 + k * n2 + j)+1];
		    	}
		    }			
	         }
            }*/

            int n = 0;	
	    for (int k = 0; k < n2; ++k) {
		for (int i = 0; i < n1; i++) {	
		    for (int j = 0; j < n2; j++) {
			Y [n++]=  X [2*(i * n2 * n2 + k * n2 + j)];
			Y [n++] = X [2*(i * n2 * n2 + k * n2 + j)+1];	
		    }
		}			
	    } 
	}
	
	void print(final char name) {
	    double[:rail] T = name=='A' ? A : name=='B' ? B : B0;
	    System.out.println(" place: " + here.id);
	    for (point [q]:T) {
		final int i = q/SQRTN/2, j = (q % (2*SQRTN))/2;
		if (j==0) System.out.println();
		System.out.print(" ("+ i +"," + j + ")=" + T[q] );
	    }
	    System.out.println();
	}		
    }
    static void rowFFTS_A(final Block[:rail] FFT, final boolean fwd) { 
	finish ateach (point [p]: UNIQUE) FFT[p].rowFFTS(true,fwd);
    }
    static void rowFFTS_B(final Block[:rail] FFT, final boolean fwd) { 
	finish ateach (point [p]: UNIQUE) FFT[p].rowFFTS(false, fwd);
    }
    static void bytwiddle_A(final Block[:rail] FFT, final double M_PI,  final int sign) {
	finish ateach(point [p]: UNIQUE) FFT[p].bytwiddle(true, M_PI, sign);
    }
    static void bytwiddle_B(final Block[:rail] FFT, final double M_PI,  final int sign) {
	finish ateach(point [p]: UNIQUE) FFT[p].bytwiddle(false, M_PI, sign);
    }

    static void transposeAB(final Block[:rail] FFT, final boolean instrumented, final long[] instr) { 
	if (instrumented) instr[0]+=System.nanoTime(); 
	finish ateach(point [p]: UNIQUE) FFT[p].transpose(FFT, FFT[p].A, FFT[p].B);
	if (instrumented) instr[1]+=System.nanoTime(); 
	finish ateach(point [p]: UNIQUE) FFT[p].exchange(FFT, ARRAY_B);
	if (instrumented) instr[2]+=System.nanoTime(); 
	finish ateach(point [p]: UNIQUE) FFT[p].scatter(FFT, FFT[p].A, FFT[p].B);
    	if (instrumented) instr[3]+=System.nanoTime(); 
    }

    static void transposeBA(final Block[:rail] FFT, final boolean instrumented, final long[] instr) { 
	if (instrumented) instr[0]+=System.nanoTime(); 
	finish ateach(point [p]: UNIQUE) FFT[p].transpose(FFT, FFT[p].B, FFT[p].A);
	if (instrumented) instr[1]+=System.nanoTime(); 
	finish ateach(point [p]: UNIQUE) FFT[p].exchange(FFT, ARRAY_A);
	if (instrumented) instr[2]+=System.nanoTime(); 
	finish ateach(point [p]: UNIQUE) FFT[p].scatter(FFT, FFT[p].B, FFT[p].A);
	if (instrumented) instr[3]+=System.nanoTime(); 
    }

    static void check(final Block[:rail] FFT) { 
	finish ateach(point [p]: UNIQUE) FFT[p].check();
    }
    static void checkEQ(final Block[:rail] FFT) { 
	finish ateach(point [p]: UNIQUE) FFT[p].checkEQ();
    }
    static void printArray(final Block[:rail] FFT, final char name) {
	System.out.println("Array "+name+":");
	for (point [p] : UNIQUE) finish async (UNIQUE[p]) FFT[p].print(name);
    }

    static void inverseFFT (final Block[:rail] FFT, final long[] tms) {

	final long[] instr = new long[4];
	final double M_PI = getPI();
	transposeBA(FFT, false, instr);
	rowFFTS_A(FFT,false);
	transposeAB(FFT, false, instr);
	bytwiddle_B(FFT, M_PI,  -1);
	rowFFTS_B(FFT,false);
	transposeBA(FFT,false,instr);
    }

    static void forwardFFT (final Block[:rail] FFT, final long[] tms) {

	final long[] instr = new long[4];
	for (int i = 0; i < 4; i++) instr[i] = 0;
	final double M_PI = getPI();
	tms[0]=System.nanoTime(); transposeAB(FFT, true, instr);
	tms[1]=System.nanoTime(); rowFFTS_B(FFT,true);
	tms[2]=System.nanoTime(); transposeBA(FFT, true, instr);
	tms[3]=System.nanoTime(); bytwiddle_A(FFT, M_PI, 1);
	tms[4]=System.nanoTime(); rowFFTS_A(FFT,true);
	tms[5]=System.nanoTime(); transposeAB (FFT, true, instr); 
	tms[6]=System.nanoTime(); 

	for (int i = 1; i < instr.length; ++i)
	    System.out.println("Instrumentation Step " + (i-1) + " took " +
			       ((double)(instr[i]-instr[i-1])*1.0e-9)+" s");
	
    }
	
    static void solve(final int SQRTN, final boolean reportTime) {

	final long N = (SQRTN * SQRTN);
	final int nRows =  SQRTN/NUM_PLACES;

	System.out.println("N="+N + " SQRTN=" + SQRTN + " nRows=" + nRows);
	if (nRows*NUM_PLACES != SQRTN) {
	    System.err.println("SQRTN must be divisible by NUM_PLACES!");
	    System.exit(-1);
	}

	final int localSize = (int) (2*N/NUM_PLACES);
	assert localSize >= 0 && nRows >=0;

	final Block[:rail] FFT = (Block[:rail]) new Block[dist.UNIQUE] (point [p]) { 
	    return new Block(p, nRows, localSize, N, SQRTN);
	};

	final long[] tms = new long[7];

	if (reportTime) System.out.println("Start timing ");

	//computation
	forwardFFT (FFT, tms);
			
	//verification 
	inverseFFT (FFT, tms);
	check(FFT);

	if (reportTime) {
	    System.out.println("After verification");
	    double secs = ((double)(tms[6] - tms[0])*1.0e-9);
	    double Gigaflops = 1.0e-9*N*5*Math.log(N)/Math.log(2)/secs;
	    double mbytes = N*2.0*8.0*2/(1024*1024);
	    System.out.println("execution time = " + secs + " secs"+" Gigaflops = "+Gigaflops);
	    System.out.println("SQRTN = " + SQRTN + ", p = " + place.MAX_PLACES);
	    System.out.println("N = " + N + " N/place=" +N/place.MAX_PLACES);
	    System.out.println("Mem = " + mbytes + " mem/place = " +mbytes/place.MAX_PLACES);
	    String[] nms = new String[] { "transpose1", "row_ffts1", "transpose2",
					  "twiddle", "row_ffts2",  "transpose3"};
	    for (int i = 1; i < tms.length; ++i)
		System.out.println("Step " + nms[i-1] + " took " +
				   ((double)(tms[i]-tms[i-1])*1.0e-9)+" s");
	}
    }

       public static void main(String[] args) {
	System.out.println("Warming up");
	//solve(256, false); // warm up, compile in x10JVM
	System.gc();
	System.out.println("Starting computation");
	solve(1024, true);
	}




    // DEBUG code
	
    /* public static void main(String[] a) {

				 
    final int SQRTN = 32;	
    System.out.println("SQRTN= " + SQRTN + ", NUM_PLACES="+ place.MAX_PLACES);
    final long N = (SQRTN * SQRTN);
    final int nRows =  SQRTN/NUM_PLACES;
    if (nRows*NUM_PLACES != SQRTN) {
    System.err.println("SQRTN must be divisible by NUM_PLACES!");
    System.exit(-1);
    }
    final int localSize = (int) (2*N/NUM_PLACES);
    assert localSize >= 0 && nRows >=0;
    final Block[:rail] FFT = (Block[:rail]) new Block[dist.UNIQUE] (point [p]) { 
    return new Block(p, nRows, localSize, N, SQRTN);
    };

    //final double M_PI = getPI();
    final long[] tms = new long[7];

    System.out.println("Starting...");
    //	printArray(FFT, 'A');
    transposeAB(FFT);
    transposeBA(FFT);
    //printArray(FFT, 'B');
    checkEQ(FFT);
    System.out.println("Done");
    }*/
}

// Local Variables:
// mode: Java
// End:
