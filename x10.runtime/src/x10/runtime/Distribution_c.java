/*
 * Created on Oct 3, 2004
 */
package x10.runtime;

import x10.lang.Distribution;
import x10.lang.Place;
import x10.lang.Region;
import x10.lang.X10Object;


/**
 * Implementation of Distributions.
 * 
 * @author Christoph von Praun
 * @author Christian Grothoff
 */
abstract class Distribution_c extends Distribution {

    Distribution_c(Region r) {
        super(r);
    }
    
    /**
     * Rough idea: Array has a method 'get(point)'.  Array_c implements that
     * by calling distribution.get(this, point).  The Distrib then finds the
     * right place and (possibly using force future) calls at the right 
     * place the "Array_c.getInternal(point)", which is GUARANTEED to be
     * called only if point resides here, so the getInternal knows that the
     * given point is in its local range.
     *   
     * @param a
     * @param point
     * @return
     */
    abstract X10Object getValueAt(Array_c a, int[] point);
    
    /**
     * Rough idea: Array has a method 'set(point,val)'.  Array_c implements that
     * by calling distribution.set(this, point, val).  The Distrib then finds the
     * right place and (possibly using force future) calls at the right 
     * place the "Array_c.getInternal(point)", which is GUARANTEED to be
     * called only if point resides here, so the getInternal knows that the
     * given point is in its local range.
     *   
     * @param a
     * @param point
     * @return
     */
    abstract void setValueAt(Array_c a, int[] point, X10Object val);
    
    
    
    
    /**
     * Get the Place of the given point in the Region of this
     * Distribution.  This function can only be used for one-dimensional
     * Regions.  We use this function to avoid an allocation of the
     * form "new int[] { point }" for this common case.
     *   
     * @param point 
     * @return the place to which the given point maps in this Distribution
     */
    public Place getPlaceOf(int point) { 
        throw new Error("TODO");
    }
    
    /**
     * Get the Place of the given point in the Region of this
     * Distribution. 
     *   
     * @param point 
     * @return the place to which the given point maps in this Distribution
     */
    public Place getPlaceOf(int[] point){ 
        throw new Error("TODO");
    }

    public Distribution intersect(Distribution d){ 
        throw new Error("TODO");
    }

    public Distribution asymmetric_union(Distribution d){ 
        throw new Error("TODO");
    }
    
    public Distribution disjoint_union(Distribution d){ 
        throw new Error("TODO");
    }
    
    public Distribution difference(Distribution d){ 
        throw new Error("TODO");
    }
    
    public Distribution range_restriction(Place r){ 
        throw new Error("TODO");
    } // change X10 report to call it place_restriction and I'll be happier
    
    public Distribution domain_restriction(Region r){ 
        throw new Error("TODO");
    }

    static class Factory_c implements Distribution.Factory {

        /**
         * Create a Distribution where the given Region is distributed
         * into blocks over all available Places.
         * @param r
         * @return
         */
        public Distribution block(Region r, Place[] p){
            throw new Error("TODO");
        }
        
        /**
         * Create a Distribution where the given Region is distributed
         * into blocks over all available Places.
         * @param r
         * @return
         */
        public Distribution block(Region r){
            throw new Error("TODO");
        }
        
        /**
         * Create a Distribution where the given Region is distributed
         * into blocks of size n over all available Places.
         * @param r
         * @return
         */
        public Distribution block(Region r, int n, Place[] p){
            throw new Error("TODO");
        }
        
        /**
         * Create a Distribution where the given Region is distributed
         * into blocks of size n over all available Places.
         * @param r
         * @return
         */
        public Distribution block(Region r, int n){
            throw new Error("TODO");
        }
        
        /**
         * Create a Distribution where the elements in the region are
         * distributed over all Places in p in a cyclic manner,
         * that is the next point in the region is at the next place
         * for a cyclic ordering of the given places.
         * @param r
         * @return
         */
        public Distribution cyclic(Region r, Place[] p){
            throw new Error("TODO");
        }
        
        /**
         * Create a Distribution where the elements in the region are
         * distributed over all Places in p in a cyclic manner,
         * that is the next point in the region is at the next place
         * for a cyclic ordering of the given places.
         * @param r
         * @return
         */
        public Distribution cyclic(Region r){
            throw new Error("TODO");
        }
        
        /**
         * Create a Distribution where the elements in the region are
         * distributed over all Places in p in a cyclic manner,
         * that is the next point in the region is at the next place
         * for a cyclic ordering of the given places.
         * @param r
         * @return
         */
        public Distribution blockCyclic(Region r, int n, Place[] p){
            throw new Error("TODO");
        }
        
        /**
         * Create a Distribution where the elements in the region are
         * distributed over all Places in p in a cyclic manner,
         * that is the next point in the region is at the next place
         * for a cyclic ordering of the given places.
         * @param r
         * @return
         */
        public Distribution blockCyclic(Region r, int n){
            throw new Error("TODO");
        }
        
        /**
         * Create a Distribution where the points of the Region are
         * distributed randomly over all available Places.
         * @param r
         * @return
         */
        public Distribution arbitrary(Region r, Place[] p){
            throw new Error("TODO");
        }
        
        /**
         * Create a Distribution where the points of the Region are
         * distributed randomly over all available Places.
         * @param r
         * @return
         */
        public Distribution arbitrary(Region r){
            throw new Error("TODO");
        }
        
        /**
         * Create a Distribution where all points in the given
         * Region are mapped to the same Place.
         * @param r
         * @param p specifically use the given place for all points
         * @return
         */
        public Distribution constant(Region r, Place p){
            throw new Error("TODO");
        }
        
        /**
         * Create a Distribution where all points in the given
         * Region are mapped to the same Place.
         * @param r
         * @return
         */
        public Distribution constant(Region r){
            throw new Error("TODO");
        }
        
        /**
         * Create a Distribution where the points in the
         * region 1...p.length are mapped to the respective
         * places.
         * @param p the list of places (implicitly defines the region)
         * @return
         */
        public Distribution unique(Place[] p){
            throw new Error("TODO");
        }
        
        /**
         * Create a Distribution where the points in the
         * region 1...p.length are mapped to the respective
         * places.
         * @return
         */
        public Distribution unique() {
            throw new Error("TODO");
        }

        
    } // end of Distribution_c.Factory_c
    
} // end of Distribution_c