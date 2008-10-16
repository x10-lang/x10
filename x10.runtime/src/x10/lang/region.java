/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.lang;

import java.lang.System;
import java.util.Iterator;

import x10.base.TypeArgument;

/**
 * A region represents a (sparse or dense) k-dimensional space of
 * points. A region is a dependent class, with the value parameter
 * specifying the dimension of the region.  A convex k-dimensional
 * region is easy to represent, e.g. as a list of k (min, max)
 * pairs. In general, regions may not be convex. (For instance
 * difference(region(dimension) r) produces non-convex regions.)
 * Non-convex regions are very important for many physical
 * problems. For instance the region of the halo around a 2-d array is
 * non-convex.
 *
 * @author vj
 */
public abstract /*value*/ class region extends Object
	implements TypeArgument, ValueType, Iterable<point>
{
	public static final int UNKNOWN = 0;
	public static final int RANGE = 1;
	public static final int ARBITRARY = 2;
	public static final int BANDED = 3;
	public static final int MULTIDIM = 4;
	public static final int TRIANGULAR = 5;

	// nat is translated to int for now.
	/*property*/ public final /*nat*/ /*long*/ int rank;
	/*property*/ public final boolean rect;
	/**
	 * A k-ary region is zeroBased if it is declared to be a
	 * rectangular region (of rank k), and [0,...0] is the smallest point
	 * in it.
	 */
	/*property*/ public final boolean zeroBased;
	/*property*/ public final boolean rail;
	/*property*/ public final boolean colMajor;

	public static final String propertyNames$ = " rank rect zeroBased rail colMajor ";

	public static abstract /*value*/ class factory implements ValueType {
		/**
		 * Create a region of zero ranks. This is an empty region of size 0.
		 */
		public abstract region /*(k)*/ emptyRegion(/*nat long*/ int k);

		/**
		 * Construct a 1-dimensional region 1..high, with stride 1.
		 */
		public region/*(1)*/ region(int high) {
			return region(1, high, 1);
		}

		/**
		 * Construct a 1-dimensional region low..high with stride 1.
		 */
		final public region/*(1)*/ region(int low, int high) {
			final region/*(1)*/ result = region(low, high, 1);
			assert result.rank == 1;
			return result; // region(low, high, 1);
		}

		/**
		 * Construct a 1-dimensional region, low..high with the given stride.
		 */
		public abstract region/*(1)*/ region(int low, int high, int stride);

		public region/*(2)*/ upperTriangular(/*nat*/ int size) {
			final region/*(2)*/ result = upperTriangular(2, size);
			assert result.rank==2;
			return result;
		}

		public region/*(2)*/ lowerTriangular(/*nat*/ int size) {
			final region/*2*/ result = lowerTriangular(2, size);
			assert result.rank==2;
			return result;
		}

		public region/*(2)*/ banded(/*nat*/ int size, /*nat*/ int width) {
			final region/*(2)*/ result = banded(2, size, width);
			assert result.rank==2;
			return result;
		}

		public region/*2*/ region(region a, region b) {
			return region(new region[] { a, b });
		}

		public region/*1*/ region(region a) {
			region ret;
			//if (a == null)
			//	return new EmptyRegion(1);
			ret = a; // TODO: cvp --> vj why this: region(new region[] { a });
			return ret;
		}

		public region/*3*/ region(region a, region b, region c) {
			return region(new region[] { a, b,c });
		}

		public region/*4*/ region(region a, region b, region c, region d) {
			return region(new region[] { a, b,c, d });
		}

		public region/*4*/ region(region a, region b, region c, region d, region e) {
			return region(new region[] { a, b,c, d,e });
		}

		/**
		 * Construct a region, using the list of region(1)'s passed as
		 * arguments to the constructor.
		 */
		public abstract region/*(regions.length)*/ region(region/*(1)*/[] regions);

		/**
		 * Return an <code>upperTriangular</code> region for a dim-dimensional
		 * space of size <code>size</code> in each dimension.
		 */
		public abstract region/*(dim)*/ upperTriangular(/*nat*/ int dim, /*nat*/ int size);

		/**
		 * Return a lowerTriangular region for a dim-dimensional space of
		 * size <code>size</code> in each dimension.
		 */
		public abstract region/*(dim)*/ lowerTriangular(/*nat*/ int dim, /*nat*/ int size);

		/**
		 * Return a banded region of width <code>width</code> for a
		 * dim-dimensional space of size <code>size</code> in each dimension.
		 */
		public abstract region/*(dim)*/ banded(/*nat*/ int dim, /*nat*/ int size, /*nat*/ int width);
	}

	public static final factory factory = Runtime.factory.getRegionFactory();

	protected region(/*nat long*/ int rank, boolean rect, boolean zeroB) {
		this(rank, rect, zeroB, false);
	}

	protected region(/*nat long*/ int rank, boolean rect, boolean zeroB, boolean colMajor) {
		assert rank >= 1;
		this.rank = rank;
		this.rect = rect;
		this.zeroBased = zeroB;
		rail = rank==1 && rect&&zeroBased;
		this.colMajor = colMajor;
	}

	/**
	 * Return the number of points in this region.
	 */
	abstract public /*nat*/int size();

	/**
	 * Use modular arithmetic on the index to determine the dimension
	 * to return.
	 */
	abstract public region/*(1)*/ rank(/*nat*/int index);

	/**
	 * Returns true iff the region contains every point between two
	 * points in the region.
	 */
	abstract public boolean isConvex();

	/**
	 * Return the low bound for a 1-dimensional region. Can only be
	 * invoked on 1-dimensional objects. Throws an exception if
	 * size of the region is 0.
	 */
	abstract public /*(:rank==1)*/ int low() throws UnsupportedOperationException;

	/**
	 * Return the high bound for a 1-dimensional region. Can only be
	 * invoked on 1-dimensional objects. Thrwos an exception if
	 * size of the region is 0.
	 */
	abstract public /*(:rank==1)*/ int high() throws UnsupportedOperationException;

	abstract public region/*(rank)*/ union(region/*(rank)*/ r);
	abstract public region/*(rank)*/ intersection(region/*(rank)*/ r);
	abstract public region/*(rank)*/ difference(region/*(rank)*/ r);
	abstract public region/*(rank)*/ convexHull();

	public point startPoint() {
		Iterator<point> it = iterator();
		return (it.hasNext() ? (point) it.next() : null);
	}

	/**
	 * Returns true iff this is a superset of r.
	 */
	public boolean contains(region r) {
		boolean ret = true;
		for (Iterator it = r.iterator(); ret && it.hasNext(); ) {
			point p = (point) it.next();
			if (!contains(p))
				ret = false;
		}
		return ret;
	}

	/**
	 * Returns true iff this region contains this point.
	 */
	abstract public boolean contains(point/*(rank)*/ p);
	abstract public boolean contains(int[] p);

	/**
	 * Returns true iff this is disjoint from r.
	 */
	abstract public boolean disjoint(region/*(rank)*/ r);

	/**
	 * Returns true iff the set of points in r and this are equal.
	 */
	public boolean equal(region/*(rank)*/ r) {
		return this.contains(r) && r.contains(this);
	}

	/**
	 * @param p a point in the coordinate space
	 * @return the ordinal number of the point [0 ... size()[
	 */
	abstract public /*nat*/int ordinal(point/*(rank)*/ p) throws ArrayIndexOutOfBoundsException;

	/**
	 * @param ord the ordinal number, must be smaller than size()
	 * @return the coordinate that has ordinal number ord
	 */
	public abstract point/*(rank)*/ coord(/*nat*/ int ord) throws ArrayIndexOutOfBoundsException;

	/**
	 * @return Iterator that yields the individual points of a region in
	 * lexicographical order.
	 */
	abstract public Iterator<point> iterator();

	abstract public String toString();
	
	/**
	 * Return the region with the dim'th dimension projected out. 
	 * @param p
	 * @return
	 */
	public abstract region/*(this.rank-p.rank)*/ project(int dim);
	

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(java.lang.Object o) {
		if (!(o instanceof region)) return false;
		boolean ret = (o == this);
		if (!ret) {
			region rhs = (region) o;
			if (rhs.rank == rank && rhs.size() == size()) {
				ret = true;
				for (Iterator it = iterator(); it.hasNext(); ) {
					point p = (point) it.next();
					if (!rhs.contains(p)) {
						ret = false;
						break;
					}
				}
			}
		}
		return ret;
	}

	public final int hashCode() {
		return rank;
	}

	/**
	 * @return a set of regions that are a partition of n consecutive chunks of
	 * the original region (this).
	 */
	public region[] partition(int n, int dim) {
		System.err.println("this should not be called");
		// the runtime library should never call this. -- and is should also
		// not be in the public interface that is available to the X10 programmer
		// (the reason is show here is because runtime implementation and
		// X10 standard library interface are the same in the current prototype)
		assert (false);
		return null;
	}

	public dist/*(:region=this)*/ toDistribution() {
		return dist.factory.local(this);
	}
	
	public int rank() {
		return rank;
	}
	public boolean rect() {
		return rect;
	}
	public boolean zeroBased() {
		return zeroBased;
	}
	public boolean rail() {
		return rail;
	}

}

