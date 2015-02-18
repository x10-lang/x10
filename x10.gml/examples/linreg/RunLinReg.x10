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
import x10.xrx.Runtime;

import x10.matrix.DenseMatrix;
import x10.matrix.Vector;
import x10.matrix.ElemType;

import x10.matrix.block.BlockMatrix;
import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.DistVector;
import x10.matrix.regression.RegressionInputData;
import x10.matrix.util.Debug;
import x10.matrix.util.MathTool;
import x10.matrix.util.PlaceGroupBuilder;

import linreg.LinearRegression;
import linreg.SeqLinearRegression;

/**
 * Test harness for Linear Regression using GML
 */
public class RunLinReg {

    public static def main(args:Rail[String]): void {
        val opts = new OptionsParser(args, [
            Option("h","help","this information"),
            Option("v","verify","verify the parallel result against sequential computation"),
            Option("p","print","print matrix V, vectors d and w on completion")
        ], [
            Option("f","inputFile","input file name"),
            Option("m","rows","number of rows, default = 10"),
            Option("n","cols","number of columns, default = 10"),
            Option("r","rowBlocks","number of row blocks, default = X10_NPLACES"),
            Option("c","colBlocks","number of columnn blocks; default = 1"),
            Option("d","density","nonzero density, default = 0.9"),
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
        if (opts.wantsUsageOnly("Options:\n")) {
            return;
        }

        val inputFile = opts("f", "");
        var mX:Long = opts("m", 10);
        var nX:Long = opts("n", 10);
        var nonzeroDensity:Float = opts("d", 0.9f);
        val verify = opts("v");
        val print = opts("p");
        val iterations = opts("i", 2n);
        val skipPlaces = opts("s", 0n);

        if (iterations<1 || nonzeroDensity<0.0f
         || skipPlaces < 0 || skipPlaces >= Place.numPlaces()) {
            Console.OUT.println("Error in settings");
            System.setExitCode(1n);
            return;
        }

        if (skipPlaces > 0) {
            if (Runtime.RESILIENT_MODE <= 0) {
                Console.ERR.println("Error: attempt to skip places when not in resilient mode.  Aborting.");
                System.setExitCode(1n);
                return;
            } else {
                Console.OUT.println("Skipping "+skipPlaces+" places to reserve for failure.");
            }
        }
        val places = (skipPlaces==0n) ? Place.places() 
                                      : PlaceGroupBuilder.makeTestPlaceGroup(skipPlaces);

        val rowBlocks = opts("r", places.size());
        val colBlocks = opts("c", 1);

        val X:DistBlockMatrix;
        val y:DistVector(X.M);
        if (inputFile.equals("")) {
            Console.OUT.printf("Linear regression with random examples X(%d,%d) blocks(%dx%d) ", mX, nX, rowBlocks, colBlocks);
            Console.OUT.printf("dist(%dx%d) nonzeroDensity:%g\n", places.size(), 1, nonzeroDensity);

            if (nonzeroDensity < LinearRegression.MAX_SPARSE_DENSITY) {
                X = DistBlockMatrix.makeSparse(mX, nX, rowBlocks, colBlocks, places.size(), 1, nonzeroDensity, places);
            } else {
                Console.OUT.println("Using dense matrix as non-zero density = " + nonzeroDensity);
                X = DistBlockMatrix.makeDense(mX, nX, rowBlocks, colBlocks, places.size(), 1, places);
            }
            y = DistVector.make(X.M, places);

            X.initRandom();
            y.initRandom();
        } else {
            val inputData = RegressionInputData.readFromFile(inputFile, places, false, 1.0 as ElemType, false);
            mX = inputData.numTraining;
            nX = inputData.numFeatures;
            nonzeroDensity = 1.0f;
            
            X = DistBlockMatrix.makeDense(mX, nX, rowBlocks, colBlocks, places.size(), 1, places);
            y = DistVector.make(X.M, places);

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
                    blk.init((i:Long, j:Long)=> trainingExamples((i-startRow)*numFeatures+j));
                }
                y.distV().init((i:Long)=> trainingLabels(i));
            }
        }

        val M = mX;
        val N = nX;
        val checkpointFrequency = opts("checkpointFreq", -1n);

        val parLR = new LinearRegression(X, y, iterations, checkpointFrequency,
                                         nonzeroDensity, places);

        var localX:DenseMatrix(M, N) = null;
        var localY:Vector(M) = null;
        if (verify) {
            val bX:BlockMatrix(parLR.V.M, parLR.V.N);
            if (nonzeroDensity < 0.1f) {
                bX = BlockMatrix.makeSparse(parLR.V.getGrid(), nonzeroDensity);
            } else {
                bX = BlockMatrix.makeDense(parLR.V.getGrid());
            }
            localX = DenseMatrix.make(M, N);
            localY = Vector.make(M);

            X.copyTo(bX as BlockMatrix(parLR.V.M, parLR.V.N));
            bX.copyTo(localX);
            y.copyTo(localY as Vector(y.M));
        }

        Debug.flushln("Starting parallel linear regression");
        val startTime = Timer.milliTime();
        parLR.run();
        val totalTime = Timer.milliTime() - startTime;
		Console.OUT.printf("Parallel linear regression --- Total: %8d ms, parallel: %8d ms, sequential: %8d ms, communication: %8d ms\n",
				totalTime, parLR.parCompT, parLR.seqCompT, parLR.commT);

        if (print) {
            Console.OUT.println("Input sparse matrix X\n" + X);
            Console.OUT.println("Input dense matrix y\n" + y);
            Console.OUT.println("Output dense matrix w\n" + parLR.w);
        }

        if (verify) {
            // Create sequential version running on dense matrices
            val seqLR = new SeqLinearRegression(localX, localY, iterations);

            Debug.flushln("Starting sequential linear regression");
            seqLR.run();
            Debug.flushln("Verifying results against sequential version");

            if (equalsRespectNaN(parLR.w, seqLR.w as Vector(parLR.w.M))) {
                Console.OUT.println("Verification passed.");
            } else {
                Console.OUT.println("Verification failed!");
            }
        }
    }

    /*
     * Vector.equals(Vector) modified to allow NaN.
     */
    public static def equalsRespectNaN(w:Vector, v:Vector):Boolean {
        val M = w.M;
        if (M != v.M) return false;
        for (var c:Long=0; c< M; c++)
            if (MathTool.isZero(w.d(c) - v.d(c)) == false && !(w.d(c).isNaN() && v.d(c).isNaN())) {
                Console.OUT.println("Diff found [" + c + "] : "+
                                    w.d(c) + " <> "+ v.d(c));
                return false;
            }
        return true;
    }
}
