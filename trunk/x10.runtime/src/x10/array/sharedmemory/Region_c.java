/*
 * Created on Oct 3, 2004
 */
package x10.array.sharedmemory;

import java.util.Iterator;

import x10.array.Range;
import x10.array.ContiguousRange;

import x10.lang.PointOutOfRegionError;
import x10.lang.region;
import x10.lang.point;
import x10.lang.Object;

/**
 * Implementation of Region. Instance of this class are immutable! 
 * This class only implements a cross-product of contiguous or strided regions.
 *  @seealso point
 * @author Christoph von Praun
 * @author Christian Grothoff
 * @author vj
 */

public class Region_c extends region  {
    private final region[] dims;
    private final int[] base_; 
    final int card;
    
    
    Region_c(final region[] dims) {
        super(dims.length);
        assert dims != null;
        int tmp_card = 1;
        this.dims = dims;
        base_ = new int[dims.length];
        for (int i = 0; i < dims.length; ++i) {
            base_[i] = tmp_card;
            tmp_card *= ((Range) dims[i]).size;
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
    Region_c sub(int partitions, int part) {
        assert partitions > 0 && part >= 0 && part < partitions;
        assert size() % partitions == 0;
        assert dims[0] instanceof ContiguousRange;
        assert ((Range) dims[0]).size % partitions == 0;
        
        ContiguousRange cr = (ContiguousRange) dims[0];
        int len = cr.size / partitions;
        int offset = len * part;
        region[] new_dims = new region[rank];
        // determine most significant dimension
        new_dims[0] = new ContiguousRange(cr.lo + offset, cr.lo + offset + len - 1);
        
        // initialize other dimensions
        for (int i = 1; i < rank; ++i) 
            new_dims[i] = dims[i];
        return new Region_c(new_dims);
    }
    
    public region union( region r ) {
        assert r != null;
        assert r.rank == rank;
        
        Region_c rc = (Region_c) r;
        region[] d = new region[rank];
        for (int i = 0; i < d.length; ++ i)
            d[i] = dims[i].union(rc.dims[i]);
        return new Region_c(d);
    }
    
    public region intersection(region r ) {
        assert r != null;
        assert r.rank == rank;
        
        Region_c rc = (Region_c) r;
        region[] d = new region[rank];
        for (int i = 0; i < d.length; ++ i)
            d[i] = dims[i].intersection(rc.dims[i]);
        return new Region_c(d);
    }
    
    // This wont return a contiguous region.
    public region difference( region d) { 
        throw new Error("TODO");
    }
    
    /**
     * @return range in the i-th dimension.
     */
    public region rank(/*nat*/int i) {
        return dims[ i % dims.length];
    }
    
    public boolean contains(region r) {
        assert r.rank == rank;
        Region_c r_c = (Region_c) r;
        boolean ret = true;
        
        for (int i = 0; i < r_c.rank && ret; ++i)
            ret = r_c.dims[i].contains(dims[i]);
        return ret;
    }
    
    public boolean contains(point p) {
        assert p.rank == rank;
        boolean ret = true;
        for (int i = 0; ret && i < rank; ++i) {
            ret = ((Range) dims[i]).contains(p.get(i));
        }
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
        for (int i = 0; i < p.rank; ++i) {
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
            Region_c r = (Region_c) dims[i];
            ret = r.isConvex();
        }
        return ret;
    }
    
    public point coord(/*nat*/ int ordinal) throws PointOutOfRegionError {
        assert ordinal < size();
        
        int[] ret = new int[rank];
        // express nextOrd_ as a base of the regions
        int rest = ordinal;
        int base = 0;
        for (int i = rank-1; i >=0 ; --i) {
            Range r = (Range) dims[i];
            int tmp = rest / base_[i];
            rest = rest % base_[i];
            ret[i] = r.coord(tmp).get(0);
        }
        return point.factory.point(Region_c.this, ret);
    }
}