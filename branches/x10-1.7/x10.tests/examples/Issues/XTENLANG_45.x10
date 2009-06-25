// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_45 extends x10Test {

    public def run():boolean {
        var s:String = "";
        s += 1;
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_45().execute();
    }
}
