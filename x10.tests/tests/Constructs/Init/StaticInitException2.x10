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

/**
 * Check exception semantics of static fields.
 * Valid for X10 2.2.3 and later.
 */
public class StaticInitException2 extends x10Test {

    // Should raise an exception at Place 0, but be fine at all other places
    static val x = 100 / here.id;

    public def run():Boolean {
        val ok = new Cell[Boolean](true);
        val result = GlobalRef[Cell[Boolean]](ok);

	for (0..2) {
          try {
            finish for (p in Place.places()) {
              async at (p) { 
                Console.OUT.println(here+" ===> "+x);
                if (here.id == 0L) {
	            Console.OUT.println("Did not get expected exception at Place(0)");
                    at (result) result()() = false;
                }
              }
            }
          } catch (e:MultipleExceptions) {
            if (e.exceptions().size != 1L) {
	        Console.OUT.println("Got unexpected number of exceptions ("+e.exceptions().size+")");
                ok() = false;
            }
          }
        }

        return ok();
    }

    public static def main (args:Rail[String]) {
        new StaticInitException2().execute();
    }
}
