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

package apgas;

/**
 * The {@link Configuration} class defines the names of the system properties
 * used to configure the global runtime.
 */
public final class Configuration {
  /**
   * Prevents instantiation.
   */
  private Configuration() {
  }

  /**
   * Name of the {@link GlobalRuntime} class implementation to instantiate
   * (String property).
   */
  public static final String APGAS_RUNTIME = "APGAS_RUNTIME";

  /**
   * Number of places to spawn (Integer property).
   * <p>
   * All the places are spawned on the localhost.
   *
   * @see GlobalRuntime
   */
  public static final String APGAS_PLACES = "APGAS_PLACES";

  /**
   * Specifies how to handle serialization errors when spawning remote tasks
   * (Boolean property).
   * <p>
   * By default, serialization exceptions are logged to System.err but masked.
   * If this property is set to "true" serialization exceptions are not masked.
   * In all cases, a remote task that failed to serialize is dropped.
   */
  public static final String APGAS_SERIALIZATION_EXCEPTION = "APGAS_SERIALIZATION_EXCEPTION";

  /**
   * Specifies the ip or socket address of the master node to connect to if any
   * (String property).
   * <p>
   * If set to "ip:port" the global runtime will only connect to this port. If
   * set to "ip" the global runtime will connect to the first available
   * Hazelcast instance at this ip within the default port range.
   */
  public static final String APGAS_MASTER = "APGAS_MASTER";

  /**
   * Disables the implicit shutdown of the global runtime when thread with ID 1
   * terminates (Boolean property).
   */
  public static final String APGAS_DAEMON = "APGAS_DAEMON";

  /**
   * Specifies the ip address of the current host (String property).
   * <p>
   * The return value of "{@code InetAddress.getLocalHost().getHostAddress()}"
   * is used if this property is not set.
   */
  public static final String APGAS_LOCALHOST = "APGAS_LOCALHOST";

  /**
   * Turns on resiliency.
   */
  public static final String APGAS_RESILIENT = "APGAS_RESILIENT";
}
