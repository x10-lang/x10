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
 * Unit tests for team creation
 */
public class NewTeam extends x10Test {
    public def run(): boolean {
        testAllPlaces();
        testSparsePlaceGroup();
        return true;
    }

    public def testAllPlaces() {
        val myTeam = new Team(Place.places());
        finish for (p in Place.places()) at(p) async {
            myTeam.barrier();
        }
    }

    public def testSparsePlaceGroup() {
        val places = new SparsePlaceGroup(new Rail[Place](Place.numPlaces()-1, (i:Long)=>Place(i)));
        Console.OUT.println("places " + places);
        val myTeam = new Team(places);
        finish for (p in places) at(p) async {
            myTeam.barrier();
        }
    }

    public static def main(args: Rail[String]) {
        new NewTeam().execute();
    }
}
