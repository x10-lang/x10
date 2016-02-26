/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2014.
 *  (C) Copyright Sara Salem Hamouda 2014.
 */
package x10.util.resilient;

import x10.util.HashMap;
import x10.matrix.distblock.BlockSet;
import x10.matrix.ElemType;
import x10.util.resilient.iterative.Snapshot;

public class BlockSetSnapshotInfo(placeIndex:Long, isSparse:Boolean) implements Snapshot {
    private var blockSet:BlockSet;
    
    //**Variables for Flat BlockSet**//
    var blocksCount:Long;
    var metadata:Rail[Long];
    var index:Rail[Long];
    var value:Rail[ElemType];
    
    public def initSparse(blocksCount:Long, metadata:Rail[Long]{self!=null}, index:Rail[Long]{self!=null}, value:Rail[ElemType]{self!=null}) {
        this.blocksCount = blocksCount;
        this.metadata = metadata;
        this.index = index;
        this.value = value;
    }
    
    public def setBlockSet(blockSet:BlockSet) {
        this.blockSet = blockSet;
    }
    
    public def initDense(blocksCount:Long, metadata:Rail[Long]{self!=null}, value:Rail[ElemType]{self!=null}) {
        this.blocksCount = blocksCount;
        this.metadata = metadata;
        this.value = value;
    }
    
    public def clone():Any {
        val cloneObject = new BlockSetSnapshotInfo(placeIndex, isSparse);
        if (isSparse) {
            cloneObject.blocksCount = blocksCount; 
            cloneObject.metadata = new Rail[Long](metadata);
            cloneObject.index = new Rail[Long](index);
            cloneObject.value = new Rail[ElemType](value);
        } else {
            if (value != null) {
                cloneObject.blocksCount = blocksCount;
                cloneObject.metadata = new Rail[Long](metadata);
                cloneObject.value = new Rail[ElemType](value);
            } else {
                cloneObject.blockSet = blockSet.clone();
            }
        }        
        return cloneObject;
    }
    
    public def getBlockSet() {
        if (blockSet == null)
            createBlockSet();
        return blockSet;
    }

    public final def remoteCopyAndSave(key:Any, hm:PlaceLocalHandle[HashMap[Any,Any]], backupPlace:Place) {	
        val idx = placeIndex;
        val sparse = isSparse;
        val blkCnt = blocksCount;
        
        if (sparse) {
            val srcbuf_value = new GlobalRail[ElemType](value);
            val srcbuf_index = new GlobalRail[Long](index);
            val metadata = this.metadata; // copy in body of 'at'
            val srcbufCnt_index = index.size;
            
            at(backupPlace) {
                val dstbuf_value = Unsafe.allocRailUninitialized[ElemType](srcbuf_value.size);
                val dstbuf_index = Unsafe.allocRailUninitialized[Long](srcbufCnt_index);
                finish {
                    Rail.asyncCopy[ElemType](srcbuf_value, 0, dstbuf_value, 0, srcbuf_value.size);
                    Rail.asyncCopy[Long](srcbuf_index, 0, dstbuf_index, 0, srcbufCnt_index);
                
                    val remoteBS = new BlockSetSnapshotInfo(idx, sparse);
                    remoteBS.blocksCount = blkCnt;
                    remoteBS.metadata = metadata;
                    remoteBS.index = dstbuf_index;
                    remoteBS.value = dstbuf_value;
                    atomic hm().put(key, remoteBS);
                }
            }
        } else if (value != null) {
            val srcbuf_value = new GlobalRail[ElemType](value);
            val metadata = this.metadata; // copy in body of 'at'
            
            at(backupPlace) {
                val dstbuf_value = Unsafe.allocRailUninitialized[ElemType](srcbuf_value.size);
                
                finish {
                    Rail.asyncCopy[ElemType](srcbuf_value, 0, dstbuf_value, 0, srcbuf_value.size);
                
                    val remoteBS = new BlockSetSnapshotInfo(idx, sparse);
                    remoteBS.blocksCount = blkCnt;
                    remoteBS.metadata = metadata;
                    remoteBS.value = dstbuf_value;
                    atomic hm().put(key, remoteBS);
                }
            }
        } else {
            val blocksCount = blockSet.blocklist.size();
            val metadata = blockSet.getBlocksMetaData();
            val totalSize = blockSet.getStorageSize();
            val allValue = Unsafe.allocRailUninitialized[ElemType](totalSize);
            blockSet.flattenValue(allValue);
            val valGR = new GlobalRail[ElemType](allValue);
            at(backupPlace) {
                val newBlockSet = BlockSet.remoteMakeDenseBlockSet(blocksCount, totalSize, metadata, valGR);
                val remoteBS = new BlockSetSnapshotInfo(idx, sparse);
                remoteBS.blockSet = newBlockSet;
                atomic hm().put(key, remoteBS);
            }
        }
    }

    public final def remoteClone(targetPlace:Place):GlobalRef[Any]{self.home==targetPlace} {
        val idx = placeIndex;
        val sparse = isSparse;
        val blkCnt = blocksCount;
        
        if (sparse) {
            val srcbuf_value = new GlobalRail[ElemType](value);
            val srcbuf_index = new GlobalRail[Long](index);
            val metadata = this.metadata; // copy in body of 'at'
            val srcbufCnt_index = index.size;
            
            val resultGR = at(targetPlace) {
                val dstbuf_value = Unsafe.allocRailUninitialized[ElemType](srcbuf_value.size);
                val dstbuf_index = Unsafe.allocRailUninitialized[Long](srcbufCnt_index);
                
                finish {
                    Rail.asyncCopy[ElemType](srcbuf_value, 0, dstbuf_value, 0, srcbuf_value.size);
                    Rail.asyncCopy[Long](srcbuf_index, 0, dstbuf_index, 0, srcbufCnt_index);
                }
                val remoteBS = new BlockSetSnapshotInfo(idx, sparse);
                remoteBS.blocksCount = blkCnt;
                remoteBS.metadata = metadata;
                remoteBS.index = dstbuf_index;
                remoteBS.value = dstbuf_value;
                
                val gr = new GlobalRef[Any](remoteBS);
                gr
            };
            return resultGR;
        } else if (value != null) { // dense
            val srcbuf_value = new GlobalRail[ElemType](value);
            val metadata = this.metadata; // copy in body of 'at'
                
            val resultGR = at(targetPlace) {
                val dstbuf_value = Unsafe.allocRailUninitialized[ElemType](srcbuf_value.size);
                
                finish {
                    Rail.asyncCopy[ElemType](srcbuf_value, 0, dstbuf_value, 0, srcbuf_value.size);
                }
                val remoteBS = new BlockSetSnapshotInfo(idx, sparse);
                remoteBS.blocksCount = blkCnt;
                remoteBS.metadata = metadata;
                remoteBS.value = dstbuf_value;
                
                val gr = new GlobalRef[Any](remoteBS);
                gr
            };
            return resultGR;
        } else { // dense
            val blocksCount = blockSet.blocklist.size();
            val metadata = blockSet.getBlocksMetaData();
            val totalSize = blockSet.getStorageSize();
            val allValue = Unsafe.allocRailUninitialized[ElemType](totalSize);
            blockSet.flattenValue(allValue);
            val valGR = new GlobalRail[ElemType](allValue);
            val resultGR = at(targetPlace) {
                val newBlockSet = BlockSet.remoteMakeDenseBlockSet(blocksCount, totalSize, metadata, valGR);
                val remoteBS = new BlockSetSnapshotInfo(idx, sparse);
                remoteBS.blockSet = newBlockSet;
                val gr = new GlobalRef[Any](remoteBS);
                gr
            };
            return resultGR;
        }
    }
    
    
    private def createBlockSet() {
        if (isSparse) {
            blockSet = BlockSet.makeSparseBlockSet(blocksCount, metadata, index, value);
        } else {
            blockSet = BlockSet.makeDenseBlockSet(blocksCount, metadata, value);
    
        }
        metadata = null;
        index = null;
        value = null;
        blocksCount = 0;
    }
}

