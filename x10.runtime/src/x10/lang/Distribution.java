/*
 * Created on Oct 3, 2004
 */
package x10.lang;

/**
 * Interface for all Distributions. 
 * 
 * @author Christoph von Praun
 * @author Christian Grothoff
 */
public abstract class Distribution implements TypeArgument {

    public final Region region;
    
    protected Distribution(Region reg) {
        this.region = reg;
    }
    
    /**
     * Get the Place of the given point in the Region of this
     * Distribution.  This function can only be used for one-dimensional
     * Regions.  We use this function to avoid an allocation of the
     * form "new int[] { point }" for this common case.
     *   
     * @param point 
     * @return the place to which the given point maps in this Distribution
     */
    public abstract Place getPlaceOf(int point);
   
    
    /**
     * Get the Place of the given point in the Region of this
     * Distribution. 
     *   
     * @param point 
     * @return the place to which the given point maps in this Distribution
     */
    public abstract Place getPlaceOf(int[] point);

    public abstract Distribution intersect(Distribution d);

    public abstract Distribution asymmetric_union(Distribution d);
    
    public abstract Distribution disjoint_union(Distribution d);
    
    public abstract Distribution difference(Distribution d);
    
    public abstract Distribution range_restriction(Place r); // change X10 report to call it place_restriction and I'll be happier
    
    public abstract Distribution domain_restriction(Region r);
    
    /**
     * A distribution factory is a class that can create new Distributions.
     * (See 10.2.1).
     * 
     * @author Christian Grothoff
     */
    public interface Factory {
        
        /**
         * Create a Distribution where the given Region is distributed
         * into blocks over all available Places.
         * @param r
         * @return
         */
        public Distribution block(Region r, Place[] p);
        
        /**
         * Create a Distribution where the given Region is distributed
         * into blocks over all available Places.
         * @param r
         * @return
         */
        public Distribution block(Region r);
        
        /**
         * Create a Distribution where the given Region is distributed
         * into blocks of size n over all available Places.
         * @param r
         * @return
         */
        public Distribution block(Region r, int n, Place[] p);
        
        /**
         * Create a Distribution where the given Region is distributed
         * into blocks of size n over all available Places.
         * @param r
         * @return
         */
        public Distribution block(Region r, int n);
        
        /**
         * Create a Distribution where the elements in the region are
         * distributed over all Places in p in a cyclic manner,
         * that is the next point in the region is at the next place
         * for a cyclic ordering of the given places.
         * @param r
         * @return
         */
        public Distribution cyclic(Region r, Place[] p);
        
        /**
         * Create a Distribution where the elements in the region are
         * distributed over all Places in p in a cyclic manner,
         * that is the next point in the region is at the next place
         * for a cyclic ordering of the given places.
         * @param r
         * @return
         */
        public Distribution cyclic(Region r);
        
        /**
         * Create a Distribution where the elements in the region are
         * distributed over all Places in p in a cyclic manner,
         * that is the next point in the region is at the next place
         * for a cyclic ordering of the given places.
         * @param r
         * @return
         */
        public Distribution blockCyclic(Region r, int n, Place[] p);
        
        /**
         * Create a Distribution where the elements in the region are
         * distributed over all Places in p in a cyclic manner,
         * that is the next point in the region is at the next place
         * for a cyclic ordering of the given places.
         * @param r
         * @return
         */
        public Distribution blockCyclic(Region r, int n);
        
        /**
         * Create a Distribution where the points of the Region are
         * distributed randomly over all available Places.
         * @param r
         * @return
         */
        public Distribution arbitrary(Region r, Place[] p);
        
        /**
         * Create a Distribution where the points of the Region are
         * distributed randomly over all available Places.
         * @param r
         * @return
         */
        public Distribution arbitrary(Region r);
        
        /**
         * Create a Distribution where all points in the given
         * Region are mapped to the same Place.
         * @param r
         * @param p specifically use the given place for all points
         * @return
         */
        public Distribution constant(Region r, Place p);
        
        /**
         * Create a Distribution where all points in the given
         * Region are mapped to the same Place.
         * @param r
         * @return
         */
        public Distribution constant(Region r);
        
        /**
         * Create a Distribution where the points in the
         * region 1...p.length are mapped to the respective
         * places.
         * @param p the list of places (implicitly defines the region)
         * @return
         */
        public Distribution unique(Place[] p);
        
        /**
         * Create a Distribution where the points in the
         * region 1...p.length are mapped to the respective
         * places.
         * @return
         */
        public Distribution unique();
        
    } // end of Distribution.Factory    
    
} // end of Distribution
