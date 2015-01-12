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
import x10.regionarray.*;

/**
 * Test that coercions and overloading behave.
 * A non-coerced call should be more specific than a coerced call. 
 *
 * @author nystrom 9/2008
 */
public class Overloading extends x10Test {
        public def f(r: Region) = "region";
        public def f(d: Dist) = "dist";

        public def g(d: Dist) = "dist";
        public def g(r: Region) = "region";

	public def run(): boolean = {
                r: Region = Region.make(0,1);
                d: Dist = r->here;

                val r1 = f(r);
                val r2 = f(d);
                val r3 = g(r);
                val r4 = g(d);

                return r1.equals("region")
                    && r2.equals("dist")
                    && r3.equals("region")
                    && r4.equals("dist");
	}

	public static def main(var args: Rail[String]): void = {
		new Overloading().execute();
	}
}

