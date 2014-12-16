/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright Australian National University 2013.
 */

import harness.x10Test;
import x10.util.Team;

/**
 * Unit tests for broadcast functionality of teams
 */
public class Bcast extends x10Test {

    def bcastTest(team:Team, root:Place, res:GlobalRef[Cell[Boolean]]) {
        val count = 113L;
        var success: Boolean = true;
                
        {
	    val src = new Rail[Double](count, (i:Long)=>((here.id+1) as Double) * i);
            val dst = new Rail[Double](count, (i:Long)=>-(i as Double));
            team.bcast(root, src, 0L, dst, 0L, count);

            for (i in 0..(count-1)) {
                val oracle:Double = (root.id()+1) * i;
                if (dst(i) != oracle) {
                    Console.OUT.printf("Team %d place %d received invalid value %f at %d instead of %f\n",
                                       team.id(), here.id, dst(i), i, oracle);
                    success = false;
                }
            }
        }

        {
	    val src = new Rail[Complex](count, (i:Long)=>(Complex(here.id+1, 0) * i));
            val dst = new Rail[Complex](count, (i:Long)=>Complex(-i, 0));
            team.bcast(root, src, 0L, dst, 0L, count);

            for (i in 0..(count-1)) {
                val oracle:Complex = Complex(root.id()+1,0) * i;
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
        Console.OUT.println("Doing broadcast for World ("+Place.numPlaces()+" places)");
        val res:Cell[Boolean] = new Cell[Boolean](true);
        val gr:GlobalRef[Cell[Boolean]] = GlobalRef[Cell[Boolean]](res);
        val root = Place(Place.numPlaces()-1); // more thorough test than 0
        finish for (p in Place.places()) {
            at(p) async bcastTest(Team.WORLD, root, gr);
        }

        return res();
    }

    public static def main(args: Rail[String]) {
        new Bcast().execute();
    }
}
