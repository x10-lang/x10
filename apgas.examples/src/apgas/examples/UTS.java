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
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import apgas.Configuration;
import apgas.Place;

final class Bag implements Serializable {
  private static final long serialVersionUID = 2200935927036145803L;

  final byte[] hash;
  final int[] depth;
  final int[] lower;
  final int[] upper;
  int size;

  Bag(int n, int slack) {
    hash = new byte[n * 20 + slack];
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
    final Bag bag = new Bag(s, 0);
    for (int i = 0; i < size; ++i) {
      final int p = upper[i] - lower[i];
      if (p >= 2) {
        System.arraycopy(hash, i * 20, bag.hash, bag.size * 20, 20);
        bag.depth[bag.size] = depth[i];
        bag.upper[bag.size] = upper[i];
        upper[i] -= p / 2;
        bag.lower[bag.size++] = upper[i];
      }
    }
    return bag;
  }

  void merge(Bag bag) {
    System.arraycopy(bag.hash, 0, hash, size * 20, bag.size * 20);
    System.arraycopy(bag.depth, 0, depth, size, bag.size);
    System.arraycopy(bag.lower, 0, lower, size, bag.size);
    System.arraycopy(bag.upper, 0, upper, size, bag.size);
    size += bag.size;
  }
}

final class UTS {
  static final UTS uts = new UTS();

  static MessageDigest encoder() {
    try {
      return MessageDigest.getInstance("SHA-1");
    } catch (final NoSuchAlgorithmException e) {
    }
    return null;
  }

  final Random random = new Random();
  final MessageDigest md = encoder();
  final double den = Math.log(4.0 / (1.0 + 4.0)); // branching factor: 4.0
  final Bag bag = new Bag(4096, 4);
  long count;

  final ConcurrentLinkedQueue<Place> thieves = new ConcurrentLinkedQueue<Place>();
  boolean lifeline = true;
  int state; // 0: inactive, 1: running, 2: stealing

  int digest() throws DigestException {
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

  void run() throws DigestException {
    System.err.println(here() + " starting");
    synchronized (this) {
      state = 1;
    }
    while (bag.size > 0) {
      while (bag.size > 0) {
        for (int n = 500; (n > 0) && (bag.size > 0); --n) {
          expand();
        }
        distribute();
      }
      steal();
    }
    synchronized (this) {
      state = 0;
    }
    lifelinesteal();
    System.err.println(here() + " stopping");
    distribute();
  }

  void lifelinesteal() {
    asyncat(place((here().id + places().size() - 1) % places().size()), () -> {
      uts.lifeline = true;
    });
  }

  void steal() {
    if (places().size() == 1) {
      return;
    }
    final Place h = here();
    int p = random.nextInt(places().size() - 1);
    if (p >= h.id) {
      p++;
    }
    synchronized (this) {
      state = 2;
    }
    uncountedasyncat(place(p), () -> {
      uts.request(h);
    });
    synchronized (this) {
      while (state >= 2) {
        try {
          wait();
        } catch (final InterruptedException e) {
        }
      }
    }
  }

  void request(Place h) {
    synchronized (this) {
      if (state == 1) {
        thieves.add(h);
        return;
      }
    }
    uncountedasyncat(h, () -> {
      uts.deal(null);
    });
  }

  void lifelinedeal(Bag b) throws DigestException {
    bag.merge(b);
    run();
  }

  void deal(Bag b) {
    if (b != null) {
      bag.merge(b);
    }
    synchronized (this) {
      state = 1;
      notifyAll();
    }
  }

  void distribute() {
    if (lifeline) {
      final Bag b = bag.split();
      if (b != null) {
        lifeline = false;
        asyncat(place((here().id + 1) % places().size()), () -> {
          uts.lifelinedeal(b);
        });
      }
    }
    Place p;
    while ((p = thieves.poll()) != null) {
      final Bag b = bag.split();
      uncountedasyncat(p, () -> {
        uts.deal(b);
      });
    }
  }

  static String sub(String str, int start, int end) {
    return str.substring(start, Math.min(end, str.length()));
  }

  static void print(long time, long count) {
    System.out.println("Performance: " + count + "/"
        + sub("" + time / 1e9, 0, 6) + " = "
        + sub("" + (count / (time / 1e3)), 0, 6) + "M nodes/s");
  }

  public static void main(String[] args) {
    if (System.getProperty(Configuration.APGAS_PLACES) == null) {
      System.setProperty(Configuration.APGAS_PLACES, "4");
    }
    finish(() -> {
      asyncat(place(places().size() - 1), () -> {
        uts.lifeline = false;
      });
    });
    System.out.println("Starting...");
    long time = System.nanoTime();
    finish(() -> {
      uts.init(19, 13);
      uts.run();
    });
    long count = 0;
    for (final Place place : places()) {
      count += at(place, () -> {
        return uts.count;
      });
    }
    time = System.nanoTime() - time;
    System.out.println("Finished.");
    print(time, count);
  }
}
