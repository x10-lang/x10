/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.array;

import x10.annotations.NoSideEffects;
import x10.lang.Indexable;
import x10.lang.point;
import x10.lang.region;
import x10.lang.RankMismatchException;

public final class point_c extends point implements Comparable {

	public static class factory extends point.factory {
		public @NoSideEffects point/*(region)*/ point(int[/*rank*/] val) {
			point p = new point_c(val);
			return p;
		}
	}

	final int[] val;
	final int hash_;

	@NoSideEffects
	public boolean isZero() {
		for (int i : val) 
			if (i != 0) return false;
		return true;
	}
	/**
	 * Is this indexable value-equals to the other indexable?
	 * @param other
	 * @return true if these objects are value-equals
	 */
	public boolean valueEquals(Indexable other) {
		if (! (other instanceof point_c))
			return false;
		point_c op = (point_c) other;
		if ((op.hash_ == hash_) && (op.val.length == val.length)) {
			for (int i = val.length-1; i >= 0; i--)
				if (val[i] != op.val[i])
					return false;
			return true;
		} else
			return false;
	}

	public int[] val() { return val; }

	public point_c(int[] val) {
		super(val.length);
		// assert region.contains(val); -- CVP omit, leads to a cyclic dependency and hence infinite recusions.
		// make a copy of the array!
		this.val = new int[val.length];
		System.arraycopy(val, 0, this.val, 0, val.length);

		// compute hash
		int b = 378551;
		int a = 63689;
		int hash_tmp = 0;
		for (int i = 0; i < val.length; i++) {
			hash_tmp = hash_tmp * a + val[i];
			a = a * b;
		}
		hash_ = hash_tmp;

	}

	/**
	 * Return the value of this point on the i'th dimension.
	 */
	public int get(/*nat*/int i) {
		if (i < 0 || i >= rank)
			throw new ArrayIndexOutOfBoundsException();
		return val[i];
	}

	/**
	 * Return true iff the point is on the upper boundary of the i'th
	 * dimension of its region.
	 */
	public boolean onUpperBoundary(/*nat*/int i, region r) {
		return r.rank(i).high() == get(i);
	}

	/**
	 * Return true iff the point is on the lower boundary of the i'th
	 * dimension.
	 */
	public boolean onLowerBoundary(/*nat*/int i, region r) {
		return r.rank(i).low() == get(i);
	}

	public boolean isValue() {
		return true; // points are values!
	}

	/*
	 * This method overrideds superclass implementation - the argument
	 * must be of type java.lang.Object.
	 */
	// [IP] FIXME: should we throw a RankMismatchException here?
	public boolean equals(java.lang.Object o) {
		assert o != null;
		boolean ret = false;

		if (o.getClass() == point_c.class) {
			point_c tmp = (point_c) o;
			if (tmp.rank == rank) {
				ret = true;
				for (int i = 0; i < val.length; ++i) {
					if (val[i] != tmp.val[i]) {
						ret = false;
						break;
					}
				}
			}
		}
		return ret;
	}

	public int hashCode() {
		return hash_;
	}

	/* lexicographical ordering */
	public int compareTo(java.lang.Object o) {
		assert o.getClass() == point_c.class;
		point_c tmp = (point_c) o;
		if (tmp.rank != rank)
			throw new RankMismatchException(tmp, rank);

		int res = 0;
		// row major ordering (C conventions)
		for (int i = 0; res == 0 && i < rank; ++i) {
			int t1 = val[i], t2 = tmp.val[i];
			if (t1 < t2)
				res = -1;
			else if (t1 > t2)
				res = 1;
		}
		return res;
	}

	public boolean gt(point p) {
		return compareTo(p) == 1;
	}
	public boolean lt(point p) {
		return compareTo(p) == -1;
	}
	public boolean ge(point p) {
		return compareTo(p) >= 0;
	}
	public boolean le(point p) {
		return compareTo(p) <= 0;
	}

	public point/*(region)*/ neg() {
		int array[] = new int[val.length];
		for (int i = 0; i < val.length; i++)
			array[i] = -val[i];
		return factory.point(array);
	}

	public point/*(region)*/ add(point/*(region)*/ p) {
		if (val.length != p.rank)
			throw new RankMismatchException(p, rank);
		int array[] = new int[val.length];
		for (int i = 0; i < val.length; i++)
			array[i] = val[i] + p.get(i);
		return factory.point(array);
	}

	public point/*(region)*/ sub(point/*(region)*/ p) {
		if (val.length != p.rank)
			throw new RankMismatchException(p, rank);
		int array[] = new int[val.length];
		for (int i = 0; i < val.length; i++)
			array[i] = val[i] - p.get(i);
		return factory.point(array);
	}

	public point/*(region)*/ mul(point/*(region)*/ p) {
		if (val.length != p.rank)
			throw new RankMismatchException(p, rank);
		int array[] = new int[val.length];
		for (int i = 0; i < val.length; i++)
			array[i] = val[i] * p.get(i);
		return factory.point(array);
	}

	public point/*(region)*/ div(point/*(region)*/ p) {
		if (val.length != p.rank)
			throw new RankMismatchException(p, rank);
		int array[] = new int[val.length];
		// The loop below may also throw an ArithmeticException
		for (int i = 0; i < val.length; i++)
			array[i] = val[i] / p.get(i);
		return factory.point(array);
	}

	public point/*(region)*/ add(int c) {
		int array[] = new int[val.length];
		for (int i = 0; i < val.length; i++)
			array[i] = val[i] + c;
		return factory.point(array);
	}

	public point/*(region)*/ mul(int c) {
		int array[] = new int[val.length];
		for (int i = 0; i < val.length; i++)
			array[i] = val[i] * c;
		return factory.point(array);
	}

	public point/*(region)*/ div(int c) {
		int array[] = new int[val.length];
		// The loop below may also throw an ArithmeticException
		for (int i = 0; i < val.length; i++)
			array[i] = val[i] / c;
		return factory.point(array);
	}

	public point/*(region)*/ invsub(int c) {
		int array[] = new int[val.length];
		for (int i = 0; i < val.length; i++)
			array[i] = c - val[i];
		return factory.point(array);
	}

	public point/*(region)*/ invdiv(int c) {
		int array[] = new int[val.length];
		// The loop below may also throw an ArithmeticException
		for (int i = 0; i < val.length; i++)
			array[i] = c / val[i];
		return factory.point(array);
	}

	@Override
	public point/*(region+p.region)*/ concat(point p) {
		int[] pval = p.val();
		int[]  a = new int[val.length + pval.length];
		System.arraycopy(val, 0,  a, 0, val.length-1);
		System.arraycopy(pval, 0,  a, val.length, pval.length-1);
		return new point_c(a);
		
	}
	@Override
	public boolean prefixedBy(point p) {
		if (p.rank > rank) return false;
		int[] pa = p.val();
		for (int i=0; i < pa.length; ++i)
			if (pa[i] != val[i])
				return false;
		return true;
		
	}
	@Override
		public point suffix(int n) throws RankMismatchException {
		if (n > rank) throw new RankMismatchException(this,n);
		int[] result = new int[rank-n];
		for (int i=0; i < result.length; ++i)
			System.arraycopy(val, n, result, 0, rank-n);
		return new point_c(result);
		
	}
	
	@Override
		public point project(int dim) throws RankMismatchException {
		if (dim > rank) throw new RankMismatchException(this, dim);
		int[] result = new int[rank-1];
		System.arraycopy(val, 0, result, 0, dim);
		System.arraycopy(val, dim+1, result, dim, rank-1-dim);
		return new point_c(result);
		
	}
	@Override
	public point expand(int dim, int val) throws RankMismatchException {
		if (dim < 0 || dim > rank+1) throw new RankMismatchException(this, dim);
		int[] result = new int[rank+1];
		for (int i=0; i <= rank; i++) {
			result[i] = i < dim? get(i) : (i==dim ? val : get(i-1));
		}
		return new point_c(result);
	}
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (int i = 0; i < val.length; ++i) {
			sb.append(val[i]);
			if (i < val.length -1)
				sb.append(",");
		}
		sb.append("]");
		return sb.toString();
	}
}

