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

import x10.matrix.util.Debug;
import x10.matrix.util.VerifyTool;

/**
 * 	Gaussian non-negative matrix factorization demo run
 */
public class RunGNMF {
	public static def main(args:Rail[String]): void {
		val mD = args.size > 0 ? Long.parse(args(0)):1000; //
		val nZ = args.size > 1 ? Double.parse(args(1)):0.001;
		val iT = args.size > 2 ? Int.parse(args(2)):10n;
		val tV = args.size > 3 ? Long.parse(args(3)):0;
		val nV = args.size > 4 ? Long.parse(args(4)):100000;

		Console.OUT.println("Set d:"+mD+" density:"+nZ+" iterations:"+iT);
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
				val seq = new SeqGNNMF(t.V, t.H, t.W, t.iterations);
				seq.run();
				seq.printTiming();
			} else if (tV == 2) { /* Verification of whole matrices*/
				t.verifyRun();
			} else { /* Random sampling verification */
				Debug.flushln("Prepare sequential run");
				val seq = new SeqGNNMF(t.V, t.H, t.W, t.iterations);
				Debug.flushln("Start parallel run");
				t.run();
				Debug.flushln("Start sequential run");
				seq.run();
				Debug.flushln("Verify W");
				t.W.equals(seq.W);
				VerifyTool.testSame(t.W, seq.W, tV);
				Debug.flushln("Verify H");
				VerifyTool.testSame(t.H, seq.H, tV);
			}
		}
	}
}
