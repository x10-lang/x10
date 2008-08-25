/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Jan 13, 2005
 */
package x10.array;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Christoph von Praun
 */
public class ArbitraryRegion extends Region {

    private final SortedSet<Point> points_;

    public ArbitraryRegion(Region[] dims) {
        super(dims.length, false, false);
        int sz = dims[0].size();
        int sz_total = sz;
        for (int i = 1; i < dims.length; ++i) {
            assert dims[i].size() == sz;
            if (dims[i].rank != 1)
                throw new RankMismatchException(dims[i], 1);
            if (zeroBased)
                assert dims[i].zeroBased;
        }
        rankCache_ = new ArbitraryRegion[dims.length];
        points_ = new TreeSet();
        permutations_(points_, new int[] {}, dims);
    }

    /* create all points in the region that is spawned by the dimensions in var */
    private void permutations_(Set<Point> hs, int[] fix, Region[] var) {
        if (var.length == 0) {
            Point new_point = Point.makeFromJavaArray(fix);
            hs.add(new_point);
        }
        else {
            Region[] var_new = new Region[var.length - 1];
            System.arraycopy(var, 1, var_new, 0, var.length - 1);
            Region cur_reg = var[0];
            int[] fix_new = new int[fix.length + 1];
            System.arraycopy(fix, 0, fix_new, 0, fix.length);
            for (Iterator<Point> it = cur_reg.iterator(); it.hasNext();) {
                Point tmp = it.next();
                int tmp_i = tmp.get(0);
                fix_new[fix.length] = tmp_i;
                permutations_(hs, fix_new, var_new);
            }
        }
    }

    /**
     * Create a new ArbitraryRegion of the given rank, with the given set of
     * points. Assume that if zeroBased is true then points contains the
     * all-zero point of rank rank.
     * 
     * @param rank
     * @param points
     * @param zeroBased
     */
    public ArbitraryRegion(int rank, Set<Point> points) {
        super(rank, false, false);
        assert (points != null);

        for (Iterator<Point> it = points.iterator(); it.hasNext();) {
            Point p = it.next();
            if (p.rank != rank)
                throw new RankMismatchException(p, rank);
        }
        rankCache_ = new ArbitraryRegion[rank];
        points_ = new TreeSet<Point>();
        points_.addAll(points);

    }

    /*
     * modifies this region - this method should be only called during the
     * initialization or creation of an arbitrary region - hence it is private.
     */
    private void add_(Point p) {
        if (p.rank != rank)
            throw new RankMismatchException(p, rank);
        points_.add(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see x10.runtime.region#size()
     */
    public int size() {
        return points_.size();
    }

    private final ArbitraryRegion[] rankCache_;

    /**
     * @return a one-dimensional region (formerly called range)
     */
    public Region rank(int index) {
        // System.err.println("ArbitraryRegion vj index=" + index + "rank = " +
        // rank);
        assert index >= 0;
        assert index < rank;
        ArbitraryRegion ret;

        synchronized (this) {
            ret = rankCache_[index];
        }
        if (ret == null) {
            TreeSet points = new TreeSet();

            for (Iterator<Point> it = iterator(); it.hasNext();) {
                Point p = it.next();
                Point p_onedim = Point.makeFromVarArgs(p.get(index));
                points.add(p_onedim);
            }
            // add it to rankCache_ only after it is initialized
            // race on rankCache possible_!
            synchronized (this) {
                rankCache_[index] = ret;
            }
            ret = new ArbitraryRegion(1, points);
        }
        return ret;
    }

    public boolean isConvex() {
        boolean ret = true;
        if (rank == 1) {
            ret = (high() - low() + 1 == size());
        }
        else {
            int conv_size = 1;
            for (int i = 0; ret && i < rank; ++i) {
                Region r = rank(i);
                ret &= r.isConvex();
                conv_size *= r.size();
            }
            ret &= size() == conv_size;
        }
        return ret;
    }

    /*
     * method only menaingful for regions of one dimension (aka ranges)
     */
    public int low() {
        if (rank != 1)
            throw new UnsupportedOperationException();
        if (points_.isEmpty()) {
            // this should never happen, beause an empty region
            // should have been constructed instead
            assert (false);
            throw new ArrayIndexOutOfBoundsException();
        }

        Point p = (Point) points_.first();
        return p.get(0);
    }

    /*
     * method only meaningful for regions of one dimension (aka ranges)
     */
    public int high() {
        if (rank != 1)
            throw new UnsupportedOperationException();
        if (points_.isEmpty())
            throw new ArrayIndexOutOfBoundsException();

        Point p = (Point) points_.last();
        return p.get(0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see x10.runtime.region#union(x10.runtime.region)
     */
    public Region union(Region r) {
        assert r != null;
        // union() will check the rank
        return union(this, r);
    }

    protected static Region union(Region r1, Region r2) {
        if (r1.rank != r2.rank)
            throw new RankMismatchException(r2, r1.rank);

        TreeSet<Point> points = new TreeSet<Point>();
        for (Iterator<Point> it = r1.iterator(); it.hasNext();) {
            Point p = (Point) it.next();
            points.add(p);
        }
        for (Iterator<Point> it = r2.iterator(); it.hasNext();) {
            Point p = (Point) it.next();
            points.add(p);
        }
        return new ArbitraryRegion(r1.rank, points);
    }

    /*
     */
    public Region intersection(Region r) {
        assert r != null;
        // intersection() will check the rank
        return intersection(this, r);
    }

    protected static Region intersection(Region r1, Region r2) {
        if (r1.rank != r2.rank)
            throw new RankMismatchException(r2, r1.rank);
        if (r1.size() > r2.size()) {
            // swap for efficiency
            Region tmp = r2;
            r2 = r1;
            r1 = tmp;
        }

        TreeSet<Point> points = new TreeSet<Point>();
        for (Iterator<Point> it = r1.iterator(); it.hasNext();) {
            Point p = (Point) it.next();
            if (r2.contains(p))
                points.add(p);
        }
        return new ArbitraryRegion(r1.rank, points);
    }

    /*
     * (non-Javadoc)
     * 
     * @see x10.runtime.region#difference(x10.runtime.region)
     */
    public Region difference(Region r) {
        assert r != null;
        // difference() will check the rank
        return difference(this, r);
    }

    protected static Region difference(Region r1, Region r2) {
        if (r1.rank != r2.rank)
            throw new RankMismatchException(r2, r1.rank);

        TreeSet<Point> points = new TreeSet<Point>();
        for (Iterator<Point> it = r1.iterator(); it.hasNext();) {
            Point p = (Point) it.next();
            if (!r2.contains(p))
                points.add(p);
        }
        ArbitraryRegion ret = new ArbitraryRegion(r1.rank, points);
        return ret;
    }

    public Region convexHull() {
        Region ret;
        if (rank == 1) {
            ret = new ContiguousRange(low(), high());
        }
        else {
            int[] mins = new int[rank];
            int[] maxs = new int[rank];
            for (int i = 0; i < rank; ++i) {
                Region r = rank(i);
                mins[i] = r.low();
                maxs[i] = r.high();
            }

            Region[] dims = new Region[rank];
            boolean zeroBased = true;
            for (int i = 0; i < rank; ++i) {
                dims[i] = new ContiguousRange(mins[i], maxs[i]);
                zeroBased &= mins[i] == 0;
            }
            ret = new MultiDimRegion(dims, zeroBased);
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see x10.runtime.region#contains(x10.runtime.point)
     */
    public boolean contains(Point p) {
        assert p != null;
        if (p.rank != rank)
            throw new RankMismatchException(p, rank);
        return points_.contains(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see x10.runtime.region#contains(int[])
     */
    public boolean contains(int[] p) {
        assert p != null;

        boolean ret;
        if (p.length == rank) {
            Point pp = Point.makeFromJavaArray(p);
            ret = contains(pp);
        }
        else
            ret = false;
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see x10.runtime.region#disjoint(x10.runtime.region)
     */
    public boolean disjoint(Region r) {
        assert r != null;
        // intersection() will check the rank
        return intersection(r).size() == 0;
    }

    /* (non-Javadoc)
     * @see x10.runtime.region#ordinal(x10.runtime.point)
     */
    public int ordinal(Point p) throws ArrayIndexOutOfBoundsException {
        assert p != null;
        if (p.rank != rank)
            throw new RankMismatchException(p, rank);

        int ret = 0;
        if (size() == 0)
            throw new ArrayIndexOutOfBoundsException();
        if (!contains(p))
            throw new ArrayIndexOutOfBoundsException();
        else {
            for (Iterator<Point> it = iterator(); it.hasNext();) {
                Point q = (Point) it.next();
                if (q.equals(p))
                    break;
                else
                    ret++;
            }
        }
        return ret;
    }

    /* (non-Javadoc)
     * @see x10.runtime.region#coord(int)
     */
    public Point coord(int ord) throws ArrayIndexOutOfBoundsException {
        assert ord >= 0;

        Point ret;
        if (ord >= size())
            throw new ArrayIndexOutOfBoundsException();
        else {
            int ctr = 0;
            Iterator it = iterator();
            while (ctr++ < ord)
                it.next();
            ret = (Point) it.next();
        }
        return ret;
    }

    /* (non-Javadoc)
     * @see x10.runtime.region#iterator()
     */
    @Override
    public Iterator<Point> iterator() {
        return points_.iterator();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        for (Iterator<Point> it = iterator(); it.hasNext();) {
            sb.append(it.next().toString());
            if (it.hasNext())
                sb.append(",");
        }
        sb.append("}");
        return sb.toString();
    }

}
