package x10.lang;


/** The class of all multidimensional, distributed int arrays in X10. Has no mutable data.
 * Specialized from array by replacing the type parameter with int.
 
 * Handtranslated from the X10 code in x10/lang/CharArray.x10
 * 
 * @author vj 12/24/2004
 */

import java.util.Iterator;

abstract public class charArray extends x10Array{

	public final dist distribution;
	/*parameter*/ public final /*nat*/int rank /*= distribution.rank*/;
	/*parameter*/ public final region/*(rank)*/ region /*= distribution.region*/;
    
	public region getRegion() { return region; }
	public dist getDistribution() { return distribution;}
    
	protected charArray( dist D) {
		this.distribution = D;
		this.region = D.region;
		this.rank = D.rank;
	}
	
	public static interface binaryOp {
		char apply(char r, char s);
	}

	public static interface unaryOp {
		char apply(char r);
	}

	
	public static interface pointwiseOp/*(region r)*/ {
		char apply(point/*(r)*/ p);
	}
	
	abstract public static /*value*/ class factory {
		/** Return the unique char value array initialized with 0 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public charArray charValueArray( /*nat*/ int k) {
			return charValueArray(k, (char) 0);
		}
		/** Return the unique int value array initialized with initVal 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public charArray/*(:rank=1)*/  charValueArray(/*nat*/ int k, char initVal) { 
			return charValueArray(x10.lang.dist.factory.local(k), initVal);
		}
		/** Return the unique int value array initialized with init 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public charArray/*(:rank=1)*/ charValueArray(/*nat*/ int k, pointwiseOp init) {
			return charValueArray( x10.lang.dist.factory.local(k), init);
		}
		
		abstract public 
		/*(distribution D)*/ charArray/*(D)*/ charValueArray(dist D, char init);
		abstract public 
		/*(distribution D)*/ charArray/*(D)*/ charValueArray( dist D, 
				pointwiseOp/*(D.region)*/ init);
		/** Return the unique char value array initialized with 0 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public CharReferenceArray CharReferenceArray( /*nat*/ int k) {
			return CharReferenceArray(k, (char)0);
		}
		/** Return the unique char value array initialized with initVal 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public CharReferenceArray/*(:rank=1)*/  CharReferenceArray(/*nat*/ int k, char initVal) { 
			return CharReferenceArray(x10.lang.dist.factory.local(k), initVal);
		}
		/** Return the unique char value array initialized with init 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public CharReferenceArray/*(:rank=1)*/ CharReferenceArray(/*nat*/ int k, pointwiseOp init) {
			return CharReferenceArray( x10.lang.dist.factory.local(k), init);
		}
		
		public CharReferenceArray CharReferenceArray( dist D) {
			return CharReferenceArray( D, (char) 0);
		}
		abstract public 
		/*(distribution D)*/ CharReferenceArray/*(D)*/ CharReferenceArray(dist D, char init);
		abstract public 
		/*(distribution D)*/ CharReferenceArray/*(D)*/ CharReferenceArray( dist D, 
				pointwiseOp/*(D.region)*/ init);
	}
	public static final factory factory = Runtime.factory.getCharArrayFactory();
	
	/** Return the value of the array at the given point in the
	 * region.
	 */
	abstract public char get(point/*(region)*/ p);
	abstract /*value*/ public char get(int p);
	abstract /*value*/ public char get(int p, int q);
	abstract /*value*/ public char get(int p, int q, int r);
	abstract /*value*/ public char get(int p, int q, int r, int s);
    abstract public char get(int[] p);
    
	/** Return the value obtained by reducing the given array with the
	 function fun, which is assumed to be associative and
	 commutative. unit should satisfy fun(unit,x)=x=fun(x,unit).
	 */
	abstract public char reduce(binaryOp fun, char unit);
	
	/** Return a CharArray with the same distribution as this, by 
	 scanning this with the function fun, and unit unit.
	 */
	abstract public CharReferenceArray/*(distribution)*/ scan(binaryOp fun, char unit);
	
	/** Return an array of B@P defined on the intersection of the
	 region underlying the array and the parameter region R.
	 */
	abstract public /*(region(rank) R)*/
	CharReferenceArray/*(distribution.restriction(R)())*/  restriction(region R);
	
	/** Return an array of B@P defined on the intersection of 
	 the region underlying this and the parametric distribution.
	 */    
	public  /*(distribution(:rank=this.rank) D)*/ 
	CharReferenceArray/*(distribution.restriction(D.region)())*/ restriction(dist D) {
	 return restriction(D.region);
	}
	public CharReferenceArray restriction(place P) {
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
	CharReferenceArray/*(distribution.union(other.distribution))*/ union( charArray other);
	
	/** Return the array obtained by overlaying this array on top of
	 other. The method takes as parameter a distribution D over the
	 same rank. It returns an array over the distribution
	 dist.asymmetricUnion(D).
	 */
	abstract public /*(distribution(:rank=this.rank) D)*/
	CharReferenceArray/*(distribution.asymmetricUnion(D))*/ overlay( charArray/*(D)*/ other);
	
	/** Update this array in place by overlaying the array other on top of this. The distribution
	 * of the input array must be a subdistribution of D.
	 * TODO: update the description of the parametric type.
	 */
    abstract public void update( charArray/*(D)*/ other);
    
	/** Assume given a CharArray a over the given distribution.
	 * Assume given a function f: char -> char -> char.
	 * Return a CharArray with distribution dist 
	 * containing fun(this.atValue(p),a.atValue(p)) for each p in
	 * dist.region.
	 */
	abstract public 
	CharReferenceArray/*(distribution)*/ lift(binaryOp fun, charArray/*(distribution)*/ a);
	abstract public 
	CharReferenceArray/*(distribution)*/ lift(unaryOp fun);
	
	/**
	 * Return an immutable copy of this array. Note: The implementation actually returns a copy
	 * at the representation of the X10 type x10.lang.charValueArray, which is charArray.
	 * @return an immutable version of this array.
	 */
	abstract public charArray toValueArray();
	
	public Iterator iterator() {
	 	return region.iterator();
	 }
	public dist toDistribution() {
		return distribution;
	}
}
