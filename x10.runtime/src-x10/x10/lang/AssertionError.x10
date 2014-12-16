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

import x10.compiler.Native;
import x10.compiler.NativeRep;


/**
 * An AssertionError is a subclass of Error that indicates a failure of assertion.
 */
@NativeRep("java", "java.lang.AssertionError", null, "x10.rtt.Types.ASSERTION_ERROR")
public class AssertionError extends Error {

    /**
     * Construct an AssertionError with no detail message and no cause.
     */
    @Native("java", "new java.lang.AssertionError()")
    public def this() { super(); } 

    /**
     * Construct an AssertionError with the specified detail message and no cause.
     *
     * @param message the detail message
     */
    @Native("java", "new java.lang.AssertionError(#message)")
    public def this(message: String) { super(message); } 

    /**
     * Construct an AssertionError with the specified detail message and the specified cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    @Native("java", "new java.lang.AssertionError(#cause)")
    public def this(message: String, cause: CheckedThrowable) { super(message, cause); }

    /**
     * Construct an AssertionError with no detail message and the specified cause.
     *
     * @param cause the cause
     */
    @Native("java", "new java.lang.AssertionError(#message, #cause)")
    public def this(cause: CheckedThrowable) { super(cause); } 

}

// vim:tabstop=4:shiftwidth=4:expandtab
