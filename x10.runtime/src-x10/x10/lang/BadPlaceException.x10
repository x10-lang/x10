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

package x10.lang;

/**
 * Thrown to indicate that an entity residing in a different place is accessed from 'here'.
 * Since most place checking is done statically, only used when accessing array elements.
 */
public class BadPlaceException extends Exception {

    /**
     * Construct a BadPlaceException with the default detail message.
     */
    public def this() = super("bad place exception at "+here);

    /**
     * Construct a BadPlaceException with the specified detail message.
     *
     * @param message the detail message
     */
    public def this(message: String) = super(message);
}

// vim:tabstop=4:shiftwidth=4:expandtab
