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

import x10.matrix.DenseMatrix;
import x10.matrix.Vector;
import x10.matrix.util.Debug;
import x10.util.Team;
import x10.matrix.util.MathTool;
import x10.util.resilient.iterative.*;
import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.DistVector;
import x10.matrix.regression.RegressionInputData;


/**
 * Test harness for Logistic Regression using GML
 */
//Test command using SystemML generated files
//X10_NPLACES=8 x10 -classpath build:$X10_HOME/x10.gml/lib/managed_gml_double.jar -libpath $X10_HOME/x10.gml/native_double/lib RunLogReg -f systemml_input/10000_100_features.csv -l systemml_input/10000_100_labels.csv


//KILL_PLACES=4 KILL_STEPS=4 X10_RESILIENT_MODE=12 X10_LAUNCHER_TTY=false  X10_NPLACES=8 X10_NTHREADS=1 x10 -DX10RT_DATASTORE=native    -classpath build:$X10_HOME/x10.gml/lib/managed_gml_double.jar  -libpath $X10_HOME/x10.gml/native_double/lib RunLogReg -f systemml_input/1000_100_features.csv -l systemml_input/1000_100_labels.csv -k 3 -s 1
//KILL_PLACES=4 KILL_STEPS=12 X10_RESILIENT_MODE=12 X10_LAUNCHER_TTY=false  X10_NPLACES=8 X10_NTHREADS=1 x10 -DX10RT_DATASTORE=Hazelcast -classpath build:$X10_HOME/x10.gml/lib/managed_gml_double.jar  -libpath $X10_HOME/x10.gml/native_double/lib RunLogReg -f systemml_input/1000_100_features.csv -l systemml_input/1000_100_labels.csv -k 3 -s 1

public class RunLogReg {
    
    public static def main(args:Rail[String]): void {
        val opts = new OptionsParser(args, [
                        Option("h","help","this information"),
                        Option("v","verify","verify the parallel result against sequential computation"),
                        Option("p","print","print matrix V, vectors d and w on completion")
                        ], [
                        Option("f","featuresFile","input features file name"),
			            Option("l","labelsFile","input labels file name"),
			            Option("z","regularization","regularization parameter (lambda = 1/C); intercept is not regularized, default 0.0"),
                        Option("m","rows","number of rows, default = 10"),
                        Option("n","cols","number of columns, default = 10"),
                        Option("r","rowBlocks","number of row blocks, default = X10_NPLACES"),
                        Option("c","colBlocks","number of columnn blocks; default = 1"),
                        Option("d","density","nonzero density, default = 0.5"),
                        Option("i","iterations","number of outer (Newton) iterations, default = 100"),
                        Option("x","innerIterations","number of inner (conjugate gradient) iterations, default = number of columns (cols)"),
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
    
        var mX:Long = opts("m", 10);
        var nX:Long = opts("n", 10);
        val sparePlaces = opts("s", 0n);
        val checkpointFreq = opts("checkpointFreq", -1n);
        var nonzeroDensity:Float = opts("d", 0.5f);

        if ((mX<=0) ||(nX<=0) || sparePlaces < 0 || sparePlaces >= Place.numPlaces()) {
            Console.OUT.println("Error in settings");
        } else {
            if (sparePlaces > 0)
                Console.OUT.println("Using "+sparePlaces+" spare place(s).");
            
            val startTime = Timer.milliTime();
            val executor = new SPMDResilientIterativeExecutor(checkpointFreq, sparePlaces, false, true);
            val places = executor.activePlaces();
            val team = executor.team();         
            
            val rowBlocks = opts("r", places.size());
            val colBlocks = opts("c", 1);
            
            val regularization:Float = opts("z", 0.0f);
            val iterations = opts("i", 100n);
            val innerIterations = opts("x", 0n);
            val verify = opts("v");
            val print = opts("p");
            
            var bias:Boolean = false;
            val X:DistBlockMatrix;
            val y:DistVector(X.M);

            val featuresFile = opts("f", "");
            if (featuresFile.equals("")) {
                Console.OUT.printf("Logistic regression with random examples X(%d,%d) blocks(%dx%d) ", mX, nX, rowBlocks, colBlocks);
                Console.OUT.printf("dist(%dx%d) nonzeroDensity:%g\n", places.size(), 1, nonzeroDensity);

                if (nonzeroDensity < LogisticRegression.MAX_SPARSE_DENSITY) {
                    X = DistBlockMatrix.makeSparse(mX, nX, rowBlocks, colBlocks, places.size(), 1, nonzeroDensity, places, team);
                } else {
                    Console.OUT.println("Using dense matrix as non-zero density = " + nonzeroDensity);
                    X = DistBlockMatrix.makeDense(mX, nX, rowBlocks, colBlocks, places.size(), 1, places, team);
                }
                y = DistVector.make(X.M, places, team);

                finish for (place in places) at(place) async {
                    X.initRandom_local(1, 10);
                    y.initRandom_local(1, 10);
                }
            } else {
                bias = true;
                val labelsFile = opts("l", "");
                if (labelsFile.equals("")) {
                    Console.ERR.println("RunLogReg: missing labels file\ntry `RunLogReg -h ' for more information");
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
                    
                    /*
                     #[SYSTEMML] Convert "Y_vec" into indicator matrice:
                     if (min (Y_vec) <= 0) { 
                        # Category labels "0", "-1" etc. are converted into the largest label
                        max_y = max (Y_vec);
                        Y_vec  = Y_vec  + (- Y_vec  + max_y + 1) * ppred (Y_vec , 0.0, "<=");
                     }
                     **/
                    val max_y = y.max_local();
                    y.map_local((a:ElemType)=>{ (a <= 0.0)? max_y + 1.0 : a} );
                }
            }
            
            Console.OUT.println("X: rows:"+mX+" cols:"+nX
                +" density:"+nonzeroDensity+" iterations:"+iterations);

            val prun = new LogisticRegression(mX, nX, X, y, iterations, innerIterations, nonzeroDensity, regularization, bias, executor);
        
            val M = mX;
            val N = nX;
            
            var denX:DenseMatrix(M,N) = null;
            var l:Vector(M) = null;
            if (verify) {
                denX = prun.X.toDense();
                l = Vector.make(denX.M);
                prun.y.copyTo(l); // gather
            }
        
            Debug.flushln("Starting logistic regression");
            
            val weightsPar = prun.train(startTime);
                        
            if (verify) { // Sequential run 
                val seq = new SeqLogReg(mX, nX, denX, l,
                    iterations, innerIterations, regularization, bias);

                Debug.flushln("Starting sequential logistic regression");
                val weightsSeq = seq.run();
                Debug.flushln("Verifying results against sequential version");
                
                Console.OUT.println("w_parallel: " + weightsPar.toString());
                Console.OUT.println("w_sequential: " + weightsSeq.toString());
                
                
                if (equalsRespectNaN(weightsPar, weightsSeq)) {
                    Console.OUT.println("Verification passed.");
                } else {
                    Console.OUT.println("Verification failed!");
                }
            }
            
        }
    }
    
    /*
     * Vector.equals(Vector) modified to allow NaN.
     */
    public static def equalsRespectNaN(w:Vector, v:Vector):Boolean {
        val M = w.M;
        if (M != v.M) return false;
        for (var c:Long=0; c< M; c++) {
            if (MathTool.isZero(w.d(c) - v.d(c)) == false && !(w.d(c).isNaN() && v.d(c).isNaN())) {
                Console.OUT.println("Diff found [" + c + "] : "+
                        w.d(c) + " <> "+ v.d(c));
                return false;
            }
        }
        return true;
    }
}
