/*
*
* (C) Copyright IBM Corporation 2006
*
*  This file is part of X10 Language.
*
*/
package x10.array.sharedmemory;

import x10.array.Distribution_c;
import x10.array.DoubleArray;
import x10.array.Operator;
import x10.array.Region3D0Base;
import x10.lang.DoubleReferenceArray;
import x10.lang.RankMismatchException;
import x10.lang.Runtime;
import x10.lang.dist;
import x10.lang.doubleArray;
import x10.lang.place;
import x10.lang.point;
import x10.lang.region;
import x10.runtime.Configuration;

/**
* Intended to implement a dense 1d array [B:I+B].
* The underlying region must be a MultiDim region over 1d.
* A single 1-d array is allocated in the VM. Portions of
* this array logically belong to different places, as
* specified by the distribution.
* This array is NOT zero-based.
*
* Note: the region underlying the distribution may be empty. 
* @author vj
*/
public final class DoubleArray1d_c extends DoubleArray_c {

	protected final int B;
	protected final int I;

	/**
	 * This constructor must not be used directly by an application programmer.
	 * Arrays are constructed by the corresponding factory methods in
	 * x10.lang.Runtime. Assume the distribution is 3d.
	 */
	public DoubleArray1d_c(dist d, Operator.Pointwise c, boolean mutable, boolean ignored) {
		this(d, c, mutable);
	}
	private DoubleArray1d_c(dist d, Operator.Pointwise c, boolean mutable) {
		this(d, mutable, null);
		if (c != null)
			scan(this, c);
	}

	/**
	 * Create a new array per the given distribution, initialized to c.
	 *
	 * @param d
	 * @param c
	 */
	public DoubleArray1d_c(dist d, double c, boolean mutable) {
		this(d, mutable, null);
		if (c != 0.0) // optimization
			scan(this, new Constant(c));
	}

	private DoubleArray1d_c(dist d, double[] a, boolean mutable) {
		this(d, mutable, a);
	}

	private DoubleArray1d_c(dist d, boolean mutable, double[] a) {
		super(d, mutable, a);
		if (d.rank != 1)
			throw new RankMismatchException(d, 1);
		region r = d.region;
		B = r.rank(0).low();
		//if (r.size() > 0 && B != 0)
		//	throw new IllegalArgumentException("Region "+d.region+" is not 0-based");
		I = r.size()==0? -1: d.region.rank(0).high()+1;
	}

	/**
	 * Return a DoubleArray_c initialized with the given local 1-d (Java) double array.
	 *
	 * @param a
	 * @param mutable
	 * @return
	 */
	public static DoubleArray1d_c DoubleArray1d_c(double[] a, boolean mutable) {
		dist d = Runtime.factory.getDistributionFactory().local(a.length);
		return new DoubleArray1d_c(d, a, mutable);
	}

	protected void assign(DoubleArray rhs) {
		assert rhs instanceof DoubleArray3d_c;
		super.assign(rhs);
	}

	protected DoubleArray newInstance(dist d) {
		assert d instanceof Distribution_c;
		//assert d.region instanceof Region3D0Base;
		return new DoubleArray1d_c(d, (Operator.Pointwise) null, true);
	}

	protected DoubleArray newInstance(dist d, double c) {
		assert d instanceof Distribution_c;
		assert d.region instanceof Region3D0Base;
		return new DoubleArray3d_c(d, c, true);
	}

	public double get(int d0, boolean chkPl, boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0));
		if (chkAOB) {
			if (d0 < B || d0 >= I+B )
				throw new ArrayIndexOutOfBoundsException("["+d0 + "] is not in [" + B + ":" + (I+B-1) +"]");
		}
		return arr_[d0-B];
	}

	/**
	 * WARNING: does not check array bounds!
	 */
	public double get(int d0) {
		return arr_[d0-B];
	}

	public double get(int d0, int d1, int d2, boolean chkPl, boolean chkAOB) {
		throw new RankMismatchException(distribution.region, 3);
	}
	
	public double get(int d0, int d1, boolean chkPl, boolean chkAOB) {
		throw new RankMismatchException(distribution.region, 2);
	}
	public double get(int d0, int d1, int d2, int d3, boolean chkPl, boolean chkAOB) {
		throw new RankMismatchException(distribution.region, 4);
	}

	public double get(point pos, boolean chkPl, boolean chkAOB) {
		if (pos.rank != 1)
			throw new RankMismatchException(pos, pos.rank);
		int[] pos1 = pos.val();
		return get(pos1[0], chkPl, chkAOB);
	}

//	public double get(int[] pos) { return get(pos, true, true); }
//	public double get(int[] pos, boolean chkPl, boolean chkAOB) {
//		// May throw an array index out of bounds. Let it propagate through.
//		return get(pos[0], pos[1], pos[2], chkPl, chkAOB);
//	}

	public double set(double v, int d0,  boolean chkPl, boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0));
		if (chkAOB) {
			if (d0 < B  || d0 >= I+B )
				throw new ArrayIndexOutOfBoundsException();
		}
		return arr_[d0-B] = v;
	}

	public double set(double v, int d0) {
		return set(v, d0, true, true);
	}

	public double set(double v, int d0, int d1, int d2, boolean chkPl, boolean chkAOB) {
		throw new RankMismatchException(distribution.region, 1);
	}

	public double set(double v, int d0, int d1, boolean chkPl, boolean chkAOB) {
		throw new RankMismatchException(distribution.region, 2);
	}

	public double set(double v, int d0, int d1, int d2, int d3, boolean chkPl, boolean chkAOB) {
		throw new RankMismatchException(distribution.region, 4);
	}

	public double set(double a, point pos) {
		if (pos.rank != 1)
			throw new RankMismatchException(pos, pos.rank);
		int[] v = pos.val();
		return set(a, v[0], false, false);
	}

	public double set(double a, point pos, boolean chkPl, boolean chkAOB) {
		if (pos.rank != 1)
			throw new RankMismatchException(pos, pos.rank);
		int[] v = pos.val();
		return set(a, v[0], chkPl, chkAOB);
	}

	public int ord(int i, int j, int k) { return i; }
	public int[] coord(int p) {
		int[] r = new int[1];
		r[0]=p;
		return r;
	}

	public void update(x10.lang.doubleArray d) {
		assert (region.contains(d.region));
		place here = x10.lang.Runtime.runtime.currentPlace();
		try {
			// FIXME: iterate over d's region
			for (int i=B; i<I+B; i++) {
				place pl = distribution.get(i);
				x10.lang.Runtime.runtime.setCurrentPlace(pl);
				set(d.get(i), i);
			}
		} finally {
			x10.lang.Runtime.runtime.setCurrentPlace(here);
		}
	}

	public DoubleReferenceArray lift(Operator.Binary op, doubleArray arg) {
		assert arg.distribution.equals(distribution);
		DoubleArray result = newInstance(distribution);
		place here = x10.lang.Runtime.runtime.currentPlace();
		try {
			for (int i=B; i<I+B; i++) {
				place pl = distribution.get(i);
				x10.lang.Runtime.runtime.setCurrentPlace(pl);
				result.set(op.apply(this.get(i), arg.get(i)), i);
			}
		} finally {
			x10.lang.Runtime.runtime.setCurrentPlace(here);
		}
		return result;
	}

	public DoubleReferenceArray lift(Operator.Unary op) {
		DoubleArray result = newInstance(distribution);
		place here = x10.lang.Runtime.runtime.currentPlace();
		try {
			for (int i=B; i<I+B; i++) {
				place pl = distribution.get(i);
				x10.lang.Runtime.runtime.setCurrentPlace(pl);
				result.set(op.apply(this.get(i)), i);
			}
		} finally {
			x10.lang.Runtime.runtime.setCurrentPlace(here);
		}
		return result;
	}

	public double reduce(Operator.Binary op, double unit, region r) {
		double result = unit;
		place here = x10.lang.Runtime.runtime.currentPlace();
		try {
			// FIXME: iterate over r
			for (int i=B; i<I+B; i++) {
				place pl = distribution.get(i);
				x10.lang.Runtime.runtime.setCurrentPlace(pl);
				result = op.apply(result, this.get(i));
			}
		} finally {
			x10.lang.Runtime.runtime.setCurrentPlace(here);
		}
		return result;
	}

	public DoubleReferenceArray scan(Operator.Binary op, double unit) {
		double temp = unit;
		DoubleArray result = newInstance(distribution);
		place here = x10.lang.Runtime.runtime.currentPlace();
		try {
			for (int i=B; i<I+B; i++) {
				place pl = distribution.get(i);
				x10.lang.Runtime.runtime.setCurrentPlace(pl);
				temp = op.apply(this.get(i), temp);
				result.set(temp, i);
			}
		} finally {
			x10.lang.Runtime.runtime.setCurrentPlace(here);
		}
		return result;
	}

	/* 
	 * TODO: make a more efficient version of restriction().
	 */
	public DoubleReferenceArray restriction(region r) {
		return super.restriction(r);
	}

	/* 
	 * TODO: make a more efficient version of overlay().
	 */
	public DoubleReferenceArray overlay(doubleArray d) {
		return super.overlay(d);
	}

	/* 
	 * TODO: make a more efficient version of union().
	 */
	public DoubleReferenceArray union(doubleArray d) {
		return super.union(d);
	}

	public doubleArray toValueArray() {
		if (!mutable_)
			return this;
		return new DoubleArray1d_c(distribution, arr_, false);
	}
}
