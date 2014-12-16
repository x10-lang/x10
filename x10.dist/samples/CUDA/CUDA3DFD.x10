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
import x10.compiler.CUDA;
import x10.compiler.CUDADirectParams;
import x10.compiler.NoInline;

public class CUDA3DFD {
    public static def init_data(data:Rail[Float], dimx:Int, dimy:Int, dimz:Int)
    {
        var off:Int = 0n;
        for(var iz:Int=0n; iz<dimz; iz++)
            for(var iy:Int=0n; iy<dimy; iy++)
                for(var ix:Int=0n; ix<dimx; ix++)
                {
                    data(off++) = iz as Float;
                }
    }

    public static def random_data(data:Rail[Float], dimx:Int, dimy:Int, dimz:Int, lower_bound:Int, upper_bound:Int)
    {
        val rnd = new Random(0n);

        var off:Int = 0n;
        for(var iz:Int=0n; iz<dimz; iz++)
            for(var iy:Int=0n; iy<dimy; iy++)
                for(var ix:Int=0n; ix<dimx; ix++)
                {
                    //data(off++) = (lower_bound + (rnd.nextInt() % (upper_bound - lower_bound))) as Float;
                    data(off++) = iz as Float; //(iy*dimx + ix) as Float;
                }
    }

    // note that this CPU implemenation is extremely naive and slow, NOT to be used for performance comparisons
    public static def reference_3D(output:Rail[Float], input:Rail[Float], coeff:Rail[Float], dimx:Int, dimy:Int, dimz:Int)
    { reference_3D(output, input, coeff, dimx, dimy, dimz, 4n); }
    
    public static def reference_3D(output:Rail[Float], input:Rail[Float], coeff:Rail[Float], dimx:Int, dimy:Int, dimz:Int, radius:Int)
    {
        val dimxy = dimx*dimy;

        var output_off:Int=0n;
        var input_off:Int=0n;
        for(var iz:Int=0n; iz<dimz; iz++)
        {
            for(var iy:Int=0n; iy<dimy; iy++)
            {
                for(var ix:Int=0n; ix<dimx; ix++)
                {
                    if( ix>=radius && ix<(dimx-radius) && iy>=radius && iy<(dimy-radius) && iz>=radius && iz<(dimz-radius) )
                    {
                        var valu:Float = input(input_off)*coeff(0n);

                        for(var ir:Int=1n; ir<=radius; ir++)
                        {
                            valu += coeff(ir) * (input(input_off+ir) + input(input_off-ir));                // horizontal
                            valu += coeff(ir) * (input(input_off+ir*dimx) + input(input_off-ir*dimx));        // vertical
                            valu += coeff(ir) * (input(input_off+ir*dimxy) + input(input_off-ir*dimxy));    // in front / behind
                        }

                        output(output_off) = valu;
                    }

                    ++output_off;
                    ++input_off;
                }
            }
        }
    }

    public static def within_epsilon(output:Rail[Float], reference:Rail[Float], dimx:Int, dimy:Int, dimz:Int)
    { return within_epsilon(output, reference, dimx, dimy, dimz, 4n); }
    public static def within_epsilon(output:Rail[Float], reference:Rail[Float], dimx:Int, dimy:Int, dimz:Int, radius:Int)
    { return within_epsilon(output, reference, dimx, dimy, dimz, radius, -1n); }
    public static def within_epsilon(output:Rail[Float], reference:Rail[Float], dimx:Int, dimy:Int, dimz:Int, radius:Int, zadjust:Int)
    { return within_epsilon(output, reference, dimx, dimy, dimz, radius, zadjust, 0.0001f); }
    public static def within_epsilon(output:Rail[Float], reference:Rail[Float], dimx:Int, dimy:Int, dimz:Int, radius:Int, zadjust:Int, delta:Float)
    {
        var retval:Boolean = true;

        var output_off:Int=0n;
        var ref_off:Int=0n;
        for(var iz:Int=0n; iz<dimz; iz++)
        {
            for(var iy:Int=0n; iy<dimy; iy++)
            {
                for(var ix:Int=0n; ix<dimx; ix++)
                {
                    if( ix>=radius && ix<(dimx-radius) && iy>=radius && iy<(dimy-radius) && iz>=radius && iz<(dimz-radius+zadjust) )
                    {
                        val difference = Math.abs( reference(ref_off) - output(output_off));

                        if( difference > delta )
                        {
                            retval = false;
                            Console.OUT.println(String.format(" ERROR: (%d,%d,%d)\t%.2f instead of %.2f", [ix,iy,iz, output(output_off), reference(ref_off)]));

                            return false;
                        }
                        //Console.OUT.println(ix+" "+iy+" "+difference);
                    }

                    ++output_off;
                    ++ref_off;
                }
            }
        }

        return retval;
    }


    public static def main (args:Rail[String]) {

        //cudaDeviceProp properties;

        //cudaGetDeviceProperties(&properties, device);
        //printf("3DFD running on: %s\n", properties.name);
        //if (properties.totalGlobalMem >= 1024*1024*1024) {
        //    printf("Total GPU Memory: %.4f GB\n", properties.totalGlobalMem/(1024.f*1024.f*1024.f) );
        //} else {
        //    printf("Total GPU Memory: %.4f MB\n", properties.totalGlobalMem/(1024.f*1024.f) );
        //}

        /////////////////////////////////////////////
        // process command-line arguments,
        // set execution parameters
        //
        var nreps:Int = 1n;              // number of time-steps, over which performance is averaged
        var check_correctness:Boolean = true;  // 1=check correcness, 0-don't.  Note that CPU code is very
                                    //   naive and not optimized, so many steps will take a 
                                    //   long time on CPU
        var pad:Int = 0n;
        var dimx_:Int = 480n+pad;
        var dimy_:Int = 480n;
        var dimz_:Int = 400n;
        
        //if( 2.2*nbytes > properties.totalGlobalMem )    // adjust the volume size if it exceeds available
        //{                                               // global memory (allowing for some memory use
        //                                                // by the driver
        //    dimz = properties.totalGlobalMem / (2.2*dimx*dimy*sizeof(float));
        //    nbytes= dimx*dimy*dimz*sizeof(float);
        //}

        if( args.size >= 3 )
        {
            dimx_   = Int.parse(args(0));
            dimy_   = Int.parse(args(1));
            dimz_   = Int.parse(args(2));
        }
        if( args.size >= 4)
            nreps = Int.parse(args(3));
        if( args.size >= 5)
            check_correctness = Boolean.parse(args(4));
        val dimx=dimx_; val dimy=dimy_; val dimz=dimz_;
        val nelements = dimx*dimy*dimz;

        Console.OUT.println(String.format("%dx%dx%d", [dimx as Any, dimy, dimz]));


        /////////////////////////////////////////////
        // setup data
        //
        
        // initialize data
        val h_data = new Rail[Float](nelements);
        val h_reference = new Rail[Float](nelements);
        random_data(h_data, dimx,dimy,dimz, 1n, 5n);

        // allocate CPU and GPU memory
        val topo = PlaceTopology.getTopology();
        val gpu = topo.numChildren(here) > 0 ? topo.getChild(here,0) : here;
        
        val d_input = CUDAUtilities.makeGlobalRail[Float](gpu,nelements,h_data); // allocate 
        val d_output = CUDAUtilities.makeGlobalRail[Float](gpu,nelements); // allocate 

        Console.OUT.println(String.format("allocated %.6f MB on device", [((2.f*nelements*4)/(1024.f*1024.f)) as Any]));

        val RADIUS = 4n;

        // setup coefficients
        val h_coeff_symmetric = new Rail[Float](RADIUS+1, 1.0f);

        // kernel launch configuration

        /////////////////////////////////////////////
        // kernel execution
        //
        var start_time : Long = System.currentTimeMillis();
        for(var i:Int=0n; i<nreps; i++) {
            val BLOCK_DIMX = 16n;
            val BLOCK_DIMY = BLOCK_DIMX;
            val THREADS = BLOCK_DIMX*BLOCK_DIMY;
            val BLOCKS_X = dimx/BLOCK_DIMX;
            val BLOCKS_Y = dimy/BLOCK_DIMY;
            val S_DATA_STRIDE = BLOCK_DIMX+2n*RADIUS;
            finish async at (gpu) @CUDA @CUDADirectParams {
                val c_coeff = CUDAConstantRail(h_coeff_symmetric);
                finish for (block in 0n..(BLOCKS_X*BLOCKS_Y-1n)) async {
                    val s_data = new Rail[Float]((BLOCK_DIMY+2n*RADIUS)*S_DATA_STRIDE, 0.0f);
                    clocked finish for (thread in 0n..(THREADS-1n)) clocked async {
                        val blockidx = block%BLOCKS_X;
                        val blockidy = block/BLOCKS_X;
                        val threadidx = thread%BLOCK_DIMX;
                        val threadidy = thread/BLOCK_DIMX;
                        val ix = blockidx * BLOCK_DIMX + threadidx;
                        val iy = blockidy * BLOCK_DIMY + threadidy;
                        var in_idx:Int=iy*dimx + ix;
                        var out_idx:Int = 0n;
                        val stride  = dimx*dimy;

                        var infront1:Float; var infront2:Float; var infront3:Float; var infront4:Float;
                        var behind1:Float; var behind2:Float; var behind3:Float; var behind4:Float;
                        var current:Float;

                        val tx = threadidx + RADIUS;
                        val ty = threadidy + RADIUS;

                        // fill the "in-front" and "behind" data
                        behind3  = d_input(in_idx);    in_idx += stride;
                        behind2  = d_input(in_idx);    in_idx += stride;
                        behind1  = d_input(in_idx);    in_idx += stride;

                        current  = d_input(in_idx);    out_idx = in_idx; in_idx += stride;

                        infront1 = d_input(in_idx);    in_idx += stride;
                        infront2 = d_input(in_idx);    in_idx += stride;
                        infront3 = d_input(in_idx);    in_idx += stride;
                        infront4 = d_input(in_idx);    in_idx += stride;

                        for(var j:Int=RADIUS; j<dimz-RADIUS; j++)
                        {
                            //////////////////////////////////////////
                            // advance the slice (move the thread-front)
                            behind4  = behind3;
                            behind3  = behind2;
                            behind2  = behind1;
                            behind1  = current;
                            current  = infront1;
                            infront1 = infront2;
                            infront2 = infront3;
                            infront3 = infront4;
                            infront4 = d_input(in_idx);

                            in_idx  += stride;
                            out_idx += stride;
                            Clock.advanceAll();

                            /////////////////////////////////////////
                            // update the data slice in smem

                            if(threadidy<RADIUS)    // halo above/below
                            {
                                s_data(threadidy*S_DATA_STRIDE + tx)                     = d_input(out_idx-RADIUS*dimx);
                                s_data((threadidy+BLOCK_DIMY+RADIUS)*S_DATA_STRIDE + tx) = d_input(out_idx+BLOCK_DIMY*dimx);
                            }

                            if(threadidx<RADIUS)    // halo left/right
                            {
                                s_data(ty*S_DATA_STRIDE + threadidx)                   = d_input(out_idx-RADIUS);
                                s_data(ty*S_DATA_STRIDE + threadidx+BLOCK_DIMX+RADIUS) = d_input(out_idx+BLOCK_DIMX);
                            }

                            // update the slice in smem
                            s_data((ty)*S_DATA_STRIDE + tx) = current;
                            Clock.advanceAll();

                            /////////////////////////////////////////
                            // compute the output value
                            var valu:Float  = @NoInline c_coeff(0) * current;
                            val sd1a = @NoInline s_data((ty-1)*S_DATA_STRIDE + tx);
                            val sd1b = @NoInline s_data((ty+1)*S_DATA_STRIDE + tx);
                            val sd1c = @NoInline s_data(ty*S_DATA_STRIDE + tx-1);
                            val sd1d = @NoInline s_data(ty*S_DATA_STRIDE + tx+1);
                            val sd2a = @NoInline s_data((ty-2)*S_DATA_STRIDE + tx);
                            val sd2b = @NoInline s_data((ty+2)*S_DATA_STRIDE + tx);
                            val sd2c = @NoInline s_data(ty*S_DATA_STRIDE + tx-2);
                            val sd2d = @NoInline s_data(ty*S_DATA_STRIDE + tx+2);
                            val sd3a = @NoInline s_data((ty-3)*S_DATA_STRIDE + tx);
                            val sd3b = @NoInline s_data((ty+3)*S_DATA_STRIDE + tx);
                            val sd3c = @NoInline s_data(ty*S_DATA_STRIDE + tx-3);
                            val sd3d = @NoInline s_data(ty*S_DATA_STRIDE + tx+3);
                            val sd4a = @NoInline s_data((ty-4)*S_DATA_STRIDE + tx);
                            val sd4b = @NoInline s_data((ty+4)*S_DATA_STRIDE + tx);
                            val sd4c = @NoInline s_data(ty*S_DATA_STRIDE + tx-4);
                            val sd4d = @NoInline s_data(ty*S_DATA_STRIDE + tx+4);
                            valu += @NoInline c_coeff(1)*( infront1 + behind1 + sd1a + sd1b + sd1c + sd1d );
                            valu += @NoInline c_coeff(2)*( infront2 + behind2 + sd2a + sd2b + sd2c + sd2d );
                            valu += @NoInline c_coeff(3)*( infront3 + behind3 + sd3a + sd3b + sd3c + sd3d );
                            valu += @NoInline c_coeff(4)*( infront4 + behind4 + sd4a + sd4b + sd4c + sd4d );
                            d_output(out_idx) = valu;
                        }
                    }
                }
            }
        }
        var elapsed_time_ms:Long = System.currentTimeMillis() - start_time;

        elapsed_time_ms /= nreps;
        val throughput_mpoints = (dimx*dimy*(dimz-2*RADIUS))/(elapsed_time_ms*1e3f);

        Console.OUT.println("-------------------------------");
        Console.OUT.println("time:       "+elapsed_time_ms+" ms");
        Console.OUT.println("throughput: "+throughput_mpoints+" MPoints/s");
        //printf("CUDA: %s\n", cudaGetErrorString(cudaGetLastError()) );


        /////////////////////////////////////////////
        // check the correctness
        //
        if( check_correctness)
        {
            Console.OUT.println("-------------------------------\n");
            Console.OUT.println("comparing to CPU result...\n");
            reference_3D( h_reference, h_data, h_coeff_symmetric, dimx,dimy,dimz, RADIUS );
            finish Rail.asyncCopy(d_output, 0, h_data, 0, nelements as Long);
            if( within_epsilon( h_data, h_reference, dimx,dimy,dimz, RADIUS*nreps, 0n ) ) 
            {
                Console.OUT.println("  Result within epsilon\n");
                Console.OUT.println("  TEST PASSED!\n");
            }
            else
            {
                Console.OUT.println("  Incorrect result\n");    
                Console.OUT.println("  TEST FAILED!\n");
            }
        }

        CUDAUtilities.deleteGlobalRail(d_input);
        CUDAUtilities.deleteGlobalRail(d_output);
    }

}

// vim: shiftwidth=4:tabstop=4:expandtab
