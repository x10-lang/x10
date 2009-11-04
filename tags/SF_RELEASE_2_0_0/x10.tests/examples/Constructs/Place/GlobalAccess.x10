// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

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
