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


public class BlockSetRemoteCopy extends BlockRemoteCopy {
	/**
	 * Copy source block to target block in distributed block matrix structure.
	 */
    public static def copySet(distBS:BlocksPLH, srcpid:Long, dstpid:Long):Long {
        if (srcpid == dstpid) return 0;
		
		val dsz = at(Place(srcpid)) {
			var datcnt:Long = 0;
			val grid   = distBS().getGrid();
			val blkitr = distBS().iterator();
			while (blkitr.hasNext()) {
				val blk = blkitr.next();
				val srcmat = blk.getMatrix();
				val dstbid = grid.getBlockId(blk.myRowId, blk.myColId);
				//srcbid is used as dstbid in BlockSet copy
				datcnt += copy(srcmat, 0, distBS, dstpid, dstbid, 0, srcmat.N);
			}
			datcnt
		};
		return dsz;
	}
	
    public static def copySetTo(distBS:BlocksPLH, dstpid:Long):Long {
		if (here.id() ==dstpid) return 0;
		
		var datcnt:Long = 0;
		val grid   = distBS().getGrid();
		val blkitr = distBS().iterator();
		while (blkitr.hasNext()) {
			val blk = blkitr.next();
			val srcmat = blk.getMatrix();
			val dstbid = grid.getBlockId(blk.myRowId, blk.myColId);	
			//srcbid is used as dstbid in BlockSet copy
			datcnt += copy(srcmat, 0, distBS, dstpid, dstbid, 0, srcmat.N );
		}
		return datcnt;		
	}
	
    public static def copySetFrom(distBS:BlocksPLH, srcpid:Long):Long {
		if (here.id() ==srcpid) return 0;
		
		var datcnt:Long = 0;
		val grid   = distBS().getGrid();
		val blkitr = distBS().iterator();
		while (blkitr.hasNext()) {
			val blk = blkitr.next();
			val dstmat = blk.getMatrix();
			val srcbid = grid.getBlockId(blk.myRowId, blk.myColId);
			datcnt += copy(distBS, srcpid, srcbid, 0, dstmat, 0, dstmat.N);
		}
		return datcnt;		
	}	
}
