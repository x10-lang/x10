import java.util.*;
import x10.lang.Runtime.*;

value class ClockFFTDist {
    const int NUM_PLACES = place.MAX_PLACES;
    const dist(:unique) UNIQUE = dist.UNIQUE; //no cast
    const int FFTW_MEASURE = 0;
    public static extern void executedft(long plan, double[:rail] A, int i0, int i1);
    public static extern long fftw_plan_dft_1d(int SQRTN, double[:rail] A1, 
    		double[:rail] A2, int m1, int what);
    public static extern void start(int sqrtn, int n);
    public static extern double getPI();
    static { System.loadLibrary("fftDist"); } // load fftw library
    
    static void row_ffts(final int SQRT_N, final int N, final double[:rail] A, 
    		final long [.] fftw_plans, final clock c) {
      		ateach (point [p]: UNIQUE)  clocked(c)    
            executedft(fftw_plans[p], A.local(), 0, SQRT_N/NUM_PLACES);
            c.doNext();
    }
    static void bytwiddle(final int SQRTN, final int N, final double M_PI, 
    		final double[:rail] A, final double sign, final clock clk) {
        ateach(point [p]: UNIQUE) clocked(clk) {
            int numLocalRows = SQRTN/NUM_PLACES;
            region R2d = [p*numLocalRows:(p+1)*numLocalRows-1,0:SQRTN-1];
            double W_N = 2.0*M_PI/N;
            for (point [i,j]: R2d) {
                int ij =i*j, idx =2*(i*SQRTN+j);
                double ar = A[idx], ai = A[idx+1];
                double c = Math.cos(W_N*ij), s=Math.sin(W_N*ij)*sign;
                A[idx] = ar*c+ai*s;
                A[idx+1] = ai*c-ar*s;
            }
        }
        clk.doNext();
    }
    static void transpose(final int SQRTN, final int N, final double[:rail] Y, 
    		final double[:rail] Z, final clock c) {
         ateach(point [p]: UNIQUE) clocked (c) {
            final int numLocalRows = SQRTN/NUM_PLACES;
            int rowStartA = p*numLocalRows; // local transpose
            region block = [0:numLocalRows-1,0:numLocalRows-1];
            for (int k=0; k<NUM_PLACES; k++) { //for each block
                int colStartA = k*numLocalRows;
                for (int i=0; i<numLocalRows; i++)
                    for (int j=i; j<numLocalRows; j++) {
                        int idxA = 2*(SQRTN * (rowStartA + i) + colStartA + j),
                            idxB = 2*(SQRTN * (rowStartA + j) + colStartA + i);
                        double tmp0 = Y[idxA], tmp1 = Y[idxA+1];
                        Y[idxA] = Y[idxB]; Y[idxA+1] = Y[idxB+1];
                        Y[idxB] = tmp0; Y[idxB+1] = tmp1;
                    }
                for (int i=0; i<numLocalRows;i++) { // now copy
                    final int srcIdx = 2*((rowStartA + i)*SQRTN+colStartA),
                        destIdx = 2*(SQRTN * (colStartA + i) + rowStartA);
                    async (UNIQUE[k])
                        x10.lang.Runtime.arrayCopy(Y, srcIdx, Z, destIdx, 2*numLocalRows);
                }
            }
        }
        c.doNext();
    }
    static void check(final int SQRTN, final int N, final double[:rail] A0, 
		      final double[:rail] A, final clock c) {
	final double epsilon = 1.0e-15;
	final double threshold = epsilon*Math.log(N)/Math.log(2)*16;
       ateach(point [p]: UNIQUE) clocked(c)
            for (point [q]:A.distribution|here) 
                if (Math.abs(A[q]/N-A0[q])> threshold)
                    System.err.println("Error at "+q+" "+A[q]/N+" "+A0[q]);
       c.doNext();
    }
    static void solve(final int SQRT_N, final boolean reportTime, final clock c) {
        final int N = (SQRT_N * SQRT_N);
        final int numLocalRows = SQRT_N/NUM_PLACES;
        if (numLocalRows*NUM_PLACES != SQRT_N) {
            System.err.println("SQRT_N must be divisible by NUM_PLACES!");
            System.exit(-1);
        }
        final region(:rail) R = [0:2*N-1];
        final dist(:rail) D = dist.factory.block(R);
        final double[:rail] A = new double[D], B0 = new double[D], B = new double[D];
        final long [.] fftw_plans = new long [UNIQUE], fftw_inverse_plans = new long [UNIQUE];
        final double M_PI = getPI();
        final long[] tms = new long[7];
        ateach (point [p] : UNIQUE) clocked (c){
            fftw_plans[p] = fftw_plan_dft_1d(SQRT_N, A.local(), A.local(), -1, FFTW_MEASURE);
            fftw_inverse_plans[p] = fftw_plan_dft_1d(SQRT_N, A.local(), A.local(), 1, FFTW_MEASURE);
            final Random rnd=new Random();
            for (point [i]: (D|here).region) {
                A[i]= rnd.nextDouble()-0.5;
                B0[i]=A[i];
            }
            start(SQRT_N, N);
        }
    	c.doNext();
        tms[0]=System.nanoTime(); transpose(SQRT_N, N, A, B, c);
        tms[1]=System.nanoTime(); row_ffts(SQRT_N, N, B, fftw_plans, c);
        tms[2]=System.nanoTime(); transpose(SQRT_N, N, B, A, c);
        tms[3]=System.nanoTime(); bytwiddle(SQRT_N, N, M_PI, A, 1.0, c);
        tms[4]=System.nanoTime(); row_ffts(SQRT_N, N, A, fftw_plans, c);
        tms[5]=System.nanoTime(); transpose (SQRT_N, N, A, B, c);
        tms[6]=System.nanoTime(); // now starting inverse FFT for verification
        transpose(SQRT_N, N, B, A, c); 
        row_ffts(SQRT_N, N, A, fftw_inverse_plans, c);
        transpose(SQRT_N, N, A, B, c);
        bytwiddle(SQRT_N, N, M_PI, B, -1.0, c);
        row_ffts(SQRT_N, N, B, fftw_inverse_plans, c);
        transpose(SQRT_N, N, B, A, c);
        check(SQRT_N, N, B0, A, c);
        if (reportTime) {
            System.out.println("After verification");
            double secs = ((double)(tms[6] - tms[0])*1.0e-9);
            double Gigaflops = 1.0e-9*N*5*Math.log(N)/Math.log(2)/secs;
            double mbytes = N*2.0*8.0*2/(1024*1024);
            System.out.println("execution time = " + secs + " secs"+" Gigaflops = "+Gigaflops);
            System.out.println("SQRT_N = " + SQRT_N + ", p = " + place.MAX_PLACES);
            System.out.println("N = " + N + " N/place=" +N/place.MAX_PLACES);
            System.out.println("Mem = " + mbytes + " mem/place = " +mbytes/place.MAX_PLACES);
            String[] nms = new String[] { "transpose1", "row_ffts1", "transpose2",
                                          "twiddle", "row_ffts2",  "transpose3"};
            for (int i = 1; i < tms.length; ++i)
                System.out.println("Step " + nms[i-1] + " took " +
                                   ((double)(tms[i]-tms[i-1])*1.0e-9)+" s");
        }
    }
    static void printArray(final String name, final int SQRT_N, final double[:rail] A, final clock c) {
        System.err.println("Array "+name+":");
         ateach (point [p] : dist.UNIQUE) clocked (c) {
            for (point [q]:(A.distribution|here)) {
                final int i = q/SQRT_N/2;
                final int j = (q % (2*SQRT_N))/2;
                System.err.println(here.id+" ("+ i +"," + j + ") "+ q + ": " + A[q]);
            }
        }
        c.doNext();
    }
    public static void main(String[] args) {
        System.out.println("Warming up");
       async {
        	final clock c = clock.factory.clock();
        	solve(4, false, c); // warm up
        	System.gc();
        	System.out.println("Starting computation");
        	solve(256, true, c);
        	}
    }
}


