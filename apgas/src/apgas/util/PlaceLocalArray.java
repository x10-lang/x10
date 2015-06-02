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

import java.util.Arrays;
import java.util.Collection;

import apgas.Place;

/**
 * The {@link PlaceLocalArray} class implements a map from places to arrays.
 *
 * @param <T>
 *          the type of the array elements
 */
public class PlaceLocalArray<T> extends PlaceLocalObject {
  /**
   * The local array.
   */
  protected final T[] array;

  /**
   * Initializes the local array.
   *
   * @param array
   *          the local array
   */
  protected PlaceLocalArray(T[] array) {
    this.array = array;
  }

  /**
   * Returns the local array element at the specified index
   *
   * @param index
   *          an index into the local array
   * @return the array element
   */
  public T get(int index) {
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
  public void set(int index, T t) {
    array[index] = t;
  }

  /**
   * Constructs a {@link PlaceLocalArray} instance.
   *
   * @param <T>
   *          the type of the array elements
   * @param places
   *          a collection of places with no repetition
   * @param localLength
   *          the length of each chunk
   * @param array
   *          an array of {@code T}
   * @return the place local array
   */
  @SafeVarargs
  public static <T> PlaceLocalArray<T> make(Collection<? extends Place> places,
      int localLength, T... array) {
    return PlaceLocalObject.make(places,
        () -> new PlaceLocalArray<T>(Arrays.copyOf(array, localLength)));
  }
}
