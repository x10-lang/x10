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

import x10.regionarray.Dist;
import x10.regionarray.DistArray;
import x10.util.Pair;

/**
 * Simple tests for Rail.uncountedCopy functionality
 * with a sampling of element types, including
 * a struct of structs.
 */
public class RailUncountedCopy extends x10Test {
    public static def doTest[T](n:long, init:(long)=>T){T haszero}:boolean {
        val start = new Rail[T](n, init);
        val remote = at (Place.places().next(here)) new GlobalRail[T](new Rail[T](n));
        val end = new Rail[T](n);
        val completed = DistArray.make[Boolean](Dist.makeUnique());
        var fail:boolean = false;

        finish {
            val markCompleted = () => {atomic completed(here.id) = true;};
            Rail.uncountedCopy[T](start, 0, remote, 0, n, markCompleted);
            Rail.uncountedCopy[T](remote, 0, end, 0, n, markCompleted);
        }

        // wait for copies to complete
        finish {
            at (remote.home()) async {
                when(completed(here.id));
            }
            when(completed(here.id));
        }
        
        for (i in start.range()) {
            if (start(i) != end(i)) {
                Console.OUT.println("Expected to find "+start(i)+" at "+i+" but found "+end(i));
                fail = true;
            }
        }
        return fail;
    }

    public def run() {
        var fail:boolean = false;
        fail |= doTest[long](101, (i:long)=>10*i+1);
        fail |= doTest[byte](101, (i:long)=>((10*i+1) as byte));
        fail |= doTest[Complex](101, (i:long)=>Complex(10*i, i+1));
        fail |= doTest[Pair[Complex,double]](101, (i:long)=>Pair[Complex,double](Complex(10*i, -3*i), 1000d*i));
        fail |= doTest[Pair[Complex,byte]](101, (i:long)=>Pair[Complex,byte](Complex(10*i, -3*i), i as byte));
        // test copy of 0 elements
        fail |= doTest[long](0, (i:long)=>10*i+1);
       
       return !fail;    
    }

    public static def main(var args: Rail[String]) {
        new RailUncountedCopy().execute();
    }

}
