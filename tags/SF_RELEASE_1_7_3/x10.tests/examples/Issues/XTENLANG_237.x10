// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_237 extends x10Test {

    const a = long.MAX_VALUE as double;
    const b = long.MIN_VALUE as double;
    const c = 123L as double;

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_237().execute();
    }
}
