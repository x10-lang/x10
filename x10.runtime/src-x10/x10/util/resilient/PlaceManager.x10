/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2016.
 *  (C) Copyright Sara Salem Hamouda 2014-2016.
 */

package x10.util.resilient;

import x10.util.ArrayList;

/**
 * The PlaceManager provides a set of APIs for managing
 * the replacement of dead Places in an activePlaces PlaceGroup
 * with spare and/or elastically created Places.
 * In a typical Resilient X10 application, the activePlaces
 * of a PlaceManager instance is used instead of Place.places()
 * to represent the "world" on which the computation is running.
 *
 * A single PlaceManager instance should be created at the master
 * Place of the application during application startup and used
 * throughout the lifetime of the application to manage its set
 * of active Places.
 */
public class PlaceManager implements x10.io.Unserializable {

    /**
     * A record describing the Place updates that
     * occurred during a single execution of rebuildActivePlaces().
     */
    public static struct ChangeDescription {
        public val oldActivePlaces:PlaceGroup;
        public val newActivePlaces:PlaceGroup;
        public val removedPlaces:ArrayList[Place];
        public val addedPlaces:ArrayList[Place];

        def this(o:PlaceGroup, n:PlaceGroup, r:ArrayList[Place], a:ArrayList[Place]) {
            oldActivePlaces = o;
            newActivePlaces = n;
            removedPlaces = r;
            addedPlaces = a;
        }

        public def somethingChanged() = oldActivePlaces != newActivePlaces;
    };

    private static val VERBOSE = true;

    private var activePlaces:PlaceGroup;
    private val sparePlaces:ArrayList[Place] = new ArrayList[Place]();
    private val allowShrinking:Boolean;
    private val numSpares:Long;

    /**
     * Create a PlaceManager instance where the initial world is all
     * Places (no places are reserved as spares) and shrinking recovery
     * (decreasing the number of places in activePlaces) is not allowed.
     */
    public def this() {
        this(0, false);
    }

    /**
     * Create a PlaceManager instance with the specified number
     * of spare places and recovery mode.
     *
     * @param numSpares the number of Places to reserve as spares
     * @param allowShrinking should shrinking recovery be allowed.
     */
    public def this(numSpares:Long, allowShrinking:Boolean) {
        this.numSpares = numSpares;
        this.allowShrinking = allowShrinking;
        if (numSpares >= Place.numPlaces()) {
            throw new IllegalArgumentException("Requested more spares than available places");
        }
        val world = Place.places();
        val numActive = world.numPlaces() - numSpares;
        if (world instanceof PlaceGroup.SimplePlaceGroup) {
            activePlaces = PlaceGroup.make(numActive);
        } else {
            activePlaces = new SparsePlaceGroup(new Rail[Place](numActive, (i:Long) => world(i)));
        }
        for (p in world) {
            if (!activePlaces.contains(p)) {
                sparePlaces.add(p);
            }
        }
    }

    /**
     * @return the current PlaceGroup of activePlaces
     */
    public def activePlaces() = activePlaces;

    /**
     * This method should be called after a place failure
     * is detected to rebuild the activePlaces PlaceGroup.
     *
     * @return a ChangeDescription instance describing how things were rebuilt.
     */
    public def rebuildActivePlaces():ChangeDescription {
        val rebuildStart = System.nanoTime();
        // First figure out which Places in activePlaces are dead.
        val deadPlaces = new ArrayList[Place]();
        for (p in activePlaces) {
            if (p.isDead()) {
                deadPlaces.add(p);
            }
        }
        val numDead = deadPlaces.size();
        if (numDead == 0) {
            return new ChangeDescription(activePlaces, activePlaces, deadPlaces, deadPlaces);
        }

        val start = System.nanoTime();
        // discover places added in the last round
        for (p in Place.places()) {
            if (!p.isDead() && !activePlaces.contains(p) && !sparePlaces.contains(p)) {
                if (VERBOSE) Console.OUT.println("PlaceManager: added spare place ["+p.id+"]");
                sparePlaces.add(p);
            }
        }
        // Next try to make sure we have enough spares to do the rebuild
        if (numDead > sparePlaces.size() - numSpares && !allowShrinking) {
            val numNeeded = numDead - sparePlaces.size() + numSpares;
            if (VERBOSE) Console.OUT.println("PlaceManager: must request "+numNeeded+" additional spares ("+numDead+" > "+ (sparePlaces.size() - numSpares) +")");
            val numAdded:Long;
            if (numSpares >= numDead) {
                numAdded = System.addPlaces(numNeeded); // no need to wait
            } else {
                numAdded = System.addPlacesAndWait(numNeeded, 20000); // wait up to 20 seconds; we're exiting if this fails.
                if (numAdded > 0) {
                    for (p in Place.places()) {
                        if (!p.isDead() && !activePlaces.contains(p) && !sparePlaces.contains(p)) {
                            if (VERBOSE) Console.OUT.println("PlaceManager: added spare place ["+p.id+"]");
                            sparePlaces.add(p);
                        }
                    }
                }
            }
            if (numAdded > 0) {
                val end = System.nanoTime();
                if (VERBOSE) Console.OUT.printf("PlaceManager: added %d places in %f seconds\n", numAdded, (end-start)/1e9);
            }
        }

        // Rebuild
        val addedPlaces = new ArrayList[Place]();
        val newActivePlaces = new ArrayList[Place]();
        for (p in activePlaces) {
            if (p.isDead()) {
                if (sparePlaces.isEmpty()) {
                    if (!allowShrinking) {
                        throw new Exception("Not enough spare places found for non-shrinking recovery");
                    }
                } else {
                    val sparePlace = sparePlaces.removeAt(0);
                    if (VERBOSE) Console.OUT.println("PlaceManager: place ["+sparePlace.id+"] is replacing ["+p.id+"] since it is dead ");
                    newActivePlaces.add(sparePlace);
                    addedPlaces.add(sparePlace);
                }
            } else {
                newActivePlaces.add(p);
            }
        }

        // Return description of changes
        val oldActivePlaces = activePlaces;
        activePlaces = new SparsePlaceGroup(newActivePlaces.toRail());
        val rebuildEnd = System.nanoTime();
        if (VERBOSE) Console.OUT.printf("PlaceManager: total rebuild time %f seconds\n", (rebuildEnd-rebuildStart)/1e9);
        return ChangeDescription(oldActivePlaces, activePlaces, deadPlaces, addedPlaces);
    }
}
