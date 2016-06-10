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

package apgas.examples;

import static apgas.Constructs.*;

import java.io.Serializable;
import java.security.DigestException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.TransactionalMap;
import com.hazelcast.transaction.TransactionalTaskContext;

import apgas.Configuration;
import apgas.DeadPlaceException;
import apgas.DeadPlacesException;
import apgas.GlobalRuntime;
import apgas.Place;
import apgas.util.PlaceLocalObject;

final class ResilientUTS extends PlaceLocalObject {
  final HazelcastInstance hz = Hazelcast.getHazelcastInstanceByName("apgas");
  final IMap<Integer, UTS> map; // the resilient map for the current wave
  final int wave; // the integer id of the current wave
  final List<? extends Place> group; // the places in the current wave
  final Worker[] workers; // the workers at the current place
  final int power; // 1<<power workers per place
  final int mask;
  final boolean resilient;

  @FunctionalInterface
  static interface Fun extends Serializable {
    void run(Worker w);
  }

  @FunctionalInterface
  static interface FunE extends Serializable {
    void run(Worker w) throws Exception;
  }

  void myAsyncAt(int dst, FunE f) {
    asyncAt(group.get(dst >> power), () -> f.run(workers[dst & mask]));
  }

  void myUncountedAsyncAt(int dst, Fun f) {
    if (group.get(dst >> power).equals(here())) {
      f.run(workers[dst & mask]);
    } else {
      uncountedAsyncAt(group.get(dst >> power),
          () -> f.run(workers[dst & mask]));
    }
  }

  ResilientUTS(int wave, Place[] group, int power, int size, int ratio,
      boolean resilient) {
    map = resilient ? hz.getMap("map" + wave) : null;
    this.group = Arrays.asList(group);
    this.wave = wave;
    this.power = power;
    this.resilient = resilient;
    mask = (1 << power) - 1;
    workers = new Worker[1 << power];
    for (int i = 0; i <= mask; i++) {
      workers[i] = new Worker(i, size, ratio);
    }
    GlobalRuntime.getRuntime().setPlaceFailureHandler(this::unblock);
  }

  void unblock(Place p) {
    if (!group.contains(p)) {
      return;
    }
    if (p.id > 0) {
      System.err.println("Observing failure of " + p + " from " + here());
    }
    for (int i = 0; i <= mask; ++i) {
      workers[i].unblock(p);
    }
  }

  final class Worker { // not serializable
    final int me; // my index in the lifeline graph
    final int prev; // index of the predecessor in the lifeline graph
    final int next; // index of the successor in the lifeline graph
    final Random random;
    final MessageDigest md = UTS.encoder();
    final UTS bag = new UTS(64);
    // pending requests from thieves
    final ConcurrentLinkedQueue<Integer> thieves = new ConcurrentLinkedQueue<Integer>();
    final AtomicBoolean lifeline; // pending lifeline request?
    int state = -2; // -3: abort, -2: inactive, -1: running, p: stealing from p

    Worker(int id, int size, int ratio) {
      me = (group.indexOf(here()) << power) + id;
      random = new Random(me);
      prev = (me + (group.size() << power) - 1) % (group.size() << power);
      next = (me + 1) % (group.size() << power);
      lifeline = new AtomicBoolean(
          ((next % ratio) != 0) || ((next / ratio) >= size));
    }

    synchronized void abort() {
      if (state == -3) {
        throw new DeadPlaceException(here());
      }
    }

    public void run() throws DigestException {
      try {
        System.err.println(me + " starting");
        synchronized (this) {
          abort();
          state = -1;
        }
        while (bag.size > 0) {
          while (bag.size > 0) {
            for (int n = 500; (n > 0) && (bag.size > 0); --n) {
              bag.expand(md);
            }
            abort();
            distribute();
          }
          if (resilient) {
            map.set(me, bag.trim());
          }
          steal();
        }
        synchronized (this) {
          abort();
          state = -2;
        }
        distribute();
        lifelinesteal();
      } finally {
        System.err.println(me + " stopping");
      }
    }

    void lifelinesteal() {
      if (group.size() == 1 && power == 0) {
        return;
      }
      myUncountedAsyncAt(prev, w -> w.lifeline.set(true));
    }

    void steal() {
      if (group.size() == 1 && power == 0) {
        return;
      }
      final int me = this.me;
      int p = random.nextInt((group.size() << power) - 1);
      if (p >= me) {
        p++;
      }
      synchronized (this) {
        abort();
        state = p;
      }
      myUncountedAsyncAt(p, w -> w.request(me));
      synchronized (this) {
        while (state >= 0) {
          try {
            wait();
          } catch (final InterruptedException e) {
          }
        }
      }
    }

    void request(int thief) {
      synchronized (this) {
        if (state == -3) {
          return;
        }
        if (state == -1) {
          thieves.add(thief);
          return;
        }
      }
      myUncountedAsyncAt(thief, w -> w.deal(null));
    }

    void lifelinedeal(UTS b) throws DigestException {
      bag.merge(b);
      run();
    }

    synchronized void deal(UTS loot) {
      if (state == -3) {
        return;
      }
      if (loot != null) {
        bag.merge(loot);
      }
      state = -1;
      notifyAll();
    }

    synchronized void unblock(Place p) {
      state = -3;
      notifyAll();
    }

    void transfer(int thief, UTS loot) {
      final UTS bag = this.bag.trim();
      final int me = this.me;
      final int wave = ResilientUTS.this.wave;
      hz.executeTransaction((TransactionalTaskContext context) -> {
        final TransactionalMap<Integer, UTS> map = context.getMap("map" + wave);
        map.set(me, bag);
        final UTS old = map.getForUpdate(thief);
        loot.count = old == null ? 0 : old.count;
        map.set(thief, loot);
        return null;
      });
    }

    void distribute() {
      if (group.size() == 1 && power == 0) {
        return;
      }
      Integer thief;
      while ((thief = thieves.poll()) != null) {
        final UTS loot = bag.split();
        if (loot != null && resilient) {
          transfer(thief, loot);
        }
        myUncountedAsyncAt(thief, w -> w.deal(loot));
      }
      if (bag.size > 0 && lifeline.get()) {
        final UTS loot = bag.split();
        if (loot != null) {
          thief = next;
          if (resilient) {
            transfer(thief, loot);
          }
          lifeline.set(false);
          myAsyncAt(next, w -> w.lifelinedeal(loot));
        }
      }
    }
  }

  public static List<UTS> step(List<UTS> bags, int wave, int power,
      boolean resilient) {
    final Place[] group = places().toArray(new Place[0]);
    while (bags.size() > (group.length << power)) {
      final UTS b = bags.remove(bags.size() - 1);
      bags.get(0).merge(b);
      bags.get(0).count += b.count;
    }
    final int s = bags.size();
    final int r = (group.length << power) / s;
    final ResilientUTS uts = PlaceLocalObject.make(Arrays.asList(group),
        () -> new ResilientUTS(wave, group, power, s, r, resilient));
    if (resilient) {
      for (int i = 0; i < s; i++) {
        uts.map.set(i * r, bags.get(i));
      }
    }
    try {
      finish(() -> {
        for (int i = 1; i < s; i++) {
          final UTS bag = bags.get(i);
          uts.myAsyncAt(i * r, w -> {
            w.bag.count = bag.count;
            w.lifelinedeal(bag);
          });
        }
        uts.workers[0].bag.count = bags.get(0).count;
        uts.workers[0].lifelinedeal(bags.get(0));
      });
    } catch (final DeadPlacesException e) {
    }
    final UTS bag = new UTS();
    final List<UTS> l = new ArrayList<UTS>();
    if (resilient) {
      final Collection<UTS> values = uts.hz
          .executeTransaction((TransactionalTaskContext context) -> {
            return context.<Integer, UTS> getMap("map" + wave).values();
          });
      for (final UTS b : values) {
        if (b.size > 0) {
          l.add(b);
        } else {
          bag.count += b.count;
        }
      }
    } else {
      for (final Place p : group) {
        bag.count += at(p, () -> {
          long count = 0;
          for (int i = 0; i < 1 << power; i++) {
            count += uts.workers[i].bag.count;
          }
          return count;
        });
      }
    }
    if (!l.isEmpty()) {
      l.get(0).count += bag.count;
    } else {
      l.add(bag);
    }
    return l;
  }

  static List<UTS> explode(UTS bag) {
    final List<UTS> bags = new ArrayList<UTS>();
    for (int i = 0; i < bag.upper[0]; i++) {
      final UTS b = new UTS(64);
      b.merge(bag);
      if (i == 0) {
        b.count = 1;
      }
      b.lower[0] = i;
      b.upper[0] = i + 1;
      bags.add(b);
    }
    return bags;
  }

  public static void main(String[] args) {
    int depth = 13;
    try {
      depth = Integer.parseInt(args[0]);
    } catch (final Exception e) {
    }
    int _power = 1;
    try {
      _power = Integer.parseInt(args[1]);
    } catch (final Exception e) {
    }
    final int power = _power;

    if (System.getProperty(Configuration.APGAS_PLACES) == null) {
      System.setProperty(Configuration.APGAS_PLACES, "2");
    }
    System.setProperty(Configuration.APGAS_THREADS, "" + ((1 << power) + 1));

    final boolean resilient = Boolean.getBoolean(Configuration.APGAS_RESILIENT);

    final int maxPlaces = places().size();

    final MessageDigest md = UTS.encoder();

    System.out.println("Warmup...");

    final UTS tmp = new UTS(64);
    tmp.seed(md, 19, depth - 2);
    finish(() -> ResilientUTS.step(explode(tmp), -1, power, resilient));

    System.out.println("Starting...");
    Long time = -System.nanoTime();

    final UTS bag = new UTS(64);
    bag.seed(md, 19, depth);
    List<UTS> bags = explode(bag);

    int wave = 0;
    while (bags.get(0).size > 0) {
      final int w = wave++;
      final List<UTS> b = bags;
      System.out.println("Wave: " + w);
      try {
        bags = finish(() -> ResilientUTS.step(b, w, power, resilient));
      } catch (final DeadPlacesException e) {
      }
    }

    time += System.nanoTime();
    System.out.println("Finished.");

    System.out.println("Depth: " + depth + ", Places: " + maxPlaces
        + ", Waves: " + wave + ", Performance: " + bags.get(0).count + "/"
        + UTS.sub("" + time / 1e9, 0, 6) + " = "
        + UTS.sub("" + (bags.get(0).count / (time / 1e3)), 0, 6) + "M nodes/s");
  }
}
