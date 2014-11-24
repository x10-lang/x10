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

public class VectorSnapshotInfo(placeIndex:Long, data:Rail[Double]{self!=null}) implements Snapshot { 
    public def clone():Any {  
        return new VectorSnapshotInfo(placeIndex, new Rail[Double](data));
    }

    public final def remoteCopyAndSave(key:Any, hm:GlobalRef[HashMap[Any,Any]]) {
        val srcbuf = new GlobalRail[Double](data);
        val srcbufCnt = data.size;
        val idx = placeIndex;
        at(hm) {
            val dstbuf = new Rail[Double](srcbufCnt);
            finish Rail.asyncCopy[Double](srcbuf, 0, dstbuf, 0, srcbufCnt);
            atomic hm().put(key, new VectorSnapshotInfo(idx, dstbuf));
        }
    }

    public final def remoteClone(targetPlace:Place):GlobalRef[Any]{self.home==targetPlace} {
        val srcbuf = new GlobalRail[Double](data);
        val srcbufCnt = data.size;
        val idx = placeIndex;
        val resultGR = at(targetPlace) {
            val dstbuf = new Rail[Double](srcbufCnt);
            finish Rail.asyncCopy[Double](srcbuf, 0, dstbuf, 0, srcbufCnt);
            val gr = new GlobalRef[Any](new VectorSnapshotInfo(idx, dstbuf));
            gr
        };
        return resultGR;
    }
}

