/*
 * Created on Nov 1, 2004
 *
 *
 */
package x10.array;

import java.util.Iterator;

import x10.array.sharedmemory.Distribution_c;
import x10.lang.place;
import x10.lang.point;
import x10.lang.region;
import x10.lang.Runtime;

/** A ContiguousRange is a 1-d region (Range) from lo (inclusive) to hi (inclusive).
 * TODO: Implement difference (which produces discontinguous ranges).
 * @author praun
 * @author vj
 * 
 * 
 */
public class ContiguousRange extends Range {
	
	/**
	 * Range that starts at 0 to hi (including!).
	 */
	public ContiguousRange(int hi) {
		this(0, hi);
	}
	
	/**
	 * Range that starts at lo (including) to hi (including!).
	 */
	public ContiguousRange(int lo, int hi) {
	    super(lo, hi, hi - lo + 1);
	    assert (lo <= hi);
	}
	
	
	public /*nat*/int ordinal(point p) {
		assert contains(p);
		return p.get(0) - lo;
	}
	
	public region union( region r ) {
		assert r != null;
		region ret = null;
		if (r instanceof ContiguousRange) {
		    ContiguousRange cr = (ContiguousRange) r;
		    if (contains(cr.lo) || contains(cr.hi)) {
		        // regions overlap - fuse them
		        int l = (lo < cr.lo) ? lo : cr.lo;
		        int h = (hi > cr.hi) ? hi : cr.hi;
		        ret = new ContiguousRange(l, h);
		    }
		}
		if (ret == null) {
		    ret = super.union(r);
		}
		return ret;
	}
	
	public region intersection( region r ) {
		assert r != null;
		region ret;
		if (r instanceof ContiguousRange) {
		    ContiguousRange cr = (ContiguousRange) r;
		    int l = (lo > cr.lo) ? lo : cr.lo;
		    int h = (hi < cr.hi) ? hi : cr.hi;
		    if (l <= h)
		        ret = new ContiguousRange(l, h);
		    else
		        ret = new EmptyRegion(rank);
		} else {
		    ret = super.intersection(r);
		}
		return ret;
	}
	
	public region difference(region r ) {
		assert r != null;
		region ret = null;
		if (r instanceof ContiguousRange) {
		    ContiguousRange cr = (ContiguousRange) r;
		    if (lo == cr.lo || hi == cr.hi) {
		        ret = intersection(r);
		    }
		}
		if (ret == null)
		    ret = super.difference(r);
		return ret;
	}
	
	public boolean disjoint(region r ) {
		assert r != null;
		boolean ret;
		if (r instanceof ContiguousRange) {
		    ContiguousRange cr = (ContiguousRange) r;
		    ret = cr.hi < lo || hi < cr.lo;
		} else {
		    ret = super.disjoint(r);
		}
		return ret;
	}
	
	public boolean contains(point p) {
		assert p.rank == 1;
		int val = p.get(0);
		return lo <= val && val <= hi; 
	}
	
	public boolean contains(int[] p) {
		assert p.length == 1;
		int val = p[0];
		return lo <= val && val <= hi; 
	}
	
	public boolean contains(region r) {
		assert r != null;
		boolean ret;
		if (r instanceof ContiguousRange) {
		    ContiguousRange cr = (ContiguousRange) r;
		    ret = cr.lo >= lo && cr.hi <= hi;
		} else {
		    ret = super.contains(r);
		}
		return ret;
	}
	
	public String toString() {
		return  lo + ":" + hi ;
	}
    
    public region[] partition(int n) {
        assert n > 0;
        region[] ret = new region[n];  
        int all_size = size();
        int base = low();
        
        if (n == 1) {
            ret[0] = this;
        } else if (n >= all_size) {
            int i = 0;
            for (; i < all_size; i++) {
                ret [i] = new ContiguousRange(base, base);
                base++;
            }
            for ( ; i < n; i++)
                ret [i] = new EmptyRegion(1);
        } else {
            int sub_size = (all_size % n == 0) ? (all_size / n) : ((all_size + (all_size % n)) / n);
            for (int i=0; i < n; i++) {
                int lo = base + (i * sub_size);
                int hi = (i < n - 1) ? (lo + sub_size - 1) : (base + all_size - 1);
                ret[i] = new ContiguousRange(lo, hi);
            }
        }
        return ret;
    }
	
	public boolean isConvex() {
		return true;
	}
	public region convexHull() {
		return this;
	}
	
	public /*nat*/int size() {
		return size;
	}
	public region rank( int index ) {
		return this;
	}
	// TODO: vj check that this arithmetic is correct.
	public  point/*(rank)*/ coord(/*nat*/ int ord) {
        assert ord < size();
        
		return Runtime.factory.getPointFactory().point(this, new int[] {ord + lo});
	}
	public Iterator iterator() {
		return new RegionIterator();
	}
	private class RegionIterator implements Iterator {
		private int next_ = lo;
		
		public boolean hasNext() {
			return next_ <= hi;
		}
		
		public void remove() {
			throw new Error("not implemented");
		}
		
		public java.lang.Object next() {
			assert hasNext();
			point ret = Runtime.factory.getPointFactory().point(ContiguousRange.this, new int[] { next_ });
			next_++;
			return ret;
		}
	}
}
