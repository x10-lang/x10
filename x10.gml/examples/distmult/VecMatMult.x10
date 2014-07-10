/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import x10.matrix.util.Debug;
import x10.matrix.Matrix;
import x10.matrix.util.MathTool;
import x10.matrix.Vector;
import x10.matrix.block.Grid;
import x10.matrix.distblock.DistMap;
import x10.matrix.distblock.DistGrid;
import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.DupBlockMatrix;
import x10.matrix.distblock.DistVector;
import x10.matrix.distblock.DupVector;
import x10.matrix.distblock.DistDupVectorMult;

public class VecMatMult {
    public static def main(args:Rail[String]) {
		val testcase = new VecMatMult(args);
		testcase.run();
    }
	
    public val M:Long;
	public val N:Long;
	public val bM:Long;
	public val bN:Long;
	public val nzd:Double;
	public val verify:Boolean;
	
	public def this(args:Rail[String]) {
		M = args.size > 0 ? Long.parse(args(0)):20;
		N = args.size > 1 ? Long.parse(args(1)):(M as Int)+1;
		bM = args.size > 2 ? Long.parse(args(2)):4;
		bN = args.size > 3 ? Long.parse(args(3)):5;
		nzd =  args.size > 4 ?Double.parse(args(4)):0.99;
		verify = args.size > 5 ? false:true;
	}

	public def run (): void {
		var ret:Boolean = true;

		ret &= (demoDistDistMult());
		ret &= (demoDupDistMult());
		ret &= (demoDupDistDupMult());
		
		if (ret)
			Console.OUT.println("Vector-matrix multiply demo done.");
		else
			Console.OUT.println("----------------Error in Dist/Dup Vector DistMatrix multiply demo!----------------");
	}
	
	
	public def demoDistDistMult() {
		Console.OUT.println("Starting DistVector * DistBlockMatrix -> DupVector demo, matrix has vertical dist");
		val pM = Place.numPlaces(), pN= 1;//Vertical distribution

		val vA = DistVector.make(M, pM).initRandom();
		val mB = DistBlockMatrix.makeDense(M, N, bM, bN, pM, pN).initRandom();
		val vC = vA % mB;
		
		if (verify) {
			Console.OUT.printf("Starting verification\n");
			val vc = vA.toVector() % mB.toDense();

			if (vc.equals(vC.local() as Vector(vc.M)))
				Console.OUT.println("DistVector * DistBlockMatrix result verified");
			else {
				Console.OUT.println("--------ERROR in DistVector * DistBlockMatrix multiply!--------");
				return false;
			}
		}
		return true;
	}


	public def demoDupDistMult() : Boolean {
		Console.OUT.println("Starting DupVector * DistBlockMatrix -> DistVector, matrix has horizontal dist");
		val pM = 1, pN=Place.numPlaces(); //Horizontal distribution

		val vA = DupVector.make(M).initRandom();
		val gPart = new Grid(M, N, bM, bN); //May not be balanced when dist over (pM x pN) places;
		//use new Grid(M, N, bM, bN, pM, pN) to get better balanced 
		val distG = new DistGrid(gPart, pM, pN);
		val mB = DistBlockMatrix.makeSparse(gPart, distG, nzd).initRandom() as DistBlockMatrix(M,N);

		val vC = vA % mB;

		if (verify) {
			Console.OUT.printf("Starting verification\n");
			val vc = vA.local() % mB.toDense();
			if ( vC.equals(vc as Vector(vC.M)))			
				Console.OUT.println("DupVector * DistBlockMatrix result verified");
			else {
				Console.OUT.println("--------ERROR in DupVector * DistBlockMatrix multiply!--------");
				return false;
			}
		}
		return true;
	}


	public def demoDupDistDupMult():Boolean{
		val pM:Long = MathTool.sqrt(Place.numPlaces());
		val pN:Long = Place.numPlaces() / pM;
		
		Console.OUT.printf("Starting DupVector += DupVector * DistBlockMatrix, matrix is dist over %dx%d places\n", pM, pN);
		val vA = DupVector.make(M).initRandom();
		val mB = DistBlockMatrix.makeSparse(M, N, bM, bN, pM, pN, nzd).initRandom();
		val vC = DupVector.make(N).initRandom();
		val vc = vC.local().clone();
		
		DistDupVectorMult.comp(vA, mB, vC, true);

		if (verify) {
			Console.OUT.printf("Starting verification\n");
			vc.mult(vA.local(), mB.toDense(), true);
			if (vc.equals(vC.local()))
				Console.OUT.println("DupVector += DupVector * DistBlockMatrix result verified");
			else {
				Console.OUT.println("--------ERROR DupVector * DistBlockMatrix = DupVector multiply!--------");
				return false;
			}
		}
		return true;
	}
}
