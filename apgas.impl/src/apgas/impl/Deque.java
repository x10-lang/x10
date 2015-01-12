/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 *
 * Edited from jsr166 java.util.concurrent.ForkJoinPool (revision 1.216)
 *
 * Originally written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */

package apgas.impl;

import java.lang.reflect.Field;
import java.util.concurrent.RejectedExecutionException;

/**
 * Queues supporting work-stealing as well as external task submission. See
 * above for descriptions and algorithms. Performance on most platforms is very
 * sensitive to placement of instances of both WorkQueues and their arrays -- we
 * absolutely do not want multiple WorkQueue instances or multiple queue arrays
 * sharing cache lines. The @Contended annotation alerts JVMs to try to keep
 * instances apart.
 */
@SuppressWarnings({ "restriction", "javadoc" })
@sun.misc.Contended
final class Deque {

  /**
   * Capacity of work-stealing queue array upon initialization. Must be a power
   * of two; at least 4, but should be larger to reduce or eliminate cacheline
   * sharing among queues. Currently, it is much larger, as a partial workaround
   * for the fact that JVMs often place arrays in locations that share GC
   * bookkeeping (especially cardmarks) such that per-write accesses encounter
   * serious memory contention.
   */
  static final int INITIAL_QUEUE_CAPACITY = 1 << 13;

  /**
   * Maximum size for queue arrays. Must be a power of two less than or equal to
   * 1 &lt;&lt; (31 - width of array entry) to ensure lack of wraparound of
   * index calculations, but defined to a value a bit less than this to help
   * users trap runaway programs before saturating systems.
   */
  static final int MAXIMUM_QUEUE_CAPACITY = 1 << 26; // 64M

  // Instance fields
  volatile int base; // index of next slot for poll
  int top; // index of next slot for push
  Task[] array; // the elements (initially unallocated)

  Deque() {
    // Place indices in the center of array (that is not yet allocated)
    base = top = INITIAL_QUEUE_CAPACITY >>> 1;
    growArray();
  }

  /**
   * Returns the approximate number of tasks in the queue.
   */
  final int queueSize() {
    final int n = base - top; // non-owner callers must read base first
    return (n >= 0) ? 0 : -n; // ignore transient negative
  }

  /**
   * Provides a more accurate estimate of whether this queue has any tasks than
   * does queueSize, by checking whether a near-empty queue has at least one
   * unclaimed task.
   */
  final boolean isEmpty() {
    Task[] a;
    int n, m, s;
    return ((n = base - (s = top)) >= 0 || (n == -1 && // possibly one task
    ((a = array) == null || (m = a.length - 1) < 0 || U.getObject(a,
        (long) ((m & (s - 1)) << ASHIFT) + ABASE) == null)));
  }

  /**
   * Pushes a task. Call only by owner in unshared queues. (The shared-queue
   * version is embedded in method externalPush.)
   *
   * @param task
   *          the task. Caller must ensure non-null.
   * @throws RejectedExecutionException
   *           if array cannot be resized
   */
  final void push(Task task) {
    Task[] a;
    final int b = base, s = top;
    int n;
    if ((a = array) != null) { // ignore if queue removed
      final int m = a.length - 1; // fenced write for task visibility
      U.putOrderedObject(a, ((m & s) << ASHIFT) + ABASE, task);
      U.putOrderedInt(this, QTOP, s + 1);
      if ((n = s - b) <= 1) {
        ;
      } else if (n >= m) {
        growArray();
      }
    }
  }

  /**
   * Initializes or doubles the capacity of array. Call either by owner or with
   * lock held -- it is OK for base, but not top, to move while resizings are in
   * progress.
   */
  final Task[] growArray() {
    final Task[] oldA = array;
    final int size = oldA != null ? oldA.length << 1 : INITIAL_QUEUE_CAPACITY;
    if (size > MAXIMUM_QUEUE_CAPACITY) {
      throw new RejectedExecutionException("Queue capacity exceeded");
    }
    int oldMask, t, b;
    final Task[] a = array = new Task[size];
    if (oldA != null && (oldMask = oldA.length - 1) >= 0
        && (t = top) - (b = base) > 0) {
      final int mask = size - 1;
      do { // emulate poll from old array, push to new array
        Task x;
        final int oldj = ((b & oldMask) << ASHIFT) + ABASE;
        final int j = ((b & mask) << ASHIFT) + ABASE;
        x = (Task) U.getObjectVolatile(oldA, oldj);
        if (x != null && U.compareAndSwapObject(oldA, oldj, x, null)) {
          U.putObjectVolatile(a, j, x);
        }
      } while (++b != t);
    }
    return a;
  }

  /**
   * Takes next task, if one exists, in LIFO order. Call only by owner in
   * unshared queues.
   */
  final Task pop() {
    Task[] a;
    Task t;
    int m;
    if ((a = array) != null && (m = a.length - 1) >= 0) {
      for (int s; (s = top - 1) - base >= 0;) {
        final long j = ((m & s) << ASHIFT) + ABASE;
        if ((t = (Task) U.getObject(a, j)) == null) {
          break;
        }
        if (U.compareAndSwapObject(a, j, t, null)) {
          U.putOrderedInt(this, QTOP, s);
          return t;
        }
      }
    }
    return null;
  }

  /**
   * Takes next task, if one exists, in FIFO order.
   */
  final Task poll() {
    Task[] a;
    int b;
    Task t;
    while ((b = base) - top < 0 && (a = array) != null) {
      final int j = (((a.length - 1) & b) << ASHIFT) + ABASE;
      t = (Task) U.getObjectVolatile(a, j);
      if (base == b) {
        if (t != null) {
          if (U.compareAndSwapObject(a, j, t, null)) {
            base = b + 1;
            return t;
          }
        } else if (b + 1 == top) {
          break;
        }
      }
    }
    return null;
  }

  // Unsafe mechanics. Note that some are (and must be) the same as in FJP
  private static final sun.misc.Unsafe U;
  private static final int ABASE;
  private static final int ASHIFT;
  private static final long QTOP;
  static {
    try {
      if (Deque.class.getClassLoader() != null) {
        final Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        U = (sun.misc.Unsafe) f.get(null);
      } else {
        U = sun.misc.Unsafe.getUnsafe();
      }
      final Class<?> wk = Deque.class;
      final Class<?> ak = Task[].class;
      QTOP = U.objectFieldOffset(wk.getDeclaredField("top"));
      ABASE = U.arrayBaseOffset(ak);
      final int scale = U.arrayIndexScale(ak);
      if ((scale & (scale - 1)) != 0) {
        throw new Error("data type scale not a power of two");
      }
      ASHIFT = 31 - Integer.numberOfLeadingZeros(scale);
    } catch (final Exception e) {
      throw new Error(e);
    }
  }
}
