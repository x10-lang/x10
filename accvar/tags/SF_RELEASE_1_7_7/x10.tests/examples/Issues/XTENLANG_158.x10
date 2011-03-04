// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_158 extends x10Test {

    val a = int.MAX_VALUE;
    val b = double.MAX_VALUE;

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_158().execute();
    }
}
