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

import x10.util.Timer;
import x10.io.Console;

/**
 * Simple version of HPC Challenge Stream benchmark with a collection 
 * of local arrays implementing a global array.
 * 
 * For a scalable, high-performance version of this benchmark see
 * Stream.x10 in the X10 Benchmarks (separate download from x10-lang.org)
 */
public class FSSimpleDist {

    static MEG = 1024*1024;
    static alpha = 3.0D;

    static NUM_TIMES = 10;

    static DEFAULT_SIZE = MEG / 8;

    static NUM_PLACES = Place.MAX_PLACES;

    public static def main(args:Rail[String]) {
        val verified = new Cell[Boolean](true);
        val times = GlobalRef[Rail[double]](new Rail[double](NUM_TIMES));
        val N0 = args.size > 0 ? int.parse(args(0)) : DEFAULT_SIZE;
        val N = N0 * NUM_PLACES;
        val localSize =  N0;

        Console.OUT.println("localSize=" + localSize);

        finish {

            for (p in 0..(NUM_PLACES-1)) {
                
                async at(Place.place(p)) {
                    
                    val a = new Rail[double](localSize);
                    val b = new Rail[double](localSize);
                    val c = new Rail[double](localSize);
                    
                    for (i in 0..(localSize-1)) {
                        b(i) = 1.5 * (p*localSize+i);
                        c(i) = 2.5 * (p*localSize+i);
                    }
                    
                    for (j in 0..(NUM_TIMES-1)) {
                        if (p==0L) {
                        	val t = times as GlobalRef[Rail[double]]{self.home==here};
                        	t()(j) = -now(); 
                        }
                        for (var i:int=0; i<localSize; i++)
                            a(i) = b(i) + alpha*c(i);
                        if (p==0L) {
                        	val t = times as GlobalRef[Rail[double]]{self.home==here};
                        	t()(j) += now();
                        }
                    }
                    
                    // verification
                    for (var i:int=0; i<localSize; i++)
                        if (a(i) != b(i) + alpha*c(i)) 
                            verified.set(false);
                }
            }
        }

        var min:double = 1000000;
        for (var j:int=0; j<NUM_TIMES; j++)
            if (times()(j) < min)
                min = times()(j);
        printStats(N, min, verified());
    }

    static def now():double = Timer.nanoTime() * 1e-9;

    static def printStats(N:long, time:double, verified:boolean) {
        val size = (3*8*N/MEG);
        val rate = (3*8*N) / (1.0E9*time);
        Console.OUT.println("Number of places=" + NUM_PLACES);
        Console.OUT.println("Size of arrays: " + size +" MB (total)" + size/NUM_PLACES + " MB (per place)");
        Console.OUT.println("Min time: " + time + " rate=" + rate + " GB/s");
        Console.OUT.println("Result is " + (verified ? "verified." : "NOT verified."));
    }                                
}
