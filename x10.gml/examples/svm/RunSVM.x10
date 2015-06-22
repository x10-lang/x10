/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2014-2015.
 */

import x10.util.Option;
import x10.util.OptionsParser;
import x10.util.Timer;

import x10.matrix.DenseMatrix;
import x10.matrix.Vector;
import x10.matrix.block.BlockMatrix;
import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.DistVector;
import x10.matrix.regression.RegressionInputData;
import x10.matrix.util.Debug;
import x10.matrix.util.VerifyTool;

/**
 * Test harness for Support Vector Machine using GML
 */
public class RunSVM {
    public static def main(args:Rail[String]):void {
        val opts = new OptionsParser(args, [
            Option("h","help","this information"),
            Option("l","libsvmInput","use libsvm input file format"),
            Option("v","verify","verify the parallel result against sequential computation"),
            Option("p","print","print matrix X, vector w on completion")
        ], [
            Option("f","inputFile","input file name"),
            Option("i","iterations","number of iterations, default = 30"),
            Option("t","trainingFraction","fraction of examples to be used as training data, default = 0.6"),
            Option("s","stepSize","initial step size, default = 1.0"),
            Option("r","regularization","regularization parameter, default = 0.1")
        ]);

        if (opts.filteredArgs().size!=0) {
            Console.ERR.println("Unexpected arguments: "+opts.filteredArgs());
            Console.ERR.println("Use -h or --help.");
            System.setExitCode(1n);
            return;
        }
        if (opts.wantsUsageOnly("Options:\n")) {
            return;
        }

        val inputFile = opts("f", "");
        if (inputFile.equals("")) {
            Console.ERR.println("svm: missing input file\ntry `svm -h ' for more information");
            System.setExitCode(1n);
            return;
        }
        val iterations = opts("i", 30n);
        val trainingFraction = opts("t", 0.6);
        val stepSize = opts("s", 1.0);
        val regularization = opts("r", 0.1);
        val libsvmInput = opts("l");
        val verify = opts("v");
        val print = opts("p");

        val places = Place.places();
        val addBias = true;
        val inputData = RegressionInputData.readFromFile(inputFile, places, libsvmInput, trainingFraction, addBias);

        val mX = inputData.numTraining;
        val nX = inputData.numFeatures+1;
        
        val X = DistBlockMatrix.makeDense(mX, nX, places.size(), 1, places.size(), 1, places);
        val y = DistVector.make(X.M, places);

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
            y.distV().init((i:Long)=> trainingLabels(i));
        }

        var localX:DenseMatrix(mX, nX) = null;
        var localY:Vector(mX) = null;
        if (verify) {
            val bX = BlockMatrix.makeDense(X.getGrid());
            localX = DenseMatrix.make(mX, nX);

            localY = Vector.make(mX);

            X.copyTo(bX as BlockMatrix(X.M, X.N));
            bX.copyTo(localX);

            y.copyTo(localY as Vector(y.M));
        }

        Debug.flushln("Training SVM with stepSize: " + stepSize
            + ", regularization: " + regularization
            + " for " + iterations + " iterations...");

        val parSVM = new SVM(nX, places).train(X, y, stepSize, regularization, iterations);

        if (inputData.numTest > 0) {
            Debug.flushln("Attempting to predict " + inputData.numTest
                + " test examples...");

            val totalSuccessful = finish(new Reducible.SumReducer[Long]()) {
                for (place in places) at(place) async {
                    var successful:Long = 0;
                    for (i in 0..(inputData.local().testLabels.size()-1)) {
                        val testExample = inputData.local().testExamples.moveSectionToRail(0, inputData.numFeatures);
                        val test = new Vector(testExample);
                        val raw = parSVM.predict(test);
                        val predicted = (raw < 0.0 ? 0.0 : 1.0);
                        val actual = inputData.local().testLabels(i);
                        if (predicted == actual) successful++;
                    }
                    offer successful;
                }
            };
            Console.OUT.printf("Success rate: %5.3f\n", totalSuccessful as Double / inputData.numTest);
        }

        if (verify) {
            Debug.flushln("Verifying results against sequential version");
            val seqSVM = new SeqSVM(nX).train(localX, localY, stepSize, regularization, iterations);

            if (VerifyTool.testSame(parSVM.w.local(), seqSVM.w)) {
                Console.OUT.println("Verification passed.");
            } else {
                Console.OUT.println("Verification failed!");
            }
        }
    }
}
