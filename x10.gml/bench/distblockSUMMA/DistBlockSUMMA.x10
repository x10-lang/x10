/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2012-2014.
 */

import x10.util.Option;
import x10.util.OptionsParser;
import x10.util.Timer;

import x10.matrix.util.Debug;
import x10.matrix.util.MathTool;
import x10.matrix.DenseMatrix;

import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.summa.SummaMult;
import x10.matrix.distblock.summa.SummaMultTrans;

public class DistBlockSUMMA {
	public static def main(args:Rail[String]) {
        val opts = new OptionsParser(args, [
            Option("h","help","this information")
        ], [
            Option("m","","number of rows in matrices A and C, default = 100"),
            Option("k","","number of columns in matrix A and rows in matrix B, default = 100"),
            Option("n","","number of columns in matrices B and C, default = 100"),
            Option("d","density","nonzero density, default = 1.0 (dense)"),
            Option("p","panelSize","number of row blocks, default = 100"),
            Option("b","blockMN","number of row/column blocks in matrix C; default = 1"),
            Option("i","iterations","number of iterations, default = 10")
        ]);

        if (opts.filteredArgs().size!=0) {
            Console.ERR.println("Unexpected arguments: "+opts.filteredArgs());
            Console.ERR.println("Use -h or --help.");
            System.setExitCode(1n);
            return;
        }
        if (opts("h")) {
            Console.OUT.println(opts.usage(""));
            return;
        }

        val M = opts("m", 100);
        val K = opts("k", 100);
        val N = opts("n", 100);
        val nonzeroDensity = opts("d", 1.0f);
        val panelSize = opts("p", 100);
        val blockMN = opts("b", 1);
        val iterations = opts("i", 10);

		val testcase = new BenchRunSumma(M,K,N,nonzeroDensity,iterations,panelSize,blockMN);
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
	
	public def this(m:Long, k:Long, n:Long, nzd:Float, it:Long, pnl:Long, blkmn:Long) {
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

		Debug.flushln("Start initialization dist matrices");

		A.initRandom();
		B.initRandom();
		tB.initRandom();

		if (nzd < 0.9) 
			Debug.flushln("All matrices have sparse blocks, sparsity is set to " + nzd);
		else
			Debug.flushln("All matrices have dense blocks");
	}

    public def run (): void {
		benchMult();
		benchMultTrans();	
	}
    
	public def benchMult(){
		Console.OUT.println("Starting SUMMA on dist block matrix multiplication benchmark");
		Console.OUT.flush();
		val stt = Timer.milliTime();
		for (1..itnum) {
			summa.parallelMult();
		}
		val runtime = 1.0*(Timer.milliTime() - stt)/itnum;

		val cmmtime = 1.0*summa.commTime/itnum;
		val caltime = 1.0*summa.calcTime/itnum;
		Console.OUT.printf("SUMMA mult total run time: %8.1f ms, ", runtime);
		Console.OUT.printf("commun: %8.1f ms( %2.1f percent), comput: %8.1f ms( %2.1f percent)\n",
				cmmtime, 100.0*cmmtime/runtime, caltime,  100.0*caltime/runtime);

        val flops = 2.0*M*N*K;
        val gflopPerSec = flops/runtime/1e6;
        Console.OUT.printf("GFLOP: %9.2f GFLOP/s: %9.2f GFLOP/s/place: %9.2f\n", flops, gflopPerSec, gflopPerSec/Place.numPlaces());
	}

	public def benchMultTrans() {
		Console.OUT.println("Starting SUMMA on dist block matrix of multiply-Transpose benchmark");
		Console.OUT.flush();
		val stt = Timer.milliTime();
		for (1..itnum) {
			summaT.parallelMultTrans();
		}
		val runtime = 1.0*(Timer.milliTime() - stt)/itnum;
		
		val cmmtime = 1.0*summaT.commTime/itnum;
		val caltime = 1.0*summaT.calcTime/itnum;
		Console.OUT.printf("SUMMA multTrans total run time: %8.1f ms, " , runtime);
		Console.OUT.printf("commun: %8.1f ms( %2.1f percent), comput: %8.1f ms( %2.1f percent)\n",
				cmmtime, 100.0*cmmtime/runtime, caltime,  100.0*caltime/runtime);
        val flops = 2.0*M*N*K;
        val gflopPerSec = flops/runtime/1e6;
        Console.OUT.printf("GFLOP: %9.2f GFLOP/s: %9.2f GFLOP/s/place: %9.2f\n", flops, gflopPerSec, gflopPerSec/Place.numPlaces());
	}
} 
