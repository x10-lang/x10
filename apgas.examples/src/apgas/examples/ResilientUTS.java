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

package apgas.examples;

import static apgas.Constructs.*;

import java.security.DigestException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import apgas.Configuration;
import apgas.DeadPlaceException;
import apgas.GlobalRuntime;
import apgas.MultipleException;
import apgas.Place;
import apgas.util.PlaceLocalObject;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.TransactionalMap;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.TransactionalTask;
import com.hazelcast.transaction.TransactionalTaskContext;

final class ResilientUTS extends PlaceLocalObject {
  final HazelcastInstance hz = Hazelcast.getHazelcastInstanceByName("apgas");
  final IMap<Integer, UTS> map;

  final int me; // my index in the lifeline graph
  final int prev; // index of the predecessor in the lifeline graph
  final int next; // index of the successor in the lifeline graph
  final int wave; // the integer id of the current wave
  final List<? extends Place> group; // the places in the current wave
  final Random random;
  final MessageDigest md = UTS.encoder();
  final UTS bag = new UTS(64);
  final ConcurrentLinkedQueue<Integer> thieves = new ConcurrentLinkedQueue<Integer>(); // pending
                                                                                       // requests
                                                                                       // from
                                                                                       // thieves
  final AtomicBoolean lifeline; // pending lifeline request?
  int state = -2; // -3: abort, -2: inactive, -1: running, p: stealing from p
  int transfers; // number of transactions so far

  ResilientUTS(int wave, List<? extends Place> group, int size, int ratio) {
    this.group = group;
    this.wave = wave;
    map = hz.getMap("map" + wave);
    me = group.indexOf(here());
    random = new Random(me);
    prev = (me + group.size() - 1) % group.size();
    next = (me + 1) % group.size();
    lifeline = new AtomicBoolean(((next % ratio) != 0)
        || ((next / ratio) >= size));
    GlobalRuntime.getRuntime().setPlaceFailureHandler(this::unblock);
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
        map.set(me, bag.trim());
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
    if (group.size() == 1) {
      return;
    }
    uncountedAsyncAt(group.get(prev), () -> {
      lifeline.set(true);
    });
  }

  void steal() {
    if (group.size() == 1) {
      return;
    }
    final int me = this.me;
    int p = random.nextInt(group.size() - 1);
    if (p >= me) {
      p++;
    }
    synchronized (this) {
      abort();
      state = p;
    }
    uncountedAsyncAt(group.get(p), () -> {
      request(me);
    });
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
    uncountedAsyncAt(group.get(thief), () -> {
      deal(null);
    });
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
    if (p.id > 0) {
      System.err.println("Observing failure of " + p + " from " + here());
    }
    if (!group.contains(p)) {
      return;
    }
    state = -3;
    notifyAll();
  }

  void transfer(int thief, UTS loot) {
    transfers++;
    final UTS bag = this.bag.trim();
    final int me = this.me;
    final int wave = this.wave;
    hz.executeTransaction(new TransactionalTask<Object>() {
      @Override
      public Object execute(TransactionalTaskContext context)
          throws TransactionException {
        final TransactionalMap<Integer, UTS> map = context.getMap("map" + wave);
        map.set(me, bag);
        final UTS old = map.getForUpdate(thief);
        loot.count = old == null ? 0 : old.count;
        map.set(thief, loot);
        return null;
      }
    });
  }

  void distribute() {
    if (group.size() == 1) {
      return;
    }
    Integer thief;
    while ((thief = thieves.poll()) != null) {
      final UTS loot = bag.split();
      if (loot != null) {
        transfer(thief, loot);
      }
      uncountedAsyncAt(group.get(thief), () -> {
        deal(loot);
      });
    }
    if (bag.size > 0 && lifeline.get()) {
      final UTS loot = bag.split();
      if (loot != null) {
        thief = next;
        transfer(thief, loot);
        lifeline.set(false);
        asyncAt(group.get(next), () -> {
          lifelinedeal(loot);
        });
      }
    }
  }

  @SuppressWarnings("unchecked")
  public static List<UTS> step(List<UTS> bags, int wave) {
    final List<? extends Place> group = places();
    while (bags.size() > group.size()) {
      final UTS b = bags.remove(bags.size() - 1);
      bags.get(0).merge(b);
      bags.get(0).count += b.count;
    }
    final int s = bags.size();
    final int r = group.size() / s;
    final ResilientUTS uts = PlaceLocalObject.make(group,
        () -> new ResilientUTS(wave, group, s, r));
    for (int i = 0; i < s; i++) {
      uts.map.set(i * r, bags.get(i));
    }
    try {
      finish(() -> {
        for (int i = 1; i < s; i++) {
          final UTS bag = bags.get(i);
          asyncAt(group.get(i * r), () -> {
            uts.bag.count = bag.count;
            uts.lifelinedeal(bag);
          });
        }
        uts.bag.count = bags.get(0).count;
        uts.lifelinedeal(bags.get(0));
      });
    } catch (final MultipleException e) {
      if (!e.isDeadPlaceException()) {
        e.printStackTrace();
      }
    }
    return (List<UTS>) uts.hz
        .executeTransaction(new TransactionalTask<Object>() {
          @Override
          public Object execute(TransactionalTaskContext context)
              throws TransactionException {
            final TransactionalMap<Integer, UTS> map = context.getMap("map"
                + wave);
            final UTS bag = new UTS(64);
            final ArrayList<UTS> l = new ArrayList<UTS>();
            for (final UTS b : map.values()) {
              if (b.size > 0) {
                l.add(b);
              } else {
                bag.count += b.count;
              }
            }
            if (!l.isEmpty()) {
              l.get(0).count += bag.count;
            } else {
              l.add(bag);
            }
            return l;
          }
        });
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

    if (System.getProperty(Configuration.APGAS_PLACES) == null) {
      System.setProperty(Configuration.APGAS_PLACES, "4");
    }
    System.setProperty(Configuration.APGAS_THREADS, "2");
    System.setProperty(Configuration.APGAS_RESILIENT, "true");

    final int maxPlaces = places().size();

    final MessageDigest md = UTS.encoder();

    System.out.println("Warmup...");

    final UTS tmp = new UTS(64);
    tmp.seed(md, 19, depth - 2);
    finish(() -> ResilientUTS.step(explode(tmp), -1));

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
        bags = finish(() -> ResilientUTS.step(b, w));
      } catch (final MultipleException e) {
        if (!e.isDeadPlaceException()) {
          e.printStackTrace();
        }
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
