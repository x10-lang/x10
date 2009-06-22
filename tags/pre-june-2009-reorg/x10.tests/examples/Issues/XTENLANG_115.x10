// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_115 extends x10Test {

    class R {
        def size() = 0;
    }
    
    public def run():boolean {
        val a = new R();
        val z = 0;
        return a.size()==z;
    }

    public static def main(Rail[String]) {
        new XTENLANG_115().execute();
    }
}
