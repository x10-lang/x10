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
import x10.matrix.distblock.DistBlockMatrix;

import gnmf.GNNMF;
import gnmf.SeqGNNMF;

/**
 * <p>
 * Gaussian non-negative matrix factorization demo run.
 * <p>
 * Input matrix: V, Input-output matrices: W and H
 * <p> for (1..numIteration) {
 * <p> H . (W^t * V / (W^t * W) * H) -> H 
 * <p> W . (V * H^t / W * (H * H^t)) -> W
 * <p> }
 * <p>
 * Execution input parameters includes:
 * <p> (1) Rows of W and V. Default 1000
 * <p> (2) Non-zero sparsity of W and V. Default 0.001
 * <p> (3) Number of iterations. Default 10
 * <p> (4) Number of row blocks partitioning V and W. Default is the number of places, or Place.MAX_PLACES.
 * <p> (5) Number of column blocks partitioning V. Default 1
 * <p> (6) Verification flag. Default 0 (no verification); 1 (sequentail verison run); 
 * 2 (parallel version run and full matrix verification); 3 (parallel version run and random sampling verification).
 * <p> (7) Column of V. Default: 100000
 */
public class RunGNMF {

	public static def main(args:Array[String](1)): void {
		
		val mD = args.size > 0 ? Int.parse(args(0)):1000;
		val nZ = args.size > 1 ? Double.parse(args(1)):0.001;
		val iT = args.size > 2 ? Int.parse(args(2)):10;
		val rbV= args.size > 3 ? Int.parse(args(3)):Place.MAX_PLACES;
		val cbV= args.size > 4 ? Int.parse(args(4)):1;
		val tV = args.size > 5 ? Int.parse(args(5)):0;
		val nV = args.size > 6 ? Int.parse(args(6)):100000;

		Console.OUT.println("Set d:"+mD+" density:"+nZ+" iteration:"+iT);
		if ((mD<=0) || (iT<1) || (tV<0))
			Console.OUT.println("Error in settings");
		else {
			val t = GNNMF.make(mD, nV, nZ, iT, rbV, cbV);
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
				t.W.equals(seq.W as Matrix(t.W.M, t.W.N));
				VerifyTools.testSame(t.W, seq.W, tV);
				Debug.flushln("Verify H");
				VerifyTools.testSame(t.H, seq.H, tV);
			}
		}
	}
}