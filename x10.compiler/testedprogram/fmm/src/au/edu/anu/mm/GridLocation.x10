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

/**
 * This class represents a box location in the three dimensional
 * grid.  Boxes are numbered 0..dim-1 in each dimension.
 */
public struct GridLocation {
    public val x : Int;
    public val y : Int;
    public val z : Int;

    public def this(x : Int, y : Int, z : Int) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}

