/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2016.
 */

import x10.xrx.Runtime;
import x10.compiler.Native;

/**
 * This is a microbenchmark used to isolate the
 * overheads of different resilient finish implementations
 * for common combinations of finish/async/at.
 * An earlier version of this code was used to 
 * generate figure 6 in the PPoPP'14 paper on Resilient X10
 * (http://dx.doi.org/10.1145/2555243.2555248).
 */
public class BenchMicro {

    static OUTER_ITERS = 100;
    static INNER_ITERS = 100;
    static MIN_NANOS = (10*1e9) as long; // require each test to run for at least 10 seconds (reduce jitter)

    @Native("c++", "false")
    @Native("java", "true")
    static native def needsWarmup():Boolean;

    public static def main(args:Rail[String]){here==Place.FIRST_PLACE}{
        if (Place.numPlaces() < 2) {
            Console.ERR.println("Fair evaluation of place-zero based finish requires more than one place, and preferably more than one host.");
            System.setExitCode(1n);
            return;
        }

        val think:Long = args.size == 0 ? 0 : Long.parse(args(0));

	if (Runtime.RESILIENT_MODE == 0n) {
            Console.OUT.println("Configuration: DEFAULT (NON-RESILIENT)");
        } else {
            Console.OUT.println("Configuration: RESILIENT MODE "+Runtime.RESILIENT_MODE);
        }

        Console.OUT.println("Running with "+Place.numPlaces()+" places.");
        Console.OUT.println("Min elapsed time for each test: "+MIN_NANOS/1e9+" seconds.");
        Console.OUT.println("Think time for each activity: "+think+" nanoseconds.");

        if (needsWarmup()) {
            Console.OUT.println("Doing warmup for ManagedX10 -- expect 1 minute delay");
            doTest("warmup", think, false, MIN_NANOS/2);
            Console.OUT.println("Warmup complete");
        }
        
        Console.OUT.println("Test based from place 0");
        doTest("place 0 -- ", think, true, MIN_NANOS);
        Console.OUT.println();

        Console.OUT.println("Test based from place 1");
        at (Place(1)) doTest("place 1 -- ", think, true, MIN_NANOS);
        Console.OUT.println();
    }

    public static def doTest(prefix:String, t:Long, print:Boolean, minTime:Long) {
        var time0:Long, time1:Long;
        var iterCount:Long;
        val home = here;

        iterCount = 0;
        time0 = System.nanoTime();
        do {
            for (i in 1..OUTER_ITERS) {
                finish {
                    // Completely empty finish to measure cost of finish start/end
                    // independent of per-activity costs.
                }
            }
            time1 = System.nanoTime();
        iterCount++;
        } while (time1-time0 < minTime);
        if (print) Console.OUT.println(prefix+"empty finish: "+(time1-time0)/1E9/OUTER_ITERS/iterCount+" seconds");

        iterCount = 0;
        time0 = System.nanoTime();
        do {
            for (i in 1..OUTER_ITERS) {
                finish {
                    for (j in 1..INNER_ITERS) { 
                        async { think(t); };
                    }
                }
            }
            time1 = System.nanoTime();
            iterCount++;
        } while (time1-time0 < minTime);
        if (print) Console.OUT.println(prefix+"local termination of "+INNER_ITERS+" activities: "+(time1-time0)/1E9/OUTER_ITERS/iterCount+" seconds");

        iterCount = 0;
        time0 = System.nanoTime();
        val next = Place.places().next(home);
        do {
            for (i in 1..OUTER_ITERS) {
                finish {
                    at (next) async { think(t); }
                }
            }
            time1 = System.nanoTime();
            iterCount++;
        } while (time1-time0 < minTime);
        if (print) Console.OUT.println(prefix+"single activity: "+(time1-time0)/1E9/OUTER_ITERS/iterCount+" seconds");

        iterCount = 0;
        time0 = System.nanoTime();
        do {
            for (i in 1..OUTER_ITERS) {
                finish {
                    for (p in Place.places()) {
                        at (p) async { think(t); }
                    }
                }
            }
            time1 = System.nanoTime();
            iterCount++;
        } while (time1-time0 < minTime);
        if (print) Console.OUT.println(prefix+"flat fan out: "+(time1-time0)/1E9/OUTER_ITERS/iterCount+" seconds");

        iterCount = 0;
        time0 = System.nanoTime();
        do {
            for (i in 1..OUTER_ITERS) {
                finish {
                    for (p in Place.places()) {
                        at (p) async {
                            at (home) async { think(t); }
                        }
                    }
                }
            }
            time1 = System.nanoTime();
            iterCount++;
        } while (time1-time0 < minTime);
        if (print) Console.OUT.println(prefix+"flat fan out - message back: "+(time1-time0)/1E9/OUTER_ITERS/iterCount+" seconds");

        iterCount = 0;
        time0 = System.nanoTime();
        do {
            for (i in 1..OUTER_ITERS) {
                finish {
                    for (p in Place.places()) {
                        at (p) async {
                            finish {
                                for (j in 1..INNER_ITERS) {
                                    async { think(t); };
                                }
                            }
                        }
                    }
                }
            }
            time1 = System.nanoTime();
            iterCount++;
        } while (time1-time0 < minTime);
        if (print) Console.OUT.println(prefix+"fan out - internal work "+INNER_ITERS+" activities: "+(time1-time0)/1E9/OUTER_ITERS/iterCount+" seconds");

        iterCount = 0;
        time0 = System.nanoTime();
        do {
            finish {
                for (p in Place.places()) {
                    at (p) async {
                        for (q in Place.places()) at (q) async {
                            think(t);
                        }
                    }
                }
            }
            time1 = System.nanoTime();
            iterCount++;
        } while (time1-time0 < minTime);
        if (print) Console.OUT.println(prefix+"fan out - broadcast: "+(time1-time0)/1E9/iterCount+" seconds");

        iterCount = 0;
        time0 = System.nanoTime();
        do {
            finish {
                for (p in Place.places()) {
                    at (p) async {
                        finish {
                            for (q in Place.places()) at (q) async {
                                think(t);
                            }
                        }
                    }
                }
            }
            time1 = System.nanoTime();
            iterCount++;
        } while (time1-time0 < minTime);
        if (print) Console.OUT.println(prefix+"fan out - nested finish broadcast: "+(time1-time0)/1E9/iterCount+" seconds");

        iterCount = 0;
        time0 = System.nanoTime();
        do {
            downTree(t);
            time1 = System.nanoTime();
            iterCount++;
        } while (time1-time0 < minTime);
        if (print) Console.OUT.println(prefix+"tree fan out: "+(time1-time0)/1E9/iterCount+" seconds");

        iterCount = 0;
        val endPlace = Place.places().prev(here);
        time0 = System.nanoTime();
        do {
            ring(t, endPlace);
            time1 = System.nanoTime();
            iterCount++;
        } while (time1-time0 < minTime);
        if (print) Console.OUT.println(prefix+"ring around via at: "+(time1-time0)/1E9/iterCount+" seconds");
    }

    private static def downTree(thinkTime:long):void {
        think(thinkTime);
        val parent = here.id;
        val child1 = parent*2 + 1;
        val child2 = child1 + 1;
        if (parent == 1 && Place.numPlaces() == 2) {
            // special case to invert tree so scaling calcuation is reasonable.
            finish at (Place(0)) async think(thinkTime);
        }
        if (child1 < Place.numPlaces() || child2 < Place.numPlaces()) {
            finish {
                if (child1 < Place.numPlaces()) at (Place(child1)) async downTree(thinkTime);
                if (child2 < Place.numPlaces()) at (Place(child2)) async downTree(thinkTime);
            }
        }
    }

    private static def ring(thinkTime:long, destination:Place):void {
        think(thinkTime);
        if (destination == here) return;
        val nextHop = Place.places().next(here);
        at (nextHop) ring(thinkTime, destination);
    }


    public static def think(think:Long) {
        if (think == 0) return;
        val start = System.nanoTime();
        do {} while (System.nanoTime() - start < think);
    }
}
