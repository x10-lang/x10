/*
 * Created on Oct 3, 2004
 */
package x10.array.sharedmemory;

import java.util.Iterator;

import x10.array.InvalidIndexException;
import x10.array.Range;
import x10.array.ContiguousRange;
import x10.array.Region;

/**
 * Implementation of Region. The Points in a region, aka. tuples, are
 * implemented as one-dimensional int arrays. Instance of this class are
 * immutable!
 * 
 * @author Christoph von Praun
 * @author Christian Grothoff
 */
class Region_c implements Region {

    private final Range[] dims_;

    final int card;

    final int rank;

	static Region_c makeUpperTriangular(int n) {
        assert n >= 0;
        throw new Error ("TODO");
	}
	
	static Region_c makeLowerTriangular(int n) {
        assert n >= 0;
        throw new Error ("TODO");
	}
 
	static Region_c makeBanded(int n, int k) {
        assert n >= 0;
        throw new Error ("TODO");
	}
	    
    /**
     * Convenience constructor. Region starts in all dimensions 
     * at index 0, to index idx[i], including the latter.
     */
    Region_c(int[] dims) {
        assert dims != null;
        rank = dims.length;

        int tmp_card = 1;
        dims_ = new Range[dims.length];
        for (int i = 0; i < dims.length; ++i) {
            dims_[i] = new ContiguousRange(0, dims[i]);
            tmp_card *= dims[i];
        }
        card = tmp_card;
    }

    Region_c(Range[] dims) {
        assert dims != null;
        rank = dims.length;
        int tmp_card = 1;
        dims_ = dims;
        for (int i = 0; i < dims.length; ++i)
            tmp_card *= dims_[i].count();
        card = tmp_card;
    }

    /**
     * Copy constructor.
     */
    Region_c (Region_c r) {
        dims_ = r.dims_;
        card = r.card;
        rank = r.rank;
    }
    
    public int rank() {
        return rank;
    }

    /**
     * Creates a subregion from this region
     * @param partitions
     * @param part
     * @return The new sub-region.
     */
    Region_c sub(int partitions, int part) {
        assert partitions > 0 && part >= 0 && part < partitions;
        assert count() % partitions == 0;
        assert dims_[0] instanceof ContiguousRange;
        assert dims_[0].count() % partitions == 0;
        
        ContiguousRange cr = (ContiguousRange) dims_[0];
        int len = cr.card / partitions;
        int offset = len * part;
        Range[] new_dims = new Range[rank];
        // determine most significant dimension
        new_dims[0] = new ContiguousRange(cr.lo + offset, cr.lo + offset + len - 1);
        
        // initialize other dimensions
        for (int i = 1; i < rank; ++i) 
            new_dims[i] = dims_[i];
        return new Region_c(new_dims);
    }
    
    public Region union(Region r) {
        assert r != null;
        assert r.rank() == rank;
        assert r instanceof Region_c;
        
        Region_c rc = (Region_c) r;
        Range[] d = new Range[rank];
        for (int i = 0; i < d.length; ++ i)
            d[i] = dims_[i].union(rc.dims_[i]);
        return new Region_c(d);
    }

    public Region intersect(Region r) {
        assert r != null;
        assert r.rank() == rank;
        assert r instanceof Region_c;
        
        Region_c rc = (Region_c) r;
        Range[] d = new Range[rank];
        for (int i = 0; i < d.length; ++ i)
            d[i] = dims_[i].intersect(rc.dims_[i]);
        return new Region_c(d);
    }

    public Region difference(Region d) { 
        throw new Error("TODO");
    }
    
    /**
     * @return range in the i-th dimension.
     */
    public Range dim(int i) {
        assert i < rank;
        return dims_[i];
    }

    public Range[] dim() {
        Range[] ret = new Range[rank];
        System.arraycopy(dims_, 0, ret, 0, ret.length);
        return ret;
    }
    
    public boolean contains(Region r) {
        assert r.rank() == rank;

        Region_c r_c = (Region_c) r;
        boolean ret = true;

        for (int i = 0; i < r_c.rank && ret; ++i)
            ret = r_c.dims_[i].contains(dims_[i]);
        return ret;
    }

    public boolean contains(int[] p) {
        if (p.length != rank)
            throw new InvalidIndexException();

        boolean ret = true;
        for (int i = 0; ret && i < rank; ++i) {
            Range r = dims_[i];
            if (!r.contains(p[i]))
                ret = false;
        }
        return ret;
    }

    public int count() {
        int ret = 1;
        for (int i = rank - 1; i >= 0; i--)
            ret *= dims_[i].count(); // TODO: check overflow?
        return ret;
    }
    
    /**
     * @param p A point in the region; the dimension of p must be compatible
     *          with this region.
     * @return Returns the ordinal of the point in this region (its position,
     *         where the initial constant is assigned an ordinal of zero).
     */
    public int ordinal(int[] p) {
        if (p.length != rank)
            throw new InvalidIndexException();

        int ret = 0;
        int base = 1;
        for (int i = 0; i < p.length; ++i) {
            ret += dims_[i].ordinal(p[i]) * base;
            base *= dims_[i].count();
        }
        return ret;
    }

    /**
     * @return Iterator that yields the individual points of a region in lexicographical
     * order. Points are specified as arrays of int.
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

    public boolean equals(Object o) {
        assert o.getClass() == getClass();

        Region_c rhs = (Region_c) o;
        boolean ret = rhs.rank == rank && rhs.card == card;
        for (int i = 0; ret && i < rank; ++i)
            ret = dims_[i].equals(rhs.dims_[i]);
        return ret;
    }

    public int hashCode() {
        return card;
    }

    static final class Empty extends Region_c {
        Empty() {
            super(new Range[0]);
        }
    }
    
    private class RegionIterator implements Iterator {
        private int nextOrd_;

        public boolean hasNext() {
            return nextOrd_ < card;
        }

        public void remove() {
            throw new Error("not implemented");
        }

        public Object next() {
            assert hasNext();

            int[] ret = new int[rank];
            // express nextOrd_ as a base of the regions
            int rest = nextOrd_;
            int base = 0;
            for (int i = 0; rest > 0 && i < rank; ++i) {
                base = dims_[i].count();
                int tmp = rest % base;
                rest = (rest - tmp) / base;
                ret[i] = dims_[i].coord(tmp);
            }
            nextOrd_++;
            return ret;
        }
    }
}