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
 */
public final value Place(id: int) {
    public const MAX_PLACES = x10.runtime.NativeRuntime.MAX_PLACES;
    public const places = Rail.makeVal[Place](MAX_PLACES, ((id: int) => new Place(id)));
    public const FIRST_PLACE: Place(0) = places(0) as Place(0);

    public def this(id: int):Place{self.id==id} {
	property(id);
    }
	
    public static def place(id: int) = places(id);

    public def next(): Place = next(1);

    public def prev(): Place = next(-1);

    public def prev(i: int): Place = next(-i);
    
    public def next(i: int): Place {
        // -1 % n == -1, not n-1, so need to add n
        val k = (id + i % MAX_PLACES + MAX_PLACES) % MAX_PLACES;
        return place(k);
	}
	
	public def isFirst(): boolean = id == 0;

	public def isLast(): boolean = id == MAX_PLACES - 1;

    public def toString() = "(Place " + id + ")";
}    
