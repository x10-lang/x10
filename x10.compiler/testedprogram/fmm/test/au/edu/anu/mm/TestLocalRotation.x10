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
 * Some tests of the rotations and operations B, C on local expansions
 * @author haigh
 */
public class TestLocalRotation {
    static val RANDOM_SEED = 10101111L;
    static val R = new Random(RANDOM_SEED);

    public static def main(args : Array[String](1)) {
	    val args_doub = new Array[Double](0..7, 0);
	    if (args.size() == 6) { 
		    // Test operation B
		    for (i in 0..5) args_doub(i) = Double.parseDouble(args(i));
		    Console.OUT.println(new TestLocalRotation().operatorC(args_doub, true));
	    } else if (args.size() == 1) { 
		    for (j in 0..5) args_doub(j) = R.nextDouble() * 0.5;
		    // The purpose of this code is to try all 64 cases of the points in all quadrants
		    for (i in 0..63) {
			    for (k in 0..5) Console.OUT.print(args_doub(k) + " ");
			    Console.OUT.print( (new TestLocalRotation().operatorC(args_doub, false)) + "\n");

			    var j : int = 0; while (args_doub(j) < 0) { args_doub(j) = args_doub(j) * -1; j++; }
			    args_doub(j) = args_doub(j) * -1;
		    }
	    } else { 
		    // Reads in an entire expansion, term-by-term, and then performs a rotation on it, and compares the result
		    val p = Int.parse(args(0)); var k : Int = 0;
		    val exp = new MultipoleExpansion(p);
		    for (l in 0..p) for (m in -l..l) exp.terms(l, m) = Complex( Double.parseDouble(args(k++)), Double.parseDouble(args(k++)) );
		    new TestLocalRotation().arbitraryExpansionShift(exp, Point3d(-3, -3, -3) );
	    }
    }

    /** 
     * Takes two expansions and prints out the difference (absolute value) between corresponding terms
     *	@param two expansions
     */
    public def compare(first : Expansion, second : Expansion) { 
	    val p = first.terms.region.max(0);
	    for (i in 0..p) {
	        for (j in -i..i) { Console.OUT.print( (first.terms(i, j) - second.terms(i, j)).abs() + " ");
		    if ( (first.terms(i, j) - second.terms(i, j)).abs() > 0.1 ) Console.OUT.print("*" + i + " " + j + "*"); }
	        Console.OUT.print("\n");
	    }	
    }

    /** 
     * Returns the greatest discrepancy between the two expansions passed in as parameters
     * For really big p this does not work so well because the magnitude of the terms ~e30 makes errors seem large 
     */
    public def ok(first : Expansion, second : Expansion) { 
	    val p = first.terms.region.max(0);
	    var m : double = 0.0;
	    for (i in 0..p) for (j in -i..i) m = Math.max(m, (first.terms(i, j) - second.terms(i, j)).abs() );
	    return m; // < 10e-8;
    }

   def operatorC(args : Array[Double](1), print : boolean ) { 
	    val p = 5;
	    val oldCenter = Point3d(args(0), args(1), args(2)); val newCenter = Point3d(args(3), args(4), args(5));
	    val translationTuple = (newCenter - oldCenter) as Tuple3d;
	    val translationPolar = Polar3d.getPolar3d(translationTuple);
	    val M_lm = LocalExpansion.getMlm(oldCenter as Tuple3d, p);

	    // Direct method
	    var direct_result : LocalExpansion = new LocalExpansion(p);
	    direct_result.translateAndAddLocal( MultipoleExpansion.getOlm(translationTuple, p) , M_lm);

	    // Indirect method
	    var indirect_result : LocalExpansion = new LocalExpansion(p);
	    indirect_result.translateAndAddLocal( translationTuple, M_lm );

	    if (print) compare(direct_result, indirect_result);
	    return ok(direct_result, indirect_result);
   }

   /**
    * Takes a two vector (as 6 doubles), constructs a multipole and rotates it to the second vector and back, and checks the result is the same
    */
   def operatorCandBack(args : Array[Double](1), print : boolean ) { 
	val p = 3;
	val oldCenter = Point3d(args(0), args(1), args(2)); val newCenter = Point3d(args(3), args(4), args(5));
	val translationTuple = (newCenter - oldCenter) as Tuple3d;

	val O_lm = LocalExpansion.getMlm(oldCenter as Tuple3d, p);

	// Indirect method
	var indirect_result : LocalExpansion = new LocalExpansion(p);
	indirect_result.translateAndAddLocal( (newCenter - oldCenter) , O_lm );

	// Undone
	var undone : LocalExpansion = new LocalExpansion(p);
	undone.translateAndAddLocal( (oldCenter - newCenter) , indirect_result );

	if (print) {
		Console.OUT.println( LocalExpansion.getMlm(oldCenter, p) );
		Console.OUT.println( undone );
	}
	return ok( undone, LocalExpansion.getMlm(oldCenter, p) );
   }

   /**
    * Takes two vectors (as 6 doubles), constructs a multipole at the first and applies operator B to it to result in a local expansion at the second vector
    * This is done with and without rotations and the results are compared.
    * This test is failing at the moment unless the old translation operator is used to do the translation inside the rotation.
    */
   def operatorB(args : Array[Double](1), print : boolean ) { 
	val p = 5;
	val oldCenter = Point3d(args(0), args(1), args(2)); val newCenter = Point3d(args(3), args(4), args(5));
	val translationTuple = (newCenter - oldCenter) as Tuple3d;

	val O_lm = MultipoleExpansion.getOlm( oldCenter , p);

	// Without rotations
	val direct_method = new LocalExpansion(p);
	direct_method.transformAndAddToLocal( LocalExpansion.getMlm( translationTuple , p) , O_lm);

	// With rotations
	val indirect_method = new LocalExpansion(p);
	indirect_method.transformAndAddToLocal( newCenter - oldCenter , O_lm);

	if (print) { 
		Console.OUT.println( direct_method );
		Console.OUT.println( indirect_method );
		compare(indirect_method, direct_method);
	}
	return ok(indirect_method, direct_method);
   }

   /**
    * Takes an expansion, translates it by a given vector and compares the result with/without rotations
    */
   def arbitraryExpansionShift(O_lm : MultipoleExpansion, translationTuple : Tuple3d ) {
	val p = 5;

	// Without rotations
	val direct_method = new LocalExpansion(p);
	direct_method.transformAndAddToLocal( LocalExpansion.getMlm( translationTuple , p) , O_lm);

	// With rotations
	val indirect_method = new LocalExpansion(p);
	indirect_method.transformAndAddToLocal( translationTuple , O_lm);

	if (false) { 
		Console.OUT.println( direct_method );
		Console.OUT.println( indirect_method );
		compare(indirect_method, direct_method);
	}
	return ok(indirect_method, direct_method);
   }

}
