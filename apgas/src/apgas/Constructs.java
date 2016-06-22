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

package apgas;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * The {@link Constructs} class defines the APGAS constructs by means of static
 * methods.
 */
public final class Constructs {
  /**
   * Prevents instantiation.
   */
  private Constructs() {
  }

  /**
   * Runs {@code f} then waits for all tasks transitively spawned by {@code f}
   * to complete.
   * <p>
   * If {@code f} or the tasks transitively spawned by {@code f} have uncaught
   * exceptions then {@code finish(f)} then throws a {@link MultipleException}
   * that collects these uncaught exceptions.
   *
   * @param f
   *          the function to run
   * @throws MultipleException
   *           if there are uncaught exceptions
   */
  public static void finish(Job f) {
    GlobalRuntime.getRuntimeImpl().finish(f);
  }

  /**
   * Evaluates {@code f}, waits for all the tasks transitively spawned by
   * {@code f}, and returns the result.
   * <p>
   * If {@code f} or the tasks transitively spawned by {@code f} have uncaught
   * exceptions then {@code finish(F)} then throws a {@link MultipleException}
   * that collects these uncaught exceptions.
   *
   * @param <T>
   *          the type of the result
   * @param f
   *          the function to run
   * @return the result of the evaluation
   */
  public static <T> T finish(Callable<T> f) {
    return GlobalRuntime.getRuntimeImpl().finish(f);
  }

  /**
   * Submits a new local task to the global runtime with body {@code f} and
   * returns immediately.
   *
   * @param f
   *          the function to run
   */
  public static void async(Job f) {
    GlobalRuntime.getRuntimeImpl().async(f);
  }

  /**
   * Submits a new task to the global runtime to be run at {@link Place}
   * {@code p} with body {@code f} and returns immediately.
   *
   * @param p
   *          the place of execution
   * @param f
   *          the function to run
   */
  public static void asyncAt(Place p, SerializableJob f) {
    GlobalRuntime.getRuntimeImpl().asyncAt(p, f);
  }

  /**
   * Submits an uncounted task to the global runtime to be run at {@link Place}
   * {@code p} with body {@code f} and returns immediately. The termination of
   * this task is not tracked by the enclosing finish. Exceptions thrown by the
   * task are ignored.
   *
   * @param p
   *          the place of execution
   * @param f
   *          the function to run
   */
  public static void uncountedAsyncAt(Place p, SerializableJob f) {
    GlobalRuntime.getRuntimeImpl().uncountedAsyncAt(p, f);
  }

  /**
   * Evaluates {@code f} at {@link Place} {@code p}, waits for all the tasks
   * transitively spawned by {@code f}, and returns the result.
   *
   * @param <SerializableT>
   *          the type of the result (must implement java.io.Serializable)
   * @param p
   *          the place of execution
   * @param f
   *          the function to run
   * @return the result of the evaluation
   */
  public static <SerializableT extends Serializable> SerializableT at(Place p,
      SerializableCallable<SerializableT> f) {
    return GlobalRuntime.getRuntimeImpl().at(p, f);
  }

  /**
   * Runs {@code f} at {@link Place} {@code p} and waits for all the tasks
   * transitively spawned by {@code f}.
   * <p>
   * Equivalent to {@code finish(() -> asyncAt(p, f))}
   *
   * @param p
   *          the place of execution
   * @param f
   *          the function to run
   */
  public static void at(Place p, SerializableJob f) {
    GlobalRuntime.getRuntimeImpl().at(p, f);
  }

  /**
   * Returns the current {@link Place}.
   *
   * @return the current place
   */
  public static Place here() {
    return GlobalRuntime.getRuntimeImpl().here();
  }

  /**
   * Returns the place with the given ID.
   *
   * @param id
   *          the requested ID
   * @return the place with the given ID
   */
  public static Place place(int id) {
    return GlobalRuntime.getRuntimeImpl().place(id);
  }

  /**
   * Returns the current list of places in the global runtime.
   *
   * @return the current list of places in the global runtime
   */
  public static List<? extends Place> places() {
    return GlobalRuntime.getRuntimeImpl().places();
  }
}
