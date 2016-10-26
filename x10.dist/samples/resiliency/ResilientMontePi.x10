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

import x10.util.Random;

/**
 * Calculation of an approximation to pi by using a Monte Carlo simulation
 * (throwing darts into the unit square and determining the fraction that land
 * in the unit circle).
 *
 * This is an example of shrinking recovery in Resilient X10. Since the
 * result is already approximate, we can simply suppress failures.
 */
public class ResilientMontePi {

    static class Result {
        var inCircle:Long = 0; // darts that landed in the unit circle
        var thrown:Long = 0;   // darts thrown

        def this() { }

        def this(hits:Long, thrown:Long) {
            inCircle = hits;
            this.thrown = thrown;
        }

        def accumulate(other:Result) {
            inCircle += other.inCircle;
            thrown += other.thrown;
        }
    }

    public static def main (args:Rail[String]) {
        val N = args.size > 0 ? Long.parse(args(0)) : 10000000;
        val result = GlobalRef(new Result());

        finish for (p in Place.places()) async {
            try {
                at (p) {
                    val rand = new Random();
                    var hits:Long = 0;
                    for (iter in 1..N) {
                        val x = rand.nextDouble();
                        val y = rand.nextDouble();
                        if (x*x + y*y <= 1.0) hits++;
                    }
                    val myPiece = new Result(hits, N);
                    Console.OUT.println("Work done at: "+here);
                    at (result) atomic {
                        result().accumulate(myPiece);
                    }
                }
            } catch (e:DeadPlaceException) {
                Console.OUT.println("Got DeadPlaceException from "+e.place);
            }
        }

        val pi = (4.0 * result().inCircle) / result().thrown;
        Console.OUT.println("pi = "+pi+" calculated with "+result().thrown+" samples.");
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab

