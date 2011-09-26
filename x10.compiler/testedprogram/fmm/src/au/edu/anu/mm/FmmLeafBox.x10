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

import x10.util.ArrayList;

import x10x.vector.Point3d;
import au.edu.anu.chem.PointCharge;

/**
 * This class represents a leaf node (with no children)
 * in the 3D division of space for the fast multipole method.
 * @author milthorpe
 */
public class FmmLeafBox extends FmmBox {
    public val  atoms : ArrayList[PointCharge] = new ArrayList[PointCharge]();

    /** The U-list consists of all leaf boxes not well-separated to this box. */
    private var uList : Rail[Point(3)];

    public def this(level : Int, x : Int, y : Int, z : Int, numTerms : Int, parent : GlobalRef[FmmBox]) { 
        super(level, x, y, z, numTerms, parent);
    }

    public def addAtom(atom : PointCharge) {
        atomic(atoms, atom) {
            atoms.add(atom);
        }
    }

    public def getUList() = this.uList;

    public def setUList(uList : Rail[Point(3)]) {
        this.uList = uList;
    }

    /**
     * Creates the U-list for this box.
     * The U-list consists of all leaf boxes not well-separated from this box.
     */
    public def createUList(ws : Int) {
        val levelDim = Math.pow2(this.level);
        // interact with "left half" of uList i.e. only boxes with x<=box.x
        val uList = new ArrayList[Point(3)]();
        for (x in Math.max(0,this.x-ws)..this.x) {
            for (y in Math.max(0,this.y-ws)..Math.min(levelDim-1,this.y+ws)) {
                for (z in Math.max(0,this.z-ws)..Math.min(levelDim-1,this.z+ws)) {
                    if (x < this.x || (x == this.x && y < this.y) || (x == this.x && y == this.y && z < this.z)) {
                        uList.add(Point.make(x,y,z));
                    }
                }
            }
        }
        this.uList = uList.toArray();
    }

    /**
     * Creates the U-list for this box for
     * use with the periodic FMM.
     * The U-list consists of all leaf boxes not well-separated from this box.
     */
    public def createUListPeriodic(ws : Int) {
        val levelDim = Math.pow2(this.level);
        // interact with "left half" of uList i.e. only boxes with x<=box.x
        val uList = new ArrayList[Point(3)]();
        for (x in (this.x-ws)..this.x) {
            for (y in (this.y-ws)..(this.y+ws)) {
                for (z in (this.z-ws)..(this.z+ws)) {
                    if (x < this.x || (x == this.x && y < this.y) || (x == this.x && y == this.y && z < this.z)) {
                        uList.add(Point.make(x,y,z));
                    }
                }
            }
        }
        this.uList = uList.toArray();
    }
}

