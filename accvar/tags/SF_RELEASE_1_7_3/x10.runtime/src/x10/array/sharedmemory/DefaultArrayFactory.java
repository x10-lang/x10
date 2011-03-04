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

import x10.array.ArrayFactory;
import x10.array.Operator;
import x10.array.Region3D0Base;
import x10.lang.BooleanReferenceArray;
import x10.lang.ByteReferenceArray;
import x10.lang.CharReferenceArray;
import x10.lang.DoubleReferenceArray;
import x10.lang.FloatReferenceArray;
import x10.lang.GenericReferenceArray;
import x10.lang.IntReferenceArray;
import x10.lang.LongReferenceArray;
import x10.lang.ShortReferenceArray;
import x10.lang.booleanArray;
import x10.lang.byteArray;
import x10.lang.charArray;
import x10.lang.dist;
import x10.lang.doubleArray;
import x10.lang.floatArray;
import x10.lang.intArray;
import x10.lang.longArray;
import x10.lang.shortArray;

public class DefaultArrayFactory extends ArrayFactory {
	public BooleanReferenceArray BooleanArray(dist d, Operator.Pointwise init, boolean safe, boolean mutable, boolean ignored) {
		assert (safe);
		return new BooleanArray_c(d, init, mutable, ignored);
	}

	public ByteReferenceArray ByteArray(dist d, Operator.Pointwise init, boolean safe, boolean mutable, boolean ignored) {
		assert (safe);
		return new ByteArray_c(d, init, mutable, ignored);
	}

	public CharReferenceArray CharArray(dist d, Operator.Pointwise init, boolean safe, boolean mutable, boolean ignored) {
		assert (safe);
		return new CharArray_c(d, init, mutable, ignored);
	}

	public intArray intValueArray(int[] a) {
		return IntArray_c.IntArray_c(a, false);
	}

	public IntReferenceArray IntArray(dist d, Operator.Pointwise init, boolean safe, boolean mutable, boolean ignored) {
		assert (safe);
		return new IntArray_c(d, init, mutable, ignored);
	}

	public LongReferenceArray LongArray(dist d, Operator.Pointwise init, boolean safe, boolean mutable, boolean ignored) {
		assert (safe);
		return new LongArray_c(d, init, mutable, ignored);
	}

	public ShortReferenceArray ShortArray(dist d, Operator.Pointwise init, boolean safe, boolean mutable, boolean ignored) {
		assert (safe);
		return new ShortArray_c(d, init, mutable, ignored);
	}

	public FloatReferenceArray FloatArray(dist d, Operator.Pointwise init, boolean safe, boolean mutable, boolean ignored) {
		assert (safe);
		return new FloatArray_c(d, init, mutable, ignored);
	}

	public doubleArray doubleValueArray(double[] a) {
		return DoubleArray_c.DoubleArray_c(a, false);
	}

	public DoubleReferenceArray DoubleArray(dist d, Operator.Pointwise init, boolean safe, boolean mutable, boolean ignored) {
		assert (safe);
		return new DoubleArray_c(d, init, mutable, ignored);
	}
	public DoubleReferenceArray DoubleArray3d(dist d, Operator.Pointwise init, boolean safe, boolean mutable, boolean ignored) {
		assert (safe);
		return new DoubleArray3d_c(d, init, mutable, ignored);
	}

	public GenericReferenceArray GenericArray(dist d, Operator.Pointwise init, boolean safe, boolean mutable, boolean ref_to_values) {
		assert (safe);
		return new GenericArray_c(d, init, mutable, ref_to_values);
	}
}
