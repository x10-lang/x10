package x10.array;

import java.util.Iterator;
import java.util.Set;

import x10.runtime.Place;
import x10.runtime.Runtime;

public abstract class Dist implements Iterable<Point> {

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
            return DistFactory.makeConstant(restrictToRegion(P), P);
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


}
