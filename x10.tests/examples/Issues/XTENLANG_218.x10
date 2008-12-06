// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_218 extends x10Test {

    static def check(a:Object, b:Object) = a==b;
    
    public def run():boolean {
        return check(1,1)==true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_218().execute();
    }
}
