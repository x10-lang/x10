/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on 28.09.2006
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package x10.array.sharedmemory;

import x10.array.Operator;
import x10.array.Region0Base;
import x10.array.Region1D0Base;
import x10.array.Region3D0Base;
import x10.array.Operator.Pointwise;
import x10.lang.DoubleReferenceArray;
import x10.lang.IntReferenceArray;
import x10.lang.dist;

public class SpecializedArrayFactory extends DefaultArrayFactory {

	public static final SpecializedArrayFactory factory = new SpecializedArrayFactory();
	private SpecializedArrayFactory() { }

	/**
	 * Specialized version.
	 * @see x10.array.sharedmemory.DefaultArrayFactory#DoubleArray(x10.lang.dist, x10.array.Operator.Pointwise, boolean, boolean, boolean)
	 */
	public DoubleReferenceArray DoubleArray(dist d, Operator.Pointwise init, boolean safe, boolean mutable, boolean ignored) {
		if (d.region instanceof Region3D0Base)
			return new DoubleArray3d_c(d, init, mutable, ignored);
		if (d.region.rank() == 2)
			return new DoubleArray2d_anybase_c (d, init, mutable, ignored);
		if (d.region instanceof Region1D0Base)
			return new DoubleArray1d_c(d, init, mutable, ignored);
		return super.DoubleArray(d, init, safe, mutable, ignored);
	}
	public DoubleReferenceArray DoubleArray1d(dist d, Operator.Pointwise init, boolean safe, boolean mutable, boolean ignored) {
	
			return new DoubleArray1d_c(d, init, mutable, ignored);
		
	}

	/**
	 * Specialized version.
	 * @see x10.array.sharedmemory.DefaultArrayFactory#IntArray(x10.lang.dist, x10.array.Operator.Pointwise, boolean, boolean, boolean)
	 */
	public IntReferenceArray IntArray(dist d, Pointwise init, boolean safe, boolean mutable, boolean ignored) {
		if (d.region instanceof Region0Base)
			return new ZeroBasedIntArray_c(d, init, mutable, ignored);
		return super.IntArray(d, init, safe, mutable, ignored);
	}
}
