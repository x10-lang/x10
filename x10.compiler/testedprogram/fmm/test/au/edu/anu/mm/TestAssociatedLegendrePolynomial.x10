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
import au.edu.anu.mm.AssociatedLegendrePolynomial;

/**
 * Test calculation of associated Legendre polynomials.
 * @author milthorpe
 */
class TestAssociatedLegendrePolynomial extends x10Test {
    public def run(): boolean {
        val p = 10;

        Console.OUT.println("Associated Legendre Polynomials, calculated on theta:");
        val Plk = AssociatedLegendrePolynomial.getPlk(Math.PI/3.0, p);
		for (l in 0..p) {
		var fa : Double = 1.0;
            for (k in 0..l) {
			    Console.OUT.print("" + Plk(l,k) + " ");
		fa /= Math.sqrt((l-k+2.0)*(l+k+1.0));
            }
            Console.OUT.println();
		}

        Console.OUT.println("Associated Legendre Polynomials, calculated on cos(theta):");
        val Plm = AssociatedLegendrePolynomial.getPlm(Math.cos(Math.PI/3.0), p);
		for (i in 0..p) {
            for (j in 0..i) {
			    Console.OUT.print("" + Plm(i,j) + " ");
            }
            Console.OUT.println();
		}

        return true;
    }

    public static def main(Array[String](1)) {
        new TestAssociatedLegendrePolynomial().execute();
    }

}
