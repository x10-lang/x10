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

import java.io.Serializable;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import apgas.Configuration;
import apgas.DeadPlaceException;
import apgas.GlobalRuntime;
import apgas.MultipleException;
import apgas.Place;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.TransactionalMap;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.TransactionalTask;
import com.hazelcast.transaction.TransactionalTaskContext;

final class UTX {
  static final class Bag implements Serializable {
    private static final long serialVersionUID = 2200935927036145803L;

    final byte[] hash;
    final int[] depth;
    final int[] lower;
    final int[] upper;
    int size;

    Bag(int n) {
      hash = new byte[n * 20 + 4]; // slack for in-place SHA1 computation
      depth = new int[n];
      lower = new int[n];
      upper = new int[n];
    }

    Bag split() {
      int s = 0;
      for (int i = 0; i < size; ++i) {
        if ((upper[i] - lower[i]) >= 2) {
          ++s;
        }
      }
      if (s == 0) {
        return null;
      }
      final Bag b = new Bag(s);
      for (int i = 0; i < size; ++i) {
        final int p = upper[i] - lower[i];
        if (p >= 2) {
          System.arraycopy(hash, i * 20, b.hash, b.size * 20, 20);
          b.depth[b.size] = depth[i];
          b.upper[b.size] = upper[i];
          upper[i] -= p / 2;
          b.lower[b.size++] = upper[i];
        }
      }
      return b;
    }

    void merge(Bag b) {
      System.arraycopy(b.hash, 0, hash, size * 20, b.size * 20);
      System.arraycopy(b.depth, 0, depth, size, b.size);
      System.arraycopy(b.lower, 0, lower, size, b.size);
      System.arraycopy(b.upper, 0, upper, size, b.size);
      size += b.size;
    }
  }

  static final class Checkpoint implements Serializable {
    private static final long serialVersionUID = -7591718010712195252L;

    final Bag bag;
    final long count;

    Checkpoint(long c) {
      bag = null;
      count = c;
    }

    Checkpoint(Bag b, long c) {
      if (b.size == b.depth.length) {
        bag = b;
      } else {
        bag = new Bag(b.size);
        System.arraycopy(b.hash, 0, bag.hash, 0, b.size * 20);
        System.arraycopy(b.depth, 0, bag.depth, 0, b.size);
        System.arraycopy(b.lower, 0, bag.lower, 0, b.size);
        System.arraycopy(b.upper, 0, bag.upper, 0, b.size);
        bag.size = b.size;
      }
      count = c;
    }
  }

  static final class Worker {
    static final int power = 3;
    static final Worker[] uts = new Worker[1 << power];

    static MessageDigest encoder() {
      try {
        return MessageDigest.getInstance("SHA-1");
      } catch (final NoSuchAlgorithmException e) {
      }
      return null;
    }

    synchronized void handle(Place p) {
      // p is dead, unblock if waiting on p
      if (state >> power == p.id) {
        // attempt to extract loot from store
        final Checkpoint c = map.get(location);
        if (c.bag != null) {
          merge(c.bag);
        }
        state = -1;
        notifyAll();
      }
    }

    final HazelcastInstance hz = Hazelcast.getHazelcastInstanceByName("apgas");
    final IMap<Integer, Checkpoint> map = hz.getMap("map");
    final int location;
    final int locations = places().size() << power;

    static {
      for (int id = 0; id < (1 << power); ++id) {
        uts[id] = new Worker(id + (here().id << power));
      }
      GlobalRuntime.getRuntime().setPlaceFailureHandler(place -> {
        if (place.id > 0) {
          System.err.println(here() + " observes that " + place + " failed!");
        }
        for (int id = 0; id < (1 << power); ++id) {
          uts[id].handle(place);
        }
      });
    }

    Worker(int location) {
      this.location = location;
      lifeline = new AtomicBoolean(location != locations - 1);
    }

    final Random random = new Random();
    final MessageDigest md = encoder();
    final double den = Math.log(4.0 / (1.0 + 4.0)); // branching factor: 4.0
    Bag bag = new Bag(4096);
    long count;
    long transfers;

    final ConcurrentLinkedQueue<Integer> thieves = new ConcurrentLinkedQueue<Integer>();
    final AtomicBoolean lifeline;
    int state = -2; // -2: inactive, -1: running, p: stealing from p

    int digest() throws DigestException {
      if (bag.size >= bag.depth.length) {
        grow();
      }
      final int offset = bag.size * 20;
      md.digest(bag.hash, offset, 20);
      ++count;
      final int v = ((0x7f & bag.hash[offset + 16]) << 24)
          | ((0xff & bag.hash[offset + 17]) << 16)
          | ((0xff & bag.hash[offset + 18]) << 8)
          | (0xff & bag.hash[offset + 19]);
      return (int) (Math.log(1.0 - v / 2147483648.0) / den);
    }

    void init(int seed, int depth) throws DigestException {
      bag.hash[16] = (byte) (seed >> 24);
      bag.hash[17] = (byte) (seed >> 16);
      bag.hash[18] = (byte) (seed >> 8);
      bag.hash[19] = (byte) seed;
      md.update(bag.hash, 0, 20);
      final int v = digest();
      if (v > 0) {
        bag.depth[0] = depth;
        bag.upper[0] = v;
        bag.size = 1;
      }
      map.set(location, new Checkpoint(bag, count));
    }

    void expand() throws DigestException {
      final int top = bag.size - 1;
      final int d = bag.depth[top];
      final int l = bag.lower[top];
      final int u = bag.upper[top] - 1;
      if (d > 1) {
        if (u == l) {
          --bag.size;
        } else {
          bag.upper[top] = u;
        }
        final int offset = top * 20;
        bag.hash[offset + 20] = (byte) (u >> 24);
        bag.hash[offset + 21] = (byte) (u >> 16);
        bag.hash[offset + 22] = (byte) (u >> 8);
        bag.hash[offset + 23] = (byte) u;
        md.update(bag.hash, offset, 24);
        final int v = digest();
        if (v > 0) {
          bag.depth[bag.size] = d - 1;
          bag.lower[bag.size] = 0;
          bag.upper[bag.size++] = v;
        }
      } else {
        --bag.size;
        count += 1 + u - l;
      }
    }

    void grow() {
      final Bag b = new Bag(bag.depth.length * 2);
      System.arraycopy(bag.hash, 0, b.hash, 0, bag.size * 20);
      System.arraycopy(bag.depth, 0, b.depth, 0, bag.size);
      System.arraycopy(bag.lower, 0, b.lower, 0, bag.size);
      System.arraycopy(bag.upper, 0, b.upper, 0, bag.size);
      b.size = bag.size;
      bag = b;
    }

    void run() throws DigestException {
      System.err.println(location + " starting");
      synchronized (this) {
        state = -1;
      }
      while (bag.size > 0) {
        while (bag.size > 0) {
          for (int n = 500; (n > 0) && (bag.size > 0); --n) {
            expand();
          }
          distribute();
        }
        map.set(location, new Checkpoint(count));
        steal();
      }
      synchronized (this) {
        state = -2;
      }
      lifelinesteal();
      System.err.println(location + " stopping");
      distribute();
    }

    void lifelinesteal() {
      if (locations == 1) {
        return;
      }
      try {
        final int victim = (location + locations - 1) % locations;
        final int id = victim & ((1 << power) - 1);
        asyncat(place(victim >> power), () -> {
          uts[id].lifeline.set(true);
        });
      } catch (final DeadPlaceException e) {
        // TODO should go to next lifeline, but correct as is
      }
    }

    void steal() {
      if (locations == 1) {
        return;
      }
      final int thief = location;
      int victim = random.nextInt(locations - 1);
      if (victim >= thief) {
        victim++;
      }
      if (!places().contains(place(victim >> power))) {
        // TODO should try other place, but ok as is
        return;
      }
      synchronized (this) {
        state = victim;
      }
      try {
        final int id = victim & ((1 << power) - 1);
        uncountedasyncat(place(victim >> power), () -> {
          uts[id].request(thief);
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

    void request(int thief) {
      synchronized (this) {
        if (state == -1) {
          thieves.add(thief);
          return;
        }
      }
      try {
        final int victim = location;
        final int id = thief & ((1 << power) - 1);
        uncountedasyncat(place(thief >> power), () -> {
          uts[id].deal(victim, null);
        });
      } catch (final DeadPlaceException e) {
        // place is dead, nothing to do
      }
    }

    void merge(Bag b) {
      while (bag.size + b.size > bag.depth.length) {
        grow();
      }
      bag.merge(b);
    }

    void lifelinedeal(Bag b) throws DigestException {
      merge(b);
      run();
    }

    synchronized void deal(int victim, Bag b) {
      if (state != victim) {
        // victim is dead, ignore late distribution
        return;
      }
      if (b != null) {
        merge(b);
      }
      state = -1;
      notifyAll();
    }

    void transfer(int thief, Bag b) {
      transfers++;
      while (true) {
        try {
          hz.executeTransaction(new TransactionalTask<Object>() {
            @Override
            public Object execute(TransactionalTaskContext context)
                throws TransactionException {
              final TransactionalMap<Integer, Checkpoint> map = context
                  .getMap("map");
              map.set(location, new Checkpoint(bag, count));
              final Checkpoint c = map.getForUpdate(thief);
              final long n = c == null ? 0 : c.count;
              map.set(thief, new Checkpoint(b, n));
              return null;
            }
          });
          return;
        } catch (final Throwable t) {
          System.err.println("Exception in transaction at " + location
              + "... retrying");
          t.printStackTrace();
        }
      }
    }

    void distribute() {
      Integer thief;
      if (lifeline.get()) {
        final Bag b = bag.split();
        if (b != null) {
          thief = (location + 1) % locations;
          lifeline.set(false);
          transfer(thief, b);
          try {
            final int id = thief & ((1 << power) - 1);
            asyncat(place(thief >> power), () -> {
              uts[id].lifelinedeal(b);
            });
          } catch (final DeadPlaceException e) {
            // thief died, nothing to do
          }
        }
      }
      while ((thief = thieves.poll()) != null) {
        final Bag b = bag.split();
        if (b != null) {
          transfer(thief, b);
        }
        try {
          final int victim = location;
          final int id = thief & ((1 << power) - 1);
          uncountedasyncat(place(thief >> power), () -> {
            uts[id].deal(victim, b);
          });
        } catch (final DeadPlaceException e) {
          // thief died, nothing to do
        }
      }
    }

    long seq(Bag b) {
      count = 0;
      try {
        bag = b;
        while (bag.size > 0) {
          expand();
        }
      } catch (final DigestException e) {
      }
      return count;
    }
  }

  static String sub(String str, int start, int end) {
    return str.substring(start, Math.min(end, str.length()));
  }

  public static void main(String[] args) {
    if (System.getProperty(Configuration.APGAS_PLACES) == null) {
      System.setProperty(Configuration.APGAS_PLACES, "4don;");
    }
    System.setProperty(Configuration.APGAS_SERIALIZATION_EXCEPTION, "true");
    System.setProperty(Configuration.APGAS_RESILIENT, "true");
    GlobalRuntime.getRuntime(); // force init

    int d = 13;
    try {
      d = Integer.parseInt(args[0]);
    } catch (final Exception e) {
    }
    final int depth = d;

    System.out.println("Starting...");
    long time = System.nanoTime();

    try {
      finish(() -> {
        Worker.uts[0].init(19, depth);
        Worker.uts[0].run();
      });
    } catch (final MultipleException e) {
      if (!e.isDeadPlaceException()) {
        throw e;
      }
    }

    long count = 0;
    // collect all counts
    for (final Checkpoint c : Worker.uts[0].map.values()) {
      count += c.count;
    }

    // if places have died, process remaining nodes sequentially at place 0
    for (final Map.Entry<Integer, Checkpoint> e : Worker.uts[0].map.entrySet()) {
      final Bag b = e.getValue().bag;
      if (b != null && b.size != 0) {
        System.err.println("Recovering " + e.getKey());
        count += Worker.uts[0].seq(b);
      }
    }

    time = System.nanoTime() - time;
    System.out.println("Finished.");

    long transfers = 0;
    // collect all counts
    for (final Place p : places()) {
      transfers += at(p, () -> {
        int v = 0;
        for (int id = 0; id < (1 << Worker.power); ++id) {
          v += Worker.uts[id].transfers;
        }
        return v;
      });
    }

    System.out.println("Performance: " + count + "/"
        + sub("" + time / 1e9, 0, 6) + " = "
        + sub("" + (count / (time / 1e3)), 0, 6) + "M nodes/s using "
        + transfers + " transactions");
  }
}
