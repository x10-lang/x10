/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on 17.10.2006
 */
package x10.array.sharedmemory;

import java.util.Iterator;

import x10.array.Distribution_c;
import x10.array.Helper;
import x10.array.IntArray;
import x10.array.Operator;
import x10.lang.IntReferenceArray;
import x10.lang.RankMismatchException;
import x10.lang.Runtime;
import x10.lang.dist;
import x10.lang.intArray;
import x10.lang.place;
import x10.lang.point;
import x10.lang.region;
import x10.runtime.Configuration;

/**
 * A class to represent contiguous 1-dimensional integer arrays.
 * 
 * @author igor
 */
public class IntArray1d_c extends IntArray_c {

	public final int low_;

	public IntArray1d_c(dist d, Operator.Pointwise c, boolean mutable, boolean ignored) {
		super(d, c, mutable, ignored);
		assert (d.rank == 1);
		low_ = d.region.low();
	}

	public IntArray1d_c(dist d, int[] a, boolean mutable) {
		super(d, a, mutable);
		assert (d.rank == 1);
		low_ = d.region.low();
	}

	protected IntArray newInstance(dist d) {
		return new IntArray1d_c(d, null, true);
	}

	protected IntArray newInstance(dist d, int c) {
		return new IntArray1d_c(d, new Constant(c), true, false);
	}

	public int get(int d0, boolean chkPl, boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0));
		return arr_[d0 - low_];
	}

	public int get(int d0) {
		return get(d0, true, true);
	}

	public int get(int d0, int d1, boolean chkPl, boolean chkAOB) {
		throw new RankMismatchException(distribution.region, 2);
	}

	public int get(int d0, int d1, int d2, boolean chkPl, boolean chkAOB) {
		throw new RankMismatchException(distribution.region, 3);
	}

	public int get(int d0, int d1, int d2, int d3, boolean chkPl, boolean chkAOB) {
		throw new RankMismatchException(distribution.region, 4);
	}

	public int get(point pos, boolean chkPl, boolean chkAOB) {
		if (pos.rank != 1)
			throw new RankMismatchException(pos, 1);
		return get(pos.get(0), chkPl, chkAOB);
	}

	public int set(int v, int d0, boolean chkPl, boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0));
		return arr_[d0 - low_] = v;
	}

	public int set(int v, int d0) {
		return set(v, d0, true, true);
	}

	public int set(int v, int d0, int d1, boolean chkPl, boolean chkAOB) {
		throw new RankMismatchException(distribution.region, 2);
	}

	public int set(int v, int d0, int d1, int d2, boolean chkPl, boolean chkAOB) {
		throw new RankMismatchException(distribution.region, 3);
	}

	public int set(int v, int d0, int d1, int d2, int d3, boolean chkPl, boolean chkAOB) {
		throw new RankMismatchException(distribution.region, 4);
	}

	public int set(int v, point pos, boolean chkPl, boolean chkAOB) {
		if (pos.rank != 1)
			throw new RankMismatchException(pos, 1);
		return set(v, pos.get(0), chkPl, chkAOB);
	}

	public Object toJava() {
		final int[] ret = new int[arr_.length];
		System.arraycopy(arr_, 0, ret, 0, ret.length);
		return ret;
	}
}
