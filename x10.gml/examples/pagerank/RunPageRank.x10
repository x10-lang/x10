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
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
//
//import x10.matrix.dist.DupDenseMatrix;
import x10.matrix.dist.DistDenseMatrix;
import x10.matrix.dist.DistSparseMatrix;

import pagerank.PageRank;
import pagerank.SeqPageRank;

/**
 * Page Rank demo
 * <p>
 * Execution input parameters:
 * <p> (1) Rows and columns of G. Default 10000
 * <p> (2) Iteration number. Default 20
 * <p> (3) Row-wise partition of G. Default Place.MAX_PLACES, or number of places
 * <p> (4) Column-wise partition of G. Default 1.
 * <p> (5) Verification flag. Default 0 or false.
 * <p> (6) Nonzero density of G: Default 0.001
 * <p> (7) Print output flag: Default false. 
 * 
 */
public class RunPageRank {

	public static def main(args:Array[String](1)): void {
		
		val mG = args.size > 0 ? Int.parse(args(0)):10000; // Rows and columns of G
		val iT = args.size > 1 ? Int.parse(args(1)):20;//Iterations
		val rG = args.size > 2 ? Int.parse(args(2)):Place.MAX_PLACES;
		val cG = args.size > 3 ? Int.parse(args(3)):1;
		val vf = args.size > 4 ? Int.parse(args(4)):0; //Verify result or not
		val nZ = args.size > 5 ? Double.parse(args(5)):0.001; //G's nonzero density
		val pP = args.size > 6 ? Int.parse(args(6)):0; //Print out input and output matrices

		Console.OUT.println("Set row/col G:"+mG+" density:"+nZ+" iteration:"+iT);
		if (mG<=0 || iT<1 || nZ<0.0)
			Console.OUT.println("Error in settings");
		else {
			val paraPR = PageRank.make(mG, nZ, iT, rG, cG);
			paraPR.init();
			//paraPR.G.printMatrix("Input G sparse matrix");

			paraPR.printInfo();

			val orgP = paraPR.P.local().toDense(); //for verification purpose

			val paraP = paraPR.run();
			
			if (pP > 0) {
				paraPR.G.printMatrix("Input G sparse matrix");
				paraPR.P.printMatrix("Output matrix P");
			}
			
			if (vf > 0){
				val g = paraPR.G;
				val seqPR = new SeqPageRank(g.toDense(), orgP, 
						paraPR.E.toDense(), paraPR.U.toDense(), iT, nZ);
				val seqP = seqPR.run();
				if (paraP.equals(seqP as Matrix{self.M==paraP.M, self.N==paraP.N})) 
					Console.OUT.println("Result reverified");
				else
					Console.OUT.println("Verification failed!!!!");
			}
		}
	}
}

