/*
 * Created on Oct 3, 2004
 */
package x10.array;

import java.util.Iterator;
import x10.array.Place;


/**
 * Implementation of Distributions.
 * 
 * @author Christoph von Praun
 * @author Christian Grothoff
 */
abstract class Distribution_c 
    implements Distribution {
    
    // First: static factory methods for the use by the Runtime implemenation
    
    /**
     * Create a Distribution where the given Region is distributed
     * into blocks over all available Places.
     * @param r
     * @return
     */
	static Distribution makeBlock(Region R, Place[] Q) {
        int N = Q.length;
        int q = R.size() % N;
        int p = R.size() / N;
        Distribution[] dists = new Distribution[N];
        for (int i=0;i<q;i++) {
            dists[i] = new Distribution_c.Constant(((Region_c)R).subOrdinal(i*(p+1), (i+1)*(p+1)), 
                                                   Q[i]);
        }
        int base = q * (p+1);
        for (int i=q;i<N;i++) {
            int off = (i-q) * p;
            dists[i] = new Distribution_c.Constant(((Region_c)R).subOrdinal(base+off, base+off+p), 
                                                   Q[i]);
        }
        return new Distribution_c.Combined(R, dists);
    }
    
    /**
     * Create a Distribution where the given Region is distributed
     * into blocks over the first N Places.
     * @param r
     * @return
     */
	static Distribution makeBlock(Region R, int N, Place[] Q) {
        assert N <= Q.length;
        int q = R.size() % N;
        int p = R.size() / N;
        Distribution[] dists = new Distribution[N];
        for (int i=0;i<q;i++) {
            dists[i] = new Distribution_c.Constant(((Region_c)R).subOrdinal(i*(p+1), (i+1)*(p+1)), 
                                                   Q[i]);
        }
        int base = q * (p+1);
        for (int i=q;i<N;i++) {
            int off = (i-q) * p;
            dists[i] = new Distribution_c.Constant(((Region_c)R).subOrdinal(base+off, base+off+p), 
                                                   Q[i]);
        }
        return new Distribution_c.Combined(R, dists);
    }
    
    /**
     * Create a Distribution where the elements in the region are
     * distributed over all Places in p in a cyclic manner,
     * that is the next point in the region is at the next place
     * for a cyclic ordering of the given places.
     * @param r
     * @return
     */
    static Distribution makeCyclic(Region R, Place[] Q) {
        int N = R.size();
        Distribution[] dists = new Distribution[N];
        for (int i=0;i<N;i++) {
            dists[i] = new Distribution_c.Constant(((Region_c)R).subOrdinal(i,i+1), 
                                                   Q[i % Q.length]);
        }
        return new Distribution_c.Combined(R, dists);
    }
    
    /**
     * Create a Distribution where the elements in the region are
     * distributed over all Places in p in a cyclic manner,
     * that is the next point in the region is at the next place
     * for a cyclic ordering of the given places.
     * @param r
     * @return
     */
    static Distribution makeBlockCyclic(Region R, int N, Place[] Q) {
        assert N > 0;
        int RS = R.size();
        int p = RS / N;
        int q = RS % N;
        if (q != 0)
            p++;
        int QL = Q.length;
        Distribution[] dists = new Distribution[p];
        for (int i=0;i<p;i++) {
            int s = i*N;
            int e = s + N;
            if (e > RS)
                e = RS;
            dists[i] = new Distribution_c.Constant(((Region_c)R).subOrdinal(s, e), 
                                                   Q[i % QL]);
        }
        return new Distribution_c.Combined(R, dists);
    }
    
    /**
     * Create a Distribution where the points of the Region are
     * distributed randomly over all available Places.
     * @param r
     * @return
     */
    static Distribution makeArbitrary(Region r, Place[] p) {
        return makeBlockCyclic(r, 32, p);
    }
    
    /**
     * Create a Distribution where all points in the given
     * Region are mapped to the same Place.
     * @param r
     * @param p specifically use the given place for all points
     * @return
     */
    static Distribution makeConstant(Region r, Place p) {
        return new Distribution_c.Constant(r, p);
    }
    
    /**
     * Create a Distribution where the points in the
     * region 1...p.length are mapped to the respective
     * places.
     * @param p the list of places (implicitly defines the region)
     * @return
     */
    static Distribution makeUnique(Place[] p) {
        return new Distribution_c.Unique(p);
    }
    
    
    // Actual Distribution implementation(s)
    
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
    
    public Region subOrdinal(int i, int j) {
        return ((Region_c)region_).subOrdinal(i, j);
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
    
    static class Combined extends Distribution_c {
        
        private final Distribution[] members_;
        
        Combined(Region r, Distribution[] members_) {
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