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

// SOURCEPATH: x10.dist/samples

public class HeatTransferTest_v1 extends x10Test {
    public def run():boolean {
        val ht = new HeatTransfer_v1(11);
	Console.OUT.print("Beginning computation...");
	val start = System.nanoTime();
        ht.run();
	val stop = System.nanoTime();
	Console.OUT.printf("...completed in %1.3f seconds.\n", (stop-start as double)/1e9);
	ht.prettyPrintResult();

	// Spot check.  A(6,6) should be close to 0.25.
        // Can't use epsilon as the check because the termination
        // condition is that the rate of change is less than epsilon.
        val tmp = at (ht.A.place(6,6)) ht.A(6,6);
        Console.OUT.println("The value of A(6,6) is "+tmp);
        chk(tmp > 0.249);
        chk(tmp < 0.251);

	return true;
    }

    public static def main(args:Rail[String]) {
	new HeatTransferTest_v1().execute();
    }
}
