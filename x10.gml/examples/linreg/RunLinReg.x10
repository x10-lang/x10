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

import x10.matrix.DenseMatrix;
import x10.matrix.Vector;
import x10.matrix.block.BlockMatrix;
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
            Option("m","rows","number of rows, default = 10"),
            Option("n","cols","number of columns, default = 10"),
            Option("r","rowBlocks","number of row blocks, default = X10_NPLACES"),
            Option("c","colBlocks","number of columnn blocks; default = 1"),
            Option("d","density","nonzero density, default = 0.9"),
            Option("i","iterations","number of iterations, default = 2"),
            Option("s","skip","skip places count (at least one place should remain), default = 0"),
            Option("f","checkpointFreq","checkpoint iteration frequency")
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

        val mV = opts("m", 10);
        val nV = opts("n", 10);
        val rowBlocks = opts("r", Place.numPlaces());
        val colBlocks = opts("c", 1);
        val nonzeroDensity = opts("d", 0.9);
        val iterations = opts("i", 2n);
        val verify = opts("v");
        val print = opts("p");
        val skipPlaces = opts("s", 0n);
        val checkpointFrequency = opts("f", -1n);

        Console.OUT.println("V: rows:"+mV+" cols:"+nV
                           +" density:"+nonzeroDensity+" iterations:"+iterations);

        if (mV<=0 || nV<=0 || iterations<1 || nonzeroDensity<0.0
         || skipPlaces < 0 || skipPlaces >= Place.numPlaces()) {
            Console.OUT.println("Error in settings");
        } else {
            if (skipPlaces > 0)
                Console.OUT.println("Skipping "+skipPlaces+" places to reserve for failure.");
            val places = (skipPlaces==0n) ? Place.places() 
                                          : PlaceGroupBuilder.makeTestPlaceGroup(skipPlaces);

            // Create parallel linear regression
            val parLR = LinearRegression.make(mV, nV, 
                                              rowBlocks, colBlocks, 
                                              nonzeroDensity, iterations,
                                              checkpointFrequency, places);

            var V:DenseMatrix(mV, nV) = null;
            var b:Vector(nV) = null;
            if (verify) {
                val bV:BlockMatrix(parLR.V.M, parLR.V.N);
                if (nonzeroDensity < 0.1) {
                    bV = BlockMatrix.makeSparse(parLR.V.getGrid(), nonzeroDensity);
                } else {
                    bV = BlockMatrix.makeDense(parLR.V.getGrid());
                }
                V = DenseMatrix.make(mV, nV);
                b = Vector.make(nV);

                parLR.V.copyTo(bV as BlockMatrix(parLR.V.M, parLR.V.N));
                bV.copyTo(V);
                parLR.b.copyTo(b as Vector(parLR.b.M));
            }

            //Run the parallel linear regression
            Debug.flushln("Starting parallel linear regression");
            val startTime = Timer.milliTime();
            parLR.run();
            val totalTime = Timer.milliTime() - startTime;
			Console.OUT.printf("Parallel linear regression --- Total: %8d ms, parallel runtime: %8d ms, commu time: %8d ms\n",
					totalTime, parLR.parCompT, parLR.commT);

            if (print) {
                Console.OUT.println("Input sparse matrix V\n" + parLR.V);
                Console.OUT.println("Input dense matrix b\n" + parLR.b);
                Console.OUT.println("Output dense matrix w\n" + parLR.w);
            }

            if (verify) {
                // Create sequential version running on dense matrices
                val seqLR = new SeqLinearRegression(V, b, iterations);

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
