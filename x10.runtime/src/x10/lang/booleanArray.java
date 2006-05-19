package x10.lang;


/** The class of all multidimensional, distributed int arrays in X10. Has no mutable data.
 * Specialized from array by replacing the type parameter with int.
 
 * Handtranslated from the X10 code in x10/lang/BooleanArray.x10
 * 
 * @author vj 12/24/2004
 */


import x10.lang.intArray.binaryOp;

abstract public class booleanArray extends x10Array{

	protected booleanArray( dist D) {
		super(D);
	}
	
	public static interface binaryOp {
		boolean apply(boolean r, boolean s);
	}
	
    public static final binaryOp xor = new binaryOp() { public boolean apply(boolean r, boolean s) { return r ^ s;}};
    public static final binaryOp or = new binaryOp() { public boolean apply(boolean r, boolean s) { return r | s;}};
    public static final binaryOp and = new binaryOp() { public boolean apply(boolean r, boolean s) { return r & s;}};
    
    public static interface unaryOp {
        boolean apply(boolean r);
    }
    
    public static final unaryOp neg = new unaryOp() { public boolean apply(boolean r) { return !r;}};
    
	public static interface pointwiseOp/*(region r)*/ {
		boolean apply(point/*(r)*/ p);
	}
	
	abstract public static /*value*/ class factory {
		/** Return the unique boolean value array initialized with false 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public booleanArray booleanValueArray( /*nat*/ int k) {
			return booleanValueArray(k, false);
		}
		/** Return the unique int value array initialized with initVal 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public booleanArray/*(:rank=1)*/  booleanValueArray(/*nat*/ int k, boolean initVal) { 
			return booleanValueArray(x10.lang.dist.factory.local(k), initVal);
		}
		/** Return the unique int value array initialized with init 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public booleanArray/*(:rank=1)*/ booleanValueArray(/*nat*/ int k, pointwiseOp init) {
			return booleanValueArray( x10.lang.dist.factory.local(k), init);
		}
		
		abstract public 
		/*(distribution D)*/ booleanArray/*(D)*/ booleanValueArray(dist D, boolean init);
		abstract public 
		/*(distribution D)*/ booleanArray/*(D)*/ booleanValueArray( dist D, 
				pointwiseOp/*(D.region)*/ init);
		/** Return the unique boolean value array initialized with false 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public BooleanReferenceArray BooleanReferenceArray( /*nat*/ int k) {
			return BooleanReferenceArray(k, false);
		}
		/** Return the unique int value array initialized with initVal 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public BooleanReferenceArray/*(:rank=1)*/  BooleanReferenceArray(/*nat*/ int k, boolean initVal) { 
			return BooleanReferenceArray(x10.lang.dist.factory.local(k), initVal);
		}
		/** Return the unique int value array initialized with init 
		 * and defined over the distribution 0..k-1 -> here.
		 * 
		 * @param k
		 * @return
		 */
		public BooleanReferenceArray/*(:rank=1)*/ BooleanReferenceArray(/*nat*/ int k, pointwiseOp init) {
			return BooleanReferenceArray( x10.lang.dist.factory.local(k), init);
		}
		
		public BooleanReferenceArray BooleanReferenceArray( dist D) {
			return BooleanReferenceArray( D, false);
		}
		abstract public 
		/*(distribution D)*/ BooleanReferenceArray/*(D)*/ BooleanReferenceArray(dist D, boolean init);
		abstract public 
		/*(distribution D)*/ BooleanReferenceArray/*(D)*/ BooleanReferenceArray( dist D, 
				pointwiseOp/*(D.region)*/ init);
	}
	public static final factory factory = Runtime.factory.getBooleanArrayFactory();
	
	/** Return the value of the array at the given point in the
	 * region.
	 */
	abstract public boolean get(point/*(region)*/ p);
	abstract /*value*/ public boolean get(int p);
	abstract /*value*/ public boolean get(int p, int q);
	abstract /*value*/ public boolean get(int p, int q, int r);
	abstract /*value*/ public boolean get(int p, int q, int r, int s);
    abstract public boolean get(int[] p);
    
 
    
	/** Return the value obtained by reducing the given array with the
	 function fun, which is assumed to be associative and
	 commutative. unit should satisfy fun(unit,x)=x=fun(x,unit).
	 */
	abstract public boolean reduce(binaryOp fun, boolean unit);
	
	/** Return a BooleanArray with the same distribution as this, by 
	 scanning this with the function fun, and unit unit.
	 */
	abstract public BooleanReferenceArray/*(distribution)*/ scan(binaryOp fun, boolean unit);
	
	/** Return an array of B@P defined on the intersection of the
	 region underlying the array and the parameter region R.
	 */
	abstract public /*(region(rank) R)*/
	BooleanReferenceArray/*(distribution.restriction(R)())*/  restriction(region R);
	
	/** Return an array of B@P defined on the intersection of 
	 the region underlying this and the parametric distribution.
	 */    
	public  /*(distribution(:rank=this.rank) D)*/ 
	BooleanReferenceArray/*(distribution.restriction(D.region)())*/ restriction(dist D) {
	 return restriction(D.region);
	}
	
	public BooleanReferenceArray restriction(place P) {
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
	BooleanReferenceArray/*(distribution.union(other.distribution))*/ union( booleanArray other);
	
	/** Return the array obtained by overlaying this array on top of
	 other. The method takes as parameter a distribution D over the
	 same rank. It returns an array over the distribution
	 dist.asymmetricUnion(D).
	 */
	abstract public /*(distribution(:rank=this.rank) D)*/
	BooleanReferenceArray/*(distribution.asymmetricUnion(D))*/ overlay( booleanArray/*(D)*/ other);
	
	/** Update this array in place by overlaying the array other on top of this. The distribution
	 * of the input array must be a subdistribution of D.
	 * TODO: update the description of the parametric type.
	 */
    abstract public void update( booleanArray/*(D)*/ other);
    
	/** Assume given a BooleanArray a over the given distribution.
	 * Assume given a function f: boolean -> boolean -> boolean.
	 * Return a BooleanArray with distribution dist 
	 * containing fun(this.atValue(p),a.atValue(p)) for each p in
	 * dist.region.
	 */
	abstract public 
	BooleanReferenceArray/*(distribution)*/ lift(binaryOp fun, booleanArray/*(distribution)*/ a);
	abstract public 
	BooleanReferenceArray/*(distribution)*/ lift(unaryOp fun);
	
	/**
	 * Return an immutable copy of this array. Note: The implementation actually returns a copy
	 * at the representation of the X10 type x10.lang.booleanValueArray, which is booleanArray.
	 * @return an immutable version of this array.
	 */
	abstract public booleanArray toValueArray();
}
