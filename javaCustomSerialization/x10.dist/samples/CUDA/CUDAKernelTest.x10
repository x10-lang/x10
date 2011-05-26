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
import x10.compiler.NoInline;
import x10.compiler.CUDA;
import x10.compiler.CUDADirectParams;

public class CUDAKernelTest {

    static def doTest1 (init:Array[Float](1){rail}, recv:Array[Float](1){rail}, p:Place, len:Int) {

        val remote = CUDAUtilities.makeRemoteArray[Float](p,len,(Int)=>0.0 as Float); // allocate 

        finish async at (p) @CUDA {
            finish for (block in 0..7) async {
                clocked finish for (thread in 0..63) clocked async {
                    val tid = block*64 + thread;
                    val tids = 8*64;
                    for (var i:Int=tid ; i<len ; i+=tids) {
                        remote(i) = Math.sqrtf(@NoInline init(i));
                    }
                }
            }
        }

        finish Array.asyncCopy(remote, 0, recv, 0, len); // dma back

        // validate
        var success:Boolean = true;
        for ([i] in recv.region) {
            val oracle = i as Float;
            if (Math.abs(1 - (recv(i)*recv(i))/oracle) > 1E-6f) {
                Console.ERR.println("recv("+i+"): "+recv(i)+" * "+recv(i)+" = "+(recv(i)*recv(i)));
                success = false;
            }
        }
        Console.OUT.println((success?"SUCCESS":"FAIL")+" at "+p);

        CUDAUtilities.deleteRemoteArray(remote);
    }

    static def doTest2 (p:Place) {

        val recv = new Array[Float](64,(i:Int)=>0.0f);

        val remote = CUDAUtilities.makeRemoteArray[Float](p,64,(Int)=>0.0f); // allocate 

        val arr1 = new Array[Float](64);
        val arr2 = new Array[Int](64);

        finish async at (p) @CUDA @CUDADirectParams {
            finish for (block in 0..1) async {
                val shm1 = new Array[Float](arr1);
                val shm2 = new Array[Int](64, (x:Int)=>{val tmp = x+10; return tmp;});
                val shm3 = new Array[Int](64, 0);
                val shm4 = new Array[Float](64, (Int)=>0.0f);
                clocked finish for (thread in 0..63) clocked async {
                    shm1(thread) = thread;
                    Clock.advanceAll();
                    shm2(thread) = @NoInline shm1(63-thread) as Int;
                    Clock.advanceAll();
                    shm3(thread) = @NoInline shm2(63-thread);
                    Clock.advanceAll();
                    remote(thread) = @NoInline shm3(63-thread);
                }
            }
        }

        finish Array.asyncCopy(remote, 0, recv, 0, 64); // dma back

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

        CUDAUtilities.deleteRemoteArray(remote);
    }

    static def doTest3 (p:Place) {

        val blocks = 30;
        val threads = 64;
        val tids = blocks * threads;

        val recv = new Array[Float](tids,(i:Int)=>0.0f);

        val remote = CUDAUtilities.makeRemoteArray[Float](p,tids,(Int)=>0.0 as Float); // allocate 

        val rnd = new Random();
        val arr1 = new Array[Float](threads,(i:Int)=>rnd.nextFloat());

        finish async at (p) @CUDA @CUDADirectParams {
            val ccache = arr1.sequence();
            finish for (block in 0..(blocks-1)) async {
                clocked finish for (thread in 0..(threads-1)) clocked async {
                    remote(threads*block + thread) = @NoInline ccache(thread);
                }
            }
        }

        finish Array.asyncCopy(remote, 0, recv, 0, recv.size); // dma back

        // validate
        var success:Boolean = true;
        for ([i] in recv.region) {
            val oracle = arr1(i % threads);
            if (Math.abs(oracle - recv(i)) > 1E-6f) {
                Console.ERR.println("recv("+i+"): "+recv(i)+" not "+oracle);
                success = false;
            }
        }
        Console.OUT.println((success?"SUCCESS":"FAIL")+" at "+p);

        CUDAUtilities.deleteRemoteArray(remote);
    }

     @CUDA static def function (x:Int) : Int = x * x - 22;

    static def doTest4 (p:Place) {

        val blocks = 30;
        val threads = 64;
        val tids = blocks * threads;

        val recv = new Array[Float](tids,(i:Int)=>0.0f);

        val remote = CUDAUtilities.makeRemoteArray[Float](p,tids,(Int)=>0.0 as Float); // allocate 

        finish async at (p) @CUDA @CUDADirectParams {
            finish for (block in 0..(blocks-1)) async {
                clocked finish for (thread in 0..(threads-1)) clocked async {
                    val r = function(5);
                    remote(threads*block + thread) = r;
                }
            }
        }

        finish Array.asyncCopy(remote, 0, recv, 0, recv.size); // dma back

        // validate
        var success:Boolean = true;
        for ([i] in recv.region) {
            val oracle = 3;
            if (Math.abs(oracle - recv(i)) > 1E-6f) {
                Console.ERR.println("recv("+i+"): "+recv(i)+" not "+oracle);
                success = false;
            }
        }
        Console.OUT.println((success?"SUCCESS":"FAIL")+" at "+p);

        CUDAUtilities.deleteRemoteArray(remote);
    }


    public static def main (args:Array[String](1)) {
        val len = args.size==1 ? Int.parse(args(0)) : 1000;

        for (host in Place.places()) at (host) {

            val init = new Array[Float](len,(i:Int)=>i as Float);
            val recv = new Array[Float](len,(i:Int)=>0.0 as Float);

            var done_work:Boolean = false;

            for (gpu in here.children().values()) if (gpu.isCUDA()) {
                Console.OUT.println("Running test on GPU called "+gpu);
                doTest1(init, recv, gpu, len);
                doTest2(gpu);
                doTest3(gpu);
                doTest4(gpu);
                done_work = true;
            }

            if (!done_work) {
                Console.OUT.println("Running test on CPU called "+here);
                Console.OUT.println("Set X10RT_ACCELS=ALL to use GPUs if you have them.");
                doTest1(init, recv, here, len);
                doTest2(here);
                doTest3(here);
                doTest4(here);
            }
        }
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab
