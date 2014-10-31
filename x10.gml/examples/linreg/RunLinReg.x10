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

import x10.matrix.util.Debug;
import x10.matrix.util.MathTool;
import x10.matrix.DenseMatrix;
import x10.matrix.Vector;
import x10.matrix.block.BlockMatrix;

import linreg.LinearRegression;
import linreg.SeqLinearRegression;
import x10.matrix.util.PlaceGroupBuilder;
import x10.matrix.distblock.DistBlockMatrix;

/**
 * Demo of linear regression test
 */
public class RunLinReg {

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
        if (opts("h")) {
            Console.OUT.println(opts.usage(""));
            return;
        }

        val mV = opts("m", 10);
        val nV = opts("n", 10);
        val mB = opts("r", Place.numPlaces());
        val nB = opts("c", 1);
        val nZ = opts("d", 0.9);
        val iT = opts("i", 2n);
        val vf = opts("v");
        val pP = opts("p");
        val sP = opts("s", 0n);
        val cI = opts("f", -1n);

        Console.OUT.println("Set row V:"+mV+" col V:"+nV+" density:"+nZ+" iteration:"+iT+" skipPlaces:"+sP);

        if (mV<=0 || nV<=0 || iT<1 || nZ<0.0 || sP < 0 || sP >= Place.numPlaces())
            Console.OUT.println("Error in settings");
        else {
            val places:PlaceGroup = (sP==0n? Place.places() :PlaceGroupBuilder.makeTestPlaceGroup(sP));

            // Create parallel linear regression
            val parLR = LinearRegression.make(mV, nV, mB, nB, nZ, iT, cI, places);

            //Run the parallel linear regression
            Debug.flushln("Starting parallel linear regression");
            val tt:Long = Timer.milliTime();
            parLR.run();
            val totaltime = Timer.milliTime() - tt;
            Debug.flushln("Parallel linear regression --- total:"+totaltime+" ms "+
                            "commuTime:"+parLR.commT+" ms " +
                            "paraComp:"+parLR.parCompT + " ms");

            if (pP) {
                Console.OUT.println("Input sparse matrix V\n" + parLR.V);
                Console.OUT.println("Input dense matrix b\n" + parLR.b);
                Console.OUT.println("Output dense matrix w\n" + parLR.w);
            }

            if (vf) {
                // Create sequential version running on dense matrices
                val bV = BlockMatrix.makeSparse(parLR.V.getGrid(), nZ);
                val V = DenseMatrix.make(mV, nV);
                val b = Vector.make(nV);

                parLR.V.copyTo(bV as BlockMatrix(parLR.V.M, parLR.V.N));
                bV.copyTo(V);
                parLR.b.copyTo(b as Vector(parLR.b.M));
                val seqLR = new SeqLinearRegression(V, b, iT);


                // Result verification
                Debug.flushln("Starting sequential linear regression");
                seqLR.run();
                // Verification of parallel against sequential
                Debug.flushln("Start verifying results");

                if (equalsRespectNaN(parLR.w, seqLR.w as Vector(parLR.w.M))) {
                    Console.OUT.println("Verification passed! "+
                                        "Parallel linear regression is same as sequential version");
                } else {
                    Console.OUT.println("Verification failed!");
                }
            }
        }
    }
}
