/*
 * This file is part of ANUChem.
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * (C) Copyright Josh Milthorpe 2010.
 */
package au.edu.anu.mm;

import harness.x10Test;
import au.edu.anu.mm.WignerRotationMatrix;

/**
 * Test generation of the Wigner Rotation Matrix D^l_{km}.
 * @author milthorpe
 */
class TestWignerRotationMatrix extends x10Test {
    public def run(): boolean {
        val l = 3;
        val Dmk = WignerRotationMatrix.getDmk(5.0*Math.PI/4, l);
        Console.OUT.println("Dkm(5PI/4):");
        for (m in -l..l) {
		    for (k in -l..l) {
			    Console.OUT.print("" + Dmk(k,m) + " ");
            }
            Console.OUT.println();
		}

        return true;
    }

    public static def main(Array[String](1)) {
        new TestWignerRotationMatrix().execute();
    }

}
