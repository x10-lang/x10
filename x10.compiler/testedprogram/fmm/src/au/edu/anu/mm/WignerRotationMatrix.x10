/*
 * This file is part of ANUChem.
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * (C) Copyright Josh Milthorpe 2010-2011.
 */
package au.edu.anu.mm;

/**
 * This class calculates the Wigner rotation matrix D^l_{mk}.
 * @see Dachsel (1996).
 *   "Fast and accurate determination of the Wigner rotation matrices in the fast multipole method".
 *   J. Chem. Phys. 124 (14) 144115. 14 April 2006.
 *   info:doi/10.1063/1.2194548
 * @author milthorpe
 */
public class WignerRotationMatrix {
        public static val OPERATOR_A : Int = 0;
        public static val OPERATOR_B : Int = -1;
        public static val OPERATOR_C : Int = 2;

	/**
	 * Calculate Wigner rotation matrix D_{mk}(theta, l) for m: [-l..l], k: [-l..l]
	 */
	public static def getDmk(theta: double, l : int) : Array[Double](2){rect} {
		if (Math.abs(theta) > 2.0 * Math.PI) {
		    throw new IllegalArgumentException("abs(x) > 2*PI: Wigner rotation matrix is only defined on [0..2*PI].");
		}

		val D = new Array[Double]((-l..l) * (-l..l));

		if (theta == 0.0) {
		    // Eq. 30
		    for (k in -l..l) {
		        D(k,k) = 1.0;
		    }
		    return D;
		} else if (theta == Math.PI) {
		    // Eq. 30
		    for (k in -l..l) {
		        D(k,-k) = Math.pow(-1, l+k);
		    }
		    return D;
		}

		var thetaPrime : Double = theta;
		// if cosTheta < 0 need to calculate Dlm using addition theorems - Eq. 30
		if (theta >= Math.PI/2.0) {
		    if (theta < Math.PI) {
		        thetaPrime = Math.PI - theta;
		    } else if (theta > Math.PI && theta < 3.0*Math.PI/2.0) {
		        thetaPrime = theta - Math.PI;
		    }/* else if (theta >= 3.0*Math.PI/2.0) { 
			thetaPrime = 2*Math.PI - theta;
		    }*/
		    // theta == PI already handled above
		}
		// Console.OUT.println("theta prime = " + thetaPrime);

		val cosTheta = Math.cos(thetaPrime);
		val sinTheta = Math.sin(thetaPrime);
		// Console.OUT.println("costheta = " + cosTheta + "; sintheta = " + sinTheta);

		// gl0 starting point, Eq. 29
		var gk0 : Double = 1.0;
		for (k in 1..l) {
		    gk0 = Math.sqrt((2.0*k-1) / (2.0*k)) * gk0;
		}

		// starting point for recursion, Eq. 28
		val gl0 = gk0;
		D(0,l) = Math.pow(-1.0, l) * gl0 * Math.pow(sinTheta, l);
		var glm : Double = gl0;
		var sign : Double = Math.pow(-1, l);
		for (m in 1..l) {
		    glm = Math.sqrt((l-m+1) as Double / (l+m)) * glm; // Eq. 29
		    sign *= -1.0;
		    //Console.OUT.println("g_{" + l + " " + m + "} = " + glm + " // (-1)^(l+m) = " + sign + 
			// " // sinTheta =  " + sinTheta + " // pow(sinTheta) = " + Math.pow(sinTheta, l-m)   );
		    D(m,l) = sign * glm * Math.pow(1.0 + cosTheta, m) * Math.pow(sinTheta, l-m);

		    // Console.OUT.println("D(m, l) = " + D(m, l) );
		}

		// Eq. 26
		for (var k:Int=l; k> -l; k--) {
		    D(l,k-1) = (l+k) / Math.sqrt(l*(l+1.0) - k*(k-1.0)) * sinTheta / (1.0 + cosTheta) * D(l,k);
		}

		// Eq. 25
		for (var m:Int=l-1; m>=0; m--) {
		    for (var k:Int=l; k> -l; k--) {
		        D(m,k-1) = Math.sqrt(((l*(l+1) - m*(m+1)) as Double) / (l*(l+1) - k*(k-1))) * D(m+1,k)
		                 + (m+k) / Math.sqrt((l*(l+1) - k*(k-1)) as Double) * sinTheta / (1.0 + cosTheta) * D(m,k);
		    }
		}

		// Eq. 27
		for (m in -l..-1) {
		    sign = Math.pow(-1, m-l);
		    for (k in -l..l) {
		        D(m,k) = sign * D(-m,-k);
		        sign *= -1;
		    }
		}

		// if cosTheta < 0 need to calculate Dlm using addition theorems - Eq. 30
		if (theta >= Math.PI/2.0) {
		    if (theta < Math.PI) {
			// Console.OUT.println("in the 2nd quadrant");
		        sign = -1.0;
		        for (m in -l..l) {
		            sign *= -1.0;
		            for (k in -l..0) {
		                val tmp = D(m,k);
		                D(m,k) = sign * D(m,-k);
		                D(m,-k) = sign * tmp;
		            }
		        }
		    } else if (theta > Math.PI && theta < 3.0*Math.PI/2.0) {
			// Console.OUT.println("in the 3rd quadrant");
		        sign = -1.0;
		        for (m in -l..0) {
		            sign *= -1.0;
		            for (k in -l..l) {
		                val tmp = D(m,k);
		                D(m,k) = sign * D(-m,k);
		                D(-m,k) = sign * tmp;
		            }
		        }
		    }
		}
		return D;
	}

	/** 
     * Generates all of the matrices that are needed to transform a multipole of length numTerms and returns them together,
     * indexed first by forward (0) and backward (1) rotations, then by l value
     * @param theta, angle for matrix
     * @param numTerms, maximum size of matrix required
     * @return all wigner matrices needed to rotate an expansion of length numTerms by angle theta
	 */
	public static def getCollection(theta : double, numTerms : int) : Rail[Rail[Array[Double](2){rect}]] {
		val collection = new Array[Rail[Array[Double](2){rect}]](2);
		for (r in 0..1) { 
			val R = new Array[Array[Double](2){rect}](numTerms+1);
			for (l in 0..numTerms) { 
				R(l) = WignerRotationMatrix.getDmk( (r==0)?theta:(2*Math.PI - theta) , l);
			}
        		collection(r) = R;
		}
		return collection;
        }

    /**
     * Takes the matrices generated in the above function and premultiplies each term by the appropriate factor so that they can be used
     * directly to transform expansions, saving later calculations.
     * @see Dachsel 2006, eqn 4 & 5
     */
	public static def getExpandedCollection(theta : double, numTerms : int, op : int) {
		val collection = getCollection(theta, numTerms);
		var F_mk : Double;
		for ([rev,l] in (0..1)*(0..numTerms)) { 
			val R = collection(rev)(l);
			for ([m, k] in R.region) {
				/* this is the corresponding factor for rotating a multipole expansion ... */
				F_mk = Math.sqrt( Factorial.getFactorial(l - k) * Factorial.getFactorial(l + k) / ( Factorial.getFactorial(l - m) * Factorial.getFactorial(l + m) ) );
				/* if this is for rotating a local expansion (i.e. operator C or the backwards rotation of operator B), adjust the factor */
				if ( op == OPERATOR_C || (rev == 1 && op == OPERATOR_B) ) F_mk = 1 / F_mk;

				R(m, k) = R(m, k) * F_mk;
			}
		}
		return collection;
	}

    /** 
     * More convenient function calls to the above function
     * Operator A needs multipole factors both before and after, Operator C needs local factors before and after, Operator B needs multipole before and local after
     */
	public static def getACollection(theta : double, numTerms : int) { return getExpandedCollection(theta, numTerms, OPERATOR_A); }
	public static def getBCollection(theta : double, numTerms : int) { return getExpandedCollection(theta, numTerms, OPERATOR_B); }
	public static def getCCollection(theta : double, numTerms : int) { return getExpandedCollection(theta, numTerms, OPERATOR_C); }
}
