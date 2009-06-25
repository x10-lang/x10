/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Nov 1, 2004
 */
package x10.array;

import java.util.HashSet;
import java.util.Iterator;

/**
 * @author praun
 * @author vj
 */
public class StridedRange extends Range {

	public final int stride;

	public StridedRange(int lo, int hi, int stride) {
		super(lo, hi, ((hi - lo) / stride) + 1);

		assert ((hi - lo) % stride) == 0;
		this.stride = stride;
	}

	public Point coord(/*nat*/int ord) {
		assert ord < size;
		int val = lo + ord * stride;
		return Point.makeFromVarArgs(val);
	}

	public /*nat*/int ordinal(Point p) {
		assert contains(p);
		int val = p.get(0);
		return (val - lo) % stride;
	}

	public Region union(Region r) {
		if (r.rank != 1)
			throw new RankMismatchException(r, 1);
		HashSet set = new HashSet();
	
		for (Iterator it = iterator(); it.hasNext(); ) {
		    Point p = (Point) it.next();
			set.add(p);
			
		}
		for (Iterator it = r.iterator(); it.hasNext(); ) {
		    Point p = (Point) it.next();
			set.add(p);
			
		}
		return new ArbitraryRegion(1, set);
	}

	public Region intersection(Region r) {
		if (r.rank != 1)
			throw new RankMismatchException(r, 1);
		HashSet set = new HashSet();
		
		for (Iterator it = iterator(); it.hasNext(); ) {
		    Point p = (Point) it.next();
			if (contains(p)) {
				set.add(p);
				
			}
		}
		return new ArbitraryRegion(1, set);
	}

	/**
	 * Returns this range less the points that are in r.
	 * If r is not one dimensional, then this is returned.
	 */
	public Region difference(Region r) {
		assert r != null;
		// super.difference() will check the rank
		return super.difference(r);
	}

	// [IP] FIXME: should we throw a RankMismatchException here?
	public boolean contains(Point p) {
		boolean ret = p.rank == 1;
		if (ret) {
			int val = p.get(0);
			ret = (val >= lo) && (val <= hi) && ((val - lo) % stride) == 0;
		}
		return ret;
	}

	public String toString() {
		return "[" + lo + ", " + hi + ": " + stride + "]";
	}

	public boolean isConvex() {
		return stride == 1;
	}

	public Region convexHull() {
		if (stride == 1)
			return this;
		return new ContiguousRange(lo, hi);
	}

	// [IP] FIXME: should we throw a RankMismatchException here?
	public boolean disjoint(Region r) {
		boolean ret = false;
		if (r.rank == 1) {
			for (Iterator it = r.iterator(); ret && it.hasNext(); ) {
			    Point p = (Point) it.next();
				if (contains(p))
					ret = false;
			}
		}
		return ret;
	}

	// [IP] FIXME: should we throw a RankMismatchException here?
	public boolean contains(Region r) {
		boolean ret = false;
		if (r.rank == 1) {
			ret = true;
			for (Iterator it = r.iterator(); ret && it.hasNext(); ) {
				if (!contains((Point) it.next()))
					ret = false;
			}
		}
		return ret;
	}

	public Iterator iterator() {
		return new StridedRangeIterator();
	}

	private class StridedRangeIterator implements Iterator {
		private int cur_ = lo;

		public boolean hasNext() {
			return cur_ < hi;
		}

		public void remove() {
			throw new Error("not implemented");
		}

		public java.lang.Object next() {
			assert hasNext();
			Point ret = Point.makeFromVarArgs(cur_);
			cur_ += stride;
			return ret;
		}
	}
}

