/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 */

package gnmf;

import x10.io.Console;
import x10.util.Timer;
//
import x10.matrix.Debug;
//
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.VerifyTools;
//
import x10.matrix.block.Grid;
import x10.matrix.distblock.DistGrid;
import x10.matrix.distblock.DistBlockMatrix;
//
import x10.matrix.distblock.DupBlockMatrix;
import x10.matrix.distblock.DistDupMult;
//
//import x10.matrix.dist.DistMultDistToDup;
//import x10.matrix.dist.DistMultDupToDist;

/**
 * Parallel GNNMF implementation is based on GML distributed dense/sparse matrix.
 * Input V, and input-output W and H use grid partitioning, where V and W
 * have the same number of rows and same row partitioning, and
 * W's columns and H's rows are same and share the same partitioning,
 * same as V's columns and H's columns.
 * 
 * Input matrix V is partitioned into (numRowBsV &#42 numColBsV) blocks
 * <p>
 * <p>[v_(0,0),           v_(0,1),           ..., v(0,numColBsV-1)]
 * <p>[v_(1,0),           v_(1,1),           ..., v(1,numColBsV-1)]
 * <p>......
 * <p>[v_(numRowBsV-1,0), v_(numRowBsV-1,1), ..., v(numRowBsV-1,numColBsV-1)]
 * <p>
 * All numRowBsV &#42 numColBsV blocks are distributed to (Place.MAX_PLACES &#42 1) 
 * places, or in vertical distribution.
 * 
 * <p>
 * Input-output matrix W is partitioned into (numRowBsV &#42 numColBsW) blocks
 * <p>
 * <p>[w_(0,0),           w_(0,1),         ..., w(0,numColBsV-1)]
 * <p>[w_(1,0),           w_(1,1),         ..., w(1,numColBsV-1)]
 * <p>......
 * <p>[w_(numRowBsV-1,0), w_(numRowBsV,1), ..., w(numRowBsV-1,numColBsW-1)]
 * <p>
 * <p>Matrix W is partitioned in the same way as V row-wise. 
 * All numRowBsV &#42 numColBsW blocks in W are distributed to (Place.MAX_PLACES &#42 1) places, 
 * or vertical distribution.
 * 
 * <p>
 * Input-output matrix H is partitioned into (numColBsW &#42 numColBsV) blocks, which
 * are duplicated in all places.
 */ 
public class GNNMF {

	//------GNNMF matrix size------
	val Vm:Int;
	val Vn:Int;// = 100000;
	//val Wm:Int{self==Vm};
	val Wn:Int = 10;
	// ------Matrix partitioning and distrinution parameters -----
	val numRowBsV:Int;
	val numColBsV:Int;
	val numColBsW:Int;
	
	// ------GNNMF parameters------
	public val iteration:Int;
	val nzDensity:Double;
	// ------Data partitioning------
	val gridV:Grid;
	val gridW:Grid;
	val gridH:Grid;
	
	// ------Block distribution -------
	val distV:DistGrid;
	val distW:DistGrid;
	
	// ------Input and output data------
	public val V:DistBlockMatrix(Vm, Vn);
	public val W:DistBlockMatrix(Vm, Wn);
	public val H:DupBlockMatrix(Wn, Vn);
	// ------Temp data and matrix------ 
	val WtV:DupBlockMatrix(W.N, V.N);   //Store W^t * V result (10x100000) like H
	//val tmpWV:DupDenseMatrix(W.N, V.N);    
	val WtW:DupBlockMatrix(W.N, W.N);   //Store W^t * W  (10x10)
	//val tmpWW:DupDenseMatrix(W.N, W.N);   

	val WtWH:DupBlockMatrix(W.N, H.N);  //Store WW * H   (10x100000) like H
	val VHt:DistBlockMatrix(Vm, Wn);    //Store V * H^t  (dx10) like W
	val HHt:DupBlockMatrix(H.M, H.M);   //Store H * H^t, (10x10)
	val WHHt:DistBlockMatrix(Vm, Wn);   //Store W * HH   (dx10) like W
	
	// ------Profile timing------
	val ts:Long = Timer.milliTime();
	var tt:Long = 0;
	var t1:Long = 0;

	public def this(d:Int, nv:Int, nz:Double, i:Int, 
			mbV:Int, nbV:Int, nbW:Int) {
	
		Vm = d; Vn =nv;
		nzDensity=nz;
		iteration = i;
		//
		numRowBsV = mbV; numColBsV = nbV; numColBsW = nbW;
		//
		gridV = new Grid(Vm, Vn, numRowBsV, numColBsV);
		gridW = new Grid(Vm, Wn, numRowBsV, numColBsW);
		gridH = new Grid(Wn, Vn, gridW.numColBlocks, gridV.numColBlocks); // H = W^t * V
		val gridWtW = new Grid(Wn, Wn, gridW.numColBlocks, gridW.numColBlocks);
		val gridHHt = new Grid(Wn, Wn, gridV.numColBlocks, gridV.numColBlocks);
		//
		distV = new DistGrid(gridV, Place.MAX_PLACES, 1);
		distW = new DistGrid(gridW, Place.MAX_PLACES, 1);
		//		
		//------Input matrix data allocation------
		Debug.flushln("Start memory allocation");		
		V = DistBlockMatrix.makeSparse(gridV, distV.dmap, nzDensity) as DistBlockMatrix(Vm, Vn);
		W = DistBlockMatrix.makeDense(gridW, distW.dmap) as DistBlockMatrix(Vm, Wn);
		H = DupBlockMatrix.makeDense(gridH) as DupBlockMatrix(Wn,Vn);

		WtV  = DupBlockMatrix.makeDense(gridH) as DupBlockMatrix(Wn,Vn);   // W^t * V
		//tmpWV  =  DupDenseMatrix.make(W.N, V.N);
		WtW  = DupBlockMatrix.makeDense(gridWtW) as DupBlockMatrix(Wn,Wn); // W^t * W
		//tmpWW  = DupDenseMatrix.make(W.N, W.N); // W^t * W
		WtWH = DupBlockMatrix.makeDense(gridH) as DupBlockMatrix(Wn,Vn);   // (W^t*W)*H
		
		VHt  = DistBlockMatrix.makeDense(gridW, distW.dmap) as DistBlockMatrix(Vm, Wn);   // V*H^t
		HHt  = DupBlockMatrix.makeDense(gridHHt); // H * H^t
		WHHt = DistBlockMatrix.makeDense(gridW, distW.dmap) as DistBlockMatrix(Vm, Wn);   // W * (H*H^t)
	}

	public def init():void {
		Debug.flushln("Start initialize input data");		
		V.initRandom();
		Debug.flushln("Dist block matrix in sparse blocks initialization completes");		
		W.initRandom();
		Debug.flushln("Dist block matrix in dense blocks initialization completes");
		H.initRandom();
		Debug.flushln("Dup block matrix in dense blocks initialization completes");
	}

	public def printInfo():void {
		val nzc:Float =  V.getTotalNonZeroCount() as Float;
		val nzd:Float =  nzc / (V.M * V.N as Float);

		Debug.flushln("Starting X10 GNNMF ");
		Console.OUT.printf("Input matrix V:(%dx%d), partitioning:(%dx%d) blocks, distribution:(%dx%d) places\n", 
						   V.M, V.N, gridV.numRowBlocks, gridV.numColBlocks, 
						   distV.numRowPlaces, distV.numColPlaces);
		Console.OUT.printf("V nonzero density: %f, total nonzero count: %f\n", nzd, nzc);
						   
		Console.OUT.printf("Input-output matrix W:(%dx%d), partitioning:(%dx%d) blocks, distribution:(%dx%d) places\n", 
				W.M, W.N, gridW.numRowBlocks, gridW.numColBlocks, distW.numRowPlaces, distV.numColPlaces);

		Console.OUT.printf("Input-output matrix H:(%dx%d), partitioning:(%dx%d) blocks, duplicated in all places\n", 
				H.M, H.N, gridH.numRowBlocks, gridH.numColBlocks);

		Console.OUT.flush();

		//Console.OUT.printf("Average column nonzero count:%.3f, std:%.3f\n",
		//				   V.getAvgColumnSize(), V.getColumnSizeStdDvn());
		//Console.OUT.printf("Average nonzero index distance:%.3f, std:%.3f\n",
		//				   V.compAvgIndexDst(), V.compIndexDstStdDvn());
		//V.printBlockColumnSizeAvgStd();
		Console.OUT.flush();

	}

	public def comp_WV_WWH() : void {
		/* H . (W^t * V / (W^t * W) * H) -> H */
		WtV.transMult(W, V, false); // W^t * V  -> WV

		//WtV.print("Parallel W^t * V =");
		WtW.transMult(W, W, false);// W^t * W  -> WW

		//WtW.print("Parallel W^t * W = ");
		WtWH.mult(WtW, H, false);

		//WtWH.print("Parallel dup WW * H = ");
		WtV.cellDiv(WtWH);                     // WV / WWH -> WV

		//WtV.print("Parallel WV ./ WWH = ");
		H.cellMult(WtV);                      // H . WV   -> H		
		//H.print("Parallel H update:");
	}

	public def comp_VH_WHH() : void {
		/* W . (V * H^t / W * (H * H^t)) -> W */
		//V.print("Parallel input V");
		//H.print("Parallel Input H");
		VHt.multTrans(V, H, false);                // V  * H^t -> VH
		//VHt.print("Parallel VH:");

		HHt.multTrans(H, H, false);                // H  * H^t -> HH
		//HHt.print("Parallel HH:");

		WHHt.mult(W, HHt, false);                  // W  * HH  -> WHH  
		//WHH.print("Parallel WHH:");

		VHt.cellDiv(WHHt);                         // VH / WHH -> VH 
		//VHt.print("Parallel VH/WHH:");

		W.cellMult(VHt);                          // W  . VH  -> W
		//W.print("Parallel W updated:");
	}

	public def run() : void {

		/* Timing */ val st = Timer.milliTime();
		for (var i:Int =0; i<iteration; i++) {
			comp_WV_WWH();
			/* Timing */ t1 += Timer.milliTime() - st;
			comp_VH_WHH();
		}
		/* Timing */ tt += Timer.milliTime() - st;
	}

	public def verifyRun() : void {
		
		Debug.flushln("Prepare verification process\n");
		//V.print("Input V:");
		//H.print("Input H:");
		//W.print("Input W:");
		val seq = new SeqGNNMF(V, H, W, iteration);
		//------------------------------------------
		for (var i:Int =0; i<iteration; i++) {
			Debug.flushln("Iteration "+i+" start parallel computing H");
			comp_WV_WWH();
			seq.comp_WV_WWH();
			if (!seq.verifyH(H)) break;

			Debug.flushln("Iteration "+i+" start parallel computing W");
			comp_VH_WHH();
			seq.comp_VH_WHH();
			if (!seq.verifyW(W)) break;

		}
		//------------------------------------------
	}
	
	public def printTiming() : void {
		//
		var tcalc:Long = 0;
		//
		tcalc += WtV.getCalcTime();  // (W^t * V) and (WV / WWH)
		tcalc += WtW.getCalcTime();  // (W^t * W ) time
		tcalc += WtWH.getCalcTime(); // (WW * H) 
		tcalc += H.getCalcTime();   // (H . WV)
		//
		tcalc += HHt.getCalcTime();  // (H * H^t)
		tcalc += WHHt.getCalcTime(); // (W * HH)
		tcalc += VHt.getCalcTime();  // (V * H^t) and (VH / WHH)
		tcalc += W.getCalcTime();   // (W . VH)
		//
		//---------
		var tcomm:Long = 0;
		tcomm += WtV.getCommTime();  // (W^t * V) all reduce sum time
		tcomm += WtW.getCommTime();  // (W^t * W) all reduce sum time
		tcomm += H.getCommTime();   // H initial bcast
		//

		Console.OUT.printf("Total time:    %dms, Calc: %dms, Comm:  %dms\n",
						   tt, tcalc, tcomm);
		Console.OUT.printf("W^t * V: Comp: %d, AllReduce Comm: %d\n",
						   WtV.getCalcTime(), WtV.getCommTime());

		Console.OUT.printf("W^t * W: Comp: %d, AllReduce Comm: %d\n",
						   WtW.getCalcTime(), WtW.getCommTime());
		
		Console.OUT.printf("V * H^t  Comp: %d\n",
						   VHt.getCalcTime());

		Console.OUT.printf("One time H bcast: %d\n", H.getCommTime());		
		
		Console.OUT.flush();
		

	}
}
