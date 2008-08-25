/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Oct 3, 2004
 */
package x10.array;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import x10.runtime.Place;
import x10.runtime.Runtime;

/**
 * Implementation of Distributions. vj: Changed class to extend
 * x10.runtime.Dist, not Region_c. vj: Added factory class to separate out
 * factory methods.
 * 
 * @author Christoph von Praun
 * @author Christian Grothoff
 * @author vj
 */
public abstract class Distribution_c extends Dist /* implements Distribution */{

    private int _blockSize;
    private int _cycleSize;

    public boolean isValue() {
        return true;
    }

    /* this field should actually be final - ?? */
    protected final Set<Place> places;

    public Set<Place> places() {
        return places;
    }

    public Distribution_c(Region r, Place onePlace) {
        this(r, onePlace, false);
    }

    protected Distribution_c(Region r, Place onePlace, boolean unique) {
        super(r, onePlace, unique);
        this.places = new HashSet();
    }

    public Region restrictToRegion(Place pl) {
        return restriction(this, pl);
    }

    /**
     * Returns the region mapped by this distribution to the Place P. The value
     * returned is a subset of this.region.
     */
    protected static Region/* (rank) */restriction(Dist th, Place pl) {

        if (th instanceof Combined)
            return ((Combined) th).restrictToRegion(pl);

        if (th instanceof Constant)
            return ((Constant) th).restrictToRegion(pl);

        if (th instanceof Unique)
            return ((Unique) th).restrictToRegion(pl);

        Set points = new HashSet();
        boolean zeroBased = false;
        Point allZero = Point.makeZero(th.rank);
        for (Iterator<Point> it = th.region.iterator(); it.hasNext();) {
            Point p = (Point) it.next();
            if (th.get(p) == pl) {
                points.add(p);
                zeroBased |= p.equals(allZero);
            }
        }
        Region ret = new ArbitraryRegion(th.region.rank, points);
        return ret;
    }

    public Dist restriction(Set<Place> Ps) {
        return restriction(this, Ps);
    }

    /**
     * Returns the distribution obtained by range-restricting this to Ps. The
     * region of the distribution returned is contained in this.region.
     */
    protected static Dist/* (:this.region.contains(region)) */
    restriction(Dist th, Set<Place> Ps) {
        HashMap hm = new HashMap();
        Set points = new HashSet();
        boolean zeroBased = false;
        Point allZero = Point.makeZero(th.rank);
        for (Iterator<Point> it = th.region.iterator(); it.hasNext();) {
            Point p = (Point) it.next();
            Place pl = th.get(p);
            if (Ps.contains(pl)) {
                points.add(p);
                hm.put(p, pl);
                zeroBased |= p.equals(allZero);
            }
        }
        Region reg = new ArbitraryRegion(th.rank, points);
        Place onePlace = Ps.size() == 1 ? (Place) Ps.toArray()[0] : null;
        Dist ret = new Arbitrary(reg, hm, onePlace);
        return ret;
    }

    public Dist restriction(Region r) {
        return restriction(this, r);
    }

    /**
     * Returns a new distribution obtained by restricting this to the domain
     * region.intersection(R), where parameter R is a region with the same
     * dimension.
     */
    protected static/* (Region(rank) R) */Dist/* (region.intersection(R)) */
    restriction(Dist th, Region/* (rank) */r) {
        if (r.rank != th.rank)
            throw new RankMismatchException(r, th.rank);

        HashMap hm = new HashMap();
        Set points = new HashSet();
        boolean isSet = false;
        Place onePlace = null;
        for (Iterator it = th.region.iterator(); it.hasNext();) {
            Point p = (Point) it.next();
            if (r.contains(p)) {
                Place Place = (Place) th.get(p);
                onePlace = isSet ? (onePlace == null ? null : (onePlace.equals(Place) ? onePlace : null)) : Place;
                isSet = true;
                points.add(p);
                hm.put(p, th.get(p));

            }
        }
        Region reg = new ArbitraryRegion(th.rank, points);
        Dist ret = new Arbitrary(reg, hm, onePlace);
        return ret;
    }

    public Dist difference(Region r) {
        return difference(this, r);
    }

    /**
     * Returns the restriction of this with the domain region.difference(R),
     * where parameter R is a region with the same dimension.
     */
    protected static/* (Region(rank) R) */Dist/* (region.difference(R)) */
    difference(Dist th, Region/* (rank) */r) {
        if (r.rank != th.rank)
            throw new RankMismatchException(r, th.rank);
        Region reg = th.region.difference(r);
        HashMap hm = new HashMap();
        boolean isSet = false;
        Place onePlace = null;
        for (Iterator it = reg.iterator(); it.hasNext();) {
            Point p = (Point) it.next();
            Place Place = th.get(p);
            if (isSet) {
                if (onePlace != null)
                    onePlace = onePlace.equals(Place) ? onePlace : null;
            }
            else {
                onePlace = Place;
            }

            isSet = true;
            hm.put(p, th.get(p));
        }
        Dist ret = new Arbitrary(reg, hm, onePlace);
        return ret;
    }

    public Dist union(Dist d) {
        return union(this, d);
    }

    /**
     * Takes as parameter a distribution D defined over a region disjoint from
     * this. Returns a distribution defined over a region which is the union of
     * this.region and D.region. This distribution must assume the value of D
     * over D.region and this over this.region.
     * 
     * @see distribution.asymmetricUnion.
     */
    protected static/*
                     * (distribution(:region.disjoint(this.region) &&
                     * rank=this.rank) D)
                     */
    Dist/* (region.union(D.region)) */
    union(Dist th, Dist/* (:region.disjoint(this.region) && rank=this.rank) */d) {
        if (d.rank != th.rank)
            throw new RankMismatchException(d, th.rank);
        if (!d.region.disjoint(th.region))
            throw new IllegalArgumentException("Non-disjoint region in a disjoint union");

        Region reg = d.region.union(th.region);
        HashMap hm = new HashMap();
        for (Iterator it = th.region.iterator(); it.hasNext();) {
            Point p = (Point) it.next();
            hm.put(p, th.get(p));
        }
        for (Iterator it = d.region.iterator(); it.hasNext();) {
            Point p = (Point) it.next();
            hm.put(p, d.get(p));
        }
        Dist ret = new Arbitrary(reg, hm, th.onePlace == null ? null : (th.onePlace.equals(d.onePlace) ? th.onePlace : null));
        return ret;

    }

    public Dist intersection(Dist D) {
        return intersection(this, D);
    }

    public Dist overlay(Region r, Dist d) {
        return overlay(this, r, d);
    }

    public Dist overlay(Dist d) {
        return overlay(this, d.region, d);
    }

    /**
     * Returns a distribution defined on region.union(R): it takes on
     * this.get(p) for all points p in region, and D.get(p) for all points in
     * R.difference(region).
     */
    protected static/* (Region(rank) R) */Dist/* (region.union(R)) */
    overlay(Dist th, Region/* (rank) */r, Dist/* (R) */d) {
        if (d.rank != th.rank)
            throw new RankMismatchException(d, th.rank);
        if (r.rank != th.rank)
            throw new RankMismatchException(r, th.rank);

        Region reg = r.union(th.region);
        HashMap hm = new HashMap();
        boolean isSet = false;
        Place onePlace = null;
        for (Iterator it = reg.iterator(); it.hasNext();) {
            Point p = (Point) it.next();
            Place pl;
            if (d.region.contains(p)) {
                pl = d.get(p);
                assert (pl != null);
            }
            else {
                pl = th.get(p);
                assert (pl != null);
            }
            onePlace = isSet ? (pl.equals(onePlace) ? onePlace : null) : pl;
            isSet = true;
            hm.put(p, pl);
        }
        // TODO: *must* try to convert this to a recognized form (constant?
        // Unique?)
        // otherwise serialization will have to send the entire hashtable over
        Dist ret = new Arbitrary(reg, hm, onePlace);
        return ret;
    }

    /**
     * Returns a the intersection of distributions th and d. The result contains
     * all those points that are contained in the intersection of th.region and
     * d.region that are mapped to the *same Place* in both distributions.
     */
    protected static/* (Region(rank) R) */Dist/* (region.union(R)) */
    intersection(Dist th, Dist/* (R) */d) {
        if (d.rank != th.rank)
            throw new RankMismatchException(d, th.rank);

        Region reg = th.region.intersection(d.region);
        HashMap hm = new HashMap();
        Set points = new HashSet();
        boolean isSet = false;
        Place onePlace = null;
        for (Iterator it = reg.iterator(); it.hasNext();) {
            Point p = (Point) it.next();
            Place pl;
            if ((pl = th.get(p)).equals(d.get(p))) {
                onePlace = isSet ? (pl.equals(onePlace) ? onePlace : null) : pl;
                hm.put(p, pl);
                points.add(p);

            }
        }
        Region reg_new = new ArbitraryRegion(th.rank, points);
        Dist ret = new Arbitrary(reg_new, hm, onePlace);
        return ret;
    }

    public boolean subDistribution(Region r, Dist d) {
        return subDistribution(this, r, d);
    }

    /**
     * Return true iff the given distribution D, which must be over a region of
     * the same rank as this, is defined over a subset of this.region and agrees
     * with it at each Point.
     */
    protected/* (Region(rank) r) */static boolean subDistribution(Dist th, Region/* (rank) */r, Dist/* (R) */d) {
        if (d.rank != th.rank)
            throw new RankMismatchException(d, th.rank);
        boolean ret = false;
        if (th.region.contains(d.region)) {
            ret = true;
            for (Iterator it = d.region.iterator(); it.hasNext();) {
                Point p = (Point) it.next();
                Place p1 = th.get(p);
                Place p2 = d.get(p);
                if (!p1.equals(p2)) {
                    ret = false;
                    break;
                }
            }
        }
        return ret;
    }

    static final class Empty extends Distribution_c {

        Empty() {
            this(1);
        }

        /**
         * The empty region of rank k
         * 
         * @param k
         */
        Empty(/* nat */int k) {
            // get an empty region of rank 0
            super(RegionFactory.makeEmpty(k), null);
        }

        /**
         * Returns the Place to which the Point p in region is mapped.
         */
        public Place get(Point/* (region) */p) {
            throw new ArrayIndexOutOfBoundsException();
        }

        public Place get(int[] p) {
            throw new ArrayIndexOutOfBoundsException();
        }

        /**
         * Returns the region mapped by this distribution to the Place P. The
         * value returned is a subset of this.region.
         */
        public Region/* (rank) */restrictToRegion(Place P) {
            return this.region;
        }

        /**
         * Returns the distribution obtained by range-restricting this to Ps.
         * The region of the distribution returned is contained in this.region.
         */
        public Dist/* (:this.region.contains(region)) */
        restriction(Set/* <Place> */Ps) {
            return this;
        }

        /**
         * Returns a new distribution obtained by restricting this to the domain
         * region.intersection(R), where parameter R is a region with the same
         * dimension.
         */
        public/* (Region(rank) R) */Dist/* (region.intersection(R)) */
        restriction(Region/* (rank) */R) {
            if (R.rank != rank)
                throw new RankMismatchException(R, rank);
            return this;
        }

        /**
         * Returns the restriction of this to the domain region.difference(R),
         * where parameter R is a region with the same dimension.
         */
        public/* (region(rank) R) */Dist/* (region.difference(R)) */
        difference(Region/* (rank) */R) {
            if (R.rank != rank)
                throw new RankMismatchException(R, rank);
            return this;
        }

        /**
         * Takes as parameter a distribution D defined over a region disjoint
         * from this. Returns a distribution defined over a region which is the
         * union of this.region and D.region. This distribution must assume the
         * value of D over D.region and this over this.region.
         * 
         * @see distribution.asymmetricUnion.
         */
        public/*
                 * (distribution(:region.disjoint(this.region) &&
                 * rank=this.rank) D)
                 */
        Dist/* (region.union(D.region)) */
        union(Dist D) {
            if (D.rank != rank)
                throw new RankMismatchException(D, rank);
            return D;
        }

        /**
         * Return true iff the given distribution D, which must be over a region
         * of the same rank as this, is defined over a subset of this.region and
         * agrees with it at each Point.
         */
        public/* (Region(rank) r) */boolean subDistribution(Region/* (rank) */R, Dist/* (R) */D) {
            if (D.rank != rank)
                throw new RankMismatchException(D, rank);
            return (D instanceof Empty || D.region.size() == 0);
        }

        public String toString() {
            StringBuffer s = new StringBuffer("Distribution_c.Empty<");
            s.append(region.toString());
            s.append("|>");
            return s.toString();
        }
    } // end of Distribution_c.Empty

    static final class Constant extends Distribution_c {
        Place place_;

        Constant(Region r, Place p) {
            super(r, p);
            this.places.add(p);
            place_ = p;
        }

        /*
         * (non-Javadoc)
         * 
         * @see x10.runtime.Dist#getPerPlaceRegions()
         */
        public final Region[] getPerPlaceRegions() {
            Region theRegions[] = new Region[Runtime.MAX_PLACES];
            theRegions[place_.id] = this.region;

            return theRegions;
        }

        /**
         * Returns the Place to which the Point p in region is mapped.
         */
        public Place get(Point/* (region) */p) {
            if (p.rank != rank)
                throw new RankMismatchException(p, rank);
            if (!region.contains(p))
                throw new ArrayIndexOutOfBoundsException();
            return place_;
        }

        /**
         * Returns the region mapped by this distribution to the Place P. The
         * value returned is a subset of this.region.
         */
        public Region/* (rank) */restrictToRegion(Place P) {
            if (P.equals(place_))
                return this.region;

            return RegionFactory.makeEmpty(this.rank);
        }

        /**
         * Returns the distribution obtained by range-restricting this to Ps.
         * The region of the distribution returned is contained in this.region.
         */
        public Dist/* (:this.region.contains(region)) */
        restriction(Set/* <Place> */Ps) {
            if (Ps.contains(place_))
                return this;
            return new Empty(this.rank);
        }

        /**
         * Returns a new distribution obtained by restricting this to the domain
         * region.intersection(R), where parameter R is a region with the same
         * dimension.
         */
        public/* (Region(rank) R) */Dist/* (region.intersection(R)) */
        restriction(Region/* (rank) */R) {
            if (R.rank != rank)
                throw new RankMismatchException(R, rank);
            Region r = this.region.intersection(R);
            Dist ret;
            if (r.size() == 0)
                ret = new Empty(rank);
            else
                ret = new Constant(r, place_);
            // assert result.region == R.intersection(this.region);
            return ret;
        }

        /**
         * Returns the restriction of this to the domain region.difference(R),
         * where parameter R is a region with the same dimension.
         */
        public/* (Region(rank) R) */Dist/* (region.difference(R)) */
        difference(Region/* (rank) */R) {
            if (R.rank != rank)
                throw new RankMismatchException(R, rank);
            Region r = this.region.difference(R);
            Dist ret;
            if (r.size() == 0)
                ret = new Empty(rank);
            else
                ret = new Constant(r, place_);
            return ret;
        }

        /**
         * Takes as parameter a distribution D defined over a region disjoint
         * from this. Returns a distribution defined over a region which is the
         * union of this.region and D.region. This distribution must assume the
         * value of D over D.region and this over this.region.
         * 
         * @see distribution.asymmetricUnion.
         */
        public/*
                 * (distribution(:region.disjoint(this.region) &&
                 * rank=this.rank) D)
                 */
        Dist/* (region.union(D.region)) */
        union(Dist/* (:region.disjoint(this.region) && rank=this.rank) */D) {
            if (D.rank != rank)
                throw new RankMismatchException(D, rank);
            assert D.region.disjoint(region); // assume
            assert D instanceof Distribution_c;
            Dist ret;
            if (D.region.size() == 0)
                ret = this;
            else {
                Distribution_c arr[] = new Distribution_c[2];
                arr[0] = this;
                arr[1] = (Distribution_c) D;
                Region r = region.union(D.region);
                ret = new Distribution_c.Combined(r, arr, arr[0].onePlace == arr[1].onePlace ? arr[0].onePlace : null);
            }
            return ret;
        }

        public String toString() {
            StringBuffer s = new StringBuffer("Distribution_c.Constant<region=|");
            s.append(region.toString());
            s.append("|, Place=|");
            s.append(place_);
            s.append("|");
            s.append(">");
            return s.toString();
        }
    } // end of Distribution_c.Constant

    static class Unique extends Distribution_c {

        Place[] placeseq;

        Unique(Place[] ps) {
            super(new ContiguousRange(0, ps.length - 1), ps.length == 1 ? ps[0] : null, ps.length == Runtime.MAX_PLACES);
            this.placeseq = ps;
            for (int i = 0; i < placeseq.length; i++)
                places.add(ps[i]);
        }

        /**
         * Returns the Place to which the Point p in region is mapped.
         */
        public Place get(Point/* (region) */p) {
            if (p.rank != rank)
                throw new RankMismatchException(p, rank);
            if (!region.contains(p))
                throw new ArrayIndexOutOfBoundsException();
            return placeseq[(p.get(0)) % placeseq.length];
        }

        public Place get(int[] val) {
            assert val.length == 1;
            if (val[0] >= placeseq.length || val[0] < 0)
                throw new ArrayIndexOutOfBoundsException();
            return placeseq[val[0]];
        }

        /**
         * Returns the region mapped by this distribution to the Place P. The
         * value returned is a subset of this.region.
         */
        public Region/* (rank) */restrictToRegion(Place P) {
            int index = -1;
            for (int i = 0; i < placeseq.length; ++i) {
                if (placeseq[i] == P) {
                    index = i;
                    break;
                }
            }
            if (index < 0)
                return RegionFactory.makeEmpty(1);
            return RegionFactory.makeRect(index, index);
        }

        public String toString() {
            StringBuffer s = new StringBuffer("Distribution_c.Unique<");
            for (int i = 0; i < placeseq.length; i++)
                s.append(placeseq[i].toString());
            return s.append(">").toString();
        }
    } // end of Distribution_c.Unique

    static class Combined extends Distribution_c {

        private final Distribution_c[] members_;

        /**
         * @param r
         */
        // [IP] FIXME: should this verify that all members have the same rank?
        Combined(Region r, Distribution_c[] members_, Place onePlace) {
            super(r, onePlace);
            assert members_ != null;
            // defensive copy
            this.members_ = (Distribution_c[]) members_.clone();
            for (int i = 0; i < members_.length; i++) {
                places.addAll(members_[i].places());
            }
        }

        /* (non-Javadoc)
         * @see x10.runtime.distribution#get(x10.runtime.Point)
         */
        public Place get(Point p) {
            if (p.rank != rank)
                throw new RankMismatchException(p, rank);
            Place ret = null;
            for (int i = 0; ret == null && i < members_.length; ++i) {
                if (members_[i].region.contains(p)) {
                    ret = members_[i].get(p);
                    assert ret != null;
                }
            }
            if (ret == null)
                throw new ArrayIndexOutOfBoundsException();
            return ret;
        }

        public Place get(int[] p) {
            Place ret = null;
            for (int i = 0; ret == null && i < members_.length; ++i) {
                if (members_[i].region.contains(p))
                    ret = members_[i].get(p);
            }
            if (ret == null)
                throw new ArrayIndexOutOfBoundsException();
            return ret;
        }

        public Region/*(rank)*/restrictToRegion(Place pl) {

            Region[] newRegions = new Region[members_.length];
            int count = 0;
            int i;
            for (i = 0; i < members_.length; ++i) {
                Region r = restriction(members_[i], pl);
                if (r instanceof EmptyRegion)
                    continue;
                newRegions[count++] = r;
            }

            if (0 == count)
                return RegionFactory.makeEmpty(region.rank);

            if (1 == count)
                return newRegions[0]; // we win

            // now must merge results--may or may not be as cheap as
            // a Distribution_c.restiction
            Set points = new HashSet();
            boolean zeroBased = false;
            Point allZero = Point.makeZero(rank);
            for (i = 0; i < count; ++i) {
                for (Iterator it = newRegions[i].iterator(); it.hasNext();) {
                    Point p = (Point) it.next();
                    points.add(p);
                    zeroBased |= p.equals(allZero);
                }
            }

            Region ret = new ArbitraryRegion(region.rank, points);
            return ret;
        }

        public String toString() {
            StringBuffer s = new StringBuffer("CombinedDistribution_c<");
            for (int i = 0; i < members_.length; i++)
                s.append(members_[i]);
            return s.append(">").toString();
        }
    } // end of Distribution_c.Combined

    static final class Arbitrary extends Distribution_c {

        private final Map map_;

        Arbitrary(Region r, Map m, Place onePlace) {
            super(r, onePlace);
            map_ = m;
            places.addAll(m.values());
            //	this._indexMap = generateIndexMap(this, m);
            //		this._distributionType = ARBITRARY;
        }

        /**
         * Returns the Place to which the Point p in region is mapped.
         */
        // [IP] FIXME: should we throw a RankMismatchException here?
        public Place get(Point/*(region)*/p) {
            assert p != null;
            Place ret = (Place) map_.get(p);
            if (ret == null)
                throw new ArrayIndexOutOfBoundsException();
            return ret;
        }

        public String toString() {
            StringBuffer s = new StringBuffer("Distribution_c.Arbitrary<\n");
            for (Iterator it = map_.entrySet().iterator(); it.hasNext();) {
                Map.Entry me = (Map.Entry) it.next();
                Point p = (Point) me.getKey();
                Place pl = (Place) me.getValue();
                s.append("[" + p + ", " + pl + "]");
                if (it.hasNext())
                    s.append(",\n");
            }
            s.append(">");
            return s.toString();
        }
    } // end of Distribution_c.Arbitrary
}
