/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Oct 28, 2004
 */
package x10.array;

import java.util.Iterator;

import x10.lang.ArrayOperations;
import x10.lang.BooleanReferenceArray;
import x10.lang.Indexable;
import x10.lang.dist;
import x10.lang.place;
import x10.lang.point;
import x10.lang.region;
import x10.lang.Runtime;
import x10.runtime.Configuration;

/**
 * Boolean arrays.
 *
 * @author Christoph von Praun
 * @author Igor Peshansky
 */
public abstract class BooleanArray extends BooleanReferenceArray {

	public BooleanArray(dist d, boolean mutable) {
		super(d, mutable);
	}

	protected abstract BooleanArray newInstance(dist d);
	protected abstract BooleanArray newInstance(dist d, boolean c);
	protected final BooleanArray newInstance(dist d, Operator.Pointwise p) {
		BooleanArray res = newInstance(d);
		if (p != null)
			ArrayOperations.scan(res, p, this);
		return res;
	}

	/**
	 * Return the element at position rawIndex in the backing store.
	 * The canonical index has already be calculated and adjusted.
	 * Can be used by any dimensioned array.
	 */
	public abstract boolean getOrdinal(int rawIndex);

	/**
	 * Set the element at position rawIndex in the backing store to v.
	 * The canonical index has already be calculated and adjusted.
	 * Can be used by any dimensioned array.
	 */
	public abstract boolean setOrdinal(boolean v, int rawIndex);

	public boolean set(boolean v, point pos) { return set(v,pos,true,true); }
	public boolean set(boolean v, point pos,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(pos));
		int theIndex = Helper.ordinal(distribution,pos,chkAOB);
		return setOrdinal(v, theIndex);
	}

	public boolean set(boolean v, int d0) { return set(v,d0,true,true); }
	public boolean set(boolean v, int d0,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0));
		int theIndex = Helper.ordinal(distribution,d0,chkAOB);
		return setOrdinal(v, theIndex);
	}

	public boolean set(boolean v, int d0, int d1) { return set(v,d0,d1,true,true); }
	public boolean set(boolean v, int d0, int d1,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0, d1));
		int theIndex = Helper.ordinal(distribution,d0,d1,chkAOB);
		return setOrdinal(v, theIndex);
	}

	public boolean set(boolean v, int d0, int d1,int d2) {return set(v,d0,d1,d2,true,true);}
	public boolean set(boolean v, int d0, int d1, int d2,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0, d1, d2));
		int theIndex = Helper.ordinal(distribution,d0,d1,d2,chkAOB);
		return setOrdinal(v, theIndex);
	}

	public boolean set(boolean v, int d0, int d1,int d2,int d3) {return set(v,d0,d1,d2,d3,true,true);}
	public boolean set(boolean v, int d0, int d1, int d2, int d3,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));
		int theIndex = Helper.ordinal(distribution,d0,d1,d2,d3,chkAOB);
		return setOrdinal(v, theIndex);
	}

	public boolean get(point pos) {return get(pos,true,true);}
	public boolean get(point pos,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(pos));
		int theIndex = Helper.ordinal(distribution,pos,chkAOB);
		return getOrdinal(theIndex);
	}

	public boolean get(int d0) {return get(d0,true,true);}
	public boolean get(int d0,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0));
		int theIndex = Helper.ordinal(distribution,d0,chkAOB);
		return getOrdinal(theIndex);
	}

	public boolean get(int d0,int d1) {return get(d0,d1,true,true);}
	public boolean get(int d0, int d1,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0, d1));
		int theIndex = Helper.ordinal(distribution,d0,d1,chkAOB);
		return getOrdinal(theIndex);
	}

	public boolean get(int d0,int d1,int d2) {return get(d0,d1,d2,true,true);}
	public boolean get(int d0, int d1, int d2,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0, d1, d2));
		int theIndex = Helper.ordinal(distribution,d0,d1,d2,chkAOB);
		return getOrdinal(theIndex);
	}

	public boolean get(int d0,int d1,int d2,int d3) {return get(d0,d1,d2,d3,true,true);}
	public boolean get(int d0, int d1, int d2, int d3,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));
		int theIndex = Helper.ordinal(distribution,d0,d1,d2,d3,chkAOB);
		return getOrdinal(theIndex);
	}

//	public boolean get(int[] pos) {return get(pos,true,true);}
//	public boolean get(int[] pos,boolean chkPl,boolean chkAOB) {
//		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
//			Runtime.hereCheckPlace(distribution.get(pos));
//		final point p = Runtime.factory.getPointFactory().point(pos);
//		return get(p);
//	}

	public boolean valueEquals(Indexable other) {
		BooleanArray o = (BooleanArray)other;
		if (!o.distribution.equals(distribution))
			return false;
		place here = x10.lang.Runtime.runtime.currentPlace();
		try {
			for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
				point pos = (point) it.next();
				place pl = distribution.get(pos);
				x10.lang.Runtime.runtime.setCurrentPlace(pl);
				if (get(pos) != o.get(pos))
					return false;
			}
		} finally {
			x10.lang.Runtime.runtime.setCurrentPlace(here);
		}
		return true;
	}

    protected void assign(BooleanArray rhs) {
        assert rhs instanceof BooleanArray;
		assert rhs.distribution.equals(distribution);

		place here = x10.lang.Runtime.runtime.currentPlace();
		BooleanArray rhs_t =  rhs;
		try {
			for (Iterator it = rhs_t.distribution.region.iterator(); it.hasNext(); ) {
				point pos = (point) it.next();
				place pl = distribution.get(pos);
				x10.lang.Runtime.runtime.setCurrentPlace(pl);
				set(rhs_t.get(pos), pos);
			}
		} finally {
			x10.lang.Runtime.runtime.setCurrentPlace(here);
		}
	}

	public void reduction(Operator.Reduction op) {
		ArrayOperations.reduction(this, op);
	}

	public boolean bitAndSet(boolean v, point/*(region)*/ p) {
		return BooleanArrayOperations.bitAndSet(this, v, p);
	}
	public boolean bitAndSet(boolean v, int p) {
		return BooleanArrayOperations.bitAndSet(this, v, p);
	}
	public boolean bitAndSet(boolean v, int p, int q) {
		return BooleanArrayOperations.bitAndSet(this, v, p, q);
	}
	public boolean bitAndSet(boolean v, int p, int q, int r) {
		return BooleanArrayOperations.bitAndSet(this, v, p, q, r);
	}
	public boolean bitAndSet(boolean v, int p, int q, int r, int s) {
		return BooleanArrayOperations.bitAndSet(this, v, p, q, r, s);
	}
	public boolean bitOrSet(boolean v, point/*(region)*/ p) {
		return BooleanArrayOperations.bitOrSet(this, v, p);
	}
	public boolean bitOrSet(boolean v, int p) {
		return BooleanArrayOperations.bitOrSet(this, v, p);
	}
	public boolean bitOrSet(boolean v, int p, int q) {
		return BooleanArrayOperations.bitOrSet(this, v, p, q);
	}
	public boolean bitOrSet(boolean v, int p, int q, int r) {
		return BooleanArrayOperations.bitOrSet(this, v, p, q, r);
	}
	public boolean bitOrSet(boolean v, int p, int q, int r, int s) {
		return BooleanArrayOperations.bitOrSet(this, v, p, q, r, s);
	}
	public boolean bitXorSet(boolean v, point/*(region)*/ p) {
		return BooleanArrayOperations.bitXorSet(this, v, p);
	}
	public boolean bitXorSet(boolean v, int p) {
		return BooleanArrayOperations.bitXorSet(this, v, p);
	}
	public boolean bitXorSet(boolean v, int p, int q) {
		return BooleanArrayOperations.bitXorSet(this, v, p, q);
	}
	public boolean bitXorSet(boolean v, int p, int q, int r) {
		return BooleanArrayOperations.bitXorSet(this, v, p, q, r);
	}
	public boolean bitXorSet(boolean v, int p, int q, int r, int s) {
		return BooleanArrayOperations.bitXorSet(this, v, p, q, r, s);
	}

	public Object toJava() {
		final int[] dims_tmp = new int[distribution.rank];
		for (int i = 0; i < distribution.rank; ++i)
			dims_tmp[i] = distribution.region.rank(i).high() + 1;

		final Object ret = java.lang.reflect.Array.newInstance(Boolean.TYPE, dims_tmp);
		ArrayOperations.pointwise(this, null, new Operator.Pointwise() {
			public boolean apply(point p, boolean arg) {
				Object handle = ret;
				int i = 0;
				for (; i < dims_tmp.length - 1; ++i) {
					handle = java.lang.reflect.Array.get(handle, p.get(i));
				}
				java.lang.reflect.Array.setBoolean(handle, p.get(i), arg);
				return arg;
			}
		});
		return ret;
	}

	public x10.lang.booleanArray toValueArray() {
		if (!mutable_) return this;
		throw new Error("TODO: <T>ReferenceArray --> <T>ValueArray");
	}
}
