/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright Australian National University 2010-2011.
 */

import harness.x10Test;
import x10.array.*;

/**
 * Tests performance of DistArray iteration
 * @author milthorpe 11/2010
 */
public class BenchmarkIterateDistArrayNew(elementsPerPlace : Long) extends x10Test {

    public def this(elementsPerPlace : Long) {
        property(elementsPerPlace);
    }

    public def run(): Boolean {
        val arraySize = elementsPerPlace * Place.numPlaces();

        val a = new DistArray_Block_1[Long](arraySize);

        var start:Long = System.nanoTime();
        for (t in 1..100) {
            // iterate and update each element of the distributed array
            finish for (place in a.placeGroup()) async at (place) {
                for ([i] in a.localIndices()) {
                    a(i) = i;
                }
            }
        }
        var stop:Long = System.nanoTime();

        Console.OUT.printf("iterate DistArray_Block avg: %g ms\n", ((stop-start) as Double) / 1e08);

        start = System.nanoTime();
        for (t in 1..100) {
            // iterate and update each element of the distributed array
            finish for (place in a.placeGroup()) async at (place) {
                val aLocal = a.raw();
                for (i in aLocal.range()) {
                    aLocal(i) = i;
                }
            }
        }
        stop = System.nanoTime();

        Console.OUT.printf("iterate DistArray over local Rail avg: %g ms\n", ((stop-start) as Double) / 1e08);

        return true;
    }

    public static def main(var args: Rail[String]): void {
        var elementsPerPlace : Long = 1000;
        if (args.size > 0) {
            elementsPerPlace = Long.parse(args(0));
        }
        new BenchmarkIterateDistArrayNew(elementsPerPlace).execute();
    }

}
