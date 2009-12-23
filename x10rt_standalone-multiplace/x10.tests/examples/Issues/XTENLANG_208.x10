// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

public class XTENLANG_208 extends x10Test {

    public operator this + (that: XTENLANG_208) = this;
    
    def foo() {
        val sum = this + this;
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_208().execute();
    }
}
