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

import x10.matrix.util.Debug;
import x10.matrix.comm.mpi.WrapMPI;

/**
 * This class broadcasts data in double-precision or CompressArray to all places
 * 
 * <p> Two implementations are available, using MPI routines and X10 remote array copy.
 * To enable MPI communication, add "-define MPI_COMMU -cxx-prearg -DMPI_COMMU"
 * in x10c++ build command, when you include commu package in your application source
 * code, or link to the proper GML library (native_mpi version).
 * 
 * <p>For more information on how to build different backends and runtime, 
 * run command "make help" at the root directory of GML library.
 */
public class ArrayBcast extends ArrayRemoteCopy {
    /**
     * Broadcast data array from here to all other places.
     * Remote places are accessed via PlaceLocalHandle.
     * 
     * @param dmat    distributed storage for source and its copies in all places
     */
    public static def bcast(duplist:DataArrayPLH) {
        val data = duplist();
        bcast(duplist, data.size);
    }

    /**
     * Broadcast data array from here to a place group.
     * Remote places are accessed via PlaceLocalHandle.
     */
    public static def bcast(duplist:DataArrayPLH, pg: PlaceGroup) {
        @Ifdef("MPI_COMMU") {
            Debug.exit("No MPI implementation");
        }
        @Ifndef("MPI_COMMU") {
            val data = duplist();
            x10Bcast(duplist, data.size, pg);
        }
    }

    /**
     * Broadcast data array from here to all other places.
     * 
     * @param duplist     distributed storage for source and its copies in all places
     * @param dataCnt     count of double-precision data to broadcast
     */
    public static def bcast(duplist:DataArrayPLH, dataCnt:Long) : void {
        Debug.assure(dataCnt <= duplist().size, "Data overflow in data buffer");
        
        @Ifdef("MPI_COMMU") {
            mpiBcast(duplist, dataCnt);
        }
        @Ifndef("MPI_COMMU") {
            //Debug.flushln("start bcast to "+numPlaces);
            x10Bcast(duplist, dataCnt);
        }
    } 

    /**
     * Broadcast data array from here to 
     * to all other places.
     * 
     * @param dmlist     distributed storage for source and its broadcast copies in all places
     * @param dataCnt    count of double-precision data to broadcast
     */
    protected static def mpiBcast(dmlist:DataArrayPLH, dataCnt:Long):void {
        @Ifdef("MPI_COMMU") {
            
            if (Place.numPlaces() <= 1) return;
            
            val root   = here.id();
            finish ateach([p] in WrapMPI.world.dist) {
                //Need: dmlist, dataCnt, root
                val dstbuf = dmlist();
                WrapMPI.world.bcast(dstbuf, 0, dataCnt, root);
            }
        }
    }

    /**
     *  Broadcast data to number of places from here
     */
    protected static def x10Bcast(dmlist:DataArrayPLH, dataCnt:Long): void {
        val pcnt = Place.numPlaces();

        if (pcnt <= 1 || dataCnt == 0L) return;
        
        binaryTreeCast(dmlist, dataCnt, pcnt);
    }

    /**
     *  Broadcast data to number of places from here
     */
    protected static def x10Bcast(dmlist:DataArrayPLH, dataCnt:Long, pg: PlaceGroup): void {            
        binaryTreeCast(dmlist, dataCnt, pg, 0, pg.size()-1);
    }

    /**
     * X10 implementation of broadcast data via Binary tree structure.
     */
    protected static def binaryTreeCast(dmlist:DataArrayPLH, dataCnt:Long, pcnt:Long): void {        
        binaryTreeCast(dmlist, dataCnt, Place.places(), 0, pcnt-1);
    }


    /**
     * Broadcast to specified list of places
     */
    protected static def binaryTreeCast(dmlist:DataArrayPLH, dataCnt:Long, pg:PlaceGroup, start:Long, end:Long): void {
        if (end < start) return;
        val src = dmlist();
        val mid = start + (end-start) / 2;        

        // Specify the remote buffer
        val srcbuf = new GlobalRail[Double](src as Rail[Double]{self!=null});

        finish     {            
            at(pg(mid)) {
                val dstbuf = dmlist();
                // Using copyFrom style
                finish Rail.asyncCopy[Double](srcbuf, 0, dstbuf, 0, dataCnt);               
            }
            // Perform binary bcast on the right brank
            async {
                binaryTreeCast(dmlist, dataCnt, pg, start, mid-1);
            }
            // Perform binary bcast on the left branch
            async {
                binaryTreeCast(dmlist, dataCnt, pg, mid+1, end); 
            }
        }
    }

    /**
     * Bcast a segment of data to specified list of places
     */
    public static def bcast(duplist:DataArrayPLH, offset:Long, datCnt:Long, plcList:Rail[Long]) {
        for (var i:Long=0; i<plcList.size; i++) {
            val pid = plcList(i);
            copy(duplist(), offset, duplist, pid, offset, datCnt);
        }
    }


    // Broadcast SparseCSC matrix to all

    /**
     * Broadcast compress array stored in DistArray
     * at here to all other places. 
     * 
     * @param smlist     compress array data buffer in all places
     */
    public static def bcast(smlist:CompArrayPLH) {
        val data = smlist();
        bcast(smlist, data.storageSize());
    }

    /**
     * Broadcast compress array stored in dist array from here
     * to all other places. 
     * 
     * @param smlist    compress array date buffer in all places
     * @param dataCnt   number of data to broadcast
     */
    public static def bcast(smlist:CompArrayPLH, dataCnt:Long): void {
        Debug.assure(dataCnt <= smlist().storageSize(), "Data overflow in bcast");
        
        @Ifdef("MPI_COMMU") {
            mpiBcast(smlist, dataCnt);
        }
        @Ifndef("MPI_COMMU") {
            //Debug.flushln("start bcast to "+numPlaces);
            x10Bcast(smlist, dataCnt);
        }
    } 

    /**
     * Broadcast compress array in the PlaceLocalHandle here 
     * to all other places.
     * 
     * @param smlist     source and target compress array
     * @param dataCnt    number of data to broadcast
     */

    /**
     * Using MPI routine to implement sparse matrix broadcast
     * 
     */
    protected static def mpiBcast(smlist:CompArrayPLH, dataCnt:Long):void {
        @Ifdef("MPI_COMMU") {
            
            if (Place.numPlaces() <= 1) return;
            
            val root   = here.id();
            finish ateach([p] in WrapMPI.world.dist) {
                //Need: root, smlist, datasz, colOff, colCnt,
                val ca = smlist();    
                
                WrapMPI.world.bcast(ca.index, 0, dataCnt, root);
                WrapMPI.world.bcast(ca.value, 0, dataCnt, root);
            }
        }
    }
    
    /**
     *  Broadcast compress array among the pcnt number of places followed from here
     */
    protected static def x10Bcast(smlist:CompArrayPLH, dataCnt:Long): void {
        val pcnt = Place.numPlaces();
        if (pcnt <= 1 || dataCnt == 0L) return;
        
        binaryTreeCast(smlist, dataCnt, pcnt);
    }
    

    /**
     * Broadcast compress array using remote array copy in X10
      * TODO: pcnt not required 
     */
    protected static def binaryTreeCast(smlist:CompArrayPLH, dataCnt:Long, pcnt:Long): void {        
        binaryTreeCast(smlist, dataCnt, Place.places(), 0, pcnt-1);
    }

    protected static def binaryTreeCast(smlist:CompArrayPLH, dataCnt:Long, pg:PlaceGroup, start:Long, end:Long): void {        
        if (end < start) return;            
        val mid = start + (end-start) / 2;    

        // Specify the remote buffer
        val srcca = smlist();
        val idxbuf = srcca.index;
        val valbuf = srcca.value;
        val srcidx = new GlobalRail[Long  ](idxbuf as Rail[Long  ]{self!=null});
        val srcval = new GlobalRail[Double](valbuf as Rail[Double]{self!=null});

        finish {        
            at(pg(mid)){
                //Need: smlist, srcidx, srcval, srcOff, colOff, colCnt and datasz
                val dstca = smlist();
                finish Rail.asyncCopy[Long  ](srcidx, 0, 
                        dstca.index, 0, dataCnt);
                finish Rail.asyncCopy[Double](srcval, 0, 
                        dstca.value, 0, dataCnt);
            }
            // Perform binary bcast on the right brank
            async {
                binaryTreeCast(smlist, dataCnt, pg, start, mid-1);
            }
            // Perform binary bcast on the left branch
            async {
                binaryTreeCast(smlist, dataCnt, pg, mid+1, end); 
            }
        }
    }

    
    //util
    public static def verify(srcplh:DataArrayPLH, dataCnt:Long):Boolean {
        var ret:Boolean = true;
        val buf=srcplh();
        //for ([p] in Place.places()) {
        for (place in Place.places()) {
            val rmt= at(place) srcplh();//remote capture
            for (var i:Long=0; i<dataCnt; i++) ret &= (buf(i)==rmt(i));
        }
        return ret;
    }
}
