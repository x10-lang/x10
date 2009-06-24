/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.array;


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
public class RegionFactory {
    public static Region /* (k) */makeEmpty(/* nat */int k) {
        return new EmptyRegion(k);
    }

    /**
     * Construct a 1-dimensional Region_c, low..high with the given stride.
     * Return an empty region if low > high
     */
    public static Region /* (1) */makeRectStrided(int low, int high, int stride) {
        if (low > high)
            return makeEmpty(1);
        
        return (stride == 1) ? (Region) new ContiguousRange(low, high) : (Region) new StridedRange(low, high, stride);
    }
    
    /**
     * Construct a 1-dimensional Region_c, low..high with the given stride.
     * Return an empty region if low > high
     */
    public static Region /* (1) */makeRect(int low, int high) {
        return makeRectStrided(low, high, 1);
    }

    /**
     * Construct a Region_c, using the list of Region_c(1)'s passed as arguments
     * to the constructor.
     */
    public static Region/* (regions.length) */make(Region/* (1) */[] regions) {
        boolean zeroBased = true;
        for (int i = 0; i < regions.length; i++)
            zeroBased &= regions[i].zeroBased;
        return new MultiDimRegion(regions, zeroBased);
    }

    public static Region/* (2) */make(Region a, Region b) {
        return new MultiDimRegion(new Region[] { a, b }, a.zeroBased && b.zeroBased);
    }

    /**
     * Return an \code{upperTriangular} Region_c for a dim-rankal space of size
     * \code{size} in each dimension.
     */
    public static Region/* (rank) */makeUpperTriangular( /* nat */int rank, /* nat */int size) {
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
    public static Region/* (rank) */makeLowerTriangular( /* nat */int rank, /* nat */int size) {
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
    public static Region/* (rank) */makeBanded( /* nat */int rank,
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


}
