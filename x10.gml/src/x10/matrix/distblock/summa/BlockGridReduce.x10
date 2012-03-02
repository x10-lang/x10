/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2012.
 */

package x10.matrix.distblock.summa;

import x10.io.Console;
import x10.util.Timer;
import x10.util.ArrayList;

import x10.compiler.Ifdef;
import x10.compiler.Ifndef;
import x10.compiler.Uninitialized;

import x10.matrix.Debug;
import x10.matrix.RandTool;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.sparse.SparseCSC;
import x10.matrix.block.MatrixBlock;

import x10.matrix.comm.WrapMPI;

import x10.matrix.distblock.CastPlaceMap;
import x10.matrix.distblock.BlockSet;


/**
 * Grid row/column wise reduce
 *.
 */
public class BlockGridReduce {

	//==================================================
	// RingCast: receive form previous one and send to one next in a ring 
	//==================================================
	public static def rowWise(rid:Int, cid:Int):Int = rid;
	public static def colWise(rid:Int, cid:Int):Int = cid;
	
	
	/**
	 * Row-wise reduction-sum of front blocks
	 */
	public static def rowReduceSum(
			distBS:PlaceLocalHandle[BlockSet], 
			tmpBS:PlaceLocalHandle[BlockSet], 
			rootbid:Int, 
			datCnt:Int,
			plst:Array[Int](1)):void {
		reduceToRoot(distBS, tmpBS, rootbid, datCnt, 
				(rid:Int,cid:Int)=>rid, 
				(src:DenseMatrix,dst:DenseMatrix, dcnt:Int)=>sum(src, dst, dcnt), plst);
	}
	
	/**
	 * Column-wise reduction-sum of font blocks
	 */
	public static def colReduceSum(
			distBS:PlaceLocalHandle[BlockSet], 
			tmpBS:PlaceLocalHandle[BlockSet], 
			rootbid:Int, 
			datCnt:Int,
			plst:Array[Int](1)):void {
		reduceToRoot(distBS, tmpBS, rootbid, datCnt, 
				(rid:Int,cid:Int)=>cid, 
				(src:DenseMatrix,dst:DenseMatrix, dcnt:Int)=>sum(src, dst, dcnt), plst);
	}
	
	//------------------------------------------------------------------------
	public static def sum(src:DenseMatrix, dst:DenseMatrix, datCnt:Int):DenseMatrix(dst) {
		Debug.assure(src.M==dst.M, 
			"Leading dimensions of destination and source are not same"); 
		for (var idx:Int=0; idx < datCnt; idx++)
			dst.d(idx) += src.d(idx);
		return dst;		
	}

	//=========================================================
	//=========================================================
	/**
	 * 
	 */
	protected static def reduceToRoot(
			distBS:PlaceLocalHandle[BlockSet], 
			tmpBS:PlaceLocalHandle[BlockSet], 
			rootbid:Int, 
			datCnt:Int, 
			select:(Int, Int)=>Int,
			opFunc:(DenseMatrix, DenseMatrix, Int)=>DenseMatrix,
			plst:Array[Int](1) ) {
		
		//find rootpid in the place list, if not, assume root is first one
		val rootpid = distBS().findPlace(rootbid);
		var i:Int=0;
		for (; i<plst.size && plst(i)!=rootpid; i++);
		if (i>=plst.size) {
			//Did not find root in place list
			Debug.exit("Cannot find root pid "+rootpid+" in place list "+plst.toString()+" in reduce");
		}
		//remove root place from list
		val rootidx=i;
		val newplst = new Array[Int](plst.size-1, (j:Int)=>j<rootidx?plst(j):plst(j+1));
		at (Dist.makeUnique()(rootpid)) {
			//Debug.flushln("Root place:"+here.id()+" place list:"+newplst.toString());
			splitReduceToHere(distBS, tmpBS, rootbid, datCnt, select, opFunc, newplst);
		}
	}
	
	
	private static def splitReduceToHere(
			distBS:PlaceLocalHandle[BlockSet], 
			tmpBS:PlaceLocalHandle[BlockSet], 
			rootbid:Int, 
			datCnt:Int, 
			select:(Int, Int)=>Int,
			opFunc:(DenseMatrix, DenseMatrix, Int)=>DenseMatrix,
			plst:Array[Int](1)
			):void {
		if (datCnt ==0) return;
		if (plst.size > 0) {
			val pcnt      = plst.size-1;   //remove left branch root from place counter
			val leftRoot  = plst(0);
			val leftCnt   = (pcnt+1) / 2;  //divide all places into two parts, left is larger
			val rightCnt  = pcnt - leftCnt;
			
			val leftlst  = new Array[Int](leftCnt,  (i:Int)=>plst(i+1));
			val rightlst = new Array[Int](rightCnt, (i:Int)=>plst(leftCnt+i+1));
			@Ifdef("MPI_COMMU") {
				//Debug.flushln("call mpiBinaryTreeReduce");
				mpiBinaryTreeReduce(distBS, tmpBS, rootbid, datCnt, select, opFunc, leftRoot, leftlst, rightlst);
			}
			@Ifndef("MPI_COMMU") {
				x10BinaryTreeReduce(distBS, tmpBS, rootbid, datCnt, select, opFunc, leftRoot, leftlst, rightlst);
			}
		} 
	}
		
	
	//-----------------------------
	private static def x10BinaryTreeReduce(
			distBS:PlaceLocalHandle[BlockSet], 
			tmpBS:PlaceLocalHandle[BlockSet], 
			rootbid:Int, 
			datCnt:Int,
			select:(Int,Int)=>Int,	
			opFunc:(DenseMatrix, DenseMatrix, Int)=>DenseMatrix,
			leftRoot:Int, leftPlist:Array[Int](1), 
			rightPlist:Array[Int](1)):void {
	
		var rmtbuf:RemoteArray[Double]=null;
		//Debug.flushln("Left root:"+leftRoot+" Left list"+leftPlist.toString()+" right:"+rightPlist.toString());
		
		finish {
			//Left branch reduction
			async {
				// Need to bring data from remote place
				rmtbuf =  at (Dist.makeUnique()(leftRoot)) {
					//Remote capture:distBS, tmpBS, colCnt, rightPlist
					val rmtblk = distBS().findFrontBlock(rootbid, select);
					//Debug.flushln("Visiting block("+rmtblk.myRowId+","+rmtblk.myColId+")");

					if (leftPlist.size > 0)  {
						//Only if there are more places to visit than remote pid
						splitReduceToHere(distBS, tmpBS, rootbid, datCnt, select, opFunc, leftPlist);
					}
					new RemoteArray[Double](rmtblk.getData() as Array[Double]{self!=null})
				};
			}
			//Right branch reduction
			if (rightPlist.size > 0)	async {
				splitReduceToHere(distBS, tmpBS, rootbid, datCnt, select, opFunc, rightPlist);
			}
		}
		
		val dstblk = distBS().findFrontBlock(rootbid, select);
		val rcvblk = tmpBS().findFrontBlock(rootbid, select);
		val rcvden = rcvblk.getMatrix() as DenseMatrix;
		val dstden = dstblk.getMatrix() as DenseMatrix;

		finish Array.asyncCopy[Double](rmtbuf, 0, rcvden.d, 0, datCnt);
		//Debug.flushln("Perform reduction with data from place "+leftRoot);
		opFunc(rcvden, dstden, datCnt);
	}

	private static def mpiBinaryTreeReduce(
			distBS:PlaceLocalHandle[BlockSet], 
			tmpBS:PlaceLocalHandle[BlockSet], 
			rootbid:Int, 
			datCnt:Int,
			select:(Int,Int)=>Int,
			opFunc:(DenseMatrix, DenseMatrix, Int)=>DenseMatrix, 
			leftRoot:Int, leftPlist:Array[Int](1), 
			rightPlist:Array[Int](1)):void  {
				
		@Ifdef("MPI_COMMU") {
			val dstpid = here.id();
			val rcvblk = tmpBS().findFrontBlock(rootbid, select);
			val rcvden = rcvblk.getMatrix() as DenseMatrix;
			finish {
				//Left branch reduction
				//Debug.flushln("Goto leftroot:"+leftRoot);
				at (Dist.makeUnique()(leftRoot)) async {
					//Debug.flushln("Start left branch");
					//Remote capture:distBS, tmpBS, colCnt, remotePlcList
					if (leftPlist.size > 0)  {
						splitReduceToHere(distBS, tmpBS, rootbid, datCnt, select, opFunc, leftPlist);
					}
					val rmtblk = distBS().findFrontBlock(rootbid, select);
					val rmtden = rmtblk.getMatrix() as DenseMatrix;
					val tag    = 10000+rootbid;//baseTagCopyTo + here.id();
					
					//Debug.flushln("Start sending data to "+dstpid);
					WrapMPI.world.send(rmtblk.getData(), 0, datCnt, dstpid, tag);
					//Debug.flushln("Done sending data to "+dstpid);
				}
		
				//left branch reduction
				if (rightPlist.size > 0) async {
					//Debug.flushln("Start right branch");
					splitReduceToHere(distBS, tmpBS, rootbid, datCnt, select, opFunc, rightPlist);
				}
			
				async {
					val tag    = 10000+rootbid;//baseTagCopyTo + remotepid;
					//Debug.flushln("Ready to receive data from "+leftRoot+" tag:"+tag);
					WrapMPI.world.recv(rcvden.d, 0, datCnt, leftRoot, tag);
					//Debug.flushln("Data recieved from "+leftRoot+" tag:"+tag);
				}
			}
			val dstblk = distBS().findFrontBlock(rootbid, select);
			val dstden = dstblk.getMatrix() as DenseMatrix;
			opFunc(rcvden, dstden, datCnt);
		}
	}
}