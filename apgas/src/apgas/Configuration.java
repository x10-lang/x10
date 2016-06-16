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

/**
 * The {@link Configuration} class declares the main system properties used to
 * configure the global runtime.
 */
public final class Configuration {
  /**
   * Prevents instantiation.
   */
  private Configuration() {
  }

  /**
   * Property {@value #APGAS_PLACES} specifies the desired number of places
   * (Integer property).
   * <p>
   * Defaults to 1. If {@value #APGAS_MASTER} is not set the global runtime
   * spawns the desired number of places, otherwise it waits for the places to
   * appear.
   */
  public static final String APGAS_PLACES = "apgas.places";

  /**
   * Property {@value #APGAS_MASTER} optionally specifies the ip or socket
   * address of the master node (String property).
   * <p>
   * If set to an ip the global runtime connects to the first available global
   * runtime instance at this ip within the default port range.
   */
  public static final String APGAS_MASTER = "apgas.master";

  /**
   * Property {@value #APGAS_HOSTFILE} specifies a filename that lists hosts on
   * which to launch places (String property).
   */
  public static final String APGAS_HOSTFILE = "apgas.hostfile";

  /**
   * Property {@value #APGAS_RESILIENT} enables fault tolerance (Boolean
   * property).
   * <p>
   * If set, the global runtime does not abort the execution if a place fails.
   */
  public static final String APGAS_RESILIENT = "apgas.resilient";

  /**
   * Property {@value #APGAS_THREADS} specifies the desired level of parallelism
   * (Integer property).
   * <p>
   * The return value of {@code Runtime.getRuntime().availableProcessors()} is
   * used if this property is not set.
   */
  public static final String APGAS_THREADS = "apgas.threads";

  /**
   * Property {@value #APGAS_VERBOSE_SERIALIZATION} controls the verbosity of
   * the serialization (Boolean property).
   */
  public static final String APGAS_VERBOSE_SERIALIZATION = "apgas.verbose.serialization";

  /**
   * Property {@value #APGAS_VERBOSE_LAUNCHER} controls the verbosity of the
   * launcher (Boolean property).
   */
  public static final String APGAS_VERBOSE_LAUNCHER = "apgas.verbose.launcher";
}
