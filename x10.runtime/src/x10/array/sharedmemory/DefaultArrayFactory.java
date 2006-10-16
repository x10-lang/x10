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
import x10.compilergenerated.Parameter1;
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
	public BooleanReferenceArray BooleanReferenceArray(dist d, boolean c) {
		return new BooleanArray_c(d, c, true, true);
	}

	public BooleanReferenceArray BooleanReferenceArray(dist d, Operator.Pointwise f) {
		assert (false);
		return new BooleanArray_c(d, f, true, true, false);
	}

	public booleanArray booleanValueArray(dist d, boolean c) {
		assert (false);
		return new BooleanArray_c(d, c, true, false);
	}

	public booleanArray booleanValueArray(dist d, Operator.Pointwise f) {
		assert (false);
		return new BooleanArray_c(d, f, true, false, false);
	}

	public ByteReferenceArray ByteReferenceArray(dist d, byte c) {
		assert (false);
		return new ByteArray_c(d, c, true, true);
	}

	public ByteReferenceArray ByteReferenceArray(dist d, Operator.Pointwise f) {
		assert (false);
		return new ByteArray_c(d, f, true, true, false);
	}

	public byteArray byteValueArray(dist d, byte c) {
		assert (false);
		return new ByteArray_c(d, c, true, false);
	}

	public byteArray byteValueArray(dist d, Operator.Pointwise f) {
		assert (false);
		return new ByteArray_c(d, f, true, false, false);
	}

	public CharReferenceArray CharReferenceArray(dist d, char c) {
		assert (false);
		return new CharArray_c(d, c, true, true);
	}

	public CharReferenceArray CharReferenceArray(dist d, Operator.Pointwise f) {
		assert (false);
		return new CharArray_c(d, f, true, true, false);
	}

	public charArray charValueArray(dist d, char c) {
		assert (false);
		return new CharArray_c(d, c, true, false);
	}

	public charArray charValueArray(dist d, Operator.Pointwise f) {
		assert (false);
		return new CharArray_c(d, f, true, false, false);
	}

	public ShortReferenceArray ShortReferenceArray(dist d, short c) {
		assert (false);
		return new ShortArray_c(d, c, true, true);
	}

	public ShortReferenceArray ShortReferenceArray(dist d, Operator.Pointwise f) {
		assert (false);
		return new ShortArray_c(d, f, true, true, false);
	}

	public shortArray shortValueArray(dist d, short c) {
		assert (false);
		return new ShortArray_c(d, c, true, false);
	}

	public shortArray shortValueArray(dist d, Operator.Pointwise f) {
		assert (false);
		return new ShortArray_c(d, f, true, false, false);
	}

	public DoubleReferenceArray DoubleReferenceArray(dist d, double c) {
		assert (false);
		return new DoubleArray_c(d, c, true, true);
	}

	public DoubleReferenceArray DoubleReferenceArray(dist d, Operator.Pointwise f) {
		assert (false);
		return new DoubleArray_c(d, f, true, true, false);
	}

	public doubleArray doubleValueArray(dist d, double c) {
		assert (false);
		return new DoubleArray_c(d, c, true, false);
	}

	public doubleArray doubleValueArray(dist d, Operator.Pointwise f) {
		assert (false);
		return new DoubleArray_c(d, f, true, false, false);
	}

	public doubleArray doubleValueArray(double[] a) {
		assert (false);
		return DoubleArray_c.DoubleArray_c(a, true, false);
	}

	public FloatReferenceArray FloatReferenceArray(dist d, float c) {
		assert (false);
		return new FloatArray_c(d, c, true, true);
	}

	public FloatReferenceArray FloatReferenceArray(dist d, Operator.Pointwise f) {
		assert (false);
		return new FloatArray_c(d, f, true, true, false);
	}

	public floatArray floatValueArray(dist d, float c) {
		assert (false);
		return new FloatArray_c(d, c, true, false);
	}

	public floatArray floatValueArray(dist d, Operator.Pointwise f) {
		assert (false);
		return new FloatArray_c(d, f, true, false, false);
	}

	public IntReferenceArray IntReferenceArray(dist d, int c) {
		assert (false);
		return new IntArray_c(d, c, true, true);
	}

	public IntReferenceArray IntReferenceArray(dist d, Operator.Pointwise f) {
		assert (false);
		return new IntArray_c(d, f, true, true, false);
	}

	public intArray intValueArray(dist d, int c) {
		assert (false);
		return new IntArray_c(d, c, true, false);
	}

	public intArray intValueArray(dist d, Operator.Pointwise f) {
		assert (false);
		return new IntArray_c(d, f, true, false, false);
	}

	public intArray intValueArray(int[] a) {
		assert (false);
		return IntArray_c.IntArray_c(a, true, false);
	}

	public LongReferenceArray LongReferenceArray(dist d, long c) {
		assert (false);
		return new LongArray_c(d, c, true, true);
	}

	public LongReferenceArray LongReferenceArray(dist d, Operator.Pointwise f) {
		assert (false);
		return new LongArray_c(d, f, true, true, false);
	}

	public longArray longValueArray(dist d, long c) {
		assert (false);
		return new LongArray_c(d, c, true, false);
	}

	public longArray longValueArray(dist d, Operator.Pointwise f) {
		assert (false);
		return new LongArray_c(d, f, true, false, false);
	}

	public GenericReferenceArray GenericReferenceArray(dist d, Parameter1 c) {
		assert (false);
		return new GenericArray_c(d, c, true, true, false);
	}

	public GenericReferenceArray GenericReferenceArray(dist d, Operator.Pointwise f) {
		assert (false);
		return new GenericArray_c(d, f, true, true, false);
	}

	public x10.lang.genericArray GenericValueArray(dist d, Parameter1 c, boolean refs_to_values) {
		assert (false);
		return new GenericArray_c(d, c, true, false, refs_to_values);
	}

	public x10.lang.genericArray GenericValueArray(dist d, Operator.Pointwise f, boolean refs_to_values) {
		assert (false);
		return new GenericArray_c(d, f, true, false, refs_to_values);
	}

	public BooleanReferenceArray BooleanArray(dist d, Operator.Pointwise init, boolean safe, boolean mutable, boolean ignored) {
		return new BooleanArray_c(d, init, safe, mutable, ignored);
	}

	public ByteReferenceArray ByteArray(dist d, Operator.Pointwise init, boolean safe, boolean mutable, boolean ignored) {
		return new ByteArray_c(d, init, safe, mutable, ignored);
	}

	public CharReferenceArray CharArray(dist d, Operator.Pointwise init, boolean safe, boolean mutable, boolean ignored) {
		return new CharArray_c(d, init, safe, mutable, ignored);
	}

	public IntReferenceArray IntArray(dist d, Operator.Pointwise init, boolean safe, boolean mutable, boolean ignored) {
		return new IntArray_c(d, init, safe, mutable, ignored);
	}

	public LongReferenceArray LongArray(dist d, Operator.Pointwise init, boolean safe, boolean mutable, boolean ignored) {
		return new LongArray_c(d, init, safe, mutable, ignored);
	}

	public ShortReferenceArray ShortArray(dist d, Operator.Pointwise init, boolean safe, boolean mutable, boolean ignored) {
		return new ShortArray_c(d, init, safe, mutable, ignored);
	}

	public FloatReferenceArray FloatArray(dist d, Operator.Pointwise init, boolean safe, boolean mutable, boolean ignored) {
		return new FloatArray_c(d, init, safe, mutable, ignored);
	}

	public DoubleReferenceArray DoubleArray(dist d, Operator.Pointwise init, boolean safe, boolean mutable, boolean ignored) {
		return new DoubleArray_c(d, init, safe, mutable, ignored);
	}
	public DoubleReferenceArray DoubleArray3d(dist d, Operator.Pointwise init, boolean safe, boolean mutable, boolean ignored) {
		return new DoubleArray3d_c(d, init, safe, mutable, ignored);
	}

	public GenericReferenceArray GenericArray(dist d, Operator.Pointwise init, boolean safe, boolean mutable, boolean ref_to_values) {
		return new GenericArray_c(d, init, safe, mutable, ref_to_values);
	}
}
