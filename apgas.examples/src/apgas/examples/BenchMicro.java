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

import apgas.Configuration;
import apgas.Place;

public class BenchMicro {
  static int OUTER_ITERS = 100;
  static int INNER_ITERS = 100;

  // require each test to run for at least 10 seconds (reduce jitter)
  static long MIN_NANOS = 10 * 1000000000L;

  public static void main(String[] args) {
    if (System.getProperty(Configuration.APGAS_PLACES) == null) {
      System.setProperty(Configuration.APGAS_PLACES, "4");
    }

    if (places().size() < 2) {
      System.err.println(
          "Fair evaluation of place-zero based finish requires more than one place, and preferably more than one host.");
      System.exit(1);
    }

    final long thinkTime = args.length == 0 ? 0 : Long.parseLong(args[0]);

    final boolean resilient = Boolean.getBoolean(Configuration.APGAS_RESILIENT);
    System.out
        .println("Configuration: " + (resilient ? "" : "not ") + "resilient");

    System.out.println("Running with " + places().size() + " places.");
    System.out.println(
        "Min elapsed time for each test: " + MIN_NANOS / 1e9 + " seconds.");
    System.out.println(
        "Think time for each activity: " + thinkTime + " nanoseconds.");

    System.out.println("Test based from place 0");
    doTest("place 0 -- ", thinkTime);
    System.out.println();

    System.out.println("Test based from place 1");
    at(place(1), () -> doTest("place 1 -- ", thinkTime));
    System.out.println();
  }

  public static void doTest(final String prefix, long t) {
    long time0;
    long time1;
    long iterCount;
    final Place home = here();

    iterCount = 0;
    time0 = System.nanoTime();
    do {
      for (int i = 0; i < OUTER_ITERS; ++i) {
        finish(() -> {
        });
      }
      time1 = System.nanoTime();
      iterCount++;
    } while (time1 - time0 < MIN_NANOS);
    System.out.println(prefix + "empty finish: "
        + (time1 - time0) / 1E9 / OUTER_ITERS / iterCount + " seconds");

    iterCount = 0;
    time0 = System.nanoTime();
    do {
      for (int i = 0; i < OUTER_ITERS; ++i) {
        finish(() -> {
          for (int j = 0; j < INNER_ITERS; ++j) {
            async(() -> think(t));
          }
        });
      }
      time1 = System.nanoTime();
      iterCount++;
    } while (time1 - time0 < MIN_NANOS);
    System.out.println(
        prefix + "local termination of " + INNER_ITERS + " activities: "
            + (time1 - time0) / 1E9 / OUTER_ITERS / iterCount + " seconds");

    iterCount = 0;
    time0 = System.nanoTime();
    final Place next = place((home.id + 1) % places().size());
    do {
      for (int i = 0; i < OUTER_ITERS; ++i) {
        finish(() -> {
          asyncAt(next, () -> think(t));
        });
      }
      time1 = System.nanoTime();
      iterCount++;
    } while (time1 - time0 < MIN_NANOS);
    System.out.println(prefix + "single activity: "
        + (time1 - time0) / 1E9 / OUTER_ITERS / iterCount + " seconds");

    iterCount = 0;
    time0 = System.nanoTime();
    do {
      for (int i = 0; i < OUTER_ITERS; ++i) {
        finish(() -> {
          for (final Place p : places()) {
            asyncAt(p, () -> think(t));
          }
        });
      }
      time1 = System.nanoTime();
      iterCount++;
    } while (time1 - time0 < MIN_NANOS);
    System.out.println(prefix + "flat fan out: "
        + (time1 - time0) / 1E9 / OUTER_ITERS / iterCount + " seconds");

    iterCount = 0;
    time0 = System.nanoTime();
    do {
      for (int i = 0; i < OUTER_ITERS; ++i) {
        finish(() -> {
          for (final Place p : places()) {
            asyncAt(p, () -> {
              asyncAt(home, () -> think(t));
            });
          }
        });
      }
      time1 = System.nanoTime();
      iterCount++;
    } while (time1 - time0 < MIN_NANOS);
    System.out.println(prefix + "flat fan out, message back: "
        + (time1 - time0) / 1E9 / OUTER_ITERS / iterCount + " seconds");

    iterCount = 0;
    time0 = System.nanoTime();
    do {
      for (int i = 0; i < OUTER_ITERS; ++i) {
        finish(() -> {
          for (final Place p : places()) {
            asyncAt(p, () -> {
              finish(() -> {
                for (int j = 0; j < INNER_ITERS; ++j) {
                  async(() -> think(t));
                }
              });
            });
          }
        });
      }
      time1 = System.nanoTime();
      iterCount++;
    } while (time1 - time0 < MIN_NANOS);
    System.out.println(
        prefix + "fan out, internal work " + INNER_ITERS + " activities: "
            + (time1 - time0) / 1E9 / OUTER_ITERS / iterCount + " seconds");

    iterCount = 0;
    time0 = System.nanoTime();
    do {
      finish(() -> {
        for (final Place p : places()) {
          asyncAt(p, () -> {
            for (final Place q : places()) {
              asyncAt(q, () -> think(t));
            }
          });
        }
      });
      time1 = System.nanoTime();
      iterCount++;
    } while (time1 - time0 < MIN_NANOS);
    System.out.println(prefix + "fan out, broadcast: "
        + (time1 - time0) / 1E9 / iterCount + " seconds");

    iterCount = 0;
    time0 = System.nanoTime();
    do {
      finish(() -> {
        for (final Place p : places()) {
          asyncAt(p, () -> {
            finish(() -> {
              for (final Place q : places()) {
                asyncAt(q, () -> think(t));
              }
            });
          });
        }
      });
      time1 = System.nanoTime();
      iterCount++;
    } while (time1 - time0 < MIN_NANOS);
    System.out.println(prefix + "fan out, nested finish broadcast: "
        + (time1 - time0) / 1E9 / iterCount + " seconds");

    iterCount = 0;
    time0 = System.nanoTime();
    do {
      downTree(t);
      time1 = System.nanoTime();
      iterCount++;
    } while (time1 - time0 < MIN_NANOS);
    System.out.println(prefix + "tree fan out: "
        + (time1 - time0) / 1E9 / iterCount + " seconds");

    iterCount = 0;
    final Place endPlace = place(
        (home.id + places().size() - 1) % places().size());
    time0 = System.nanoTime();
    do {
      ring(t, endPlace);
      time1 = System.nanoTime();
      iterCount++;
    } while (time1 - time0 < MIN_NANOS);
    System.out.println(prefix + "ring around via at: "
        + (time1 - time0) / 1E9 / iterCount + " seconds");
  }

  private static void downTree(long t) {
    think(t);
    final int parent = here().id;
    final int child1 = parent * 2 + 1;
    final int child2 = child1 + 1;
    if (child1 < places().size() || child2 < places().size()) {
      finish(() -> {
        if (child1 < places().size()) {
          asyncAt(place(child1), () -> downTree(t));
        }
        if (child2 < places().size()) {
          asyncAt(place(child2), () -> downTree(t));
        }
      });
    }
  }

  private static void ring(long t, Place destination) {
    think(t);
    if (destination.equals(here())) {
      return;
    }
    final Place nextHop = place((here().id + 1) % places().size());
    at(nextHop, () -> ring(t, destination));
  }

  public static void think(long t) {
    if (t == 0) {
      return;
    }
    final long start = System.nanoTime();
    do {
    } while (System.nanoTime() - start < t);
  }

}
