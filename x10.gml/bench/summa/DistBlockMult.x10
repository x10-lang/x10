/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2012.
 */

import x10.io.Console;

import x10.matrix.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;

import x10.matrix.block.Grid;
import x10.matrix.block.BlockMatrix;
import x10.matrix.block.DenseBlockMatrix;

import x10.matrix.distblock.DistMap;
import x10.matrix.distblock.DistGrid;

import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.summa.SummaMult;
import x10.matrix.distblock.summa.SummaMultTrans;


/**
   <p>

   <p>
 */
public class DistBlockMult {
	
    public static def main(args:Array[String](1)) {
		val testcase = new BenchRunSumma(args);
		testcase.run();
	}
}

class BenchRunSumma {
	public val M:Int;
	public val K:Int;
	public val N:Int;
	public val bM:Int;
	public val bN:Int;
	
	//-------------
	//Matrix block partitioning
	val gA:Grid;
	val gB:Grid, gTransB:Grid;
	val gC:Grid;
	//Matrix blocks distribution
	val gdA:DistGrid, dA:DistMap;
	val dB:DistMap;
	val dC:DistMap;
	val itnum:Int;
	val panel:Int;
	//---------------------
	val A:DistBlockMatrix(M,K);
	val B:DistBlockMatrix(K,N);
	val C:DistBlockMatrix(M,N);
	val tB:DistBlockMatrix(N,K);
	//-----------
	val summa:SummaMult;
	val summaT:SummaMultTrans;
	
	
	public def this(args:Array[String](1)) {
		M = args.size > 0 ?Int.parse(args(0)):100;
		K = args.size > 1 ?Int.parse(args(1)):100;
		N = args.size > 2 ?Int.parse(args(2)):100;
		bM = args.size > 3 ?Int.parse(args(3)):4;
		bN = args.size > 4 ?Int.parse(args(4)):4;
		var psz:Int = args.size > 5 ?Int.parse(args(5)):10;
		itnum = args.size > 6 ?Int.parse(args(6)):4;
		
		gA = new Grid(M, K, bM, bN);
		gB = new Grid(K, N, bM, bN);
		gC = new Grid(M, N, bM, bN);
		gTransB = new Grid(N, K, bM, bN);
		
		gdA = DistGrid.make(gA);
		dA  = gdA.dmap;
		dB = (DistGrid.make(gB)).dmap;
		dC = (DistGrid.make(gC)).dmap;
		
		A = DistBlockMatrix.makeDense(gA, dA).initRandom() as DistBlockMatrix(M,K);
		B = DistBlockMatrix.makeDense(gB, dB).initRandom() as DistBlockMatrix(K,N);
		C = DistBlockMatrix.makeDense(gC, dC) as DistBlockMatrix(M,N);
		tB= DistBlockMatrix.makeDense(gTransB, dB).initRandom() as DistBlockMatrix(N,K);
		//-------------------
		panel = SummaMult.estPanelSize(psz, A.getGrid(), B.getGrid());
		val w1 = A.makeTempFrontColBlocks(panel);
		val w2 = B.makeTempFrontRowBlocks(panel);
		//----- multTrans
		val w1t = C.makeTempFrontColBlocks(panel);
		val w2t = tB.makeTempFrontRowBlocks(panel);
		val tmp= C.makeTempFrontColBlocks(panel);
		val beta = 0.0;
		
		summa  = new SummaMult(panel, beta, A, B, C, w1, w2);
		summaT = new SummaMultTrans(panel, beta, A, tB, C, w1t, w2t, tmp);
		//-----------------------------------------
		Console.OUT.printf("matrix (%dx%d) x (%dx%d) partitioned in (%dx%d) blocks ",
				M, K, K, N, bM, bN);
		Console.OUT.printf("distributed in (%dx%d) places, panel size:%d\n", 
				gdA.numRowPlaces, gdA.numColPlaces, panel);
		Console.OUT.flush();
	}

    public def run (): void {

		var ret:Boolean = true;
 		// Set the matrix function
		benchMult();
		benchMultTrans();
	
	}
    
	public def benchMult(){
		Debug.flushln("Starting SUMMA on multiply dense block Matrix benchmark");
		for (1..itnum) {
			summa.parallelMult();
		}
		val cmmtime = 1.0*summa.commTime/itnum;
		val caltime = 1.0*summa.calcTime/itnum;
		val runtime = cmmtime +caltime;
		Console.OUT.printf("SUMMA mult avg time:%.3f s, communit time:%.1f%% compute time:%.1f%%\n",
				runtime, cmmtime/runtime *100, caltime/runtime*100);
		
	}

	public def benchMultTrans() {
		Console.OUT.println("Starting SUMMA on multiply-Transpose of dense block Matrix benchmark");
		for (1..itnum) 
			summaT.parallelMultTrans();
		
		val cmmtime = 1.0*summa.commTime/itnum;
		val caltime = 1.0*summa.calcTime/itnum;
		val runtime = cmmtime +caltime;
		Console.OUT.printf("SUMMA multTrans avg time:%.3f s, communit time:%.1f%% compute time:%.1f%%\n",
				runtime, cmmtime/runtime *100, caltime/runtime*100);
		
	}

} 
