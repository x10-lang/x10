// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_198 extends x10Test {

    static class A {
        def foo() = "A";
        val x:String;
        def this() {
            x = foo();
        }
    }
    
    static class B extends A {
        def foo() = "B";
    }
    
    public def run():boolean {
        System.out.println("new B().x: " + (new B().x));
        return new B().x.equals("B");
    }

    public static def main(Rail[String]) {
        new XTENLANG_198().execute();
    }
}
