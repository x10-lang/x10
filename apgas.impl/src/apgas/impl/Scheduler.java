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

/**
 * The {@link Scheduler} class implements the local scheduling engine for the
 * global runtime.
 */
final class Scheduler {
  /**
   * The deque of pending tasks.
   * <p>
   * Each worker also maintains a deque.
   */
  final Deque queue = new Deque();

  private boolean live = true;

  /**
   * The resizable pool of worker threads.
   */
  Worker pool[] = new Worker[256];

  /**
   * The current number of threads in the thread pool.
   */
  int size = Runtime.getRuntime().availableProcessors();

  /**
   * The number of permits that have been released for threads to grab.
   */
  private int permits = size;

  /**
   * The number of idle threads.
   */
  private int count = 0;

  private final Semaphore semaphore = new Semaphore(size);
  private Task offer;

  /**
   * Instantiates a new {@link Scheduler}.
   * <p>
   * Populates the thread pool. But threads are not started yet.
   */
  Scheduler() {
    for (int i = 0; i < size; i++) {
      pool[i] = new Worker(this);
    }
  }

  /**
   * Starts the threads in the thread pool.
   */
  void start() {
    final int n = size;
    for (int i = 0; i < n; i++) {
      pool[i].start();
    }
  }

  /**
   * Acquires a permit.
   */
  void acquirePermit() {
    semaphore.acquireUninterruptibly();
  }

  /**
   * Releases a permit.
   */
  void releasePermit() {
    semaphore.release();
  }

  /**
   * Release a permit
   *
   * @return true if the scheduler has not been shut down yet
   */
  boolean live() {
    if (live) {
      if (semaphore.availablePermits() < 0) {
        semaphore.release();
        semaphore.acquireUninterruptibly();
      }
      return true;
    } else {
      return false;
    }
  }

  /**
   * Shuts down the scheduler.
   * <p>
   * Notifies idle threads.
   */
  synchronized void shutdown() {
    live = false;
    notifyAll();
  }

  /**
   * Resumes one suspended thread if any, otherwise instantiates and start a new
   * thread.
   */
  void increaseParallelism() {
    synchronized (this) {
      if (++permits > size) {
        if (size >= pool.length) {
          final Worker tmp[] = new Worker[pool.length << 1];
          System.arraycopy(pool, 0, tmp, 0, pool.length);
          pool = tmp;
        }
        pool[size] = new Worker(this);
        pool[size++].start();
      }
    }
    semaphore.release();
  }

  /**
   * Reduces the number of permits.
   */
  void decreaseParallelism() {
    semaphore.reducePermits(1);
    synchronized (this) {
      --permits;
    }
  }

  /**
   * Submits one task to this scheduler.
   *
   * @param worker
   *          the worker doing the submission or null
   * @param task
   *          the submitted task
   */
  void submit(Worker worker, Task task) {
    if (worker != null && (count == 0 || offer != null)) {
      worker.deque.push(task);
      return;
    }
    synchronized (this) {
      if (count == 0 || offer != null) {
        if (worker != null) {
          worker.deque.push(task);
        } else {
          queue.push(task);
        }
      } else {
        offer = task;
        notify();
      }
    }
  }

  /**
   * Attempts to offer the specified task to an idle thread.
   *
   * @param task
   *          the task to give away
   * @return the task if unsuccessful, null otherwise
   */
  Task deal(Task task) {
    if (count == 0 || offer != null) {
      return task;
    }
    synchronized (this) {
      if (count == 0 || offer != null) {
        return task;
      } else {
        offer = task;
        notify();
        return null;
      }
    }
  }

  /**
   * Parks an idle thread until it is offered a task to run or the scheduler is
   * shut down.
   *
   * @param worker
   *          the worker invoking this method
   * @return the task offered if any or null
   */
  synchronized Task await(Worker worker) {
    Task task = queue.pop();
    if (task != null) {
      return task;
    }
    count++;
    if (live) {
      try {
        wait();
      } catch (final InterruptedException e) {
      }
    }
    count--;
    task = offer;
    offer = null;
    return task;
  }
}
