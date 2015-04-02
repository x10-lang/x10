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

package apgas.examples.uts;

import static apgas.Constructs.*;

import java.security.DigestException;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import apgas.Configuration;
import apgas.DeadPlaceException;
import apgas.GlobalRuntime;
import apgas.Job;
import apgas.MultipleException;
import apgas.Place;
import apgas.util.GlobalObject;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.TransactionalMap;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.TransactionalTask;
import com.hazelcast.transaction.TransactionalTaskContext;

final class GlobalUTS extends GlobalObject<GlobalUTS> implements
    Consumer<Place>, Job {

  @Override
  public synchronized void accept(Place place) {
    // p is dead, unblock if waiting on p
    if (place.id > 0) {
      System.err.println(home + " observes that " + place + " failed!");
    }
    if (state == place.id) {
      // attempt to extract loot from store
      final Bag b = map.get(home);
      if (b.size != 0) {
        bag.merge(b);
      }
      state = -1;
      notifyAll();
    }
  }

  final HazelcastInstance hz = Hazelcast.getHazelcastInstanceByName("apgas");
  final IMap<Place, Bag> map = hz.getMap("map");
  final Place home = here();
  final int places = places().size();
  final Random random = new Random();
  final MessageDigest md = Bag.encoder();
  final Bag bag = new Bag(64);
  long transfers;
  final ConcurrentLinkedQueue<Place> thieves = new ConcurrentLinkedQueue<Place>();
  final AtomicBoolean lifeline = new AtomicBoolean(home.id != places - 1);
  int state = -2; // -2: inactive, -1: running, p: stealing from p

  void seed(int s, int d) {
    bag.seed(md, s, d);
    map.set(home, bag.trim());
  }

  @Override
  public void run() throws DigestException {
    System.err.println(home + " starting");
    synchronized (this) {
      state = -1;
    }
    while (bag.size > 0) {
      while (bag.size > 0) {
        for (int n = 500; (n > 0) && (bag.size > 0); --n) {
          bag.expand(md);
        }
        distribute();
      }
      map.set(home, bag.trim());
      steal();
    }
    synchronized (this) {
      state = -2;
    }
    distribute();
    lifelinesteal();
    System.err.println(home + " stopping");
  }

  void lifelinesteal() {
    if (places == 1) {
      return;
    }
    try {
      asyncat(place((home.id + places - 1) % places), uts -> {
        uts.lifeline.set(true);
      });
    } catch (final DeadPlaceException e) {
      // TODO should go to next lifeline, but correct as is
    }
  }

  void steal() {
    if (places == 1) {
      return;
    }
    final Place from = home;
    int p = random.nextInt(places - 1);
    if (p >= from.id) {
      p++;
    }
    if (!places().contains(place(p))) {
      // TODO should try other place, but ok as is
      return;
    }
    synchronized (this) {
      state = p;
    }
    try {
      uncountedasyncat(place(p), uts -> {
        uts.request(from);
      });
    } catch (final DeadPlaceException e) {
      // pretend stealing failed
      // TODO should try other place, but ok as is
      synchronized (this) {
        state = -1;
      }
    }
    synchronized (this) {
      while (state >= 0) {
        try {
          wait();
        } catch (final InterruptedException e) {
        }
      }
    }
  }

  void request(Place p) {
    synchronized (this) {
      if (state == -1) {
        thieves.add(p);
        return;
      }
    }
    try {
      final Place h = home;
      uncountedasyncat(p, uts -> {
        uts.deal(h, null);
      });
    } catch (final DeadPlaceException e) {
      // place is dead, nothing to do
    }
  }

  void lifelinedeal(Bag b) throws DigestException {
    bag.merge(b);
    run();
  }

  synchronized void deal(Place p, Bag b) {
    if (state != p.id) {
      // thief is no longer waiting for this message, discard
      return;
    }
    if (b != null) {
      bag.merge(b);
    }
    state = -1;
    notifyAll();
  }

  void transfer(Place p, Bag b) {
    transfers++;
    while (true) {
      try {
        hz.executeTransaction(new TransactionalTask<Object>() {
          @Override
          public Object execute(TransactionalTaskContext context)
              throws TransactionException {
            final TransactionalMap<Place, Bag> map = context.getMap("map");
            map.set(home, bag.trim());
            final Bag old = map.getForUpdate(p);
            b.count = old == null ? 0 : old.count;
            map.set(p, b);
            return null;
          }
        });
        return;
      } catch (final Throwable t) {
        System.err.println("Exception in transaction at " + home
            + "... retrying");
        t.printStackTrace();
      }
    }
  }

  void distribute() {
    Place p;
    if (lifeline.get()) {
      final Bag b = bag.split();
      if (b != null) {
        p = place((home.id + 1) % places);
        lifeline.set(false);
        transfer(p, b);
        try {
          asyncat(p, uts -> {
            uts.lifelinedeal(b);
          });
        } catch (final DeadPlaceException e) {
          // thief died, nothing to do
        }
      }
    }
    while ((p = thieves.poll()) != null) {
      final Bag b = bag.split();
      if (b != null) {
        transfer(p, b);
      }
      try {
        final Place h = home;
        uncountedasyncat(p, uts -> {
          uts.deal(h, b);
        });
      } catch (final DeadPlaceException e) {
        // thief died, nothing to do
      }
    }
  }

  void init() {
    finish(() -> {
      for (final Place p : places()) {
        asyncat(p, uts -> {
          GlobalRuntime.getRuntime().setPlaceFailureHandler(uts);
        });
      }
    });
  }

  public static void main(String[] args) {
    int depth = 13;
    try {
      depth = Integer.parseInt(args[0]);
    } catch (final Exception e) {
    }

    if (System.getProperty(Configuration.APGAS_PLACES) == null) {
      System.setProperty(Configuration.APGAS_PLACES, "4");
    }
    System.setProperty(Configuration.APGAS_SERIALIZATION_EXCEPTION, "true");
    System.setProperty(Configuration.APGAS_RESILIENT, "true");

    // initialize uts and place failure handler in each place
    GlobalUTS uts = GlobalObject.make(places(), () -> new GlobalUTS());
    uts.init();

    System.out.println("Warmup...");
    try {
      uts.seed(19, depth - 2); // seed: 19
      finish(uts);
    } catch (final MultipleException e) {
      if (!e.isDeadPlaceException()) {
        throw e;
      }
    }

    uts.map.clear();

    // initialize uts and place failure handler in each place
    uts = GlobalObject.make(places(), () -> new GlobalUTS());
    uts.init();

    System.out.println("Starting...");
    long time = System.nanoTime();

    try {
      uts.seed(19, depth); // seed: 19
      finish(uts);
    } catch (final MultipleException e) {
      if (!e.isDeadPlaceException()) {
        throw e;
      }
    }

    long count = 0;
    // collect all counts
    // if places have died, process remaining nodes sequentially at place 0
    for (final Map.Entry<Place, Bag> e : uts.map.entrySet()) {
      final Bag b = e.getValue();
      if (b.size != 0) {
        System.err.println("Recovering " + e.getKey());
        b.run(uts.md);
      }
      count += b.count;
    }

    time = System.nanoTime() - time;
    System.out.println("Finished.");

    long transfers = 0;
    // collect all counts
    for (final Place p : places()) {
      transfers += uts.at(p, u -> u.transfers);
    }

    System.out.println("Depth: " + depth + ", Places: " + uts.places
        + ", Performance: " + count + "/" + Bag.sub("" + time / 1e9, 0, 6)
        + " = " + Bag.sub("" + (count / (time / 1e3)), 0, 6)
        + "M nodes/s using " + transfers + " transactions");
  }
}
