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

import x10.compiler.Inline;
import x10.util.ArrayList;

import x10x.vector.Point3d;

/**
 * This class represents a box in the 3D division of space
 * for the fast multipole method.
 * @author milthorpe
 */
public class FmmBox {
    public val parent : GlobalRef[FmmBox];

    public val level : Int;
    public val x : Int;
    public val y : Int;
    public val z : Int;

    /** 
     * The V-list consists of the children of those boxes 
     * not well-separated from this box's parent.
     */
    private var vList : Rail[Point(3)];

    /** The multipole expansion of the charges within this box. */
    public val multipoleExp : MultipoleExpansion;

    /** The Taylor expansion of the potential within this box due to particles in well separated boxes. */
    public val localExp : LocalExpansion;

    /**
     * Creates a new FmmBox with multipole and local expansions
     * of the given number of terms.
     */
    public def this(level : Int, x : Int, y : Int, z : Int, numTerms : Int, parent : GlobalRef[FmmBox]) {
        this.level = level;
        this.x = x;
        this.y = y;
        this.z = z;
        this.parent = parent;
        this.multipoleExp = new MultipoleExpansion(numTerms);
        this.localExp = new LocalExpansion(numTerms);
    }

    public def getCentre(size : Double) : Point3d {
        dim : Int = Math.pow2(level);
        sideLength : Double = size / dim;
        offset : Double = 0.5 * size;
        return Point3d( (x + 0.5) * sideLength - offset,
                        (y + 0.5) * sideLength - offset,
                        (z + 0.5) * sideLength - offset);
    }

    /**
     * Returns true if this box is well-separated from <code>x,y,z</code>
     * on the same level, i.e. if there are at least <code>ws</code>
     * boxes separating them.
     */
    public def wellSeparated(ws : Int, x2 : Int, y2 : Int, z2 : Int) : Boolean {
        return Math.abs(x - x2) > ws 
            || Math.abs(y - y2) > ws 
            || Math.abs(z - z2) > ws;
    }

    /**
     * Returns true if this box is well-separated from <code>box2</code>
     * on the same level, i.e. if there are at least <code>ws</code>
     * boxes separating them.
     */
    public def wellSeparated(ws : Int, box2 : FmmBox) : Boolean {
        return Math.abs(x - box2.x) > ws 
            || Math.abs(y - box2.y) > ws 
            || Math.abs(z - box2.z) > ws;
    }

    public @Inline final def getTranslationIndex(boxIndex2 : Point(3)) : Point(3) {
        return Point.make(boxIndex2(0)-x, boxIndex2(1)-y, boxIndex2(2)-z);
    }

    public def getVList() = this.vList;

    public def setVList(vList : Rail[Point(3)]) {
        this.vList = vList;
    }

    /**
     * Creates the V-list for this box.
     * The V-list consists of the children of those boxes not 
     * well-separated from the parent.
     */
    public def createVList(ws : Int) {
        val levelDim = Math.pow2(this.level);
        val xOffset = this.x%2 == 1 ? -1 : 0;
        val yOffset = this.y%2 == 1 ? -1 : 0;
        val zOffset = this.z%2 == 1 ? -1 : 0;
        val vList = new ArrayList[Point(3)]();
        for (x in Math.max(0,this.x-2*ws+xOffset)..Math.min(levelDim-1,this.x+2*ws+1+xOffset)) {
            for (y in Math.max(0,this.y-2*ws+yOffset)..Math.min(levelDim-1,this.y+2*ws+1+yOffset)) {
                for (z in Math.max(0,this.z-2*ws+zOffset)..Math.min(levelDim-1,this.z+2*ws+1+zOffset)) {
                    if (wellSeparated(ws, x, y, z)) {
                        vList.add(Point.make(x,y,z));
                    }
                }
            }
        }
        this.vList = vList.toArray();
    }

    /**
     * Creates the V-list for this box for use with
     * the periodic FMM.
     * The V-list consists of the children of those boxes not 
     * well-separated from the parent.
     */
    public def createVListPeriodic(ws : Int) {
        val xOffset = this.x%2 == 1 ? -1 : 0;
        val yOffset = this.y%2 == 1 ? -1 : 0;
        val zOffset = this.z%2 == 1 ? -1 : 0;
        val vList = new ArrayList[Point(3)]();
        for (x in (this.x-2*ws+xOffset)..(this.x+2*ws+1+xOffset)) {
            for (y in (this.y-2*ws+yOffset)..(this.y+2*ws+1+yOffset)) {
                for (z in (this.z-2*ws+zOffset)..(this.z+2*ws+1+zOffset)) {
                    if (wellSeparated(ws, x, y, z)) {
                        vList.add(Point.make(x,y,z));
                    }
                }
            }
        }
        this.vList = vList.toArray();
    }

    public def toString(): String {
        return "FmmBox level " + level + " (" + x + "," + y + "," + z + ")";
    }
}

