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
 * Not a test. Convenience class to simplify debugging the test
 * cases. Perhaps this should be in x10Test
 *
 * TODO: there's a genericCheck method in x10Test now, but it has 
 * a different parameter order (actual, expected, message).
 * We should rewrite all of the tests that are using these methods 
 * to use x10Test.genericCheck instead... DG: 10/22/2009.
 *
 * @author bdlucas 8/2008
 */

abstract class GenericTest extends x10Test {

    var result:boolean = true;

    def genericCheck(test:String, actual:long, expected:long) = {

        var result:boolean = actual == expected;

        if (!result) {
            pr(test + " fails: expected " + expected + ", got " + actual);
            this.result = false;
        } else
            pr(test + " succeeds: got " + actual);
    }

    def genericCheck(test:String, actual:String, expected:String) = {

        var result:boolean = actual.equals(expected);

        if (!result) {
            pr(test + " fails: expected " + expected + ", got " + actual);
            this.result = false;
        } else
            pr(test + " succeeds: got " + actual);
    }

    def genericCheck(test:String, actual:Any, expected:Any) = {

        var result:boolean = actual.equals(expected);

        if (!result) {
            pr(test + " fails: expected " + expected + ", got " + actual);
            this.result = false;
        } else
            pr(test + " succeeds: got " + actual);
    }

    def pr(s:String):void = {
        x10.io.Console.OUT.println(s);
    }

    //
    // handy for testing
    //

}
