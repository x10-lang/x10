// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.lang;

import x10.compiler.SetOps;
import x10.array.BaseRegion;

/**
 * A Region(rank) represents a set of points of class Point(rank). The
 * Region class defines a set of static factory methods for
 * constructing regions. There are properties and methods for
 * accessing basic information about of a region, such as its bounding
 * box, its size, whether or not it is convex, whether or not it is
 * empty. There are a set of methods for supporting algebraic
 * operations on regions, such as intersection, union, difference, and
 * so on. The set of points in a region may be iterated over.
 *
 * @author bdlucas
 */

public abstract value class Region(
    rank: int,
    rect: boolean,
    zeroBased: boolean
) implements Iterable[Point{self.rank==this.rank}] {

    property rail = rank==1 && rect && zeroBased;
    property region = this; // structural affinity w/ Dist, Array for compiler

    //
    // factories
    //

    /**
     * Construct an empty region of the specified rank.
     */

    public static def makeEmpty(rank: int): Region(rank) = BaseRegion.makeEmpty1(rank);
    
    /**
     * Construct an unbounded region of a given rank that contains all
     * points of that rank.
     */

    public static def makeFull(rank: int): Region(rank) = BaseRegion.makeFull1(rank);
    
    /**
     * Construct a region of rank 0 that contains the single point of
     * rank 0. Useful as the identity region under Cartesian product.
     */

    public static def makeUnit(): Region(0) = BaseRegion.makeUnit1();

    
    /**
     * Construct an unbounded halfspace region of rank normal.rank
     * that consists of all points p satisfying dot(p,normal) + k <= 0.
     */

    public static def makeHalfspace(normal:Point, k:int): Region(normal.rank)
        = BaseRegion.makeHalfspace1(normal, k);


    //
    // rectangular factories
    //

    /**
     * Construct a rectangular region whose bounds are specified as
     * rails of ints.
     */

    public static def makeRectangular(min: Rail[int], max: Rail[int]): RectRegion(min.length)
        = BaseRegion.makeRectangular1(min, max);

    /**
     * Construct a rank-1 rectangular region with the specified bounds.
     */

    // XTENLANG-109 prevents zeroBased==(min==0)
    public static def makeRectangular(min: int, max: int): Region{self.rank==1 && /*self.zeroBased==(min==0) &&*/ self.rect}
        = BaseRegion.makeRectangular1(min, max);

    /**
     * Construct a rank-1 rectangular region with the specified bounds.
     */

    public static def make(min: int, max: int): RectRegion(1)
        = BaseRegion.makeRectangular1(min, max);

    /**
     * Construct a rank-n rectangular region that is the Cartesian
     * product of the specified rank-1 regions.
     */

    public static def make(regions: ValRail[Region]): RectRegion(regions.length)
        = BaseRegion.make1(regions as Rail[Region]);


    //
    // non-rectangular factories
    //

    /**
     * Construct a banded region of the given size, with the specified
     * number of diagonals above and below the main diagonal
     * (inclusive of the main diagonal).
     */

    public static def makeBanded(size: int, upper: int, lower: int): Region(2)
        = BaseRegion.makeBanded1(size, upper, lower);

    /**
     * Construct a banded region of the given size that includes only
     * the main diagonal.
     */

    public static def makeBanded(size: int): Region(2)
        = BaseRegion.makeBanded1(size);
    
    /**
     * Construct an upper triangular region of the given size.
     */

    public static def makeUpperTriangular(size: int): Region(2)
        = BaseRegion.makeUpperTriangular1(0, 0, size);

    /**
     * Construct an upper triangular region of the given size with the
     * given lower bounds.
     */

    public static def makeUpperTriangular(rowMin: int, colMin: int, size: int): Region(2)
        = BaseRegion.makeUpperTriangular1(rowMin, colMin, size);
    
    /**
     * Construct a lower triangular region of the given size.
     */

    public static def makeLowerTriangular(size: int): Region(2)
        = BaseRegion.makeLowerTriangular1(0, 0, size);

    /**
     * Construct an lower triangular region of the given size with the
     * given lower bounds.
     */

    public static def makeLowerTriangular(rowMin: int, colMin: int, size: int): Region(2)
        = BaseRegion.makeLowerTriangular1(rowMin, colMin, size);


    //
    // Basic non-property information.
    //

    /**
     * Returns the number of points in this region.
     */

    public abstract def size(): int;

    /**
     * Returns true iff this region is convex.
     */

    public abstract def isConvex(): boolean;

    /**
     * Returns true iff this region is empty.
     */

    public abstract def isEmpty(): boolean;



    //
    // bounding box
    //

    /**
     * The bounding box of a region r is the smallest rectangular region
     * that contains all the points of r.
     */

    abstract public def boundingBox(): Region(rank);

    /**
     * Returns the lower bounds of the bounding box of the region as a
     * Rail[int].
     */

    abstract public def min(): ValRail[int];

    /**
     * Returns the upper bounds of the bounding box of the region as a
     * Rail[int].
     */

    abstract public def max(): ValRail[int];
    
    /**
     * Returns the lower bound of the bounding box of the region along
     * the ith axis.
     */

    public def min(i:nat) = min()(i);

    /**
     * Returns the upper bound of the bounding box of the region along
     * the ith axis.
     */

    public def max(i:nat) = max()(i);    


    //
    // geometric ops
    //

    /**
     * Returns the complement of a region. The complement of a bounded
     * region will be unbounded.
     */

    abstract public def complement(): Region(rank);

    /**
     * Returns the union of two regions: a region that contains all
     * points that are in either this region or that region.
     */

    abstract public def union(that: Region(rank)): Region(rank);

    /**
     * Returns the union of two regions if they are disjoint,
     * otherwise throws an exception.
     */

    abstract public def disjointUnion(that: Region(rank)): Region(rank);

    /**
     * Returns the intersection of two regions: a region that contains all
     * points that are in both this region and that region.
     */

    abstract public def intersection(that: Region(rank)): Region(rank);

    /**
     * Returns the difference between two regions: a region that
     * contains all points that are in this region but are not in that
     * region.
     */

    abstract public def difference(that: Region(rank)): Region(rank);

    /**
     * Returns the Cartesian product of two regions. The Cartesian
     * product has rank this.rank+that.rank. For every point p in the
     * Cartesian product, the first this.rank coordinates of p are a
     * point in this region, while the last that.rank coordinates of p
     * are a point in that.region.
     */

    abstract public def product(that: Region): Region;

    /**
     * Returns the region shifted by a Point (vector). The Point has
     * to have the same rank as the region. For every point p in the
     * resulting region, each coordinate is that of the corresponding
     * point q shifted by the same coordinate of the given point.
     */

    abstract public def translate(v: Point(rank)): Region(rank);

    /**
     * Returns the projection of a region onto the specified axis. The
     * projection is a rank-1 region such that for every point (i) in
     * the projection, there is some point p in this region such that
     * p(axis)==i.
     */

    abstract public def projection(axis: int): Region(1);

    /**
     * Returns the projection of a region onto all axes but the
     * specified axis.
     */

    abstract public def eliminate(axis: int): Region(rank-1);

    /**
     * Returns true iff this region has no points in common with that
     * region.
     */

    public abstract def disjoint(that: Region(rank)): boolean;


    /**
     * Utility method to return an array of n regions, which together
     * block divide the rank-1 region r.
     */

    // XXX temp port from previous util/Dist.x10 to support Rice trial
    // refactor dist, region to generalize this; and/or connect to
    // tiled regions

    public static def makeBlock(r: Region(1), n:int): ValRail[Region(1)](n) {
        assert n >=0;
        val min = r.min(0);
        val max = r.max(0);
        val count = max-min+1;
        val baseSize = count/n;
        val extra = count - baseSize*n;
        val result = ValRail.make[Region(1)](n, (i:nat):Region(1) => {
            val start = min + i*baseSize + (i<extra?i:extra);
            return start..start+baseSize+(i<extra?0:-1);
        });
        return result;
    }


    /**
     * Return an iterator for this region. Normally accessed using the
     * syntax
     *
     *    for (p:Point in r)
     *        ... p ...
     */

    public abstract def iterator(): Iterator[Point{self.rank==this.rank}];


    /**
     * The Scanner class supports efficient scanning. Usage:
     *
     *    for (s:Scanner in r.scanners()) {
     *        int min0 = s.min(0);
     *        int max0 = s.max(0);
     *        for (var i0:int=min0; i0<=max0; i0++) {
     *            s.set(0,i0);
     *            int min1 = s.min(1);
     *            int max1 = s.max(1);
     *            for (var i1:int=min1; i1<=max1; i1++) {
     *                ...
     *            }
     *        }
     *    }
     *
     */

    public static interface Scanner {
        def set(axis: int, position: int): void;
        def min(axis: int): int;
        def max(axis: int): int;
    }

    public abstract def scanners(): Iterator[Scanner];

    public def scan() = new x10.array.PolyScanner(this);


    //
    // conversion
    //

    public static operator (rs: ValRail[Region]): RectRegion(rs.length) = make(rs);


    //
    // ops
    //

    public operator ! this: Region(rank) = complement();
    public operator this && (that: Region(rank)): Region(rank) = intersection(that);
    public operator this || (that: Region(rank)): Region(rank) = union(that);
    public operator this - (that: Region(rank)): Region(rank) = difference(that);

    public operator this * (that: Region) = product(that);

    public operator this + (v: Point(rank)) = translate(v);

    public operator (v: Point(rank)) + this = translate(v);


    //
    // comparison
    //

    abstract public def contains(that: Region(rank)): boolean;
    // FIXME: EQUALS HACK
    // abstract public def equals(that: Region/*(rank)*/): boolean; 
    //
    abstract public def contains(p: Point): boolean;


    //
    //
    //



    //
    //
    //

    protected def this(r: int, t: boolean, z: boolean)
        :Region{self.rank==r, self.rect==t, self.zeroBased==z} {
        property(r, t, z);
    }
}

