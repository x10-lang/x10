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

package x10.matrix.comm;

import x10.compiler.Ifdef;
import x10.compiler.Ifndef;

import x10.matrix.ElemType;

import x10.matrix.comm.mpi.WrapMPI;

/**
 * This class provides implementation for reduce-sum  operation for data arrays which
 * can be accessed via PlaceLocalHandle structure in all places.
 * 
 * <p>To enable MPI communication, add "-define MPI_COMMU -cxx-prearg -DMPI_COMMU"
 * in x10c++ build command, when you include commu package in your application source
 * code, or link to the proper GML library (native_mpi version).
 * 
 * <p>For more information on how to build different backends and runtime, 
 * run command "make help" at the root directory of GML library.
 */
public class ArrayReduce extends ArrayRemoteCopy {
    /** Reduce data from all places to here via PlaceLocalHandle */
    public static def arraySum(src:Rail[ElemType],dst:Rail[ElemType], cnt:Long):Int {
        for (var i:Long=0; i<cnt; i++) dst(i) += src(i);
        return 1n;
    }
    
    /**
     * Reduce data array by adding them together.
     * 
     * @param ddmat     Distributed storage for input and output data arrays
     * @param ddtmp     Temp distributed storage.
     * @param datcnt    count of double-precision data elements
     */
    public static def reduce(dat:DataArrayPLH, tmp:DataArrayPLH, datCnt:Long, 
            opFunc:(Rail[ElemType],Rail[ElemType],Long)=>Int):void {
        @Ifdef("MPI_COMMU") {
            throw new UnsupportedOperationException("No MPI implementation");
        }
        @Ifndef("MPI_COMMU") {
            x10ReduceToHere(dat, tmp, datCnt, Place.numPlaces(), opFunc);
        }
    } 


    /**
     * Reduce data array from a place group by a given reduce function. 
     
     * 
     * @param ddmat     Distributed storage for input and output data arrays
     * @param ddtmp     temp distributed storage used in inter-place communication data.
     * @param datcnt    count of double-precision data elements
     * @param places    the places used for reduction 
     * @param opFunc    the reduction function
     */
    public static def reduce(dat:DataArrayPLH, tmp:DataArrayPLH, datCnt:Long, places:PlaceGroup,
                opFunc:(Rail[ElemType],Rail[ElemType],Long)=>Int):void {                    
        @Ifdef("MPI_COMMU") {
            throw new UnsupportedOperationException("No MPI implementation");
        }
        @Ifndef("MPI_COMMU") {
            x10ReduceToHere(dat, tmp, datCnt, places.size(), here.id, places, opFunc);
        }
    }

    /**
     * Reduce data array by adding them together.
     * 
     * @param ddmat     Distributed storage for input and output data arrays
     * @param ddtmp     Temp distributed storage.
     * @param datcnt    count of double-precision data elements
     */
    public static def reduceSum(dat:DataArrayPLH, tmp:DataArrayPLH,    datCnt:Long):void {
        @Ifdef("MPI_COMMU") 
        {
            mpiReduceSum(dat, tmp, datCnt);
        }
        @Ifndef("MPI_COMMU") {
            x10ReduceToHere(dat, tmp, datCnt, Place.numPlaces(), 
                    (src:Rail[ElemType], dst:Rail[ElemType], c:Long)=>arraySum(src,dst,c));
        }
    }

    /**
     * Reduce data array from a place group by adding them together.
     * [MPI not supported]
     * 
     * @param ddmat     Distributed storage for input and output data arrays
     * @param ddtmp     temp distributed storage used in inter-place communication data.
     * @param datcnt    count of double-precision data elements
     * @param places    the places used for reduction
     */
    public static def reduceSum(dat:DataArrayPLH, tmp:DataArrayPLH, datCnt:Long, places:PlaceGroup):void {        
        @Ifdef("MPI_COMMU") {
            throw new UnsupportedOperationException("No MPI implementation");
        }
        @Ifndef("MPI_COMMU") {
            x10ReduceToHere(dat, tmp, datCnt, places.size(), here.id,places, 
               (src:Rail[ElemType], dst:Rail[ElemType], c:Long)=>arraySum(src,dst,c));
        }
    }


    /**
     * Reduce arrays from all places to here by adding them together.
     * 
     * @param ddmat    Distributed storage for input and output data arrays. 
     * @param ddtmp    Temp distributed storage
     * @param datCnt   count of data in array
     */
    public static def mpiReduceSum(dat:DataArrayPLH, tmp:DataArrayPLH, datCnt:Long):void{
        @Ifdef("MPI_COMMU") 
        {
            val root = here.id();
            finish ateach([p] in WrapMPI.world.dist) {
                val src = tmp();
                val dst = dat();
                Rail.copy(dst, 0L, src, 0L, datCnt);
                // Counting the all reduce-sum time in communication
                WrapMPI.world.reduceSum(src, dst, datCnt, root);
            }
        }
    }
    
    /**
     * Binary recursive reduce sum.
     * Notice dat is input and output data array.
     */
        protected static def x10ReduceToHere(dat:DataArrayPLH, tmp:DataArrayPLH, datCnt:Long, pcnt:Long, 
            opFunc:(Rail[ElemType],Rail[ElemType],Long)=>Int) {
            x10ReduceToHere(dat, tmp, datCnt, pcnt, here.id, Place.places(), opFunc);
    }

        /**
         * Binary recursive reduce from a place group.
         * Notice dat is input and output data array.
         * TODO: fix the limitation that it always starts at place 0
         * 
         * @param dat      Distributed storage for input and output data arrays. 
         * @param tmp      temp distributed storage used in inter-place communication data.
         * @param datCnt   count of data in array
         * @param pcnt     The number of places to reduce from
         * @param root      The index of the root place in the given place group
         * @param places    the places used for reduction
         * @param opFunc    the reduction function
         */
        protected static def x10ReduceToHere(dat:DataArrayPLH, tmp:DataArrayPLH, datCnt:Long, pcnt:Long, root:Long,places:PlaceGroup, 
            opFunc:(Rail[ElemType],Rail[ElemType],Long)=>Int) {
                    
            if (pcnt <= 1) return;        
            val rtcnt  = (pcnt+1) / 2; // make sure right part is larger, if cnt is odd 
            val lfcnt  = pcnt - rtcnt;
            val rtroot = root + lfcnt;
            finish {
                if (rtcnt > 1) {
                    at(places(rtroot)) async {
                        x10ReduceToHere(dat, tmp, datCnt, rtcnt, rtroot, places, opFunc);
                    }
                }
                if (lfcnt > 0) {
                    x10ReduceToHere(dat, tmp, datCnt, lfcnt, root, places, opFunc);
                }
            }
            val dstbuf = dat() as Rail[ElemType]{self!=null};
            val rcvbuf = tmp() as Rail[ElemType]{self!=null};
            x10Copy(dat, places(rtroot).id, 0, rcvbuf, 0, datCnt);
            opFunc(rcvbuf, dstbuf, datCnt);
        }    
    /**
     * Perform all reduce sum operation. 
     * @see reduceSum()
     * Result is synchronized for all copies
     * 
     * @param ddmat    distributed storage for input and output data arrays in all places. 
     * @param ddtmp    temp distributed storage used in inter-place communication data.
     * @param datCnt   count of data in array
     */
    public static def allReduce(
            dat:DataArrayPLH,
            tmp:DataArrayPLH, 
            datCnt:Long, 
            opFunc:(Rail[ElemType],Rail[ElemType],Long)=>Int) {
        
        @Ifdef("MPI_COMMU")    {
            throw new UnsupportedOperationException("No MPI implementation");
        }
        @Ifndef("MPI_COMMU") {
            x10AllReduce(dat, tmp, datCnt, opFunc); 
        }
    } 

    /**
     * Perform all reduce sum operation. 
     * @see reduceSum()
     * Result is synchronized for all copies
     * 
     * @param ddmat    distributed storage for input and output data arrays in all places. 
     * @param ddtmp    temp distributed storage used in inter-place communication data.
     * @param datCnt   count of data in array
     */
    public static def allReduceSum(dat:DataArrayPLH, tmp:DataArrayPLH, datCnt:Long) {
        @Ifdef("MPI_COMMU")    {
            mpiAllReduceSum(dat, tmp, datCnt);
        }
        @Ifndef("MPI_COMMU") {
            x10AllReduce(dat, tmp, datCnt, 
                    (src:Rail[ElemType], dst:Rail[ElemType], c:Long)=>arraySum(src,dst,c));
        }
    } 

    
    /**
     * Perform all reduce sum operation. [MPI not supported]
     * @see reduceSum()
     * Result is synchronized for all copies
     * [MPI not supported]
     * 
     * @param ddmat    distributed storage for input and output data arrays in all places. 
     * @param ddtmp    temp distributed storage used in inter-place communication data.
     * @param datCnt   count of data in array
     * @param places   the places used for reduction
     */
    public static def allReduceSum(dat:DataArrayPLH, tmp:DataArrayPLH, datCnt:Long, places:PlaceGroup) {
        @Ifdef("MPI_COMMU") {
            throw new UnsupportedOperationException("No MPI implementation");
        }
        @Ifndef("MPI_COMMU") {
               x10AllReduce(dat, tmp, datCnt, places,
               (src:Rail[ElemType], dst:Rail[ElemType], c:Long)=>arraySum(src,dst,c));
        }
    } 

    /**
     * Perform all reduce sum operation. 
     * @see reduceSum()
     * Result is synchronized for all copies
     * 
     * @param ddmat    distributed storage for input and output data arrays. 
     * @param ddtmp    temp distributed storage used in inter-place communication.
     * @param datCnt   count of data in array
     */
    protected static def mpiAllReduceSum(
            dat:DataArrayPLH,
            tmp:DataArrayPLH, 
            datCnt:Long): void {
        
        @Ifdef("MPI_COMMU") 
        {
            val root = here.id();
            finish ateach([p] in WrapMPI.world.dist) {
                val pid = here.id();
                val src = tmp();
                val dst = dat();
                Rail.copy(dst, 0L, src, 0L, datCnt);
                // Counting the all reduce-sum time in communication
                WrapMPI.world.allReduceSum(src, dst, datCnt);
            }
        }
    }
    
    protected static def x10AllReduce(dat:DataArrayPLH, tmp:DataArrayPLH, datCnt:Long,
            opFunc:(Rail[ElemType],Rail[ElemType],Long)=>Int){
    
        x10ReduceToHere(dat, tmp, datCnt, Place.numPlaces(), opFunc);
        ArrayBcast.x10Bcast(dat, datCnt);
    }


    /**
     * Perform all reduce operation from a specified place group 
     * Result is synchronized for all copies
     * [MPI not supported]
     * 
     * @param ddmat    distributed storage for input and output data arrays in all places. 
     * @param ddtmp    temp distributed storage used in inter-place communication data.
     * @param datCnt   count of data in array
     * @param places   the places used for reduction
     * @param opFunc   the reduction function
     */
    protected static def x10AllReduce(dat:DataArrayPLH, tmp:DataArrayPLH, datCnt:Long, places:PlaceGroup,
                  opFunc:(Rail[ElemType],Rail[ElemType],Long)=>Int){
          x10ReduceToHere(dat, tmp, datCnt, places.size(), here.id, places, opFunc);
           ArrayBcast.bcast(dat, places);
    }

    /**
     * Perform reduce sum of all array data accessed via PlaceLocalHandle
     * from specified list of places. This method is not optimized.
     *     Result is stored in the data array at root place.
     * 
     * @param ddmat    distributed storage for input and output data arrays. 
     * @param tmp      temp data array storing the inter-place communication data at root.
     * @param datCnt   column count
     * @param plist    list of place IDs
     */
    public static def reduceSum(
            dat:DataArrayPLH,
            tmp:Rail[ElemType]{self!=null}, datCnt:Long,
            plist:Rail[Long]):void{

        val root = here.id();
        val dstbuf = dat();
        val srcbuf = tmp;
        for (placeId in plist) {
            if (placeId != here.id()) {
                copy(dat, placeId, 0, srcbuf, 0, datCnt);
                for (var i:Long=0; i<datCnt; i++) dstbuf(i) += srcbuf(i);
            }
        }
    }
}

