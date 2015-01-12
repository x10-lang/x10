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

 */
public class ClockedFinish extends x10Test {
    var x:long=0;
    public def m():boolean {
    	clocked finish {
    		clocked async {
    			x=1;
    		}
    	}
    	return x==1;
	  
    }

    public def run() = m();

    public static def main(Rail[String]) {
	   new ClockedFinish().execute();
    }
}
