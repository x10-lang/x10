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

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.function.Consumer;

import apgas.Configuration;
import apgas.Constructs;
import apgas.GlobalRuntime;
import apgas.Job;
import apgas.MultipleException;
import apgas.Place;
import apgas.SerializableCallable;
import apgas.SerializableJob;
import apgas.util.GlobalID;

import com.hazelcast.core.IMap;

/**
 * The {@link GlobalRuntimeImpl} class implements the
 * {@link apgas.GlobalRuntime} class.
 */
public final class GlobalRuntimeImpl extends GlobalRuntime {
  /**
   * The value of the APGAS_SERIALIZATION_EXCEPTION system property.
   */
  final boolean serializationException;

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
    return (GlobalRuntimeImpl) GlobalRuntime.getRuntime();
  }

  /**
   * Constructs a new {@link GlobalRuntimeImpl} instance.
   *
   * @throws Exception
   *           if an error occurs
   */
  public GlobalRuntimeImpl() throws Exception {
    // parse configuration
    final int p = Integer.getInteger(Configuration.APGAS_PLACES, 1);
    final int threads = Integer.getInteger(Configuration.APGAS_THREADS, Runtime
        .getRuntime().availableProcessors());
    final int maxThreads = Integer.getInteger(Configuration.APGAS_MAX_THREADS,
        256);
    final String master = System.getProperty(Configuration.APGAS_MY_MASTER);
    final String ip = System.getProperty(Configuration.APGAS_MY_IP);
    serializationException = Boolean
        .getBoolean(Configuration.APGAS_SERIALIZATION_EXCEPTION);
    resilient = Boolean.getBoolean(Configuration.APGAS_RESILIENT);
    final boolean compact = Boolean.getBoolean(Configuration.APGAS_COMPACT);
    final String finishName = System.getProperty(Configuration.APGAS_FINISH);
    final String java = System.getProperty(Configuration.APGAS_JAVA, "java");
    final String transportName = System
        .getProperty(Configuration.APGAS_TRANSPORT);
    final String launcherName = System
        .getProperty(Configuration.APGAS_LAUNCHER);
    final boolean launcherVerbose = Boolean
        .getBoolean(Configuration.APGAS_LAUNCHER_VERBOSE);

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
    ctl.setLong(pool, ctl.getLong(pool) + (((long) maxThreads - threads) << 48));

    // initialize transport
    Transport transport = null;
    if (transportName != null) {
      try {
        transport = (Transport) Class
            .forName(transportName)
            .getDeclaredConstructor(GlobalRuntimeImpl.class, String.class,
                String.class, boolean.class)
            .newInstance(this, master, ip, compact);
      } catch (InstantiationException | IllegalAccessException
          | ExceptionInInitializerError | ClassNotFoundException
          | NoClassDefFoundError | ClassCastException e) {
        System.err.println("[APGAS] Unable to instantiate transport: "
            + transportName + ". Using default transport.");
        e.printStackTrace();
      }
    }
    if (transport == null) {
      transport = new Transport(this, master, ip, compact);
    }
    this.transport = transport;

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
          e.printStackTrace();
        }
      }
      if (launcher == null) {
        launcher = new LocalLauncher();
      }
    }
    this.launcher = launcher;

    // initialize here
    here = transport.here();
    home = new Place(here);

    resilientFinishMap = resilient ? transport
        .<GlobalID, ResilientFinishState> getResilientFinishMap() : null;

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
        command.add("-Xbootclasspath:"
            + ManagementFactory.getRuntimeMXBean().getBootClassPath());
        command.add("-cp");
        command.add(System.getProperty("java.class.path"));
        for (final String property : System.getProperties()
            .stringPropertyNames()) {
          if (property.startsWith("apgas.")
              && !property.startsWith("apgas.my.")) {
            command.add("-D" + property + "=" + System.getProperty(property));
          }
        }
        command.add("-D" + Configuration.APGAS_MY_MASTER + "="
            + (master == null ? transport.getAddress() : master));
        command.add(getClass().getSuperclass().getCanonicalName());

        launcher.launch(p - 1, command, launcherVerbose);
      } catch (final Throwable t) {
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
      places = Collections.<Place> unmodifiableList(new ArrayList<Place>(
          placeSet));
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

  @Override
  public void finish(Job f) {
    final Worker worker = currentWorker();
    final Finish finish = factory.make(worker == null ? NullFinish.SINGLETON
        : worker.task.finish);
    new Task(finish, f, here).finish(worker);
    final List<Throwable> exceptions = finish.exceptions();
    if (exceptions != null) {
      throw new MultipleException(exceptions);
    }
  }

  @Override
  public void async(Job f) {
    final Worker worker = currentWorker();
    final Finish finish = worker == null ? NullFinish.SINGLETON
        : worker.task.finish;
    finish.spawn(here);
    new Task(finish, f, here).async(worker);
  }

  @Override
  public void asyncAt(Place p, SerializableJob f) {
    final Worker worker = currentWorker();
    final Finish finish = worker == null ? NullFinish.SINGLETON
        : worker.task.finish;
    finish.spawn(p.id);
    new Task(finish, f, here).asyncat(p.id);
  }

  @Override
  public void uncountedAsyncAt(Place p, SerializableJob f) {
    new UncountedTask(f).uncountedasyncat(p.id);
  }

  @Override
  public void immediateAsyncAt(Place p, SerializableRunnable f) {
    transport.send(p.id, f);
  }

  @Override
  public void at(Place p, SerializableJob f) {
    Constructs.finish(() -> Constructs.asyncAt(p, f));
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends Serializable> T at(Place p, SerializableCallable<T> f) {
    final GlobalID id = new GlobalID();
    final Place home = here();
    Constructs.finish(() -> Constructs.asyncAt(p, () -> {
      final T result = f.call();
      Constructs.asyncAt(home, () -> id.putHere(result));
    }));
    return (T) id.removeHere();
  }

  @Override
  public Place here() {
    return home;
  }

  @Override
  public List<? extends Place> places() {
    return places;
  }

  @Override
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
