/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.array;

import java.util.Iterator;

import x10.core.Value;

/**
 * A region represents a (sparse or dense) k-dimensional space of points. A
 * region is a dependent class, with the value parameter specifying the
 * dimension of the region. A convex k-dimensional region is easy to represent,
 * e.g. as a list of k (min, max) pairs. In general, regions may not be convex.
 * (For instance difference(region(dimension) r) produces non-convex regions.)
 * Non-convex regions are very important for many physical problems. For
 * instance the region of the halo around a 2-d array is non-convex.
 * 
 * @author vj
 */
public abstract class Region extends Value {
    public static final int UNKNOWN = 0;
    public static final int RANGE = 1;
    public static final int ARBITRARY = 2;
    public static final int BANDED = 3;
    public static final int MULTIDIM = 4;
    public static final int TRIANGULAR = 5;

    // nat is translated to int for now.
    /* property */public final/* nat *//* long */int rank;
    /* property */public final boolean rect;
    /**
     * A k-ary region is zeroBased if it is declared to be a rectangular region
     * (of rank k), and [0,...0] is the smallest point in it.
     */
    /* property */public final boolean zeroBased;
    /* property */public final boolean rail;
    /* property */public final boolean colMajor;

    public static Region /* (k) */emptyRegion(/* nat */int k) {
        return new EmptyRegion(k);
    }

    /**
     * Construct a 1-dimensional Region_c, low..high with the given stride.
     * Return an empty region if low > high
     */
    public static Region /* (1) */region(int low, int high, int stride) {
        if (low > high)
            return emptyRegion(1);
        
        return (stride == 1) ? (Region) new ContiguousRange(low, high) : (Region) new StridedRange(low, high, stride);
    }
    
    /**
     * Construct a 1-dimensional Region_c, low..high with the given stride.
     * Return an empty region if low > high
     */
    public static Region /* (1) */region(int low, int high) {
        return region(low, high, 1);
    }

    /**
     * Construct a Region_c, using the list of Region_c(1)'s passed as arguments
     * to the constructor.
     */
    public static Region/* (regions.length) */region(Region/* (1) */[] regions) {
        boolean zeroBased = true;
        for (int i = 0; i < regions.length; i++)
            zeroBased &= regions[i].zeroBased;
        return new MultiDimRegion(regions, zeroBased);
    }

    public static Region/* (2) */region(Region a, Region b) {
        return new MultiDimRegion(new Region[] { a, b }, a.zeroBased && b.zeroBased);
    }

    /**
     * Return an \code{upperTriangular} Region_c for a dim-rankal space of size
     * \code{size} in each dimension.
     */
    public static Region/* (rank) */upperTriangular( /* nat */int rank, /* nat */int size) {
        Region ret;
        if (rank != 2)
            throw new Error("Triangular region of dimension != 2 not supported.");
        else {
            ContiguousRange cr = new ContiguousRange(0, size - 1);
            Region[] r = new Region[] { cr, cr };
            ret = new TriangularRegion(r, false);
        }
        return ret;
    }

    /**
     * Return a lowerTriangular Region_c for a rank-dimensional space of size
     * \code{size} in each dimension.
     */
    public static Region/* (rank) */lowerTriangular( /* nat */int rank, /* nat */int size) {
        Region ret;
        if (rank != 2)
            throw new Error("Triangular region of dimension != 2 not supported.");
        else {
            ContiguousRange cr = new ContiguousRange(0, size - 1);
            Region[] r = new Region[] { cr, cr };
            ret = new TriangularRegion(r, true);
        }
        return ret;
    }

    /**
     * Return a banded Region_c of width {\code width} for a rank-dimensional
     * space of size {\code size} in each dimension.
     */
    public static Region/* (rank) */banded( /* nat */int rank,
    /* nat */int size,
    /* nat */int width) {
        Region ret;
        if (rank != 2)
            throw new Error("Banded region of dimension != 2 not supported.");
        else {
            ContiguousRange cr = new ContiguousRange(0, size - 1);
            Region[] r = new Region[] { cr, cr };
            ret = new BandedRegion(r, width, true);
        }
        return ret;
    }

    protected Region(/* nat long */int rank, boolean rect, boolean zeroB) {
        this(rank, rect, zeroB, false);
    }

    protected Region(/* nat long */int rank, boolean rect, boolean zeroB, boolean colMajor) {
        assert rank >= 1;
        this.rank = rank;
        this.rect = rect;
        this.zeroBased = zeroB;
        rail = rank == 1 && rect && zeroBased;
        this.colMajor = colMajor;
    }

    /**
     * Return the number of points in this region.
     */
    abstract public/* nat */int size();

    /**
     * Use modular arithmetic on the index to determine the dimension to return.
     */
    abstract public Region/* (1) */rank(/* nat */int index);

    /**
     * Returns true iff the region contains every point between two points in
     * the region.
     */
    abstract public boolean isConvex();

    /**
     * Return the low bound for a 1-dimensional region. Can only be invoked on
     * 1-dimensional objects. Throws an exception if size of the region is 0.
     */
    abstract public/* (:rank==1) */int low() throws UnsupportedOperationException;

    /**
     * Return the high bound for a 1-dimensional region. Can only be invoked on
     * 1-dimensional objects. Thrwos an exception if size of the region is 0.
     */
    abstract public/* (:rank==1) */int high() throws UnsupportedOperationException;

    abstract public Region/* (rank) */union(Region/* (rank) */r);

    abstract public Region/* (rank) */intersection(Region/* (rank) */r);

    abstract public Region/* (rank) */difference(Region/* (rank) */r);

    abstract public Region/* (rank) */convexHull();

    public Point startPoint() {
        Iterator<Point> it = iterator();
        return (it.hasNext() ? (Point) it.next() : null);
    }

    /**
     * Returns true iff this is a superset of r.
     */
    public boolean contains(Region r) {
        boolean ret = true;
        for (Iterator<Point> it = r.iterator(); ret && it.hasNext();) {
            Point p = (Point) it.next();
            if (!contains(p))
                ret = false;
        }
        return ret;
    }

    /**
     * Returns true iff this region contains this point.
     */
    abstract public boolean contains(Point/* (rank) */p);

    abstract public boolean contains(int[] p);

    /**
     * Returns true iff this is disjoint from r.
     */
    abstract public boolean disjoint(Region/* (rank) */r);

    /**
     * Returns true iff the set of points in r and this are equal.
     */
    public boolean equal(Region/* (rank) */r) {
        return this.contains(r) && r.contains(this);
    }

    /**
     * @param p
     *                a point in the coordinate space
     * @return the ordinal number of the point [0 ... size()[
     */
    abstract public/* nat */int ordinal(Point/* (rank) */p) throws ArrayIndexOutOfBoundsException;

    /**
     * @param ord
     *                the ordinal number, must be smaller than size()
     * @return the coordinate that has ordinal number ord
     */
    public abstract Point/* (rank) */coord(/* nat */int ord) throws ArrayIndexOutOfBoundsException;

    /**
     * @return Iterator that yields the individual points of a region in
     *         lexicographical order.
     */
    abstract public Iterator<Point> iterator();

    abstract public String toString();

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(java.lang.Object o) {
        if (!(o instanceof Region))
            return false;
        boolean ret = (o == this);
        if (!ret) {
            Region rhs = (Region) o;
            if (rhs.rank == rank && rhs.size() == size()) {
                ret = true;
                for (Iterator it = iterator(); it.hasNext();) {
                    Point p = (Point) it.next();
                    if (!rhs.contains(p)) {
                        ret = false;
                        break;
                    }
                }
            }
        }
        return ret;
    }

    public final int hashCode() {
        return rank;
    }

    /**
     * @return a set of regions that are a partition of n consecutive chunks of
     *         the original region (this).
     */
    public Region[] partition(int n, int dim) {
        System.err.println("this should not be called");
        // the runtime library should never call this. -- and is should also
        // not be in the public interface that is available to the X10 programmer
        // (the reason is show here is because runtime implementation and
        // X10 standard library interface are the same in the current prototype)
        assert (false);
        return null;
    }

    public Dist/*(:region=this)*/toDistribution() {
        return Dist.local(this);
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

    public boolean rail() {
        return rail;
    }

}
