// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_132 extends x10Test {

    static class A(p:int) {
        def this(p:int) = property(p);
    }
    
    static class Bug {
    
        static type A(p:int) = A{self.p==p};
    
        public static def run():boolean {
    
            val a = new A(0);
    
            try {
                val a1 = a as A(1);
                x10.io.Console.OUT.println("did not get exception");
                return false;
            } catch (e:ClassCastException) {
                x10.io.Console.OUT.println("got exception");
                return true;
            }
        }
    }

    public def run(): boolean {
        return Bug.run();
    }

    public static def main(Rail[String]) {
        new XTENLANG_132().execute();
    }
}
