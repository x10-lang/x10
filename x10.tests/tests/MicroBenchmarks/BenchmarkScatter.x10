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
 * Benchmarks performance of Team.scatter 
 */
public class BenchmarkScatter extends x10Test {
    private static ITERS = 10;
    private static COUNT_PER_PLACE = 2<<19;
    private static SRC_OFFSET = 5;
    
    public def run(): Boolean {
        // root=nplaces/2  hangs starting from using  5 places
        val root = Place(Place.numPlaces()-1);
        
        finish for (place in Place.places()) at (place) async {
            val warmupIn = new Rail[Double](Place.numPlaces());
            var warmupOut:Rail[Double] = new Rail[Double](1);
            Team.WORLD.scatter(root,warmupIn, 0, warmupOut, 0, 1); // warm up comms layer
            for (var s:Long= 1; s <= COUNT_PER_PLACE; s *= 2) {
                var src:Rail[Double] = null;
                if (here.id == root.id){
                    src = new Rail[Double](s*Place.numPlaces()+SRC_OFFSET, (i:Long) => i as Double);
                }
                val dst = new Rail[Double](s);
                
                val start = System.nanoTime();
                for (iter in 1..ITERS) {
                    Team.WORLD.scatter(root,src, SRC_OFFSET, dst, 0, s);
                }
                val stop = System.nanoTime();
                
                // check correctness
                for (i in 0..(s-1)) {
                    val expectedValue = here.id*s+i+SRC_OFFSET;
                    chk(dst(i) == expectedValue as Double , "elem " + i + " is " + dst(i) + " should be " + expectedValue);
                }

                if (here == Place.FIRST_PLACE) Console.OUT.printf("scatter %d: %g ms\n", s, ((stop-start) as Double) / 1e6 / ITERS);
            }
        }
        return true;
    }

    public static def main(var args: Rail[String]): void {
        new BenchmarkScatter().execute();
    }
}