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

import x10.util.ArrayList;
import x10.util.HashMap;
import x10.util.Pair;
import x10.compiler.Uncounted;

/**
 * A testing utility to programatically kill places at specific
 * time steps or times after starting in a program that uses
 * one of the iterative frameworks.
 */
public class SimplePlaceHammer {
    val stepMap:HashMap[Long,Long] = new HashMap[Long,Long]();
    val timers:ArrayList[Pair[Long,Long]] = new ArrayList[Pair[Long,Long]]();

    public def this() {
        this(System.getenv("KILL_STEPS"), System.getenv("KILL_TIMES"), System.getenv("KILL_PLACES"));
    }

    public def this(steps:String, times:String, places:String) {
        if (steps != null && times != null) {
            throw new IllegalArgumentException("Limitation: can't set both KILL_STEPS and KILL_TIMES");
        }
        if (places != null) {
            val tmp = places.split(",");
            val pRail = new Rail[Long](tmp.size, (i:Long) => { Long.parseLong(tmp(i)) });
            if (steps != null) {
                val tmp2 = steps.split(",");
                val sRail = new Rail[Long](tmp2.size, (i:Long) => { Long.parseLong(tmp2(i)) });
                val min = Math.min(sRail.size, pRail.size);
                for (i in 0..(min-1)) {
                    stepMap.put(sRail(i), pRail(i));
                }
            }
            if (times != null) {
                val tmp2 = times.split(",");
                val tRail = new Rail[Long](tmp2.size, (i:Long) => { Long.parseLong(tmp2(i)) });
                val min = Math.min(tRail.size, pRail.size);
                for (i in 0..(min-1)) {
                    timers.add(Pair(tRail(i), pRail(i)));
                }
            }
        }
    }

    public def printPlan() {
        Console.OUT.print("Hammer step plan: ");
        for (e in stepMap.entries()) {
            Console.OUT.print("<"+e.getKey()+", "+e.getValue()+"> ");
        }
        Console.OUT.print("\nHammer timer plan: ");
        for (p in timers) {
            Console.OUT.print("<"+p.first+", "+p.second+"> ");
        }
        Console.OUT.println();
    }
    
    public def sayGoodBye(curStep:Long):Boolean {
        val placeToKill = stepMap.getOrElse(curStep,-1);
        if (placeToKill == here.id) {
            return true;
        } else {
            return false;
        }
    }
    
    public def getVictimPlaceId(curStep:Long):Long {
        return stepMap.getOrElse(curStep,-1);
    }

    public def scheduleTimers() {
        for (p in timers) {
            val killId = p.second;
            val killTime = p.first;
            if (Place.places().contains(killId)) {
                at (Place(killId)) @Uncounted async {
                    Console.OUT.println("Hammer kill timer at "+here+" starting sleep for "+killTime+" secs");
                    val deadline = System.currentTimeMillis() + 1000 * killTime;
                    while (deadline > System.currentTimeMillis()) {
                        val sleepTime = deadline - System.currentTimeMillis();
                        System.sleep(sleepTime);
                    }
                    Console.OUT.println("Hammer calling killHere at "+System.currentTimeMillis());
                    System.killHere();
                }
            }
        }
    }
}
