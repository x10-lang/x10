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

package apgas.util;

import java.util.Collection;

import apgas.Place;

/**
 * The {@link PlaceLocalIntArray} class implements a map from places to
 * {@code int} arrays.
 */
public class PlaceLocalIntArray extends PlaceLocalObject {
  /**
   * The local array.
   */
  protected final int[] array;

  /**
   * Initializes the local array.
   *
   * @param n
   *          the length of the local array
   */
  protected PlaceLocalIntArray(int n) {
    array = new int[n];
  }

  /**
   * Returns the local array element at the specified index
   *
   * @param index
   *          an index into the local array
   * @return the array element
   */
  public int get(int index) {
    return array[index];
  }

  /**
   * Sets the local array element at the specified index
   *
   * @param index
   *          an index into the local array
   * @param t
   *          the desired value
   */
  public void set(int index, int t) {
    array[index] = t;
  }

  /**
   * Constructs a {@link PlaceLocalArray} instance.
   *
   * @param places
   *          a collection of places with no repetition
   * @param localLength
   *          the length of each chunk
   * @return the place local array
   */
  public static PlaceLocalIntArray make(Collection<? extends Place> places,
      int localLength) {
    return PlaceLocalObject.make(places, () -> new PlaceLocalIntArray(
        localLength));
  }
}
