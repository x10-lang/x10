// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_19 extends x10Test {

    class P {}
    class X {}
    
    class Init implements Indexable[P,X] {
        incomplete public def apply(P): X;
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_19().execute();
    }
}
