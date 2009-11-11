/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

import x10.runtime.NativeRuntime;

/**
 * @author Christian Grothoff
 * @author Raj Barik, Vivek Sarkar
 * @author tardieu
 * @author vj
 * @author Dave Cunningham
 */
public final struct Place(id: Int) {
    public const MAX_PLACES = NativeRuntime.MAX_HOSTS;
    public const places = Rail.makeVal[Place](MAX_PLACES, ((id: Int) => Place(id)));
    public const children = Rail.makeVal[ValRail[Place]](
                                NativeRuntime.MAX_PLACES,
                                (p: Int) => Rail.makeVal[Place](NativeRuntime.numChildren(p),
                                                                (i:Int) => Place(NativeRuntime.child(p,i))));
    public const FIRST_PLACE: Place(0) = places(0) as Place(0);

    public def this(id: Int):Place{self.id==id} {
        property(id);
    }
	
    public static def place(id: Int) = places(id);
    public def next(): Place = next(1);
    public def prev(): Place = next(-1);
    public def prev(i: Int): Place = next(-i);
    public def next(i: Int): Place {
        // -1 % n == -1, not n-1, so need to add n
        if (NativeRuntime.isHost(id)) {
            val k = (id + i % MAX_PLACES + MAX_PLACES) % MAX_PLACES;
            return place(k);
        }
        // FIXME: iterate through peers
        return this;
	}
	
	public def isFirst(): Boolean = id == 0;
	public def isLast(): Boolean = id == MAX_PLACES - 1;

	public def isHost(): Boolean = NativeRuntime.isHost(id);
	public def isSPE(): Boolean = NativeRuntime.isSPE(id);
	public def isCUDA(): Boolean = NativeRuntime.isCUDA(id);

    public def numChildren() = NativeRuntime.numChildren(id);
    public def child(i:Int) = Place(NativeRuntime.child(id,i));

    public def children() = children(id);

    public def parent() = places(NativeRuntime.parent(id));

    public def childIndex() {
        if (isHost()) {
            throw new BadPlaceException();
        }
        return NativeRuntime.childIndex(id);
    }

    public def toString() = "(Place " + id + ")";
    public def equals(p:Place) = this.id==p.id;
}    
