// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import x10.util.Box;
import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_210 extends x10Test {

    var os:Rail[Object]! = Rail.make[Object](10);
    
    def set(i0:int, vue:double): void = {
        os(i0) = vue as Box[double];
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_210().execute();
    }
}
