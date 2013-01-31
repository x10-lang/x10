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
import safe.lang.Int;
import safe.lang.Boolean; 
import safe.array.Point;
import safe.array.Region; 

/**
 * Represents an empty region.
 */
final class EmptyRegion extends Region{rect == Boolean.TRUE()} {

    def this(val rank: Int): EmptyRegion{self.rank==rank} {
        super(rank,Boolean.TRUE(),Boolean.FALSE());
	if (Boolean.asX10Boolean(rank<Int.asInt(0))) throw new IllegalArgumentException("Rank is negative ("+rank+")");
    }

    public def isConvex() = Boolean.TRUE();
    public def isEmpty() = Boolean.TRUE();
    public def size() = Int.asInt(0);
    public def indexOf(Point) = Int.asInt(-1);
    public def intersection(that: Region(rank)): Region(rank) = this;
    public def product(that: Region)/*: Region(this.rank+that.rank)*/ 
        = new EmptyRegion(this.rank + that.rank);
    public def projection(axis: int): Region(Int.asInt(1)) = new EmptyRegion(Int.asInt(1));
    public def translate(p:Point(rank)) = this; 
    public def eliminate(i:Int)= new EmptyRegion(rank-Int.asInt(1));
    protected def computeBoundingBox(): Region(rank) {
        throw new IllegalOperationException("bounding box not not defined for empty region");
    }
    public def min():(Int)=>Int {
        throw new IllegalOperationException("min not not defined for empty region");
    }
    public def max():(Int)=>Int {
        throw new IllegalOperationException("max not not defined for empty region");
    }
    public def contains(that: Region(rank)) = that.isEmpty();
    public def contains(p:Point):Boolean = Boolean.FALSE();

    private static class ERIterator(myRank:Int) implements Iterator[Point(myRank)] {
        def this(r:Int) { property(r); }
        public def hasNext():x10.lang.Boolean = false;
        public def next():Point(myRank) {
            throw new x10.util.NoSuchElementException();
        }
    }
    public def iterator():Iterator[Point(rank)] {
        return new ERIterator(rank);
    }

    public def toString() = "empty(" + rank + ")";

}
