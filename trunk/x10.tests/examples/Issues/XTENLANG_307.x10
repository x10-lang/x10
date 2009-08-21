// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_307 extends x10Test {

    val r = Rail.makeVar[char](1);

    public def run(): boolean {
        r(0) = Char.chr('a'.ord()+1);
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_307().execute();
    }
}
