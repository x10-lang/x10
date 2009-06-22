// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_195 extends x10Test {


    var x:Value = 3;

    static def foo(v:Value) {}
    
    public def run():boolean {
        foo(3);
        var v:Value;
        v = 3;
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_195().execute();
    }
}
