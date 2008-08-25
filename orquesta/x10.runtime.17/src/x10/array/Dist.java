package x10.array;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import x10.runtime.Place;
import x10.runtime.Runtime;

public abstract class Dist {

    /*property*/ public final Region region;

    /**
     * The parameter rank may be used in constructing types derived
     * from the class distribution. For instance, distribution(rank=k)
     * is the type of all k-dimensional distributions.
     */
    /*property*/ public final /*nat*/ int rank;
    /**
     * Does the distribution map all points in its region to a single Place, onePlace?
     * If not, onePlace is null.
     */
    /*property*/ public final Place onePlace;
    /**
     * == region.rect
     */
    /*property*/ public final boolean rect;
    /**
     *  == region.zeroBased
     */
    /*property*/ public final boolean zeroBased;
    /**
     * Does this distribution map exactly one Point to each Place? (And is it asserted as doing so.)
     */
    /*property*/ public final boolean unique;
    /**
     * Does the distribution map all points in its region to a single Place?
     * Is true iff onePlace != null
     */
    /*property*/ public final boolean constant;
    /*property*/ public final boolean rail;

    /*
     * distribution is Indexable and as such regarded by the compiler as an X10array.
     * Hence it must have a field 'distrubution' (see ateach construct)
     */
    public final Dist dist;

    /**
     * places is the range of the distribution. Guranteed that if a
     * Place P is in this set then for some Point p in region,
     * this.valueAt(p)==P.
     */
    abstract public Set<Place> places(); // consider making this a parameter?

    public Place[] placesArray() {
            java.lang.Object[] a = places().toArray();
            Place[] res = new Place[a.length];
            java.lang.System.arraycopy(a,0,res,0,a.length);
            return res;
    }

    protected Dist(Region R, Place onePlace) {
            this(R, onePlace, false);

            //_indexMap = null;
    }

    protected Dist(Region R, Place onePlace, boolean unique) {
            this.region = R;
            this.rank = R.rank;
            this.dist = this;
            this.zeroBased = R.zeroBased;
            this.rect = R.rect;
            this.onePlace = onePlace;
            this.constant = onePlace != null;
            this.unique = unique;
            this.rail = rank==1&&zeroBased&&rect;
    }

    public static class MalformedError extends java.lang.RuntimeException {}

    public static final Dist UNIQUE = unique(Runtime.placeSet());
    

    /**
     * Returns the Place to which the Point p in region is mapped.
     */
    abstract public Place get(Point/*(region)*/ p) throws MalformedError;
    
    public Place get(int[] p) {
            return get(Point.makeFromJavaArray(p));
    }
    public Place get(int i) {
            return get(new int[] {i});
    }
    public Place get(int i, int j) {
            return get(new int[] {i, j});
    }
    public Place get(int i, int j, int k) {
            return get(new int[] {i, j, k});
    }
    public Place get(int i, int j, int k, int l) {
            return get(new int[] {i,j, k, l});
    }
    public Place get(int i, int j, int k, int l, int m) {
            return get(new int[] {i, j, k, l, m});
    }

    /**
     * Returns the region mapped by this distribution to the Place P.
     * The value returned is a subset of this.region.
     */
    abstract public Region/*(rank)*/ restrictToRegion(Place P);

    public Dist restriction(Place P) {
            return constant(restrictToRegion(P), P);
    }

    /**
     * This method is necessary because the compiler does not implement
     * the automatic conversions of distributions to regions is necessary
     * and hence a distribution is passed to the runtime where actually a
     * region is expected.
     */
    public Dist restriction(Dist P) {
            return restriction(P.region);
    }
    
    /**
     * Returns the distribution obtained by range-restricting this to Ps.
     * The region of the distribution returned is contained in this.region.
     */
    abstract public Dist/*(:this.region.contains(region))*/
    restriction(Set<Place> Ps);

    /**
     * Returns a new distribution obtained by restricting this to the
     * domain region.intersection(R), where parameter R is a region
     * with the same dimension.
     */
    abstract public
    /*(region(rank) R)*/ Dist/*(region.intersection(R))*/
    restriction(Region/*(rank)*/ R);

    /**
     * Returns the restriction of this to the domain region.difference(R),
     * where parameter R is a region with the same dimension.
     */
    abstract public
    /*(region(rank) R)*/ Dist/*(region.difference(R))*/
    difference(Region/*(rank)*/ R);

    /**
     * Returns the restriction of this to the domain region.difference(D.region),
     * where parameter D is a distribution with the same dimension.
     */
    public
    /*(region(rank) R)*/ Dist/*(region.difference(R))*/
    difference(Dist /*(rank)*/ D) {
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
    Dist/*(region.union(D.region))*/
    union(Dist/*(:region.disjoint(this.region) &&
    rank=this.rank)*/ D);

    abstract public Dist/*(:rank=this.rank)*/
    intersection(Dist /*(:rank=this.rank)*/D);

    /**
     * Returns a distribution defined on region.union(R): it
     * takes on D.get(p) for all points p in R, and this.get(p) for
     * all remaining points.
     */
    abstract public /*(region(rank) R)*/ Dist/*(region.union(R))*/
    overlay(Dist/*(R)*/ D);

    /**
     * Return true iff the given distribution D, which must be over a
     * region of the same rank as this, is defined over a subset
     * of this.region and agrees with it at each Point.
     */
    abstract public /*(region(rank) r)*/
    boolean subDistribution(Region/*(rank)*/ R, Dist/*(R)*/ D);

    public boolean contains(Point p) {
            return this.region.contains(p);
    }

    /**
     * Returns true iff this and d map each Point in their common
     * domain to the same Place.
     */
    public boolean equals(java.lang.Object o) {
            if (! (o instanceof Dist)) return false;
            Dist ot = (Dist) o;
            if (! (ot.region.equals(this.region))) return false;

            return this.subDistribution(region,ot)
                  && ot.subDistribution(region, this);
    }

    public Iterator<Point> iterator() {
            return region.iterator();
    }

    public Dist toDistribution() {
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
            // 1) Compute number of points per Place, and total number of points
            int totalPoints = 0;
            int pointCount[] = new int[Runtime.MAX_PLACES];
            for (Iterator it = iterator(); it.hasNext(); ) {
                    Point p = (Point) it.next();
                    Place pl = get(p);
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
    public Region region() {
            return region;
    }
    public Place onePlace() {
            return onePlace;
    }
    public boolean constant() { return constant; }
    public boolean unique() { return unique; }

    public static
    /*(region R)*/ Dist/*(R)*/ block(final Region base, final Region[] R) {
            final Dist/*(R)*/ result = block/*(R)*/(base, R, Runtime.placeSet());
            //assert result.region.equals(R);
            return result;
    }

    /**
     * Returns the cyclic distribution over the given region, and over
     * all places.
     */
    public static /*(region R)*/ Dist/*(R)*/ cyclic(final Region R) {
            /*final*/ Dist result = cyclic/*(R)*/(R, Runtime.placeSet());
            assert result.region.equals(R);
            return result;
    }

    /**
     * Create a Distribution where the elements in the Region_c are
     * distributed over all Places in p in a cyclic manner,
     * that is the next Point in the Region_c is at the next Place
     * for a cyclic ordering of the given places.
     * @param r
     * @return
     */
    public static Dist cyclic(Region r, Set<Place> qs) {
        assert r.rank > 0;
        final int dim_to_split = r.rank - 1;
        int sz = r.rank(dim_to_split).size();
        //if (sz % qs.size() != 0)
        //  throw new Error("DistributionFactory::cyclic - only supported if least significant dimension has a size that is a multiple of the number of places to cycle over");
        //else
        return blockCyclic(r, 1, qs);
    }

    public static Dist blockCyclic(Region r, /*nat*/int p) {
        return blockCyclic(r, p, Runtime.placeSet());
    }

    /**
     * Create a Distribution where the elements in the Region_c are
     * distributed over all Places in p in a cyclic manner,
     * that is the next Point in the Region_c is at the next Place
     * for a cyclic ordering of the given places.
     * @param r
     * @return
     */
    public static Dist blockCyclic(Region r, /*nat*/int n, Set<Place> qs) {
        Dist ret;
        assert n > 0;
        assert r.rank > 0;

        final boolean dim_splittable = r instanceof ContiguousRange || r instanceof MultiDimRegion;
        final int dim_to_split = r.rank - 1;
        int sz = r.rank(dim_to_split).size();
        int qsize = qs.size();
        assert (qsize > 0);

        //if (sz % (n * qsize) != 0)
        //  throw new Error("DistributionFactory::blockCyclic - only supported if least significant dimension has a size that is a multiple of the number of places to cycle over");
        Place[] q = qs.toArray(new Place[qs.size()]);
        // actually qs should be a sorted set of places - here we sort the in global order 
        assert (q[0] instanceof Comparable);

        Arrays.sort(q);
        if (qsize == 1) {
            Place p = (Place) q[0];
            ret = new Distribution_c.Constant(r, p);
        }
        else if (sz % (n * qsize) == 0 && dim_splittable) {
            // partitioning done along dimension dim_to_split
            int chunks = (sz % n == 0 || sz < n) ? (sz / n) : ((sz / n) + 1);
            assert chunks > 0;
            Distribution_c[] dists = new Distribution_c[chunks];
            Region[] sub = r.partition(chunks, dim_to_split);
            for (int i = 0; i < chunks; i++) {
                dists[i] = new Distribution_c.Constant(sub[i], (Place) q[i % q.length]);
            }
            ret = new Distribution_c.Combined(r, dists, q.length == 1 ? (Place) q[0] : null);

        }
        else if (sz == n && dim_to_split > 0 && qsize == r.rank(dim_to_split - 1).size()) {
            // blocking entire rows
            // currently only support it if virtual mapping function is a simple offset ie
            // number of rows must equal number of places

            int adjustment = 0;
            // FIXME n should be == num of places, but if x10c config differs from x10
            // then it won't be
            int adjustmentOffset[] = new int[Runtime.places().length];
            int chunks = r.rank(dim_to_split - 1).size();
            ;
            Distribution_c[] dists = new Distribution_c[chunks];
            Region[] sub = r.partition(chunks, dim_to_split - 1);//split along rows
            for (int i = 0; i < chunks; ++i) {
                int placeId = ((Place) q[i % chunks]).id;
                adjustmentOffset[placeId] = adjustment;

                //System.err.println("set adjustment to:"+adjustment);
                adjustment += sub[i].size();
                dists[i] = new Distribution_c.Constant(sub[i], (Place) q[i % chunks]);
            }
            ret = new Distribution_c.Combined(r, dists, q.length == 1 ? (Place) q[0] : null);

        }
        else {
            ret = blockCyclicHelper_(r, n, q);
        }

        return ret;
    }

    private static Distribution_c.Arbitrary blockCyclicHelper_(Region r, int bf, Place[] places) {
        assert bf > 0;
        HashMap<Point, Place> hm = new HashMap<Point, Place>();
        int offset = 0;
        for (Iterator<Point> it = r.iterator(); it.hasNext();) {
            Point p = (Point) it.next();
            hm.put(p, places[(offset / bf) % places.length]);
            offset++;
        }
        Distribution_c.Arbitrary ret = new Distribution_c.Arbitrary(r, hm, places.length == 1 ? (Place) places[0] : null);

        return ret;
    }

    public static Dist random(Region r) {
        return cyclic(r);
    }
    
    public static Dist local(Region R) {
        return constant(R, Runtime.here());
    }
    
    /**
     * Return the distribution that maps the region 0..k-1 to here. 
     *
     * @param k -- the upper bound of the 1-dimensional region. k >= 0
     * @return the given distribution
     */
    public static Dist/*(:rank=1)*/ local(int k) {
            return local(Region.region(0, k-1));
    }

    /**
     * Create a Distribution where the given Region is distributed
     * into blocks the specified places
     * @param r Region
     * @param q The set of Places
     * @return
     */
    public static Dist block(Region r, Set<Place> q) {
        return block(r, q.size(), q);
    }

    /**
     * Create a Distribution where the given tiled region r is distributed
     * into blocks over the given q places. 
     * For now we require the programmer to provide the base region, base,
     * which is intended to be split into r as well. It is the 
     * programmer's responsibility to ensure that r partitions base.
     * @param r
     * @return
     */
    public static Dist block(Region base, Region[] r, Set<Place> q) {
        return block(base, r, q.size(), q);
    }

    public static Dist block(Region base, Region[] r, int n, Set<Place> qs) {
        assert (n > 0);
        if (r.length != n)
            throw new Error("Not implemented yet.");
        Distribution_c[] dists = new Distribution_c[n];
        Object[] q = qs.toArray();

        for (int i = 0; i < n; i++) {
            dists[i] = new Distribution_c.Constant(r[i], (Place) q[i]);
        }
        return new Distribution_c.Combined(base, dists, null);
    }

    /**
     * Create a Distribution where the given Region is distributed
     * into blocks over the first n Places.
     * @param r
     * @return
     */
    public static Dist block(Region r, int n, Set<Place> qs) {
        assert n <= qs.size();
        assert n > 0;

        final boolean dim_splittable = r instanceof ContiguousRange || r instanceof MultiDimRegion;
        final int dim_to_split = 0; //r.rank - 1;
        Dist ret = null;
        int sz = r.rank(dim_to_split).size();

        //if (sz < n)
        // throw new Error("DistributionFactory::block - blocking only supported along the most significant dimension and blocking factor must be lower than or equal to the size of that dimension.");

        Place[] q = qs.toArray(new Place[qs.size()]);
        //      actually qs should be a sorted set of places - here we sort the in global order 
        assert (q[0] instanceof Comparable);

        Arrays.sort(q);
        if (n == 1) {
            Place p = (Place) q[0];
            ret = new Distribution_c.Constant(r, p);
        }
        else if (sz >= n && sz % n == 0 && dim_splittable) {
            // partition along dimension dim_to_split           
            Region sub[] = r.partition(n, dim_to_split);

            Distribution_c[] dists = new Distribution_c[n];
            int adjustment = 0;
            // FIXME n should be == num of places, but if x10c config differs from x10
            // then it won't be
            int adjustmentOffset[] = new int[Runtime.places().length];
            for (int i = 0; i < n; i++) {
                int placeId = ((Place) q[i]).id;
                adjustmentOffset[placeId] = adjustment;

                //System.err.println("set adjustment to:"+adjustment);
                adjustment += sub[i].size();
                dists[i] = new Distribution_c.Constant(sub[i], (Place) q[i]);
            }
            ret = new Distribution_c.Combined(r, dists, n == 1 ? (Place) q[0] : null);

        }
        else {

            ret = blockHelper_(r, n, q);
        }

        return ret;
    }

    private static Distribution_c.Arbitrary blockHelper_(Region r, int nb, Place[] places) {
        assert nb > 0 && nb <= places.length;
        int total_points = r.size();
        int p = total_points / nb;
        int q = total_points % nb;

        int adjustment = 0;
        int adjustmentOffset[] = new int[Runtime.places().length];

        HashMap hm = new HashMap();
        int offsWithinPlace = 0;
        int blockNum = 0;
        for (Iterator it = r.iterator(); it.hasNext();) {
            Point pt = (Point) it.next();
            hm.put(pt, places[blockNum]);
            offsWithinPlace++;
            ++adjustment;
            if (offsWithinPlace == (p + ((blockNum < q) ? 1 : 0))) {
                offsWithinPlace = 0; // start next block
                blockNum++;
                if (blockNum < places.length)
                    adjustmentOffset[blockNum] = adjustment;
            }
        }
        Distribution_c.Arbitrary ret = new Distribution_c.Arbitrary(r, hm, places.length == 1 ? (Place) places[0] : null);

        return ret;
    }

    /**
     * Create a Distribution where the points of the Region are
     * distributed randomly over all available Places.
     * @param r
     * @return
     */
    public static Dist arbitrary(Region r) {
        Dist ret;
        int blocksize = r.size() / x10.runtime.Runtime.places().length;
        if (blocksize == 0)
            ret = constant(r, x10.runtime.Runtime.places()[0]);
        else
            ret = blockCyclic(r, blocksize, x10.runtime.Runtime.placeSet());
        return ret;
    }

    /**
     * Create a Distribution where all points in the given
     * Region are mapped to the same Place.
     * @param r
     * @param p specifically use the given Place for all points
     * @return
     */
    public static Dist constant(Region r, Place p) {
        // initialized to zero
        int adjustmentOffset[] = new int[Runtime.places().length];

        Dist newDist = new Distribution_c.Constant(r, p);
        return newDist;
    }

    /**
     * Create a Distribution where the points in the
     * Region_c 1...p.length are mapped to the respective
     * places.
     * @param p the list of places (implicitly defines the Region_c)
     * @return
     */
    public static Dist unique(Set<Place> p) {
        Object[] places = p.toArray();
        Place[] ps = new Place[places.length];
        for (int i = 0; i < places.length; i++)
            ps[i] = (Place) places[i];

        Dist newDist = new Distribution_c.Unique(ps);
        return newDist;
    }

    public static Dist unique(Region R) {
        Dist newDist = new KDimUnique(R);
        return newDist;
    }

}
