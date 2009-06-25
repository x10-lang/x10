// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_177 extends x10Test {

    val x = 1.0;
    val y = "1";
    val s = String.format("%f %s", [x, y]);

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_177().execute();
    }
}
