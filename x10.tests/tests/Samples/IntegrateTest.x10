/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;

// SOURCEPATH: x10.dist/samples

public class IntegrateTest extends x10Test {
    public def run():boolean {
        val obj = new Integrate((x:double)=>(x*x + 1.0) * x);
        val area = obj.computeArea(0, 10);
	    Console.OUT.println("Computed area is "+area);
	    chk(area > 2549.999);
	    chk(area < 2550.001);
	    return true;
    }

    public static def main(args:Rail[String]) {
	    new IntegrateTest().execute();
    }
}
