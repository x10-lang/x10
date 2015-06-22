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

import x10.matrix.DenseMatrix;
import x10.matrix.ElemType;

import x10.matrix.sparse.SparseCSC;
import x10.matrix.block.MatrixBlock;
import x10.matrix.comm.mpi.WrapMPI;
import x10.matrix.distblock.BlockSet;

/**
 * Grid row/column-wise broadcast
 */
protected class BinaryTreeCast  {
    /**
     * Broadcast data from local root block at here to a list of places. 
     * plist does not contains here.id()
     */
    protected static def castToPlaces(
                                      distBS:PlaceLocalHandle[BlockSet], rootbid:Long, datCnt:Long, 
                                      select:(Long,Long)=>Long, 
                                      plist:Rail[Long]){
        
        if (plist.size < 1) return;
        val pcnt   = plist.size;
        val rtcnt  = (pcnt+1) / 2 - 1; 
        val lfcnt  = pcnt - rtcnt - 1;
        val rtroot = plist(lfcnt);
        
        val lfplist = new Rail[Long](lfcnt, (i:Long)=>plist(i));
        val rtplist = new Rail[Long](rtcnt, (i:Long)=>plist(lfcnt+i+1));
        
        finish {
            async {
                copyBlockToRightBranch(distBS, rootbid, rtroot, datCnt, select, rtplist);
            }
            // Perform binary bcast on the left branch
            if (lfcnt > 0) {
                castToPlaces(distBS, rootbid, datCnt, select, lfplist); 
            }
        }
    }
    
    private static def copyBlockToRightBranch(
                                              distBS:PlaceLocalHandle[BlockSet], rootbid:Long, remotepid:Long, datCnt:Long,
                                              select:(Long,Long)=>Long, plist:Rail[Long]):void {
        
        val srcblk = distBS().findFrontBlock(rootbid, select);
        if (srcblk.isDense()) {
            @Ifdef("MPI_COMMU") {
                mpiCopyDenseBlock(distBS, rootbid, srcblk, remotepid, datCnt, select, plist);
            }
            @Ifndef("MPI_COMMU") {
                x10CopyDenseBlock(distBS, rootbid, srcblk, remotepid, datCnt, select, plist);
            }
        } else if (srcblk.isSparse()) {
            @Ifdef("MPI_COMMU") {
                mpiCopySparseBlock(distBS, rootbid, srcblk, remotepid, datCnt, select, plist);
            }
            @Ifndef("MPI_COMMU") {
                x10CopySparseBlock(distBS, rootbid, srcblk, remotepid, datCnt, select, plist);
            }                   
        } else {
            throw new UnsupportedOperationException("Error in block type");
        }
    }
    
    private static def x10CopyDenseBlock(distBS:PlaceLocalHandle[BlockSet], rootbid:Long, 
                                         srcblk:MatrixBlock, rmtpid:Long, datCnt:Long, select:(Long,Long)=>Long, 
                                         plist:Rail[Long]) {
        
        val srcden = srcblk.getMatrix() as DenseMatrix;
        val srcbuf = new GlobalRail[ElemType](srcden.d as Rail[ElemType]{self!=null});
        at(Place(rmtpid)) {
            //Remote capture:distBS, rootbid, datCnt, rtplist
            val blk  = distBS().findFrontBlock(rootbid, select);
            val dstden = blk.getMatrix() as DenseMatrix;
            // Using copyFrom style
            if (datCnt > 0) {
                finish Rail.asyncCopy[ElemType](srcbuf, 0, dstden.d, 0, datCnt);
            }
            // Perform binary bcast on the right branch
            if (plist.size > 0) {
                castToPlaces(distBS, rootbid, datCnt, select, plist);
            }
        }
    }
    
    private static def x10CopySparseBlock(
                                          distBS:PlaceLocalHandle[BlockSet], 
                                          rootbid:Long, srcblk:MatrixBlock, rmtpid:Long, datCnt:Long,
                                          select:(Long,Long)=>Long, plist:Rail[Long]) {
        
        val srcspa = srcblk.getMatrix() as SparseCSC;
        val srcidx = new GlobalRail[Long](srcspa.getIndex() as Rail[Long]{self!=null});
        val srcval = new GlobalRail[ElemType](srcspa.getValue() as Rail[ElemType]{self!=null});
        
        at(Place(rmtpid)) {
            //Remote capture:distBS, rootbid, datCnt, rtplist
            val blk    = distBS().findFrontBlock(rootbid, select);
            val dstspa = blk.getMatrix() as SparseCSC;
            // Using copyFrom style
            dstspa.initRemoteCopyAtDest(datCnt);
            if (datCnt > 0) {
                finish Rail.asyncCopy[Long  ](srcidx, 0L, dstspa.getIndex(), 0L, datCnt);
                finish Rail.asyncCopy[ElemType](srcval, 0L, dstspa.getValue(), 0L, datCnt);
            }
            // Perform binary bcast on the right branch
            if (plist.size > 0) {
                castToPlaces(distBS, rootbid, datCnt, select, plist);
            }
            dstspa.finalizeRemoteCopyAtDest();
        }       
    }   
    
    private static def mpiCopyDenseBlock(distBS:PlaceLocalHandle[BlockSet], 
                                         rootbid:Long, srcblk:MatrixBlock, rmtpid:Long, datCnt:Long, select:(Long,Long)=>Long, 
                                         plist:Rail[Long]):void {
        //Must use ":void", otherwise, @Ifdef("xxx") won't work
        //Need further investiagtion.
        val srcpid = here.id();
        val srcden = srcblk.getMatrix() as DenseMatrix;
        val tag    = rootbid;//RandTool.nextLong(Int.MAX_VALUE);
        
        @Ifdef("MPI_COMMU") 
        {
            //Tag is used to differ different ring cast.
            //Row and column-wise ringcast must NOT be carried out at the same
            //time. This tag only allows ringcast be differed by root block id.
            finish {
                at(Place(rmtpid)) async {
                    //Remote capture:distBS, rootbid, datCnt, rtplist, tag
                    val blk    = distBS().findFrontBlock(rootbid, select);
                    val dstden = blk.getMatrix() as DenseMatrix;
                    WrapMPI.world.recv(dstden.d, 0, datCnt, srcpid, tag);
                    if (plist.size > 0)  {
                        castToPlaces(distBS, rootbid, datCnt, select, plist);
                    }
                }
                WrapMPI.world.send(srcden.d, 0, datCnt, rmtpid, tag);
            }
        }
    }
    
    private static def mpiCopySparseBlock(distBS:PlaceLocalHandle[BlockSet], 
                                          rootbid:Long, srcblk:MatrixBlock, rmtpid:Long, datCnt:Long, select:(Long,Long)=>Long, 
                                          plist:Rail[Long]):void {
        
        val srcpid = here.id();
        val srcspa = srcblk.getMatrix() as SparseCSC;
        val tag = rootbid;//RandTool.nextLong(Int.MAX_VALUE);
        //Tag must allow to differ multiply ringcast.
        
        @Ifdef("MPI_COMMU") 
        {
            finish {
                at(Place(rmtpid)) async {
                    //Remote capture:distBS, rootbid, datCnt, rtplist, tag
                    val blk    = distBS().findFrontBlock(rootbid, select);
                    val dstspa = blk.getMatrix() as SparseCSC;
                    dstspa.initRemoteCopyAtDest(datCnt);
                    WrapMPI.world.recv(dstspa.getIndex(), 0L, datCnt, srcpid, tag);
                    WrapMPI.world.recv(dstspa.getValue(), 0L, datCnt, srcpid, tag+1000000);
                    
                    // Perform binary bcast on the right branch
                    if (plist.size > 0) {
                        castToPlaces(distBS, rootbid, datCnt, select, plist);
                    }
                    dstspa.finalizeRemoteCopyAtDest();
                }
                
                WrapMPI.world.send(srcspa.getIndex(), 0L, datCnt, rmtpid, tag);
                WrapMPI.world.send(srcspa.getValue(), 0L, datCnt, rmtpid, tag+1000000);
            }
        }
    }
}
