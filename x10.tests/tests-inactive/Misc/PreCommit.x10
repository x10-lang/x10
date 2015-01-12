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
import x10.regionarray.*;

public class PreCommit extends x10Test {

    // Variant of superclass execute method designed 
    // to run multiple test cases in succession before failing.
    public def myExecute(test:x10Test):boolean {
        var b: boolean = false;
        try {
            finish b = test.run();
        } catch (e: Exception) {
            e.printStackTrace();
        }
        if (b) {
            Console.OUT.println("SUCCESS: "+test.typeName());
        } else {
            Console.OUT.println("FAILURE: "+test.typeName());
        }
        return b;
    }

    public def runConstructSampling():boolean {
        var result:boolean = true;

        result &= myExecute(new AtCheck2());
        result &= myExecute(new AtomicTest());
        result &= myExecute(new StructCall());
        result &= myExecute(new IntToInt1());
        val ct = new ClockTest6();
        ct.quiet = true;
        result &= myExecute(ct);
        result &= myExecute(new ClosureTypeParameters1b());
        result &= myExecute(new DepType(3n,9n));
        result &= myExecute(new BlockDistWithPlaceGroup());
        result &= myExecute(new ForLoop3());
        result &= myExecute(new Generics2());
        result &= myExecute(new InstanceofDownCast());
        result &= myExecute(new PolyEquality1());
        result &= myExecute(new StructEquality());
        result &= myExecute(new Typedef2());
        result &= myExecute(new ClassPathTest());

        // Note: precommit runs x10c tests with only 1 place due to
        //       limitations of multi-vm on windows.  Therefore can't
        //       require that all of these tests will be run.
        if (Place.numPlaces() >= 2) {
            result &= myExecute(new AsyncTest3());
            result &= myExecute(new AtCopy());
        }

        return result;
    }


    public def runHeatTransfer():boolean {
	Console.OUT.print("Running HeatTransfer...");
        val ht = new HeatTransfer_v1(11);
        ht.run();
	// Spot check result.  A(6,6) should be close to 0.25.
        val tmp = at (ht.A.place(6,6)) ht.A(6,6);
        if (tmp < 0.249 || tmp > 0.251) {
            Console.OUT.println("FAILURE: The value of A(6,6) is "+tmp);
            return false;
        } else {
            Console.OUT.println("SUCCESS");
            return true;
        }
    }

    public def run():boolean {
        var passed:boolean = true;
        passed &= runHeatTransfer();
        passed &= runConstructSampling();
        
        return passed;
    }

    public static def main(args:Rail[String]) {
	new PreCommit().execute();
    }
}
