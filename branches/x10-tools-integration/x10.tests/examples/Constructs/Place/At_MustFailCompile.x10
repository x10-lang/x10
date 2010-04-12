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
 * Testing that remote field access (for this) is detected by the compiler.
 * 
 * @author vj
 */

public class At_MustFailCompile extends x10Test {
	var x:At_MustFailCompile =null;
    def m(b: At_MustFailCompile) =
	   at (b) {
    	 // We dont know that this local. 
	     this.x
    };
    
    public def run()=true;

    public static def main(Rail[String]) {
        new At_MustFailCompile().execute();
    }
}
