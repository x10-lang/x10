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
        asyncat(place, () -> System.out.println("Hello from " + here()));
      }
    });

    System.out.println("Bye");
  }
}
