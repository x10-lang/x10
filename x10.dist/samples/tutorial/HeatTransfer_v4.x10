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
 * This program is illustrating an SPMD with all-to-all reduction
 * ("MPI-style").</p>
 */
public class HeatTransfer_v4 {
    static val n = 3n;
    static val epsilon = 1.0e-5;

    static val BigD = Dist.makeBlock(Region.make(0n..(n+1n), 0n..(n+1n)), 0n);
    static val D = BigD | Region.make(1n..n, 1n..n);
    static val LastRow = Region.make(0n..0n, 1n..n);

    val A = DistArray.make[Double](BigD,(p:Point)=>{ LastRow.contains(p) ? 1.0 : 0.0 });

    def stencil_1([x,y]:Point(2)): Double {
        return ((at(A.dist(x-1,y)) A(x-1,y)) + 
                (at(A.dist(x+1,y)) A(x+1,y)) + 
                (at(A.dist(x,y-1)) A(x,y-1)) + 
                (at(A.dist(x,y+1)) A(x,y+1))) / 4;
    }

    // TODO: The array library really should provide an efficient 
    //       all-to-all collective reduction.
    //       This is a quick and sloppy implementation, which does way too much work.
    def reduceMax(diff:DistArray[Double],z:Point{self.rank==diff.rank}, scratch:DistArray[Double]) {
        val max = diff.reduce((x:Double,y:Double)=>Math.max(x,y), 0.0);
        diff(z) = max;
        Clock.advanceAll();
    }

    def run() {
        clocked finish {
            val D_Base = Dist.makeUnique(D.places());
            val diff = DistArray.make[Double](D_Base);
            val scratch = DistArray.make[Double](D_Base);
            val Temp = DistArray.make[Double](BigD);
            for (z in D_Base) clocked async at (D_Base(z)) {
                do {
                    diff(z) = 0;
                    for (p:Point(2) in D | here) {
                        Temp(p) = stencil_1(p);
                        diff(z) = Math.max(diff(z), Math.abs(A(p) - Temp(p)));
                    }
                    Clock.advanceAll();
                    for (p:Point(2) in D | here) {
                        A(p) = Temp(p);
                    }
                    reduceMax(diff, z, scratch);
                } while (diff(z) > epsilon);
            }
        }
    }
 
   def prettyPrintResult() {
       for ([i] in A.region.projection(0)) {
           for ([j] in A.region.projection(1)) {
                val pt = Point.make(i,j);
                at (BigD(pt)) {
                    val tmp = A(pt);
                    at (Place.FIRST_PLACE) Console.OUT.printf("%1.4f ", tmp);
                }
            }
            Console.OUT.println();
        }
    }

    public static def main(Rail[String]) {
        Console.OUT.println("HeatTransfer Tutorial example with n="+n+" and epsilon="+epsilon);
        Console.OUT.println("Initializing data structures");
        Console.OUT.println("NUM_PLACES=" + Place.numPlaces());
        val s = new HeatTransfer_v4();
        Console.OUT.print("Beginning computation...");
        val start = System.nanoTime();
        s.run();
        val stop = System.nanoTime();
        Console.OUT.printf("...completed in %1.3f seconds.\n", ((stop-start) as double)/1e9);
        s.prettyPrintResult();
    }
}
