/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright Australian National University 2011.
 */

import x10.regionarray.Array;
import x10.regionarray.Dist;
import x10.regionarray.DistArray;
import x10.regionarray.Region;

/**
 * Tests performance of DistArray ghost updates
 * @author milthorpe 09/2011
 */
public class BenchmarkUpdateGhosts(arrayDim:Long) {
    public static ITERS = 1000;

    public def this(elementsPerPlace:Long) {
        property(elementsPerPlace);
    }

    public def run(): Boolean {
        val facI = Math.sqrt(Place.numPlaces()) as Long;
        val facJ = Place.numPlaces() / facI;
        val r = Region.makeRectangular(0..(arrayDim*facI-1), 0..(arrayDim*facJ-1), 0..(arrayDim-1));
        val d = Dist.makeBlockBlock(r, 0, 1);

        val a = DistArray.make[Double](d, 2, false);

        val shiftTime = finish(Reducible.MaxReducer[Long](-1)) {
            ateach(p in Dist.makeUnique(d.places())) {
                val startHere = System.nanoTime();
                for (iter in 1..ITERS) {
                    a.sendGhostsLocal();
                    a.waitForGhostsLocal();
                }
                val stopHere = System.nanoTime();
                offer (stopHere-startHere);
            }
        };
        Console.OUT.printf("updateGhosts avg: %g ms\n", ((shiftTime) as Double) / (1e06 * ITERS));

        return true;
    }

    public static def main(var args: Rail[String]): void {
        var arrayDim:Long = 100;
        if (args.size > 0) {
            arrayDim = Long.parseLong(args(0));
        }
        new BenchmarkUpdateGhosts(arrayDim).run();
    }
}

// vim:tabstop=4:shiftwidth=4:expandtab
