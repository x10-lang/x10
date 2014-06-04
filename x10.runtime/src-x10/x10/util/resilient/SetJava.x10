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

package x10.util.resilient;

import x10.util.Box;

import java.util.Iterator;
import java.util.Set;

/**
 * This class is based on Java's Set interface.  It is used when it is necessary
 * to access a Java-implemented set.  For sets purely implemented in X10, the
 * x10.util.Set interface can be used
 */

public class SetJava {

    var javaSet: java.util.Set;


    public def this(set: java.util.Set) {
      javaSet = set;
    }

    /**
     * Remove all elements from the set.
     */
    public def clear(): void {
        javaSet.clear();
    };

    /**
     * Returns true if the set is empty.
     */
    public def isEmpty(): Boolean {
        return javaSet.isEmpty();
    };

    /**
     * Returns an iterator over the elements in the set.
     */
    public def iterator(): java.util.Iterator {
        return javaSet.iterator();
    };

    /**
     * Return size of the set.
     */
    public def size(): Int {
        return javaSet.size();
    };


}
