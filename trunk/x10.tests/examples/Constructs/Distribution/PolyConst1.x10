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

/**
 * Constant distribution
 *
 * (Was ConstDist)
 */

class PolyConst1 extends TestDist {

    public def run() {
	
	var r: Region = r(0,9,0,9);
        pr("r " + r);

	var d: Dist = Dist.makeConstant(r, here);
        pr("d " + d);

        var a: Array[double](r) = Array.make[double](Dist.makeConstant(r, here));
        pr("a " + a);

        var b: Array[double](r) = Array.make[double](Dist.makeConstant(r, here));
        pr("b " + b);
		


        return status();
    }

    def expected() =
        "r [0..9,0..9]\n"+
        "d Dist(0->[0..9,0..9])\n"+
        "a Array(Dist(0->[0..9,0..9]))\n"+
        "b Array(Dist(0->[0..9,0..9]))\n";
    
    public static def main(Rail[String]) {
        new PolyConst1().execute();
    }
}
