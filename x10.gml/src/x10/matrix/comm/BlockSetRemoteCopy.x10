package x10.matrix.comm;

import x10.io.Console;
import x10.util.Timer;
import x10.util.Pair;

import x10.compiler.Ifdef;
import x10.compiler.Ifndef;
import x10.compiler.Uninitialized;

import x10.matrix.Debug;
import x10.matrix.Matrix;

import x10.matrix.block.Grid;
import x10.matrix.block.MatrixBlock;
import x10.matrix.distblock.BlockSet;
import x10.matrix.distblock.DistGrid;


/**
 */
public class BlockSetRemoteCopy extends BlockRemoteCopy {

	//-----------------------------------------
	/**
	 * Copy source block to target block in distributed block matrix structure.
	 */
	public static def copySet(distBS:BlocksPLH, srcpid:Int, dstpid:Int):Int {
		if (srcpid ==dstpid) return 0;
		
		val dsz = at (Dist.makeUnique()(srcpid)) {
			var datcnt:Int = 0;
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
	
	public static def copySetTo(distBS:BlocksPLH, dstpid:Int):Int {
		if (here.id() ==dstpid) return 0;
		
		var datcnt:Int = 0;
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
	
	public static def copySetFrom(distBS:BlocksPLH, srcpid:Int):Int {
		if (here.id() ==srcpid) return 0;
		
		var datcnt:Int = 0;
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