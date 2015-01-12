/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_298 extends x10Test {

    
    static class C(p:long) {
    	// The new initialization semantics guarantee that all properties
        // will be initialized before all the fields.
        val q=p; 
        def this(p:long) {
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
