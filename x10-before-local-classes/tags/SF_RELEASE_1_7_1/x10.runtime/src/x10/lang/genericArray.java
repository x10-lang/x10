/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.lang;

import x10.array.Operator;
import x10.compilergenerated.Parameter1;

/**
 * The class of all multidimensional, distributed non-primitive arrays in X10.
 * Has no mutable data.
 * Specialized from array by replacing the type parameter with Parameter1.
 * Handtranslated from the X10 code in x10/lang/GenericArray.x10
 *
 * @author vj 12/24/2004
 */
abstract public class genericArray /*(distribution distribution)*/
/*implements Cloneable, Serializable */
extends x10Array {

	public static class Constant extends Operator.Pointwise {
		private final Parameter1 c_;
		public Constant(Parameter1 c) { c_ = c; }
		public Parameter1 apply(point p, Parameter1 i) { return c_; }
	}

	protected genericArray(dist D, boolean mutable) {
		super(D, mutable);
	}

	abstract public Parameter1[] getBackingArray();

	/**
	 * Return the value of the array at the given point in the
	 * region.
	 */
	abstract public Parameter1 get(point/*(region)*/ p);
	abstract /*value*/ public Parameter1 get(int p);
	abstract /*value*/ public Parameter1 get(int p, int q);
	abstract /*value*/ public Parameter1 get(int p, int q, int r);
	abstract /*value*/ public Parameter1 get(int p, int q, int r, int s);
//	abstract public Parameter1 get(int[] p);

	/**
	 * Return the value obtained by reducing the given array with the
	 * function fun, which is assumed to be associative and
	 * commutative. unit should satisfy fun(unit,x)=x=fun(x,unit).
	 */
	abstract public Parameter1 reduce(Operator.Binary fun, Parameter1 unit);

	/**
	 * Return a GenericArray with the same distribution as this, by
	 * scanning this with the function fun, and unit unit.
	 */
	abstract public GenericReferenceArray/*(distribution)*/ scan(Operator.Binary fun, Parameter1 unit);

	/**
	 * Return an array of B@P defined on the intersection of the
	 * region underlying the array and the parameter region R.
	 */
	abstract public /*(region(rank) R)*/
	GenericReferenceArray/*(distribution.restriction(R)())*/  restriction(region R);

	/**
	 * Return an array of B@P defined on the intersection of
	 * the region underlying this and the parametric distribution.
	 */
	public  /*(distribution(:rank=this.rank) D)*/
	GenericReferenceArray/*(distribution.restriction(D.region)())*/ restriction(dist D) {
		return restriction(D.region);
	}

	public GenericReferenceArray restriction(place P) {
		return restriction(distribution.restriction(P));
	}

	/**
	 * Take as parameter a distribution D of the same rank as *
	 * this, and defined over a disjoint region. Take as argument an *
	 * array other over D. Return an array whose distribution is the
	 * union of this and D and which takes on the value
	 * this.atValue(p) for p in this.region and other.atValue(p) for p
	 * in other.region.
	 */
	abstract public /*(distribution(:region.disjoint(this.region) && rank=this.rank) D)*/
	GenericReferenceArray/*(distribution.union(other.distribution))*/ union(genericArray other);

	/**
	 * Return the array obtained by overlaying this array on top of
	 * other. The method takes as parameter a distribution D over the
	 * same rank. It returns an array over the distribution
	 * dist.asymmetricUnion(D).
	 */
	abstract public /*(distribution(:rank=this.rank) D)*/
	GenericReferenceArray/*(distribution.asymmetricUnion(D))*/ overlay(genericArray/*(D)*/ other);

	/**
	 * Update this array in place by overlaying the array other on top of
	 * this. The distribution of the input array must be a subdistribution
	 * of D.
	 * TODO: update the description of the parametric type.
	 */
	abstract public void update(genericArray/*(D)*/ other);

	/**
	 * Assume given a GenericArray a over the given distribution.
	 * Assume given a function f: Parameter1 -> Parameter1 -> Parameter1.
	 * Return a GenericArray with distribution dist
	 * containing fun(this.atValue(p),a.atValue(p)) for each p in
	 * dist.region.
	 */
	abstract public
	GenericReferenceArray/*(distribution)*/ lift(Operator.Binary fun, genericArray/*(distribution)*/ a);
	abstract public
	GenericReferenceArray/*(distribution)*/ lift(Operator.Unary fun);

	/**
	 * Return an immutable copy of this array.
	 * Note: The implementation actually returns a copy at the
	 * representation of the X10 type x10.lang.GenericValueArray,
	 * which is Parameter1Array.
	 * @return an immutable version of this array.
	 */
	abstract public genericArray toValueArray();

	/**
	 * Return the local chunk of this distributed array.  The result will have a
	 * 1-dimensional 0-based contiguous region.
	 * FIXME: this is just a stub for now.
	 * @return the local chunk of this array.
	 */
	public genericArray local() {
		return this;
	}
}
