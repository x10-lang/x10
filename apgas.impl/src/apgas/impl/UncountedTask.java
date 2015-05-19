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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.RecursiveAction;

import apgas.DeadPlaceException;
import apgas.SerializableJob;

/**
 * The {@link UncountedTask} class represents an uncounted task.
 *
 * <p>
 * This class implements task serialization and handles errors in the
 * serialization process.
 */
final class UncountedTask extends RecursiveAction implements
    SerializableRunnable {
  private static final long serialVersionUID = 5031683857632950143L;

  /**
   * The function to run.
   */
  private SerializableJob f;

  /**
   * Constructs a new {@link UncountedTask}.
   *
   * @param f
   *          the function to run
   */
  UncountedTask(SerializableJob f) {
    this.f = f;
  }

  /**
   * Submits the task for asynchronous execution.
   */
  @Override
  public void run() {
    GlobalRuntimeImpl.getRuntime().execute(this);
  }

  @Override
  protected void compute() {
    try {
      f.run();
    } catch (final Throwable t) {
      System.err.println("[APGAS] Uncaught exception in uncounted task");
      System.err.println("[APGAS] Caused by: " + t);
      System.err.println("[APGAS] Ignoring...");
    }
  }

  /**
   * Submits the task for asynchronous uncounted execution at place p.
   * <p>
   * If serialization fails, the task is dropped. The exception is logged to
   * System.err and masked unless APGAS_SERIALIZATION_EXCEPTION is set to
   * "true".
   *
   * @param p
   *          the place ID
   */
  void uncountedasyncat(int p) {
    try {
      GlobalRuntimeImpl.getRuntime().transport.send(p, this);
    } catch (final Throwable e) {
      if (GlobalRuntimeImpl.getRuntime().serializationException
          || e instanceof DeadPlaceException) {
        throw e;
      } else {
        final StackTraceElement elm = new Exception().getStackTrace()[3];
        System.err
            .println("[APGAS] Failed to spawn an uncounted task at place " + p
                + " (" + elm.getFileName() + ":" + elm.getLineNumber() + ")");
        System.err.println("[APGAS] Caused by: " + e.getCause());
        System.err.println("[APGAS] Ignoring...");
      }
    }
  }

  private static final SerializableJob NULL = () -> {
  };

  /**
   * Deserializes the task.
   * <p>
   * If deserialization fails, the task is dropped. The exception is logged to
   * System.err.
   *
   * @param in
   *          the object input stream
   * @throws IOException
   *           if I/O errors occur
   * @throws ClassNotFoundException
   *           if the class of the serialized object cannot be found
   */
  private void readObject(ObjectInputStream in) throws IOException,
      ClassNotFoundException {
    try {
      f = (SerializableJob) in.readObject();
    } catch (final Throwable e) {
      final StackTraceElement elm = e.getStackTrace()[0];
      System.err
          .println("[APGAS] Failed to receive an uncounted task at place "
              + GlobalRuntimeImpl.getRuntime().here + " (" + elm.getFileName()
              + ":" + elm.getLineNumber() + ")");
      System.err.println("[APGAS] Caused by: " + e);
      System.err.println("[APGAS] Ignoring...");
      f = NULL;
    }
  }
}
