/*
 * Created on Oct 3, 2004
 */
package x10.runtime;

import java.util.Iterator;

import x10.lang.Distribution;
import x10.lang.Place;
import x10.lang.Range;
import x10.lang.Region;


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
    
    public int size() {
        return region_.size();
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