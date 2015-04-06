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
public class GlobalIntArray extends GlobalObject {
  public final int[] a;

  protected GlobalIntArray(int n) {
    a = new int[n];
  }

  public static GlobalIntArray make(Collection<? extends Place> places, int n) {
    return GlobalObject.make(places, () -> new GlobalIntArray(n));
  }
}
