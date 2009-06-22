/**
(C) Copyright IBM Corp. 2006
**/
// x10c -BAD_PLACE_RUNTIME_CHECK=false fft.x10
// x10 -J-mx2000m -J-ms2000m -Djava.library.path=. -NUMBER_OF_LOCAL_PLACES=mmm -INIT_THREADS_PER_PLACE=1  -PRELOAD_CLASSES=true fft
// x10 -J-mx2000m -J-ms2000m -J-Xjit:count=0,optLevel=veryHot -Djava.library.path=. -NUMBER_OF_LOCAL_PLACES=1 -INIT_THREADS_PER_PLACE=1  -PRELOAD_CLASSES=true fft
import java.util.Random;

class fft {
    public static extern long fftw_plan_dft_1d(int SQRTN,
                                               double[:self.rect && self.zeroBased && self.rank==1] A1,
                                               double[:self.rect && self.zeroBased && self.rank==1] A2,
                                               int m1,
                                               int what);
    public static extern void transpose_and_swap(double[:self.rect && self.zeroBased && self.rank==1] A, int topLefta_r, int topLefta_c, int topLeftb_r, int topLeftb_c, int bSize);
    public static extern void execute_dft(long plan, double[:self.rect && self.zeroBased && self.rank==1] A, int i0, int i1);
    public static extern void bytwiddle(double[:self.rect && self.zeroBased && self.rank==1] A, int i0, int i1, int j0, int j1, int N);
    public static extern void start(double[:self.rect && self.zeroBased && self.rank==1] A, int sqrtn, int n);
    public static extern void check(double[:self.rect && self.zeroBased && self.rank==1] A, int n);
    //private static String libName = "xsupport";
    //    private static String libName = "xsupport_64";
    static {
        System.loadLibrary("xsupport");
    }
    public final static int FFTW_MEASURE = 0;
    private final int SQRTN;
    private final int N;
    private final boolean reportTime;
    public static void main(String[] args) {
        new fft(4, 16, false).solve();     // to get things compiled
        System.gc();
        new fft(4096, 64, true).solve();   // to run the test
        //new fft(8192, 128, true).solve();   // to run the test
        //new fft(16384, 256, true).solve();   // to run the test
    }

    private final int BSIZE; //block size
    void transpose() {
        // SQRTN must e even power of 2
        final int bSize = (SQRTN > BSIZE) ? BSIZE : SQRTN;
        int nBlocks = SQRTN / bSize;
        int p = 0;
        finish for (int r = 0; r < nBlocks; ++r) {
            for (int c = r; c < nBlocks; ++c) {
                final int topLefta_r = (bSize * r);
                final int topLefta_c = (bSize * c);
                final int topLeftb_r = (bSize * c);
                final int topLeftb_c = (bSize * r);
                async (place.factory.place(p++)) transpose_and_swap(A, topLefta_r, topLefta_c, topLeftb_r, topLeftb_c, bSize);
            }
        }
    }

    /* transform row i, for i0 <= i < i1 */
    void row_ffts() {
        finish ateach(point p : dist.factory.unique()) {
            final region cR = (cD | here).region;
            if (cR.size() > 0) {
                execute_dft(fftw_plan, A, cR.rank(0).low(), cR.rank(0).high()+1);
            }
        }
    }

    /* multiply A[i][j] by exp(-2*pi*sqrt(-1)*i*j/N), 
       for i0 <= i < i1, j0 <= j < j1 */
    void bytwiddle() {
        finish ateach(point p : dist.factory.unique()) {
            final region cR = (cD | here).region;
            if (cR.size() > 0) {
                bytwiddle(A, cR.rank(0).low(), cR.rank(0).high()+1, 0, SQRTN, N);
            }
        }
    }
    
    void solve() {
        long[] tms = new long[7];
        start(A, SQRTN, N);
        startNanos = System.nanoTime();
        //tms[0] = startNanos;
        transpose();
        //tms[1] = System.nanoTime();
        row_ffts();
        //tms[2] = System.nanoTime();
        transpose();
        //tms[3] = System.nanoTime();
        bytwiddle();
        //tms[4] = System.nanoTime();
        row_ffts();
        //tms[5] = System.nanoTime();
        transpose();
        stopNanos = System.nanoTime();
        //tms[6] = stopNanos;
        check(A, SQRTN);
        if (reportTime) {
            System.out.println("execution time: " + ((double)(stopNanos - startNanos)*1.0e-9) + " s");
            String[] nms = new String[tms.length];
            nms[1] = "transpose1";
            nms[2] = "row_ffts1";
            nms[3] = "transpose2";
            nms[4] = "twiddle";
            nms[5] = "row_ffts2";
            nms[6] = "transpose3";
            //for (int i = 1; i < tms.length; ++i) {
            //    System.out.println("Step " + nms[i] + " took " +
            //                       ((double)(tms[i] - tms[i-1]) * 1.0e-9) + " s");
            //}
        }
    }
    fft(int SQRTN, int BSIZE, boolean reportTime) {
        this.SQRTN = SQRTN;
        this. N = (SQRTN * SQRTN);
        this.BSIZE = BSIZE;
        this.reportTime = reportTime;
        Random rnd = new Random();
        R = [0 : 2*N - 1];
        cD = dist.factory.block([0:SQRTN-1]);
        A = (double[:self.rect && self.zeroBased && self.rank==1]) new double[R];
        fftw_plan = fftw_plan_dft_1d(SQRTN, A, A, -1, FFTW_MEASURE);
        
        int intIndex = 0;
        for (int i = 0; i < SQRTN; ++i) {
            for (int j = 0; j < SQRTN; ++j) {
                A[intIndex++] = rnd.nextDouble() - .5;
                A[intIndex++] = rnd.nextDouble() - .5;
            }
        }

    }
    private region R;
    private dist cD;
    final double[:self.rect && self.zeroBased && self.rank==1] A;
    private long fftw_plan;
    private long startNanos;
    private long stopNanos;
}
