/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;

/**
 * It is possible to assign to a variable by 
 * doing an at to another place and then returning
 * to the original place.
 */
public class AtAndBack extends x10Test {

    public def run():boolean {
	var x:int = 10;
	val h = here;
	at (here.next()) {
	    at (h) {
                x = 20;
            }
        }
	return x == 20;
    }

    public static def main(Array[String](1)) {
        new AtAndBack().execute();
    }
}
