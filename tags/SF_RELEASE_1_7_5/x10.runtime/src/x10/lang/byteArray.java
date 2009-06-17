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
 * The class of all multidimensional, distributed byte arrays in X10.
 * Has no mutable data.
 * Specialized from array by replacing the type parameter with byte.
 * 
 * Handtranslated from the X10 code in x10/lang/ByteArray.x10
 * 
 * @author vj 12/24/2004
 */
abstract public class byteArray extends x10Array {

	public static class Constant extends Operator.Pointwise {
		private final byte c_;
		public Constant(byte c) { c_ = c; }
		public byte apply(point p, byte i) { return c_; }
	}

	protected byteArray(dist D, boolean mutable) {
		super(D, mutable);
	}
	
	abstract public byte[] getBackingArray();

	public static final Operator.Binary sub = new Operator.Binary() { public byte apply(byte r, byte s) { return (byte)(r-s); }};
	public static final Operator.Binary add = new Operator.Binary() { public byte apply(byte r, byte s) { return (byte)(r+s); }};
	public static final Operator.Binary mul = new Operator.Binary() { public byte apply(byte r, byte s) { return (byte)(r*s); }};
	public static final Operator.Binary div = new Operator.Binary() { public byte apply(byte r, byte s) { return (byte)(r/s); }};
	public static final Operator.Binary max = new Operator.Binary() { public byte apply(byte r, byte s) { return (byte)Math.max(r,s); }};
	public static final Operator.Unary abs = new Operator.Unary() { public byte apply(byte r) { return (byte)Math.abs(r); }};
	
	/**
	 * Return the value of the array at the given point in the
	 * region.
	 */
	abstract public byte get(point/*(region)*/ p);
	abstract public byte getOrdinal(int p);
	abstract /*value*/ public byte get(int p);
	abstract /*value*/ public byte get(int p, int q);
	abstract /*value*/ public byte get(int p, int q, int r);
	abstract /*value*/ public byte get(int p, int q, int r, int s);
//    abstract public byte get(int[] p);
    
    /** Convenience method for returning the sum of the array.
     * @return sum of the array.
     */
	public int sum() {
		return reduce(add, (byte) 0);
	}
	/**
	 * Convenience method for returning the max of the array.
	 * @return
	 */
	public int max() {
		return reduce(max, java.lang.Byte.MIN_VALUE);
	}
	/**
	 * Convenience method for returning the max of the array after applying the given fun.
	 * @param fun
	 * @return
	 */
	public int max(Operator.Unary fun) {
		return lift(fun).reduce(max, java.lang.Byte.MIN_VALUE);
	}
	/**
	 * Convenience method for applying abs to each element in the array.
	 * @return
	 */
	public ByteReferenceArray abs() {
		return lift(abs);
	}
	
	/**
	 * Convenience method for subtracting another array pointwise.
	 * @return
	 */
	public ByteReferenceArray sub(byteArray s) {
		return lift(sub, s);
	}
	/**
	 * Convenience method for subtracting another array pointwise.
	 * @return
	 */
	public ByteReferenceArray add(byteArray s) {
		return lift(add, s);
	}
	/**
	 * Convenience method for subtracting another array pointwise.
	 * @return
	 */
	public ByteReferenceArray mul(byteArray s) {
		return lift(mul, s);
	}
	/**
	 * Convenience method for subtracting another array pointwise.
	 * @return
	 */
	public ByteReferenceArray div(byteArray s) {
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
	abstract public int reduce(Operator.Binary fun, byte unit);
	
	/** Return a ByteArray with the same distribution as this, by 
	 scanning this with the function fun, and unit unit.
	 */
	abstract public ByteReferenceArray/*(distribution)*/ scan(Operator.Binary fun, byte unit);
	
	/** Return an array of B@P defined on the intersection of the
	 region underlying the array and the parameter region R.
	 */
	abstract public /*(region(rank) R)*/
	ByteReferenceArray/*(distribution.restriction(R)())*/ restriction(region R);
	
	/** Return an array of B@P defined on the intersection of 
	 the region underlying this and the parametric distribution.
	 */    
	public  /*(distribution(:rank=this.rank) D)*/ 
	ByteReferenceArray/*(distribution.restriction(D.region)())*/ restriction(dist D) {
		return restriction(D.region);
	}
	
	/** Take as parameter a distribution D of the same rank as *
	 * this, and defined over a disjoint region. Take as argument an *
	 * array other over D. Return an array whose distribution is the
	 * union of this and D and which takes on the value
	 * this.atValue(p) for p in this.region and other.atValue(p) for p
	 * in other.region.
	 */
	abstract public /*(distribution(:region.disjoint(this.region) && rank=this.rank) D)*/
	ByteReferenceArray/*(distribution.union(other.distribution))*/ union(byteArray other);
	
	/** Return the array obtained by overlaying this array on top of
	 other. The method takes as parameter a distribution D over the
	 same rank. It returns an array over the distribution
	 dist.asymmetricUnion(D).
	 */
	abstract public /*(distribution(:rank=this.rank) D)*/
	ByteReferenceArray/*(distribution.asymmetricUnion(D))*/ overlay(byteArray/*(D)*/ other);
	
	/** Update this array in place by overlaying the array other on top of this. The distribution
	 * of the input array must be a subdistribution of D.
	 * TODO: update the description of the parametric type.
	 */
    abstract public void update(byteArray/*(D)*/ other);
    
	/** Assume given a ByteArray a over the given distribution.
	 * Assume given a function f: byte -> byte -> byte.
	 * Return a ByteArray with distribution dist 
	 * containing fun(this.atValue(p),a.atValue(p)) for each p in
	 * dist.region.
	 */
	abstract public 
	ByteReferenceArray/*(distribution)*/ lift(Operator.Binary fun, byteArray/*(distribution)*/ a);
	abstract public 
	ByteReferenceArray/*(distribution)*/ lift(Operator.Unary fun);
	
	/**
	 * Return an immutable copy of this array. Note: The implementation actually returns a copy
	 * at the representation of the X10 type x10.lang.byteValueArray, which is byteArray.
	 * @return an immutable version of this array.
	 */
	abstract public byteArray toValueArray();

	/**
	 * Return the local chunk of this distributed array.  The result will have a
	 * 1-dimensional 0-based contiguous region.
	 * FIXME: this is just a stub for now.
	 * @return the local chunk of this array.
	 */
	public byteArray local() {
		return this;
	}
}
