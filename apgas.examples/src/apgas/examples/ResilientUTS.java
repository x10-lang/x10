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
import apgas.util.Cell;
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

  ResilientUTS(int wave, List<? extends Place> group, int s) {
    this.group = group;
    this.wave = wave;
    map = hz.getMap("map" + wave);
    me = group.indexOf(here());
    random = new Random(me);
    prev = (me + group.size() - 1) % group.size();
    next = (me + 1) % group.size();
    lifeline = new AtomicBoolean(next >= s);
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
  public static ArrayList<UTS> step(ArrayList<UTS> col, int wave) {
    final List<? extends Place> group = places();
    while (col.size() > group.size()) {
      final UTS b = col.remove(col.size() - 1);
      col.get(0).merge(b);
      col.get(0).count += b.count;
    }
    final int s = col.size();
    final ResilientUTS uts = PlaceLocalObject.make(group,
        () -> new ResilientUTS(wave, group, s));
    for (int i = 0; i < col.size(); i++) {
      uts.map.set(i, col.get(i));
    }
    try {
      finish(() -> {
        for (int i = 1; i < col.size(); i++) {
          final UTS bag = col.get(i);
          asyncAt(group.get(i), () -> {
            uts.bag.count = bag.count;
            uts.lifelinedeal(bag);
          });
        }
        uts.bag.count = col.get(0).count;
        uts.lifelinedeal(col.get(0));
      });
    } catch (final MultipleException e) {
      if (!e.isDeadPlaceException()) {
        e.printStackTrace();
      }
    }
    return (ArrayList<UTS>) uts.hz
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

    System.out.println("Warmup...");

    final Cell<ArrayList<UTS>> cell = new Cell<ArrayList<UTS>>();
    cell.set(new ArrayList<UTS>());

    final UTS tmp = new UTS(64);
    final MessageDigest md = UTS.encoder();
    tmp.seed(md, 19, depth - 2);
    cell.get().add(tmp);
    finish(() -> ResilientUTS.step(cell.get(), -1));

    cell.get().clear();

    System.out.println("Starting...");
    Long time = -System.nanoTime();

    final UTS bag = new UTS(64);
    bag.seed(md, 19, depth);
    cell.get().add(bag);

    int wave = 0;
    while (cell.get().get(0).size > 0) {
      final int w = wave++;
      System.out.println("Wave: " + w);
      try {
        finish(() -> cell.set(ResilientUTS.step(cell.get(), w)));
      } catch (final MultipleException e) {
        if (!e.isDeadPlaceException()) {
          e.printStackTrace();
        }
      }
    }

    time += System.nanoTime();
    System.out.println("Finished.");

    System.out.println("Depth: " + depth + ", Places: " + maxPlaces
        + ", Waves: " + wave + ", Performance: " + cell.get().get(0).count
        + "/" + UTS.sub("" + time / 1e9, 0, 6) + " = "
        + UTS.sub("" + (cell.get().get(0).count / (time / 1e3)), 0, 6)
        + "M nodes/s");
  }
}
