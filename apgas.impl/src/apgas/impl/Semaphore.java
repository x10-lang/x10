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

package apgas.impl;

/**
 * The {@link Semaphore} class extends the
 * {@link java.util.concurrent.Semaphore java.util.concurrent.Semaphore} class
 * to expose the {@link #reducePermits(int)} method.
 *
 */
final class Semaphore extends java.util.concurrent.Semaphore {
  private static final long serialVersionUID = -7522284546281301809L;

  /**
   * Constructs a new {@link Semaphore} with the given number of permits.
   *
   * @param permits
   *          the initial number of permits available
   */
  public Semaphore(int permits) {
    super(permits);
  }

  @Override
  public void reducePermits(int reduction) {
    super.reducePermits(reduction);
  }
}
