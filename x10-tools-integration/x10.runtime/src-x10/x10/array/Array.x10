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

import x10.compiler.Header;
import x10.compiler.Inline;
import x10.compiler.Native;
import x10.compiler.NoInline;
import x10.compiler.NoReturn;
import x10.util.IndexedMemoryChunk;

/**
 * <p>An array defines a mapping from {@link Point}s to data values of some type T.
 * The Points in the Array's domain are defined by specifying a {@link Region}
 * over which the Array is defined.  Attempting to access a data value
 * at a Point not included in the Array's Region will result in a 
 * {@link ArrayIndexOutOfBoundsException} being raised.</p>
 * 
 * <p>All of the data in an Array is stored in a single Place, the 
 * Array's object home.  Data values may only be accessed at
 * the Array's home place.</p>
 *
 * <p>The Array implementation is optimized for relatively dense 
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
 * <p>The closely related class {@link DistArray} is used to define 
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
    region:Region{self != null},

    /**
     * The number of points/data values in the array.
     * Will always be equal to region.size(), but cached here to make it available as a property.
     */
     size:Int,

    /**
     * Is this array's region a "rail" (one-dimensional, rect, and zero-based)?
     * Cached in the Array for efficient acccess at runtime in optimize apply/set methods
     */
    rail:Boolean
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

    private global val raw:IndexedMemoryChunk[T];
    private global val rawLength:int;
    private val layout:RectLayout;

    @Native("java", "(!`NO_CHECKS`)")
    @Native("c++", "BOUNDS_CHECK_BOOL")
    private native def checkBounds():boolean;

    /**
     * Return the IndexedMemoryChunk[T] that is providing the backing storage for the array.
     * This method is primarily intended to be used to interface with native libraries 
     * (eg BLAS, ESSL). <p>
     * 
     * This method should be used sparingly, since it may make client code dependent on the layout
     * algorithm used to map Points in the Array's Region to indicies in the backing IndexedMemoryChunk.
     * The specifics of this mapping are unspecified, although it would be reasonable to assume that
     * if the rect property is true, then every element of the backing IndexedMemoryChunk[T] actually
     * contatins a valid element of T.  Furthermore, for a multi-dimensional array it is currently true
     * (and likely to remain true) that the layout used is a row-major layout (like C, unlike Fortran)
     * and is compatible with the layout expected by platform BLAS libraries that operate on row-major
     * C arrays.
     *
     * @return the IndexedMemoryChunk[T] that is the backing storage for the Array object.
     */
    public @Header @Inline def raw() = raw;


    /**
     * Construct an Array over the region reg whose elements are zero-initialized; 
     * in future releases of X10, this method will only be callable if sizeof(T) bytes 
     * of zeros is a valid value of type T. 
     *
     * @param reg The region over which to construct the array.
     */
    public def this(reg:Region):Array[T]{self.region==reg} {
	property(reg, reg.size(), reg.rail);

        layout = RectLayout(reg.min(), reg.max());
        val n = layout.size();
        raw = IndexedMemoryChunk.allocate[T](n, true);
        rawLength = n;
    }


    /**
     * Construct an Array over the region reg whose
     * values are initialized as specified by the init function.
     *
     * @param reg The region over which to construct the array.
     * @param init The function to use to initialize the array.
     */    
    public def this(reg:Region, init:(Point(reg.rank))=>T):Array[T]{self.region==reg} {
        property(reg, reg.size(), reg.rail);

        layout = RectLayout(reg.min(), reg.max());
        val n = layout.size();
        val r  = IndexedMemoryChunk.allocate[T](n);
	for (p:Point(reg.rank) in reg) {
            r(layout.offset(p))= init(p);
        }
        raw = r;
        rawLength = n;
    }


    /**
     * Construct an Array over the region reg whose
     * values are initialized to be init.
     *
     * @param reg The region over which to construct the array.
     * @param init The function to use to initialize the array.
     */    
    public def this(reg:Region, init:T):Array[T]{self.region==reg} {
        property(reg, reg.size(), reg.rail);

        layout = RectLayout(reg.min(), reg.max());
        val n = layout.size();
        val r  = IndexedMemoryChunk.allocate[T](n);
        if (reg.rect) {
            // Can be optimized into a simple fill of the backing IndexedMemoryChunk
            // because every element of the chunk is used by a point in the region.
	    for (var i:int = 0; i<n; i++) {
                r(i) = init;
	    }
        } else {
	    for (p:Point(reg.rank) in reg) {
                r(layout.offset(p))= init;
            }
        }
        raw = r;
        rawLength = n;
    }


    /**
     * Construct Array over the region 0..aRail.length-1 whose
     * values are initialized to the corresponding values in the 
     * argument Rail.
     *
     */    
    public def this(aRail:Rail[T]!):Array[T]{rank==1,rect,zeroBased,self.rail} {
	this(Region.makeRectangular(0, aRail.length-1), ((i):Point(1)) => aRail(i));
    }


    /**
     * Construct Array over the region 0..rail.length-1 whose
     * values are initialized to the corresponding values in the 
     * argument ValRail.
     *
     * XTENLANG-1527: rail is declared to be a ValRail[T]! as a hack around
     *       a compiler bug.  Without the !, the front-end complains that you
     *       can't refer to "T" in a static context, which is complete nonsense
     *       since this is a constructor.
     */    
    public def this(aRail:ValRail[T]!):Array[T]{rank==1,rect,zeroBased,self.rail} {
	this(Region.makeRectangular(0, aRail.length-1), ((i):Point(1)) => aRail(i));
    }


    /**
     * Construct Array over the region 0..size-1 whose elements are zero-initialized; 
     * in future releases of X10, this method will only be callable if sizeof(T) bytes 
     * of zeros is a valid value of type T. 
     */    
    public def this(size:int):Array[T]{rank==1,rect,zeroBased,self.rail} {
	this(Region.makeRectangular(0, size-1));
    }


    /**
     * Construct Array over the region 0..size-1 whose
     * values are initialized as specified by the init function.
     *
     * @param reg The region over which to construct the array.
     * @param init The function to use to initialize the array.
     */    
    public def this(size:int, init:(Point(1))=>T):Array[T]{rank==1,rect,zeroBased,self.rail} {
	this(Region.makeRectangular(0, size-1), init);
    }


    /**
     * Construct Array over the region 0..size-1 whose
     * values are initialized to be init
     *
     * @param reg The region over which to construct the array.
     * @param init The function to use to initialize the array.
     */    
    public def this(size:int, init:T):Array[T]{rank==1,rect,zeroBased,self.rail} {
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
    public safe @Header @Inline def apply(i0:int){rank==1}:T {
	if (rail) {
            if (checkBounds() && !((i0 as UInt) < (size as UInt))) {
                raiseBoundsError(i0);
            }
            return raw(i0);
        } else {
            if (checkBounds() && !region.contains(i0)) {
                raiseBoundsError(i0);
            }
            return raw(layout.offset(i0));
       }
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
    public safe @Header @Inline def apply(i0:int, i1:int){rank==2}:T {
        if (checkBounds() && !region.contains(i0, i1)) {
            raiseBoundsError(i0, i1);
        }
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
    public safe @Header @Inline def apply(i0:int, i1:int, i2:int){rank==3}:T {
        if (checkBounds() && !region.contains(i0, i1, i2)) {
            raiseBoundsError(i0, i1, i2);
        }
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
    public safe @Header @Inline def apply(i0:int, i1:int, i2:int, i3:int){rank==4}:T {
        if (checkBounds() && !region.contains(i0, i1, i2, i3)) {
            raiseBoundsError(i0, i1, i2, i3);
        }
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
    public safe @Header @Inline def apply(pt:Point{self.rank==this.rank}):T {
        if (checkBounds() && !region.contains(pt)) {
            raiseBoundsError(pt);
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
    public safe @Header @Inline def set(v:T, i0:int){rank==1}:T {
	if (rail) {
            if (checkBounds() && !((i0 as UInt) < (size as UInt))) {
                raiseBoundsError(i0);
            }
            raw(i0) = v;
        } else {
            if (checkBounds() && !region.contains(i0)) {
                raiseBoundsError(i0);
            }
            raw(layout.offset(i0)) = v;
        }
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
    public safe @Header @Inline def set(v:T, i0:int, i1:int){rank==2}:T {
        if (checkBounds() && !region.contains(i0, i1)) {
            raiseBoundsError(i0, i1);
        }
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
    public safe @Header @Inline def set(v:T, i0:int, i1:int, i2:int){rank==3}:T {
        if (checkBounds() && !region.contains(i0, i1, i2)) {
            raiseBoundsError(i0, i1, i2);
        }
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
    public safe @Header @Inline def set(v:T, i0:int, i1:int, i2:int, i3:int){rank==4}:T {
        if (checkBounds() && !region.contains(i0, i1, i2, i3)) {
            raiseBoundsError(i0, i1, i2, i3);
        }
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
    public safe @Header @Inline def set(v:T, p:Point{self.rank==this.rank}):T {
        if (checkBounds() && !region.contains(p)) {
            raiseBoundsError(p);
        }
        raw(layout.offset(p)) = v;
        return v;
    }


    /**
     * Fill all elements of the array to contain the argument value.
     *
     * @param v the value with which to fill the array
     */
    public def fill(v:T) {
	if (region.rect) {
            // In a rect region, every element in the backing raw IndexedMemoryChunk[T]
            // is included in the points of region, therfore we can simply fill
            // the IndexedMemoryChunk itself.
            for (var i:int =0; i<rawLength; i++) {
                raw(i) = v;
            }	
        } else {
            for (p in region) {
                raw(layout.offset(p)) = v;
            }
        }
    }

	
    /**
     * Map the function onto the elements of this array
     * constructing a new result array such that for all points <code>p</code>
     * in <code>this.region</code>,
     * <code>result(p) == op(this(p))</code>.<p>
     * 
     * @param op the function to apply to each element of the array
     * @return a new array with the same region as this array where <code>result(p) == op(this(p))</code>
     * 
     * @see #reduce((U,T)=>U,U)
     * @see #scan((U,T)=>U,U)
     */
    public def map[U](op:(T)=>U):Array[U](region)! {
        return new Array[U](region, (p:Point(this.rank))=>op(apply(p)));
    }


    /**
     * Map the given function onto the elements of this array
     * storing the results in the dst array such that for all points <code>p</code>
     * in <code>this.region</code>,
     * <code>dst(p) == op(this(p))</code>.<p>
     *
     * @param dst the destination array for the results of the map operation
     * @param op the function to apply to each element of the array
     * @return dst after applying the map operation.
     * 
     * @see #reduce((U,T)=>U,U)
     * @see #scan((U,T)=>U,U)
     */
    public def map[U](dst:Array[U](region)!, op:(T)=>U):Array[U](region)!{self==dst} {
	// TODO: parallelize these loops.
	if (region.rect) {
            // In a rect region, every element in the backing raw IndexedMemoryChunk[T]
            // is included in the points of region, therfore we can optimize
            // the traversal and simply map on the IndexedMemoryChunk itself.
            for (var i:int =0; i<rawLength; i++) {
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
     * Map the given function onto the elements of this array
     * and the other src array, storing the results in a new result array 
     * such that for all points <code>p</code> in <code>this.region</code>,
     * <code>result(p) == op(this(p), src(p))</code>.<p>
     *
     * @param src the other src array
     * @param op the function to apply to each element of the array
     * @return a new array with the same region as this array containing the result of the map
     * @see #reduce((U,T)=>U,U)
     * @see #scan((U,T)=>U,U)
     */
    public def map[S,U](src:Array[U](this.region)!, op:(T,U)=>S):Array[S](region)! {
        return new Array[S](region, (p:Point(this.rank))=>op(apply(p), src(p)));
    }


    /**
     * Map the given function onto the elements of this array
     * and the other src array, storing the results in the given dst array 
     * such that for all points <code>p</code> in <code>this.region</code>,
     * <code>dst(p) == op(this(p), src(p))</code>.<p>
     *
     * @param dst the destination array for the map operation
     * @param src the second source array for the map operation
     * @param op the function to apply to each element of the array
     * @return destination after applying the map operation.
     * @see #reduce((U,T)=>U,U)
     * @see #scan((U,T)=>U,U)
     */
    public def map[S,U](dst:Array[S](region)!, src:Array[U](region)!, op:(T,U)=>S):Array[S](region)! {
	// TODO: parallelize these loops.
	if (region.rect) {
            // In a rect region, every element in the backing raw IndexedMemoryChunk
            // is included in the points of region, therfore we can optimize
            // the traversal and simply map on the IndexedMemoryChunk itself.
            for (var i:int =0; i<rawLength; i++) {
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
     * Reduce this array using the given function and the given initial value.
     * Starting with the initial value, apply the operation pointwise to the current running value
     * and each element of this array.
     * Return the final result of the reduction.
     *
     * @param op the reduction function
     * @param unit the given initial value
     * @return the final result of the reduction.
     * @see #map((T)=>S)
     * @see #scan((U,T)=>U,U)
     */
    public def reduce[U](op:(U,T)=>U, unit:U):U {
        // TODO: once collecting finish is available,
        //       use it to efficiently parallelize these loops.
        var accum:U = unit;
	if (region.rect) {
            // In a rect region, every element in the backing raw IndexedMemoryChunk[T]
            // is included in the points of region, therfore we can optimize
            // the traversal and simply reduce on the IndexedMemoryChunk itself.
            for (var i:int=0; i<rawLength; i++) {
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
     * Scan this array using the function and the given initial value.
     * Starting with the initial value, apply the operation pointwise to the current running value
     * and each element of this array.
     * Return a new array with the same region as this array.
     * Each element of the new array is the result of applying the given function to the
     * current running value and the corresponding element of this array.
     *
     * @param op the scan function
     * @param unit the given initial value
     * @return a new array containing the result of the scan 
     * @see #map((T)=>U)
     * @see #reduce((U,T)=>U,U)
     */
    public def scan[U](op:(U,T)=>U, unit:U) = scan(new Array[U](region), op, unit); // TODO: private constructor to avoid useless zeroing


    /**
     * Scan this array using the given function and the given initial value.
     * Starting with the initial value, apply the operation pointwise to the current running value
     * and each element of this array storing the result in the destination array.
     * Return the destination array where each element has been set to the result of 
     * applying the given operation to the current running value and the corresponding 
     * element of this array.
     *
     * @param op the scan function
     * @param unit the given initial value
     * @return a new array containing the result of the scan 
     * @see #map((T)=>U)
     * @see #reduce((U,T)=>U,U)
     */
    public def scan[U](dst:Array[U](region)!, op:(U,T)=>U, unit:U): Array[U](region)! {
        var accum:U = unit;
        for (p in region) {
            accum = op(accum, apply(p));
            dst(p) = accum;
        }
        return dst;
    }


    /**
     * Copy all of the values from this Array to the destination Array.
     * The two arrays must be defined over equal Regions; if the Regions
     * are not equal, then an IllegalArgumentExeption will be raised.
     * If the destination Array is in a different place, then this copy
     * is performed asynchronously and the resulting activity will be 
     * registered with the dynamically enclosing finish.</p>
     *
     * Warning: This method is only intended to be used on Arrays containing
     *   non-Object data elements.  The elements are actually copied via an
     *   optimized DMA operation if available.  Therefore object-references will
     *   not be properly transferred. Ideally, future versions of the X10 type
     *   system would enable this restriction to be checked statically.</p>
     *
     * @param dst the destination array.  May be local or remote
     * @throws IllegalArgumentException if !region.equals(dst.region)
     */
    public def copyTo(dst:Array[T](this.rank)) {
	copyTo(dst,false);
    }

    /**
     * Copy all of the values from this Array to the destination Array.
     * The two arrays must be defined over equal Regions; if the Regions
     * are not equal, then an IllegalArgumentExeption will be raised.
     * If the destination Array is in a different place, then this copy
     * is performed asynchronously. Depending on the value of the 
     * uncounted parameter, the resulting activity will either be 
     * registered with the dynamically enclosing finish or
     * treated as if it was annotated with @Uncounted (not registered with any finish).</p>
     *
     * Warning: This method is only intended to be used on Arrays containing
     *   non-Object data elements.  The elements are actually copied via an
     *   optimized DMA operation if available.  Therefore object-references will
     *   not be properly transferred. Ideally, future versions of the X10 type
     *   system would enable this restriction to be checked statically.</p>
     *
     * @param dst the destination array.  May be local or remote
     * @param uncounted Should the spawned activity be treated as if it were annotated @Uncounted
     * @throws IllegalArgumentException if !region.equals(dst.region)
     */
    public def copyTo(dst:Array[T](this.rank), uncounted:boolean) {
	if (checkBounds() && !region.equals(dst.region)) throw new IllegalArgumentException("source and destination Regions are not equal");
        raw.copyTo(0, dst.home, dst.raw, 0, rawLength, uncounted);
    }


    /**
     * Copy the specified values from this Array to the destination Array.
     * The two arrays must be of Rank(1).
     * If the destination Array is in a different place, then this copy
     * is performed asynchronously and the resulting activity will be 
     * registered with the dynamically enclosing finish.</p>
     *
     * Warning: This method is only intended to be used on Arrays containing
     *   non-Object data elements.  The elements are actually copied via an
     *   optimized DMA operation if available.  Therefore object-references will
     *   not be properly transferred. Ideally, future versions of the X10 type
     *   system would enable this restriction to be checked statically.</p>
     *
     * @param srcIndex the first element to copy from in the source array
     * @param dst the destination array.  May be local or remote
     * @param dstIndex the first element to copy to in the destination array
     * @param numElems the number of elements to copy
     */
    public def copyTo(srcIndex:int, dst:Array[T](1), dstIndex:int, numElems:int){rank==1} {
        copyTo(srcIndex, dst, dstIndex, numElems, false);
    }

    /**
     * Copy the specified values from this Array to the destination Array.
     * The two arrays must be of Rank(1).
     * If the destination Array is in a different place, then this copy
     * is performed asynchronously. Depending on the value of the 
     * uncounted parameter, the resulting activity will either be 
     * registered with the dynamically enclosing finish or
     * treated as if it was annotated with @Uncounted (not registered with any finish).</p>
     *
     * Warning: This method is only intended to be used on Arrays containing
     *   non-Object data elements.  The elements are actually copied via an
     *   optimized DMA operation if available.  Therefore object-references will
     *   not be properly transferred. Ideally, future versions of the X10 type
     *   system would enable this restriction to be checked statically.</p>
     *
     * @param srcIndex the first element to copy from in the source array
     * @param dst the destination array.  May be local or remote
     * @param dstIndex the first element to copy to in the destination array
     * @param numElems the number of elements to copy
     * @param uncounted Should the spawned activity be treated as if it were annotated @Uncounted
     */
    public def copyTo(srcIndex:int, dst:Array[T](1), dstIndex:int, numElems:int, uncounted:boolean){rank==1} {
        if (checkBounds()) {
	    if (!region.contains(srcIndex)) raiseBoundsError(srcIndex);
            if (!region.contains(srcIndex+numElems-1)) raiseBoundsError(srcIndex+numElems-1);
            if (!dst.region.contains(dstIndex)) raiseBoundsError(dstIndex);
            if (!dst.region.contains(dstIndex+numElems-1)) dst.raiseBoundsError(dstIndex+numElems-1);
        }

        raw.copyTo(srcIndex-region.min()(0), dst.home, dst.raw, dstIndex-dst.region.min()(0), numElems, uncounted);
    }


    /**
     * Copy all of the values from the source array into this Array.
     * The two arrays must be defined over equal Regions; if the Regions
     * are not equal, then an IllegalArgumentExeption will be raised.
     * If the source Array is in a different place, then this copy
     * is performed asynchronously and the resulting activity will be 
     * registered with the dynamically enclosing finish.</p>
     *
     * Warning: This method is only intended to be used on Arrays containing
     *   non-Object data elements.  The elements are actually copied via an
     *   optimized DMA operation if available.  Therefore object-references will
     *   not be properly transferred. Ideally, future versions of the X10 type
     *   system would enable this restriction to be checked statically.</p>
     *
     * @param src the source array.  May be local or remote
     * @throws IllegalArgumentException if !region.equals(dst.region)
     */
    public def copyFrom(src:Array[T](this.rank)) {
        copyFrom(src, false);
    }

    /**
     * Copy all of the values from the source array into this Array.
     * The two arrays must be defined over equal Regions; if the Regions
     * are not equal, then an IllegalArgumentExeption will be raised.
     * If the source Array is in a different place, then this copy
     * is performed asynchronously. Depending on the value of the 
     * uncounted parameter, the resulting activity will either be 
     * registered with the dynamically enclosing finish or
     * treated as if it was annotated with @Uncounted (not registered with any finish).</p>
     *
     * Warning: This method is only intended to be used on Arrays containing
     *   non-Object data elements.  The elements are actually copied via an
     *   optimized DMA operation if available.  Therefore object-references will
     *   not be properly transferred. Ideally, future versions of the X10 type
     *   system would enable this restriction to be checked statically.</p>
     *
     * @param src the source array.  May be local or remote
     * @throws IllegalArgumentException if !region.equals(dst.region)
     */
    public def copyFrom(src:Array[T](this.rank), uncounted:boolean) {
	if (checkBounds() && !region.equals(src.region)) throw new IllegalArgumentException("source and destination Regions are not equal");
	raw.copyFrom(0, src.home, src.raw, 0, rawLength, uncounted);
    }


    /**
     * Copy the specified values from the source Array to this Array.
     * The two arrays must be of Rank(1).
     * If the source Array is in a different place, then this copy
     * is performed asynchronously and the resulting activity will be 
     * registered with the dynamically enclosing finish.</p>
     *
     * Warning: This method is only intended to be used on Arrays containing
     *   non-Object data elements.  The elements are actually copied via an
     *   optimized DMA operation if available.  Therefore object-references will
     *   not be properly transferred. Ideally, future versions of the X10 type
     *   system would enable this restriction to be checked statically.</p>
     *
     * @param dstIndex the first element to copy to in the destination array
     * @param src the destination array.  May be local or remote
     * @param srcIndex the first element to copy from in the source array
     * @param numElems the number of elements to copy
     */
    public def copyFrom(dstIndex:int, src:Array[T](1), srcIndex:int, numElems:int){rank==1} {
        copyFrom(dstIndex, src, srcIndex, numElems, false);
    }

    /**
     * Copy the specified values from the source Array to this Array.
     * The two arrays must be of Rank(1).
     * If the source Array is in a different place, then this copy
     * is performed asynchronously. Depending on the value of the 
     * uncounted parameter, the resulting activity will either be 
     * registered with the dynamically enclosing finish or
     * treated as if it was annotated with @Uncounted (not registered with any finish).</p>
     *
     * Warning: This method is only intended to be used on Arrays containing
     *   non-Object data elements.  The elements are actually copied via an
     *   optimized DMA operation if available.  Therefore object-references will
     *   not be properly transferred. Ideally, future versions of the X10 type
     *   system would enable this restriction to be checked statically.</p>
     *
     * @param dstIndex the first element to copy to in the destination array
     * @param src the destination array.  May be local or remote
     * @param srcIndex the first element to copy from in the source array
     * @param numElems the number of elements to copy
     */
    public def copyFrom(dstIndex:int, src:Array[T](1), srcIndex:int, numElems:int, uncounted:boolean){rank==1} {
        if (checkBounds()) {
	    if (!src.region.contains(srcIndex)) raiseBoundsError(srcIndex);
            if (!src.region.contains(srcIndex+numElems-1)) raiseBoundsError(srcIndex+numElems-1);
            if (!region.contains(dstIndex)) raiseBoundsError(dstIndex);
            if (!region.contains(dstIndex+numElems-1)) raiseBoundsError(dstIndex+numElems-1);
        }

        raw.copyFrom(dstIndex-region.min()(0), src.home, src.raw, srcIndex-src.region.min()(0), numElems, uncounted);
    }

    private static @NoInline @NoReturn def raiseBoundsError(i0:int) {
        throw new ArrayIndexOutOfBoundsException("point (" + i0 + ") not contained in array");
    }    
    private static @NoInline @NoReturn def raiseBoundsError(i0:int, i1:int) {
        throw new ArrayIndexOutOfBoundsException("point (" + i0 + ", "+i1+") not contained in array");
    }    
    private static @NoInline @NoReturn def raiseBoundsError(i0:int, i1:int, i2:int) {
        throw new ArrayIndexOutOfBoundsException("point (" + i0 + ", "+i1+", "+i2+") not contained in array");
    }    
    private static @NoInline @NoReturn def raiseBoundsError(i0:int, i1:int, i2:int, i3:int) {
        throw new ArrayIndexOutOfBoundsException("point (" + i0 + ", "+i1+", "+i2+", "+i3+") not contained in array");
    }    
    private static @NoInline @NoReturn def raiseBoundsError(pt:Point) {
        throw new ArrayIndexOutOfBoundsException("point " + pt + " not contained in array");
    }    

}

// vim:tabstop=4:shiftwidth=4:expandtab
