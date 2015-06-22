/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.lang;

/**
 * Thrown to indicate that a Place has died.
 * Place death lasts until the end of execution.  All state at the place is lost.
 */
public class DeadPlaceException(place:Place) extends Exception {

    /**
     * Construct a DeadPlaceException with the default detail message.
     */
    public def this() {
        super("DeadPlaceException at "+here);
        property(here);
    }

    /**
     * Construct a DeadPlaceException with the specified detail message.
     *
     * @param message the detail message
     */
    public def this(message:String) {
        super(message);
        property(here);
    }

    /**
     * Construct a DeadPlaceException with the default detail message.
     *
     * @param p The place that has died.
     */
    public def this(p:Place) {
        super("DeadPlaceException at "+p);
        property(p);
    }

    /**
     * Construct a DeadPlaceException with the specified detail message.
     *
     * @param p The place that has died.
     * @param message the detail message
     */
    public def this(p:Place, message:String) {
        super(message);
        property(p);
    }
}

// vim:tabstop=4:shiftwidth=4:expandtab
