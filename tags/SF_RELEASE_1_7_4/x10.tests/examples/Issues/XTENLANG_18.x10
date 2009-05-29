// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_18 extends x10Test {

    public class C implements (int)=>int {
        public def apply(i:int) = 0;
    }

    class P {}
    
    class I implements (P)=>int {
        incomplete public def apply(P): int;
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_18().execute();
    }
}
