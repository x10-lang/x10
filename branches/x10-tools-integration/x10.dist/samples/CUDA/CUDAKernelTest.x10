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
import x10.compiler.CUDA;

public class CUDAKernelTest {

    static def doWork (init:Rail[Float]!, recv:Rail[Float]!, p:Place, len:Int) {
        val remote = Rail.makeRemote(p,len,(Int)=>0.0 as Float); // allocate 

        //finish init.copyTo(0, remote, 0, len); // dma there
        val init_ = init as ValRail[Float];

        finish async (p) @CUDA {
            for ((block):Point in 0..7) {
                for ((thread):Point in 0..63) async {
                    val tid = block*64 + thread;
                    val tids = 8*64;
                    for (var i:Int=tid ; i<len ; i+=tids) {
                        //remote(i) = Math.sqrt(remote(i));
                        remote(i) = Math.sqrt(init_(i));
                    }
                }
            }
        }

        finish recv.copyFrom(0, remote, 0, len); // dma back

        // validate
        var success:Boolean = true;
        for ((i) in 0..remote.length-1) {
            if (Math.abs(1 - (recv(i)*recv(i))/(i as Float)) > 1E-6f) {
                Console.ERR.println("recv("+i+"): "+recv(i)+" * "+recv(i)+" = "+(recv(i)*recv(i)));
                success = false;
            }
        }
        Console.OUT.println((success?"SUCCESS":"FAIL")+" at "+p);
    }

    public static def main (args : Rail[String]!) {
        val len = args.length==1 ? Int.parse(args(0)) : 1000;

        for (host in Place.places) at (host) {

            val init = Rail.make[Float](len,(i:Int)=>i as Float);
            val recv = Rail.make[Float](len,(i:Int)=>0.0 as Float);

            var done_work:Boolean = false;

            for (gpu in here.children()) if (gpu.isCUDA()) {
                doWork(init, recv, gpu, len);
                done_work = true;
            }

            if (!done_work) {
                doWork(init, recv, here, len);
            }
        }
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab

