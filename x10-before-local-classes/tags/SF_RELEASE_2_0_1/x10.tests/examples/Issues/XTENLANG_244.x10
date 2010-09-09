// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_244 extends x10Test {

    static def check(a:Object, b:Object) = a==b;
    
    public def run():boolean {
        return check(1 as Box[Int],1 as Box[Int])==false;
    }

    public static def main(Rail[String]) {
        new XTENLANG_244().execute();
    }
}
