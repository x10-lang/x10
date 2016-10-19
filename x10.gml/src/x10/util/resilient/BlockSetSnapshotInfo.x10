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
import x10.util.resilient.localstore.Cloneable;

public class BlockSetSnapshotInfo(placeIndex:Long, isSparse:Boolean) implements Cloneable {
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
    
    public def clone():Cloneable {
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

