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

public class HelloWholeWorldLoop2 {

    public static def main(args:Rail[String]):void {
        if (args.size < 2) {
            Console.OUT.println("Usage: HelloWholeWorldLoop2 message loopcount interval");
            return;
        }

        System.registerPlaceAddedHandler((p:Place)=>{Console.OUT.println("I see that "+p+" was added");});
        System.registerPlaceRemovedHandler((p:Place)=>{Console.OUT.println("I see that "+p+" was removed");});

        val loopcount = Long.parse(args(1));
        val interval = args.size < 3 ? 10000 : Long.parse(args(2));
        val tmp = System.getenv("X10_RESILIENT_MODE");
        val injectfailure = tmp != null && Long.parse(tmp) > 0;

        for (i in 0..(loopcount-1)) {
            try {
                val world = Place.places();
                Console.OUT.println(here+" sees "+world.numPlaces()+" places");
                finish for (p in world) {
                    at (p) async {
                        if (injectfailure && here.id != 0 && new x10.util.Random().nextBoolean()) {
                            Console.OUT.println(here+" is dying "+i);
                            System.killHere();
                        } else {
                            Console.OUT.println(here+" says hello and "+args(0)+" "+i);
                        }
                    }
                }
            }
            catch (me:MultipleExceptions) {
                for (e in me.getExceptionsOfType[DeadPlaceException](true)) {
                    Console.OUT.println("Got DeadPlaceException from "+e.place+" in round "+i);
                }
            }

            System.sleep(interval);
        }

        Console.OUT.println("Goodbye");
    }
}
