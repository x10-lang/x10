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

import java.util.List;

import apgas.Configuration;
import apgas.DeadPlaceException;
import apgas.MultipleException;
import apgas.Place;

final class ResilientHelloWorld {
  public static void main(String[] args) {
    System.setProperty(Configuration.APGAS_RESILIENT, "true");
    System.setProperty(Configuration.APGAS_SERIALIZATION_EXCEPTION, "true");
    // Run with four places unless specified otherwise
    if (System.getProperty(Configuration.APGAS_PLACES) == null) {
      System.setProperty(Configuration.APGAS_PLACES, "4");
    }

    System.out.println("Running main at " + here() + " of " + places().size()
        + " places");

    for (;;) {
      try {
        finish(() -> {
          final List<? extends Place> world = places();
          System.err.println("There are " + world.size() + " places");
          for (final Place place : world) {
            finish(() -> asyncat(place,
                () -> finish(() -> System.out.println("Hello from " + here()))));
          }
        });
      } catch (final MultipleException | DeadPlaceException e) {
        System.err.println("Ignoring MultipleException");
        try {
          Thread.sleep(2000);
        } catch (final InterruptedException x) {
        }
      }
    }
  }
}
