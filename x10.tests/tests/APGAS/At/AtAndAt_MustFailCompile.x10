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

/**
 * It should not be possible to change to an arbitrary
 * place and then assign to a variable defined in the
 * original place.  Must be statically rejected by the compiler.
 */
public class AtAndAt_MustFailCompile extends x10Test {

    public def run():boolean {
	var x:long = 10;
	at (Place.places().next(here)) {
	    at (Place.places().next(here)) {
                x = 20; // ERR: Local variable "x" is accessed at a different place, and must be declared final.
            }
        }
	return x == 20;
    }

    public static def main(Rail[String]) {
        new AtAndAt_MustFailCompile().execute();
    }
}
