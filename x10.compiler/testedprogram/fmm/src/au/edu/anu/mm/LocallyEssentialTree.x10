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

import au.edu.anu.chem.PointCharge;

/**
 * This class represents the Locally Essential Tree (LET) of
 * a single place.  This is the combined interaction lists
 * of all the boxes assigned to that place.
 * @author milthorpe
 */
public class LocallyEssentialTree {
    public val combinedUList : Rail[Point(3)];
    public val combinedVList : Rail[Rail[Point(3)]];
    public val uListMin : Rail[Int];
    public val uListMax : Rail[Int];
    public val vListMin : Rail[Rail[Int]];
    public val vListMax : Rail[Rail[Int]];

    /**
     * A cache of multipole copies for the combined V-list of all
     * boxes at this place.  Used to overlap fetching of the multipole
     * expansions with other computation.
     * The Array has one element for each level; each element
     * holds the portion of the combined V-list for that level.
     */
    public val multipoleCopies : Rail[DistArray[MultipoleExpansion](3)];

    /**
     * A cache of PointCharge for the combined U-list of all
     * boxes at this place.  Used to store fetched atoms from 
     * non-well-separated boxes for use in direct evaluations 
     * with all atoms at a given place.
     */
    public val cachedAtoms : DistArray[Rail[PointCharge]](3);
    
    public def this(combinedUList : Rail[Point(3)],
                combinedVList : Rail[Rail[Point(3)]],
                uListMin : Rail[Int],
                uListMax : Rail[Int],
                vListMin : Rail[Rail[Int]],
                vListMax : Rail[Rail[Int]]) {
        this.combinedUList = combinedUList;
        this.combinedVList = combinedVList;
        this.uListMin = uListMin;
        this.uListMax = uListMax;
        this.vListMin = vListMin;
        this.vListMax = vListMax;
        val multipoleCopies = new Array[DistArray[MultipoleExpansion](3)](combinedVList.size);
        for (i in 0..(combinedVList.size-1)) {
            if (combinedVList(i) != null) {
                val multipoleCopiesLevelRegion = vListMin(i)(0)..vListMax(i)(0) * vListMin(i)(1)..vListMax(i)(1) * vListMin(i)(2)..vListMax(i)(2);
                multipoleCopies(i) = DistArray.make[MultipoleExpansion](new PeriodicDist(Dist.makeConstant(multipoleCopiesLevelRegion)));
            }
        }
        this.multipoleCopies = multipoleCopies;

        val cachedAtomsRegion = uListMin(0)..uListMax(0) * uListMin(1)..uListMax(1) * uListMin(2)..uListMax(2);
        this.cachedAtoms = DistArray.make[Rail[PointCharge]](new PeriodicDist(Dist.makeConstant(cachedAtomsRegion)));
    }
}
