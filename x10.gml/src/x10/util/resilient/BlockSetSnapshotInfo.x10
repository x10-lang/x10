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

public class BlockSetSnapshotInfo(placeIndex:Long, blockSet:BlockSet) implements Snapshot {
    public def clone():Any {
        return new BlockSetSnapshotInfo(placeIndex, blockSet.clone());
    }

    public final def remoteCopyAndSave(key:Any, hm:GlobalRef[HashMap[Any,Any]]) {
        val copyBlockSet = blockSet.clone();            
        val idx = placeIndex;            
        val blocksCount = copyBlockSet.blocklist.size();            
        val isSparse =  copyBlockSet.blocklist.get(0).isSparse();            
        val metaDataRail = copyBlockSet.getBlocksMetaData();            
        val metaDataSize = metaDataRail.size;            
        val totalSize = copyBlockSet.getStorageSize();
        if (isSparse) {
            copyBlockSet.initSparseBlocksRemoteCopyAtSource();
            val allIndex = new Rail[Long](totalSize);
            val allValue = new Rail[Double](totalSize);
            copyBlockSet.flattenIndex(allIndex);
            copyBlockSet.flattenValue(allValue);
            val idxGR = new GlobalRail[Long](allIndex);
            val valGR = new GlobalRail[Double](allValue);
            val mGR = new GlobalRail[Long](metaDataRail);
            at(hm) {
                val newBlockSet = BlockSet.remoteMakeSparseBlockSet(blocksCount, metaDataSize, totalSize, mGR, idxGR, valGR);                    
                val remoteBS = new BlockSetSnapshotInfo(idx, newBlockSet);                                        
                atomic hm().put(key, remoteBS);
            }
            //no need to call finalize because the object will not be used again
            //copyBlockSet.finalizeSparseBlocksRemoteCopyAtSource();
        } else {
            val allValue = new Rail[Double](totalSize);
            copyBlockSet.flattenValue(allValue);
            val valGR = new GlobalRail[Double](allValue);
            val mGR = new GlobalRail[Long](metaDataRail);
            at(hm) {
                val newBlockSet = BlockSet.remoteMakeDenseBlockSet(blocksCount, metaDataSize, totalSize, mGR, valGR);
                val remoteBS = new BlockSetSnapshotInfo(placeIndex, newBlockSet);
                atomic hm().put(key, remoteBS);
            }
        }
    }

    public final def remoteClone(targetPlace:Place):GlobalRef[Any]{self.home==targetPlace} {
        val copyBlockSet = blockSet.clone();            
        val idx = placeIndex;
        val blocksCount = copyBlockSet.blocklist.size();
        val isSparse =  copyBlockSet.blocklist.get(0).isSparse();        
        val metaDataRail = copyBlockSet.getBlocksMetaData();
        val metaDataSize = metaDataRail.size;
        val totalSize = copyBlockSet.getStorageSize();
        if (isSparse) {
            copyBlockSet.initSparseBlocksRemoteCopyAtSource();
            val allIndex = new Rail[Long](totalSize);
            val allValue = new Rail[Double](totalSize);                
            copyBlockSet.flattenIndex(allIndex);                                
            copyBlockSet.flattenValue(allValue);                        
            val idxGR = new GlobalRail[Long](allIndex);
            val valGR = new GlobalRail[Double](allValue);
            val mGR = new GlobalRail[Long](metaDataRail);

            val resultGR = at(targetPlace) {
                val newBlockSet = BlockSet.remoteMakeSparseBlockSet(blocksCount, metaDataSize, totalSize, mGR, idxGR, valGR);
                val gr = new GlobalRef[Any](new BlockSetSnapshotInfo(idx, newBlockSet));
                gr
            };
            //no need to call finalize because the object will not be used again
            //copyBlockSet.finalizeSparseBlocksRemoteCopyAtSource();
            return resultGR;
        } else {
            val allValue = new Rail[Double](totalSize);
            copyBlockSet.flattenValue(allValue);
            val valGR = new GlobalRail[Double](allValue);
            val mGR = new GlobalRail[Long](metaDataRail);
            val resultGR = at(targetPlace) {
                val newBlockSet = BlockSet.remoteMakeDenseBlockSet(blocksCount, metaDataSize, totalSize, mGR, valGR);
                val gr = new GlobalRef[Any](new BlockSetSnapshotInfo(idx, newBlockSet));
                gr
            };
            return resultGR;
        }
    }
}
