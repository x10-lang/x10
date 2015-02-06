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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import apgas.util.GlobalID;

import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.core.IMap;
import com.hazelcast.map.AbstractEntryProcessor;
import com.hazelcast.query.Predicate;

@SuppressWarnings("javadoc")
final class ResilientFinishState implements Serializable {
  private static final long serialVersionUID = 756668504413905415L;

  static final IMap<GlobalID, ResilientFinishState> map = GlobalRuntimeImpl
      .getRuntime().transport
      .<GlobalID, ResilientFinishState> getMap("apgas:finish");

  Set<Integer> deads; // places that have died during the finish
  final GlobalID pid; // parent
  Set<GlobalID> cids; // children
  Set<GlobalID> dids; // dead children
  List<SerializableThrowable> exceptions;
  final Map<Long, Integer> counts = new HashMap<Long, Integer>();
  int max; // max place encountered

  static long index(int p, int q) {
    return (((long) p) << 32) + q;
  }

  void clear(int p, int q) {
    counts.remove(index(p, q));
  }

  void add(long index, int delta) {
    final int v = counts.getOrDefault(index, 0) + delta;
    if (v == 0) {
      counts.remove(index);
    } else {
      counts.put(index, v);
    }
  }

  void incr(int p, int q) {
    if (p > max) {
      max = p;
    }
    if (q > max) {
      max = q;
    }
    add(index(p, q), 1);
  }

  void decr(int p, int q) {
    if (p > max) {
      max = p;
    }
    if (q > max) {
      max = q;
    }
    add(index(p, q), -1);
  }

  ResilientFinishState(GlobalID pid, int p) {
    max = p;
    this.pid = pid;
    counts.put(index(p, p), 1);
  }

  static void purge(int p) {
    final int here = GlobalRuntimeImpl.getRuntime().here;
    // only process finish states for the current place and the dead place
    final Predicate<GlobalID, ResilientFinishState> predicate = entry -> {
      return entry.getKey().home.id == here || entry.getKey().home.id == p;
    };
    for (final GlobalID id : map.keySet(predicate)) {
      executeOnKey(id, state -> {
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
        for (int i = 0; i <= state.max; i++) {
          state.clear(p, i);
          state.clear(i, p);
        }
        return state;
      });
    }
  }

  @FunctionalInterface
  static interface Processor extends Serializable {
    ResilientFinishState process(ResilientFinishState state);
  }

  static void executeOnKey(GlobalID id, Processor f) {
    try {
      final GlobalID pid = (GlobalID) map.executeOnKey(id,
          new AbstractEntryProcessor<GlobalID, ResilientFinishState>() {
            private static final long serialVersionUID = 6777775768226692449L;

            @Override
            public GlobalID process(
                Map.Entry<GlobalID, ResilientFinishState> entry) {
              final ResilientFinishState state = f.process(entry.getValue());
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
          });
      if (pid == null) {
        return;
      }
      executeOnKey(pid, state -> {
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
    } catch (final DeadPlaceError | HazelcastInstanceNotActiveException e) {
      // this place is dead for the world
      System.exit(42);
    }
  }
}
