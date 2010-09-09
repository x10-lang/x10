// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author igor 5/2009
 */

class XTENLANG_335 extends x10Test {

    public def run():boolean {
        val x:Value = "";
        return (x as String) == "";
    }

    public static def main(Rail[String]) {
        new XTENLANG_335().execute();
    }
}
