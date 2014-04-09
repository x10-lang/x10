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

import x10.matrix.Vector;

import pagerank.PageRank;
import pagerank.SeqPageRank;

/**
 * Page Rank demo
 * <p>
 * Execution input parameters:
 * <p> (1) Rows and columns of G. Default 10000
 * <p> (2) Iterations number. Default 20
 * <p> (3) Row-wise partition of G. Default Place.MAX_PLACES, or number of places
 * <p> (4) Column-wise partition of G. Default 1.
 * <p> (5) Verification flag. Default 0 or false.
 * <p> (6) Nonzero density of G: Default 0.001
 * <p> (7) Print output flag: Default false. 
 */
public class RunPageRank {
	public static def main(args:Rail[String]): void {
		val mG = args.size > 0 ? Long.parse(args(0)):100; // Rows and columns of G
		val rG = args.size > 1 ? Long.parse(args(1)):Place.MAX_PLACES;
		val cG = args.size > 2 ? Long.parse(args(2)):1;
		val nZ = args.size > 3 ? Double.parse(args(3)):0.9001; //G's nonzero density
		val iT = args.size > 4 ? Long.parse(args(4)):20; //Iterations
		val vf = args.size > 5 ? Int.parse(args(5)):0n; //Verify result or not
		val pP = args.size > 6 ? Int.parse(args(6)):0n; //Print out input and output matrices

		Console.OUT.println("Set row/col G:"+mG+" density:"+nZ+" iterations:"+iT);
		if (mG<=0 || iT<1 || nZ<0.0)
			Console.OUT.println("Error in settings");
		else {
			val paraPR = PageRank.make(mG, nZ, iT, rG, cG);
			paraPR.init();

			paraPR.printInfo();

			val orgP = paraPR.P.local().clone(); //for verification purpose

			val paraP = paraPR.run();
			
			if (pP > 0) {
				Console.OUT.println("Input G sparse matrix\n" + paraPR.G);
				Console.OUT.println("Output vector P\n" + paraP);
			}
			
			if (vf > 0){
				val g = paraPR.G;
				val seqPR = new SeqPageRank(g.toDense(), orgP, 
						paraPR.E, paraPR.U, iT, nZ);
				val seqP = seqPR.run();
				if (paraP.equals(seqP as Vector(paraP.M))) 
					Console.OUT.println("Result verified");
				else
					Console.OUT.println("Verification failed!!!!");
			}
		}
	}
}

