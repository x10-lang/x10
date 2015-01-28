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
import java.util.List;
import java.util.Map;

import apgas.NoSuchPlaceException;
import apgas.util.GlobalID;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MapEvent;
import com.hazelcast.map.AbstractEntryProcessor;

@SuppressWarnings({ "javadoc", "serial" })
final class ResilientFinish implements Serializable, Finish {
  private static final long serialVersionUID = -8238404708052769991L;

  private static final IMap<GlobalID, State> map = GlobalRuntimeImpl
      .getRuntime().transport.<GlobalID, State> getMap("apgas:finish");

  final GlobalID id;
  transient List<Throwable> exceptions;

  static final class State implements Serializable {
    private static final long serialVersionUID = 4155719029376056951L;

    List<Integer> deads; // places that have died during the finish
    int count;
    final GlobalID pid; // parent
    List<GlobalID> cids; // children
    List<GlobalID> dids; // dead children
    int counts[][]; // TODO dynamic array
    List<Throwable> exceptions;

    State(GlobalID pid, int here, int p, int places) {
      this.pid = pid;
      counts = new int[places][places];
      counts[here][p] = 1;
      count = 1;
    }
  }

  ResilientFinish(ResilientFinish parent, int p) {
    id = new GlobalID();
    final GlobalID pid = parent == null ? null : parent.id;
    final int here = GlobalRuntimeImpl.getRuntime().here;
    map.set(id, new State(pid, here, p, 100)); // TODO fix size
    if (pid == null) {
      return;
    }
    executeOnKey(pid, (Map.Entry<GlobalID, State> entry) -> {
      final State state = entry.getValue();
      if (state == null || state.deads != null && state.deads.contains(here)) {
        // parent finish thinks this place is dead, exit
        throw new DeadPlaceError();
      }
      if (state.dids == null || !state.dids.contains(id)) {
        if (state.cids == null) {
          state.cids = new ArrayList<GlobalID>();
        }
        state.cids.add(id);
      }
      entry.setValue(state);
      return null;
    });
  }

  static State filter(GlobalID id, State state) {
    if (state.count > 0 || state.cids != null && !state.cids.isEmpty()
        || state.deads == null || !state.deads.contains(id.home.id)) {
      // state is still useful:
      // finish is incomplete or we need to preserve its exceptions
      return state;
    } else {
      // finish is complete and place of finish has died, remove entry
      return null;
    }
  }

  static GlobalID next(State state) {
    if (state.count > 0 || state.cids != null && !state.cids.isEmpty()) {
      return null;
    } else {
      return state.pid;
    }
  }

  static void purge(int p) {
    for (final GlobalID id : map.keySet()) {
      executeOnKey(id, (Map.Entry<GlobalID, State> entry) -> {
        final State state = entry.getValue();
        if (state == null) {
          // entry has been removed already, ignore
          return null;
        }
        if (state.deads == null) {
          state.deads = new ArrayList<Integer>();
        }
        if (state.deads.contains(p)) {
          // death of p has already been processed
          return null;
        }
        state.deads.add(p);
        for (int i = 0; i < state.counts.length; i++) {
          if (state.counts[p][i] != 0) {
            state.count--;
          }
          state.counts[p][i] = 0;
          if (state.counts[i][p] != 0) {
            state.count--;
          }
          state.counts[i][p] = 0;
        }
        entry.setValue(filter(id, state));
        return next(state);
      });
    }
  }

  @Override
  public void submit(int p) {
    final int here = GlobalRuntimeImpl.getRuntime().here;
    if (p == here) {
      // task originated here, no transit stage
      return;
    }
    executeOnKey(id, (Map.Entry<GlobalID, State> entry) -> {
      final State state = entry.getValue();
      if (state == null || state.deads != null && state.deads.contains(here)) {
        // finish thinks this place is dead, exit
        throw new DeadPlaceError();
      }
      if (state.deads != null && state.deads.contains(p)) {
        // source place has died, refuse task but keep place alive
        throw new NoSuchPlaceException();
      }
      if (state.counts[p][here]-- == 0) {
        state.count++;
      }
      if (state.counts[p][here] == 0) {
        state.count--;
      }
      if (state.counts[here][here]++ == 0) {
        state.count++;
      }
      if (state.counts[here][here] == 0) {
        state.count--;
      }
      entry.setValue(filter(id, state));
      return next(state);
    });
  }

  @Override
  public void spawn(int p) {
    final int here = GlobalRuntimeImpl.getRuntime().here;
    executeOnKey(id, (Map.Entry<GlobalID, State> entry) -> {
      final State state = entry.getValue();
      if (state == null || state.deads != null && state.deads.contains(here)) {
        // finish thinks this place is dead, exit
        throw new DeadPlaceError();
      }
      if (state.deads != null && state.deads.contains(p)) {
        // destination place has died, reject task
        throw new NoSuchPlaceException();
      }
      if (state.counts[here][p]++ == 0) {
        state.count++;
      }
      if (state.counts[here][p] == 0) {
        state.count--;
      }
      entry.setValue(filter(id, state));
      return next(state);
    });
  }

  @Override
  public void unspawn(int p) {
    final int here = GlobalRuntimeImpl.getRuntime().here;
    executeOnKey(id, (Map.Entry<GlobalID, State> entry) -> {
      final State state = entry.getValue();
      if (state == null || state.deads != null && state.deads.contains(here)) {
        // finish thinks this place is dead, exit
        throw new DeadPlaceError();
      }
      if (state.deads != null && state.deads.contains(p)) {
        // destination place has died, reject task
        throw new NoSuchPlaceException();
      }
      if (state.counts[here][p]-- == 0) {
        state.count++;
      }
      if (state.counts[here][p] == 0) {
        state.count--;
      }
      entry.setValue(filter(id, state));
      return next(state);
    });
  }

  @Override
  public void tell() {
    final int here = GlobalRuntimeImpl.getRuntime().here;
    executeOnKey(id, (Map.Entry<GlobalID, State> entry) -> {
      final State state = entry.getValue();
      if (state == null || state.deads != null && state.deads.contains(here)) {
        // finish thinks this place is dead, exit
        throw new DeadPlaceError();
      }
      if (state.counts[here][here]-- == 0) {
        state.count++;
      }
      if (state.counts[here][here] == 0) {
        state.count--;
      }
      entry.setValue(filter(id, state));
      return next(state);
    });
  }

  @Override
  public void addSuppressed(Throwable exception) {
    final int here = GlobalRuntimeImpl.getRuntime().here;
    executeOnKey(id, (Map.Entry<GlobalID, State> entry) -> {
      final State state = entry.getValue();
      if (state == null || state.deads != null && state.deads.contains(here)) {
        // finish thinks this place is dead, exit
        throw new DeadPlaceError();
      }
      if (state.exceptions == null) {
        state.exceptions = new ArrayList<Throwable>();
      }
      state.exceptions.add(exception);
      entry.setValue(state);
      return null;
    });
  }

  @FunctionalInterface
  interface Processor extends Serializable {
    GlobalID process(Map.Entry<GlobalID, State> entry);
  }

  static void executeOnKey(GlobalID id, Processor f) {
    try {
      final GlobalID pid = (GlobalID) map.executeOnKey(id,
          new AbstractEntryProcessor<GlobalID, State>() {
            @Override
            public GlobalID process(Map.Entry<GlobalID, State> entry) {
              return f.process(entry);
            }
          });
      if (pid == null) {
        return;
      }
      executeOnKey(pid, (Map.Entry<GlobalID, State> entry) -> {
        final State state = entry.getValue();
        if (state == null) {
          // parent has been purged already
          // stop propagating termination
          return null;
        }
        if (state.cids != null && state.cids.contains(id)) {
          state.cids.remove(id);
        } else {
          if (state.dids == null) {
            state.dids = new ArrayList<GlobalID>();
          }
          if (!state.dids.contains(id)) {
            state.dids.add(id);
          }
        }
        entry.setValue(filter(pid, state));
        return next(state);
      });
    } catch (final DeadPlaceError | HazelcastInstanceNotActiveException e) {
      // this place is dead for the world
      System.exit(42);
    }
  }

  @Override
  public boolean isReleasable() {
    final State state = map.get(id);
    if (state == null) {
      return true;
    }
    if (state.count > 0 || state.cids != null && !state.cids.isEmpty()) {
      return false;
    }
    exceptions = state.exceptions;
    map.delete(id);
    return true;
  }

  @Override
  public boolean block() {
    final String reg = map.addEntryListener(
        new EntryListener<GlobalID, State>() {

          @Override
          public void entryAdded(EntryEvent<GlobalID, State> event) {
          }

          @Override
          public void entryRemoved(EntryEvent<GlobalID, State> event) {
            synchronized (ResilientFinish.this) {
              ResilientFinish.this.notifyAll();
            }
          }

          @Override
          public void entryUpdated(EntryEvent<GlobalID, State> event) {
            synchronized (ResilientFinish.this) {
              ResilientFinish.this.notifyAll();
            }
          }

          @Override
          public void entryEvicted(EntryEvent<GlobalID, State> event) {
          }

          @Override
          public void mapEvicted(MapEvent event) {
          }

          @Override
          public void mapCleared(MapEvent event) {
          }
        }, id, false);
    synchronized (this) {
      while (!isReleasable()) {
        try {
          wait(1000);
        } catch (final InterruptedException e) {
        }
      }
    }
    map.removeEntryListener(reg);
    return true;
  }

  @Override
  public List<Throwable> exceptions() {
    return exceptions;
  }

  @Override
  public int home() {
    return id.home.id;
  }
}
