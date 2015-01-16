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

import x10.matrix.DenseMatrix;
import x10.matrix.Vector;
import x10.matrix.util.Debug;
import x10.matrix.util.PlaceGroupBuilder;

import logreg.SeqLogReg;
import logreg.LogisticRegression;

/**
 * Test harness for Logistic Regression using GML
 */
public class RunLogReg {
    
    public static def main(args:Rail[String]): void {
        val opts = new OptionsParser(args, [
					    Option("h","help","this information"),
					    Option("v","verify","verify the parallel result against sequential computation"),
					    Option("p","print","print matrix V, vectors d and w on completion")
					    ], [
						Option("m","rows","number of rows, default = 10"),
						Option("n","cols","number of columns, default = 10"),
						Option("r","rowBlocks","number of row blocks, default = X10_NPLACES"),
						Option("c","colBlocks","number of columnn blocks; default = 1"),
						Option("d","density","nonzero density, default = 0.5"),
						Option("i","iterations","number of iterations, default = 2"),
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
	
        val mX = opts("m", 10);
        val nX = opts("n", 10);
        val skipPlaces = opts("s", 0n);

        if ((mX<=0) ||(nX<=0) || skipPlaces < 0 || skipPlaces >= Place.numPlaces()) {
            Console.OUT.println("Error in settings");
        } else {
            if (skipPlaces > 0)
                Console.OUT.println("Skipping "+skipPlaces+" places to reserve for failure.");

            val places = (skipPlaces==0n) ? Place.places() 
                : PlaceGroupBuilder.makeTestPlaceGroup(skipPlaces);

            val rowBlocks = opts("r", places.size());
            val colBlocks = opts("c", 1);
            val nonzeroDensity = opts("d", 0.5f);
            val iterations = opts("i", 2n);
            val verify = opts("v");
            val print = opts("p");
            val checkpointFreq = opts("checkpointFreq", -1n);

            Console.OUT.println("X: rows:"+mX+" cols:"+nX
                +" density:"+nonzeroDensity+" iterations:"+iterations);

            val prun = LogisticRegression.make(mX, nX, rowBlocks, colBlocks, nonzeroDensity, iterations, iterations, checkpointFreq, places);
	    
            var denX:DenseMatrix(mX,nX) = null;
            var y:Vector(mX) = null;
            var w:Vector(nX) = null;
            if (verify) {
                denX = prun.X.toDense();
                y = Vector.make(denX.M);
                prun.y.copyTo(y); // gather
                w = prun.w.clone();// as Vector(nX);
            }
	    
            Debug.flushln("Starting logistic regression");
			val startTime = Timer.milliTime();
			prun.run();
			val totalTime = Timer.milliTime() - startTime;

		    Console.OUT.printf("Parallel logistic regression --- Total: %8d ms, parallel: %8d ms, sequential: %8d ms, communication: %8d ms\n",
				    totalTime, prun.parCompT, prun.seqCompT, prun.commT);
			
			if (verify) { /* Sequential run */
				val seq = new SeqLogReg(denX, y, w, iterations, iterations);

		        Debug.flushln("Starting sequential logistic regression");
				seq.run();
                Debug.flushln("Verifying results against sequential version");
                Console.OUT.println("prun.w " + prun.w);
                Console.OUT.println("w " + w);
				if (prun.w.equals(w)) {
					Console.OUT.println("Verification passed.");
				} else {
                    Console.OUT.println("Verification failed!");
                }
	    }
	}
    }
}
