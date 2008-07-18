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
 * The class of all multidimensional, distributed double arrays in X10.
 * Has no mutable data.
 * Specialized from array by replacing the type parameter with double.
 *
 * Handtranslated from the X10 code in x10/lang/DoubleArray.x10
 *
 * @author vj 12/24/2004
 */
abstract public class doubleArray extends x10Array {

	public static class Constant extends Operator.Pointwise {
		private final double c_;
		public Constant(double c) { c_ = c; }
		public double apply(point p, double i) { return c_; }
	}

	protected doubleArray(dist D, boolean mutable) {
		super(D, mutable);
	}

	abstract public double[] getBackingArray();

	public static final Operator.Binary sub = new Operator.Binary() { public double apply(double r, double s) { return r-s; }};
	public static final Operator.Binary add = new Operator.Binary() { public double apply(double r, double s) { return r+s; }};
	public static final Operator.Binary mul = new Operator.Binary() { public double apply(double r, double s) { return r*s; }};
	public static final Operator.Binary div = new Operator.Binary() { public double apply(double r, double s) { return r/s; }};
	public static final Operator.Binary max = new Operator.Binary() { public double apply(double r, double s) { return Math.max(r,s); }};
	public static final Operator.Unary abs = new Operator.Unary() { public double apply(double r) { return Math.abs(r); }};
	public static final Operator.Unary id = new Operator.Unary() { public double apply(double r) { return r; }};

	/**
	 * Return the value of the array at the given point in the
	 * region.
	 */
	abstract public double get(point/*(region)*/ p);
	abstract public double getOrdinal(int p);
	abstract /*value*/ public double get(int p);
	abstract /*value*/ public double get(int p, int q);
	abstract /*value*/ public double get(int p, int q, int r);
	abstract /*value*/ public double get(int p, int q, int r, int s);
//	abstract public double get(int[] p);

	/**
	 * Convenience method for returning the sum of the array.
	 * @return sum of the array.
	 */
	public double sum() {
		return reduce(add, 0);
	}
	public double sum(region r) {
		return reduce(add, 0, r);
	}
	/**
	 * Convenience method for returning the max of the array.
	 * @return
	 */
	public double max() {
		return reduce(max, java.lang.Double.MIN_VALUE);
	}
	public double max(region r) {
		return reduce(max, java.lang.Double.MIN_VALUE, r);
	}
	/**
	 * Convenience method for returning the max of the array after applying the given fun.
	 * @param fun
	 * @return
	 */
	public double max(Operator.Unary fun) {
		return lift(fun).reduce(max, java.lang.Double.MIN_VALUE);
	}
	/**
	 * Convenience method for applying abs to each element in the array.
	 * @return
	 */
	public DoubleReferenceArray abs() {
		return lift(abs);
	}

	/**
	 * Convenience method for subtracting another array pointwise.
	 * @return
	 */
	public DoubleReferenceArray sub(doubleArray s) {
		return lift(sub, s);
	}
	/**
	 * Convenience method for subtracting another array pointwise.
	 * @return
	 */
	public DoubleReferenceArray add(doubleArray s) {
		return lift(add, s);
	}
	/**
	 * Convenience method for subtracting another array pointwise.
	 * @return
	 */
	public DoubleReferenceArray mul(doubleArray s) {
		return lift(mul, s);
	}
	/*
	 * Given an array A, return A^k.
	 */
	public DoubleReferenceArray pow(int k) {
		return k <= 0 ? lift(id) : mul(pow(k-1));
	}
	/**
	 * Convenience method for subtracting another array pointwise.
	 * @return
	 */
	public DoubleReferenceArray div(doubleArray s) {
		return lift(div, s);
	}
	/*
	 * Given an array A, return k*A, where k is a scalar.
	 */
	public DoubleReferenceArray scale(final int k) {
		return lift(new Operator.Unary() { public double apply(double r) { return r*k; }});
	}

	/**
	 * Convenience method for applying max after applying abs.
	 * @return
	 */
	public double maxAbs() {
		return max(abs);
	}

	public double reduce(Operator.Binary fun, double unit) {
		return reduce(fun, unit, distribution.region);
	}
	/**
	 * Return the value obtained by reducing the given array (viewed only
	 * through the region r) with the function fun, which is assumed to be
	 * associative and commutative.
	 * unit should satisfy fun(unit,x)=x=fun(x,unit).
	 * Assume r is contained in the array's region.
	 */
	abstract public double reduce(Operator.Binary fun, double unit, region r);

	/**
	 * Return a DoubleArray with the same distribution as this, by scanning
	 * this with the function fun, and unit unit.
	 */
	abstract public DoubleReferenceArray/*(distribution)*/ scan(Operator.Binary fun, double unit);

	/**
	 * Return an array of B@P defined on the intersection of the region
	 * underlying the array and the parameter region R.
	 */
	abstract public /*(region(rank) R)*/
	DoubleReferenceArray/*(distribution.restriction(R)())*/  restriction(region R);

	/**
	 * Return an array of B@P defined on the intersection of the region
	 * underlying this and the parametric distribution.
	 */
	public  /*(distribution(:rank=this.rank) D)*/
	DoubleReferenceArray/*(distribution.restriction(D.region)())*/ restriction(dist D) {
		return restriction(D.region);
	}

	public DoubleReferenceArray restriction(place P) {
		return restriction(distribution.restriction(P));
	}

	/**
	 * Take as parameter a distribution D of the same rank as
	 * this, and defined over a disjoint region. Take as argument an
	 * array other over D. Return an array whose distribution is the
	 * union of this and D and which takes on the value
	 * this.atValue(p) for p in this.region and other.atValue(p) for p
	 * in other.region.
	 */
	abstract public /*(distribution(:region.disjoint(this.region) && rank=this.rank) D)*/
	DoubleReferenceArray/*(distribution.union(other.distribution))*/ union(doubleArray other);

	/**
	 * Return the array obtained by overlaying this array on top of other.
	 * The method takes as parameter a distribution D over the same rank.
	 * It returns an array over the distribution dist.asymmetricUnion(D).
	 */
	abstract public /*(distribution(:rank=this.rank) D)*/
	DoubleReferenceArray/*(distribution.asymmetricUnion(D))*/ overlay(doubleArray/*(D)*/ other);

	/**
	 * Update this array in place by overlaying the array other on top of
	 * this. The distribution of the input array must be a subdistribution
	 * of D.
	 * TODO: update the description of the parametric type.
	 */
	abstract public void update(doubleArray/*(D)*/ other);

	/**
	 * Assume given a DoubleArray a over the given distribution.
	 * Assume given a function f: double -> double -> double.
	 * Return a DoubleArray with distribution dist
	 * containing fun(this.atValue(p),a.atValue(p)) for each p in
	 * dist.region.
	 */
	abstract public
	DoubleReferenceArray/*(distribution)*/ lift(Operator.Binary fun, doubleArray/*(distribution)*/ a);
	abstract public
	DoubleReferenceArray/*(distribution)*/ lift(Operator.Unary fun);

	/**
	 * Return an immutable copy of this array. Note: The implementation actually returns a copy
	 * at the representation of the X10 type x10.lang.doubleValueArray, which is doubleArray.
	 * @return an immutable version of this array.
	 */
	abstract public doubleArray toValueArray();

	/**
	 * Return the local chunk of this distributed array.  The result will have a
	 * 1-dimensional 0-based contiguous region.
	 * FIXME: this is just a stub for now.
	 * @return the local chunk of this array.
	 */
	public doubleArray local() {
		return this;
	}
}
