package x10.lang;

/** A distribution is a mapping from a given region to a set of
 * places. It takes as parameter the region over which the mapping is
 * defined. The dimensionality of the distribution is the same as the
 * dimensionality of the underlying region.
 
 * Handtranslated from the X10 source in x10/lang
 @author vj
 @date 12/24/2004
 */
import /*x10*/java.util.Set;
import java.util.Iterator;

abstract public /*value*/ class distribution/*( region region )*/ extends Object 
implements Indexable {
	public final region region;
	/** The parameter dimension may be used in constructing types derived
	 * from the class distribution. For instance,
	 * distribution(dimension=k) is the type of all k-dimensional
	 * distributions.
	 */
	/*parameter*/ public final /*nat*/ int rank;
    
    /* disrtibution is Indexable and as such regarded by the compiler as an X10array.
     * Hence it must have a field 'distrubution' (see ateach construct) */
    public final distribution distribution;
	
	/** places is the range of the distribution. Guranteed that if a
	 * place P is in this set then for some point p in region,
	 * this.valueAt(p)==P.
	 */
	abstract public Set/*<place>*/ places(); // consider making this a parameter?

	protected distribution(region R) {
		this.region = R;
		this.rank = R.rank;
        this.distribution = this;
	}
	
	public static class MalformedError extends java.lang.Error {}
	
	abstract public static /*value*/ class factory extends Object {
		
		public factory() {}
		
		/** Returns the unique 1-dimensional distribution U over the region 1..k,
		 * (where k is the cardinality of Q) which maps the point [i] to the 
		 * i'th element in Q in canonical place-order.
		 */
		abstract public 
		distribution/*(:rank=1)*/ unique( Set/*<place>*/ Q );
		public distribution/*(:rank=1)*/ unique() {
			return unique( x10.lang.place.places );
		}
		public /*(region R)*/ distribution/*(R)*/ local( region R) {
			return constant( R, Runtime.here());
		}
		/** Return the distribution that maps the region 0..k-1 to here.
		 * 
		 * @param k -- the upper bound of the 1-dimensional region. k >= 0
		 * @return the given distribution
		 */
		public distribution/*(:rank=1)*/ local( int k) {
			return local(x10.lang.region.factory.region(k));
		}
		
		/** Returns the constant distribution which maps every point in its
		 region to the given place P.
		 */
		abstract public 
		/*(region R)*/ distribution/*(R)*/ constant( region R, place P );
		
		/** Returns the block distribution over the given region, and over
		 * place.MAX_PLACES places.
		 */
		public 
		/*(region R)*/ distribution/*(R)*/ block( final region R ) {
			final distribution/*(R)*/ result = this.block/*(R)*/(R, x10.lang.place.places);
			assert result.region.equals(R); 
			return result;
		}
		
		/** Returns the block distribution over the given region and the
		 * given set of places. Chunks of the region are distributed over
		 * s, in canonical order.
		 */
		abstract public  
		/*(region R)*/ distribution/*(R)*/ block( region R, Set/*<place>*/ s);
		
		
		/** Returns the cyclic distribution over the given region, and over
		 * all places.
		 */
		public /*(region R)*/ distribution/*(R)*/ cyclic( final region R ) {
			final distribution result = this.cyclic/*(R)*/(R, x10.lang.place.places);
			assert result.region.equals(R);
			return result;
		}
		
		abstract public /*(region R)*/ distribution/*(R)*/ cyclic( region R, Set/*<place>*/ s);
		
		/** Returns the block-cyclic distribution over the given region, and over
		 * place.MAX_PLACES places. Exception thrown if blockSize < 1.
		 */
		abstract public  
		/*(region R)*/ distribution/*(R)*/ blockCyclic( region R, /*nat*/int blockSize) 
		throws MalformedError;
		
		/** Returns a distribution which assigns a random place in the
		 * given set of places to each point in the region.
		 */
		abstract public  
		/*(region R)*/ distribution/*(R)*/ random( region R );
		
		/** Returns a distribution which assigns some arbitrary place in
		 * the given set of places to each point in the region. There are
		 * no guarantees on this assignment, e.g. all points may be
		 * assigned to the same place.
		 */
		abstract public 
		/*(region R)*/ distribution/*(R)*/ arbitrary( region R );
		
	}	
	
	public static final factory factory = Runtime.factory.getDistributionFactory();
	
	/** Returns the place to which the point p in region is mapped.
	 */
	abstract public place get(point/*(region)*/ p) throws MalformedError;
	public place get(int[] p) {
		return get(Runtime.factory.getPointFactory().point(region, p));
	}
	public place get(int i) {
		return get(new int[] {i} );
	}
	public place get(int i, int j) {
		return get(new int[] {i, j} );
	}
	public place get(int i, int j, int k) {
		return get(new int[] {i, j, k} );
	}
	public place get(int i, int j, int k, int l) {
		return get(new int[] {i,j, k, l} );
	}
	public place get(int i, int j, int k, int l, int m) {
		return get(new int[] {i, j, k, l, m} );
	}
    
	public void checkAccess(point p) {
	    // System.out.println("CHECK ACCESS!");
            // assert this.get(p) == x10.lang.Runtime.here();
        }
	
	/** Returns the region mapped by this distribution to the place P.
	 The value returned is a subset of this.region.
	 */
	abstract public region/*(rank)*/ restrictToRegion( place P );
	
	public distribution restriction(place P) {
		return distribution.factory.constant(restrictToRegion(P), P);
	}
	
	/** Returns the distribution obtained by range-restricting this to Ps.
	 The region of the distribution returned is contained in this.region.
	 */
	abstract public distribution/*(:this.region.contains(region))*/
	restriction( Set/*<place>*/Ps );
	
	/** Returns a new distribution obtained by restricting this to the
	 * domain region.intersection(R), where parameter R is a region
	 * with the same dimension.
	 */
	abstract public 
	/*(region(rank) R)*/ distribution/*(region.intersection(R))*/
	restriction( region/*(rank)*/ R);
	
	/** Returns the restriction of this to the domain region.difference(R),
	 where parameter R is a region with the same dimension.
	 */
	abstract public 
	/*(region(rank) R)*/ distribution/*(region.difference(R))*/
	difference( region/*(rank)*/ R);
	
	/** Returns the restriction of this to the domain region.difference(D.region),
	 where parameter D is a distribution with the same dimension.
	 */
public 
	/*(region(rank) R)*/ distribution/*(region.difference(R))*/
	difference( distribution /*(rank)*/ D) {
		return difference(D.region);
	}
	
	/** Takes as parameter a distribution D defined over a region
	 disjoint from this. Returns a distribution defined over a
	 region which is the union of this.region and D.region.
	 This distribution must assume the value of D over D.region
	 and this over this.region.
	 
	 @seealso distribution.asymmetricUnion.
	 */
	abstract public /*(distribution(:region.disjoint(this.region) &&
	rank=this.rank) D)*/ 
	distribution/*(region.union(D.region))*/
	union(distribution/*(:region.disjoint(this.region) &&
	rank=this.rank)*/ D);
	
	abstract public distribution/*(:rank=this.rank)*/ intersection( distribution /*(:rank=this.rank)*/D);
    
	/** Returns a distribution defined on region.union(R): it
	 takes on D.get(p) for all points p in R, and this.get(p) for
	 all remaining points.
	 */
	abstract public /*(region(rank) R)*/ distribution/*(region.union(R))*/ 
	overlay(  distribution/*(R)*/ D);
	
	
	/** Return true iff the given distribution D, which must be over a
	 * region of the same rank as this, is defined over a subset
	 * of this.region and agrees with it at each point.
	 */
	abstract public /*(region(rank) r)*/ 
	boolean subDistribution( region/*(rank)*/ R, distribution/*(R)*/ D); 
	
	public boolean contains( point p ) {
		return this.region.contains(p);
	}
	
	/** Returns true iff this and d map each point in their common
	 * domain to the same place.
	 */
	public boolean equals(java.lang.Object o) {
		if (! (o instanceof distribution)) return false;
		distribution ot = (distribution) o;
		if (! (ot.region.equals(this.region))) return false;
		
		return this.subDistribution(region,ot) 
		      && ot.subDistribution(region, this);
	}
	 public Iterator iterator() {
	 	return region.iterator();
	 }
	
	public distribution toDistribution() {
		return this;
	}
}
