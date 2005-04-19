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
    private final region[] dims_;
    private final int[] base_; 
    final int card;
    
    
    public MultiDimRegion(final region[] d) {
        super(d.length);
        assert d != null;
        // assert that all dims are actually Ranges
        dims_ = new region[d.length];
        for (int i = 0; i < dims_.length; ++i)
            dims_[i] = d[i];
        
        int tmp_card = 1;
        base_ = new int[dims_.length];
        // row major ordering (C conventions)
        for (int i = rank-1; i >= 0; --i) {
            base_[i] = tmp_card;
            tmp_card *= dims_[i].size();
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
    public region[] partition(int n, int dim_to_split) {   
        assert (n > 0);
        
        if (! (dims_[dim_to_split] instanceof ContiguousRange)) 
            throw new Error("MultiDimRegion::partition can only block those arrays that have a contiguous dimension to split.");
        ContiguousRange cr = (ContiguousRange) dims_[dim_to_split];
        // partition fails if the dim_to_split dimension has size less than n
        if (cr.size() < n) 
            throw new Error("MultiDimRegion::partition can only block those arrays that have size of dimension of the dimension to split larger than or equal to number of partitions.");
        region[] ret = new region[n];
        if (n == 1) {
            ret[0] = this;
        } else {
            region[] split_dim = cr.partition(n, 0);
            for (int i = 0; i < n; ++i) {
                if (split_dim[i].size() == 0) 
                    ret[i] = new EmptyRegion(rank);
                else {
                    region[] new_dims = new region[rank];
                    new_dims[dim_to_split] = split_dim[i];
                    for (int j = 0; j < rank; ++j) {
                        if (j != dim_to_split)
                            new_dims[j] = dims_[j];
                    }
                    ret[i] = new MultiDimRegion(new_dims);
                }
            }
        }
        return ret;
    }
    
    public region union(region r) {
        assert r != null;
        assert r.rank == rank;
    
        region ret = null;
        if (r instanceof MultiDimRegion) {
            MultiDimRegion rc = (MultiDimRegion) r;
            region[] d = new region[rank];
            for (int i = 0; ret == null && i < d.length; ++ i) {
                d[i] = dims_[i].union(rc.dims_[i]);
                if (d[i].size() == 0)
                    ret = new EmptyRegion(rank);
            }
            if (ret == null)
                ret = new MultiDimRegion(d);
        } else {
            ret = ArbitraryRegion.union(this, r);
        }
        return ret;
    }
    
    public region intersection(region r) {
        assert r != null;
        assert r.rank == rank;
        region ret = null;
        
        if (r instanceof MultiDimRegion) {
            MultiDimRegion rc = (MultiDimRegion) r;
            region[] d = new region[rank];
            for (int i = 0; ret == null && i < d.length; ++ i) {
                d[i] = dims_[i].intersection(rc.dims_[i]);
                if (d[i].size() == 0)
                    ret = new EmptyRegion(rank);
            }
            if (ret == null)
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
        assert i < dims_.length;
        assert i >= 0;
        
        return dims_[i];
    }
    
    public boolean contains(region r) {
        assert r != null;
        assert r.rank == rank;
        
        boolean ret = true;
        if (r instanceof MultiDimRegion) {
            MultiDimRegion r_c = (MultiDimRegion) r;
            for (int i = 0; i < r_c.rank && ret; ++i)
                ret = dims_[i].contains(r_c.dims_[i]);
        } else 
            ret = super.contains(r);
        return ret;
    }
    
    public boolean contains(point p) {
        assert p.rank == rank;
        boolean ret = true;
        for (int i = 0; ret && i < rank; ++i) {
            int[] coord = {p.get(i)};
            ret = dims_[i].contains(coord);
        }
        return ret;
    }
    
    public boolean contains(int[] val) {
        assert val.length == rank;
        boolean ret = true;
        for (int i = 0; ret && i < rank; ++i) {
            int[] coord = {val[i]};
            ret = dims_[i].contains(coord);
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
            Range r = (Range) dims_[i];
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
            sb.append(dims_[i].toString());
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
            return dims_[0].high();
        throw new EmptyRegionError();
    }
    
    /* TODO cvp -> vj: fix the exception, I do not think empty region excp. is meaningful */
    public int low() throws EmptyRegionError {
        if (rank > 0) 
            return dims_[0].low();
        throw new EmptyRegionError();
    }
    
    public boolean isConvex() {
        boolean ret = true;	
        for (int i = 0; i < rank && ret; ++i) {
            ret = dims_[i].isConvex();
        }
        return ret;
    }
    
    public point coord(/*nat*/ int ordinal) throws ArrayIndexOutOfBoundsException {
        assert ordinal < size();
        
        int[] ret = new int[rank];
        int rest = ordinal;
        int base = 0;
        for (int i = 0; i < rank ; ++i) {
            region r = dims_[i];
            int tmp = rest / base_[i];
            rest = rest % base_[i];
            ret[i] = r.coord(tmp).get(0);
        }
        return point.factory.point(MultiDimRegion.this, ret);
    }
}