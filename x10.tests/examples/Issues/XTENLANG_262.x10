// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_262 extends x10Test {

    public def run(): boolean = {
        val x = 1.0;
        x10.io.Console.OUT.println(x.toString());
        return x.toString().equals("1.0");
    }

    public static def main(Rail[String]) {
        new XTENLANG_262().execute();
    }
}
