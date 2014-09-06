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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import apgas.DeadPlaceException;
import apgas.util.GlobalID;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.IMap;
import com.hazelcast.map.AbstractEntryProcessor;

@SuppressWarnings({ "javadoc", "unchecked", "serial" })
final class ResilientFinish implements Finish, Serializable {
  private static final long serialVersionUID = -8238404708052769991L;

  private static final IMap<GlobalID, State> map = GlobalRuntimeImpl
      .getRuntime().transport.<GlobalID, State> getMap("_APGAS_FINISH_");

  GlobalID id;
  List<Throwable> exceptions;

  static class State implements Serializable {
    private static final long serialVersionUID = 4155719029376056951L;

    int count;
    final GlobalID pid;
    List<GlobalID> children;
    List<GlobalID> completed;
    int counts[][]; // TODO dynamic array
    List<Throwable> exceptions;

    State(GlobalID pid, int places) {
      this.pid = pid;
      counts = new int[places][places];
    }
  }

  ResilientFinish(ResilientFinish parent) {
    id = new GlobalID();
    final GlobalID pid = parent == null ? null : parent.id;
    map.put(id, new State(pid, 100)); // TODO fix size
    if (pid != null) {
      final Future<State> f = map.submitToKey(pid,
          new AbstractEntryProcessor<GlobalID, State>() {
            @Override
            public State process(Map.Entry<GlobalID, State> entry) {
              final State state = entry.getValue();
              if (state.completed == null || !state.completed.contains(id)) {
                if (state.children == null) {
                  state.children = new ArrayList<GlobalID>();
                }
                state.children.add(id);
              }
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
  }

  static void purge(int p) {
    for (final GlobalID id : map.keySet()) {
      final Future<State> f = map.submitToKey(id,
          new AbstractEntryProcessor<GlobalID, State>() {
            @Override
            public State process(Map.Entry<GlobalID, State> entry) {
              final State state = entry.getValue();
              for (int i = 0; i < state.counts.length; i++) {
                if (state.counts[p][i] != 0
                    && state.counts[p][i] != Integer.MIN_VALUE) {
                  state.count--;
                }
                state.counts[p][i] = Integer.MIN_VALUE;
                if (state.counts[i][p] != 0
                    && state.counts[i][p] != Integer.MIN_VALUE) {
                  state.count--;
                }
                state.counts[i][p] = Integer.MIN_VALUE;
              }
              entry.setValue(state);
              return state;
            }
          });
      propagate(id, f);
    }
  }

  @Override
  public void submit(int p) {
    final int here = GlobalRuntimeImpl.getRuntime().here;
    if (p == here) {
      return;
    }
    final Future<State> f = map.submitToKey(id,
        new AbstractEntryProcessor<GlobalID, State>() {
          @Override
          public State process(Map.Entry<GlobalID, State> entry) {
            final State state = entry.getValue();
            if (state.counts[p][here] != Integer.MIN_VALUE) {
              if (state.counts[p][here] == 0) {
                state.count++;
              }
              if (state.counts[p][here] == 1) {
                state.count--;
              }
              state.counts[p][here]--;
              if (state.counts[here][here] == 0) {
                state.count++;
              }
              if (state.counts[here][here] == -1) {
                state.count--;
              }
              state.counts[here][here]++;
              entry.setValue(state);
              return state;
            } else {
              // either place p has died and we have assumed tasks in transit
              // from p have been lost so we cannot run this task even if it was
              // successfully delivered
              // or here is considered dead and should not mess with the finish
              return null;
            }
          }
        });
    propagate(id, f);
  }

  @Override
  public void spawn(int p) {
    final int here = GlobalRuntimeImpl.getRuntime().here;
    final Future<State> f = map.submitToKey(id,
        new AbstractEntryProcessor<GlobalID, State>() {
          @Override
          public State process(Map.Entry<GlobalID, State> entry) {
            final State state = entry.getValue();
            if (state.counts[here][p] != Integer.MIN_VALUE) {
              if (state.counts[here][p] == 0) {
                state.count++;
              }
              if (state.counts[here][p] == -1) {
                state.count--;
              }
              state.counts[here][p]++;
              entry.setValue(state);
              return state;
            } else {
              // either we have already processed the death of p from this
              // finish point of view so we cannot spawn at place p under this
              // finish any longer
              // or here is considered dead and should not mess with the finish
              return null;
            }
          }
        });
    propagate(id, f);
  }

  @Override
  public void unspawn(int p) {
    final int here = GlobalRuntimeImpl.getRuntime().here;
    final Future<State> f = map.submitToKey(id,
        new AbstractEntryProcessor<GlobalID, State>() {
          @Override
          public State process(Map.Entry<GlobalID, State> entry) {
            final State state = entry.getValue();
            if (state.counts[here][p] != Integer.MIN_VALUE) {
              if (state.counts[here][p] == 0) {
                state.count++;
              }
              if (state.counts[here][p] == 1) {
                state.count--;
              }
              state.counts[here][p]--;
              entry.setValue(state);
              return state;
            } else {
              return null;
            }
          }
        });
    propagate(id, f);
  }

  @Override
  public void tell() {
    final int here = GlobalRuntimeImpl.getRuntime().here;
    final Future<State> f = map.submitToKey(id,
        new AbstractEntryProcessor<GlobalID, State>() {
          @Override
          public State process(Map.Entry<GlobalID, State> entry) {
            final State state = entry.getValue();
            if (state.counts[here][here] != Integer.MIN_VALUE) {
              if (state.counts[here][here] == 0) {
                state.count++;
              }
              if (state.counts[here][here] == 1) {
                state.count--;
              }
              state.counts[here][here]--;
              entry.setValue(state);
              return state;
            } else {
              return null;
            }
          }
        });
    propagate(id, f);
  }

  @Override
  public void addSuppressed(Throwable exception) {
    final Future<State> f = map.submitToKey(id,
        new AbstractEntryProcessor<GlobalID, State>() {
          @Override
          public State process(Map.Entry<GlobalID, State> entry) {
            final State state = entry.getValue();
            if (state.exceptions == null) {
              state.exceptions = new ArrayList<Throwable>();
            }
            state.exceptions.add(exception);
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

  static void propagate(GlobalID id, Future<State> f) {
    try {
      while (!f.isDone()) {
        try {
          final State state = f.get();
          if (state == null) {
            throw new DeadPlaceException();
          }
          if (state.pid != null && state.count == 0
              && (state.children == null || state.children.size() == 0)) {
            done(id, state.pid);
          }
        } catch (final InterruptedException e) {
        }
      }
    } catch (final ExecutionException e) {
      e.printStackTrace();
    }
  }

  static void done(GlobalID id, GlobalID pid) {
    final Future<State> f = map.submitToKey(pid,
        new AbstractEntryProcessor<GlobalID, State>() {
          @Override
          public Object process(Map.Entry<GlobalID, State> entry) {
            final State state = entry.getValue();
            // TODO state might be null here?
            if (state.children != null) {
              state.children.remove(id);
            } else {
              if (state.completed == null) {
                state.completed = new ArrayList<GlobalID>();
              }
              state.completed.add(id);
            }
            entry.setValue(state);
            return state;
          }
        });
    propagate(pid, f);
  }

  @Override
  public boolean waiting() {
    final State state = map.get(id);
    if (state == null) {
      return false;
    }
    if (state.count > 0 || state.children != null && state.children.size() > 0) {
      return true;
    }
    exceptions = state.exceptions;
    // map.remove(id);
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
            synchronized (ResilientFinish.this) {
              ResilientFinish.this.notifyAll();
            }
          }

          @Override
          public void entryEvicted(EntryEvent<GlobalID, State> event) {
          }
        }, id, true);
    synchronized (this) {
      while (waiting()) {
        try {
          wait();
        } catch (final InterruptedException e) {
        }
      }
    }
    map.removeEntryListener(reg);
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
