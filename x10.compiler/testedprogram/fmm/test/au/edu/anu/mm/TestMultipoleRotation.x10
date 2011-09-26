/*
 * This file is part of ANUChem.
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * (C) Copyright Andrew Haigh 2011.
 */
package au.edu.anu.mm;

import x10x.vector.Point3d;
import x10x.vector.Tuple3d;
import x10x.polar.Polar3d;
import x10.util.Random;

/**
 * Some tests of the rotations code extension of Fmm3d
 * @author haigh
 */
public class TestMultipoleRotation {
    static val RANDOM_SEED = 10101110L;
    static val R = new Random(RANDOM_SEED);

    public static def main(args : Array[String](1)) {
	    val args_doub = new Array[Double](0..7, 0);
	    if (args.size() == 2) { 
		    // Rotate multipole and back
		    val theta = Double.parseDouble(args(0)); val phi = Double.parseDouble(args(1));
		    new TestMultipoleRotation().rotation(theta, phi);
	    } else if (args.size() == 6) { 
		    // Test operation A
		    for (i in 0..5) args_doub(i) = Double.parseDouble(args(i));
		    new TestMultipoleRotation().rotationTest(args_doub);
	    } else { 
		    for (j in 0..5) args_doub(j) = R.nextDouble() * 0.5;
		    // The purpose of this code is to try all 64 cases of the points in all quadrants
		    for (i in 0..63) {
			    for (k in 0..5) Console.OUT.print(args_doub(k) + " ");
			    new TestMultipoleRotation().rotationTest(args_doub);

			    var j : int = 0; while (args_doub(j) < 0) { args_doub(j) = args_doub(j) * -1; j++; }
			    args_doub(j) = args_doub(j) * -1;
		    }
	    }
    }

    /**
     * Takes two expansions and prints out the difference (absolute value) between corresponding terms
     * @param two expansions
     */
    public def compare(first : Expansion, second : Expansion) { 
	    val p = first.terms.region.max(0);
	    for (i in 0..p) {
	        for (j in -i..i) Console.OUT.print( (first.terms(i, j) - second.terms(i, j)).abs() + " ");
	        Console.OUT.print("\n");
	    }
    }

    /** 
     * To determine whether two expansions are in fact the same
     * @param two expansions
     * @return true iff two Expansions are the same, within a fixed tolerance (10e-8)
     */
    public def ok(first : Expansion, second : Expansion) : boolean { 
	    val p = first.terms.region.max(0);
	    for (i in 0..p) {
            for (j in -i..i) {
                if ((first.terms(i, j) - second.terms(i, j)).abs() > 10e-8) return false;
            }
        }
	    return true;
    }

    /**
     * Performs a rotation on a multipole expansion (of arbitraray point) and then undoes it, checking to see if the original multipole is returned, works for all values.
     * @param two angles that determine the rotation to perform
     */
    public def rotation(theta : Double, phi : Double) { 
	    val p = 30;
	    val O_lm = MultipoleExpansion.getOlm(1.0, Point3d(-2.0, 3.0, -1.0) as Tuple3d, p);
	    //Console.OUT.println(O_lm);

	    val rotated_O_lm = O_lm.rotate(theta, phi);
	    //Console.OUT.println(rotated_O_lm);

	    val returned_O_lm = rotated_O_lm.rotate(2*Math.PI - theta, 0).rotate(0, 2*Math.PI - phi);
	    //Console.OUT.println(returned_O_lm);

	    Console.OUT.println(ok(O_lm, returned_O_lm));
    }

    /** 
     * Compares the results of shifting a multipole expansion using the direct method and the method involving rotations
     * @param 6 doubles representing the vector to construct multipole at and destination vector
     * @return whether the resultant multipoles were equal
     */
    public def rotationTest(args : Array[Double](1)) { 
	    val p = 5;
	    val oldCenter = Point3d(args(0), args(1), args(2)); val newCenter = Point3d(args(3), args(4), args(5));
	    val translationTuple = (newCenter - oldCenter) as Tuple3d;
	    val O_lm = MultipoleExpansion.getOlm(oldCenter as Tuple3d, p);

	    // Direct method
	    var direct_result : MultipoleExpansion = new MultipoleExpansion(p);
	    direct_result.translateAndAddMultipole( MultipoleExpansion.getOlm(translationTuple, p) , O_lm);

	    // Indirect method
	    var indirect_result : MultipoleExpansion = new MultipoleExpansion(p);
	    indirect_result.translateAndAddMultipole( translationTuple , O_lm );

	    //compare(direct_result, indirect_result);
	    Console.OUT.println(ok(direct_result, indirect_result));
    }
}
