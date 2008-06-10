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
import java.io.PrintStream;

import x10.lang.DoubleReferenceArray;
import x10.lang.Indexable;
import x10.lang.dist;
import x10.lang.place;
import x10.lang.point;
import x10.lang.region;
import x10.lang.Runtime;
import x10.runtime.Configuration;

/**
 * Double arrays.
 *
 * @author Christoph von Praun
 * @author Igor Peshansky
 */
public abstract class DoubleArray extends DoubleReferenceArray {

	public DoubleArray(dist d, boolean mutable) {
		super(d, mutable);
	}

	protected abstract DoubleArray newInstance(dist d);
	protected abstract DoubleArray newInstance(dist d, double c);
	protected final DoubleArray newInstance(dist d, Operator.Pointwise p) {
		DoubleArray res = newInstance(d);
		if (p != null)
			scan(res, p);
		return res;
	}

	/**
	 * Return the element at position rawIndex in the backing store.
	 * The canonical index has already be calculated and adjusted.
	 * Can be used by any dimensioned array.
	 */
	public abstract double getOrdinal(int rawIndex);

	/**
	 * Set the element at position rawIndex in the backing store to v.
	 * The canonical index has already be calculated and adjusted.
	 * Can be used by any dimensioned array.
	 */
	public abstract double setOrdinal(double v, int rawIndex);

	public double set(double v, point pos) { return set(v,pos,true,true); }
	public double set(double v, point pos,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(pos));
		int theIndex = Helper.ordinal(distribution,pos,chkAOB);
		return setOrdinal(v, theIndex);
	}

	public double set(double v, int d0) { return set(v,d0,true,true); }
	public double set(double v, int d0,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0));
		int theIndex = Helper.ordinal(distribution,d0,chkAOB);
		return setOrdinal(v, theIndex);
	}

	public double set(double v, int d0,int d1) { return set(v,d0,d1,false,false); }
	public double set(double v, int d0, int d1,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0, d1));
		int theIndex = Helper.ordinal(distribution,d0,d1,chkAOB);
		return setOrdinal(v, theIndex);
	}

	public double set(double v, int d0,int d1,int d2) {return set(v,d0,d1,d2,false,false);}
	public double set(double v, int d0, int d1, int d2,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0, d1, d2));
		int theIndex = Helper.ordinal(distribution,d0,d1,d2,chkAOB);
		return setOrdinal(v, theIndex);
	}

	public double set(double v, int d0,int d1,int d2,int d3) {return set(v,d0,d1,d2,d3,true,true);}
	public double set(double v, int d0, int d1, int d2, int d3,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));
		int theIndex = Helper.ordinal(distribution,d0,d1,d2,d3,chkAOB);
		return setOrdinal(v, theIndex);
	}

	public double get(point pos) {return get(pos,true,true);}
	public double get(point pos,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(pos));
		int theIndex = Helper.ordinal(distribution,pos,chkAOB);
		return getOrdinal(theIndex);
	}

	public double get(int d0) {return get(d0,true,true);}
	public double get(int d0,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0));
		int theIndex = Helper.ordinal(distribution,d0,chkAOB);
		return getOrdinal(theIndex);
	}

	public double get(int d0,int d1) {return get(d0,d1,true,true);}
	public double get(int d0, int d1,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0, d1));
		int theIndex = Helper.ordinal(distribution,d0,d1,chkAOB);
		return getOrdinal(theIndex);
	}

	public double get(int d0,int d1,int d2) {return get(d0,d1,d2,true,true);}
	public double get(int d0, int d1, int d2,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0, d1, d2));
		int theIndex = Helper.ordinal(distribution,d0,d1,d2,chkAOB);
		return getOrdinal(theIndex);
	}

	public double get(int d0,int d1,int d2,int d3) {return get(d0,d1,d2,d3,true,true);}
	public double get(int d0, int d1, int d2, int d3,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));
		int theIndex = Helper.ordinal(distribution,d0,d1,d2,d3,chkAOB);
		return getOrdinal(theIndex);
	}

//	public double get(int[] pos) {return get(pos,true,true);}
//	public double get(int[] pos,boolean chkPl,boolean chkAOB) {
//		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
//			Runtime.hereCheckPlace(distribution.get(pos));
//		final point p = Runtime.factory.getPointFactory().point(pos);
//		return get(p);
//	}

	public boolean valueEquals(Indexable other) {
		DoubleArray o = (DoubleArray)other;
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

	protected void assign(DoubleArray rhs) {
		assert rhs instanceof DoubleArray;
		assert rhs.distribution.equals(distribution);

		place here = x10.lang.Runtime.runtime.currentPlace();
		DoubleArray rhs_t = rhs;
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

	/*
	 * Generic implementation - an array with fixed, known number of dimensions
	 * can of course do without the Iterator.
	 */
	public void pointwise(DoubleArray res, Operator.Pointwise op, DoubleArray arg) {
		assert res == null || res.distribution.equals(distribution);
		assert arg != null;
		assert arg.distribution.equals(distribution);

		place here = x10.lang.Runtime.runtime.currentPlace();
		try {
			for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
				point p = (point) it.next();
				place pl = distribution.get(p);
				x10.lang.Runtime.runtime.setCurrentPlace(pl);
				double arg1 = get(p);
				double arg2 = arg.get(p);
				double val = op.apply(p, arg1, arg2);
				if (res != null)
					res.set(val, p);
			}
		} finally {
			x10.lang.Runtime.runtime.setCurrentPlace(here);
		}
	}

	public void pointwise(DoubleArray res, Operator.Pointwise op) {
		assert res == null || res.distribution.equals(distribution);

		place here = x10.lang.Runtime.runtime.currentPlace();
		try {
			for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
				point p = (point) it.next();
				place pl = distribution.get(p);
				x10.lang.Runtime.runtime.setCurrentPlace(pl);
				double arg1 = get(p);
				double val = op.apply(p, arg1);
				if (res != null)
					res.set(val, p);
			}
		} finally {
			x10.lang.Runtime.runtime.setCurrentPlace(here);
		}
	}

	/* operations can be performed in any order */
	public void reduction(Operator.Reduction op) {
		place here = x10.lang.Runtime.runtime.currentPlace();
		try {
			for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
				point p = (point) it.next();
				place pl = distribution.get(p);
				x10.lang.Runtime.runtime.setCurrentPlace(pl);
				double arg1 = get(p);
				op.apply(arg1);
			}
		} finally {
			x10.lang.Runtime.runtime.setCurrentPlace(here);
		}
	}

	/* operations are performed in canonical order */
	public void scan(DoubleArray res, Operator.Unary op) {
		assert res == null || res instanceof DoubleArray;
		assert res.distribution.equals(distribution);

		place here = x10.lang.Runtime.runtime.currentPlace();
		try {
			for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
				point p = (point) it.next();
				place pl = distribution.get(p);
				x10.lang.Runtime.runtime.setCurrentPlace(pl);
				double arg1 = get(p);
				double val = op.apply(arg1);
				if (res != null)
					res.set(val, p);
			}
		} finally {
			x10.lang.Runtime.runtime.setCurrentPlace(here);
		}
	}

	/* operations are performed in canonical order */
	public void scan(DoubleArray res, Operator.Pointwise op) {
		assert res == null || res instanceof DoubleArray;
		assert res.distribution.equals(distribution);

		place here = x10.lang.Runtime.runtime.currentPlace();
		try {
			for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
				point p = (point) it.next();
				place pl = distribution.get(p);
				x10.lang.Runtime.runtime.setCurrentPlace(pl);
				double val = op.apply(p, 0.);
				if (res != null)
					res.set(val, p);
			}
		} finally {
			x10.lang.Runtime.runtime.setCurrentPlace(here);
		}
	}

	public DoubleReferenceArray lift(Operator.Binary op, x10.lang.doubleArray arg) {
		assert arg.distribution.equals(distribution);
		DoubleReferenceArray result = newInstance(distribution);
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
		DoubleReferenceArray result = newInstance(distribution);
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
				result = op.apply(result, this.get(p));
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

	/*
	 * FIXME: this could be made much more efficient with knowledge of overlay() semantics.
	 */
	public x10.lang.DoubleReferenceArray overlay(x10.lang.doubleArray d) {
		dist dist = distribution.overlay(d.distribution);
		DoubleArray ret = newInstance(dist, 0.);
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
	 * FIXME: this could be made much more efficient with knowledge of union() semantics.
	 */
	public DoubleReferenceArray union(x10.lang.doubleArray d) {
		dist dist = distribution.union(d.distribution);
		DoubleArray ret = newInstance(dist, 0.);
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
		DoubleArray ret = newInstance(dist, 0.);
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

	public Object toJava() {
		final int[] dims_tmp = new int[distribution.rank];
		for (int i = 0; i < distribution.rank; ++i)
			dims_tmp[i] = distribution.region.rank(i).high() + 1;

		final Object ret = java.lang.reflect.Array.newInstance(Double.TYPE, dims_tmp);
		pointwise(null, new Operator.Pointwise() {
			public double apply(point p, double arg) {
				Object handle = ret;
				int i = 0;
				for (; i < dims_tmp.length - 1; ++i) {
					handle = java.lang.reflect.Array.get(handle, p.get(i));
				}
				java.lang.reflect.Array.setDouble(handle, p.get(i), arg);
				return arg;
			}
		});
		return ret;
	}

	public x10.lang.doubleArray toValueArray() {
		if (!mutable_) return this;
		throw new Error("TODO: <T>ReferenceArray --> <T>ValueArray");
	}

	/* for debugging */
	public static void printArray(String prefix, double[][] a) {
		printArray(prefix, a, System.out);
	}

	/* for debugging */
	public static void printArray(String prefix, double[][] a, PrintStream out) {
		out.print(prefix + "{");
		for (int i = 0; i < a.length; ++i) {
			out.print("{");
			for (int j = 0; j < a[i].length; ++ j) {
				out.print(a[i][j]);
				if (j < a[i].length - 1)
					out.print(", ");
			}
			out.print("}");
			if (i < a.length - 1)
				out.print(", ");
		}
		out.println("}");
	}
}

