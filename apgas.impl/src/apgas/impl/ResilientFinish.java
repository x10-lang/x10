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

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import apgas.util.GlobalID;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.IMap;
import com.hazelcast.map.AbstractEntryProcessor;

@SuppressWarnings("javadoc")
final class ResilientFinish implements Finish, Serializable {
  private static final long serialVersionUID = -8238404708052769991L;

  private static final IMap<GlobalID, State> map = GlobalRuntimeImpl
      .getRuntime().transport.<GlobalID, State> getMap("APGAS");

  GlobalID id;
  boolean finished;

  static class State implements Serializable {
    private static final long serialVersionUID = 4155719029376056951L;

    int counts[][];

    State(int places) {
      counts = new int[places][places];
    }
  }

  ResilientFinish() {
    id = new GlobalID();
    map.put(id, new State(GlobalRuntimeImpl.getRuntime().allPlaces.size()));
  }

  @Override
  public int home() {
    return id.home.id;
  }

  @Override
  public void submit(int p) {
    final int here = GlobalRuntimeImpl.getRuntime().here;
    if (p == here) {
      return;
    }
    final Future f = map.submitToKey(id,
        new AbstractEntryProcessor<GlobalID, State>() {
          @Override
          public Object process(Map.Entry<GlobalID, State> entry) {
            final State state = entry.getValue();
            state.counts[here][here]++;
            state.counts[p][here]--;
            entry.setValue(state);
            return null;
          }
        });
    try {
      while (!f.isDone()) {
        try {
          f.get();
        } catch (final InterruptedException e) {
        }
      }
    } catch (final ExecutionException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void spawn(int p) {
    final int here = GlobalRuntimeImpl.getRuntime().here;
    final Future f = map.submitToKey(id,
        new AbstractEntryProcessor<GlobalID, State>() {
          @Override
          public Object process(Map.Entry<GlobalID, State> entry) {
            final State state = entry.getValue();
            state.counts[here][p]++;
            entry.setValue(state);
            return null;
          }
        });
    try {
      while (!f.isDone()) {
        try {
          f.get();
        } catch (final InterruptedException e) {
        }
      }
    } catch (final ExecutionException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void unspawn(int p) {
    // TODO Auto-generated method stub

  }

  @Override
  public void tell() {
    final int here = GlobalRuntimeImpl.getRuntime().here;
    final Future f = map.submitToKey(id,
        new AbstractEntryProcessor<GlobalID, State>() {
          @Override
          public Object process(Map.Entry<GlobalID, State> entry) {
            final State state = entry.getValue();
            state.counts[here][here]--;
            entry.setValue(state);
            return null;
          }
        });
    try {
      while (!f.isDone()) {
        try {
          f.get();
        } catch (final InterruptedException e) {
        }
      }
    } catch (final ExecutionException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void addSuppressed(Throwable exception) {
  }

  @Override
  public boolean waiting() {
    final State state = map.get(id);
    for (int i = 0; i < state.counts.length; i++) {
      for (int j = 0; j < state.counts[i].length; j++) {
        if (state.counts[i][j] != 0) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public void await() {
    final String reg = map.addEntryListener(
        new EntryListener<GlobalID, State>() {

          @Override
          public void entryAdded(EntryEvent<GlobalID, State> event) {
          }

          @Override
          public void entryRemoved(EntryEvent<GlobalID, State> event) {
          }

          @Override
          public void entryUpdated(EntryEvent<GlobalID, State> event) {
            final State state = event.getValue();
            for (int i = 0; i < state.counts.length; i++) {
              for (int j = 0; j < state.counts[i].length; j++) {
                if (state.counts[i][j] != 0) {
                  return;
                }
              }
            }
            synchronized (ResilientFinish.this) {
              finished = true;
              ResilientFinish.this.notifyAll();
            }
          }

          @Override
          public void entryEvicted(EntryEvent<GlobalID, State> event) {
          }
        }, id, true);
    if (waiting()) {
      synchronized (this) {
        while (!finished) {
          try {
            wait();
          } catch (final InterruptedException e) {
          }
        }
      }
    }
    map.removeEntryListener(reg);
  }

  @Override
  public List<Throwable> exceptions() {
    return null;
  }
}
