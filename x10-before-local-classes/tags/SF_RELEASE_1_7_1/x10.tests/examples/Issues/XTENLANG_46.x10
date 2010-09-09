// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_46 extends x10Test {

    value class X {
        val x = 1;
    }
    
    public def run():boolean {
        val x1 = new X();
        val x2 = new X();
        return x1==x2;
    }

    public static def main(Rail[String]) {
        new XTENLANG_46().execute();
    }
}
