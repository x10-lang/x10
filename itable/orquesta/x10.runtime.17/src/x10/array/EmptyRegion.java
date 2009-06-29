/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Jan 4, 2005
 */
package x10.array;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The empty region of rank k. There is only one unique object in each such type.
 * @author vj Jan 4, 2005
 */
public class EmptyRegion extends Region {

	/**
	 * @param rank
	 */
	public EmptyRegion(/*long*/ int rank) {
		super(rank, true,true);
	}

	/* (non-Javadoc)
	 * @see x10.runtime.region#size()
	 */
	public int size() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see x10.runtime.region#rank(long)
	 */
	public Region rank(int index) {
		assert index < rank;
		return new EmptyRegion(1);
	}

	/* (non-Javadoc)
	 * @see x10.runtime.region#isConvex()
	 */
	public boolean isConvex() {
		return true;
	}

	/* (non-Javadoc)
	 * @see x10.runtime.region#low()
	 */
	public int low() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see x10.runtime.region#high()
	 */
	public int high() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see x10.runtime.region#union(x10.runtime.region)
	 */
	public Region union(Region r) {
		assert r != null;
		if (r.rank != rank)
			throw new RankMismatchException(r, rank);
		return r;
	}

	/* (non-Javadoc)
	 * @see x10.runtime.region#intersection(x10.runtime.region)
	 */
	public Region intersection(Region r) {
		assert r != null;
		if (r.rank != rank)
			throw new RankMismatchException(r, rank);
		return this;
	}

	/* (non-Javadoc)
	 * @see x10.runtime.region#difference(x10.runtime.region)
	 */
	public Region difference(Region r) {
		assert r != null;
		if (r.rank != rank)
			throw new RankMismatchException(r, rank);
		return this;
	}

	/* (non-Javadoc)
	 * @see x10.runtime.region#convexHull()
	 */
	public Region convexHull() {
		return this;
	}

	/* (non-Javadoc)
	 * @see x10.runtime.region#contains(x10.runtime.region)
	 */
	public boolean contains(Region r) {
		if (r.rank != rank)
			throw new RankMismatchException(r, rank);
		return r.size() == 0;
	}

	/* (non-Javadoc)
	 * @see x10.runtime.region#contains(x10.runtime.point)
	 */
	public boolean contains(Point p) {
		if (p.rank != rank)
			throw new RankMismatchException(p, rank);
		return false;
	}

	// [IP] FIXME: should we be asserting this here?
	public boolean contains(int[] p) {
		assert this.rank == p.length;
		return false;
	}

	/* (non-Javadoc)
	 * @see x10.runtime.region#disjoint(x10.runtime.region)
	 */
	public boolean disjoint(Region r) {
		if (r.rank != rank)
			throw new RankMismatchException(r, rank);
		return true;
	}
	
	public Region[] partition(int n, int dim) {
		assert n > 0;
		Region[] ret = new Region[n];
		for (int i = 0; i < n; ++i) 
			ret [i] = new EmptyRegion(rank);
		return ret;
	}
		
	/* (non-Javadoc)
	 * @see x10.runtime.region#ordinal(x10.runtime.point)
	 */
	public int ordinal(Point p) throws ArrayIndexOutOfBoundsException {
		throw new ArrayIndexOutOfBoundsException();
	}

	/* (non-Javadoc)
	 * @see x10.runtime.region#coord(int)
	 */
	public Point coord(int ord) throws ArrayIndexOutOfBoundsException {
		throw new ArrayIndexOutOfBoundsException();
	}

	/* (non-Javadoc)
	 * @see x10.runtime.region#iterator()
	 */
	public Iterator iterator() {

		class RegionIterator implements Iterator {

			public boolean hasNext() {
				return false;
			}

			public void remove() {
				throw new Error("not implemented");
			}

			public java.lang.Object next() {
				throw new Error("No such element.");
			}
		}

		return new Iterator() {
			public boolean hasNext() {
				return false;
			}
			public void remove() throws UnsupportedOperationException {
				throw new UnsupportedOperationException();
			}
			public java.lang.Object next() throws NoSuchElementException {
				throw new NoSuchElementException();
			}
		};
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "EmptyRegion("+rank+")";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(java.lang.Object o) {
		boolean ret;
		if (o == null || !(o instanceof Region))
			ret = false;
		else {
			Region oe = (Region) o;
			ret = (oe.rank == this.rank) && oe.size() == 0;
		}
		return ret;
	}

	
}

