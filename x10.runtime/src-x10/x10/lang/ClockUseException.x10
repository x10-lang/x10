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
 * Thrown to indicate that a clock was used incorrectly.  For example, attempting to operate
 * on a dropped clock throws an instance of this class.
 */
public class ClockUseException extends Exception {

    /**
     * Construct a ClockUseException with the default detail message.
     */
    public def this() { super("clock use exception"); }

    /**
     * Construct a ClockUseException with the specified detail message.
     *
     * @param message the detail message
     */
    public def this(message: String) { super(message); } 
}

// vim:shiftwidth=4:tabstop=4:expandtab
