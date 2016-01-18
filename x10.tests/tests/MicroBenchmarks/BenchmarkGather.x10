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
 * Benchmarks performance of Team.gather 
 */
public class BenchmarkGather extends x10Test {
    private static ITERS = 10;
    private static COUNT_PER_PLACE = 2<<19;
    private static SRC_OFFSET = 5;
    private static DST_OFFSET = 3;
    
    public def run(): Boolean {
        // root=nplaces/2  hangs starting from using  5 places
        val root = Place(Place.numPlaces()-1);
        
        finish for (place in Place.places()) at (place) async {
            val warmupIn = new Rail[Double](1);
            var warmupOut:Rail[Double] = new Rail[Double](Place.numPlaces());
            Team.WORLD.gather(root,warmupIn, 0, warmupOut, 0, 1); // warm up comms layer
            for (var s:Long= 1; s <= COUNT_PER_PLACE; s *= 2) {
                var dst:Rail[Double] = null;
                val dstSize = s*Place.numPlaces()+DST_OFFSET;
                if (here.id == root.id){
                    dst = new Rail[Double](dstSize);
                }
                val srcSize = s + SRC_OFFSET;
                val src = new Rail[Double](srcSize, (i:Long) => here.id as Double);
                
                val start = System.nanoTime();
                for (iter in 1..ITERS) {
                    Team.WORLD.gather(root, src, SRC_OFFSET, dst, DST_OFFSET, s);
                }
                val stop = System.nanoTime();
                
                // check correctness
                if (here.id == root.id){
                    for (i in DST_OFFSET..(dstSize-1)) {
                        val expectedValue = (i-DST_OFFSET)/s;
                        chk(dst(i) == expectedValue as Double , "elem " + i + " is " + dst(i) + " should be " + expectedValue);
                    }
                }

                if (here == Place.FIRST_PLACE) Console.OUT.printf("gather %d: %g ms\n", s, ((stop-start) as Double) / 1e6 / ITERS);
            }
        }
        return true;
    }

    public static def main(var args: Rail[String]): void {
        new BenchmarkGather().execute();
    }
}