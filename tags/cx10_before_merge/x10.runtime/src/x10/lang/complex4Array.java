package x10.lang;


/** The class of all multidimensional, distributed int arrays in X10. Has no mutable data.
 * Specialized from array by replacing the type parameter with int.
 
 * Handtranslated from the X10 code in x10/lang/FloatArray.x10
 * 
 * @author cmd
 */

import java.util.Iterator;

abstract public class complex4Array /*( distribution distribution )*/ 
/*implements Cloneable, Serializable */
implements Indexable, Unsafe {

	public final dist distribution;
	/*parameter*/ public final /*nat*/int rank /*= distribution.rank*/;
	/*parameter*/ public final region/*(rank)*/ region /*= distribution.region*/;
	
	protected complex4Array( dist D) {
		this.distribution = D;
		this.region = D.region;
		this.rank = D.rank;
	}
	
	public static interface binaryOp {
		float applyReal(float rr,float ri, float sr,float si);
		float applyImag(float rr,float ri, float sr,float si);
	}
	/*
	public static final binaryOp sub = new binaryOp() { public float apply(float rr,float ri,float sr,float si) { return r-s;}};
	public static final binaryOp add = new binaryOp() { public float apply(float rr,float ri,float sr,float si) { return r+s;}};
	public static final binaryOp mul = new binaryOp() { public float apply(float rr,float ri,float sr,float si) { return r*s;}};
	public static final binaryOp div = new binaryOp() { public float apply(float rr,float ri,float sr,float si) { return r/s;}};
	*/
	public static interface unaryOp {
		float apply(float r);
	}
	/*
	public static final unaryOp abs = new unaryOp() { public float apply(float r) { return Math.abs(r);}};
	*/
	public static interface pointwiseOp/*(region r)*/ {
		float applyReal(point/*(r)*/ p);
		float applyImag(point/*(r)*/ p);
	}
	
	abstract public static /*value*/ class factory {
		/** Return the unique int value array initialized with 0 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public complex4Array complex4ValueArray( /*nat*/ int k) {
			return complex4ValueArray(k, 0);
		}
		/** Return the unique int value array initialized with initVal 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public complex4Array/*(:rank=1)*/  complex4ValueArray(/*nat*/ int k, float initVal) { 
			return complex4ValueArray(x10.lang.dist.factory.local(k), initVal);
		}
		/** Return the unique int value array initialized with init 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public complex4Array/*(:rank=1)*/ complex4ValueArray(/*nat*/ int k, pointwiseOp init) {
			return complex4ValueArray( x10.lang.dist.factory.local(k), init);
		}
		
		abstract public 
		/*(distribution D)*/ complex4Array/*(D)*/ complex4ValueArray(dist D, float init);
		abstract public 
		/*(distribution D)*/ complex4Array/*(D)*/ complex4ValueArray( dist D, 
				pointwiseOp/*(D.region)*/ init);
		/** Return the unique int value array initialized with 0 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public Complex4ReferenceArray Complex4ReferenceArray( /*nat*/ int k) {
			return Complex4ReferenceArray(k, 0);
		}
		/** Return the unique int value array initialized with initVal 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public Complex4ReferenceArray/*(:rank=1)*/  Complex4ReferenceArray(/*nat*/ int k, float initVal) { 
			return Complex4ReferenceArray(x10.lang.dist.factory.local(k), initVal);
		}
		/** Return the unique int value array initialized with init 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public Complex4ReferenceArray/*(:rank=1)*/ Complex4ReferenceArray(/*nat*/ int k, pointwiseOp init) {
			return Complex4ReferenceArray( x10.lang.dist.factory.local(k), init);
		}
		
		public Complex4ReferenceArray Complex4ReferenceArray( dist D) {
			return Complex4ReferenceArray( D, 0);
		}
		abstract public 
		/*(distribution D)*/ Complex4ReferenceArray/*(D)*/ Complex4ReferenceArray(dist D, float init);
		abstract public 
		/*(distribution D)*/ Complex4ReferenceArray/*(D)*/ Complex4ReferenceArray( dist D, 
				pointwiseOp/*(D.region)*/ init);
	}
	public static final factory factory = Runtime.factory.getComplex4ArrayFactory();
	
	/** Return the value of the array at the given point in the
	 * region.
	 */
	abstract public float getReal(point/*(region)*/ p);
	abstract /*value*/ public float getReal(int p);
	abstract /*value*/ public float getReal(int p, int q);
	abstract /*value*/ public float getReal(int p, int q, int r);
	abstract /*value*/ public float getReal(int p, int q, int r, int s);
	abstract public float getImag(point/*(region)*/ p);
	abstract /*value*/ public float getImag(int p);
	abstract /*value*/ public float getImag(int p, int q);
	abstract /*value*/ public float getImag(int p, int q, int r);
	abstract /*value*/ public float getImag(int p, int q, int r, int s);
    abstract public float getReal(int[] p);
    abstract public float getImag(int[] p);
    
    /** Convenience method for returning the sum of the array.
     * @return sum of the array.
     */
	public float sum() {
	if (true) throw new RuntimeException("unimplemented"); return 0;//	return reduce(add, 0);
	}
	/**
	 * Convenience method for returning the max of the array.
	 * @return
	 */
	public float max() {
		if (true) throw new RuntimeException("unimplemented"); return 0;//return reduce(max, 0);
	}
	/**
	 * Convenience method for returning the max of the array after applying the given fun.
	 * @param fun
	 * @return
	 */
	public float max(unaryOp fun) {
		if (true) throw new RuntimeException("unimplemented"); return 0;//return lift(fun).reduce(max, 0);
	}
	/**
	 * Convenience method for applying abs to each element in the array.
	 * @return
	 */
//	public Complex4ReferenceArray abs() {
//		if (true) throw new RuntimeException("unimplemented"); return 0;//	return lift(abs);
//	}
	
	/**
	 * Convenience method for subtracting another array pointwise.
	 * @return
	 */
//	public Complex4ReferenceArray sub( complex4Array s) {
//		if (true) throw new RuntimeException("unimplemented"); return 0;//	return lift(sub, s);
//	}
	/**
	 * Convenience method for subtracting another array pointwise.
	 * @return
	 */
//	public Complex4ReferenceArray add( complex4Array s) {
//		if (true) throw new RuntimeException("unimplemented"); return 0;//	return lift(add, s);
//	}
	/**
	 * Convenience method for subtracting another array pointwise.
	 * @return
	 */
//	public Complex4ReferenceArray mul( complex4Array s) {
//		if (true) throw new RuntimeException("unimplemented"); return 0;//return lift(mul, s);
//	}
	/**
	 * Convenience method for subtracting another array pointwise.
	 * @return
	 */
//	public Complex4ReferenceArray div( complex4Array s) {
//		if (true) throw new RuntimeException("unimplemented"); return 0;//return lift(div, s);
//	}
	
	/**
	 * Convenience method for applying max after applying abs.
	 * @return
	 */
	public float maxAbs() {
		if (true) throw new RuntimeException("unimplemented"); return 0;//	return max(abs);
	}
    
	/** Return the value obtained by reducing the given array with the
	 function fun, which is assumed to be associative and
	 commutative. unit should satisfy fun(unit,x)=x=fun(x,unit).
	 */
	abstract public float reduce(binaryOp fun, float unit);
	
	/** Return a Complex4Array with the same distribution as this, by 
	 scanning this with the function fun, and unit unit.
	 */
	abstract public Complex4ReferenceArray/*(distribution)*/ scan(binaryOp fun, float unit);
	
	/** Return an array of B@P defined on the intersection of the
	 region underlying the array and the parameter region R.
	 */
	abstract public /*(region(rank) R)*/
	Complex4ReferenceArray/*(distribution.restriction(R)())*/  restriction(region R);
	
	/** Return an array of B@P defined on the intersection of 
	 the region underlying this and the parametric distribution.
	 */    
	public  /*(distribution(:rank=this.rank) D)*/ 
	Complex4ReferenceArray/*(distribution.restriction(D.region)())*/ restriction(dist D) {
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
	Complex4ReferenceArray/*(distribution.union(other.distribution))*/ union( complex4Array other);
	
	/** Return the array obtained by overlaying this array on top of
	 other. The method takes as parameter a distribution D over the
	 same rank. It returns an array over the distribution
	 dist.asymmetricUnion(D).
	 */
	abstract public /*(distribution(:rank=this.rank) D)*/
	Complex4ReferenceArray/*(distribution.asymmetricUnion(D))*/ overlay( complex4Array/*(D)*/ other);
	
	/** Update this array in place by overlaying the array other on top of this. The distribution
	 * of the input array must be a subdistribution of D.
	 * TODO: update the description of the parametric type.
	 */
    abstract public void update( complex4Array/*(D)*/ other);
    
	/** Assume given a Complex4Array a over the given distribution.
	 * Assume given a function f: float -> float -> float.
	 * Return a Complex4Array with distribution dist 
	 * containing fun(this.atValue(p),a.atValue(p)) for each p in
	 * dist.region.
	 */
	abstract public 
	Complex4ReferenceArray/*(distribution)*/ lift(binaryOp fun, complex4Array/*(distribution)*/ a);
	abstract public 
	Complex4ReferenceArray/*(distribution)*/ lift(unaryOp fun);
	
	/**
	 * Return an immutable copy of this array. Note: The implementation actually returns a copy
	 * at the representation of the X10 type x10.lang.complex4ValueArray, which is complex4Array.
	 * @return an immutable version of this array.
	 */
	abstract public complex4Array toValueArray();
	
	public Iterator iterator() {
	 	return region.iterator();
	 }
	public dist toDistribution() {
		return distribution;
	}
}
