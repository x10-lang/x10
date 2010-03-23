package x10.lang;

/** A distribution is a mapping from a given region to a set of
 * places. It takes as parameter the region over which the mapping is
 * defined. The dimensionality of the distribution is the same as the
 * dimensionality of the underlying region.

   @author vj
   @date 12/24/2004
 */

public final value class distribution( region region ) {
    /** The parameter dimension may be used in constructing types derived
     * from the class distribution. For instance,
     * distribution(dimension=k) is the type of all k-dimensional
     * distributions.
     */
    parameter int dimension = region.dimension;

    /** places is the range of the distribution. Guranteed that if a
     * place P is in this set then for some point p in region,
     * this.valueAt(p)==P.
     */
    public final Set<place> places; // consider making this a parameter?

    /** Returns the place to which the point p in region is mapped.
     */
    extern public place valueAt(point(region) p);

    /** Returns the region mapped by this distribution to the place P.
        The value returned is a subset of this.region.
     */
    extern public region(dimension) restriction( place P );

    /** Returns the distribution obtained by range-restricting this to Ps.
        The region of the distribution returned is contained in this.region.
     */
    extern public distribution(:this.region.contains(region))
        restriction( Set<place> Ps );

    /** Returns a new distribution obtained by restricting this to the
     * domain region.intersection(R), where parameter R is a region
     * with the same dimension.
     */
    extern public (region(dimension) R) distribution(region.intersection(R))
        restriction();

    /** Returns the restriction of this to the domain region.difference(R),
        where parameter R is a region with the same dimension.
     */
    extern public (region(dimension) R) distribution(region.difference(R))
        difference();

    /** Takes as parameter a distribution D defined over a region
        disjoint from this. Returns a distribution defined over a
        region which is the union of this.region and D.region.
        This distribution must assume the value of D over D.region
        and this over this.region.

        @seealso distribution.asymmetricUnion.
     */
    extern public (distribution(:region.disjoint(this.region) &&
                                dimension=this.dimension) D)
        distribution(region.union(D.region)) union();

    /** Returns a distribution defined on region.union(R): it takes on
        this.valueAt(p) for all points p in region, and D.valueAt(p) for all
        points in R.difference(region).
     */
    extern public (region(dimension) R) distribution(region.union(R))
        asymmetricUnion( distribution(R) D);

    /** Return a distribution on region.setMinus(R) which takes on the
     * same value at each point in its domain as this. R is passed as
     * a parameter; this allows the type of the return value to be
     * parametric in R.
     */
    extern public (region(dimension) R) distribution(region.setMinus(R))
        setMinus();

    /** Return true iff the given distribution D, which must be over a
     * region of the same dimension as this, is defined over a subset
     * of this.region and agrees with it at each point.
     */
    extern public (region(dimension) r)
        boolean subDistribution( distribution(r) D);

    /** Returns true iff this and d map each point in their common
     * domain to the same place.
     */
    public boolean equal( distribution( region ) d ) {
        return this.subDistribution(region)(d)
            && d.subDistribution(region)(this);
    }

    /** Returns the unique 1-dimensional distribution U over the region 1..k,
     * (where k is the cardinality of Q) which maps the point [i] to the
     * i'th element in Q in canonical place-order.
     */
    extern public static distribution(:dimension=1) unique( Set<place> Q );

    /** Returns the constant distribution which maps every point in its
        region to the given place P.
    */
    extern public static (region R) distribution(R) constant( place P );

    /** Returns the block distribution over the given region, and over
     * place.MAX_PLACES places.
     */
    public static (region R) distribution(R) block() {
        return this.block(R)(place.places);
    }

    /** Returns the block distribution over the given region and the
     * given set of places. Chunks of the region are distributed over
     * s, in canonical order.
     */
    extern public static (region R) distribution(R) block( Set<place> s);


    /** Returns the cyclic distribution over the given region, and over
     * all places.
     */
    public static (region R) distribution(R) cyclic() {
        return this.cyclic(R)(place.places);
    }

    extern public static (region R) distribution(R) cyclic( Set<place> s);

    /** Returns the block-cyclic distribution over the given region, and over
     * place.MAX_PLACES places. Exception thrown if blockSize < 1.
     */
    extern public static (region R)
        distribution(R) blockCyclic( int blockSize)
        throws MalformedRegionException;

    /** Returns a distribution which assigns a random place in the
     * given set of places to each point in the region.
     */
    extern public static (region R) distribution(R) random();

    /** Returns a distribution which assigns some arbitrary place in
     * the given set of places to each point in the region. There are
     * no guarantees on this assignment, e.g. all points may be
     * assigned to the same place.
     */
    extern public static (region R) distribution(R) arbitrary();

}

