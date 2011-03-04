package FT;

import x10.compiler.Native;
import x10.compiler.Immediate;
import x10.lang.PlaceLocalHandle;
import x10.util.Pair;
import x10.compiler.*;

import util.Comm;

@NativeCPPInclude("ft_natives.h")
@NativeCPPOutputFile("hpccfft.h")
@NativeCPPOutputFile("wrapfftw.h")
@NativeCPPCompilationUnit("fft235.c")
@NativeCPPCompilationUnit("wrapfftw.c")
@NativeCPPCompilationUnit("zfft1d.c")
@NativeCPPCompilationUnit("ft_natives.cc")
class fft {
    @Native("c++", "execute_plan(#1, &(#2)->raw()[0], &(#3)->raw()[0], #4, #5, #6)")
    native static def execute_plan(plan:Long, A:Array[Double](1), B:Array[Double](1), SQRTN:Int, i0:Int, i1:Int):void;

    @Native("c++", "create_plan(#1, #2, #3)")
    native static def create_plan(SQRTN:Int, direction:Int, flags:Int):Long;

    static unique = Dist.makeUnique();

    static class Block {
        val A:Array[Double](1){rail};
        val B:Array[Double](1){rail};
        val C:Array[Double](1){rail};
        val Cs:PlaceLocalHandle[Array[Double](1){rail}];
        val D:Array[Double](1){rail};
        val I:Int;
        val nRows:Int;
        val SQRTN:Int;
        val N:Long;
        val fftwPlan:Long;
        val fftwInversePlan:Long;
        val world:Comm = Comm.WORLD();
        var nCopy:Int;
        var alltoall_timer:Long = 0;
        var twiddle_timer:Long = 0;
 
        def this(I:Int, nRows:Int, localSize:Int, N:Long, SQRTN:Int, verify:Boolean, Cs:PlaceLocalHandle[Array[Double](1){rail}]) {
            this.I = I; this.nRows = nRows; this.N = N; this.SQRTN = SQRTN; this.Cs = Cs;
            A = new Array[Double](localSize);
            B = new Array[Double](localSize);
            //C = Rail.make[Double](localSize);
             C = Cs();
            D = verify ? new Array[Double](localSize) : null;
            fftwPlan = create_plan(SQRTN, -1, 0);
            fftwInversePlan = create_plan(SQRTN, 1, 0);
            nCopy = 0;
        }

        def init(localSize:Int, verify:Boolean) {
            val rand = new Random(I);
            if (verify) {
                for (var i:Int=0; i<localSize; ++i) {
                    D(i) = A(i) = rand.nextDouble() - 0.5;
                }
            } else {
                for (var i:Int=0; i<localSize; ++i) {
                    A(i) = rand.nextDouble() - 0.5;
                }
            }
        }

        static def make(I:Int, nRows:Int, localSize:Int, N:Long, SQRTN:Int, verify:Boolean, Cs:PlaceLocalHandle[Array[Double](1){rail}]):Block {
            val block = new Block(I, nRows, localSize, N, SQRTN, verify, Cs);
            block.init(localSize, verify);
            /* finish */ ateach (p in unique) {} // initialize transport
            return block;
        }

        def rowFFTS(fwd:Boolean) {
            execute_plan(fwd?fftwPlan:fftwInversePlan, A, B, SQRTN, 0, nRows);
            world.barrier();
        }

        def bytwiddle(sign:Int) {
            twiddle_timer -= System.nanoTime();
            val W_N = 2.0 * Math.PI / N;
            for (var i:Int = 0; i < nRows; ++i) {
                for (var j:Int = 0; j < SQRTN; ++j) {
                    val idx = 2*(i*SQRTN+j);
                    val ar = A(idx);
                    val ai = A(idx+1);
                    val ij = (I*nRows+i) * j;
                    val c = Math.cos(W_N * ij);
                    val s = Math.sin(W_N * ij)*sign;
                    A(idx) = ar * c + ai * s;
                    A(idx+1) = ai * c - ar * s;
                }
            }
            twiddle_timer += System.nanoTime();
            world.barrier();
        }

        def check() {
            val epsilon = 1.0e-15;
            val threshold = epsilon*Math.log(N as double)/Math.log(2.0)*16;
            for (var q:Int=0; q<A.size; ++q) {
                if (Math.abs(A(q)-D(q)) > threshold) Console.ERR.println("Error at "+q+" "+A(q).toString()+" "+D(q).toString());
            }
            world.barrier();
        }

        def transpose() {           
            val n0 = Place.MAX_PLACES;
            val n1 = nRows;
            val n2 = SQRTN;           
            val chunkSize = 2 * nRows * nRows; 
            val dstIndex = I * chunkSize;    
            val FFTE_NBLK = 16;

            for (var k:Int = 0; k < n0; ++k) {
                for (var ii:Int = 0; ii < n1; ii += FFTE_NBLK) {
                    for (var jj:Int = k * nRows; jj < (k+1) * nRows; jj += FFTE_NBLK) {
                        
                        val tmin1 = ii + FFTE_NBLK < n1 ? ii + FFTE_NBLK : n1;
                        for (var i:Int = ii; i < tmin1; ++i) {
                            
                            val tmin2 = jj + FFTE_NBLK < n2 ? jj + FFTE_NBLK : n2;
                            for (var j:Int = jj; j < tmin2; ++j) {
                                B(2*(n1 * j + i)) = A(2*(n2 * i + j));
                                B(2*(n1 * j + i)+1) = A(2*(n2 * i + j)+1);
                            }
                        }
                    }
                }

               //x10.io.Console.OUT.println("here" + C.length +  " " + dstIndex);
                //B.copyTo(k * chunkSize, Place.places(k), Cs, dstIndex, chunkSize, ()=>{++nCopy;});
            }
            alltoall_timer -= System.nanoTime();
            world.alltoall(B, C, chunkSize);
            alltoall_timer += System.nanoTime();
            //await (nCopy == Place.MAX_PLACES);
            //nCopy = 0;
            //x10.io.Console.OUT.println("before barrier" + here.id);                   
            world.barrier();
            //x10.io.Console.OUT.println("after barrier" + here.id);                   
        }

        def scatter() {
            val n1 = Place.MAX_PLACES;
            val n2 = nRows;
            val FFTE_NBLK = 16;

            for (var k:Int = 0; k < n2; ++k) {

                for (var ii:Int = 0; ii < n1; ii += FFTE_NBLK) {
                    for (var jj:Int = 0; jj < n2; jj += FFTE_NBLK) {
                        
                        val tmin1 = ii + FFTE_NBLK < n1 ? ii + FFTE_NBLK : n1;
                        for (var i:Int = ii; i < tmin1; ++i) {
                            
                            val tmin2 = jj + FFTE_NBLK < n2 ? jj + FFTE_NBLK : n2;
                            for (var j:Int = jj; j < tmin2; ++j) {
                                A(2*(k * n2 * n1 + j + i * n2)) = C(2*(i * n2 * n2 + k * n2 + j));
                                A(2*(k * n2 * n1 + j + i * n2)+1) = C(2*(i * n2 * n2 + k * n2 + j)+1);
                            }
                        }
                    }                   
                }
            }
            world.barrier();
        }
    }

    static def transpose_A(FFT:PlaceLocalHandle[Block]) {
        /* finish  ateach ((p) in unique)*/  FFT().transpose();
        /* finish  ateach ((p) in unique) */ FFT().scatter();
    }

    static def bytwiddle_A(FFT:PlaceLocalHandle[Block], sign:Int) {
        /* finish  ateach ((p) in unique) */ FFT().bytwiddle(sign);
    }

    static def rowFFTS_A(FFT:PlaceLocalHandle[Block], fwd:Boolean) { 
        /* finish  ateach ((p) in unique)*/ FFT().rowFFTS(fwd);
    }

    static def format(t:Long) = (t as Double) * 1.0e-9;

    static def compute(FFT:PlaceLocalHandle[Block], fwd:Boolean, N:Long) {
        val timers = new Array[Long](7);
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
        if (here.id ==0) Console.OUT.println("execution time=" + secs + " secs" + " Gigaflops=" + Gigaflops);
        val steps = ["transpose1", "row_ffts1", "transpose2", "twiddle", "row_ffts2", "transpose3"];
        if (here.id==0) for (var i:Int = 0; i < steps.size; ++i) {
            Console.OUT.println("Step " + steps(i) + " took " + format(timers(i+1) - timers(i)) + " s");
        }
        
        return secs;        
    }
        
    static def check(FFT:PlaceLocalHandle[Block]) { 
        /* finish  ateach ((p) in unique)*/ FFT().check();
    }
    
    public static def main(args:Array[String](1)) {
        val M = (args.size > 0) ? Int.parseInt(args(0)) : 10;
        val verify = (args.size > 1) ? Boolean.parseBoolean(args(1)) : true;
        val SQRTN = 1 << M;
        val N = (SQRTN as Long) * (SQRTN as Long);
        val nRows = SQRTN / Place.MAX_PLACES;
        val localSize = 2 * SQRTN * nRows;
        val mbytes = N*2.0*8.0*2/(1024*1024);

        Console.OUT.println("M=" + M + " SQRTN=" + SQRTN + " N=" + N + " nRows=" + nRows +
            " localSize=" + localSize + " MAX_PLACES=" + Place.MAX_PLACES +
            " Mem=" + mbytes + " mem/MAX_PLACES=" +mbytes/Place.MAX_PLACES);

        if (nRows * Place.MAX_PLACES != SQRTN) {
            Console.ERR.println("SQRTN must be divisible by Place.MAX_PLACES!");
            return;
        }

        // Initialization
        val Cs = PlaceLocalHandle.make[Array[Double](1){rail}](unique, ()=>new Array[Double](localSize));
        val FFT = PlaceLocalHandle.make[Block](unique, ()=>Block.make(here.id, nRows, localSize, N, SQRTN, verify, Cs));

        finish ateach (p in unique) {
            // FFT
            if (unique(p).id==0) Console.OUT.println("Start FFT");
            var secs:Double = compute(FFT, true, N);
            if (unique(p).id==0) Console.OUT.println("alltoall: " + FFT().alltoall_timer/1e9 + " s");
            if (unique(p).id==0) Console.OUT.println("twiddle: " + FFT().twiddle_timer/1e9 + " s");
            if (unique(p).id==0) Console.OUT.println("FFT complete");

            // Reverse FFT
            if (unique(p).id==0) Console.OUT.println("Start reverse FFT");
            secs += compute(FFT, false, N);
            if (unique(p).id==0) Console.OUT.println("Reverse FFT complete");

            // Output
            if (unique(p).id==0) Console.OUT.println("Now combining forward and inverse FTT measurements");
            val Gigaflops = 2.0e-9*N*5*Math.log(N as double)/Math.log(2.0)/secs;
            if (unique(p).id==0) Console.OUT.println("execution time=" + secs + " secs"+" Gigaflops="+Gigaflops);

            // Verification
            if (verify) {        
                if (unique(p).id == 0) Console.OUT.println("Start verification");
                check(FFT);
                if (unique(p).id == 0) Console.OUT.println("Verification complete");
            }
        }
    }
}
