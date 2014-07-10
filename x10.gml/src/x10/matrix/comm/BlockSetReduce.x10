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

package x10.matrix.comm;

import x10.regionarray.Dist;
import x10.compiler.Ifdef;
import x10.compiler.Ifndef;

import x10.matrix.util.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.comm.mpi.WrapMPI;
import x10.matrix.distblock.BlockSet;

/**
 * The class provides reduce-sum communication for distributed matrix,
 */
public class BlockSetReduce extends BlockSetRemoteCopy {
	/**
	 * Reduce all distributed blocks and store the result of sum of all blocks at specified root block.
	 * All input blocks will be modified, and result will be stored at root block.
	 * 
	 * @param distBS     distributed blocks
	 * @param tmpBS      temporary space to store received blocks. Only one block in each place is required
	 * @param rootbid    root block ID.
	 * 
	 */
	public static def reduceSum(distBS:BlocksPLH, tmpBS:BlocksPLH, rootpid:Long): void {
		@Ifdef("MPI_COMMU") {
			mpiReduceSum(distBS, tmpBS, rootpid);
		}
		@Ifndef("MPI_COMMU") {
			x10Reduce(distBS, tmpBS, rootpid, (a:DenseMatrix, b:DenseMatrix)=>b.cellAdd(a as DenseMatrix(b.M,b.N)));
		}
	}
	
	/**
	 * Perform reduce on all blocks, and store the result at root block.
	 * Input blocks will be modified.  The MPI implementation only has sum operation available.
	 * 
	 * @param distBS        input distributed blocks
	 * @param tmpBS         temporary space to store received blocks. Only one block is required for each place
	 * @param rootbid       root block ID
	 * @param opFunc        Reduce operation function, which take two dense matrix as parameters, the 
	 */
	public static def reduce(distBS:BlocksPLH, tmpBS:BlocksPLH, 
			rootpid:Long,
			opFunc:(DenseMatrix, DenseMatrix)=>DenseMatrix) {
		@Ifdef("MPI_COMMU") {
			mpiReduceSum(distBS, tmpBS, rootpid);
		}
		@Ifndef("MPI_COMMU") {
			x10Reduce(distBS, tmpBS, rootpid, opFunc);
		}
	}
	
	/**
	 * Reduce all dense matrices stored in DistArray from all places
	 * to here
	 *
	 * @param ddmat      Input/Output. Duplicated dense matrix of source and target matrix
	 * @param ddtmp      Temp matrix space to store the receiving data.
	 * @return           Number of elements to received
	 */
	public static def x10Reduce(distBS:BlocksPLH, tmpBS:BlocksPLH, rootpid:Long,
			opFunc:(DenseMatrix, DenseMatrix)=>DenseMatrix) {
		
		val dmap = distBS().getDistMap();
		var leftpcnt:Long = Place.numPlaces();
		
		//Debug.assure(here.id() == 0);
		if (here.id() != rootpid) {
			at(Place(rootpid)) {
				x10Reduce(distBS, tmpBS, rootpid, opFunc);
			}
		} else {
			finish {
				if (rootpid == 0L) 
					reduceToHere(distBS, tmpBS, 0, Place.numPlaces(), opFunc);
				else {
					val lfpcnt = rootpid;
					val rtpcnt = Place.numPlaces() - lfpcnt;
					binaryTreeReduce(distBS, tmpBS, rootpid, rtpcnt, 0, lfpcnt, opFunc);
				}
			}
		}
	}

	private static def binaryTreeReduce(
			distBS:BlocksPLH, tmpBS:BlocksPLH, 
			nearbypid:Long, rootPCnt:Long,
			remotepid:Long, remotePCnt:Long,
			opFunc:(DenseMatrix, DenseMatrix)=>DenseMatrix) {
		var rmtbuflst:Rail[GlobalRail[Double]];
		finish {
			//Left branch reduction
			rmtbuflst =  at(Place(remotepid)) {
				//Remote capture:distBS, tmpBS, lfpcnt;
				async {
					reduceToHere(distBS, tmpBS, here.id(), remotePCnt, opFunc);
				}
				val bl = distBS().blocklist;
				new Rail[GlobalRail[Double]](bl.size(), 
						(i:Long)=>new GlobalRail[Double](bl(i).getData() as Rail[Double]{self!=null}))
			};
			//Right branch reduction
			async {
				reduceToHere(distBS, tmpBS, nearbypid, rootPCnt, opFunc);
			}
		}
		//val dstIt = distBS().iterator();
		//val tmpIt = tmpBS().iterator();
		for (var i:Long=0; i<rmtbuflst.size; i++)  {
			val dstblk = distBS().blocklist.get(i);
			val dstden = dstblk.getMatrix() as DenseMatrix;
			val rcvden = tmpBS().blocklist.get(i).getMatrix() as DenseMatrix;
			val datcnt = dstden.M*dstden.N;
			finish Rail.asyncCopy[Double](rmtbuflst(i), 0L, rcvden.d, 0L, datcnt);
			opFunc(rcvden, dstden);
			
		}
	}


	protected static def reduceToHere(distBS:BlocksPLH, tmpBS:BlocksPLH, 
			rootpid:Long, pcnt:Long, 
			opFunc:(DenseMatrix,DenseMatrix)=>DenseMatrix): void {
		
		val leftRoot = here.id();
		if (pcnt > 1) {
			val leftPCnt  = (pcnt+1) / 2; // make sure left part is larger, if cnt is odd 
			val rightPCnt  = pcnt - leftPCnt;
			val rightRoot  = leftRoot + leftPCnt;
		
			binaryTreeReduce(distBS, tmpBS, rootpid, leftPCnt, rightRoot, rightPCnt, opFunc);
		}
	}
	

	/**
	 * Perform reduce sum of all matrces stored in the duplicated matrix
	 * Result is stored in the matrix at root place.
	 * 
	 * @param ddmat     input and output matrix. 
	 * @param ddtmp     temp matrix storing the inter-place communication data.
	 */
	public static def mpiReduceSum(distBS:BlocksPLH, tmpBS:BlocksPLH, rootpid:Long):void {
		@Ifdef("MPI_COMMU") {
			finish ateach([p] in Dist.makeUnique()) {
				//Remote capture: rootpid, 
				for (var i:Long =0; i< distBS().blocklist.size(); i++) {
					val blk = distBS().blocklist.get(i);
					val dst = blk.getMatrix() as DenseMatrix;
					val src = tmpBS().blocklist.get(i).getMatrix() as DenseMatrix(dst.M,dst.N);
					
					//Copy to src
					dst.copyTo(src);
					val dsz = dst.M*dst.N;
					
					WrapMPI.world.reduceSum(src.d, dst.d, dsz, rootpid);
				}
			}
		}
	}
	
	/**
	 * Reduce all distributed blocks and store the result of sum of all blocks at specified root block.
	 * All input blocks will be modified, and result will be stored at root block.
	 * 
	 * @param distBS     distributed blocks
	 * @param tmpBS      temporary space to store received blocks. Only one block in each place is required
	 * 
	 */
	public static def allReduceSum(distBS:BlocksPLH, tmpBS:BlocksPLH): void {
		@Ifdef("MPI_COMMU") {
			mpiAllReduceSum(distBS, tmpBS);
		}
		@Ifndef("MPI_COMMU") {
			x10AllReduce(distBS, tmpBS, (a:DenseMatrix, b:DenseMatrix)=>b.cellAdd(a as DenseMatrix(b.M,b.N)));
		}
	}
	
	/**
	 * Perform reduce on all blocks, and store the result at root block.
	 * Input blocks will be modified.  The MPI implementation only has sum operation available.
	 * 
	 * @param distBS        input distributed blocks
	 * @param tmpBS         temporary space to store received blocks. Only one block is required for each place
	 * @param rootbid       root block ID
	 * @param opFunc        Reduce operation function, which take two dense matrix as parameters, the 
	 */
	public static def allReduce(distBS:BlocksPLH, tmpBS:BlocksPLH, 
			opFunc:(DenseMatrix, DenseMatrix)=>DenseMatrix) {
		@Ifdef("MPI_COMMU") {
			mpiAllReduceSum(distBS, tmpBS);
		}
		@Ifndef("MPI_COMMU") {
			x10AllReduce(distBS, tmpBS, opFunc);
		}
	}

	public static def x10AllReduce(distBS:BlocksPLH, tmpBS:BlocksPLH,
			opFunc:(DenseMatrix,DenseMatrix)=>DenseMatrix) {
				
		val rtpid = here.id();
		x10Reduce(distBS, tmpBS, rtpid, opFunc);
		BlockSetBcast.bcast(distBS, rtpid);
	}

	/**
	 * Perform all reduce sum operation. 
	 * @see reduceSum()
	 * Result is synchronized for all copies of duplicated matrix
	 * 
	 * @param ddmat     input and output matrix. 
	 * @param ddtmp     temp matrix storing the inter-place communication data.
	 */
	protected static def mpiAllReduceSum(distBS:BlocksPLH, tmpBS:BlocksPLH): void {
		@Ifdef("MPI_COMMU") {
			finish ateach([p] in Dist.makeUnique()) {
				//Remote capture: rootpid, 
				for (var i:Long =0; i< distBS().blocklist.size(); i++) {
					val blk = distBS().blocklist.get(i);
					val dst = blk.getMatrix() as DenseMatrix;
					val src = tmpBS().blocklist.get(i).getMatrix() as DenseMatrix(dst.M,dst.N);
					
					//Copy to src
					dst.copyTo(src);
					val dsz = dst.M*dst.N;
					
					WrapMPI.world.allReduceSum(src.d, dst.d, dsz);
				}
			}
		}
	}

	/**
	 * Create temporary space used in reduce for storing received data
	 */
	public static def makeTempDistBlockMatrix(m:Long, n:Long):BlocksPLH =
		PlaceLocalHandle.make[BlockSet](Place.places(), 
				()=>BlockSet.makeDense(m*Place.numPlaces(), n, Place.numPlaces(), 1, Place.numPlaces(),1));
}

