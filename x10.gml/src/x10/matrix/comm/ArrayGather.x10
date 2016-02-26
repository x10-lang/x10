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
 * Gather operations collects data arrays distributed in all places to
 * root. The distributed storage can be accessed via PlaceLocalHandle.
 */
public class ArrayGather extends ArrayRemoteCopy {
    /**
     * Gather distributed arrays from all places to here
     * at here.
     * 
     * @param src    distributed storage of source arrays on PlaceLocalHandle
     * @param dst    storage of list arrays for gather result
     */
    public static def gather(
            src:DataArrayPLH, 
            dst:Rail[Rail[ElemType]{self!=null}]) : void {
        
        val nb = Place.numPlaces();
        assert (nb==dst.size) :
            "Number of blocks in dist and local array do not match";
        
        finish for (var bid:Long=0; bid<nb; bid++) {
            val dstbuf = dst(bid);
            
            if (bid == here.id()) {
                val srcbuf = src();
                Rail.copy(srcbuf, 0L, dstbuf, 0L, dstbuf.size);

            } else {
                x10Copy(src, bid, 0, dstbuf, 0, dstbuf.size);
            }
            
        }
    }

    /**
     * Gather distributed arrays from all places to here and store 
     * in a continuous memory space
     *
     * @param src    distributed storage of source arrays on PlaceLocalHandle
     * @param dst    storage array for gather result
     * @param gp     list of array sizes
     */
    public static def gather( 
            src:DataArrayPLH, 
            dst:Rail[ElemType]{self!=null},
            gp:Rail[Long]) : void {

        x10Gather(src, dst, gp);
    }

    /**
     * Gather distributed arrays from a place group to here and store 
     * in a continuous memory space
     * 
     * @param src     distributed storage of source arrays on PlaceLocalHandle
     * @param dst     storage array for gather result
     * @param gp      list of array sizes
    * @param places  the place group to gather from
     */
    public static def gather( 
            src:DataArrayPLH, 
            dst:Rail[ElemType]{self!=null},
            gp:Rail[Long],
            places:PlaceGroup) : void {
        
        x10Gather(src, dst, gp, places);
    }

    /**
     * Gather distributed arrays from all places to here by using x10 remote array copy.
     * 
     * @param src     distributed storage of data arrays in all places.
     * @param dstbuf  storage array for gather result
     * @param gp      list of array sizes
     */
    public static def x10Gather(
            src:DataArrayPLH, 
            dstbuf:Rail[ElemType]{self!=null},
            gp:Rail[Long]): void {

        x10Gather(src, dstbuf, gp, Place.places());        
    }

    /**
     * Gather distributed arrays from a place group to here by using x10 remote array copy.
     * 
     * @param src     distributed storage of data arrays in all places.
     * @param dstbuf  storage array for gather result
     * @param gp list of array sizes
     * @param places      the place group 
     */
    public static def x10Gather(
            src:DataArrayPLH, 
            dstbuf:Rail[ElemType]{self!=null},
            gp:Rail[Long],
            places:PlaceGroup): void {

        assert (gp.size == places.size()) :
            "Number of segments "+gp.size+" not equal to number of places "+places.size();
        val root = here.id();
        var off:Long=0;
        finish for (cb in 0..(places.size()-1)) {
            val datcnt = gp(cb);
            val dstoff = off;
            val pid = places(cb).id;

            if (pid != root) {
                async x10Copy(src, pid, 0, dstbuf, dstoff, datcnt);
            } else {
                //Make local copying
                val srcbuf = src();
                async Rail.copy(srcbuf, 0L, dstbuf, dstoff, datcnt);
            }
            off += datcnt;
        }
    }

    public static def verify(
            src:DataArrayPLH, buf:Rail[ElemType], 
            szlist:Rail[Long]):Boolean =
            ArrayScatter.verify(buf, src, szlist);

    public static def verify(
            src:DataArrayPLH, buf:Rail[ElemType], 
            szlist:Rail[Long], places:PlaceGroup):Boolean =
            ArrayScatter.verify(buf, src, szlist, places);

}
