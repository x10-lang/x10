/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2012.
 */

import x10.util.Timer;

import x10.matrix.util.Debug;
import x10.matrix.util.MathTool;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;

import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.summa.SummaMult;
import x10.matrix.distblock.summa.SummaMultTrans;

public class DistBlockSUMMA {
	public static def main(args:Rail[String]) {
		val M   = args.size > 0 ? Long.parse(args(0)):100;
		val K   = args.size > 1 ? Long.parse(args(1)):100;
		val N   = args.size > 2 ? Long.parse(args(2)):100;
		val nzd = args.size > 3 ? Double.parse(args(3)):1.0;//Default is dense block matrix
		val pnl = args.size > 4 ? Long.parse(args(4)):64;
		val bMN = args.size > 5 ? Long.parse(args(5)):1;
		val it  = args.size > 6 ? Long.parse(args(6)):4;

		val testcase = new BenchRunSumma(M,K,N,nzd,it,pnl,bMN);
		testcase.run();
	}
}

class BenchRunSumma {
	public val M:Long;
	public val K:Long;
	public val N:Long;
	public val bM:Long;
	public val bN:Long;
	

	val itnum:Long;
	val panel:Long;

	val A:DistBlockMatrix(M,K);
	val B:DistBlockMatrix(K,N);
	val C:DistBlockMatrix(M,N);
	val tB:DistBlockMatrix(N,K);

	val summa:SummaMult;
	val summaT:SummaMultTrans;
	
	public def this(m:Long, k:Long, n:Long, nzd:Double, it:Long, pnl:Long, blkmn:Long) {
		val pM = MathTool.sqrt(Place.numPlaces());
		val pN = Place.numPlaces()/pM;

		itnum = it;	panel = pnl; 
		M = m; K=k; N=n;
		bM = blkmn<1?pM:blkmn*pM;
		bN = blkmn<1?pN:blkmn*pN;
			
		A = (nzd<0.9)? DistBlockMatrix.makeSparse(M, K, bM, bN, pM, pN, nzd):
			DistBlockMatrix.makeDense(M, K, bM, bN, pM, pN);
		B = (nzd<0.9)? DistBlockMatrix.makeSparse(K, N, bM, bN, pM, pN, nzd):
			DistBlockMatrix.makeDense(K, N, bM, bN, pM, pN);
		C = DistBlockMatrix.makeDense(M, N, bM, bN, pM, pN);
		
		tB= (nzd<0.9)? DistBlockMatrix.makeSparse(N, K, bM, bN, pM, pN, nzd):
			DistBlockMatrix.makeDense(N, K, bM, bN, pM, pN);

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

		Console.OUT.printf("Input matrix  A:(%d,%d) partitioned in (%dx%d) blocks, distr (%dx%d) places\n",
				M, K, bM, bN, pM, pN);
		Console.OUT.flush();
		Console.OUT.printf("Input matrix  B:(%d,%d) partitioned in (%dx%d) blocks, distr (%dx%d) places\n",
				K, N, bM, bN, pM, pN);
		Console.OUT.printf("Output matrix C:(%d,%d) partitioned in (%dx%d) blocks, distr (%dx%d) places\n",
				M, N, bM, bN, pM, pN);
		Console.OUT.flush();
		Console.OUT.printf("SUMMA panel size:%d\n", panel);

		Console.OUT.printf("Start initialization dist matrices\n", panel);
		Console.OUT.flush();

		A.initRandom();
		B.initRandom();
		tB.initRandom();

		if (nzd < 0.9) 
			Console.OUT.printf("All matries has sparse blocks, sparsity is set to %f\n", nzd);
		else
			Console.OUT.printf("All matries has dense blocks\n");
		Console.OUT.flush();
	}

    public def run (): void {

		var ret:Boolean = true;
 		// Set the matrix function
		benchMult();
		benchMultTrans();	
	}
    
	public def benchMult(){
		Console.OUT.println("Starting SUMMA on dist block matrix multiplication benchmark");
		Console.OUT.flush();
		val stt:Long = Timer.milliTime();
		for (1..itnum) {
			summa.parallelMult();
		}
		val runtime:Double = 1.0*(Timer.milliTime() - stt)/itnum;

		val cmmtime = 1.0*summa.commTime/itnum;
		val caltime = 1.0*summa.calcTime/itnum;
		Console.OUT.printf("SUMMA mult total run time: %8.1f ms, ", runtime);
		Console.OUT.printf("commun: %8.1f ms( %2.1f percent), comput: %8.1f ms( %2.1f percent)\n",
				cmmtime, 100.0*cmmtime/runtime, caltime,  100.0*caltime/runtime);
		
	}

	public def benchMultTrans() {
		Console.OUT.println("Starting SUMMA on dist block matrix of multiply-Transpose benchmark");
		Console.OUT.flush();
		val stt:Long = Timer.milliTime();
		for (1..itnum) {
			summaT.parallelMultTrans();
		}
		val runtime:Double = 1.0*(Timer.milliTime() - stt)/itnum;
		
		val cmmtime = 1.0*summaT.commTime/itnum;
		val caltime = 1.0*summaT.calcTime/itnum;
		Console.OUT.printf("SUMMA multTrans total run time: %8.1f ms, " , runtime);
		Console.OUT.printf("commun: %8.1f ms( %2.1f percent), comput: %8.1f ms( %2.1f percent)\n",
				cmmtime, 100.0*cmmtime/runtime, caltime,  100.0*caltime/runtime);
	}

} 
