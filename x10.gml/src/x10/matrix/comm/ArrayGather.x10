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
 * Gather operations collects data arrays distributed in all places to
 * root. The distributed storage can be accessed via PlaceLocalHandle.
 *
 * <p> Two implementations are available. One uses MPI routines, and the other
 * is based on X10 remote array copy.
 * To enable MPI communication, add "-define MPI_COMMU -cxx-prearg -DMPI_COMMU"
 * in x10c++ build command, when you include commu package in your application source
 * code, or link to the proper GML library (native_mpi version).
 * 
 * <p>For more information on how to build different backends and runtime, 
 * run command "make help" at the root directory of GML library.
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

                @Ifdef("MPI_COMMU") { 
                    mpiCopy(src, bid, 0, dstbuf, 0, dstbuf.size);    
                }
                @Ifndef("MPI_COMMU") {
                    x10Copy(src, bid, 0, dstbuf, 0, dstbuf.size);
                }
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

        @Ifdef("MPI_COMMU") {
            mpiGather(src, dst, gp);
        }
        @Ifndef("MPI_COMMU") {
            x10Gather(src, dst, gp);
        }
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
        
        @Ifdef("MPI_COMMU") {
            throw new UnsupportedOperationException("No MPI implementation");
        }
        @Ifndef("MPI_COMMU") {
            x10Gather(src, dst, gp, places);
        }
    }

    /**
     * Gather distributed arrays from all places to here
     * by using mpi gather routine.
     * 
     * @param src     distributed storage of data arrays 
     * @param dst     storage array for gather result at here
     * @param szlist  list of array sizes.
     */
    public static def mpiGather(
            src:DataArrayPLH, 
            dst:Rail[ElemType],
            szlist:Rail[Long]):void {
        
        @Ifdef("MPI_COMMU") {
            val root = here.id();
            finish { 
                for([p] in WrapMPI.world.dist) {
                    val datcnt = szlist(p);
                    if (p != root) {
                        at(WrapMPI.world.dist(p)) async {
                            val srcbuf = src();
                            /*******************************************/
                            // Not working
                            //val tmpbuf= null; //fake
                            //val tmplst=null;//   //fake
                            /*******************************************/
                            val tmpbuf = new Rail[ElemType](0); //fake
                            val tmplst = new Rail[Long](0);   //fake
                            WrapMPI.world.gatherv(srcbuf, 0, datcnt, tmpbuf, 0, tmplst, root);
                        }
                    } 
                }

                async {
                    /**********************************************/
                    // DO NOT move this block into for loop block
                    // MPI process will hang, Cause is not clear
                    /**********************************************/    
                    val srcbuf = src();
                
                    WrapMPI.world.gatherv(srcbuf, 0, szlist(root), dst, 0, szlist, root);
                }
            
            }
        }
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
