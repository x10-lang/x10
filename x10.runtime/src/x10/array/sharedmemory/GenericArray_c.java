/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Oct 20, 2004
 */
package x10.array.sharedmemory;

import x10.array.Distribution_c;
import x10.array.GenericArray;
import x10.array.Operator;
import x10.base.UnsafeContainer;
import x10.compilergenerated.Parameter1;
import x10.lang.Indexable;
import x10.lang.Runtime;
import x10.lang.dist;
import x10.runtime.Configuration;

/**
 * @author Christian Grothoff, Christoph von Praun, Igor Peshansky
 */
public class GenericArray_c extends GenericArray implements UnsafeContainer, Cloneable {

	public final Parameter1[] arr_;
	public final int[] descriptor_;

	/**
	 * Constructor that implements constant promotion (Chap. 10.4.2).
	 * @param d  Distribution of the array
	 * @param c  constant used to intialize all values of the array
	 */
	public GenericArray_c(dist d, Parameter1 c, boolean mutable, boolean refs_to_values) {
		this(d, new Constant(c), mutable, refs_to_values);
	}

	/**
	 * This constructor must not be used directly by an application programmer.
	 * Arrays are constructed by the corresponding factory methods in
	 * x10.lang.Runtime.
	 */
	public GenericArray_c(dist d, Operator.Pointwise c, boolean mutable, boolean refs_to_values) {
		this(d, mutable, refs_to_values, null);
		if (c != null)
			scan(this, c);
	}

	private GenericArray_c(dist d, boolean mutable, boolean refs_to_values, Parameter1[] a) {
		super(d, mutable, refs_to_values);
		assert (d instanceof dist);
		int count = d.region.size();
		int rank = d.region.rank;
		descriptor_ = new int[rank+1];
		descriptor_[0] = rank;
		for (int i = 1; i <= rank; ++i)
			descriptor_[i] = d.region.rank(i-1).size();
		if (a != null) {
			this.arr_ = a;
		} else {
			this.arr_ = new Parameter1[count];
		}
	}

	private GenericArray_c(dist d, Parameter1[] a, boolean mutable, boolean refs_to_values) {
		this(d, mutable, refs_to_values, a);
	}

	/**
	 * Return a GenericArray_c initialized with the given local 1-d (Java) Object array.
	 * TODO: Expose this through the factory class.
	 *
	 * @param a
	 * @return
	 */
	public static GenericArray_c GenericArray_c(Parameter1[] a, boolean mutable, boolean refs_to_values) {
		dist d = Runtime.factory.getDistributionFactory().local(a.length);
		return new GenericArray_c(d, a, mutable, refs_to_values);
	}

	public boolean valueEquals(Indexable other) {
		GenericArray_c o = (GenericArray_c) other;
		if (o.arr_.length != arr_.length)
			return false;
		if (refsToValues_) {
			for (int i = arr_.length-1; i >= 0; i--) 
				if (!x10.lang.Runtime.equalsequals(arr_[i], o.arr_[i]))
					return false;
		} else {
			for (int i = arr_.length-1; i >= 0; i--) 
				if (arr_[i] != o.arr_[i])
					return false;
		}
		return true;
	}

	public Parameter1[] getBackingArray() {
		return arr_;
	}

	public int[] getDescriptor() {
		return descriptor_;
	}

	public Parameter1 setOrdinal(Parameter1 v, int rawIndex) {
		return arr_[rawIndex] = v;
	}

	public Parameter1 getOrdinal(int rawIndex) {
		return arr_[rawIndex];
	}

	protected GenericArray newInstance(dist d) {
		return new GenericArray_c(d, (Operator.Pointwise) null, true, false);
	}

	protected GenericArray newInstance(dist d, Parameter1 c) {
		return new GenericArray_c(d, c, true, false);
	}

	/* Overrides the superclass method - this implementation is more efficient */
	protected void assign(GenericArray rhs) {
		assert rhs.getClass() == this.getClass();

		GenericArray_c rhs_t = (GenericArray_c) rhs;
		if (!Configuration.BAD_PLACE_RUNTIME_CHECK &&
				rhs.distribution.equals(distribution)) {
			int count = arr_.length;
			for (int i = 0; i < count; ++i)
				arr_[i] = rhs_t.arr_[i];
		} else
			// fall back to generic implementation
			super.assign(rhs);
	}

	/* Overrides the superclass method - this implementation is more efficient */
	public void reduction(Operator.Reduction op) {
		if (!Configuration.BAD_PLACE_RUNTIME_CHECK) {
			int count = arr_.length;
			for (int i = 0; i < count; ++i)
				op.apply(arr_[i]);
		} else {
			super.reduction(op);
		}
	}
}
