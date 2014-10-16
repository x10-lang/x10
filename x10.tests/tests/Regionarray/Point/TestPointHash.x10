/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 *  (C) Copyright Australian National University 2009-2010.
 */

import harness.x10Test;
import x10.regionarray.*;

/**
 * Tests Point equals and hashCode operations.
 * @author milthorpe 04/2010
 */
public class TestPointHash extends x10Test {

	public def run(): Boolean = {
        val b = Point.make(1,1,1);
        val c = Point.make(1,1,1);
        chk (b.equals(c));
        chk (b.hashCode() == c.hashCode());

        return true;
	}

	public static def main(Rail[String]) {
		new TestPointHash().execute();
	}
}
