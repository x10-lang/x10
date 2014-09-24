/*
 *  This file is part of the X10 project (http://x10-lang.org).
 * 
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 * 
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.array;

/**
 * An IterationSpace represents an iteration space,
 * ie. a lexograpically ordered finite collection of 
 * equal rank Points.
 */
public abstract class IterationSpace(rank:Long,rect:Boolean) implements Iterable[Point(this.rank)] {

    protected def this(rank:Long, rect:Boolean) {
        property(rank, rect);
    }

    public static operator (r:IntRange):IterationSpace{self.rank==1,self.rect} = new DenseIterationSpace_1(r.min, r.max);

    public static operator (r:LongRange):IterationSpace{self.rank==1,self.rect} = new DenseIterationSpace_1(r.min, r.max);

    public abstract def iterator():Iterator[Point(this.rank)]; 

    public abstract def min(i:Long):Long;

    public abstract def max(i:Long):Long;

    public abstract def isEmpty():Boolean;

    public def toString() {
        val sb = new x10.util.StringBuilder();
        val it = iterator();

        sb.add("{");
        for (var c:Int = 0n; c < 10n && it.hasNext(); c++) {
            sb.add(it.next());
            if (it.hasNext()) sb.add(", ");
        }
	if (it.hasNext()) sb.add("...");
        sb.add("}");

        return sb.toString();
    }
}

public type IterationSpace(r:Long) = IterationSpace{self.rank==r};
