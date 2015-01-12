/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011-2015.
 */

import x10.util.Option;
import x10.util.OptionsParser;
import x10.util.Timer;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.block.Grid;
import x10.matrix.dist.DistSparseMatrix;
import x10.matrix.util.Debug;

public class RunLogReg {

	public static def main(args:Rail[String]): void {
        val opts = new OptionsParser(args, [
            Option("h","help","this information"),
            Option("v","verify","verify the parallel result against sequential computation")
        ], [
            Option("m","rows","number of rows, default = 10"),
            Option("n","cols","number of columns, default = 10"),
            Option("d","density","nonzero density, default = 0.5"),
            Option("i","iterations","number of iterations, default = 2")
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
        val nonzeroDensity = opts("d", 0.5);
        val iterations = opts("i", 2n);
        val verify = opts("v");

        Console.OUT.println("X: rows:"+mX+" cols:"+nX
                           +" density:"+nonzeroDensity+" iterations:"+iterations);
		if ((mX<=0) ||(nX<=0))
			Console.OUT.println("Error in settings");
		else {
			val prt = new Grid(mX, nX, Place.numPlaces(), 1);
			val X = DistSparseMatrix.make(prt, nonzeroDensity) as DistSparseMatrix(mX, nX);
			val y = DenseMatrix.make(X.M, 1);
			val w = DenseMatrix.make(X.N, 1);
			
			//X = Rand(rows = 1000, cols = 1000, min = 1, max = 10, pdf = "uniform");
			X.initRandom(1, 10);
			//y = Rand(rows = 1000, cols = 1, min = 1, max = 10, pdf = "uniform");
			y.initRandom(1, 10);
			w.initRandom();
			
			val prun = new LogisticRegression(X, y, w, iterations, iterations);
		
            Debug.flushln("Starting logistic regression");
			val startTime = Timer.milliTime();
			prun.run();
			val totalTime = Timer.milliTime() - startTime;

			Console.OUT.printf("Parallel logistic regression --- Total: %8d ms, parallel runtime: %8d ms, commu time: %8d ms\n",
					totalTime, prun.paraRunTime, prun.commUseTime); 
			
			if (verify) { /* Sequential run */
				val denX = X.toDense();
			    val yt = y.clone();
			    val wt = w.clone();
				val seq = new SeqLogReg(denX, yt, wt, iterations, iterations);

		        Debug.flushln("Starting sequential logistic regression");
				seq.run();
                Debug.flushln("Verifying results against sequential version");
				
				if (w.equals(wt as Matrix(w.M, w.N))) {
					Console.OUT.println("Verification passed.");
				} else {
                    Console.OUT.println("Verification failed!");
				}
			}
		}
	}
}
