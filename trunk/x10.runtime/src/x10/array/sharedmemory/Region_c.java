/*
 * Created on Oct 3, 2004
 */
package x10.array.sharedmemory;

import java.util.Iterator;
import java.util.NoSuchElementException;

import x10.array.Range;
import x10.array.ContiguousRange;
import x10.array.StridedRange;
import x10.array.EmptyRegion;

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
	final int card;
	
	
	
	/**
	 * Convenience constructor. Region starts in all ranks 
	 * at index 0, to index idx[i], including the latter.
	 */
	Region_c(int[] dims) {
		super(dims.length);
		assert dims != null;
		
		int tmp_card = 1;
		this.dims = new region[dims.length];
		for (int i = 0; i < dims.length; ++i) {
			this.dims[i] = new ContiguousRange(0, dims[i]);
			tmp_card *= dims[i];
		}
		card = tmp_card;
	}
	
	Region_c(final region[] dims) {
		super(dims.length);
		assert dims != null;
		int tmp_card = 1;
		this.dims = dims;
		for (int i = 0; i < dims.length; ++i)
			tmp_card *= ((Range) dims[i]).size;
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
		region[] new_dims = new region[(int) rank];
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
		region[] d = new region[(int) rank];
		for (int i = 0; i < d.length; ++ i)
			d[i] = dims[i].union(rc.dims[i]);
		return new Region_c(d);
	}
	
	public region intersection( region r ) {
		assert r != null;
		assert r.rank == rank;
		
		Region_c rc = (Region_c) r;
		region[] d = new region[(int) rank];
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
		return dims[((int) i) % dims.length];
	}
	
	public region[] dim() {
		region[] ret = new region[(int) rank];
		System.arraycopy(dims, 0, ret, 0, ret.length);
		return ret;
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
			ret = ! ((Range) dims[i]).contains(p.valueAt(i));
		}
		return ret;
	}
	
	public /*nat*/int size() {
		int ret = 1;
		for (int i = (int) rank - 1; i >= 0; i--)
			ret *= ((Range) dims[i]).size; // TODO: check overflow?
		return ret;
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
			ret += r.ordinal(p.valueAt(i)) * base;
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
	
	public boolean equals( Object o ) {
		assert o.getClass() == getClass();
		
		Region_c rhs = (Region_c) o;
		boolean ret = rhs.rank == rank && rhs.card == card;
		for (int i = 0; ret && i < rank; ++i)
			ret = dims[i].equals(rhs.dims[i]);
		return ret;
	}
	
	public int hashCode() {
		return card;
	}
	
	private class RegionIterator implements Iterator {
		private int nextOrd_;
		
		public boolean hasNext() {
			return nextOrd_ < card;
		}
		
		public void remove() {
			throw new Error("not implemented");
		}
		
		public java.lang.Object next() {
			assert hasNext();
			
			int[] ret = new int[(int) rank];
			// express nextOrd_ as a base of the regions
			int rest = nextOrd_;
			int base = 0;
			for (int i = 0; rest > 0 && i < rank; ++i) {
				Range r = (Range) dims[i];
				base = r.size;
				int tmp = rest % base;
				rest = (rest - tmp) / base;
				ret[i] = r.coord(tmp).valueAt(0);
			}
			nextOrd_++;
			return point.factory.point(Region_c.this, ret);
		}
	}
	public region convexHull() {
	 throw new Error("TODO");
	}
	public boolean disjoint(region r) {
		throw new Error("TODO");
	}
	
	public int high() throws EmptyRegionError {
		if (rank > 0) 
			return dims[0].high();
		throw new EmptyRegionError();
	}
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
	 throw new Error("TODO");
	}
    /*
    public int[] firstElement() {
        if (rank == 0)
            return null; // empty!
        int[] ret = new int[rank];
        // express nextOrd_ as a base of the regions
        for (int i = 0; i < rank; ++i) {
            if (dims[i].size() == 0)
                return null; // empty!
            ret[i] = dims[i].coord(0);
}
        return ret;
    }

    public int[] nextElement(int[] in) {
        for (int i = rank-1;i>=0;i--) {
            int ord = dims[i].ordinal(in[i]);
            int base = dims[i].size();
            if (ord < base-1) {
              in[i] = dims[i].coord(ord+1);  
              break;
            } else {
              in[i] = dims[i].coord(0);
              if (i == 0) 
                  return null;
            }            
        }            
        return in;
    }

    public boolean hasNextElement(int[] in) {
        return (in != null);
    }
*/
    
}