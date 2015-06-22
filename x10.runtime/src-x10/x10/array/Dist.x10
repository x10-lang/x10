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

package x10.array;

/**
 * A Dist represents a function mapping Points in an IterationSpace
 * to Places in a PlaceGroup. 
 * 
 * @see DistArray
 * @see IterationSpace
 * @see PlaceGroup
 */ 
public abstract class Dist(pg:PlaceGroup{self!=null}, is:IterationSpace{self!=null}) {
    property rank():Long = is.rank;

    def this(pg:PlaceGroup{self!=null}, is:IterationSpace{self!=null}) {
        property(pg, is);
    }
}

public type Dist(r:Long) = Dist{self.rank==r};

