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
 * A Node represents an X10RT node that is part of the current computation.
 * A similar concept as a Place in X10, but avoiding using that name to
 * allow a more interesting mapping of Places to Nodes without naming confusion.
 */
public final class Node {
    private final int id;

    Node(int id) {
        this.id = id;
    }

    /**
     * Returns the numeric id of the Node.  This will be an integer between
     * 0 and {@link X10RT#numNodes()}
     * @return The numeric id of the Node.
     */
    public int getId() { return id; }

    @Override
    public String toString() {
        return "Node "+id;
    }
}

