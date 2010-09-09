// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_210 extends x10Test {

    var os: Rail[Object] = Rail.makeVar[Object](10);
    
    def set(i0: int, vue: double): void = {
        os(i0) = vue;
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_210().execute();
    }
}
