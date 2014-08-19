package apgas.impl;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import apgas.util.GlobalID;

/**
 * The {@link Finish} class implements the distributed termination semantics of
 * the finish construct.
 * <p>
 * A single dynamic instance of a finish construct is implemented by a
 * collection of {@link Finish} objects: one per place in which tasks governed
 * by this finish have been spawned. These objects are identified by their
 * {@link apgas.util.GlobalID GlobalID} instance.
 * <p>
 * The collection is created lazily. First, the {@link apgas.util.GlobalID
 * GlobalID} is allocated only when the {@link Finish} object is serialized for
 * the first time. Second, {@link Finish} objects are added to the collection
 * upon deserialization. The {@code #readResolve()} method ensures that a single
 * object is allocated in each place.
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
final class Finish implements Serializable {
  private static final long serialVersionUID = 3789869778188598267L;

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
   * Must be called before a task is enqueued for execution (local task or
   * incoming remote task).
   */
  synchronized void submit() {
    final int here = GlobalRuntimeImpl.getRuntime().here;
    if (id != null && id.home.id != here) {
      // remote finish
      count++;
    }
  }

  /**
   * Must be called before a task is spawned at place p (local task or outgoing
   * remote task).
   *
   * @param p
   *          a place
   */
  synchronized void spawn(int p) {
    final int here = GlobalRuntimeImpl.getRuntime().here;
    if (id == null || id.home.id == here) {
      // local or root finish
      if (counts == null) {
        if (here == p) {
          count++;
          return;
        }
        counts = new int[GlobalRuntimeImpl.getRuntime().places.size()];
        counts[here] = count;
        count = 1;
      } else if (p >= counts.length) {
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

  /**
   * Must be called to undo the call to {@link #spawn(int)} if the attempt to
   * spawn the task at place p was unsuccessful.
   *
   * @param p
   *          a place
   */
  synchronized void unspawn(int p) {
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

  /**
   * Must be called once a task has completed its execution.
   */
  synchronized void tell() {
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
        GlobalRuntimeImpl.getRuntime().transport.send(id.home.id,
            () -> update(_counts));
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

  /**
   * Reports an uncaught exception to this finish object.
   *
   * @param exception
   *          the uncaught exception
   */
  synchronized void addSuppressed(Throwable exception) {
    final int here = GlobalRuntimeImpl.getRuntime().here;
    if (id == null || id.home.id == here) {
      // root finish
      if (exceptions == null) {
        exceptions = new ArrayList<Throwable>();
      }
      exceptions.add(exception);
    } else {
      // remote finish
      new ExceptionalTask(this, exception).spawn();
    }
  }

  /**
   * Returns true if the code and tasks in the finish scope have completed.
   * <p>
   * This method is intentionally not synchronized.
   *
   * @return true if terminated
   */
  boolean waiting() {
    return count != 0;
  }

  /**
   * Returns the list of exceptions collected by this finish object.
   *
   * @return the collected exceptions
   */
  synchronized List<Throwable> exceptions() {
    return exceptions;
  }

  /**
   * Waits for the termination of the code and tasks in scope of the finish.
   * <p>
   * Blocks the calling thread.
   */
  synchronized void await() {
    while (count != 0) {
      try {
        wait();
      } catch (final InterruptedException e) {
      }
    }
  }

  /**
   * Reallocates the {@link #counts} array to account for larger place counts.
   *
   * @param min
   *          a minimal size for the reallocation
   */
  private void resize(int min) {
    final int[] tmp = new int[Math.max(min,
        GlobalRuntimeImpl.getRuntime().places.size())];
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
    Finish me = (Finish) id.putHereIfAbsent(this);
    if (me == null) {
      me = this;
    }
    synchronized (me) {
      final int here = GlobalRuntimeImpl.getRuntime().here;
      if (id.home.id != here && me.counts == null) {
        me.counts = new int[GlobalRuntimeImpl.getRuntime().places.size()];
      }
    }
    return me;
  }
}
