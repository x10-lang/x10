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
  public static final String APGAS_RUNTIME = "apgas.runtime";

  /**
   * Name of the network transport class implementation to instantiate (String
   * property).
   * <p>
   * Currently "{@code apgas.impl.Transport}" and "
   * {@code apgas.sockets.SocketTransport}" are supported. Defaults to "
   * {@code apgas.impl.Transport}".
   *
   */
  public static final String APGAS_TRANSPORT = "apgas.transport";

  /**
   * Enables or disables compression on the network links when using transport "
   * {@code apgas.sockets.SocketTransport}".
   * <p>
   * Set to "none" or "snappy", which is the default.
   */
  public static final String APGAS_TRANSPORT_COMPRESSION = "apgas.transport.compression";

  /**
   * Desired number of places (Integer property).
   * <p>
   * Defaults to 1. If "{@code apgas.my.master}" is not set the global runtime
   * will spawn the desired number of places, otherwise it will wait for the
   * places to appear.
   */
  public static final String APGAS_PLACES = "apgas.places";

  /**
   * Desired level of parallelism (Integer property).
   * <p>
   * The return value of "{@code Runtime.getRuntime().availableProcessors()}" is
   * used if this property is not set.
   */
  public static final String APGAS_THREADS = "apgas.threads";

  /**
   * Upper bound on the number of persistent threads in the thread pool (Integer
   * property).
   * <p>
   * Defaults to 256.
   */
  public static final String APGAS_MAX_THREADS = "apgas.max.threads";

  /**
   * Reduces the number of threads used by Hazelcast if set (Boolean property).
   */
  public static final String APGAS_COMPACT = "apgas.compact";

  /**
   * Specifies how to handle serialization errors when spawning remote tasks
   * (Boolean property).
   * <p>
   * By default, serialization exceptions are logged to System.err but masked.
   * If this property is set to "true" serialization exceptions are not masked.
   * In all cases, a remote task that failed to serialize is dropped.
   */
  public static final String APGAS_SERIALIZATION_EXCEPTION = "apgas.serialization.exception";

  /**
   * Specifies the ip or socket address of the master node to connect to if any
   * (String property).
   * <p>
   * If set to "ip:port" the global runtime will only connect to this port. If
   * set to "ip" the global runtime will connect to the first available
   * Hazelcast instance at this ip within the default port range.
   */
  public static final String APGAS_MY_MASTER = "apgas.my.master";

  /**
   * Optionally specifies the preferred ip of this host (String property).
   */
  public static final String APGAS_MY_IP = "apgas.my.ip";

  /**
   * Specifies the java command to run for spawning places (String property).
   * <p>
   * Defaults to "{@code java}".
   */
  public static final String APGAS_JAVA = "apgas.java";

  /**
   * Enables or disables resiliency.
   */
  public static final String APGAS_RESILIENT = "apgas.resilient";

  /**
   * Name of the finish implementation class to instantiate (String property).
   * <p>
   * Defaults to "{@code apgas.impl.DefaultFinish}" or "
   * {@code apgas.impl.ResilientFinish}".
   */
  public static final String APGAS_FINISH = "apgas.finish";

  /**
   * Name of the launcher implementation class to instantiate (String property).
   * <p>
   * Defaults to "{@code apgas.impl.LocalLauncher}".
   */
  public static final String APGAS_LAUNCHER = "apgas.launcher";

  /**
   * If set the runtime will ouput on stderr the command line used to launch
   * places (Boolean property).
   */
  public static final String APGAS_LAUNCHER_VERBOSE = "apgas.launcher.verbose";

  /**
   * Specifies the hostfile when using a compatible launcher such as "
   * {@code apgas.impl.SshLauncher}" (String property).
   * <p>
   * Defaults tp "{@code hostfile}".
   */
  public static final String APGAS_HOSTFILE = "apgas.hostfile";
}
