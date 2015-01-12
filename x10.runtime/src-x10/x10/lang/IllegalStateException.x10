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

import x10.compiler.Native;
import x10.compiler.NativeRep;


/**
 * Thrown to indicate that a method has been invoked at an illegal or
 * inappropriate time.
 */
@NativeRep("java", "java.lang.IllegalStateException", null, "x10.rtt.Types.ILLEGAL_STATE_EXCEPTION")
public class IllegalStateException extends Exception {

    /**
     * Construct an IllegalStateException with no detail message and no cause.
     */
    @Native("java", "new java.lang.IllegalStateException()")
    public def this() { super(); }

    /**
     * Construct an IllegalStateException with the specified detail message and no cause.
     *
     * @param message the detail message
     */
    @Native("java", "new java.lang.IllegalStateException(#message)")
    public def this(message: String) { super(message); } 

    /**
     * Construct an IllegalStateException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    @Native("java", "new java.lang.IllegalStateException(#message, #cause)")
    public def this(message: String, cause: CheckedThrowable) { super(message, cause); } 

    /**
     * Construct an IllegalStateException with no detail message and the specified cause.
     *
     * @param cause the cause
     */
    @Native("java", "new java.lang.IllegalStateException(#cause)")
    public def this(cause: CheckedThrowable) { super(cause); } 
}

// vim:tabstop=4:shiftwidth=4:expandtab
