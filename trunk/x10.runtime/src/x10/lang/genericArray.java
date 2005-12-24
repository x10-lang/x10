package x10.lang;


/** The class of all multidimensional, distributed non-primitive arrays in X10. Has no mutable data.
 * Specialized from array by replacing the type parameter with Parameter1.
 * Handtranslated from the X10 code in x10/lang/GenericArray.x10
 * 
 * @author vj 12/24/2004
 */

import java.util.Iterator;

import x10.compilergenerated.Parameter1;

abstract public class genericArray /*( distribution distribution )*/ 
/*implements Cloneable, Serializable */
extends x10Array {

	public final dist distribution;
	/*parameter*/ public final /*nat*/int rank /*= distribution.rank*/;
	/*parameter*/ public final region/*(rank)*/ region /*= distribution.region*/;
	
	protected genericArray( dist D) {
		this.distribution = D;
		this.region = D.region;
		this.rank = D.rank;
	}
	
	public static interface binaryOp {
		Parameter1 apply(Parameter1 r, Parameter1 s);
	}
	public static interface unaryOp {
		Parameter1 apply(Parameter1 r);
	}
	
	public static interface pointwiseOp/*(region r)*/ {
		Parameter1 apply(point/*(r)*/ p);
	}
	
	abstract public static /*value*/ class factory {
		/** Return the unique int value array initialized with 0 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public genericArray GenericValueArray( /*nat*/ int k) {
			return GenericValueArray(k, (Parameter1) null);
		}
		/** Return the unique int value array initialized with initVal 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public genericArray/*(:rank=1)*/  GenericValueArray(/*nat*/ int k, Parameter1 initVal) { 
			return GenericValueArray(x10.lang.dist.factory.local(k), initVal, false);
		}
		/** Return the unique int value array initialized with init 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public genericArray/*(:rank=1)*/ GenericValueArray(/*nat*/ int k, pointwiseOp init) {
			return GenericValueArray( x10.lang.dist.factory.local(k), init, false);
		}
		
		abstract public 
		/*(distribution D)*/ genericArray/*(D)*/ GenericValueArray(dist D, Parameter1 init, boolean refs_to_values);
		abstract public 
		/*(distribution D)*/ genericArray/*(D)*/ GenericValueArray( dist D, 
				pointwiseOp/*(D.region)*/ init, boolean refs_to_values);
		/** Return the unique int value array initialized with null 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public GenericReferenceArray GenericReferenceArray( /*nat*/ int k) {
			return GenericReferenceArray(k, (Parameter1) null);
		}
		/** Return the unique int value array initialized with initVal 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public GenericReferenceArray/*(:rank=1)*/  GenericReferenceArray(/*nat*/ int k, Parameter1 initVal) { 
			return GenericReferenceArray(x10.lang.dist.factory.local(k), initVal);
		}
		/** Return the unique int value array initialized with init 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public GenericReferenceArray/*(:rank=1)*/ GenericReferenceArray(/*nat*/ int k, pointwiseOp init) {
			return GenericReferenceArray( x10.lang.dist.factory.local(k), init);
		}
		
		public GenericReferenceArray GenericReferenceArray( dist D) {
			return GenericReferenceArray( D, (Parameter1) null);
		}
		abstract public 
		/*(distribution D)*/ GenericReferenceArray/*(D)*/ GenericReferenceArray(dist D, Parameter1 init);
		abstract public 
		/*(distribution D)*/ GenericReferenceArray/*(D)*/ GenericReferenceArray( dist D, 
				pointwiseOp/*(D.region)*/ init);
	}
	public static final factory factory = Runtime.factory.getGenericArrayFactory();
	
	/** Return the value of the array at the given point in the
	 * region.
	 */
	abstract public Parameter1 get(point/*(region)*/ p);
	abstract /*value*/ public Parameter1 get(int p);
	abstract /*value*/ public Parameter1 get(int p, int q);
	abstract /*value*/ public Parameter1 get(int p, int q, int r);
	abstract /*value*/ public Parameter1 get(int p, int q, int r, int s);
    abstract public Parameter1 get(int[] p);
    
	/** Return the value obtained by reducing the given array with the
	 function fun, which is assumed to be associative and
	 commutative. unit should satisfy fun(unit,x)=x=fun(x,unit).
	 */
	abstract public Parameter1 reduce(binaryOp fun, Parameter1 unit);
	
	/** Return a GenericArray with the same distribution as this, by 
	 scanning this with the function fun, and unit unit.
	 */
	abstract public GenericReferenceArray/*(distribution)*/ scan(binaryOp fun, Parameter1 unit);
	
	/** Return an array of B@P defined on the intersection of the
	 region underlying the array and the parameter region R.
	 */
	abstract public /*(region(rank) R)*/
	GenericReferenceArray/*(distribution.restriction(R)())*/  restriction(region R);
	
	/** Return an array of B@P defined on the intersection of 
	 the region underlying this and the parametric distribution.
	 */    
	public  /*(distribution(:rank=this.rank) D)*/ 
	GenericReferenceArray/*(distribution.restriction(D.region)())*/ restriction(dist D) {
	 return restriction(D.region);
	}
	
	public GenericReferenceArray restriction(place P) {
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
	GenericReferenceArray/*(distribution.union(other.distribution))*/ union( genericArray other);
	
	/** Return the array obtained by overlaying this array on top of
	 other. The method takes as parameter a distribution D over the
	 same rank. It returns an array over the distribution
	 dist.asymmetricUnion(D).
	 */
	abstract public /*(distribution(:rank=this.rank) D)*/
	GenericReferenceArray/*(distribution.asymmetricUnion(D))*/ overlay( genericArray/*(D)*/ other);
	
	/** Update this array in place by overlaying the array other on top of this. The distribution
	 * of the input array must be a subdistribution of D.
	 * TODO: update the description of the parametric type.
	 */
    abstract public void update( genericArray/*(D)*/ other);
    
	/** Assume given a GenericArray a over the given distribution.
	 * Assume given a function f: Parameter1 -> Parameter1 -> Parameter1.
	 * Return a GenericArray with distribution dist 
	 * containing fun(this.atValue(p),a.atValue(p)) for each p in
	 * dist.region.
	 */
	abstract public 
	GenericReferenceArray/*(distribution)*/ lift(binaryOp fun, genericArray/*(distribution)*/ a);
	abstract public 
	GenericReferenceArray/*(distribution)*/ lift(unaryOp fun);
	
	/**
	 * Return an immutable copy of this array. Note: The implementation actually returns a copy
	 * at the representation of the X10 type x10.lang.GenericValueArray, which is Parameter1Array.
	 * @return an immutable version of this array.
	 */
	abstract public genericArray toValueArray();
	
	public Iterator iterator() {
	 	return region.iterator();
	 }
	public dist toDistribution() {
		return distribution;
	}
}
