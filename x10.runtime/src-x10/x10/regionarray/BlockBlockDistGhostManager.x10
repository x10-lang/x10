/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright Australian National University 2011.
 *  (C) IBM Corporation 2015.
 */

package x10.regionarray;

import x10.compiler.Inline;
import x10.util.ArrayList;

/**
 * A BlockBlockDistGhostManager manages the local ghost region for a Ghostable
 * array that is distributed using a BlockBlockDist.
 * Ghost regions are sent using a simple put algorithm with no internal synchronization.
 */
final class BlockBlockDistGhostManager extends GhostManager {
    val bbd:BlockBlockDist;
    val neighbors:Rail[GhostNeighborFlag];
    private val periodic:Boolean;

    public def this(ghostWidth:Long, bbd:Dist, periodic:Boolean) {
        super(ghostWidth);
        this.bbd = bbd as BlockBlockDist;
        this.periodic = periodic;
        this.neighbors = createNeighbors();
    }

    public def getNeighbors() = new Rail[Place](neighbors.size, (i:Long)=>neighbors(i).place);

    /** 
     * Creates the list of neighboring places that hold the blocks immediately
     * surrounding place p.  In a BlockBlockDist, a place may hold two blocks
     * contiguous in axis0 ("west-east"), so a place may adjoin either one or
     * two neighboring places in the "north" and "south" directions.
     * In a periodic distribution, a single neighbor may appear multiple times
     * in the list due to wrapping in any dimension.
     * The neighbors for place p are laid out as follows:
     *
     *   -- axis0 -->
     *  6 | 7 | 8 | 9  ^
     * --------------- |
     *  4 |   p   | 5  | axis1
     * --------------- |
     *  0 | 1 | 2 | 3
     *
     */
    private def createNeighbors() {
        val neighborList = new ArrayList[GhostNeighborFlag]();
        val b = bbd.region.boundingBox();
        val axis0 = bbd.axis0;
        val axis1 = bbd.axis1;
        val pg = bbd.places();
        val min0 = b.min(axis0);
        val max0 = b.max(axis0);
        val min1 = b.min(axis1);
        val max1 = b.max(axis1);
        val size0 = (max0 - min0 + 1);
        val size1 = (max1 - min1 + 1);
        val size0Even = size0 % 2 == 0 ? size0 : size0-1;
        val P = Math.min(pg.numPlaces(), size0Even * size1);
        val divisions0 = Math.min(size0Even, Math.pow2(Math.ceil((Math.log(P as Double) / Math.log(2.0)) / 2.0) as Long));
        val divisions1 = Math.min(size1, Math.ceil((P as Double) / divisions0) as Long);
        val numBlocks = divisions0 * divisions1;
        val leftOver = numBlocks - P;

        val i = pg.indexOf(here);

        val leftOverOddOffset = (divisions0 % 2 == 0) ? 0 : i*2/(divisions0+1);

        val blockIndex0 = i < leftOver ? (i*2-leftOverOddOffset) % divisions0 : (i+leftOver) % divisions0;
        val blockIndex1 = i < leftOver ? (i*2) / divisions0 : (i+leftOver) / divisions0;

        for (y in -1..1) {
            for (x in -1..1) {
                if (x != 0 || y !=0) {
                    var neighborBlockIndex0:Long = (blockIndex0 + x);
                    var neighborBlockIndex1:Long = (blockIndex1 + y);
                    if (i < leftOver && x == 1) {
                        // this place holds two blocks
                        neighborBlockIndex0++;
                    }
                    val groupIndex = getGroupIndex(neighborBlockIndex0, neighborBlockIndex1, divisions0, divisions1, leftOver, periodic, i);
                    val place = pg(groupIndex);
                    if (periodic || place != here) {
                        val shift = getNeighborShift(neighborBlockIndex0, neighborBlockIndex1, divisions0, divisions1, periodic);
                        neighborList.add(new GhostNeighborFlag(place, shift));
                    }
                    if (x == 0) {
                        // there may be two different neighbors to the south
                        if (i < leftOver && groupIndex >= leftOver) {
                            val place2 = pg(groupIndex+1);
                            if (periodic || place2 != here) {
                                val shift2 = getNeighborShift(neighborBlockIndex0, neighborBlockIndex1, divisions0, divisions1, periodic);
                                neighborList.add(new GhostNeighborFlag(place2, shift2));
                            }
                        } else if (i >= leftOver && groupIndex < leftOver) {
                        // A single neighbor may be both north and northwest
                        // or northeast. Remove the duplicate (north).
                            neighborList.removeLast();
                        }
                    }
                }
            }
        }
        return neighborList.toRail();
    }

    public def getInverseNeighborIndex(neighborIndex:Long):Long {
        assert neighborIndex >= 0 && neighborIndex <= 9;
        return 9 - neighborIndex;
    }
    
    public atomic def setNeighborReceived(place:Place, shift:Point) {
        for (neighborFlag in neighbors) {
            if (neighborFlag.place == place && neighborFlag.shift.equals(shift)) {
                neighborFlag.received = true;
                return;
            }
        }
        throw new BadPlaceException(here + " trying to notify received from neighbor " + place + " shift " + shift + " - not a neighbor!");
    }

    public atomic def allNeighborsReceived():Boolean {
        var i:Long=0;
        for (neighborFlag in neighbors) {
            if (neighborFlag.received == false) {
                return false;
            }
            i++;
        }
        return true;
    }

    public atomic def resetNeighborsReceived() {
        for (neighborFlag in neighbors) {
            neighborFlag.received = false;
        }
    }

    private atomic def setAllNeighborsReceived() {
        for (neighborFlag in neighbors) {
            neighborFlag.received = true;
        }
    }

    private def getNeighborShift(neighborBlockIndex0:Long, neighborBlockIndex1:Long, divisions0:Long, divisions1:Long, periodic:Boolean) {
        var axis0Shift:Long = 0;
        var axis1Shift:Long = 0;

        if (periodic) {
            // wrap around
            if (neighborBlockIndex0 < 0) {
                axis0Shift =  1;
            } else if (neighborBlockIndex0 >= divisions0) {
                axis0Shift = -1;
            }
            if (neighborBlockIndex1 < 0) {
                axis1Shift =  1;
            } else if (neighborBlockIndex1 >= divisions1) {
                axis1Shift = -1;
            }
        }
        return getShift(axis0Shift, axis1Shift);
    }

    private def getShift(axis0Shift:Long, axis1Shift:Long) {
        val fullRegion = bbd.region;
        val axis0 = bbd.axis0;
        val axis1 = bbd.axis1;
        val axis0Size = fullRegion.max(axis0) - fullRegion.min(axis0) + 1;
        val axis1Size = fullRegion.max(axis1) - fullRegion.min(axis1) + 1;
        val coords = new Rail[Long](fullRegion.rank);
        coords(axis0) = axis0Shift * axis0Size;
        coords(axis1) = axis1Shift * axis1Size;

        return Point.make(coords);
    }

    /**
     * Send ghost data for this place to neighboring places in a BlockBlockDist 
     * using a simple 'put' algorithm with no internal synchronization.
     */
    public def sendGhosts(array:Ghostable) {
        prepareToSendGhosts();
        val regionHere = bbd(here);
        if (regionHere.isEmpty()) {
            setAllNeighborsReceived();
            return;
        }

        for (neighborFlag in neighbors) {
            var sentToNeighbor:Boolean = false;
            val neighborPlace = neighborFlag.place;
            if (neighborPlace != here || periodic) {
                val neighborReg = getGhostRegion(neighborPlace) as Region(bbd.region.rank()){rect};

                var regionToSend:Region(regionHere.rank()){rect};
                val shift = neighborFlag.shift as Point(regionHere.rank);
                if (periodic) {
                    val shiftedNeighbor = (neighborReg - shift);
                    regionToSend = (regionHere && shiftedNeighbor) as Region(regionHere.rank()){rect};
                } else {
                    regionToSend = (regionHere && neighborReg) as Region(regionHere.rank()){rect};
                }
                array.putOverlap(regionToSend, neighborPlace, shift, currentPhase());
                sentToNeighbor = true;
            }
        }
    }

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
        val region = bbd(place);
        if (region.isEmpty()) return region;

        val r = region.boundingBox();
        val min = new Rail[Long](r.rank);
        val max = new Rail[Long](r.rank);
        for (i in 0..(r.rank-1)) {
            if (i == bbd.axis0 || i == bbd.axis1) {
                if (periodic) {
                    min(i) = r.min(i) - ghostWidth;
                    max(i) = r.max(i) + ghostWidth;
                } else {
                    min(i) = Math.max(bbd.region.min(i), r.min(i) - ghostWidth);
                    max(i) = Math.min(bbd.region.max(i), r.max(i) + ghostWidth);
                }
            } else {
                min(i) = r.min(i);
                max(i) = r.max(i);
            }
        }
        return Region.makeRectangular(min, max);
    }

    private static def getGroupIndex(var neighborBlockIndex0:Long, var neighborBlockIndex1:Long, divisions0:Long, divisions1:Long, leftOver:Long, periodic:Boolean, groupIndexHere:Long) {
        if (periodic) {
            // wrap around
            if (neighborBlockIndex0 < 0) {
                neighborBlockIndex0 += divisions0;
            } else if (neighborBlockIndex0 >= divisions0) {
                neighborBlockIndex0 -= divisions0;
            }
            if (neighborBlockIndex1 < 0) {
                neighborBlockIndex1 += divisions1;
            } else if (neighborBlockIndex1 >= divisions1) {
                neighborBlockIndex1 -= divisions1;
            }
        } else {
            if (neighborBlockIndex0 < 0 
             || neighborBlockIndex0 >= divisions0
             || neighborBlockIndex1 < 0
             || neighborBlockIndex1 >= divisions1) {
                // no neighbor in this direction
                return groupIndexHere;
            }
        }
        val groupIndex:Long;
        val neighborBlockIndex = (neighborBlockIndex1 * divisions0) + neighborBlockIndex0;
        if (neighborBlockIndex <= leftOver * 2) {
            groupIndex = (neighborBlockIndex / 2) as Long;
        } else {
            groupIndex = (neighborBlockIndex - leftOver);
        }
        return groupIndex;
    }
}

// vim:tabstop=4:shiftwidth=4:expandtab
