// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_258 extends x10Test {

    public def run():boolean {
        val v: Rail[int] = Rail.makeVar[int](1);
        v(0) += 1;
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_258().execute();
    }
}
