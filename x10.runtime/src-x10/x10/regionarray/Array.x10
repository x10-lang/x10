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

import x10.compiler.CompilerFlags;
import x10.compiler.SuppressTransientError;
import x10.compiler.Inline;
import x10.compiler.Native;
import x10.compiler.NoInline;
import x10.compiler.NoReturn;
import x10.util.RailUtils;

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
 * such an implementation is not yet available as part of the x10.regionarray package.</p>
 * 
 * <p>The closely related class {@link DistArray} is used to define 
 * distributed arrays where the data values for the Points in the 
 * array's domain are distributed over multiple places.</p>
 * 
 * @see Point
 * @see Region
 * @see DistArray
 */
public final class Array[T] (
        /**
         * The region of this array.
         */
        region:Region{self != null},
        
        /**
         * The rank of this array.
         */
        rank:Long,//(region.rank), //{self==region.rank},
        
        /**
         * Is this array defined over a rectangular region?  
         */
        rect:Boolean,//(region.rect), //{self==region.rect},
        
        /**
         * Is this array's region zero-based?
         */
        zeroBased:Boolean,//(region.zeroBased), // {self==region.zeroBased},
        
        /**
         * Is this array's region a "rail" (one-dimensional, rect, and zero-based)?
         */
        rail:Boolean,//(region.rail), //{self==region.rail},
        
        /**
         * The number of points/data values in the array.
         * Will always be equal to region.size(), but cached here to make it available as a property.
         */
        size:Long
) {rank==region.rank,
	   rect==region.rect,
	   zeroBased==region.zeroBased,
	   rail==region.rail
   }
   implements (Point(rank))=>T,
               Iterable[Point(region.rank)] {
    
    /**
     * The backing storage for the array's elements
     */
    private val raw:Rail[T]{self!=null};
    
    /**
     * Return the Rail[T] that is providing the backing storage for the array.
     * This method is primarily intended to be used to interface with native libraries 
     * (eg BLAS, ESSL). <p>
     * 
     * This method should be used sparingly, since it may make client code dependent on the layout
     * algorithm used to map Points in the Array's Region to indicies in the backing Rail.
     * The specifics of this mapping are unspecified, although it would be reasonable to assume that
     * if the rect property is true, then every element of the backing Rail[T] actually
     * contatins a valid element of T.  Furthermore, for a multi-dimensional array it is currently true
     * (and likely to remain true) that the layout used is a row-major layout (like C, unlike Fortran)
     * and is compatible with the layout expected by platform BLAS libraries that operate on row-major
     * C arrays.
     * 
     * @return the Rail[T] that is the backing storage for the Array object.
     */
    public @Inline def raw() = raw;

    /**
     * Construct an Array over the region reg whose elements are zero-initialized.
     * 
     * @param reg The region over which to construct the array.
     */
    public @Inline def this(reg:Region) {T haszero}
    {
        property(reg as Region{self != null}, reg.rank, reg.rect, reg.zeroBased, reg.rail, reg.size());
        val crh = new LayoutHelper(reg);
        layout_min0 = crh.min0;
        layout_stride1 = crh.stride1;
        layout_min1 = crh.min1;
        layout = crh.layout;
        val n = crh.size;
        raw = new Rail[T](n);
    }   


    /*
     * Construct an Array over the region reg.
     * 
     * @param reg The region over which to construct the array.
     */
    private @Inline def this(zeroed:Boolean, reg:Region) {T haszero}
    {
    	property(reg as Region{self != null}, reg.rank, reg.rect, reg.zeroBased, reg.rail, reg.size());
    	val crh = new LayoutHelper(reg);
    	layout_min0 = crh.min0;
    	layout_stride1 = crh.stride1;
    	layout_min1 = crh.min1;
    	layout = crh.layout;
    	val n = crh.size;
    	if (zeroed) {
    	    raw = new Rail[T](n);
    	} else {
    	    raw = Unsafe.allocRailUninitialized[T](n);
    	}
    }   


    /**
     * Construct an Array over the region reg whose
     * values are initialized as specified by the init function.
     * The function will be evaluated exactly once for each point
     * in reg in an arbitrary order to 
     * compute the initial value for each array element.</p>
     * 
     * It is unspecified whether the function evaluations will
     * be done sequentially for each point by the current activity 
     * or concurrently for disjoint sets of points by one or more 
     * child activities. 
     * 
     * @param reg The region over which to construct the array.
     * @param init The function to use to initialize the array.
     */    
    public @Inline def this(reg:Region, init:(Point(reg.rank))=>T)
    {
        property(reg as Region{self != null}, reg.rank, reg.rect, reg.zeroBased, reg.rail, reg.size());
        val crh = new LayoutHelper(reg);
        layout_min0 = crh.min0;
        layout_stride1 = crh.stride1;
        layout_min1 = crh.min1;
        layout = crh.layout;
        val n = crh.size;
        val r  = Unsafe.allocRailUninitialized[T](n);
        for (p:Point(reg.rank) in reg) {
            r(offset(p))= init(p);
        }
        raw = r;
    }

    /**
     * Construct an Array over the region reg whose
     * values are initialized to be init.
     * 
     * @param reg The region over which to construct the array.
     * @param init The function to use to initialize the array.
     */    
    public @Inline def this(reg:Region, init:T)
    {
        property(reg as Region{self!=null}, reg.rank, reg.rect, reg.zeroBased, reg.rail, reg.size());
        
        val crh = new LayoutHelper(reg);
        layout_min0 = crh.min0;
        layout_stride1 = crh.stride1;
        layout_min1 = crh.min1;
        layout = crh.layout;
        val n = crh.size;
        val r  = Unsafe.allocRailUninitialized[T](n);
        if (reg.rect) {
            // Can be optimized into a simple fill of the backing Rail
            // because every element of the chunk is used by a point in the region.
            for (i in r.range) {
                r(i) = init;
            }
        } else {
            for (p:Point(reg.rank) in reg) {
                r(offset(p))= init;
            }
        }
        raw = r;
    }


    /**
     * Construct an Array view of a backing Rail
     * using the argument region to define how to map Points into
     * offsets in the backingStorage.  The size of the Rail
     * must be at least as large as the number of points in the boundingBox
     * of the given Region.
     * 
     * @param reg The region over which to define the array.
     * @param backingStore The backing storage for the array data.
     */
    public @Inline def this(reg:Region, backingStore:Rail[T])
    {
        property(reg as Region{self!=null}, reg.rank, reg.rect, reg.zeroBased, reg.rail, reg.size());
        
        val crh = new LayoutHelper(reg);
        layout_min0 = crh.min0;
        layout_stride1 = crh.stride1;
        layout_min1 = crh.min1;
        layout = crh.layout;
        val n = crh.size;
        if (n > backingStore.size) {
            throw new IllegalArgumentException("backingStore too small");
        }
        raw = backingStore as Rail[T]{self!=null};
    }
    
    /**
     * Construct an Array view of a backing Rail
     * using the region 0..(backingStore.size-1)
     * 
     * @param backingStore The backing storage for the array data.
     */
    public @Inline def this(backingStore:Rail[T])
    {
        val s = backingStore.size;
        val myReg = new RectRegion1D(s-1);
        property(myReg, 1, true, true, true, s);

	layout_min0 = layout_stride1 = layout_min1 = 0;
        layout = null;
        raw = backingStore as Rail[T]{self!=null};
    }

    
    /**
     * Construct Array over the region 0..(size-1) whose elements are zero-initialized.
     */
    public @Inline def this(size:Long) {T haszero}
    {
        val myReg = new RectRegion1D(size-1);
        property(myReg, 1, true, true, true, size);

	layout_min0 = layout_stride1 = layout_min1 = 0;
        layout = null;
        raw = new Rail[T](size);
    }
    
    
    /*
     * Construct Array over the region 0..(size-1).
     */
    private @Inline def this(zeroed:Boolean, size:Long) {T haszero}
    {
    	val myReg = new RectRegion1D(size-1);
    	property(myReg, 1, true, true, true, size);

    	layout_min0 = layout_stride1 = layout_min1 = 0;
    	layout = null;
    	if (zeroed) {
    		raw = new Rail[T](size);
    	} else {
    		raw = Unsafe.allocRailUninitialized[T](size);
    	}
    }

    /**
     * Construct Array over the region 0..(size-1) whose
     * values are initialized as specified by the init function.
     * The function will be evaluated exactly once for each point
     * in reg in an arbitrary order to 
     * compute the initial value for each array element.</p>
     * 
     * It is unspecified whether the function evaluations will
     * be done sequentially for each point by the current activity 
     * or concurrently for disjoint sets of points by one or more 
     * child activities. 
     * 
     * 
     * @param reg The region over which to construct the array.
     * @param init The function to use to initialize the array.
     */    
    public @Inline def this(size:Long, init:(Long)=>T)
    {
        val myReg = new RectRegion1D(size-1);
        property(myReg, 1, true, true, true, size);
        
        layout_min0 = layout_stride1 = layout_min1 = 0;
        layout = null;
        val r  = Unsafe.allocRailUninitialized[T](size);
        for (i in 0..(size-1)) {
            r(i)= init(i);
        }
        raw = r;
    }
    
    /**
     * Construct Array over the region 0..(size-1) whose
     * values are initialized to be init
     * 
     * @param reg The region over which to construct the array.
     * @param init The function to use to initialize the array.
     */    
    public @Inline def this(size:Long, init:T)
    {
        val myReg = new RectRegion1D(size-1);
        property(myReg, 1, true, true, true, size);
        
	layout_min0 = layout_stride1 = layout_min1 = 0;
        layout = null;
        val r  = Unsafe.allocRailUninitialized[T](size);
        for (i in 0..(size-1)) {
            r(i)= init;
        }
        raw = r;
    }
    
    
    /**
     * Construct a copy of the given Array.
     * 
     * @param init The array to copy.
     */    
    public @Inline def this(init:Array[T])
    {
        property(init.region, init.rank, init.rect, init.zeroBased, init.rail, init.size);
        layout_min0 = init.layout_min0;
        layout_stride1 = init.layout_stride1;
        layout_min1 = init.layout_min1;
        layout = init.layout;
        val r  = Unsafe.allocRailUninitialized[T](init.raw.size);
        Rail.copy(init.raw, 0, r, 0, r.size);
        raw = r;
    }
    
    /**
     * Construct a copy of the given RemoteArray.
     * 
     * @param init The remote array to copy.
     */    
 // TODO: propagate the typeArg of the target (this) to the call in ConstructorSplitterVisitor
    public @Inline def this(init:RemoteArray[T]{init.array.home==here})
    {
        this((init.array)());
    }
    
    /**
     * Return the string representation of this array.
     * 
     * @return the string representation of this array.
     */
    public def toString(): String {
        if (rail) {
            val sb = new x10.util.StringBuilder();
            sb.add("[");
            val sz = Math.min(size, 10);
            for (i in 0..(sz-1)) {
                if (i > 0) sb.add(",");
                sb.add("" + raw(i));
            }
            if (sz < size) sb.add("...(omitted " + (size - sz) + " elements)");
            sb.add("]");
            return sb.toString();
        } else {
            return "Array(" + region + ")";
        }
    }
    
    /**
     * Return an iterator over the points in the region of this array.
     * 
     * @return an iterator over the points in the region of this array.
     * @see x10.lang.Iterable[T]#iterator()
     */
    public def iterator():Iterator[Point(rank)] = region.iterator(); 
    
    
    /**
     * Return an Iterable[T] that can construct iterators 
     * over this array.<p>
     * @return an Iterable[T] over this array.
     */
    public @Inline def values():Iterable[T] {
        if (rect) {
            return new Iterable[T]() {
    	        public def iterator() = new Iterator[T]() {
    		    var cur:Long = 0;
    		    public def next() = Array.this.raw(cur++);
    		    public def hasNext() = cur < Array.this.raw.size;
    	        };
            };
        } else {
            return new Iterable[T]() {
    	        public def iterator() = new Iterator[T]() {
    		    val regIt = Array.this.iterator();
    		    public def next() = Array.this(regIt.next());
    		    public def hasNext() = regIt.hasNext();
    	        };
            };
        }
    }
    
    /**
     * Return the element of this array corresponding to the given index.
     * Only applies to one-dimensional arrays.
     * Functionally equivalent to indexing the array via a one-dimensional point.
     * 
     * @param i0 the given index in the first dimension
     * @return the element of this array corresponding to the given index.
     * @see #operator(Point)
     * @see #set(T, Long)
     */
    @Native("cuda", "(#this).raw[#i0]")
    public @Inline operator this(i0:Long){rank==1}:T {
        if (rail) {
            // Bounds checking by backing Rail is sufficient
            return raw(i0);
        } else {
            if (CompilerFlags.checkBounds() && !region.contains(i0)) {
                raiseBoundsError(i0);
            }
	    return raw(i0 - layout_min0);
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
     * @see #operator(Point)
     * @see #set(T, Long, Long)
     */
    public @Inline operator this(i0:Long, i1:Long){rank==2}:T {
        if (CompilerFlags.checkBounds() && !region.contains(i0, i1)) {
            raiseBoundsError(i0, i1);
        }
        var offset:Long  = i0 - layout_min0;
        offset = offset*layout_stride1 + i1 - layout_min1;
        return raw(offset);
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
     * @see #operator(Point)
     * @see #set(T, Long, Long, Long)
     */
    public @Inline operator this(i0:Long, i1:Long, i2:Long){rank==3}:T {
        if (CompilerFlags.checkBounds() && !region.contains(i0, i1, i2)) {
            raiseBoundsError(i0, i1, i2);
        }
        return raw(offset(i0, i1, i2));
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
     * @see #operator(Point)
     * @see #set(T, Long, Long, Long, Long)
     */
    public @Inline operator this(i0:Long, i1:Long, i2:Long, i3:Long){rank==4}:T {
        if (CompilerFlags.checkBounds() && !region.contains(i0, i1, i2, i3)) {
            raiseBoundsError(i0, i1, i2, i3);
        }
        return raw(offset(i0, i1, i2, i3));
    }
    
    /**
     * Return the element of this array corresponding to the given point.
     * The rank of the given point has to be the same as the rank of this array.
     * 
     * @param pt the given point
     * @return the element of this array corresponding to the given point.
     * @see #operator(Long)
     * @see #set(T, Point)
     */
    public @Inline operator this(pt:Point{self.rank==this.rank}):T {
        if (CompilerFlags.checkBounds() && !region.contains(pt)) {
            raiseBoundsError(pt);
        }
        return raw(offset(pt));
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
     * @see #operator(Long)
     * @see #set(T, Point)
     */
    @Native("cuda", "(#this).raw[#i0] = (#v)")
    public @Inline operator this(i0:Long)=(v:T){rank==1}:T{self==v} {
        if (rail) {
            // Bounds checking by backing Rail is sufficient
            raw(i0) = v;
        } else {
            if (CompilerFlags.checkBounds() && !region.contains(i0)) {
                raiseBoundsError(i0);
            }
            raw(i0-layout_min0) = v;
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
     * @see #operator(Long, Long)
     * @see #set(T, Point)
     */
    public @Inline operator this(i0:Long,i1:Long)=(v:T){rank==2}:T{self==v} {
        if (CompilerFlags.checkBounds() && !region.contains(i0, i1)) {
            raiseBoundsError(i0, i1);
        }
        var offset:Long  = i0 - layout_min0;
        offset = offset*layout_stride1 + i1 - layout_min1;
        raw(offset) = v;
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
     * @see #operator(Long, Long, Long)
     * @see #set(T, Point)
     */
    public @Inline operator this(i0:Long, i1:Long, i2:Long)=(v:T){rank==3}:T{self==v} {
        if (CompilerFlags.checkBounds() && !region.contains(i0, i1, i2)) {
            raiseBoundsError(i0, i1, i2);
        }
        raw(offset(i0, i1, i2)) = v;
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
     * @see #operator(Long, Long, Long, Long)
     * @see #set(T, Point)
     */
    public @Inline operator this(i0:Long, i1:Long, i2:Long, i3:Long)=(v:T){rank==4}:T{self==v} {
        if (CompilerFlags.checkBounds() && !region.contains(i0, i1, i2, i3)) {
            raiseBoundsError(i0, i1, i2, i3);
        }
        raw(offset(i0, i1, i2, i3)) = v;
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
     * @see #operator(Point)
     * @see #set(T, Long)
     */
    public @Inline operator this(p:Point{self.rank==this.rank})=(v:T):T{self==v} {
        if (CompilerFlags.checkBounds() && !region.contains(p)) {
            raiseBoundsError(p);
        }
        raw(offset(p)) = v;
        return v;
    }
    
    
    /**
     * Fill all elements of the array to contain the argument value.
     * 
     * @param v the value with which to fill the array
     */
    public def fill(v:T) {
        if (rect) {
            // In a rect array, every element in the backing raw Rail
            // is included in the array, therefore we can simply fill the Rail.
	    raw.fill(v);
        } else {
            for (p in region) {
                raw(offset(p)) = v;
            }
        }
    }


    /**
     * Fill all elements of the array with the zero value of type T 
     * @see x10.lang.Zero.get[T]()
     */
    public def clear(){T haszero} {
        raw.clear(0, raw.size);
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
    public @Inline def map[U](op:(T)=>U):Array[U](region) {
        return new Array[U](region, (p:Point(this.rank))=>op(this(p)));
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
    public @Inline def map[U](dst:Array[U](this.rank), op:(T)=>U):Array[U](this.rank){self==dst} {
        // TODO: parallelize these loops.
        if (rect) {
            // In a rect region, every element in the backing raw Rail[T]
            // is included in the array, therefore we can optimize
            // the traversal and simply map on the Rail itself.
            RailUtils.map(raw, dst.raw, op);
            return dst; 
        } else {
            for (p in region) {
                dst(p) = op(this(p));
            }
        }
        return dst;
    }
    
    
    /**
     * Map the given function onto the elements of this array for the subset
     * of points contained in the filter region such that for all points <code>p</code>
     * in <code>filter</code>,
     * <code>dst(p) == op(this(p))</code>.<p>
     * 
     * @param dst the destination array for the results of the map operation
     * @param filter the region to select the subset of points to include in the map
     * @param op the function to apply to each element of the array
     * @return dst after applying the map operation.
     * 
     * @see #reduce((U,T)=>U,U)
     * @see #scan((U,T)=>U,U)
     */
    public @Inline def map[U](dst:Array[U](this.rank), filter:Region(this.rank), op:(T)=>U):Array[U](this.rank){self==dst} {
        val fregion = region && filter;
        for (p in fregion) {
            dst(p) = op(this(p));
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
    public @Inline def map[S,U](src:Array[U](this.rank), op:(T,U)=>S):Array[S](this.region) {
        return new Array[S](region, (p:Point(this.rank))=>op(this(p), src(p)));
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
    public @Inline def map[S,U](dst:Array[S](this.rank), src:Array[U](this.rank), op:(T,U)=>S):Array[S](this.rank){self==dst} {
        // TODO: parallelize these loops.
        if (rect && this.size == src.size) {
            // In a rect array, every element in the backing raw Rail
            // is included in the array, therefore we can optimize
            // the traversal and simply map on the Rail itself.
            RailUtils.map(this.raw, src.raw as Rail[U]{self.size==this.raw.size}, dst.raw, op);
            return dst;
        } else {
            for (p in region) {
                dst(p) = op(this(p), src(p));
            }
        }
        return dst;
    }
    
    
    /**
     * Map the given function onto the elements of this array
     * and the other src array for the subset of points contained in the filter region, 
     * storing the results in the given dst array such that for all points <code>p</code> 
     * in <code>filter</code>,
     * <code>dst(p) == op(this(p), src(p))</code>.<p>
     * 
     * @param dst the destination array for the map operation
     * @param src the second source array for the map operation
     * @param filter the region to select the subset of points to include in the map
     * @param op the function to apply to each element of the array
     * @return destination after applying the map operation.
     * @see #reduce((U,T)=>U,U)
     * @see #scan((U,T)=>U,U)
     */
    public @Inline def map[S,U](dst:Array[S](this.rank), src:Array[U](this.rank), filter:Region(rank), op:(T,U)=>S):Array[S](rank){self==dst} {
        val fregion = region && filter;
        for (p in fregion) {
            dst(p) = op(this(p), src(p));
        }
        return dst;
    }
    
    
    /**
     * Reduce this array using the given function and the given initial value.
     * Each element of the array will be given as an argument to the reduction
     * function exactly once, but in an arbitrary order.  The reduction function
     * may be applied concurrently to implement a parallel reduction. 
     * 
     * @param op the reduction function
     * @param unit the given initial value
     * @return the final result of the reduction.
     * @see #map((T)=>S)
     * @see #scan((U,T)=>U,U)
     */
    public @Inline def reduce[U](op:(U,T)=>U, unit:U):U {
        if (rect) {
            // In a rect array, every element in the backing raw Rail[T]
            // is included in the array, therefore we can optimize
            // the traversal and simply reduce on the Rail itself.
            return RailUtils.reduce(raw, op, unit);
        } else {
            var accum:U = unit;
            for (p in region) {
                accum = op(accum, this(p));
            }
            return accum;
        }
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
    public @Inline def scan[U](op:(U,T)=>U, unit:U) {U haszero}
    = scan(new Array[U](false,region), op, unit);
    
    
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
    public @Inline def scan[U](dst:Array[U](region), op:(U,T)=>U, unit:U): Array[U](region) {
        var accum:U = unit;
        for (p in region) {
            accum = op(accum, this(p));
            dst(p) = accum;
        }
        return dst;
    }
    
    
    /**
     * Asynchronously copy all of the values from the source Array to the 
     * Array referenced by the destination RemoteArray.
     * The two arrays must be defined over Regions with equal size 
     * bounding boxes; if the backing storage for the two arrays is 
     * not of equal size, then an IllegalArgumentExeption will be raised.<p>
     * 
     * The activity created to do the copying will be registered with the
     * dynamically enclosing finish.<p>
     * 
     * Warning: This method is only intended to be used on Arrays containing
     *   non-Object data elements.  The elements are actually copied via an
     *   optimized DMA operation if available.  Therefore object-references will
     *   not be properly transferred. Ideally, future versions of the X10 type
     *   system would enable this restriction to be checked statically.</p>
     * 
     * @param src the source array.
     * @param dst the destination array.
     * @throws IllegalArgumentException if mismatch in size of backing storage
     *         of the two arrays.
     */
    public static def asyncCopy[T](src:Array[T], dst:RemoteArray[T]) {
        Rail.asyncCopy(src.raw, 0, dst.rawData, 0, src.raw.size);
    }
    
    
    /**
     * Asynchronously copy the specified values from the source Array to the 
     * specified portion of the Array referenced by the destination RemoteArray.
     * If accessing any point in either the specified source or the 
     * specified destination range would in an ArrayIndexOutOfBoundsError
     * being raised, then this method will throw an IllegalArgumentException.<p>
     * 
     * The activity created to do the copying will be registered with the
     * dynamically enclosing finish.<p>
     * 
     * Warning: This method is only intended to be used on Arrays containing
     *   non-Object data elements.  The elements are actually copied via an
     *   optimized DMA operation if available.  Therefore object-references will
     *   not be properly transferred. Ideally, future versions of the X10 type
     *   system would enable this restriction to be checked statically.</p>
     * 
     * @param src the source array.
     * @param srcPoint the first element in this array to be copied.  
     * @param dst the destination array.  May actually be local or remote
     * @param dstPoint the first element in the destination
     *                 array where copied data elements will be stored.
     * @param numElems the number of elements to be copied.
     * 
     * @throws IllegalArgumentException if the specified copy regions would 
     *         result in an ArrayIndexOutOfBoundsException.
     */
    public static def asyncCopy[T](src:Array[T], srcPoint:Point, 
                                   dst:RemoteArray[T], dstPoint:Point, 
                                   numElems:Long) {
        val gra = dst.array;
        val dstIndex = at (gra) gra().region.indexOf(dstPoint);
        asyncCopy(src, src.region.indexOf(srcPoint), dst, dstIndex, numElems);
    }
    
    
    /**
     * Asynchronously copy the specified values from the source Array to the 
     * specified portion of the Array referenced by the destination RemoteArray.
     * The index arguments that are used to specify the start of the source
     * and destination regions are interpreted as of they were the result
     * of calling {@link Region#indexOf} on the Point that specifies the
     * start of the copy region.  Note that for Arrays that have the 
     * <code>rail</code> property, this exactly corresponds to the index
     * that would be used to access the element via apply or set.
     * If accessing any point in either the specified source or the 
     * specified destination range would in an ArrayIndexOutOfBoundsError
     * being raised, then this method will throw an IllegalArgumentException.<p>
     * 
     * The activity created to do the copying will be registered with the
     * dynamically enclosing finish.<p>
     * 
     * Warning: This method is only intended to be used on Arrays containing
     *   non-Object data elements.  The elements are actually copied via an
     *   optimized DMA operation if available.  Therefore object-references will
     *   not be properly transferred. Ideally, future versions of the X10 type
     *   system would enable this restriction to be checked statically.</p>
     * 
     * @param src the source array.
     * @param srcIndex the index of the first element in this array
     *        to be copied.  
     * @param dst the destination array.  May actually be local or remote
     * @param dstIndex the index of the first element in the destination
     *        array where copied data elements will be stored.
     * @param numElems the number of elements to be copied.
     * 
     * @see Region#indexOf
     * 
     * @throws IllegalArgumentException if the specified copy regions would 
     *         result in an ArrayIndexOutOfBoundsException.
     */
    public static def asyncCopy[T](src:Array[T], srcIndex:Long, 
                                   dst:RemoteArray[T], dstIndex:Long, 
                                   numElems:Long) {
        Rail.asyncCopy(src.raw, srcIndex, dst.rawData, dstIndex, numElems);
    }
    
    
    /**
     * Asynchronously copy all of the values from the source Array 
     * referenced by the RemoteArray to the destination Array.
     * The two arrays must be defined over Regions with equal size 
     * bounding boxes; if the backing storage for the two arrays is 
     * not of equal size, then an IllegalArgumentExeption will be raised.<p>
     * 
     * The activity created to do the copying will be registered with the
     * dynamically enclosing finish.<p>
     * 
     * Warning: This method is only intended to be used on Arrays containing
     *   non-Object data elements.  The elements are actually copied via an
     *   optimized DMA operation if available.  Therefore object-references will
     *   not be properly transferred. Ideally, future versions of the X10 type
     *   system would enable this restriction to be checked statically.</p>
     * 
     * @param src the source array.
     * @param dst the destination array.
     * @throws IllegalArgumentException if mismatch in size of backing storage
     *         of the two arrays.
     */
    public static def asyncCopy[T](src:RemoteArray[T], dst:Array[T]) {
        Rail.asyncCopy(src.rawData, 0, dst.raw, 0, dst.raw.size);
    }
    
    
    /**
     * Asynchronously copy the specified values from the Array referenced by
     * the source RemoteArray to the specified portion of the destination Array.
     * If accessing any point in either the specified source or the 
     * specified destination range would in an ArrayIndexOutOfBoundsError
     * being raised, then this method will throw an IllegalArgumentException.<p>
     * 
     * The activity created to do the copying will be registered with the
     * dynamically enclosing finish.<p>
     * 
     * Warning: This method is only intended to be used on Arrays containing
     *   non-Object data elements.  The elements are actually copied via an
     *   optimized DMA operation if available.  Therefore object-references will
     *   not be properly transferred. Ideally, future versions of the X10 type
     *   system would enable this restriction to be checked statically.</p>
     * 
     * @param src the source array.
     * @param srcPoint the first element in this array to be copied.  
     * @param dst the destination array.  May actually be local or remote
     * @param dstPoint the first element in the destination
     *                 array where copied data elements will be stored.
     * @param numElems the number of elements to be copied.
     * 
     * @throws IllegalArgumentException if the specified copy regions would 
     *         result in an ArrayIndexOutOfBoundsException.
     */
    public static def asyncCopy[T](src:RemoteArray[T], srcPoint:Point, 
                                   dst:Array[T], dstPoint:Point, 
                                   numElems:Long) {
        val gra = src.array;
        val srcIndex = at (gra) gra().region.indexOf(srcPoint);
        asyncCopy(src, srcIndex, dst, dst.region.indexOf(dstPoint), numElems);
    }
    
    
    /**
     * Asynchronously copy the specified values from the Array referenced by
     * the source RemoteArray to the specified portion of the destination Array.
     * The index arguments that are used to specify the start of the source
     * and destination regions are interpreted as of they were the result
     * of calling {@link Region#indexOf} on the Point that specifies the
     * start of the copy region.  Note that for Arrays that have the 
     * <code>rail</code> property, this exactly corresponds to the index
     * that would be used to access the element via apply or set.
     * If accessing any point in either the specified source or the 
     * specified destination range would in an ArrayIndexOutOfBoundsError
     * being raised, then this method will throw an IllegalArgumentException.<p>
     * 
     * The activity created to do the copying will be registered with the
     * dynamically enclosing finish.<p>
     * 
     * Warning: This method is only intended to be used on Arrays containing
     *   non-Object data elements.  The elements are actually copied via an
     *   optimized DMA operation if available.  Therefore object-references will
     *   not be properly transferred. Ideally, future versions of the X10 type
     *   system would enable this restriction to be checked statically.</p>
     * 
     * @param src the source array.
     * @param srcIndex the index of the first element in this array
     *        to be copied.  
     * @param dst the destination array.  May actually be local or remote
     * @param dstIndex the index of the first element in the destination
     *        array where copied data elements will be stored.
     * @param numElems the number of elements to be copied.
     * 
     * @see Region#indexOf
     * 
     * @throws IllegalArgumentException if the specified copy regions would 
     *         result in an ArrayIndexOutOfBoundsException.
     */
    public static def asyncCopy[T](src:RemoteArray[T], srcIndex:Long, 
                                   dst:Array[T], dstIndex:Long, 
                                   numElems:Long) {
        Rail.asyncCopy(src.rawData, srcIndex, dst.raw, dstIndex, numElems);
    }
    
    
    /**
     * Copy all of the values from the source Array to the destination Array.
     * The two arrays must be defined over Regions with equal size 
     * bounding boxes; if the backing storage for the two arrays is 
     * not of equal size, then an IllegalArgumentExeption will be raised.<p>
     * 
     * @param src the source array.
     * @param dst the destination array.
     * @throws IllegalArgumentException if mismatch in size of backing storage
     *         of the two arrays.
     */
    public static def copy[T](src:Array[T], dst:Array[T]) {
        Rail.copy(src.raw, 0, dst.raw, 0, src.raw.size);
    }
    
    
    /**
     * Copy the specified values from the source Array to the 
     * specified portion of the destination Array.
     * If accessing any point in either the specified source or the 
     * specified destination range would in an ArrayIndexOutOfBoundsError
     * being raised, then this method will throw an IllegalArgumentException.<p>
     * 
     * @param src the source array.
     * @param srcPoint the first element in this array to be copied.  
     * @param dst the destination array.  May actually be local or remote
     * @param dstPoint the first element in the destination
     *                 array where copied data elements will be stored.
     * @param numElems the number of elements to be copied.
     * 
     * @throws IllegalArgumentException if the specified copy regions would 
     *         result in an ArrayIndexOutOfBoundsException.
     */
    public static def copy[T](src:Array[T], srcPoint:Point, 
                              dst:Array[T], dstPoint:Point, 
                              numElems:Long) {
        copy(src, src.region.indexOf(srcPoint), dst, dst.region.indexOf(dstPoint), numElems);
    }
    
    
    /**
     * Copy the specified values from the source Array to the 
     * specified portion of the destination Array.
     * The index arguments that are used to specify the start of the source
     * and destination regions are interpreted as of they were the result
     * of calling {@link Region#indexOf} on the Point that specifies the
     * start of the copy region.  Note that for Arrays that have the 
     * <code>rail</code> property, this exactly corresponds to the index
     * that would be used to access the element via apply or set.
     * If accessing any point in either the specified source or the 
     * specified destination range would in an ArrayIndexOutOfBoundsError
     * being raised, then this method will throw an IllegalArgumentException.<p>
     * 
     * @param src the source array.
     * @param srcIndex the index of the first element in this array
     *        to be copied.  
     * @param dst the destination array.  May actually be local or remote
     * @param dstIndex the index of the first element in the destination
     *        array where copied data elements will be stored.
     * @param numElems the number of elements to be copied.
     * 
     * @see Region#indexOf
     * 
     * @throws IllegalArgumentException if the specified copy regions would 
     *         result in an ArrayIndexOutOfBoundsException.
     */
    public static def copy[T](src:Array[T], srcIndex:Long, 
                              dst:Array[T], dstIndex:Long, 
                              numElems:Long) {
        Rail.copy(src.raw, srcIndex, dst.raw, dstIndex, numElems);
    }
    
    
    private static @NoInline @NoReturn def raiseBoundsError(i0:Long) {
        throw new ArrayIndexOutOfBoundsException("point (" + i0 + ") not contained in array");
    }    
    private static @NoInline @NoReturn def raiseBoundsError(i0:Long, i1:Long) {
        throw new ArrayIndexOutOfBoundsException("point (" + i0 + ", "+i1+") not contained in array");
    }    
    private static @NoInline @NoReturn def raiseBoundsError(i0:Long, i1:Long, i2:Long) {
        throw new ArrayIndexOutOfBoundsException("point (" + i0 + ", "+i1+", "+i2+") not contained in array");
    }    
    private static @NoInline @NoReturn def raiseBoundsError(i0:Long, i1:Long, i2:Long, i3:Long) {
        throw new ArrayIndexOutOfBoundsException("point (" + i0 + ", "+i1+", "+i2+", "+i3+") not contained in array");
    }    
    private static @NoInline @NoReturn def raiseBoundsError(pt:Point) {
        throw new ArrayIndexOutOfBoundsException("point " + pt + " not contained in array");
    }    

    /*
     * Implementation of layout calculations.
     * An inlined version of the code that used to be in RectLayout,
     * Structured this way to obtain optimal time/space performance
     * on both Managed and Native X10 for arrays of rank 1 and 2.
     */

    val layout_min0:Long;
    val layout_stride1:Long;
    val layout_min1:Long;

    /*
     * Contains stride and min information for dimensions > 2.
     * Will be null if rank<=2.
     * layout(2*(i-2)) is the stride for dimension i.
     * layout(2*(i-2)+1) is the min value for dimension i.
     */
    val layout:Rail[Long];

    // NOTE: Hand-inlined into operator this() 
    private @Inline def offset(i0:Long):Long = i0 - layout_min0;

    // NOTE: Hand-inlined into operator this() 
    private @Inline def offset(i0:Long, i1:Long):Long {
        var offset:Long  = i0 - layout_min0;
        offset = offset*layout_stride1 + i1 - layout_min1;
        return offset;
    }

    private @Inline def offset(i0:Long, i1:Long, i2:Long):Long {
        var offset:Long  = i0 - layout_min0;
        offset = offset*layout_stride1 + i1 - layout_min1;
        offset = offset*layout(0) + i2 - layout(1);
        return offset;
    }

    private @Inline def offset(i0:Long, i1:Long, i2:Long, i3:Long):Long {
        var offset:Long = i0 - layout_min0;
        offset = offset*layout_stride1 + i1 - layout_min1;
        offset = offset*layout(0) + i2 - layout(1);
        offset = offset*layout(2) + i3 - layout(3);
        return offset;
    }

    private @Inline def offset(pt:Point):Long {
        var offset:Long = pt(0n) - layout_min0;
        if (pt.rank>1) {
            offset = offset*layout_stride1 + pt(1) - layout_min1;
            for (i in 2..(pt.rank-1)) {
                offset = offset * layout(2*(i-2)) + pt(i) - layout(2*(i-2)+1);
            }
        }
        return offset;
    }

    // We could eliminate this struct at the cost of making the 
    // layout related fields of the class var instead of val.
    // There's no good way in X10 to factor out the initialization of
    // a subset of a classes val fields, so instead we use this struct to
    // bundle up the initial values of the instance fields and then copy them
    // to the actual fields in the various Array constructors.
    private static struct LayoutHelper {
        val min0:Long;
        val stride1:Long;
        val min1:Long;
        val size:Long;
        val layout:Rail[Long];

        def this(reg:Region) {
            if (reg.isEmpty()) {
                min0 = stride1 = min1 = 0;
                size = 0;
                layout = null;
            } else {
                if (reg.rank == 1) {
                    min0 = reg.min(0n);
                    stride1 = 0;
                    min1 = 0;
                    size = reg.max(0) - reg.min(0) + 1;
                    layout = null;
                } else if (reg.rank == 2) {
                    min0 = reg.min(0);
                    min1 = reg.min(1);
                    stride1 = reg.max(1) - reg.min(1) + 1;
                    size = stride1 * (reg.max(0)-reg.min(0)+1);
                    layout = null;
                } else {
                    layout = new Rail[Long](2*(reg.rank-2));
                    min0 = reg.min(0);
                    min1 = reg.min(1);
                    stride1 = reg.max(1) - reg.min(1) + 1;
                    var sz:Long = stride1 * (reg.max(0n)-reg.min(0n)+1);
                    for (i in 2..(reg.rank-1)) {
                        val stride = reg.max(i) - reg.min(i) + 1;
	                sz *= stride;
                        layout(2*(i-2)) = stride;
                        layout(2*(i-2)+1) = reg.min(i);
                    }
                    size = sz;
                }
            }
        }
    }
}

public type Array[T](r:Long) = Array[T]{self.rank==r};
public type Array[T](r:Region) = Array[T]{self.region==r};
public type Array[T](a:Array[T]) = Array[T]{self==a};

// vim:tabstop=4:shiftwidth=4:expandtab
