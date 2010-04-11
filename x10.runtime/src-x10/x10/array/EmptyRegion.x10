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

/**
 * Represents an empty region.
 *
 * @author bdlucas
 * @author vj
 */
class EmptyRegion extends BaseRegion {

    def this(val rank: int): EmptyRegion{self.rank==rank} {
        super(rank,true,false);
    }

    public global def isConvex() = true;
    public global def isEmpty() = true;
    public global def size() = 0;
    public global def intersection(that: Region(rank)): Region(rank) = this;
    public global def product(that: Region): Region/*(this.rank+that.rank)*/ 
        = new EmptyRegion(this.rank + that.rank);
    public global def projection(axis: int): Region(1) = new EmptyRegion(1);
    public global def translate(p:Point(rank)): Region(rank) = this;
    public global def eliminate(i:Int)= new EmptyRegion(rank-1);
    protected global def computeBoundingBox(): Region(rank) {
        throw U.illegal("bounding box not not defined for empty region");
    }
    public global def contains(that: Region(rank)) = that.isEmpty();
    public global def contains(p:Point):Boolean = false;

    private static class ERIterator(myRank:int) implements Iterator[Point(myRank)]() {
        def this(r:int) { property(r); }
        public def hasNext():boolean = false;
        public def next():Point(myRank) {
            throw new x10.util.NoSuchElementException();
        }
    }
    public global def iterator():Iterator[Point(rank)] {
        return new ERIterator(rank);
    }

    public global safe def toString() = "empty(" + rank + ")";

}
