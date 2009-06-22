// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_31 extends x10Test {

    class X[T] {}
    
    class C {}
    
    class A(p:int) extends X[C] {
        def this(i:int) = property(i);
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_31().execute();
    }
}
