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

package x10.runtime.impl.java;

/**
 * This class provides a set of static methods that
 * provide low-level memory synchronization barriers.
 *
 * For performance, these methods should be implemented
 * by the VM as instrinsics but can be simulated using
 * load/stores to volatile variables.
 */
public final class FencesUtils {
  static volatile int v1;
  static volatile int v2;
  static int d1;
  static int d2;
  
  private FencesUtils() {}

  public static void loadStoreBarrier() {
    v2 = v1;
  }

  public static void storeLoadBarrier() {
    v2 = d1;
    d2 = v1;
  }

  public static void loadLoadBarrier() {
    d1 = v1;
    d2 = v2;
  }
  
  public static void storeStoreBarrier() {
    v1 = d1;
    v2 = d2;
  }
}