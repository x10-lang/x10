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

package gnmf;

import x10.util.Timer;

import x10.matrix.util.Debug;
import x10.matrix.block.Grid;
import x10.matrix.distblock.DistGrid;
import x10.matrix.distblock.DistMap;
import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.DupBlockMatrix;

/**
 * Parallel GNNMF implementation is based on GML distributed dense/sparse matrix.
 * Input V, and input-output W and H use grid partitioning, where V and W
 * have the same number of rows and same row partitioning, and
 * W's columns and H's rows are same and share the same partitioning as
 * V's columns and H's columns.
 * 
 * Input matrix V is partitioned into (rowBsV &#42 colBsV) blocks
 * <p>
 * <p>[v_(0,0),        v_(0,1),        ..., v(0,colBsV-1)]
 * <p>[v_(1,0),        v_(1,1),        ..., v(1,colBsV-1)]
 * <p>......
 * <p>[v_(rowBsV-1,0), v_(rowBsV-1,1), ..., v(rowBsV-1,colBsV-1)]
 * <p>
 * All rowBsV &#42 colBsV blocks are distributed to (Place.numPlaces() &#42 1) 
 * places, or in vertical distribution.
 * 
 * <p>
 * Input-output matrix W is partitioned into (rowBsV &#42 colBsW) blocks
 * <p>
 * <p>[w_(0,0),        w_(0,1),      ..., w(0,colBsW-1)]
 * <p>[w_(1,0),        w_(1,1),      ..., w(1,colBsW-1)]
 * <p>......
 * <p>[w_(rowBsV-1,0), w_(rowBsV,1), ..., w(rowBsV-1,colBsW-1)]
 * <p>
 * <p>Matrix W is partitioned in the same way as V row-wise. 
 * All numRowBsV &#42 numColBsW blocks in W are distributed to (Place.numPlaces() &#42 1) places, 
 * or vertical distribution.
 * 
 * <p>
 * Input-output matrix H is partitioned into (colBsW &#42 colBsV) blocks, which
 * are duplicated in all places.
 */ 
public class GNNMF {
	static val wN = 10;
	// ------GNNMF execution parameters------
	public val iterations:Int;
	// ------Input and output matrix------
	public val V:DistBlockMatrix;
	public val W:DistBlockMatrix(V.M, wN);
	public val H:DupBlockMatrix(W.N, V.N);
	
	// ------Temp matrix------ 
	val WtV:DupBlockMatrix(W.N, V.N);     //Store W^t * V result (10x100000) like H
	val WtW:DupBlockMatrix(W.N, W.N);     //Store W^t * W  (10x10)

	val WtWH:DupBlockMatrix(W.N, H.N);    //Store WW * H   (10x100000) like H
	val VHt:DistBlockMatrix(V.M, W.N);    //Store V * H^t  (dx10) like W
	val HHt:DupBlockMatrix(H.M, H.M);     //Store H * H^t, (10x10)
	val WHHt:DistBlockMatrix(V.M, W.N);   //Store W * HH   (dx10) like W
	
	// ------Profile timing------
	var st:Long = Timer.milliTime();
	var tt:Long = 0;
	var t1:Long = 0;

	public def this(v:DistBlockMatrix, w:DistBlockMatrix(v.M), h:DupBlockMatrix(w.N,v.N), it:Int) {
		V = v; 
		W = w as DistBlockMatrix(V.M, wN); 
		H = h as DupBlockMatrix(W.N,V.N);
		iterations = it;

		Debug.assure(DistGrid.isVertical(v.getGrid(), v.getMap()), 
		"Input distributed block matrix V does not have vertical distribution");
		Debug.assure(DistGrid.isVertical(w.getGrid(), w.getMap()), 
		"Input-output distributed block matrix V does not have vertical distribution");

		val gV = V.getGrid();
		val gW = W.getGrid();
		val gH = h.getGrid();
		val rowPls = Place.numPlaces();
		val colPls = 1;

		//This has least overhead in creating matrix, but not compatible if W, V,and H are created in a different way
		//WtV  = DupBlockMatrix.makeDense(W.N, V.N, gW.numColBlocks, gV.numColBlocks) as DupBlockMatrix(W.N, V.N); // W^t * V
		//WtW  = DupBlockMatrix.makeDense(W.N, W.N, gW.numColBlocks, gW.numColBlocks) as DupBlockMatrix(W.N, W.N); // W^t * W
		//WtWH = DupBlockMatrix.makeDense(W.N, V.N, gW.numColBlocks, gV.numColBlocks) as DupBlockMatrix(W.N, V.N); // (W^t*W)*H
		//VHt  = DistBlockMatrix.makeDense(V.M, W.N, gV.numRowBlocks, gW.numColBlocks, rowPls, colPls) as DistBlockMatrix(V.M, W.N); // V*H^t
		//HHt  = DupBlockMatrix.makeDense(H.M, H.M, gH.numRowBlocks, gH.numRowBlocks) as DupBlockMatrix(H.M, H.M); // H * H^t
		//WHHt = DistBlockMatrix.makeDense(V.M, W.N, gV.numRowBlocks, gW.numColBlocks, rowPls, colPls) as DistBlockMatrix(V.M, W.N);   // W * (H*H^t)
		
		//This has least overhead in creating matrix, but not compatible if W, V,and H are created in a different way
		val gridWtV = new Grid(W.N, V.N, gW.colBs, gV.colBs);//captured to all places
		WtV  = DupBlockMatrix.makeDense(gridWtV) as DupBlockMatrix(W.N, V.N); // W^t * V
		val gridWtW = new Grid(W.N, W.N, gW.colBs, gW.colBs);
		WtW  = DupBlockMatrix.makeDense(gridWtW) as DupBlockMatrix(W.N, W.N); // W^t * W
		val gridWtWH = new Grid(W.N, V.N, gW.colBs, gV.colBs);
		WtWH = DupBlockMatrix.makeDense(gridWtWH) as DupBlockMatrix(W.N, V.N); // (W^t*W)*H
		val gridVHt = new Grid(V.M, W.N, gV.rowBs, gW.colBs);//captured to all places
		VHt  = DistBlockMatrix.makeDense(gridVHt, V.getMap()) as DistBlockMatrix(V.M, W.N); // V*H^t
		val gridHHt = new Grid(H.M, H.M, gH.rowBs, gH.rowBs);
		HHt  = DupBlockMatrix.makeDense(gridHHt) as DupBlockMatrix(H.M, H.M); // H * H^t
		val gridWHHt = new Grid(V.M, W.N, gV.rowBs, gW.colBs);
		WHHt = DistBlockMatrix.makeDense(gridWHHt, V.getMap()) as DistBlockMatrix(V.M, W.N);   // W * (H*H^t)
	}
	
	public static def make(gridV:Grid, gridW:Grid, gridH:Grid, blockMap:DistMap, nzd:Double, it:Int) {
		//------Input matrix data allocation------
		Debug.flushln("Start memory allocation");
		Debug.assure(DistGrid.isVertical(gridV, blockMap), "Distribution of block matrix V or W is not vertical");
		
		val v = DistBlockMatrix.makeSparse(gridV, blockMap, nzd);
		val w = DistBlockMatrix.makeDense(gridW, blockMap);
		val h = DupBlockMatrix.makeDense(gridH);
		return new GNNMF(v, w, h, it);
	}

	public static def make(vM:Long, vN:Long, nzd:Double, it:Int, vRowBs:Long, vColBs:Long) {
		//Preset parameters
		val wColBs = 1;
		//Vertical distribution
		val rowPls = Place.numPlaces();
		val colPls = 1;
		//------Input matrix data allocation------
		Debug.flushln("Start creating input-output matrix and memory allocation");		
		val v = DistBlockMatrix.makeSparse(vM, vN, vRowBs, vColBs, rowPls, colPls, nzd);
		val w = DistBlockMatrix.makeDense( vM, wN, vRowBs, wColBs, rowPls, colPls);
		val h = DupBlockMatrix.makeDense(  wN, vN, wColBs, vColBs);
		return new GNNMF(v, w, h, it);
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
		val gV = V.getGrid();
	
		Debug.flushln("Starting X10 GNNMF ");
		Console.OUT.printf("Input matrix V:(%dx%d), partitioning:(%dx%d) blocks, vertical distribution\n", 
						   V.M, V.N, gV.numRowBlocks, gV.numColBlocks);
		Console.OUT.printf("V nonzero density: %f, total nonzero count: %f\n", nzd, nzc);
						   
		val gW = W.getGrid();
		Console.OUT.printf("Input-output matrix W:(%dx%d), partitioning:(%dx%d) blocks, vertical distribution\n", 
				W.M, W.N, gW.numRowBlocks, gW.numColBlocks);

		val gH = H.getGrid();
		Console.OUT.printf("Input-output matrix H:(%dx%d), partitioning:(%dx%d) blocks, duplicated in all places\n", 
				H.M, H.N, gH.numRowBlocks, gH.numColBlocks);

		Console.OUT.flush();

	}

	public def comp_WV_WWH() : void {
		/* H . (W^t * V / (W^t * W) * H) -> H */
		WtV.transMult(W, V); // W^t * V  -> WV
        //Console.OUT.println("Parallel W^t * V =" + WtV);

		WtW.transMult(W, W); // W^t * W  -> WW
		//Console.OUT.println("Parallel W^t * W =" + WtW);

		WtWH.mult(WtW, H);
		//Console.OUT.println("Parallel dup WW * H = " + WtWH);

		WtV.cellDiv(WtWH);                     // WV / WWH -> WV
		//Console.OUT.println("Parallel WV ./ WWH = " + WtV);

		H.cellMult(WtV);                      // H . WV   -> H		
		//Console.OUT.println("Parallel H update:" + H);
	}

	public def comp_VH_WHH() : void {
		/* W . (V * H^t / W * (H * H^t)) -> W */
		//V.print("Parallel input V");
		//H.print("Parallel Input H");
		VHt.multTrans(V, H);                // V  * H^t -> VH
		//VHt.print("Parallel VH:");
		
		HHt.multTrans(H, H);                // H  * H^t -> HH
		//HHt.print("Parallel HH:");

		WHHt.mult(W, HHt);                  // W  * HH  -> WHH  
		//WHH.print("Parallel WHH:");

		VHt.cellDiv(WHHt);                         // VH / WHH -> VH 
		//VHt.print("Parallel VH/WHH:");

		W.cellMult(VHt);                          // W  . VH  -> W
		//W.print("Parallel W updated:");
	}

	public def run() : void {
		tt += H.getCommTime();
		/* Timing */ st = Timer.milliTime();
		for (i in 1..iterations) {
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
		val seq = new SeqGNNMF(V, H, W, iterations);

		for (i in 1..iterations) {
			Debug.flushln("Iteration "+i+" start parallel computing H");
			comp_WV_WWH();
			seq.comp_WV_WWH();
			if (!seq.verifyH(H)) break;

			Debug.flushln("Iteration "+i+" start parallel computing W");
			comp_VH_WHH();
			seq.comp_VH_WHH();
			if (!seq.verifyW(W)) break;

		}
	}
	
	public def printTiming() : void {
		var tcalc:Long = 0;
		tcalc += WtV.getCalcTime();  // (W^t * V) and (WV / WWH)
		tcalc += WtW.getCalcTime();  // (W^t * W ) time
		tcalc += WtWH.getCalcTime(); // (WW * H) 
		tcalc += H.getCalcTime();    // (H . WV)

		tcalc += HHt.getCalcTime();  // (H * H^t)
		tcalc += WHHt.getCalcTime(); // (W * HH)
		tcalc += VHt.getCalcTime();  // (V * H^t) and (VH / WHH)
		tcalc += W.getCalcTime();    // (W . VH)

		var tcomm:Long = 0;
		tcomm += WtV.getCommTime();  // (W^t * V) all reduce sum time
		tcomm += WtW.getCommTime();  // (W^t * W) all reduce sum time
		tcomm += H.getCommTime();   // H initial bcast

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
