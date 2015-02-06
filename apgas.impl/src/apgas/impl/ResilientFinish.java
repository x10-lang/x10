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

import apgas.NoSuchPlaceException;
import apgas.util.GlobalID;

/**
 * The {@link ResilientFinish} class implements a finish construct resilient to
 * place failure.
 */
class ResilientFinish implements Serializable, Finish {
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

  /**
   * The unique id of this finish instance.
   */
  protected GlobalID id;

  /**
   * An empty constructor for subclassing.
   */
  protected ResilientFinish() {
  }

  private ResilientFinish(Finish parent) {
    init(parent);
  }

  /**
   * Does the buld of the finish construction.
   *
   * @param parent
   *          the parent finish instance
   */
  protected void init(Finish parent) {
    final GlobalID id = new GlobalID();
    this.id = id;
    final GlobalID pid = parent instanceof ResilientFinish ? ((ResilientFinish) parent).id
        : null;
    final int here = GlobalRuntimeImpl.getRuntime().here;
    // map.set(id, new ResilientFinishState(pid, here, p));
    ResilientFinishState.update(id, state -> {
      return new ResilientFinishState(pid, here);
    });
    if (pid == null) {
      return;
    }
    ResilientFinishState.update(pid, state -> {
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
    ResilientFinishState.update(id, state -> {
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
    ResilientFinishState.update(id, state -> {
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
    ResilientFinishState.update(id, state -> {
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
    ResilientFinishState.update(id, state -> {
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
    ResilientFinishState.update(id, state -> {
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
    return isDone();
  }

  private boolean isDone() {
    final int here = GlobalRuntimeImpl.getRuntime().here;
    return ResilientFinishState.execute(id,
        false, // no need to apply on backup
        entry -> {
          final ResilientFinishState state = entry.getValue();
          if (state == null || state.deads != null
              && state.deads.contains(here)) {
            // parent finish thinks this place is dead, exit
            throw new DeadPlaceError();
          }
          return state.counts.size() == 0
              && (state.cids == null || state.cids.isEmpty());
        });
  }

  @Override
  public boolean block() {
    final String reg = ResilientFinishState.addListener(this);
    synchronized (this) {
      while (!isDone()) {
        try {
          wait(1000);
        } catch (final InterruptedException e) {
        }
      }
    }
    ResilientFinishState.removeListener(reg);
    return true;
  }

  @Override
  public List<Throwable> exceptions() {
    final int here = GlobalRuntimeImpl.getRuntime().here;
    final List<SerializableThrowable> exceptions = ResilientFinishState
        .execute(
            id,
            entry -> {
              final ResilientFinishState state = entry.getValue();
              if (state == null || state.deads != null
                  && state.deads.contains(here)) {
                // parent finish thinks this place is dead, exit
                throw new DeadPlaceError();
              }
              entry.setValue(null);
              return state.exceptions;
            });
    if (exceptions == null) {
      return null;
    }
    final List<Throwable> list = new ArrayList<Throwable>();
    for (final SerializableThrowable t : exceptions) {
      list.add(t.t);
    }
    return list;
  }
}
