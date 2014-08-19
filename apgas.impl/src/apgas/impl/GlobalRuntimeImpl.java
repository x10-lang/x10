package apgas.impl;

import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
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
  Process processes[] = null;

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

  private void refresh() {
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
    String master = System.getProperty(Configuration.APGAS_MASTER);
    final boolean daemon = Boolean.getBoolean(Configuration.APGAS_DAEMON);
    serializationException = Boolean
        .getBoolean(Configuration.APGAS_SERIALIZATION_EXCEPTION);

    // initialize scheduler and hazelcast
    scheduler = new Scheduler();
    transport = new HazelcastTransport(scheduler::shutdown, master);
    here = transport.here();

    // launch additional places
    if (p > 1) {
      processes = new Process[p - 1];
      String command = getClass().getSuperclass().getCanonicalName();
      if (master == null) {
        master = transport.getAddress();
      }
      if (serializationException) {
        command = "-D" + Configuration.APGAS_SERIALIZATION_EXCEPTION + "=true "
            + command;
      }
      command = "-D" + Configuration.APGAS_DAEMON + "=true " + command;
      command = "-D" + Configuration.APGAS_MASTER + "=" + master + " "
          + command;
      command = "java -cp " + System.getProperty("java.class.path") + " "
          + command;
      for (int i = 0; i < p - 1; i++) {
        try {
          processes[i] = exec(command);
        } catch (final Throwable t) {
          shutdown();
          throw t;
        }
      }
    }

    // install shutdown hook on main thread if it can be identified
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

    // wait for other places to join the global runtime
    while (transport.places() < p) {
      try {
        Thread.sleep(100);
      } catch (final InterruptedException e) {
      }
      for (int i = 0; i < p - 1; i++) {
        if (!processes[i].isAlive()) {
          shutdown();
          throw new IOException("A process exited prematurely");
        }
      }
    }
    refresh();

    // start scheduler
    scheduler.start();
  }

  @Override
  public void shutdown() {
    transport.shutdown();
    if (processes != null) {
      // waits for 10s max
      int p = 0;
      for (int i = 0; i < 100; i++) {
        // skip over dead processes
        while (p < processes.length && processes[p] != null
            && !processes[p].isAlive()) {
          p++;
        }
        if (p == processes.length || processes[p] == null) {
          // all processes have exited
          return;
        }
        try {
          Thread.sleep(100);
        } catch (final InterruptedException e) {
        }
      }
      // kill all remaining processes
      for (; p < processes.length && processes[p] != null; p++) {
        System.err.println("[APGAS] Killing remaining processes...");
        if (processes[p].isAlive()) {
          processes[p].destroyForcibly();
        }
      }
    }
  }

  @Override
  public void finish(VoidFun f) {
    final Worker worker = currentWorker();
    final Finish finish = new Finish();
    finish.spawn(here);
    new Task(f, finish).finish(worker);
    if (finish.exceptions() != null) {
      throw new MultipleException(finish.exceptions());
    }
  }

  @Override
  public void async(VoidFun f) {
    final Worker worker = currentWorker();
    final Finish finish = worker == null ? new Finish() : worker.task.finish;
    finish.spawn(here);
    new Task(f, finish).async(worker);
  }

  @Override
  public void asyncat(Place p, VoidFun f) {
    if (p.id < 0 || p.id >= places().size()) {
      throw new BadPlaceException();
    }
    final Worker worker = currentWorker();
    final Finish finish = worker == null ? new Finish() : worker.task.finish;
    finish.spawn(p.id);
    new Task(f, finish).asyncat(p);
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
      refresh();
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
