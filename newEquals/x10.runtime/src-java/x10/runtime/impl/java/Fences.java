/*
 *
 * (C) Copyright IBM Corporation 2009
 *
 *  This file is part of X10 Language.
 *
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
public final class Fences {
  static volatile int v1;
  static volatile int v2;
  static int d1;
  static int d2;
  
  private Fences() {}

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