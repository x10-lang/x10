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

import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import apgas.impl.GlobalRuntimeImpl;

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
   * The command line arguments if the main method of this class is invoked.
   */
  private static String[] args;

  /**
   * A wrapper class for implementing the singleton pattern.
   */
  private static class GlobalRuntimeWrapper {
    /**
     * The {@link GlobalRuntime} instance for this place.
     */
    private static final GlobalRuntimeImpl runtime = new GlobalRuntimeImpl(
        args);
  }

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
    return getRuntimeImpl();
  }

  /**
   * Returns the {@link GlobalRuntimeImpl} instance for this place.
   *
   * @return the GlobalRuntimeImpl instance
   */
  static GlobalRuntimeImpl getRuntimeImpl() {
    return GlobalRuntimeWrapper.runtime;
  }

  /**
   * Shuts down the {@link GlobalRuntime} instance.
   */
  public abstract void shutdown();

  /**
   * Registers a place failure handler.
   * <p>
   * The handler is invoked for each failed place.
   *
   * @param handler
   *          the handler to register or null to unregister the current handler
   */
  public abstract void setPlaceFailureHandler(Consumer<Place> handler);

  /**
   * Returns the executor service for the place.
   *
   * @return the executor service
   */
  public abstract ExecutorService getExecutorService();

  /**
   * Initializes the global runtime.
   *
   * @param args
   *          the command line arguments
   */
  public static void main(String[] args) {
    GlobalRuntime.args = args;
    getRuntime();
  }
}
