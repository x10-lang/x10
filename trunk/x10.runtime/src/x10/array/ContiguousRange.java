/*
 * Created on Nov 1, 2004
 *
 *
 */
package x10.array;

import java.util.Iterator;

import x10.lang.point;
import x10.lang.region;
import x10.lang.Runtime;
import x10.lang.Object;

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
	}
	
	
	public /*nat*/int ordinal(point p) {
		assert contains(p);
		return p.valueAt(0) - lo;
	}
	
	public region union( region r ) {
		assert r != null;
		region ret;
		if (r instanceof ContiguousRange) {
		    ContiguousRange cr = (ContiguousRange) r;
		    int l = (lo < cr.lo) ? lo : cr.lo;
		    int h = (hi > cr.hi) ? hi : cr.hi;
		    ret = new ContiguousRange(l, h);
		} else {
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
		    return new ContiguousRange(l, h);
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
		int val = p.valueAt(0);
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
	
	public boolean equals(Object o) {
		assert o != null;
		assert o instanceof Range;
		if (!(o instanceof ContiguousRange))
			return false;
		
		ContiguousRange rhs = (ContiguousRange) o;
		return rhs.lo == lo && rhs.hi == hi;
		
	}
	
	public int hashCode() {
		return size;
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
		return Runtime.factory.getPointFactory().point(this, new int[] { (ord % size) + lo});
	}
	public Iterator iterator() {
		return new RegionIterator();
	}
	private class RegionIterator implements Iterator {
		private int next;
		
		public boolean hasNext() {
			return next <= hi;
		}
		
		public void remove() {
			throw new Error("not implemented");
		}
		
		public java.lang.Object next() {
			assert hasNext();
			next++;
			return Runtime.factory.getPointFactory().point(ContiguousRange.this, new int[] { next });
		}
	}
}
