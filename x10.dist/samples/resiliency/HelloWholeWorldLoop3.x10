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

import x10.util.resilient.PlaceManager;

/**
 * An example program showing the usage of the x10.util.resilient.PlaceManager.
 * This program is intended to be run with an X10 launcher
 * that supports automatic creation of new Places; for example
 * the x10rt yarn launcher.
 *
 * Run as "X10_RESILIENT_MODE=1 X10_NTHREADS=1 X10_NPLACES=4 x10 -x10rt yarn -cp x10yarn.jar:HelloWholeWorldLoop3.jar HelloWholeWorldLoop3 hi 10 5000"
 */
public class HelloWholeWorldLoop3 {

    public static def main(args:Rail[String]):void {
        if (args.size < 2) {
            Console.OUT.println("Usage: HelloWholeWorldLoop3 message loopcount interval");
            return;
        }

        System.registerPlaceAddedHandler((p:Place)=>{Console.OUT.println("I see that "+p+" was added");});
        System.registerPlaceRemovedHandler((p:Place)=>{Console.OUT.println("I see that "+p+" was removed");});

        val pm = new PlaceManager();        

        val loopcount = Long.parse(args(1));
        val interval = args.size < 3 ? 10000 : Long.parse(args(2));
        val resilientMode = Long.parse(System.getenv().getOrElse("X10_RESILIENT_MODE", "0"));
        val injectfailure = resilientMode > 0;
        val hc = resilientMode == 12; // See x10.xrx.Configuration.
        val rand = new x10.util.Random();

        for (i in 0..(loopcount-1)) {
            try {
                val world = pm.activePlaces();
                Console.OUT.println(here+" sees "+world.numPlaces()+" places");
                val victim = rand.nextLong(world.numPlaces()-2) + 1;
                finish for (p in world) {
                    at (p) async {
                        if (injectfailure && world.indexOf(here) == victim) {
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
            
            pm.rebuildActivePlaces();
        }

        Console.OUT.println("Goodbye");
    }
}
