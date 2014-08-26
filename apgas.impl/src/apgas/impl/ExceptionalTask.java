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

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * The {@link ExceptionalTask} class is used to transport exceptions across
 * places.
 * <p>
 * Exceptions that are not serializable are automatically replaced by
 * {@link java.io.NotSerializableException} exceptions.
 */
public class ExceptionalTask implements SerializableRunnable {
  private static final long serialVersionUID = -4842601206169675750L;

  /**
   * The target finish object.
   */
  Finish finish;

  /**
   * The exception to transport to the root finish object.
   */
  Throwable t;

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
    this.t = t;
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
    finish.addSuppressed(t);
    finish.tell();
  }

  private void writeObject(ObjectOutputStream out) throws IOException {
    out.writeObject(finish);
    out.writeInt(parent);
    final NotSerializableException e = new NotSerializableException(t
        .getClass().getCanonicalName());
    e.setStackTrace(t.getStackTrace());
    out.writeObject(e);
    try {
      out.writeObject(t);
    } catch (final Throwable t) {
    }
  }

  private void readObject(ObjectInputStream in) throws IOException,
      ClassNotFoundException {
    finish = (Finish) in.readObject();
    parent = in.readInt();
    t = (Throwable) in.readObject();
    try {
      t = (Throwable) in.readObject();
    } catch (final Throwable e) {
    }
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
