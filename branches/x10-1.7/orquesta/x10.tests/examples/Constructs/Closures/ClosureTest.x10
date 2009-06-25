// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test. *

import harness.x10Test;


abstract class ClosureTest extends x10Test {

    var result:boolean = true;

    def check(test:String, actual:Object, expected:Object) = {

        var result:boolean = actual.equals(expected);

        if (!result) {
            pr(test + " fails: expected " + expected + ", got " + actual);
            this.result = false;
        } else
            pr(test + " succeeds: got " + actual);
    }

    /*
    def check(test:String, actual:int, expected:int) = {
        pr(test + " " + expected + " " + actual + " " + (expected==actual));
    }
    */

    def pr(s:String):void = {
        System.out.println(s);
    }

}
