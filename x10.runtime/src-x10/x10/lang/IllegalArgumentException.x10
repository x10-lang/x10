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
 * Thrown to indicate that a method has been passed an illegal or inappropriate argument.
 */
public class IllegalArgumentException extends Exception {

    /**
     * Construct an IllegalArgumentException with no detail message and no cause.
     */
    public def this() { super(); }

    /**
     * Construct an IllegalArgumentException with the specified detail message and no cause.
     *
     * @param message the detail message
     */
    public def this(message: String) { super(message); } 

    /**
     * Construct an IllegalArgumentException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public def this(message: String, cause: Throwable) { super(message, cause); } 

    /**
     * Construct an IllegalArgumentException with no detail message and the specified cause.
     *
     * @param cause the cause
     */
    public def this(cause: Throwable) { super(cause); } 
}

// vim:tabstop=4:shiftwidth=4:expandtab
