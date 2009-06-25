// (C) Copyright IBM Corporation 2009
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author igor 2/2009
 */

class XTENLANG_311 extends x10Test {

    public def run(): boolean {
        var t:int = 0;
        t++;
        return t==1;
    }

    public static def main(Rail[String]) {
        new XTENLANG_311().execute();
    }
}
