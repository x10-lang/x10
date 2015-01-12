/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import x10.util.OptionsParser;
import x10.util.Option;
import x10.util.Random;
import x10.util.resilient.ResilientMap;

/**
 * Resilient calculation of PI using MonteCarlo simulation
 * with periodic checkpointing of partial results via 
 * x10.util.ResilientMap.  
 *
 * To run using the Hazelcast-based implementation of
 * x10.util.ResilientMap, invoke the x10 script like: 
 * <pre>
 * X10_RESILIENT_MODE=11 X10_NPLACES=4 x10 -DX10RT_IMPL=JavaSockets -DX10RT_DATASTORE=Hazelcast ResilientMontePiCheckpoint
 * </pre>
 */
public class ResilientMontePiCheckpoint {

    static class Result {
        var inCircle:long;
        var total:long;

        def accum(r:Result) {
            if (r != null) {
                inCircle += r.inCircle;
                total += r.total;
            }
        }

        def toString() = inCircle + " / " + total;

        def pi() = (4.0 * inCircle) / total;

        def this(c:long, t:long) { 
            inCircle = c;
            total = t;
        }
    }

    public static def main (args:Rail[String]) {
        if (Place.numPlaces() < 2) {
             Console.ERR.println("This program must be run with at least 2 places");
             System.setExitCode(-1n);
             return;
        }

        val opts = new OptionsParser(args, [
            Option("h","help","this information")
        ], [
            Option("s","samples","total number of samples"),
            Option("c","checkpoints","number of checkpoints per place")
        ]);
        if (opts.filteredArgs().size != 0) {
            Console.ERR.println("Unexpected arguments: "+opts.filteredArgs());
            Console.ERR.println("Use -h or --help.");
            System.setExitCode(1n);
            return;
        }
        if (opts("-h")) {
            Console.OUT.println(opts.usage(""));
            return;
        }

        val num_samples = opts("-s",1000000000);
        val num_checkpoints = opts("-c", 1000);

        val world = Place.places();

        val iters_per_place = num_samples / world.size();
        val inner_iters = num_checkpoints < 1 ? iters_per_place : iters_per_place / num_checkpoints;

        // Do the computation
        try {
            finish for (p in world) at (p) async {
                val myResultsMap = ResilientMap.getMap[Long,Result]("result");
                val rand = new Random(System.nanoTime());
                var inCircle:Long = 0;
                var total:Long = 0;
                for (cp in 1..num_checkpoints) {
                    for (1..inner_iters) {
                        val x = rand.nextDouble();
                        val y = rand.nextDouble();
                        if (x*x + y*y <= 1.0) inCircle++;
                        total++;
                    }
                    val tmp = new Result(inCircle, total);
                    myResultsMap.put(here.id, tmp);
                    Console.OUT.println(here+" checkpoint "+cp);
                }
                val myResult = new Result(inCircle, total);
                myResultsMap.put(here.id, myResult); 
                Console.OUT.println("Work done at: "+here +" "+myResult);
            }
        } catch (e:MultipleExceptions) {
            val dpes = e.getExceptionsOfType[DeadPlaceException]();
            for (dpe in dpes) {
                Console.OUT.println(dpe.place+ " failed during execution.");
            }
        }

        // Accumulate the results from the ResilientMap.
        val myResultsMap = ResilientMap.getMap[Long,Result]("result");
        val finalResult = new Result(0,0);
        for (p in world) {
            finalResult.accum(myResultsMap.get(p.id));
        }

        Console.OUT.println("pi = "+finalResult.pi()+"   calculated with "+finalResult.total+" samples.");
    }

}

// vim: shiftwidth=4:tabstop=4:expandtab
