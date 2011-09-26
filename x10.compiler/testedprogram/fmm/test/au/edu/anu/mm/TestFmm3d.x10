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
import au.edu.anu.chem.mm.MMAtom;
import au.edu.anu.chem.mm.ElectrostaticDirectMethod;
import au.edu.anu.chem.mm.TestElectrostatic;
import au.edu.anu.util.Timer;

/**
 * Tests the distributed FMM 3D implementation.
 * @author milthorpe
 */
public class TestFmm3d extends TestElectrostatic {
    public def sizeOfCentralCluster() : Double = 80.0;

    public static def main(args : Array[String](1)) {
        var numAtoms : Int;
        var density : Double = 60.0;
        var numTerms : Int = 10;
        var wellSpaced : Int = 2;
        var verbose : Boolean = false;
        var compare : Boolean = false;
        if (args.size > 0) {
            numAtoms = Int.parseInt(args(0));
            if (args.size > 1) {
                density = Double.parseDouble(args(1));
                if (args.size > 2) {
                    numTerms = Int.parseInt(args(2));
                    if (args.size > 3) {
                        wellSpaced = Int.parseInt(args(3));
                        if (args.size > 4) {
                            if (args(4).equals("-verbose")) {
                                verbose = true;
                            }
                            if (args.size > 5) {
                                if (args(5).equals("-compare")) {
                                    compare = true;
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Console.ERR.println("usage: TestFmm3d numAtoms [density] [numTerms] [wellSpaced] [-verbose] [-compare]");
            return;
        }

        new TestFmm3d().test(numAtoms, density, numTerms, wellSpaced, verbose, compare);
    }

    public def test(numAtoms : Int, density : Double, numTerms : Int, wellSpaced : Int, verbose : Boolean, compare : Boolean) {
        if (verbose) {
            val numLevels = Math.max(2, (Math.log(numAtoms / density) / Math.log(8.0) + 1.0) as Int);
            Console.OUT.println("Testing FMM for " + numAtoms 
                      + " atoms, target density = " + density
                      + " numTerms = " + numTerms
                      + " wellSpaced param = " + wellSpaced
                      + " numLevels = " + numLevels);
        } else {
            Console.OUT.print(numAtoms + " atoms: ");
        }

        val atoms = generateAtoms(numAtoms);
        val fmm3d = new Fmm3d(density, numTerms, wellSpaced, Point3d(0.0, 0.0, 0.0), SIZE, numAtoms, atoms);
        fmm3d.assignAtomsToBoxes();
        val energy = fmm3d.calculateEnergy();
        
        if (verbose) {
            Console.OUT.println("energy = " + energy);

            logTime("Tree construction", Fmm3d.TIMER_INDEX_TREE, fmm3d.timer);
            logTime("Prefetch",  Fmm3d.TIMER_INDEX_PREFETCH,  fmm3d.timer);
            logTime("Direct",    Fmm3d.TIMER_INDEX_DIRECT,    fmm3d.timer);
            logTime("Multipole", Fmm3d.TIMER_INDEX_MULTIPOLE, fmm3d.timer);
            logTime("Combine",   Fmm3d.TIMER_INDEX_COMBINE,   fmm3d.timer);
            logTime("Transform", Fmm3d.TIMER_INDEX_TRANSFORM, fmm3d.timer);
            logTime("Far field", Fmm3d.TIMER_INDEX_FARFIELD,  fmm3d.timer);
        }

        logTime("Total",     Fmm3d.TIMER_INDEX_TOTAL,     fmm3d.timer, verbose);

        if (compare) {
            val direct = new ElectrostaticDirectMethod(atoms);
            val directEnergy = direct.getEnergy();
            logTime(" cf. Direct electrostatic ", ElectrostaticDirectMethod.TIMER_INDEX_TOTAL, direct.timer);
            if (verbose) {
                val error = directEnergy - energy;
                Console.OUT.println("direct = " + directEnergy + " error = " + error + " relative error = " + Math.abs(error) / Math.abs(energy));
            }
        }
    }
}

