// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_117 extends x10Test {

    value class R {}
    
    class Bug {
        val x:Box[R] = null;
        val y = x as R;
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_117().execute();
    }
}
