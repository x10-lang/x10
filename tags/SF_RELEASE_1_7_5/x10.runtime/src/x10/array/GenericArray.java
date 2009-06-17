/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on March 2nd, 2005
 *
 */
package x10.array;

import java.util.Iterator;
import java.io.PrintStream;

import x10.compilergenerated.Parameter1;
import x10.lang.GenericReferenceArray;
import x10.lang.Indexable;
import x10.lang.dist;
import x10.lang.place;
import x10.lang.point;
import x10.lang.region;
import x10.lang.Runtime;
import x10.runtime.Configuration;

/**
 * Generic arrays.
 *
 * @author Christian Grothoff
 * @author Igor Peshansky
 */
public abstract class GenericArray extends GenericReferenceArray {

	protected final boolean refsToValues_;

	public GenericArray(dist d, boolean mutable, boolean refs_to_values) {
		super(d, mutable);
		this.refsToValues_ = refs_to_values;
	}

	protected abstract GenericArray newInstance(dist d);
	protected abstract GenericArray newInstance(dist d, Parameter1 c);
	protected final GenericArray newInstance(dist d, Operator.Pointwise p) {
		GenericArray res = newInstance(d);
		if (p != null)
			scan(res, p);
		return res;
	}

	/**
	 * Return the element at position rawIndex in the backing store.
	 * The canonical index has already be calculated and adjusted.
	 * Can be used by any dimensioned array.
	 */
	public abstract Parameter1 getOrdinal(int rawIndex);

	/**
	 * Set the element at position rawIndex in the backing store to v.
	 * The canonical index has already be calculated and adjusted.
	 * Can be used by any dimensioned array.
	 */
	public abstract Parameter1 setOrdinal(Parameter1 v, int rawIndex);

	public Parameter1 set(Parameter1 v, point pos) { return set(v,pos,true,true); }
	public Parameter1 set(Parameter1 v, point pos,boolean chkPl,boolean chkAOB) {
		if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(pos));
		int theIndex = Helper.ordinal(distribution,pos,chkAOB);
		return setOrdinal(v, theIndex);
	}

	public Parameter1 set(Parameter1 v, int d0) { return set(v,d0,true,true); }
	public Parameter1 set(Parameter1 v, int d0,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0));
		int theIndex = Helper.ordinal(distribution,d0,chkAOB);
		return setOrdinal(v, theIndex);
	}

	public Parameter1 set(Parameter1 v, int d0,int d1) { return set(v,d0,d1,true,true); }
	public Parameter1 set(Parameter1 v, int d0, int d1,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0, d1));
		int theIndex = Helper.ordinal(distribution,d0,d1,chkAOB);
		return setOrdinal(v, theIndex);
	}

	public Parameter1 set(Parameter1 v, int d0,int d1,int d2) {return set(v,d0,d1,d2,true,true);}
	public Parameter1 set(Parameter1 v, int d0, int d1, int d2,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0, d1, d2));
		int theIndex = Helper.ordinal(distribution,d0,d1,d2,chkAOB);
		return setOrdinal(v, theIndex);
	}

	public Parameter1 set(Parameter1 v, int d0,int d1,int d2,int d3) {return set(v,d0,d1,d2,d3,true,true);}
	public Parameter1 set(Parameter1 v, int d0, int d1, int d2, int d3,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));
		int theIndex = Helper.ordinal(distribution,d0,d1,d2,d3,chkAOB);
		return setOrdinal(v, theIndex);
	}

	public Parameter1 get(point pos) {return get(pos,true,true);}
	public Parameter1 get(point pos,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(pos));
		int theIndex = Helper.ordinal(distribution,pos,chkAOB);
		return getOrdinal(theIndex);
	}

	public Parameter1 get(int d0) {return get(d0,true,true);}
	public Parameter1 get(int d0,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0));
		int theIndex = Helper.ordinal(distribution,d0,chkAOB);
		return getOrdinal(theIndex);
	}

	public Parameter1 get(int d0,int d1) {return get(d0,d1,true,true);}
	public Parameter1 get(int d0, int d1,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0, d1));
		int theIndex = Helper.ordinal(distribution,d0,d1,chkAOB);
		return getOrdinal(theIndex);
	}

	public Parameter1 get(int d0,int d1,int d2) {return get(d0,d1,d2,true,true);}
	public Parameter1 get(int d0, int d1, int d2,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0, d1, d2));
		int theIndex = Helper.ordinal(distribution,d0,d1,d2,chkAOB);
		return getOrdinal(theIndex);
	}

	public Parameter1 get(int d0,int d1,int d2,int d3) {return get(d0,d1,d2,d3,true,true);}
	public Parameter1 get(int d0, int d1, int d2, int d3,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));
		int theIndex = Helper.ordinal(distribution,d0,d1,d2,d3,chkAOB);
		return getOrdinal(theIndex);
	}

//	public Parameter1 get(int[] pos) {return get(pos,true,true);}
//	public Parameter1 get(int[] pos,boolean chkPl,boolean chkAOB) {
//		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
//			Runtime.hereCheckPlace(distribution.get(pos));
//		final point p = Runtime.factory.getPointFactory().point(pos);
//		return get(p);
//	}

	public boolean valueEquals(Indexable other) {
		GenericArray o = (GenericArray)other;
		if (!o.distribution.equals(distribution))
			return false;
		place here = x10.lang.Runtime.runtime.currentPlace();
		try {
			if (refsToValues_) {
				for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
					point pos = (point) it.next();
					place pl = distribution.get(pos);
					x10.lang.Runtime.runtime.setCurrentPlace(pl);
					if (!x10.lang.Runtime.equalsequals(get(pos), o.get(pos)))
						return false;
				}
			} else {
				for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
					point pos = (point) it.next();
					place pl = distribution.get(pos);
					x10.lang.Runtime.runtime.setCurrentPlace(pl);
					if (get(pos) != o.get(pos))
						return false;
				}
			}
		} finally {
			x10.lang.Runtime.runtime.setCurrentPlace(here);
		}
		return true;
	}

	protected void assign(GenericArray rhs) {
		assert rhs instanceof GenericArray;
		assert rhs.distribution.equals(distribution);

		place here = x10.lang.Runtime.runtime.currentPlace();
		GenericArray rhs_t = (GenericArray) rhs;
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
	public void pointwise(GenericArray res, Operator.Pointwise op, GenericArray arg) {
		assert res == null || res.distribution.equals(distribution);
		assert arg != null;
		assert arg.distribution.equals(distribution);

		GenericArray res_t = (GenericArray) res;
		GenericArray arg_t = (GenericArray) arg;
		place here = x10.lang.Runtime.runtime.currentPlace();

		try {
			for (Iterator it = distribution.region.iterator(); it.hasNext();) {
				point p = (point) it.next();
				// for the execution of this operation, set
				// the current place of the activity temprarily to the one
				// specified by the distribution
				place pl = distribution.get(p);
				x10.lang.Runtime.runtime.setCurrentPlace(pl);
				Parameter1 arg1 = get(p);
				Parameter1 arg2 = arg_t.get(p);
				Parameter1 val = op.apply(p, arg1, arg2);
				if (res_t != null)
					res_t.set(val, p);
			}
		} finally {
			x10.lang.Runtime.runtime.setCurrentPlace(here);
		}
	}

	public void pointwise(GenericArray res, Operator.Pointwise op) {
		assert res == null || res.distribution.equals(distribution);
		place here = x10.lang.Runtime.runtime.currentPlace();

		try {
			for (Iterator it = distribution.region.iterator(); it.hasNext();) {
				point p = (point) it.next();
				// for the execution of this operation, set
				// the current place of the activity temprarily to the one
				// specified by the distribution
				place pl = distribution.get(p);
				x10.lang.Runtime.runtime.setCurrentPlace(pl);
				Parameter1 arg1 = get(p);
				Parameter1 val = op.apply(p, arg1);
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
			for (Iterator it = distribution.region.iterator(); it.hasNext();) {
				point p = (point) it.next();
				place pl = distribution.get(p);
				x10.lang.Runtime.runtime.setCurrentPlace(pl);
				Parameter1 arg1 = get(p);
				op.apply(arg1);
			}
		} finally {
			x10.lang.Runtime.runtime.setCurrentPlace(here);
		}
	}

	/* operations are performed in canonical order */
	public void scan(GenericArray res, Operator.Unary op) {
		assert res == null || res instanceof GenericArray;
		assert res.distribution.equals(distribution);
		place here = x10.lang.Runtime.runtime.currentPlace();

		GenericArray res_t = (res == null) ? null : (GenericArray) res;
		try {
			for (Iterator it = distribution.region.iterator(); it.hasNext();) {
				point p = (point) it.next();
				place pl = distribution.get(p);
				x10.lang.Runtime.runtime.setCurrentPlace(pl);
				Parameter1 arg1 = get(p);
				Parameter1 val = op.apply(arg1);
				if (res_t != null)
					res_t.set(val, p);
			}
		} finally {
			x10.lang.Runtime.runtime.setCurrentPlace(here);
		}
	}

	public void scan(GenericArray res, Operator.Pointwise op) {
		assert res == null || res instanceof GenericArray;
		assert res.distribution.equals(distribution);
		place here = x10.lang.Runtime.runtime.currentPlace();

		GenericArray res_t = (res == null) ? null : (GenericArray) res;
		try {
			for (Iterator it = distribution.region.iterator(); it.hasNext();) {
				point p = (point) it.next();
				place pl = distribution.get(p);
				x10.lang.Runtime.runtime.setCurrentPlace(pl);
				Parameter1 val = op.apply(p, (Parameter1)null);
				if (res_t != null)
					res_t.set(val, p);
			}
		} finally {
			x10.lang.Runtime.runtime.setCurrentPlace(here);
		}
	}

	public GenericReferenceArray lift(Operator.Binary op, x10.lang.genericArray arg) {
		assert arg.distribution.equals(distribution);
		GenericReferenceArray result = newInstance(distribution);
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
	public GenericReferenceArray lift(Operator.Unary op) {
		GenericReferenceArray result = newInstance(distribution);
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

	public Parameter1 reduce(Operator.Binary op, Parameter1 unit) {
		Parameter1 result = unit;
		place here = x10.lang.Runtime.runtime.currentPlace();
		try {
			for (Iterator it = distribution.region.iterator(); it.hasNext();) {
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

	public GenericReferenceArray scan(Operator.Binary op, Parameter1 unit) {
		Parameter1 temp = unit;
		GenericReferenceArray result = newInstance(distribution);
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
	public x10.lang.GenericReferenceArray overlay(x10.lang.genericArray d) {
		dist dist = distribution.overlay(d.distribution);
		GenericArray ret = newInstance(dist, (Parameter1)null);
		place here = x10.lang.Runtime.runtime.currentPlace();
		try {
			for (Iterator it = dist.iterator(); it.hasNext(); ) {
				point p = (point) it.next();
				place pl = dist.get(p);
				x10.lang.Runtime.runtime.setCurrentPlace(pl);
				Parameter1 val = (d.distribution.region.contains(p)) ? d.get(p) : get(p);
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
	public void update(x10.lang.genericArray d) {
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
	public GenericReferenceArray union(x10.lang.genericArray d) {
		dist dist = distribution.union(d.distribution);
		GenericArray ret = newInstance(dist, (Parameter1)null);
		place here = x10.lang.Runtime.runtime.currentPlace();
		try {
			for (Iterator it = dist.iterator(); it.hasNext(); ) {
				point p = (point) it.next();
				place pl = dist.get(p);
				x10.lang.Runtime.runtime.setCurrentPlace(pl);
				Parameter1 val = (distribution.region.contains(p)) ? get(p) : d.get(p);
				ret.set(val, p);
			}
		} finally {
			x10.lang.Runtime.runtime.setCurrentPlace(here);
		}
		return ret;
	}

	public GenericReferenceArray restriction(dist d) {
		return restriction(d.region);
	}

	/*
	 * FIXME: this could use the fact that the region is rectangular
	 * FIXME: (in fact, why are we even iterating over the unknown array here?)
	 */
	public GenericReferenceArray restriction(region d) {
		dist dist = distribution.restriction(d);
		GenericArray ret = newInstance(dist, (Parameter1)null);
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

		final Object ret = java.lang.reflect.Array.newInstance(Parameter1.class, dims_tmp);
		pointwise(null, new Operator.Pointwise() {
			public Parameter1 apply(point p, Parameter1 arg) {
				Object handle = ret;
				int i = 0;
				for (; i < dims_tmp.length - 1; ++i) {
					handle = java.lang.reflect.Array.get(handle, p.get(i));
				}
				java.lang.reflect.Array.set(handle, p.get(i), arg);
				return arg;
			}
		});
		return ret;
	}

	public x10.lang.genericArray toValueArray() {
		if (!mutable_) return this;
		try {
			return (x10.lang.genericArray) this.clone();
		} catch (CloneNotSupportedException x) {
			throw new Error("TODO: <T>ReferenceArray --> <T>ValueArray");
		}
	}

	/* for debugging */
	public static void printArray(String prefix, Object[][] a) {
		printArray(prefix, a, System.out);
	}

	/* for debugging */
	public static void printArray(String prefix, Object[][] a, PrintStream out) {
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
