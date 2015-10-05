/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2014-2015.
 */
import harness.x10Test;

import x10.util.Team;

/**
 * Benchmarks performance of Team.allreduce for varying data size
 */
public class ResilientAllreduceTest extends x10Test {
    private static SIZE = 2<<19;
    private val victim:Long;

    public def this(victimPlace:Long){
        this.victim = victimPlace;
    }
    
	public def run(): Boolean {
        try{
            finish for (place in Place.places()) at (place) async {
            
                if (here.id == victim){
                    async System.killHere();
                }
                val src = new Rail[Double](SIZE, (i:Long) => i as Double);
                val dst = new Rail[Double](SIZE);
                val start = System.nanoTime();
            
                Team.WORLD.allreduce(src, 0, dst, 0, SIZE, Team.ADD);     
             }
        } catch(dpe:DeadPlaceException){
            Console.OUT.println("All reduce failed with a dead place exception ...");
        } catch (me:MultipleExceptions) {
            val dper = me.getExceptionsOfType[DeadPlaceException]();
            if (dper.size > 0)
                Console.OUT.println("All reduce failed with a dead place exception ...");
        }
        return true;
	}

	public static def main(var args: Rail[String]): void {
        var victimPlace:Long = 1;
        if (args.size > 0) {
            victimPlace = Long.parse(args(0));
        }
		new ResilientAllreduceTest(victimPlace).execute();
	}
}

/*
x10c++ -sourcepath ../x10lib/ ResilientAllreduceTest.x10 -o ResilientAllreduceTest.sock
X10_TEAM_DEBUG_INTERNALS=1 X10_RESILIENT_MODE=1 X10_NPLACES=6 ./ResilientAllreduceTest.sock 2

=> sometimes the program hangs

Place(1):team0 entered AllReduce phase=0, root=Place(0)
Place(1) getLinks called with myIndex=1 parent=-1 startIndex=0, endIndex=5
Place(1) getLinks called with myIndex=1 parent=0 startIndex=1, endIndex=2
Place(1):team0, root=Place(0) has parent Place(0)
Place(1):team0, root=Place(0) has children Place(2), Place(-1)
Place(1):team0 waiting for children phase 3
Place(2):team0 entered AllReduce phase=0, root=Place(0)
Place(3):team0 entered AllReduce phase=0, root=Place(0)
Place(3) getLinks called with myIndex=3 parent=-1 startIndex=0, endIndex=5
Place(3) getLinks called with myIndex=3 parent=0 startIndex=3, endIndex=5
Place(3):team0, root=Place(0) has parent Place(0)
Place(3):team0, root=Place(0) has children Place(4), Place(5)
Place(3):team0 waiting for children phase 2
Place(2) getLinks called with myIndex=2 parent=-1 startIndex=0, endIndex=5
Place(2) getLinks called with myIndex=2 parent=0 startIndex=1, endIndex=2
Place(2) getLinks called with myIndex=2 parent=1 startIndex=2, endIndex=2
Place(2):team0, root=Place(0) has parent Place(1)
Place(2):team0, root=Place(0) has children Place(-1), Place(-1)
Place(2):team0 waiting for children phase 4
Place(2):team0 released by children phase 4
Place(2) moving data to parent
Place(2) waiting for parent Place(1):team0 to release us from phase 4
Place(1) parent ready to receive phase 3
Place(4):team0 entered AllReduce phase=0, root=Place(0)
Place(4) getLinks called with myIndex=4 parent=-1 startIndex=0, endIndex=5
Place(4) getLinks called with myIndex=4 parent=0 startIndex=3, endIndex=5
Place(4) getLinks called with myIndex=4 parent=3 startIndex=4, endIndex=4
Place(4):team0, root=Place(0) has parent Place(3)
Place(4):team0, root=Place(0) has children Place(-1), Place(-1)
Place(4):team0 waiting for children phase 4
Place(4):team0 released by children phase 4
Place(4) moving data to parent
Place(4) waiting for parent Place(3):team0 to release us from phase 4

Place 2 exited unexpectedly with exit code: 1

Place(5):team0 entered AllReduce phase=0, root=Place(0)
Place(5) getLinks called with myIndex=5 parent=-1 startIndex=0, endIndex=5
Place(5) getLinks called with myIndex=5 parent=0 startIndex=3, endIndex=5
Place(5) getLinks called with myIndex=5 parent=3 startIndex=5, endIndex=5
Place(5):team0, root=Place(0) has parent Place(3)
Place(5):team0, root=Place(0) has children Place(-1), Place(-1)
Place(5):team0 waiting for children phase 4
Place(5):team0 released by children phase 4
Place(5) moving data to parent
Place(5) waiting for parent Place(3):team0 to release us from phase 4
Place(3) parent ready to receive phase 2
Place(3) parent ready to receive phase 2
Place(0):team0 entered AllReduce phase=0, root=Place(0)
Place(0) getLinks called with myIndex=0 parent=-1 startIndex=0, endIndex=5
Place(0):team0, root=Place(0) has parent Place(-1)
Place(0):team0, root=Place(0) has children Place(1), Place(3)
Place(0):team0 waiting for children phase 2
Place(3):team0 released by children phase 4
Place(3) moving data to parent
Place(3) waiting for parent Place(0):team0 to release us from phase 4
Place(0) parent ready to receive phase 2

==> hang

*/