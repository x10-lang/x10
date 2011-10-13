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

package x10.lang;

/**
 * Thrown to indicate that the code has attempted to cast an object to a subclass of which it is
 * not an instance.
 */
public class ClassCastException extends Exception {

    /**
     * Construct a ClassCastException with no detail message.
     */
    public def this() { super(); }

    /**
     * Construct a ClassCastException with the specified detail message.
     *
     * @param message the detail message
     */
    public def this(message: String) { super(message); }
}

// vim:tabstop=4:shiftwidth=4:expandtab
