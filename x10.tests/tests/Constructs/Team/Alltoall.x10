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
 * Unit tests for all-to-all functionality of Team
 */
public class Alltoall extends x10Test {

    def allToAllTest(team:Team, res:GlobalRef[Cell[Boolean]]) {
        val count = 113L;
        val sz = count*team.size();
        val src = new Rail[Double](count, (i:long)=>((here.id*count+i) as Double));
        val dst = new Rail[Double](sz, (i:long)=>-(i as Double));
        var success: boolean = true;
        {
            team.alltoall(src, 0L, dst, 0L, count);

            for (i in 0..(count-1)) {
                val oracle = i as Double;
                if (dst(i) != oracle) {
                    Console.OUT.printf("Team %d place %d received invalid value %f at %d instead of %f\n",
                                       team.id(), here.id, dst(i), i, oracle);
                    success = false;
                }
            }
        }

        val reducedSuccess = team.allreduce(success ? 1 : 0, Team.AND);

        team.barrier();

        if (reducedSuccess != 1) {
            Console.OUT.println("Reduced Success value was "+reducedSuccess+" but expected 1");
        }

        success &= (reducedSuccess == 1);

        if (!success) at (res.home) res().set(false);

    }

    public def run(): boolean {
        Console.OUT.println("Doing alltoall for World ("+Place.numPlaces()+" places)");
        val res:Cell[Boolean] = new Cell[Boolean](true);
        val gr:GlobalRef[Cell[Boolean]] = GlobalRef[Cell[Boolean]](res);
        finish for (p in Place.places()) {
            async at(p) allToAllTest(Team.WORLD, gr);
        }

        return res();
    }

    public static def main(args: Rail[String]) {
        new Alltoall().execute();
    }
}
