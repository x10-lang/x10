/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Jun 1st, 2005
 */
package x10.array.sharedmemory;

import x10.array.IntArray;
import x10.array.Operator;
import x10.array.Region0Base;
import x10.array.Operator.Binary;
import x10.array.Operator.Pointwise;
import x10.array.Operator.Unary;
import x10.lang.IntReferenceArray;
import x10.lang.Runtime;
import x10.lang.dist;
import x10.lang.point;
import x10.runtime.Configuration;

/**
 * @author CMD
 */
public class ZeroBasedIntArray_c extends IntArray1d_c {

	public ZeroBasedIntArray_c(dist d, Operator.Pointwise c, boolean mutable, boolean ignored) {
		super(d, c, mutable, ignored);
		assert (d.region instanceof Region0Base);
	}

	public ZeroBasedIntArray_c(dist d, int[] a, boolean mutable) {
		super(d, a, mutable);
		assert (d.region instanceof Region0Base);
	}

	protected IntArray newInstance(dist d) {
		return new ZeroBasedIntArray_c(d, null, true);
	}

	protected IntArray newInstance(dist d, int c) {
		return new ZeroBasedIntArray_c(d, new Constant(c), true, false);
	}

	public ZeroBasedIntArray_c(dist d, int c, boolean mutable) {
		this(d, new Constant(c), true, mutable);
	}

	public int get(int d0, boolean chkPl, boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0));
		return arr_[d0];
	}

	public int set(int v, int d0, boolean chkPl, boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0));
		return arr_[d0] = v;
	}
}
