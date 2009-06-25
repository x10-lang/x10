// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_219 extends x10Test {

    public def run():boolean {
        val x = true;
        return x.toString().equals("true");
    }

    public static def main(Rail[String]) {
        new XTENLANG_219().execute();
    }
}
