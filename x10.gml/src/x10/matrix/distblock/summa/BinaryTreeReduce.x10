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

import x10.matrix.util.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.ElemType;

import x10.matrix.block.MatrixBlock;
import x10.matrix.distblock.BlockSet;

/**
 * Grid row/column wise reduce
 */
protected class BinaryTreeReduce {
    protected static def reduceToHere(distBS:PlaceLocalHandle[BlockSet], tmpBS:PlaceLocalHandle[BlockSet], 
                                      rootbid:Long, dstblk:MatrixBlock, datCnt:Long, 
                                      select:(Long,Long)=>Long, opFunc:(DenseMatrix,DenseMatrix,Long)=>DenseMatrix, 
                                      plist:Rail[Long]):void {
        
        if (datCnt == 0L) return;
        if (plist.size < 1) return;
        val pcnt   = plist.size;
        val rtcnt  = (pcnt+1) / 2 - 1; 
        val lfcnt  = pcnt - rtcnt - 1;
        val rtroot = plist(lfcnt);
        
        val lfplist = new Rail[Long](lfcnt, (i:Long)=>plist(i));
        val rtplist = new Rail[Long](rtcnt, (i:Long)=>plist((lfcnt+i+1)));
        
        x10BinaryTreeReduce(distBS, tmpBS, rootbid, dstblk, datCnt, select, opFunc, lfplist, rtroot, rtplist);
    }
    
    private static def x10BinaryTreeReduce(
                                           distBS:PlaceLocalHandle[BlockSet], tmpBS:PlaceLocalHandle[BlockSet], 
                                           rootbid:Long, dstblk:MatrixBlock, datCnt:Long,
                                           select:(Long,Long)=>Long, opFunc:(DenseMatrix,DenseMatrix,Long)=>DenseMatrix,
                                           leftPlist:Rail[Long], rightRoot:Long, rightPlist:Rail[Long]) {
        
        val rmtbuf:GlobalRail[ElemType];
        
        finish {
            //Left branch reduction
            if (leftPlist.size > 0)     async {
                    reduceToHere(distBS, tmpBS, rootbid, dstblk, datCnt, select, opFunc, leftPlist);
                }
            // Need to bring data from remote place
            rmtbuf = at(Place(rightRoot)) {
                //Remote capture:distBS, tmpBS, colCnt, rightPlist
                val rmtblk = distBS().findFrontBlock(rootbid, select);
                //Debug.flushln("Visiting block("+rmtblk.myRowId+","+rmtblk.myColId+")");
                
                if (rightPlist.size > 0)  {
                    //Only if there are more places to visit than remote pid
                    reduceToHere(distBS, tmpBS, rootbid, rmtblk, datCnt, select, opFunc, rightPlist);
                }
                new GlobalRail[ElemType](rmtblk.getData() as Rail[ElemType]{self!=null})
            };
        }
        
        //val dstblk = distBS().findFrontBlock(rootbid, select);
        val rcvblk = tmpBS().findFrontBlock(rootbid, select);
        val rcvden = rcvblk.getMatrix() as DenseMatrix;
        val dstden = dstblk.getMatrix() as DenseMatrix;
        
        finish Rail.asyncCopy[ElemType](rmtbuf, 0, rcvden.d, 0, datCnt);
        //Debug.flushln("Perform reduction with data from place "+leftRoot);
        opFunc(rcvden, dstden, datCnt);
    }
}
