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

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import apgas.SerializableJob;
import apgas.util.GlobalID;

/**
 * The {@link DefaultFinish} class implements the distributed termination
 * semantics of the finish construct.
 * <p>
 * A single dynamic instance of a finish construct is implemented by a
 * collection of {@link DefaultFinish} objects: one per place in which tasks
 * governed by this finish have been spawned. These objects are identified by
 * their {@link apgas.util.GlobalID GlobalID} instance.
 * <p>
 * The collection is created lazily. First, the {@link apgas.util.GlobalID
 * GlobalID} is allocated only when the {@link DefaultFinish} object is
 * serialized for the first time. Second, {@link DefaultFinish} objects are
 * added to the collection upon deserialization. The {@code #readResolve()}
 * method ensures that a single object is allocated in each place.
 * <p>
 * A finish object may represent:
 * <ul>
 * <li>a local finish: a finish with no remote task (yet).</li>
 * <li>a root finish: a finish instantiated here with remote tasks.</li>
 * <li>a remote finish: a finish instantiated elsewhere.</li>
 * </ul>
 * <p>
 * The finish body counts as one local task.
 */
final class DefaultFinish implements Serializable, Finish {
  private static final long serialVersionUID = 3789869778188598267L;

  /**
   * A factory producing {@link DefaultFinish} instances.
   */
  static class Factory extends Finish.Factory {
    @Override
    DefaultFinish make(Finish parent) {
      return new DefaultFinish();
    }
  }

  /**
   * The {@link GlobalID} instance for this finish construct.
   * <p>
   * Null until the finish object is first serialized.
   */
  GlobalID id;

  /**
   * A multi-purpose task counter.
   * <p>
   * This counter counts:
   * <ul>
   * <li>all tasks for a local finish</li>
   * <li>places with non-zero task counts for a root finish</li>
   * <li>local task count for a remote finish</li>
   * </ul>
   */
  private transient int count;

  /**
   * Per-place count of task spawned minus count of terminated tasks.
   * <p>
   * Null until a remote task is spawned.
   */
  private transient int counts[];

  /**
   * Uncaught exceptions collected by this finish construct.
   */
  private transient List<Throwable> exceptions;

  /**
   * Constructs a finish instance.
   */
  DefaultFinish() {
    final int here = GlobalRuntimeImpl.getRuntime().here;
    spawn(here);
  }

  @Override
  public synchronized void submit(int p) {
    final int here = GlobalRuntimeImpl.getRuntime().here;
    if (id != null && id.home.id != here) {
      // remote finish
      count++;
    }
  }

  @Override
  public synchronized void spawn(int p) {
    final int here = GlobalRuntimeImpl.getRuntime().here;
    if (id == null || id.home.id == here) {
      // local or root finish
      if (counts == null) {
        if (here == p) {
          count++;
          return;
        }
        counts = new int[GlobalRuntimeImpl.getRuntime().maxPlace()];
        counts[here] = count;
        count = 1;
      }
      if (p >= counts.length) {
        resize(p + 1);
      }
      if (counts[p]++ == 0) {
        count++;
      }
      if (counts[p] == 0) {
        --count;
      }
    } else {
      // remote finish
      if (p >= counts.length) {
        resize(p + 1);
      }
      counts[p]++;
    }
  }

  @Override
  public synchronized void unspawn(int p) {
    final int here = GlobalRuntimeImpl.getRuntime().here;
    if (id == null || id.home.id == here) {
      // root finish
      if (counts == null) {
        // task must have been local
        --count;
      } else {
        if (counts[p] == 0) {
          count++;
        }
        if (--counts[p] == 0) {
          --count;
        }
      }
    } else {
      // remote finish
      --counts[p];
    }
  }

  @Override
  public synchronized void tell(int p) {
    final int here = GlobalRuntimeImpl.getRuntime().here;
    if (id == null || id.home.id == here) {
      // local or root finish
      if (counts != null) {
        if (counts[here] == 0) {
          count++;
        }
        if (--counts[here] != 0) {
          return;
        }
      }
      if (--count == 0) {
        notifyAll();
      }
    } else {
      // remote finish
      --counts[here];
      if (--count == 0) {
        final int _counts[] = counts;
        final DefaultFinish that = this;
        GlobalRuntimeImpl.getRuntime().transport.send(id.home.id,
            () -> that.update(_counts));
        Arrays.fill(counts, 0);
      }
    }
  }

  /**
   * Applies an update message from a remote finish to the root finish.
   *
   * @param _counts
   *          incoming counters
   */
  synchronized void update(int _counts[]) {
    if (_counts.length > counts.length) {
      resize(_counts.length);
    }
    for (int i = 0; i < _counts.length; i++) {
      if (counts[i] != 0) {
        --count;
      }
      counts[i] += _counts[i];
      if (counts[i] != 0) {
        count++;
      }
    }
    if (count == 0) {
      notifyAll();
    }
  }

  @Override
  public synchronized void addSuppressed(Throwable exception) {
    final int here = GlobalRuntimeImpl.getRuntime().here;
    if (id == null || id.home.id == here) {
      // root finish
      if (exceptions == null) {
        exceptions = new ArrayList<Throwable>();
      }
      exceptions.add(exception);
    } else {
      // remote finish: spawn remote task to transfer exception to root finish
      final SerializableThrowable t = new SerializableThrowable(exception);
      final DefaultFinish that = this;
      spawn(id.home.id);
      new Task(this, (SerializableJob) () -> {
        that.addSuppressed(t.t);
      }, here).asyncat(id.home.id);
    }
  }

  @Override
  public synchronized boolean isReleasable() {
    return count == 0;
  }

  @Override
  public synchronized List<Throwable> exceptions() {
    return exceptions;
  }

  @Override
  public synchronized boolean block() {
    while (count != 0) {
      try {
        wait();
      } catch (final InterruptedException e) {
      }
    }
    return count == 0;
  }

  /**
   * Reallocates the {@link #counts} array to account for larger place counts.
   *
   * @param min
   *          a minimal size for the reallocation
   */
  private void resize(int min) {
    final int[] tmp = new int[Math.max(min, GlobalRuntimeImpl.getRuntime()
        .maxPlace())];
    System.arraycopy(counts, 0, tmp, 0, counts.length);
    counts = tmp;
  }

  /**
   * Serializes the finish object.
   *
   * @param out
   *          the object output stream
   * @throws IOException
   *           if I/O errors occur
   */
  private void writeObject(ObjectOutputStream out) throws IOException {
    synchronized (this) {
      if (id == null) {
        id = new GlobalID();
        id.putHere(this);
      }
    }
    out.defaultWriteObject();
  }

  /**
   * Deserializes the finish object.
   *
   * @return the finish object
   * @throws ObjectStreamException
   *           if an error occurs
   */
  private Object readResolve() throws ObjectStreamException {
    DefaultFinish me = (DefaultFinish) id.putHereIfAbsent(this);
    if (me == null) {
      me = this;
    }
    synchronized (me) {
      final int here = GlobalRuntimeImpl.getRuntime().here;
      if (id.home.id != here && me.counts == null) {
        me.counts = new int[GlobalRuntimeImpl.getRuntime().maxPlace()];
      }
    }
    return me;
  }
}
