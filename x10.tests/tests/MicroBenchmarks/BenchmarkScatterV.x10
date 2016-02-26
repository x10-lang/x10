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
 * Benchmarks performance of Team.scatterv
 */
public class BenchmarkScatterV extends x10Test {
    private static ITERS = 10;
    private static MAX_S = 17;
    private static SRC_OFFSET = 5;
    private static DST_OFFSET = 3;

    public def run(): Boolean {
        // root=nplaces/2  hangs starting from using  5 places
        val NPLACES = Place.numPlaces();
        val root = Place(NPLACES-1);

        finish for (place in Place.places()) at (place) async {
            val warmupIn = new Rail[Double](NPLACES);
            var warmupOut:Rail[Double] = new Rail[Double](1);
            val warmupCounts = new Rail[Int](NPLACES, 1n);
            Team.WORLD.scatterv(root,warmupIn, 0, warmupOut, 0, warmupCounts); // warm up comms layer

            for (var s:Long= 1; s <= MAX_S; s++) {
                var src:Rail[Double] = null;
                val svalue = s;
                val scounts = new Rail[Int](NPLACES, (i:Long) => (Math.pow(2,svalue) * (i + 1)) as Int);
                //the segment size at place i is 2^S*(i+1) --> i=0..NPLACES-1
                //the summation of the series SEGMA(j=1..N){j} = (N * N+1 / 2))
                val srcSize = (Math.pow(2,s) * (NPLACES * (NPLACES+1)/2) + SRC_OFFSET) as Long;

                if (here.id == root.id){
                    src = new Rail[Double](srcSize);
                    var lastPlaceId:Long = 0;
                    var it:Long = SRC_OFFSET;
                    //initialize the src rail so that each place segment contains its the place id as a value
                    while(it<srcSize){
                        for (var j:Long = 0; j < (Math.pow(2,s)*(lastPlaceId+1)) as Long; j++){
                            src(it++) = lastPlaceId;
                        }
                        lastPlaceId++;
                    }
                }

                val dstSize = (Math.pow(2,s) * (here.id + 1)) as Long + DST_OFFSET;
                val dst = new Rail[Double](dstSize);

                val start = System.nanoTime();
                for (iter in 1..ITERS) {
                    Team.WORLD.scatterv(root,src, SRC_OFFSET, dst, DST_OFFSET, scounts);
                }
                val stop = System.nanoTime();

                // check correctness
                for (i in DST_OFFSET..(dstSize-1)) {
                    val expectedValue = here.id;
                    chk(dst(i) == expectedValue as Double , "elem " + i + " is " + dst(i) + " should be " + expectedValue);
                }

                if (here == Place.FIRST_PLACE) Console.OUT.printf("scatterV %d: %g ms\n", srcSize, ((stop-start) as Double) / 1e6 / ITERS);
            }
        }

        return true;
    }
    public static def main(var args: Rail[String]): void {
        new BenchmarkScatterV().execute();
    }
}
