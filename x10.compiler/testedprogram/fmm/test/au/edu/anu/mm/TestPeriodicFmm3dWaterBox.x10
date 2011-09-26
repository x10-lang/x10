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

import x10.util.ArrayList;

import x10x.vector.Point3d;
import au.edu.anu.chem.mm.GromacsStructureFileReader;
import au.edu.anu.chem.mm.MMAtom;
import au.edu.anu.chem.mm.TestElectrostatic;
import au.edu.anu.util.Timer;

/**
 * Tests the distributed Periodic FMM implementation using
 * the "bigwater" box - an 80 Angstrom cube filled with SPC water.
 * atom coordinates are read from a GROMACS structure file.
 * @see H. Berendsen, J. Grigera, and T. Straatsma,
 *      "The missing term in effective pair potentials,"
 *      J. Phys. Chem., vol. 91, pp. 6269-71, Nov 1987.
 * @author milthorpe
 */
public class TestPeriodicFmm3dWaterBox extends TestElectrostatic {
    public def sizeOfCentralCluster() : Double = 80.0;

    public static def main(args : Array[String](1)) {
        var structureFileName : String;
        var density : Double = 60.0;
        var numTerms : Int = 10;
        var numShells : Int = 10;
        if (args.size > 0) {
            structureFileName = args(0);
            if (args.size > 1) {
                density = Double.parseDouble(args(1));
                if (args.size > 2) {
                    numTerms = Int.parseInt(args(2));
                    if (args.size > 3) {
                        numShells = Int.parseInt(args(3));
                        if (numShells < 1) numShells = 1; // doesn't make sense to do 0 shells
                    }
                }
            }
        } else {
            Console.ERR.println("usage: periodicFmm3d myStructureFile.gro [density] [numTerms] [numShells]");
            return;
        }

        new TestPeriodicFmm3dWaterBox().test(structureFileName, density, numTerms, numShells);
    }

    public def test(structureFileName : String, density : Double, numTerms : Int, numShells : Int) {

        Console.OUT.println("Testing Periodic with structure file " + structureFileName
                          + " atoms, target density = " + density
                          + " numTerms = " + numTerms
                          + " numShells = " + numShells);

        val molecule = new GromacsStructureFileReader(structureFileName).readMolecule();

        val molAtoms = molecule.getAtoms();
        Console.OUT.println("read " + molAtoms.size() + " atoms.");

        val tempAtoms = DistArray.make[ArrayList[MMAtom]](Dist.makeUnique(), (Point) => new ArrayList[MMAtom]());
        finish for (var i : Int = 0; i < molAtoms.size(); i++) {
            val atom = molAtoms(i);
            val centre = atom.centre;

            // normalise coordinates to fit inside box
            val x = (centre.i < 0.0) ? centre.i + SIZE : (centre.i >= SIZE) ? centre.i - SIZE : centre.i;
            val y = (centre.j < 0.0) ? centre.j + SIZE : (centre.j >= SIZE) ? centre.j - SIZE : centre.j;
            val z = (centre.k < 0.0) ? centre.k + SIZE : (centre.k >= SIZE) ? centre.k - SIZE : centre.k;

            val charge = atom.charge;
            val p = getPlaceId(x, y, z);
            if (p >= 0 && p < Place.MAX_PLACES) {
                async at (Place.place(p)) {
                    val atom = new MMAtom(Point3d(x,y,z), charge);
                    //Console.OUT.println(atom);
                    atomic(tempAtoms) { tempAtoms(p).add(atom); }
                }
            } else {
                Console.ERR.println("could not map atom to place: " + atom.centre);
            }
        }
        val atoms = DistArray.make[Rail[MMAtom]](Dist.makeUnique(), ([p] : Point) => tempAtoms(p).toArray());

        val fmm3d = new PeriodicFmm3d(density, numTerms, Point3d(0.0, 0.0, 0.0), SIZE, molAtoms.size(), atoms, numShells);
        val energy = fmm3d.calculateEnergy();

        Console.OUT.println("energy = " + energy);

        logTime("Prefetch",  Fmm3d.TIMER_INDEX_PREFETCH,  fmm3d.timer);
        logTime("Direct",    Fmm3d.TIMER_INDEX_DIRECT,    fmm3d.timer);
        logTime("Multipole", Fmm3d.TIMER_INDEX_MULTIPOLE, fmm3d.timer);
        logTime("Combine",   Fmm3d.TIMER_INDEX_COMBINE,   fmm3d.timer);
        logTime("Macroscopic", PeriodicFmm3d.TIMER_INDEX_MACROSCOPIC, fmm3d.timer);
        logTime("Transform", Fmm3d.TIMER_INDEX_TRANSFORM, fmm3d.timer);
        logTime("Far field", Fmm3d.TIMER_INDEX_FARFIELD,  fmm3d.timer);
        logTime("Total",     Fmm3d.TIMER_INDEX_TOTAL,     fmm3d.timer);
        Console.OUT.printf("Tree construction: %g seconds\n", (fmm3d.timer.total(Fmm3d.TIMER_INDEX_TREE) as Double) / 1e9);
    }
}

