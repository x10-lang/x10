/*
 * Created on Oct 3, 2004
 */
package x10.array;

import x10.array.Place;

/**
 * Interface for all Distributions.
 * 
 * @author Christoph von Praun
 * @author Christian Grothoff
 */
public interface Distribution extends Region {

	/**
	 * Get the Place of the given point in the Region of this Distribution.
	 * 
	 * @param point
	 * @return the place to which the given point maps in this Distribution
	 */
	public Place getPlaceOf(int[] point);

	public Distribution intersect(Distribution d);

	public Distribution asymetricUnion(Distribution d);

	public Distribution disjointUnion(Distribution d);

	public Distribution difference(Distribution d);

	/*
	 * The name in the X10-Spec is misleading - there it is called range
	 * restriction, but it is actually a restiction to the elements at a certain
	 * place.
	 */
	public Distribution placeRestriction(Place r);

	public Distribution domainRestriction(Region r);

} // end of Distribution
