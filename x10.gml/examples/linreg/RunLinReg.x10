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
        val mV = args.size > 0 ? Long.parse(args(0)):10; // Rows and columns of V
        val nV = args.size > 1 ? Long.parse(args(1)):10; //column of V
        val mB = args.size > 2 ? Long.parse(args(2)):5;
        val nB = args.size > 3 ? Long.parse(args(3)):5;
        val nZ = args.size > 4 ? Double.parse(args(4)):0.9; //V's nonzero density
        val iT = args.size > 5 ? Long.parse(args(5)):2; //Iterations
        val vf = args.size > 6 ? Int.parse(args(6)):0n; //Verify result or not
        val pP = args.size > 7 ? Int.parse(args(7)):0n; // print V, d and w out
        val sP = args.size > 8 ? Int.parse(args(8)):0n; // skip places count (at least 1 place should remain)
        val cI = args.size > 9 ? Int.parse(args(9)):-1n; // checkpoint iteration frequency

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

            if (pP != 0n) {
                Console.OUT.println("Input sparse matrix V\n" + parLR.V);
                Console.OUT.println("Input dense matrix b\n" + parLR.b);
                Console.OUT.println("Output dense matrix w\n" + parLR.w);
            }

            if (vf > 0) {
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
