/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011.
 */

import harness.x10Test;

/**
 * Test overriding of methods with UShort parameters.
 *
 * @author Salikh Zakirov 5/2011
 */
public class UShortOverride3 extends x10Test {

    static class H[T] {
	def f(x:T) {
	    return x.typeName();
	}
    }

    static class HI extends H[Short] {}
    static class HU extends H[UShort] {}

    public def run() : Boolean {
	val hi = new H[Short]();
	val hu = new H[UShort]();
	val hi2 = new HI();
	val hu2 = new HU();
	assert hi.f(1s).equals("x10.lang.Short");
	assert hu.f(1us).equals("x10.lang.UShort");
	assert hi2.f(1s).equals("x10.lang.Short");
	assert hu2.f(1us).equals("x10.lang.UShort");
	return true;
    }

    public static def main(Rail[String]) {
        new UShortOverride3().execute();
    }
}
