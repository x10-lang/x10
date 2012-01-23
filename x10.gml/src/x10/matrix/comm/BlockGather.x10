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

package x10.matrix.comm;

import x10.io.Console;
import x10.util.Timer;
import x10.util.Pair;

import x10.compiler.Ifdef;
import x10.compiler.Ifndef;
import x10.compiler.Uninitialized;

import x10.matrix.Debug;
//import x10.matrix.comm.mpi.UtilMPI;

import x10.matrix.Matrix;

import x10.matrix.block.Grid;
import x10.matrix.block.BlockMatrix;
import x10.matrix.block.MatrixBlock;

import x10.matrix.distblock.DistBlockMatrix;

/**
 * This class provide gather communication for distributed block matrix.
 * There is no MPI collective used. All are based on p2p communication.
 */
public class BlockGather extends BlockRemoteCopy {

	//==============================================
	// Constructor
	//==============================================
	public def this() {
		super();
	}
	//==============================================


	/**
	 * Gather single-row partitioning blocks or single column block matrix from all places to a 
	 * dense matrix at here
	 */
	public static def gather(src:DistBlockMatrix, dst:Matrix{self.M==src.M,self.N==src.N}) : void {

		val srcgrid = src.getGrid();
		if (dst instanceof BlockMatrix) {
			val dstbm = dst as BlockMatrix;
			Debug.assure(srcgrid.equals(dstbm.grid),
					"source and destionation matrix partitions are not compatible");
			gather(src.handleBS, dstbm.listBs);
		} else if (dst.N == 1) {
			gatherVector(src.handleBS, dst as Matrix{self.N==1});
		} else if (srcgrid.numRowBlocks==1) {
			gatherRowBs(src.handleBS, dst);
		}
		
		Debug.exit("Source and destination matrics are not supported in gather");
	}
	
	/**
	 * Gather blocks from distributed BlockSet in all places to block matrix
	 * at here.
	 * 
	 * @param src     source distributed matrix blocks
	 * @param dst     target matrix block array 
	 */
	public static def gather(src:BlocksPLH, dst:Array[MatrixBlock](1)) : void {
		
		val srcg   = src().getGrid();
		val srcmap = src().getDistMap();

		Debug.assure(dst.size >= srcg.size,
				"Not enough blocks at receiving side"); 
		
		finish for (var bid:Int=0; bid<srcg.size; bid++) {
			BlockRemoteCopy.copy(src, bid, dst(bid));
		}
	}
	
	/**
	 * Copy distributed dense matrix blocks from all places to the dense matrix
	 * at here.
	 * 
	 * @param gp     single row block partitioning
	 * @param src     source matrix, distributed in all places.
	 * @param dstden     target dense matrix at here
	 */
	protected static def gatherRowBs(src:BlocksPLH, dst:Matrix): void {

		val gp = src().getGrid();
		var coloff:Int=0;
		Debug.assure(gp.numRowBlocks==1, "Cannot perform non-single row blocks gather");
		
		for (var cb:Int=0; cb<gp.numColBlocks; cb++) {

			val colcnt = gp.colBs(cb);
			
			BlockRemoteCopy.copy(src, cb, 0, dst, coloff, colcnt); 
			coloff += colcnt;
		}
	}
	
	protected static def gatherVector(src:BlocksPLH, dst:Matrix{self.N==1}): void {

		val gp = src().getGrid();
		var rowoff:Int=0;
		for (var rb:Int=0; rb<gp.numRowBlocks; rb++) {

			val rowcnt = gp.rowBs(rb);
			
			BlockRemoteCopy.copy(src, rb, 0, dst, rowoff, rowcnt); 

			rowoff += rowcnt;
		}
	}


}
