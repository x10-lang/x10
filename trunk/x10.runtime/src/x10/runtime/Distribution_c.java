/*4
 * Created on Oct 3, 2004
 */
package x10.runtime;

import java.util.Iterator;

import x10.lang.Distribution;
import x10.lang.Place;
import x10.lang.Range;
import x10.lang.Region;
import x10.lang.X10Object;


/**
 * Implementation of Distributions.
 * 
 * @author Christoph von Praun
 * @author Christian Grothoff
 */
abstract class Distribution_c 
    implements Distribution {

    final Region region_;
    
    Distribution_c(Region r) {
        this.region_ = r;
    }
    
    // Region interface (fascade style)
    
    public int rank() {
        return region_.rank();
    }
    
    public Region sub(Range[] dims) {
        return region_.sub(dims);
    }
    
    public Region combine(Region r) {
        return region_.combine(r);
    }
    
    public Range dim(int i) {
        return region_.dim(i);
    }
    
    public boolean contains(Region r) {
        return region_.contains(r);
    }
    
    public boolean contains(int[] p) {
        return region_.contains(p);
    }
    
    public int ordinal(int[] p) {
        return region_.ordinal(p);
    }
    
    public Iterator iterator() {
        return region_.iterator();
    }

    // Actual Distribution code...
    

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
    X10Object getValueAt(Array_c a, int[] point) {
        Place p = getPlaceOf(point);
        if (p == x10.lang.Runtime.here())
            return a.getInternal(region_.ordinal(point));
        else
            throw new Error("not implemented");
    }
    
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
    void setValueAt(Array_c a, int[] point, X10Object val) {
        Place p = getPlaceOf(point);
        if (p == x10.lang.Runtime.here())
            a.setInternal(region_.ordinal(point), val);
        else
            throw new Error("not implemented");
    }
    

    public Distribution intersect(Distribution d) { 
        throw new Error("TODO");
    }

    public Distribution asymmetric_union(Distribution d) { 
        throw new Error("TODO");
    }
    
    public Distribution disjoint_union(Distribution d) { 
        throw new Error("TODO");
    }
    
    public Distribution difference(Distribution d) { 
        throw new Error("TODO");
    }
    
    public Distribution range_restriction(Place r) { 
        throw new Error("TODO");
    } // change X10 report to call it place_restriction and I'll be happier
    
    public Distribution domain_restriction(Region r) { 
        throw new Error("TODO");
    }

    static final class Constant extends Distribution_c {
        
        private final Place place_;

        Constant(Region r, Place p) {
            super(r);
            this.place_ = p;
        }
        
        public Place getPlaceOf(int[] point) { 
            return place_;
        }
        
    } // end of Distribution_c.Constant
    
    static final class ConstantHere extends Distribution_c {
        
        ConstantHere(Region r) {
            super(r);
        }

        X10Object getValueAt(Array_c a, int[] point) {
            return a.getInternal(region_.ordinal(point));
        }
        
        void setValueAt(Array_c a, int[] point, X10Object val) {
            a.setInternal(region_.ordinal(point), val);
        }        
        
        public Place getPlaceOf(int[] point){ 
            return x10.lang.Runtime.here();
        }
        
    } // end of Distribution_c.ConstantHere

    
    static class Unique extends Distribution_c {
        
        private final Place[] places_;
        
        Unique(Place[] p) {
            super(new Region_c(new Range[] { new Range_c(1, p.length) }));
            this.places_ = p;
        }
        
        public Place getPlaceOf(int[] point){ 
            assert point.length == 1;
            assert contains(point);
            return places_[point[0]-1];
        }
        
    } // end of Distribution_c.Unique
    
    
    
    
    
} // end of Distribution_c