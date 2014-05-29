/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2012.
 */

import x10.matrix.util.Debug;
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

public class DistSparseBlockSUMMA {
    public static def main(args:Rail[String]) {
    	val M = args.size > 0 ? Long.parse(args(0)):100;
    	val K = args.size > 1 ? Long.parse(args(1)):100;
    	val N = args.size > 2 ? Long.parse(args(2)):100;
    	val it = args.size > 3 ? Long.parse(args(3)):4;
    	val pnl = args.size > 4 ? Long.parse(args(4)):10;
    	val bMN = args.size > 5 ? Long.parse(args(5)):-1;
    	
		val testcase = new BenchRunSumma(M,K,N,it,pnl,bMN);
		testcase.run();
	}
}

class BenchRunSumma {
	public val M:Long;
	public val K:Long;
	public val N:Long;
	public val bM:Long;
	public val bN:Long;
	public val nzd:Double;
	

	//Matrix block partitioning
	val gA:Grid;
	val gB:Grid, gTransB:Grid;
	val gC:Grid;
	//Matrix blocks distribution
	val gdA:DistGrid, dA:DistMap;
	val dB:DistMap;
	val dC:DistMap;
	val itnum:Long;
	val panel:Long;

	val A:DistBlockMatrix(M,K);
	val B:DistBlockMatrix(K,N);
	val C:DistBlockMatrix(M,N);
	val tB:DistBlockMatrix(N,K);

	val summa:SummaMult;
	val summaT:SummaMultTrans;
	
	public def this(m:Long, k:Long, n:Long, it:Long, pnl:Long, blkmn:Long) {
		M = m; K=k; N=n;
		itnum = it;	panel = pnl; bM=blkmn; bN=blkmn;
		nzd = 0.01;

		
		gA = blkmn<0?Grid.make(M,K):new Grid(M, K, bM, bN);
		gB = blkmn<0?Grid.make(K,N):new Grid(K, N, bM, bN);
		gC = blkmn<0?Grid.make(M,N):new Grid(M, N, bM, bN);
		gTransB = blkmn<0?Grid.make(N,K):new Grid(N, K, bM, bN);
		
		gdA = DistGrid.make(gA);
		dA  = gdA.dmap;
		dB = (DistGrid.make(gB)).dmap;
		dC = (DistGrid.make(gC)).dmap;
		
		A = DistBlockMatrix.makeSparse(gA, dA, nzd).initRandom() as DistBlockMatrix(M,K);
		B = DistBlockMatrix.makeSparse(gB, dB, nzd).initRandom() as DistBlockMatrix(K,N);
		C = DistBlockMatrix.makeDense(gC, dC) as DistBlockMatrix(M,N);
		tB= DistBlockMatrix.makeSparse(gTransB, dB, nzd).initRandom() as DistBlockMatrix(N,K);

		//panel = SummaMult.estPanelSize(psz, A.getGrid(), B.getGrid());
		val w1 = A.makeTempFrontColBlocks(panel);
		val w2 = B.makeTempFrontRowBlocks(panel);
		//----- multTrans
		val w1t = C.makeTempFrontColBlocks(panel);
		val w2t = tB.makeTempFrontRowBlocks(panel);
		val tmp= C.makeTempFrontColBlocks(panel);
		val beta = 0.0;
		
		summa  = new SummaMult(panel, beta, A, B, C, w1, w2);
		summaT = new SummaMultTrans(panel, beta, A, tB, C, w1t, w2t, tmp);

		Console.OUT.printf("matrix (%dx%d) x (%dx%d) partitioned in (%dx%d) blocks ",
				gA.M, gA.N, gB.M, gB.N, gA.numRowBlocks, gA.numColBlocks);
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
		Debug.flushln("Starting SUMMA on multiply sparseCSC block Matrix benchmark");
		for (1..itnum) {
			summa.parallelMult();
		}
		val cmmtime = 1.0*summa.commTime/itnum;
		val caltime = 1.0*summa.calcTime/itnum;
		val runtime = cmmtime +caltime;
		Console.OUT.printf("SUMMA mult avg time:%.3f ms, ", runtime);
		Console.OUT.printf("communit time:%.1f percent, compute time:%.1f percent\n",
				100.0*cmmtime/runtime, 100.0*caltime/runtime);
			
	}

	public def benchMultTrans() {
		Console.OUT.println("Starting SUMMA on multiply-Transpose of sparseCSC block Matrix benchmark");
		for (1..itnum) 
			summaT.parallelMultTrans();
		
		val cmmtime = 1.0*summa.commTime/itnum;
		val caltime = 1.0*summa.calcTime/itnum;
		val runtime = cmmtime +caltime;
		Console.OUT.printf("SUMMA multTrans avg time:%.3f ms," , runtime);
		Console.OUT.printf("communit time:%.1f percent, compute time:%.1f percent\n",
				100.0*cmmtime/runtime, 100.0*caltime/runtime);
			
	}

} 
