/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright Australian National University 2010.
 */
import harness.x10Test;

/**
 * Tests a distributed clocked finish
 *
 * @author milthorpe 09/2010
 */
public class TestClockedFinish extends x10Test {
    public def run():Boolean {
        clocked finish {
            for (p1 in Place.places()) clocked async at(p1) {
                Console.OUT.println("starting at " + here.id);
                Clock.advanceAll();
                Console.OUT.println("finished at " + here.id);
            }
        }
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new TestClockedFinish().execute();
    }
}
