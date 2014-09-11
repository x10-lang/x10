/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright Australian National University 2014.
 */

package x10.matrix.util;

/**
 * A utility class to test the use of arbitrary place groups in the GML library
 */
public class PlaceGroupBuilder {
    public static def makeTestPlaceGroup(skipPlaces:Long):PlaceGroup {
        val livePlaces = new x10.util.ArrayList[Place]();

        for (pl in Place.places())
            livePlaces.add(pl);
        val rgen = RandTool.getRandGen();
        for (var c:Long =0; c < skipPlaces; c++){
            var skipPlaceIndex:Long = rgen.nextLong(livePlaces.size()-1);
            if (skipPlaceIndex == 0)
                skipPlaceIndex++;
            livePlaces.removeAt(skipPlaceIndex);
        }
        var placeGroup:SparsePlaceGroup = new SparsePlaceGroup(livePlaces.toRail());
        return placeGroup;
    }
}
