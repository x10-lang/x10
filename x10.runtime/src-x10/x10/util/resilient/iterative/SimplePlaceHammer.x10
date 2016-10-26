/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2016.
 *  (C) Copyright Sara Salem Hamouda 2014-2016.
 */

package x10.util.resilient.iterative;

import x10.util.HashMap;

/**
 * A testing utility to programatically kill places at specific
 * time steps in a program that uses one of the iterative frameworks.
 */
public class SimplePlaceHammer {
    val map:HashMap[Long,Long] = new HashMap[Long,Long]();

    public def this() {
        this(System.getenv("KILL_STEPS"), System.getenv("KILL_PLACES"));
    }

    public def this(steps:String, places:String) {
        var sRail:Rail[Long] = new Rail[Long](0);
        var pRail:Rail[Long] = new Rail[Long](0);
        if (steps != null) {
            val tmp = steps.split(",");
            sRail = new Rail[Long](tmp.size, (i:Long) => { Long.parseLong(tmp(i)) });
        }
        if (places != null) {
            val tmp = places.split(",");
            pRail = new Rail[Long](tmp.size, (i:Long) => { Long.parseLong(tmp(i)) });
        }
        val min = Math.min(sRail.size, pRail.size);
        for (i in 0..(min-1)) {
            map.put(sRail(i), pRail(i));
        }
    }

    public def printPlan() {
        Console.OUT.print("Hammer plan: ");
        for (e in map.entries()) {
            Console.OUT.print("<"+e.getKey()+", "+e.getValue()+">");
        }
        Console.OUT.println();
    }
    
    public def sayGoodBye(curStep:Long):Boolean {
        val placeToKill = map.getOrElse(curStep,-1);
        if (placeToKill == here.id) {
            return true;
        } else {
            return false;
        }
    }
    
    public def getVictimPlaceId(curStep:Long):Long {
        return map.getOrElse(curStep,-1);
    }
}