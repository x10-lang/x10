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
import apgas.GlobalRuntime;

final class Fibonacci {
  static int seqfib(final int n) {
    if (n < 2) {
      return n;
    }
    return seqfib(n - 1) + seqfib(n - 2);
  }

  static int parfib(final int n) {
    if (n < 22) {
      return seqfib(n);
    }
    final int result[] = new int[2];
    finish(() -> {
      async(() -> result[0] = parfib(n - 2));
      result[1] = parfib(n - 1);
    });
    return result[0] + result[1];
  }

  public static void main(String[] args) {
    int n = 42;
    if (args.length > 0) {
      try {
        n = Integer.parseInt(args[0]);
      } catch (final NumberFormatException e) {
        System.err.println("usage: java Fibonacci [int]");
        System.exit(1);
      }
    }

    // Force global runtime initialization before we start computing
    System.out.println("Initializing the global runtime...");
    GlobalRuntime.getRuntime();

    System.out.println("Beginning to compute fib(" + n + ") sequentially...");
    for (int i = 0; i < 5; i++) {
      long time = System.nanoTime();
      final int f = seqfib(n);
      time = System.nanoTime() - time;
      System.out.println("fib(" + n + ")=" + f + " in " + time / 1e9 + "s");
    }

    System.out.println("Beginning to compute fib(" + n + ") in parallel...");
    for (int i = 0; i < 5; i++) {
      long time = System.nanoTime();
      final int f = parfib(n);
      time = System.nanoTime() - time;
      System.out.println("fib(" + n + ")=" + f + " in " + time / 1e9 + "s");
    }
  }
}
