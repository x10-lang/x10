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

package gnmf;

import x10.util.Timer;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.DupBlockMatrix;

/**
 * Sequential implementation of GNNMF based on GML dense/sparse matrix.
 * This is used for verification purpose.
 */
public class SeqGNNMF {

	// GNNMF settings
	val iterate:Int;

	// Input and output data
	// Conversion matrices
	public val V:DenseMatrix;
	public val W:DenseMatrix{self.M==V.M};
	public val H:DenseMatrix(W.N, V.N);

	
	// Temp data and matrix 
	val WV:DenseMatrix(W.N, V.N);   //Store W^t * V result (10x100000) like H
	val WW:DenseMatrix(W.N, W.N);   //Store W^t * W  (10x10)
	val WWH:DenseMatrix(W.N, H.N);  //Store WW * H   (10x100000) like H
	val VH:DenseMatrix(V.M, H.M);  //Store V * H^t  (dx10) like W
	val HH:DenseMatrix(H.M, H.M);   //Store H * H^t, (10x10)
	val WHH:DenseMatrix(W.M, H.M); //Store W * HH   (dx10) like W
	
	// Profiling
	var tt:Long = 0;

	public def this(v:DistBlockMatrix, 
					h:DupBlockMatrix,
					w:DistBlockMatrix,
					i:Int) {
		iterate = i;
		V=v.toDense(); 
		W=w.toDense() as DenseMatrix{self.M==V.M};
		H=h.toDense() as DenseMatrix(W.N, V.N); 

		WV  = new DenseMatrix(W.N, V.N); // W^t * V
		WW  = new DenseMatrix(W.N, W.N); // W^t * W
		WWH = new DenseMatrix(W.N, H.N); // (W^t*W)*H
		
		VH  = new DenseMatrix(V.M, H.M); // V*H^t
		HH  = new DenseMatrix(H.M, H.M); // H * H^t
		WHH = new DenseMatrix(W.M, H.M);   // W * (H*H^t)
	}

	public def comp_WV_WWH() : void {
		/* H . (W^t * V / (W^t * W) * H) -> H */
		WV.transMult(W, V);                       // W^t * V  -> WV
		//WV.print("Sequentail W^t * V = ");

		WW.transMult(W, W);                       // W^t * W  -> WW
		//WW.print("Sequential W^t * W = ");

		WWH.mult(WW, H);                          // WW * H   -> WWH
		//WWH.print("Sequential WW * H= ");

		WV.cellDiv(WWH);                        // WV / WWH -> WV
		//WV.print("Sequential WV / WWH =");

		H.cellMult(WV);                         // H . WV   -> H
		//H.print("Sequential H . WV = ");
	}

	public def comp_VH_WHH() : void {
		/* W . (V * H^t / W * (H * H^t)) -> W */
		//V.print("Sequential input V");
		//H.print("Sequential input H");
		VH.multTrans(V, H);                         // V  * H^t -> VH
		//VH.print("Sequential VH:");

		HH.multTrans(H, H);                         // H  * H^t -> HH
		//HH.print("Sequential HH:");

		WHH.mult(W, HH);                            // W  * HH  -> WHH
		//WHH.print("Sequential WHH:");

		VH.cellDiv(WHH);                        // VH / WHH -> VH 
		//VH.print("Sequential VH/WHH updated:");
		W.cellMult(VH);                         // W  . VH  -> W
		//W.print("Sequential W updated:");

	}

	public def run() : void {
		/* Timing */ val st = Timer.milliTime();
		for (var i:Long =0; i<iterate; i++) {
			comp_WV_WWH();
			comp_VH_WHH();
		}
		/* Timing */ tt += Timer.milliTime() - st;
	}
	
	public def printTiming() : void {
		Console.OUT.printf("Total time: %dms used sequential execution\n",  tt);
		Console.OUT.flush();
	}

	public def verifyH(vH:DupBlockMatrix):Boolean {

		//H.print("Sequential computing result H:");
		//vH.print("Parallel computing result H:");
		Console.OUT.print("Verifying H - ");
		if (! H.equals(vH as Matrix(H.M, H.N))) {
			Console.OUT.println("Fail!!!!!! H is not same.");
			return false;
		}
		Console.OUT.println("Pass!");
		return true;
	}

	public def verifyW(vW:DistBlockMatrix):Boolean {
		Console.OUT.print("Verifying W - ");
		if (! W.equals(vW as Matrix(W.M, W.N))) {
			Console.OUT.println("Fail!!!!! W is not same.");
			return false;
		} 
		Console.OUT.println("Pass!");
		return true;
	}

	public def verify(vH:DupBlockMatrix,
					  vW:DistBlockMatrix
					  ):Boolean {
		return verifyH(vH)&&verifyW(vW);
	}

}
