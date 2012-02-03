/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 */

import x10.io.Console;
import x10.util.Timer;
//
import x10.matrix.Debug;
//
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.VerifyTools;
//
import x10.matrix.dist.DistDenseMatrix;
import x10.matrix.dist.DistSparseMatrix;

import gnmf.GNNMF;
import gnmf.SeqGNNMF;

/**
 * <p>
 * 	Gaussian non-negative matrix factorization demo run
 * <p>
 */
public class RunGNMF {

	public static def main(args:Array[String](1)): void {
		
		val mD = args.size > 0 ? Int.parse(args(0)):1000; //
		val nZ = args.size > 1 ? Double.parse(args(1)):0.001;
		val iT = args.size > 2 ? Int.parse(args(2)):10;
		val tV = args.size > 3 ? Int.parse(args(3)):0;
		val nV = args.size > 4 ? Int.parse(args(4)):100000;

		Console.OUT.println("Set d:"+mD+" density:"+nZ+" iteration:"+iT);
		if ((mD<=0) || (iT<1) || (tV<0))
			Console.OUT.println("Error in settings");
		else {
			val t = new GNNMF(mD, nV, nZ, iT);
			t.init();
			t.printInfo();
			if (tV == 0 ) {
				t.run();
				t.printTiming();
			} else if (tV == 1) { /* Sequential run */
				val seq = new SeqGNNMF(t.V, t.H, t.W, t.iteration);
				seq.run();
				seq.printTiming();
			} else if (tV == 2) { /* Verification of whole matrices*/
				t.verifyRun();
			} else { /* Random sampling verification */
				Debug.flushln("Prepare sequential run");
				val seq = new SeqGNNMF(t.V, t.H, t.W, t.iteration);
				Debug.flushln("Start parallel run");
				t.run();
				Debug.flushln("Start sequential run");
				seq.run();
				Debug.flushln("Verify W");
				t.W.equals(seq.W);
				VerifyTools.testSame(t.W, seq.W, tV);
				Debug.flushln("Verify H");
				VerifyTools.testSame(t.H, seq.H, tV);
			}
		}
	}
}