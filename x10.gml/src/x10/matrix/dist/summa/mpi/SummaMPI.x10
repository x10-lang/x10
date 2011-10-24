/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2011.
 */

package x10.matrix.dist.summa.mpi;

import x10.io.Console;
//
import x10.compiler.Native;
import x10.compiler.NativeCPPInclude;
import x10.compiler.NativeCPPCompilationUnit;
//
// Matrix base and dense matrix
import x10.matrix.Matrix;
import x10.matrix.Debug;
import x10.matrix.DenseMatrix;
// Grid distribution
import x10.matrix.block.Grid;
import x10.matrix.dist.DistMatrix;
import x10.matrix.dist.DistDenseMatrix;

// SUMMA C implementation and API
@NativeCPPInclude("summa_api.h")
@NativeCPPCompilationUnit("summa_api.c")
@NativeCPPInclude("summa.h")
@NativeCPPCompilationUnit("summa.c")

/**
 * This is C-MPI implementation X10 API.
 * The C-MPI implementation can be found at 
 * "Summa: Scalable universal matrix multiplication algorithm" 
 * by Robert A. Van De Geijn ,  Jerrell Watts
 * 
 */
public class SummaMPI {

	//------------------------------------------------
    @Native("c++","print_proc_info()")
		public static native def print_proc():void;

    @Native("c++","reset_usetime()")
		public static native def reset_usetime():void;
    @Native("c++","get_usetime((#1)->raw()->raw())")
 		public static native def get_usetime(t:Array[Double](1)):void;

    @Native("c++","setup_grid_partition(#1, #2, #3, #4, #5)")
		public static native def setup_grid_partition(
				pid:Int, 
				numRow:Int, 
				numCol:Int,
				rowId:Int,  
				colId:Int):void;

	@Native("c++","summa_mult((#1)->raw()->raw(),(#2)->raw()->raw(),(#3)->raw()->raw(),(#4)->raw()->raw(),(#5)->raw()->raw(),(#6)->raw()->raw(),(#7)->raw()->raw(),(#8)->raw()->raw(),(#9)->raw()->raw())")
		public static native def summa_mult(
				mat:Array[Int](1), 
				scal:Array[Double](1),
				Arows:Array[Int](1), 
				Acols:Array[Int](1), 
				Adata:Array[Double](1),
				Brows:Array[Int](1), 
				Bcols:Array[Int](1), 
				Bdata:Array[Double](1),
				Cdata:Array[Double](1)):void;

	@Native("c++","summa_mult_T((#1)->raw()->raw(),(#2)->raw()->raw(),(#3)->raw()->raw(),(#4)->raw()->raw(),(#5)->raw()->raw(),(#6)->raw()->raw(),(#7)->raw()->raw(),(#8)->raw()->raw(),(#9)->raw()->raw())")
		public static native def summa_mult_T(
				mat:Array[Int](1), 
				scal:Array[Double](1),
				Arows:Array[Int](1), 
				Acols:Array[Int](1), 
				Adata:Array[Double](1),
				Brows:Array[Int](1), 
				//Bcols:Array[Int](1), == Acols 
				Bdata:Array[Double](1),
				//Crows==Arows
				Ccols:Array[Int](1), 
				Cdata:Array[Double](1)):void;
	//------------------------------------------------------------------


	//=====================================================================
	// Static function
	//=====================================================================

	/**
	 * SUMMA algorithm C implementation API
	 * 
	 * @param ps -- panel size
	 * @param beta -- scaling factor of output matrix C
	 * @param A  -- first operand matrix 
	 * @param B  -- second operand matrix
	 * @param C  -- output matrix
	 * @return  -- output matrix C
	 */
	public static def mult(
			ps:Int, beta:Double, 
			A:DistDenseMatrix, 
			B:DistDenseMatrix, 
			C:DistDenseMatrix): DistDenseMatrix(C) {

		val panelSize = Math.min(Math.min(ps, A.grid.getMinColSize()),
								 Math.min(ps, B.grid.getMinRowSize()));
		val M:Int = A.M;
		val N:Int = B.N;
		val K:Int = B.M;
		
		Debug.assure(C.M==A.M&&A.N==B.M&&B.N==C.N, "Matrix mutiply dimension mismatch");
		Debug.assure(A.grid.numRowBlocks==B.grid.numRowBlocks&&
				A.grid.numColBlocks==B.grid.numColBlocks&&
				B.grid.numRowBlocks==C.grid.numRowBlocks&&
				B.grid.numColBlocks==C.grid.numColBlocks, 
		"Matrix partitioning missmatch!");

		//
		finish ateach (val [p]:Point in C.dist) {
			//
			val r = C.grid.getRowBlockId(p);
			val c = C.grid.getColBlockId(p);
			//
			val Ad = A.distBs(p).getData();
			val Bd = B.distBs(p).getData();
			val Cd = C.distBs(p).getData();
			//
			setup_grid_partition(p, C.grid.numRowBlocks, C.grid.numColBlocks, r, c);
			summa_mult([M, N, K, panelSize], [1.0, beta], 
					A.grid.rowBs, A.grid.colBs, Ad, 
					B.grid.rowBs, B.grid.colBs, Bd,
					Cd);
			//Debug.flushln("Place "+p+" Summa Done");
			val tlist= new Array[Double](2);
			get_usetime(tlist);
			C.distBs(here.id()).calcTime+=tlist(0) as long;
			C.distBs(here.id()).commTime+=tlist(1) as long;
			//Debug.flushln("get comm time"+C.getCommTime());			
		}
		return C;
	}
	
	/**
	 * Perform parallel matrix multiplication with B transposed using SUMMA algorithm.
	 * 
	 * @param ps -- panel size
	 * @param beta -- scaling factor of output matrix C
	 * @param A  -- first operand matrix 
	 * @param B  -- second operand matrix which will be used in transposed 
	 * @param C  -- output matrix
	 * @return  -- output matrix C
	 */
	public static def multTrans(
			ps:Int, beta:Double, 
			A:DistDenseMatrix, 
			B:DistDenseMatrix, 
			C:DistDenseMatrix):DistDenseMatrix(C) {
		//C.summaMult(alpha, beta, A, B);
		val panelSize = Math.min(ps, A.grid.getMinColSize());
		val M:Int = A.M;
		val N:Int = B.N;
		val K:Int = C.N;//K used in iteration is not common part of A and B,
		//K is column of C, which is used in iteration.

		Debug.assure(C.M==A.M&&A.N==B.N&&B.M==C.N, "Matrix mutiply dimension mismatch");

		Debug.assure(A.grid.numRowBlocks==B.grid.numRowBlocks&&
					 A.grid.numColBlocks==B.grid.numColBlocks&&
					 B.grid.numRowBlocks==C.grid.numRowBlocks&&
					 B.grid.numColBlocks==C.grid.numColBlocks, 
					"Matrix partitioning missmatch!");
		
		finish ateach (val [p]:Point in C.dist) {
			//
			val Ad = A.distBs(p).getData();
			val Bd = B.distBs(p).getData();
			val Cd = C.distBs(p).getData();
			//
			val r = C.grid.getRowBlockId(p);
			val c = C.grid.getColBlockId(p);
			//
			setup_grid_partition(p, C.grid.numRowBlocks, C.grid.numColBlocks, r, c);
			summa_mult_T([M, N, K, panelSize], [1.0, beta], 
						 A.grid.rowBs, A.grid.colBs, Ad, 
						 B.grid.rowBs,
						 //B.grid.colBs, B's column partition is same as A's column*/
						 Bd, 
						 //C.grid.rowBs same as A.grid.rowBs
						 C.grid.colBs,  //C's column partition is NOT same as B's column 
						 Cd);
			
			//-----------------------------------
			val tlist= new Array[Double](2);
			get_usetime(tlist);
			C.distBs(here.id()).calcTime+=tlist(0) as long;
			C.distBs(here.id()).commTime+=tlist(1) as long;
		}
		return C;
	}
	//-------------


	public static def mult(
			ps:Int, beta:Double, 
			A:DistDenseMatrix, 
			B:DistDenseMatrix):DistDenseMatrix {
		
		val M = A.M;
		val N = B.N;
		val g = new Grid(M, N, A.grid.rowBs, B.grid.colBs);
		val C:DistDenseMatrix = DistDenseMatrix.make(g);
		SummaMPI.mult(ps, beta, A, B, C);
		return C;
	}

	public static def multTrans(
			ps:Int, beta:Double,
			A:DistDenseMatrix, 
			B:DistDenseMatrix):DistDenseMatrix {
		
		val M = A.M;
		val N = B.M;
		val g = new Grid(M, N, A.grid.rowBs, B.grid.colBs);
		val C:DistDenseMatrix = DistDenseMatrix.make(g);
		SummaMPI.multTrans(ps, beta, A, B, C);
		return C;
	}
}