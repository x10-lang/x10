/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011-2014.
 */

import x10.util.Timer;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.Vector;
import x10.matrix.block.Grid;
import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.util.Debug;
import x10.matrix.util.VerifyTool;

import logreg.SeqLogReg;
import logreg.LogisticRegression;

public class RunLogReg {

	public static def main(args:Rail[String]): void {
		val mX = args.size > 0 ? Long.parse(args(0)):1000; //
		val nX = args.size > 1 ? Long.parse(args(1)):1000; //
		val mB = args.size > 2 ? Long.parse(args(2)):5;
		val nB = args.size > 3 ? Long.parse(args(3)):5;

		val nzd= args.size > 4 ? Double.parse(args(4)):0.5;
		val it = args.size > 5 ? Long.parse(args(5)):3;
		val tV = args.size > 6 ? Long.parse(args(6)):0;

		Console.OUT.println("Set X row:"+mX+ " col:"+nX);
		if ((mX<=0) ||(nX<=0) ||(tV<0))
			Console.OUT.println("Error in settings");
		else {
			val X:DistBlockMatrix(mX, nX) = DistBlockMatrix.makeSparse(mX, nX, mB, nB, Place.numPlaces(), 1, nzd);
			val y:Vector(X.M) = Vector.make(X.M);
			val w:Vector(X.N) = Vector.make(X.N);
			
			X.initRandom(1, 10);
			y.initRandom(1, 10);
			w.initRandom();
			val yt = y.clone();
			val wt = w.clone();
			
			val prun = new LogisticRegression(X, y, w, it, it);
		
			val stt = Timer.milliTime();
			prun.run();
			val totalTime = Timer.milliTime() - stt;
			
			if (tV > 0) { /* Sequential run */
				val denX = X.toDense();
				val seq = new SeqLogReg(denX, yt, wt, it, it);
				seq.run();
				
				if (w.equals(wt as Vector(w.M))) {
					Console.OUT.println("Verification passed!");
				} else {
					Console.OUT.println("-------------- Verification failed!!!!---------------");
				}
			}
			
			Console.OUT.printf("Logistic regression --- Total: %8d ms, parallel runtime: %8d ms, commu time: %8d ms\n",
					totalTime, prun.paraRunTime, prun.commUseTime); 
			
		}
	}
}
