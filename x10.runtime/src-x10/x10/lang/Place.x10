/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.lang;

import x10.compiler.Native;
import x10.compiler.CompilerFlags;

/**
 * Representation of a place within the APGAS model.
 * 
 */
public final struct Place(
    /*
     * Implementation note.
     * The X10RT messaging layer and native XRC/XRJ native runtime
     * only support int (32 bit) Place id's. 
     * When entering/exiting the native layer we simply truncate/extend the id.
     * The truncation is unchecked.  This is safe because we check in the constructor
     * of Place that the id is between -1 and MAX_PLACES and MAX_PLACES will be limited
     * by the native runtime (which sets it) to a positive 32 bit int.
     */
    id:Long)  {
    public property id():Long = id;

    /** The number of places including accelerators.
     * Accelerator places have limitations on the kinds of code they can run.
     */
    @Native("java", "((long)x10.runtime.impl.java.Runtime.MAX_PLACES)")
    @Native("c++", "((x10_long)x10aux::num_places)")
    public static ALL_PLACES:Long = 4;

    /** The number of places not including accelerators. */
    @Native("java", "((long)x10.runtime.impl.java.Runtime.MAX_PLACES)")
    @Native("c++", "((x10_long)x10aux::num_hosts)")
    public static MAX_PLACES: Long = 4;

    /** The number of accelerators. */
    public static NUM_ACCELS = ALL_PLACES - MAX_PLACES;

    /**
     * Find number of children under a place.
     * For hosts, this returns the number of accelerators at that host.
     * For accelerators, it returns 0.
     */
    @Native("c++", "((x10_long)x10aux::num_children((x10_int)#id))")
    public static def numChildren(id:Long):Long = 0;

    /**
     * Returns whether a place is a host.
     */
    @Native("c++", "x10aux::is_host((x10_int)#id)")
    public static def isHost(id:Long):Boolean = true;

    /**
     * Returns whether a place is a CUDA GPU.
     */
    @Native("c++", "x10aux::is_cuda((x10_int)#id)")
    public static def isCUDA(id:Long):Boolean = false;

    /**
     * Find parent of a place.
     * For hosts, this returns the host itself.
     * For accelerators, it is the host of the accelerator.
     */
    @Native("c++", "x10aux::parent((x10_int)#id)")
    public static def parent(id:Long):Long = id;

    /**
     * Return id of ith child of place p.
     * Use i between 0 and numChildren(p)-1 inclusive.
     * Throws BadPlaceException if i invalid.
     */
    @Native("c++", "((x10_long)x10aux::child((x10_int)#p,(x10_int)#i))")
    public static def child(p:Long, i:Long):Long { throw new BadPlaceException(); }

    /**
     * Return the index of a given child, within a place.
     * Throws BadPlaceException if given place is not a child.
     */
    @Native("c++", "((x10_long)x10aux::child_index((x10_int)#id))")
    public static def childIndex(id:Long):Long { throw new BadPlaceException(); }

    public static children =
        new Rail[Rail[Place]](ALL_PLACES,
            (p: Long) => new Rail[Place](numChildren(p), (i:Long) => Place(child(p, i))));

    /**
     * A convenience for iterating over all host places.
     */
    public static def places():PlaceGroup = PlaceGroup.WORLD;

    /**
     * The place that runs 'main'.
     */
    public static FIRST_PLACE:Place(0L) = Place(0L);
    
    /**
     * Special place type for non-existent places
     */
    public static INVALID_PLACE:Place(-1L) = Place(-1L);

    /**
     * Creates a Place struct from an integer place id.
     */
    public def this(id:Long):Place(id) { 
        property(id); 
        if (CompilerFlags.checkPlace() && (id < -1 || id >= ALL_PLACES)) {
            throw new IllegalArgumentException(id+" is not a valid Place id");
        }
    }

    /**
     * Another way to get a place from an id. @deprecated
     */
    public static def place(id:Long):Place(id) = Place(id);

    /**
     * Returns the place with the next higher integer index.
     */
    public def next():Place = next(1);

    /**
     * Returns the place with the next lower integer index.
     */
    public def prev():Place = next(-1);

    /**
     * Returns the same place as would be obtained by using prev() 'i' times.
     */
    public def prev(i:Long):Place = next(-i);

    /**
     * Returns the same place as would be obtained by using next() 'i' times.
     */
    public def next(i:Long):Place {
        // -1 % n == -1, not n-1, so need to add n
        if (isHost(id)) {
            val k = (id + i % MAX_PLACES + MAX_PLACES) % MAX_PLACES;
            return place(k);
        }
        // FIXME: iterate through peers
        return this;
    }

    /**
     * The number of places including accelerators.
     */
    public static def numPlaces():Long = ALL_PLACES;

    /**
     * 
     */
    public def isFirst():Boolean = id == 0L;
    public def isLast():Boolean = id == MAX_PLACES - 1L;

    /** Is this place a host (i.e. not an accelerator)? */
    public def isHost():Boolean = isHost(id);

    /** Is this place a CUDA GPU? */
    public def isCUDA():Boolean = isCUDA(id);

    /** 
     *How many accelerators does this place have?
     * Returns 0 if this place is an accelerator. 
     */
    public def numChildren() = numChildren(id);

    /** 
     * Get the child of this place at the given index.  0 is the first child, etc.
     */
    public def child(i:Long) = Place(child(id,i));

    /** A convenience for iterating over this place's children. */
    public def children() = children(id);

    /** The host of this place if this place is an accelerator, otherwise returns this place. */
    public def parent() = Place(parent(id));

    /** Returns the index of this child place amongst the other children of its parent.
     * This function complements child(Long):Place.
     * @throws BadPlaceException if this place is not an accelerator. */
    public def childIndex() {
        if (isHost()) {
            throw new BadPlaceException();
        }
        return childIndex(id);
    }

    public def toString() = "Place(" + this.id + ")";
    public def equals(p:Place) = p.id==this.id;
    public def equals(p:Any) = p instanceof Place && (p as Place).id==this.id;
    public def hashCode() = id as int;

    
    /**
     * Converts a GlobalRef to its home.
     */
    @Native("java", "(#r).home")
    @Native("c++", "x10::lang::Place::place(((x10_long)((#r)->location)))")
    public static native operator[T] (r:GlobalRef[T]){T isref}: Place{self==r.home};

}

public type Place(id:Long) = Place{self.id==id};
public type Place(p:Place) = Place{self==p};
