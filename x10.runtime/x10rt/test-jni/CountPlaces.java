/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import x10.x10rt.X10RT;

public class CountPlaces {
  public static void main(String[] args) throws InterruptedException {

    X10RT.barrier();

    if (X10RT.here() == X10RT.getPlace(X10RT.numPlaces()-1)) {
      System.out.println(X10RT.here()+" is about to sleep for 1 second...");
      Thread.sleep(1000);
      System.out.println("Hello world: I am the tardy node "+X10RT.here());
    } else {
      System.out.println("Hello world: There are " +X10RT.numPlaces()+" Places and I am "+X10RT.here());
    }

    X10RT.barrier();

    System.out.println("Exiting from "+X10RT.here());
  }
}
