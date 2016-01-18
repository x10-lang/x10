/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2016.
 */

package x10.lang;

import x10.util.HashSet;

/**
 * <p>An implementation of PlaceGroup that simply uses a Rail[Place] to
 * represent the Places in the group.  This implementation is only suitable
 * when the PlaceGroup contains a fairly small number of places.
 * This can happen either becase the group is very sparse, or because the total 
 * number of places being used by the program is small.  In either case, 
 * this PlaceGroup should have acceptable performance.</p>
 *
 * <p>Although the basic operations (contains, indexOf) could be asymptotically
 * improved from O(N) to O(log(N)) by using binary search over a sorted Rail,
 * the expected performance of this class would still be poor due to
 * the space overhead of explictly representing all of the Places in the group,
 * which in turn would yield O(size()) serialization costs.  Therefore, we have
 * decided to go with the lower constants and ignore the asymptotic analysis.</p>
 */
public final class SparsePlaceGroup extends PlaceGroup {
 
  /**
   * The set of places.
   * Only places that are in the group are in the array.
   */
  private val places:Rail[Place];

  /**
   * Construct a SparsePlaceGroup from a Rail[Place].
   * Places may appear in any order, but must represent a 
   * set of Places (no duplicate entries).
   * If the argument rail is not a set an IllegalArgumentException 
   * will be thrown (unless the X10 standaed library was compiled with NO_CHECKS).
   */
  public def this(ps:Rail[Place]) {
    places = new Rail[Place](ps);
    if (x10.compiler.CompilerFlags.checkPlace()) {
        var sorted:Boolean = true;
        // First try the cheap test; see if the Rail is sorted.
        for (i in 1..(places.size-1)) {
            if (places(i).id <= places(i-1).id) {
                sorted = false;
                break;
            }
        }
        if (!sorted) {
            val seen:HashSet[Place] = new HashSet[Place]();
            for (p in places) {
                if (seen.contains(p)) {
                    throw new IllegalArgumentException("Argument rail was not sorted");
                }
                seen.add(p);
            }
        }
    }
  }

  /**
   * Construct a SparsePlaceGroup from another PlaceGroup.
   */
  public def this(pg:PlaceGroup) {
    places = new Rail[Place](pg.size(), (i:Long)=>pg(i));
  }

  /**
   * Construct a SparsePlaceGroup that contains a single place, p.
   * @param p the place 
   */
  public def this(p:Place) {
    places = new Rail[Place](1, p);
  }

  public operator this(i:Long):Place = places(i);

  public def iterator() = places.iterator();

  public def numPlaces() = places.size;

  public def contains(id:Long):Boolean {
    for (p in places) {
        if (p.id == id) return true;
    }
    return false;
  }

  public def indexOf(id:Long):Long {
    for (i in places.range()) {
        if (places(i).id == id) return i;
    }
    return -1;
  }
}
 

