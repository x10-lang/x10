package x10.lang;

/** The class of all multidimensional, distributed int arrays in X10.
 * Specialized from array by replacing the type parameter with int.
 
 * Handtranslated from the X10 code in x10/lang/DoubleArray.x10
 * 
 * @author vj 12/24/2004
 */

abstract public /*value*/ class DoubleArray /*( distribution distribution )*/ {
	public final distribution distribution;
	/*parameter*/ public final /*nat*/long rank /*= distribution.rank*/;
	/*parameter*/ public final region/*(rank)*/ region /*= distribution.region*/;
	
	protected DoubleArray( distribution D) {
		this.distribution = D;
		this.region = D.region;
		this.rank = D.rank;
	}
	
	public static interface binaryOp {
		double apply(double r, double s);
	}
	
	public static interface pointwiseOp/*(region r)*/ {
		double apply(point/*(r)*/ p);
	}
	
	abstract public static /*value*/ class factory {
		abstract public 
		/*(distribution D)*/ DoubleArray/*(D)*/ DoubleArray(distribution D, double init);
		abstract public 
		/*(distribution D)*/ DoubleArray/*(D)*/ DoubleArray( distribution D, pointwiseOp/*(D.region)*/ init);
	}
	public static final factory factory = Runtime.factory.getDoubleArrayFactory();
	
	/** Return the value of the array at the given point in the
	 * region.
	 */
	abstract public double get(point/*(region)*/ p);
	
	abstract public void set( double v, point/*(region)*/ p);
	
	/** Return the value obtained by reducing the given array with the
	 function fun, which is assumed to be associative and
	 commutative. unit should satisfy fun(unit,x)=x=fun(x,unit).
	 */
	abstract public double reduce(binaryOp fun, double unit);
	
	/** Return a DoubleArray with the same distribution as this, by 
	 scanning this with the function fun, and unit unit.
	 */
	abstract public DoubleArray/*(distribution)*/ scan(binaryOp fun, double unit);
	
	/** Return an array of B@P defined on the intersection of the
	 region underlying the array and the parameter region R.
	 */
	abstract public /*(region(rank) R)*/
	DoubleArray/*(distribution.restriction(R)())*/  restriction(region R);
	
	/** Return an array of B@P defined on the intersection of 
	 the region underlying this and the parametric distribution.
	 */    
	public  /*(distribution(:rank=this.rank) D)*/ 
	DoubleArray/*(distribution.restriction(D.region)())*/ restriction(distribution D) {
	 throw new Error("TODO");
	}
	
	/** Take as parameter a distribution D of the same rank as *
	 * this, and defined over a disjoint region. Take as argument an *
	 * array other over D. Return an array whose distribution is the
	 * union of this and D and which takes on the value
	 * this.atValue(p) for p in this.region and other.atValue(p) for p
	 * in other.region.
	 */
	abstract public /*(distribution(:region.disjoint(this.region) && rank=this.rank) D)*/
	DoubleArray/*(distribution.union(other.distribution))*/ union( DoubleArray other);
	
	/** Return the array obtained by overlaying this array on top of
	 other. The method takes as parameter a distribution D over the
	 same rank. It returns an array over the distribution
	 dist.asymmetricUnion(D).
	 */
	abstract public /*(distribution(:rank=this.rank) D)*/
	DoubleArray/*(distribution.asymmetricUnion(D))*/ overlay( DoubleArray/*(D)*/ other);
	
	
	/** Assume given a DoubleArray a over the given distribution.
	 * Assume given a function f: double -> double -> double.
	 * Return a DoubleArray with distribution dist 
	 * containing fun(this.atValue(p),a.atValue(p)) for each p in
	 * dist.region.
	 */
	abstract public 
	DoubleArray/*(distribution)*/ lift(binaryOp fun, DoubleArray/*(distribution)*/ a);
	
}
