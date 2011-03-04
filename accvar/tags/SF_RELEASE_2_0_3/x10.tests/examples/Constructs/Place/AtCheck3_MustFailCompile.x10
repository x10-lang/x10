/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

//OPTIONS: -STATIC_CALLS

import harness.x10Test;

/**
 * Testing that an at(b) d.x is legal.
 * @author vj
 */

public class AtCheck3_MustFailCompile extends x10Test {
	var x:AtCheck3_MustFailCompile =null;
    def m(b: AtCheck3_MustFailCompile, d:AtCheck3_MustFailCompile) {
    	val c = b;
	    at ( b) {
	     val e = d.x;
	    }
    }
    
    public def run()=true;

    public static def main(Rail[String]) {
        new AtCheck3_MustFailCompile().execute();
    }
}
