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

/**
 * Represents an empty region.
 */
final class EmptyRegion extends Region{rect} {

    def this(val rank:Long): EmptyRegion{self.rank==rank} {
        super(rank,true,false);
	if (rank<0) throw new IllegalArgumentException("Rank is negative ("+rank+")");
    }

    public def isConvex() = true;
    public def isEmpty() = true;
    public def size() = 0;
    public def indexOf(Point) = -1;
    public def intersection(that:Region(rank)):Region(rank) = this;
    public def product(that:Region)/*: Region(this.rank+that.rank)*/ 
        = new EmptyRegion(this.rank + that.rank);
    public def projection(axis:Long):Region(1) = new EmptyRegion(1);
    public def translate(p:Point(rank)) = this; 
    public def eliminate(i:Long)= new EmptyRegion(rank-1);
    protected def computeBoundingBox():Region(rank) {
        throw new IllegalOperationException("bounding box not not defined for empty region");
    }
    public def min():(Long)=>Long {
        throw new IllegalOperationException("min not not defined for empty region");
    }
    public def max():(Long)=>Long {
        throw new IllegalOperationException("max not not defined for empty region");
    }
    public def contains(that: Region(rank)) = that.isEmpty();
    public def contains(p:Point):Boolean = false;

    private static class ERIterator(myRank:Long) implements Iterator[Point(myRank)] {
        def this(r:Long) { property(r); }
        public def hasNext():Boolean = false;
        public def next():Point(myRank) {
            throw new x10.util.NoSuchElementException();
        }
    }
    public def iterator():Iterator[Point(rank)] {
        return new ERIterator(rank);
    }

    public def toString() = "empty(" + rank + ")";

}
