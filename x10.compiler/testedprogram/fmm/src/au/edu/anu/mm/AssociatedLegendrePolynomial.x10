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

import x10.array.TriangularRegion;

/**
 * This class calculates associated Legendre polynomials.
 * @author milthorpe
 */
public class AssociatedLegendrePolynomial {

    /**
     * Calculate associated Legendre polynomials P_{lk}(x) up to l=p (m.ge.0)
     * @see Dachsel (1996).
     *   "Fast and accurate determination of the Wigner rotation matrices in the fast multipole method".
     *   J. Chem. Phys. 124 (14) 144115. 14 April 2006.
     *   info:doi/10.1063/1.2194548
     */
    public static def getPlk(theta: double, p : int) : Array[Double](2) {
        val cosTheta = Math.cos(theta);
        val sinTheta = Math.sin(theta);

        val triRegion = new TriangularRegion(0,0,p+1,true);
        val P = new Array[Double](triRegion);
        P(0,0) = 1.0;
		var fact : Double = 1.0;
        for (l in 1..p) {
            P(l,l) = fact * sinTheta * -P(l-1,l-1);
            P(l,l-1) = fact * cosTheta * P(l-1,l-1);
			fact += 2.0;
        }

		fact = 1.0;
        for (l in 2..p) {
			fact += 2.0;
            for (k in 0..(l-2)) {
            	P(l,k) = (fact * cosTheta * P(l-1,k) - (l+k-1) * P(l-2,k)) / (l-k);
            }
        }
        return P;
    }

    /*
     * Calculate associated Legendre polynomials P_{lm}(x) up to l=p (m.ge.0)
     * @see White and Head-Gordon (1994).
     *      "Derivation and efficient implementation of the fast multipole method".
     *      J. Chem. Phys. 101 (8) 15 October 1994.
     */
    public static def getPlm(x: double, p : int) : Array[Double](2) {
        if (Math.abs(x) > 1.0) {
            throw new IllegalArgumentException("abs(x) > 1: Associated Legendre functions are only defined on [-1, 1].");
        }

        val triRegion = new TriangularRegion(0,0,p+1,true);
        val Plm = new Array[Double](triRegion);
		//val Plm = DistArray.make[double](Region.makeLowerTriangular(p+1));
		Plm(0,0) = 1.0;
		val somx2 : Double = Math.sqrt((1.0 - x) * (1.0 + x));
		var fact : Double = 1.0;
		for (var i : Int = 1; i<=p; i++) {
			Plm(i,i) = -Plm(i-1,i-1) * fact * somx2;
			Plm(i,i-1) = x * fact * Plm(i-1,i-1);
			fact += 2.0;
		}
		for (var m : Int = 0; m<=p-2; m++) {
			for (var l : Int = m+2; l<=p; l++) {
				Plm(l,m) = (x * (2.0 * l - 1.0) 
							* Plm(l-1,m) - (l + m - 1.0) 
							* Plm(l-2,m)) / (l - m);
			}
		}
		return Plm;
	}
}
