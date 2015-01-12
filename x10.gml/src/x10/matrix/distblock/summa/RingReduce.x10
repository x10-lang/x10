/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.matrix.distblock.summa;

import x10.compiler.Ifdef;
import x10.compiler.Ifndef;

import x10.matrix.util.Debug;
import x10.matrix.DenseMatrix;
import x10.matrix.block.MatrixBlock;
import x10.matrix.comm.mpi.WrapMPI;
import x10.matrix.distblock.BlockSet;

/**
 * Grid row/column wise reduce
 */
protected class RingReduce {
	protected static def reduceToHere(distBS:PlaceLocalHandle[BlockSet], tmpBS:PlaceLocalHandle[BlockSet], 
			rootbid:Long, dstblk:MatrixBlock, datCnt:Long, select:(Long,Long)=>Long, opFunc:(DenseMatrix,DenseMatrix,Long)=>DenseMatrix,
			plist:Rail[Long] ):void {
		if (plist.size < 1 ) return;
		@Ifdef("MPI_COMMU") 
		{
			//Debug.flushln("call mpiBinaryTreeReduce");
			mpiReduceToHere(distBS, tmpBS, rootbid, dstblk, datCnt, select, opFunc, plist);
		}
		@Ifndef("MPI_COMMU") {
			x10ReduceToHere(distBS, tmpBS, rootbid, dstblk, datCnt, select, opFunc, plist);
		}
	}
	
	private static def x10ReduceToHere(
			distBS:PlaceLocalHandle[BlockSet], tmpBS:PlaceLocalHandle[BlockSet],
			rootbid:Long, dstblk:MatrixBlock, datCnt:Long,
			select:(Long,Long)=>Long, opFunc:(DenseMatrix,DenseMatrix,Long)=>DenseMatrix,
			plist:Rail[Long]) {
		
		//Debug.flushln("Left root:"+leftRoot+" Left list"+leftPlist.toString()+" right:"+rightPlist.toString());
		val prepid = plist(plist.size-1);
		val nplist = new Rail[Long](plist.size-1, (i:Long)=>plist(i));
		
		// Need to bring data from remote place
		val rmtbuf =  at(Place(prepid)) {
			//Remote capture:distBS, tmpBS, rootbid, datCnt, nplist
			val datblk = distBS().findFrontBlock(rootbid, select);
			//Debug.flushln("Visiting block("+rmtblk.myRowId+","+rmtblk.myColId+")");
			if (nplist.size > 0)
				x10ReduceToHere(distBS, tmpBS, rootbid, datblk, datCnt, select, opFunc, nplist);
			new GlobalRail[Double](datblk.getData() as Rail[Double]{self!=null})
		};
	
		val rcvblk = tmpBS().findFrontBlock(rootbid, select);
		val rcvden = rcvblk.getMatrix() as DenseMatrix;
		val dstden = dstblk.getMatrix() as DenseMatrix;
		finish Rail.asyncCopy[Double](rmtbuf, 0, rcvden.d, 0, datCnt);
		//Debug.flushln("Perform reduction with data from place "+leftRoot);
		opFunc(rcvden, dstden, datCnt);
	}
			
	private static def mpiReduceToHere(distBS:PlaceLocalHandle[BlockSet], tmpBS:PlaceLocalHandle[BlockSet], 
			rootbid:Long, dstblk:MatrixBlock, datCnt:Long, 
			select:(Long,Long)=>Long, opFunc:(DenseMatrix,DenseMatrix,Long)=>DenseMatrix, 
			plist:Rail[Long]):void  {
		
		@Ifdef("MPI_COMMU") 
		{
			val plsz = plist.size;
			val root = here.id();
			finish {
				for (var p:Long=0; p < plsz; p++) {
					val nxtpid = (p==plsz-1)?root:plist(p+1); //Implicitly carry to next place
					val prepid = (p==0L)?root:plist(p-1); //Implicitly carry to next place
					val curpid = plist(p);

					at(Place(curpid)) async {
						//Remote capture: nxtpid, prepid, distBS, tmpBS, datCnt
						val sndblk = distBS().findFrontBlock(rootbid, select);
						val sndden = sndblk.getMatrix() as DenseMatrix;
						val dtag = rootbid;
						if (prepid != root) {
							val rcvblk = tmpBS().findFrontBlock(rootbid, select);
							val rcvden = rcvblk.getMatrix() as DenseMatrix;
							//receive data from pre-place
							WrapMPI.world.recv(rcvden.d, 0, datCnt, prepid, dtag);
							opFunc(rcvden, sndden, datCnt);
						}
						WrapMPI.world.send(sndden.d, 0, datCnt, nxtpid, dtag);
					}
				}
				async {
					val rcvblk = tmpBS().findFrontBlock(rootbid, select);
					val rcvden = rcvblk.getMatrix() as DenseMatrix;
					val prepid = plist(plsz-1);
					val dtag   = rootbid;
					//Debug.flushln("Start sending data to "+dstpid);
					WrapMPI.world.recv(rcvden.d, 0, datCnt, prepid, dtag);
					//Debug.flushln("Done sending data to "+dstpid);
					val dstden = dstblk.getMatrix() as DenseMatrix;
					opFunc(rcvden, dstden, datCnt);
				}
			}
		}
	}
}
