/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

/**
 * @author Christian Grothoff
 * @author Raj Barik, Vivek Sarkar
 * @author tardieu
 * @author vj
 * @author Dave Cunningham
 */
public final struct Place(id: Int) implements Equals {
    public const MAX_PLACES = Runtime.MAX_HOSTS;
    public const places = ValRail.make[Place](MAX_PLACES, ((id: Int) => Place(id)));
    public const children = ValRail.make[ValRail[Place]](
                                Runtime.MAX_PLACES,
                                (p: Int) => ValRail.make[Place](Runtime.numChildren(p),
                                                                (i:Int) => Place(Runtime.child(p,i))));
    public const NUM_ACCELS = Runtime.MAX_PLACES - Runtime.MAX_HOSTS;
    public const FIRST_PLACE: Place(0) = places(0) as Place(0);

    public def this(id: Int):Place(id) { property(id); }

    public static def place(id: Int): Place(id) = places(id) as Place(id);
    public def next(): Place = next(1);
    public def prev(): Place = next(-1);
    public def prev(i: Int): Place = next(-i);
    public def next(i: Int): Place {
        // -1 % n == -1, not n-1, so need to add n
        if (Runtime.isHost(id)) {
            val k = (id + i % MAX_PLACES + MAX_PLACES) % MAX_PLACES;
            return place(k);
        }
        // FIXME: iterate through peers
        return this;
	}

	public def isFirst(): Boolean = id == 0;
	public def isLast(): Boolean = id == MAX_PLACES - 1;

	public def isHost(): Boolean = Runtime.isHost(id);
	public def isSPE(): Boolean = Runtime.isSPE(id);
	public def isCUDA(): Boolean = Runtime.isCUDA(id);

    public def numChildren() = Runtime.numChildren(id);
    public def child(i:Int) = Place(Runtime.child(id,i));

    public def children() = children(id);

    public def parent() = places(Runtime.parent(id));

    public def childIndex() {
        if (isHost()) {
            throw new BadPlaceException();
        }
        return Runtime.childIndex(id);
    }

    public global safe def toString() = "(Place " + this.id + ")";
    public global safe def equals(p:Any) = p instanceof Place && (p as Place).id==this.id;
    public global safe def hashCode()=id;
}
