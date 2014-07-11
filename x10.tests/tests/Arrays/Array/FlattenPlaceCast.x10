/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;
import x10.regionarray.*;

/**
 * 
 */
public class FlattenPlaceCast extends x10Test {

    val a: DistArray[Test](2);
    val d: DistArray[Place](1);

    public def this() {
        a = DistArray.make[Test](Region.make(1..10, 1..10) -> here, (Point)=>new Test());
        d = DistArray.make[Place](Region.make(1, 10) -> here, (Point)=>here);
    }
   
    static class Test {
    	private val root = GlobalRef[Test](this);
    };

    public def run():boolean  = {
	chk(Place.numPlaces() >= 2, "This test must be run with at least two places");
        val d1next = Place.places().next(d(1));
        val x = a(1,1);
        return !(x instanceof Test{x.root.home == d1next}) && (x instanceof Test{x.root.home == here});
    }

    public static def main(Rail[String]) {
        new FlattenPlaceCast().execute();
    }
    
}

