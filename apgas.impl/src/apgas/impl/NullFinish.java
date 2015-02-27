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
import java.util.List;

/**
 * The {@link NullFinish} class implements a dummy finish.
 */
final class NullFinish implements Serializable, Finish {
  private static final long serialVersionUID = -6486525914605983562L;

  /**
   * The singleton {@link NullFinish} instance.
   */
  static final NullFinish SINGLETON = new NullFinish();

  private NullFinish() {
  }

  @Override
  public void submit(int p) {
  }

  @Override
  public void spawn(int p) {
  }

  @Override
  public void unspawn(int p) {
  }

  @Override
  public void tell(int p) {
  }

  @Override
  public void addSuppressed(Throwable exception) {
  }

  @Override
  public boolean isReleasable() {
    return true;
  }

  @Override
  public boolean block() {
    return true;
  }

  @Override
  public List<Throwable> exceptions() {
    return null;
  }
}
