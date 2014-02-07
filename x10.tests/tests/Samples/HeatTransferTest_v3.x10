
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
import x10.regionarray.*;

// SOURCEPATH: x10.dist/samples/tutorial

public class HeatTransferTest_v3 extends x10Test {
    public def run():boolean {
        val s = new HeatTransfer_v3();
	Console.OUT.print("Beginning computation...");
	val start = System.nanoTime();
        s.run();
	val stop = System.nanoTime();
	Console.OUT.printf("...completed in %1.3f seconds.\n", (stop-start as double)/1e9);
	s.prettyPrintResult();

	// Spot check.  A(2,2) should be close to 0.25.
        // Can't use epsilon as the ckeck because the termination
        // condition is that the rate of change is less than epsilon.
        val pt = Point.make(2,2);
        at (HeatTransfer_v3.BigD(pt)) {
            val tmp = s.A(pt);
            Console.OUT.println("The value of A(2,2) is "+tmp);
            chk(tmp > 0.249);
            chk(tmp < 0.251);
        }

	return true;
    }

    public static def main(args:Rail[String]) {
	new HeatTransferTest_v3().execute();
    }
}
