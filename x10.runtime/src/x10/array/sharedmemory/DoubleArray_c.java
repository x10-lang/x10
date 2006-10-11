/*
 * Created on Oct 20, 2004
 */
package x10.array.sharedmemory;

import java.util.Iterator;
import x10.array.Distribution_c;
import x10.array.DoubleArray;
import x10.array.Helper;
import x10.array.Operator;
import x10.base.Allocator;
import x10.base.MemoryBlock;
import x10.base.UnsafeContainer;
import x10.lang.Indexable;
import x10.lang.Runtime;
import x10.lang.place;
import x10.lang.point;
import x10.base.MemoryBlockSafeDoubleArray;
import x10.lang.dist;
import x10.lang.region;
import x10.lang.DoubleReferenceArray;
import x10.runtime.Configuration;

/**
 * @author Christian Grothoff, Christoph von Praun
 */
public class DoubleArray_c extends DoubleArray implements UnsafeContainer, Cloneable {

	protected final boolean safe_;
	public final MemoryBlock arr_;
	protected final boolean mutable_;

	public boolean valueEquals(Indexable other) {
		return arr_.valueEquals(((DoubleArray_c)other).arr_);
	}

	/**
	 * This constructor must not be used directly by an application programmer.
	 * Arrays are constructed by the corresponding factory methods in
	 * x10.lang.Runtime.
	 */
	protected DoubleArray_c(dist d, Operator.Pointwise c, boolean safe) {
		this(d, c, safe, true);
	}
	public DoubleArray_c(dist d, Operator.Pointwise c, boolean safe, boolean mutable, boolean ignored) {
		this(d, c, safe, mutable);
	}
	protected DoubleArray_c(dist d, Operator.Pointwise c, boolean safe, boolean mutable) {
		this(d, safe, mutable, null);
		if (c != null)
			scan(this, c);
	}

	/**
	 * Create a new array per the given distribution, initialized to c.
	 *
	 * @param d
	 * @param c
	 * @param safe
	 */
	protected DoubleArray_c(dist d, double c) {
		this(d, c, true);
	}
	protected DoubleArray_c(dist d, double c, boolean safe) {
		this(d, c, safe, true);
	}
	public DoubleArray_c(dist d, double c, boolean safe, boolean mutable) {
		this(d, safe, mutable, null);
		scan(this, new Constant(c));
	}

	protected DoubleArray_c(dist d, boolean safe, MemoryBlock arr, boolean mutable) {
		super(d);
		safe_ = safe;
		arr_  = arr;
		mutable_ = mutable;
	}

	protected DoubleArray_c(dist d, boolean safe, boolean mutable, double[] a) {
		super(d);
		assert (d instanceof Distribution_c);
		this.safe_ = safe;
		this.mutable_ = mutable;
		int count = d.region.size();
		if (!safe) {
			int rank = d.region.rank;
			int ranks[] = new int[rank];
			for (int i = 0; i < rank; ++i)
				ranks[i] = d.region.rank(i).size();
			this.arr_ = Allocator.allocUnsafe(count, ranks, Double.TYPE);
		} else if (a == null) {
			this.arr_ = Allocator.allocSafe(count, Double.TYPE, d);
		} else {
			this.arr_ = Allocator.allocSafeDoubleArray(a,d);
		}
	}

	protected DoubleArray_c(dist d, double[] a, boolean safe, boolean mutable) {
		this(d, safe, mutable, a);
	}

	/**
	 * Return a safe DoubleArray_c initialized with the given local 1-d (Java) double array.
	 * TODO: Expose this through the factory class.
	 * @param a
	 * @return
	 */
	public static DoubleArray_c DoubleArray_c(double[] a, boolean safe, boolean mutable) {
		dist d = Runtime.factory.getDistributionFactory().local(a.length);
		return new DoubleArray_c(d, a, safe, mutable);
	}

	public void keepItLive() {}

        public double[] getBackingArray() { 
        return (arr_ instanceof MemoryBlockSafeDoubleArray) ?
    		((MemoryBlockSafeDoubleArray) arr_).getBackingArray()
			: null; }

       public int[] getDescriptor() {
          return arr_.getDescriptor();
       }

	public long getUnsafeAddress() {
		return arr_.getUnsafeAddress();
	}

	public long getUnsafeDescriptor() {
		return arr_.getUnsafeDescriptor();
	}

	/* Overrides the superclass method - this implementation is more efficient */
	public void reduction(Operator.Reduction op) {
		int count = arr_.count();
		for (int i  = 0; i < count; ++i)
			op.apply(arr_.getDouble(i));
	}

	/* Overrides the superclass method - this implementation is more efficient */
	protected void assign(DoubleArray rhs) {
		assert rhs instanceof DoubleArray_c;

		DoubleArray_c rhs_t = (DoubleArray_c) rhs;
		if (rhs.distribution.equals(distribution)) {
			int count = arr_.count();
			for (int i  = 0; i < count; ++i)
				arr_.setDouble(rhs_t.arr_.getDouble(i), i);
		} else
			// fall back to generic implementation
			super.assign(rhs);
	}

	protected DoubleArray newInstance(dist d) {
		assert d instanceof Distribution_c;
		return new DoubleArray_c(d, (Operator.Pointwise) null, safe_);
	}

	protected DoubleArray newInstance(dist d, Operator.Pointwise c) {
		assert d instanceof Distribution_c;
		return new DoubleArray_c(d, c, safe_);
	}

	public DoubleReferenceArray lift(Operator.Binary op, x10.lang.doubleArray arg) {
		assert arg.distribution.equals(distribution);
		DoubleArray result = newInstance(distribution);
		place here = x10.lang.Runtime.runtime.currentPlace();
		try {
			for (Iterator it = distribution.region.iterator(); it.hasNext();) {
				point p = (point) it.next();
				place pl = distribution.get(p);
				x10.lang.Runtime.runtime.setCurrentPlace(pl);
				result.set(op.apply(this.get(p), arg.get(p)),p);
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
			for (Iterator it = distribution.region.iterator(); it.hasNext();) {
				point p = (point) it.next();
				place pl = distribution.get(p);
				x10.lang.Runtime.runtime.setCurrentPlace(pl);
				result.set(op.apply(this.get(p)),p);
			}
		} finally {
			x10.lang.Runtime.runtime.setCurrentPlace(here);
		}
		return result;
	}

	/**
	 * Assume that r is contained in distribution.region.
	 */
	public double reduce(Operator.Binary op, double unit, region r) {
		double result = unit;
		place here = x10.lang.Runtime.runtime.currentPlace();
		try {
			for (Iterator it = r.iterator(); it.hasNext();) {
				point p = (point) it.next();
				place pl = distribution.get(p);
				x10.lang.Runtime.runtime.setCurrentPlace(pl);
				result = op.apply(this.get(p), result);
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
			for (Iterator it = distribution.region.iterator(); it.hasNext();) {
				point p = (point) it.next();
				place pl = distribution.get(p);
				x10.lang.Runtime.runtime.setCurrentPlace(pl);
				temp = op.apply(this.get(p), temp);
				result.set(temp, p);
			}
		} finally {
			x10.lang.Runtime.runtime.setCurrentPlace(here);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see x10.lang.DoubleArray#set(int, int[])
	 */
	public double set(double v, point pos) {return set(v,pos,true,true);}
	public double set(double v, point pos,boolean chkPl,boolean chkAOB) {
		if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(pos));
		return arr_.setDouble(v, (int) distribution.region.ordinal(pos));
	}
	/**
	 * the cannonical index has already be calculated and adjusted.
	 * Can be used by any dimensioned array.
	 */
	public double setOrdinal(double v, int rawIndex) {

		return arr_.setDouble(v,rawIndex);
	}

	public double set(double v, int d0) {return set(v,d0,true,true);}
	public double set(double v, int d0,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0));
		d0 = Helper.ordinal(distribution,d0,chkAOB);
		return arr_.setDouble(v,d0);
	}

	public double set(double v, int d0,int d1) {return set(v,d0,d1,false,false);}
	public double set(double v, int d0, int d1,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0, d1));
		int	theIndex = Helper.ordinal(distribution,d0,d1,chkAOB);
		return arr_.setDouble(v,theIndex);
	}

	public double set(double v, int d0,int d1,int d2) {return set(v,d0,d1,d2,false,false);}
	public double set(double v, int d0, int d1, int d2,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0, d1, d2));
		int	theIndex = Helper.ordinal(distribution,d0,d1,d2,chkAOB);
		return arr_.setDouble(v,theIndex);
	}

	public double set(double v, int d0,int d1,int d2,int d3) {return set(v,d0,d1,d2,d3,true,true);}
	public double set(double v, int d0, int d1, int d2, int d3,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));
		int	theIndex = Helper.ordinal(distribution,d0,d1,d2,d3,chkAOB);
		return arr_.setDouble(v,theIndex);
	}

	/* (non-Javadoc)
	 * @see x10.lang.DoubleArray#get(int[])
	 */
	public double get(point pos) {return get(pos,true,true);}
	public double get(point pos,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(pos));
		return arr_.getDouble((int) distribution.region.ordinal(pos));
	}

	/**
	 * the cannonical index has already be calculated and adjusted.
	 * Can be used by any dimensioned array.
	 */
	public double getOrdinal(int rawIndex) {
		return arr_.getDouble(rawIndex);
	}

	public double get(int d0) {return get(d0,true,true);}
	public double get(int d0,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0));
		d0 = Helper.ordinal(distribution,d0,chkAOB);
		return arr_.getDouble(d0);
	}

	public double get(int d0,int d1) {return get(d0,d1,true,true);}
	public double get(int d0, int d1,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0, d1));
		int theIndex = Helper.ordinal(distribution,d0,d1,chkAOB);
		return arr_.getDouble(theIndex);
	}

	public double get(int d0,int d1,int d2) {return get(d0,d1,d2,true,true);}
	public double get(int d0, int d1, int d2,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0, d1, d2));
		int theIndex = Helper.ordinal(distribution,d0,d1,d2,chkAOB);
		return arr_.getDouble(theIndex);
	}

	public double get(int d0,int d1,int d2,int d3) {return get(d0,d1,d2,d3,true,true);}
	public double get(int d0, int d1, int d2, int d3,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));
		int	theIndex = Helper.ordinal(distribution,d0,d1,d2,d3,chkAOB);
		return arr_.getDouble(theIndex);
	}

	public double get(int[] pos) {return get(pos,true,true);}
	public double get(int[] pos,boolean chkPl,boolean chkAOB) {
		final point p = Runtime.factory.getPointFactory().point(pos);
		return get(p);
	}

	/*
	 * FIXME: this could be made much more inefficient with knowledge of overlay() semantics.
	 */
	public x10.lang.DoubleReferenceArray overlay(x10.lang.doubleArray d) {
		dist dist = distribution.overlay(d.distribution);
		DoubleArray_c ret = new DoubleArray_c(dist, 0, safe_);
		place here = x10.lang.Runtime.runtime.currentPlace();
		try {
			for (Iterator it = dist.iterator(); it.hasNext(); ) {
				point p = (point) it.next();
				place pl = dist.get(p);
				x10.lang.Runtime.runtime.setCurrentPlace(pl);
				double val = (d.distribution.region.contains(p)) ? d.get(p) : get(p);
				ret.set(val, p);
			}
		} finally {
			x10.lang.Runtime.runtime.setCurrentPlace(here);
		}
		return ret;
	}

	/*
	 * FIXME: this could use the fact that d is rectangular
	 * FIXME: (in fact, why are we even iterating over the unknown array here?)
	 */
	public void update(x10.lang.doubleArray d) {
		assert (region.contains(d.region));
		place here = x10.lang.Runtime.runtime.currentPlace();
		try {
			for (Iterator it = d.iterator(); it.hasNext(); ) {
				point p = (point) it.next();
				place pl = distribution.get(p);
				x10.lang.Runtime.runtime.setCurrentPlace(pl);
				set(d.get(p), p);
			}
		} finally {
			x10.lang.Runtime.runtime.setCurrentPlace(here);
		}
	}

	/*
	 * FIXME: this could be made much more inefficient with knowledge of union() semantics.
	 */
	public DoubleReferenceArray union(x10.lang.doubleArray d) {
		dist dist = distribution.union(d.distribution);
		DoubleArray_c ret = new DoubleArray_c(dist, 0, safe_);
		place here = x10.lang.Runtime.runtime.currentPlace();
		try {
			for (Iterator it = dist.iterator(); it.hasNext(); ) {
				point p = (point) it.next();
				place pl = dist.get(p);
				x10.lang.Runtime.runtime.setCurrentPlace(pl);
				double val = (distribution.region.contains(p)) ? get(p) : d.get(p);
				ret.set(val, p);
			}
		} finally {
			x10.lang.Runtime.runtime.setCurrentPlace(here);
		}
		return ret;
	}

	public DoubleReferenceArray restriction(dist d) {
		return restriction(d.region);
	}

	/*
	 * FIXME: this could use the fact that the region is rectangular
	 * FIXME: (in fact, why are we even iterating over the unknown array here?)
	 */
	public DoubleReferenceArray restriction(region r) {
		dist dist = distribution.restriction(r);
		DoubleArray_c ret = new DoubleArray_c(dist, 0, safe_);
		place here = x10.lang.Runtime.runtime.currentPlace();
		try {
			for (Iterator it = dist.iterator(); it.hasNext(); ) {
				point p = (point) it.next();
				place pl = dist.get(p);
				x10.lang.Runtime.runtime.setCurrentPlace(pl);
				ret.set(get(p), p);
			}
		} finally {
			x10.lang.Runtime.runtime.setCurrentPlace(here);
		}
		return ret;
	}

	public x10.lang.doubleArray toValueArray() {
		if (!mutable_)
			return this;
		return new DoubleArray_c(distribution, safe_, arr_, false);
	}

	public boolean isValue() {
		return !this.mutable_;
	}
}
