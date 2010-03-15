/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.x10rt;

/**
 * A Place represents an X10RT place that is part of the current computation.
 */
public final class Place {
    private final int id;

    Place(int id) {
        this.id = id;
    }

    /**
     * Returns the numeric id of the Place.  This will be an integer between
     * 0 and {@link X10RT#numPlaces()}
     * @return The numeric id of the Node.
     */
    public int getId() { return id; }

    @Override
    public String toString() {
        return "Node "+id;
    }
}

