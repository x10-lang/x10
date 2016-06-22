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
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.function.Consumer;

import com.hazelcast.core.IMap;

import apgas.Configuration;
import apgas.Constructs;
import apgas.GlobalRuntime;
import apgas.Job;
import apgas.MultipleException;
import apgas.Place;
import apgas.SerializableCallable;
import apgas.SerializableJob;
import apgas.util.GlobalID;

/**
 * The {@link GlobalRuntimeImpl} class implements the
 * {@link apgas.GlobalRuntime} class.
 */
public final class GlobalRuntimeImpl extends GlobalRuntime {
  private static GlobalRuntimeImpl runtime;
  /**
   * The value of the APGAS_VERBOSE_SERIALIZATION system property.
   */
  final boolean verboseSerialization;

  /**
   * The value of the APGAS_RESILIENT system property.
   */
  final boolean resilient;

  /**
   * The finish factory.
   */
  final Finish.Factory factory;

  /**
   * The transport for this global runtime instance.
   */
  final Transport transport;

  /**
   * This place's ID.
   */
  final int here;

  /**
   * This place.
   */
  final Place home;

  /**
   * The pool for this global runtime instance.
   */
  final ForkJoinPool pool;

  /**
   * The mutable set of places in this global runtime instance.
   */
  final SortedSet<Place> placeSet = new TreeSet<Place>();

  /**
   * An immutable ordered list of the current places.
   */
  List<Place> places;

  /**
   * The launcher used to spawn additional places.
   */
  final Launcher launcher;

  /**
   * The registered place failure handler.
   */
  Consumer<Place> handler;

  /**
   * True if shutdown is in progress.
   */
  private boolean dying;

  /**
   * The resilient map from finish IDs to finish states.
   */
  final IMap<GlobalID, ResilientFinishState> resilientFinishMap;

  private static Worker currentWorker() {
    final Thread t = Thread.currentThread();
    return t instanceof Worker ? (Worker) t : null;
  }

  public static GlobalRuntimeImpl getRuntime() {
    return runtime;
  }

  /**
   * Constructs a new {@link GlobalRuntimeImpl} instance.
   *
   * @param args
   *          the command line arguments
   */
  public GlobalRuntimeImpl(String[] args) {
    try {
      GlobalRuntimeImpl.runtime = this;

      // parse configuration
      final int p = Integer.getInteger(Configuration.APGAS_PLACES, 1);
      final int threads = Integer.getInteger(Configuration.APGAS_THREADS,
          Runtime.getRuntime().availableProcessors());
      final String master = System.getProperty(Configuration.APGAS_MASTER);
      final String hostfile = System.getProperty(Configuration.APGAS_HOSTFILE);
      verboseSerialization = Boolean
          .getBoolean(Configuration.APGAS_VERBOSE_SERIALIZATION);
      final boolean verboseLauncher = Boolean
          .getBoolean(Configuration.APGAS_VERBOSE_LAUNCHER);
      resilient = Boolean.getBoolean(Configuration.APGAS_RESILIENT);

      final boolean compact = Boolean.getBoolean(Config.APGAS_COMPACT);
      final int maxThreads = Integer.getInteger(Config.APGAS_MAX_THREADS, 256);
      final String serialization = System
          .getProperty(Config.APGAS_SERIALIZATION, "kryo");
      final String finishName = System.getProperty(Config.APGAS_FINISH);
      final String java = System.getProperty(Config.APGAS_JAVA, "java");
      final String transportName = System.getProperty(Config.APGAS_TRANSPORT);
      final String launcherName = System.getProperty(Config.APGAS_LAUNCHER);

      final String localhost = InetAddress.getLoopbackAddress()
          .getHostAddress();

      // parse hostfile
      List<String> hosts = null;
      if (master == null && hostfile != null) {
        try {
          hosts = Files
              .readAllLines(FileSystems.getDefault().getPath(hostfile));
          if (hosts.isEmpty()) {
            System.err.println(
                "[APGAS] Empty hostfile: " + hostfile + ". Using localhost.");
          }
        } catch (final IOException e) {
          System.err.println("[APGAS] Unable to read hostfile: " + hostfile
              + ". Using localhost.");
        }
      }

      // initialize launcher
      Launcher launcher = null;
      if (master == null && p > 1) {
        if (launcherName != null) {
          try {
            launcher = (Launcher) Class.forName(launcherName).newInstance();
          } catch (InstantiationException | IllegalAccessException
              | ExceptionInInitializerError | ClassNotFoundException
              | NoClassDefFoundError | ClassCastException e) {
            System.err.println("[APGAS] Unable to instantiate launcher: "
                + launcherName + ". Using default launcher.");
          }
        }
        if (launcher == null) {
          launcher = new SshLauncher();
        }
      }
      this.launcher = launcher;

      if (master == null && args != null && args.length > 0) {
        // invoked as a launcher
        final ArrayList<String> command = new ArrayList<String>();
        command.add(java);
        command.add("-Duser.dir=" + System.getProperty("user.dir"));
        command.add("-Xbootclasspath:"
            + ManagementFactory.getRuntimeMXBean().getBootClassPath());
        command.add("-cp");
        command.add(System.getProperty("java.class.path"));
        for (final String property : System.getProperties()
            .stringPropertyNames()) {
          if (property.startsWith("apgas.")) {
            command.add("-D" + property + "=" + System.getProperty(property));
          }
        }
        command.addAll(Arrays.asList(args));
        final String host = hosts == null || hosts.isEmpty() ? localhost
            : hosts.get(0);
        final Process process = launcher.launch(command, host, verboseLauncher);
        while (true) {
          try {
            System.exit(process.waitFor());
          } catch (final InterruptedException e) {
          }
        }
      }

      // initialize finish
      Finish.Factory factory = null;
      if (finishName != null) {
        final String finishFactoryName = finishName + "$Factory";
        try {
          factory = (Finish.Factory) Class.forName(finishFactoryName)
              .newInstance();
        } catch (InstantiationException | IllegalAccessException
            | ExceptionInInitializerError | ClassNotFoundException
            | NoClassDefFoundError | ClassCastException e) {
          System.err.println("[APGAS] Unable to instantiate finish factory: "
              + finishFactoryName + ". Using default factory.");
        }
      }
      if (factory == null) {
        this.factory = resilient ? new ResilientFinishOpt.Factory()
            : new DefaultFinish.Factory();
      } else {
        this.factory = factory;
      }

      // initialize scheduler
      pool = new ForkJoinPool(maxThreads, new WorkerFactory(), null, false);
      final Field ctl = ForkJoinPool.class.getDeclaredField("ctl");
      ctl.setAccessible(true);
      ctl.setLong(pool,
          ctl.getLong(pool) + (((long) maxThreads - threads) << 48));

      // serialization
      final Boolean kryo = !"java".equals(serialization);
      if (kryo && !"kryo".equals(serialization)) {
        System.err
            .println("[APGAS] Unable to instantiate serialization framework: "
                + serialization + ". Using default serialization.");
      }

      // attempt to select a good ip for this host
      String ip = null;
      String host = master;
      if (host == null && hosts != null) {
        for (final String h : hosts) {
          try {
            if (!InetAddress.getByName(h).isLoopbackAddress()) {
              host = h;
              break;
            }
          } catch (final UnknownHostException e) {
          }
        }
      }
      if (host == null) {
        host = localhost;
      }
      try {
        final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface
            .getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
          final NetworkInterface ni = networkInterfaces.nextElement();
          if (!InetAddress.getByName(host).isReachable(ni, 0, 100)) {
            continue;
          }
          final Enumeration<InetAddress> e = ni.getInetAddresses();
          while (e.hasMoreElements()) {
            final InetAddress inetAddress = e.nextElement();
            if (inetAddress.isLoopbackAddress()
                || inetAddress instanceof Inet6Address) {
              continue;
            }
            ip = inetAddress.getHostAddress();
          }
        }
      } catch (final IOException e) {
      }

      // check first entry of hostfile
      if (hosts != null && !hosts.isEmpty()) {
        try {
          final InetAddress inet = InetAddress.getByName(hosts.get(0));
          if (!inet.isLoopbackAddress()) {
            if (NetworkInterface.getByInetAddress(inet) == null) {
              System.err.println(
                  "[APGAS] First hostfile entry does not correspond to localhost. Ignoring and using localhost instead.");
            }
          }
        } catch (final IOException e) {
          System.err.println(
              "[APGAS] Unable to resolve first hostfile entry. Ignoring and using localhost instead.");
        }
      }

      // initialize transport
      Transport transport = null;
      if (transportName != null) {
        try {
          transport = (Transport) Class.forName(transportName)
              .getDeclaredConstructor(GlobalRuntimeImpl.class, String.class,
                  String.class, boolean.class)
              .newInstance(this, master, ip, compact);
        } catch (InstantiationException | IllegalAccessException
            | ExceptionInInitializerError | ClassNotFoundException
            | NoClassDefFoundError | ClassCastException e) {
          System.err.println("[APGAS] Unable to instantiate transport: "
              + transportName + ". Using default transport.");
        }
      }
      if (transport == null) {
        transport = new Transport(this, master, ip, compact, kryo);
      }
      this.transport = transport;
      if (verboseLauncher) {
        System.err.println(
            "[APGAS] New place starting at " + transport.getAddress() + ".");
      }

      // initialize here
      here = transport.here();
      home = new Place(here);

      resilientFinishMap = resilient
          ? transport.<GlobalID, ResilientFinishState> getResilientFinishMap()
          : null;

      // install hook on thread 1
      if (master == null) {
        final Thread thread[] = new Thread[Thread.activeCount()];
        Thread.enumerate(thread);
        for (final Thread t : thread) {
          if (t != null && t.getId() == 1) {
            new Thread(() -> {
              while (t.isAlive()) {
                try {
                  t.join();
                } catch (final InterruptedException e) {
                }
              }
              shutdown();
            }).start();
            break;
          }
        }
      }

      // start monitoring cluster
      transport.start();

      // launch additional places
      if (master == null && p > 1) {
        try {
          final ArrayList<String> command = new ArrayList<String>();
          command.add(java);
          command.add("-Duser.dir=" + System.getProperty("user.dir"));
          command.add("-Xbootclasspath:"
              + ManagementFactory.getRuntimeMXBean().getBootClassPath());
          command.add("-cp");
          command.add(System.getProperty("java.class.path"));
          for (final String property : System.getProperties()
              .stringPropertyNames()) {
            if (property.startsWith("apgas.")) {
              command.add("-D" + property + "=" + System.getProperty(property));
            }
          }
          command.add(
              "-D" + Configuration.APGAS_MASTER + "=" + transport.getAddress());
          command.add(getClass().getSuperclass().getCanonicalName());

          launcher.launch(p - 1, command, hosts, verboseLauncher);
        } catch (final Exception t) {
          // initiate shutdown
          shutdown();
          throw t;
        }
      }

      // wait for enough places to join the global runtime
      while (maxPlace() < p) {
        try {
          Thread.sleep(100);
        } catch (final InterruptedException e) {
        }
        if (launcher != null && !launcher.healthy()) {
          throw new Exception("A process exited prematurely");
        }
      }
    } catch (final RuntimeException e) {
      throw e;
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Updates the place collections.
   *
   * @param added
   *          added places
   * @param removed
   *          removed places
   */
  public void updatePlaces(List<Integer> added, List<Integer> removed) {
    synchronized (placeSet) {
      for (final int id : added) {
        placeSet.add(new Place(id));
      }
      for (final int id : removed) {
        placeSet.remove(new Place(id));
      }
      places = Collections
          .<Place> unmodifiableList(new ArrayList<Place>(placeSet));
    }
    if (removed.isEmpty()) {
      return;
    }
    if (!resilient) {
      shutdown();
      return;
    }
    final Consumer<Place> handler = this.handler;
    execute(new RecursiveAction() {
      private static final long serialVersionUID = 1052937749744648347L;

      @Override
      public void compute() {
        for (final int id : removed) {
          ResilientFinishState.purge(id);
        }
        if (handler != null) {
          for (final int id : removed) {
            handler.accept(new Place(id));
          }
        }
      }
    });
  }

  @Override
  public void setPlaceFailureHandler(Consumer<Place> handler) {
    this.handler = handler;
  }

  @Override
  public void shutdown() {
    synchronized (this) {
      if (dying) {
        return;
      }
      dying = true;
    }
    if (launcher != null) {
      launcher.shutdown();
    }
    pool.shutdown();
    transport.shutdown();
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
  public void finish(Job f) {
    final Worker worker = currentWorker();
    final Finish finish = factory
        .make(worker == null ? NullFinish.SINGLETON : worker.task.finish);
    new Task(finish, f, here).finish(worker);
    final List<Throwable> exceptions = finish.exceptions();
    if (exceptions != null) {
      throw MultipleException.make(exceptions);
    }
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
  public <T> T finish(Callable<T> f) {
    final Cell<T> cell = new Cell<T>();
    finish(() -> cell.set(f.call()));
    return cell.get();
  }

  /**
   * Submits a new local task to the global runtime with body {@code f} and
   * returns immediately.
   *
   * @param f
   *          the function to run
   */
  public void async(Job f) {
    final Worker worker = currentWorker();
    final Finish finish = worker == null ? NullFinish.SINGLETON
        : worker.task.finish;
    finish.spawn(here);
    new Task(finish, f, here).async(worker);
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
  public void asyncAt(Place p, SerializableJob f) {
    final Worker worker = currentWorker();
    final Finish finish = worker == null ? NullFinish.SINGLETON
        : worker.task.finish;
    finish.spawn(p.id);
    new Task(finish, f, here).asyncAt(p.id);
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
  public void uncountedAsyncAt(Place p, SerializableJob f) {
    new UncountedTask(f).uncountedAsyncAt(p.id);
  }

  /**
   * /** Submits an immediate task to the global runtime to be run at
   * {@link Place} {@code p} with body {@code f}.
   *
   * @param p
   *          the place of execution
   * @param f
   *          the function to run
   */
  public void immediateAsyncAt(Place p, SerializableRunnable f) {
    transport.send(p.id, f);
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
  public void at(Place p, SerializableJob f) {
    Constructs.finish(() -> Constructs.asyncAt(p, f));
  }

  /**
   * Evaluates {@code f} at {@link Place} {@code p}, waits for all the tasks
   * transitively spawned by {@code f}, and returns the result.
   *
   * @param <T>
   *          the type of the result (must implement java.io.Serializable)
   * @param p
   *          the place of execution
   * @param f
   *          the function to run
   * @return the result of the evaluation
   */
  @SuppressWarnings("unchecked")
  public <T extends Serializable> T at(Place p, SerializableCallable<T> f) {
    final GlobalID id = new GlobalID();
    final Place home = here();
    Constructs.finish(() -> Constructs.asyncAt(p, () -> {
      final T result = f.call();
      Constructs.asyncAt(home, () -> id.putHere(result));
    }));
    return (T) id.removeHere();
  }

  /**
   * Returns the current {@link Place}.
   *
   * @return the current place
   */
  public Place here() {
    return home;
  }

  /**
   * Returns the current list of places in the global runtime.
   *
   * @return the current list of places in the global runtime
   */
  public List<? extends Place> places() {
    return places;
  }

  /**
   * Returns the place with the given ID.
   *
   * @param id
   *          the requested ID
   * @return the place with the given ID
   */
  public Place place(int id) {
    return new Place(id);
  }

  /**
   * Returns the first unused place ID.
   *
   * @return the first unused place ID
   */
  public int maxPlace() {
    return transport.maxPlace();
  }

  @Override
  public ExecutorService getExecutorService() {
    return pool;
  }

  /**
   * Submits a task to the pool making sure that a thread will be available to
   * run it. run.
   *
   * @param task
   *          the task
   */
  void execute(ForkJoinTask<?> task) {
    pool.execute(task);
  }
}
