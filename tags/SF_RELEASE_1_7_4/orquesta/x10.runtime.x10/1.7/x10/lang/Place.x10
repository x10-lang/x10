package x10.lang;

/**
 * This class implements the notion of places in X10. The maximum
 * number of places is determined by a configuration parameter
 * (MAX_PLACES). Each place is indexed by a nat, from 0 to MAX_PLACES;
 * thus there are MAX_PLACES+1 places. This ensures that there is
 * always at least 1 place, the 0'th place.

 * <p>We use a dependent parameter to ensure that the compiler can track
 * indices for places.
 * 
 * <p>Note that place(i), for i =< MAX_PLACES, can now be used as a non-empty type.

 * <p>Thus it is possible to run an async at another place, without
 * using arays --- just use async(place(i)) {...} for an appropriate
 * i.

 * @author vj 06/12/08
 */

public abstract value Place(id: Nat) {
    // Implementation Notes:

    // The runtime subclasses Places, and creates instances of it,
    // using invocation time information to determine the total number
    // of places. The runtime must ensure that it creates all and
    // exactly the places 0, 1, ... number of places -1.  It stores
    // these in a ValRail that can be retrieved through
    // Runtime.runtime.places().

    // Any attempt by user code to create places (e.g. by subclassing
    // Place and invoking a constructor) should throw an error.

    protected def this(i: Nat) = {
	// if (Runtime.runtime.initialized()) throw new InvalidUseException();
	property(i);
    }
    
    /**
       Returns the place at which the activity invoking this method is executing.
     */
    public static def here(): Place = Runtime.runtime.here(); 

    // Cannot store the ValRail of all places obtained from the
    // runtime in static state because we run into circular static
    // initialization issues.

    public static def maxPlaces() = places().length;
    public static def first() = places()(0);
    public static def last() = places()(length-1);
    public static def places(f: (nat)=>boolean) = places().filter(f);
    public static def places()=Runtime.runtime.places();

    public def next(k :int) = places(k+id % places.length);
    public def next()=next(1);
    public def prev()=prev(1);
    public def prev(k :int) = next(-k);
    public def isLast() = this.id==maxPlaces();

}
