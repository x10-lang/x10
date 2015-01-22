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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Consumer;

/**
 * The {@link GlobalRuntime} class provides mechanisms to initialize and shut
 * down the APGAS global runtime for the application.
 * <p>
 * The global runtime is implicitly initialized when this class is loaded.
 * <p>
 * If the system property APGAS_PLACES is set to an integer 'n' greater than 1,
 * this initialization will spawn 'n-1' additional JVMs. These additional JVMs
 * will execute the same main method as the current one.
 * <p>
 * The current runtime can be obtained from the {@link #getRuntime()} method.
 */
public abstract class GlobalRuntime {
  /**
   * The {@link GlobalRuntime} instance for this application.
   */
  private static final GlobalRuntime runtime;

  /**
   * Throws {@code UnsupportedOperationException}.
   *
   * @throws UnsupportedOperationException
   *           when invoked
   */
  protected GlobalRuntime() {
    if (runtime != null) {
      throw new UnsupportedOperationException();
    }
  }

  /**
   * Returns the {@link GlobalRuntime} instance for this application.
   *
   * @return the GlobalRuntime instance
   */
  public static GlobalRuntime getRuntime() {
    return runtime;
  }

  static {
    try {
      final String className = System.getProperty(Configuration.APGAS_RUNTIME,
          "apgas.impl.GlobalRuntimeImpl");
      final Constructor<?> constructor = Class.forName(className)
          .getConstructor(new Class<?>[0]);
      constructor.setAccessible(true);
      try {
        runtime = (GlobalRuntime) constructor.newInstance(new Object[0]);
      } catch (final InvocationTargetException e) {
        throw e.getCause();
      }
    } catch (final Throwable t) {
      throw new ExceptionInInitializerError(t);
    }
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
  protected abstract void asyncat(Place p, Job f);

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
  protected abstract void uncountedasyncat(Place p, Job f);

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
  protected abstract void at(Place p, Job f);

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
   * @return the result
   */
  protected abstract <T> T at(Place p, Fun<T> f);

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
   * @return a {@link Place} instance with the given ID
   */
  protected abstract Place place(int id);

  /**
   * Returns the current list of places in the global runtime.
   * <p>
   * Subsequent calls to this method may return different lists as more places
   * are added to the global runtime.
   *
   * @return the current list of places in the global runtime
   */
  protected abstract List<? extends Place> places();

  /**
   * Starts the global runtime and waits for incoming tasks.
   *
   * @param args
   *          ignored
   */
  public static void main(String[] args) {
  }
}
