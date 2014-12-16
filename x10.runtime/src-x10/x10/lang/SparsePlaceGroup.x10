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

package x10.lang;

/**
 * <p>An implementation of PlaceGroup that simply uses a sorted Rail[Place] to
 * represent the Places in the group.  This implementation is only suitable
 * when the PlaceGroup contains a fairly small number of places.
 * This can happen either becase the group is very sparse, or because the total 
 * number of places being used by the program is small.  In either case, 
 * this PlaceGroup should have acceptable performance.</p>
 *
 * <p>Although the basic operations (contains, indexOf) could be asymptotically
 * improved from O(N) to O(log(N)) by using binary search instead of linear
 * search, the expected performance of this class would still be poor due to
 * the space overhead of explictly representing all of the Places in the group,
 * which in turn would yield O(size()) serialization costs.  Therefore, we have
 * decided to go with the lower constants and ignore the asymptotic analysis.</p>
 */
public final class SparsePlaceGroup extends PlaceGroup {
 
  /**
   * The set of places.
   * The array is in sorted order by Place.id.
   * Only places that are in the group are in the array.
   */
  private val places:Rail[Place];

  /**
   * Construct a SparsePlaceGroup from a Rail[Place].
   * The argument rail must be a set and be sorted in order of increasing id;
   * if this is not true then an IllegalArgumentException will be thrown.
   */
  public def this(ps:Rail[Place]) {
    places = new Rail[Place](ps);
    for (i in 1..(places.size-1)) {
        if (places(i).id <= places(i-1).id) {
            throw new IllegalArgumentException("Argument rail was not sorted");
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
 

