// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_193 extends x10Test {

    public static final class Bug[T]  {
        private const x = ValRail.make[int](0, (nat)=>0);
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_193().execute();
    }
}
