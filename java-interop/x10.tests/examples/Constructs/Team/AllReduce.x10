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

import harness.x10Test;
import x10.util.Team;

/**
 * Unit tests for all reduce functionality of teams
 */
public class AllReduce extends x10Test {

    def allReduceTest(team:Team, role:int, res:GlobalRef[Cell[Boolean]]) {
        val count = 113;        
        val src = new Array[Double](count, (i:int)=>((role+1) as Double) * i * i);
        val dst = new Array[Double](count, (i:int)=>-(i as Double));
        var success: boolean = true;
                
        {
            team.allreduce(role, src, 0, dst, 0, count, Team.ADD);

            val oracle_base = ((team.size()*team.size() + team.size())/2) as Double;
            for ([i] in 0..(count-1)) {
                val oracle:double = oracle_base * i * i;
                if (dst(i) != oracle) {
                    Console.OUT.printf("Team %d role %d received invalid sum %f at %d instead of %f\n",
                                       team.id(), role, dst(i), i, oracle);
                    success = false;
                }
            }
        }

        {
            team.allreduce(role, src, 0, dst, 0, count, Team.MAX);

            val oracle_base = (team.size()) as Double;
            for ([i] in 0..(count-1)) {
                val oracle:double = oracle_base * i * i;
                if (dst(i) != oracle) {
                    Console.OUT.printf("Team %d role %d received invalid max %f at %d instead of %f\n",
                                       team.id(), role, dst(i), i, oracle);
                    success = false;
                }
            }
        }

        {
            team.allreduce(role, src, 0, dst, 0, count, Team.MIN);

            val oracle_base = 1.0f;
            for ([i] in 0..(count-1)) {
                val oracle:double = oracle_base * i * i;
                if (dst(i) != oracle) {
                    Console.OUT.printf("Team %d role %d received invalid max %f at %d instead of %f\n",
                                       team.id(), role, dst(i), i, oracle);
                    success = false;
                }
            }
        }

        val reducedSuccess = team.allreduce(role, success ? 1 : 0, Team.AND);

        team.barrier(role);

        if (reducedSuccess != 1) {
            Console.OUT.println("Reduced Success value was "+reducedSuccess+" but expected 1");
        }

        success &= (reducedSuccess == 1);

        if (!success) at (res.home) res().set(false);

    }

    public def run(): boolean {
        Console.OUT.println("Doing all reduce for World ("+Place.numPlaces()+" places)");
        val res:Cell[Boolean] = new Cell[Boolean](true);
        val gr:GlobalRef[Cell[Boolean]] = GlobalRef[Cell[Boolean]](res);
        finish for (p in Place.places()) {
            async at(p) allReduceTest(Team.WORLD, here.id, gr);
        }

        return res();
    }

    public static def main(args: Array[String]) {
        new AllReduce().execute();
    }

}
