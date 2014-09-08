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

package apgas.impl;

import java.util.Random;

/**
 * The {@link Worker} class implements a worker thread.
 * <p>
 * The worker thread owns a work-stealing {@link Deque} and keeps track of the
 * currently running task.
 */
final class Worker extends Thread {
  /**
   * The scheduler this worker thread reports to.
   */
  private final Scheduler scheduler;

  /**
   * The work-stealing deque for this worker instance.
   */
  final Deque deque = new Deque();

  /**
   * The current task.
   */
  Task task;

  /**
   * Instantiates a worker thread.
   *
   * @param scheduler
   *          the scheduler this worker instance belongs to
   */
  Worker(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  /**
   * Finds and runs pending tasks until the scheduler is shut down.
   */
  @Override
  public void run() {
    final Random random = new Random();
    scheduler.acquirePermit();
    while (scheduler.live()) { // implicit park if too many threads
      Task task = pop();
      if (task == null) { // scan deques for task
        final int max = scheduler.size;
        int offset = random.nextInt(max);
        for (int i = 0; i < max && task == null; i++) {
          if (scheduler.pool[offset] != null) {
            task = scheduler.pool[offset].deque.poll();
          }
          if (++offset >= max) {
            offset = 0;
            // check for pending tasks
            if (task == null) {
              task = scheduler.queue.poll();
            }
          }
        }
      }
      if (task == null) {
        task = scheduler.await(this); // sleep
      }
      if (task != null) {
        task.run(this);
      }
    }
    scheduler.releasePermit();
  }

  /**
   * Fetches a task from the deque.
   * <p>
   * Pushes tasks to idle worker threads.
   *
   * @return a task
   */
  private Task pop() {
    Task task = null;
    while (task == null) {
      task = deque.pop();
      if (task == null) {
        return null;
      }
      task = scheduler.deal(task);
    }
    return task;
  }

  /**
   * Runs pending tasks from this worker's deque until the deque is empty or the
   * finish has terminated.
   *
   * @param finish
   *          the finish object to monitor
   */
  void help(Finish finish) {
    while (finish.waiting()) {
      final Task task = pop();
      if (task == null) {
        scheduler.increaseParallelism();
        finish.await();
        scheduler.decreaseParallelism();
        return;
      }
      task.run(this);
    }
  }
}
