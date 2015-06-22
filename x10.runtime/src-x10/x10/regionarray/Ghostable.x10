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
 * A Ghostable array allows the sending of ghost data to other places.
 * This is an interface to DistArray, to avoid the use of generic types
 * in the interface GhostManager.
 */
public interface Ghostable {
    /** Gets the distribution over which this Ghostable array is defined. */
    def getDist():Dist;

    /**
     * Collect data from the given overlap region from this place and send to
     * the ghost region at neighborPlace, shifted by the given shift.
     * @param overlap the region of this place that overlaps with the ghost
     *   region at the neighboring place
     * @param neighborPlace the neighboring place
     * @param shift the vector by which to shift the overlap region at the
     *   neighboring place.  Must be null e.g. [0, 0, ..., 0] for non-periodic
     *   distributions; for a periodic distribution, the shift will be non-zero
     *   in any dimension for which the overlap region wraps past the edge
     *   of the full region.
     * @param phase the phase for which these ghosts are valid. If the
     *   neighboring place has not yet reached this phase, the ghosts will be
     *   held awaiting the change of phase.
     */
    def putOverlap(overlap:Region{rect}, neighborPlace:Place, shift:Point(overlap.rank), phase:Byte):void;
}

// vim:tabstop=4:shiftwidth=4:expandtab
