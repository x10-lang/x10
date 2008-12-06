// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_237 extends x10Test {

    const a = long.MAX_VALUE to double;
    const b = long.MIN_VALUE to double;
    const c = 123L to double;

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_237().execute();
    }
}
