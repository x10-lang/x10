/*
 *  This file is part of the X10 project (http://x10-lang.org).
 * 
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 * 
 *  (C) Copyright IBM Corporation 2006-2013.
 */

package x10.simplearray;

/**
 * An IterationSpace represents an iteration space,
 * ie. a lexograpically ordered finite collection of 
 * equal rank Points.
 */
public abstract class IterationSpace(rank:int,rect:boolean) implements Iterable[Point(this.rank)] {

    protected def this(rank:int, rect:boolean) {
        property(rank, rect);
    }

    public abstract def iterator():Iterator[Point(this.rank)]; 

    public abstract def min(i:Int):long;

    public abstract def max(i:Int):long;

    public def toString() {
        val sb = new x10.util.StringBuilder();
        val it = iterator();

        sb.add("{");
        for (var c:int = 0; c < 10 && it.hasNext(); c++) {
            sb.add(it.next());
            if (it.hasNext()) sb.add(", ");
        }
	if (it.hasNext()) sb.add("...");
        sb.add("}");

        return sb.toString();
    }
}

public type IterationSpace(r:int) = IterationSpace{self.rank==r};
