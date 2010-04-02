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

import x10.array.Array;
import x10.array.Dist;
import x10.array.Region;

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
    static type Real=Double;
    const n = 3, epsilon = 1.0e-5;

    const BigD = Dist.makeBlock([0..n+1, 0..n+1], 0);
    const D = BigD | ([1..n, 1..n] as Region);
    const LastRow = [0..0, 1..n] as Region;
    const A = Array.make[Real](BigD,(p:Point)=>{ LastRow.contains(p) ? 1.0 : 0.0 });
    const Temp = Array.make[Real](BigD);

    static def stencil_1((x,y):Point(2)) = (([x-1..x+1,y..y] as Region(2)) || [x..x,y-1..y+1]) - [x..x,y..y];

    static def subtract(a:Array[Real],b:Array[Real]) = Array.make[Real](a.dist, (p:Point)=>a(p as Point(a.rank))-b(p as Point(b.rank)));

    // TODO: The array library really should provide an efficient 
    //       all-to-all collective reduction.
    //       This is a quick and sloppy implementation, which does way too much work.
    static def reduceMax(z:Point{self.rank==diff.rank}, diff:Array[Real], scratch:Array[Real]) {
        val max = diff.reduce(Math.max.(Double,Double), 0.0);
        diff(z) = max;
        next;
    }

    def run() {
        finish async {
            val c = Clock.make();
            val D_Base = Dist.makeUnique(D.places());
            val diff = Array.make[Real](D_Base);
            val scratch = Array.make[Real](D_Base);
            ateach (z in D_Base) clocked(c) {
                do {
                    diff(z) = 0;
                    for (p:Point(2) in D | here) {
                        Temp(p) = (A | stencil_1(p)).reduce(Double.+, 0.0)/4;
                        diff(z) = Math.max(diff(z), Math.abs(A(p) - Temp(p)));
                    }
                    next;
                    for (p:Point(2) in D | here) {
                        A(p) = Temp(p);
                    }
                    reduceMax(z, diff, scratch);
                } while (diff(z) > epsilon);
            }
        }
    }
 
   def prettyPrintResult() {
       for ((i) in A.region.projection(0)) {
           for ((j) in A.region.projection(1)) {
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
        val s = new HeatTransfer_v4();
        Console.OUT.print("Beginning computation...");
        val start = System.nanoTime();
        s.run();
        val stop = System.nanoTime();
	Console.OUT.printf("...completed in %1.3f seconds.\n", (stop-start as double)/1e9);
        s.prettyPrintResult();
    }
}
