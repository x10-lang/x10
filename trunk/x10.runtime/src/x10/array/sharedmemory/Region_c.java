/*
 * Created on Oct 3, 2004
 */
package x10.array.sharedmemory;

import java.util.Iterator;
import java.util.Set;

import x10.array.Range;
import x10.array.Region;


/**
 * Implementation of Region. The Points in a region, aka. tuples, 
 * are implemented as one-dimensional int arrays. Instance of 
 * this class are immutable!
 *
 * @author Christoph von Praun
 * @author Christian Grothoff
 */
class Region_c implements Region {
	
	private final Range[] dims_;
	final int card;

	final int rank;
    
	/**
	 * Convenience constructor.
	 * Region starts in all dimensions at index 0.
	 */
	Region_c(int[] dims) {
		assert dims != null;
		rank = dims.length;
		
		int tmp_card = 1;
		dims_ = new Range[dims.length];
		for (int i = 0; i < dims.length; ++ i) {
			dims_[i] = new Range(0, dims[i]);
			tmp_card *= dims[i];
		}
		card = tmp_card;
	}
	
	Region_c(Range[] dims) {
		assert dims != null;
		rank = dims.length;
		int tmp_card = 1;
		dims_ = dims;
		for (int i = 0; i < dims.length; ++ i) 
			tmp_card *= dims_[i].card;
		card = tmp_card;
	}

	public int rank() {
	    return rank;
	}
    
	/**
	 * @param  dims Regions that must be subsets of the ranges of 
	 *              this region, or null if the entire range along 
	 *              one dimension should be considered.
	 * @return A sub-region of this region. 
	 */
	public Region sub(Range[] dims) {
		assert dims.length == rank;
		for (int i = 0; i < dims.length; ++i) {
			if (dims[i] != null) {
				assert dims_[i].contains(dims[i]);
			} else 
				dims[i] = dims_[i];
		}
		return new Region_c(dims);
	}
    
        Region_c subOrdinal(int start, int end) { // end exclusive here!
        Range[] ret = new Range[rank];
        int multiplier = 1;
        for (int i = rank - 1; i >= 0; i--)
            multiplier *= dims_[i].card;

        for (int i = rank - 1; i >= 0; i--) {
            multiplier /= dims_[i].card;
            int offS = (start / multiplier);
            int delS = (start % multiplier);
            int offE = (end / multiplier);
            int delE = (end % multiplier);

            if (!(((delS == 0) && (delE == multiplier - 1)) || ((offS == offE))))
                throw new Error("Not implemented"); /*
                                                     * we don't get a nice-cut
                                                     * region here!
                                                     */

            ret[i] = new Range(offS + dims_[i].lo, offE + dims_[i].lo); // -1: range is inclusive!
            start = delS;
            end = delE;
        }
        return new Region_c(ret);
    }

	
	public Region combine(Region r) {
		throw new Error("not implemented");
	}
	
	/**
     * @return range in the i-th dimension.
     */
	public Range dim(int i) {
		assert i < rank;
		return dims_[i];
	}
	
	public boolean contains(Region r) {
		assert r.rank() == rank;
		
		Region_c r_c = (Region_c) r;
		boolean ret = true;
		
		for (int i = 0; i < r_c.rank && ret; ++ i) 
			ret = r_c.dims_[i].contains(dims_[i]);
		return ret;
	}
	
	public boolean contains(int[] p) {
		assert p.length == dims_.length;
		
		boolean ret = true;
		for (int i = 0; i < dims_.length; ++i) {
			Range r = dims_[i];
			for (int j = 0; i < p.length; ++j) {
				if (!r.contains(p[j])) {
					ret = false;
					break;
				}
			}
			if (!ret) break;
		}
		return ret;
	}
	
	public int size() {
	    int ret = 1;
	    for (int i=dims_.length-1;i>=0;i--) 
	        ret *= dims_[i].card; // TODO: check overflow?
	    return ret;            
	}
    
	/**
	 * @param p A point in the region; the dimension of p must be compatible 
	 *          with this region.
	 * @return  Returns the ordinal of the point in this region (its position, 
	 *          where the initial constant is assigned an ordinal of zero).
	 */
	public int ordinal(int[] p) {
		assert p.length == dims_.length;
		
		int ret = 0;
		for (int i = 0; i < p.length - 2; ++i) {
			ret = (ret + dims_[i].ordinal(p[i])) * dims_[i+1].card;
		}
		ret += dims_[p.length -1].ordinal(p.length -1);
		return ret;
	}
	
	/**
	 * Iterator that yields the individual points of a region in 
	 * lexicographical order. Points are specified as arrays of int.
	 */
	public Iterator iterator() {
		return new RegionIterator();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		for (int i = 0; i < dims_.length; ++i) {
			sb.append(dims_[i].toString());
			if (i < dims_.length - 1)
				sb.append(",");
		}
		sb.append("}");
		return sb.toString();
	}
	
	public boolean equals(Object o) {
		assert o instanceof Range;
		
		Region_c rhs = (Region_c) o;
		boolean ret = rhs.rank == rank && rhs.card == card;
		for (int i = 0; ret && i < dims_.length; ++ i) 
			ret = dims_[i].equals(rhs.dims_[i]);
		return ret;
	}
	
	public int hashCode() {
		return card;
	}
	
	/** 
	 * @return A combined region that covers the space of the 
	 *         convex hull of all other regions.
	 */
	static Region_c combine(Set l) {
		int rank = -1;
		for (Iterator it = l.iterator(); it.hasNext(); ) {
			Region_c r = (Region_c) it.next();
			if (rank == -1)
				rank = r.rank;
			else
				assert rank == r.rank;
		}
		
		int[] lo = new int[rank];
		int[] hi = new int[rank];
		for (Iterator it = l.iterator(); it.hasNext(); ) {
			Region_c r = (Region_c) it.next();
			for (int i = 0; i < rank; ++i) {
				Range rg = r.dim(i);
				if (lo[i] > rg.lo)
					lo[i] = rg.lo;
				if (hi[i] < rg.hi)
					hi[i] = rg.hi;
			}
		}
		
		Range[] ra = new Range[rank];
		for (int i = 0; i < rank; ++i)
			ra[i] = new Range(lo[i], hi[i]);
		return new Region_c(ra);
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
			for (int i = rank - 1; i > 0; --i) {
				int tmp = rest / dims_[i].card;
				rest -= tmp;
				ret[i] = tmp + dims_[i].lo;
			}
			nextOrd_++;
			return ret;
		}
	}
}