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
import harness.x10Test;

import x10.array.Array_1;

/**
 * Tests performance of Array iteration with IntRange and Region
 * @author milthorpe 03/2011
 */
public class BenchmarkIterateArray(elements : Long) extends x10Test {

    public def this(elements : Long) {
        property(elements);
    }

    public def run(): Boolean = {
        val a = new Array_1[Long](elements);
        val iterations = (1e7 / elements) as Int;
        Console.OUT.println("iterations = " + iterations);

        var start : Long = System.nanoTime();
        for (t in 1..iterations) {
            // iterate and update each element of the array
            for (i in a.indices()) {
                a(i) = i(0);
            }
        }
        var stop : Long = System.nanoTime();

        Console.OUT.printf("iterate Array with IterationSpace and Point avg: %g us\n", ((stop-start) as Double) / (1e3*iterations));

        start = System.nanoTime();
        for (t in 1..iterations) {
            for ([i] in a.indices()) {
                a(i) = i;
            }
        }
        stop = System.nanoTime();

        Console.OUT.printf("iterate Array with IterationSpace and exploded Point avg: %g us\n", ((stop-start) as Double) / (1e3*iterations));

        start = System.nanoTime();
        for (t in 1..iterations) {
            for (i in 0L..(a.size-1)) {
                a(i) = i;
            }
        }
        stop = System.nanoTime();

        Console.OUT.printf("iterate Array with LongRange avg: %g us\n", ((stop-start) as Double) / (1e3*iterations));

        return true;
    }

    public static def main(args:Rail[String]) {
        var elements : Long = 10000;
        if (args.size > 0) {
            elements = Long.parse(args(0));
        }
        new BenchmarkIterateArray(elements).execute();
    }

}
