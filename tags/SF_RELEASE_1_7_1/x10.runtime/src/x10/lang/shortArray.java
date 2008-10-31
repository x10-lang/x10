/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.lang;

import x10.array.Operator;

/**
 * The class of all multidimensional, distributed int arrays in X10. Has no mutable data.
 * Specialized from array by replacing the type parameter with int.
 * 
 * Handtranslated from the X10 code in x10/lang/ShortArray.x10
 *
 * @author vj 12/24/2004
 */
abstract public class shortArray extends x10Array {

	protected shortArray(dist D, boolean mutable) {
		super(D, mutable);
	}

	abstract public short[] getBackingArray();

	public static final Operator.Binary sub = new Operator.Binary() { public short apply(short r, short s) { return (short)(r-s); }};
	public static final Operator.Binary add = new Operator.Binary() { public short apply(short r, short s) { return (short)(r+s); }};
	public static final Operator.Binary mul = new Operator.Binary() { public short apply(short r, short s) { return (short)(r*s); }};
	public static final Operator.Binary div = new Operator.Binary() { public short apply(short r, short s) { return (short)(r/s); }};
	public static final Operator.Binary max = new Operator.Binary() { public short apply(short r, short s) { return (short)Math.max(r,s); }};
	public static final Operator.Unary abs = new Operator.Unary() { public short apply(short r) { return (short)Math.abs(r); }};

	public static interface pointwiseOp/*(region r)*/ {
		short apply(point/*(r)*/ p);
	}

	public static class Constant extends Operator.Pointwise {
		private final short c_;
		public Constant(short c) { c_ = c; }
		public short apply(point p, short i) { return c_; }
	}

	/** Return the value of the array at the given point in the
	 * region.
	 */
	abstract public short get(point/*(region)*/ p);
	abstract public short getOrdinal(int p);
	abstract /*value*/ public short get(int p);
	abstract /*value*/ public short get(int p, int q);
	abstract /*value*/ public short get(int p, int q, int r);
	abstract /*value*/ public short get(int p, int q, int r, int s);
//	abstract public short get(int[] p);

	/** Convenience method for returning the sum of the array.
	 * @return sum of the array.
	 */
	public int sum() {
		return reduce(add, (short) 0);
	}
	/**
	 * Convenience method for returning the max of the array.
	 * @return
	 */
	public int max() {
		return reduce(max, java.lang.Short.MIN_VALUE);
	}
	/**
	 * Convenience method for returning the max of the array after applying the given fun.
	 * @param fun
	 * @return
	 */
	public int max(Operator.Unary fun) {
		return lift(fun).reduce(max, java.lang.Short.MIN_VALUE);
	}
	/**
	 * Convenience method for applying abs to each element in the array.
	 * @return
	 */
	public ShortReferenceArray abs() {
		return lift(abs);
	}

	/**
	 * Convenience method for subtracting another array pointwise.
	 * @return
	 */
	public ShortReferenceArray sub(shortArray s) {
		return lift(sub, s);
	}
	/**
	 * Convenience method for subtracting another array pointwise.
	 * @return
	 */
	public ShortReferenceArray add(shortArray s) {
		return lift(add, s);
	}
	/**
	 * Convenience method for subtracting another array pointwise.
	 * @return
	 */
	public ShortReferenceArray mul(shortArray s) {
		return lift(mul, s);
	}
	/**
	 * Convenience method for subtracting another array pointwise.
	 * @return
	 */
	public ShortReferenceArray div(shortArray s) {
		return lift(div, s);
	}

	/**
	 * Convenience method for applying max after applying abs.
	 * @return
	 */
	public int maxAbs() {
		return max(abs);
	}

	/** Return the value obtained by reducing the given array with the
	 function fun, which is assumed to be associative and
	 commutative. unit should satisfy fun(unit,x)=x=fun(x,unit).
	 */
	abstract public int reduce(Operator.Binary fun, short unit);

	/** Return a ShortArray with the same distribution as this, by
	 scanning this with the function fun, and unit unit.
	 */
	abstract public ShortReferenceArray/*(distribution)*/ scan(Operator.Binary fun, short unit);

	/** Return an array of B@P defined on the intersection of the
	 region underlying the array and the parameter region R.
	 */
	abstract public /*(region(rank) R)*/
	ShortReferenceArray/*(distribution.restriction(R)())*/  restriction(region R);

	/** Return an array of B@P defined on the intersection of
	 the region underlying this and the parametric distribution.
	 */
	public  /*(distribution(:rank=this.rank) D)*/
	ShortReferenceArray/*(distribution.restriction(D.region)())*/ restriction(dist D) {
		return restriction(D.region);
	}

	public ShortReferenceArray restriction(place P) {
		return restriction(distribution.restriction(P));
	}
	/** Take as parameter a distribution D of the same rank as *
	 * this, and defined over a disjoint region. Take as argument an *
	 * array other over D. Return an array whose distribution is the
	 * union of this and D and which takes on the value
	 * this.atValue(p) for p in this.region and other.atValue(p) for p
	 * in other.region.
	 */
	abstract public /*(distribution(:region.disjoint(this.region) && rank=this.rank) D)*/
	ShortReferenceArray/*(distribution.union(other.distribution))*/ union(shortArray other);

	/** Return the array obtained by overlaying this array on top of
	 other. The method takes as parameter a distribution D over the
	 same rank. It returns an array over the distribution
	 dist.asymmetricUnion(D).
	 */
	abstract public /*(distribution(:rank=this.rank) D)*/
	ShortReferenceArray/*(distribution.asymmetricUnion(D))*/ overlay(shortArray/*(D)*/ other);

	/** Update this array in place by overlaying the array other on top of this. The distribution
	 * of the input array must be a subdistribution of D.
	 * TODO: update the description of the parametric type.
	 */
	abstract public void update(shortArray/*(D)*/ other);

	/** Assume given a ShortArray a over the given distribution.
	 * Assume given a function f: short -> short -> short.
	 * Return a ShortArray with distribution dist
	 * containing fun(this.atValue(p),a.atValue(p)) for each p in
	 * dist.region.
	 */
	abstract public
	ShortReferenceArray/*(distribution)*/ lift(Operator.Binary fun, shortArray/*(distribution)*/ a);
	abstract public
	ShortReferenceArray/*(distribution)*/ lift(Operator.Unary fun);

	/**
	 * Return an immutable copy of this array. Note: The implementation actually returns a copy
	 * at the representation of the X10 type x10.lang.shortValueArray, which is shortArray.
	 * @return an immutable version of this array.
	 */
	abstract public shortArray toValueArray();

	/**
	 * Return the local chunk of this distributed array.  The result will have a
	 * 1-dimensional 0-based contiguous region.
	 * FIXME: this is just a stub for now.
	 * @return the local chunk of this array.
	 */
	public shortArray local() {
		return this;
	}
}
