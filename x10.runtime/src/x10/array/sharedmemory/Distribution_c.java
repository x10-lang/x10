/*
 * Created on Oct 3, 2004
 */
package x10.array.sharedmemory;

import x10.array.Distribution;
import x10.array.Place;
import x10.array.Range;
import x10.array.ContiguousRange;
import x10.array.Region;


/**
 * Implementation of Distributions.
 * 
 * @author Christoph von Praun
 * @author Christian Grothoff
 */
abstract class Distribution_c extends Region_c implements Distribution {

    // First: static factory methods for the use by the Runtime implemenation
    
    /**
     * Create a Distribution where the given Region is distributed
     * into blocks the specified places
     * @param r Region
     * @param q The set of Places
     * @return
     */
	static Distribution_c makeBlock(Region_c r, Place[] q) {
	    return makeBlock(r, q.length, q);
    }
    
    /**
     * Create a Distribution where the given Region is distributed
     * into blocks over the first n Places.
     * @param r
     * @return
     */
	static Distribution_c makeBlock(Region_c r, int n, Place[] q) {
        assert n <= q.length;
        
        Distribution[] dists = new Distribution[n];
        for (int i=0; i < n; i++) 
            dists[i] = new Distribution_c.Constant(r.sub(n, i), q[i]);
        return new Distribution_c.Combined(r, dists);
    }
    
    /**
     * Create a Distribution where the elements in the region are
     * distributed over all Places in p in a cyclic manner,
     * that is the next point in the region is at the next place
     * for a cyclic ordering of the given places.
     * @param r
     * @return
     */
    static Distribution_c makeCyclic(Region_c r, Place[] q) {
        return makeBlockCyclic(r, q.length, q);
    }
    
    /**
     * Create a Distribution where the elements in the region are
     * distributed over all Places in p in a cyclic manner,
     * that is the next point in the region is at the next place
     * for a cyclic ordering of the given places.
     * @param r
     * @return
     */
    static Distribution_c makeBlockCyclic(Region_c r, int n, Place[] q) {
        assert n > 0;
        int rs = r.size();
        int pt = rs / n;
        int qt = rs % n;
        if (qt != 0)
            pt++;
        int ql = q.length;
        Distribution[] dists = new Distribution[pt];
        for (int i=0;i<pt;i++) {
            int s = i*n;
            int e = s + n;
            if (e > rs)
                e = rs;
            dists[i] = new Distribution_c.Constant(r.subOrdinal(s, e), 
                                                   q[i % ql]);
        }
        return new Distribution_c.Combined(r, dists);
    }
    
    /**
     * Create a Distribution where the points of the Region are
     * distributed randomly over all available Places.
     * @param r
     * @return
     */
    static Distribution_c makeArbitrary(Region_c r, Place[] p) {
        return makeBlockCyclic(r, 32, p);
    }
    
    /**
     * Create a Distribution where all points in the given
     * Region are mapped to the same Place.
     * @param r
     * @param p specifically use the given place for all points
     * @return
     */
    static Distribution_c makeConstant(Region_c r, Place p) {
        return new Distribution_c.Constant(r, p);
    }
    
    /**
     * Create a Distribution where the points in the
     * region 1...p.length are mapped to the respective
     * places.
     * @param p the list of places (implicitly defines the region)
     * @return
     */
    static Distribution_c makeUnique(Place[] p) {
        return new Distribution_c.Unique(p);
    }

    Distribution_c(Region_c r) {
        super(r);
    }
    
    public Distribution asymetricUnion(Distribution d) { 
        throw new Error("TODO");
    }
    
    public Distribution placeRestriction(Place r) { 
        throw new Error("TODO");
    } // change X10 report to call it place_restriction and I'll be happier
    
    public Distribution domainRestriction(Region r) { 
        throw new Error("TODO");
    }

    static final class Constant extends Distribution_c {
        
        private final Place place_;

        Constant(Region_c r, Place p) {
            super(r);
            this.place_ = p;
        }
        
        public Place placeOf(int[] point) { 
            return place_;
        }
        
    } // end of Distribution_c.Constant
    
    static class Unique extends Distribution_c {
        
        private final Place[] places_;
        
        Unique(Place[] p) {
            super(new Region_c(new Range[] { new ContiguousRange(1, p.length) }));
            this.places_ = p;
        }
        
        public Place placeOf(int[] point) { 
            assert point.length == 1;
            assert contains(point);
            return places_[point[0]-1];
        }
        
    } // end of Distribution_c.Unique
    
    static class Combined extends Distribution_c {
        
        private final Distribution[] members_;
        
        Combined(Region_c r, Distribution[] members_) {
            super(r);
            assert members_ != null;
            this.members_ = members_;
        }
        
        public Place placeOf(int[] point) {
            Place ret = null;
            for (int i=0; ret == null && i < members_.length; ++i) {
                if (members_[i].contains(point)) 
                    ret = members_[i].placeOf(point);
            }
            assert ret != null;
            return ret;
        }
        
    } // end of Distribution_c.Combined
    
} // end of Distribution_c