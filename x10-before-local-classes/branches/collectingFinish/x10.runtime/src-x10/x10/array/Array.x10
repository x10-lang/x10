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
import x10.compiler.Native;

/**
 * An array defines a mapping from {@link Point}s to data values of some type T.
 * The Points in the Array's domain are defined by specifying a {@link Region}
 * over which the Array is defined.  Attempting to access a data value
 * at a Point not included in the Array's Region will result in a 
 * {@link ArrayIndexOutOfBoundsException} being raised.</p>
 * 
 * All of the data in an Array is stored in a single Place, the 
 * Array's object home.  Data values may only be accessed at
 * the Array's home place.</p>
 *
 * The Array implementation is optimized for relatively dense 
 * region of points. In particular, to compute the storage required
 * to store an array instance's data, the array's Region is asked
 * for its bounding box (n-dimensional box such that all points in
 * the Region are contained within the bounding box). Backing storage 
 * is allocated for every Point in the bounding box of the array's Region.
 * Using the Array with partially defined Regions (ie, Regions that do 
 * not include every point in the Region's bounding box) is supported
 * and will operate as expected, however if the Region is sparse and large
 * there will be significant space overheads incurred for defining an Array
 * over the Region.  In future versions of X10, we may support a more 
 * space efficient implementation of Arrays over sparse regions, but 
 * such an implementation is not yet available as part of the x10.array package.</p>
 *
 * The closely related class {@link DistArray} is used to define 
 * distributed arrays where the data values for the Points in the 
 * array's domain are distributed over multiple places.</p>
 * 
 * @see Point
 * @see Region
 * @see DistArray
 */
public final class Array[T](
    /**
     * The region of this array.
     */
    region:Region
)  implements (Point(region.rank))=>T,
              Iterable[Point(region.rank)] {

    //
    // properties
    //

    /**
     * The rank of this array.
     */
    public property rank: int = region.rank;

    /**
     * Is this array defined over a rectangular region?
     */
    public property rect: boolean = region.rect;

    /**
     * Is this array's region zero-based?
     */
    public property zeroBased: boolean = region.zeroBased;

    /**
     * Is this array's region a "rail" (one-dimensional contiguous zero-based)?
     */
    public property rail: boolean = region.rail;


    private val raw:Rail[T]!;
    private val layout:RectLayout!;

    @Native("java", "true") // TODO: optimize this for Java as well.
    @Native("c++", "BOUNDS_CHECK_BOOL")
    private native def checkBounds():boolean;

    // TODO: made public for use in Rail.copyTo/copyFrom from user code until proper copyTo/copyFrom 
    //       is implemented for arrays.  This method will not be public in X10 2.0.4.
    public @Inline def raw():Rail[T]! = raw;
   

    // TODO: This is a hack around the way regions are currently defined.
    //       to try to make checking slightly more efficient.
    //       This will be fixed in 2.0.4.
    private val baseRegion:BaseRegion{self.rank==this.rank};

    // TODO: XTENLANG-1188 this should be a const (static) field, but working around C++ backend bug
    private val bounds = (pt:Point):RuntimeException => new ArrayIndexOutOfBoundsException("point " + pt + " not contained in array");


    /**
     * Construct an uninitialized Array over the region reg.
     *
     * @param reg The region over which to construct the array.
     */
    public def this(reg:Region):Array[T]{self.region==reg} {
	property(reg);

        layout = new RectLayout(reg.min(), reg.max());
        val n = layout.size();
        raw = Rail.make[T](n);
        baseRegion = reg as BaseRegion{self.rank==this.rank};
    }


    /**
     * Construct an Array over the region reg whose
     * values are initialized as specified by the init function.
     *
     * @param reg The region over which to construct the array.
     * @param init The function to use to initialize the array.
     */    
    public def this(reg:Region, init:(Point(reg.rank))=>T):Array[T]{self.region==reg} {
        property(reg);

        layout = new RectLayout(reg.min(), reg.max());
        val n = layout.size();
        val r  = Rail.make[T](n);
	for (p:Point(reg.rank) in reg) {
            r(layout.offset(p))= init(p);
        }
        raw = r;
        baseRegion = reg as BaseRegion{self.rank==this.rank};
    }

    /**
     * Construct Array over the region 0..rail.length-1 whose
     * values are initialized to the corresponding values in the 
     * argument Rail.
     *
     */    
    public def this(rail:Rail[T]!):Array[T]{self.rank==1,rect,zeroBased} {
        // TODO: could make this more efficient by optimizing rail copy.
	this(Region.makeRectangular(0, rail.length-1), ((i):Point(1)) => rail(i));
    }


    /**
     * Construct Array over the region 0..rail.length-1 whose
     * values are initialized to the corresponding values in the 
     * argument ValRail.
     *
     * TODO: rail is declared to be a ValRail[T]! as a hack around
     *       a compiler bug.  Without the !, the front-end complains that you
     *       can't refer to "T" in a static context, which is complete nonsense
     *       since this is a constructor.
     */    
    public def this(rail:ValRail[T]!):Array[T]{self.rank==1,rect,zeroBased} {
        // TODO: could make this more efficient by optimizing rail copy.
	this(Region.makeRectangular(0, rail.length-1), ((i):Point(1)) => rail(i));
    }


    /**
     * Construct Array over the region 0..size-1 whose
     * values are uninitialized
     */    
    public def this(size:int):Array[T]{self.rank==1,rect,zeroBased} {
	this(Region.makeRectangular(0, size-1));
    }


    /**
     * Construct Array over the region 0..size-1 whose
     * values are initialized as specified by the init function.
     *
     * @param reg The region over which to construct the array.
     * @param init The function to use to initialize the array.
     */    
    public def this(size:int, init:(Point(1))=>T):Array[T]{self.rank==1,rect,zeroBased} {
	this(Region.makeRectangular(0, size-1), init);
    }



    /**
     * Return an iterator over the points in the region of this array.
     *
     * @return an iterator over the points in the region of this array.
     * @see x10.lang.Iterable[T]#iterator()
     */
    public def iterator():Iterator[Point(rank)] = region.iterator() as Iterator[Point(rank)];


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
    public safe @Inline def apply(i0:int){rank==1}:T {
        if (checkBounds()) baseRegion.check(bounds, i0);
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
    public safe @Inline def apply(i0:int, i1:int){rank==2}:T {
        if (checkBounds()) baseRegion.check(bounds, i0, i1);
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
    public safe @Inline def apply(i0:int, i1:int, i2:int){rank==3}:T {
        if (checkBounds()) baseRegion.check(bounds, i0, i1, i2);
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
    public safe @Inline def apply(i0:int, i1:int, i2:int, i3:int){rank==4}:T {
        if (checkBounds()) baseRegion.check(bounds, i0, i1, i2, i3);
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
        if (checkBounds()) {
	    baseRegion.check(bounds, pt);
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
    public safe @Inline def set(v:T, i0:int){rank==1}:T {
        if (checkBounds()) baseRegion.check(bounds, i0);
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
    public safe @Inline def set(v:T, i0:int, i1:int){rank==2}:T {
        if (checkBounds()) baseRegion.check(bounds, i0, i1);
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
    public safe @Inline def set(v:T, i0:int, i1:int, i2:int){rank==3}:T {
        if (checkBounds()) baseRegion.check(bounds, i0, i1, i2);
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
    public safe @Inline def set(v:T, i0:int, i1:int, i2:int, i3:int){rank==4}:T {
        if (checkBounds()) baseRegion.check(bounds, i0, i1, i2, i3);
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
    public safe @Inline def set(v:T, p:Point{self.rank==this.rank}):T {
        if (checkBounds()) {
            baseRegion.check(bounds, p);
        }
        raw(layout.offset(p)) = v;
        return v;
    }

	
    /**
     * Lift this array using the given unary operation.
     * Apply the operation pointwise to the elements of this array.
     * Return a new array with the same region as this array.
     * Each element of the new array is the result of applying the given operation to the
     * corresponding element of this array.
     *
     * @param op the given unary operation
     * @return a new array with the same region as this array.
     * @see #reduce((T,T)=>T,T)
     * @see #scan((T,T)=>T,T)
     */
    public def lift(op:(T)=>T):Array[T](region)! {
        // TODO: parallelize this operation.
        return new Array[T](region, (p:Point(this.rank))=>op(apply(p)));
    }


    /**
     * Lift this array using the given unary operation.
     * Apply the operation pointwise to the elements of this array
     * storing the result in the destination array.
     * Each element of destination will be set to be the result of 
     * applying the given operation to the corresponding element of this array.
     *
     * @param dst the destination array for the lift operation
     * @param op the given unary operation
     * @return dst after applying the lift operation.
     * @see #reduce((T,T)=>T,T)
     * @see #scan((T,T)=>T,T)
     */
    public def lift(dst:Array[T](region)!, op:(T)=>T):Array[T](region)! {
	// TODO: parallelize these loops.
	if (region.rect) {
            // In a rect region, every element in the backing raw Rail[T]
            // is included in the points of region, therfore we can optimize
            // the traversal and simply lift on the Rail itself.
            for (var i:int =0; i<raw.length; i++) {
                dst.raw(i) = op(raw(i));
            }	
        } else {
            for (p in region) {
                dst(p) = op(apply(p));
            }
        }
        return dst;
    }


    /**
     * Lift this array and a second source array using the given binary operation.
     * Apply the operation pointwise to the elements of this array and the
     * argument src array, returning a new array with the same region as this array.
     * Each element of the new array is the result of applying the given operation to the
     * corresponding element of this array and src.
     *
     * @param op the given binary operation
     * @param src the other src array
     * @return a new array with the same region as this array containing the result of the lift.
     * @see #reduce((T,T)=>T,T)
     * @see #scan((T,T)=>T,T)
     */
    public def lift(src:Array[T](this.region)!, op:(T,T)=>T):Array[T](region)! {
        // TODO: parallelize this operation.
        return new Array[T](region, (p:Point(this.rank))=>op(apply(p), src(p)));
    }


    /**
     * Lift this array and a second source array using the given binary operation.
     * Apply the operation pointwise to the elements of this array and the 
     * other array storing the result in the destination array.
     * Each element of destination will be set to be the result of 
     * applying the given operation to the corresponding element of this array
     * and the src array.
     *
     * @param dst the destination array for the lift operation
     * @param src the second source array for the lift operation
     * @param op the given binary operation
     * @return destination after applying the lift operation.
     * @see #reduce((T,T)=>T,T)
     * @see #scan((T,T)=>T,T)
     */
    public def lift(dst:Array[T](region)!, src:Array[T](region)!, op:(T,T)=>T):Array[T](region)! {
	// TODO: parallelize these loops.
	if (region.rect) {
            // In a rect region, every element in the backing raw Rail[T]
            // is included in the points of region, therfore we can optimize
            // the traversal and simply lift on the Rail itself.
            for (var i:int =0; i<raw.length; i++) {
                dst.raw(i) = op(raw(i), src.raw(i));
            }	
        } else {
            for (p in region) {
                dst(p) = op(apply(p), src(p));
            }
        }
        return dst;
    }


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
    public def reduce(op:(T,T)=>T, unit:T):T {
        // TODO: once collecting finish is available,
        //       use it to efficiently parallelize these loops.
        var accum:T = unit;
	if (region.rect) {
            // In a rect region, every element in the backing raw Rail[T]
            // is included in the points of region, therfore we can optimize
            // the traversal and simply reduce on the Rail itself.
            for (var i:int=0; i<raw.length; i++) {
                accum = op(accum, raw(i));
            }          
        } else {
            for (p in region) {
                accum = op(accum, apply(p));
            }
	}
        return accum;
    }

    /**
     * Scan this array using the given binary operation and the given initial value.
     * Starting with the initial value, apply the operation pointwise to the current running value
     * and each element of this array.
     * Return a new array with the same region as this array.
     * Each element of the new array is the result of applying the given operation to the
     * current running value and the corresponding element of this array.
     *
     * @param op the given binary operation
     * @param unit the given initial value
     * @return a new array containing the result of the scan 
     * @see #lift((T)=>T)
     * @see #reduce((T,T)=>T,T)
     */
    public def scan(op:(T,T)=>T, unit:T): Array[T](region)! = scan(new Array[T](region), op, unit); // TODO: private constructor to avoid useless zeroing


    /**
     * Scan this array using the given binary operation and the given initial value.
     * Starting with the initial value, apply the operation pointwise to the current running value
     * and each element of this array storing the result in the destination array.
     * Return the destination array where each element has been set to the result of 
     * applying the given operation to the current running value and the corresponding 
     * element of this array.
     *
     * @param op the given binary operation
     * @param unit the given initial value
     * @return a new array containing the result of the scan 
     * @see #lift((T)=>T)
     * @see #reduce((T,T)=>T,T)
     */
    public def scan(dst:Array[T](region)!, op:(T,T)=>T, unit:T): Array[T](region)! {
        var accum:T = unit;
        for (p in region) {
            accum = op(accum, apply(p));
            dst(p) = accum;
        }
        return dst;
    }
}

// vim:tabstop=4:shiftwidth=4:expandtab
