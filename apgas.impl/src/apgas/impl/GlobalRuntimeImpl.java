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

import java.io.IOException;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ForkJoinPool;
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
import apgas.util.Launcher;

import com.hazelcast.core.IMap;

/**
 * The {@link GlobalRuntimeImpl} class implements the
 * {@link apgas.GlobalRuntime} class.
 */
final class GlobalRuntimeImpl extends GlobalRuntime {
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
    final String master = System.getProperty(Configuration.APGAS_MASTER);
    final boolean daemon = Boolean.getBoolean(Configuration.APGAS_DAEMON);
    serializationException = Boolean
        .getBoolean(Configuration.APGAS_SERIALIZATION_EXCEPTION);
    resilient = Boolean.getBoolean(Configuration.APGAS_RESILIENT);
    Finish.Factory factory = null;
    final String finishConfig = System.getProperty(Configuration.APGAS_FINISH);
    if (finishConfig != null) {
      final String className = finishConfig + "$Factory";
      try {
        factory = (Finish.Factory) Class.forName(className).newInstance();
      } catch (InstantiationException | IllegalAccessException
          | ExceptionInInitializerError | ClassNotFoundException
          | NoClassDefFoundError | ClassCastException e) {
        System.err.println("[APGAS] Unable to instantiate finish factory: "
            + className + ". Using default factory.");
      }
    }
    if (factory == null) {
      this.factory = resilient ? new ResilientFinishOpt.Factory()
          : new DefaultFinish.Factory();
    } else {
      this.factory = factory;
    }
    final boolean compact = Boolean.getBoolean(Configuration.APGAS_COMPACT);
    final String java = System.getProperty(Configuration.APGAS_JAVA, "java");

    // initialize scheduler and transport
    pool = new ForkJoinPool(threads, new WorkerFactory(), null, false);
    transport = new Transport(this, master, InetAddress.getLocalHost()
        .getHostAddress(), compact);

    // initialize here
    here = transport.here();
    home = new Place(here);

    resilientFinishMap = resilient ? transport
        .<GlobalID, ResilientFinishState> getResilientFinishMap() : null;

    // install hook on thread 1
    if (!daemon) {
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

    if (p == 0) {
      launcher = null;
    } else {
      final String name = System.getProperty(Configuration.APGAS_LAUNCHER);
      launcher = (name == null) ? new LocalLauncher() : (Launcher) Class
          .forName(name).newInstance();
      // launch additional places
      try {
        final ArrayList<String> command = new ArrayList<String>();
        command.add(java);
        command.add("-Xbootclasspath:"
            + ManagementFactory.getRuntimeMXBean().getBootClassPath());
        command.add("-cp");
        command.add(System.getProperty("java.class.path"));
        if (resilient) {
          command.add("-D" + Configuration.APGAS_RESILIENT + "=true");
        }
        if (serializationException) {
          command.add("-D" + Configuration.APGAS_SERIALIZATION_EXCEPTION
              + "=true");
        }
        if (compact) {
          command.add("-D" + Configuration.APGAS_COMPACT + "=true");
          command.add("-XX:CICompilerCount=3");
          command.add("-XX:ParallelGCThreads=2");
        }
        if (factory != null) {
          command.add("-D" + Configuration.APGAS_FINISH + "=" + finishConfig);
        }
        command.add("-D" + Configuration.APGAS_THREADS + "=" + threads);
        command.add("-D" + Configuration.APGAS_DAEMON + "=true");
        command.add("-D" + Configuration.APGAS_MASTER + "="
            + (master == null ? transport.getAddress() : master));
        command.add(getClass().getSuperclass().getCanonicalName());

        launcher.launch(p - 1, command);

        // wait for spawned places to join the global runtime
        while (maxPlace() < p) {
          try {
            Thread.sleep(100);
          } catch (final InterruptedException e) {
          }
          if (!launcher.healthy()) {
            throw new IOException("A process exited prematurely");
          }
        }
      } catch (final Throwable t) {
        // initiate shutdown
        shutdown();
        throw t;
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
  void updatePlaces(List<Integer> added, List<Integer> removed) {
    for (final int id : added) {
      placeSet.add(new Place(id));
    }
    for (final int id : removed) {
      placeSet.remove(new Place(id));
    }
    places = Collections
        .<Place> unmodifiableList(new ArrayList<Place>(placeSet));
    if (removed.isEmpty()) {
      return;
    }
    if (!resilient) {
      shutdown();
      return;
    }
    final Consumer<Place> handler = this.handler;
    pool.execute(new RecursiveAction() {
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
    final Finish finish;
    if (worker == null) {
      finish = NullFinish.SINGLETON;
    } else {
      finish = worker.task.finish;
      finish.spawn(here);
    }
    new Task(finish, f, here).async(worker);
  }

  @Override
  public void asyncat(Place p, SerializableJob f) {
    final Worker worker = currentWorker();
    final Finish finish;
    if (worker == null) {
      finish = NullFinish.SINGLETON;
    } else {
      finish = worker.task.finish;
      finish.spawn(p.id);
    }
    new Task(finish, f, here).asyncat(p);
  }

  @Override
  public void uncountedasyncat(Place p, SerializableJob f) {
    new UncountedTask(f).uncountedasyncat(p);
  }

  @Override
  public void at(Place p, SerializableJob f) {
    Constructs.finish(() -> Constructs.asyncat(p, f));
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends Serializable> T at(Place p, SerializableCallable<T> f) {
    final GlobalID id = new GlobalID();
    final Place home = here();
    Constructs.finish(() -> Constructs.asyncat(p, () -> {
      final T result = f.call();
      Constructs.asyncat(home, () -> id.putHere(result));
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
}
