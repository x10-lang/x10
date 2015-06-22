/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright Australian National University 2011.
 */

package x10.regionarray;

/**
 * A GhostManager manages the ghost region at a single place, including
 * sending ghost data to other places.
 * Intended for use in a phased computation: in each phase, ghost data are
 * updated at all places, then used at all places.
 * However, synchronization is local between neighboring places, rather than
 * global between all places.
 */
public abstract class GhostManager {
    /**
     * The width of the "ghost" region for which each place should
     * hold a copy of data stored at neighboring places.
     */
    public val ghostWidth:Long;

    /**
     * The current phase of the computation with regard to ghost cell updates.
     * Places are assumed to progress together; in even phases, ghost cells are
     * used; in odd phases, ghost cells are updated.  No place may start phase 
     * P+2 before neighboring places have completed phase P.
     */
    protected var currentPhase:Byte;

    public def this(ghostWidth:Long) {
        this.ghostWidth = ghostWidth;
        this.currentPhase = 0Y;
    }

    public final def currentPhase():Byte {
        return currentPhase;
    }

    /** Get the neighboring places that hold ghost regions for this place. */
    public abstract def getNeighbors():Rail[Place];

    /** @return the ghost region at the given place */
    public abstract def getGhostRegion(place:Place):Region;

    public abstract def setNeighborReceived(place:Place, shift:Point):void;
    public abstract def allNeighborsReceived():Boolean;
    public abstract def resetNeighborsReceived():void;
    public abstract def sendGhosts(array:Ghostable):void;

    /** 
     * Wait for all ghosts to be received and then return.
     * Used to switch ghost manager phase from sending to using ghost data.
     */
    public def waitOnGhosts() {
        when (allNeighborsReceived()) {
            currentPhase++;
            resetNeighborsReceived();
        }
    }

    /**
     * Prepare to send ghosts to other places.
     * Used to switch ghost manager phase from using to sending ghost data.
     */
    public atomic def prepareToSendGhosts() {
        currentPhase++;
    }

    /**
     * A GhostNeighborFlag holds the status of a neighbor place for a GhostManager.
     */
    static class GhostNeighborFlag(place:Place, shift:Point) {
        public var received:Boolean = false;
    }
}

// vim:tabstop=4:shiftwidth=4:expandtab
