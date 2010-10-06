// Yoav added: IGNORE_FILE
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
import x10.compiler.CUDA;

public class CUDAKernelTest {

    static def doWork (init:Array[Float]{rail}, recv:Array[Float]{rail}, p:Place, len:Int) {


        val remote = CUDAUtilities.makeRemoteArray[Float](p,len,(Int)=>0.0 as Float); // allocate 

        finish async at (p) @CUDA {
            for ([block] in 0..7) {
                for ([thread] in 0..63) async {
                    val tid = block*64 + thread;
                    val tids = 8*64;
                    for (var i:Int=tid ; i<len ; i+=tids) {
                        remote(i) = Math.sqrt(init(i));
                    }
                }
            }
        }

        finish Array.asyncCopy(remote, 0, recv, 0, len); // dma back

        // validate
        var success:Boolean = true;
        for ([i] in remote.region) {
            if (Math.abs(1 - (recv(i)*recv(i))/(i as Float)) > 1E-6f) {
                Console.ERR.println("recv("+i+"): "+recv(i)+" * "+recv(i)+" = "+(recv(i)*recv(i)));
                success = false;
            }
        }
        Console.OUT.println((success?"SUCCESS":"FAIL")+" at "+p);
    }

    public static def main (args:Array[String](1)) {
        val len = args.size==1 ? Int.parse(args(0)) : 1000;

        for (host in Place.places()) at (host) {

            val init = new Array[Float](len,(i:Int)=>i as Float);
            val recv = new Array[Float](len,(i:Int)=>0.0 as Float);

            var done_work:Boolean = false;

            for (gpu in here.children().values()) if (gpu.isCUDA()) {
                Console.OUT.println("Running test on GPU called "+gpu);
                doWork(init, recv, gpu, len);
                done_work = true;
            }

            if (!done_work) {
                Console.OUT.println("Running test on CPU called "+here);
                Console.OUT.println("Set X10RT_ACCELS=ALL to use GPUs if you have them.");
                doWork(init, recv, here, len);
            }
        }
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab

