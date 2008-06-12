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

public value place(id: nat) {
    native def this();

    /**
       Returns the place at which the activity invoking this method is executing.
     */
    public native static def maxPlaces():nat;
    public native static def here():place; 
    public native static def first():place;
    public native static def last():place;
    public native static def places(f :nat=>boolean):ValRail[place];

    public def next()=next(0);
    public native def next(k :int): place;
    public def prev()=prev(0);
    public native def prev(k :int): place;
    public def isLast() = this.id==maxPlaces();

}
