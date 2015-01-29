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

/**
 * The {@link ExceptionalTask} class is used to transport exceptions across
 * places in the default finish implementation.
 */
final class ExceptionalTask implements SerializableRunnable {
  private static final long serialVersionUID = -4842601206169675750L;

  /**
   * The target finish object.
   */
  Finish finish;

  /**
   * The exception to transport to the root finish object.
   */
  SerializableThrowable t;

  /**
   * The place of the parent task.
   */
  int parent;

  /**
   * Constructs a new {@link ExceptionalTask}.
   *
   * @param finish
   *          a finish instance
   * @param t
   *          an exception
   * @param parent
   *          the place of the parent task
   */
  ExceptionalTask(Finish finish, Throwable t, int parent) {
    this.finish = finish;
    this.t = new SerializableThrowable(t);
    this.parent = parent;
  }

  /**
   * Reports the exception to the finish object.
   * <p>
   * Must be called from the home place of the finish.
   */
  @Override
  public void run() {
    finish.submit(parent);
    finish.addSuppressed(t.t);
    finish.tell();
  }

  /**
   * Spawns this {@link ExceptionalTask} instance.
   */
  void spawn() {
    final int p = finish.home();
    finish.spawn(p);
    GlobalRuntimeImpl.getRuntime().transport.send(p, this);
  }
}
