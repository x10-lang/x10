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
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import apgas.NoSuchPlaceException;
import apgas.util.GlobalID;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.core.MapEvent;
import com.hazelcast.map.AbstractEntryProcessor;

@SuppressWarnings("javadoc")
final class ResilientFinish implements Serializable, Finish {
  private static final long serialVersionUID = -8238404708052769991L;

  /**
   * A factory producing {@link ResilientFinish} instances.
   */
  static class Factory extends Finish.Factory {
    @Override
    ResilientFinish make(Finish parent) {
      return new ResilientFinish(parent);
    }
  }

  final GlobalID id;

  ResilientFinish(Finish parent) {
    final GlobalID id = new GlobalID();
    this.id = id;
    final GlobalID pid = parent instanceof ResilientFinish ? ((ResilientFinish) parent).id
        : null;
    final int here = GlobalRuntimeImpl.getRuntime().here;
    // map.set(id, new ResilientFinishState(pid, here, p));
    ResilientFinishState.executeOnKey(id, state -> {
      return new ResilientFinishState(pid, here);
    });
    if (pid == null) {
      return;
    }
    ResilientFinishState.executeOnKey(pid, state -> {
      if (state == null || state.deads != null && state.deads.contains(here)) {
        // parent finish thinks this place is dead, exit
        throw new DeadPlaceError();
      }
      if (state.dids == null || !state.dids.contains(id)) {
        if (state.cids == null) {
          state.cids = new HashSet<GlobalID>();
        }
        state.cids.add(id);
      }
      return state;
    });
  }

  @Override
  public void submit(int p) {
    final int here = GlobalRuntimeImpl.getRuntime().here;
    if (p == here) {
      // task originated here, no transit stage
      return;
    }
    ResilientFinishState.executeOnKey(id, state -> {
      if (state == null || state.deads != null && state.deads.contains(here)) {
        // finish thinks this place is dead, exit
        throw new DeadPlaceError();
      }
      if (state.deads != null && state.deads.contains(p)) {
        // source place has died, refuse task but keep place alive
        throw new NoSuchPlaceException();
      }
      state.decr(p, here);
      state.incr(here, here);
      return state;
    });
  }

  @Override
  public void spawn(int p) {
    final int here = GlobalRuntimeImpl.getRuntime().here;
    ResilientFinishState.executeOnKey(id, state -> {
      if (state == null || state.deads != null && state.deads.contains(here)) {
        // finish thinks this place is dead, exit
        throw new DeadPlaceError();
      }
      if (state.deads != null && state.deads.contains(p)) {
        // destination place has died, reject task
        throw new NoSuchPlaceException();
      }
      state.incr(here, p);
      return state;
    });
  }

  @Override
  public void unspawn(int p) {
    final int here = GlobalRuntimeImpl.getRuntime().here;
    ResilientFinishState.executeOnKey(id, state -> {
      if (state == null || state.deads != null && state.deads.contains(here)) {
        // finish thinks this place is dead, exit
        throw new DeadPlaceError();
      }
      if (state.deads != null && state.deads.contains(p)) {
        // destination place has died, reject task
        throw new NoSuchPlaceException();
      }
      state.decr(here, p);
      return state;
    });
  }

  @Override
  public void tell(int p) {
    final int here = GlobalRuntimeImpl.getRuntime().here;
    ResilientFinishState.executeOnKey(id, state -> {
      if (state == null || state.deads != null && state.deads.contains(here)) {
        // finish thinks this place is dead, exit
        throw new DeadPlaceError();
      }
      state.decr(here, here);
      return state;
    });
  }

  @Override
  public void addSuppressed(Throwable exception) {
    final int here = GlobalRuntimeImpl.getRuntime().here;
    final SerializableThrowable t = new SerializableThrowable(exception);
    ResilientFinishState.executeOnKey(id, state -> {
      if (state == null || state.deads != null && state.deads.contains(here)) {
        // finish thinks this place is dead, exit
        throw new DeadPlaceError();
      }
      if (state.exceptions == null) {
        state.exceptions = new ArrayList<SerializableThrowable>();
      }
      state.exceptions.add(t);
      return state;
    });
  }

  @Override
  public boolean isReleasable() {
    final int here = GlobalRuntimeImpl.getRuntime().here;
    try {
      return (boolean) ResilientFinishState.map.executeOnKey(id,
          new AbstractEntryProcessor<GlobalID, ResilientFinishState>(false) {
            private static final long serialVersionUID = -7314315521004813385L;

            @Override
            public Boolean process(
                Map.Entry<GlobalID, ResilientFinishState> entry) {
              final ResilientFinishState state = entry.getValue();
              if (state == null || state.deads != null
                  && state.deads.contains(here)) {
                // parent finish thinks this place is dead, exit
                throw new DeadPlaceError();
              }
              return state.counts.size() == 0
                  && (state.cids == null || state.cids.isEmpty());
            }
          });
    } catch (final DeadPlaceError | HazelcastInstanceNotActiveException e) {
      // this place is dead for the world
      System.exit(42);
      throw e;
    }
  }

  @Override
  public boolean block() {
    final String reg = ResilientFinishState.map.addEntryListener(
        new EntryListener<GlobalID, ResilientFinishState>() {

          @Override
          public void entryAdded(
              EntryEvent<GlobalID, ResilientFinishState> event) {
          }

          @Override
          public void entryRemoved(
              EntryEvent<GlobalID, ResilientFinishState> event) {
            synchronized (ResilientFinish.this) {
              ResilientFinish.this.notifyAll();
            }
          }

          @Override
          public void entryUpdated(
              EntryEvent<GlobalID, ResilientFinishState> event) {
            synchronized (ResilientFinish.this) {
              ResilientFinish.this.notifyAll();
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
        }, id, false);
    synchronized (this) {
      while (!isReleasable()) {
        try {
          wait(1000);
        } catch (final InterruptedException e) {
        }
      }
    }
    ResilientFinishState.map.removeEntryListener(reg);
    return true;
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Throwable> exceptions() {
    final int here = GlobalRuntimeImpl.getRuntime().here;
    try {
      final List<SerializableThrowable> exceptions = (List<SerializableThrowable>) ResilientFinishState.map
          .executeOnKey(id,
              new AbstractEntryProcessor<GlobalID, ResilientFinishState>() {
                private static final long serialVersionUID = -6913737556384372242L;

                @Override
                public List<SerializableThrowable> process(
                    Map.Entry<GlobalID, ResilientFinishState> entry) {
                  final ResilientFinishState state = entry.getValue();
                  if (state == null || state.deads != null
                      && state.deads.contains(here)) {
                    // parent finish thinks this place is dead, exit
                    throw new DeadPlaceError();
                  }
                  entry.setValue(null);
                  return state.exceptions;
                }
              });
      if (exceptions == null) {
        return null;
      }
      final List<Throwable> list = new ArrayList<Throwable>();
      for (final SerializableThrowable t : exceptions) {
        list.add(t.t);
      }
      return list;
    } catch (final DeadPlaceError | HazelcastInstanceNotActiveException e) {
      // this place is dead for the world
      System.exit(42);
      throw e;
    }
  }
}
