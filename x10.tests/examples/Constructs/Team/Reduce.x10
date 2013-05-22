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
 * Unit tests for reduce functionality of teams
 */
public class Reduce extends x10Test {

    def reduceTest(team:Team, root:Place, res:GlobalRef[Cell[Boolean]]) {
        val count = 123L;
        val src = new Rail[Double](count, (i:Long)=>((here.id+1) as Double) * i * i);
        val dst = new Rail[Double](count, (i:Long)=>-(i as Double));
        var success: Boolean = true;
                
        {
            team.reduce(root, src, 0L, dst, 0L, count, Team.ADD);

            if (here == root) {
                val oracle_base = ((team.size()*team.size() + team.size())/2) as Double;
                for (i in 0..(count-1)) {
                    val oracle:double = oracle_base * i * i;
                    if (dst(i) != oracle) {
                        Console.OUT.printf("Team %d place %d received invalid value %f at %d instead of %f\n",
                                           team.id(), here.id, dst(i), i, oracle);
                        success = false;
                    }
                }
            }
        }

        {
            team.reduce(root, src, 0L, dst, 0L, count, Team.MAX);

            if (here == root) {
                val oracle_base = (team.size()) as Double;
                for (i in 0..(count-1)) {
                    val oracle:double = oracle_base * i * i;
                    if (dst(i) != oracle) {
                        Console.OUT.printf("Team %d place %d received invalid max %f at %d instead of %f\n",
                                           team.id(), here.id, dst(i), i, oracle);
                        success = false;
                    }
                }
            }
        }

        {
            team.reduce(root, src, 0L, dst, 0L, count, Team.MIN);

            if (here == root) {
                val oracle_base = 1.0f;
                for (i in 0..(count-1)) {
                    val oracle:double = oracle_base * i * i;
                    if (dst(i) != oracle) {
                        Console.OUT.printf("Team %d place %d received invalid max %f at %d instead of %f\n",
                                           team.id(), here.id, dst(i), i, oracle);
                        success = false;
                    }
                }
            }
        }

        if (!success) at (res.home) res().set(false);
    }

    public def run(): Boolean {
        Console.OUT.println("Doing reduce for World ("+Place.numPlaces()+" places)");
        val res:Cell[Boolean] = new Cell[Boolean](true);
        val gr:GlobalRef[Cell[Boolean]] = GlobalRef[Cell[Boolean]](res);
        val root = Place(Place.MAX_PLACES-1);
        finish for (p in Place.places()) {
            async at(p) reduceTest(Team.WORLD, root, gr);
        }

        return res();
    }

    public static def main(args: Rail[String]) {
        new Reduce().execute();
    }
}
