/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.lang;

import x10.compiler.Native;
import x10.compiler.CompilerFlags;

/**
 * Representation of a Place within the APGAS model.
 */
public final struct Place(
    /*
     * Implementation note.
     * The X10RT messaging layer and native XRC/XRJ native runtime
     * only support int (32 bit) Place id's. 
     * When entering/exiting the native layer we simply truncate/extend the id.
     * The truncation is unchecked.  This is safe because we check in the constructor
     * of Place that the id is between -1 and numAllPlaces() and numAllPlaces() will be limited
     * by the native runtime (which sets it) to a positive 32 bit int.
     */
    id:Long)  {
    public property id():Long = id;

    /**
     * The place in which the user 'main' function is run.
     */
    public static FIRST_PLACE:Place(0) = Place(0);
    
    /**
     * Special Place that encodes non-existent Places
     */
    public static INVALID_PLACE:Place(-1) = Place(-1);

    /**
     * The number of primary places (does not include accelerators).
     * Invariant: Place.numPlaces() == Place.places().numPlaces().
     */
    @Native("java", "((long)x10.x10rt.X10RT.numPlaces())")
    @Native("c++", "((x10_long)x10rt_nhosts())")
    public static native def numPlaces():Long;
    
    /** 
     * The total number of all kinds of places (both primary and children/accelerators).
     */
    @Native("java", "((long)x10.x10rt.X10RT.numPlaces())")
    @Native("c++", "((x10_long)x10rt_nplaces())")
    public static native def numAllPlaces():Long;

    /** 
     * The number of primary places known to be dead by the 
     * current place, does not include accelerators. 
     */
    @Native("java", "((long)x10.x10rt.X10RT.numDead())")
    @Native("c++", "((x10_long)x10rt_ndead())")
    public static native def numDead():Long;

    /**
     * Returns whether a place is dead.
     */
    @Native("java", "x10.x10rt.X10RT.isPlaceDead((int)#id)")
    @Native("c++", "x10rt_is_place_dead((x10_int)#id)")
    public static def isDead(id:Long):Boolean = false;

    /**
     * A PlaceGroup the contains all the currently live primary Places.
     */
    public static def places():PlaceGroup{self!=null} {
        val nd = numDead();
        if (nd == 0) {
            return new PlaceGroup.SimplePlaceGroup(numPlaces());
        } else {
            val np = numPlaces();
            try {
                CUR_WORLD.lock.lock();
                if (CUR_WORLD.numPlaces == np && CUR_WORLD.numDead == nd) {
                    // cache hit.  Simply return the pre-built place group for (np, nd)
                    val world = CUR_WORLD.world;
                    return world; // finally will unlock.
                } else {
                    // cache miss.  Have to construct a PlaceGroup.
                    //              This is expensive, so hold CUR_WORLD.lock
                    //              to avoid doing it more than we have to.
                    val live = new x10.util.GrowableRail[Place](np);
                    var seenDead:long = 0;
                    for (i in 0..(np-1)) {
                        val p = Place(i);
                        if (p.isDead()) {
                            seenDead++;
                        } else {
                            live.add(p);
                        }
                    }
                    val res = new SparsePlaceGroup(live.toRail());
                    if (seenDead == nd) {
                        // Didn't observably change while we were building res.  Cache it.
                        CUR_WORLD.numPlaces = np;
                        CUR_WORLD.numDead = nd;
                        CUR_WORLD.world = res;
                    }
                    return res; // finally will unlock
                }
            } finally {
                CUR_WORLD.lock.unlock();
            }
        }
    } 

    /**
     * Creates a Place struct from a place id.
     */
    public def this(id:Long):Place(id) { 
        property(id); 
        if (CompilerFlags.checkPlace() && (id < -1 || id >= numAllPlaces())) {
            throw new IllegalArgumentException(id+" is not a valid Place id");
        }
    }

    /** Is this place dead? */
    public def isDead():Boolean = isDead(id);

    // TODO: Should probably remove in favor of PlaceTopology
    @Native("c++", "::x10aux::is_cuda((x10_int)((#this)->FMGL(id)))")
    public def isCUDA():Boolean = false;

    // TODO: Should probably remove in favor of PlaceTopology
    @Native("c++", "::x10::lang::Place::_make(::x10aux::parent((x10_int)((#this)->FMGL(id))))")
    public def parent():Place = this;

    public def toString() = "Place(" + this.id + ")";
    public def equals(p:Place) = p.id==this.id;
    public def equals(p:Any) = p instanceof Place && (p as Place).id==this.id;
    public def hashCode() = id as Int;

    
    /**
     * Converts a GlobalRef to its home.
     */
    @Native("java", "(#r).home")
    @Native("c++", "::x10::lang::Place::_make(((x10_long)((#r)->location)))")
    public static native operator[T] (r:GlobalRef[T]){T isref}: Place{self==r.home};


    private static CUR_WORLD:PlaceGroupCache = new PlaceGroupCache(0, 0, new PlaceGroup.SimplePlaceGroup(0));

    private static class PlaceGroupCache {
        var numPlaces:Long;
        var numDead:Long;
        var world:PlaceGroup{self!=null};
        val lock:x10.util.concurrent.Lock;

        def this(np:Long, nd:Long, w:PlaceGroup{self!=null}) {
          numPlaces = np;
          numDead = nd;
          world = w;
          lock = new x10.util.concurrent.Lock();
        }
    }

}

public type Place(id:Long) = Place{self.id==id};
public type Place(p:Place) = Place{self==p};
