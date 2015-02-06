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
 * A BlockDistGhostManager manages the local ghost region for a Ghostable
 * array that is distributed using a BlockDist.
 */
final class BlockDistGhostManager extends GhostManager {
    private val bd:BlockDist;
    private val leftNeighbor:GhostNeighborFlag;
    private val rightNeighbor:GhostNeighborFlag;
    private val periodic:Boolean;

    public def this(ghostWidth:Long, bd:BlockDist, periodic:Boolean) {
        super(ghostWidth);
        this.bd = bd;

        val pg = bd.places();

        val i = pg.indexOf(here);
        val left:Place;
        var leftShift:Point(bd.rank);
        if (i>0) {
            left = pg(i-1);
            leftShift = Point.make(bd.rank, (i:Long)=>0);
        } else if (periodic) {
            left = pg(pg.size()-1);
            leftShift = Point.make(bd.rank, (i:Long)=> (i==bd.axis) ? -(bd.region.max(bd.axis)-bd.region.min(bd.axis)+1) : 0);
        } else {
            left = here;
            leftShift = Point.make(bd.rank, (i:Long)=>0);
        }
        this.leftNeighbor = new GhostNeighborFlag(left, leftShift);

        val right:Place;
        var rightShift:Point(bd.rank);
        if (i<(pg.size()-1)) {
            right = pg(i+1);
            rightShift = Point.make(bd.rank, (i:Long)=>0);
        } else if (periodic) {
            right = pg(0);
            rightShift = Point.make(bd.rank, (i:Long)=> (i==bd.axis) ? (bd.region.max(bd.axis)-bd.region.min(bd.axis)+1) : 0);
        } else {
            right = here;
            rightShift = Point.make(bd.rank, (i:Long)=>0);
        }
        this.rightNeighbor = new GhostNeighborFlag(right, rightShift);
        this.periodic = periodic;
    }

    public def getNeighbors() = [leftNeighbor.place, rightNeighbor.place];

    /**
     * Gets the ghost region for a given place, which is the bounding box 
     * for the region held at that place, expanded in each dimension by
     * <code>ghostWidth</code>.  For a periodic distribution, the ghost
     * region may extend beyond the limits of the entire region. For a 
     * non-periodic dist, the ghost region does not extend beyond the min/max
     * of the region in any dimension.
     * @return the ghost region for the given place
     */
    public def getGhostRegion(place:Place):Region {
        val region = bd(place);
        if (region.isEmpty()) return region;

        val r = region.boundingBox();
        val min = new Rail[Long](r.rank);
        val max = new Rail[Long](r.rank);
        for (i in 0..(r.rank-1)) {
            if (i == bd.axis) {
                if (periodic) {
                    min(i) = r.min(i) - ghostWidth;
                    max(i) = r.max(i) + ghostWidth;
                } else {
                    min(i) = Math.max(bd.region.min(i), r.min(i) - ghostWidth);
                    max(i) = Math.min(bd.region.max(i), r.max(i) + ghostWidth);
                }
            } else {
                min(i) = r.min(i);
                max(i) = r.max(i);
            }
        }
        return Region.makeRectangular(min, max);
    }

    public def getInverseNeighborIndex(neighborIndex:Long):Long {
        if (neighborIndex == 0) return 1;
        else if (neighborIndex == 1) return 0;
        else throw new UnsupportedOperationException("no inverse neighbor found for neighborIndex " + neighborIndex);
    }
    
    public atomic def setNeighborReceived(place:Place, shift:Point) {
        if (leftNeighbor.place==place && leftNeighbor.received==false) {
            leftNeighbor.received = true;
            //Console.OUT.println("notified leftNeighbor " + place + " at " + here);
        } else if (rightNeighbor.place==place && rightNeighbor.received==false) {
            rightNeighbor.received = true;
            //Console.OUT.println("notified rightNeighbor " + place + " at " + here);
        } else {
            throw new BadPlaceException(here + " trying to notify received from neighbor "
                + place + " - not a neighbor or already received!");
        }
    }

    public atomic def allNeighborsReceived():Boolean {
        return leftNeighbor.received && rightNeighbor.received;
    }

    public atomic def resetNeighborsReceived() {
        leftNeighbor.received = false;
        rightNeighbor.received = false;
    }

    private atomic def setAllNeighborsReceived() {
        leftNeighbor.received = true;
        rightNeighbor.received = true;
    }

    /**
     * Send ghost data for this place to neighboring places in a BlockDist.
     * As this DistArray is only divided along one axis, data only need
     * to be sent along that axis.
     */
    public def sendGhosts(array:Ghostable) {
        prepareToSendGhosts();

        val r = bd(here);
        if (r.isEmpty()) {
            setAllNeighborsReceived();
            return;
        }

        if (periodic || leftNeighbor.place != here) {
            val leftMin = new Rail[Long](r.rank, (i:Long) => r.min(i));
            val leftMax = new Rail[Long](r.rank, (i:Long) => i==bd.axis ? r.min(i)+ghostWidth-1 : r.max(i));
            val leftReg = Region.makeRectangular(leftMin, leftMax);
            val shiftCoords = new Rail[Long](r.rank, (i:Long)=> i==bd.axis ?
                (r.min(bd.axis) == bd.region.min(bd.axis) ? 
                    bd.region.max(bd.axis)-bd.region.min(bd.axis)+1
                    : 0)
                : 0);
            val shift = Point.make(shiftCoords);
                
            array.putOverlap(leftReg, leftNeighbor.place, shift, currentPhase());
        } else {
            leftNeighbor.received = true;
            //Console.OUT.println("notified leftNeighbor here at " + here);
        }

        if (periodic || rightNeighbor.place != here) {
            val rightMin = new Rail[Long](r.rank, (i:Long) => i==bd.axis ? r.max(i)-ghostWidth+1 : r.min(i));
            val rightMax = new Rail[Long](r.rank, (i:Long) => r.max(i));
            val rightReg = Region.makeRectangular(rightMin, rightMax);
            val shiftCoords = new Rail[Long](r.rank, (i:Long)=> i==bd.axis ?
                (r.max(bd.axis) == bd.region.max(bd.axis) ? 
                    -(bd.region.max(bd.axis)-bd.region.min(bd.axis)+1)
                    : 0)
                : 0);
            val shift = Point.make(shiftCoords);
                
            array.putOverlap(rightReg, rightNeighbor.place, shift, currentPhase());
        } else {
            rightNeighbor.received = true;
            //Console.OUT.println("notified rightNeighbor here at " + here);
        }
    }
}

// vim:tabstop=4:shiftwidth=4:expandtab
