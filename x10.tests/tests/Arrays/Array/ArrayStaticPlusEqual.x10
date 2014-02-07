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

public class ArrayStaticPlusEqual extends x10Test {

    val v  = GlobalRef[Rail[long]](new Rail[long](2, 0));

    public def run() {
	    at (v) {
	    	val myV = (v as GlobalRef[Rail[long]]{self.home==here})();
            for (i in 0..1) myV(i) += 5;
            for (i in 0..1) chk(myV(i) == 5);
        }
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ArrayStaticPlusEqual().execute();
    }
}
