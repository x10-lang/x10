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
import java.io.ObjectOutputStream;

import apgas.Job;
import apgas.NoSuchPlaceException;
import apgas.Place;

/**
 * The {@link Task} class represents an APGAS task.
 *
 * <p>
 * This class implements task serialization and handles errors in the
 * serialization process.
 */
final class Task implements SerializableRunnable {
  private static final long serialVersionUID = 5288338719050788305L;

  /**
   * The finish object for this {@link Task} instance.
   */
  Finish finish;

  /**
   * The function to run.
   */
  private Job f;

  /**
   * The place of the parent task.
   */
  int parent;

  /**
   * Constructs a new {@link Task}.
   *
   * @param f
   *          the function to run
   * @param finish
   *          the finish object for this task
   * @param parent
   *          the place of the parent task
   */
  Task(Finish finish, Job f, int parent) {
    this.finish = finish;
    this.f = f;
    this.parent = parent;
  }

  /**
   * Submits the task for asynchronous execution.
   */
  @Override
  public void run() {
    try {
      async(null);
    } catch (final NoSuchPlaceException e) {
      // source place has died while task was in transit, discard
    }
  }

  /**
   * Runs the task and notify the task's finish upon termination.
   *
   * @param worker
   *          the worker thread running the task (cannot be null)
   */
  void run(Worker worker) {
    worker.task = this;
    try {
      f.run();
    } catch (final Throwable t) {
      finish.addSuppressed(t);
    }
    finish.tell();
  }

  /**
   * Runs the tasks, notify the task's finish upon termination, and wait for the
   * task's finish to terminate.
   *
   * @param worker
   *          the worker thread running the task or null if not a worker thread
   */
  void finish(Worker worker) {
    if (worker == null) {
      async(worker);
      finish.await();
    } else {
      final Task savedTask = worker.task;
      run(worker);
      worker.help(finish);
      worker.task = savedTask;
    }
  }

  /**
   * Submits the task for asynchronous execution.
   *
   * @param worker
   *          the worker doing the submission or null if not a worker thread
   */
  void async(Worker worker) {
    finish.submit(parent);
    GlobalRuntimeImpl.getRuntime().scheduler.submit(worker, this);
  }

  /**
   * Submits the task for asynchronous execution at place p.
   * <p>
   * If serialization fails, the task is dropped and the task's finish is
   * notified. The exception is logged to System.err and masked unless
   * APGAS_SERIALIZATION_EXCEPTION is set to "true".
   *
   * @param p
   *          the place of execution
   */
  void asyncat(Place p) {
    try {
      GlobalRuntimeImpl.getRuntime().transport.send(p.id, this);
    } catch (final Throwable e) {
      finish.unspawn(p.id);
      if (GlobalRuntimeImpl.getRuntime().serializationException
          || e instanceof NoSuchPlaceException) {
        throw e;
      } else {
        final StackTraceElement elm = new Exception().getStackTrace()[3];
        System.err
            .println("[APGAS] Failed to spawn a remote async at place " + p.id
                + " (" + elm.getFileName() + ":" + elm.getLineNumber() + ")");
        System.err.println("[APGAS] Caused by: " + e.getCause());
        System.err.println("[APGAS] Ignoring...");
      }
    }
  }

  /**
   * Serializes the task.
   *
   * @param out
   *          the object output stream
   *
   * @throws IOException
   *           if I/O errors occur
   */
  private void writeObject(ObjectOutputStream out) throws IOException {
    out.writeObject(finish);
    out.writeInt(parent);
    out.writeObject(f);
  }

  private static final Job NULL = () -> {
  };

  /**
   * Deserializes the task.
   * <p>
   * If deserialization fails, a dummy task is returned. The exception is logged
   * to System.err and masked unless APGAS_SERIALIZATION_EXCEPTION is set to
   * "true".
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
    finish = (Finish) in.readObject();
    parent = in.readInt();
    try {
      f = (Job) in.readObject();
    } catch (final Throwable e) {
      if (GlobalRuntimeImpl.getRuntime().serializationException) {
        finish.addSuppressed(e);
      } else {
        final StackTraceElement elm = e.getStackTrace()[0];
        System.err.println("[APGAS] Failed to receive remote async at place "
            + GlobalRuntimeImpl.getRuntime().here + " (" + elm.getFileName()
            + ":" + elm.getLineNumber() + ")");
        System.err.println("[APGAS] Caused by: " + e);
        System.err.println("[APGAS] Ignoring...");
      }
      f = NULL;
    }
  }
}
