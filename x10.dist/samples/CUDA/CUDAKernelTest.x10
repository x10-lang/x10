/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
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
            finish for (block in 0..7) async {
                clocked finish for (thread in 0..63) clocked async {
                    val tid = block*64 + thread;
                    val tids = 8*64;
                    for (var i:Long=tid ; i<len ; i+=tids) {
                        remote(i) = Math.sqrtf(init(i));
                    }
                }
            }
        }

        finish Rail.asyncCopy(remote, 0l, recv, 0l, len); // dma back

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
            finish for (block in 0..7) async {
                clocked finish for (thread in 0..63) clocked async {
                    val tid = block*64 + thread;
                    val tids = 8*64;
                    for (var i:Long=tid ; i<len ; i+=tids) {
                        remote(i) = Math.sqrt(init(i));
                    }
                }
            }
        }

        finish Rail.asyncCopy(remote, 0l, recv, 0l, len); // dma back

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

        val recv = new Rail[Float](64,(i:Long)=>0.0f);

        val remote = CUDAUtilities.makeGlobalRail[Float](p,64,(Long)=>0.0f); // allocate 

        val arr1 = new Rail[Float](64);
        val arr2 = new Rail[Int](64);

        finish async at (p) @CUDA @CUDADirectParams {
            finish for (block in 0..1) async {
                val shm1 = new Rail[Float](arr1);
                val shm2 = new Rail[Int](64l, (x:Long)=>{val tmp = (x as Int)+10; return tmp;});
                val shm3 = new Rail[Int](64l, 0);
                val shm4 = new Rail[Float](64l, (Long)=>0.0f);
                clocked finish for (thread in 0..63) clocked async {
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

        finish Rail.asyncCopy(remote, 0l, recv, 0l, 64l); // dma back

        // validate
        var success:Boolean = true;
        for (var i:Int=0 ; i<64 ; ++i) {
            val oracle = 63-i%64;
            if (Math.abs(oracle - recv(i)) > 1E-6f) {
                Console.ERR.println("recv("+i+"): "+recv(i)+" not "+oracle);
                success = false;
            }
        }
        Console.OUT.println((success?"SUCCESS":"FAIL")+" at "+p);

        CUDAUtilities.deleteGlobalRail(remote);
    }

    static def doTest3 (p:Place) {

        val blocks = 30;
        val threads = 64;
        val tids = blocks * threads;

        val recv = new Rail[Float](tids,(i:Long)=>0.0f);

        val remote = CUDAUtilities.makeGlobalRail[Float](p,tids,(Long)=>0.0 as Float); // allocate 

        val rnd = new Random();
        val arr1 = new Rail[Float](threads,(i:Long)=>rnd.nextFloat());

        finish async at (p) @CUDA @CUDADirectParams {
            val ccache = CUDAConstantRail(arr1);
            finish for (block in 0..(blocks-1)) async {
                clocked finish for (thread in 0..(threads-1)) clocked async {
                    remote(threads*block + thread) = ccache(thread);
                }
            }
        }

        finish Rail.asyncCopy(remote, 0l, recv, 0l, recv.size); // dma back

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

     @CUDA static def function (x:Int) : Int = x * x - 22;

    static def doTest4 (p:Place) {

        val blocks = 30;
        val threads = 64;
        val tids = blocks * threads;

        val recv = new Rail[Float](tids,(i:Long)=>0.0f);

        val remote = CUDAUtilities.makeGlobalRail[Float](p,tids,(Long)=>0.0 as Float); // allocate 

        finish async at (p) @CUDA @CUDADirectParams {
            finish for (block in 0..(blocks-1)) async {
                clocked finish for (thread in 0..(threads-1)) clocked async {
                    val r = function(5);
                    remote(threads*block + thread) = r;
                }
            }
        }

        finish Rail.asyncCopy(remote, 0l, recv, 0l, recv.size); // dma back

        // validate
        var success:Boolean = true;
        for (i in recv.range) {
            val oracle = 3;
            if (Math.abs(oracle - recv(i)) > 1E-6f) {
                Console.ERR.println("recv("+i+"): "+recv(i)+" not "+oracle);
                success = false;
            }
        }
        Console.OUT.println((success?"SUCCESS":"FAIL")+" at "+p);

        CUDAUtilities.deleteGlobalRail(remote);
    }


    public static def main (args:Rail[String]) {
        val len = args.size==1l ? Long.parse(args(0)) : 1000l;

        for (host in Place.places()) at (host) {

            val init = new Rail[Float](len,(i:Long)=>i as Float);
            val recv = new Rail[Float](len,(i:Long)=>0.0 as Float);
            val init_d = new Rail[Double](len,(i:Long)=>i as Double);
            val recv_d = new Rail[Double](len,(i:Long)=>0.0 as Double);

            var done_work:Boolean = false;

            for (gpu in here.children()) if (gpu.isCUDA()) {
                Console.OUT.println("Running test on GPU called "+gpu);
                doTest1(init, recv, gpu, len);
                doTest1d(init_d, recv_d, gpu, len);
                doTest2(gpu);
                doTest3(gpu);
                doTest4(gpu);
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
            }
        }
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab
