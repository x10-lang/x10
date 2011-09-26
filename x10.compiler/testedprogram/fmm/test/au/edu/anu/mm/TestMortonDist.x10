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


/**
 * Test Morton distribution including generation and mapping to points of Morton indices.
 * @author milthorpe
 */
class TestMortonDist extends x10Test {
    public def run(): boolean {
        val smallDist = MortonDist.make(0..3 * 0..3 * 0..3);
        Console.OUT.println("smallDist: " + smallDist);

        val p = Point.make(0, 2, 1);
        val pm = smallDist.getMortonIndex(p);
        Console.OUT.println(pm.toBinaryString());
        Console.OUT.println(smallDist.getPoint(pm));

        Console.OUT.println(smallDist(p));

        val q = Point.make(1, 1, 0);
        val qm = smallDist.getMortonIndex(q);
        Console.OUT.println(qm.toBinaryString());
        Console.OUT.println(smallDist.getPoint(qm));
        Console.OUT.println(smallDist(q));

        val mediumDist = MortonDist.make(0..7 * 0..7 * 0..7);
        val r = Point.make(5, 3, 7);
        val rm = mediumDist.getMortonIndex(r);
        Console.OUT.println(rm.toBinaryString());
        Console.OUT.println(mediumDist.getPoint(rm));
        Console.OUT.println(mediumDist(r));

        val bigDist = MortonDist.make(0..31 * 0..31 * 0..31);
        val s = Point.make(31, 30, 29);
        val sm = bigDist.getMortonIndex(s);
        Console.OUT.println(sm.toBinaryString());
        Console.OUT.println(bigDist.getPoint(sm));
        Console.OUT.println(bigDist(s));

        for (pt in bigDist | here) {
            if (!(pt.equals(bigDist.getPoint(bigDist.getMortonIndex(pt))))) {
                Console.OUT.println(pt + " => " + bigDist.getMortonIndex(pt) + " => " + bigDist.getPoint(bigDist.getMortonIndex(pt)));
            }
        }

        val hugeDist = MortonDist.make(0..63 * 0..63 * 0..63);
        val t = Point.make(61, 48, 63);
        val tm = hugeDist.getMortonIndex(t);
        Console.OUT.println(tm.toBinaryString());
        Console.OUT.println(hugeDist.getPoint(tm));
        Console.OUT.println(hugeDist(t));

        chk(hugeDist.getPoint(tm).equals(t));

        return true;
    }

    public static def main(Array[String](1)) {
        new TestMortonDist().execute();
    }

}
