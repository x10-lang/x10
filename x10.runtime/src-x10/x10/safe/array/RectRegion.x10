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

package safe.array;

import x10.compiler.CompilerFlags;
import x10.compiler.Inline;

import safe.lang.Int;
import safe.lang.Boolean; 
import safe.lang.Rail; 
import safe.array.Point;
import safe.array.Region; 
import safe.array.Array; 
import safe.lang.Math; 


/**
 * A RectRegion is a finite dense rectangular region with a specified rank.
 * This class implements a specialization of PolyRegion.
 */
final class RectRegion extends Region{rect == Boolean.TRUE()} {

    private val size:Int;           /* Will be < 0 iff the true size of the region is not expressible as an Int */
    private val mins:Array[Int](1); /* will be null if rank<5 */
    private val maxs:Array[Int](1); /* will be null if rank<5 */

    private val min0:Int;
    private val min1:Int;
    private val min2:Int;
    private val min3:Int;
    private val max0:Int;
    private val max1:Int;
    private val max2:Int;
    private val max3:Int;

    // cached polyRep representation; space inefficient, so don't want to serialize it.
    transient protected var polyRep:Region(rank)=null;

    private static def allZeros(a:Array[Int](1)) {
       for ([i] in a) if (a(i) != Int.asInt(0)) return false;
       return true;
    }

    /**
     * Create a rectangular region containing all points p such that min <= p and p <= max.
     */
//     def this(minArg:Array[Int](1), maxArg:Array[Int](1)):RectRegion{self.rank==minArg.size} {
//         super(minArg.size, true, allZeros(minArg));
// 
// 	if (minArg.size != maxArg.size) throw new IllegalArgumentException("size of min and max args are not equal");
// 
//         var s:long = 1;
//         for (var i:Int = 0; i<minArg.size; i++) {
// 	    var rs:long = (maxArg(i) as Long) - (minArg(i)) as Long + 1;
//             if (rs < 0) rs = 0;
//             s *= rs;
//         }
//         if (s > Int.MAX_VALUE as Long) {
//             size = -1; // encode overflow
//         } else {
//             size = s as Int;
//         }
// 
//         if (minArg.size>0) {
//             min0 = minArg(0);
//             max0 = maxArg(0);
//         } else {
//             min0 = max0 = 0;
//         }
// 
//         if (minArg.size>1) {
//             min1 = minArg(1);
//             max1 = maxArg(1);
//         } else {
//             min1 = max1 = 0;
//         }
// 
//         if (minArg.size>2) {
//             min2 = minArg(2);
//             max2 = maxArg(2);
//         } else {
//             min2 = max2 = 0;
//         }
// 
//         if (minArg.size>3) {
//             min3 = minArg(3);
//             max3 = maxArg(3);
//         } else {
//             min3 = max3 = 0;
//         }
// 	
// 	if (minArg.size>4) {
// 	  mins = minArg;
// 	  maxs = maxArg;
//         } else {
// 	  mins = null;
//           maxs = null;
//         }
//     }

    /**
     * Create a 1-dim region min..max.
     */
    @Inline def this(min:Int, max:Int):RectRegion{self.rank==Int.asInt(1),
    											  self.rect==Boolean.TRUE()
    											  } {
        super(1, Boolean.TRUE(), min==Int.asInt(0));
    	size = max - min + Int.asInt(1);
        min0 = min;
        max0 = max;

        min1 = min2 = min3 = Int.asInt(0);
        max1 = max2 = max3 = Int.asInt(0);
        mins = null;
        maxs = null;
    }

    public def size():Int {
      if ((size < Int.asInt(0))()) 
    	  throw new UnboundedRegionException("size exceeds capacity of Int");
      return size;
    }

    public def isConvex() = Boolean.TRUE();

    public def isEmpty() = Int.sequals(size, Int.asInt(0));

    public def indexOf(pt:Point) {
	if (!contains(pt)()) return Int.asInt(-1);
        var offset: Int = pt(Int.asInt(0)) - min(Int.asInt(0));
        for (var i:Int=Int.asInt(1); (i<rank)(); i = i + 1) {
            val min_i = min(i);
            val max_i = max(i);
            val pt_i = pt(i);
            val delta_i = max_i - min_i + Int.asInt(1);
            offset = offset*delta_i + pt_i - min_i;
        }
        return offset;
    }

    public def indexOf(i0:Int):Int {
        if (Boolean.asX10Boolean(zeroBased)) {
	    if (rank != Int.asInt(1) || !containsInternal(i0)()) return Int.asInt(-1);
            return i0;
        } else { 
	    if (rank != Int.asInt(1) || !containsInternal(i0)()) return Int.asInt(-1);
            return i0 - min0;
        }
    }

    public def indexOf(i0:Int, i1:Int):Int {
        if (Boolean.asX10Boolean(zeroBased)) {
	    if (rank != Int.asInt(2) || !containsInternal(i0,i1)()) return Int.asInt(-1);
            var offset:Int = i0;
            offset = offset*(max1 + Int.asInt(1)) + i1;
            return offset;
        } else { 
	    if (rank != Int.asInt(2) || !containsInternal(i0,i1)()) return Int.asInt(-1);
            var offset:Int = i0 - min0;
            offset = offset*(max1 - min1 + Int.asInt(1)) + i1 - min1;
            return offset;
        }
    }

    public def indexOf(i0:Int, i1:Int, i2:Int):Int {
        if ((zeroBased)()) {
	    if (rank != Int.asInt(3) || !containsInternal(i0,i1,i2)()) return Int.asInt(-1);
            var offset:Int = i0;
            offset = offset*(max1 + Int.asInt(1)) + i1;
            offset = offset*(max2 + Int.asInt(1)) + i2;
            return offset;
        } else { 
	    if (rank != Int.asInt(3) || !containsInternal(i0,i1,i2)()) return Int.asInt(-1);
            var offset:Int = i0 - min0;
            offset = offset*(max1 - min1 + Int.asInt(1)) + i1 - min1;
            offset = offset*(max2 - min2 + Int.asInt(1)) + i2 - min2;
            return offset;
        }
    }

    public def indexOf(i0:Int, i1:Int, i2:Int, i3:Int):Int {
        if (Boolean.asX10Boolean(zeroBased)) {
	    if (rank != Int.asInt(4) || !containsInternal(i0,i1,i2,i3)()) return Int.asInt(-1);
            var offset:Int = i0;
            offset = offset*(max1 + Int.asInt(1)) + i1;
            offset = offset*(max2 + Int.asInt(1)) + i2;
            offset = offset*(max3 + Int.asInt(1)) + i3;
            return offset;
        } else { 
	    if (rank != Int.asInt(4) || !containsInternal(i0,i1,i2,i3)()) return Int.asInt(-1);
            var offset:Int = i0 - min0;
            offset = offset*(max1 - min1 + Int.asInt(1)) + i1 - min1;
            offset = offset*(max2 - min2 + Int.asInt(1)) + i2 - min2;
            offset = offset*(max3 - min3 + Int.asInt(1)) + i3 - min3;
            return offset;
        }
    }


    public def min(i:Int):Int {
        if ((i<Int.asInt(0))() || (i>=rank)()) 
        	throw new ArrayIndexOutOfBoundsException("min: "+i+" is not a valid rank for "+this);
	    if (i == Int.asInt(0)) return min0;
	    if (i == Int.asInt(1)) return min1;
	    if (i == Int.asInt(2)) return min2;
	    if (i == Int.asInt(3)) return min3;
	    
	    throw new IllegalArgumentException("Dimension too high"); 
    }

    public def max(i:Int):Int {
        if ((i<Int.asInt(0) || i>=rank)()) 
        	throw new ArrayIndexOutOfBoundsException("max: "+i+" is not a valid rank for "+this);
	
	    if (i == Int.asInt(0)) return max0;
	    if (i == Int.asInt(1)) return max1;
	    if (i == Int.asInt(2)) return max2;
	    if (i == Int.asInt(3)) return max3;
		
	    throw new IllegalArgumentException("Dimension too high");
    }


    //
    // region operations
    //

    protected def computeBoundingBox():Region(rank)=this; 
    
    public def min():(Int)=>Int = (i:Int)=> min(i);
    public def max():(Int)=>Int = (i:Int)=> max(i);

    public def contains(that:Region(rank)): Boolean {
       if (that instanceof RectRegion) {
           val thatMin = (that as RectRegion).min();
           val thatMax = (that as RectRegion).max();
           for (var i:Int =Int.asInt(0); (i<rank)(); i=i+1) {
               if ((min(i) > thatMin(i))()) return Boolean.FALSE();
               if ((max(i) < thatMax(i))()) return Boolean.FALSE();
           }
           return true;
       } else if (that instanceof RectRegion1D) {
           return min(Int.asInt(0)) <= that.min(Int.asInt(0)) && max(Int.asInt(0)) >= that.max(Int.asInt(0));
       } else {
           return this.contains(that.computeBoundingBox());
       }
    }

    public def contains(p:Point):Boolean {
        if (p.rank != rank) return false;
        val i = p.rank - Int.asInt(1);
        
        if (i == Int.asInt(3)) { 
        	val tmp = p(Int.asInt(3)); 
        	if ((tmp<min3 || tmp>max3)()) return false; 
        }
        if (i == Int.asInt(2)) { 
        	val tmp = p(Int.asInt(2)); 
        	if ((tmp<min2 || tmp>max2)()) return false; }
        if (i == Int.asInt(1)) { 
        	val tmp = p(Int.asInt(1)); 
        	if ((tmp<min1 || tmp>max1)()) return false; }
        if (i == Int.asInt(0)) { 
        	val tmp = p(Int.asInt(0)); 
        	if ((tmp<min0 || tmp>max0)()) return false; }

		return true;
    }

    public def contains(i0:Int){rank==Int.asInt(1)}:Boolean = containsInternal(i0);
    public def contains(i0:Int, i1:Int){rank==Int.asInt(2)}:Boolean = containsInternal(i0,i1);
    public def contains(i0:Int, i1:Int, i2:Int){rank==Int.asInt(3)}:Boolean = containsInternal(i0,i1,i2);
    public def contains(i0:Int, i1:Int, i2:Int, i3:Int){rank==Int.asInt(4)}:Boolean = containsInternal(i0,i1,i2,i3);

    private def containsInternal(i0:Int):Boolean {
        return i0>=min0 && i0<=max0;
    }

    private def containsInternal(i0:Int, i1:Int):Boolean { 
        if (CompilerFlags.useUnsigned() && Boolean.asX10Boolean(zeroBased)) {
            return ((i0() as UInt) <= (max0() as UInt)) &&
                   ((i1() as UInt) <= (max1() as UInt));
        } else {
            return i0>=min0 && i0<=max0 && 
                   i1>=min1 && i1<=max1;
        }
    }

    private def containsInternal(i0:Int, i1:Int, i2:Int):Boolean {
        if (CompilerFlags.useUnsigned() && Boolean.asX10Boolean(zeroBased)) {
            return ((i0() as UInt) <= (max0() as UInt)) &&
                   ((i1() as UInt) <= (max1() as UInt)) &&
                   ((i2() as UInt) <= (max2() as UInt));
        } else {
            return i0>=min0 && i0<=max0 && 
                   i1>=min1 && i1<=max1 && 
                   i2>=min2 && i2<=max2;
        }
    }

    private def containsInternal(i0:Int, i1:Int, i2:Int, i3:Int):Boolean {
        if (CompilerFlags.useUnsigned() && Boolean.asX10Boolean(zeroBased)) {
            return ((i0() as UInt) <= (max0() as UInt)) &&
                   ((i1() as UInt) <= (max1() as UInt)) &&
                   ((i2() as UInt) <= (max2() as UInt)) &&
                   ((i3() as UInt) <= (max3() as UInt));
        } else {
            return i0>=min0 && i0<=max0 && 
                   i1>=min1 && i1<=max1 && 
                   i2>=min2 && i2<=max2 && 
                   i3>=min3 && i3<=max3;
        }
    }


    /**
     * Return a PolyRegion with the same set of points as this region. This permits
     * general algorithms for Intersection, restriction etc to be applied to RectRegion's.
     */
    // public def toPolyRegion() {
    // 	if (polyRep==null) {
    //         polyRep = Region.makeRectangularPoly(new Array[Int](rank, min()), new Array[Int](rank, max()));
    // 	}
    // 	return polyRep;
    // }
    
    
    // public def intersection(that:Region(rank)):Region(rank) {
    //     if (that.isEmpty()()) {
	   //     return that;
    //     } else if (that instanceof FullRegion) {
    //         return this;
    //     } else if (that instanceof RectRegion) {
    //         val thatMin = (that as RectRegion).min();
    //         val thatMax = (that as RectRegion).max();
	   //  val newMin = new Array[Int](rank, (i:Int)=>Math.max(min(i), thatMin(i)));
	   //  val newMax = new Array[Int](rank, (i:Int)=>Math.min(max(i), thatMax(i)));
	   //  //for ([i] in Int.asInt(0)..(newMin.size-1)) {
	   //  for (var i : Int = 0; (i < newMin.size)(); i = i + 1) {
    //             if ((newMax(i)<newMin(i))()) return Region.makeEmpty(rank);
    //         }
    //         return new RectRegion(newMin, newMax) as Region(rank);
    //     } else if (that instanceof RectRegion1D) {
    //         val newMin = Math.max(min(Int.asInt(0)), that.min(Int.asInt(0)));
    //         val newMax = Math.min(max(Int.asInt(0)), that.max(Int.asInt(0)));
    //         if ((newMax < newMin)()) return Region.makeEmpty(Int.asInt(1)) as Region(rank);
    //         return new RectRegion1D(newMin, newMax) as Region(rank);
    //     } else {
    //     	throw new IllegalArgumentException("Non-rectangular regions not supported yet."); 
    //         // Use the general representation.
    //         //return (toPolyRegion() as Region(rank)).Intersection(that);
    //     }
    // }
//     
// 
//     
//     public def product(that:Region):Region{self != null} /*self.rank==this.rank+that.rank*/{
//         if (that.isEmpty()) {
//             return Region.makeEmpty(rank + that.rank);
//         } else if (that instanceof RectRegion) {
//             val thatMin = (that as RectRegion).min();
//             val thatMax = (that as RectRegion).max();
//             val k = rank+that.rank;
//             val newMin = new Array[Int](k, (i:Int)=>i<rank?min(i):thatMin(i-rank));
//             val newMax = new Array[Int](k, (i:Int)=>i<rank?max(i):thatMax(i-rank));
//             return new RectRegion(newMin, newMax);
//         } else if (that instanceof RectRegion1D) {
//             val thatMin = that.min(Int.asInt(0));
//             val thatMax = that.max(Int.asInt(0));
//             val k = rank+Int.asInt(1);
//             val newMin = new Array[Int](k, (i:Int)=>i<rank?min(i):thatMin);
//             val newMax = new Array[Int](k, (i:Int)=>i<rank?max(i):thatMax);
//             return new RectRegion(newMin, newMax);
//         } else if (that instanceof FullRegion) {
//        	    val k = rank+that.rank;
//             val newMin = new Array[Int](k, (i:Int)=>i<rank?min(i):Int.MIN_VALUE);
//             val newMax = new Array[Int](k, (i:Int)=>i<rank?max(i):Int.MAX_VALUE);
// 	    return new RectRegion(newMin,newMax);
//         } else {
// 	   return (toPolyRegion() as Region(rank)).product(that);
//         }
//     }
// 
//     public def translate(v: Point(rank)):Region(rank){self.rect} {
//         val newMin = new Array[Int](rank, (i:Int)=>min(i)+v(i));
//         val newMax = new Array[Int](rank, (i:Int)=>max(i)+v(i));
//         return new RectRegion(newMin, newMax) as Region(rank){self.rect};
//     }
// 
//     public def projection(axis:Int):Region(Int.asInt(1)){self.rect} {
//         return new RectRegion(min(axis), max(axis));
//     }
// 
//     public def eliminate(axis:Int):Region{self.rect} /*(rankInt.asInt(-1))*/ {
//     	val k = rankInt.asInt(-1);
//         val newMin = new Array[Int](k, (i:Int)=>i<axis?min(i):min(i+Int.asInt(1)));
//         val newMax = new Array[Int](k, (i:Int)=>i<axis?max(i):max(i+Int.asInt(1)));
//         return new RectRegion(newMin, newMax);
//     }    


    private static class RRIterator(myRank:Int) implements Iterator[Point(myRank)] {
        val min:(Int)=>Int;
        val max:(Int)=>Int;
        var done:Boolean;
        val cur:Rail[Int]{self.size==myRank};

        def this(rr:RectRegion):RRIterator{self.myRank==rr.rank} {
            property(rr.rank);
            min = rr.min();
            max = rr.max();
            done = rr.size == Int.asInt(0);
            cur = new Rail[Int](myRank, min);
        }        

        public def hasNext() = !done();

        public def next():Point(myRank) {
        	// TODO: why can't it infer the proper type without the as?
            val ans = Point.make(cur) as Point {self.rank == myRank};
            if ((cur(myRank()-1)<max(myRank-1))()) {
                cur((myRank-1)()) = cur((myRank-1)()) + 1;
            } else {
	        if (myRank() == 1) {
	            done = true;
                } else {
	            // reset lowest rank to min and ripple carry
                    cur((myRank-1)()) = min(myRank-1);
	            cur(myRank()-2) = cur(myRank()-2) + 1;
	            var carryRank:x10.lang.Int = myRank()-2;
	            while (carryRank>0 && (cur(carryRank) > max(carryRank))()) {
                        cur(carryRank) = min(carryRank);
	                	cur(carryRank-1) = cur(carryRank-1) +1;
                        carryRank--;
                    }
	            if (carryRank == 0 && (cur(0) > max(0))()) {
	                done = true;
                    }
                }
            }
            return ans;
        }
    }

    public def iterator():Iterator[Point(rank)] {
        return new RRIterator(this);
    }


    public def equals(thatObj:Any):x10.lang.Boolean {
	if (this == thatObj) return true;
        if (!(thatObj instanceof Region)) return false; 
        val that:Region = thatObj as Region;

        // we only handle rect==rect
        if (!(that instanceof RectRegion))
            return super.equals(that);

        // ranks must match
        if (this.rank!=that.rank)
            return false;

        // fetch bounds
        val thisMin = this.min();
        val thisMax = this.max();
        val thatMin = (that as RectRegion).min();
        val thatMax = (that as RectRegion).max();

        // compare 'em
        for (var i: Int = Int.asInt(0); (i<rank)(); i = i + 1) {
            if (thisMin(i)!=thatMin(i) || thisMax(i)!=thatMax(i))
                return false;
        }
        return true;
    }

    public def toString():String {
        val thisMin = this.min();
        val thisMax = this.max();
        var s: String = "[";
        for (var i: Int = Int.asInt(0); (i<rank)(); i = i + 1) {
            if ((i>Int.asInt(0))()) s += ",";
            s += "" + thisMin(i) + ".." + thisMax(i);
        }
        s += "]";
        return s;
    }
}
