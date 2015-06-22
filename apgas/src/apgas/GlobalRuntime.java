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

package apgas;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * The {@link GlobalRuntime} class provides mechanisms to initialize and shut
 * down the APGAS global runtime for the application.
 * <p>
 * The global runtime is implicitly initialized when first used. The current
 * runtime can be obtained from the {@link #getRuntime()} method, which forces
 * initialization.
 */
public abstract class GlobalRuntime {
  /**
   * A wrapper class for implementing double-checked locking.
   */
  private static class GlobalRuntimeWrapper {
    /**
     * The {@link GlobalRuntime} instance for this place.
     */
    private final GlobalRuntime runtime;

    /**
     * Initializes the {@link GlobalRuntime} instance for this place.
     */
    private GlobalRuntimeWrapper() {
      try {
        final String className = System.getProperty(
            Configuration.APGAS_RUNTIME, "apgas.impl.GlobalRuntimeImpl");
        runtime = (GlobalRuntime) Class.forName(className).newInstance();
      } catch (final ReflectiveOperationException e) {
        throw new RuntimeException(e);
      }
    }
  }

  /**
   * The {@link GlobalRuntimeWrapper} instance for this place.
   */
  private static GlobalRuntimeWrapper runtime;

  /**
   * Constructs a new {@link GlobalRuntime} instance.
   */
  protected GlobalRuntime() {
  }

  /**
   * Returns the {@link GlobalRuntime} instance for this place.
   *
   * @return the GlobalRuntime instance
   */
  public static GlobalRuntime getRuntime() {
    GlobalRuntimeWrapper r = runtime;
    if (r == null) {
      synchronized (GlobalRuntime.class) {
        if (runtime == null) {
          runtime = new GlobalRuntimeWrapper();
        }
        r = runtime;
      }
    }
    return r.runtime;
  }

  /**
   * Registers a place failure handler.
   * <p>
   * The handler is invoked for each failed place.
   *
   * @param handler
   *          the handler to register or null to deregister the current handler
   */
  public abstract void setPlaceFailureHandler(Consumer<Place> handler);

  /**
   * Shuts down the {@link GlobalRuntime} instance.
   */
  public abstract void shutdown();

  /**
   * Runs {@code f} then waits for all tasks transitively spawned by {@code f}
   * to complete.
   * <p>
   * If {@code f} or the transitively tasks spawned by {@code f} have uncaught
   * exceptions then {@code finish(F)} then throws a {@link MultipleException}
   * that collects these uncaught exceptions.
   *
   * @param f
   *          the function to run
   * @throws MultipleException
   *           if there are uncaught exceptions
   */
  protected abstract void finish(Job f);

  /**
   * Submits a new local task to the global runtime with body {@code f} and
   * returns immediately.
   *
   * @param f
   *          the function to run
   */
  protected abstract void async(Job f);

  /**
   * Submits a new task to the global runtime to be run at {@link Place}
   * {@code p} with body {@code f} and returns immediately.
   *
   * @param p
   *          the place of execution
   * @param f
   *          the function to run
   */
  protected abstract void asyncAt(Place p, SerializableJob f);

  /**
   * Submits an uncounted task to the global runtime to be run at {@link Place}
   * {@code p} with body {@code f} and returns immediately. The termination of
   * this task is not tracked by the enclosing finish. If an exception is thrown
   * by the task it is logged to System.err and ignored.
   *
   * @param p
   *          the place of execution
   * @param f
   *          the function to run
   */
  protected abstract void uncountedAsyncAt(Place p, SerializableJob f);

  /**
   * Runs {@code f} at {@link Place} {@code p} and waits for all the tasks
   * transitively spawned by {@code f}.
   * <p>
   * Equivalent to {@code finish(()->asyncat(p, f))}
   *
   * @param p
   *          the requested place of execution
   * @param f
   *          the function to run
   */
  protected abstract void at(Place p, SerializableJob f);

  /**
   * Evaluates {@code f} at {@link Place} {@code p}, waits for all the tasks
   * transitively spawned by {@code f}, and returns the result.
   *
   * @param <T>
   *          the type of the result
   * @param p
   *          the requested place of execution
   * @param f
   *          the function to run
   * @return the result of the evaluation
   */
  protected abstract <T extends Serializable> T at(Place p,
      SerializableCallable<T> f);

  /**
   * Returns the current {@link Place}.
   *
   * @return the current place
   */
  protected abstract Place here();

  /**
   * Returns the place with the given ID.
   *
   * @param id
   *          the requested ID
   * @return the place with the given ID
   */
  protected abstract Place place(int id);

  /**
   * Returns the current list of places in the global runtime.
   *
   * @return the current list of places in the global runtime
   */
  protected abstract List<? extends Place> places();

  /**
   * Returns the executor service for the place.
   *
   * @return the executor service
   */
  public abstract ExecutorService getExecutorService();

  /**
   * Intializes the global runtime.
   *
   * @param args
   *          ignored
   */
  public static void main(String[] args) {
    getRuntime();
  }
}
