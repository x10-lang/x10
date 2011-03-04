/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2010.
 *
 */

import x10.io.Console;
import x10.util.Timer;
import x10.util.Team;

public class SummaTeam { 
// This is X10-Team SUMMA implementation, matrix multiply is implemented in X10 too
// Compute: C = alaph* A * B + beta*C

	//
	val K:Int; //the number of columns in A, or the number of rows in B
	//
	val panelSize :Int;
	//
	val rowC_Block:Int; // Number of row blocks in C
	val colC_Block:Int; // Number of column blocks in C
	//
	val rowC_Bs:Array[Int](1); // Row block sizes in C
	val colC_Bs:Array[Int](1); // Column block sizes in C
	val colA_Bs:Array[Int](1); // Column block sizes in A
	//
	val rowTeam:Array[Team](1); //Collective communicator among places in the same block row
	val colTeam:Array[Team](1); //Collective communicator among places in the same block colomn
	//
	
	//------------------------------------------------

	//=====================================================================
	// Constructor
	//------------
	public def this(ps:Int,                /* Size of panel */
					rA_Bs:Array[Int](1),   /* Row block size of input matrix A */
					cA_Bs:Array[Int](1),   /* Column block size of input matrix A */
					cB_Bs:Array[Int](1))   /* Column block size of input matrix B */ {
		panelSize = ps;
		//
		rowC_Bs = rA_Bs; colC_Bs = cB_Bs; colA_Bs = cA_Bs;
		//
		rowC_Block = rowC_Bs.size();	
		colC_Block = colC_Bs.size();
		//
		rowTeam = new Array[Team](rowC_Block);
		colTeam = new Array[Team](colC_Block);
		//
		// Creating collective communicator for places in the same block row
		for (var r:Int=0; r<rowC_Block; r++) {
			val rid = r;
			val p = new Array[Place](colC_Block, 
									 (i:Int)=>Place.place(i*rowC_Block+rid));
			rowTeam(r) = Team(p);
		}
		//
		// Creating collective communicator for places in the same block column
		for (var c:Int=0; c<colC_Block; c++) {
			val cid = c;
			val p = new Array[Place](rowC_Block, 
									 (i:Int)=>Place.place(cid*rowC_Block+i));
			colTeam(c) = Team(p);
		}
		// Compute the number of column in A (= number of rows in B)
		var k:Int=0;
		for (var c:Int=0; c<colA_Bs.size(); c++)
			k += colA_Bs(c);
		K = k;
	}

	//=====================================================================
	// This function performs SUMMA multiply in all places
	//             C = alpha * A * B + beta * C 
	//
	public def mult(alpha:Double,                      /* Scaling factor for the inputs*/
					A:DistArray[Array[Double](1)](1),  /* Input of distributed matrix */
					B:DistArray[Array[Double](1)](1),  /* Input of distributed matrix */
					beta:Double,                       /* Scaling factor for the output*/
					C:DistArray[Array[Double](1)](1)   /* Output of matrix multiply */ ) {

		finish ateach (val [p]:Point in C.dist()) {
			// On each place, start the parallel matrix block multiply
			parallelMult(p, alpha, A(p), B(p), beta, C(p));
		}

	}
	//=====================================================================
	// Concurrent matrix block multiply based on SUMMA algorithm 
	// 
	public def parallelMult(pid:Int,            /* Place (process) id of current block */
							alpha:Double,       /* Scaling factor for the input blocks */
							bA:Array[Double](1),/* Input matrix block */
							bB:Array[Double](1),/* Input matrix block */
							beta:Double,        /* Scaling factor for the output block */
							bC:Array[Double](1) /* Output matrix block */ ) {

		//------------------------
		var itRow:Int = 0;
		var itCol:Int = 0; //Current processing iteration
		//
		var iwrk:Int = 0;
		var ii:Int = 0;
		var jj:Int = 0;
		//
		val myColId = pid / rowC_Block;
		val myRowId = pid % rowC_Block;
		//
		val bC_M = rowC_Bs(myRowId); //Number of rows in output matrix block C
		val bC_N = colC_Bs(myColId); //Number of columns in output matrix block C
		//
		val work1 = new Array[Double](bC_M*panelSize);
		val work2 = new Array[Double](panelSize*bC_N);
		//

		if (x10.lang.Math.abs(beta) < 0.00000001D) {
			for (var i:Int=0; i<bC_M*bC_N; i++) 
				bC(i) = 0.0;
		} else {
			for (var i:Int=0; i<bC_M*bC_N; i++) 
				bC(i) *= beta;
		}

		for (var kk:Int=0; kk<K; kk+=iwrk) {
			// Compute the size of actual panel size used for this iteration
			iwrk = (panelSize<(rowC_Bs(itRow)-ii)) ? panelSize : (rowC_Bs(itRow)-ii);
			iwrk = (iwrk<colC_Bs(itCol)-jj)        ? iwrk      : (colC_Bs(itCol)-jj); 

			// Creating work place for row-wise bcast in "bC_M x iwrk" matrix 
			if (myColId == itCol) {
				var src_idx:Int = jj * bC_M;
				var dst_idx:Int = 0;
				for (var i:Int=0; i<iwrk; i++)
					for (var j:Int=0; j<bC_M; j++) {
						work1(dst_idx++) = bA(src_idx++);
					}
			}
			// Creating work place for column-wise bcast in "iwrk x bC_N" matrix
			if (myRowId == itRow) {
				var src_idx:Int = ii;
				var dst_idx:Int = 0;
				for (var i:Int=0; i<bC_N; i++) {
					src_idx = i * bC_M + ii; // set the offset
					for (var j:Int=0; j<iwrk; j++) 
						work2(dst_idx++) = bB(src_idx++);
				}
			}
			//------------------------------------------------
			// Broadcast in row-wise
			// At myRowId-th row, broadcast work1 (offset 0) from root (myColId, itCol)
			rowTeam(myRowId).bcast[Double](myColId, itCol, work1, 0, work1, 0, bC_M*iwrk);
			//------------------------------------------------
			// Broacast in column-wise
			// At myColId-th column, broadcast work2 (offset 0) from root (itRow, myRowId)
			colTeam(myColId).bcast[Double](myRowId, itRow, work2, 0, work2, 0, iwrk*bC_N);
			//
			//------------------------------------------------
			// Matrix block multiply
			x10mult(bC_M, bC_N, iwrk, alpha, work1, work2, 1.0, bC);
			
			//-----------------------------------------------
			// Matrix block multiply
			// MatrixBlas.matrixT_matrixT_mult(work1, work2, bC, 
			//								[bC_M, bC_N, iwrk],
			//								[alpha, 1.0], 
			//								[0, 0]);
			//------------------------------------------------

			/* update icurcol, icurrow, ii, jj */
			ii += iwrk;
			jj += iwrk;
			if ( jj>=colC_Bs(itCol)) { itCol++; jj = 0; };
			if ( ii>=rowC_Bs(itRow)) { itRow++; ii = 0; };

		}
	}
	
	public def x10mult(M:Int, N:Int, K:Int, 
					   alpha:Double, mA:Array[Double](1), mB:Array[Double](1), 
					   beta:Double,  mC:Array[Double](1)) {

		for (var r:Int=0; r<M; r++) {
			var idx_2:Int = 0;
			for (var c:Int=0; c<N; c++) {

				var v:Double=0.0;
				var idx_1:Int=r;

				for (var k:Int=0; k<K; k++) {
					v     += mA(idx_1) * mB(idx_2);
					idx_1 += M;
					idx_2 += 1;
				}
				mC(c*M+r) =  mC(c*M+r) * beta + v * alpha;
			}
		}
	}
}