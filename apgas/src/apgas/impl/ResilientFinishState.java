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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import apgas.DeadPlaceException;
import apgas.Place;
import apgas.util.GlobalID;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.core.MapEvent;
import com.hazelcast.map.AbstractEntryProcessor;
import com.hazelcast.query.Predicate;

/**
 * The {@link ResilientFinishState} class defines the entry associated with a
 * finish object in the resilient store.
 *
 */
final class ResilientFinishState implements Serializable {
  private static final long serialVersionUID = 756668504413905415L;

  /**
   * The set of places that have died during this finish execution.
   */
  Set<Integer> deads;

  /**
   * The ID of the parent resilient finish object if any.
   */
  final GlobalID pid;

  /**
   * The IDs of the live immediatly nested finish objects.
   */
  Set<GlobalID> cids;

  /**
   * The IDs of the dead immediatly nested finish objects.
   */
  Set<GlobalID> dids;

  /**
   * The exceptions reported to this finish so far.
   */
  List<SerializableThrowable> exceptions;

  /**
   * The task counts.
   */
  final Map<Long, Integer> counts = new HashMap<Long, Integer>();

  /**
   * The largest place ID encountered so far.
   */
  int max;

  /**
   * Computes the index of the (p, q) counter.
   *
   * @param p
   *          source place ID
   * @param q
   *          destination place ID
   * @return the computed index
   */
  private static long index(int p, int q) {
    return (((long) p) << 32) + q;
  }

  /**
   * Clears (p, q) counter.
   *
   * @param p
   *          source place ID
   * @param q
   *          destination place ID
   */
  void clear(int p, int q) {
    counts.remove(index(p, q));
  }

  /**
   * Update counter by delta
   *
   * @param index
   *          the index of the counter
   * @param delta
   *          the delta
   */
  private void add(long index, int delta) {
    final int v = counts.getOrDefault(index, 0) + delta;
    if (v == 0) {
      counts.remove(index);
    } else {
      counts.put(index, v);
    }
  }

  /**
   * Increments (p, q) counter.
   *
   * @param p
   *          source place ID
   * @param q
   *          destination place ID
   */
  void incr(int p, int q) {
    if (p > max) {
      max = p;
    }
    if (q > max) {
      max = q;
    }
    add(index(p, q), 1);
  }

  /**
   * Decrements (p, q) counter.
   *
   * @param p
   *          source place ID
   * @param q
   *          destination place ID
   */
  void decr(int p, int q) {
    if (p > max) {
      max = p;
    }
    if (q > max) {
      max = q;
    }
    add(index(p, q), -1);
  }

  /**
   * Constructs a resilient finish state.
   *
   * @param pid
   *          the ID of the parent resilient finish if any
   * @param p
   *          the place ID of the finish
   */
  ResilientFinishState(GlobalID pid, int p) {
    max = p;
    this.pid = pid;
    counts.put(index(p, p), 1);
  }

  /**
   * Updates the finish states when a place dies.
   *
   * @param p
   *          the dead place ID
   */
  static void purge(int p) {
    final int here = GlobalRuntimeImpl.getRuntime().here;
    // only process finish states for the current place and the dead place
    final Predicate<GlobalID, ResilientFinishState> predicate = entry -> {
      return entry.getKey().home.id == here || entry.getKey().home.id == p;
    };
    for (final GlobalID id : GlobalRuntimeImpl.getRuntime().resilientFinishMap
        .keySet(predicate)) {
      submit(id, state -> {
        if (state == null) {
          // entry has been removed already, ignore
          return null;
        }
        if (state.deads == null) {
          state.deads = new HashSet<Integer>();
        }
        if (state.deads.contains(p)) {
          // death of p has already been processed
          return null;
        }
        state.deads.add(p);
        final int count = state.counts.size();
        for (int i = 0; i <= state.max; i++) {
          state.clear(p, i);
          state.clear(i, p);
        }
        if (state.counts.size() < count) {
          if (state.exceptions == null) {
            state.exceptions = new ArrayList<SerializableThrowable>();
          }
          state.exceptions.add(new SerializableThrowable(
              new DeadPlaceException(new Place(p))));
        }
        return state;
      });
    }
  }

  /**
   * A function to process finish states.
   */
  @FunctionalInterface
  static interface Processor extends Serializable {
    /**
     * The function.
     *
     * @param state
     *          the state to process
     * @return the updated state or null
     */
    ResilientFinishState process(ResilientFinishState state);
  }

  /**
   * Updates a resilient finish state.
   *
   * @param id
   *          the finish state ID to update
   * @param processor
   *          the function to apply
   */
  static void update(GlobalID id, Processor processor) {
    execute(id, entry -> {
      entry.setValue(processor.process(entry.getValue()));
      return null;
    });
  }

  /**
   * An entry processor.
   *
   * @param <T>
   *          the return type of the processor
   */
  @FunctionalInterface
  static interface EntryProcessor<T> extends Serializable {
    /**
     * The function.
     *
     * @param entry
     *          the entry to process
     * @return the result
     */
    T process(Map.Entry<GlobalID, ResilientFinishState> entry);
  }

  /**
   * Apply an entry processor to an entry.
   *
   * @param <T>
   *          the return type of the processor
   * @param id
   *          the ID of the entry
   * @param processor
   *          the processor
   * @return the result
   */
  static <T> T execute(GlobalID id, EntryProcessor<T> processor) {
    return execute(id, true, processor);
  }

  /**
   * Apply an entry processor to an entry.
   *
   * @param <T>
   *          the return type of the processor
   * @param id
   *          the ID of the entry
   * @param applyOnBackup
   *          whether to apply the processor on backup entries
   * @param processor
   *          the processor
   * @return the result
   */
  @SuppressWarnings("unchecked")
  static <T> T execute(GlobalID id, boolean applyOnBackup,
      EntryProcessor<T> processor) {
    try {
      return (T) GlobalRuntimeImpl.getRuntime().resilientFinishMap
          .executeOnKey(id,
              new AbstractEntryProcessor<GlobalID, ResilientFinishState>(
                  applyOnBackup) {
                private static final long serialVersionUID = -8787905766218374656L;

                @Override
                public T process(Map.Entry<GlobalID, ResilientFinishState> entry) {
                  return processor.process(entry);
                }
              });
    } catch (final DeadPlaceError | HazelcastInstanceNotActiveException e) {
      // this place is dead for the world
      System.exit(42);
      throw e;
    }
  }

  /**
   * Updates a resilient finish state asynchronously and propagates termination
   * to parent if necessary.
   *
   * @param id
   *          the finish state ID to update
   * @param processor
   *          the function to apply
   */
  static void submit(GlobalID id, Processor processor) {
    GlobalRuntimeImpl.getRuntime().resilientFinishMap.submitToKey(id,
        new AbstractEntryProcessor<GlobalID, ResilientFinishState>(true) {
          private static final long serialVersionUID = 1754842053698962361L;

          @Override
          public GlobalID process(
              Map.Entry<GlobalID, ResilientFinishState> entry) {
            final ResilientFinishState state = processor.process(entry
                .getValue());
            if (state == null) {
              return null;
            }
            if (state.counts.size() > 0 || state.cids != null
                && !state.cids.isEmpty() || state.deads == null
                || !state.deads.contains(id.home.id)) {
              // state is still useful:
              // finish is incomplete or we need to preserve its exceptions
              entry.setValue(state);
            } else {
              // finish is complete and place of finish has died, remove entry
              entry.setValue(null);
            }
            if (state.counts.size() > 0 || state.cids != null
                && !state.cids.isEmpty()) {
              return null;
            } else {
              return state.pid;
            }
          }
        }, new ExecutionCallback<GlobalID>() {

          @Override
          public void onResponse(GlobalID pid) {
            if (pid == null) {
              return;
            }
            submit(pid, state -> {
              if (state == null) {
                // parent has been purged already
                // stop propagating termination
                return null;
              }
              if (state.cids != null && state.cids.contains(id)) {
                state.cids.remove(id);
              } else {
                if (state.dids == null) {
                  state.dids = new HashSet<GlobalID>();
                }
                if (!state.dids.contains(id)) {
                  state.dids.add(id);
                }
              }
              return state;
            });
          }

          @Override
          public void onFailure(Throwable t) {
            if (t instanceof DeadPlaceError
                || t instanceof HazelcastInstanceNotActiveException) {
              // this place is dead for the world
              System.exit(42);
            }
          }
        });
  }

  /**
   * Registers a resilient store listener.
   * <p>
   * The finish instance is notified when its entry is updated or removed from
   * the resilient store.
   *
   * @param finish
   *          the finish instance to register
   * @return the unique id of the registration
   */
  static String addListener(ResilientFinish finish) {
    return GlobalRuntimeImpl.getRuntime().resilientFinishMap.addEntryListener(
        new EntryListener<GlobalID, ResilientFinishState>() {

          @Override
          public void entryAdded(
              EntryEvent<GlobalID, ResilientFinishState> event) {
          }

          @Override
          public void entryRemoved(
              EntryEvent<GlobalID, ResilientFinishState> event) {
            synchronized (finish) {
              finish.notifyAll();
            }
          }

          @Override
          public void entryUpdated(
              EntryEvent<GlobalID, ResilientFinishState> event) {
            synchronized (finish) {
              finish.notifyAll();
            }
          }

          @Override
          public void entryEvicted(
              EntryEvent<GlobalID, ResilientFinishState> event) {
          }

          @Override
          public void mapEvicted(MapEvent event) {
          }

          @Override
          public void mapCleared(MapEvent event) {
          }
        }, finish.id, false);
  }

  /**
   * Deregisters a listener.
   *
   * @param registration
   *          the unique id of the registration
   */
  static void removeListener(String registration) {
    GlobalRuntimeImpl.getRuntime().resilientFinishMap
        .removeEntryListener(registration);
  }
}
