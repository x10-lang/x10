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
import apgas.Configuration;
import apgas.Place;

final class HelloWorld {
  public static void main(String[] args) {
    // Run with four places unless specified otherwise
    if (System.getProperty(Configuration.APGAS_PLACES) == null) {
      System.setProperty(Configuration.APGAS_PLACES, "4");
    }

    System.out.println("Running main at " + here() + " of " + places().size()
        + " places");

    finish(() -> {
      for (final Place place : places()) {
        asyncAt(place, () -> System.out.println("Hello from " + here()));
      }
    });

    System.out.println("Bye");
  }
}
