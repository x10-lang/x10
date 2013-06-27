/**
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 */

import x10.util.Timer;

import x10.matrix.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;

/**
 * Demo of linear regression test
 */
public class RunLinReg {
	public static def main(args:Rail[String]): void {
		val mV = args.size > 0 ? Int.parse(args(0)):100; // Rows and columns of V
		val nV = args.size > 1 ? Int.parse(args(1)):100; //column of V
		val nZ = args.size > 2 ? Double.parse(args(2)):0.1; //V's nonzero density
		val iT = args.size > 3 ? Int.parse(args(3)):10;//Iterations
		val vf = args.size > 4 ? Int.parse(args(4)):0; //Verify result or not
		val pP = args.size > 5 ? Int.parse(args(5)):0; // print V, d and w out

		Console.OUT.println("Set row V:"+mV+" col V:"+nV+" density:"+nZ+" iteration:"+iT);
		if (mV<=0 || nV<=0 || iT<1 || nZ<0.0)
			Console.OUT.println("Error in settings");
		else {
			// Create parallel linear regression 
			val	parLR = new LinearRegression(mV, nV, nZ, iT);
			parLR.V.printStatistics();

			//Run the parallel linear regression
			Debug.flushln("Starting parallel linear regression");
			val tt:Long = Timer.milliTime();
			parLR.run();
			val totaltime = Timer.milliTime() - tt;
			Debug.flushln("Parallel linear regression --- total:"+totaltime+" ms "+
							"commuTime:"+parLR.commT+" ms " +
					        "paraComp:"+parLR.parCompT + " ms");

			if (pP !=0) {
				Console.OUT.println("Input sparse matrix V\n" + parLR.V);
				Console.OUT.println("Input dense matrix b\n" + parLR.b);
				Console.OUT.println("Output dense matrix w\n" + parLR.w);
			}
			
			if (vf > 0) {			 
				// Create sequential version running on dense matrices
				val V = DenseMatrix.make(mV, nV);
				val b = DenseMatrix.make(nV, 1);
				parLR.V.copyTo(V);
				parLR.b.copyTo(b);
				val seqLR = new SeqLinearRegression(V, b, iT);

				// Result verification
				Debug.flushln("Starting sequential linear regression");
				seqLR.run();
				// Verification of parallel against sequential
				Debug.flushln("Start verifying results");

				if (parLR.w.equals(seqLR.w as Matrix(parLR.w.M, parLR.w.N))) {
					Console.OUT.println("Verification passed! "+
										"Parallel linear reqresion is same as sequential version");
				} else {
					Console.OUT.println("Verifcation failed!");
				}
			}
		}
	}
}
