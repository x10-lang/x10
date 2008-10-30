// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_113 extends x10Test {

    class R {
        def size() = 100000;
    }
    
    public def run():boolean {
        val a:R = new R();
        val b:R = new R();
        return a.size()==b.size();
    }

    public static def main(Rail[String]) {
        new XTENLANG_113().execute();
    }
}
