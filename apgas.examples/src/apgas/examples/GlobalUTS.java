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
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import apgas.Configuration;
import apgas.Place;
import apgas.util.PlaceLocalObject;

final class GlobalUTS extends PlaceLocalObject {
  final Place home = here();
  final int places = places().size();
  final Random random = new Random(home.id);
  final MessageDigest md = UTS.encoder();
  final UTS bag = new UTS(64);
  final ConcurrentLinkedQueue<Place> thieves = new ConcurrentLinkedQueue<Place>();
  final AtomicBoolean lifeline = new AtomicBoolean(home.id != places - 1);
  int state = -2; // -2: inactive, -1: running, p: stealing from p

  void seed(int s, int d) {
    bag.seed(md, s, d);
  }

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
    asyncAt(place((home.id + places - 1) % places), () -> {
      lifeline.set(true);
    });
  }

  void steal() {
    if (places == 1) {
      return;
    }
    final Place h = home;
    int p = random.nextInt(places - 1);
    if (p >= h.id) {
      p++;
    }
    synchronized (this) {
      state = p;
    }
    uncountedAsyncAt(place(p), () -> {
      request(h);
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

  void request(Place p) {
    synchronized (this) {
      if (state == -1) {
        thieves.add(p);
        return;
      }
    }
    final Place h = home;
    uncountedAsyncAt(p, () -> {
      deal(h, null);
    });
  }

  void lifelinedeal(UTS b) throws DigestException {
    bag.merge(b);
    run();
  }

  synchronized void deal(Place p, UTS b) {
    assert state == p.id;
    if (b != null) {
      bag.merge(b);
    }
    state = -1;
    notifyAll();
  }

  void distribute() {
    if (places == 1) {
      return;
    }
    Place p;
    while ((p = thieves.poll()) != null) {
      final UTS b = bag.split();
      final Place h = home;
      uncountedAsyncAt(p, () -> {
        deal(h, b);
      });
    }
    if (bag.size > 0 && lifeline.get()) {
      final UTS b = bag.split();
      if (b != null) {
        p = place((home.id + 1) % places);
        lifeline.set(false);
        asyncAt(p, () -> {
          lifelinedeal(b);
        });
      }
    }
  }

  public void reset() {
    bag.count = 0;
    lifeline.set(home.id != places - 1);
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

    final GlobalUTS uts = PlaceLocalObject
        .make(places(), () -> new GlobalUTS());

    System.out.println("Warmup...");
    uts.seed(19, depth - 2);
    finish(uts::run);

    finish(() -> {
      for (final Place p : places()) {
        asyncAt(p, uts::reset);
      }
    });

    System.out.println("Starting...");
    long time = System.nanoTime();

    uts.seed(19, depth);
    finish(uts::run);

    long count = 0;
    // collect all counts
    for (final Place p : places()) {
      count += at(p, () -> uts.bag.count);
    }

    time = System.nanoTime() - time;
    System.out.println("Finished.");

    System.out.println("Depth: " + depth + ", Places: " + uts.places
        + ", Performance: " + count + "/" + UTS.sub("" + time / 1e9, 0, 6)
        + " = " + UTS.sub("" + (count / (time / 1e3)), 0, 6) + "M nodes/s");
  }
}
