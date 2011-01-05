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

import x10.compiler.CompilerFlags;
import x10.compiler.Header;
import x10.compiler.Inline;
import x10.compiler.Native;
import x10.compiler.NoInline;
import x10.compiler.NoReturn;
import x10.compiler.Incomplete;

import x10.util.IndexedMemoryChunk;

/**
 * <p>A distributed array (DistArray) defines a mapping from {@link Point}s to data 
 * values of some type T. The Points in the DistArray's domain are defined by
 * specifying a {@link Region} over which the Array is defined.  Attempting to access 
 * a data value at a Point not included in the Array's Region will result in a 
 * {@link ArrayIndexOutOfBoundsException} being raised. The array's distribution ({@link Dist})
 * defines a mapping for the Points in the DistArray's region to Places. This defines
 * the Place where the data element for each Point is actually stored. Attempting to access
 * a data element from any other place will result in a {@link BadPlaceException} being
 * raised.</p>
 *
 * <p>The closely related class {@link Array} is used to define 
 * non-distributed arrays where the data values for the Points in the 
 * array's domain are all stored in a single place.  Although it is possible to
 * use a DistArray to store data in only a single place by creating a "constant distribution",
 * an Array will significantly outperform a DistArray with a constant distribution.</p>
 *
 * @see Point
 * @see Region
 * @see Dist
 * @see Array
 */
public class DistArray[T] (
    /**
     * The distribution of this array.
     */
    dist:Dist
) implements (Point(dist.region.rank))=>T,
             Iterable[Point(dist.region.rank)]
{

    //
    // property methods that forward to dist and dist.region
    //

    /**
     * The region this array is defined over.
     */
    public property region: Region(rank) = dist.region;

    /**
     * The rank of this array.
     */
    public property rank: int = dist.rank;

    // Need a trivial wrapper class because PlaceLocalHandle[T] requires that T <: Object
    protected static class LocalState[T](data:IndexedMemoryChunk[T]) {
      public def this(c:IndexedMemoryChunk[T]) { property(c); }
    }
    /** The place-local backing storage for the DistArray */
    protected val localHandle:PlaceLocalHandle[LocalState[T]];
    /** Can the backing storage be obtained from cachedRaw? */
    protected transient var cachedRawValid:boolean;
    /** Cached pointer to the backing storage */
    protected transient var cachedRaw:IndexedMemoryChunk[T];
    
    /**
     * Method to acquire a pointer to the backing storage for the 
     * array's data in the current place.
     */
    protected final def raw():IndexedMemoryChunk[T] {
        if (!cachedRawValid) {
            cachedRaw = localHandle().data;
            x10.util.concurrent.Fences.storeStoreBarrier();
	    cachedRawValid = true;
        }
        return cachedRaw;
    }


    /**
     * Create a zero-initialized distributed array over the argument distribution.
     *
     * @param dist the given distribution
     * @return the newly created DistArray
     */
    public static def make[T](dist:Dist) {T haszero} = new DistArray[T](dist);

    // TODO: consider making this constructor public
    def this(dist: Dist) {T haszero} : DistArray[T]{self.dist==dist} {
        property(dist);

        val plsInit:()=>LocalState[T] = () => {
            val localRaw = IndexedMemoryChunk.allocate[T](dist.maxOffset()+1, true);
	    return new LocalState(localRaw);
        };

        localHandle = PlaceLocalHandle.make[LocalState[T]](dist, plsInit);
    }


    /**
     * Create a distributed array over the argument distribution whose elements
     * are initialized by executing the given initializer function for each 
     * element of the array in the place where the argument Point is mapped.
     *
     * @param dist the given distribution
     * @param init the initializer function
     * @return the newly created DistArray
     * @see #make[T](Dist)
     */
    public static def make[T](dist:Dist, init:(Point(dist.rank))=>T)= new DistArray[T](dist, init);

    // TODO: consider making this constructor public
    def this(dist:Dist, init:(Point(dist.rank))=>T):DistArray[T]{self.dist==dist} {
        property(dist);

        val plsInit:()=>LocalState[T] = () => {
            val localRaw = IndexedMemoryChunk.allocate[T](dist.maxOffset()+1);
            val region = dist.get(here);

            for (pt in region) {
               localRaw(dist.offset(pt)) = init(pt as Point(dist.rank));
            }

            return new LocalState(localRaw);
        };

        localHandle = PlaceLocalHandle.make[LocalState[T]](dist, plsInit);
    }



    public final operator this(pt:Point(rank)): T {
        val offset = dist.offset(pt);
        return raw()(offset);
    }


    final public operator this(i0:int){rank==1}: T {
	val offset = dist.offset(i0);
        return raw()(offset);
    }

    final public operator this(i0:int, i1:int){rank==2}: T {
        val offset = dist.offset(i0, i1);
        return raw()(offset);
    }

    final public operator this(i0:int, i1:int, i2:int){rank==3}: T {
        val offset = dist.offset(i0, i1, i2);
        return raw()(offset);
    }

    final public operator this(i0:int, i1:int, i2:int, i3:int){rank==4}: T {
	val offset = dist.offset(i0, i1, i2, i3);
        return raw()(offset);
    }


    public final operator this(pt: Point(rank))=(v: T): T {
        val offset = dist.offset(pt);
        raw()(offset) = v;
        return v;
    }

    final public operator this(i0: int)=(v: T){rank==1}: T {
        val offset = dist.offset(i0);
        raw()(offset) = v;
        return v;
    }

    final public operator this(i0: int, i1: int)=(v: T){rank==2}: T {
        val offset = dist.offset(i0, i1);
        raw()(offset) = v;
        return v;
    }

    final public operator this(i0: int, i1: int, i2: int)=(v: T){rank==3}: T {
        val offset = dist.offset(i0,i1,i2);
        raw()(offset) = v;
        return v;
    }

    final public operator this(i0: int, i1: int, i2: int, i3: int)=(v: T){rank==4}: T {
        val offset = dist.offset(i0,i1,i2,i3);
        raw()(offset) = v;
        return v;
    }


    /*
     * restriction view
     */

    public def restriction(d: Dist(rank)) {
        return new DistArray[T](this, d) as DistArray[T](rank);
    }

    def this(a: DistArray[T], d: Dist):DistArray[T]{self.dist==d} {
    	property(d);
    	localHandle = PlaceLocalHandle.make[LocalState[T]](d, ()=>a.localHandle());
    }


///// TODO: BELOW HERE IS CODE PULLED IN FROM BaseArray.  Need to reorgzinze this.

    //
    // views
    //

    public def restriction(r: Region(rank)): DistArray[T](rank) {
        return restriction(dist.restriction(r) as Dist(rank));
    }

    public def restriction(p: Place): DistArray[T](rank) {
        return restriction(dist.restriction(p) as Dist(rank));
    }


    //
    // operations
    //

    public def map(op:(T)=>T): DistArray[T](dist)
        = make[T](dist, ((p:Point)=>op(this(p as Point(rank)))));

    public def map(r:Region(rank), op:(T)=>T): DistArray[T]
        = make[T](dist | r, ((p:Point)=>op(this(p as Point(rank)))));


    public def map(src:DistArray[T](this.dist), op:(T,T)=>T):DistArray[T](dist)
        = make[T](dist, ((p:Point)=>op(this(p as Point(rank)), src(p as Point(rank)))));

    public def map(src:DistArray[T](this.dist), r:Region(rank), op:(T,T)=>T):DistArray[T](rank)
        = make[T]((dist | r) as Dist(rank), ((p:Point)=>op(this(p as Point(rank)), src(p as Point(rank)))));

    public def reduce(op:(T,T)=>T, unit:T):T {
        // scatter
        // TODO: recode using Team collective APIs to improve scalability
        // TODO: optimize scatter inner loop for locally rect regions
        val results = Rail.make[T](dist.numPlaces(), (p:Int) => unit);
	finish for (where in dist.places()) {
	    async {
                results(where.id) = at (where) {
                    var localRes:T = unit;
                    for (pt in dist(where)) {
                        localRes = op(localRes, this(pt));
                    }
                    localRes
                };
            };
        }

        // gather
        var result: T = unit;
        for (var i:int = 0; i<results.length; i++) {
            result = op(result, results(i));
        }

        return result;
    }            


    //
    // ops
    //

    public operator this | (r: Region(rank)) = restriction(r);
    public operator this | (p: Place) = restriction(p);


    public def toString(): String {
        return "DistArray(" + dist + ")";
    }


    /**
     * Return an iterator over the points in the region of this array.
     *
     * @return an iterator over the points in the region of this array.
     * @see x10.lang.Iterable[T]#iterator()
     */
    public def iterator(): Iterator[Point(rank)] = region.iterator() as Iterator[Point(rank)];
}

// vim:tabstop=4:shiftwidth=4:expandtab
