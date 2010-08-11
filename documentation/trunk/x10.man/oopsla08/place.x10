/**

 * This class implements the notion of places in X10. The maximum
 * number of places is determined by a configuration parameter
 * (MAX_PLACES). Each place is indexed by a nat, from 0 to MAX_PLACES;
 * thus there are MAX_PLACES+1 places. This ensures that there is
 * always at least 1 place, the 0'th place.

 * We use a dependent parameter to ensure that the compiler can track
 * indices for places.
 *
 * Note that place(i), for i <= MAX_PLACES, can now be used as a non-empty type.
 * Thus it is possible to run an async at another place, without using arays---
 * just use async(place(i)) {...} for an appropriate i.

 * @author Christoph von Praun
 * @author vj
 */

package x10.lang;

import x10.util.List;
import x10.util.Set;

public value class place (nat i : i <= MAX_PLACES){

    /** The number of places in this run of the system. Set on
     * initialization, through the command line/init parameters file.
     */
    config nat MAX_PLACES;

    // Create this array at the very beginning.
    private constant place value [] myPlaces = new place[MAX_PLACES+1] fun place (int i) {
	return new place( i )(); };

    /** The last place in this program execution.
     */
    public static final place LAST_PLACE = myPlaces[MAX_PLACES];

    /** The first place in this program execution.
     */
    public static final place FIRST_PLACE = myPlaces[0];
    public static final Set<place> places = makeSet( MAX_PLACES );

    /** Returns the set of places from first place to last place.
     */
    public static Set<place> makeSet( nat lastPlace ) {
	Set<place> result = new Set<place>();
	for ( int i : 0 .. lastPlace ) {
	    result.add( myPlaces[i] );
	}
	return result;
    }

    /**  Return the current place for this activity.
     */
    public static place here() {
	return activity.currentActivity().place();
    }

    /** Returns the next place, using modular arithmetic. Thus the
     * next place for the last place is the first place.
     */
    public place(i+1 % MAX_PLACES) next()  { return next( 1 ); }

    /** Returns the previous place, using modular arithmetic. Thus the
     * previous place for the first place is the last place.
     */
    public place(i-1 % MAX_PLACES) prev()  { return next( -1 ); }

    /** Returns the k'th next place, using modular arithmetic. k may
     * be negative.
     */
    public place(i+k % MAX_PLACES) next( int k ) {
	return places[ (i + k) % MAX_PLACES];
    }

    /**  Is this the first place?
     */
    public boolean isFirst() { return i==0; }

    /** Is this the last place?
     */
    public boolean isLast() { return i==MAX_PLACES; }
}
