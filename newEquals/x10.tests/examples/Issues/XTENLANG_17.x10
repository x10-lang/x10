// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_17 extends x10Test {

    class X {}
    
    var min: Rail[Rail[X]] = Rail.make[Rail[X]](3);

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_17().execute();
    }
}
