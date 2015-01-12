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

/**
 * <p> A runtime representation of the topology of Places
 * in a running X10 computation.  A PlaceTopology 
 * captures relationships between the set of live Places.  
 * Depending on the characteristics of the system on which the computation
 * is running, these relationships may include information
 * about the CUDA Places (GPUs) that are attached to a CPU Place
 * or more generally about network topology. </p>
 *
 * <p> A PlaceTopology contains 'primary' Places, which can execute
 * any X10 operation and have full connectivity to all other 
 * primary Places and 'child' Places, which only have connectivity
 * to their 'parent' Place and possibly to their children. A child Place
 * may not be capable of executing all X10 operations; in particular it
 * may be a GPU and only capable of executing the subset of the X10
 * language that can be compiled to CUDA.</p>
 *
 * @see Place
 */
public abstract class PlaceTopology {
    /**
     * Get the topology of the currently live places
     */
    public static def getTopology() {
        val ap = num_all_places();
        val pp = num_primary_places();
        if (ap == pp) {
            return new FlatPlaceTopology(pp);
        } else {
            val cp = ap-pp;
	    assert(cp > 0);
            return new OneLevelPlaceTopology(pp, cp);
        }
    }

    /**
     * How many primary Places are in the topology?
     */
    public abstract def numPrimaryPlaces():Long;

    /**
     * How many non-primary Places are in the topology?
     */
    public abstract def numChildrenPlaces():Long;

    /**
     * Is the argument Place a primary Place?
     */
    public abstract def isPrimary(p:Place):Boolean;

    /**
     * Is the argument Place mapped to a CUDA device?
     */
    public abstract def isCUDA(p:Place):Boolean;

    /**
     * What is the parent of the given Place?
     * A Primary place returns itself as its Parent.
     */
    public abstract def getParent(p:Place):Place;

    /**
     * How many children does the given Place have?
     */
    public abstract def numChildren(p:Place):Long;

    /**
     * Return an Iterator over the children of the given Place.
     */
    public abstract def children(p:Place):Iterable[Place];

    /**
     * Get the c'th child of Place p. 
     * @throws BadPlaceException if p does not have a c'th child.
     */
    public abstract def getChild(p:Place, c:Long):Place;

    /**
     * Return the childIndex (dual of getChild) of the given Place. 
     * @throws BadPlaceException if given place is not a child.
     */
    public abstract def childIndex(c:Place):Long;

  
    /*
     * Implementation methods hooking into X10RT.
     * The current implementation is not very flexible 
     * or Elastic X10 friendly, so we want to hide it from
     * users to make it possible to replace with something better.
     */


    @Native("java", "((long)x10.x10rt.X10RT.numPlaces())")
    @Native("c++", "((x10_long)::x10aux::num_places)")
    protected static native def num_all_places():Long;

    @Native("java", "((long)x10.x10rt.X10RT.numPlaces())")
    @Native("c++", "((x10_long)::x10aux::num_hosts)")
    protected static native def num_primary_places():Long;

    /**
     * Trivial topology where there are only primary places
     * and no information about the network topology is available.
     */
    final static class FlatPlaceTopology extends PlaceTopology {
        val numPlaces:Long;

        def this(np:Long) {
            this.numPlaces = np;
        }

        public def numPrimaryPlaces():Long = numPlaces;
        public def numChildrenPlaces():Long = 0;
        public def isPrimary(p:Place):Boolean = true;
        public def isCUDA(p:Place):Boolean = false;
        public def getParent(p:Place):Place = p;
        public def numChildren(p:Place):Long = 0;
        public def children(p:Place):Iterable[Place] {
            return new Rail[Place](0);
        }
        public def getChild(p:Place, c:Long):Place {
            throw new BadPlaceException(p+" has no children");
        }
        public def childIndex(c:Place):Long {
            throw new BadPlaceException(c+" is not a child Place");
        }
    }

    /**
     * Simple topology for primary nodes with attached CUDA devices
     * and no information about the network topology is available.
     */
    static class OneLevelPlaceTopology extends PlaceTopology {
        val numPrimary:Long;
        val numChild:Long;

        def this(np:Long, nc:Long) {
            this.numPrimary = np;
            this.numChild = nc;
        }

        public def numPrimaryPlaces():Long = numPrimary;
        public def numChildrenPlaces():Long = numChild;

        @Native("c++", "::x10aux::is_host((x10_int)((#p)->FMGL(id)))")
        public def isPrimary(p:Place):Boolean = true;

        @Native("c++", "::x10aux::is_cuda((x10_int)((#p)->FMGL(id)))")
        public def isCUDA(p:Place):Boolean = false;

        @Native("c++", "::x10::lang::Place::_make(::x10aux::parent((x10_int)((#p)->FMGL(id))))")
        public def getParent(p:Place):Place = p;

        @Native("c++", "((x10_long)::x10aux::num_children((x10_int)((#p)->FMGL(id))))")
        public def numChildren(p:Place):Long = 0;

        public def children(p:Place):Iterable[Place] {
            return new Rail[Place](numChildren(p), (i:long)=>getChild(p, i));
        }

        @Native("c++", "x10::lang::Place::_make(((x10_long)::x10aux::child((x10_int)((#p)->FMGL(id)),(x10_int)(#c))))")
        public def getChild(p:Place, c:Long):Place {
            throw new BadPlaceException(p+" has no children");
        }

        @Native("c++", "((x10_long)::x10aux::child_index((x10_int)((#c)->FMGL(id))))")
        public def childIndex(c:Place):Long {
            throw new BadPlaceException(c+" is not a child Place");
        }
    }
}
