// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_243 extends x10Test {

    val s = "hi";
    val b = s.substring(0,1).equals("h");

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_243().execute();
    }
}
