// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class A {
    val name1 = className();
    def name2() = className();
}
    
class B extends A {}
    
class XTENLANG_242 extends x10Test {

    public def run():boolean {
        return new B().name1.equals("B") && new B().name2().equals("B");
    }

    public static def main(Rail[String]) {
        new XTENLANG_242().execute();
    }
}
