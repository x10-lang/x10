package x10.lang;

import java.io.Serializable;


/** The class of all multidimensional, distributed int arrays in X10. Has no mutable data.
 * Specialized from array by replacing the type parameter with int.
 
 * Handtranslated from the X10 code in x10/lang/FloatArray.x10
 * 
 * @author vj 12/24/2004
 */


abstract public class floatArray extends x10Array{

	protected floatArray( dist D) {
		super(D);
	}
	
	public static interface binaryOp extends Serializable {
		float apply(float r, float s);
	}
	public static final binaryOp sub = new binaryOp() { public float apply(float r, float s) { return r-s;}};
	public static final binaryOp add = new binaryOp() { public float apply(float r, float s) { return r+s;}};
	public static final binaryOp mul = new binaryOp() { public float apply(float r, float s) { return r*s;}};
	public static final binaryOp div = new binaryOp() { public float apply(float r, float s) { return r/s;}};
	public static final binaryOp max = new binaryOp() { public float apply(float r, float s) { return Math.max(r,s);}};
	public static interface unaryOp {
		float apply(float r);
	}
	public static final unaryOp abs = new unaryOp() { public float apply(float r) { return Math.abs(r);}};
	
	public static interface pointwiseOp/*(region r)*/ extends Serializable {
		float apply(point/*(r)*/ p);
	}
	
	abstract public static /*value*/ class factory {
		/** Return the unique int value array initialized with 0 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public floatArray floatValueArray( /*nat*/ int k) {
			return floatValueArray(k, 0);
		}
		/** Return the unique int value array initialized with initVal 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public floatArray/*(:rank=1)*/  floatValueArray(/*nat*/ int k, float initVal) { 
			return floatValueArray(x10.lang.dist.factory.local(k), initVal);
		}
		/** Return the unique int value array initialized with init 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public floatArray/*(:rank=1)*/ floatValueArray(/*nat*/ int k, pointwiseOp init) {
			return floatValueArray( x10.lang.dist.factory.local(k), init);
		}
		
		abstract public 
		/*(distribution D)*/ floatArray/*(D)*/ floatValueArray(dist D, float init);
		abstract public 
		/*(distribution D)*/ floatArray/*(D)*/ floatValueArray( dist D, 
				pointwiseOp/*(D.region)*/ init);
		/** Return the unique int value array initialized with 0 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public FloatReferenceArray FloatReferenceArray( /*nat*/ int k) {
			return FloatReferenceArray(k, 0);
		}
		/** Return the unique int value array initialized with initVal 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public FloatReferenceArray/*(:rank=1)*/  FloatReferenceArray(/*nat*/ int k, float initVal) { 
			return FloatReferenceArray(x10.lang.dist.factory.local(k), initVal);
		}
		/** Return the unique int value array initialized with init 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public FloatReferenceArray/*(:rank=1)*/ FloatReferenceArray(/*nat*/ int k, pointwiseOp init) {
			return FloatReferenceArray( x10.lang.dist.factory.local(k), init);
		}
		
		public FloatReferenceArray FloatReferenceArray( dist D) {
			return FloatReferenceArray( D, 0);
		}
		abstract public 
		/*(distribution D)*/ FloatReferenceArray/*(D)*/ FloatReferenceArray(dist D, float init);
		abstract public 
		/*(distribution D)*/ FloatReferenceArray/*(D)*/ FloatReferenceArray( dist D, 
				pointwiseOp/*(D.region)*/ init);
	}
	public static final factory factory = Runtime.factory.getFloatArrayFactory();
	
	/** Return the value of the array at the given point in the
	 * region.
	 */
	abstract public float get(point/*(region)*/ p);
	abstract /*value*/ public float get(int p);
	abstract /*value*/ public float get(int p, int q);
	abstract /*value*/ public float get(int p, int q, int r);
	abstract /*value*/ public float get(int p, int q, int r, int s);
    abstract public float get(int[] p);
    
    /** Convenience method for returning the sum of the array.
     * @return sum of the array.
     */
	public float sum() {
		return reduce(add, 0);
	}
	/**
	 * Convenience method for returning the max of the array.
	 * @return
	 */
	public float max() {
		return reduce(max, 0);
	}
	/**
	 * Convenience method for returning the max of the array after applying the given fun.
	 * @param fun
	 * @return
	 */
	public float max(unaryOp fun) {
		return lift(fun).reduce(max, 0);
	}
	/**
	 * Convenience method for applying abs to each element in the array.
	 * @return
	 */
	public FloatReferenceArray abs() {
		return lift(abs);
	}
	
	/**
	 * Convenience method for subtracting another array pointwise.
	 * @return
	 */
	public FloatReferenceArray sub( floatArray s) {
		return lift(sub, s);
	}
	/**
	 * Convenience method for subtracting another array pointwise.
	 * @return
	 */
	public FloatReferenceArray add( floatArray s) {
		return lift(add, s);
	}
	/**
	 * Convenience method for subtracting another array pointwise.
	 * @return
	 */
	public FloatReferenceArray mul( floatArray s) {
		return lift(mul, s);
	}
	/**
	 * Convenience method for subtracting another array pointwise.
	 * @return
	 */
	public FloatReferenceArray div( floatArray s) {
		return lift(div, s);
	}
	
	/**
	 * Convenience method for applying max after applying abs.
	 * @return
	 */
	public float maxAbs() {
		return max(abs);
	}
    
	/** Return the value obtained by reducing the given array with the
	 function fun, which is assumed to be associative and
	 commutative. unit should satisfy fun(unit,x)=x=fun(x,unit).
	 */
	abstract public float reduce(binaryOp fun, float unit);
	
	/** Return a FloatArray with the same distribution as this, by 
	 scanning this with the function fun, and unit unit.
	 */
	abstract public FloatReferenceArray/*(distribution)*/ scan(binaryOp fun, float unit);
	
	/** Return an array of B@P defined on the intersection of the
	 region underlying the array and the parameter region R.
	 */
	abstract public /*(region(rank) R)*/
	FloatReferenceArray/*(distribution.restriction(R)())*/  restriction(region R);
	
	/** Return an array of B@P defined on the intersection of 
	 the region underlying this and the parametric distribution.
	 */    
	public  /*(distribution(:rank=this.rank) D)*/ 
	FloatReferenceArray/*(distribution.restriction(D.region)())*/ restriction(dist D) {
	 return restriction(D.region);
	}
	public FloatReferenceArray restriction(place P) {
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
	FloatReferenceArray/*(distribution.union(other.distribution))*/ union( floatArray other);
	
	/** Return the array obtained by overlaying this array on top of
	 other. The method takes as parameter a distribution D over the
	 same rank. It returns an array over the distribution
	 dist.asymmetricUnion(D).
	 */
	abstract public /*(distribution(:rank=this.rank) D)*/
	FloatReferenceArray/*(distribution.asymmetricUnion(D))*/ overlay( floatArray/*(D)*/ other);
	
	/** Update this array in place by overlaying the array other on top of this. The distribution
	 * of the input array must be a subdistribution of D.
	 * TODO: update the description of the parametric type.
	 */
    abstract public void update( floatArray/*(D)*/ other);
    
	/** Assume given a FloatArray a over the given distribution.
	 * Assume given a function f: float -> float -> float.
	 * Return a FloatArray with distribution dist 
	 * containing fun(this.atValue(p),a.atValue(p)) for each p in
	 * dist.region.
	 */
	abstract public 
	FloatReferenceArray/*(distribution)*/ lift(binaryOp fun, floatArray/*(distribution)*/ a);
	abstract public 
	FloatReferenceArray/*(distribution)*/ lift(unaryOp fun);
	
	/**
	 * Return an immutable copy of this array. Note: The implementation actually returns a copy
	 * at the representation of the X10 type x10.lang.floatValueArray, which is floatArray.
	 * @return an immutable version of this array.
	 */
	abstract public floatArray toValueArray();
}
