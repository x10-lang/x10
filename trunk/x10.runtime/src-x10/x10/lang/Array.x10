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

package x10.lang;

import x10.array.BaseArray;

/**
 * An array defines a mapping from points to data of some type T. An
 * array is defined over a particular region. Distributed arrays are
 * supported by defining an array over a distribution; the
 * distribution determines at what place each array data item is
 * stored. Arrays may be accessed using a(p) because arrays implement
 * (Point)=>T.
 * TODO Array views are supported via the restriction function.
 *
 * @param T the element type of the array
 *
 * @author bdlucas
 */
public abstract class Array[T](
    /**
     * The distribution of this array.
     */
    dist:Dist
) implements (Point(dist.region.rank))=>T,
             Iterable[Point(dist.region.rank)]
{
    //
    // properties
    //

    // region via dist
    /**
     * The region this array is defined over.
     */
    public global property region: Region(rank) = dist.region;

    /**
     * The rank of this array.
     */
    public global property rank: int = dist.rank;

    /**
     * Is this array defined over a rectangular region?
     */
    public global property rect: boolean = dist.rect;

    /**
     * Is this array's region zero-based?
     */
    public global property zeroBased: boolean = dist.zeroBased;

    // dist
    /**
     * Is this array's region a "rail" (one-dimensional contiguous zero-based)?
     */
    public global property rail: boolean = dist.rail;

    /**
     * Is this array's distribution "unique" (at most one point per place)?
     */
    public global property unique: boolean = dist.unique;

    /**
     * Is this array's distribution "constant" (all points map to the same place)?
     */
    public global property constant: boolean = dist.constant;

    /**
     * If this array's distribution is "constant", the place all points map to (or null).
     */
    public global property onePlace: Place = dist.onePlace;


    //
    // factories
    //

    /**
     * Create a mutable local array over the given region and default initial values for elements.
     *
     * @param T the element type
     * @param region the given region
     * @return a mutable array with a constant distribution mapping all points in the given region to 'here'.
     * @see #make[T](Dist)
     * @see #make[T](Region, (Point)=>T)
     */
    public static def make[T](region:Region)= BaseArray.makeVar1[T](region);

    /**
     * Create a mutable array over the given distribution and default initial values for elements.
     *
     * @param T the element type
     * @param dist the given distribution
     * @return a mutable array with the given distribution.
     * @see #make[T](Region)
     * @see #make[T](Dist, (Point)=>T)
     */
    public static def make[T](dist: Dist)= BaseArray.makeVar1[T](dist);

    /**
     * Create a mutable local array over the given region.
     * Executes the given initializer function for each element of the array.
     *
     * @param T the element type
     * @param region the given region
     * @param init the initializer function
     * @return a mutable array with a constant distribution mapping all points in the given region to 'here'.
     * @see #make[T](Region)
     * @see #make[T](Dist, (Point)=>T)
     */
    public static def make[T](region:Region, init: (Point(region.rank))=>T)= BaseArray.makeVar1[T](region, init);

    /**
     * Create a mutable array over the given distribution.
     * Executes the given initializer function for each element of the array.
     *
     * @param T the element type
     * @param dist the given distribution
     * @param init the initializer function
     * @return a mutable array with the given distribution.
     * @see #make[T](Dist)
     * @see #make[T](Region, (Point)=>T)
     */
    public static def make[T](dist: Dist, init: (Point(dist.rank))=>T)= BaseArray.makeVar1[T](dist, init);

    /**
     * Create a mutable local array with the shape and values of the given Rail.
     *
     * @param T the element type
     * @param rail the given Rail
     * @return a mutable array with a constant distribution mapping all points in the region
     *         0..rail.length-1 to 'here' and the values from the corresponding elements of rail.
     * @see #make[T](ValRail[T])
     * @see #make[T](Region, (Point)=>T)
     */
    public static def make[T](rail: Rail[T]!): Array[T]{rank==1&&rect&&zeroBased}
        = BaseArray.makeVar1[T](rail);

    /**
     * Create a mutable local array with the shape and values of the given ValRail.
     *
     * @param T the element type
     * @param rail the given ValRail
     * @return a mutable array with a constant distribution mapping all points in the region
     *         0..rail.length-1 to 'here' and the values from the corresponding elements of rail.
     * @see #make[T](Rail[T])
     * @see #make[T](Region, (Point)=>T)
     */
    public static def make[T](rail: ValRail[T]): Array[T]{rank==1&&rect&&zeroBased}
        = BaseArray.makeVar1[T](rail);

    /**
     * Create a mutable local one-dimensional zero-based array of the given size.
     * Executes the given initializer function for each element of the array.
     *
     * @param T the element type
     * @param size the size of the array
     * @param init the initializer function
     * @return a mutable array with a constant distribution mapping all points in the
     *         region 0..size-1 to 'here'.
     * @see #make[T](Rail[T])
     * @see #make[T](ValRail[T])
     * @see #make[T](Region, (Point)=>T)
     */
    public static def make[T](size: Int, init: (Point(1))=>T): Array[T](1)
        = BaseArray.makeVar1[T](0..size-1, init);


    //
    // operations
    //

    /**
     * Return the element of this array corresponding to the given point.
     * The rank of the given point has to be the same as the rank of this array.
     *
     * @param pt the given point
     * @return the element of this array corresponding to the given point.
     * @see #apply(Int)
     * @see #set(T, Point)
     */
    public abstract safe global def apply(pt: Point(rank)): T;

    /**
     * Return the element of this array corresponding to the given index.
     * Only applies to one-dimensional arrays.
     * Functionally equivalent to indexing the array via a one-dimensional point.
     *
     * @param i0 the given index in the first dimension
     * @return the element of this array corresponding to the given index.
     * @see #apply(Point)
     * @see #set(T, Int)
     */
    public abstract safe global def apply(i0: int) {rank==1}: T;

    /**
     * Return the element of this array corresponding to the given pair of indices.
     * Only applies to two-dimensional arrays.
     * Functionally equivalent to indexing the array via a two-dimensional point.
     *
     * @param i0 the given index in the first dimension
     * @param i1 the given index in the second dimension
     * @return the element of this array corresponding to the given pair of indices.
     * @see #apply(Point)
     * @see #set(T, Int, Int)
     */
    public abstract safe global def apply(i0: int, i1: int) {rank==2}: T;

    /**
     * Return the element of this array corresponding to the given triple of indices.
     * Only applies to three-dimensional arrays.
     * Functionally equivalent to indexing the array via a three-dimensional point.
     *
     * @param i0 the given index in the first dimension
     * @param i1 the given index in the second dimension
     * @param i2 the given index in the third dimension
     * @return the element of this array corresponding to the given triple of indices.
     * @see #apply(Point)
     * @see #set(T, Int, Int, Int)
     */
    public abstract safe global def apply(i0: int, i1: int, i2: int) {rank==3}: T;

    /**
     * Return the element of this array corresponding to the given quartet of indices.
     * Only applies to four-dimensional arrays.
     * Functionally equivalent to indexing the array via a four-dimensional point.
     *
     * @param i0 the given index in the first dimension
     * @param i1 the given index in the second dimension
     * @param i2 the given index in the third dimension
     * @param i3 the given index in the fourth dimension
     * @return the element of this array corresponding to the given quartet of indices.
     * @see #apply(Point)
     * @see #set(T, Int, Int, Int, Int)
     */
    public abstract safe global def apply(i0: int, i1: int, i2: int, i3:int) {rank==4}: T;


    /**
     * Set the element of this array corresponding to the given point to the given value.
     * Return the new value of the element.
     * The rank of the given point has to be the same as the rank of this array.
     *
     * @param v the given value
     * @param pt the given point
     * @return the new value of the element of this array corresponding to the given point.
     * @see #apply(Point)
     * @see #set(T, Int)
     */
    public abstract safe global def set(v:T, pt: Point(rank)): T;

    /**
     * Set the element of this array corresponding to the given index to the given value.
     * Return the new value of the element.
     * Only applies to one-dimensional arrays.
     * Functionally equivalent to setting the array via a one-dimensional point.
     *
     * @param v the given value
     * @param i0 the given index in the first dimension
     * @return the new value of the element of this array corresponding to the given index.
     * @see #apply(Int)
     * @see #set(T, Point)
     */
    public abstract safe global def set(v:T, i0: int) {rank==1}: T;

    /**
     * Set the element of this array corresponding to the given pair of indices to the given value.
     * Return the new value of the element.
     * Only applies to two-dimensional arrays.
     * Functionally equivalent to setting the array via a two-dimensional point.
     *
     * @param v the given value
     * @param i0 the given index in the first dimension
     * @param i1 the given index in the second dimension
     * @return the new value of the element of this array corresponding to the given pair of indices.
     * @see #apply(Int, Int)
     * @see #set(T, Point)
     */
    public abstract safe global def set(v:T, i0: int, i1: int) {rank==2}: T;

    /**
     * Set the element of this array corresponding to the given triple of indices to the given value.
     * Return the new value of the element.
     * Only applies to three-dimensional arrays.
     * Functionally equivalent to setting the array via a three-dimensional point.
     *
     * @param v the given value
     * @param i0 the given index in the first dimension
     * @param i1 the given index in the second dimension
     * @param i2 the given index in the third dimension
     * @return the new value of the element of this array corresponding to the given triple of indices.
     * @see #apply(Int, Int, Int)
     * @see #set(T, Point)
     */
    public abstract safe global def set(v:T, i0: int, i1: int, i2: int) {rank==3}: T;

    /**
     * Set the element of this array corresponding to the given quartet of indices to the given value.
     * Return the new value of the element.
     * Only applies to four-dimensional arrays.
     * Functionally equivalent to setting the array via a four-dimensional point.
     *
     * @param v the given value
     * @param i0 the given index in the first dimension
     * @param i1 the given index in the second dimension
     * @param i2 the given index in the third dimension
     * @param i3 the given index in the fourth dimension
     * @return the new value of the element of this array corresponding to the given quartet of indices.
     * @see #apply(Int, Int, Int, Int)
     * @see #set(T, Point)
     */
    public abstract safe global def set(v:T, i0: int, i1: int, i2: int, i3:int) {rank==4}: T;


    /**
     * Restrict this array to the given region.
     * The rank of the given region has to be the same as the rank of this array.
     * Return a copy of this array whose region is the intersection of the given region and the
     * region of this array.
     * Also available as operator '|'.
     * TODO: create an array view instead.
     *
     * @param r the given region
     * @return a copy of this array whose region is the intersection of the given region and the
     *         region of this array.
     * @see #restriction(Place)
     */
    public abstract safe global def restriction(r: Region(rank)): Array[T](rank);

    /**
     * Restrict this array to the given place.
     * Return a copy of the portion of this array that resides in the given place.
     * Also available as operator '|'.
     * TODO: create an array view instead.
     *
     * @param p the given place
     * @return a copy of the portion of this array that resides in the given place.
     * @see #restriction(Region)
     */
    public abstract safe global def restriction(p: Place): Array[T](rank);


    /**
     * Restrict this array to the given region.
     * The rank of the given region has to be the same as the rank of this array.
     * Return a copy of this array whose region is the intersection of the given region and the
     * region of this array.
     * TODO: create an array view instead.
     *
     * @param r the given region
     * @return a copy of this array whose region is the intersection of the given region and the
     *         region of this array.
     * @see #restriction(Region)
     */
    public abstract safe global operator this | (r: Region(rank)): Array[T](rank);

    /**
     * Restrict this array to the given place.
     * Return a copy of the portion of this array that resides in the given place.
     * TODO: create an array view instead.
     *
     * @param p the given place
     * @return a copy of the portion of this array that resides in the given place.
     * @see #restriction(Place)
     */
    public abstract safe global operator this | (p: Place): Array[T](rank);


    //
    // array operations
    //

    /**
     * Lift this array using the given unary operation.
     * Apply the operation pointwise to the elements of this array.
     * Return a new array with the same distribution as this array.
     * Each element of the new array is the result of applying the given operation to the
     * corresponding element of this array.
     *
     * @param op the given unary operation
     * @return a new array with the same distribution as this array.
     * @see #reduce((T,T)=>T,T)
     * @see #scan((T,T)=>T,T)
     */
    public abstract global def lift(op:(T)=>T): Array[T](dist);

    /**
     * Reduce this array using the given binary operation and the given initial value.
     * Starting with the initial value, apply the operation pointwise to the current running value
     * and each element of this array.
     * Return the final result of the reduction.
     *
     * @param op the given binary operation
     * @param unit the given initial value
     * @return the final result of the reduction.
     * @see #lift((T)=>T)
     * @see #scan((T,T)=>T,T)
     */
    public abstract global def reduce(op:(T,T)=>T, unit:T): T;

    /**
     * Scan this array using the given binary operation and the given initial value.
     * Starting with the initial value, apply the operation pointwise to the current running value
     * and each element of this array.
     * Return a new array with the same distribution as this array.
     * Each element of the new array is the result of applying the given operation to the
     * current running value and the corresponding element of this array.
     *
     * @param op the given binary operation
     * @param unit the given initial value
     * @return the final result of the reduction.
     * @see #lift((T)=>T)
     * @see #reduce((T,T)=>T,T)
     */
    public abstract global def scan(op:(T,T)=>T, unit:T): Array[T](dist);


    //
    // further generalizations TBD:
    // - extra array arg to contain result
    // - op takes current Point
    //
    // also TBD:
    //   public abstract global def lift[U](op:(T)=>U): Array[U](dist);
    //   public abstract global def lift[U,V](op:(T,U)=>V, that:Array[U](dist)): Array[V](dist);
    //   public abstract global def overlay(that:Array[T](rank)): Array[T](rank);
    //   public abstract global def update(that:Array[T](rank)): Array[T](rank);
    //


    //
    //
    //

    /**
     * Convert the given Rail to an array.
     * @deprecated Use {@link x10.lang.Array#make[T](Rail[T])} instead.
     *
     * @param T the element type
     * @param r the given Rail
     * @return a mutable array with a constant distribution mapping all points in the region 0..r.length-1
     *         to 'here' and the values from the corresponding elements of r.
     * @see #make[T](Rail[T])
     */
    public static operator [T](r: Rail[T]!): Array[T](1) = make(r);

    /**
     * Convert the given ValRail to an array.
     * @deprecated Use {@link x10.lang.Array#make[T](ValRail[T])} instead.
     *
     * @param T the element type
     * @param r the given ValRail
     * @return a mutable array with a constant distribution mapping all points in the region 0..r.length-1
     *         to 'here' and the values from the corresponding elements of r.
     * @see #make[T](ValRail[T])
     */
    public static operator [T](r: ValRail[T]): Array[T](1) = make(r);


    /**
     * Return an iterator over the points in the region of this array.
     *
     * @return an iterator over the points in the region of this array.
     * @see x10.lang.Iterable[T]#iterator()
     */
    public global def iterator(): Iterator[Point(rank)] = region.iterator() as Iterator[Point(rank)];


    //
    //
    //

    /**
     * Construct an Array with the given distribution.
     *
     * @param dist the given distribution
     */
    protected def this(dist: Dist) = property(dist);
}

// vim:tabstop=4:shiftwidth=4:expandtab
