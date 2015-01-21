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
import java.lang.ProcessBuilder.Redirect;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import apgas.Configuration;
import apgas.Constructs;
import apgas.Fun;
import apgas.GlobalRuntime;
import apgas.Handler;
import apgas.Job;
import apgas.MultipleException;
import apgas.Place;
import apgas.util.GlobalID;

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
   * The scheduler for this global runtime instance.
   */
  final Scheduler scheduler;

  /**
   * The mutable set of places in this global runtime instance.
   */
  final SortedSet<Place> placeSet = new TreeSet<Place>();

  /**
   * An immutable ordered list of the current places.
   */
  List<Place> places;

  /**
   * The processes we spawned.
   */
  final List<Process> processes = new ArrayList<Process>();

  /**
   * Status of the shutdown sequence (0 live, 1 shutting down the Global
   * Runtime, 2 shutting down the JVM).
   */
  int dying;

  Handler handler;

  private static Worker currentWorker() {
    final Thread t = Thread.currentThread();
    return t instanceof Worker ? (Worker) t : null;
  }

  private static Process exec(List<String> command) throws IOException {
    final ProcessBuilder pb = new ProcessBuilder(command);
    pb.redirectOutput(Redirect.INHERIT);
    pb.redirectError(Redirect.INHERIT);
    return pb.start();
  }

  public static GlobalRuntimeImpl getRuntime() {
    return (GlobalRuntimeImpl) GlobalRuntime.getRuntime();
  }

  /**
   * Constructs a new {@link GlobalRuntimeImpl} instance.
   *
   * @throws IOException
   *           if an error occurs
   */
  public GlobalRuntimeImpl() throws IOException {
    // parse configuration
    final int p = Integer.getInteger(Configuration.APGAS_PLACES, 1);
    final int threads = Integer.getInteger(Configuration.APGAS_THREADS, Runtime
        .getRuntime().availableProcessors());
    final String master = System.getProperty(Configuration.APGAS_MASTER);
    final boolean daemon = Boolean.getBoolean(Configuration.APGAS_DAEMON);
    serializationException = Boolean
        .getBoolean(Configuration.APGAS_SERIALIZATION_EXCEPTION);
    resilient = Boolean.getBoolean(Configuration.APGAS_RESILIENT);
    final boolean compact = Boolean.getBoolean(Configuration.APGAS_COMPACT);
    final String localhost = System.getProperty(Configuration.APGAS_LOCALHOST,
        InetAddress.getLocalHost().getHostAddress());

    // initialize scheduler and transport
    scheduler = new Scheduler(threads);
    transport = new Transport(this, master, localhost, compact);

    // initialize here
    here = transport.here();
    home = new Place(here);

    // install shutdown hook
    Runtime.getRuntime().addShutdownHook(new Thread(() -> terminate()));

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

    if (p > 1) {
      // launch additional places
      try {
        final ArrayList<String> command = new ArrayList<String>();
        command.add("java");
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
        command.add("-D" + Configuration.APGAS_THREADS + "=" + threads);
        command.add("-D" + Configuration.APGAS_DAEMON + "=true");
        command.add("-D" + Configuration.APGAS_MASTER + "="
            + (master == null ? transport.getAddress() : master));
        command.add("-D" + Configuration.APGAS_LOCALHOST + "=" + localhost);
        command.add(getClass().getSuperclass().getCanonicalName());
        for (int i = 0; i < p - 1; i++) {
          Process process = exec(command);
          synchronized (processes) {
            if (dying <= 1) {
              processes.add(process);
              process = null;
            }
          }
          if (process != null) {
            process.destroyForcibly();
            throw new IllegalStateException("Shutdown in progress");
          }
        }

        // wait for spawned places to join the global runtime
        while (transport.places() < p) {
          try {
            Thread.sleep(100);
          } catch (final InterruptedException e) {
          }
          for (final Process process : processes) {
            if (!process.isAlive()) {
              throw new IOException("A process exited prematurely");
            }
          }
        }
      } catch (final Throwable t) {
        // initiate shutdown
        shutdown();
        throw t;
      }
    }

    // start scheduler
    scheduler.start();
  }

  /**
   * Kills all spawned processes.
   */
  private void terminate() {
    synchronized (processes) {
      dying = 2;
    }
    for (final Process process : processes) {
      process.destroyForcibly();
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
    if (here == 0) {
      for (final int id : removed) {
        ResilientFinish.purge(id);
      }
    }
    if (handler != null) {
      for (final int id : removed) {
        handler.handle(place(id));
      }
    }
  }

  @Override
  public void setPlaceFailureHandler(Handler handler) {
    this.handler = handler;
  }

  /**
   * Asks the scheduler and the transport to shutdown.
   */
  @Override
  public void shutdown() {
    synchronized (processes) {
      if (dying > 0) {
        return;
      }
      dying = 1;
    }
    scheduler.shutdown();
    transport.shutdown();
  }

  /**
   * Constructs a new finish instance.
   *
   * @param parent
   *          the parent finish or null
   * @param place
   *          the place ID of the main task
   * @return the finish instance
   */
  Finish newFinish(Finish parent, int place) {
    return resilient ? new ResilientFinish((ResilientFinish) parent, place)
        : new DefaultFinish(place);
  }

  @Override
  public void finish(Job f) {
    final Worker worker = currentWorker();
    final Finish finish = newFinish(worker == null ? null : worker.task.finish,
        here);
    new Task(finish, f, here).finish(worker);
    if (finish.exceptions() != null) {
      throw new MultipleException(finish.exceptions());
    }
  }

  @Override
  public void async(Job f) {
    final Worker worker = currentWorker();
    final Finish finish;
    if (worker == null) {
      finish = newFinish(null, here);
    } else {
      finish = worker.task.finish;
      finish.spawn(here);
    }
    new Task(finish, f, here).async(worker);
  }

  @Override
  public void asyncat(Place p, Job f) {
    final Worker worker = currentWorker();
    final Finish finish;
    if (worker == null) {
      finish = newFinish(null, p.id);
    } else {
      finish = worker.task.finish;
      finish.spawn(p.id);
    }
    new Task(finish, f, here).asyncat(p);
  }

  @Override
  public void uncountedasyncat(Place p, Job f) {
    transport.send(p.id, () -> {
      try {
        f.run();
      } catch (final Throwable e) {
        System.err.println("[APGAS] Uncaught exception in uncountedasyncat");
        System.err.println("[APGAS] Caused by: " + e);
        System.err.println("[APGAS] Ignoring...");
      }
    });
  }

  @Override
  public void at(Place p, Job f) {
    Constructs.finish(() -> Constructs.asyncat(p, f));
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T at(Place p, Fun<T> f) {
    final GlobalID id = new GlobalID();
    final Place home = here();
    Constructs.finish(() -> Constructs.asyncat(p, () -> {
      final T result = f.call();
      Constructs.asyncat(home, () -> id.putHere(result));
    }));
    return (T) id.getHere();
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
}
