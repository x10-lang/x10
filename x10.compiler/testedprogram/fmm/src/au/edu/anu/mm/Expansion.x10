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

import x10.util.StringBuilder;

/**
 * This is the superclass for multipole and local expansions, as used in
 * <a href="info:doi/10.1063/1.468354">
 *      Derivation and efficient implementation of the fast multipole method
 * </a>, White and Head-Gordon, 1994.
 * These expansions have a peculiarly shaped region(abs(x0)<=x1 && 0<=x1<=p)
 * (X10 gives it as (x0+x1>=0 && x0-x1>=0 && x0<=3), which is constructed
 * here by subtracting two halfspaces from a rectangular region. 
 * @author milthorpe
 */
public class Expansion {
    /** The terms X_{lm} (with m >= 0) in this expansion */
    /*S Zhang*/
    public /*atomicfield*/ val terms : Array[Complex](2);

    /** The number of terms in the expansion. */
    public val p : Int;

    public def this(p : Int) {
        val expRegion = new ExpansionRegion(p);
        this.terms = new Array[Complex](expRegion);
        this.p = p;
    }

    public def this(e : Expansion) { 
	    this.terms = new Array[Complex](e.terms);
        this.p = e.p;
    }

    /**
     * Add each term of e to this expansion. 
     * This operation is atomic and therefore thread-safe.
     */
    public atomic def add(e : Expansion) {
	    for (l in 0..p) {
	        for (m in -l..l) {
	            this.terms(l,m) = this.terms(l,m) + e.terms(l,m);
    	    }
        }
    }

    /**
     * Add each term of e to this expansion. 
     * This operation is not atomic, therefore not thread-safe.
     */
    def unsafeAdd(e : Expansion) {
        // TODO should be just:  for ([l,m] in terms.region) {
	    for (l in 0..p) {
	        for (m in -l..l) {
	            this.terms(l,m) = this.terms(l,m) + e.terms(l,m);
    	    }
        }
    }

    public def toString() : String {
        val s = new StringBuilder();
        for (i in 0..p) {
            for (j in -i..i) {
		        s.add("" + terms(i,j) + " ");
            }
            s.add("\n");
	    }
        return s.toString();
    }

    /**
     * Code to calculate the exponentials ( exp(i*k*phi) ) required to do a rotation around z-axis given an angle, used in Fmm3d and also for test cases where once-off angles are needed
     * @param phi, k
     * @return array of Complex first indexed by forward (0), backward (1) then by k
     * @see Dachsel 2006, eqn 4 & 5
     */
    public static def genComplexK(phi : Double, p : int) : Rail[Array[Complex](1){rect,rail==false}] { 
    	val complexK = new Array[Array[Complex](1){rect,rail==false}](2);
    	for (r in 0..1) { 
	    	complexK(r) = new Array[Complex](-p..p) as Array[Complex](1){rect,rail==false}; 
    		for (k in -p..p) complexK(r)(k) = Math.exp(Complex.I * k * phi * ((r==0)?1:-1) );
	    }
    	return complexK;
    }

    /** 
     * Rotates this expansion (local and multipole are differentiated by different precalculated wigner matrices) in three dimensions.
     * Performs rotation around z-axis first, THEN rotation around x-axis (for "forwards" rotation)
     * @param temp an array of complex numbers of size at least (-p..p) to do temporary calculations in
     * @param wigner, precalculated wigner matrices, an array of WignerMatrices, indexed first by p from 0 to max terms in expansion
     * @param complexK, values of exp(i*k*phi)
     * @see Dachsel 2006 eqn 4 & 5
     */
    public def rotate(temp : Array[Complex](1){rect,rail==false}, complexK : Array[Complex](1){rect,rail==false}, wigner : Rail[Array[Double](2){rect}]) {
    	//val temp = new Array[Complex](-p..p);
        for (l in 1..p) {
            val Dl = wigner(l); // avoids calculating matrices directly

	        for (k in -l..l) temp(k) = terms(l, k) * complexK(k);
           
	        var m_sign : int = 1;
            for (m in 0..l) {
	            var O_lm : Complex = Complex.ZERO;
                for (k in -l..l) {
                    O_lm = O_lm + temp(k) * Dl(m, k); // Eq. 5
                }
                terms(l,m) = O_lm;

        	    terms(l, -m) = O_lm.conjugate() * m_sign;
            	m_sign = -m_sign; // instead of doing the conjugate
            }
        }
    }

    /** 
     * Rotates this expansion (local and multipole are differentiated by different precalculated wigner matrices) in three dimensions.
     * Performs rotation around x-axis first, THEN rotation around z-axis (for "backwards" rotation)
     * @param temp an array of complex numbers of size at least (-p..p) to do temporary calculations in
     * @param wigner, precalculated wigner matrices, an array of WignerMatrices, indexed first by p from 0 to max terms in expansion
     * @param complexK, values of exp(i*k*phi)
     * @see Dachsel 2006 eqn 4 & 5
     */
    public def backRotate(temp : Array[Complex](1){rect,rail==false}, complexK : Array[Complex](1){rect,rail==false}, wigner : Rail[Array[Double](2){rect}]) {
    	//val temp = new Array[Complex](-p..p);
        for (l in 1..p) {
            val Dl = wigner(l); // avoids calculating matrices directly

	        for (k in -l..l) temp(k) = terms(l, k);
           
	        var m_sign : int = 1;
            for (m in 0..l) {
	            var O_lm : Complex = Complex.ZERO;
                for (k in -l..l) {
                    O_lm = O_lm + temp(k) * Dl(m, k); // Eq. 5
                }
                O_lm = O_lm * complexK(m);
                terms(l,m) = O_lm;

        	    terms(l, -m) = O_lm.conjugate() * m_sign;
            	m_sign = -m_sign; // instead of doing the conjugate
            }
        }
    }

}
