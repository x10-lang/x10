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
import x10.util.ClockedIndexedMemoryChunk;

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
public class ClockedArray[T] 

               extends Array[T] {



    private val cRaw:ClockedIndexedMemoryChunk[T];


    /**
     * Return the IndexedMemoryChunk[T] that is providing the backing storage for the array.
     * This method is primarily intended to be used to interface with native libraries 
     * (eg BLAS, ESSL) and to support user-defined copyTo/copyFrom idioms until the proper
     * copyTo/copyFrom methods are added to the Array API as first class operations.<p>
     * 
     * This method should be used sparingly, since it may make client code dependent on the layout
     * algorithm used to map Points in the Array's Region to indicies in the backing IndexedMemoryChunk.
     * The specifics of this mapping are unspecified, although it would be reasonable to assume that
     * if the rect property is true, then every element of the backing IndexedMemoryChunk[T] actually
     * contatins a valid element of T.  Furthermore, for a multi-dimensional array it is currently true
     * (and likely to remain true) that the layout used is compatible with the expected by 
     * platform BLAS libraries.
     *
     * @return the IndexedMemoryChunk[T] that is the backing storage for the Array object.
     */
    public @Header @Inline def cRaw() = cRaw;

    /**
     * Construct an Array over the region reg whose elements are zero-initialized; 
     * in future releases of X10, this method will only be callable if sizeof(T) bytes 
     * of zeros is a valid value of type T. 
     *
     * @param reg The region over which to construct the array.
     */

    public def this(reg:Region, c: Clock, oper: (T,T) => T, opInitial:T):ClockedArray[T]{self.region==reg} {
	super(reg, true);
        val n = layout.size();
        cRaw = ClockedIndexedMemoryChunk[T](n, c, oper, opInitial);
    }

    /* Opless */
    public def this(reg:Region, c: Clock):ClockedArray[T]{self.region==reg} {
	super(reg, true);
        val n = layout.size();
        cRaw = ClockedIndexedMemoryChunk[T](n, c);
    }

    /**
     * Construct an Array over the region reg whose
     * values are initialized as specified by the init function.
     *
     * @param reg The region over which to construct the array.
     * @param init The function to use to initialize the array.
     */    
    public def this(reg:Region, init:(Point(reg.rank))=>T, c: Clock, oper: (T,T) => T, opInitial:T):ClockedArray[T]{self.region==reg} {
	super(reg, true);
        val n = layout.size();
        val r = ClockedIndexedMemoryChunk[T](n, c, oper, opInitial);
	for (p:Point(reg.rank) in reg) {
            r.setRead(init(p), layout.offset(p));
        }
        cRaw = r;
    }

public def this(reg:Region, init:(Point(reg.rank))=>T, c: Clock):ClockedArray[T]{self.region==reg} {
	super(reg, true);
        val n = layout.size();
        val r = ClockedIndexedMemoryChunk[T](n, c);
	for (p:Point(reg.rank) in reg) {
            r.setRead(init(p), layout.offset(p));
        }
        cRaw = r;
    }


    /**
     * Construct an Array over the region reg whose
     * values are initialized to be init.
     *
     * @param reg The region over which to construct the array.
     * @param init The function to use to initialize the array.
     */    
    public def this(reg:Region, init:T, c: Clock, oper: (T,T) => T, opInitial:T):ClockedArray[T]{self.region==reg} {
	super(reg, true);
        val n = layout.size();
        val r = ClockedIndexedMemoryChunk[T](n, c, oper, opInitial);
	for (var i:int = 0; i<n; i++) {
            r(i) = init;
	}
        cRaw = r;
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
        if (checkBounds() && !region.contains(i0)) {
            raiseBoundsError(i0);
        }
        return cRaw(layout.offset(i0));
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
        return cRaw(layout.offset(i0,i1));
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
        return cRaw(layout.offset(i0, i1, i2));
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
        return cRaw(layout.offset(i0, i1, i2, i3));
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
        return cRaw(layout.offset(pt));
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
        if (checkBounds() && !region.contains(i0)) {
            raiseBoundsError(i0);
        }
        cRaw(layout.offset(i0)) = v;
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
        cRaw(layout.offset(i0,i1)) = v;
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
        cRaw(layout.offset(i0, i1, i2)) = v;
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
        cRaw(layout.offset(i0, i1, i2, i3)) = v;
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
        cRaw(layout.offset(p)) = v;
        return v;
    }


    /**
     * Fill all elements of the array to contain the argument value.
     *
     * @param v the value with which to fill the array
     */
    public def fill(v:T) {
	if (region.rect) {
            // In a rect region, every element in the backing cRaw IndexedMemoryChunk[T]
            // is included in the points of region, therfore we can simply fill
            // the IndexedMemoryChunk itself.
            for (var i:int =0; i<rawLength; i++) {
                cRaw(i) = v;
            }	
        } else {
            for (p in region) {
                cRaw(layout.offset(p)) = v;
            }
        }
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
            // In a rect region, every element in the backing cRaw IndexedMemoryChunk[T]
            // is included in the points of region, therfore we can optimize
            // the traversal and simply reduce on the IndexedMemoryChunk itself.
            for (var i:int=0; i<rawLength; i++) {
                accum = op(accum, cRaw(i));
            }          
        } else {
            for (p in region) {
                accum = op(accum, apply(p));
            }
	}
        return accum;
    }

    private @NoInline @NoReturn def raiseBoundsError(i0:int) {
        throw new ArrayIndexOutOfBoundsException("point (" + i0 + ") not contained in array");
    }    
    private @NoInline @NoReturn def raiseBoundsError(i0:int, i1:int) {
        throw new ArrayIndexOutOfBoundsException("point (" + i0 + ", "+i1+") not contained in array");
    }    
    private @NoInline @NoReturn def raiseBoundsError(i0:int, i1:int, i2:int) {
        throw new ArrayIndexOutOfBoundsException("point (" + i0 + ", "+i1+", "+i2+") not contained in array");
    }    
    private @NoInline @NoReturn def raiseBoundsError(i0:int, i1:int, i2:int, i3:int) {
        throw new ArrayIndexOutOfBoundsException("point (" + i0 + ", "+i1+", "+i2+", "+i3+") not contained in array");
    }    
    private @NoInline @NoReturn def raiseBoundsError(pt:Point(rank)) {
        throw new ArrayIndexOutOfBoundsException("point " + pt + " not contained in array");
    }    

}

// vim:tabstop=4:shiftwidth=4:expandtab
