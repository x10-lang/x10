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

/**
 * @author Christian Grothoff
 * @author Raj Barik, Vivek Sarkar
 * @author tardieu
 * @author vj
 * @author Dave Cunningham
 */
public final struct Place(id: Int)  {

    @Native("java", "x10.runtime.impl.java.Runtime.MAX_PLACES")
    @Native("c++", "x10aux::num_places")
    public const ALL_PLACES = 4;

    @Native("java", "x10.runtime.impl.java.Runtime.MAX_PLACES")
    @Native("c++", "x10aux::num_hosts")
    public const MAX_PLACES = 4;

    /**
     * Find number of children under a place.
     * For hosts, this returns the number of accelerators at that host.
     * For accelerators, it returns 0.
     */
    @Native("c++", "x10aux::num_children(#1)")
    public static def numChildren(id:Int) = 0;

    /**
     * Returns whether a place is a host.
     */
    @Native("c++", "x10aux::is_host(#1)")
    public static def isHost(id:Int) = true;

    /**
     * Returns whether a place is an SPE of a Cell CPU.
     */
    @Native("c++", "x10aux::is_spe(#1)")
    public static def isSPE(id:Int) = false;

    /**
     * Returns whether a place is a CUDA GPU.
     */
    @Native("c++", "x10aux::is_cuda(#1)")
    public static def isCUDA(id:Int) = false;

    /**
     * Find parent of a place.
     * For hosts, this returns the host itself.
     * For accelerators, it is the host of the accelerator.
     */
    @Native("c++", "x10aux::parent(#1)")
    public static def parent(id:Int) = id;

    /**
     * Iterate over the children of a place.
     * Use i between 0 and numChildren(p)-1 inclusive.
     * Throws BadPlaceException if i invalid.
     */
    @Native("c++", "x10aux::child(#1,#2)")
    public static def child(p:Int, i:Int):Int { throw new BadPlaceException(); }

    /**
     * Return the index of a given child, within a place.
     * Throws BadPlaceException if given place is not a child.
     */
    @Native("c++", "x10aux::child_index(#1)")
    public static def childIndex(id:Int):Int { throw new BadPlaceException(); }

    public const places = ValRail.make[Place](MAX_PLACES, ((id: Int) => Place(id)));
    public const children = ValRail.make[ValRail[Place]](
                                ALL_PLACES,
                                (p: Int) => ValRail.make[Place](numChildren(p),
                                                                (i:Int) => Place(child(p,i))));
    public const NUM_ACCELS = ALL_PLACES - MAX_PLACES;
    public const FIRST_PLACE: Place(0) = places(0) as Place(0);

    public def this(id: Int):Place(id) { property(id); }

    public static def place(id: Int): Place(id) = Place(id);
    public def next(): Place = next(1);
    public def prev(): Place = next(-1);
    public def prev(i: Int): Place = next(-i);
    public def next(i: Int): Place {
        // -1 % n == -1, not n-1, so need to add n
        if (isHost(id)) {
            val k = (id + i % MAX_PLACES + MAX_PLACES) % MAX_PLACES;
            return place(k);
        }
        // FIXME: iterate through peers
        return this;
	}

	public def isFirst(): Boolean = id == 0;
	public def isLast(): Boolean = id == MAX_PLACES - 1;

	public def isHost(): Boolean = isHost(id);
	public def isSPE(): Boolean = isSPE(id);
	public def isCUDA(): Boolean = isCUDA(id);

    public def numChildren() = numChildren(id);
    public def child(i:Int) = Place(child(id,i));

    public def children() = children(id);

    public def parent() = places(parent(id));

    public def childIndex() {
        if (isHost()) {
            throw new BadPlaceException();
        }
        return childIndex(id);
    }

    public global safe def toString() = "(Place " + this.id + ")";
    public global safe def equals(p:Any) = p instanceof Place && (p as Place).id==this.id;
    public global safe def hashCode()=id;
}
