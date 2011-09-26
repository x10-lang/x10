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

import x10x.vector.*;
import x10x.polar.Polar3d;

/**
 * This class calculates local Taylor-type expansions, using the algorithms given in
 * <a href="info:doi/10.1063/1.468354">
 *      Derivation and efficient implementation of the fast multipole method
 * </a>, White and Head-Gordon, 1994.
 * @author milthorpe
 */
public class LocalExpansion extends Expansion {

    public def this(p : Int) {
        super(p);
    }

    /**
     * A constructor which makes a copy of an existing expansion
     */
    public def this(source : LocalExpansion) {
    	super(source);
    }

    /**
     * Calculate the local Taylor-type expansion M_{lm} (with m >= 0) for a single point v.
     */
    public static def getMlm(v : Tuple3d, p : int) : LocalExpansion {
        val exp = new LocalExpansion(p);
        val terms = exp.terms;
        val v_pole = Polar3d.getPolar3d(v);
        val pplm = AssociatedLegendrePolynomial.getPlk(v_pole.theta, p);
        val rfac0 : Double = 1.0 / v_pole.r;

        terms(0,0) = Complex(rfac0 * pplm(0,0), 0.0);

        val phifac0 = Complex(Math.cos(v_pole.phi), Math.sin(v_pole.phi));
        var rfac : Double = rfac0 * rfac0;
        var il : Double = 1.0;
        for (l in 1..p) {
            il = il * l;
            var ilm : Double = il;
            var phifac : Complex = Complex.ONE;
            terms(l,0) = phifac * (rfac * pplm(l,0) * ilm);
	        var m_sign : boolean = false;
            for (m in 1..l) {
                ilm = ilm / (l+1-m);
                phifac = phifac * phifac0;
        		val M_lm = phifac * (rfac * pplm(l,m) * ilm);
                terms(l,m) = M_lm;
        		m_sign = !m_sign;
            }
            for (m in -l..-1) {
                terms(l,m) = terms(l,-m).conjugate() * (1-2*(-m%2));
            }
            rfac = rfac * rfac0;
        }

        return exp;
    }

    /** 
     * Translate a local expansion centred around the origin along a vector b,
     * and adds to this expansion centred at b, where shift is the expansion
     * of the vector b.
     * This corresponds to "Operator C", Equations 18-21 in White & Head-Gordon.
     * Note: this defines C^lm_jk(b) = O_j-l,k-m(b); however this is only defined
     * where abs(k-m) <= j-l, therefore we restrict k to [l-j+m..-l+j+m]
     * @param shift the multipole expansion of the translation
     * @param source the source local expansion, centred at the origin
     */
    public def translateAndAddLocal(shift : MultipoleExpansion,
                                         source : LocalExpansion) {
        // TODO should be just:  for ([l,m] in terms.region) {
        for (l in 0..p) {
            for (m in -l..l) {
                for (j in l..p) {
                    for (k in (l-j+m)..(-l+j+m)) {
                        val C_lmjk = shift.terms(j-l, k-m);
                        val O_jk = source.terms(j,k);
                        this.terms(l,m) = this.terms(l,m) + C_lmjk * O_jk;
                    }
                }
            }
        }
    }

    /** 
     * More efficient version of Operator C (translation and addition) with rotations
     * @param v a Tuple representing the shift of the expansion
     * @param wigner a collection of Wigner matrices precalculated to speed up the rotation
     * @param complexK is the pre calculated values of exp(i*k*phi)
     * @param source the source local expansion
     * @see Dachsel 2006, eqn 18
     */
    public def translateAndAddLocal(v : Vector3d, complexK : Rail[Array[Complex](1){rect,rail==false}], source : LocalExpansion, wigner : Rail[Rail[Array[Double](2){rect}]]) { 
	    val b = v.length();
	    val temp = new Array[Complex](-p..p) as Array[Complex](1){rect,rail==false};

	    val scratch : LocalExpansion = new LocalExpansion( source );
        scratch.rotate(temp, complexK(1), wigner(0) );

    	val targetTerms = scratch.terms;
    	var m_sign : int = 1;
    	for (m in 0..p) {
    		for (l in m..p) temp(l) = targetTerms(l, m);

    		for (l in m..p) {
    			var M_lm : Complex = Complex.ZERO;
    			var F_lm : Double = 1.0;
    			for (j in l..p) {  
    				M_lm = M_lm + temp(j) * F_lm;
    				F_lm = F_lm * b / (j - l + 1);
    			}
    			targetTerms(l, m) = M_lm;
    			if (m != 0) targetTerms(l, -m) = targetTerms(l, m).conjugate() * m_sign;
    		}
	    	m_sign = -m_sign;
	   	}

	    scratch.backRotate(temp, complexK(0), wigner(1) );
    	unsafeAdd(scratch);
    }

    /**
     * Different method call for Operator C which pre calculates matrices and exp(k*i*phi) for use in tests etc 
     * @param v a Tuple representing the shift of the expansion
     * @param source the source local expansion
     */
    public def translateAndAddLocal(v : Vector3d, source : LocalExpansion) {
	    val polar = Polar3d.getPolar3d(v);
	    translateAndAddLocal(v, genComplexK(polar.phi, p), source, WignerRotationMatrix.getCCollection(polar.theta, p) );
    }

   /** 
     * Transform a multipole expansion centred around the origin into a
     * Taylor expansion centred about b, and adds to this expansion.
     * This corresponds to "Operator B", Equations 13-15 in White & Head-Gordon.
     * This operator is inexact due to truncation of the series at <em>p</em> poles.
     * Note: this defines B^lm_jk(b) = M_j+l,k+m(b), therefore restrict l to [0..p-j]
     * @param b the vector along which to translate the multipole
     * @param source the source multipole expansion, centred at the origin
     */
    public def transformAndAddToLocal(transform : LocalExpansion,
                                         source : MultipoleExpansion) {
        // TODO should be just:  for ([j,k] in terms.region) {
        for (j in 0..p) {
            for (k in -j..j) {
                val O_jk = source.terms(j,k);
                for (l in 0..(p-j)) {
                    for (m in -l..l) {
                        if (Math.abs(k+m) <= (j+l)) {
                            val B_lmjk = transform.terms(j+l, k+m);
                            //Console.OUT.println("source.terms.dist(" + j + "," + k + ") = " + source.terms.dist(j,k));
                            this.terms(l,m) = this.terms(l,m) + B_lmjk * O_jk;
                        }
                    }
                }
            }
        }
    }

    /** 
     * More efficient version of Operator B with rotations
     * @param scratch, a MultipoleExpansion which can be used to do temporary calculations in
     * @param temp, a Complex array to do temporary calculations in
     * @param v, a Tuple representing the shift of the expansion
     * @param complexK, is the pre calculated values of exp(i*k*phi)
     * @param source, the expansion which should be shifted and added to this one
     * @param wigner, a collection of Wigner matrices precalculated to speed up the rotation
     * @see Dachsel 2006, eqn 17
     */
    public def transformAndAddToLocal(scratch : MultipoleExpansion, temp : Array[Complex](1){rect,rail==false}, v : Vector3d, complexK : Rail[Array[Complex](1){rect,rail==false}], source : MultipoleExpansion, wigner : Rail[Rail[Array[Double](2){rect}]]) { 
    	val inv_b = 1 / v.length();

        Array.copy(source.terms, scratch.terms);
    	scratch.rotate(temp, complexK(0), wigner(0) );

	    val targetTerms = scratch.terms;
	    //val temp = new Array[Complex](0..p);
	    var m_sign : int = 1;
        var b_m_pow : double = 1.0;
	    for (m in 0..p) {
		    for (l in m..p) temp(l) = targetTerms(l, -m);

            var b_lm1_pow : double = inv_b * b_m_pow * b_m_pow;
		    for (l in m..p) {
			    var M_lm : Complex = Complex.ZERO;
			    var F_lm : Double = Factorial.getFactorial(l + m) * b_lm1_pow;
			    for (j in m..(p-l)) {
				    M_lm = M_lm + temp(j) * F_lm;
				    F_lm = F_lm * (j + l + 1) * inv_b;
			    }
			    targetTerms(l, m) = M_lm;
			    if (m != 0) targetTerms(l, -m) = targetTerms(l, m).conjugate() * m_sign;
                b_lm1_pow = b_lm1_pow * inv_b;
		    }
            
		    m_sign = -m_sign;
            b_m_pow = b_m_pow * inv_b;
	    }

	    scratch.backRotate(temp, complexK(0), wigner(1) );
	    unsafeAdd(scratch);
    }

    /**
     * Different method call for Operator B which pre calculates matrices and exp(k*i*phi) for use in tests etc 
     * @param v, a Tuple representing the shift of the expansion
     * @param source, the source local expansion which will be added to this one
     */
    public def transformAndAddToLocal(v : Vector3d, source : MultipoleExpansion) {
    	val polar = Polar3d.getPolar3d(v);
        val scratch = new MultipoleExpansion(p);
        val temp = new Array[Complex](-p..p) as Array[Complex](1){rect,rail==false};
    	transformAndAddToLocal(scratch, temp, v, genComplexK(polar.phi, p), source, WignerRotationMatrix.getBCollection(polar.theta, p) );
    }
    /**
     * Different method call for rotate which does the precalculations for the user
     * @param theta, rotation angle
     * @param phi, rotation angle
     * @return a new expansion, which is the current expansion after it is rotated
     */
    public def rotate(theta : Double, phi : Double) {
        val target = new LocalExpansion(this);
        val temp = new Array[Complex](-p..p) as Array[Complex](1){rect,rail==false};
    	target.rotate(temp, genComplexK(phi, p)(0), WignerRotationMatrix.getCCollection(theta, p)(0) );
    	return target;
    }

    /**
     * Transforms this local expansion about the origin to the potential
     * acting on <code>q</code> at point <code>v</code>.
     * @param q the charge at point v
     * @param v the location of charge q
     */
    public def getPotential(q : Double,
                                v : Tuple3d) : Double {
        val transform = MultipoleExpansion.getOlm(q, v, p);
        var potential : Double = 0.0;
        // TODO use lift/reduction?
        // TODO should be just:  for ([j,k] in terms.region) {
        for (j in 0..p) {
            for (k in -j..j) {
                potential += (terms(j,k) * transform.terms(j,k)).re;
            }
        }
        return potential;
    }

    /**
     * For a periodic FMM, gets the macroscopic parent local expansion
     * for the 3x3x3 box that is the parent of the box for which
     * this is the local expansion.
     * Uses the self similarity relation
     * L^(j+1)_(lm) = L^j_(lm) / 3^l+1
     * @see Kudin & Scuseria (1998) eq. 2.5
     */ 
    public def getMacroscopicParent() : LocalExpansion {
        val parentExpansion = new LocalExpansion(p);
        for (l in 0..p) {
            for (m in -l..l) {
                parentExpansion.terms(l,m) = terms(l,m) / Math.pow(3.0, l+1);
            }
        }
        return parentExpansion;
    }
}


