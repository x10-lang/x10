/**
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 */

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.dist.DistDenseMatrix;
import x10.matrix.dist.DistSparseMatrix;

/**
 * Page Rank demo
 */
public class RunPageRank {
	public static def main(args:Rail[String]): void {
		val mG = args.size > 0 ? Int.parse(args(0)):10000; // Rows and columns of G
		val iT = args.size > 1 ? Int.parse(args(1)):20;//Iterations
		val vf = args.size > 2 ? Int.parse(args(2)):0; //Verify result or not
		val nP = args.size > 3 ? Int.parse(args(3)):1; //column of P
		val nZ = args.size > 4 ? Double.parse(args(4)):0.001; //G's nonzero density
		val pP = args.size > 5 ? Int.parse(args(5)):0; //Print out input and output matrices

		Console.OUT.println("Set row/col G:"+mG+" density:"+nZ+" iteration:"+iT);
		if (mG<=0 || iT<1 || nP<1 || nZ<0.0)
			Console.OUT.println("Error in settings");
		else {
			val paraPR = new PageRank(mG, nP, nZ, iT);
			paraPR.init();
			Console.OUT.println("Input G sparse matrix\n" + paraPR.G);

			paraPR.printInfo();

			val orgP = paraPR.P.local().clone(); //for verification purpose

			val paraP = paraPR.run();
			
			if (pP > 0) {
				Console.OUT.println("Input G sparse matrix\n" + paraPR.G);
				Console.OUT.println("Output matrix P\n" + paraP);
			}
			
			if (vf > 0){
				val g = paraPR.G;
				val seqPR = new SeqPageRank(g.toDense(), orgP,
						paraPR.E, paraPR.U, iT, nZ);
				val seqP = seqPR.run();
				if (paraP.equals(seqP)) 
					Console.OUT.println("Result reverified");
				else
					Console.OUT.println("Verification failed!!!!");
			}
		}
	}
}

