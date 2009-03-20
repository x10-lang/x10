// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_133 extends x10Test {

    public def run():boolean {
        val d1 = Dist.makeConstant([0..2, 0..3]);
        val d2 = Dist.makeConstant(-1..-2, here);
        d1.equals(d2);
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_133().execute();
    }
}
