/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.array;

import x10.util.Set;

/**
 * A distributution supports distributed arrays by providing a mapping
 * from Points to Places.  Equivalently, a distribution may be defined
 * as a mapping from Places to Regions.  The Dist class provides a set
 * of factory methods for constructing various distributions.  There
 * are a set of methods supporting algebraic operations on
 * distributions, such as union, intersection, difference, and so on.
 */
public abstract class Dist(
    /**
     * The region this distribution is defined over.
     */
    region: Region,
    /**
     * Is this distribution "unique" (at most one point per place)?
     */
    unique: boolean,
    /**
     * Is this distribution "constant" (all points map to the same place)?
     */
    constant: boolean,
    /**
     * If this distribution is "constant", the place all points map to (or null).
     */
    onePlace: Place
) implements
    (Point/*(region.rank)*/)=>Place
    // (Place)=>Region XTENLANG-60
    , Iterable[Point(region.rank)]
{

    /**
     * The rank of this distribution.
     */
    property rank: Int = region.rank;
    /**
     * Is this distribution defined over a rectangular region?
     */
    property rect: boolean = region.rect;
    /**
     * Is this distribution's region zero-based?
     */
    property zeroBased: boolean = region.zeroBased;
    /**
     * Is this distribution's region a "rail" (one-dimensional contiguous zero-based)?
     */
    property rail: boolean = region.rail;

    // XTENLANG-50: workaround requires explicit return type decls here
    // XTENLANG-4: workaround requires BaseDist methods to be named differently from these methods

    //
    // factories - place is all applicable places
    //

    /**
     * Create a distribution over a rank-1 region that maps every
     * point in the region to a distinct place, and which maps some
     * point in the region to every place.
     *
     * @return a "unique" distribution over all places.
     */
    // TODO: [IP] return Dist(1){rect&&unique}
    public static def makeUnique(): Dist(1){rect} = BaseDist.makeUnique1();

    /**
     * Create a distribution over the specified region that maps
     * every point in the region to here.
     *
     * @param r the given region
     * @return a "constant" distribution over r.
     */
    // TODO: [IP] return Dist(r){constant&&onePlace==here}
    public static def makeConstant(r: Region): Dist(r) = BaseDist.makeConstant1(r);

    /**
     * Create a distribution over the specified region that maps
     * every point in the region to here.
     *
     * @param r the given region
     * @return a "constant" distribution over r.
     * @see #makeConstant(Region)
     */
    public static def make(r: Region): Dist(r) = makeConstant(r);

    /**
     * Create a distribution over the specified region that varies in
     * place only along the specified axis, and maps the ith
     * coordinate along that axis to place i%Place.NUM_PLACES.
     *
     * @param r the given region
     * @param axis the dimension to cycle over
     * @return a "cyclic" distribution over r.
     */
    public static def makeCyclic(r: Region, axis: int): Dist(r)
        = BaseDist.makeBlockCyclic1(r, axis, 1);

    /**
     * Create a distribution over the specified region that varies in
     * place only along the zeroth axis, and maps the ith
     * coordinate along that axis to place i%Place.NUM_PLACES.
     *
     * @param r the given region
     * @return a "cyclic" distribution over r, cycling over the zeroth axis.
     */
    public static def makeCyclic(r: Region): Dist(r)
        = BaseDist.makeBlockCyclic1(r, 0, 1);

    /**
     * Create a distribution over the specified region that varies in
     * place only along the specified axis. It divides the coordinates
     * along that axis into Place.MAX_PLACES blocks, and assigns
     * successive blocks to successive places.
     *
     * @param r the given region
     * @param axis the dimension to block over
     * @return a "block" distribution over r.
     */
    public static def makeBlock(r: Region, axis: int): Dist(r) {
        val n = r.max()(axis) - r.min()(axis) + 1;
        val bs = (n + Place.MAX_PLACES - 1) / Place.MAX_PLACES;
        return BaseDist.makeBlockCyclic1(r, axis, bs);
    }

    /**
     * Create a distribution over the specified region that varies in
     * place only the zeroth axis. It divides the coordinates
     * along that axis into Place.MAX_PLACES blocks, and assigns
     * successive blocks to successive places.
     *
     * @param r the given region
     * @return a "block" distribution over r, blocked over the zeroth axis.
     */
    public static def makeBlock(r:Region) = makeBlock(r, 0);

    /**
     * Create a distribution over the specified region that varies in
     * place only along the specified axis. It divides the coordinates
     * along that axis into blocks of the specified size, and assigns
     * block i to place i%Place.MAX_PLACES.
     *
     * @param r the given region
     * @param axis the dimension to block over
     * @param blockSize the size of the block
     * @return a "block-cyclic" distribution over r.
     */
    public static def makeBlockCyclic(r: Region, axis: int, blockSize: int): Dist(r)
        = BaseDist.makeBlockCyclic1(r, axis, blockSize);

    //
    // factories - place is a parameter
    //

    /**
     * Create a distribution over a rank-1 region that maps every
     * point in the region to a place in ps, and which maps some
     * point in the region to every place in ps.
     *
     * @param ps the rail of places
     * @return a "unique" distribution over the places in ps
     */
    public static def makeUnique(ps:Rail[Place]): Dist(1)
        = BaseDist.makeUnique1(ps);

    /**
     * Create a distribution over a rank-1 region that maps every
     * point in the region to a place in ps, and which maps some
     * point in the region to every place in ps.
     *
     * @param ps the set of places
     * @return a "unique" distribution over the places in ps
     */
    public static def makeUnique(ps: Set[Place]): Dist(1)
        = BaseDist.makeUnique1(ps);

    /**
     * Create a distribution over the specified region that maps
     * every point in the region to the specified place.
     *
     * @param r the given region
     * @param p the given place
     * @return a "constant" distribution over r that maps to p.
     */
    public static def makeConstant(r: Region, p: Place): Dist(r)
        = BaseDist.makeConstant1(r, p);

    /**
     * Create a distribution over the specified region that varies in
     * place only along the specified axis, and maps the ith
     * coordinate along that axis to place ps(i%ps.length).
     *
     * @param r the given region
     * @param axis the dimension to cycle over
     * @param ps the set of places
     * @return a "cyclic" distribution over r, cycling over the places in ps.
     */
    public static def makeCyclic(r: Region, axis: int, ps: Set[Place]): Dist(r)
        = BaseDist.makeCyclic1(r, axis, ps);

    /**
     * Create a distribution over the specified region that varies in
     * place only along the specified axis. It divides the coordinates
     * along that axis into ps.length blocks, and assigns successive
     * blocks to successive places in ps.
     *
     * @param r the given region
     * @param axis the dimension to block over
     * @param ps the set of places
     * @return a "block" distribution over r, blocking over the places in ps.
     */
    public static def makeBlock(r: Region, axis: int, ps: Set[Place]): Dist(r)
        = BaseDist.makeBlock1(r, axis, ps);

    /**
     * Create a distribution over the specified region that varies in
     * place only along the specified axis. It divides the coordinates
     * along that axis into blocks of the specified size, and assigns
     * block i to place ps(i%ps.length).
     *
     * @param r the given region
     * @param axis the dimension to block over
     * @param blockSize the size of the block
     * @param ps the set of places
     * @return a "block-cyclic" distribution over r, cycling over the places in ps.
     */
    public static def makeBlockCyclic(r: Region, axis: int, blockSize: int, ps: Set[Place])
        = BaseDist.makeBlockCyclic1(r, axis, blockSize, ps);


    //
    // mapping places to regions
    //

    /**
     * Return the set of places that this distribution maps some point to.
     */
    abstract public global def places(): ValRail[Place];

    /**
     * Return the set of regions that this distribution maps some place to.
     */
    abstract public global def regions(): ValRail[Region(rank)]; // essentially regionMap().values()

    /**
     * Return the region consisting of points which this distribution
     * maps to the specified place.
     *
     * @param p the given place
     * @return the points that this distribution maps to p.
     */
    abstract public global def get(p: Place): Region(rank);



    //
    // mapping points to places
    //

    /**
     * Return the place which this distribution maps the specified point to.
     *
     * @param pt the given point
     * @return the place that this distribution maps pt to.
     */
    abstract public global def apply(pt: Point/*(rank)*/): Place;

    /**
     * Return the place which this distribution maps the specified index to.
     * Only applies to one-dimensional distributions.
     * Functionally equivalent to indexing the distribution via a one-dimensional point.
     *
     * @param i0 the given index in the first dimension
     * @return the place that this distribution maps the given index to.
     * @see #apply(Point)
     */
    abstract public global def apply(i0: int): Place;

    /**
     * Return the place which this distribution maps the specified pair of indices to.
     * Only applies to two-dimensional distributions.
     * Functionally equivalent to indexing the distribution via a two-dimensional point.
     *
     * @param i0 the given index in the first dimension
     * @param i1 the given index in the second dimension
     * @return the place that this distribution maps the given pair of indices to.
     * @see #apply(Point)
     */
    abstract public global def apply(i0: int, i1: int): Place;

    /**
     * Return the place which this distribution maps the specified triple of indices to.
     * Only applies to three-dimensional distributions.
     * Functionally equivalent to indexing the distribution via a three-dimensional point.
     *
     * @param i0 the given index in the first dimension
     * @param i1 the given index in the second dimension
     * @param i2 the given index in the third dimension
     * @return the place that this distribution maps the given triple of indices to.
     * @see #apply(Point)
     */
    abstract public global def apply(i0: int, i1: int, i2: int): Place;

    /**
     * Return the place which this distribution maps the specified quartet of indices to.
     * Only applies to four-dimensional distributions.
     * Functionally equivalent to indexing the distribution via a four-dimensional point.
     *
     * @param i0 the given index in the first dimension
     * @param i1 the given index in the second dimension
     * @param i2 the given index in the third dimension
     * @param i3 the given index in the fourth dimension
     * @return the place that this distribution maps the given quartet of indices to.
     * @see #apply(Point)
     */
    abstract public global def apply(i0: int, i1: int, i2: int, i3: int): Place;


    //
    //
    //

    /**
     * Return an iterator for the underlying region of this distribution.
     * Supports the syntax
     *
     *   (for p:Point in d)
     *       ... p ...
     *
     * @return an iterator over the points in the region of this distribution.
     * @see x10.lang.Iterable[T]#iterator()
     */
    public global def iterator(): Iterator[Point{self.rank==region.rank}] = region.iterator() as Iterator[Point{self.rank==region.rank}];


    //
    // geometric ops with region
    //

    /**
     * Return the distribution defined over this.region&&that.region,
     * and which maps every point in its region to the same place as
     * this distribution.
     *
     * @param r the given region
     * @return the intersection of this distribution with r.
     */
    abstract public global def intersection(r: Region(rank)): Dist(rank);

    /**
     * Return the distribution defined over this.region-that.region,
     * and which maps every point in its region to the same place as
     * this distribution.
     *
     * @param r the given region
     * @return the difference of this distribution and r.
     */
    abstract public global def difference(r: Region(rank)): Dist(rank);

    /**
     * Return the distribution defined over r, which must be
     * contained in this.region, and which maps every point in its
     * region to the same place as this distribution.
     * Functionally equivalent to {@link #intersection(Region)}.
     *
     * @param r the given region
     * @return the restriction of this distribution to r.
     */
    abstract public global def restriction(r: Region(rank)): Dist(rank);


    //
    // geometric ops with distribution
    //

    /**
     * Return true iff that.region is contained in this.region, and
     * that distribution maps every point to the same place as this
     * distribution.
     *
     * @param that the given distribution
     * @return true if that is a sub-distribution of this distribution.
     */
    abstract public global def isSubdistribution(that: Dist(rank)): boolean;

    /**
     * Return a distribution containing only points that are
     * contained in both this distribution and that distribution,
     * and which the two distributions map to the same place.
     *
     * @param that the given distribution
     * @return the intersection of this distribution with that.
     */
    abstract public global def intersection(that: Dist(rank)): Dist(rank);

    /**
     * Return the distribution that contains every point in this
     * distribution, except for those points which are also contained
     * in that distribution and which that distribution maps to the
     * same place as this distribution.
     *
     * @param that the given distribution
     * @return the difference of this distribution and that.
     */
    abstract public global def difference(that: Dist(rank)): Dist(rank);

    /**
     * If this distribution and that distribution are disjoint,
     * return the distribution that contains all points p that are in
     * either distribution, and which map p to the same place as the
     * distribution that contains p.  Otherwise throws an exception.
     *
     * @param that the given distribution
     * @return the disjoint union of this distribution and that.
     */
    abstract public global def union(that: Dist(rank)): Dist(rank);

    /**
     * Return a distribution whose region is the union of the regions
     * of the two distributions, and which maps each point p to
     * this(p) if p is contained in this, otherwise maps p to that(p).
     *
     * @param that the given distribution
     * @return the union of this distribution and that.
     */
    abstract public global def overlay(that: Dist(rank)): Dist(rank);

    /**
     * Return true iff that is a distribution and both distributions are defined
     * over the same regions, and map every point in that region to the same place.
     *
     * @param that the given distribution
     * @return true if that is equal to this distribution.
     */
    abstract public global safe def equals(that:Any):boolean;

    //
    // other geometric ops
    //

    /**
     * Return the distribution restricted to those points which it
     * maps to the specified place.
     *
     * @param p the given place
     * @return the portion of this distribution that maps to p.
     */
    abstract public global def restriction(p: Place): Dist(rank);

    /**
     * Return true iff this.region contains p.
     *
     * @param p the given point
     * @return true if this distribution contains p.
     */
    abstract public global def contains(p: Point): boolean;


    //
    // ops
    //

    /**
     * Restrict this distribution to the specified region.
     *
     * @param r the given region
     * @return the restriction of this distribution to r.
     */
    public global operator this | (r: Region(this.rank)): Dist(this.rank)
	= restriction(r);

    /**
     * Restrict this distribution to the specified place.
     *
     * @param p the given place
     * @return the restriction of this distribution to p.
     */
    public global operator this | (p: Place): Dist(rank) = restriction(p);

    /**
     * Intersect this distribution with the specified distribution.
     *
     * @param d the given distribution
     * @return the intersection of this distribution and d.
     */
    public global operator this && (d: Dist(rank)): Dist(rank) = intersection(d);

    /**
     * Union this distribution with the specified distribution.
     *
     * @param d the given distribution
     * @return the disjoint union of this distribution and d.
     */
    public global operator this || (d: Dist(rank)): Dist(rank) = union(d);

    /**
     * Subtract the specified distribution from this distribution.
     *
     * @param d the given distribution
     * @return the difference of this distribution and d.
     */
    public global operator this - (d: Dist(rank)): Dist(rank) = difference(d);

    /**
     * Subtract the specified region from this distribution.
     *
     * @param r the given region
     * @return the difference of this distribution and r.
     */
    public global operator this - (r: Region(rank)): Dist(rank) = difference(r);


    //
    //
    //

    /**
     * Construct a distribution over the specified region.
     *
     * @param region the given region
     * @param unique whether to construct a "unique" distribution
     * @param constant whether to construct a "constant" distribution
     * @param onePlace the place all points map to (if "constant") or null
     */
    protected def this(region: Region, unique: boolean, constant: boolean, onePlace: Place) = {
        property(region, unique, constant, onePlace);
    }
}

// vim:shiftwidth=4:tabstop=4:expandtab
