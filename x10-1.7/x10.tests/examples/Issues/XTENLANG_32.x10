// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_32 extends x10Test {

    def foo(x: (nat)=>int) = x(0);
    
    public def run():boolean {
        val r = Rail.makeVal[int](2); // same problem with literal [1.2]
        foo(r);
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_32().execute();
    }
}
