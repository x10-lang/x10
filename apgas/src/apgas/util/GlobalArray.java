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
public class GlobalArray<T> extends GlobalObject {
  public final T[] a;

  protected GlobalArray(T[] a) {
    this.a = a;
  }

  @SafeVarargs
  public static <T> GlobalArray<T> make(Collection<? extends Place> places,
      int n, T... a) {
    return GlobalObject.make(places,
        () -> new GlobalArray<T>(Arrays.copyOf(a, n)));
  }
}
