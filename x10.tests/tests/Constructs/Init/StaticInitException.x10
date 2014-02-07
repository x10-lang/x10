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
 * Check exception semantics of static fields.
 * Based on testcase in XTENLANG-2400.
 * Valid for X10 2.2.3 and later.
 */
public class StaticInitException extends x10Test {
    static x:Long = f();
    static def f() {
        if (Math.sin(42) < 1000) { // should always happen
            throw new Exception("I will try to break things.");
        }
        return 42;
    }
    static y:Long = x;

    public def run():Boolean {
        var ok:Boolean = true;
        try {
            Console.OUT.println("Value is: "+y);
	    Console.OUT.println("FAILURE.  An exception should have been raised on access to x");
            ok = false;
        } catch (e:Exception) {
	    Console.OUT.println("OK.  An exception was raised when y was accessed the first time");
        }

        try {
            Console.OUT.println("Value is: "+x);
	    Console.OUT.println("FAILURE.  An exception should have been raised on access to x");
            ok = false;
        } catch (e:Exception) {
	    Console.OUT.println("OK.  An exception was raised when x was accessed the second time");
        }

        try {
            Console.OUT.println("Value is: "+y);
	    Console.OUT.println("FAILURE.  An exception should have been raised on access to y (take 2)");
            ok = false;
        } catch (e:Exception) {
	    Console.OUT.println("OK.  An exception was raised when y was accessed the second time");
        }

        return ok;
    }

    public static def main (args:Rail[String]) {
        new StaticInitException().execute();
    }
}
