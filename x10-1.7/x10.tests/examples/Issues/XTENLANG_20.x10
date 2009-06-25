// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_20 extends x10Test {

    class X {}
    
    class I implements (X)=>int {
        incomplete public def apply(X): int;
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_20().execute();
    }
}
