/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.lang;

import x10.annotations.NoSideEffects;
import x10.compilergenerated.Parameter1;

abstract public /*value*/ class point/*(region region)*/ extends java.lang.Object
	implements Indexable, ValueType, Parameter1
{
	//public final region region;
	/*parameter*/ public final /*nat*/int rank;

	abstract public static /*value*/ class factory implements ValueType {
		
		/**
		 * Return a point with the given coordinates.
		 */
		abstract public @NoSideEffects point point(int[/*rank*/] val);
		/**
		 * Return a point of rank 1 with the given coordinates, created in the
		 * smallest region that can enclose it.
		 */
		public @NoSideEffects point point(int v1) {
			return point(new int[] { v1 });
		}
		public @NoSideEffects point allZero(int d) {
			return point(new int[d]);
		}
		/**
		 * Return a point of rank 2 with the given coordinates, created in the
		 * smallest region that can enclose it.
		 */
		public @NoSideEffects point point(int v1, int v2) {
			return point(new int[] { v1, v2 });
		}
		/**
		 * Return a point of rank 3 with the given coordinates, created in the
		 * smallest region that can enclose it.
		 */
		public @NoSideEffects point point(int v1, int v2, int v3) {
			return point(new int[] { v1, v2, v3 });
		}
		/**
		 * Return a point of rank 4 with the given coordinates, created in the
		 * smallest region that can enclose it.
		 */
		public @NoSideEffects point point(int v1, int v2, int v3, int v4) {
			return point(new int[] { v1, v2, v3, v4 });
		}
		/**
		 * Return a point of rank 5 with the given coordinates, created in the
		 * smallest region that can enclose it.
		 */
		public @NoSideEffects point point(int v1, int v2, int v3, int v4, int v5) {
			return point(new int[] { v1, v2, v3, v4, v5 });
		}
	}
	public static final factory factory = Runtime.factory.getPointFactory();

	protected point(/*region region*/int rank) {
		//this.region = region;
		this.rank = rank;
	}
	/**
	 * Return the value of this point on the i'th dimension.
	 */
	abstract public int get(/*nat*/ int i);

	/**
	 * Return true iff the point is on the upper boundary of the i'th
	 * dimension.
	 */
	abstract public boolean onUpperBoundary(/*nat*/ int i, region r);

	/**
	 * Return true iff the point is on the lower boundary of the i'th
	 * dimension.
	 */
	abstract public boolean onLowerBoundary(/*nat*/ int i, region r);

	abstract public boolean gt(point/*(region)*/ p);
	abstract public boolean lt(point/*(region)*/ p);
	abstract public boolean ge(point/*(region)*/ p);
	abstract public boolean le(point/*(region)*/ p);

	abstract public point/*(region)*/ neg();
	abstract public point/*(region)*/ add(point/*(region)*/ p);
	public point/*(region)*/ sub(point/*(region)*/ p) { return add(p.neg()); }
	abstract public point/*(region)*/ mul(point/*(region)*/ p);
	abstract public point/*(region)*/ div(point/*(region)*/ p);
	private static point/*(region)*/ toPoint(int rank, int c) {
		int[] a = new int[rank];
		for (int i = 0; i < rank; i++) a[i] = c;
		return factory.point(a);
	}
	public point/*(region)*/ add(int c) { return add(toPoint(rank, c)); }
	public point/*(region)*/ sub(int c) { return add(-c); }
	public point/*(region)*/ mul(int c) { return mul(toPoint(rank, c)); }
	public point/*(region)*/ div(int c) { return div(toPoint(rank, c)); }
	public point/*(region)*/ invadd(int c) { return add(c); }
	public point/*(region)*/ invsub(int c) { return add(-c).neg(); }
	public point/*(region)*/ invmul(int c) { return mul(c); }
	public point/*(region)*/ invdiv(int c) { return toPoint(rank, c).div(this); }
	
	@NoSideEffects
	public abstract boolean isZero();
	
	abstract public point/*(region+p.region)*/ concat(point p);
	/**
	 * Returns true if this is prefixed by p, i.e. for some q, this= p.concat(q).
	 * @param p
	 * @return
	 */
	abstract public boolean prefixedBy(point p);
	/**
	 * Throw a RankMismatchException if n > rank. Otherwise return a new point with rank rank-n
	 * whose coordinates are the last rank-n indices of this.
	 * @param p
	 * @return
	 */
	abstract public point suffix(int n) throws RankMismatchException;
	
	/** 
	 * Throw a RankMismatchException if n > rank. Otherwise return a new point of rank-1 with
	 * the value of the dim'th dimension elided.
	 * 
	 */
	
	abstract public point project(int dim) throws RankMismatchException;
	
	abstract public int[] val();
}

