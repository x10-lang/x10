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

public class CUDATopology {
    public static def main (Rail[String]) {
        for (p in Place.places()) { // iterate over host places
            at (p) {
                val topo = PlaceTopology.getTopology();
                Console.OUT.println("Place: "+p);
                Console.OUT.println("  NumChildren: "+topo.numChildren(p));
                for (c in topo.children(p)) {
                    Console.OUT.println("  Child "+topo.childIndex(c)+": "+c);
                    Console.OUT.println("    Parent: "+c.parent());
                    if (c.isCUDA()) Console.OUT.println("    Is a CUDA place");
                }
                Console.OUT.println();
            }
        }
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab
