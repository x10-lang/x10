/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2016.
 */

package apgas.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import apgas.DeadPlaceException;
import apgas.Job;
import apgas.SerializableJob;

/**
 * The {@link Task} class represents an APGAS task.
 *
 * <p>
 * This class implements task serialization and handles errors in the
 * serialization process.
 */
final class Task extends RecursiveAction
    implements SerializableRunnable, KryoSerializable {
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
    } catch (final DeadPlaceException e) {
      // source place has died while task was in transit, discard
    }
  }

  /**
   * Runs the task and notify the task's finish upon termination.
   */
  @Override
  protected void compute() {
    final Worker worker = (Worker) Thread.currentThread();
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
      try {
        ForkJoinPool.managedBlock(finish);
      } catch (final InterruptedException e) {
      }
    } else {
      final Task savedTask = worker.task;
      compute();
      Task t;
      while (!finish.isReleasable()
          && (t = (Task) ForkJoinTask.peekNextLocalTask()) != null
          && finish == t.finish && t.tryUnfork()) {
        t.compute();
      }
      try {
        ForkJoinPool.managedBlock(finish);
      } catch (final InterruptedException e) {
      }
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
    if (worker == null) {
      GlobalRuntimeImpl.getRuntime().execute(this);
    } else {
      fork();
    }
  }

  /**
   * Submits the task for asynchronous execution at place p.
   * <p>
   * If serialization fails, the task is dropped and the task's finish is
   * notified. The exception is logged to System.err and masked unless
   * APGAS_SERIALIZATION_EXCEPTION is set to "true".
   *
   * @param p
   *          the place ID
   */
  void asyncAt(int p) {
    try {
      GlobalRuntimeImpl.getRuntime().transport.send(p, this);
    } catch (final Throwable e) {
      finish.unspawn(p);
      if (GlobalRuntimeImpl.getRuntime().verboseSerialization
          && !(e instanceof DeadPlaceException)) {
        System.err.println(
            "[APGAS] Failed to spawn a task at place " + p + " due to: " + e);
      }
      throw e;
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

  private static final SerializableJob NULL = () -> {
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
  private void readObject(ObjectInputStream in)
      throws IOException, ClassNotFoundException {
    finish = (Finish) in.readObject();
    parent = in.readInt();
    try {
      f = (SerializableJob) in.readObject();
    } catch (final Throwable e) {
      if (GlobalRuntimeImpl.getRuntime().verboseSerialization
          && !(e instanceof DeadPlaceException)) {
        System.err.println("[APGAS] Failed to receive a task at place "
            + GlobalRuntimeImpl.getRuntime().here + " due to: " + e);
      }
      finish.addSuppressed(e);
      f = NULL;
    }
  }

  @Override
  public void write(Kryo kryo, Output output) {
    kryo.writeClassAndObject(output, finish);
    output.writeInt(parent);
    kryo.writeClassAndObject(output, f);
  }

  @Override
  public void read(Kryo kryo, Input input) {
    finish = (Finish) kryo.readClassAndObject(input);
    parent = input.readInt();
    try {
      f = (Job) kryo.readClassAndObject(input);
    } catch (final Throwable e) {
      if (GlobalRuntimeImpl.getRuntime().verboseSerialization
          && !(e instanceof DeadPlaceException)) {
        System.err.println("[APGAS] Failed to receive a task at place "
            + GlobalRuntimeImpl.getRuntime().here + " due to: " + e);
      }
      finish.addSuppressed(e);
      f = NULL;
    }
  }
}
