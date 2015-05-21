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

import apgas.pool.ForkJoinPool;
import apgas.pool.ForkJoinWorkerThread;

/**
 * The {@link Worker} class implements a worker thread.
 */
final class Worker extends ForkJoinWorkerThread {
  /**
   * Instantiates a Worker operating in the given pool.
   *
   * @param pool
   *          the pool this worker works in
   */
  protected Worker(ForkJoinPool pool) {
    super(pool);
  }

  /**
   * The current task.
   */
  Task task;
}
