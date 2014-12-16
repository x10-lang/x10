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

import x10.regionarray.*;        

/**
 * Constant distribution
 *
 * (Was ConstDist)
 */
class PolyConst1 extends TestDist {

    public def run() {
	
	val r: Region = r(0,9,0,9);
        pr("r " + r);

	var d: Dist = Dist.makeConstant(r, here);
        pr("d " + d);

        var a: DistArray[double](r) = DistArray.make[double](Dist.makeConstant(r, here));
        pr("a " + a);

        var b: DistArray[double](r) = DistArray.make[double](Dist.makeConstant(r, here));
        pr("b " + b);
		


        return status();
    }

    def expected() =
        "r [0..9,0..9]\n"+
        "d Dist([0..9,0..9]->0)\n"+
        "a DistArray(Dist([0..9,0..9]->0))\n"+
        "b DistArray(Dist([0..9,0..9]->0))\n";
    
    public static def main(Rail[String]) {
        new PolyConst1().execute();
    }
}
