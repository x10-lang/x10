/**
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 */

import x10.io.Console;
import x10.util.Timer;
//
import x10.matrix.Debug;
//
import x10.matrix.MathTool;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.Vector;
import x10.matrix.sparse.SparseCSC;

import x10.matrix.block.BlockMatrix;
import x10.matrix.distblock.DistBlockMatrix;

import linreg.LinearRegression; 
import linreg.SeqLinearRegression;


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
		for (var c:Int=0; c< M; c++)
			if (MathTool.isZero(w.d(c) - v.d(c)) == false && !(w.d(c).isNaN() && v.d(c).isNaN())) {
				Console.OUT.println("Diff found [" + c + "] : "+ 
									w.d(c) + " <> "+ v.d(c));
				return false;
			}
		return true;
	}

	public static def main(args:Rail[String]): void {
		
		val mV = args.size > 0 ? Int.parse(args(0)):10; // Rows and columns of V
		val nV = args.size > 1 ? Int.parse(args(1)):10; //column of V
		val mB = args.size > 2 ? Int.parse(args(2)):5;
		val nB = args.size > 3 ? Int.parse(args(3)):5;
		val nZ = args.size > 4 ? Double.parse(args(4)):0.9; //V's nonzero density
		val iT = args.size > 5 ? Int.parse(args(5)):2;//Iterations
		val vf = args.size > 6 ? Int.parse(args(6)):0; //Verify result or not
		val pP = args.size > 7 ? Int.parse(args(7)):0; // print V, d and w out

		Console.OUT.println("Set row V:"+mV+" col V:"+nV+" density:"+nZ+" iteration:"+iT);
		if (mV<=0 || nV<=0 || iT<1 || nZ<0.0)
			Console.OUT.println("Error in settings");
		else {

			// Create parallel linear regression 
			val	parLR = LinearRegression.make(mV, nV, mB, nB, nZ, iT);
			//parLR.V.printRandomInfo();
			//----------------------------------------

			//Run the parallel linear regression
			Debug.flushln("Starting parallel linear regression");
			val tt:Long = Timer.milliTime();
			parLR.run();
			val totaltime = Timer.milliTime() - tt;
			//parLR.w.print("Parallel result");
			Debug.flushln("Parallel linear regression --- total:"+totaltime+" ms "+
							"commuTime:"+parLR.commT+" ms " +
					        "paraComp:"+parLR.parCompT + " ms");

			if (pP !=0) {
				parLR.V.print("Input sparse matrix V\n");
				parLR.b.print("Input dense matrix b\n");
				parLR.w.print("Output dense matrix w\n");
			}
			
			if (vf > 0) {			 
				// Create sequential version running on dense matrices
				val bV:BlockMatrix(mV, nV) = BlockMatrix.makeSparse(parLR.V.getGrid(), nZ) as BlockMatrix(mV,nV);
				val V:DenseMatrix(mV, nV) = DenseMatrix.make(mV, nV);
				val b:Vector(nV)  = Vector.make(nV);
				
				parLR.V.copyTo(bV as BlockMatrix(parLR.V.M, parLR.V.N));
				bV.copyTo(V);
				parLR.b.copyTo(b as Vector(parLR.b.M));
				val seqLR = new SeqLinearRegression(V, b, iT);
			
				//------------------------
				// Result verification
				Debug.flushln("Starting sequential linear regression");
				seqLR.run();
				// Verification of parallel against sequential
				Debug.flushln("Start verifying results");
				
				if (equalsRespectNaN(parLR.w, seqLR.w as Vector(parLR.w.M))) {
					Console.OUT.println("Verification passed! "+
										"Parallel linear reqresion is same as sequential version");
				} else {
					Console.OUT.println("Verifcation failed!");
				}
			}
		}
	}
}