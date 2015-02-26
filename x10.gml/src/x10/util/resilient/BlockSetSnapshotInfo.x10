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

    public final def remoteCopyAndSave(key:Any, hm:GlobalRef[HashMap[Any,Any]]) {
        val idx = placeIndex;
        val sparse = isSparse;
        val blkCnt = blocksCount;
        
        if (sparse) {
            val srcbuf_value = new GlobalRail[ElemType](value);
            val srcbuf_index = new GlobalRail[Long](index);
            val srcbuf_meta = new GlobalRail[Long](metadata);
            val srcbufCnt_value = value.size;
            val srcbufCnt_index = index.size;
            val srcbufCnt_meta = metadata.size;
            
            at(hm) {
                val dstbuf_value = new Rail[ElemType](srcbufCnt_value);
                val dstbuf_index = new Rail[Long](srcbufCnt_index);
                val dstbuf_meta = new Rail[Long](srcbufCnt_meta);
                
                finish {
                    Rail.asyncCopy[ElemType](srcbuf_value, 0, dstbuf_value, 0, srcbufCnt_value);
                    Rail.asyncCopy[Long](srcbuf_index, 0, dstbuf_index, 0, srcbufCnt_index);
                    Rail.asyncCopy[Long](srcbuf_meta, 0, dstbuf_meta, 0, srcbufCnt_meta);
                }
                
                val remoteBS = new BlockSetSnapshotInfo(idx, sparse);
                remoteBS.blocksCount = blkCnt;
                remoteBS.metadata = dstbuf_meta;
                remoteBS.index = dstbuf_index;
                remoteBS.value = dstbuf_value;
                atomic hm().put(key, remoteBS);
            }
        } else if (value != null) {
            val srcbuf_value = new GlobalRail[ElemType](value);
            val srcbuf_meta = new GlobalRail[Long](metadata);
            val srcbufCnt_value = value.size;
            val srcbufCnt_meta = metadata.size;
            
            at(hm) {
                val dstbuf_value = new Rail[ElemType](srcbufCnt_value);
                val dstbuf_meta = new Rail[Long](srcbufCnt_meta);
                
                finish {
                    Rail.asyncCopy[ElemType](srcbuf_value, 0, dstbuf_value, 0, srcbufCnt_value);
                    Rail.asyncCopy[Long](srcbuf_meta, 0, dstbuf_meta, 0, srcbufCnt_meta);
                } 
                
                val remoteBS = new BlockSetSnapshotInfo(idx, sparse);
                remoteBS.blocksCount = blkCnt;
                remoteBS.metadata = dstbuf_meta;
                remoteBS.value = dstbuf_value;
                atomic hm().put(key, remoteBS);
               }
        } else {
              ///val copyBlockSet = blockSet;  no need to clone in save 
            val blocksCount = blockSet.blocklist.size();
            val metaDataRail = blockSet.getBlocksMetaData();
            val metaDataSize = metaDataRail.size;
            val totalSize = blockSet.getStorageSize();
            val allValue = new Rail[ElemType](totalSize);
            blockSet.flattenValue(allValue);
            val valGR = new GlobalRail[ElemType](allValue);
            val mGR = new GlobalRail[Long](metaDataRail);
            at(hm) {
                val newBlockSet = BlockSet.remoteMakeDenseBlockSet(blocksCount, metaDataSize, totalSize, mGR, valGR);
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
            val srcbuf_meta = new GlobalRail[Long](metadata);
            val srcbufCnt_value = value.size;
            val srcbufCnt_index = index.size;
            val srcbufCnt_meta = metadata.size;
            
            val resultGR = at(targetPlace) {
                val dstbuf_value = new Rail[ElemType](srcbufCnt_value);
                val dstbuf_index = new Rail[Long](srcbufCnt_index);
                val dstbuf_meta = new Rail[Long](srcbufCnt_meta);
                
                finish {
                    Rail.asyncCopy[ElemType](srcbuf_value, 0, dstbuf_value, 0, srcbufCnt_value);
                    Rail.asyncCopy[Long](srcbuf_index, 0, dstbuf_index, 0, srcbufCnt_index);
                    Rail.asyncCopy[Long](srcbuf_meta, 0, dstbuf_meta, 0, srcbufCnt_meta);
                } 
                
                val remoteBS = new BlockSetSnapshotInfo(idx, sparse);
                remoteBS.blocksCount = blkCnt;
                remoteBS.metadata = dstbuf_meta;
                remoteBS.index = dstbuf_index;
                remoteBS.value = dstbuf_value;
                
                val gr = new GlobalRef[Any](remoteBS);
                gr
            };
            return resultGR;
        } else if (value != null) { // dense
            val srcbuf_value = new GlobalRail[ElemType](value);
            val srcbuf_meta = new GlobalRail[Long](metadata);
            val srcbufCnt_value = value.size;
            val srcbufCnt_meta = metadata.size;
                
            val resultGR = at(targetPlace) {
                val dstbuf_value = new Rail[ElemType](srcbufCnt_value);
                val dstbuf_meta = new Rail[Long](srcbufCnt_meta);
                
                finish {
                    Rail.asyncCopy[ElemType](srcbuf_value, 0, dstbuf_value, 0, srcbufCnt_value);
                    Rail.asyncCopy[Long](srcbuf_meta, 0, dstbuf_meta, 0, srcbufCnt_meta);
                }
                
                val remoteBS = new BlockSetSnapshotInfo(idx, sparse);
                remoteBS.blocksCount = blkCnt;
                remoteBS.metadata = dstbuf_meta;
                remoteBS.value = dstbuf_value;
                
                val gr = new GlobalRef[Any](remoteBS);
                gr
            };
            return resultGR;
        } else { // dense
            // because many threads can be loading the same object from the resilient store
            val copyBlockSet = blockSet.clone();
            val blocksCount = copyBlockSet.blocklist.size();
            val metaDataRail = copyBlockSet.getBlocksMetaData();
            val metaDataSize = metaDataRail.size;
            val totalSize = copyBlockSet.getStorageSize();
            val allValue = new Rail[ElemType](totalSize);
            copyBlockSet.flattenValue(allValue);
            val valGR = new GlobalRail[ElemType](allValue);
            val mGR = new GlobalRail[Long](metaDataRail);
            val resultGR = at(targetPlace) {
                val newBlockSet = BlockSet.remoteMakeDenseBlockSet(blocksCount, metaDataSize, totalSize, mGR, valGR);
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

