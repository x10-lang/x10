/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011-2014.
 */

import x10.util.Option;
import x10.util.OptionsParser;
import x10.util.Timer;

import x10.matrix.Vector;
import x10.matrix.util.Debug;
import x10.matrix.util.PlaceGroupBuilder;
import x10.matrix.util.VerifyTool;

/**
 * Page Rank demo
 * <p>
 * Execution input parameters:
 * <ol>
 * <li>Rows and columns of G. Default 10000</li>
 * <li>Iterations number. Default 20</li>
 * <li>Verification flag. Default 0 or false.</li>
 * <li>Row-wise partition of G. Default number of places</li>
 * <li>Column-wise partition of G. Default 1.</li>
 * <li>Nonzero density of G: Default 0.001f</li>
 * <li>Print output flag: Default false.</li>
 * </ol>
 */
public class RunPageRank {
    public static def main(args:Rail[String]): void {
        val opts = new OptionsParser(args, [
            Option("h","help","this information"),
            Option("v","verify","verify the parallel result against sequential computation"),
            Option("p","print","print matrix V, vectors d and w on completion")
        ], [
            Option("m","rows","number of rows, default = 100000"),
            Option("r","rowBlocks","number of row blocks, default = X10_NPLACES"),
            Option("c","colBlocks","number of columnn blocks; default = 1"),
            Option("d","density","nonzero density, default = 0.001"),
            Option("i","iterations","number of iterations, default = 20"),
            Option("s","skip","skip places count (at least one place should remain), default = 0"),
            Option("", "checkpointFreq","checkpoint iteration frequency")
        ]);

        if (opts.filteredArgs().size!=0) {
            Console.ERR.println("Unexpected arguments: "+opts.filteredArgs());
            Console.ERR.println("Use -h or --help.");
            System.setExitCode(1n);
            return;
        }
        if (opts("h")) {
            Console.OUT.println(opts.usage(""));
            return;
        }

        val mG = opts("m", 100000);
        val nonzeroDensity = opts("d", 0.001f);
        val iterations = opts("i", 20n);
        val verify = opts("v");
        val print = opts("p");
        val skipPlaces = opts("s", 0n);
        val checkpointFreq = opts("checkpointFreq", -1n);

        Console.OUT.printf("G: rows/cols %d density: %.3f (non-zeros: %d) iterations: %d\n",
                            mG, nonzeroDensity, (nonzeroDensity*mG*mG) as Long, iterations);
		if ((mG<=0) || iterations < 1n || nonzeroDensity <= 0.0 || skipPlaces < 0 || skipPlaces >= Place.numPlaces())
            Console.OUT.println("Error in settings");
        else {
            val places = (skipPlaces==0n) ? Place.places() 
                                          : PlaceGroupBuilder.makeTestPlaceGroup(skipPlaces);
            val rowBlocks = opts("r", places.size());
            val colBlocks = opts("c", 1);

            val paraPR = PageRank.make(mG, nonzeroDensity, iterations, rowBlocks, colBlocks, checkpointFreq, places);
            paraPR.init(nonzeroDensity);

            if (print) paraPR.printInfo();

            var origP:Vector(mG) = null;
            if (verify) {
                origP = paraPR.P.local().clone();
            }

			val startTime = Timer.milliTime();
            val paraP = paraPR.run();
			val totalTime = Timer.milliTime() - startTime;

			Console.OUT.printf("Parallel PageRank --- Total: %8d ms, parallel runtime: %8d ms, seq: %8d ms, commu time: %8d ms\n",
					totalTime, paraPR.paraRunTime, paraPR.seqTime, paraPR.commTime); 
            
            if (print) {
                Console.OUT.println("Input G sparse matrix\n" + paraPR.G);
                Console.OUT.println("Output vector P\n" + paraP);
            }
            
            if (verify) {
                val g = paraPR.G;
                val localU = Vector.make(g.N);
                paraPR.U.copyTo(localU);
                
                val seqPR = new SeqPageRank(g.toDense(), origP, 
                        localU, iterations);
		        Debug.flushln("Start sequential PageRank");
                val seqP = seqPR.run();
                Debug.flushln("Verifying results against sequential version");
                val localP = Vector.make(g.N);
                paraP.copyTo(localP);
                if (VerifyTool.testSame(localP, seqP)) 
                    Console.OUT.println("Verification passed.");
                else
                    Console.OUT.println("Verification failed!!!!");
            }
        }
    }
}

