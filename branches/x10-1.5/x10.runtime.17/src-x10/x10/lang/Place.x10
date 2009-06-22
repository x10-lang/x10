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
 */
public final value Place(id: nat) {
    public const MAX_PLACES = x10.runtime.NativeRuntime.MAX_PLACES;
    public const places = Rail.makeVal[Place](MAX_PLACES, ((id: nat) => new Place(id)));
    public const FIRST_PLACE = place(0);

	public def this(id: nat) = property(id);
	
	public static def place(id: nat): Place = places(id);

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
