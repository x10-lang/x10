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

import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.DistVector;
import x10.matrix.regression.RegressionInputData;
import x10.util.resilient.PlaceManager;

import x10.util.Team;

/**
 * Run Logistic Regression using GML against input files created by SystemML
 * @see https://systemml.apache.org/
 */
public class CompareLogRegSystemML {
    
    public static def main(args:Rail[String]): void {
        val opts = new OptionsParser(args, [
					    Option("h","help","this information"),
					    Option("v","verify","verify the parallel result against sequential computation"),
					    Option("p","print","print weights on completion")
					    ], [
						Option("f","featuresFile","input features file name"),
						Option("l","labelsFile","input labels file name"),
						Option("z","regularization","regularization parameter (lambda = 1/C); intercept is not regularized"),
						Option("i","iterations","number of outer (Newton) iterations, default = 100"),
						Option("x","innerIterations","number of inner (conjugate gradient) iterations, default = 0 (no max)"),
						Option("r","rowBlocks","number of row blocks, default = X10_NPLACES"),
						Option("c","colBlocks","number of columnn blocks; default = 1"),
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

        val featuresFile = opts("f", "");
        if (featuresFile.equals("")) {
            Console.ERR.println("logReg: missing features file\ntry `CompareLogRegSystemML -h ' for more information");
            System.setExitCode(1n);
            return;
        }
        val labelsFile = opts("l", "");
        if (labelsFile.equals("")) {
            Console.ERR.println("logReg: missing labels file\ntry `CompareLogRegSystemML -h ' for more information");
            System.setExitCode(1n);
            return;
        }
        val iter = opts("i", 100n);
        val innerIter = opts("x", 0n);
        val print = opts("p");
        val checkpointFreq = opts("checkpointFreq", -1n);
        val skipPlaces = opts("s", 0n);
        if (skipPlaces < 0 || skipPlaces >= Place.numPlaces()) {
            Console.OUT.println("Error in settings");
        } else {
            if (skipPlaces > 0) {
                Console.OUT.println("Skipping "+skipPlaces+" places to reserve for failure.");
            }
        }

        val startTime = Timer.milliTime();

        val manager = new PlaceManager(skipPlaces, false);
        val places = manager.activePlaces();
        val rowBlocks = opts("r", places.size());
        val colBlocks = opts("c", 1);

        val team = new Team(places);
        val addBias = true;
        val trainingFraction = 1.0;
        val inputData = RegressionInputData.readFromSystemMLFile(featuresFile, labelsFile, places, trainingFraction, addBias);
        val mX = inputData.numTraining;
        val nX = inputData.numFeatures+1;
        
        val X = DistBlockMatrix.makeDense(mX, nX, places.size(), 1);
        val y = DistVector.make(X.M);

        // initialize labels, examples at each place
        finish for (place in places) at(place) async {
            val numFeatures = inputData.numFeatures;
            val trainingLabels = inputData.local().trainingLabels;
            val trainingExamples = inputData.local().trainingExamples;
            val startRow = X.getGrid().startRow(places.indexOf(place));
            val blks = X.handleBS();
            val blkitr = blks.iterator();
            while (blkitr.hasNext()) {
                val blk = blkitr.next();
                // examples have an additional feature appended for the intercept
                blk.init((i:Long, j:Long)=> trainingExamples((i-startRow)*nX+j));
            }
            y.init_local((i:Long)=> trainingLabels(i));
        }

        val prun = new LogisticRegression(nX, X, y, iter, innerIter, 1.0f, checkpointFreq, places, team);
        prun.train(startTime);
        Console.OUT.println("w_parallel: " + prun.w.local().toString());
    }
}
