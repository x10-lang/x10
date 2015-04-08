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

@SuppressWarnings("javadoc")
public class PlaceLocalIntArray extends PlaceLocalObject {
  protected final int[] array;

  protected PlaceLocalIntArray(int n) {
    array = new int[n];
  }

  public int get(int index) {
    return array[index];
  }

  public void set(int index, int t) {
    array[index] = t;
  }

  public static PlaceLocalIntArray make(Collection<? extends Place> places, int localLength) {
    return PlaceLocalObject.make(places, () -> new PlaceLocalIntArray(localLength));
  }
}
