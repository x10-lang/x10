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

package x10.matrix.distblock.summa;

import x10.compiler.Ifdef;
import x10.compiler.Ifndef;

import x10.matrix.util.Debug;
import x10.matrix.sparse.SparseCSC;
import x10.matrix.block.MatrixBlock;
import x10.matrix.distblock.BlockSet;

/**
 * Grid row/column-wise broadcast
 */
public class BlockGridCast  {
    // GridCast: row-wise or column-wise 
    public static def rowWise(rid:Long, cid:Long):Long = rid;
    public static def colWise(rid:Long, cid:Long):Long = cid;
    
    /**
     * Sending columns of data in root block at here to front column blocks row-wise, that has same row block id 
     * as root block to all front blocks in specified places
     *
     * @param distBS     distributed block sets in all places
     * @param rootbid    root block id
     * @param colCnt     Number of columns in root block sending out (used in sparse matrix)
     * @param datCnt     number of data to send out
     * @param plst       list of places to receive data (exclude here.id())
     */
    public static def rowCastToPlaces(distBS:PlaceLocalHandle[BlockSet], rootbid:Long, 
				      colCnt:Long, datCnt:Long, plst:Rail[Long]):void {
	@Ifdef("ENABLE_RING_COMM") {
	    ringCastToPlaces(distBS, rootbid, colCnt, datCnt, (r:Long,c:Long)=>r, plst);
	}
	@Ifndef("ENABLE_RING_COMM") {
	    castToPlaces(distBS, rootbid, colCnt, datCnt, (r:Long,c:Long)=>r, plst);
	}
    }
    
    /**
     * Sending rows of data in root block data at here column-wise to front row blocks that has the same column block 
     * id as root block in specified places
     * 
     * @param distBS     distributed block sets in all places
     * @param rootbid    root block id
     * @param colCnt     number of data to send out
     * @param plst       list of places (exclude here.id())
     */     
    public static def colCastToPlaces(distBS:PlaceLocalHandle[BlockSet], rootbid:Long, 
				      colCnt:Long, datCnt:Long, plst:Rail[Long]) {
	@Ifdef("ENABLE_RING_COMM") {
	    ringCastToPlaces(distBS, rootbid, colCnt, datCnt, (r:Long,c:Long)=>c, plst);
	}
	@Ifndef("ENABLE_RING_COMM") {
	    castToPlaces(distBS, rootbid, colCnt, datCnt, (r:Long,c:Long)=>c, plst);
	}
    }
    
    /**
     * Send data of root block to all front blocks in the specified list of places
     * row/column-wise. This method reqires to be at the place of root block.
     * The root place ID (here.id()) is not in the place list. 
     * 
     */
    public static def castToPlaces(distBS:PlaceLocalHandle[BlockSet], rootbid:Long, 
				   colCnt:Long, datCnt:Long, 
				   select:(Long,Long)=>Long, plst:Rail[Long]) {
	// Must start at the place of root block
	if (plst.size > 0) {
	    val rtblk = distBS().findFrontBlock(rootbid, select);
	    if (rtblk.isSparse()) {
		val spa = rtblk.getMatrix() as SparseCSC;
		spa.initRemoteCopyAtSource(0, colCnt);
		//binaryTreeCastTo(distBS, rootbid, datCnt, select, plst);
		BinaryTreeCast.castToPlaces(distBS, rootbid, datCnt, select, plst);
		spa.finalizeRemoteCopyAtSource();
	    } else {
		//binaryTreeCastTo(distBS, rootbid, datCnt, select, plst);
		//Debug.flushln("Binary tree cast to places:"+plst.toString());
		BinaryTreeCast.castToPlaces(distBS, rootbid, datCnt, select, plst);
	    }
	}
    }
    
    public static def ringCastToPlaces(distBS:PlaceLocalHandle[BlockSet], rootbid:Long, 
				       colCnt:Long, datCnt:Long, 
				       select:(Long,Long)=>Long, plst:Rail[Long]) {
	// Must start at the place of root block
	if (plst.size > 0) {
	    val rtblk = distBS().findFrontBlock(rootbid, select);
	    if (rtblk.isSparse()) {
		val spa = rtblk.getMatrix() as SparseCSC;
		spa.initRemoteCopyAtSource(0, colCnt);
		//binaryTreeCastTo(distBS, rootbid, datCnt, select, plst);
		RingCast.castToPlaces(distBS, rootbid, datCnt, select, plst);
		spa.finalizeRemoteCopyAtSource();
	    } else {
		//binaryTreeCastTo(distBS, rootbid, datCnt, select, plst);
		RingCast.castToPlaces(distBS, rootbid, datCnt, select, plst);
	    }
	}
    }
    
    
    public static def verifyCast(klen:Long,
				 chkBS:PlaceLocalHandle[BlockSet],
				 srcblk:MatrixBlock, 
				 var nxtrid:Long, var nxtcid:Long,
				 select:(Long,Long)=>Long, dir:(Long,Long)=>Long):Boolean {
	var retval:Boolean = true;
	
	val grid = chkBS().getGrid();
	val dmap = chkBS().getDistMap();
	while (retval) {
	    //My neighoring block's place
	    nxtrid = select(nxtrid, dir(nxtrid-1, nxtrid+1));
	    nxtcid = select(dir(nxtcid-1, nxtcid+1), nxtcid);
	    
	    if (nxtrid < 0 || nxtrid >= grid.numRowBlocks) break;
	    if (nxtcid < 0 || nxtcid >= grid.numColBlocks) break;
	    val nxtbid = grid.getBlockId(nxtrid, nxtcid);
	    val nxtplc = dmap.findPlace(nxtbid);
	    if (nxtplc == here.id()) continue;
	    
	    val nxtRowId = nxtrid;
	    val nxtColId = nxtcid;
	    retval &= at(Place(nxtplc)) {
		val objblk = chkBS().findFrontBlock(nxtRowId, nxtColId, select);
		val srcbuf = srcblk.getData();
		val objbuf = objblk.getData();
		var eq:Boolean = true;
		for (var i:Long=0; i<klen*srcblk.getMatrix().M&&eq; i++) {
		    eq &= (srcbuf(i)== objbuf(i));
		}
		if (!eq) {
		    Debug.flushln("Check equal failed");
		    Console.OUT.println("Remote source block:\n" + srcblk.getMatrix());
		    Console.OUT.println("check front block:"+nxtRowId+","+nxtColId+" at "+here.id()+"\n" + objblk.getMatrix());
		} else {
		    eq = verifyCast(klen, chkBS, objblk, nxtRowId, nxtColId, select, dir);
                }
		eq
		    };
	}
	return retval;
    }
    
    public static def verifyRowCastEast(klen:Long, chkBS:PlaceLocalHandle[BlockSet], rootblk:MatrixBlock) =
	verifyCast(klen, chkBS, rootblk, rootblk.myRowId, rootblk.myColId, (r:Long,c:Long)=>r, (w:Long,e:Long)=>e);
    
    public static def verifyRowCastWest(klen:Long, chkBS:PlaceLocalHandle[BlockSet], rootblk:MatrixBlock) =
	verifyCast(klen, chkBS, rootblk, rootblk.myRowId, rootblk.myColId, (r:Long,c:Long)=>r, (w:Long,e:Long)=>w);
    
    public static def verifyColCastNorth(klen:Long, chkBS:PlaceLocalHandle[BlockSet], rootblk:MatrixBlock) =
	verifyCast(klen, chkBS, rootblk,  rootblk.myRowId, rootblk.myColId, (r:Long,c:Long)=>c, (n:Long,s:Long)=>n);
    
    public static def verifyColCastSouth(klen:Long, chkBS:PlaceLocalHandle[BlockSet], rootblk:MatrixBlock) =
	verifyCast(klen, chkBS, rootblk,  rootblk.myRowId, rootblk.myColId, (r:Long,c:Long)=>c, (n:Long,s:Long)=>s);
    
}
