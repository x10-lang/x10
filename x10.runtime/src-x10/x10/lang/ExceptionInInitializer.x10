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
 * Thrown to indicate that an exception has occurred during
 * the evaluation of the initialization expression of a 
 * static field.
 */
public class ExceptionInInitializer extends Exception {

    /**
     * Construct an ExceptionInInitializer with the default detail message.
     */
    public def this() { super("exception in static field initialization"); } 

    /**
     * Construct an ExceptionInInitializer with the specified detail message.
     *
     * @param message the detail message
     */
    public def this(message: String) { super(message); }

    /**
     * Construct an ExceptionInInitializer with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public def this(message: String, cause: CheckedThrowable) { super(message, cause); } 

    /**
     * Construct an ExceptionInInitializer with no detail message and the specified cause.
     *
     * @param cause the cause
     */
    public def this(cause: CheckedThrowable) { super(cause); } 
}

// vim:tabstop=4:shiftwidth=4:expandtab
