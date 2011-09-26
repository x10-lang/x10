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

import x10x.vector.Point3d;
import x10x.vector.Vector3d;
import au.edu.anu.chem.PointCharge;
import au.edu.anu.chem.mm.MMAtom;
import au.edu.anu.util.Timer;

/**
 * This subclass of Fmm3d extends the base FMM with periodic boundary
 * conditions.  In addition to the interactions within the unit cell, 
 * the  unit cell interacts with 3^k * 3^k * 3^k copies of itself in 
 * concentric shells of increasingly coarse-grained aggregate cells.
 *
 * @see Lambert, Darden & Board (1996). "A Multipole-Based Algorithm 
 *  for Efficient Calculation of Forces and Potentials in Macroscopic 
 *  Periodic Assemblies of Particles". J Comp Phys 126 274-285
 *
 * @see Kudin & Scuseria (1998). "A fast multipole method for Periodic 
 * systems with arbitrary unit cell geometries". 
 * Chem. Phys. Letters 283 61-68
 * @author milthorpe
 */
public class PeriodicFmm3d extends Fmm3d {
    /** The number of concentric shells of copies of the unit cell. */
    public val numShells : Int;

    public static val TIMER_INDEX_MACROSCOPIC : Int = 8;

    /** A region representing a cube of 3x3x3 boxes, used for constructing macroscopic multipoles. */
    static val threeCube : Region(3) = (-1..1)*(-1..1)*(-1..1);

    /** A region representing a cube of 9x9x9 boxes, used for interacting with macroscopic multipoles. */
    static val nineCube : Region(3) = (-4..4)*(-4..4)*(-4..4);

    /**
     * Initialises a periodic fast multipole method electrostatics 
     * calculation for the given system of atoms.
     * @param density mean number of particles per lowest level box
     * @param numTerms number of terms in multipole and local expansions
     * @param size length of a side of the simulation cube
     * @param atoms the atoms for which to calculate electrostatics
     * @param numShells the number of concentric shells of copies of the unit cell
     */
    public def this(density : Double, 
                    numTerms : Int,
                    topLeftFront : Point3d,
                    size : Double,  
                    numAtoms : Int,
                    atoms: DistArray[Rail[MMAtom]](1),
                    numShells : Int) {
        // Periodic FMM always uses ws = 1
        // TODO is it possible to formulate for well-spaced > 1?
        super(density, numTerms, 1, topLeftFront, size, numAtoms, atoms, 0, true);
        this.numShells = numShells;
    }
    
    public def calculateEnergy() : Double {
        timer.start(TIMER_INDEX_TOTAL);
        finish {
            async {
                prefetchRemoteAtoms();
            }
            multipoleLowestLevel();

            combineMultipoles();
            combineMacroscopicExpansions();
            transformToLocal();
        }
        val totalEnergy = getDirectEnergy() + getFarFieldEnergy();
        timer.stop(TIMER_INDEX_TOTAL);
        return totalEnergy;
    }

    /** 
     * Generate and includes macroscopic expansions, for concentric rings
     * of aggregates of copies of the unit cell.
     */
    def combineMacroscopicExpansions() {
        timer.start(TIMER_INDEX_MACROSCOPIC);
        // TODO distributed impl.
        val numShells = this.numShells; // TODO shouldn't be necessary XTENLANG-1913
        val numTerms = this.numTerms; // TODO shouldn't be necessary XTENLANG-1913
        val size = this.size; // TODO shouldn't be necessary XTENLANG-1913
        val boxes = this.boxes; // TODO shouldn't be necessary XTENLANG-1913
        at (boxes(0).dist(0,0,0)) {
            val macroMultipoles = new Array[MultipoleExpansion](numShells+1);
            val macroLocalTranslations = new Array[LocalExpansion](numShells+1);
            val topLevelBox = boxes(0)(0,0,0);
            macroMultipoles(0) = topLevelBox.multipoleExp;

            var macroTranslation : MultipoleExpansion = new MultipoleExpansion(numTerms);

            // multipoles for shell 1
            for ([i,j,k] in threeCube) {
                val translationVector = Vector3d(i * size,
                                                 j * size,
                                                 k * size);
                val translation = MultipoleExpansion.getOlm(translationVector, numTerms);
                macroTranslation.unsafeAdd(translation); // only one thread for macro, so this is OK
            }
            macroMultipoles(1) = new MultipoleExpansion(numTerms);
            macroMultipoles(1).translateAndAddMultipole(macroTranslation, macroMultipoles(0));
            //Console.OUT.println("final for 1 = " + macroMultipoles(1));

            // locals for shell 1
            macroLocalTranslations(0) = new LocalExpansion(numTerms);
            for ([i,j,k] in nineCube) {
                if (Math.abs(i) > 1 || Math.abs(j) > 1 || Math.abs(k) > 1) {
                    // inner 27 boxes done at a lower level
                    val translationVector = Vector3d(i * size,
                                                     j * size,
                                                     k * size);
                    val transform = LocalExpansion.getMlm(translationVector, numTerms);
                    macroLocalTranslations(0).unsafeAdd(transform); // only one thread for macro, so this is OK
                }
            }
            macroLocalTranslations(1) = macroLocalTranslations(0).getMacroscopicParent();

            // remaining shells
            for (var shell: Int = 2; shell <= numShells; shell++) {
                macroTranslation = macroTranslation.getMacroscopicParent();
                macroMultipoles(shell) = new MultipoleExpansion(numTerms);
                macroMultipoles(shell).translateAndAddMultipole(macroTranslation, macroMultipoles(shell-1));
                //Console.OUT.println("final for " + shell + " = " + macroMultipoles(shell));
                macroLocalTranslations(shell) = macroLocalTranslations(shell-1).getMacroscopicParent();
            }

            // now transform and add macroscopic multipoles to local expansion for top level box
            for (var shell: Int = 0; shell <= numShells; shell++) {
                val localExpansion = macroLocalTranslations(shell);
                topLevelBox.localExp.transformAndAddToLocal(localExpansion, macroMultipoles(shell));
            }
            //Console.OUT.println("final for topLevel = " + topLevelBox.localExp);
        }
        timer.stop(TIMER_INDEX_MACROSCOPIC);
    }

    public def assignAtomsToBoxes() {
        timer.start(TIMER_INDEX_TREE);
        val offset = this.offset; // TODO shouldn't be necessary XTENLANG-1913
        val lowestLevelBoxes = this.lowestLevelBoxes; // TODO shouldn't be necessary XTENLANG-1913
        val atoms = this.atoms; // TODO shouldn't be necessary XTENLANG-1913
        val lowestLevelDim = this.lowestLevelDim; // TODO shouldn't be necessary XTENLANG-1913
        val size = this.size; // TODO shouldn't be necessary XTENLANG-1913
        val dipole = finish(VectorSumReducer()) {
            ateach (p1 in atoms) {
                var myDipole : Vector3d = Vector3d.NULL;
                val localAtoms = atoms(p1);
                finish for (i in 0..(localAtoms.size-1)) {
                    val atom = localAtoms(i);
                    val charge = atom.charge;
                    val offsetCentre = atom.centre + offset;
                    myDipole = myDipole + Vector3d(offsetCentre) * charge;
                    val boxIndex = Fmm3d.getLowestLevelBoxIndex(offsetCentre, lowestLevelDim, size);
                    // TODO should be able to call PeriodicDist.dist with Point(3) inlined
                    async at(lowestLevelBoxes.dist(boxIndex(0), boxIndex(1), boxIndex(2))) {
                        val remoteAtom = new PointCharge(offsetCentre, charge);
                        val leafBox = lowestLevelBoxes(boxIndex) as FmmLeafBox;
                        leafBox.addAtom(remoteAtom);
                    }
                }
                offer myDipole;
            }
        };

        cancelDipole(dipole);

        // post-prune leaf boxes
        // TODO prune intermediate empty boxes as well
        // TODO pruning before cancel dipole causes NPE on corner 
        //      boxes for small or non-uniform distributions
        finish ateach (boxIndex in lowestLevelBoxes) {
            val box = lowestLevelBoxes(boxIndex) as FmmLeafBox;
            if (box.atoms.size() == 0) {
                lowestLevelBoxes(boxIndex) = null;
            }
        }
        timer.stop(TIMER_INDEX_TREE);
    }

    def addAtomToLowestLevelBoxAsync(boxIndex : Point(3), offsetCentre : Point3d, charge : Double) {
        val size = this.size; // TODO shouldn't be necessary XTENLANG-1913
        val numTerms = this.numTerms; // TODO shouldn't be necessary XTENLANG-1913
        val lowestLevelBoxes = this.lowestLevelBoxes; // TODO shouldn't be necessary XTENLANG-1913
        async at(lowestLevelBoxes.dist(boxIndex)) {
            val remoteAtom = new PointCharge(offsetCentre, charge);
            val leafBox = lowestLevelBoxes(boxIndex) as FmmLeafBox;
            val boxLocation = leafBox.getCentre(size).vector(offsetCentre);
            val atomExpansion = MultipoleExpansion.getOlm(charge, boxLocation, numTerms);
            // both the following operations are atomic.  however they don't need to be completed together
            leafBox.addAtom(remoteAtom);
            leafBox.multipoleExp.add(atomExpansion);
        }
    }

    /** 
     * Add fictious charges to the corners of the unit cell 
     * to cancel the dipole moment.
     * @see Kudin & Scuseria, section 2.3
     */
    def cancelDipole(dipole : Vector3d) : Vector3d {
        //Console.OUT.println("dipole = " + dipole);
        var newDipole : Vector3d = dipole;
        finish {
            val p1 = Point3d(size, 0.0, 0.0) + offset;
            val q1 = - dipole.i / size;
            addAtomToLowestLevelBoxAsync(Point.make(lowestLevelDim-1, 0, 0), p1, q1);
            newDipole = newDipole + Vector3d(p1) * q1;

            val p2 = Point3d(0.0, size, 0.0) + offset;
            val q2 = - dipole.j / size;
            addAtomToLowestLevelBoxAsync(Point.make(0, lowestLevelDim-1, 0), p2, q2);
            newDipole = newDipole + Vector3d(p2) * q2;


            val p3 = Point3d(0.0, 0.0, size) + offset;
            val q3 = - dipole.k / size;
            addAtomToLowestLevelBoxAsync(Point.make(0, 0, lowestLevelDim-1), p3, q3);
            newDipole = newDipole + Vector3d(p3) * q3;


            val p0 = Point3d(0.0, 0.0, 0.0)  + offset;
            val q0 = -(q1 + q2 + q3);
            addAtomToLowestLevelBoxAsync(Point.make(0, 0, 0),                p0, q0);
            newDipole = newDipole + Vector3d(p0) * q0;
/*
            Console.OUT.println(q1 + " at " + p1);
            Console.OUT.println(q2 + " at " + p2);
            Console.OUT.println(q3 + " at " + p3);
            Console.OUT.println(q0 + " at " + p0);
*/
        }

        //Console.OUT.println("after cancelling, dipole = " + newDipole);
        return newDipole; 
    }

    /**
     * Gets sum of direct (pairwise) energy for all pairs of atoms
     * in non-well-separated boxes. This operations requires only
     * that atoms have already been assigned to boxes, and so can 
     * be done in parallel with other steps of the algorithm.
     */
    def getDirectEnergy() : Double {
        //Console.OUT.println("direct");
        timer.start(TIMER_INDEX_DIRECT);

        val locallyEssentialTrees = this.locallyEssentialTrees; // TODO shouldn't be necessary XTENLANG-1913
        val lowestLevelBoxes = this.lowestLevelBoxes; // TODO shouldn't be necessary XTENLANG-1913
        val lowestLevelDim = this.lowestLevelDim; // TODO shouldn't be necessary XTENLANG-1913
        val size = this.size; // TODO shouldn't be necessary XTENLANG-1913
        val directEnergy = finish (SumReducer()) {
            ateach (p1 in locallyEssentialTrees) {
                val myLET = locallyEssentialTrees(p1);
                val cachedAtoms = myLET.cachedAtoms;
                var thisPlaceEnergy : Double = 0.0;
                for ([x1,y1,z1] in lowestLevelBoxes.dist(here)) {
                    val box1 = lowestLevelBoxes(x1,y1,z1) as FmmLeafBox;
                    if (box1 != null) {
                        for (atomIndex1 in 0..(box1.atoms.size()-1)) {
                            // direct calculation with all atoms in same box
                            val atom1 = box1.atoms(atomIndex1);
                            for (sameBoxAtomIndex in 0..(atomIndex1-1)) {
                                val sameBoxAtom = box1.atoms(sameBoxAtomIndex);
                                val pairEnergy = atom1.charge * sameBoxAtom.charge / atom1.centre.distance(sameBoxAtom.centre);
                                thisPlaceEnergy += pairEnergy;
                            }
                        }

                        // direct calculation with all atoms in non-well-separated boxes
                        val uList = box1.getUList();
                        for (p in 0..(uList.size-1)) {
                            val boxIndex2 = uList(p);
                            // TODO - should be able to detect Point rank and inline
                            val x2 = boxIndex2(0);
                            val y2 = boxIndex2(1);
                            val z2 = boxIndex2(2);
                            val boxAtoms = cachedAtoms(x2, y2, z2);
                            if (boxAtoms != null) {
                                val translation = getTranslation(lowestLevelDim, size, x2, y2, z2);
                                for (otherBoxAtomIndex in 0..(boxAtoms.size-1)) {
                                    val atom2 = boxAtoms(otherBoxAtomIndex);
                                    val translatedCentre = atom2.centre + translation;
                                    for (atomIndex1 in 0..(box1.atoms.size()-1)) {
                                        val atom1 = box1.atoms(atomIndex1);
                                        val distance = atom1.centre.distance(translatedCentre);
                                        if (distance != 0.0) { // don't include dipole-balancing charges at same point
                                            thisPlaceEnergy += atom1.charge * atom2.charge / atom1.centre.distance(translatedCentre);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                offer thisPlaceEnergy;
            }
        };
        timer.stop(TIMER_INDEX_DIRECT);

        return directEnergy;
    }

    /**
     * Gets the atom centre translation vector due to a lowest-level box 
     * being in a neighbouring image, rather than the central unit cell.
     */
    private static def getTranslation(lowestLevelDim : Int, size : Double, x:  Int, y : Int, z : Int) : Vector3d {
        var translationX : Double = 0.0;
        if (x >= lowestLevelDim) {
            translationX = size;
        } else if (x < 0) {
            translationX = -size;
        }

        var translationY : Double = 0;
        if (y >= lowestLevelDim) {
            translationY = size;
        } else if (y < 0) {
            translationY = -size;
        }

        var translationZ : Double = 0;
        if (z >= lowestLevelDim) {
            translationZ = size;
        } else if (z < 0) {
            translationZ = -size;
        }
        return Vector3d(translationX, translationY, translationZ);
    }

    static struct VectorSumReducer implements Reducible[Vector3d] {
        public def zero() = Vector3d.NULL;
        public operator this(a:Vector3d, b:Vector3d) = (a + b);
    }

}

