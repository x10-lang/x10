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

import x10x.vector.Point3d;
import x10x.vector.Tuple3d;

/**
 * Test local Taylor-type expansions.
 * @author milthorpe
 */
class TestLocalExpansion extends MathTest {
    public def run(): boolean {
        val p = 3; // multipole level
        val x = Point3d(1.0, 2.0, -1.0);

        val Mlm = LocalExpansion.getMlm(x as Tuple3d, p);
        Console.OUT.println("local expansion:\n" + Mlm.toString());

        val target = new LocalExpansion(p);
        val y = Point3d(2.0, -3.0, 1.0);
        val translation = MultipoleExpansion.getOlm(y as Tuple3d, p);
        target.translateAndAddLocal(translation, Mlm);
        Console.OUT.println("translated local:\n" + target.toString());

        val roundtrip = new LocalExpansion(p);
        val z = Point3d(-2.0, 3.0, -1.0);
        val reverseTranslation = MultipoleExpansion.getOlm(z as Tuple3d, p);
        roundtrip.translateAndAddLocal(reverseTranslation, target);
        Console.OUT.println("translated local - roundtrip:\n" + roundtrip.toString());
		for (i in 0..p) {
            for (j in -i..i) {
                chk(nearlyEqual(roundtrip.terms(i,j), Mlm.terms(i,j), 1.0e-6, 1.0e-12));
		    }
        }

        return true;
    }

    public static def main(Array[String](1)) {
        new TestLocalExpansion().execute();
    }

}
