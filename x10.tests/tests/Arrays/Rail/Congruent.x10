/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;

// SKIP_MANAGED_X10: Congruent memory not supported by Managed X10

public class Congruent extends x10Test { 
    val probsize:long;

    public def this(ps:long) {
        probsize = ps;
    }

    public def run () {
        val allocator = Runtime.MemoryAllocator.requestAllocator(false, true);
        val elements = (probsize * 1024/8) as long;
        val plh = PlaceLocalHandle.make[Rail[Long]](Place.places(), ()=>new Rail[Long](elements, allocator));
        Console.OUT.println("Construction complete.");
        val str0 = plh().toString();
        for (p in Place.places()) {
            val str = at (p) plh().toString();
            if (!str.equals(str0)) {
                Console.ERR.println("Rails were not congruent at "+here+":");
                Console.ERR.println(str);
                Console.ERR.println(str0);
                return false;
            }
        }
        Console.OUT.println("Verified congruence.");

        // do some remote ops
        finish for (p in Place.places()) async at (p) {
            val rail = plh() as Rail[Long]{self!=null};
	    val gr = Unsafe.getCongruentSibling(rail, Place.places().next(p));
            for (i in 0L..(elements-1)) {
                val oracle = Math.sqrt(i as Double) as Long;
                GlobalRail.remoteAdd(gr, i, oracle);
            }
        }
        Console.OUT.println("Remote ops complete.");

        // verify
        val errs = new Cell[Long](0);
        finish for (p in Place.places()) async at (p) {
            var errors:Long = 0;
            val rail = plh();
            for (i in 0L..(elements-1)) {
                val oracle = Math.sqrt(i as Double) as Long;
                if (rail(i) != oracle) {
                    Console.ERR.println(here+": rail("+i+")=="+rail(i)+" (should be "+oracle+")");
                    errors++;
                }
            }
            val errors_ = errors;
            at (Place.FIRST_PLACE) atomic errs() += errors_;
        }
        if (errs()>0) {
            Console.ERR.println(errs()+" errors.");
            return false;
        } else {
            Console.OUT.println("Verification complete.");
        }
        return true;
    }
    public static def main(args:Rail[String]) {
        var kBytes:long = 4;
        if (args.size>0) {
            kBytes = Long.parseLong(args(0));
        } 
        new Congruent(kBytes*1024/(Place.numPlaces() as int)).execute();
    }
}
