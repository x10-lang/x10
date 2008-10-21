/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
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


import java.util.Set;

import x10.base.TypeArgument;
import x10.runtime.Future_c;

public /*value*/ class place /*(nat i : i =< MAX_PLACES)*/ extends x10.lang.Object 
implements TypeArgument, ValueType{
    private static int count_ = 0;
    public final /*nat*/int id;
	
	/** The number of places in this run of the system. Set on
	 * initialization, through the command line/init parameters file.
	 * is initialized by a specific runtime, e.g. x10.runtime.DefaultRuntime_c
	 */
	/*config*/ public static /*final*/ /*nat*/ int MAX_PLACES = 10;
	
	
	protected place() {
        synchronized (place.class) {
            id = count_++;
        } 
	}
	
	public static abstract /*value*/ class factory implements ValueType {
		/**  Return the place numbered i, using modulo MAX_PLACES
		 *  arithmetic.
		 */
		abstract public place/*(i % MAX_PLACES)*/ place( final /*nat*/int i );
		
		/** Return the set of places from 0 to last. */
		abstract public Set/*<place>*/ places( /*nat*/int last);
		
		/** Return the place of the current activity.
		 */
		abstract public place here();
	}
	public static final factory factory = Runtime.factory.getPlaceFactory();
	
	/** The last place in this program execution.
	 */
	public static /*final*/ place/*(MAX_PLACES)*/ LAST_PLACE /*= factory.place(MAX_PLACES-1)*/;
	
	/** The first place in this program execution.
	 */
	public static /*final*/ place/*(0)*/ FIRST_PLACE /*= factory.place(0)*/;
	
	public static /*final*/ Set/*<place>*/ places /*= factory.places( MAX_PLACES-1 )*/;	
	public static place[] placeArray;
	/**
	 * Needs to be called after MAX_PLACES has its final value.
	 *
	 */
	public static void initialize() {
		LAST_PLACE = factory.place(MAX_PLACES-1);
		FIRST_PLACE = factory.place(0);
		places = factory.places(MAX_PLACES-1);
		placeArray = new place[MAX_PLACES];
		for (int i=0; i < MAX_PLACES; ++i) placeArray[i] = factory.place(i);
	}
	public static place places(int i) { return factory.place(i);}
	public boolean equals(java.lang.Object o) {
        boolean ret = false;
        if (o != null && o instanceof x10.lang.place) {
            x10.lang.place op = (x10.lang.place) o;
            ret = op.id == id;
        }
        return ret;
    }
    
	/** Returns the next place, using modular arithmetic. Thus the
	 * next place for the last place is the first place. 
	 */
	public place/*(id+1 % MAX_PLACES)*/ next()  { 
		final place result = next(1);
		// System.err.println("place:" + this + ".next()=" + result);
		return result; 
	}
	
	/** Returns the previous place, using modular arithmetic. Thus the
	 * previous place for the first place is the last place. 
	 */
	public place/*(i-1 % MAX_PLACES)*/ prev()  { 
		return next(-1); 
	}
	
	
	/** Returns the k'th next place, using modular arithmetic. k may
	 * be negative.
	 */
	public place/*(i+k % MAX_PLACES)*/ next( final int k ) {
		int index = ( id + k);
		while (index < 0) { index += MAX_PLACES; }
		final place result = factory.place( index % MAX_PLACES);
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
    
	public String toString() {
	    return "place(id=" + id +")"; 
	}

        // ahk ... find a way to remove the need for this
	public void runAsyncNoRemapping(x10.runtime.Activity a){throw new RuntimeException("Should never be called");}
	
	public void runAsync(x10.runtime.Activity a){throw new RuntimeException("Should never be called");}
	public  Future runFuture(Future_c.Activity a) {if(true) throw new RuntimeException("Should never be called"); return null;}

   
   
}
