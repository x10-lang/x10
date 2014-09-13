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

import harness.x10Test;
import x10.util.Team;

/**
 * Unit tests for scatter functionality of teams
 */
public class Scatter extends x10Test {

    def bcastTest(team:Team, root:Place, res:GlobalRef[Cell[Boolean]]) {
        val count = 5L;
        val sz = count*team.size();
        var success: Boolean = true;

        {
            val src:Rail[Double];
            if (here == root) {
                src = new Rail[Double](sz, (i:Long)=> i as Double);
            } else {
                src = null;
            }
            val dst = new Rail[Double](count, (i:Long)=> -1 as Double);
            team.scatter(root, src, 0L, dst, 0L, count);

            for (i in 0..(count-1)) {
                val oracle:Double = here.id*count + i;
                if (dst(i) != oracle) {
                    Console.OUT.printf("Team %d place %d received invalid value %f at %d instead of %f\n",
                                       team.id(), here.id, dst(i), i, oracle);
                    success = false;
                }
            }
        }

        {
            val src:Rail[Complex];
            if (here == root) {
                src = new Rail[Complex](sz, (i:Long)=> (Complex(i, 0)));
            } else {
                src = null;
            }
            val dst = new Rail[Complex](count, (i:Long)=> Complex(-1, 0));
            team.scatter(root, src, 0L, dst, 0L, count);

            for (i in 0..(count-1)) {
                val oracle = Complex((here.id*count+i) as Double, 0.0);
                if (dst(i) != oracle) {
                    Console.OUT.printf("Team %d place %d received invalid value (%f,%f) at %d instead of (%f,%f)\n",
                                       [team.id(), here.id, dst(i).re, dst(i).im, i, oracle.re, oracle.im]);
                    success = false;
                }
            }
        }

        if (!success) at (res.home) res().set(false);
    }

    public def run(): Boolean {
        Console.OUT.println("Doing scatter for World ("+Place.numPlaces()+" places)");
        val res:Cell[Boolean] = new Cell[Boolean](true);
        val gr:GlobalRef[Cell[Boolean]] = GlobalRef[Cell[Boolean]](res);
        val root = Place(Place.numPlaces()-1); // more thorough test than 0
        finish for (p in Place.places()) {
            at(p) async bcastTest(Team.WORLD, root, gr);
        }

        return res();
    }

    public static def main(args: Rail[String]) {
        new Scatter().execute();
    }
}
