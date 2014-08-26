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

package apgas.impl;

import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import apgas.BadPlaceException;
import apgas.Configuration;
import apgas.Constructs;
import apgas.Fun;
import apgas.GlobalRuntime;
import apgas.MultipleException;
import apgas.Place;
import apgas.VoidFun;
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
   * The transport for this global runtime instance.
   */
  final HazelcastTransport transport;

  /**
   * This place's ID.
   */
  final int here;

  /**
   * The scheduler for this global runtime instance.
   */
  final Scheduler scheduler;

  /**
   * The current list of places in this global runtime instance.
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

  private static Worker currentWorker() {
    final Thread t = Thread.currentThread();
    return t instanceof Worker ? (Worker) t : null;
  }

  private static Process exec(String command) throws IOException {
    final ProcessBuilder pb = new ProcessBuilder(command.split(" "));
    pb.redirectOutput(Redirect.INHERIT);
    pb.redirectError(Redirect.INHERIT);
    return pb.start();
  }

  private void refreshPlaces() {
    final List<Place> places = new ArrayList<Place>();
    for (int i = 0; i < transport.places(); i++) {
      places.add(new Place(i));
    }
    this.places = Collections.<Place> unmodifiableList(places);
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
    final String master = System.getProperty(Configuration.APGAS_MASTER);
    final boolean daemon = Boolean.getBoolean(Configuration.APGAS_DAEMON);
    serializationException = Boolean
        .getBoolean(Configuration.APGAS_SERIALIZATION_EXCEPTION);
    final String localhost = System.getProperty(Configuration.APGAS_LOCALHOST,
        InetAddress.getLocalHost().getHostAddress());

    // initialize scheduler and hazelcast
    scheduler = new Scheduler();
    transport = new HazelcastTransport(this::shutdown, master, localhost);
    here = transport.here();

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

    if (p > 1) {
      // launch additional places
      try {
        String command = getClass().getSuperclass().getCanonicalName();
        if (serializationException) {
          command = "-D" + Configuration.APGAS_SERIALIZATION_EXCEPTION
              + "=true " + command;
        }
        command = "-D" + Configuration.APGAS_DAEMON + "=true " + command;
        command = "-D" + Configuration.APGAS_MASTER + "="
            + (master == null ? transport.getAddress() : master) + " "
            + command;
        command = "-D" + Configuration.APGAS_LOCALHOST + "=" + localhost + " "
            + command;
        command = "java -cp " + System.getProperty("java.class.path") + " "
            + command;
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

    refreshPlaces();

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

  @Override
  public void finish(VoidFun f) {
    final Worker worker = currentWorker();
    final Finish finish = new DefaultFinish();
    finish.spawn(here);
    new Task(finish, f, here).finish(worker);
    if (finish.exceptions() != null) {
      throw new MultipleException(finish.exceptions());
    }
  }

  @Override
  public void async(VoidFun f) {
    final Worker worker = currentWorker();
    final Finish finish = worker == null ? new DefaultFinish()
        : worker.task.finish;
    finish.spawn(here);
    new Task(finish, f, here).async(worker);
  }

  @Override
  public void asyncat(Place p, VoidFun f) {
    if (p.id < 0 || p.id >= places().size()) {
      throw new BadPlaceException();
    }
    final Worker worker = currentWorker();
    final Finish finish = worker == null ? new DefaultFinish()
        : worker.task.finish;
    finish.spawn(p.id);
    new Task(finish, f, here).asyncat(p);
  }

  @Override
  public void at(Place p, VoidFun f) {
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
    return places.get(here);
  }

  @Override
  public List<? extends Place> places() {
    if (places.size() < transport.places()) {
      refreshPlaces();
    }
    return places;
  }

  @Override
  public Place place(int id) {
    if (id < 0 || id >= places().size()) {
      throw new BadPlaceException();
    }
    return places().get(id);
  }
}
