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

 * @author vj
 */
 
package x10.lang;


import /*x10*/java.util.Set;

public /*value*/ class place /*(nat i : i =< MAX_PLACES)*/ {
    public final /*nat*/long id;

    /** The number of places in this run of the system. Set on
     * initialization, through the command line/init parameters file.
     * is initialized by a specific runtime, e.g. x10.runtime.DefaultRuntime_c
     */
    /*config*/ public static /*final*/ /*nat*/ long MAX_PLACES = 10;
   

    protected place( /*nat*/ long id) {
     this.id = id;
    }

    public static abstract /*value*/ class factory {
	/**  Return the place numbered i, using modulo MAX_PLACES
	 *  arithmetic.
	 */
	abstract public place/*(i % MAX_PLACES)*/ place( final /*nat*/long i );

	/** Return the set of places from 0 to last. */
	abstract public Set/*<place>*/ places( /*nat*/long last);

	/** Return the place of the current activity.
	 */
	abstract public place here();
    }
   public static final factory factory = Runtime.factory.getPlaceFactory();

    /** The last place in this program execution.
     */
    public static final place/*(MAX_PLACES)*/ LAST_PLACE = factory.place(MAX_PLACES);

    /** The first place in this program execution.
     */
    public static final place/*(0)*/ FIRST_PLACE = factory.place(0);
    public static final Set/*<place>*/ places = factory.places( MAX_PLACES );	


    /** Returns the next place, using modular arithmetic. Thus the
     * next place for the last place is the first place. 
     */
    public place/*(id+1 % MAX_PLACES)*/ next()  { 
    	final place result = next(1);
    	assert result.id == (id+1 % MAX_PLACES);
    	return result; 
    }

    /** Returns the previous place, using modular arithmetic. Thus the
     * previous place for the first place is the last place. 
     */
    public place/*(i-1 % MAX_PLACES)*/ prev()  { 
    	final place result = next( -1 );
    	assert result.id == (id-1 % MAX_PLACES);
    	return result; 
    }


    /** Returns the k'th next place, using modular arithmetic. k may
     * be negative.
     */
    public place/*(i+k % MAX_PLACES)*/ next( final int k ) {
	final place result = factory.place( (id + k) % MAX_PLACES);
	assert result.id == (id+k % MAX_PLACES);
	return result; 
    }

    /**  Is this the first place?
     */
    public boolean isFirst() { 
	return id==0;
    }

    /** Is this the last place?
     */
    public boolean isLast() { 
	return id==MAX_PLACES -1; 
    }
}
