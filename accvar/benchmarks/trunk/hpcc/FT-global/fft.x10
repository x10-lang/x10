package FT;

import x10.compiler.Native;
import x10.lang.Complex;
import x10.util.Pair;
import x10.compiler.*;

class Random {
	@Native("c++", "srandom(#1)")
	static native def srandom(seed:Int):void;

	@Native("c++", "random()")
	native def random():Long;
	
	def this(I:Int) {
		srandom(I);
	}
	
	def nextDouble() = random() / ((1L<<31) as Double);
}


@NativeCPPInclude("ft_natives.h")
@NativeCPPOutputFile("hpccfft.h")
@NativeCPPOutputFile("wrapfftw.h")
@NativeCPPCompilationUnit("fft235.c")
@NativeCPPCompilationUnit("wrapfftw.c")
@NativeCPPCompilationUnit("zfft1d.c")
@NativeCPPCompilationUnit("ft_natives.cc")
public final class fft {
    @Native("c++", "::cos(#1)")
    static native def nativeCos(x:Double):Double;

    @Native("c++", "::sin(#1)")
    static native def nativeSin(x:Double):Double;
 
    @Native("c++", "execute_plan(#1, (double*) (#2)->raw(), (double*) (#3)->raw(), #4, #5, #6)")
    native static def execute_plan(plan:Long, A:Rail[Complex]!, B:Rail[Complex]!, SQRTN:Int, i0:Int, i1:Int):void;

    @Native("c++", "create_plan(#1, #2, #3)")
    native static def create_plan(SQRTN:Int, direction:Int, flags:Int):Long;

final static class FFTPlans {
        val fftwPlan:Long;
        val fftwInversePlan:Long;
        def this(sqrtn: Int) {
          fftwPlan = create_plan(sqrtn, -1, 0);
          fftwInversePlan = create_plan(sqrtn, 1, 0);
        }
}

    const unique = Dist.makeUnique();

        global val A:HPL_Dist[Complex]!;
        global val B:HPL_Dist[Complex]!;
        global val C:HPL_Dist[Complex]!;
        global val D:HPL_Dist[Complex]!;
        global val nRows: Int;
        global val nCols: Int;
        global val N: Long;
        global val plans: PlaceLocalHandle[FFTPlans];

        def this(N: Long, sqrtn: Int, verify:Boolean) {
            val nb = sqrtn / Place.MAX_PLACES;
            val py = 1;
            val px = Place.MAX_PLACES;
            A = new HPL_Dist[Complex](sqrtn, sqrtn, nb, sqrtn, px, py, unique);
	    // The cast of null is to workaround XTENLANG-1204 (and related type inference problems with ternary operator)
            D = verify ? new HPL_Dist[Complex](sqrtn, sqrtn, nb, sqrtn, px, py, unique) : (null as HPL_Dist[Complex]!);

            /* intermediate arrays => transposed layout of A */
            B = new HPL_Dist[Complex](sqrtn, sqrtn, sqrtn, nb, py, px, unique);
            C = new HPL_Dist[Complex](sqrtn, sqrtn, sqrtn, nb, py, px, unique);

            this.N = N;
            nRows = sqrtn/px;
            nCols = sqrtn;
            plans = PlaceLocalHandle.make[FFTPlans](unique, ()=>new FFTPlans(sqrtn));
        }

        global def init(verify:Boolean) {
            val rand = new Random(here.id);
            val A_lcl = A.blockFullHere().block(here.id,0);
            if (verify) {
               val D_lcl = (D.blockFullHere()).block(here.id,0) ;
                for ((i, j): Point in [A_lcl.min_x..A_lcl.max_x, A_lcl.min_y..A_lcl.max_y]) {
                  A_lcl(i, j)  = Complex(rand.nextDouble() - 0.5, rand.nextDouble() - 0.5);
                  D_lcl(i, j) = A_lcl(i,j);
                }
            } else {
                for ((i, j): Point in [A_lcl.min_x..A_lcl.max_x, A_lcl.min_y..A_lcl.max_y]) {
                  A_lcl(i,j) = Complex(rand.nextDouble() - 0.5, rand.nextDouble() - 0.5);
                }
            }
        }

        static def make(N: Long, sqrtn: Int, verify:Boolean): fft! {
            val FFT = new fft(N, sqrtn, verify);
        	//FFT.init(verify);
        	finish ateach (p in unique) {FFT.init(verify);} // initialize transport
        	return  FFT;
        }

        global def rowFFTS(fwd:Boolean) {
            val A_raw = (A.blockFullHere()).block(here.id,0).raw;
            val B_raw = (B.blockFullHere()).block(0, here.id).raw;
            execute_plan(fwd?plans().fftwPlan:plans().fftwInversePlan, A_raw, B_raw, nCols, 0, nRows);
        }

        global def bytwiddle(sign:Int) {
            val A_lcl = (A.blockFullHere()).block(here.id,0);
            val W_N = 2.0 * Math.PI / N;
            for ((i, j): Point in [A_lcl.min_x..A_lcl.max_x, A_lcl.min_y..A_lcl.max_y]) {
		            val ar = A_lcl(i, j).re;
		            val ai = A_lcl(i,j).im;
		            val ij = i*j;
		            val c = nativeCos(W_N * ij);
		            val s = nativeSin(W_N * ij)*sign;
		            A_lcl(i, j) = Complex(ar * c + ai * s,  ai * c - ar * s);
            }
        }

        global def check() {
            val epsilon = 1.0e-15;
            val threshold = epsilon*Math.log(N as double)/Math.log(2.0)*16;
            val A_lcl = (A.blockFullHere()).block(here.id,0);
            val D_lcl = (D.blockFullHere()).block(here.id,0);
            for ((i, j): Point in[A_lcl.min_x..A_lcl.max_x, A_lcl.min_y..A_lcl.max_y]) {
            	if (Math.abs(A_lcl(i, j).re-D_lcl(i, j).re) > threshold || Math.abs(A_lcl(i, j).im-D_lcl(i, j).im) > threshold) 
                   Console.ERR.println("Error at "+i+" " + j+" "+A_lcl(i, j)+" "+D_lcl(i, j) );
            }
        }

        global def transpose() {
            val A_lcl = (A.blockFullHere()).block(here.id,0);
            val B_lcl = (B.blockFullHere()).block(0,here.id);
            val n0 = Place.MAX_PLACES;
            val n1 = A_lcl.max_x+1;
            val chunkSize =  nRows * nRows; 
            val dstIndex = here.id * chunkSize;    
            val FFTE_NBLK = 16;

            for ((k): Point in [0..(n0-1)]) {
                for (var ii:Int = A_lcl.min_x; ii < n1  ; ii += FFTE_NBLK) {
                    for (var jj:Int = A_lcl.min_y + k * nRows; jj < A_lcl.min_y + (k+1) * nRows; jj += FFTE_NBLK) {
                       
                        val tmin1 = ii + FFTE_NBLK < n1 ? ii + FFTE_NBLK : n1;
                        for (var i:Int = ii; i < tmin1; ++i) {
                        
                            val n2 = A_lcl.min_y + (k+1)*nRows;    
                            val tmin2 = jj + FFTE_NBLK < n2 ? jj + FFTE_NBLK : n2;
                            for (var j:Int = jj; j < tmin2; ++j) {
                                B_lcl(j,i) = A_lcl(i,j);
                            }
                        }
                    }
                }


                val srcIndex = k * chunkSize;
                val finder = ()=>Pair[Rail[Complex],Int](C.blockFullHere().block(0, here.id).raw, dstIndex);
                B_lcl.raw.copyTo(srcIndex, Place.places(k), finder, chunkSize);
                Runtime.dealloc(finder);
            }                   
        }

	global def scatter() {
            val FFTE_NBLK = 16;
            val A_lcl = (A.blockFullHere()).block(here.id, 0);
            val C_lcl = (C.blockFullHere()).block(0, here.id);
            val n0 = Place.MAX_PLACES;
            val n2 = C_lcl.max_y+1;
            for ((k): Point in [0..(n0-1)]) {
		        for (var ii:Int = C_lcl.min_x + k*nRows; ii < C_lcl.min_x + (k+1)*nRows; ii += FFTE_NBLK){
		            for (var jj:Int = C_lcl.min_y; jj < C_lcl.max_y+1; jj += FFTE_NBLK){

                               val n1 = C_lcl.min_x + (k+1) * nRows;
                                val tmin1 = ii + FFTE_NBLK < n1 ? ii + FFTE_NBLK : n1;
                               for (var i: Int = ii; i <tmin1; i++) {

                                  val tmin2 = jj + FFTE_NBLK < n2 ? jj + FFTE_NBLK : n2;
                                  for (var j: Int = jj; j <tmin2; j++) { 
		                        A_lcl(A_lcl.min_x+(i%nRows), k*nRows+(j%nRows)) = C_lcl(i,  j);
		                    }}
                        }
		}
            }
        }

    static def transpose_A(FFT: fft!){
        finish ateach (p in unique) FFT.transpose();
        finish ateach (p in unique) FFT.scatter();
    }

    static def bytwiddle_A(FFT: fft!, sign: Int) {
        finish ateach (p in unique) FFT.bytwiddle(sign);
    }

    static def rowFFTS_A(FFT: fft!, fwd: Boolean){
        finish ateach (p in unique) FFT.rowFFTS(fwd);
    }

    static def check(FFT: fft!){
        finish ateach (p in unique) FFT.check();
    }

    static def format(t:Long) = (t as Double) * 1.0e-9;

    static def compute(FFT: fft!, fwd:Boolean, N:Long) {
        val steps = Rail.make[String](6);
        steps(0) = "transpose1"; steps(1) = "row_ffts1"; steps(2) = "transpose2";
        steps(3) = "twiddle"; steps(4) = "row_ffts2"; steps(5) = "transpose3";
        val timers = Rail.make[Long](7);
        timers(0)=System.nanoTime(); transpose_A(FFT);
        timers(1)=System.nanoTime(); rowFFTS_A(FFT, fwd);
        timers(2)=System.nanoTime(); transpose_A(FFT);
        timers(3)=System.nanoTime(); bytwiddle_A(FFT, fwd ? 1 : -1);
        timers(4)=System.nanoTime(); rowFFTS_A(FFT, fwd);
        timers(5)=System.nanoTime(); transpose_A(FFT);
        timers(6)=System.nanoTime(); 

		// Output
        val secs = format(timers(6) - timers(0));
        val Gigaflops = 1.0e-9*N*5*Math.log(N as double)/Math.log(2.0)/secs;
        Console.OUT.println("execution time=" + secs + " secs" + " Gigaflops=" + Gigaflops);
        for (var i:Int = 0; i < steps.length; ++i) {
            Console.OUT.println("Step " + steps(i) + " took " + format(timers(i+1) - timers(i)) + " s");
        }
        
        return secs;        
    }
        
    
    public static def main(args:Rail[String]!) {here==Place.FIRST_PLACE} {
        val M = (args.length > 0) ? Int.parseInt(args(0)) : 10;
        val verify = (args.length > 1) ? Boolean.parseBoolean(args(1)) : true;
        val SQRTN = 1 << M;
        val N = (SQRTN as Long) * (SQRTN as Long);
        val nRows = SQRTN / Place.MAX_PLACES;
        val localSize = SQRTN * nRows;
        val mbytes = N*2.0*8.0*2/(1024*1024);

        Console.OUT.println("M=" + M + " SQRTN=" + SQRTN + " N=" + N + " nRows=" + nRows +
        	" localSize=" + localSize + " MAX_PLACES=" + Place.MAX_PLACES +
        	" Mem=" + mbytes + " mem/MAX_PLACES=" +mbytes/Place.MAX_PLACES);

        if (nRows * Place.MAX_PLACES != SQRTN) {
            Console.ERR.println("SQRTN must be divisible by Place.MAX_PLACES!");
            System.setExitCode(1);
            return;
        }

		// Initialization
        val FFT = fft.make(N, SQRTN, verify);

		// FFT
        Console.OUT.println("Start FFT");
        var secs:Double = compute(FFT, true, N);
        Console.OUT.println("FFT complete");

		// Reverse FFT
        Console.OUT.println("Start reverse FFT");
        secs += compute(FFT, false, N);
        Console.OUT.println("Reverse FFT complete");

		// Output
        Console.OUT.println("Now combining forward and inverse FTT measurements");
        val Gigaflops = 2.0e-9*N*5*Math.log(N as double)/Math.log(2.0)/secs;
        Console.OUT.println("execution time=" + secs + " secs"+" Gigaflops="+Gigaflops);

        // Verification
        if (verify) {        
	        Console.OUT.println("Start verification");
	        check(FFT);
	        Console.OUT.println("Verification complete");
		}
    }
}
