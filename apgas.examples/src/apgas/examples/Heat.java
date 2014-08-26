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

/**
 * Implements a five-point Laplacian 2D array stencil, similar to the
 * HeatTransfer example from the X10 2.1 tutorial
 *
 * TODO global reduction over maxDelta
 *
 * @see http://x10.sourceforge.net/tutorials/x10-2.1/SC_2010/
 *      SC10_tut143_X10_Tutorial_final_v3.html
 */
public class Heat {
  final static double EPSILON = 1.0e-5;
  final static double TIME_STEPS = 1000;
  int N;
  double[] current;
  double[] previous;

  public Heat(int N) {
    this.N = N;
    current = new double[N * N];
    previous = new double[N * N];
  }

  public double parallelStencil() {
    final double[] temp = previous;
    previous = current;
    current = temp;
    final double maxDelta = 0;
    finish(() -> {
      for (int i = 1; i <= N - 2; i++) {
        final int x = i;
        async(() -> {
          double localMax = 0.0;
          for (int y = 1; y <= N - 2; y++) {
            current[x * N + y] = (previous[x * N + y + 1]
                + previous[x * N + y - 1] + previous[(x + 1) * N + y] + previous[(x - 1)
                * N + y]) / 4.0;
            localMax = Math.max(localMax,
                Math.abs(current[x * N + y] - previous[x * N + y]));
          }
          // TODO reduction
          // atomic maxDelta = Math.max(localMax, maxDelta);
        });
      }
    });
    return maxDelta;
  }

  public void initialise() {
    for (int y = 0; y < N; y++) {
      current[y] = 1.0;
      previous[y] = 1.0;
    }
    for (int x = 1; x <= N - 2; x++) {
      for (int y = 1; y <= N - 2; y++) {
        current[x * N + y] = 0.0;
      }
    }
  }

  @SuppressWarnings("unused")
  private void printGrid() {
    System.out.println("grid = ");
    for (int x = 0; x <= N - 1; x++) {
      for (int y = 0; y <= N - 1; y++) {
        System.out.print(current[x * N + y] + " ");
      }
      System.out.println();
    }
  }

  public boolean run() {
    initialise();
    final long start = System.nanoTime();
    for (int t = 1; t <= TIME_STEPS; t++) {
      final double maxDelta = parallelStencil();
      if (maxDelta < EPSILON) {
        System.out.println("converged after " + t + " time steps");
        break;
      }
    }
    final long stop = System.nanoTime();
    // printGrid();
    System.out.printf(
        "parallel five-point stencil over %d elements avg: %g ms\n", N,
        (stop - start) / 1e06 / TIME_STEPS);

    return true;
  }

  public static void main(String[] args) {
    int elementsPerPlace = 512;
    if (args.length > 0) {
      try {
        elementsPerPlace = Integer.parseInt(args[0]);
      } catch (final NumberFormatException e) {
        System.err.println("usage: java Heat [int]");
        System.exit(1);
      }
    }
    // Force global runtime initialization before we start computing
    GlobalRuntime.getRuntime();
    new Heat(elementsPerPlace).run();
  }
}
