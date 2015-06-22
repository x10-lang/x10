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

public class AsyncInit extends x10Test {
    static def testInt(v:Int) {
	val x:Int;
	finish async x = v;
	return x;
    }
    static def test[T](v:T) {
        val x:T;
        finish async x = v;
        return x;
    }
    public def run(): Boolean {
    	Console.OUT.println(testInt(2n));
        Console.OUT.println(test(2));
        return true;
    }
    public static def main(Rail[String]) {
        new AsyncInit().execute();
    }
}
