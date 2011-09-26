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

import x10x.vector.*;
import x10x.polar.Polar3d;

/**
 * This class calculates multipole expansions, using the algorithms given in
 * <a href="info:doi/10.1063/1.468354">
 *      Derivation and efficient implementation of the fast multipole method
 * </a>, White and Head-Gordon, 1994.
 * @author milthorpe
 */
public class MultipoleExpansion extends Expansion {

    public def this(p : Int) {
        super(p);
    }

    /**
     * A constructor which makes a copy of an existing expansion
     */
    public def this(source : MultipoleExpansion) {
	    super(source);
    }

    /**
     * Calculate the multipole-like term O_{lm} (with m >= 0) for a point v.
     */
    public static def getOlm(q : Double, v : Tuple3d, p : Int) : MultipoleExpansion {
        val exp = new MultipoleExpansion(p);
        val v_pole = Polar3d.getPolar3d(v);
        val pplm = AssociatedLegendrePolynomial.getPlk(v_pole.theta, p); 

        exp.terms(0,0) = Complex(q * pplm(0,0), 0.0);

        val phifac0 = Complex(Math.cos(-v_pole.phi), Math.sin(-v_pole.phi));
        var rfac : Double = v_pole.r;
        var il : Double = 1.0;
        for (l in 1..p) {
            il = il * l;
            var ilm : Double = il;
            var phifac : Complex = Complex.ONE;
            exp.terms(l,0) = phifac / ilm * (q * rfac * pplm(l,0)); 
            for (m in 1..l) {
                ilm = ilm*(l+m);
                phifac = phifac * phifac0;
		        val O_lm = phifac / ilm * (q * rfac * pplm(l,m));
                exp.terms(l,m) = O_lm;
        		//to avoid conjugate if (m != 0) { if (m_sign) exp.terms(l, -m) = Complex(O_lm.re,-O_lm.im); else exp.terms(l, -m) = Complex(-O_lm.re,O_lm.im); }
    	    }
            for (m in -l..-1) {
                exp.terms(l,m) = exp.terms(l,-m).conjugate() * (1-2*(-m%2));
            }
            rfac = rfac * v_pole.r;
        }

        return exp;
    }

    /**
     * Calculate the chargeless multipole-like term O_{lm} (with m >= 0) for a point v.
     */
    public static def getOlm(v : Tuple3d, p : Int) : MultipoleExpansion {
        val exp = new MultipoleExpansion(p);
        val v_pole = Polar3d.getPolar3d(v);
        val pplm = AssociatedLegendrePolynomial.getPlk(v_pole.theta, p); 

        exp.terms(0,0) = Complex(pplm(0,0), 0.0);

        val phifac0 = Complex(Math.cos(-v_pole.phi), Math.sin(-v_pole.phi));
        var rfac : Double = v_pole.r;
        var il : Double = 1.0;
        for (l in 1..p) {
            il = il * l;
            var ilm : Double = il;
            var phifac : Complex = Complex.ONE;
            exp.terms(l,0) = phifac / ilm * (rfac * pplm(l,0)); 
	        var m_sign : boolean = false;
            for (m in 1..l) {
                ilm = ilm*(l+m);
                phifac = phifac * phifac0;
        		val O_lm = phifac / ilm * (rfac * pplm(l,m));
                exp.terms(l,m) = O_lm;
	        	//to avoid conjugate if (m != 0) { if (m_sign) exp.terms(l, -m) = Complex(O_lm.re,-O_lm.im); else exp.terms(l, -m) = Complex(-O_lm.re,O_lm.im); }
	        	m_sign = !m_sign;
            }
            for (m in -l..-1) {
                exp.terms(l,m) = exp.terms(l,-m).conjugate() * (1-2*(-m%2));
            }
            rfac = rfac * v_pole.r;
        }

        return exp;
    }

    /** 
     * Translate a multipole expansion centred around the origin along a vector -b,
     * and adds to this expansion centred at -b.
     * This corresponds to "Operator A", Equations 10-11 in White & Head-Gordon.
     * Note: this defines A^lm_jk(b) = O_l-j,m-k(b); however this is only defined
     * where abs(m-k) <= l-j, therefore we restrict m to [j-l+k..-j+l+k]
     * @param b the vector along which to translate the multipole
     * @param source the source multipole expansion, centred at the origin
     */
    public atomic def translateAndAddMultipole(shift : MultipoleExpansion,
                                         source : MultipoleExpansion) {
        // TODO this atomic should be around inner loop update.
        // however as it's "stop the world" it's more efficient to do it out here
        {
            // TODO should be just:  for ([j,k] in terms.region) {
            for (j in 0..p) {
                for (k in -j..j) {
                    val O_jk = source.terms(j,k);
                    for (l in j..p) {
                        for (m in -l..l) {
                            if (Math.abs(m-k) <= (l-j)) {
                                val A_lmjk = shift.terms(l-j, m-k);
                                this.terms(l,m) = this.terms(l,m) + A_lmjk * O_jk; /*term is defined in super class*/
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * This is Operator A implementing rotations so that the actual translation occurs parallel with the z-axis
     * @param v is the vector through which the source should be translated
     * @param complexK is the pre calculated values of exp(i*-k*phi)
     * @param source is the multipole to add
     * @param wigner is the pre calculated Wigner matrices for the rotation angle theta, indexed first by forwards (0) and backwards (1)
     * @see Dachsel 2006, eqn 9
     */
    public def translateAndAddMultipole(v : Vector3d, complexK : Rail[Array[Complex](1){rect,rail==false}], source : MultipoleExpansion, wigner : Rail[Rail[Array[Double](2){rect}]]) { 
	    val b = v.length();
	    val invB = 1 / b;
	    val temp = new Array[Complex](-p..p) as Array[Complex](1){rect,rail==false}; // temporary space to do calculations in

	    val scratch = new MultipoleExpansion( source );
	    scratch.rotate(temp, complexK(0), wigner(0) );

	    val targetTerms = scratch.terms;
        var m_sign : int = 1;
	    for (m in 0..p) {
		    for (l in m..p) temp(l) = targetTerms(l, m);

            var b_lm_pow : double = 1.0;
		    for (l in m..p) {
			    var O_lm : Complex = Complex.ZERO;
			    var F_lm : Double = b_lm_pow / Factorial.getFactorial(l - m); // Factorial are already computed
			    for (j in m..l) {
				    O_lm = O_lm + temp(j) * F_lm; // explicitly this would be * Math.pow(translationPolar.r, l - j) / fact(l - j);
				    F_lm = F_lm * (l - j) * invB;
			    }
			    targetTerms(l, m) = O_lm;
			    //to avoid conjugate if (m != 0) { if (m_sign) targetTerms(l, -m) = Complex(O_lm.re,-O_lm.im); else targetTerms(l, -m) = Complex(-O_lm.re,O_lm.im); }
			    if (m != 0) targetTerms(l, -m) = O_lm.conjugate() * m_sign;
                b_lm_pow = b_lm_pow * b;
		    }
		    m_sign = -m_sign;
	    }

	    scratch.backRotate(temp, complexK(1), wigner(1) ); 
    	unsafeAdd(scratch);
    }

    /**
     * This is Operator A implementing rotations used if values are not already precalculated (i.e. in one-off tests)
     * Wigner matrices and values of exp(i*-k*phi) are calculated inside the method
     * @param v is the vector through which the source should be translated
     * @param source is the multipole to add
     */
    public def translateAndAddMultipole(v : Vector3d, source : MultipoleExpansion) {
    	val polar = Polar3d.getPolar3d(v);
    	translateAndAddMultipole(v, genComplexK(polar.phi, p), source, WignerRotationMatrix.getACollection(polar.theta, p) );
    }
    /**
     * Rotation of this expansion where Wigner matrices and exp(i*-k*phi) are not precalculated
     * Different method call for rotate which does the precalculations for the user
     * @param theta, rotation angle
     * @param phi, rotation angle
     * @return a new expansion which has been rotated
     */
    public def rotate(theta : Double, phi : Double) {
    	val target = new MultipoleExpansion(this);
        val temp = new Array[Complex](-p..p) as Array[Complex](1){rect,rail==false};
    	target.rotate(temp, genComplexK(phi, p)(1) , WignerRotationMatrix.getACollection(theta, p)(0) );
    	return target;
    }

    /**
     * For a periodic FMM, gets the macroscopic parent multipole
     * for the 3x3x3 box that is the parent of the box for which
     * this is the multipole expansion.
     * Uses the self similarity relation
     * M^(j+1)_(lm) = M^j_(lm) * 3^l
     * @see Kudin & Scuseria (1998) eq. 2.5
     */ 
    public def getMacroscopicParent() : MultipoleExpansion {
        val parentExpansion = new MultipoleExpansion(p);
        for ([l,m] in terms.region) {
            parentExpansion.terms(l,m) = terms(l,m) * Math.pow(3.0, l);
        }
        return parentExpansion;
    }
}
