/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.regionarray;

import x10.compiler.NoInline;
import x10.compiler.NoReturn;

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
    region:Region
) implements
    (Point(region.rank))=>Place
    // (Place)=>Region XTENLANG-60
    , Iterable[Point(region.rank)]
{
    /**
     * The rank of this distribution.
     */
    property rank():Long = region.rank;

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
    public static def makeUnique():Dist(1) {
        return new UniqueDist() as Dist(1);
    }

    /**
     * Create a distribution over the specified region that maps
     * every point in the region to here.
     *
     * @param r the given region
     * @return a "constant" distribution over r.
     */
    public static def makeConstant(r:Region):Dist(r) {
        return new ConstantDist(r, here);
    }

    /**
     * Create a distribution over the specified region that maps
     * every point in the region to here.
     *
     * @param r the given region
     * @return a "constant" distribution over r.
     * @see #makeConstant(Region)
     */
    public static def make(r:Region):Dist(r) = makeConstant(r);

    /**
     * Create a distribution of the specified region over all 
     * Places that varies in place only along the specified axis.
     * It divides the coordinates along that axis into Place.numPlaces() blocks, 
     * and assigns successive blocks to successive places.  If the number of coordinates
     * in the axis does not divide evenly into the number of blocks, then 
     * the first (max(axis)-min(axis)+1)%Place.numPlaces() blocks will be assigned 
     * one more coordinate than the remaining blocks.
     *
     * @param r the given region
     * @param axis the dimension to block over
     * @return a "block" distribution over r.
     */
    public static def makeBlock(r:Region, axis:Long):Dist(r) = makeBlock(r, axis, Place.places());

    /**
     * Creates a block, block distribution across the specified PlaceGroup pg.
     * The coordinates are split along axis0 into M divisions such that M is the minimum of:
     *   - 2^q where q is the next integer above log2(P) / 2
     *   - the length of axis0
     * and split along axis1 into N divisions such that M*(N-1) <= pg.size() <= M*N.
     * Thus there are M*N blocks of size (axis0/M, axis1/N).
     * The blocks are not necessarily of integer size in either dimension.
     * Places 0..(M*N-pg.size()) are each assigned two such blocks, contiguous in axis0.
     * The remaining places are assigned a single block.
     * Block min and max coordinates are rounded to create subregions for each place,
     * e.g. a block [1.0..1.5,2.25..2.75] is rounded to a subregion [1..2,2..2].
     * @param r the given region
     * @param axis0 the first dimension to block over
     * @param axis1 the second dimension to block over
     * @param pg the set of places
     * @return a "block,block" distribution of region r over the places in pg
     */
    public static def makeBlockBlock(r:Region, axis0:Long, axis1:Long, pg:PlaceGroup):Dist(r) {
        return new BlockBlockDist(r, axis0, axis1, Place.places());
    }

    /**
     * Creates a block, block distribution across all places.
     * @param r the given region
     * @param axis0 the first dimension to block over
     * @param axis1 the second dimension to block over
     * @see makeBlockBlock(Region, Long, Long, PlaceGroup)
     */
    public static def makeBlockBlock(r:Region, axis0:Long, axis1:Long):Dist(r)
        = makeBlockBlock(r, axis0, axis1, Place.places());

    /**
     * Creates a block, block distribution across all places that varies in
     * place along the 0-th and 1st axes.
     * @param r the given region
     * @see makeBlockBlock(Region, Long, Long, PlaceGroup)
     */
    public static def makeBlockBlock(r:Region):Dist(r)
        = makeBlockBlock(r, 0, 1, Place.places());

    /**
     * Create a distribution of the specified region over all places that varies in
     * place only along the 0-th axis. It divides the coordinates
     * along the 0-th axis into Place.numPlaces() blocks, and assigns
     * successive blocks to successive places.  If the number of coordinates
     * in the axis does not divide evenly into the number of blocks, then 
     * the first (max(axis)-min(axis)+1)%Place.numPlaces() blocks will be assigned 
     * one more coordinate than the remaining blocks.
     *
     * @param r the given region
     * @return a "block" distribution over r.
     */
    public static def makeBlock(r:Region) = makeBlock(r, 0, Place.places());

    /**
     * Create a distribution over a rank-1 region that maps every
     * point in the region to a place in pg, and which maps some
     * point in the region to every place in ps.
     *
     * @param pg the set of places
     * @return a "unique" distribution over the places in ps
     */
    public static def makeUnique(pg:PlaceGroup):Dist(1) {
        if (pg.equals(Place.places())) {
            return makeUnique();
        } else {
            return new UniqueDist(pg) as Dist(1);
        }
    }

    /**
     * Create a distribution over the specified region that maps
     * every point in the region to the specified place.
     *
     * @param r the given region
     * @param p the given place
     * @return a "constant" distribution over r that maps to p.
     */
    public static def makeConstant(r:Region, p:Place):Dist(r) {
        return new ConstantDist(r, p);
    }

    /**
     * Create a distribution over the specified region that varies in
     * place only along the specified axis. It divides the coordinates
     * along that axis into pg.numPlaces() blocks, and assigns successive
     * blocks to successive places in pg. If the number of coordinates
     * in the axis does not divide evenly into the number of places in pg, then 
     * the first (max(axis)-min(axis)+1)%pg.numPlaces() blocks will be assigned 
     * one more coordinate than the remaining blocks.
     *
     * @param r the given region
     * @param axis the dimension to block over
     * @param pg the PlaceGroup over which to distribute the region
     * @return a "block" distribution over r, blocking over the places in ps.
     */
    public static def makeBlock(r:Region, axis:Long, pg:PlaceGroup):Dist(r) {
        return new BlockDist(r, axis, pg);
    }

    //
    // mapping places to regions
    //

    /**
     * The PlaceGroup over which the distribuiton is defined
     */
    abstract public def places():PlaceGroup;

    /**
     * How many places are included in the distribution?
     */
    abstract public def numPlaces():Long;

    /**
     * @return an object that implements Iterable[Region] that can be used to
     *         iterate over the set of Regions that this distribution maps some point to.
     */
    abstract public def regions():Iterable[Region(rank)];

    /**
     * Return the region consisting of points which this distribution
     * maps to the specified place.
     *
     * @param p the given place
     * @return the region that this distribution maps to p.
     */
    abstract public def get(p:Place):Region(rank);

    /**
     * Return the region consisting of points which this distribution
     * maps to the specified place.  This is a shorthave for get(p).
     *
     * @param p the given place
     * @return the region that this distribution maps to p.
     */
   public operator this(p:Place):Region(rank) = get(p);



    //
    // mapping points to places
    //

    /**
     * Return the place which this distribution maps the specified point to.
     *
     * @param pt the given point
     * @return the place that this distribution maps pt to.
     */
    abstract public operator this(pt:Point(rank)):Place;

    /**
     * Return the place which this distribution maps the specified index to.
     * Only applies to one-dimensional distributions.
     * Functionally equivalent to indexing the distribution via a one-dimensional point.
     *
     * @param i0 the given index in the first dimension
     * @return the place that this distribution maps the given index to.
     * @see #operator(Point)
     */
    public operator this(i0:Long){rank==1}:Place = this(Point.make(i0));

    /**
     * Return the place which this distribution maps the specified pair of indices to.
     * Only applies to two-dimensional distributions.
     * Functionally equivalent to indexing the distribution via a two-dimensional point.
     *
     * @param i0 the given index in the first dimension
     * @param i1 the given index in the second dimension
     * @return the place that this distribution maps the given pair of indices to.
     * @see #operator(Point)
     */
    public operator this(i0:Long, i1:Long){rank==2}:Place = this(Point.make(i0, i1));

    /**
     * Return the place which this distribution maps the specified triple of indices to.
     * Only applies to three-dimensional distributions.
     * Functionally equivalent to indexing the distribution via a three-dimensional point.
     *
     * @param i0 the given index in the first dimension
     * @param i1 the given index in the second dimension
     * @param i2 the given index in the third dimension
     * @return the place that this distribution maps the given triple of indices to.
     * @see #operator(Point)
     */
    public operator this(i0:Long, i1:Long, i2:Long){rank==3}:Place = this(Point.make(i0, i1, i2));

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
     * @see #operator(Point)
     */
    public operator this(i0:Long, i1:Long, i2:Long, i3:Long){rank==4}:Place = this(Point.make(i0,i1,i2,i3));



    /**
     * Return the offset in linearized place-local storage of the given point.
     * Throw a BadPlaceException if the given point is not mapped to 
     * the current place.  Primarily intended to be used by the DistArray implementation,
     * but may be useful for other data structures as well that need to associate 
     * Points in a Distribution with a dense, zero-based numbering.
     *
     * @param pt the given point
     * @return the storage offset assigned to pt by this distribution
     */
    abstract public def offset(pt:Point(rank)):Long;

    /**
     * Return the offset in linearized place-local storage of the point [i0]
     * Throw a BadPlaceException if the given point is not mapped to 
     * the current place.  Primarily intended to be used by the DistArray implementation,
     * but may be useful for other data structures as well that need to associate 
     * Points in a Distribution with a dense, zero-based numbering.
     *
     * Only applies to one-dimensional distributions.
     *
     * @param i0 the given index in the first dimension
     * @return the storage offset assigned to [i0] by this distribution
     * @see #offset(Point)
     */
    public def offset(i0:Long){rank==1}:Long = offset(Point.make(i0));

    /**
     * Return the offset in linearized place-local storage of the point [i0,i1].
     * Throw a BadPlaceException if the given point is not mapped to 
     * the current place.  Primarily intended to be used by the DistArray implementation,
     * but may be useful for other data structures as well that need to associate 
     * Points in a Distribution with a dense, zero-based numbering.
     *
     * Only applies to two-dimensional distributions.
     *
     * @param i0 the given index in the first dimension
     * @param i1 the given index in the second dimension
     * @return the storage offset assigned to [i0,i1] by this distribution
     * @see #offset(Point)
     */
    public def offset(i0:Long, i1:Long){rank==2}:Long = offset(Point.make(i0, i1));

    /**
     * Return the offset in linearized place-local storage of the point [i0,i1,i2].
     * Throw a BadPlaceException if the given point is not mapped to 
     * the current place.  Primarily intended to be used by the DistArray implementation,
     * but may be useful for other data structures as well that need to associate 
     * Points in a Distribution with a dense, zero-based numbering.
     *
     * Only applies to three-dimensional distributions.
     *
     * @param i0 the given index in the first dimension
     * @param i1 the given index in the second dimension
     * @param i2 the given index in the third dimension
     * @return the storage offset assigned to [i0,i1,i2] by this distribution
     * @see #offset(Point)
     */
    public def offset(i0:Long, i1:Long, i2:Long){rank==3}:Long = offset(Point.make(i0, i1, i2));

    /**
     * Return the offset in linearized place-local storage of the point [i0,i1,i2,i3].
     * Throw a BadPlaceException if the given point is not mapped to 
     * the current place.  Primarily intended to be used by the DistArray implementation,
     * but may be useful for other data structures as well that need to associate 
     * Points in a Distribution with a dense, zero-based numbering.
     *
     * Only applies to four-dimensional distributions.
     *
     * @param i0 the given index in the first dimension
     * @param i1 the given index in the second dimension
     * @param i2 the given index in the third dimension
     * @param i3 the given index in the fourth dimension
     * @return the storage offset assigned to [i0,i1,i2,i3] by this distribution
     * @see #offset(Point)
     */
    public def offset(i0:Long, i1:Long, i2:Long, i3:Long){rank==4}:Long = offset(Point.make(i0,i1,i2,i3));

    /**
     * @return the maximum value returned by the offset method for
     *         the current place for any possible argument Point
     * @see #offset(Point)
     */
    public abstract def maxOffset():Long;


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
    public def iterator():Iterator[Point(region.rank)] = region.iterator();


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
    
     abstract public def intersection(r:Region(rank)):Dist(rank);
 */
    /**
     * Return the distribution defined over this.region-that.region,
     * and which maps every point in its region to the same place as
     * this distribution.
     *
     * @param r the given region
     * @return the difference of this distribution and r.
    
    abstract public def difference(r:Region(rank)):Dist(rank);
 */
    
    /**
     * Return the distribution defined over r, which must be
     * contained in this.region, and which maps every point in its
     * region to the same place as this distribution.
     * Functionally equivalent to {@link #intersection(Region)}.
     *
     * @param r the given region
     * @return the restriction of this distribution to r.
     */
    abstract public def restriction(r:Region(rank)):Dist(rank);


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
    public def isSubdistribution(that:Dist(rank)): Boolean {
        for (p:Place in Place.places())
            if (!that.get(p).contains(this.get(p)))
                return false;
        return true;
    }


    /**
     * Return a distribution containing only points that are
     * contained in both this distribution and that distribution,
     * and which the two distributions map to the same place.
     *
     * @param that the given distribution
     * @return the intersection of this distribution with that.
   
    abstract public def intersection(that:Dist(rank)):Dist(rank);
  */
    /**
     * Return the distribution that contains every point in this
     * distribution, except for those points which are also contained
     * in that distribution and which that distribution maps to the
     * same place as this distribution.
     *
     * @param that the given distribution
     * @return the difference of this distribution and that.
    
    abstract public def difference(that:Dist(rank)):Dist(rank);
 */
    /**
     * If this distribution and that distribution are disjoint,
     * return the distribution that contains all points p that are in
     * either distribution, and which map p to the same place as the
     * distribution that contains p.  Otherwise throws an exception.
     *
     * @param that the given distribution
     * @return the disjoint union of this distribution and that.
     
    abstract public def union(that:Dist(rank)):Dist(rank);
*/
    
    /**
     * Return a distribution whose region is the union of the regions
     * of the two distributions, and which maps each point p to
     * this(p) if p is contained in this, otherwise maps p to that(p).
     *
     * @param that the given distribution
     * @return the union of this distribution and that.
     */
   // abstract public def overlay(that:Dist(rank)):Dist(rank);

    /**
     * Return true iff that is a distribution and both distributions are defined
     * over equal regions and place groups, and map every point in said region to 
     * the same place.
     *
     * @param that the given distribution
     * @return true if that is equal to this distribution.
     */
    public def equals(thatObj:Any):Boolean {
        if (this == thatObj) return true;
        if (!(thatObj instanceof Dist)) return false;
        val that = thatObj as Dist;
	if (rank != that.rank) return false;
	if (!region.equals(that.region)) return false;
	val pg = places();
        for (p in pg) {
            if (!get(p).equals(that.get(p))) return false;
        }
        return true;
    }

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
    abstract public def restriction(p:Place):Dist(rank);

    /**
     * Return true iff this.region contains p.
     *
     * @param p the given point
     * @return true if this distribution contains p.
     */
    public def contains(p:Point):Boolean = region.contains(p);

    /**
     * Returns true iff this.region contains p and
     * the distribution maps p to here.
     * This method is intended to be overriden in subclasses
     * that can provide more efficient implementations of this operation.
     */
    public def containsLocally(p:Point):Boolean = contains(p) && this(here).contains(p);

    //
    // ops
    //

    /**
     * Restrict this distribution to the specified region.
     *
     * @param r the given region
     * @return the restriction of this distribution to r.
     */
    public operator this | (r:Region(this.rank)):Dist(this.rank)
	= restriction(r);

    /**
     * Restrict this distribution to the specified place.
     *
     * @param p the given place
     * @return the region that this distribution maps to p.
     */
    public operator this | (p:Place):Dist(rank) = restriction(p);

    /**
     * Intersect this distribution with the specified distribution.
     *
     * @param d the given distribution
     * @return the intersection of this distribution and d.
     */
   //  public operator this && (d:Dist(rank)):Dist(rank) = intersection(d);

    /**
     * Union this distribution with the specified distribution.
     *
     * @param d the given distribution
     * @return the disjoint union of this distribution and d.
    
    public operator this || (d:Dist(rank)):Dist(rank) = union(d);
 */
    
    /**
     * Subtract the specified distribution from this distribution.
     *
     * @param d the given distribution
     * @return the difference of this distribution and d.
    
    public operator this - (d:Dist(rank)):Dist(rank) = difference(d);
 */
    
    /**
     * Subtract the specified region from this distribution.
     *
     * @param r the given region
     * @return the difference of this distribution and r.
     
    public operator this - (r:Region(rank)):Dist(rank) = difference(r);
*/


    public def toString():String {
        var s:String = "Dist(";
        var first:Boolean = true;
        for (p:Place in places()) {
            if (!first) s += ",";
            s +=  "" + get(p) + "->" + p.id;
            first = false;
        }
        s += ")";
        return s;
    }

    //
    //
    //

    /**
     * Construct a distribution over the specified region.
     *
     * @param region the given region
     */
    protected def this(region:Region):Dist{self.region==region} {
        property(region);
    }

    protected static @NoInline @NoReturn def raiseBoundsError(i0:Long) {
        throw new ArrayIndexOutOfBoundsException("point (" + i0 + ") not contained in distribution");
    }    
    protected static @NoInline @NoReturn def raiseBoundsError(i0:Long, i1:Long) {
        throw new ArrayIndexOutOfBoundsException("point (" + i0 + ", "+i1+") not contained in distribution");
    }    
    protected static @NoInline @NoReturn def raiseBoundsError(i0:Long, i1:Long, i2:Long) {
        throw new ArrayIndexOutOfBoundsException("point (" + i0 + ", "+i1+", "+i2+") not contained in distribution");
    }    
    protected static @NoInline @NoReturn def raiseBoundsError(i0:Long, i1:Long, i2:Long, i3:Long) {
        throw new ArrayIndexOutOfBoundsException("point (" + i0 + ", "+i1+", "+i2+", "+i3+") not contained in distribution");
    }    
    protected static @NoInline @NoReturn def raiseBoundsError(pt:Point) {
        throw new ArrayIndexOutOfBoundsException("point " + pt + " not contained in distribution");
    }    

    protected static @NoInline @NoReturn def raisePlaceError(i0:Long) {
        throw new BadPlaceException("point (" + i0 + ") not defined at " + here);
    }    
    protected static @NoInline @NoReturn def raisePlaceError(i0:Long, i1:Long) {
        throw new BadPlaceException("point (" + i0 + ", "+i1+") not defined at " + here);
    }    
    protected static @NoInline @NoReturn def raisePlaceError(i0:Long, i1:Long, i2:Long) {
        throw new BadPlaceException("point (" + i0 + ", "+i1+", "+i2+") not defined at " + here);
    }    
    protected static @NoInline @NoReturn def raisePlaceError(i0:Long, i1:Long, i2:Long, i3:Long) {
        throw new BadPlaceException("point (" + i0 + ", "+i1+", "+i2+", "+i3+") not defined at " + here);
    }    
    protected static @NoInline @NoReturn def raisePlaceError(pt:Point) {
        throw new BadPlaceException("point " + pt + " not defined at " + here);
    }    
}
public type Dist(r:Long) = Dist{self.rank==r};
public type Dist(r:Region) = Dist{self.region==r};
public type Dist(d:Dist) = Dist{self==d};

// vim:shiftwidth=4:tabstop=4:expandtab
