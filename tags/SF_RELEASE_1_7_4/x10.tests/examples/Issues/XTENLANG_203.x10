// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

import x10.io.Console;

class XTENLANG_203 extends x10Test {

    
    def foo() {throw new Error();}

    public def run(): boolean {

        var a:boolean = true;
        var b:boolean = false;
        var c:boolean = false;
        var d:boolean = false;

        try {
            try {
                foo();
                a = false;
            } finally {
                b = true;
            }
        } catch (Throwable) {
            c = true;
        } finally {
            d = true;
        }

        return a && b && c && d;
    }

    public static def main(Rail[String]) {
        new XTENLANG_203().execute();
    }
}
