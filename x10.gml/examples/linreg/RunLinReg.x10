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
import x10.util.Team;
import x10.util.resilient.iterative.SPMDResilientIterativeExecutor;

/**
 * Test harness for Linear Regression using GML
 */
//Example run commands to use Hazelcast or native stores
//KILL_PLACES=4 KILL_STEPS=12 X10_RESILIENT_MODE=12 X10_LAUNCHER_TTY=false  X10_NPLACES=8 X10_NTHREADS=1 x10 -DX10RT_DATASTORE=native -classpath build:$X10_HOME/x10.gml/lib/managed_gml_double.jar  -libpath $X10_HOME/x10.gml/native_double/lib RunLinReg  -m 1000 -n 1000 --density 1.0 --iterations 30 --verify -k 10 -s 1
//KILL_PLACES=4 KILL_STEPS=12 X10_RESILIENT_MODE=12 X10_LAUNCHER_TTY=false  X10_NPLACES=8 X10_NTHREADS=1 x10 -DX10RT_DATASTORE=Hazelcast -classpath build:$X10_HOME/x10.gml/lib/managed_gml_double.jar  -libpath $X10_HOME/x10.gml/native_double/lib RunLinReg  -m 1000 -n 1000 --density 1.0 --iterations 30 --verify -k 10 -s 1
public class RunLinReg {

    public static def main(args:Rail[String]): void {
        val opts = new OptionsParser(args, [
            Option("h","help","this information"),
            Option("v","verify","verify the parallel result against sequential computation"),
            Option("p","print","print matrix V, vectors d and w on completion")
        ], [
			Option("f","featuresFile","input features file name"),
			Option("l","labelsFile","input labels file name"),
			Option("z","regularization","regularization parameter (lambda = 1/C); intercept is not regularized"),
            Option("m","rows","number of rows, default = 10"),
            Option("n","cols","number of columns, default = 10"),
            Option("r","rowBlocks","number of row blocks, default = X10_NPLACES"),
            Option("c","colBlocks","number of columnn blocks; default = 1"),
            Option("d","density","nonzero density, default = 0.9"),
            Option("i","iterations","number of iterations, default = 0 (no max)"),
            Option("t","tolerance","convergence tolerance, default = 0.000001"),
            Option("s","spare","spare places count (at least one place should remain), default = 0"),
            Option("k", "checkpointFreq","checkpoint iteration frequency")
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

        val regularization:Float = opts("z", 0.000001f);
        var mX:Long = opts("m", 10);
        var nX:Long = opts("n", 10);
        var nonzeroDensity:Float = opts("d", 0.9f);
        val verify = opts("v");
        val print = opts("p");
        val iterations = opts("i", 0n);
        val tolerance = opts("t", 0.000001f);
        val sparePlaces = opts("s", 0n);
        val checkpointFrequency = opts("checkpointFreq", -1n);

        if (nonzeroDensity<0.0f
         || sparePlaces < 0 || sparePlaces >= Place.numPlaces()) {
            Console.OUT.println("Error in settings");
            System.setExitCode(1n);
            return;
        }

        if (sparePlaces > 0) {
            if (Runtime.RESILIENT_MODE <= 0) {
                Console.ERR.println("Error: attempt to skip places when not in resilient mode.  Aborting.");
                System.setExitCode(1n);
                return;
            } else {
                Console.OUT.println("Skipping "+sparePlaces+" places to reserve for failure.");
            }
        }
        
        val startTime = Timer.milliTime();

        val executor = new SPMDResilientIterativeExecutor(checkpointFrequency, sparePlaces, false, true);
        val places = executor.activePlaces();
        val team = executor.team();
        
        val rowBlocks = opts("r", places.size());
        val colBlocks = opts("c", 1);

        val X:DistBlockMatrix;
        val y:DistVector(X.M);

        val featuresFile = opts("f", "");
        if (featuresFile.equals("")) {
            Console.OUT.printf("Linear regression with random examples X(%d,%d) blocks(%dx%d) ", mX, nX, rowBlocks, colBlocks);
            Console.OUT.printf("dist(%dx%d) nonzeroDensity:%g\n", places.size(), 1, nonzeroDensity);

            if (nonzeroDensity < LinearRegression.MAX_SPARSE_DENSITY) {
                X = DistBlockMatrix.makeSparse(mX, nX, rowBlocks, colBlocks, places.size(), 1, nonzeroDensity, places, team);
            } else {
                Console.OUT.println("Using dense matrix as non-zero density = " + nonzeroDensity);
                X = DistBlockMatrix.makeDense(mX, nX, rowBlocks, colBlocks, places.size(), 1, places, team);
            }
            y = DistVector.make(X.M, places, team);

            finish for (place in places) at(place) async {
                X.initRandom_local();
                y.initRandom_local();
            }
        } else {
            val labelsFile = opts("l", "");
            if (labelsFile.equals("")) {
                Console.ERR.println("RunLinReg: missing labels file\ntry `RunLinReg -h ' for more information");
                System.setExitCode(1n);
                return;
            }
            val addBias = true;
            val trainingFraction = 1.0;
            val inputData = RegressionInputData.readFromSystemMLFile(featuresFile, labelsFile, places, trainingFraction, addBias);
            mX = inputData.numTraining;
            nX = inputData.numFeatures+1; // including bias
            nonzeroDensity = 1.0f; // TODO allow sparse input
            
            X = DistBlockMatrix.makeDense(mX, nX, rowBlocks, colBlocks, places.size(), 1, places, team);
            y = DistVector.make(X.M, places, team);

            // initialize labels, examples at each place
            finish for (place in places) at(place) async {
                val trainingLabels = inputData.local().trainingLabels;
                val trainingExamples = inputData.local().trainingExamples;
                val startRow = X.getGrid().startRow(places.indexOf(place));
                val blks = X.handleBS();
                val blkitr = blks.iterator();
                while (blkitr.hasNext()) {
                    val blk = blkitr.next();              
                    blk.init((i:Long, j:Long)=> trainingExamples((i-startRow)*X.N+j));
                }
                y.init_local((i:Long)=> trainingLabels(i));
            }
        }

        val M = mX;
        val N = nX;

        val parLR = new LinearRegression(X, y, iterations, tolerance, nonzeroDensity, regularization, executor);

        var localX:DenseMatrix(M, N) = null;
        var localY:Vector(M) = null;
        if (verify) {
            val bX:BlockMatrix(parLR.X.M, parLR.X.N);
            if (nonzeroDensity < 0.1f) {
                bX = BlockMatrix.makeSparse(parLR.X.getGrid(), nonzeroDensity);
            } else {
                bX = BlockMatrix.makeDense(parLR.X.getGrid());
            }
            localX = DenseMatrix.make(M, N);
            localY = Vector.make(M);

            X.copyTo(bX as BlockMatrix(parLR.X.M, parLR.X.N));
            bX.copyTo(localX);
            y.copyTo(localY as Vector(y.M));
        }

        Debug.flushln("Starting parallel linear regression");
        parLR.run(startTime);
        //val totalTime = Timer.milliTime() - startTime;
		//Console.OUT.printf("Parallel linear regression --- Total: %8d ms, parallel: %8d ms, sequential: %8d ms, communication: %8d ms\n",
		//		totalTime, parLR.parCompT, parLR.seqCompT, parLR.commT);
        //parLR.printTimes();

        if (print) {
            //Console.OUT.println("Input sparse matrix X\n" + X);
            //Console.OUT.println("Input dense matrix y\n" + y);
            Console.OUT.println("Output estimated weights: \n" + parLR.getResult());
        }

        if (verify) {
            // Create sequential version running on dense matrices
            val seqLR = new SeqLinearRegression(localX, localY, iterations, tolerance);

            Debug.flushln("Starting sequential linear regression");
            seqLR.run();
            Debug.flushln("Verifying results against sequential version");
            
            if (equalsRespectNaN(parLR.getResult(), seqLR.w as Vector(parLR.getResult().M))) {
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
