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
 * Intended to implement a dense 3d array [0:I,0:J,0:K].
 * The underlying region must be a MultiDim region over 3d.
 * A single 1-d array is allocated in the VM. Portions of
 * this array logically belong to different places, as
 * specified by the distribution.
 *
 * @author vj
 */
public final class DoubleArray3d_c extends DoubleArray_c {

	protected final int I, J, K;

	/**
	 * This constructor must not be used directly by an application programmer.
	 * Arrays are constructed by the corresponding factory methods in
	 * x10.lang.Runtime. Assume the distribution is 3d.
	 */
	public DoubleArray3d_c(dist d, Operator.Pointwise c, boolean mutable, boolean ignored) {
		this(d, c, mutable);
	}
	private DoubleArray3d_c(dist d, Operator.Pointwise c, boolean mutable) {
		super(d, c, mutable);
		if (d.rank != 3)
			throw new RankMismatchException(d, 3);
		// assert d.region instanceof Region3D0Base;
		//Region3D0Base r = (Region3D0Base) d.region;
		if (d.region.rank(0).low() != 0 ||
				d.region.rank(1).low() != 0 ||
				d.region.rank(2).low() != 0)
			throw new IllegalArgumentException("Region "+d.region+" is not 0-based");
		I = d.region.rank(0).high()+1;
		J = d.region.rank(1).high()+1;
		K = d.region.rank(2).high()+1;
	}

	/**
	 * Create a new array per the given distribution, initialized to c.
	 *
	 * @param d
	 * @param c
	 */
	public DoubleArray3d_c(dist d, double c, boolean mutable) {
		// FIXME: initialization needs to use the field values
		super(d, c, mutable);
		if (d.rank != 3)
			throw new RankMismatchException(d, 3);
		// assert d.region instanceof Region3D0Base;
		if (d.region.rank(0).low() != 0 ||
				d.region.rank(1).low() != 0 ||
				d.region.rank(2).low() != 0)
			throw new IllegalArgumentException("Region "+d.region+" is not 0-based");
		I = d.region.rank(0).high()+1;
		J = d.region.rank(1).high()+1;
		K = d.region.rank(2).high()+1;
	}

	private DoubleArray3d_c(dist d, double[] a, boolean mutable) {
		super(d, a, mutable);
		if (d.rank != 3)
			throw new RankMismatchException(d, 3);
		//assert d.region instanceof Region3D0Base;
		if (d.region.rank(0).low() != 0 ||
				d.region.rank(1).low() != 0 ||
				d.region.rank(2).low() != 0)
			throw new IllegalArgumentException("Region "+d.region+" is not 0-based");
		I = d.region.rank(0).high()+1;
		J = d.region.rank(1).high()+1;
		K = d.region.rank(2).high()+1;
	}

	/**
	 * Return a DoubleArray_c initialized with the given local 1-d (Java) double array.
	 *
	 * @param a
	 * @param mutable
	 * @return
	 */
	public static DoubleArray3d_c DoubleArray3d_c(double[] a, boolean mutable) {
		dist d = Runtime.factory.getDistributionFactory().local(a.length);
		return new DoubleArray3d_c(d, a, mutable);
	}

	protected void assign(DoubleArray rhs) {
		assert rhs instanceof DoubleArray3d_c;
		super.assign(rhs);
	}

	protected DoubleArray newInstance(dist d) {
		assert d instanceof Distribution_c;
		assert d.region instanceof Region3D0Base;
		return new DoubleArray3d_c(d, (Operator.Pointwise) null, true);
	}

	protected DoubleArray newInstance(dist d, double c) {
		assert d instanceof Distribution_c;
		assert d.region instanceof Region3D0Base;
		return new DoubleArray3d_c(d, c, true);
	}

	public double get(int d0, int d1, int d2, boolean chkPl, boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0, d1, d2));
		if (chkAOB) {
			if (d0 < 0 || d1 < 0 || d2 < 0 || d0 >= I || d1 >= J || d2 >= K)
				throw new ArrayIndexOutOfBoundsException("["+d0+","+d1+"," + d2+"] is not in [" + (I-1) + ","+(J-1)+","+(K-1)+"]");
		}
		return arr_[d2 + K*(d1 + J*d0)];
	}

	/**
	 * WARNING: does not check array bounds!
	 */
	public double get(int d0, int d1, int d2) {
		return arr_[d2 + K*(d1 + J*d0)];
	}

	public double get(int d0, boolean chkPl, boolean chkAOB) {
		throw new RankMismatchException(distribution.region, 1);
	}
	public double get(int d0, int d1, boolean chkPl, boolean chkAOB) {
		throw new RankMismatchException(distribution.region, 2);
	}
	public double get(int d0, int d1, int d2, int d3, boolean chkPl, boolean chkAOB) {
		throw new RankMismatchException(distribution.region, 4);
	}

	public double get(point pos, boolean chkPl, boolean chkAOB) {
		if (pos.rank != 3)
			throw new RankMismatchException(pos, 3);
		int[] pos1 = pos.val();
		return get(pos1[0], pos1[1], pos1[2], chkPl, chkAOB);
	}

//	public double get(int[] pos) { return get(pos, true, true); }
//	public double get(int[] pos, boolean chkPl, boolean chkAOB) {
//		// May throw an array index out of bounds. Let it propagate through.
//		return get(pos[0], pos[1], pos[2], chkPl, chkAOB);
//	}

	public double set(double v, int d0, int d1, int d2, boolean chkPl, boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0, d1, d2));
		if (chkAOB) {
			if (d0 < 0 || d1 < 0 || d2 < 0 || d0 >= I || d1 >= J || d2>= K)
				throw new ArrayIndexOutOfBoundsException();
		}
		return arr_[d2 + K*(d1 + J*d0)] = v;
	}

	public double set(double v, int d0, int d1, int d2) {
		return set(v, d0, d1, d2, true, true);
	}

	public double set(double v, int d0, boolean chkPl, boolean chkAOB) {
		throw new RankMismatchException(distribution.region, 1);
	}

	public double set(double v, int d0, int d1, boolean chkPl, boolean chkAOB) {
		throw new RankMismatchException(distribution.region, 2);
	}

	public double set(double v, int d0, int d1, int d2, int d3, boolean chkPl, boolean chkAOB) {
		throw new RankMismatchException(distribution.region, 4);
	}

	public double set(double a, point pos) {
		if (pos.rank != 3)
			throw new RankMismatchException(pos, 3);
		int[] v = pos.val();
		return set(a, v[0], v[1], v[2], false, false);
	}

	public double set(double a, point pos, boolean chkPl, boolean chkAOB) {
		if (pos.rank != 3)
			throw new RankMismatchException(pos, 3);
		int[] v = pos.val();
		return set(a, v[0], v[1], v[2], chkPl, chkAOB);
	}

	public int ord(int i, int j, int k) { return k+K*(j+J*i); }
	public int[] coord(int p) {
		int[] r = new int[3];
		r[2]= p%K; p=p/K; r[1]=p%J; p=p/J; r[0]=p;
		return r;
	}

	public void update(x10.lang.doubleArray d) {
		assert (region.contains(d.region));
		place here = x10.lang.Runtime.runtime.currentPlace();
		try {
			for (int i=0; i<I; i++)
				for (int j=0; j<J; j++)
					for (int k=0; k<K; k++) {
						place pl = distribution.get(i, j, k);
						x10.lang.Runtime.runtime.setCurrentPlace(pl);
						set(d.get(i, j, k), i, j, k);
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
			for (int i=0; i<I; i++)
				for (int j=0; j<J; j++)
					for (int k=0; k<K; k++) {
						place pl = distribution.get(i, j, k);
						x10.lang.Runtime.runtime.setCurrentPlace(pl);
						result.set(op.apply(this.get(i, j, k), arg.get(i, j, k)), i, j, k);
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
			for (int i=0; i<I; i++)
				for (int j=0; j<J; j++)
					for (int k=0; k<K; k++) {
						place pl = distribution.get(i, j, k);
						x10.lang.Runtime.runtime.setCurrentPlace(pl);
						result.set(op.apply(this.get(i, j, k)), i, j, k);
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
			for (int i=0; i<I; i++)
				for (int j=0; j<J; j++)
					for (int k=0; k<K; k++) {
						place pl = distribution.get(i, j, k);
						x10.lang.Runtime.runtime.setCurrentPlace(pl);
						result = op.apply(result, this.get(i, j, k));
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
			for (int i=0; i<I; i++)
				for (int j=0; j<J; j++)
					for (int k=0; k<K; k++) {
						place pl = distribution.get(i, j, k);
						x10.lang.Runtime.runtime.setCurrentPlace(pl);
						temp = op.apply(this.get(i, j, k), temp);
						result.set(temp, i, j, k);
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
		return new DoubleArray3d_c(distribution, arr_, false);
	}
}
