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

import x10.compiler.Inline;

/**
 * This class represents an array whose data is in a single place,
 * which is the same place as the LocalRectArray's home.
 * <p>
 * Warning: This class is part of an initial prototyping
 *          of the array library redesign targeted for 
 *          completion in X10 2.1.  It's API is not 
 *          likely to be stable from one release to another
 *          until after X10 2.1.
 * <p>
 * Intended API.  Eventually this class will be a subtype of
 *          Array[T], but for now it isn't because that 
 *          forces a variety of API choices that are wrong
 *          for a high-performance local rectangular array.
 *          To be fixed for X10 2.1.
 *
 */
public final class LocalRectArray[T](region:RectRegion) implements (Point(region.rank))=>T,
                                                                   Iterable[Point(region.rank)] {

    private val raw:Rail[T]!;
    private val layout:RectLayout!;
    private val checkBounds:boolean;

    // TODO: XTENLANG-1188 this should be a const (static) field, but working around C++ backend bug
    private val bounds = (pt:Point):RuntimeException => new ArrayIndexOutOfBoundsException("point " + pt + " not contained in array");

    /**
     * The rank of this array.
     */
    public global property rank:int = region.rank;

    /**
     * Is this array defined over a rectangular region?
     */
    public global property rect:boolean = true;

    /**
     * Is this array's region zero-based?
     */
    public global property zeroBased:boolean = region.zeroBased;

    /**
     * TEMP: Impedance matching with the C++ backend's loop optimizer.
     *       Once the API stabilizes a little more, we should think about
     *       a better way to encode this.
     */
    private def max(x:int) = region.max(x);


    /**
     * TEMP: Impedance matching with the C++ backend's loop optimizer.
     *       Once the API stabilizes a little more, we should think about
     *       a better way to encode this.
     */
    private def min(x:int) = region.min(x);

    /**
     * Construct an uninitialized LocalRectArray over the region reg.
     *
     * @param reg The region over which to construct the array.
     */
    public def this(reg:Region{rect}):LocalRectArray[T]{self.rank==reg.rank} {
	property(reg as RectRegion);

        layout = new RectLayout(reg.min(), reg.max());
        val n = layout.size();
        raw = Rail.make[T](n);
        checkBounds = BaseArray.checkBounds;
    }


    /**
     * Construct LocalRectArray over the region reg whose
     * values are initialized as specified by the init function.
     *
     * @param reg The region over which to construct the array.
     * @param init The function to use to initialize the array.
     */    
    public def this(reg:Region{rect}, init:(Point(reg.rank))=>T):LocalRectArray[T]{self.rank==reg.rank} {
	property(reg as RectRegion);

        layout = new RectLayout(reg.min(), reg.max());
        val n = layout.size();
        val r  = Rail.make[T](n);
	for (p:Point(reg.rank) in reg) {
            r(layout.offset(p))= init(p);
        }
        raw = r;
        checkBounds = BaseArray.checkBounds;
    }


    /**
     * Return an iterator over the points in the region of this array.
     *
     * @return an iterator over the points in the region of this array.
     * @see x10.lang.Iterable[T]#iterator()
     */
    public global def iterator():Iterator[Point(rank)] = region.iterator() as Iterator[Point(rank)];


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
    public safe @Inline def apply(i0:int){this.rank==1}:T {
        if (checkBounds) region.check(bounds, i0);
        return raw(layout.offset(i0));
    }

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
    public safe @Inline def apply(i0:int, i1:int){this.rank==2}:T {
        if (checkBounds) region.check(bounds, i0, i1);
        return raw(layout.offset(i0,i1));
    }

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
    public safe @Inline def apply(i0:int, i1:int, i2:int){this.rank==3}:T {
        if (checkBounds) region.check(bounds, i0, i1, i2);
        return raw(layout.offset(i0, i1, i2));
    }

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
    public safe @Inline def apply(i0:int, i1:int, i2:int, i3:int){this.rank==4}:T {
        if (checkBounds) region.check(bounds, i0, i1, i2, i3);
        return raw(layout.offset(i0, i1, i2, i3));
    }

    /**
     * Return the element of this array corresponding to the given point.
     * The rank of the given point has to be the same as the rank of this array.
     *
     * @param pt the given point
     * @return the element of this array corresponding to the given point.
     * @see #apply(Int)
     * @see #set(T, Point)
     */
    public safe @Inline def apply(pt:Point{self.rank==this.rank}):T {
        if (checkBounds) {
            throw new UnsupportedOperationException("Haven't implemented bounds checking for general Points on LocalRectArray");
            // TODO: SHOULD BE: region.check(pt);
        }
        return raw(layout.offset(pt));
    }

 
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
    public safe @Inline def set(v:T, i0:int){this.rank==1}:T {
        if (checkBounds) region.check(bounds, i0);
        raw(layout.offset(i0)) = v;
        return v;
    }

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
    public safe @Inline def set(v:T, i0:int, i1:int){this.rank==2}:T {
        if (checkBounds) region.check(bounds, i0, i1);
        raw(layout.offset(i0,i1)) = v;
        return v;
    }

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
    public safe @Inline def set(v:T, i0:int, i1:int, i2:int){this.rank==3}:T {
        if (checkBounds) region.check(bounds, i0, i1, i2);
        raw(layout.offset(i0, i1, i2)) = v;
        return v;
    }

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
    public safe @Inline def set(v:T, i0:int, i1:int, i2:int, i3:int){this.rank==4}:T {
        if (checkBounds) region.check(bounds, i0, i1, i2, i3);
        raw(layout.offset(i0, i1, i2, i3)) = v;
        return v;
    }

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
    public safe @Inline def set(v:T, p:Point{self.rank==this.region.rank}):T {
        if (checkBounds) {
            throw new UnsupportedOperationException("Haven't implemented bounds checking for general Points on LocalRectArray");
            // TODO: SHOULD BE: region.check(p);
        }
        raw(layout.offset(p)) = v;
        return v;
    }
}
