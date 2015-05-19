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

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * The {@link IdleTask} class is used to increase the thread pool size.
 */
class IdleTask extends RecursiveAction implements ForkJoinPool.ManagedBlocker {
  private static final long serialVersionUID = 151152908824740821L;

  private boolean kill;

  @Override
  public synchronized void compute() {
    while (!kill) {
      try {
        wait();
        ForkJoinPool.managedBlock(this);
      } catch (final InterruptedException e) {
      }
    }
  }

  @Override
  public boolean block() throws InterruptedException {
    return true;
  }

  @Override
  public boolean isReleasable() {
    return false;
  }

  /**
   * Wakes the task.
   */
  public synchronized void signal() {
    notify();
  }

  /**
   * Kills the task.
   */
  public synchronized void kill() {
    kill = true;
    notify();
  }
}
