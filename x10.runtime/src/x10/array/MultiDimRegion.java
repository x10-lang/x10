/*
 * Created on Oct 3, 2004
 */
package x10.array;

import java.util.Iterator;



import x10.lang.region;
import x10.lang.point;

/**
 * Implementation of Region. Instance of this class are immutable! 
 * This class only implements a cross-product of contiguous or strided regions.
 *  @seealso point
 * @author Christoph von Praun
 * @author Christian Grothoff
 * @author vj
 */

public class MultiDimRegion extends region  {
    private final Range[] dims;
    private final int[] base_; 
    final int card;
    
    
    public MultiDimRegion(final region[] d) {
        super(d.length);
        assert d != null;
        // assert that all dims are actually Ranges
        dims = new Range[d.length];
        for (int i = 0; i < dims.length; ++i) {
            assert (d[i] instanceof Range);
            dims[i] = (Range) d[i];
        }
        
        int tmp_card = 1;
        base_ = new int[dims.length];
        // row major ordering (C conventions)
        for (int i = rank-1; i >= 0; --i) {
            base_[i] = tmp_card;
            tmp_card *= dims[i].size;
        }
        card = tmp_card;
    }   
    
    /**
     * Creates a subregion from this region
     * @param partitions
     * @param part
     * @return The new sub-region.
     * TODO: vj -- check the intended logic survived the rework.
     */
    public region[] partition(int n) {   
        assert (n > 0);
        if (! (dims[0] instanceof ContiguousRange)) 
            throw new Error("MultiDimRegion::partition can only block those arrays that have contiguos dimension 0.");
        ContiguousRange cr = (ContiguousRange) dims[0];
        // partition fails if the first dimension has size less than n
        if (cr.size() < n) 
            throw new Error("MultiDimRegion::partition can only block those arrays that have size of dimension 0 larger than number of partitions.");
        region[] ret = new region[n];
        if (n == 1) {
            ret[0] = this;
        } else {
            region[] new_first_dims = cr.partition(n);
            for (int i = 0; i < n; ++i) {
                if (new_first_dims[i].size() == 0) 
                    ret[i] = new EmptyRegion(rank);
                else {
                    region[] new_dims = new region[rank];
                    new_dims[0] = new_first_dims[i];
                    for (int j = 1; j < rank; ++j) 
                        new_dims[j] = dims[j];
                    ret[i] = new MultiDimRegion(new_dims);
                }
            }
        }
        return ret;
    }
    
    public region union(region r) {
        assert r != null;
        assert r.rank == rank;
    
        region ret;
        if (r instanceof MultiDimRegion) {
            MultiDimRegion rc = (MultiDimRegion) r;
            region[] d = new region[rank];
            for (int i = 0; i < d.length; ++ i)
                d[i] = dims[i].union(rc.dims[i]);
            ret = new MultiDimRegion(d);
        } else {
            ret = ArbitraryRegion.union(this, r);
        }
        return ret;
    }
    
    public region intersection(region r) {
        assert r != null;
        assert r.rank == rank;
        region ret;
        
        if (r instanceof MultiDimRegion) {
            MultiDimRegion rc = (MultiDimRegion) r;
            region[] d = new region[rank];
            for (int i = 0; i < d.length; ++ i)
                d[i] = dims[i].intersection(rc.dims[i]);
            ret = new MultiDimRegion(d);
        } else {
            ret = ArbitraryRegion.intersection(this, r);
        }
        return ret;
    }
    
    // This wont return a contiguous region.
    public region difference(region d) {
        assert d != null;
        assert d.rank == rank;
        
        return ArbitraryRegion.difference(this, d);
    }
    
    /**
     * @return range in the i-th dimension.
     */
    public region rank(/*nat*/int i) {
        assert i < dims.length;
        assert i >= 0;
        
        return dims[i];
    }
    
    public boolean contains(region r) {
        assert r != null;
        assert r.rank == rank;
        
        boolean ret = true;
        if (r instanceof MultiDimRegion) {
            MultiDimRegion r_c = (MultiDimRegion) r;
            for (int i = 0; i < r_c.rank && ret; ++i)
                ret = r_c.dims[i].contains(dims[i]);
        } else 
            ret = super.contains(r);
        return ret;
    }
    
    public boolean contains(point p) {
        assert p.rank == rank;
        boolean ret = true;
        for (int i = 0; ret && i < rank; ++i) 
            ret = dims[i].contains(p.get(i));
        return ret;
    }
    public boolean contains(int[] val) {
        assert val.length == rank;
        boolean ret = true;
        for (int i = 0; ret && i < rank; ++i) {
            ret = ((Range) dims[i]).contains(val[i]);
        }
        return ret;
    }
    
    
    public /*nat*/int size() {
       return card;
    }
    
    /**
     * @param p A point in the region; the dimension of p must be compatible
     *          with this region.
     * @return Returns the ordinal of the point in this region (its position,
     *         where the initial constant is assigned an ordinal of zero).
     */
    public /*nat*/int ordinal(point/*(this)*/ p) {
        assert (p.rank == rank);
        
        /*nat*/int ret = 0;
        int base = 1;
        for (int i = p.rank -1 ; i >= 0; --i) {
            Range r = (Range) dims[i];
            ret += r.ordinal(p.get(i)) * base;
            base *= r.size;
        }
        return ret;
    }
    
    /**
     * @return Iterator that yields the individual points of a region
     * in lexicographical * order. Points are specified as arrays of
     * int.
     */
    public Iterator iterator() {
        return new RegionIterator();
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        for (int i = 0; i < rank; ++i) {
            sb.append(dims[i].toString());
            if (i < rank - 1)
                sb.append(",");
        }
        sb.append("}");
        return sb.toString();
    }
    
    private class RegionIterator implements Iterator {
        private int nextOrd_ = 0;
        
        public boolean hasNext() {
            return nextOrd_ < card;
        }
        
        public void remove() {
            throw new Error("not implemented");
        }
        
        public java.lang.Object next() {
            assert hasNext();
            return coord(nextOrd_++);
        }
    }
    
    /* TODO cvp -> vj */
    public region convexHull() {
        throw new Error("TODO");
    }
    
    public boolean disjoint(region r) {
        boolean ret = true;
        if (r.rank == rank) {
            for (Iterator it = r.iterator(); it.hasNext(); ) {
                point p = (point) it.next();
                if (contains(p)) {
                    ret = false;
                    break;
                }
            }
        }
        return ret;
    }
    
    /* TODO cvp -> vj: fix the exception, I do not think empty region excp. is meaningful */
    public int high() throws EmptyRegionError {
        if (rank > 0) 
            return dims[0].high();
        throw new EmptyRegionError();
    }
    
    /* TODO cvp -> vj: fix the exception, I do not think empty region excp. is meaningful */
    public int low() throws EmptyRegionError {
        if (rank > 0) 
            return dims[0].low();
        throw new EmptyRegionError();
    }
    
    public boolean isConvex() {
        boolean ret = true;	
        for (int i = 0; i < rank && ret; ++i) {
            ret = dims[i].isConvex();
        }
        return ret;
    }
    
    public point coord(/*nat*/ int ordinal) throws ArrayIndexOutOfBoundsException {
        assert ordinal < size();
        
        int[] ret = new int[rank];
        int rest = ordinal;
        int base = 0;
        for (int i = 0; i < rank ; ++i) {
            Range r = dims[i];
            int tmp = rest / base_[i];
            rest = rest % base_[i];
            ret[i] = r.coord(tmp).get(0);
        }
        return point.factory.point(MultiDimRegion.this, ret);
    }
}