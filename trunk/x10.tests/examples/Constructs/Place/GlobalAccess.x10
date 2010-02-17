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
 * Testing that an at(b) b.x is legal.
 * @author vj
 */

public class GlobalAccess extends x10Test {
	global val x:GlobalAccess = null;
    public def run(): boolean = {
	     val p = Place.places(1);
	     val f = future (p) {
		     val a = this.x;
		     return true;
	     };
	     return f.force();
	 }

    public static def main(Rail[String]) {
        new GlobalAccess().execute();
    }
}
