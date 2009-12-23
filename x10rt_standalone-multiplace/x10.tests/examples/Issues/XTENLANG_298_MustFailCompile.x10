// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_298_MustFailCompile extends x10Test {

    
    static class C(p:int) {
    	// The field declaration must fail compilation because p will not be
    	// initialized at this access.
        val q=p; 
        def this(p:int) {
        	property(p);
        }
    }

    public def run(): boolean {
        val c = new C(1);
        x10.io.Console.OUT.println("c.p " + c.p);
        x10.io.Console.OUT.println("c.q " + c.q);
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_298().execute();
    }
}
