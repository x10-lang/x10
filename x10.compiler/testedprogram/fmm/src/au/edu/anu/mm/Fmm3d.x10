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

import x10.compiler.Inline;
import x10.util.ArrayList;
import x10.util.HashMap;
import x10.util.HashSet;
import x10x.vector.Point3d;
import x10x.polar.Polar3d;
import x10x.vector.Vector3d;
import au.edu.anu.chem.PointCharge;
import au.edu.anu.chem.mm.MMAtom;
import au.edu.anu.util.Timer;

/**
 * This class implements the Fast Multipole Method for electrostatic
 * calculations in a cubic simulation space centred at the origin.
 * <p>
 * The 3D simulation space is divided in an octree of <code>numLevels</code> levels.
 * </p> 
 * @see White & Head-Gordon (1994). "Derivation and efficient implementation 
 *  of the fast multipole method". J Chem Phys 101 (8)
 * @see Lashuk et al. (2009). "A massively parallel adaptive fast-multipole 
 *  method on heterogeneous architectures". Proceedings of SC 2009
 * @author milthorpe
 */
public class Fmm3d {
    /** The number of levels in the octree. */
    public val numLevels : Int;

    /** The number of lowest level boxes along one side of the cube. */
    public val lowestLevelDim : Int;

    /** 
     * To ensure balanced rounding errors within the multipole and local 
     * calculations, all force/energy calculations are performed within 
     * an offset cube with top-left-front corner (-size/2, -size/2, -size/2).
     * This is the offset vector from the "real" (input) cube top-left-front
     * corner to the FMM top-left-front corner. 
     */
    public val offset : Vector3d;

    /** The side length of the cube. */
    public val size : Double; 

    /** The number of terms to use in the multipole and local expansions. */
    public val numTerms : Int;

    /** The well-separatedness parameter ws. */
    public val ws : Int;

    /** 
     * Return the top level of boxes actually used in the method.
     * This is 0 for the periodic FMM and 2 for the non-periodic FMM.
     */
    protected val topLevel : Int;

    // TODO enum - XTENLANG-1118
    public static val TIMER_INDEX_TOTAL : Int = 0;
    public static val TIMER_INDEX_PREFETCH : Int = 1;
    public static val TIMER_INDEX_DIRECT : Int = 2;
    public static val TIMER_INDEX_MULTIPOLE : Int = 3;
    public static val TIMER_INDEX_COMBINE : Int = 4;
    public static val TIMER_INDEX_TRANSFORM : Int = 5;
    public static val TIMER_INDEX_FARFIELD : Int = 6;
    public static val TIMER_INDEX_TREE : Int = 7;
    public static val TIMER_INDEX_PLACEHOLDER : Int = 8;
    /** A multi-timer for the several segments of a single getEnergy invocation, indexed by the constants above. */
    public val timer = new Timer(9);

    /** All boxes in the octree division of space. 
     * Array has numLevels elements, for levels [1..numLevels]
     * where boxes(n) = boxes at level n+1
     * Array dimensions are:
     * 0: x coordinate at that level (range 0..2^level)
     * 1: y coordinate
     * 2: z coordinate
     */
    val boxes : Rail[DistArray[FmmBox](3)];

    val lowestLevelBoxes : DistArray[FmmBox](3);

    /** The atoms in the simulation, divided up into an local Arrays, one for each place. */
    protected val atoms : DistArray[Rail[MMAtom]](1);

    /** 
     * A cache of transformations from multipole to local at the same level.
     * Dimensions are:
     * 0: a "virtual" dimension used to replicate the data across all places
     *   // TODO should be done as a global array XTENLANG-787
     * 1: box level in the tree
     * 2: x translation (difference between x coordinate of boxes)
     * 3: y translation
     * 4: z translation
     */
    val multipoleTransforms : DistArray[LocalExpansion](5);

    /** 
     * A cache of multipole translations between parent box centres and child box centres. 
     * Dimensions as per multipoleTransforms
     */
    val multipoleTranslations : DistArray[MultipoleExpansion](5);

    /**
     * An array of locally essential trees (LETs), one for each place.
     */
    protected val locallyEssentialTrees : DistArray[LocallyEssentialTree](1);

    /**
     * Are boundary conditions periodic?
     */
    val periodic : boolean;

    /** 
     * A cache of wigner matrices for translating between box centres of children to parent
     * The DistArray is a unique dist, duplicating all data to each place.
     * Dimensions of the internal Array are:
     *   0, 1, 2: x, y, z translation values
     * which in turn contains an Array indexed by:
     *   0 for forward, 1 for backward (rotations)
     * Each element of this is an Array[Array[Double](2){rect}](1)  
     *   which is indexed by l-value
     * Each element of this is a Wigner rotation matrix d^l for a particular  
     *   theta (for the translation with vector <x,y,z>)
     */
    val wignerA : DistArray[Array[Rail[Rail[Array[Double](2){rect}]]](3){rect,zeroBased}](1);
    val wignerB : DistArray[Array[Rail[Rail[Array[Double](2){rect}]]](3){rect}](1); // B is not zero-based
    val wignerC : DistArray[Array[Rail[Rail[Array[Double](2){rect}]]](3){rect,zeroBased}](1);

    /**
     * A cache of exp(k * phi * i) values for all phi that could be needed in a rotation and -p < k < p
     * The DistArray is a unique dist, duplicating all data to each place.
     * Dimensions of the internal Array are:
     *   0, 1, 2: x, y, z translation values
     * which in turn contains an Array indexed by:
     *   0 for +phi and 1 for -phi (for forward, back rotations)
     */
    val complexK : DistArray[Array[Rail[Array[Complex](1){rect,rail==false}]](3){rect}](1);

    /**
     * A flag which can be set to use the old translation operators (without rotations), probably preferable for small number of poles
     */
    public static val useOldOperators = false;

    /**
     * Initialises a fast multipole method electrostatics calculation
     * for the given system of atoms.
     * @param density mean number of particles per lowest level box
     * @param numTerms number of terms in multipole and local expansions
     * @param ws well-separated parameter
     * @param size length of a side of the simulation cube
     * @param atoms the atoms for which to calculate electrostatics
     */
    public def this(density : Double, 
                    numTerms : Int,
                    ws : Int,
                    topLeftFront : Point3d,
                    size : Double,  
                    numAtoms : Int,
                    atoms: DistArray[Rail[MMAtom]](1)) {
        // topLevel in regular FMM is 2 (boxes higher than this cannot be well-spaced)
        this(density, numTerms, ws, topLeftFront, size, numAtoms, atoms, 2, false);
    }

    /**
     * Initialises a fast multipole method electrostatics calculation
     * for the given system of atoms.
     * @param density mean number of particles per lowest level box
     * @param numTerms number of terms in multipole and local expansions
     * @param ws well-separated parameter
     * @param size length of a side of the simulation cube
     * @param atoms the atoms for which to calculate electrostatics
     * @param topLevel the topmost level for which boxes in the octree are used
     */
    protected def this(density : Double, 
                    numTerms : Int,
                    ws : Int,
                    topLeftFront : Point3d,
                    size : Double,  
                    numAtoms : Int,
                    atoms: DistArray[Rail[MMAtom]](1),
                    topLevel : Int,
                    periodic : boolean) {
        this.topLevel = topLevel;
        this.periodic = periodic;
        val numLevels = Math.max(2, (Math.log(numAtoms / density) / Math.log(8.0) + 1.0) as Int);
        this.numLevels = numLevels;

        var nBox : Int = 0;
        for (i in topLevel..numLevels) {
            nBox += Math.pow2(3*i) as Int;
        }
        val lowestLevelDim = Math.pow2(numLevels);
        this.lowestLevelDim = lowestLevelDim;
        //Console.OUT.println("numLevels = " + numLevels + " maxBoxes = " + nBox);

        this.numTerms = numTerms;
        this.ws = ws;

        val offset = Point3d(-size/2.0, -size/2.0, -size/2.0) - topLeftFront;
        this.offset = offset;
        this.size = size;

        this.atoms = atoms;

        timer.start(TIMER_INDEX_TREE);
        val boxes = constructTree(numLevels, topLevel, numTerms, ws, periodic);
        this.boxes = boxes;
        this.lowestLevelBoxes = boxes(numLevels);
        this.multipoleTranslations = precomputeTranslations(numLevels, topLevel, numTerms, size);
        this.multipoleTransforms = precomputeTransforms(numLevels, topLevel, numTerms, size);
        this.locallyEssentialTrees = createLocallyEssentialTrees(numLevels, topLevel, boxes, periodic);

    	this.wignerA = precomputeWignerA(numTerms);
    	this.wignerB = precomputeWignerB(numTerms, ws);
    	this.wignerC = precomputeWignerC(numTerms);
    	this.complexK = precomputeComplex(numTerms, ws);

        timer.stop(TIMER_INDEX_TREE);
    }
    
    public def calculateEnergy() : Double {
        timer.start(TIMER_INDEX_TOTAL);
        finish {
            async {
                prefetchRemoteAtoms();
            }
            multipoleLowestLevel();
            combineMultipoles();
            transformToLocal();
        }
        val totalEnergy = getDirectEnergy() + getFarFieldEnergy();
        timer.stop(TIMER_INDEX_TOTAL);
        return totalEnergy;
    }


    public def assignAtomsToBoxes() {
        timer.start(TIMER_INDEX_TREE);
        val offset = this.offset; // TODO shouldn't be necessary XTENLANG-1913
        val lowestLevelBoxes = this.lowestLevelBoxes; // TODO shouldn't be necessary XTENLANG-1913
        val atoms = this.atoms; // TODO shouldn't be necessary XTENLANG-1913
        val lowestLevelDim = this.lowestLevelDim; // TODO shouldn't be necessary XTENLANG-1913
        val size = this.size; // TODO shouldn't be necessary XTENLANG-1913
        //Console.OUT.println("assignAtomsToBoxes");
        finish ateach (p1 in atoms) {
            val localAtoms = atoms(p1);
            finish for (i in 0..(localAtoms.size-1)) {
                val atom = localAtoms(i);
                val charge = atom.charge;
                val offsetCentre = atom.centre + offset;
                val boxIndex = Fmm3d.getLowestLevelBoxIndex(offsetCentre, lowestLevelDim, size);
                async at(lowestLevelBoxes.dist(boxIndex)) {
                    val remoteAtom = new PointCharge(offsetCentre, charge);
                    val leafBox = lowestLevelBoxes(boxIndex) as FmmLeafBox;
                    leafBox.addAtom(remoteAtom);
                }
            }
        }
        // post-prune leaf boxes
        // TODO prune intermediate empty boxes as well
        finish ateach (boxIndex in lowestLevelBoxes) {
            val box = lowestLevelBoxes(boxIndex) as FmmLeafBox;
            if (box.atoms.size() == 0) {
                lowestLevelBoxes(boxIndex) = null;
            }
        }
        timer.stop(TIMER_INDEX_TREE);
    }

    /** 
     * For each atom, creates the multipole expansion and adds it to the
     * lowest level box that contains the atom.
     */
    def multipoleLowestLevel() {
        //Console.OUT.println("multipole lowest level");
        timer.start(TIMER_INDEX_MULTIPOLE);
        val size = this.size; // TODO shouldn't be necessary XTENLANG-1913
        val numTerms = this.numTerms; // TODO shouldn't be necessary XTENLANG-1913
        val lowestLevelBoxes = this.lowestLevelBoxes; // TODO shouldn't be necessary XTENLANG-1913
        finish for (p1 in lowestLevelBoxes.dist.places()) async at(p1) {
            for ([x,y,z] in lowestLevelBoxes.dist(here)) {
                val leafBox = lowestLevelBoxes(x,y,z) as FmmLeafBox;
                if (leafBox != null) {
                    val boxCentre = leafBox.getCentre(size);
                    for (i in 0..(leafBox.atoms.size()-1)) {
                        val atom = leafBox.atoms(i);
                        val atomLocation = boxCentre.vector(atom.centre);
                        val atomExpansion = MultipoleExpansion.getOlm(atom.charge, atomLocation, numTerms);
                        leafBox.multipoleExp.unsafeAdd(atomExpansion); // only one thread per box, so unsafeAdd is OK
                    }
                }
            }
        }
        timer.stop(TIMER_INDEX_MULTIPOLE);
    }

    /** 
     * For each level above the bottom level, combines multipole expansions for <= 8 child
     * boxes into a single multipole expansion for the parent box.
     */
    def combineMultipoles() {
        timer.start(TIMER_INDEX_COMBINE);
        for (var level: Int = numLevels-1; level >= topLevel; level--) {
            val thisLevel = level;
            //Console.OUT.println("combine level " + level + " => " + (level-1));
            val thisLevelBoxes = boxes(thisLevel);
            val lowerLevelBoxes = boxes(thisLevel+1);
            val multipoleTranslations = this.multipoleTranslations; // TODO shouldn't be necessary XTENLANG-1913
            val complexK = this.complexK; // TODO shouldn't be necessary XTENLANG-1913
            val wignerA = this.wignerA; // TODO shouldn't be necessary XTENLANG-1913
            val halfSideLength = size / Math.pow2(thisLevel+2);
            finish for (p1 in thisLevelBoxes.dist.places()) async at(p1) {
                val childMultipoles = new Array[MultipoleExpansion](8);
                for ([x,y,z] in thisLevelBoxes.dist(here)) {
                    val parent = thisLevelBoxes(x,y,z);
                    val childRegion = (2*x)..(2*x+1) * (2*y)..(2*y+1) * (2*z)..(2*z+1);
                    // asynchronously fetch all child multipole expansions
                    finish {
                        for ([x2,y2,z2] in childRegion) async {
                            childMultipoles(childRegion.indexOf(x2, y2, z2)) = getMultipoleExpansionLocalCopy(lowerLevelBoxes, x2, y2, z2);
                        }
                    }
                    // and then sequentially sum them together
                    for ([x2,y2,z2] in childRegion) {
                        val dx = ((x2+1)%2)*2-1;
                        val dy = ((y2+1)%2)*2-1;
                        val dz = ((z2+1)%2)*2-1;
                        val childExp = childMultipoles(childRegion.indexOf(x2, y2, z2));
                        if (childExp != null) {
		                    if (!useOldOperators) { 
			                    /* New! Operation A */
            	                parent.multipoleExp.translateAndAddMultipole(
				                    Vector3d(dx*halfSideLength, dy*halfSideLength, dz*halfSideLength),
				                    complexK(here.id)(dx,dy,dz), childExp, wignerA(here.id)((dx+1)/2, (dy+1)/2, (dz+1)/2));
		                    } else {
			                    /* Old Operation A */
			                    val translationIndex = multipoleTranslations(Point.make([here.id, thisLevel+1, (x2+1)%2, (y2+1)%2, (z2+1)%2]));
			                    parent.multipoleExp.translateAndAddMultipole(translationIndex, childExp);
		                    }
                        }
                    }
                }
            }
        }
        timer.stop(TIMER_INDEX_COMBINE);
    }

    /**
     * Starting at the top level, for each box, transform multipole
     * expansions for all well-separated boxes (for which the parent
     * box is not also well-separated) to local expansions for this 
     * box.  Translate the local expansion for each parent down to all
     * non-empty child boxes.
     */
    def transformToLocal() {
        timer.start(TIMER_INDEX_TRANSFORM);

        val numLevels = this.numLevels; // TODO shouldn't be necessary XTENLANG-1913
        val topLevel = this.topLevel; // TODO shouldn't be necessary XTENLANG-1913
        val multipoleTransforms = this.multipoleTransforms; // TODO shouldn't be necessary XTENLANG-1913
        val multipoleTranslations = this.multipoleTranslations; // TODO shouldn't be necessary XTENLANG-1913
        val locallyEssentialTrees = this.locallyEssentialTrees; // TODO shouldn't be necessary XTENLANG-1913
        val numTerms = this.numTerms; // TODO shouldn't be necessary XTENLANG-1913
        val boxes = this.boxes; // TODO shouldn't be necessary XTENLANG-1913
        val complexK = this.complexK; // TODO shouldn't be necessary XTENLANG-1913
        val wignerB = this.wignerB; // TODO shouldn't be necessary XTENLANG-1913
        val wignerC = this.wignerC; // TODO shouldn't be necessary XTENLANG-1913
        val startingLevel = periodic ? topLevel+1 : topLevel;
        for (thisLevel in startingLevel..numLevels) {
            //Console.OUT.println("transform level " + thisLevel);
            val sideLength = size / Math.pow2(thisLevel);
            finish ateach (p1 in Dist.makeUnique()) {
                val thisLevelBoxes = boxes(thisLevel);
                if (thisLevel == startingLevel) {
                    // must fetch top level multipoles synchronously before starting
                    Fmm3d.prefetchMultipoles(thisLevel, thisLevelBoxes, locallyEssentialTrees);
                }
                finish {
                    // can fetch next level multipoles asynchronously while computing this level
                    val lowerLevel = thisLevel+1;
                    if (lowerLevel <= numLevels) {
                        val lowestLevelBoxes = boxes(lowerLevel);
                        async Fmm3d.prefetchMultipoles(lowerLevel, lowestLevelBoxes, locallyEssentialTrees);
                    }

                    for ([x1,y1,z1] in thisLevelBoxes.dist(here)) async {
                        val myLET = locallyEssentialTrees(here.id);
                        val thisLevelMultipoleCopies = myLET.multipoleCopies(thisLevel);
                        val myComplexK = complexK(here.id);
                        val myWignerB = wignerB(here.id);
                        val myWignerC = wignerC(here.id);
                        //Console.OUT.println("starting " + x1 + "," + y1 + "," + z1);
                        val box1 = thisLevelBoxes(x1,y1,z1);
                        // these two objects are to provide temporary space for the B operator which does not have to be reallocated and GCed for each call
                        if (box1 != null) {
                            // ideally this would be replaced with a stack allocated array (TODO)
                            val scratch = new MultipoleExpansion(numTerms);    
                            val scratch_array = new Array[Complex](-numTerms..numTerms) as Array[Complex](1){rect,rail==false};
                            val vList = box1.getVList();
                            for ([p] in vList) {
                                val boxIndex2 = vList(p);
                                // TODO - should be able to detect Point rank and inline
                                val x2 = boxIndex2(0);
                                val y2 = boxIndex2(1);
                                val z2 = boxIndex2(2);
                                val box2MultipoleExp = thisLevelMultipoleCopies(x2, y2, z2);
                                if (box2MultipoleExp != null) {
                                    // TODO should be able to inline Point
                                    val dx = x2-x1;
                                    val dy = y2-y1;
                                    val dz = z2-z1;
                                    //val translation = box1.getTranslationIndex(boxIndex2);
				                    if (!useOldOperators) { 
                    					/* New Operation B */
		                    			box1.localExp.transformAndAddToLocal(scratch, scratch_array,
                    						Vector3d(dx*sideLength, dy*sideLength, dz*sideLength), 
                    						myComplexK(dx,dy,dz), box2MultipoleExp, myWignerB(dx,dy,dz) );
                    				} else { 
				                    	/* Old Operation B */
            	                        val translateP = Point.make([here.id, thisLevel, dx, dy, dz]);
            	                        val transform21 = multipoleTransforms(translateP);
            	                        box1.localExp.transformAndAddToLocal(transform21, box2MultipoleExp);
				                    }
                                }
                            }
                            if (thisLevel > topLevel) {
                                //Console.OUT.println("get parent for " + x1 + "," + y1 + "," + z1);
                                val box1Parent = box1.parent;
                                val box1ParentExp = box1Parent.evalAtHome[LocalExpansion]((parent:FmmBox) => parent.localExp);

                			    if (!useOldOperators) { 
                				    /* New Operation C */
                                    // TODO should be able to inline Point(3)
                                    val dx = (x1%2)*2-1;
                                    val dy = (y1%2)*2-1;
                                    val dz = (z1%2)*2-1;
	                			    box1.localExp.translateAndAddLocal(
                						Vector3d(dx*0.5*sideLength, dy*0.5*sideLength, dz*0.5*sideLength),
					                    myComplexK(dx,dy,dz), box1ParentExp, myWignerC((dx+1)/2, (dy+1)/2, (dz+1)/2));
                			    } else { 
                				    /* Old Operation C */
	                                val shift = multipoleTranslations(Point.make([here.id, thisLevel, box1.x%2, box1.y%2, box1.z%2]));
            	                    box1.localExp.translateAndAddLocal(shift, box1ParentExp);
                			    }
                            }
                        }
                    }
                }
            }
        }
        timer.stop(TIMER_INDEX_TRANSFORM);
    }

    public @Inline static def getChildBoxVector(shift : Point(3), sideLength : Double) { 
        return Vector3d((shift(0) - 0.5) * sideLength, (shift(1) - 0.5) * sideLength, (shift(2) - 0.5) * sideLength);
    }
    public @Inline static def getChildBoxCentreIndex(shift : Point(3)) { 
        return Point.make(shift(0)*2 - 1, shift(1)*2 - 1, shift(2)*2 - 1);
    }

    /**
     * Fetch all multipole expansions required for transform to local at this place.
     * This is communication-intensive, so can be overlapped with computation.
     */
    private static def prefetchMultipoles(level : Int, thisLevelBoxes : DistArray[FmmBox](3), locallyEssentialTrees : DistArray[LocallyEssentialTree](1)) : void {
        val myLET = locallyEssentialTrees(here.id);
        val combinedVList = myLET.combinedVList(level);
        val thisLevelCopies = myLET.multipoleCopies(level);

        val vListPlaces = new HashMap[Int,ArrayList[Point(3)]](26); // a place may have up to 26 immediate neighbours

        // separate the vList into partial lists stored at each nearby place
        for (p in combinedVList) {
            val boxIndex = combinedVList(p);
            val placeId = thisLevelBoxes.dist(boxIndex).id;
            var vListForPlace : ArrayList[Point(3)] = vListPlaces.getOrElse(placeId, null);
            if (vListForPlace == null) {
                vListForPlace = new ArrayList[Point(3)]();
                vListPlaces.put(placeId, vListForPlace);
            }
            vListForPlace.add(boxIndex);
        }

        // retrieve the partial list for each place and store into my LET
        finish for(placeEntry in vListPlaces.entries()) async {
            val placeId = placeEntry.getKey();
            val vListForPlace = placeEntry.getValue();
            val vListArray = vListForPlace.toArray();
            val multipolesForPlace = (placeId == here.id) ?
                getMultipolesForBoxList(thisLevelBoxes, vListArray) :
                at(Place.place(placeId)) { getMultipolesForBoxList(thisLevelBoxes, vListArray)};
            for (i in 0..(vListArray.size-1)) {
                thisLevelCopies(vListArray(i)) = multipolesForPlace(i);
            }
        }
    }

    /**
     * Given a level and a list of box indexes (as Point(3)) stored
     * at a single place, returns an Array, each element of which 
     * is in turn a MultipoleExpansion for the box.
     */
    private static def getMultipolesForBoxList(thisLevelBoxes : DistArray[FmmBox](3), boxList : Rail[Point(3)]) {
        val multipoleList = new Array[MultipoleExpansion](boxList.size, 
                                                            (i : Int) => 
                                                                getMultipoleExpansionLocalCopy(thisLevelBoxes,
                                                                                     boxList(i)(0), 
                                                                                     boxList(i)(1),
                                                                                     boxList(i)(2))
                                                            );
        return multipoleList;
    }

    /**
     * Fetch all atoms required for direct calculations at each place.
     * This is communication-intensive, so can be overlapped with computation.
     */
    def prefetchRemoteAtoms() : void {
        timer.start(TIMER_INDEX_PREFETCH);
        val locallyEssentialTrees = this.locallyEssentialTrees; // TODO shouldn't be necessary XTENLANG-1913
        val lowestLevelBoxes = this.lowestLevelBoxes; // TODO shouldn't be necessary XTENLANG-1913
        finish ateach (p1 in locallyEssentialTrees) {
            val myLET = locallyEssentialTrees(p1);
            val myCombinedUList = myLET.combinedUList;
            val cachedAtoms = myLET.cachedAtoms;

            val uListPlaces = new HashMap[Int,ArrayList[Point(3)]](26); // a place may have up to 26 immediate neighbours
            
            // separate the uList into partial lists stored at each nearby place
            for ([p] in myCombinedUList) {
                val boxIndex = myCombinedUList(p);
                val placeId = lowestLevelBoxes.dist(boxIndex).id;
                var uListForPlace : ArrayList[Point(3)] = uListPlaces.getOrElse(placeId, null);
                if (uListForPlace == null) {
                    uListForPlace = new ArrayList[Point(3)]();
                    uListPlaces.put(placeId, uListForPlace);
                }
                uListForPlace.add(boxIndex);
            }

            // retrieve the partial list for each place and store into my LET
            finish for (placeEntry in uListPlaces.entries()) async {
                val placeId = placeEntry.getKey();
                val uListForPlace = placeEntry.getValue();
                val uListArray = uListForPlace.toArray();
                val atomsForPlace = (placeId == here.id) ?
                    getAtomsForBoxList(lowestLevelBoxes, uListArray) :
                    at(Place.place(placeId)) { getAtomsForBoxList(lowestLevelBoxes, uListArray)};
                for (i in 0..(uListArray.size-1)) {
                    myLET.cachedAtoms(uListArray(i)) = atomsForPlace(i);
                }
            }
        }
        timer.stop(TIMER_INDEX_PREFETCH);
    }

    /**
     * Given a list of box indices as Point(3) stored at a single
     * place, returns an Rail, each element of which is in turn
     * a Rail[PointCharge] containing the atoms for each box.
     */
    private static def getAtomsForBoxList(lowestLevelBoxes : DistArray[FmmBox](3), boxList : Rail[Point(3)]) {
        val atomList = new Rail[Rail[PointCharge]](boxList.size, 
                                                    (i : Int) => 
                                                        getAtomsForBox(lowestLevelBoxes,
                                                                             boxList(i)(0), 
                                                                             boxList(i)(1),
                                                                             boxList(i)(2))
                                                    );
        return atomList;
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
                                for (otherBoxAtomIndex in 0..(boxAtoms.size-1)) {
                                    val atom2 = boxAtoms(otherBoxAtomIndex);
                                    for (atomIndex1 in 0..(box1.atoms.size()-1)) {
                                        val atom1 = box1.atoms(atomIndex1);
                                        thisPlaceEnergy += atom1.charge * atom2.charge / atom1.centre.distance(atom2.centre);
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
     * Calculates the sum of far-field interactions for all atoms:
     * for each atom, calculates the interaction using the local expansion
     * for the enclosing box, which is the combined contribution of
     * all atoms in all well-separated boxes.
     */ 
    def getFarFieldEnergy() : Double {
        //Console.OUT.println("getFarFieldEnergy");
        timer.start(TIMER_INDEX_FARFIELD);
        val size = this.size; // TODO shouldn't be necessary XTENLANG-1913
        val lowestLevelBoxes = this.lowestLevelBoxes; // TODO shouldn't be necessary XTENLANG-1913
        val farFieldEnergy = finish(SumReducer()) {
            for (p1 in lowestLevelBoxes.dist.places()) async at(p1) {
                var thisPlaceEnergy : Double = 0.0;
                for ([x,y,z] in lowestLevelBoxes.dist(here)) {
                    val box = lowestLevelBoxes(x,y,z) as FmmLeafBox;
                    if (box != null) {
                        val boxCentre = box.getCentre(size);
                        for (atomIndex in 0..(box.atoms.size()-1)) {
                            val atom = box.atoms(atomIndex);
                            val locationWithinBox = atom.centre.vector(boxCentre);
                            val farFieldEnergy = box.localExp.getPotential(atom.charge, locationWithinBox);
                            thisPlaceEnergy += farFieldEnergy;
                        }
                    }
                }
                offer thisPlaceEnergy;
            }
        };
        timer.stop(TIMER_INDEX_FARFIELD);

        return farFieldEnergy / 2.0;
    }

    static struct SumReducer implements Reducible[Double] {
        public def zero() = 0.0;
        public operator this(a:Double, b:Double) = (a + b);
    }

    /**
     * Precomputes multipole translations and multipole-to-local transformations,
     * for use in translating multipole expansions from child to parent boxes.
     * This is distributed to all places by replicating the first index in a 
     * block dist across Place.PLACES)
     * TODO workaround due to lack of global immutable arrays - XTENLANG-787
     */
    private def precomputeTranslations(numLevels : Int, topLevel : Int, numTerms : Int, size : Double) : DistArray[MultipoleExpansion](5) {
        val topChildLevel = topLevel + 1;
        if (numLevels < topChildLevel) {
            return null;
        } else {
            val multipoleTranslations = DistArray.make[MultipoleExpansion](Dist.makeBlock(0..(Place.MAX_PLACES-1) * topChildLevel..numLevels * 0..1 * 0..1 * 0..1, 0));
            finish ateach ([placeId,level,i,j,k] in multipoleTranslations) {
                val dim = Math.pow2(level);
                val sideLength = size / dim;
                val translationVector = Vector3d((i*2-1) * 0.5 * sideLength,
                                                 (j*2-1) * 0.5 * sideLength,
                                                 (k*2-1) * 0.5 * sideLength);
                multipoleTranslations(Point.make([placeId, level, i, j, k])) = MultipoleExpansion.getOlm(translationVector, numTerms);
            }
            return multipoleTranslations;
        }
    }

    /** 
     * Precomputes wigner rotation matrices premultiplied by appropriate factors for use 
     * in applying operator A. This is replicated in a unique dist across all places.
     */
    private def precomputeWignerA(numTerms : Int) {
        val wignerMatrices = DistArray.make[Array[Rail[Rail[Array[Double](2){rect}]]](3){rect,zeroBased}](Dist.makeUnique());
        finish ateach (place in wignerMatrices) {
            val placeWigner = new Array[Rail[Rail[Array[Double](2){rect}]]]((0..1)*(0..1)*(0..1));
            for ([i,j,k] in placeWigner) {
        		val theta = Polar3d.getPolar3d( Point3d(i*2-1,j*2-1,k*2-1) ).theta;
		        placeWigner(i, j, k) = WignerRotationMatrix.getACollection(theta, numTerms);
            }
            wignerMatrices(place) = placeWigner;
        }
        return wignerMatrices;
    }

    private def precomputeWignerC(numTerms : Int) {
        val wignerMatrices = DistArray.make[Array[Rail[Rail[Array[Double](2){rect}]]](3){rect,zeroBased}](Dist.makeUnique());
        finish ateach (place in wignerMatrices) {
            val placeWigner = new Array[Rail[Rail[Array[Double](2){rect}]]]((0..1)*(0..1)*(0..1));
            for ([i,j,k] in placeWigner) {
    		    val theta = Polar3d.getPolar3d( Point3d(i*2-1,j*2-1,k*2-1) ).theta;
		        placeWigner(i, j, k) = WignerRotationMatrix.getCCollection(theta, numTerms);
            }
            wignerMatrices(place) = placeWigner;
        }
        return wignerMatrices;
    }

    private def precomputeWignerB(numTerms : Int, ws : Int) {
        val wignerMatrices = DistArray.make[Array[Rail[Rail[Array[Double](2){rect}]]](3){rect}](Dist.makeUnique());
        finish ateach (place in wignerMatrices) {
            val placeWigner = new Array[Rail[Rail[Array[Double](2){rect}]]]((-(2*ws+1))..(2*ws+1) * (-(2*ws+1))..(2*ws+1) * (-(2*ws+1))..(2*ws+1));
            for ([i,j,k] in placeWigner) {
                val theta = Polar3d.getPolar3d ( Point3d(i, j, k) ).theta;
		        placeWigner(i, j, k) = WignerRotationMatrix.getBCollection(theta, numTerms);
            }
            wignerMatrices(place) = placeWigner;
        }
        return wignerMatrices;
    }

    /**
     * Precomputes the values of exp(phi * k * i) needed for every possible translation operation
     * and replicates to all places using a unique dist
     */
    private def precomputeComplex(numTerms : Int, ws : Int) {
        val complexK = DistArray.make[Array[Rail[Array[Complex](1){rect,rail==false}]](3){rect}](Dist.makeUnique());
        finish ateach (place in complexK) {
            val placeComplexK = new Array[Rail[Array[Complex](1){rect,rail==false}]]((-(2*ws+1))..(2*ws+1) * (-(2*ws+1))..(2*ws+1) * (-(2*ws+1))..(2*ws+1));
            for ([i,j,k] in placeComplexK) {
                val phi = Polar3d.getPolar3d ( Point3d(i, j, k) ).phi;
	            placeComplexK(i, j, k) = Expansion.genComplexK(phi, numTerms);
            }
            complexK(place) = placeComplexK;
        }
        return complexK;
    }

    /**
     * Precomputes a multipole transform array for use in transforming
     * multipole expansions of well-separated boxes to local expansions
     * at the current box.  This is distributed to all places by replicating 
     * the first index in a block dist across Place.PLACES)
     * TODO workaround due to lack of global immutable arrays - XTENLANG-787
     */
    private def precomputeTransforms(numLevels : Int, topLevel : Int, numTerms : Int, size : Double) : DistArray[LocalExpansion](5) {
        val multipoleTransformRegion  = 0..(Place.MAX_PLACES-1) * topLevel..numLevels * (-(2*ws+1))..(2*ws+1) * (-(2*ws+1))..(2*ws+1) * (-(2*ws+1))..(2*ws+1);
        val multipoleTransforms = DistArray.make[LocalExpansion](Dist.makeBlock(multipoleTransformRegion,0));
        finish ateach ([placeId,level,i,j,k] in multipoleTransforms) {
            val dim = Math.pow2(level);
            val sideLength = size / dim;
            val translationVector = Vector3d(i * sideLength,
                                             j * sideLength,
                                             k * sideLength);
            multipoleTransforms(Point.make([placeId, level, i, j, k])) = LocalExpansion.getMlm(translationVector, numTerms);
        }
        return multipoleTransforms;
    }

    private def constructTree(numLevels : Int, topLevel : Int, numTerms : Int, ws : Int, periodic : boolean) 
      : Rail[DistArray[FmmBox](3)] {
        val boxArray = new Array[DistArray[FmmBox](3)](numLevels+1);
        for (thisLevel in topLevel..numLevels) {
            val levelDim = Math.pow2(thisLevel) as Int;
            val thisLevelDist = MortonDist.makeMorton(0..(levelDim-1) * 0..(levelDim-1) * 0..(levelDim-1));
            if (periodic) {
                boxArray(thisLevel) = DistArray.make[FmmBox](new PeriodicDist(thisLevelDist));
            } else {
                boxArray(thisLevel) = DistArray.make[FmmBox](thisLevelDist);
            }
            //Console.OUT.println("level " + thisLevel + " dist: " + boxArray(thisLevel).dist);
        }

        for (thisLevel in topLevel..(numLevels-1)) {
            val levelDim = Math.pow2(thisLevel) as Int;
            val thisLevelBoxes = boxArray(thisLevel);
            finish ateach ([x,y,z] in thisLevelBoxes) {
                val box = new FmmBox(thisLevel, x, y, z, numTerms, Fmm3d.getParentForChild(boxArray, thisLevel, topLevel, x,y,z));
                if (periodic) {
                    if (thisLevel > topLevel) {
                        box.createVListPeriodic(ws);
                    }
                } else {
                    box.createVList(ws);
                }
                thisLevelBoxes(x,y,z) = box;
            }
        }

        val lowestLevelBoxes = boxArray(numLevels);
        finish ateach ([x,y,z] in lowestLevelBoxes) {
            val box = new FmmLeafBox(numLevels, x, y, z, numTerms, Fmm3d.getParentForChild(boxArray, numLevels, topLevel, x,y,z));
            if (periodic) {
                box.createUListPeriodic(ws);
                box.createVListPeriodic(ws);
            } else {
                box.createUList(ws);
                box.createVList(ws);
            }
            lowestLevelBoxes(x,y,z) = box;
        }

        return boxArray;
    }

    /**
     * Creates the locally essential tree at each place.  This is
     * later used to overlap remote retrieval of multipole expansion and
     * particle data with other computation.
     */
    private def createLocallyEssentialTrees(numLevels : Int, topLevel : Int, boxes : Rail[DistArray[FmmBox](3)], periodic : Boolean) : DistArray[LocallyEssentialTree]{rank==1} {
        val locallyEssentialTrees = DistArray.make[LocallyEssentialTree](Dist.makeUnique());
        finish ateach ([p1] in locallyEssentialTrees) {
            val lowestLevelBoxes = boxes(numLevels);
            val uMin = new Array[Int](3, Int.MAX_VALUE);
            val uMax = new Array[Int](3, Int.MIN_VALUE);
            val combinedUSet = new HashSet[Point(3)]();
            for ([x,y,z] in lowestLevelBoxes.dist(here)) {
                val box1 = lowestLevelBoxes(x,y,z) as FmmLeafBox;
                if (box1 != null) {
                    val uList = box1.getUList();
                    for ([p] in uList) {
                        val boxIndex2 = uList(p);
                        if (combinedUSet.add(boxIndex2)) {
                            for (i in 0..2) {
                                uMin(i) = Math.min(uMin(i), boxIndex2(i));
                                uMax(i) = Math.max(uMax(i), boxIndex2(i));        
                            }
                        }
                    }
                }
            }
            val combinedUList = new Array[Point(3)](combinedUSet.size());
            var j : Int = 0;
            for (boxIndex in combinedUSet) {
                combinedUList(j++) = boxIndex;
            }

            val combinedVList = new Rail[Rail[Point(3)]](numLevels+1);
            val vListMin = new Rail[Rail[Int]](numLevels+1);
            val vListMax = new Rail[Rail[Int]](numLevels+1);
            for (thisLevel in topLevel..numLevels) {
                if (!(periodic && thisLevel == topLevel)) {
                    val vMin = new Array[Int](3, Int.MAX_VALUE);
                    val vMax = new Array[Int](3, Int.MIN_VALUE);
                    val combinedVSet = new HashSet[Point(3)]();
                    val thisLevelBoxes = boxes(thisLevel);
                    for ([x,y,z] in thisLevelBoxes.dist(here)) {
                        val box1 = thisLevelBoxes(x,y,z);
                        if (box1 != null) {
                            val vList = box1.getVList();
                            if (vList != null) {
                                for ([p] in vList) {
                                    val boxIndex2 = vList(p);
                                    if (combinedVSet.add(boxIndex2)) {
                                        for (i in 0..2) {
                                            vMin(i) = Math.min(vMin(i), boxIndex2(i));
                                            vMax(i) = Math.max(vMax(i), boxIndex2(i));        
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //Console.OUT.println("done " + combinedVSet.size());
                    vListMin(thisLevel) = vMin;
                    vListMax(thisLevel) = vMax;
                    val thisLevelVList = new Array[Point(3)](combinedVSet.size());
                    var i : Int = 0;
                    for (boxIndex in combinedVSet) {
                        thisLevelVList(i++) = boxIndex;
                    }
                    combinedVList(thisLevel) = thisLevelVList;
                }
            }
            locallyEssentialTrees(p1) = new LocallyEssentialTree(combinedUList,
                                                                 combinedVList,
                                                                 uMin,
                                                                 uMax,
                                                                 vListMin,
                                                                 vListMax);
        }
        return locallyEssentialTrees;
    }

    protected static def getLowestLevelBoxIndex(offsetCentre : Point3d, lowestLevelDim : Int, size : Double) : Point(3) {
        return  Point.make((offsetCentre.i / size * lowestLevelDim + lowestLevelDim / 2) as Int, (offsetCentre.j / size * lowestLevelDim + lowestLevelDim / 2) as Int, (offsetCentre.k / size * lowestLevelDim + lowestLevelDim / 2) as Int);
    }

    protected static def getParentForChild(boxes : Rail[DistArray[FmmBox](3)], level : Int, topLevel : Int, x : Int, y : Int, z : Int) : GlobalRef[FmmBox] {
        if (level == topLevel)
            return GlobalRef[FmmBox](null);
        return (at(boxes(level-1).dist(x/2, y/2, z/2)) {GlobalRef[FmmBox](boxes(level-1)(x/2, y/2, z/2))});
    }

    private static def getAtomsForBox(lowestLevelBoxes : DistArray[FmmBox](3), x : Int, y : Int, z : Int) {
        val box = lowestLevelBoxes(x, y, z) as FmmLeafBox;
        if (box != null) {
            return box.atoms.toArray();
        } else {
            return null;
        }
    }

    /**
     * @return a local copy at the current place of a box's multipole expansion
     */
    private static def getMultipoleExpansionLocalCopy(thisLevelBoxes : DistArray[FmmBox](3), x : Int, y : Int, z : Int) : MultipoleExpansion {
        val boxHome = thisLevelBoxes.dist(x,y,z);
        if (boxHome == here) {
            return thisLevelBoxes(x,y,z) != null ? (thisLevelBoxes(x,y,z)).multipoleExp : null;
        } else {
            return at(boxHome) {thisLevelBoxes(x,y,z) != null ? (thisLevelBoxes(x,y,z)).multipoleExp : null};
        }
    }
}

