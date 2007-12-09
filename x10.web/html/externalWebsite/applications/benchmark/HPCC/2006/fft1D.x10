/**
(C) Copyright IBM Corp. 2006
**/
// x10c -BAD_PLACE_RUNTIME_CHECK=false fft.x10
// x10 -J-mx2000m -J-ms2000m -Djava.library.path=. -NUMBER_OF_LOCAL_PLACES=mmm -INIT_THREADS_PER_PLACE=1  -PRELOAD_CLASSES=true fft
// x10 -J-mx2000m -J-ms2000m -J-Xjit:count=0,optLevel=veryHot -Djava.library.path=. -NUMBER_OF_LOCAL_PLACES=1 -INIT_THREADS_PER_PLACE=1  -PRELOAD_CLASSES=true fft
import java.util.Random;

class fft1D {
    public static extern long fftw_plan_dft_1d(int n,
                                               double[:self.rect && self.zeroBased && self.rank==1] A_R,
                                               double[:self.rect && self.zeroBased && self.rank==1] A_I,
                                               int m1,
                                               int what);
    
    public static extern void execute_dft(long plan, double[:self.rect && self.zeroBased && self.rank==1] A_R, 
    		double[:self.rect && self.zeroBased && self.rank==1] A_I);
    
    //private static String libName = "xsupport";
    //    private static String libName = "xsupport_64";
    static {
        System.loadLibrary("xsupport");
    }
    public final static int FFTW_MEASURE = 0;
    private final int N;
    
    public static void main(String[] args) {
        new fft1D(4).solve();     // to get things compiled
    }

    

    /* transform row i, for i0 <= i < i1 */
    void row_ffts() {
         
    }

    
    
    void solve() {
        execute_dft(fftw_plan, Real, Img);
        for(point p:R) System.out.print(" ("+Real[p]+", "+Img[p]+")");
        System.out.flush();
    }
    fft1D(int N) {
       
        this. N = N;
        R = [0 : N - 1];
        cD = dist.factory.block([0:SQRTN-1]);
        Real = (double[:self.rect && self.zeroBased && self.rank==1]) new double[R];
        Img =  (double[:self.rect && self.zeroBased && self.rank==1]) new double[R];
        fftw_plan = fftw_plan_dft_1d(SQRTN, Real, Img, -1, FFTW_MEASURE);
        
        int intIndex = 0;
        for(point p: R){
        	Real[p]=1;
        	Img[p]=2;
        }

    }
    private region R;
    final double[:self.rect && self.zeroBased && self.rank==1] Real;
    final double[:self.rect && self.zeroBased && self.rank==1] Img;
    private long fftw_plan;
}
