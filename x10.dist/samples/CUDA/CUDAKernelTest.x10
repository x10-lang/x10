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
import x10.util.CUDAUtilities;
import x10.util.Random;
import x10.compiler.CUDA;
import x10.compiler.CUDADirectParams;

public class CUDAKernelTest {

    static def doTest1 (init:Rail[Float], recv:Rail[Float], p:Place, len:Long) {

        val remote : GlobalRail[Float]{self.home() == p} = CUDAUtilities.makeGlobalRail[Float](p, len); // allocate 

        finish async at (p) @CUDA {
            finish for (block in 0n..7n) async {
                clocked finish for (thread in 0n..63n) clocked async {
                    val tid = block*64 + thread;
                    val tids = 8*64;
                    for (var i:Long=tid; i<len; i+=tids) {
                        remote(i) = Math.sqrtf(init(i));
                    }
                }
            }
        }

        finish Rail.asyncCopy(remote, 0, recv, 0, len); // dma back

        // validate
        var success:Boolean = true;
        for (i in recv.range) {
            val oracle = i as Float;
            if (Math.abs(1 - (recv(i)*recv(i))/oracle) > 1E-6f) {
                Console.ERR.println("recv("+i+"): "+recv(i)+" * "+recv(i)+" = "+(recv(i)*recv(i)));
                success = false;
            }
        }
        Console.OUT.println((success?"SUCCESS":"FAIL")+" at "+p);

        CUDAUtilities.deleteGlobalRail(remote);
    }

    static def doTest1d (init:Rail[Double], recv:Rail[Double], p:Place, len:Long) {

        val remote : GlobalRail[Double]{self.home() == p} = CUDAUtilities.makeGlobalRail[Double](p, len); // allocate 

        finish async at (p) @CUDA {
            finish for (block in 0n..7n) async {
                clocked finish for (thread in 0n..63n) clocked async {
                    val tid = block*64 + thread;
                    val tids = 8*64;
                    for (var i:Long=tid; i<len; i+=tids) {
                        remote(i) = Math.sqrt(init(i));
                    }
                }
            }
        }

        finish Rail.asyncCopy(remote, 0, recv, 0, len); // dma back

        // validate
        var success:Boolean = true;
        for (i in recv.range) {
            val oracle = i as Double;
            if (Math.abs(1 - (recv(i)*recv(i))/oracle) > 1E-6f) {
                Console.ERR.println("recv("+i+"): "+recv(i)+" * "+recv(i)+" = "+(recv(i)*recv(i)));
                success = false;
            }
        }
        Console.OUT.println((success?"SUCCESS":"FAIL")+" at "+p);

        CUDAUtilities.deleteGlobalRail(remote);
    }

    static def doTest2 (p:Place) {

        val recv = new Rail[Float](64);

        val remote = CUDAUtilities.makeGlobalRail[Float](p, 64); // allocate 

        val arr1 = new Rail[Float](64);
        val arr2 = new Rail[Int](64);

        finish async at (p) @CUDA @CUDADirectParams {
            finish for (block in 0n..1n) async {
                val shm1 = new Rail[Float](arr1);
                val shm2 = new Rail[Int](64, (x:Long)=>((x as Int)+10n));
                val shm3 = new Rail[Int](64, 0n);
                clocked finish for (thread in 0n..63n) clocked async {
                    shm1(thread) = thread;
                    Clock.advanceAll();
                    shm2(thread) = shm1(63-thread) as Int;
                    Clock.advanceAll();
                    shm3(thread) = shm2(63-thread);
                    Clock.advanceAll();
                    remote(thread) = shm3(63-thread);
                }
            }
        }

        finish Rail.asyncCopy(remote, 0, recv, 0, 64); // dma back

        // validate
        var success:Boolean = true;
        for (var i:Int=0n; i<64n; ++i) {
            val oracle = 63n-i%64n;
            if (Math.abs(oracle - recv(i)) > 1E-6f) {
                Console.ERR.println("recv("+i+"): "+recv(i)+" not "+oracle);
                success = false;
            }
        }
        Console.OUT.println((success?"SUCCESS":"FAIL")+" at "+p);

        CUDAUtilities.deleteGlobalRail(remote);
    }

    static def doTest3 (p:Place) {

        val blocks = 30n;
        val threads = 64n;
        val tids = blocks * threads;

        val recv = new Rail[Float](tids);

        val remote = CUDAUtilities.makeGlobalRail[Float](p, tids); // allocate 

        val rnd = new Random();
        val arr1 = new Rail[Float](threads,(i:Long)=>rnd.nextFloat());

        finish async at (p) @CUDA @CUDADirectParams {
            val ccache = CUDAConstantRail(arr1);
            finish for (block in 0n..(blocks-1n)) async {
                clocked finish for (thread in 0n..(threads-1n)) clocked async {
                    remote(threads*block + thread) = ccache(thread);
                }
            }
        }

        finish Rail.asyncCopy(remote, 0, recv, 0, recv.size); // dma back

        // validate
        var success:Boolean = true;
        for (i in recv.range) {
            val oracle = arr1(i % threads);
            if (Math.abs(oracle - recv(i)) > 1E-6f) {
                Console.ERR.println("recv("+i+"): "+recv(i)+" not "+oracle);
                success = false;
            }
        }
        Console.OUT.println((success?"SUCCESS":"FAIL")+" at "+p);

        CUDAUtilities.deleteGlobalRail(remote);
    }

    @CUDA static def function (x:Int) : Int = x * x - 22n;

    static def doTest4 (p:Place) {

        val blocks = 30n;
        val threads = 64n;
        val tids = blocks * threads;

        val recv = new Rail[Float](tids);

        val remote = CUDAUtilities.makeGlobalRail[Float](p, tids); // allocate 

        finish async at (p) @CUDA @CUDADirectParams {
            finish for (block in 0n..(blocks-1n)) async {
                clocked finish for (thread in 0n..(threads-1n)) clocked async {
                    val r = function(5n);
                    remote(threads*block + thread) = r;
                }
            }
        }

        finish Rail.asyncCopy(remote, 0, recv, 0, recv.size); // dma back

        // validate
        var success:Boolean = true;
        for (i in recv.range) {
            val oracle = 3n;
            if (Math.abs(oracle - recv(i)) > 1E-6f) {
                Console.ERR.println("recv("+i+"): "+recv(i)+" not "+oracle);
                success = false;
            }
        }
        Console.OUT.println((success?"SUCCESS":"FAIL")+" at "+p);

        CUDAUtilities.deleteGlobalRail(remote);
    }

    static def doTest5d (init:Rail[Double], recv:Rail[Double], p:Place, len:Long) {

        val remote : GlobalRail[Double]{self.home() == p} = CUDAUtilities.makeGlobalRail[Double](p, len); // allocate 

        finish async at (p) @CUDA {
            finish for (block in 0n..7n) async {
                clocked finish for (thread in 0n..63n) clocked async {
                    val tid = block*64 + thread;
                    val tids = 8*64;
                    for (var i:Long=tid; i<len; i+=tids) {
                        remote(i) = Math.cbrt(init(i));
                    }
                }
            }
        }

        finish Rail.asyncCopy(remote, 0, recv, 0, len); // dma back

        // validate
        var success:Boolean = true;
        for (i in recv.range) {
            val oracle = i as Double;
            if (Math.abs(1 - (recv(i)*recv(i)*recv(i))/oracle) > 1E-6f) {
                Console.ERR.println("recv("+i+"): "+recv(i)+" * "+recv(i)+" * "+recv(i)+" = "+(recv(i)*recv(i)*recv(i)));
                success = false;
            }
        }
        Console.OUT.println((success?"SUCCESS":"FAIL")+" at "+p);

        CUDAUtilities.deleteGlobalRail(remote);
    }


    public static def main(args:Rail[String]) {
        val len = args.size==1 ? Long.parse(args(0)) : 1000;

        for (host in Place.places()) at (host) {
            val topo = PlaceTopology.getTopology();

            val init = new Rail[Float](len,(i:Long)=>i as Float);
            val recv = new Rail[Float](len);
            val init_d = new Rail[Double](len,(i:Long)=>i as Double);
            val recv_d = new Rail[Double](len);

            var done_work:Boolean = false;

            for (gpu in topo.children(here)) if (gpu.isCUDA()) {
                Console.OUT.println("Running test on GPU called "+gpu);
                doTest1(init, recv, gpu, len);
                doTest1d(init_d, recv_d, gpu, len);
                doTest2(gpu);
                doTest3(gpu);
                doTest4(gpu);
                doTest5d(init_d, recv_d, gpu, len);
                done_work = true;
            }

            if (!done_work) {
                Console.OUT.println("Running test on CPU called "+here);
                Console.OUT.println("Set X10RT_ACCELS=ALL to use GPUs if you have them.");
                doTest1(init, recv, here, len);
                doTest1d(init_d, recv_d, here, len);
                doTest2(here);
                doTest3(here);
                doTest4(here);
                doTest5d(init_d, recv_d, here, len);
            }
        }
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab
