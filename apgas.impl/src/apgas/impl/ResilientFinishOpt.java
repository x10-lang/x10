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

/**
 * The {link ResilientFinishOpt} encapsulates a lazily initialized resilient
 * finish instance.
 */
final class ResilientFinishOpt implements Serializable, Finish {
  private static final long serialVersionUID = -4266344084043843296L;

  /**
   * A factory producing {@link ResilientFinishOpt} instances.
   */
  static class Factory extends Finish.Factory {
    @Override
    ResilientFinishOpt make(Finish parent) {
      return new ResilientFinishOpt(parent);
    }
  }

  private final ResilientFinish finish = new ResilientFinish();

  // for all instances
  private transient int local; // local task count - 1

  // for root instance
  private transient final Finish parent; // parent finish
  private transient List<Throwable> exceptions; // root exceptions

  private ResilientFinishOpt(Finish parent) {
    this.parent = parent;
  }

  private void init() {
    synchronized (finish) {
      if (finish.id != null) {
        return; // already initialized
      }
      if (parent instanceof ResilientFinishOpt) {
        ((ResilientFinishOpt) parent).init(); // initializes parents first
      }
      finish.init(parent); // initializes resilient state
    }
  }

  @Override
  public void submit(int p) {
    finish.submit(p); // no op if p == here
  }

  @Override
  public void spawn(int p) {
    final int here = GlobalRuntimeImpl.getRuntime().here;
    if (p == here) {
      synchronized (this) {
        local++; // increment per-instance local task count
      }
      return;
    }
    init(); // initialize resilient state if needed
    finish.spawn(p); // update resilient state
  }

  @Override
  public void unspawn(int p) {
    finish.unspawn(p); // must be remote
  }

  @Override
  public void tell(int p) {
    synchronized (this) {
      if (--local >= 0) {
        return; // not done with local subtasks of this finish instance
      }
      notifyAll(); // unblock
    }
    synchronized (finish) {
      if (finish.id == null) {
        return; // not resilient
      }
    }
    finish.tell(p); // update resilient state
  }

  @Override
  public void addSuppressed(Throwable exception) {
    if (parent != null) {
      // root finish
      synchronized (this) {
        if (exceptions == null) {
          exceptions = new ArrayList<Throwable>();
        }
        exceptions.add(exception); // add exeption to this instance
      }
      return;
    }
    finish.addSuppressed(exception); // add exception to resilient state
  }

  @Override
  public boolean isReleasable() {
    synchronized (this) {
      if (local >= 0) {
        return false; // not done with local subtasks of this finish instance
      }
    }
    synchronized (finish) {
      if (finish.id == null) {
        return true; // not resilient, we are done
      }
    }
    return finish.isReleasable(); // check resilient state
  }

  @Override
  public boolean block() {
    synchronized (this) {
      while (local >= 0) {
        try { // not done with local subtasks of this finish instance
          wait(1000);
        } catch (final InterruptedException e) {
        }
      }
    }
    synchronized (finish) {
      if (finish.id == null) {
        return true; // not resilient, we are done
      }
    }
    return finish.block(); // block on resilient state
  }

  @Override
  public List<Throwable> exceptions() {
    synchronized (finish) {
      if (finish.id == null) {
        return exceptions; // not resilient, this is it
      }
    }
    // fetch exceptions from resilient store
    final List<Throwable> list = finish.exceptions();
    // concatenate
    if (list == null) {
      return exceptions;
    }
    if (exceptions == null) {
      return list;
    }
    exceptions.addAll(list);
    return exceptions;
  }
}
