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

import x10.regionarray.*;

/**
 * This is one of a series of programs showing how to express
 * different forms of parallelism in X10.</p>
 *
 * All of the example programs in the series are computing
 * the same thing:  solving a set of 2D partial differential
 * equations that can be expressed as an iterative 4-point
 * stencil operation.  See the X10 2.0 tutorial for
 * for more details and some pictures.</p>
 *
 * This program is showing the sequential code
 * from which the other codes can be derived.<p>
 */
public class HeatTransfer_v0 {
    static val n = 3;
    static val epsilon = 1.0e-5;

    static val BigD = Region.make(0..(n+1), 0..(n+1));
    static val D = Region.make(1..n, 1..n);
    static val LastRow = Region.make(0..0, 1..n);
    static val A = new Array[Double](BigD,(p:Point)=>{ LastRow.contains(p) ? 1.0 : 0.0 });
    static val Temp = new Array[Double](BigD,(p:Point(BigD.rank))=>{ A(p) });

    static def stencil_1([x,y]:Point(2)): Double {
        return (A(x-1,y) + A(x+1,y) + A(x,y-1) + A(x,y+1)) / 4;
    }

    def run() {
        var delta:Double = 1.0;
        do {
            for (p in D) {
                Temp(p) = stencil_1(p);
            }

            delta = A.map[Double,Double](Temp, (x:Double,y:Double)=>Math.abs(x-y)).reduce((x:Double,y:Double)=>Math.max(x,y), 0.0);

            for (p in D) {
                A(p) = Temp(p);
            }
        } while (delta > epsilon);
    }
 
   def prettyPrintResult() {
       for ([i] in A.region.projection(0)) {
           for ([j] in A.region.projection(1)) {
                val pt = Point.make(i,j);
                val tmp = A(pt);
                Console.OUT.printf("%1.4f ", tmp);
            }
            Console.OUT.println();
        }
    }

    public static def main(Rail[String]) {
        Console.OUT.println("HeatTransfer Tutorial example with n="+n+" and epsilon="+epsilon);
        Console.OUT.println("Initializing data structures");
        val s = new HeatTransfer_v0();
        Console.OUT.print("Beginning computation...");
        val start = System.nanoTime();
        s.run();
        val stop = System.nanoTime();
        Console.OUT.printf("...completed in %1.3f seconds.\n", ((stop-start) as double)/1e9);
        s.prettyPrintResult();
    }
}
