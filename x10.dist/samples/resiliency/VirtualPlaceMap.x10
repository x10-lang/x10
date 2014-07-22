/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import x10.lang.Exception;

public class VirtualPlaceMap {

    private var virtualMap: Rail[Place];
    private var numVirtualPlaces: Long;
    public static val NONEXISTENT_PLACE = -1L;

    /* Construct virtual place map identical to physical place map for first
     * numPlaces places.  Throw an exception if numPlaces is out of range
     */
    public def this(numPlaces: Long) {
        if (numPlaces < 1L || numPlaces > Place.MAX_PLACES)
            throw new Exception("Error: Number of places specified for VirtualPlaceMap constructor is out of range");
        numVirtualPlaces = numPlaces;
        virtualMap = new Rail[Place](numVirtualPlaces);
        var virtualPlaceIndex: Long = 0;
        for (p in Place.places()) {
            virtualMap(virtualPlaceIndex) = p;
            virtualPlaceIndex++;
	    if (virtualPlaceIndex >= numVirtualPlaces)
	        break;
        }
    }

    /* Return the virtual place id corresponding to "place".  Return 
     * NONEXISTENT_PLACE if no virtual place is found corresponding to "place"
     */
    public def physicalToVirtual(place: Place): Long {
        for (i in 0..(numVirtualPlaces - 1)) {
            if (place == virtualMap(i))
                return i;
        }
        return NONEXISTENT_PLACE;
    }

    /* Print out the contents of a virtual place map
     */
    public def printVirtualPlaceMap(): void {
        Console.OUT.println("Map of virtual place IDs to physical place IDs");
        for (i in 0..(numVirtualPlaces - 1)) {
	    Console.OUT.println(i + ": " + virtualMap(i).id);
        }
    }

    /* Replace virtual place with id "id" with physical place "Place". Throw
     * an exception if "id" is out of range
     */
    public def replaceVirtualPlace(id: Long, place: Place): void {
        if (id < 0 || id >= numVirtualPlaces)
            throw new Exception("Error: Virtual place ID passed to VirtualPlaceMap.replaceVirtualPlace is out of range");
        virtualMap(id) = place;
    }

    /* Return total number of virtual places in the map
     */
    public def totalVirtualPlaces(): Long {
        return numVirtualPlaces;
    }


    /* Return physical place with id "id". Throw exception if id is out of
     * range
     */
    public def virtualToPhysical(id: Long): Place {
        if (id < 0 || id >= numVirtualPlaces)
            throw new Exception("Error: Virtual place ID passed to VirtualPlaceMap.virtualToPhysical is out of range");
        return virtualMap(id);
    }
}
