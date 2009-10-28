// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * Not a test. Convenience class to simplify debugging the test
 * cases. Perhaps this should be in x10Test
 *
 * @author bdlucas 8/2008
 */

abstract class ClosureTest extends x10Test {

    var result:boolean = true;

    // XTENLANG-335: dup for Int and String because cannot assign to Value
    def check(test:String, actual:Int, expected:Int) = {
        var result:boolean = actual == expected;

        if (!result) {
            pr(test + " fails: expected " + expected + ", got " + actual);
            this.result = false;
        } else
            pr(test + " succeeds: got " + actual);
    }

    // XTENLANG-335: dup for Int and String because cannot assign to Value
    def check(test:String, actual:String, expected:String) = {
        var result:boolean = actual == expected;

        if (!result) {
            pr(test + " fails: expected " + expected + ", got " + actual);
            this.result = false;
        } else
            pr(test + " succeeds: got " + actual);
    }

    def check(test:String, actual:Object!, expected:Object!) = {
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
