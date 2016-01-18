/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2016.
 */

package x10.matrix.distblock.summa;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.ElemType;

import x10.matrix.sparse.SparseCSC;
import x10.matrix.distblock.BlockSet;

/**
 * Grid row/column-wise broadcast
 */
protected class RingCast  {
    protected static def castToPlaces(distBS:PlaceLocalHandle[BlockSet], rootbid:Long, datCnt:Long, select:(Long,Long)=>Long,
				      plist:Rail[Long]):void {
	if (plist.size < 1) return;
	val srcblk = distBS().findFrontBlock(rootbid, select);
	if (srcblk.isDense()) {
	    x10RingCastDense(distBS, rootbid, datCnt, select, plist);
	} else if (srcblk.isSparse()) {
	    x10RingCastSparse(distBS, rootbid, datCnt, select, plist);
	} else {
	    throw new UnsupportedOperationException("Error in block type");
	}               
    }
    
    // X10 DistRail.copy implementation of ring-cast
    
    private static def x10RingCastDense(distBS:PlaceLocalHandle[BlockSet], 
					rootbid:Long, datCnt:Long, select:(Long,Long)=>Long,
					plist:Rail[Long]):void {
	
	//Check place list 
	val srcblk = distBS().findFrontBlock(rootbid, select);
	val srcden = srcblk.getMatrix() as DenseMatrix; 
	val srcbuf = new GlobalRail[ElemType](srcden.d as Rail[ElemType]{self!=null});
	
	val nxtpid = plist(0);
	val nplist = new Rail[Long](plist.size-1, (i:Long)=>plist(i+1));
        at(Place(nxtpid)) {
	    //Implicit capture: rmtbuf, dmlist, datasz, nplist, root
	    copyDenseToHere(srcbuf, distBS, rootbid, datCnt, select, nplist);
	}
        
    }
    
    private static def copyDenseToHere(rmtbuf:GlobalRail[ElemType], distBS:PlaceLocalHandle[BlockSet], 
				       rootbid:Long, datCnt:Long, select:(Long,Long)=>Long, plist:Rail[Long]) {
        
	val rcvblk = distBS().findFrontBlock(rootbid, select);
	val rcvden = rcvblk.getMatrix() as DenseMatrix;
	finish Rail.asyncCopy[ElemType](rmtbuf, 0, rcvden.d, 0, datCnt);
	
	if (plist.size > 0) {
	    val nxtpid = plist(0); // Get next place id in the list
	    val nplist = new Rail[Long](plist.size-1, (i:Long)=>plist(i+1));
	    val srcbuf = new GlobalRail[ElemType](rcvden.d as Rail[ElemType]{self!=null});
	    
            at(Place(nxtpid)) {
		//Need: srcbuf, distBS, ootbid, datCnt, nplist
		copyDenseToHere(srcbuf, distBS, rootbid, datCnt, select, nplist);
	    }
	}
    }
    
    private static def x10RingCastSparse(distBS:PlaceLocalHandle[BlockSet], rootbid:Long, datCnt:Long, select:(Long,Long)=>Long,
					 plist:Rail[Long]) {
        
	//Check place list 
	val srcblk = distBS().findFrontBlock(rootbid, select);
	val srcmat = srcblk.getMatrix() as SparseCSC;
	val srcidx = new GlobalRail[Long](srcblk.getIndex() as Rail[Long]{self!=null});
	val srcdat = new GlobalRail[ElemType](srcblk.getData() as Rail[ElemType]{self!=null});
	//Remove root from place list
        
	val nxtpid = plist(0);
	val nplist = new Rail[Long](plist.size-1, (i:Long)=>plist(i+1));
        at(Place(nxtpid)) {
	    //Implicit capture: srcidx, srcdat, distBS, rootbid, datCnt, nplist
	    copySparseToHere(srcidx, srcdat, distBS, rootbid, datCnt, select, nplist);
	}
    }
    
    private static def copySparseToHere(rmtidx:GlobalRail[Long], rmtdat:GlobalRail[ElemType],
					distBS:PlaceLocalHandle[BlockSet], rootbid:Long, datCnt:Long, select:(Long,Long)=>Long,
					plist:Rail[Long]) {
        
	val rcvblk = distBS().findFrontBlock(rootbid, select);
	val spa = rcvblk.getMatrix() as SparseCSC;
        
	spa.initRemoteCopyAtDest(datCnt);
	if (datCnt > 0) {
	    finish Rail.asyncCopy[Long](rmtidx, 0L,   spa.getIndex(), 0L, datCnt);
	    finish Rail.asyncCopy[ElemType](rmtdat, 0L, spa.getValue(), 0L, datCnt);
	}
        
	if (plist.size > 0) {
	    val nxtpid = plist(0); // Get next place id in the list
	    val nplist = new Rail[Long](plist.size-1, (i:Long)=>plist(i+1));
	    val srcidx = new GlobalRail[Long](rcvblk.getIndex() as Rail[Long]{self!=null});
	    val srcdat = new GlobalRail[ElemType](rcvblk.getData() as Rail[ElemType]{self!=null});
	    
            at(Place(nxtpid)) {
		//Need: srcidx, srcdat, distBS, rootbid, datCnt, nplist
		copySparseToHere(srcidx, srcdat, distBS, rootbid, datCnt, select, nplist);
	    }
	}
	spa.finalizeRemoteCopyAtDest();
    }
}
