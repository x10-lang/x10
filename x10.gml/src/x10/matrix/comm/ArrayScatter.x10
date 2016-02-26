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

package x10.matrix.comm;

import x10.matrix.ElemType;

/**
 * This class supports scatter operation for data arrays which are defined
 * in PlaceLocalHandle and DistArray at all places. 
 */
public class ArrayScatter extends ArrayRemoteCopy {    
    /**
     * Scatter data from arrays at here to arrays in all places.
     * 
     * @param src      source arrays.
     * @param dst      target distributed arrays
     */
    public static def scatter(
            src:Rail[Rail[ElemType]{self!=null}], 
            dst:DataArrayPLH) : void {
        val nb = Place.numPlaces();
        assert (nb==src.size) :
            "Number blocks in dist and local array mismatch";
        
        finish for (var bid:Long=0; bid<nb; bid++) {
            val srcbuf = src(bid);
            
            if (bid == here.id()) {
                val dstbuf = dst();
                Rail.copy(srcbuf, 0L, dstbuf, 0L, srcbuf.size);

            } else {
                x10Copy(srcbuf, 0, dst, bid, 0, srcbuf.size);
            }
            
        }
    }

    /**
     * Scatter single-row partitioning blocks from here to all places
     */
    public static def scatter(
            src:Rail[ElemType]{self!=null}, 
            dst:DataArrayPLH, 
            gp:Rail[Long]): void {
        assert gp.size == Place.numPlaces();

        x10Scatter(src, dst, gp);
    }


    /**
     * Scatter single-row partitioning blocks from here to a place group
     */
    public static def scatter(
            src:Rail[ElemType]{self!=null}, 
            dst:DataArrayPLH, 
            gp:Rail[Long],
            places:PlaceGroup): void {
        assert gp.size == places.size();
        
        x10Scatter(src, dst, gp, places);
    }

    /**
     * Copy array data from here to distributed array in all places
     * 
     * @param src          source array
     * @param dst          target distributed array 
     * @param szlist       list of sizes of blocks held at each place
     */
    public static def x10Scatter(
            src:Rail[ElemType]{self!=null}, 
            dst:DataArrayPLH, 
            szlist:Rail[Long]):void {

        val root = here.id();
        var off:Long=0;
        finish for (cb in 0..(szlist.size-1)) {
            val datcnt = szlist(cb);
            val srcoff = off;
            if (cb != root) {
                async x10Copy(src, srcoff, dst, cb, 0, datcnt);
            } else {
                //Make local copying
                val dstbuf = dst();
                async Rail.copy(src, srcoff, dstbuf, 0L, datcnt);
            }
            off += datcnt;
        }
    }

    /**
     * Copy array data from here to distributed array in a place group
     * 
     * @param src          source array
     * @param dst          target distributed array 
     * @param szlist       list of sizes of blocks held at each place
     * @param places       scatter place group 
     */
    public static def x10Scatter(
            src:Rail[ElemType]{self!=null}, 
            dst:DataArrayPLH, 
            szlist:Rail[Long],
            places:PlaceGroup):void {

        val root = here.id();
        var off:Long=0;
        finish for (cb in 0..(places.size()-1)) {
            val datcnt = szlist(cb);
            val pid = places(cb).id;
            val srcoff = off;
            if (pid != root) {
                async x10Copy(src, srcoff, dst, pid, 0, datcnt);
            } else {
                //Make local copying
                val dstbuf = dst();
                async Rail.copy(src, srcoff, dstbuf, 0L, datcnt);
            }
            off += datcnt;
        }
    }

    public static def verify(
        src:Rail[ElemType], 
        dstplh:DataArrayPLH, 
        szlist:Rail[Long]):Boolean {        
        return verify(src, dstplh, szlist, Place.places());
    }
    
    public static def verify(
            src:Rail[ElemType], 
            dstplh:DataArrayPLH, 
            szlist:Rail[Long],
            places:PlaceGroup):Boolean {
        var ret:Boolean = true;
        var j:Long=0;
        for (place in places) {
            val rmt = at(place) dstplh();
            for (var i:Long=0; i<szlist(place.id); i++, j++) ret &= (src(j)==rmt(i));
        }
        return ret;
    }
}
