/*
 * Created on Oct 3, 2004
 */
package x10.array.sharedmemory;

import java.util.Iterator;

import x10.array.Distribution;
import x10.array.Place;
import x10.array.Range;
import x10.array.Region;


/**
 * Implementation of Distributions.
 * 
 * @author Christoph von Praun
 * @author Christian Grothoff
 */
abstract class Distribution_c implements Distribution {

    // First: static factory methods for the use by the Runtime implemenation
    
    /**
     * Create a Distribution where the given Region is distributed
     * into blocks the specified places
     * @param r
     * @param q
     * @return
     */
	static Distribution_c makeBlock(Region_c r, Place[] q) {
        int n = q.length;
        int size = r.size();
        int qt = size % n;
        int pt = size / n;
        Distribution[] dists = new Distribution[n];
        
        // these blocks obain p+1 elements
        for (int i=0;i<qt;i++) {
            dists[i] = new Distribution_c.Constant(r.subOrdinal(i*(pt+1), (i+1)*(pt+1)), 
                                                   q[i]);
        }
        int base = qt * (pt+1);
        
        // these blocks obtain p elements
        for (int i=qt;i<n;i++) {
            int off = (i-qt) * pt;
            dists[i] = new Distribution_c.Constant(r.subOrdinal(base+off, base+off+pt), 
                                                   q[i]);
        }
        return new Distribution_c.Combined(r, dists);
    }
    
    /**
     * Create a Distribution where the given Region is distributed
     * into blocks over the first N Places.
     * @param r
     * @return
     */
	static Distribution_c makeBlock(Region_c r, int n, Place[] q) {
        assert n <= q.length;
        int qt = r.size() % n;
        int pt = r.size() / n;
        Distribution[] dists = new Distribution[n];
        for (int i=0;i<qt;i++) {
            dists[i] = new Distribution_c.Constant(r.subOrdinal(i*(pt+1), (i+1)*(pt+1)), 
                                                   q[i]);
        }
        int base = qt * (pt+1);
        for (int i=qt;i<n;i++) {
            int off = (i-qt) * pt;
            dists[i] = new Distribution_c.Constant(((Region_c)r).subOrdinal(base+off, base+off+pt), 
                                                   q[i]);
        }
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
        int n = r.size();
        Distribution[] dists = new Distribution[n];
        for (int i=0;i<n;i++) {
            dists[i] = new Distribution_c.Constant(r.subOrdinal(i,i+1), 
                                                   q[i % q.length]);
        }
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
    
    
    // Actual Distribution implementation(s)
    
    final Region_c region_;
    
    Distribution_c(Region_c r) {
        this.region_ = r;
    }
    
    // Region interface (fascade style)
    
    public int rank() {
        return region_.rank();
    }
    
    public Region sub(Range[] dims) {
        return region_.sub(dims);
    }
    
    public Region subOrdinal(int i, int j) {
        return region_.subOrdinal(i, j);
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

    public Distribution asymetricUnion(Distribution d) { 
        throw new Error("TODO");
    }
    
    public Distribution disjointUnion(Distribution d) { 
        throw new Error("TODO");
    }
    
    public Distribution difference(Distribution d) { 
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
        
        public Place getPlaceOf(int[] point) { 
            return place_;
        }
        
    } // end of Distribution_c.Constant
    
    static final class ConstantHere extends Distribution_c {
        
        ConstantHere(Region_c r) {
            super(r);
        }

        public Place getPlaceOf(int[] point){ 
            return x10.lang.Runtime.here();
        }
        
    } // end of Distribution_c.ConstantHere

    
    static class Unique extends Distribution_c {
        
        private final Place[] places_;
        
        Unique(Place[] p) {
            super(new Region_c(new Range[] { new Range(1, p.length) }));
            this.places_ = p;
        }
        
        public Place getPlaceOf(int[] point){ 
            assert point.length == 1;
            assert contains(point);
            return places_[point[0]-1];
        }
        
    } // end of Distribution_c.Unique
    
    static class Combined extends Distribution_c {
        
        private final Distribution[] members_;
        
        Combined(Region_c r, Distribution[] members_) {
            super(r);
            this.members_ = members_;
        }
        
        public Place getPlaceOf(int[] point){
            for (int i=members_.length-1;i>=0;i--)
                if (members_[i].contains(point))
                    return members_[i].getPlaceOf(point);
            assert false;
            throw new Error("This should never happen.");
        }
        
    } // end of Distribution_c.Unique

} // end of Distribution_c