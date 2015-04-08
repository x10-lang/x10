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

@SuppressWarnings("javadoc")
public class PlaceLocalArray<T> extends PlaceLocalObject {
  protected final T[] array;

  protected PlaceLocalArray(T[] array) {
    this.array = array;
  }

  public T get(int index) {
    return array[index];
  }

  public void set(int index, T t) {
    array[index] = t;
  }

  @SafeVarargs
  public static <T> PlaceLocalArray<T> make(Collection<? extends Place> places,
      int localLength, T... array) {
    return PlaceLocalObject.make(places,
        () -> new PlaceLocalArray<T>(Arrays.copyOf(array, localLength)));
  }
}
