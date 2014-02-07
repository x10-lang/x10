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

import java.lang.management.ManagementFactory;

/**
 * The classic hello world program, with a twist - prints hello from every Place.
 * Uses Java interop to get hostname and pid.
 */
public class HelloWholeJavaWorld {
    public static def main(Rail[String]):void {
        finish for (p in Place.places()) {
            at (p) async {
                val vmName = ManagementFactory.getRuntimeMXBean().getName(); // pid@hostname
                val split = vmName.split("@");
                val pid = Long.parse(split(0));
                val hostName = split(1);
                Console.OUT.println("Hello World from place "+ p.id + " [host:" + hostName + ",pid:" + pid + "]");
            }
        }
    }
}


