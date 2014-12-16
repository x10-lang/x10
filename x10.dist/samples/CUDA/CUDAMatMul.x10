/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import x10.io.Console;
import x10.util.Random;
import x10.util.CUDAUtilities;
import x10.compiler.*;

public class CUDAMatMul {

    //
    //  auxiliary routines
    //  
    static def fill (A:Rail[Float], N:Long, maxi:Int)
    {
        val r = new Random();
        for (j in 0n..(N-1n))
            A(j) = (r.nextInt(maxi*2n) - maxi) / (maxi + 1.0f);
    }

    static def diff (m:Long, n:Long, A:Rail[Float], lda:Long, B:Rail[Float], ldb:Long)
    {
        var err:Float = 0;
        for (j in 0..(n-1))
            for (i in 0..(m-1))
                err = Math.max( err, Math.abs( A(i+j*lda) - B(i+j*ldb) ) );
        return err;
    }

    static def ourSgemm (gpu:Place, transa:Char, transb:Char, m:Int, n:Int, k:Int, alpha:Float,
                         A:GlobalRail[Float]{home==gpu}, lda:Long,
                         B:GlobalRail[Float]{home==gpu}, ldb:Long, beta:Float,
                         C:GlobalRail[Float]{home==gpu}, ldc:Long)
    {
        assert transa == 'N' || transa == 'n' : "unsupported value of 'transa' in ourSgemm()";
        assert transb == 'N' || transb == 'n' || transb == 'T' || transb == 't' || transb == 'C' || transb == 'c' :
            "invalid value of 'transb' in ourSgemm()";
        assert (m%64) == 0 && (n%16) == 0: "unsupported dimensions of matrix C in ourSgemm()";

        //dim3 grid( m/64, n/16 ), threads( 16, 4 );
        if ( transb == 'N' || transb == 'n' )
        {
            assert (k%16) == 0 && k > 0 : "unsupported shared dimension in ourSgemm( 'N', 'N', ... )";
            //sgemmNN<<<grid, threads>>>( A, lda, B, ldb, C, ldc, k, alpha, beta );
            finish async at (gpu) @CUDA @CUDADirectParams {
                finish for (block in 0n..(((m*n)/64n/16n)-1n)) async {
                    val bs = new Rail[Float](16*17, 0.0f);
                    clocked finish for (thread in 0n..63n) clocked async {
                        val inx = thread % 16n;
                        val iny = thread / 16n;
                        val ibx = (block%(m/64n)) * 64n;
                        val iby = (block/(m/64n)) * 16n;
                        val id = inx + iny*16n;

                        var A_idx:Long = ibx + id;
                        var B_idx:Long = inx + ( iby + iny ) * ( ldb );
                        var C_idx:Long = ibx + id + ( iby * ldc );

                        val Blast_idx = B_idx + k;

                        @StackAllocate val c = @StackAllocate new Rail[Float](16);

                        do
                        {
                            Clock.advanceAll();

                            @Unroll(4) for (i in 0..3) {
                                bs(inx*17+iny+4*i) = B(B_idx + (4*i)*ldb);
                            }

                            Clock.advanceAll();

                            @Unroll(16) for (i in 0..15) {
                                @Unroll(16) for (j in 0..15) {
                                    c(j) = c(j) + A(A_idx + i*lda) * bs(i*17 + j);
                                }
                            }
                            
                            // 4096 159.220289008341069 GF/s in 0.8632 seconds

                            A_idx += 16*lda;
                            B_idx += 16;

                        } while ( B_idx < Blast_idx );

                        @Unroll(16) for (i in 0..15) {
                            C(C_idx + i*ldc) = alpha*c(i) + beta*C(C_idx + i*ldc);
                        }
                    }
                }
            }
        }
        else
        {
            assert (k%4) == 0 && k > 4 : "unsupported shared dimension in ourSgemm( 'N', 'T', ... )";
            //sgemmNT<<<grid, threads>>>( A, lda, B, ldb, C, ldc, k, alpha, beta );
        }
    }


    public static def main (args:Rail[String]) {

        val N = args.size >= 1 ? Long.parse(args(0)) : 4096;

        //
        //  init arrays
        //
        val topo = PlaceTopology.getTopology();
        val gpu = topo.numChildren(here)==0 ? here : topo.getChild(here, 0);

        val dA = CUDAUtilities.makeGlobalRail[Float](gpu, N*N);
        val dB = CUDAUtilities.makeGlobalRail[Float](gpu, N*N);
        val dC = CUDAUtilities.makeGlobalRail[Float](gpu, N*N);

        val A = new Rail[Float](N*N);
        val B = new Rail[Float](N*N);
        val C = new Rail[Float](N*N);

        fill( A, N*N, 31n );
        fill( B, N*N, 31n );
        fill( C, N*N, 31n );

        finish {
            Rail.asyncCopy(A, 0, dA, 0, N*N);
            Rail.asyncCopy(B, 0, dB, 0, N*N);
        }

        val cublas_result = new Rail[Float](N*N);
        val our_result = new Rail[Float](N*N);

        //
        //  bench square matrices
        //
        for (i in 0..0)
        {
            val transa = 'N';
            val transb = i!=0 ? 'T' : 'N';

            Console.OUT.println("\ntesting sgemm( '"+transa+"', '"+transb+"', n, n, n, ... )\n");

            val nb = 64;
            //for (var idim:Int = 1n; idim <= N/nb; idim = ((idim+1)*1.1) as Int)
            val idim = N/nb;
            {
                val dim = idim*nb;

                //
                //  set up the parameters
                //
                val m = dim;
                val n = dim;
                val k = dim;
                val lda = dim;
                val ldb = dim;
                val ldc = dim;
                val alpha = 1.0f;
                val beta = -1.0f;

                //
                // compute with our routine
                //
                //ourSgemm( transa, transb, m, n, k, alpha, dA, lda, dB, ldb, beta, dC, ldc );

                //
                //  bench our routine
                //
                var start_time : Long = System.currentTimeMillis();
                val iters = 10;
                finish for (iter in 0..(iters-1)) {
                    ourSgemm(gpu, transa, transb, m as Int, n as Int, k as Int, alpha, dA, lda as Int, dB, ldb as Int, beta, dC, ldc as Int);
                }
                val elapsed_time = (System.currentTimeMillis() - start_time)/1E3/iters;


                val our_gflops = 2.*m*n*k/elapsed_time/1e9;

                //
                //  report the results
                //
                Console.OUT.println(n+" "+our_gflops+" GF/s in "+elapsed_time+" seconds");
            }
        }

        CUDAUtilities.deleteGlobalRail(dA);
        CUDAUtilities.deleteGlobalRail(dB);
        CUDAUtilities.deleteGlobalRail(dC);
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab
