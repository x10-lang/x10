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
 * Unit tests for indexOfMin and indexOfMax functionality of teams
 */
public class IndexOf extends x10Test {

    def indexTest(team:Team, root:Place, res:GlobalRef[Cell[Boolean]]) {
        var success: Boolean = true;
        val myVal = (here.id + 100) as Double;
        val myIndex = here.id as int;

        {
            val maxIndex = team.indexOfMax(myVal, myIndex);
            val oracle = (Place.numPlaces() - 1) as Int;
            if (maxIndex != oracle) {
                Console.OUT.printf("Team %d place %d received invalid maxIndex %d instead of %d\n",
                                   team.id(), here.id, maxIndex, oracle);
                success = false;
            }
        }

        {
            val minIndex = team.indexOfMin(myVal, myIndex);
            val oracle = 0n;
            if (minIndex != oracle) {
                Console.OUT.printf("Team %d place %d received invalid minIndex %d instead of %d\n",
                                   team.id(), here.id, minIndex, oracle);
                success = false;
            }
        }

        if (!success) at (res.home) res().set(false);
    }

    public def run(): Boolean {
        Console.OUT.println("Doing IndexOf for World ("+Place.numPlaces()+" places)");
        val res:Cell[Boolean] = new Cell[Boolean](true);
        val gr:GlobalRef[Cell[Boolean]] = GlobalRef[Cell[Boolean]](res);
        val root = Place(Place.numPlaces()-1);
        finish for (p in Place.places()) {
            at(p) async indexTest(Team.WORLD, root, gr);
        }

        return res();
    }

    public static def main(args: Rail[String]) {
        new IndexOf().execute();
    }
}
