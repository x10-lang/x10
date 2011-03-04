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

import java.util.Iterator;
import java.util.HashSet;

import x10.lang.point;
import x10.lang.region;
import x10.lang.RankMismatchException;

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

	public point coord(/*nat*/int ord) {
		assert ord < size;
		int val = lo + ord * stride;
		return point.factory.point(new int[] { val });
	}

	public /*nat*/int ordinal(point p) {
		assert contains(p);
		int val = p.get(0);
		return (val - lo) % stride;
	}

	public region union(region r) {
		if (r.rank != 1)
			throw new RankMismatchException(r, 1);
		HashSet<point> set = new HashSet<point>();
	
		for (point p : this) set.add(p);
		return new ArbitraryRegion(1, set);
	}

	public region intersection(region r) {
		if (r.rank != 1)
			throw new RankMismatchException(r, 1);
		HashSet<point> set = new HashSet<point>();
		for (point p : this) 
			if (r.contains(p)) 
				set.add(p);
		return new ArbitraryRegion(1, set);
	}

	/**
	 * Returns this range less the points that are in r.
	 * If r is not one dimensional, then this is returned.
	 */
	public region difference(region r) {
		assert r != null;
		// super.difference() will check the rank
		return super.difference(r);
	}

	// [IP] FIXME: should we throw a RankMismatchException here?
	public boolean contains(point p) {
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

	public region convexHull() {
		if (stride == 1)
			return this;
		return new ContiguousRange(lo, hi);
	}

	public boolean disjoint(region r) {
		if (r.rank != 1) throw new RankMismatchException(r, 1);
		for (point p : r)
			if (contains(p))
				return false;
		return true;
	}

	public boolean contains(region r) {
		if (r.rank != 1) throw new RankMismatchException(r, 1);
		for (point p : r)
			if (! contains(p))
				return false;
		return true;
	}
	public region project(int dim) {
		if (dim != 0) throw new RankMismatchException(this, dim);
		return new EmptyRegion(0);
	}

	public Iterator<point> iterator() {
		return new StridedRangeIterator();
	}

	private class StridedRangeIterator implements Iterator<point> {
		private int cur_ = lo;

		public boolean hasNext() {
			return cur_ < hi;
		}

		public void remove() {
			throw new Error("not implemented");
		}

		public point next() {
			assert hasNext();
			point ret = point.factory.point(new int[] { cur_ });
			cur_ += stride;
			return ret;
		}
	}
}

