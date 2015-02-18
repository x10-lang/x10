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

import x10.matrix.Matrix;
import x10.matrix.ElemType;
import x10.util.Timer;

import x10.matrix.util.Debug;

import x10.matrix.block.Grid;
import x10.matrix.dist.DistDenseMatrix;
import x10.matrix.dist.DistSparseMatrix;
import x10.matrix.dist.DupDenseMatrix;

/**
 * GNNMF implementation based on GML distributed dense/sparse matrix.
 */
public class GNNMF {
    //------GNNMF matrix size------
    val Vm:Long;
    val Vn:Long;// = 100000;
    val Wn:Long = 10;       
    // ------GNNMF parameters------
    public val iterations:Int;
    val nzDensity:Float;
    // ------Data partitioning------
    val gridV:Grid;
    val gridW:Grid;
    // ------Input and output data------
    public val V:DistSparseMatrix(Vm, Vn);
    public val W:DistDenseMatrix(Vm, Wn);
    public val H:DupDenseMatrix(Wn, Vn);
    // ------Temp data and matrix------ 
    val WV:DupDenseMatrix(W.N, V.N);   //Store W^t * V result (10x100000) like H
    //val tmpWV:DupDenseMatrix(W.N, V.N);    
    val WW:DupDenseMatrix(W.N, W.N);   //Store W^t * W  (10x10)
    //val tmpWW:DupDenseMatrix(W.N, W.N);   
    
    val WWH:DupDenseMatrix(W.N, H.N);  //Store WW * H   (10x100000) like H
    val VH:DistDenseMatrix(Vm, Wn);    //Store V * H^t  (dx10) like W
    val HH:DupDenseMatrix(H.M, H.M);   //Store H * H^t, (10x10)
    val WHH:DistDenseMatrix(Vm, Wn);   //Store W * HH   (dx10) like W
    
    // ------Profile timing------
    val ts:Long = Timer.milliTime();
    var tt:Long = 0;
    var t1:Long = 0;
    
    public def this(d:Long, nv:Long, nz:Float, i:Int) {
	Vm = d; Vn =nv;
	nzDensity=nz;
	iterations = i;
	//
	gridV = new Grid(Vm, Vn, Place.numPlaces(), 1);
	gridW = new Grid(Vm, Wn, Place.numPlaces(), 1);
	//              
	//------Input matrix data allocation------
	Debug.flushln("Start memory allocation");               
	V = DistSparseMatrix.make(gridV, nzDensity) as DistSparseMatrix(Vm, Vn);
	W = DistDenseMatrix.make(gridW) as DistDenseMatrix(Vm, Wn);
	H = DupDenseMatrix.make(Wn, Vn);
	
	WV  = DupDenseMatrix.make(W.N, V.N); // W^t * V
	//tmpWV  =  DupDenseMatrix.make(W.N, V.N);
	WW  = DupDenseMatrix.make(W.N, W.N); // W^t * W
	//tmpWW  = DupDenseMatrix.make(W.N, W.N); // W^t * W
	WWH = DupDenseMatrix.make(W.N, H.N); // (W^t*W)*H
	
	VH  = DistDenseMatrix.make(gridW) as DistDenseMatrix(Vm, Wn);   // V*H^t
	HH  = DupDenseMatrix.make(H.M, H.M); // H * H^t
	WHH = DistDenseMatrix.make(gridW) as DistDenseMatrix(Vm, Wn);   // W * (H*H^t)
    }
    
    public def init():void {
	Debug.flushln("Start initialize input data");           
	V.initRandom(nzDensity);
	Debug.flushln("Dist sparse matrix initialization completes");           
	W.initRandom();
	Debug.flushln("Dist dense matrix initialization completes");
	H.initRandom();
	Debug.flushln("Dup dense matrix initialization completes");
	
    }
    
    public def printInfo():void {
	val nzc =  V.getTotalNonZeroCount() as Float;
	val nzd =  nzc / (V.M * V.N);
	
	Debug.flushln("Starting X10 GNNMF ");
	Console.OUT.printf("W:(%dx%d) V:(%dx%d) H:(%dx%d)", 
			   Vm, Wn, Vm, Vn, Wn, Vn);
	Console.OUT.printf("gridV(%dx%d) gridV(%dx%d) nzDensity:%.3f\n",
			   gridW.numRowBlocks, gridW.numColBlocks,
			   gridV.numRowBlocks, gridV.numColBlocks, 
			   nzDensity);
	Console.OUT.flush();
	
	Console.OUT.printf("V nonzero %f, %dx%d, density is %f\n", 
			   nzc, V.M, V.N, nzd);
	Console.OUT.printf("Average column nonzero count:%.3f, std:%.3f\n",
			   V.getAvgColumnSize(), V.getColumnSizeStdDvn());
	Console.OUT.printf("Average nonzero index distance:%.3f, std:%.3f\n",
			   V.compAvgIndexDst(), V.compIndexDstStdDvn());
	//V.printBlockColumnSizeAvgStd();
	Console.OUT.flush();
	
    }
    
    public def comp_WV_WWH() : void {
	/* H . (W^t * V / (W^t * W) * H) -> H */
	WV.transMult(W, V, false); // W^t * V  -> WV
	
	//WV.print("Parallel W^t * V =");
	WW.transMult(W, W, false);// W^t * W  -> WW
	
	//WW.print("Parallel W^t * W = ");
	WWH.mult(WW, H);
	
	//WWH.print("Parallel dup WW * H = ");
	WV.cellDiv(WWH);                     // WV / WWH -> WV
	
	//WV.print("Parallel WV ./ WWH = ");
	H.cellMult(WV);                      // H . WV   -> H           
	//H.print("Parallel H update:");
    }
    
    public def comp_VH_WHH() : void {
	/* W . (V * H^t / W * (H * H^t)) -> W */
	//V.print("Parallel input V");
	//H.print("Parallel Input H");
	VH.multTrans(V, H, false);                // V  * H^t -> VH
	//VH.print("Parallel VH:");
	
	HH.multTrans(H, H);                       // H  * H^t -> HH
	//HH.print("Parallel HH:");
	
	WHH.mult(W, HH, false);                  // W  * HH  -> WHH  
	//WHH.print("Parallel WHH:");
	
	VH.cellDiv(WHH);                         // VH / WHH -> VH 
	//VH.print("Parallel VH/WHH:");
	
	W.cellMult(VH);                          // W  . VH  -> W
	//W.print("Parallel W updated:");
    }
    
    public def run() : void {
	/* Timing */ val st = Timer.milliTime();
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
	tcalc += WV.getCalcTime();  // (W^t * V) and (WV / WWH)
	tcalc += WW.getCalcTime();  // (W^t * W ) time
	tcalc += WWH.getCalcTime(); // (WW * H) 
	tcalc += H.getCalcTime();   // (H . WV)
	
	tcalc += HH.getCalcTime();  // (H * H^t)
	tcalc += WHH.getCalcTime(); // (W * HH)
	tcalc += VH.getCalcTime();  // (V * H^t) and (VH / WHH)
	tcalc += W.getCalcTime();   // (W . VH)
	
	var tcomm:Long = 0;
	tcomm += WV.getCommTime();  // (W^t * V) all reduce sum time
	tcomm += WW.getCommTime();  // (W^t * W) all reduce sum time
	tcomm += H.getCommTime();   // H initial bcast
	
	Console.OUT.printf("Total time:    %dms, Calc: %dms, Comm:  %dms\n",
			   tt, tcalc, tcomm);
	Console.OUT.printf("W^t * V: Comp: %d, AllReduce Comm: %d\n",
			   WV.getCalcTime(), WV.getCommTime());
	
	Console.OUT.printf("W^t * W: Comp: %d, AllReduce Comm: %d\n",
			   WW.getCalcTime(), WW.getCommTime());
	
	Console.OUT.printf("V * H^t  Comp: %d\n",
			   VH.getCalcTime());
	
	Console.OUT.printf("One time H bcast: %d\n", H.getCommTime());          
	
	Console.OUT.flush();
    }
}
