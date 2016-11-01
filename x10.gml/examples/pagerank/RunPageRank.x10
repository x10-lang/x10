/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011-2016.
 */

import x10.util.Option;
import x10.util.OptionsParser;
import x10.util.Timer;

import x10.util.resilient.iterative.*;

import x10.matrix.Vector;
import x10.matrix.util.Debug;
import x10.matrix.util.VerifyTool;
import x10.matrix.distblock.DistBlockMatrix;


/**
 * Page Rank demo
 */
//Example run commands to use Hazelcast or native store
//KILL_PLACES=4 KILL_STEPS=12 X10_RESILIENT_MODE=12 X10_LAUNCHER_TTY=false GML_ELEM_TYPE=double X10_NPLACES=8 X10_NTHREADS=1 x10 -DX10RT_DATASTORE=native -classpath build:$X10_HOME/x10.gml/lib/managed_gml_double.jar -libpath $X10_HOME/x10.gml/native_double/lib RunPageRank -m 100 --density 0.8 --iterations 30 -k 10 -s 2
//KILL_PLACES=4 KILL_STEPS=12 X10_RESILIENT_MODE=12 X10_LAUNCHER_TTY=false GML_ELEM_TYPE=double X10_NPLACES=8 X10_NTHREADS=1 x10 -DX10RT_DATASTORE=Hazelcast -classpath build:$X10_HOME/x10.gml/lib/managed_gml_double.jar -libpath $X10_HOME/x10.gml/native_double/lib RunPageRank -m 100 --density 0.8 --iterations 30 -k 10 -s 2 
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
            Option("d","density","nonzero density, default = log-normal"),
            Option("i","iterations","number of iterations, default = 0 (run until convergence)"),
            Option("t","tolerance","convergence tolerance, default = 0.0001"),
            Option("s","spare","spare places count (at least one place should remain), default = 0"),
            Option("k", "checkpointFreq","checkpoint iteration frequency")
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

        val nonzeroDensity = opts("d", 0.0f);
        val iterations = opts("i", 0n);
        val tolerance = opts("t", 0.0001f);
        val verify = opts("v");
        val print = opts("p");
        val sparePlaces = opts("s", 0n);
        val checkpointFreq = opts("checkpointFreq", -1n);
        val placesCount = Place.numPlaces() - sparePlaces;
        
        val mG = opts("m", (20000*Math.sqrt(placesCount*5)) as Long );
        
        Console.OUT.printf("G: rows/cols %d iterations: %d\n", mG, iterations);
        if ((mG<=0) || sparePlaces < 0 || sparePlaces >= Place.numPlaces())
            Console.OUT.println("Error in settings");
        else {
            val startTime = Timer.milliTime();
            val executor = new SPMDResilientIterativeExecutor(checkpointFreq, sparePlaces, false, true);
            val places = executor.activePlaces();
            
            val rowBlocks = opts("r", places.size());
            val colBlocks = opts("c", 1);

            val paraPR:PageRank;
            if (nonzeroDensity > 0.0f) {
                paraPR = PageRank.makeRandom(mG, nonzeroDensity, iterations, tolerance, rowBlocks, colBlocks, executor);
                Console.OUT.printf("random edge graph (uniform distribution) density: %.3e non-zeros: %d\n",
                            nonzeroDensity, (nonzeroDensity*mG*mG) as Long);
            } else {
                paraPR = PageRank.makeLogNormal(mG, iterations, tolerance, rowBlocks, colBlocks, executor);
                Console.OUT.println("log-normal edge graph (mu=4.0, sigma=1.3) total non-zeros: " + paraPR.G.getTotalNonZeroCount());
            }
/*
            // toy example copied from Spark (users/followers)
            val M = 6;
            val G = DistBlockMatrix.makeDense(M, M, Place.numPlaces(), 1);
            G(0,1) = 1.0;
            G(0,3) = 1.0;
            G(1,0) = 1.0;
            G(2,4) = 1.0;
            G(2,5) = 1.0;
            G(4,5) = 1.0;
            G(5,4) = 1.0;
            G(5,2) = 1.0;
            val paraPR = new PageRank(G, iterations, tolerance, 1.0f, 0, Place.places());
            Console.OUT.println("P = " + paraPR.P);
*/

            if (print) paraPR.printInfo();

            val paraP = paraPR.run(startTime);
            
            if (print) {
                Console.OUT.println("Input G sparse matrix\n" + paraPR.G);
                Console.OUT.println("Output vector P\n" + paraP);
            }
            
            if (verify) {
                val g = paraPR.G;
                val localU = Vector.make(g.N);
                
                //paraPR.U.copyTo(localU);
                
                val seqPR = new SeqPageRank(g.toDense(), iterations, tolerance);
                Debug.flushln("Start sequential PageRank");
                val seqP = seqPR.run();
                if (print) {
                    Console.OUT.println("Seq output vector P\n" + seqP);
                }
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

