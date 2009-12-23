// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_39 extends x10Test {

    public def run():boolean {
        val x = Rail.make[boolean](10);
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_39().execute();
    }
}
