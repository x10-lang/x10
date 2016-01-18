/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2014-2016.
 */
import harness.x10Test;

import x10.util.Team;

/**
 * Benchmarks performance of Team.gatherv
 */
public class BenchmarkGatherV extends x10Test {
    private static ITERS = 10;
    private static MAX_S = 17;
    private static SRC_OFFSET = 5;
    private static DST_OFFSET = 3;

    public def run(): Boolean {
        // root=nplaces/2  hangs starting from using  5 places
        val NPLACES = Place.numPlaces();
        val root = Place(NPLACES-1);

        finish for (place in Place.places()) at (place) async {
            val warmupIn = new Rail[Double](1);
            var warmupOut:Rail[Double] = new Rail[Double](NPLACES);
            val warmupCounts = new Rail[Int](NPLACES, 1n);
            Team.WORLD.gatherv(root,warmupIn, 0, warmupOut, 0, warmupCounts); // warm up comms layer
            
            for (var s:Long= 1; s <= MAX_S; s++) {
                var dst:Rail[Double] = null;
                val svalue = s;
                val dcounts = new Rail[Int](NPLACES, (i:Long) => (Math.pow(2,svalue) * (i + 1)) as Int);
                //the segment size at place i is 2^S*(i+1)
                //the summation of the series SEGMA(j=1..N){j} = (N * N+1 / 2))
                val dstSize = (Math.pow(2,s) * (NPLACES * (NPLACES+1)/2) + DST_OFFSET) as Long;

                if (here.id == root.id){
                    dst = new Rail[Double](dstSize);
                }

                val srcSize = (Math.pow(2,s) * (here.id + 1)) as Long + SRC_OFFSET;
                val src = new Rail[Double](srcSize, (i:Long) => here.id as Double);

                val start = System.nanoTime();
                for (iter in 1..ITERS) {
                    Team.WORLD.gatherv(root,src, SRC_OFFSET, dst, DST_OFFSET, dcounts);
                }
                val stop = System.nanoTime();

                // check correctness
                if (here.id == root.id){
                    var indx:Long = DST_OFFSET;
                    for (i in 0..(dcounts.size-1)){
                        val expectedValue = i;
                        for (k in 1..dcounts(i)){
                            chk(dst(indx) == expectedValue as Double , "elem " + indx + " is " + dst(indx) + " should be " + expectedValue);
                            indx++;
                        }
                    }
                }

                if (here == Place.FIRST_PLACE) Console.OUT.printf("gatherV %d: %g ms\n", dstSize, ((stop-start) as Double) / 1e6 / ITERS);
            }
        }
        return true;
    }
    public static def main(var args: Rail[String]): void {
        new BenchmarkGatherV().execute();
    }
}
