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
 * Intended to implement a dense 2d array [0:I,0:J].
 * The underlying region must be a MultiDim region over 2d.
 * A single 1-d array is allocated in the VM. Portions of
 * this array logically belong to different places, as
 * specified by the distribution.
 *
 * @author gb
 */
public final class DoubleArray2d_anybase_c extends DoubleArray_c {

	protected final int I, J; /* extents */
	protected final int i_origin, j_origin; /* origins */

	/**
	 * This constructor must not be used directly by an application programmer.
	 * Arrays are constructed by the corresponding factory methods in
	 * x10.lang.Runtime. Assume the distribution is 2d.
	 */
	public DoubleArray2d_anybase_c(dist d, Operator.Pointwise c, boolean mutable, boolean ignored) {
		this(d, c, mutable);
	}
	private DoubleArray2d_anybase_c(dist d, Operator.Pointwise c, boolean mutable) {
		super(d, (Operator.Pointwise)null, mutable);
		if (d.rank != 2)
			throw new RankMismatchException(d, 2);
		// assert d.region instanceof Region3D0Base;
		//Region3D0Base r = (Region3D0Base) d.region;
		I = d.region.rank(0).high() - d.region.rank(0).low() +1;
		J = d.region.rank(1).high() - d.region.rank(1).low() +1;
		i_origin = d.region.rank(0).low();
		j_origin = d.region.rank(1).low();
		scan (this, c);
		
	}

	/**
	 * Create a new array per the given distribution, initialized to c.
	 *
	 * @param d
	 * @param c
	 */
	public DoubleArray2d_anybase_c(dist d, double c, boolean mutable) {
		super(d, c, mutable);
		if (d.rank != 2)
			throw new RankMismatchException(d, 2);
		// assert d.region instanceof Region3D0Base;
		I = d.region.rank(0).high() - d.region.rank(0).low() +1;
		J = d.region.rank(1).high() - d.region.rank(1).low() +1;
		i_origin = d.region.rank(0).low();
		j_origin = d.region.rank(1).low();
	}

	private DoubleArray2d_anybase_c(dist d, double[] a, boolean mutable) {
		super(d, a, mutable);
		if (d.rank != 2)
			throw new RankMismatchException(d, 2);
		//assert d.region instanceof Region3D0Base;
		I = d.region.rank(0).high() - d.region.rank(0).low() +1;
		J = d.region.rank(1).high() - d.region.rank(1).low() +1;
		i_origin = d.region.rank(0).low();
		j_origin = d.region.rank(1).low();
	}

	/**
	 * Return a DoubleArray_c initialized with the given local 1-d (Java) double array.
	 *
	 * @param a
	 * @param mutable
	 * @return
	 */
	public static DoubleArray2d_anybase_c DoubleArray2d_anybase_c(double[] a, boolean mutable) {
		dist d = Runtime.factory.getDistributionFactory().local(a.length);
		return new DoubleArray2d_anybase_c(d, a, mutable);
	}

	protected void assign(DoubleArray rhs) {
		assert rhs instanceof DoubleArray2d_anybase_c;
		super.assign(rhs);
	}

	protected DoubleArray newInstance(dist d) {
		assert d instanceof Distribution_c;
		//assert d.region instanceof Region2D0Base;
		return new DoubleArray2d_anybase_c(d, (Operator.Pointwise) null, true);
	}

	protected DoubleArray newInstance(dist d, double c) {
		assert d instanceof Distribution_c;
		//assert d.region instanceof Region2D0Base;
		return new DoubleArray2d_anybase_c(d, c, true);
	}

	public double get(int d0, int d1, boolean chkPl, boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0, d1));
		if (chkAOB) {
			if (d0 < i_origin || d1 < j_origin || d0 >= I+i_origin || d1 >= J+j_origin)
				throw new ArrayIndexOutOfBoundsException("["+d0+","+d1+"," +"] is not in [" + (I-1) + ","+(J-1)+"]");
		}
		return arr_[(d1 - j_origin) + J * (d0 - i_origin)]; 
	}

	/**
	 * WARNING: does not check array bounds!
	 */
	public double get(int d0, int d1) {
		return arr_[(d1 - j_origin) + J * (d0 - i_origin)]; 
	}

	public double get(int d0, boolean chkPl, boolean chkAOB) {
		throw new RankMismatchException(distribution.region, 1);
	}

	public double get(point pos, boolean chkPl, boolean chkAOB) {
		if (pos.rank != 2)
			throw new RankMismatchException(pos, 2);
		int[] pos1 = pos.val();
		return get(pos1[0], pos1[1], chkPl, chkAOB);
	}

	public double set(double v, int d0, int d1, boolean chkPl, boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0, d1));
		if (chkAOB) {
			if (d0 < i_origin || d1 < j_origin  || d0 >= I+i_origin || d1 >= J+j_origin)
				throw new ArrayIndexOutOfBoundsException();
		}
		return arr_[(d1 - j_origin) + J * (d0 - i_origin)] = v;
	}

	public double set(double v, int d0, int d1) {
		return set(v, d0, d1, true, true);
	}

	public double set(double v, int d0, boolean chkPl, boolean chkAOB) {
		throw new RankMismatchException(distribution.region, 1);
	}

	public double set(double a, point pos) {
		if (pos.rank != 2)
			throw new RankMismatchException(pos, 2);
		int[] v = pos.val();
		return set(a, v[0], v[1], false, false);
	}

	public double set(double a, point pos, boolean chkPl, boolean chkAOB) {
		if (pos.rank != 2) 
			throw new RankMismatchException(pos, 2);
		int[] v = pos.val();
		return set(a, v[0], v[1], chkPl, chkAOB);
	}

	public int ord(int i, int j) { return (j - j_origin) + J * (i - i_origin); }

	public int[] coord(int p) {
		int[] r = new int[3];
		r[0]= p/J + i_origin;
		r[1] = p % J + j_origin;
		return r;
	}

	public void update(x10.lang.doubleArray d) {
		assert (region.contains(d.region));
		place here = x10.lang.Runtime.runtime.currentPlace();
		try {
			for (int i=i_origin; i<I; i++)
				for (int j=j_origin; j<J; j++) {
						place pl = distribution.get(i, j);
						x10.lang.Runtime.runtime.setCurrentPlace(pl);
						set(d.get(i, j), i, j);
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
			for (int i=i_origin; i<I; i++)
				for (int j=j_origin; j<J; j++) {
						place pl = distribution.get(i, j);
						x10.lang.Runtime.runtime.setCurrentPlace(pl);
						result.set(op.apply(this.get(i, j), arg.get(i, j)), i, j);
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
			for (int i=i_origin; i<I; i++)
				for (int j=j_origin; j<J; j++) {
						place pl = distribution.get(i, j);
						x10.lang.Runtime.runtime.setCurrentPlace(pl);
						result.set(op.apply(this.get(i, j)), i, j);
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
			for (int i=i_origin; i<I; i++)
				for (int j=j_origin; j<J; j++) {
						place pl = distribution.get(i, j);
						x10.lang.Runtime.runtime.setCurrentPlace(pl);
						result = op.apply(this.get(i, j), result);
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
			for (int i=i_origin; i<I; i++)
				for (int j=j_origin; j<J; j++) {
						place pl = distribution.get(i, j);
						x10.lang.Runtime.runtime.setCurrentPlace(pl);
						temp = op.apply(this.get(i, j), temp);
						result.set(temp, i, j);
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
		return new DoubleArray2d_anybase_c(distribution, arr_, false);
	}
}
