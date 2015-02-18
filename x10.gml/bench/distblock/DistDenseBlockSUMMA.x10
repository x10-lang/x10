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

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.block.Grid;
import x10.matrix.distblock.DistMap;
import x10.matrix.distblock.DistGrid;
import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.summa.SummaMult;
import x10.matrix.distblock.summa.SummaMultTrans;

public class DistDenseBlockSUMMA {
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
        val panelSize = opts("p", 100);
        val blockMN = opts("b", 1);
        val iterations = opts("i", 10);

		val testcase = new BenchRunSumma(M,K,N,iterations,panelSize,blockMN);
		testcase.run();
	}
}

class BenchRunSumma {
	public val M:Long;
	public val K:Long;
	public val N:Long;
	public val bM:Long;
	public val bN:Long;

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

		gA = blkmn<0?Grid.make(M,K):new Grid(M, K, bM, bN);
		gB = blkmn<0?Grid.make(K,N):new Grid(K, N, bM, bN);
		gC = blkmn<0?Grid.make(M,N):new Grid(M, N, bM, bN);
		gTransB = blkmn<0?Grid.make(N,K):new Grid(N, K, bM, bN);
				
		gdA = DistGrid.make(gA);
		dA  = gdA.dmap;
		dB = (DistGrid.make(gB)).dmap;
		dC = (DistGrid.make(gC)).dmap;
		
		A = DistBlockMatrix.makeDense(gA, dA).initRandom() as DistBlockMatrix(M,K);
		B = DistBlockMatrix.makeDense(gB, dB).initRandom() as DistBlockMatrix(K,N);
		C = DistBlockMatrix.makeDense(gC, dC) as DistBlockMatrix(M,N);
		tB= DistBlockMatrix.makeDense(gTransB, dB).initRandom() as DistBlockMatrix(N,K);

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
				M, K, K, N, gA.numRowBlocks, gA.numColBlocks);
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
		Console.OUT.println("Starting SUMMA on dense block Matrix multiplication benchmark");
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
		Console.OUT.println("Starting SUMMA on dense block Matrix of multiply-Transpose benchmark");
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
