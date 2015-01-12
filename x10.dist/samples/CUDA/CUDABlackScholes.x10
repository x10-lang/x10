/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import x10.io.Console;
import x10.compiler.CUDA;
import x10.compiler.CUDADirectParams;
import x10.util.CUDAUtilities;
import x10.compiler.Native;
import x10.util.Random;

/*
 * This sample evaluates fair call and put prices for a
 * given set of European options by Black-Scholes formula.
 *
 * This program is inteneded to run on NVIDIA GPU accelerators
 */

public class CUDABlackScholes {

    static def doBlackScholes(p:Place, 
            optionYears:GlobalRail[Float]{home==p},
            stockPrice:GlobalRail[Float]{home==p},
            optionStrike:GlobalRail[Float]{home==p},
            callResult:GlobalRail[Float]{home==p},
            putResult:GlobalRail[Float]{home==p},
            opt_N:Long,
            R:Float,
            V:Float) {
        val blocks = p.isCUDA() ? 480n : 1n;
        val threads = 128n;
        finish async at (p) @CUDA @CUDADirectParams {
            //val blocks = CUDAUtilities.autoBlocks(),
            //    threads = CUDAUtilities.autoThreads();
            finish for (block in 0n..(blocks-1n)) async {
                clocked finish for (thread in 0n..(threads-1n)) clocked async {
                    val tid = block * threads + thread;
                    val tids = blocks * threads;
                    for (var opt:Long=tid; opt < opt_N; opt+=tids) {
                        // Constants for Polynomial approximation of cumulative normal distribution
                        val A1 = 0.31938153f;
                        val A2 = -0.356563782f;
                        val A3 = 1.781477937f;
                        val A4 = -1.821255978f;
                        val A5 = 1.330274429f;
                        val RSQRT2PI = 0.39894228040143267793994605993438f;

                        val T = optionYears(opt);
                        val S = stockPrice(opt);
                        val X = optionStrike(opt);
                        val sqrtT = Math.sqrtf(T);
                        val d1 = (Math.logf(S/X) + (R + 0.5f * V * V) * T) / (V * sqrtT); 
                        val d2 = d1 - V * sqrtT;

                        val K1 = 1.0f / (1.0f + 0.2316419f * Math.abs(d1));
                        val K2 = 1.0f / (1.0f + 0.2316419f * Math.abs(d2));
                        var CNDD1:Float = RSQRT2PI * Math.expf(- 0.5f * d1 * d1) * 
                            (K1 * (A1 + K1 * (A2 + K1 * (A3 + K1 * (A4 + K1 * A5)))));
                        var CNDD2:Float = RSQRT2PI * Math.expf(- 0.5f * d2 * d2) * 
                            (K2 * (A1 + K2 * (A2 + K2 * (A3 + K2 * (A4 + K2 * A5)))));

                        if(d1 > 0) CNDD1 = 1.0f - CNDD1;
                        if(d2 > 0) CNDD2 = 1.0f - CNDD2;

                        //Calculate Call and Put simultaneously
                        val expRT = Math.expf(- R * T); 
                        callResult(opt) = S * CNDD1 - X * expRT * CNDD2;
                        putResult(opt)  = X * expRT * (1.0f - CNDD2) - S * (1.0f - CNDD1); 
                    }
                }
            }
        }
    }

    public static def main (Rail[String]) {

        // Problem parameters
        val OPT_N = 4000000l;
        val RISKFREE = 0.02f;
        val VOLATILITY = 0.30f;

        val topo = PlaceTopology.getTopology();
	val gpu:Place;
        if (topo.numChildren(here)==0) {
            Console.OUT.println("Set X10RT_ACCELS=ALL to enable your GPUs if you have them.");
            Console.OUT.println("Will run the test on the CPU.");
            gpu = here;
        } else {
	    gpu = topo.getChild(here, 0);
            Console.OUT.println("Using the GPU at place "+gpu);
            Console.OUT.println("This program only supports a single GPU.");
        }

        val NUM_ITERATIONS = gpu==here ? 32 : 512;
        val cpu = here;
        val rand = new Random();

        // Host arrays
        val h_CallResultCPU = new Rail[Float](OPT_N);
        val h_PutResultCPU  = new Rail[Float](OPT_N, -1.0f);
        val h_CallResultGPU = new Rail[Float](OPT_N);
        val h_PutResultGPU  = new Rail[Float](OPT_N);
        val h_StockPrice    = new Rail[Float](OPT_N, (Long)=>rand.nextFloat());
        val h_OptionStrike  = new Rail[Float](OPT_N, (Long)=>rand.nextFloat());
        val h_OptionYears   = new Rail[Float](OPT_N, (Long)=>rand.nextFloat());

        // Device arrays
        val d_CallResult    = CUDAUtilities.makeGlobalRail[Float](gpu, OPT_N);
        val d_PutResult     = CUDAUtilities.makeGlobalRail[Float](gpu, OPT_N);
        val d_StockPrice    = CUDAUtilities.makeGlobalRail[Float](gpu, OPT_N, h_StockPrice);
        val d_OptionStrike  = CUDAUtilities.makeGlobalRail[Float](gpu, OPT_N, h_OptionStrike);
        val d_OptionYears   = CUDAUtilities.makeGlobalRail[Float](gpu, OPT_N, h_OptionYears);

        Console.OUT.println("Running " + NUM_ITERATIONS + " times on place " + gpu);
        val gpuTimeStart = System.nanoTime();
        for (var i:Int=0n; i < NUM_ITERATIONS; i++) {
            doBlackScholes(gpu, 
                    d_OptionYears,
                    d_StockPrice,
                    d_OptionStrike,
                    d_CallResult,
                    d_PutResult,
                    OPT_N,
                    RISKFREE,
                    VOLATILITY);
        }
        var gpuTime:Float = System.nanoTime() - gpuTimeStart;
        gpuTime /= (NUM_ITERATIONS as Float);
        Console.OUT.println("Options count             : " + 2 * OPT_N);
        Console.OUT.println("BlackScholesGPU() time    : " + gpuTime/(1.0e-6f) + " msec");
        Console.OUT.println("Effective memory bandwidth: " + (5 * OPT_N * 4) * 1.0e-9f / (gpuTime * 1.0E-9f) + " GB/s");
        Console.OUT.println("Gigaoptions per second    : " + ((2 * OPT_N) * 1.0e-9f) / (gpuTime * 1.0e-9f));

        // Read back GPU results
        finish {
            Rail.asyncCopy(d_CallResult, 0, h_CallResultGPU, 0, OPT_N);
            Rail.asyncCopy(d_PutResult, 0, h_PutResultGPU, 0, OPT_N);
        }

        CUDAUtilities.deleteGlobalRail(d_CallResult);
        CUDAUtilities.deleteGlobalRail(d_PutResult);
        CUDAUtilities.deleteGlobalRail(d_StockPrice);
        CUDAUtilities.deleteGlobalRail(d_OptionStrike);
        CUDAUtilities.deleteGlobalRail(d_OptionYears);

        Console.OUT.println("Generating a second set of results at place " + cpu);
        doBlackScholes(cpu, 
                new GlobalRail[Float](h_OptionYears),
                new GlobalRail[Float](h_StockPrice),
                new GlobalRail[Float](h_OptionStrike),
                new GlobalRail[Float](h_CallResultCPU),
                new GlobalRail[Float](h_PutResultCPU),
                OPT_N,
                RISKFREE,
                VOLATILITY);

        Console.OUT.println("Verifying the reuslts match...");
        var sum_delta:Float = 0.0f;
        var sum_ref:Float = 0.0f;
        var max_delta:Float = 0.0f;
        for (var i:Int=0n; i < OPT_N; i++) {
            val ref_val = h_CallResultCPU(i);
            val delta = Math.abs(ref_val - h_CallResultGPU(i));
            if(delta > max_delta) max_delta = delta;
            sum_delta += delta;
            sum_ref   += Math.abs(ref_val);
        }
        val L1norm = sum_delta / sum_ref;
        Console.OUT.println("L1 norm: " + L1norm);
        Console.OUT.println("Max absolute error: " + max_delta);
        Console.OUT.println((L1norm < 1e-6f) ? "TEST PASSED" : "TEST FAILED");
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab
