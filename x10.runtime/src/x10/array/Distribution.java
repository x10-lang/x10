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
     * Get the Place of the given point in the Region of this
     * Distribution. 
     *   
     * @param point 
     * @return the place to which the given point maps in this Distribution
     */
    public Place getPlaceOf(int[] point);

    public Distribution intersect(Distribution d);

    public Distribution asymmetric_union(Distribution d);
    
    public Distribution disjoint_union(Distribution d);
    
    public Distribution difference(Distribution d);
    
    public Distribution range_restriction(Place r); // change X10 report to call it place_restriction and I'll be happier
    
    public Distribution domain_restriction(Region r);
    
} // end of Distribution
