/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2014.
 */

import x10.io.Console;

class HelloWholeWorldLoop {
  public static def main(args:Rail[String]):void {
     if (args.size < 2) {
         Console.OUT.println("Usage: HelloWholeWorld message loopcount");
         return;
     }
     
     for (i:Long in 0..(Long.parse(args(1)))){
         try {
             val nplaces = Place.numPlaces();
             Console.OUT.println(here+" sees "+nplaces+" places");
             finish for (p in 0..(nplaces-1)) { // can't use Place.places() because that caches numplaces in Place.places()
                 at (Place(p)) async Console.OUT.println(here+" says hello and "+args(0)+" "+i);
             }
         }
         catch (e:DeadPlaceException) {
             Console.OUT.println("Got DeadPlaceException from "+e.place+" in round "+i);
         }
         
         System.sleep(10000);
     }
     
     Console.OUT.println("Goodbye");
  }
}


