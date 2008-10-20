/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.lang;

/**
 * A distribution is a mapping from a given region to a set of
 * places. It takes as parameter the region over which the mapping is
 * defined. The dimensionality of the distribution is the same as the
 * dimensionality of the underlying region.
 *
 * Handtranslated from the X10 source in x10/lang
 * @author vj
 * @date 12/24/2004
 */
import /*x10*/java.util.Set;
import java.util.Iterator;
import x10.lang.GlobalIndexMap;

abstract public /*value*/ class dist/*(region region)*/ extends Object
implements Indexable, ValueType {

	/*property*/ public final region region;

	/**
	 * The parameter rank may be used in constructing types derived
	 * from the class distribution. For instance, distribution(rank=k)
	 * is the type of all k-dimensional distributions.
	 */
	/*property*/ public final /*nat*/ int rank;
	/**
	 * Does the distribution map all points in its region to a single place, onePlace?
	 * If not, onePlace is null.
	 */
	/*property*/ public final place onePlace;
	/**
	 * == region.rect
	 */
	/*property*/ public final boolean rect;
	/**
	 *  == region.zeroBased
	 */
	/*property*/ public final boolean zeroBased;
	/**
	 * Does this distribution map exactly one point to each place? (And is it asserted as doing so.)
	 */
	/*property*/ public final boolean unique;
	/**
	 * Does the distribution map all points in its region to a single place?
	 * Is true iff onePlace != null
	 */
	/*property*/ public final boolean constant;
	/*property*/ public final boolean rail;

	public static final String propertyNames$ = " region rank onePlace rect rail zeroBased constant unique ";

	/*
	 * distribution is Indexable and as such regarded by the compiler as an X10array.
	 * Hence it must have a field 'distrubution' (see ateach construct)
	 */
	public final dist distribution;

	/**
	 * places is the range of the distribution. Guranteed that if a
	 * place P is in this set then for some point p in region,
	 * this.valueAt(p)==P.
	 */
	abstract public Set/*<place>*/ places(); // consider making this a parameter?

	public place[] placesArray() {
		java.lang.Object[] a = places().toArray();
		place[] res = new place[a.length];
		java.lang.System.arraycopy(a,0,res,0,a.length);
		return res;
	}

	protected dist(region R, place onePlace) {
		this(R, onePlace, false);

		//_indexMap = null;
	}

	protected dist(region R, place onePlace, boolean unique) {
		this.region = R;
		this.rank = R.rank;
		this.distribution = this;
		this.zeroBased = R.zeroBased;
		this.rect = R.rect;
		this.onePlace = onePlace;
		this.constant = onePlace != null;
		this.unique = unique;
		this.rail = rank==1&&zeroBased&&rect;
	}

	public static class MalformedError extends java.lang.Error {}

	abstract public static /*value*/ class factory extends Object implements ValueType {

		public factory() {}

		/**
		 * Returns the unique 1-dimensional distribution U over the region 1..k,
		 * (where k is the cardinality of Q) which maps the point [i] to the
		 * i'th element in Q in canonical place-order.
		 */
		abstract public
		dist/*(:rank=1)*/ unique(Set/*<place>*/ Q);
		public dist/*(:rank=1)*/ unique() {
			return unique(x10.lang.place.places);
		}
		
		public abstract dist/*(:rank==R.rank)*/ unique(region R);
			
		public /*(region R)*/ dist/*(R)*/ local(region R) {
			return constant(R, Runtime.here());
		}
		/**
		 * Return the distribution that maps the region 0..k-1 to here.
		 *
		 * @param k -- the upper bound of the 1-dimensional region. k >= 0
		 * @return the given distribution
		 */
		public dist/*(:rank=1)*/ local(int k) {
			return local(x10.lang.region.factory.region(0, k-1));
		}

		/**
		 * Returns the constant distribution which maps every point in its
		 * region to the given place P.
		 */
		abstract public
		/*(region R)*/ dist/*(R)*/ constant(region R, place P);

		public
		/*(region R)*/ dist/*(R)*/block() {
			dist result = this.block(x10.lang.region.factory.region(0, place.MAX_PLACES-1));
			return result;
		}

		/**
		 * Returns the block distribution over the given region, and over
		 * place.MAX_PLACES places.
		 */
		public
		/*(region R)*/ dist/*(R)*/ block(final region R) {
			final dist/*(R)*/ result = this.block/*(R)*/(R, x10.lang.place.places);
			assert result.region.equals(R);
			return result;
		}

		/**
		 * Returns the block distribution over the given region and the
		 * given set of places. Chunks of the region are distributed over
		 * s, in canonical order.
		 */
		abstract public
		/*(region R)*/ dist/*(R)*/ block(region R, Set/*<place>*/ s);

		public
		/*(region R)*/ dist/*(R)*/ block(final region base, final region[] R) {
			final dist/*(R)*/ result = this.block/*(R)*/(base, R, x10.lang.place.places);
			//assert result.region.equals(R);
			return result;
		}
		abstract public dist block(region base, region[] R, Set/*<places>*/ s);

		/**
		 * Returns the cyclic distribution over the given region, and over
		 * all places.
		 */
		public /*(region R)*/ dist/*(R)*/ cyclic(final region R) {
			/*final*/ dist result = this.cyclic/*(R)*/(R, x10.lang.place.places);
			assert result.region.equals(R);
			return result;
		}

		abstract public /*(region R)*/ dist/*(R)*/ cyclic(region R, Set/*<place>*/ s);

		/**
		 * Returns the block-cyclic distribution over the given region, and over
		 * place.MAX_PLACES places. Exception thrown if blockSize < 1.
		 */
		abstract public
		/*(region R)*/ dist/*(R)*/ blockCyclic(region R, /*nat*/int blockSize)
		throws MalformedError;

		abstract public
		/*(region R)*/ dist/*(R)*/ blockCyclic(region R, /*nat*/int blockSize, Set/*<place>*/ s)
		throws MalformedError;

		/**
		 * Returns a distribution which assigns a random place in the
		 * given set of places to each point in the region.
		 */
		abstract public
		/*(region R)*/ dist/*(R)*/ random(region R);

		/**
		 * Returns a distribution which assigns some arbitrary place in
		 * the given set of places to each point in the region. There are
		 * no guarantees on this assignment, e.g. all points may be
		 * assigned to the same place.
		 */
		abstract public
		/*(region R)*/ dist/*(R)*/ arbitrary(region R);

	}

	public static final factory factory = Runtime.factory.getDistributionFactory();
	public static dist  unique(region R) { return dist.factory.unique(R);}
		
	
	public static final dist UNIQUE = factory.unique();

	/**
	 * Returns the place to which the point p in region is mapped.
	 */
	abstract public place get(point/*(region)*/ p) throws MalformedError;
	public place get(int[] p) {
		return get(Runtime.factory.getPointFactory().point(p));
	}
	public place get(int i) {
		return get(new int[] {i});
	}
	public place get(int i, int j) {
		return get(new int[] {i, j});
	}
	public place get(int i, int j, int k) {
		return get(new int[] {i, j, k});
	}
	public place get(int i, int j, int k, int l) {
		return get(new int[] {i,j, k, l});
	}
	public place get(int i, int j, int k, int l, int m) {
		return get(new int[] {i, j, k, l, m});
	}

	/**
	 * Returns the region mapped by this distribution to the place P.
	 * The value returned is a subset of this.region.
	 */
	abstract public region/*(rank)*/ restrictToRegion(place P);

	public dist restriction(place P) {
		return dist.factory.constant(restrictToRegion(P), P);
	}

	/**
	 * This method is necessary because the compiler does not implement
	 * the automatic conversions of distributions to regions is necessary
	 * and hence a distribution is passed to the runtime where actually a
	 * region is expected.
	 */
	public dist restriction(dist P) {
		return restriction(P.region);
	}

	/**
	 * Returns the distribution obtained by range-restricting this to Ps.
	 * The region of the distribution returned is contained in this.region.
	 */
	abstract public dist/*(:this.region.contains(region))*/
	restriction(Set<place> Ps);

	/**
	 * Returns a new distribution obtained by restricting this to the
	 * domain region.intersection(R), where parameter R is a region
	 * with the same dimension.
	 */
	abstract public
	/*(region(rank) R)*/ dist/*(region.intersection(R))*/
	restriction(region/*(rank)*/ R);

	/**
	 * Returns the restriction of this to the domain region.difference(R),
	 * where parameter R is a region with the same dimension.
	 */
	abstract public
	/*(region(rank) R)*/ dist/*(region.difference(R))*/
	difference(region/*(rank)*/ R);

	/**
	 * Returns the restriction of this to the domain region.difference(D.region),
	 * where parameter D is a distribution with the same dimension.
	 */
	public
	/*(region(rank) R)*/ dist/*(region.difference(R))*/
	difference(dist /*(rank)*/ D) {
		return difference(D.region);
	}

	/**
	 * Takes as parameter a distribution D defined over a region
	 * disjoint from this. Returns a distribution defined over a
	 * region which is the union of this.region and D.region.
	 * This distribution must assume the value of D over D.region
	 * and this over this.region.
	 * 
	 * @seealso distribution.asymmetricUnion.
	 */
	abstract public /*(distribution(:region.disjoint(this.region) &&
	rank=this.rank) D)*/
	dist/*(region.union(D.region))*/
	union(dist/*(:region.disjoint(this.region) &&
	rank=this.rank)*/ D);

	abstract public dist/*(:rank=this.rank)*/
	intersection(dist /*(:rank=this.rank)*/D);

	/**
	 * Returns a distribution defined on region.union(R): it
	 * takes on D.get(p) for all points p in R, and this.get(p) for
	 * all remaining points.
	 */
	abstract public /*(region(rank) R)*/ dist/*(region.union(R))*/
	overlay(dist/*(R)*/ D);

	/**
	 * Return true iff the given distribution D, which must be over a
	 * region of the same rank as this, is defined over a subset
	 * of this.region and agrees with it at each point.
	 */
	abstract public /*(region(rank) r)*/
	boolean subDistribution(region/*(rank)*/ R, dist/*(R)*/ D);

	public boolean contains(point p) {
		return this.region.contains(p);
	}

	/**
	 * Returns true iff this and d map each point in their common
	 * domain to the same place.
	 */
	public boolean equals(java.lang.Object o) {
		if (! (o instanceof dist)) return false;
		dist ot = (dist) o;
		if (! (ot.region.equals(this.region))) return false;

		return this.subDistribution(region,ot)
		      && ot.subDistribution(region, this);
	}

	public Iterator iterator() {
		return region.iterator();
	}

	public dist toDistribution() {
		return this;
	}

	/**
	 * @return fraction of "non-idle slots" if we view this distribution as a load distribution
	 */
	private float distributionEfficiency = -1;
	public float distributionEfficiency() {
		if (distributionEfficiency >= 0) {
			return distributionEfficiency;
		}
		int maxPoints;
		// 1) Compute number of points per place, and total number of points
		int totalPoints = 0;
		int pointCount[] = new int[place.MAX_PLACES];
		for (Iterator it = iterator(); it.hasNext(); ) {
			point p = (point) it.next();
			place pl = get(p);
			pointCount[pl.id]++;
			totalPoints++;
		}
		// 2) Compute max of pointCount array
		maxPoints = pointCount[0];
		for (int i = 1; i < pointCount.length; i++)
			if (pointCount[i] > maxPoints)
				maxPoints = pointCount[i];
		// 3) Return fraction of "non-idle" slots
			distributionEfficiency = totalPoints / ((float) maxPoints * (float) pointCount.length);
		return distributionEfficiency;
	}

	public int rank() {
		return rank;
	}
	public boolean rect() {
		return rect;
	}
	public boolean zeroBased() {
		return zeroBased;
	}
	public region region() {
		return region;
	}
	public place onePlace() {
		return onePlace;
	}
	public boolean constant() { return constant; }
	public boolean unique() { return unique; }
	/**
	 * dim must be in 0..rank-1.
	 * It is assumed that the projection (in the dim'th dimension) of two different points in the 
	 * underlying region yields two different points. Under this assumption, this function must return 
	 * the (uniquely defined) distribution which maps a point p to get(q) where q is the unique point 
	 * in region such that q.project(dim)==p. 
	 * <p>
	 * The rank of the resulting distribution is this.rank-1.
	 * @param dim
	 * @return
	 */
	public abstract dist project(int dim);
}
