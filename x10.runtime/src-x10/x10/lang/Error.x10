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
 * An Error is a subclass of Throwable that indicates serious problems that a reasonable
 * application should not try to catch.  Most such errors are abnormal conditions.
 * For example, an {@link x10.lang.OutOfMemoryError} represents such an condition.
 *
 * A method is not required to declare in its throws clause any subclasses of Error that
 * might be thrown during the execution of the method but not caught, since these errors
 * are abnormal conditions that should never occur.
 */
@NativeRep("java", "java.lang.Error", null, "x10.rtt.Types.ERROR")
public class Error extends CheckedThrowable {

    /**
     * Construct an Error with no detail message and no cause.
     */
    @Native("java", "new java.lang.Error()")
    public def this() { super(); }

    /**
     * Construct an Error with the specified detail message and no cause.
     *
     * @param message the detail message
     */
    @Native("java", "new java.lang.Error(#message)")
    public def this(message: String) { super(message); }

    /**
     * Construct an Error with no detail message and the specified cause.
     *
     * @param cause the cause
     */
    @Native("java", "new java.lang.Error(#cause)")
    public def this(cause: CheckedThrowable) { super(cause); }

    /**
     * Construct an Error with the specified detail message and the specified cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    @Native("java", "new java.lang.Error(#message, #cause)")
    public def this(message: String, cause: CheckedThrowable) { super(message, cause); }

}

// vim:tabstop=4:shiftwidth=4:expandtab
