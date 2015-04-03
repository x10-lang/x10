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
import apgas.SerializableCallable;

@SuppressWarnings("javadoc")
public class GlobalArray<T> extends GlobalRef<T[]> {
  private static final long serialVersionUID = -1791967739280529895L;

  @SafeVarargs
  public GlobalArray(Collection<? extends Place> places, int n, T... array) {
    super(places, () -> Arrays.copyOf(array, n));
  }

  public GlobalArray(Collection<? extends Place> places, int n,
      SerializableCallable<T[]> initializer) {
    super(places, initializer);
  }

  public T get(int index) {
    return get()[index];
  }

  public void set(int index, T t) {
    get()[index] = t;
  }
}
